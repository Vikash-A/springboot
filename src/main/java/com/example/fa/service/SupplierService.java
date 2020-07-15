package com.example.fa.service;

import com.example.fa.dto.ResponseFoodItemDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SupplierService {


    enum Suppliers {
        FRUITS("FruitSupplier"), VEGETABLES("VegetableSupplier"), GRAINS("GrainSupplier");

        private String name;

        public String getName() {
            return this.name;
        }

        Suppliers(String name) {
            this.name = name;
        }
    }

    Collection<ResponseFoodItemDTO> getFoodItemByName(String name, boolean shouldUseCache);

    Collection<ResponseFoodItemDTO> getFoodItemByNameFast(String name, boolean shouldUseCache);

    Map<String, List<ResponseFoodItemDTO>> getCachedSupplies();


}