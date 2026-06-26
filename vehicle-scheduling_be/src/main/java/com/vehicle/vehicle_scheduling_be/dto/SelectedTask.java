package com.vehicle.vehicle_scheduling_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectedTask {

    private String taskID;
    private int duration;
    private int impact;
}