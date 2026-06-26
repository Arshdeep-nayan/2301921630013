package com.vehicle.vehicle_scheduling_be.service;

import com.vehicle.vehicle_scheduling_be.auth.AuthService;
import com.vehicle.vehicle_scheduling_be.dto.VehicleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VehicleService {

    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${vehicle.api}")
    private String vehicleApi;

    public VehicleService(RestTemplate restTemplate,
                          AuthService authService) {
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    public VehicleResponse getVehicles() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authService.getAccessToken());

        HttpEntity<String> entity =
                new HttpEntity<>(headers);

        ResponseEntity<VehicleResponse> response =
                restTemplate.exchange(
                        vehicleApi,
                        HttpMethod.GET,
                        entity,
                        VehicleResponse.class
                );

        return response.getBody();
    }
}