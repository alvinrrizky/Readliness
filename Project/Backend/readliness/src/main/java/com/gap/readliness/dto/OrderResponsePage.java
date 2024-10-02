package com.gap.readliness.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface OrderResponsePage {
    Long getOrderId();
    String getOrderCode();
    Date getOrderDate();
    BigDecimal getTotalPrice();
    Integer getQuantity();
    Long getCustomerId();
    Long getItemsId();
}
