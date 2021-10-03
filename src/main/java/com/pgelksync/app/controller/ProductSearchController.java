package com.pgelksync.app.controller;

import com.pgelksync.app.model.ProductDto;
import com.pgelksync.app.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/product/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ElasticsearchService elasticsearchService;


    @GetMapping("/v2/name/{query}")
    public List<ProductDto> getProductsByName(@PathVariable String query) {
        return elasticsearchService.getProductsByName(query);
    }

    @GetMapping("/string-query/name/{query}")
    public List<ProductDto> getProductsByNameWithStringQuery(@PathVariable String query) {
        return elasticsearchService.getProductsByNameWithStringQuery(query);
    }

    @GetMapping("/criteria-query/{query}")
    public List<ProductDto> getProductsByNameOrDescriptionWithCriteriaQuery(@PathVariable String query) {
        return elasticsearchService.getProductsByNameOrDescriptionWithCriteriaQuery(query);
    }

    @GetMapping("/criteria-query/price")
    public List<ProductDto> getProductsByPriceWithCriteriaQuery(@RequestParam Double minPrice, Double maxPrice) {
        return elasticsearchService.getProductsByPriceWithCriteriaQuery(minPrice, maxPrice);
    }

    @GetMapping("/native-query/name/{query}")
    public List<ProductDto> getProductsByNameWithNativeSearchQuery(@PathVariable String query) {
        return elasticsearchService.getProductsByNameWithNativeSearchQuery(query);
    }

    @GetMapping("/fuzzy/name/{query}")
    public List<ProductDto> getProductsByNameOrDescriptionWithFuzzy(@PathVariable String query) {
        return elasticsearchService.getProductsByNameOrDescriptionWithFuzzy(query);
    }

    @GetMapping("/suggestion/{query}")
    public List<ProductDto> getProductSuggestions(@PathVariable String query) {
        return elasticsearchService.getProductSuggestions(query);
    }

    @GetMapping("/suggestion-all/{query}")
    public List<ProductDto> getProductSuggestionsByAllFields(@PathVariable String query) {
        return elasticsearchService.getProductSuggestionsByAllFields(query);
    }

    @GetMapping("/params")
    public List<ProductDto> getProductByParameters(@RequestParam String name,
                                                   @RequestParam String description,
                                                   @RequestParam Double minPrice,
                                                   @RequestParam Double maxPrice) {
        return elasticsearchService.getProductByParameters(name, description, minPrice, maxPrice);
    }
}
