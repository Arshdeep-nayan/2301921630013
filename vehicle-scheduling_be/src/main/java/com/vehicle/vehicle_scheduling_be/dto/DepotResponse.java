package com.vehicle.vehicle_scheduling_be.dto;

import com.vehicle.vehicle_scheduling_be.dto.Depot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepotResponse {

    private List<Depot> depots;
}