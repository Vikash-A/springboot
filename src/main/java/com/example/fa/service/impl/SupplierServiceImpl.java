package com.example.fa.service.impl;

import com.example.fa.configuration.SupplierConfiguration;
import com.example.fa.dto.ResponseFoodItemDTO;
import com.example.fa.dto.SupplierFoodItemDTO;
import com.example.fa.repository.FoodRepository;
import com.example.fa.repository.impl.InMemoryRepo;
import com.example.fa.service.SupplierService;
import com.example.fa.util.FoodItemDtoConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class SupplierServiceImpl implements SupplierService {

    private static Map<String, SupplierFoodItemDTO> productRepo = new HashMap<>();

    private FoodItemDtoConverter foodItemDtoConverter = new FoodItemDtoConverter();


    @Autowired
    SupplierConfiguration supplierConfiguration;

    FoodRepository inMemoryRepo = new InMemoryRepo();

    private WebClient webClient;

    @PostConstruct
    public void init() {

        webClient = WebClient
                .builder()
                .baseUrl(supplierConfiguration.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", supplierConfiguration.getBaseUrl()))
                .build();
    }

    private Collection<ResponseFoodItemDTO> getAllSupplies() {

        List<String> suppliers = Stream.of(Suppliers.values())
                .filter(v -> supplierConfiguration.getSuppliersMap().containsKey(v.getName()))
                .map(s -> supplierConfiguration.getSuppliersMap().get(s.getName()))
                .collect(Collectors.toList());

        Flux<List<SupplierFoodItemDTO>> flux = fetchSuppliesFromExternalService(suppliers);

        List<List<SupplierFoodItemDTO>> list = flux.collectList().block();

        return list.stream().flatMap(Collection::stream).map(item -> foodItemDtoConverter.convert(item)).collect(Collectors.toList());
    }


    private Mono<List<SupplierFoodItemDTO>> getSupply(String resourceUri) {

        log.info("Calling getSupply {}", resourceUri);

        return webClient.get()
                .uri(resourceUri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SupplierFoodItemDTO>>() {
                });
    }


    private Flux<List<SupplierFoodItemDTO>> fetchSuppliesFromExternalService(List<String> resourceUris) {
        return Flux.fromIterable(resourceUris)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap(this::getSupply)
                .sequential();

    }

    @Override
    public List<ResponseFoodItemDTO> getFoodItemByName(String name, boolean shouldUseCache) {

        List<ResponseFoodItemDTO> filteredResults = Collections.EMPTY_LIST;

        List<String> suppliersResourceUri = Stream.of(Suppliers.values())
                .filter(v -> supplierConfiguration.getSuppliersMap().containsKey(v.getName()))
                .map(s -> supplierConfiguration.getSuppliersMap().get(s.getName()))
                .collect(Collectors.toList());

        Optional<List<SupplierFoodItemDTO>> foodItemDTOListOptional = null;

        for (String supplierUri : suppliersResourceUri) {

            foodItemDTOListOptional = getSupply(supplierUri).blockOptional();

            if (foodItemDTOListOptional.isPresent()) {

                List<SupplierFoodItemDTO> foodItemDTOS = foodItemDTOListOptional.get();

                log.info("found {} supplies ", foodItemDTOS.size());

                List<ResponseFoodItemDTO> results = foodItemDTOS.stream().map(i -> foodItemDtoConverter.convert(i)).collect(Collectors.toList());

                if (shouldUseCache && foodItemDTOS.size() > 0)
                    inMemoryRepo.addFoodItemsInStock(results);

                filteredResults = foodItemDTOS.stream().filter(item -> name.equalsIgnoreCase(item.getItemName())
                        || name.equalsIgnoreCase(item.getName())
                        || name.equalsIgnoreCase(item.getProductName())).map(item -> foodItemDtoConverter.convert(item)).collect(Collectors.toList());


                log.info("found {} supplies with name {} ", filteredResults.size(), name);

                if(filteredResults.size()>0)
                    break;

            }

        }

        return filteredResults;
    }


    @Override
    public Collection<ResponseFoodItemDTO> getFoodItemByNameFast(String name, boolean shouldUseCache) {

        Collection<ResponseFoodItemDTO> foodItemDTOList = getAllSupplies();

        log.info("found {} supplies ", foodItemDTOList.size());

        Collection<ResponseFoodItemDTO> result = foodItemDTOList.stream().filter(foodItemDTO -> name.equalsIgnoreCase(foodItemDTO.getName())).collect(Collectors.toList());

        log.info("found {} supplies with name {} ", result.size(), name);

        return result;
    }

    @Override
    public Map<String, List<ResponseFoodItemDTO>> getCachedSupplies() {
        return inMemoryRepo.getAllStock();
    }


    public Collection<ResponseFoodItemDTO> getFoodItemFromCache(String name) {


//        if (fromStock.size() == quantity)
//
//            return fromStock;
//
//        List<String> suppliersResourceUri = Stream.of(Suppliers.values())
//                .filter(v -> supplierConfiguration.getSuppliersMap().containsKey(v.getName()))
//                .map(s -> supplierConfiguration.getSuppliersMap().get(s.getName()))
//                .collect(Collectors.toList());
//
//        Optional<List<SupplierFoodItemDTO>> foodItemDTOListOptional = null;
//
//        for (String supplierUri : suppliersResourceUri) {
//
//            foodItemDTOListOptional = getSupply(supplierUri).blockOptional();
//
//            if (foodItemDTOListOptional.isPresent()) {
//
//                List<SupplierFoodItemDTO> foodItemDTOS = foodItemDTOListOptional.get();
//
//                log.info("found {} supplies ", foodItemDTOS.size());
//
//                List<ResponseFoodItemDTO> fromSupplier = foodItemDTOS.stream().filter(item -> (name.equalsIgnoreCase(item.getItemName())
//                        || name.equalsIgnoreCase(item.getName())
//                        || name.equalsIgnoreCase(item.getProductName()))
//                        && predicate.test(foodItemDtoConverter.convert(item)))
//                        .map(item -> foodItemDtoConverter.convert(item))
//                        .collect(Collectors.toList());
//
//
//                log.info("found {} supplies with name {} ", fromSupplier.size(), name);
//
//                foodItemDTOS.removeAll(fromSupplier);
//
//                inMemoryRepo.addFoodItemsInStock(foodItemDTOS.stream().map(item -> foodItemDtoConverter.convert(item)).collect(Collectors.toList()));
//
//
//                if (!fromSupplier.isEmpty() && fromStock.size() + fromSupplier.size() >= quantity) {
//                    Comparator<ResponseFoodItemDTO> priceComparator = Comparator.comparing(ResponseFoodItemDTO::getPrice);
//                    fromStock.addAll(fromSupplier);
//                    Collections.sort(fromStock, priceComparator));
//
//
//                }
//
//            }
//
//        }
        return Collections.EMPTY_LIST;
    }


}