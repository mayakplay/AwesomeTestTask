package com.mayakplay.testtask.infrastructure;


import com.google.common.collect.ImmutableList;
import com.mayakplay.testtask.annotation.CommandDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class CommandDefinition {

    @NotNull
    @Getter
    private final Object controller;

    @NotNull
    @Getter
    private final Method commandMethod;

    @NotNull
    @Getter
    private final String description;

    @NotNull
    private final List<ArgumentDefinition> arguments;

    static CommandDefinition of(@NotNull Object controller, @NotNull Method method) {
        CommandDescription annotation = method.getAnnotation(CommandDescription.class);
        String description = annotation == null || annotation.value().isEmpty()
                ? "This is default command description ;)." : annotation.value();

        final List<ArgumentDefinition> argumentDefinitions = Arrays.stream(method.getParameters())
                .map(ArgumentDefinition::of)
                .collect(Collectors.toList());

        return new CommandDefinition(controller, method, description, argumentDefinitions);
    }

    List<ArgumentDefinition> getArgumentsList() {
        return ImmutableList.copyOf(arguments);
    }

}
