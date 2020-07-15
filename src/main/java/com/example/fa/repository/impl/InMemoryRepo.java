package com.example.fa.repository.impl;

import com.example.fa.dto.ResponseFoodItemDTO;
import com.example.fa.repository.FoodRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryRepo implements FoodRepository {

    private static ConcurrentMap<String, List<ResponseFoodItemDTO>> stock = new ConcurrentHashMap();

    @Override
    public List<ResponseFoodItemDTO> getFromStock(String name) {

        return stock.getOrDefault(name, Collections.EMPTY_LIST);
    }

//    @Override
//    public List<ResponseFoodItemDTO> removeFoodItemsFromStock(String name, Predicate<ResponseFoodItemDTO> predicate) {
//
//        List<ResponseFoodItemDTO> result = stock.getOrDefault(name, Collections.EMPTY_LIST);
//
//        List<ResponseFoodItemDTO> newList = new ArrayList<>();
//
//        result.stream().filter(predicate::test).forEach(newList::add);
//
//        result.removeAll(newList);
//
//        return newList;
//
//    }

    @Override
    public void addFoodItemsInStock(List<ResponseFoodItemDTO> items) {

        items.stream().forEach(item -> stock.compute(item.getName(), (s, responseFoodItemDTOS) -> {
            if (responseFoodItemDTOS == null) {
                responseFoodItemDTOS = new ArrayList<>();
            } else {
                responseFoodItemDTOS.add(item);
            }
            responseFoodItemDTOS.add(item);
            return responseFoodItemDTOS;
        }));
    }

    @Override
    public Map<String, List<ResponseFoodItemDTO>> getAllStock() {
        return stock;
    }
}
