package com.cats.service;

/*
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import java.util.ArrayList;
import java.util.List;

import com.cats.beans.HealthReport;
import com.cats.relay.Status;
import com.cats.relay.RelayDevice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RelayHealthCheck {

    /**
     * List of Relay Devices
     */
    List<RelayDevice> relayDevices;

    @Autowired
    public RelayHealthCheck(List<RelayDevice> relayDevices) {
        this.relayDevices = relayDevices;
    }


    /**
     * Check the health of the relay devices
     * @return List of HealthReport
     */
    public List<HealthReport> check() {
        List<HealthReport> reports = new ArrayList<>();
        for ( RelayDevice device :  relayDevices) {
            HealthReport report = new HealthReport();
            report.setDeviceId(device.getDeviceId());
            report.setEntity(device.getType());
            report.setHost(device.getHost());
            boolean healthCheckResult = this.healthCheck(device);
            if(healthCheckResult){
                report.setIsHealthy(true);
                report.setRemarks("Able to check outlet status");
                log.info("Relay device {} is healthy", device.getDeviceId());
            }else{
                report.setIsHealthy(false);
                report.setRemarks("Unable to check outlet status");
                log.info("Relay device {} is unhealthy", device.getDeviceId());
            }
            reports.add(report);
        }

        return reports ;
    }

    /**
     * Check the health of the relay device
     * @param device
     * @return Boolean
     */
    public Boolean healthCheck(RelayDevice device) {
        boolean healthStatus = false;
        int count = 0;
        try {
            for (Status status : device.status()) {
                if (status.equals(Status.ON) || status.equals(Status.OFF)) {
                    count++;
                }
                healthStatus = count == device.getMaxPort();
            }
        }catch (Exception e){
            healthStatus = false;
        }

        return healthStatus;
    }

}
