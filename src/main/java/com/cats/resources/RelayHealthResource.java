package com.cats.resources;

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

import com.cats.service.RelayHealthCheck;
import com.cats.beans.HealthReport;
import com.cats.beans.HealthStatusBean;
import com.cats.service.RelayDeviceManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Relay Health Resource defines the RESTful API for the health status of the relay devices on a rack
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Relay Health", description = "RESTful API for the health status of the relay devices on a rack")
public class RelayHealthResource {

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Autowired
    private RelayDeviceManager relayDeviceManager;


    /**
     * Get the health status of the relay devices on a rack
     * @return HealthStatusBean
     */
    @Operation(summary = "Get Relay Health", description = "Get the health status of the relay devices on a rack")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HealthStatusBean.class)) }),
            @ApiResponse(responseCode = "400", description = "Cannot get relay health status.")
    })
    @RequestMapping(method=RequestMethod.GET, produces= "application/json")
    public HealthStatusBean getRelayHealth() {
        HealthStatusBean result = new HealthStatusBean();
        RelayHealthCheck relayhealthCheck = new RelayHealthCheck(relayDeviceManager.getRelayDevices());

        try {
            List<HealthReport> reports = relayhealthCheck.check();
            result.setHwDevicesHealthStatus(reports);
            result.setIsHealthy(this.isHealthy());
            if(result.getVersion() == null){
                result.setVersion(new HashMap<>());
            }
            result.getVersion().put("MS_VERSION",getMicroServiceVersion());
            result.setDependenciesHealthStatus(null);

        }
        catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }


    /**
     * Check if the relay devices are healthy
     * @return Boolean
     */
    public Boolean isHealthy() {
        return healthEndpoint.health().getStatus() == org.springframework.boot.actuate.health.Status.UP;
    }


    /**
     * Get the microservice version
     * @return String
     */
    public String getMicroServiceVersion() {
        String version = buildVersion;
        if(version == null || version.isEmpty()) {
            version = "development";
        }

        return version;
    }


}
