package com.cats.test;

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

import com.cats.RelayMsApplication;
import junit.runner.Version;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = RelayMsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "slotmappingtest")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SlotMappingResourceTest {

    @Autowired
    private MockMvc mvc;

    private static final String MAPPINGS = "{\"slots\":{\"1\":\"1:1\",\"2\":\"1:2\",\"3\":\"1:3\",\"4\":\"1:4\",\"5\":\"2:1\",\"6\":\"2:2\",\"7\":\"2:3\",\"8\":\"2:4\"}}";

    @Test
    @Order(1)
    public void test1_getSlotMappingsTest() throws Exception {
        System.out.println("JUnit version is: " + Version.id());
        mvc.perform(get("/mappings"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"slots\":{}}"));
    }

    @Test
    @Order(2)
    public void test2_getDefaultSlotMappingsTest() throws Exception {
        mvc.perform(get("/mappings/1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"1\":\"1:1\"}"));

        mvc.perform(get("/mappings/4"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"4\":\"1:4\"}"));

        mvc.perform(get("/mappings/5"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"5\":\"2:1\"}"));
    }

    @Test
    @Order(3)
    public void test3_setSlotMappingsTest() throws Exception {
        mvc.perform(post("/mappings").content(MAPPINGS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/mappings"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(MAPPINGS));
    }

    @Test
    @Order(4)
    public void test4_setSlotMappingsTestForSlot() throws Exception {
        mvc.perform(post("/mappings/1")
                .queryParam("mapping","2:1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/mappings/1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"1\":\"2:1\"}"));
    }

    @Test
    @Order(5)
    public void test5_deleteSlotMappingsTestForSlot() throws Exception {
        mvc.perform(delete("/mappings/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/mappings/1"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @Order(6)
    public void test6_deleteMappingsTest() throws Exception {
        mvc.perform(delete("/mappings").content(MAPPINGS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/mappings"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"slots\":{}}"));
    }
}
