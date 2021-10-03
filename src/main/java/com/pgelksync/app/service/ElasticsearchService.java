package com.pgelksync.app.service;

import com.pgelksync.app.model.ProductDto;

import java.util.List;

public interface ElasticsearchService {
    List<ProductDto> getProductsByName(String query);
    List<ProductDto> getProductsByNameWithStringQuery(String query);
    List<ProductDto> getProductsByNameOrDescriptionWithCriteriaQuery(String query);
    List<ProductDto> getProductsByPriceWithCriteriaQuery(Double minPrice, Double maxPrice);
    List<ProductDto> getProductsByNameWithNativeSearchQuery(String query);
    List<ProductDto> getProductsByNameOrDescriptionWithFuzzy(String query);
    List<ProductDto> getProductSuggestions(String query);
    List<ProductDto> getProductSuggestionsByAllFields(String query);
    List<ProductDto> getProductByParameters(String name, String description, Double minPrice, Double maxPrice);
}
