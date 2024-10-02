package com.gap.readliness.services;

import com.gap.readliness.dto.*;
import com.gap.readliness.exception.NotFoundException;
import com.gap.readliness.exception.StockIsNotEnoughException;
import com.gap.readliness.model.Customer;
import com.gap.readliness.model.Item;
import com.gap.readliness.model.Order;
import com.gap.readliness.repository.CustomerRepository;
import com.gap.readliness.repository.ItemRepository;
import com.gap.readliness.repository.OrderRepository;
import com.gap.readliness.util.GenerateUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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

    public void saveCustomer(Customer customerReq) {
        log.info("Start save customer");
        try {
            Date currentDate = new Date();
            customerReq.setCustomerCode(generateUtil.generateUniqueCode(currentDate));

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
                .orElseThrow(() -> new Exception("Barang tidak ditemukan atau stok habis"));

        if (item.getStock() < quantity) {
            throw new Exception("Stok tidak mencukupi");
        }

        item.setStock(item.getStock() - quantity);
        itemRepository.save(item);
        return item;
    }

    @Transactional
    public void saveOrder(Order orderReq) throws Exception {
        log.info("Start save order");
        try {
            Date currentDate = new Date();
            Item item = processOrder(orderReq.getItemsId(),orderReq.getQuantity());

            BigDecimal price = item.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(orderReq.getQuantity());
            orderReq.setTotalPrice(price.multiply(quantity));
            orderReq.setOrderCode(item.getItemsCode() + "-" + generateUtil.generateUniqueCode(currentDate));
            orderReq.setOrderDate(currentDate);

            orderRepository.save(orderReq);
        } catch (Exception e) {
            log.error("Error save", e);
            throw e;
        }
    }

    @Transactional
    public void updateCustomer(Customer req) {
        log.info("Start update customer");
        try {
            int rowsUpdate = customerRepository.updateCustomer(
                     req.getCustomerName(),
                     req.getCustomerCode(),
                     req.getCustomerAddress(),
                     req.getCustomerPhone(),
                     req.getIsActive(),
                     req.getPic(),
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
        Date currentDate = new Date();

        try {
            int rowsUpdate = itemRepository.updateItem(
                    req.getItemsName(),
                    req.getItemsCode(),
                    req.getStock(),
                    req.getPrice(),
                    req.getIsAvailable(),
                    currentDate,
                    req.getItemsId()
            );
            if (rowsUpdate == 0) {
                log.warn("No item found with ID: " + req.getItemsId());
                throw new RuntimeException("Item not found");
            }

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
            Integer rowsUpdateItem = null;
            Item dataItem = getDetailItem(req.getItemsId());

            var orderData = orderRepository.findByOrderId(req.getOrderId());

            if (req.getQuantity() > orderData.getQuantity()) {
                var selisihQuantity = req.getQuantity() - orderData.getQuantity();
                if (selisihQuantity > dataItem.getStock()) {
                    throw new StockIsNotEnoughException("Stock Is Not Enough");
                } else if (selisihQuantity == dataItem.getStock()) {
                    rowsUpdateItem = itemRepository.updateItem(
                            dataItem.getItemsName(),
                            dataItem.getItemsCode(),
                            dataItem.getStock() - selisihQuantity,
                            dataItem.getPrice(),
                            0,
                            dataItem.getLastReStock(),
                            dataItem.getItemsId()
                    );
                } else {
                    rowsUpdateItem = itemRepository.updateItem(
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
                rowsUpdateItem = itemRepository.updateItem(
                        dataItem.getItemsName(),
                        dataItem.getItemsCode(),
                        dataItem.getStock() + (orderData.getQuantity() - req.getQuantity()),
                        dataItem.getPrice(),
                        1,
                        dataItem.getLastReStock(),
                        dataItem.getItemsId()
                );
            }

            if (rowsUpdateItem == 0) {
                log.warn("No item found with ID: " + dataItem.getItemsId());
                throw new RuntimeException("Item not found");
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

        } catch (Exception e) {
            throw new RuntimeException("Error updating order", e);
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Start deleting customer");
        try {
            Integer rowsUpdateItem = null;
            Order dataOrder = orderRepository.findByCustomerId(id);

            if (dataOrder != null) {
                Item dataItem = itemRepository.findByItemsId(dataOrder.getItemsId());
                var stockCustomer = dataOrder.getQuantity();

                rowsUpdateItem = itemRepository.updateItem(
                        dataItem.getItemsName(),
                        dataItem.getItemsCode(),
                        dataItem.getStock() + stockCustomer,
                        dataItem.getPrice(),
                        1,
                        dataItem.getLastReStock(),
                        dataItem.getItemsId()
                );

                if (rowsUpdateItem == 0) {
                    log.warn("No item found with ID: " + dataItem.getItemsId());
                    throw new RuntimeException("Item not found");
                }
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
            Integer rowsUpdateItem = null;
            var orderData = orderRepository.findByOrderId(id);
            Item dataItem = getDetailItem(orderData.getItemsId());

            rowsUpdateItem = itemRepository.updateItem(
                    dataItem.getItemsName(),
                    dataItem.getItemsCode(),
                    dataItem.getStock() + orderData.getQuantity(),
                    dataItem.getPrice(),
                    0,
                    dataItem.getLastReStock(),
                    dataItem.getItemsId()
            );

            if (rowsUpdateItem == 0) {
                log.warn("No item found with ID: " + dataItem.getItemsId());
                throw new RuntimeException("Item not found");
            }

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
            return customerRepository.findByCustomerId(id);
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

    public Order getDetailOrder(Long id) {
        log.info("Start get detail order");
        try {
            return orderRepository.findByOrderId(id);
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
                            Customer customerResponse = new Customer();
                            customerResponse.setCustomerId(customerResponseList.getCustomerId());
                            customerResponse.setCustomerName(customerResponseList.getCustomerName());
                            customerResponse.setCustomerCode(customerResponseList.getCustomerCode());
                            customerResponse.setCustomerAddress(customerResponseList.getCustomerAddress());
                            customerResponse.setCustomerPhone(customerResponseList.getCustomerPhone());
                            customerResponse.setIsActive(customerResponseList.getIsActive());
                            customerResponse.setLastOrderDate(customerResponseList.getLastOrderDate());
                            customerResponse.setPic(customerResponseList.getPic());
                            return customerResponse;
                        }).collect(Collectors.toList());
            }

            return GetCustomerListRs.builder()
                    .customerList(listResponse)
                    .pageCurrent(req.getPage())
                    .numberOfElements((long) customerPage.getNumberOfElements())
                    .totalElements(customerPage.getTotalElements())
                    .totalPages((long) customerPage.getTotalPages()).build();
        } catch (Exception e) {
            throw e;
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

            List<Order> listResponse;
            if (orderPage.isEmpty()) {
                throw new NotFoundException("Customer Not Found");
            } else {
                listResponse = orderPage.get().map(
                        orderResponseList -> {
                            Order orderResponse = new Order();
                            orderResponse.setOrderId(orderResponseList.getOrderId());
                            orderResponse.setOrderCode(orderResponseList.getOrderCode());
                            orderResponse.setOrderDate(orderResponseList.getOrderDate());
                            orderResponse.setTotalPrice(orderResponseList.getTotalPrice());
                            orderResponse.setQuantity(orderResponseList.getQuantity());
                            orderResponse.setCustomerId(orderResponseList.getCustomerId());
                            orderResponse.setItemsId(orderResponseList.getItemsId());
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
