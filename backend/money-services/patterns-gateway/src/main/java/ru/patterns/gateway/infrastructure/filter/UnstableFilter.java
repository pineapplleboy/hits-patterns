package ru.patterns.gateway.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.util.annotation.NonNull;
import ru.patterns.shared.model.response.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UnstableFilter extends OncePerRequestFilter {

    @Value("${unstable.enabled}")
    private Boolean enabled;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        boolean serviceIsUnstable = isServiceUnstable(uri);

        if (!enabled && !serviceIsUnstable) {
            filterChain.doFilter(request, response);
        }

        int minute = LocalDateTime.now().getMinute();
        var errorProbability = isEven(minute) ? 0.7 : 0.3;

        var random = ThreadLocalRandom.current().nextDouble();

        if (random < errorProbability) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

            var errorResponse = new ErrorResponse(500, "Ошибка сервера!");
            response.getWriter().write(mapper.writeValueAsString(errorResponse));

            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isEven(int number) {
        return number % 2 == 0;
    }

    private boolean isServiceUnstable(String uri) {
        return uri.startsWith("/core") ||
                uri.startsWith("/users") ||
                uri.startsWith("/credits");
    }
}
