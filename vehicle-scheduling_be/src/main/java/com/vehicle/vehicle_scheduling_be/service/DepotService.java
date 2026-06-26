package com.vehicle.vehicle_scheduling_be.service;

import com.vehicle.vehicle_scheduling_be.auth.AuthService;
import com.vehicle.vehicle_scheduling_be.dto.DepotResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DepotService {

    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${depot.api}")
    private String depotApi;

    public DepotService(RestTemplate restTemplate,
                        AuthService authService) {
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    public DepotResponse getDepots() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authService.getAccessToken());

        HttpEntity<String> entity =
                new HttpEntity<>(headers);

        ResponseEntity<DepotResponse> response =
                restTemplate.exchange(
                        depotApi,
                        HttpMethod.GET,
                        entity,
                        DepotResponse.class
                );

        return response.getBody();
    }
}