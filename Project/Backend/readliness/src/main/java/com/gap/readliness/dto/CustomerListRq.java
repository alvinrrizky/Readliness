package com.gap.readliness.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CustomerListRq implements Serializable {
    private Integer page;
    private String shortBy;
    private String direction;
    private Integer size;

}
