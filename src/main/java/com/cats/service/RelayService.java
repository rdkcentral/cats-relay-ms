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
import com.cats.relay.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RelayService {

    @Autowired
    SlotMappingService slotMappingService;

    /**
     * Returns the status of the relay device and port (ON, OFF, or UNKNOWN).
     */
    public Status getRelayStatus(Integer slot) {
        Relay relay = getRelayAtSlot(slot);
        return relay.status();
    }

    /**
     * Turns the relay device on / off.
     * @return Status of the relay device and port (ON, OFF, or UNKNOWN).
     */
    public Status turnOnOff(Integer slot, String operation) {
        operation = operation.toUpperCase();
        Relay relay = getRelayAtSlot(slot);

        switch (operation) {
            case "ON":
                try {
                    relay.on();
                    log.info("Relay device for slot {} is turned on", slot);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Bad slot: " + slot + " is not valid. \n" + e);
                }
            case "OFF":
                try {
                    relay.off();
                    log.info("Relay device for slot {} is turned off", slot);
                    break;
                } catch (Exception e) {
                    throw new IllegalArgumentException("Bad slot: " + slot + " is not valid. \n" + e);
                }
            default:
                throw new IllegalArgumentException(operation + " is an invalid operation setting. ");
        }
        return relay.status();
    }

    /**
     * Turns the relay device on for a specified duration.
     */
    public void timed(Integer slot, Integer duration) {
        Relay relay = getRelayAtSlot(slot);
        relay.timed(duration);
        log.info("Setting relay device for slot {} to turn on for {} seconds", slot, duration);
    }

    /**
     * Returns the relay device at the specified slot.
     */
    private Relay getRelayAtSlot(Integer slot) throws SlotMappingException {
        Relay relay = slotMappingService.getRelayDeviceAtSlot(slot);
        if (relay == null) {
            throw new SlotMappingException("No relay found on slot " + slot);
        }
        return relay;
    }
}
