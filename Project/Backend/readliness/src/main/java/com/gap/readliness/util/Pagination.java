package com.gap.readliness.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Pagination {

    private Pagination(){

    }
    public static Pageable setPagination(Integer pageNumber, Integer pageSize, String sortBy, String direction) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }
}
