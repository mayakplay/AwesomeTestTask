package com.mayakplay.testtask.infrastructure;

import com.mayakplay.testtask.annotation.Argument;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ArgumentDefinition {

    @NotNull
    private final String name;

    @NotNull
    private final Type type;

    @Nullable
    private final String errorDescription;

    @NotNull
    private final String originalParameterName;

    static ArgumentDefinition of(@NotNull Parameter parameter) {
        final Argument annotation = parameter.getAnnotation(Argument.class);
        final String argumentName = annotation != null ? annotation.value() : parameter.getType().getSimpleName();
        final String errorDescription = annotation != null ? annotation.onError() : null;

        return new ArgumentDefinition(argumentName, parameter.getType(), errorDescription, parameter.getName());
    }

}
