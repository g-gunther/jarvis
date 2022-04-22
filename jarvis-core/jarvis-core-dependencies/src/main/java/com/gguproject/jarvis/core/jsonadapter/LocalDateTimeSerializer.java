package com.gguproject.jarvis.core.jsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject output = new JsonObject();

        JsonObject date = new JsonObject();
        date.addProperty("day", src.getDayOfMonth());
        date.addProperty("month", src.getMonthValue());
        date.addProperty("year", src.getYear());

        JsonObject time = new JsonObject();
        time.addProperty("hour", src.getHour());
        time.addProperty("minute", src.getMinute());
        time.addProperty("second", src.getSecond());

        output.add("date", date);
        output.add("time", time);
        return output;
    }
}