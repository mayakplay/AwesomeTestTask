package com.mayakplay.testtask.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
public final class Sale {

    private int price;
    private int profit;
    private LocalDate date;

}