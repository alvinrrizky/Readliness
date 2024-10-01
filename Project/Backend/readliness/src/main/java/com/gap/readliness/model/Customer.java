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
@Table(name = "customers")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @NotNull
    @Column(name = "customer_name")
    private String customerName;

    @NotNull
    @Column(name = "customer_address")
    private String customerAddress;

    @NotNull
    @Column(name = "customer_code")
    private String customerCode;

    @NotNull
    @Column(name = "customer_phone")
    private String customerPhone;

    @NotNull
    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "last_order_date")
    private Date lastOrderDate;

    @Column(name = "pic")
    private String pic;

}
