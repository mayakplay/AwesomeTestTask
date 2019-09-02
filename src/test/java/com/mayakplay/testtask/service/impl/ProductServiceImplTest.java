package com.mayakplay.testtask.service.impl;

import com.mayakplay.testtask.exception.ProductDoesNotExistsException;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class ProductServiceImplTest {

    @Test
    public void shouldNotPurchaseUnregisteredProduct() {
        final ProductServiceImpl productService = new ProductServiceImpl();

        try {
            productService.purchaseBatch("test", 1, 1, LocalDate.now());

            fail();
        } catch (ProductDoesNotExistsException ignored) {
        }
    }

    @Test
    public void shouldNotDemandUnregisteredProduct() {
        final ProductServiceImpl productService = new ProductServiceImpl();

        try {
            productService.demandProduct("test", 1, 1, LocalDate.now());

            fail();
        } catch (ProductDoesNotExistsException ignored) {
        }
    }

    @Test
    public void shouldCalculateCorrectPrice() {
        final ProductServiceImpl productService = new ProductServiceImpl();

        final String productName = "iphone";

        productService.createProduct(productName);
        productService.purchaseBatch(productName, 1, 1000, LocalDate.of(2017, 1, 1));
        productService.purchaseBatch(productName, 2, 2000, LocalDate.of(2017, 2, 1));

        productService.demandProduct(productName, 2, 5000, LocalDate.of(2017, 3, 1));

        int profitFor = productService.getProfitFor(productName, LocalDate.of(2017, 3, 2));

        assertEquals(7000, profitFor);
    }

}