package com.pgelksync.app.service.impl;

import com.pgelksync.app.model.ProductDto;
import com.pgelksync.app.model.ProductEntity;
import com.pgelksync.app.repository.ProductEntityRepository;
import com.pgelksync.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductEntityRepository productEntityRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductDto add(ProductDto productDto) {
        final ProductEntity productEntity = modelMapper.map(productDto, ProductEntity.class);
        final ProductEntity saved = productEntityRepository.save(productEntity);
        return modelMapper.map(saved, ProductDto.class);
    }

    @Override
    public List<ProductDto> getAll() {
        final List<ProductEntity> products = productEntityRepository.findAll();
        return products.stream().map(product -> modelMapper.map(product, ProductDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsByName(String name) {
        final List<ProductEntity> products = productEntityRepository.findProductEntitiesByNameLike(name);
        return products.stream().map(product -> modelMapper.map(product, ProductDto.class)).collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductByName(String name) {
        final ProductEntity product = productEntityRepository.findProductEntityByName(name);
        return modelMapper.map(product, ProductDto.class);
    }
}
