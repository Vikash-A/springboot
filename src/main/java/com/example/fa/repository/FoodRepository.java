package com.example.fa.repository;

import com.example.fa.dto.ResponseFoodItemDTO;

import java.util.List;
import java.util.Map;

public interface FoodRepository {


    List<ResponseFoodItemDTO> getFromStock(String name);

    void addFoodItemsInStock(List<ResponseFoodItemDTO> items);

    Map<String, List<ResponseFoodItemDTO>> getAllStock();
}
