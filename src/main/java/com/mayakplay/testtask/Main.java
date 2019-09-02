package com.mayakplay.testtask;

import com.mayakplay.testtask.controller.ProductController;
import com.mayakplay.testtask.infrastructure.CommandProcessor;
import com.mayakplay.testtask.service.ProductService;
import com.mayakplay.testtask.service.impl.ProductServiceImpl;

import java.util.Scanner;

/**
 * @see CommandProcessor содержит логику обработки команд
 * @see ProductService содержит бизнес-логику
 */
public final class Main {

    private final Scanner scanner;
    private final CommandProcessor commandProcessor;

    private Main() {
        this.scanner = new Scanner(System.in);
        this.commandProcessor = new CommandProcessor();

        ProductService productService = new ProductServiceImpl();
        ProductController productController = new ProductController(productService);

        commandProcessor.registerProcessedController(productController);
    }

    /**
     * Запускает обработку команд
     */
    private void startProcessing() {
        commandProcessor.startProcessing();
        while (scanner.hasNext()) {
            System.out.println(
                    commandProcessor.processCommand(scanner.nextLine())
            );
        }
    }

    public static void main(String[] args) {
        final Main main = new Main();

        main.startProcessing();
    }

}