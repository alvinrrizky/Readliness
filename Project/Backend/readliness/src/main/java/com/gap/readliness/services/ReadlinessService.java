package com.gap.readliness.services;

import com.gap.readliness.dto.CustomerListRq;
import com.gap.readliness.dto.CustomerResponsePage;
import com.gap.readliness.dto.GetCustomerListRs;
import com.gap.readliness.exception.NotFoundException;
import com.gap.readliness.model.Customer;
import com.gap.readliness.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.gap.readliness.util.Pagination.setPagination;

@Slf4j
@Service
public class ReadlinessService {

    @Autowired
    private CustomerRepository customerRepository;

    public void saveCustomer(Customer customerReq) {
        log.info("Start save customer");
        try {
            customerRepository.save(customerReq);
        } catch (Exception e) {
            log.error("Error save", e);
            throw e;
        }
    }

    @Transactional
    public void updateCustomer(Customer req) {
        log.info("Start update customer");
        try {
            int rowsDeleted = customerRepository.updateCustomer(
                     req.getCustomerName(),
                     req.getCustomerCode(),
                     req.getCustomerAddress(),
                     req.getCustomerPhone(),
                     req.getIsActive(),
                     req.getPic(),
                     req.getCustomerId()
            );
            if (rowsDeleted == 0) {
                log.warn("No customer found with ID: " + req.getCustomerId());
                throw new RuntimeException("Customer not found");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Start deleting customer");
        try {
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

    public Customer getDetailCustomer(Long id) {
        log.info("Start get detail customer");
        try {
            return customerRepository.findByCustomerId(id);
        } catch (Exception e) {
            throw e;
        }
    }


    public GetCustomerListRs getCustomerList(CustomerListRq req) {
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
                    .numberOfElements((long) customerPage.getNumberOfElements())
                    .totalElements(customerPage.getTotalElements())
                    .totalPages((long) customerPage.getTotalPages()).build();
        } catch (Exception e) {
            throw e;
        }
    }
}
