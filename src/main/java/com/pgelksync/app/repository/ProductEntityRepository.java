package com.pgelksync.app.repository;

import com.pgelksync.app.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, Integer> {
    ProductEntity findProductEntityByName(String name);
    List<ProductEntity> findProductEntitiesByNameLike(String name);
}
