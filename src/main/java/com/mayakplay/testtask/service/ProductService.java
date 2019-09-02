package com.mayakplay.testtask.service;

import com.mayakplay.testtask.exception.NotEnoughProductionException;
import com.mayakplay.testtask.exception.ProductAlreadyExistsException;
import com.mayakplay.testtask.exception.ProductDoesNotExistsException;

import java.time.LocalDate;

public interface ProductService {

    void createProduct(String name) throws ProductAlreadyExistsException;

    void purchaseBatch(String productName, int amount, int price, LocalDate date) throws ProductDoesNotExistsException;

    void demandProduct(String productName, int amount, int price, LocalDate date)
            throws ProductDoesNotExistsException, NotEnoughProductionException;

    int getProfitFor(String productName, LocalDate date) throws ProductDoesNotExistsException;
}
