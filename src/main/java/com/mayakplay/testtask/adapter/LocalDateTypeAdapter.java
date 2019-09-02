package com.mayakplay.testtask.adapter;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    private final DateTimeFormatter dateTimeFormatter;

    public LocalDateTypeAdapter(String pattern) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        jsonWriter.value(localDate.toString());
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        return LocalDate.parse(jsonReader.nextString(), dateTimeFormatter);
    }

}