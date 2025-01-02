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


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines Inverse Web Relay XWR4R1 Port.
 * This class extends WebRelayXWR4R1Port and overrides isInverted method to return true.
 *
 * */
@Schema(name = "InverseWebRelayXWR4R1Port", description = "Defines Inverse Web Relay XWR4R1 Port")
public class InverseWebRelayXWR4R1Port extends WebRelayXWR4R1Port {

    public InverseWebRelayXWR4R1Port(RelayDevice device, Integer port, Long readTimeout) {
        super(device,port,readTimeout);
    }

    /**
     * Returns true as the port is inverted.
     * @return Boolean
     */
    @Override
    public Boolean isInverted() {
        return true;
    }
}
