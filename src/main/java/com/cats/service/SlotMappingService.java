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

import com.cats.exceptions.SlotMappingException;
import com.cats.relay.Relay;
import com.cats.relay.RelayDevice;
import com.cats.utils.SlotToPortMappings;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class SlotMappingService {

    /**
     * Slot to Port Mappings
     */
    private SlotToPortMappings slotToPortMappings;

    /**
     * ObjectMapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * File Path for Slot to Port Mappings
     */
    @Value("${slotMappingFilePath}")
    private String mappingFilePath;

    @Autowired
    RelayDeviceManager relayDeviceManager;

    @PostConstruct
    public void init() {
        initializePortMapping();
    }

    /**
     * Initialize Slot to Port Mappings by reading mappings.json file at provided mappingFilePath
     */
    private void initializePortMapping() {
        log.info("mappingFilePath " + mappingFilePath);
        try {
            File f = new File(mappingFilePath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
            }
            slotToPortMappings = mapper.readValue(new File(mappingFilePath), SlotToPortMappings.class);
            if (slotToPortMappings.getMappings().isEmpty()) {
                initializeDefaultMappings();
            }
        } catch (IOException ex) {
            log.error("Could not process slot mappings file, using default values: " + ex.getLocalizedMessage());
            slotToPortMappings = new SlotToPortMappings();
            initializeDefaultMappings();
        }
    }


    /**
     * Initializes default mappings
     */
    private void initializeDefaultMappings() {
        int slot = 1;
        for (RelayDevice relayDevice : relayDeviceManager.getRelayDevices()) {
            String deviceId = relayDevice.getDeviceId();
            for (int i = 0; i < relayDevice.relays().size(); i++) {
                slotToPortMappings.addMapping(Integer.toString(slot++), deviceId + ":" + (i + 1));
            }
        }
    }

    /**
     * Get Slot to Port Mappings
     *
     * @return SlotToPortMappings
     */
    public SlotToPortMappings getMappings() {
        return slotToPortMappings;
    }


    /**
     * Set Slot to Port Mappings
     *
     * @param mappings
     * @return SlotToPortMappings
     * @throws IOException
     */
    public SlotToPortMappings setMappings(Map<String, String> mappings) throws IOException {
        log.info("mappings = " + mappings);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFilePath))) {
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                if (!isValidMapping(entry.getValue())) {
                    log.error("Invalid mapping for slot " + entry.getKey() + ": " + entry.getValue());
                    writer.write(mapper.writeValueAsString(this.slotToPortMappings));
                    throw new SlotMappingException("Invalid mapping for slot " + entry.getKey() + ": " + entry.getValue());
                }
            }
            log.info("Setting new mapping: " + mapper.writeValueAsString(mappings));

            this.slotToPortMappings.setMappings(mappings);
            writer.write(mapper.writeValueAsString(this.slotToPortMappings));

            log.info("Slot to port mappings file updated");
            return this.slotToPortMappings;
        } catch (IOException ex) {
            log.error("Could not update slot mappings: " + ex.getLocalizedMessage());
            throw ex;
        }
    }

    /**
     * Removes all Slot to Port Mappings
     *
     * @throws IOException
     */
    public void removeMappings() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFilePath))) {
            log.info("Removing slot to port mappings");
            this.slotToPortMappings.removeMappings();

            log.info("Slot to port mappings have been removed");
            writer.write(mapper.writeValueAsString(this.slotToPortMappings));

        } catch (IOException ex) {
            log.error("Could not update slot mappings: " + ex.getLocalizedMessage());
            throw ex;
        }
    }

    /**
     * Get Mapping for a Slot
     *
     * @param slot
     * @return String
     * @throws SlotMappingException
     */
    public String getMapping(String slot) throws SlotMappingException {
        try {
            return slotToPortMappings.getMapping(slot);
        } catch (SlotMappingException ex) {
            log.error("Could not locate mapping for slot: " + slot);
            throw ex;
        }
    }

    /**
     * Get Relay Device at Slot
     *
     * @param slot
     * @return Relay
     * @throws SlotMappingException
     */
    public Relay getRelayDeviceAtSlot(Integer slot) throws SlotMappingException {
        String deviceIdWithOutlet = getMapping(String.valueOf(slot));
        log.info("deviceIdWithOutlet " + deviceIdWithOutlet);
        String[] deviceIdWithOutletArr = deviceIdWithOutlet.split(":");
        RelayDevice relayDevice = this.relayDeviceManager.getRelayDevices().stream()
                .filter(device -> device.getDeviceId().equalsIgnoreCase(deviceIdWithOutletArr[0])).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No relay with device id present " + deviceIdWithOutletArr[0]));

        return relayDevice.relay(Integer.parseInt(deviceIdWithOutletArr[1]));
    }

    /**
     * Set Mapping for a Slot
     *
     * @param slot
     * @param mapping
     * @return SlotToPortMappings
     * @throws IOException
     * @throws SlotMappingException
     */
    public SlotToPortMappings setMapping(String slot, String mapping) throws IOException, SlotMappingException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFilePath))) {
            if (!isValidMapping(mapping)) {
                log.error("Invalid mapping for slot " + slot + ": " + mapping);
                writer.write(mapper.writeValueAsString(this.slotToPortMappings));
                throw new SlotMappingException("Invalid mapping for slot " + slot + ": " + mapping);
            }
            log.info("Setting mapping on slot " + slot + " to " + mapping);

            if (this.slotToPortMappings.getMappings().containsKey(slot)) {
                this.slotToPortMappings.removeMapping(slot);
            }
            this.slotToPortMappings.addMapping(slot, mapping);

            writer.write(mapper.writeValueAsString(this.slotToPortMappings));

            log.info("Slot " + slot + " mapping updated");
            return this.slotToPortMappings;
        } catch (IOException | SlotMappingException ex) {
            log.error("Could not update slot mappings: " + ex.getLocalizedMessage());
            throw ex;
        }
    }

    /**
     * Remove Mapping for a Slot
     *
     * @param slot
     * @return SlotToPortMappings
     * @throws IOException
     * @throws SlotMappingException
     */
    public SlotToPortMappings removeMapping(String slot) throws IOException, SlotMappingException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFilePath))) {
            log.info("Removing mapping on slot " + slot);
            this.slotToPortMappings.removeMapping(slot);

            writer.write(mapper.writeValueAsString(this.slotToPortMappings));

            log.info("Slot " + slot + " mapping removed");

            return this.slotToPortMappings;
        } catch (IOException | SlotMappingException ex) {
            log.error("Could not update slot mappings: " + ex.getLocalizedMessage());
            throw ex;
        }
    }

    /**
     * Check if the mapping is valid
     *
     * @param deviceInfo
     * @return boolean
     */
    private boolean isValidMapping(String deviceInfo) {
        try {
            String[] deviceAndPort = deviceInfo.split(":");
            RelayDevice relayDevice = relayDeviceManager.getRelayDevices().get(Integer.parseInt(deviceAndPort[0]) - 1);
            if (Integer.parseInt(deviceAndPort[1]) <= relayDevice.getMaxPort()) {
                return true;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            log.warn("Invalid device info: " + deviceInfo);
            return false;
        }
        return false;
    }
}
