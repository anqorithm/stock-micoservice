package anqorithm.stocks.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = Map.of(
                "service", "Stock Market Microservice",
                "version", "1.0.0",
                "description", "A Spring Boot microservice for stock market data management",
                "timestamp", LocalDateTime.now(),
                "endpoints", Map.of(
                        "stocks", "/api/v1/stocks",
                        "health", "/api/v1/stocks/health",
                        "actuator", "/api/v1/actuator",
                        "auth", Map.of(
                                "login", "/api/v1/auth/login",
                                "register", "/api/v1/auth/register",
                                "validate", "/api/v1/auth/validate",
                                "me", "/api/v1/auth/me"
                        )
                ),
                "documentation", "https://github.com/anqorithm/stock-micoservice"
        );
        
        return ResponseEntity.ok(apiInfo);
    }
}