package com.gap.readliness.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail implements Serializable {
    private Long orderId;
    private String orderCode;
    private Date orderDate;
    private BigDecimal totalPrice;
    private Integer quantity;
    private Long customerId;
    private String customerName;
    private Long itemsId;
    private String itemsName;
}
