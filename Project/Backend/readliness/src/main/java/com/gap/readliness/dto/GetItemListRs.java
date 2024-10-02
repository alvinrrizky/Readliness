package com.gap.readliness.dto;

import com.gap.readliness.model.Item;
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
public class GetItemListRs implements Serializable {

    private List<Item> itemList;
    private Integer pageCurrent;
    private Long totalPages;
    private Long totalElements;
    private Long numberOfElements;

}
