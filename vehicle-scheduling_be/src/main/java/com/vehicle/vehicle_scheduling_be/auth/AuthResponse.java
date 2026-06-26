package com.vehicle.vehicle_scheduling_be.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token_type;
    private String access_token;
    private long expires_in;
}