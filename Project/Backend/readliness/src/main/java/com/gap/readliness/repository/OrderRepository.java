package com.gap.readliness.repository;

import com.gap.readliness.dto.OrderResponsePage;
import com.gap.readliness.model.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = " SELECT order_id AS orderId, order_code AS orderCode, order_date AS orderDate, \n" +
            "       total_price AS totalPrice, quantity, customer_id AS customerId, items_id AS itemsId \n" +
            "       FROM orders", nativeQuery = true)
    Page<OrderResponsePage> getOrder(Pageable pageable);

    Order findByOrderId(@Param("orderId") Long orderId);

    Order findByItemsId(@Param("itemsId") Long itemsId);
    
    Order findByCustomerId(@Param("customerId") Long customerId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM orders c WHERE c.order_id = :orderId", nativeQuery = true)
    int deleteByOrderId(@Param("orderId") Long orderId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE orders SET order_date = :orderDate, total_price = :totalPrice, quantity = :quantity WHERE order_id = :orderId", nativeQuery = true)
    int updateOrder(@Param("orderDate") Date orderDate,
                   @Param("totalPrice") BigDecimal totalPrice,
                   @Param("quantity") Integer quantity,
                   @Param("orderId") Long orderId);
}
