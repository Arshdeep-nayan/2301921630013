package com.vehicle.vehicle_scheduling_be.service;

import com.vehicle.vehicle_scheduling_be.Exception.ResourceNotFoundException;
import com.vehicle.vehicle_scheduling_be.dto.Depot;
import com.vehicle.vehicle_scheduling_be.dto.DepotResponse;
import com.vehicle.vehicle_scheduling_be.dto.ScheduleResponse;
import com.vehicle.vehicle_scheduling_be.dto.SelectedTask;
import com.vehicle.vehicle_scheduling_be.dto.Vehicle;
import com.vehicle.vehicle_scheduling_be.dto.VehicleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final DepotService depotService;
    private final VehicleService vehicleService;
    private final KnapsackService knapsackService;

    public ScheduleService(DepotService depotService,
                           VehicleService vehicleService,
                           KnapsackService knapsackService) {
        this.depotService = depotService;
        this.vehicleService = vehicleService;
        this.knapsackService = knapsackService;
    }

    public ScheduleResponse generateSchedule(int depotId) {

        DepotResponse depotResponse =
                depotService.getDepots();

        System.out.println(depotResponse.getDepots());

        Depot depot = depotResponse.getDepots()
                .stream()
                .filter(d -> d.getId() == depotId)
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Depot not found with id : "
                                        + depotId
                        )
                );

        int mechanicHours =
                depot.getMechanicHours();

        VehicleResponse vehicleResponse =
                vehicleService.getVehicles();

        List<Vehicle> vehicles =
                vehicleResponse.getVehicles();

        List<SelectedTask> selectedTasks =
                knapsackService.getOptimalTasks(
                        vehicles,
                        mechanicHours
                );

        int totalImpact =
                knapsackService.getMaximumImpact(
                        vehicles,
                        mechanicHours
                );

        int totalDuration =
                selectedTasks.stream()
                        .mapToInt(
                                SelectedTask::getDuration
                        )
                        .sum();

        ScheduleResponse response =
                new ScheduleResponse();

        response.setDepotId(depotId);
        response.setMechanicHours(mechanicHours);
        response.setTotalDuration(totalDuration);
        response.setTotalImpact(totalImpact);
        response.setSelectedTasks(selectedTasks);

        return response;
    }
}