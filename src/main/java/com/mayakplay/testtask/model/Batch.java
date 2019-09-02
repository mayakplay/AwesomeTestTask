package com.mayakplay.testtask.model;

import com.mayakplay.testtask.exception.NotEnoughProductionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
public final class Batch {

    private int amount;
    private int price;
    private LocalDate date;

    /**
     * Метод вытягивает определенное количество продукта,
     * возвращая цену
     *
     * @param amount количество продукта
     * @return общую цену продукта
     *
     * @throws NotEnoughProductionException если продукции недостаточно
     */
    public int pullProduction(int amount) throws NotEnoughProductionException {
        if (amount > this.amount) {
            throw new NotEnoughProductionException();
        }

        this.amount -= amount;
        return amount * price;
    }

}
