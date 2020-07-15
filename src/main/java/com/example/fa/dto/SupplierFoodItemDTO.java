package com.example.fa.dto;


import lombok.Data;

@Data
public class SupplierFoodItemDTO {

    private String id;
    private String itemId;
    private String productId;

    private String name;
    private String productName;
    private String itemName;


    private int quantity;
    private String price;




}
