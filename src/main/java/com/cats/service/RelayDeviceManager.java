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

import com.cats.config.RelayConfiguration;
import com.cats.relay.RelayDevice;
import com.cats.relay.RelayDeviceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RelayDeviceManager {

    @Autowired
    RelayConfiguration relayConfiguration;

    @Autowired
    RelayBuilder relayBuilder;

    /**
     * List of Relay Devices
     */
    List<RelayDevice> relayDevices = new ArrayList<>();


    /**
     * Initialize the relay devices
     */
    @PostConstruct
    public void init(){
        if ( relayConfiguration.getRelays() == null ) {
            throw new IllegalArgumentException("Relay configuration is null. Please configure relay devices.");
        }

        for(RelayDeviceConfig device: relayConfiguration.getRelays()){

            if(device.getType() == null
                    || device.getDeviceId() == null
                    || device.getHost() == null
                    || device.getPort() == null
                    || device.getMaxPort() == null) {
                throw new IllegalArgumentException("Some required properties are null "+device);
            }

            relayDevices.add(relayBuilder.get(device.getType(),device.getDeviceId(),device.getHost(), device.getPort(),
                    device.getMaxPort(),device.getInvertRelays()));
        }

        log.info(String.valueOf(relayDevices));
    }

    /**
     * Get the list of relay devices
     * @return List of RelayDevice
     */
    public List<RelayDevice> getRelayDevices(){
        return relayDevices;
    }
}
