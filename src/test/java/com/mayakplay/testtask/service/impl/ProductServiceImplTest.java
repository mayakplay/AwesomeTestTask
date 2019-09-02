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

}