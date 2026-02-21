package ru.patterns.credit.application.infrastructure;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.coyote.BadRequestException;
import ru.patterns.credit.application.common.constants.ErrorMessages;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationDeserializer extends JsonDeserializer<Duration> {

    private static final Pattern PATTERN = Pattern.compile("(?i)^(?=.*\\d)(\\d+d)?(\\d+h)?(\\d+m)?$");

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String rawString = jsonParser.getValueAsString();
        if (rawString == null) return null;

        String s = rawString.trim().toLowerCase();
        if (s.isEmpty()) return null;

        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new BadRequestException(ErrorMessages.INVALID_WRITE_OFF_DATE);
        }

        long days = parseDateTimePart(matcher.group(1));
        long hours = parseDateTimePart(matcher.group(2));
        long minutes = parseDateTimePart(matcher.group(3));

        Duration duration = Duration.ZERO
                .plusDays(days)
                .plusHours(hours)
                .plusMinutes(minutes);

        if (duration.isZero() || duration.isNegative()) {
            throw new BadRequestException(ErrorMessages.NEGATIVE_WRITE_OFF_DATE);
        }

        return duration;
    }

    private static long parseDateTimePart(String part) {
        if (part == null || part.isBlank()) return 0L;

        String number = part.substring(0, part.length() - 1);
        return Long.parseLong(number);
    }
}
