package com.pgelksync.app.service;

import com.pgelksync.app.model.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto add(ProductDto productDto);
    List<ProductDto> getAll();
    List<ProductDto> getProductsByName(String name);
    ProductDto getProductByName(String name);
}
