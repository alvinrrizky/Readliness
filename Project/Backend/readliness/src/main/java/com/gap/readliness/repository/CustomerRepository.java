package com.gap.readliness.repository;

import com.gap.readliness.dto.CustomerResponsePage;
import com.gap.readliness.model.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = " SELECT customer_id AS customerId, customer_name AS customerName, customer_code AS customerCode, \n" +
            "       customer_phone AS customerPhone, customer_address AS customerAddress, is_active AS isActive, \n" +
            "       last_order_date AS lastOrderDate, pic \n" +
            "       FROM customers", nativeQuery = true)
    Page<CustomerResponsePage> getCustomer(Pageable pageable);

    Customer findByCustomerId(@Param("customerId") Long customerId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM customers c WHERE c.customer_id = :customerId", nativeQuery = true)
    int deleteByCustomerId(@Param("customerId") Long customerId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE customers SET customer_name = :customerName, customer_code = :customerCode, customer_address = :customerAddress, customer_phone = :customerPhone, is_active = :isActive, pic = :pic WHERE customer_id = :customerId", nativeQuery = true)
    int updateCustomer(@Param("customerName") String customerName,
                       @Param("customerCode") String customerCode,
                       @Param("customerAddress") String customerAddress,
                       @Param("customerPhone") String customerPhone,
                       @Param("isActive") Integer isActive,
                       @Param("pic") String pic,
                       @Param("customerId") Long customerId);
}
