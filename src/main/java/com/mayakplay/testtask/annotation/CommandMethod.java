package com.mayakplay.testtask.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface CommandMethod {

    /**
     * @return декларацию <b>имени</b> параметра
     */
    String value();

}
