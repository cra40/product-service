package com.example.product.controller;

import com.example.product.dao.builder.ProductBuilder;
import com.example.product.dao.domain.Product;
import com.example.product.dao.repository.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionSystemException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductControllerTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductController controller = new ProductController();

    @Test
    public void testInsert_ShouldCreateAndReturnObject() {
        Product product = new ProductBuilder().withName("InsertOne").withDescription("InsertOne").build();
        when(productRepository.save(product)).thenReturn(product);

        Product actualResponse = controller.insert(product);

        verify(productRepository, times(1)).save(product);
        assertEquals(product, actualResponse);
    }

    @Test(expected = TransactionSystemException.class)
    public void testInsert_ShouldReturnValidationExceptionWhenNameisBlank() {
        Product product = new ProductBuilder().withName(" ").withDescription("test").build();
        when(productRepository.save(product)).thenThrow(TransactionSystemException.class);

        controller.insert(product);

        verify(productRepository, times(0)).save(product);
    }

    @Test
    public void testGetById_ShouldReturnObject() {
        Long id = 1l;
        Product product = new ProductBuilder().withId(id).withName("Name One").withDescription("Description One").build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product actualResponse = controller.getById(id);

        verify(productRepository, times(1)).findById(id);
        assertEquals(product, actualResponse);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetById_ShouldThrowExceptionIfResourceNotFound() {
        Long id = 1000l;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        controller.getById(id);
    }

    @Test
    public void testGet_ShouldReturnAllObjects() {
        List expectedResponse = Arrays.asList(
                new ProductBuilder().withId(1l).withName("Name One").withDescription("Description One").build(),
                new ProductBuilder().withId(2l).withName("Name Two").build());
        when(productRepository.findAll()).thenReturn(expectedResponse);

        Iterable<Product> actualResponse = controller.getAll();

        verify(productRepository, times(1)).findAll();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testPut_ShouldUpdateAndReturnObject() {
        Long id = 1l;

        Product product = new ProductBuilder().withId(id).withName("InsertOne").withDescription("InsertOne").build();
        Product updateProduct = new ProductBuilder().withId(id).withName("UpdatedInsertOne").withDescription("InsertOne").build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(updateProduct)).thenReturn(updateProduct);

        Product actualResponse = controller.put(id, updateProduct);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(updateProduct);
        assertEquals(updateProduct, actualResponse);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testPut_ShouldThrowExceptionIfResourceNotFound() {
        Long id = 1000l;
        Product updateProduct = new ProductBuilder().withId(id).withName("UpdatedInsertOne").withDescription("InsertOne").build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        controller.put(id, updateProduct);

        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void testDelete_ShouldDeleteObject() {
        Long id = 1l;

        Product product = new ProductBuilder().withId(id).withName("InsertOne").withDescription("InsertOne").build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        controller.delete(id);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).delete(product);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDelete_ShouldThrowExceptionIfResourceNotFound() {
        Long id = 1000l;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        controller.delete(id);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(0)).delete(any(Product.class));
    }
}
