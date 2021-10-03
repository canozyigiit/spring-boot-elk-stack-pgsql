package com.pgelksync.app.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDto implements Serializable {
    private static final long serialVersionUID = -4220461526849197768L;
    private Integer id;
    private String name;
    private String description;
    private Double price;
}
