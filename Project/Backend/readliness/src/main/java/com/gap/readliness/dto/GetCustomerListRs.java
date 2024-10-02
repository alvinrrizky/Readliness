package com.gap.readliness.dto;

import com.gap.readliness.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerListRs implements Serializable {

    private List<Customer> customerList;
    private Integer pageCurrent;
    private Long totalPages;
    private Long totalElements;
    private Long numberOfElements;

}
