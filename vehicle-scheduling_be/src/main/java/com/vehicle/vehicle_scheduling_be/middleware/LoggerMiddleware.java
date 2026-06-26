package com.vehicle.vehicle_scheduling_be.middleware;

import com.vehicle.vehicle_scheduling_be.auth.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoggerMiddleware {

    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${logging.api.url}")
    private String logApi;

    public LoggerMiddleware(RestTemplate restTemplate,
                            AuthService authService) {
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    public void log(String stack,
                    String level,
                    String packageName,
                    String message) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authService.getAccessToken());

        Map<String, String> body = new HashMap<>();

        body.put("stack", stack);
        body.put("level", level);
        body.put("package", packageName);
        body.put("message", message);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            logApi,
                            entity,
                            String.class
                    );

            System.out.println(response.getBody());

        }
        catch (Exception e) {

            System.out.println(
                    "Logging Failed : "
                            + e.getMessage()
            );
        }
    }
}