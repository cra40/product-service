package com.example.product.controller;

import com.example.product.dao.domain.Product;
import com.example.product.dao.repository.ProductRepository;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/product")
public class ProductController {

    private static Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Product insert(@Valid @RequestBody Product product) {
        return productRepository.save(product);
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Iterable<Product> getAll() {
        return productRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Product getById(@PathVariable Long id) {
        MDC.put("id", id);
        LOG.info("Starting get");
        Optional<Product> maybeProduct = productRepository.findById(id);
        Product product = maybeProduct.orElseThrow(ResourceNotFoundException::new);
        LOG.info("Get ouput: " + product.toString());
        MDC.clear();
        return product;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody Product put(@PathVariable Long id, @Valid @RequestBody Product product) {
        Optional<Product> maybeProduct = productRepository.findById(id)
                .filter(product1 -> product1.getId() == product.getId());
        return maybeProduct.map(product1 -> productRepository.save(product)).orElseThrow(ResourceNotFoundException::new);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Optional<Product> maybeProduct = productRepository.findById(id);
        productRepository.delete(maybeProduct.orElseThrow(ResourceNotFoundException::new));
    }

}
