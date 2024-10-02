package com.gap.readliness.controllers;

import com.gap.readliness.dto.GetOrderListRs;
import com.gap.readliness.dto.ListRq;
import com.gap.readliness.dto.GetCustomerListRs;
import com.gap.readliness.dto.GetItemListRs;
import com.gap.readliness.model.Customer;
import com.gap.readliness.model.Item;
import com.gap.readliness.model.Order;
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
public class ReadlinessController {

    @Autowired
    ReadlinessService readlinessService;

    @PostMapping(value = "/savecustomer", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void saveCustomer(@RequestBody Customer customerReq) {
        readlinessService.saveCustomer(customerReq);
    }

    @PostMapping(value = "/getcustomerlist")
    public GetCustomerListRs getCustomerList(@RequestBody ListRq customerlistRq) {
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

    @PostMapping(value = "/saveitem", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void saveItem(@RequestBody Item itemReq) {
        readlinessService.saveItem(itemReq);
    }

    @PostMapping(value = "/getitemlist")
    public GetItemListRs getItemList(@RequestBody ListRq itemlistRq) {
        return readlinessService.getItemList(itemlistRq);
    }

    @PostMapping(value = "/updateitem")
    public void updateItem(@RequestBody Item itemReq) {
        readlinessService.updateItem(itemReq);
    }

    @PostMapping(value = "/getdetailitem")
    public Item getDetailItem(@RequestParam Long id) {
        return readlinessService.getDetailItem(id);
    }

    @PostMapping(value = "/deleteitem")
    public void deleteItem(@RequestParam Long id) {
        readlinessService.deleteItem(id);
    }

    @PostMapping(value = "/saveorder", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void saveOrder(@RequestBody Order orderReq) throws Exception {
        readlinessService.saveOrder(orderReq);
    }

    @PostMapping(value = "/getorderlist")
    public GetOrderListRs getOrderList(@RequestBody ListRq orderlistRq) {
        return readlinessService.getOrderList(orderlistRq);
    }

    @PostMapping(value = "/updateorder")
    public void updateOrder(@RequestBody Order orderReq) {
        readlinessService.updateOrder(orderReq);
    }

    @PostMapping(value = "/getdetailorder")
    public Order getDetailOrder(@RequestParam Long id) {
        return readlinessService.getDetailOrder(id);
    }

    @PostMapping(value = "/deleteorder")
    public void deleteOrder(@RequestParam Long id) {
        readlinessService.deleteOrder(id);
    }

//    @PostMapping(value = "/downloadavailableorderdatalist")
//    public void deleteItem(@RequestParam Long id) {
//        readlinessService.deleteItem(id);
//    }
}
