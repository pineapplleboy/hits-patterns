package ru.patterns.gateway.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.shared.model.response.ErrorResponse;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    @RequestMapping("/{service}")
    public ResponseEntity<ErrorResponse> fallback(@PathVariable String service) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(503, "Сервис " + service + " временно недоступен"));
    }
}
