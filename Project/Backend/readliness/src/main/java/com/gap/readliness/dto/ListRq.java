package com.gap.readliness.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListRq implements Serializable {
    private Integer page;
    private String shortBy;
    private String direction;
    private Integer size;

}
