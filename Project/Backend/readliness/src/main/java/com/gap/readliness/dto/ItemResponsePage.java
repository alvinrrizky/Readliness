package com.gap.readliness.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface ItemResponsePage {
    Long getItemsId();
    String getItemsName();
    String getItemsCode();
    Integer getStock();
    BigDecimal getPrice();
    Integer getIsAvailable();
    Date getLastReStock();
}
