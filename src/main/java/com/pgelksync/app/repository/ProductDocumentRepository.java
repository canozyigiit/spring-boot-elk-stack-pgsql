package com.pgelksync.app.repository;

import com.pgelksync.app.model.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Integer> {
    List<ProductDocument> findByName(String name);
}
