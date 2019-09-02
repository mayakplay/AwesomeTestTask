package com.mayakplay.testtask.service.impl;

import com.mayakplay.testtask.exception.NotEnoughProductionException;
import com.mayakplay.testtask.exception.ProductAlreadyExistsException;
import com.mayakplay.testtask.exception.ProductDoesNotExistsException;
import com.mayakplay.testtask.model.Batch;
import com.mayakplay.testtask.model.Sale;
import com.mayakplay.testtask.service.ProductService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ProductServiceImpl implements ProductService {

    private final Map<String, List<Batch>> batchMap = new HashMap<>();
    private final Map<String, List<Sale>> salesMap = new HashMap<>();

    @Override
    public void createProduct(String name) throws ProductAlreadyExistsException {
        if (batchMap.containsKey(name))
            throw new ProductAlreadyExistsException();

        batchMap.put(name, new ArrayList<>());
    }

    @Override
    public void purchaseBatch(String productName, int amount, int price, LocalDate date) {
        if (!batchMap.containsKey(productName))
            throw new ProductDoesNotExistsException();

        batchMap.get(productName).add(new Batch(amount, price, date));
    }

    @Override
    public void demandProduct(String productName, int amount, int price, LocalDate date) {
        final List<Batch> batchList = batchMap.get(productName);
        if (batchList == null)
            throw new ProductDoesNotExistsException();

        final List<Batch> collect = batchList.stream()
                .filter(batch -> batch.getDate().compareTo(date) < 1)
                .collect(Collectors.toList());

        int stockAmount = collect.stream()
                .mapToInt(Batch::getAmount)
                .sum();

        if (amount > stockAmount)
            throw new NotEnoughProductionException();

        int calculatedAmount = amount;
        int finalPrice = 0;
        for (final Batch batch : collect) {
            final int pullAmount = Math.min(calculatedAmount, batch.getAmount());
            calculatedAmount -= pullAmount;

            finalPrice += batch.pullProduction(pullAmount);

            if (calculatedAmount == 0)
                break;
        }

        final List<Sale> sales = salesMap.computeIfAbsent(productName, s -> new ArrayList<>());

        final int profit = amount * price - finalPrice;

        sales.add(new Sale(finalPrice, profit, date));
    }

    @Override
    public int getProfitFor(String productName, LocalDate date) throws ProductDoesNotExistsException {
        final List<Sale> sales = salesMap.get(productName);
        if (sales == null)
            throw new ProductDoesNotExistsException();

        return sales.stream()
                .filter(sale -> sale.getDate().compareTo(date) < 1)
                .mapToInt(Sale::getProfit)
                .sum();
    }

}