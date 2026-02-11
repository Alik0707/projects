package com.example.product.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("products")   // можно опустить, если имя класса совпадает
public class Product {

    @Id
    private Long id;

    private String name;
    private double price;
    private String address;


}