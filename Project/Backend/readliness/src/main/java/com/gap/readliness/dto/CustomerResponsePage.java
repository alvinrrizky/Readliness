package com.gap.readliness.dto;

import java.util.Date;

public interface CustomerResponsePage {
    Long getCustomerId();
    String getCustomerName();
    String getCustomerAddress();
    String getCustomerCode();
    String getCustomerPhone();
    Integer getIsActive();
    Date getLastOrderDate();
    String getPic();
}
