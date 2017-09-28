package com.example.product.dao.repository;

import com.example.product.dao.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
