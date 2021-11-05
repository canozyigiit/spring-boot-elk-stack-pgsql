package com.pgelksync.app.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity implements Serializable {

    private static final long serialVersionUID = 1278321087868016151L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private Double price;

    @CreatedDate
    @Column(name = "created_date")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;
}
