package com.mayakplay.testtask.infrastructure;

import com.mayakplay.testtask.type.ArgumentMistakeType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ArgumentProcessingDescription {

    @NotNull
    private final ArgumentMistakeType mistakeType;

    @Nullable
    private final Object object;

    @Nullable
    private final String errorDescription;

    @NotNull
    static ArgumentProcessingDescription ok(@NotNull Object object) {
        return new ArgumentProcessingDescription(ArgumentMistakeType.OK, object, null);
    }

    @NotNull
    static ArgumentProcessingDescription error(@NotNull String errorDescription) {
        return new ArgumentProcessingDescription(ArgumentMistakeType.ERROR, null, errorDescription);
    }

    @NotNull
    static ArgumentProcessingDescription notSpecified(@NotNull String commandName) {
        String errorDescription = String.format(" Argument \"%s\" is not specified!", commandName);
        return new ArgumentProcessingDescription(ArgumentMistakeType.NOT_SPECIFIED, null, errorDescription);
    }

}
