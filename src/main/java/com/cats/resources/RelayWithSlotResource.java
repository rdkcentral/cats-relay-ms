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

import com.cats.dto.RelayResponse;
import com.cats.service.RelayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Relay With Slot Resource defines the RESTful API for the relay devices on a rack
 */
@RestController
@RequestMapping("/")
@Tag(name = "Relay Control With Slot", description = "RESTful API for control of the relay devices on a rack given slot details.")
public class RelayWithSlotResource {

    @Autowired
    RelayService relayService;
    
    /**
     * Returns the status of the relay device and port (ON, OFF, or UNKNOWN).
     */
    @Operation(summary = "Get Relay Status", description = "Get the status of the relay device and port (ON, OFF, or UNKNOWN) given rack and slot information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RelayResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Cannot get relay status for given rack/slot.")
    })
    @GetMapping("{rack}/{slot}/relay/status")
    public RelayResponse status(@Parameter(description="Rack to query for relay status") @PathVariable("rack") String rack,
                                @Parameter(description="Slot to query for relay status") @PathVariable("slot") Integer slot) {

        RelayResponse retVal = new RelayResponse();
        retVal.setStatus(relayService.getRelayStatus(slot));
        return retVal;
    }
    
    /**
     * Turns the specified relay device port on or off.
     * @param rack Name of the rack
     * @param slot The slot number of the requested relay
     * @param operation on or off
     * @return Empty response if successful.
     */
    @Operation(summary = "Turn Relay On/Off", description = "Turn the specified relay device port on or off given rack, slot, and operation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RelayResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Cannot turn relay on/off for given rack/slot.")
    })
    @PostMapping("{rack}/{slot}/relay/{operation}")
    public RelayResponse turnOnOff(@Parameter(description="Rack to query for relay status") @PathVariable("rack") String rack,
                                   @Parameter(description="Slot to query for relay status") @PathVariable("slot") Integer slot,
                                   @Parameter(description="Operation to preform i.e. ON, OFF") @PathVariable("operation") String operation) {
        RelayResponse retVal = new RelayResponse();
        retVal.setStatus(relayService.turnOnOff(slot,operation));
        return retVal;
    }
    
    /**
     * Turns relay on for N seconds; omitting duration will use relay default
     * @param rack Name of the rack
     * @param slot The slot number of the requested  relay
     * @param duration The amount of seconds to turn relay on.
     * @return Empty response if successful.
     */
    @Operation(summary = "Turn Relay On for N Seconds", description = "Turn the specified relay device port on for N seconds given rack, slot, and duration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful"),
            @ApiResponse(responseCode = "400", description = "Cannot turn relay on for given rack/slot in timed interval.")
    })
    @PostMapping("{rack}/{slot}/relay/timed")
    public void timed(@Parameter(description="Rack to query for relay status") @PathVariable("rack") String rack,
                      @Parameter(description="Slot to query for relay status") @PathVariable("slot") Integer slot,
                      @Parameter(description = "Duration for timed operation. Default: 0") @RequestParam(value = "duration", defaultValue = "0") String duration) {
        try{
            int timed = Integer.parseInt(duration);
            relayService.timed(slot,timed);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Duration " + duration + " is not a valid integer.");
        }
   }
}
