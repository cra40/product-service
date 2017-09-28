package com.example.product.it;

import com.example.product.dao.builder.ProductBuilder;
import com.example.product.dao.domain.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductControllerIT {

    private static final String BASE_PATH = "/api/product";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testInsert() {
        ProductBuilder productBuilder = new ProductBuilder().withName("InsertOne").withDescription("InsertOne");

        ResponseEntity<Product> responseEntity = restTemplate.postForEntity(BASE_PATH, productBuilder.build(), Product.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Product responseProduct = responseEntity.getBody();
        assertEquals(productBuilder.withId(responseProduct.getId()).build(), responseProduct);
    }

    @Test
    public void testInsert_ForValidationException() {
        ProductBuilder productBuilder = new ProductBuilder().withName(" ").withDescription("test");
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(BASE_PATH, productBuilder.build(), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        String response = responseEntity.getBody();
        assertEquals("Name should not be blank!", response);
    }

    @Test
    public void testGetById_ShouldReturnObject() {
        Long id = 1l;
        Product expectedResponse = new ProductBuilder().withId(id).withName("Name One").withDescription("Description One").build();

        ResponseEntity<Product> responseEntity = restTemplate.getForEntity(BASE_PATH + "/" + id, Product.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    public void testGetById_ShouldThrowExceptionIfResourceNotFound() {
        Long id = 1000l;

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(BASE_PATH + "/" + id, String.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testGet_ShouldReturnAllObjects() {
        List expectedResponse = Arrays.asList(
                new ProductBuilder().withId(1l).withName("Name One").withDescription("Description One").build(),
                new ProductBuilder().withId(2l).withName("Name Two").build());

        ResponseEntity<Iterable<Product>> responseEntity =
                restTemplate.exchange(BASE_PATH,
                        HttpMethod.GET, null, new ParameterizedTypeReference<Iterable<Product>>() {});

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    public void testPut_ShouldUpdateAndReturnObject() {
        Long id = 1l;

        Product updateProduct = new ProductBuilder().withId(id).withName("UpdatedInsertOne").withDescription("InsertOne").build();

        ResponseEntity<Product> responseEntity = restTemplate.exchange(BASE_PATH + "/" + id, HttpMethod.PUT, new HttpEntity<Product>(updateProduct), Product.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updateProduct, responseEntity.getBody());
    }

    @Test
    public void testPut_ShouldThrowExceptionIfResourceNotFound() {
        Long id = 1000l;
        Product updateProduct = new ProductBuilder().withId(id).withName("UpdatedInsertOne").withDescription("InsertOne").build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(BASE_PATH + "/" + id, HttpMethod.PUT, new HttpEntity<Product>(updateProduct), String.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testDelete_ShouldDeleteObject() {
        Long id = 1l;

        ResponseEntity<Product> responseEntity = restTemplate.exchange(BASE_PATH + "/" + id, HttpMethod.DELETE, null, Product.class);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testDelete_ShouldThrowExceptionIfResourceNotFound() {
        Long id = 1000l;

        ResponseEntity<String> responseEntity = restTemplate.exchange(BASE_PATH + "/" + id, HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

}
