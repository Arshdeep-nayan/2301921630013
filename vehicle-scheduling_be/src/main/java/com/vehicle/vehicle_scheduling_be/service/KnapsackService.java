package com.vehicle.vehicle_scheduling_be.service;

import com.vehicle.vehicle_scheduling_be.dto.SelectedTask;
import com.vehicle.vehicle_scheduling_be.dto.Vehicle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KnapsackService {

    public List<SelectedTask> getOptimalTasks(List<Vehicle> vehicles,
                                              int mechanicHours) {

        int n = vehicles.size();

        int[][] dp = new int[n + 1][mechanicHours + 1];
        for (int i = 1; i <= n; i++) {

            int duration = vehicles.get(i - 1).getDuration();
            int impact = vehicles.get(i - 1).getImpact();

            for (int j = 0; j <= mechanicHours; j++) {

                if (duration <= j) {
                    dp[i][j] = Math.max(
                            impact + dp[i - 1][j - duration],
                            dp[i - 1][j]
                    );
                }
                else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        List<SelectedTask> selectedTasks = new ArrayList<>();

        int j = mechanicHours;

        for (int i = n; i > 0; i--) {

            if (dp[i][j] != dp[i - 1][j]) {

                Vehicle vehicle = vehicles.get(i - 1);

                selectedTasks.add(
                        new SelectedTask(
                                vehicle.getTaskID(),
                                vehicle.getDuration(),
                                vehicle.getImpact()
                        )
                );

                j -= vehicle.getDuration();
            }
        }

        Collections.reverse(selectedTasks);

        return selectedTasks;
    }

    public int getMaximumImpact(List<Vehicle> vehicles,
                                int mechanicHours) {

        int n = vehicles.size();

        int[][] dp = new int[n + 1][mechanicHours + 1];

        for (int i = 1; i <= n; i++) {

            int duration = vehicles.get(i - 1).getDuration();
            int impact = vehicles.get(i - 1).getImpact();

            for (int j = 0; j <= mechanicHours; j++) {

                if (duration <= j) {
                    dp[i][j] = Math.max(
                            impact + dp[i - 1][j - duration],
                            dp[i - 1][j]
                    );
                }
                else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        return dp[n][mechanicHours];
    }
}