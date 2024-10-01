package com.gap.readliness.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_code")
    private Long orderCode;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "total_price")
    private Long totalPrice;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "items_id")
    private Long itemsId;

}
