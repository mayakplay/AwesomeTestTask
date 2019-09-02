package com.mayakplay.testtask.controller;

import com.mayakplay.testtask.annotation.CommandMethod;

public class TestControllerUppercase {

    @CommandMethod("TEST")
    private void testCommand() {
        System.out.println("TEST");
    }

}
