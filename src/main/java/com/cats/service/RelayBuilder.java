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

import com.cats.relay.RelayDevice;
import com.cats.relay.WebRelayXWR4R1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelayBuilder {

    /**
     * Read Timeout
     */
    @Value("${deviceReadTimeout}")
    Long readTimeout;


    /**
     * Get the relay device
     * @param type
     * @param deviceId
     * @param host
     * @param port
     * @param maxPorts
     * @return RelayDevice
     */
    public RelayDevice get(String type, String deviceId, String host, Integer port, Integer maxPorts) {
        RelayDevice device;
        switch (type) {
            case "XWR4R1":
                device = new WebRelayXWR4R1(deviceId, host, port, maxPorts, type, readTimeout);
                break;
            default:
                throw new IllegalArgumentException("Cannot identify relay device");
        }
        return device;
    }

    /**
     * Get the relay device
     * @param type
     * @param deviceId
     * @param host
     * @param port
     * @param maxPorts
     * @param invertRelays
     * @return RelayDevice
     */
    public RelayDevice get(String type, String deviceId, String host, Integer port, Integer maxPorts, List<Boolean> invertRelays) {
        RelayDevice device;
        switch (type) {
            case "XWR4R1":
                device = new WebRelayXWR4R1(deviceId, host, port, maxPorts, invertRelays, type, readTimeout);
                break;
            default:
                throw new IllegalArgumentException("Cannot identify relay device");
        }
        return device;
    }
}