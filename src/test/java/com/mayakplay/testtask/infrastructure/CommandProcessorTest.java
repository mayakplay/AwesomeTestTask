package com.mayakplay.testtask.infrastructure;

import com.mayakplay.testtask.controller.TestController;
import com.mayakplay.testtask.controller.TestControllerUppercase;
import com.mayakplay.testtask.exception.CommandAlreadyExistsException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandProcessorTest {

    @Test
    public void shouldNotRegisterSimilarCommandsWithDifferentCase() {

        final CommandProcessor processor = new CommandProcessor();

        processor.registerProcessedController(new TestController());

        try {
            processor.registerProcessedController(new TestControllerUppercase());
        } catch (CommandAlreadyExistsException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void shouldNotRegisterSimilarCommands() {
        final CommandProcessor processor = new CommandProcessor();

        processor.registerProcessedController(new TestController());

        try {
            processor.registerProcessedController(new TestController());
            fail();
        } catch (CommandAlreadyExistsException ignored) {
        }
    }

}