package com.example.fa.util;

import com.example.fa.dto.ResponseFoodItemDTO;
import com.example.fa.dto.SupplierFoodItemDTO;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class FoodItemDtoConverter implements Converter<SupplierFoodItemDTO, ResponseFoodItemDTO> {

    @Override
    public ResponseFoodItemDTO convert(SupplierFoodItemDTO sDto) {

        ResponseFoodItemDTO dto = new ResponseFoodItemDTO();

        Optional<String> idOptional = Stream.of(sDto.getId(), sDto.getItemId(), sDto.getProductId())
                .filter(Objects::nonNull).findFirst();

        dto.setId(idOptional.orElse("not-found"));

        Optional<String> nameOptional = Stream.of(sDto.getName(), sDto.getProductName(), sDto.getItemName())
                .filter(Objects::nonNull).findFirst();

        dto.setName(nameOptional.orElse("not-found"));

        dto.setPrice(sDto.getPrice());

        dto.setQuantity(sDto.getQuantity());

        return dto;
    }



}
