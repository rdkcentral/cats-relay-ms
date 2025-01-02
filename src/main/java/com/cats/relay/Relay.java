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

import org.w3c.dom.Node;

/**
 * Basic interface for relays.
 */
public interface Relay {
    /**
     * Turn relay on.
     */
    void on();
    /**
     * Turn relay off.
     */
    void off();
    /**
     * Turn relay on for N seconds and turn it off.
     * @param seconds 
     */
    void timed(Integer seconds);
    
    /**
     * Return state of relay.
     * @return - ON|OFF depending on state.
     */
    Status status();

    /**
     * Return the port status of the relay.
     * @param child - XML node of the relay.
     * @return - ON|OFF depending on state.
     */
    Status getPortStatus(Node child);

    /**
     * Get the port number of the relay.
     * @return - Integer.
     */
    Integer getPort();

    /**
     * Verify if relay device is inverted.
     * @return - Boolean.
     */
    Boolean isInverted();

}
