package com.mayakplay.testtask.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Argument {

    /**
     * @return имя аргумента
     */
    String value();

    /**
     * @return сообщение, которое будет выведено при ошибке,
     *      выведет стандартное, если строка пустая ""
     */
    String onError() default "";

}
