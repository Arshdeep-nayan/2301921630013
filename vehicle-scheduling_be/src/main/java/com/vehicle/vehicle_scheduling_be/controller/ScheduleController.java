package com.vehicle.vehicle_scheduling_be.controller;

import com.vehicle.vehicle_scheduling_be.dto.DepotResponse;
import com.vehicle.vehicle_scheduling_be.dto.ScheduleResponse;
import com.vehicle.vehicle_scheduling_be.dto.VehicleResponse;
import com.vehicle.vehicle_scheduling_be.service.DepotService;
import com.vehicle.vehicle_scheduling_be.service.ScheduleService;
import com.vehicle.vehicle_scheduling_be.service.VehicleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    private final DepotService depotService;
    private final VehicleService vehicleService;
    private final ScheduleService scheduleService;

    public ScheduleController(DepotService depotService,
                              VehicleService vehicleService,
                              ScheduleService scheduleService) {
        this.depotService = depotService;
        this.vehicleService = vehicleService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/depots")
    public DepotResponse getDepots() {
        return depotService.getDepots();
    }

    @GetMapping("/vehicles")
    public VehicleResponse getVehicles() {
        return vehicleService.getVehicles();
    }

    @GetMapping("/schedules/{depotId}")
    public ScheduleResponse getSchedule(
            @PathVariable int depotId) {

        return scheduleService.generateSchedule(depotId);
    }
}