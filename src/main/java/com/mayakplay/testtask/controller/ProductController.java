package com.mayakplay.testtask.controller;

import com.mayakplay.testtask.annotation.Argument;
import com.mayakplay.testtask.annotation.CommandMethod;
import com.mayakplay.testtask.service.ProductService;
import lombok.AllArgsConstructor;

import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@AllArgsConstructor
public final class ProductController {

    private final ProductService productService;

    @CommandMethod("NEW")
    public void createProduct(@Argument("name") String productName) {
        productService.createProduct(productName);
    }

    @CommandMethod("PURCHASE")
    public void purchaseProduct(
            @Argument("name") String productName,
            @Positive @Argument("amount") int amount,
            @Positive @Argument("price") int price,
            @PastOrPresent @Argument("date") LocalDate date
    ) {
        productService.purchaseBatch(productName, amount, price, date);
    }

    @CommandMethod("DEMAND")
    public void demandProduct(
            @Argument("name") String productName,
            @Positive @Argument("amount") int amount,
            @Positive @Argument("price") int price,
            @PastOrPresent @Argument("date") LocalDate date
    ) {
        productService.demandProduct(productName, amount, price, date);
    }

    @CommandMethod("SALESREPORT")
    public int getReport(
            @Argument("name") String productName,
            @PastOrPresent @Argument("date") LocalDate date
    ) {
        return productService.getProfitFor(productName, date);
    }

}
