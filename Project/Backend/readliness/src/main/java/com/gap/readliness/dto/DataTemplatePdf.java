package com.gap.readliness.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTemplatePdf {

    private String orderCode;
    private Date orderDate;
    private BigDecimal totalPrice;
    private Integer quantity;
    private String customerName;
    private String itemsName;
}
