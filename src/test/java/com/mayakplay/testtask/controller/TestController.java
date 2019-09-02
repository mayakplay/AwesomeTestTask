package com.mayakplay.testtask.controller;

import com.mayakplay.testtask.annotation.CommandMethod;

public class TestController {

    @CommandMethod("Test")
    private void testCommand() {
        System.out.println("test");
    }

}
