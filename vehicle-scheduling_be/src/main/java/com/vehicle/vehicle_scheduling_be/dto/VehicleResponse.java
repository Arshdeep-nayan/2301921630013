package com.vehicle.vehicle_scheduling_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {

    private List<Vehicle> vehicles;
}