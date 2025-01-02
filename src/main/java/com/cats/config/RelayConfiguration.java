package com.cats.config;

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

import com.cats.relay.RelayDeviceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration for the relay devices on the rack, pulled from the given configuration file
 */
@Component
@ConfigurationProperties
@PropertySource(value="file:./relayms/config.yml", factory =YamlPropertySourceFactory.class)
public class RelayConfiguration
{
	List<RelayDeviceConfig> relays;

	String rackIp;
	public List<RelayDeviceConfig> getRelays() {
		return relays;
	}
	public void setRelays(List<RelayDeviceConfig> relays) {
		this.relays = relays;
	}

	public String getRackIp() {
		return rackIp;
	}

	public void setRackIp(String rackIp) {
		this.rackIp = rackIp;
	}
	
}
