package com.gap.readliness.controllers;

import com.gap.readliness.dto.*;
import com.gap.readliness.model.Customer;
import com.gap.readliness.model.Item;
import com.gap.readliness.model.Order;
import com.gap.readliness.services.ReadlinessService;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@RestController
public class ReadlinessController {

    @Autowired
    ReadlinessService readlinessService;

    @PostMapping(value = "/savecustomer", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void saveCustomer(@RequestPart Customer customerReq, @RequestPart("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        readlinessService.saveCustomer(customerReq, file);
    }

    @PostMapping(value = "/getcustomerlist")
    public GetCustomerListRs getCustomerList(@RequestBody ListRq customerlistRq) {
        return readlinessService.getCustomerList(customerlistRq);
    }

    @PostMapping(value = "/getcustomer")
    public List<GetCustomer> getCustomer() {
        return readlinessService.getCustomer();
    }

    @PostMapping(value = "/getitem")
    public List<GetItem> getItem() {
        return readlinessService.getItem();
    }

    @PostMapping(value = "/updatecustomer", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updateCustomer(@RequestPart Customer customerReq, @RequestPart("file") MultipartFile file) {
        readlinessService.updateCustomer(customerReq, file);
    }

    @PostMapping(value = "/getdetailcustomer")
    public getDetailCustomer getDetailCustomer(@RequestParam Long id) {
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
    public OrderDetail getDetailOrder(@RequestParam Long id) {
        return readlinessService.getDetailOrder(id);
    }

    @PostMapping(value = "/deleteorder")
    public void deleteOrder(@RequestParam Long id) {
        readlinessService.deleteOrder(id);
    }

    @GetMapping(value = "/getFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> getFile(@RequestParam String fileName) {
        try {
            InputStreamResource resource = readlinessService.getFile(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
