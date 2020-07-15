package com.example.fa.controller;

import com.example.fa.dto.ResponseFoodItemDTO;
import com.example.fa.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
public class FoodAggregatorController {

    @Autowired
    SupplierService foodSupplierService;

    @RequestMapping(method = GET, value = "/buy-item/{name}")
    public ResponseEntity<Object> getFoodItemByName(@PathVariable("name") String name) {

        log.info("request received: /buy-item/{}", name);

        Collection<ResponseFoodItemDTO> results = foodSupplierService.getFoodItemByName(name, false);

        if (!results.isEmpty()) {

            return ResponseEntity.ok(results);

        }

        return ResponseEntity.ok("not-found");
    }


    @RequestMapping(method = GET, value = "/fast-buy-item/{name}")
    public ResponseEntity<Object> getFoodItemByNameFast(@PathVariable("name") String name) {

        log.info("request received: /fast-buy-item/{}", name);

        Collection<ResponseFoodItemDTO> results = foodSupplierService.getFoodItemByNameFast(name, false);

        if (!results.isEmpty()) {

            return ResponseEntity.ok(results);

        }

        return ResponseEntity.ok("not-found");
    }


    @RequestMapping(method = GET, value = "/buy-item-qty/{name}/{quantity}")
    public ResponseEntity<Object> getFoodItemByNameAndQuantity(@PathVariable("name") String name, @PathVariable("quantity") int quantity) {

        log.info("request received: /buy-item-qty/{}/{}", name, quantity);

        Collection<ResponseFoodItemDTO> results = foodSupplierService.getFoodItemByNameFast(name, false);

        int total = results.stream().mapToInt(ResponseFoodItemDTO::getQuantity).sum();

        if (total >= quantity) {

            return ResponseEntity.ok(results);

        }

        return ResponseEntity.ok("not-found");
    }


    @RequestMapping(method = GET, value = "/buy-item-qty-price/{name}/{quantity}/{price}")

    public ResponseEntity<Object> getFoodItemByNameQuantityMaxPrice(@PathVariable("name") String name, @PathVariable("quantity") int quantity, @PathVariable("price") String price) {
        log.info("request received: /buy-item-qty/{}/{}/{}", name, quantity, price);

        foodSupplierService.getFoodItemByName(name, true);

        return ResponseEntity.ok("not-found");
    }


    @RequestMapping(method = GET, value = "/show-summary")

    public ResponseEntity<Object> getStock() {
        log.info("request received: /show-summary");
        return ResponseEntity.ok(foodSupplierService.getCachedSupplies());
    }

}