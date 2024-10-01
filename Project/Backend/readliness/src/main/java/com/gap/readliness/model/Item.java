package com.gap.readliness.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "items_id")
    @NotNull
    private Long ItemsId;

    @Column(name = "items_name")
    private String itemsName;

    @Column(name = "items_code")
    private String itemsCode;

    @NotNull
    @Column(name = "stock")
    private Integer stock;

    @NotNull
    @Column(name = "price")
    private Long price;

    @NotNull
    @Column(name = "is_available")
    private Integer isAvailable;

    @Column(name = "last_re_stock")
    private Date lastReStock;

}
