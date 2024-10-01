package com.gap.readliness.controllers;

import com.gap.readliness.dto.CustomerListRq;
import com.gap.readliness.dto.GetCustomerListRs;
import com.gap.readliness.model.Customer;
import com.gap.readliness.services.ReadlinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class readlinessController {

    @Autowired
    ReadlinessService readlinessService;

    @PostMapping(value = "/savecustomer", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void saveCustomer(@RequestBody Customer customerReq) {
        readlinessService.saveCustomer(customerReq);
    }

    @PostMapping(value = "/getcustomerlist")
    public GetCustomerListRs getCustomerList(@RequestBody CustomerListRq customerlistRq) {
        return readlinessService.getCustomerList(customerlistRq);
    }

    @PostMapping(value = "/updatecustomer")
    public void updateCustomer(@RequestBody Customer customerReq) {
        readlinessService.updateCustomer(customerReq);
    }

    @PostMapping(value = "/getdetailcustomer")
    public Customer getDetailCustomer(@RequestParam Long id) {
        return readlinessService.getDetailCustomer(id);
    }

    @PostMapping(value = "/deletecustomer")
    public void deleteCustomer(@RequestParam Long id) {
        readlinessService.deleteCustomer(id);
    }
}
