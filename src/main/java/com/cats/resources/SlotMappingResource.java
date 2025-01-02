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

import com.cats.exceptions.SlotMappingException;
import com.cats.service.SlotMappingService;
import com.cats.utils.SlotToPortMappings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Slot Mapping Resource defines implementation for modifying slots -> device:port mappings
 */
@RestController
@RequestMapping("/mappings")
@Slf4j
@Tag(name = "Slot Mapping", description = "RESTful API for modifying slot mappings")
public class SlotMappingResource {

    @Autowired
    private SlotMappingService mappingService;

    @Operation(summary = "Get Slot Mappings", description = "Get all slot mappings for the rack")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SlotToPortMappings.class)) }),
            @ApiResponse(responseCode = "400", description = "Cannot fetch slot mappings for rack."),
            @ApiResponse(responseCode = "404", description = "Slot mappings not found for rack.")
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public SlotToPortMappings getMappings(){
        return mappingService.getMappings();
    }

    @Operation(summary = "Set Slot Mappings", description = "Set the slot mappings for the rack")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SlotToPortMappings.class)) }),
            @ApiResponse(responseCode = "400", description = "Cannot set provided slot mappings for rack.")
    })
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public SlotToPortMappings setMappings(@Parameter(description = "Provided slot mappings to set for rack") @RequestBody SlotToPortMappings slotToPortMappings) throws IOException, SlotMappingException {
        mappingService.setMappings(slotToPortMappings.getMappings());
        return mappingService.getMappings();
    }

    @Operation(summary = "Delete Slot Mappings", description = "Delete all slot mappings for the rack")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful"),
            @ApiResponse(responseCode = "400", description = "Cannot delete slot mappings for rack."),
            @ApiResponse(responseCode = "404", description = "Slot mappings not found for rack.")
    })
    @DeleteMapping("")
    public void deleteMappings() throws IOException{
        mappingService.removeMappings();
    }

    @Operation(summary = "Get Slot Mapping", description = "Get the existing slot mapping for the provided slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Cannot get slot mapping for provided slot."),
            @ApiResponse(responseCode = "404", description = "Mapping not found for slot.")
    })
    @GetMapping(value = "/{slot}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getMapping(@Parameter(description="Slot to query for mapping") @PathVariable("slot") String slot) throws SlotMappingException{
        Map<String, String> mapping = new HashMap<>();

        String deviceInfo = mappingService.getMapping(slot);
        mapping.put(slot, deviceInfo);

        return mapping;
    }

    @Operation(summary = "Set Slot Mapping for Slot", description = "Set the slot mapping for the provided slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SlotToPortMappings.class)) }),
            @ApiResponse(responseCode = "400", description = "Cannot set slot mapping for provided slot.")
    })
    @PostMapping(value = "/{slot}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SlotToPortMappings setMapping(@Parameter(description="Slot to set mapping on") @PathVariable("slot") String slot,
                                         @Parameter(description="Desired mapping to set for slot - i.e. 'device:port'") @RequestParam("mapping") String mapping)
            throws IOException, SlotMappingException{
        if(mapping == null){
            log.error("Mapping request for slot " + slot + " did not include query param");
            throw new SlotMappingException("Mapping request for slot " + slot + " did not include query param");
        }

        mappingService.setMapping(slot, mapping);
        return mappingService.getMappings();
    }

    @Operation(summary = "Remove Slot Mapping", description = "Remove the slot mapping for the provided slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SlotToPortMappings.class)) }),
            @ApiResponse(responseCode = "400", description = "Cannot remove slot mapping for provided slot."),
            @ApiResponse(responseCode = "404", description = "Mapping not found for slot.")
    })
    @DeleteMapping("/{slot}")
    public SlotToPortMappings removeMapping(@Parameter(description="Slot to remove mapping for") @PathVariable("slot") String slot)
            throws IOException, SlotMappingException{
        return mappingService.removeMapping(slot);
    }
}