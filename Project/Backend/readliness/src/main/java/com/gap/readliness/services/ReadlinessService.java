package com.gap.readliness.services;

import com.gap.readliness.dto.*;
import com.gap.readliness.exception.*;
import com.gap.readliness.model.Customer;
import com.gap.readliness.model.Item;
import com.gap.readliness.model.Order;
import com.gap.readliness.repository.CustomerRepository;
import com.gap.readliness.repository.ItemRepository;
import com.gap.readliness.repository.OrderRepository;
import com.gap.readliness.util.GenerateUtil;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.gap.readliness.util.Pagination.setPagination;

@Slf4j
@Service
public class ReadlinessService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GenerateUtil generateUtil;

    @Autowired
    private MinioClient minioClient;

    public void saveCustomer(Customer customerReq, MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("Start save customer");
        try {
            Date currentDate = new Date();
            String objectName = null;

            if (file != null) {
                objectName = UUID.randomUUID().toString() + file.getOriginalFilename();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket("spring-online-shop")
                                .object(objectName)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            customerReq.setCustomerCode(generateUtil.generateUniqueCode(currentDate));
            customerReq.setPic(objectName);
            customerReq.setIsActive(1);

            customerRepository.save(customerReq);
        } catch (Exception e) {
            log.error("Error save", e);
            throw e;
        }
    }

    public void saveItem(Item itemReq) {
        log.info("Start save item");
        try {
            Date currentDate = new Date();
            itemReq.setItemsCode(generateUtil.generateItemCode());
            itemReq.setLastReStock(currentDate);

            itemRepository.save(itemReq);
        } catch (Exception e) {
            log.error("Error save", e);
            throw e;
        }
    }

    public Item processOrder(Long itemsId, int quantity) throws Exception {
        Item item = itemRepository.findByIdWithLock(itemsId)
                .orElseThrow(() -> new OptimisticLockingException("Item not found or stock is not enough"));

        if (item.getStock() < quantity) {
            throw new StockIsNotEnoughException("Stock Is Not Enough");
        }

        var stockItem = item.getStock() - quantity;
        if (item.getStock() <= 0) {
            item.setIsAvailable(0);
        }

        item.setStock(stockItem);
        itemRepository.save(item);
        return item;
    }

    @Transactional
    public void saveOrder(Order orderReq) throws Exception {
        log.info("Start save order");
        try {
            Date currentDate = new Date();

            if (orderReq.getQuantity() == 0) {
                throw new NotChangeException("Quantity cannot nol");
            }

            Item item = processOrder(orderReq.getItemsId(), orderReq.getQuantity());

            BigDecimal price = item.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(orderReq.getQuantity());
            orderReq.setTotalPrice(price.multiply(quantity));
            orderReq.setOrderCode(item.getItemsCode() + "-" + generateUtil.generateUniqueCode(currentDate));
            orderReq.setOrderDate(currentDate);

            orderRepository.save(orderReq);
        } catch (OptimisticLockingException | StockIsNotEnoughException | NotChangeException e) {
            log.error("Order processing failed", e);
            throw new CustomException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error save", e);
            throw e;
        }
    }

    @Transactional
    public void updateCustomer(Customer req, MultipartFile file) {
        log.info("Start update customer");
        try {
            String objectName = null;

            Customer existingCustomer = customerRepository.findByCustomerId(req.getCustomerId());
            String existingObjectName = existingCustomer != null ? existingCustomer.getPic() : null;

            if (existingObjectName != null) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket("spring-online-shop")
                        .object(existingObjectName)
                        .build());
            }

            if (file != null) {
                objectName = UUID.randomUUID().toString() + file.getOriginalFilename();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket("spring-online-shop")
                                .object(objectName)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            int rowsUpdate = customerRepository.updateCustomer(
                     req.getCustomerName(),
                     req.getCustomerCode(),
                     req.getCustomerAddress(),
                     req.getCustomerPhone(),
                     req.getIsActive(),
                     objectName,
                     req.getCustomerId()
            );
            if (rowsUpdate == 0) {
                log.warn("No customer found with ID: " + req.getCustomerId());
                throw new RuntimeException("Customer not found");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    @Transactional
    public void updateItem(Item req) {
        log.info("Start update item");
        try {
            Date currentDate = new Date();
            Integer isAvailable = null;

            if (req.getStock() <= 0) {
                isAvailable = 0;
            } else if (req.getStock() >= 1){
                isAvailable = 1;
            } else {
                isAvailable = req.getIsAvailable();
            }

            itemRepository.updateItem(
                    req.getItemsName(),
                    req.getItemsCode(),
                    req.getStock(),
                    req.getPrice(),
                    isAvailable,
                    currentDate,
                    req.getItemsId()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error updating item", e);
        }
    }

    @Transactional
    public void updateOrder(Order req) {
        log.info("Start update order");
        try {
            Date currentDate = new Date();
            BigDecimal totalPrice = BigDecimal.ZERO;
            Item dataItem = getDetailItem(req.getItemsId());

            var orderData = orderRepository.findByOrderId(req.getOrderId());

            if (req.getQuantity() > orderData.getQuantity()) {
                var selisihQuantity = req.getQuantity() - orderData.getQuantity();
                if (selisihQuantity > dataItem.getStock()) {
                    throw new StockIsNotEnoughException("Stock Is Not Enough");
                } else if (selisihQuantity == dataItem.getStock()) {
                    itemRepository.updateItem(
                            dataItem.getItemsName(),
                            dataItem.getItemsCode(),
                            dataItem.getStock() - selisihQuantity,
                            dataItem.getPrice(),
                            0,
                            dataItem.getLastReStock(),
                            dataItem.getItemsId()
                    );
                } else {
                    itemRepository.updateItem(
                            dataItem.getItemsName(),
                            dataItem.getItemsCode(),
                            dataItem.getStock() - selisihQuantity,
                            dataItem.getPrice(),
                            1,
                            dataItem.getLastReStock(),
                            dataItem.getItemsId()
                    );
                }

            } else if (req.getQuantity() < orderData.getQuantity()) {
                itemRepository.updateItem(
                        dataItem.getItemsName(),
                        dataItem.getItemsCode(),
                        dataItem.getStock() + (orderData.getQuantity() - req.getQuantity()),
                        dataItem.getPrice(),
                        1,
                        dataItem.getLastReStock(),
                        dataItem.getItemsId()
                );
            }

            BigDecimal price = dataItem.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(req.getQuantity());
            totalPrice = price.multiply(quantity);

            int rowsUpdate = orderRepository.updateOrder(
                    currentDate,
                    totalPrice,
                    req.getQuantity(),
                    req.getOrderId()
            );
            if (rowsUpdate == 0) {
                log.warn("No order found with ID: " + req.getCustomerId());
                throw new RuntimeException("order not found");
            }

        } catch (StockIsNotEnoughException | NotChangeException e) {
            throw new CustomException(e.getMessage(),e);
        } catch (Exception e) {
            throw new RuntimeException("Error updating order", e);
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Start deleting customer");
        try {
            Order dataOrder = orderRepository.findByCustomerId(id);

            if (dataOrder != null) {
                Item dataItem = itemRepository.findByItemsId(dataOrder.getItemsId());
                var stockCustomer = dataOrder.getQuantity();

                itemRepository.updateItem(
                        dataItem.getItemsName(),
                        dataItem.getItemsCode(),
                        dataItem.getStock() + stockCustomer,
                        dataItem.getPrice(),
                        1,
                        dataItem.getLastReStock(),
                        dataItem.getItemsId()
                );
            }



            int rowsDeleted = customerRepository.deleteByCustomerId(id);
            if (rowsDeleted == 0) {
                log.warn("No customer found with ID: " + id);
                throw new RuntimeException("Customer not found");
            }
        } catch (Exception e) {
            log.error("Error deleting customer", e);
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    @Transactional
    public void deleteItem(Long id) {
        log.info("Start deleting item");
        try {
            int rowsDeleted = itemRepository.deleteByItemsId(id);
            if (rowsDeleted == 0) {
                log.warn("No item found with ID: " + id);
                throw new RuntimeException("Item not found");
            }
        } catch (Exception e) {
            log.error("Error deleting item", e);
            throw new RuntimeException("Error deleting item", e);
        }
    }

    @Transactional
    public void deleteOrder(Long id) {
        log.info("Start deleting order");
        try {
            var orderData = orderRepository.findByOrderId(id);
            Item dataItem = getDetailItem(orderData.getItemsId());

            itemRepository.updateItem(
                    dataItem.getItemsName(),
                    dataItem.getItemsCode(),
                    dataItem.getStock() + orderData.getQuantity(),
                    dataItem.getPrice(),
                    1,
                    dataItem.getLastReStock(),
                    dataItem.getItemsId()
            );

            int rowsDeleted = orderRepository.deleteByOrderId(id);
            if (rowsDeleted == 0) {
                log.warn("No order found with ID: " + id);
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            log.error("Error deleting order", e);
            throw new RuntimeException("Error deleting order", e);
        }
    }

    public Customer getDetailCustomer(Long id) {
        log.info("Start get detail customer");
        try {

            var dataDetail = customerRepository.findByCustomerId(id);

            String objectName = dataDetail.getPic();
            String presignedUrl = generatePresignedUrl(objectName);

            Customer customer = new Customer();
            customer.setCustomerId(dataDetail.getCustomerId());
            customer.setCustomerName(dataDetail.getCustomerName());
            customer.setCustomerCode(dataDetail.getCustomerCode());
            customer.setCustomerPhone(dataDetail.getCustomerPhone());
            customer.setCustomerAddress(dataDetail.getCustomerAddress());
            customer.setIsActive(dataDetail.getIsActive());
            customer.setLastOrderDate(dataDetail.getLastOrderDate());
            customer.setPic(presignedUrl);

            return customer;
        } catch (Exception e) {
            throw e;
        }
    }

    public Item getDetailItem(Long id) {
        log.info("Start get detail item");
        try {
            return itemRepository.findByItemsId(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public OrderDetail getDetailOrder(Long id) {
        log.info("Start get detail order");
        try {
            Order orderdetail = orderRepository.findByOrderId(id);
            Customer customer = customerRepository.findByCustomerId(orderdetail.getCustomerId());
            Item item = itemRepository.findByItemsId(orderdetail.getItemsId());

            return OrderDetail.builder()
                    .orderId(orderdetail.getOrderId())
                    .orderCode(orderdetail.getOrderCode())
                    .orderDate(orderdetail.getOrderDate())
                    .quantity(orderdetail.getQuantity())
                    .totalPrice(orderdetail.getTotalPrice())
                    .customerId(orderdetail.getCustomerId())
                    .customerName(customer.getCustomerName())
                    .itemsId(orderdetail.getItemsId())
                    .itemsName(item.getItemsName())
                    .build();
        } catch (Exception e) {
            throw e;
        }
    }

    public List<GetItem> getItem() throws NotFoundException {
        try {
            List<Item> list = itemRepository.findAll();

            if (list.isEmpty()) {
                throw new NotFoundException("Item Not Found");
            }

            List<GetItem> listResponse = list.stream().map(item -> {
                GetItem response = new GetItem();
                response.setItemsId(item.getItemsId());
                response.setItemsName(item.getItemsName());
                return response;
            }).collect(Collectors.toList());
            return listResponse;
        } catch (NotFoundException e) {
            throw new CustomException(e.getMessage(), e);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<GetCustomer> getCustomer() throws NotFoundException {
        try {
            List<Customer> list = customerRepository.findAll();

            if (list.isEmpty()) {
                throw new NotFoundException("Customer Not Found");
            }

            List<GetCustomer> listResponse = list.stream().map(customer -> {
                GetCustomer response = new GetCustomer();
                response.setCustomerId(customer.getCustomerId());
                response.setCustomerName(customer.getCustomerName());
                return response;
            }).collect(Collectors.toList());
            return listResponse;
        } catch (NotFoundException e) {
            throw new CustomException(e.getMessage(), e);
        } catch (Exception e) {
            throw e;
        }
    }


    public GetCustomerListRs getCustomerList(ListRq req) {
        log.info("Start get List customer");
        try {
            if (req.getPage() == null || req.getPage() == 0) {
                throw new IllegalArgumentException("Page number cannot be null or 0");
            }

            Pageable pageRequest = setPagination(
                    req.getPage() - 1,
                    req.getSize() == null  ? 10 : req.getSize(),
                    req.getShortBy() == null || req.getShortBy().isEmpty() ? "customer_id" : req.getShortBy(),
                    req.getDirection() == null || req.getDirection().isEmpty() ? "" : req.getDirection());


            Page<CustomerResponsePage> customerPage;

            customerPage = customerRepository.getCustomer(pageRequest);

            List<Customer> listResponse;
            if (customerPage.isEmpty()) {
                throw new NotFoundException("Customer Not Found");
            } else {
                listResponse = customerPage.get().map(
                        customerResponseList -> {

                            String objectName = customerResponseList.getPic();
                            String presignedUrl = generatePresignedUrl(objectName);

                            Customer customerResponse = new Customer();
                            customerResponse.setCustomerId(customerResponseList.getCustomerId());
                            customerResponse.setCustomerName(customerResponseList.getCustomerName());
                            customerResponse.setCustomerCode(customerResponseList.getCustomerCode());
                            customerResponse.setCustomerAddress(customerResponseList.getCustomerAddress());
                            customerResponse.setCustomerPhone(customerResponseList.getCustomerPhone());
                            customerResponse.setIsActive(customerResponseList.getIsActive());
                            customerResponse.setLastOrderDate(customerResponseList.getLastOrderDate());
                            customerResponse.setPic(presignedUrl);
                            return customerResponse;
                        }).collect(Collectors.toList());
            }

            return GetCustomerListRs.builder()
                    .customerList(listResponse)
                    .pageCurrent(req.getPage())
                    .numberOfElements((long) customerPage.getNumberOfElements())
                    .totalElements(customerPage.getTotalElements())
                    .totalPages((long) customerPage.getTotalPages()).build();
        } catch (NotFoundException e) {
            log.error("Error ", e);
            throw new CustomException(e.getMessage(), e);
        } catch (Exception e){
            throw e;
        }
    }

    private String generatePresignedUrl(String objectName) {
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .bucket("spring-online-shop")
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(7, TimeUnit.DAYS)
                    .build();
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error("Error generating presigned URL for object: {}", objectName, e);
            throw new RuntimeException("Failed to generate presigned URL for object: " + objectName, e);
        }
    }

    public GetItemListRs getItemList(ListRq req) {
        log.info("Start get List Item");
        try {
            if (req.getPage() == null || req.getPage() == 0) {
                throw new IllegalArgumentException("Page number cannot be null or 0");
            }

            Pageable pageRequest = setPagination(
                    req.getPage() - 1,
                    req.getSize() == null  ? 10 : req.getSize(),
                    req.getShortBy() == null || req.getShortBy().isEmpty() ? "items_id" : req.getShortBy(),
                    req.getDirection() == null || req.getDirection().isEmpty() ? "" : req.getDirection());


            Page<ItemResponsePage> itemPage;

            itemPage = itemRepository.getItem(pageRequest);

            List<Item> listResponse;
            if (itemPage.isEmpty()) {
                throw new NotFoundException("Customer Not Found");
            } else {
                listResponse = itemPage.get().map(
                        itemResponseList -> {
                            Item itemResponse = new Item();
                            itemResponse.setItemsId(itemResponseList.getItemsId());
                            itemResponse.setItemsName(itemResponseList.getItemsName());
                            itemResponse.setItemsCode(itemResponseList.getItemsCode());
                            itemResponse.setStock(itemResponseList.getStock());
                            itemResponse.setPrice(new BigDecimal(String.valueOf(itemResponseList.getPrice())));
                            itemResponse.setIsAvailable(itemResponseList.getIsAvailable());
                            itemResponse.setLastReStock(itemResponseList.getLastReStock());
                            return itemResponse;
                        }).collect(Collectors.toList());
            }

            return GetItemListRs.builder()
                    .itemList(listResponse)
                    .pageCurrent(req.getPage())
                    .numberOfElements((long) itemPage.getNumberOfElements())
                    .totalElements(itemPage.getTotalElements())
                    .totalPages((long) itemPage.getTotalPages()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    public GetOrderListRs getOrderList(ListRq req) {
        log.info("Start get List Order");
        try {
            if (req.getPage() == null || req.getPage() == 0) {
                throw new IllegalArgumentException("Page number cannot be null or 0");
            }

            Pageable pageRequest = setPagination(
                    req.getPage() - 1,
                    req.getSize() == null  ? 10 : req.getSize(),
                    req.getShortBy() == null || req.getShortBy().isEmpty() ? "order_id" : req.getShortBy(),
                    req.getDirection() == null || req.getDirection().isEmpty() ? "" : req.getDirection());


            Page<OrderResponsePage> orderPage;

            orderPage = orderRepository.getOrder(pageRequest);

            List<OrderList> listResponse;
            if (orderPage.isEmpty()) {
                throw new NotFoundException("Customer Not Found");
            } else {
                listResponse = orderPage.get().map(
                        orderResponseList -> {
                            Customer customer = customerRepository.findByCustomerId(orderResponseList.getCustomerId());
                            Item item = itemRepository.findByItemsId(orderResponseList.getItemsId());

                            OrderList orderResponse = new OrderList();
                            orderResponse.setOrderId(orderResponseList.getOrderId());
                            orderResponse.setOrderCode(orderResponseList.getOrderCode());
                            orderResponse.setOrderDate(orderResponseList.getOrderDate());
                            orderResponse.setTotalPrice(orderResponseList.getTotalPrice());
                            orderResponse.setQuantity(orderResponseList.getQuantity());
                            orderResponse.setCustomerName(customer.getCustomerName());
                            orderResponse.setItemsName(item.getItemsName());
                            return orderResponse;
                        }).collect(Collectors.toList());
            }

            return GetOrderListRs.builder()
                    .orderList(listResponse)
                    .pageCurrent(req.getPage())
                    .numberOfElements((long) orderPage.getNumberOfElements())
                    .totalElements(orderPage.getTotalElements())
                    .totalPages((long) orderPage.getTotalPages()).build();
        } catch (Exception e) {
            throw e;
        }
    }
}
