package ru.patterns.credit.infrastructure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (duration == null) {
            jsonGenerator.writeNull();
            return;
        }

        long seconds = duration.getSeconds();

        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) result.append(days).append(" дней ");
        if (hours > 0) result.append(hours).append(" часов ");
        if (minutes > 0) result.append(minutes).append(" минут ");

        if (result.isEmpty()) {
            result.append("0 минут");
        }

        jsonGenerator.writeString(result.toString().trim());
    }
}
