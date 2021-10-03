package com.pgelksync.app.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Data
@Document(indexName = "product_index")
public class ProductDocument {

    @Id
    private Integer id;
    private String name;
    private String description;
    private Double price;
}
