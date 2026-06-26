package com.vehicle.vehicle_scheduling_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {

    private int depotId;

    private int mechanicHours;

    private int totalDuration;

    private int totalImpact;

    private List<SelectedTask> selectedTasks;
}