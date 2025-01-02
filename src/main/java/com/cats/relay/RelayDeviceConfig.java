package com.cats.relay;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a block of RelayDevice objects as defined in config.yml.
 */
@Data
@Schema(name = "RelayDeviceConfig", description = "Represents a block of RelayDevice objects as defined in config.yml")
public class RelayDeviceConfig {

    /**
     * The host (or starting ip) of the relay device.
     */
    private String host;

    /**
     * The relay type.
     */
    private String type;

    /**
     * The connection port of the relay.
     */
    private Integer port;

    /**
     * The maximum number of ports on relay device.
     */
    private Integer maxPort;

    /**
     * The relay deviceId.
     */
    private String deviceId;

    /**
     * The invertRelays list.
     */
    private List<Boolean> invertRelays = new ArrayList<>();



    /**
     * @return String
     * Returns the host (or starting ip) of the relay device.
     */
    @JsonProperty
    public String getHost() {
        return host;
    }

    /**
     * Sets the host (or starting ip) of the relay device.
     */
    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return Integer
     * Returns the connection port of the relay.
     */
    @JsonProperty
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the connection port of the relay.
     */
    @JsonProperty("port")
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return Integer
     * Returns the maximum number of ports on relay device.
     */
    @JsonProperty
    public Integer getMaxPort() {
        return maxPort;
    }

    /**
     * Sets the maximum number of ports on relay device.
     */
    @JsonProperty("maxPort")
    public void setMaxPort(Integer maxPort) {
        this.maxPort = maxPort;
    }

    /**
     * @return String
     * Returns the relay type.
     */
    @JsonProperty
    public String getType() {
        return type;
    }

    /**
     * Sets the relay type.
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return String
     * Returns the relay deviceId.
     */
    @JsonProperty
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the relay deviceId.
     */
    @JsonProperty("deviceId")
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return List<Boolean>
     * Returns the invertRelays list.
     */
    public List<Boolean> getInvertRelays() {
        return invertRelays;
    }

    /**
     * Sets the invertRelays list.
     */
    @JsonProperty("invertRelays")
    public void setInvertRelays(Boolean invertRelay, int idx) {
        this.invertRelays.set(idx, invertRelay);
    }

}
