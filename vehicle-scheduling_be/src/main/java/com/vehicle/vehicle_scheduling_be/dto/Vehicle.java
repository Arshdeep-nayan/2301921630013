package com.vehicle.vehicle_scheduling_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    private String TaskID;
    private int Duration;
    private int Impact;
}