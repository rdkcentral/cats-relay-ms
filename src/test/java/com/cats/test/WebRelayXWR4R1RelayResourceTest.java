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
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = RelayMsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "integrationtest")
public class WebRelayXWR4R1RelayResourceTest {

    private static final String ON_RESPONSE = "<?xml version='1.0' encoding='utf-8'?><datavalues><relay1state>1</relay1state><relay2state>1</relay2state><relay3state>1</relay3state><relay4state>1</relay4state></datavalues>";
    private static final String OFF_RESPONSE = "<?xml version='1.0' encoding='utf-8'?><datavalues><relay1state>0</relay1state><relay2state>0</relay2state><relay3state>0</relay3state><relay4state>0</relay4state></datavalues>";

    @Autowired
    private MockMvc mvc;

    public static MockWebServer relayMock1;
    public static MockWebServer relayMock2;

    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws IOException {
        relayMock1 = new MockWebServer();
        relayMock1.start(13180);
        relayMock2 = new MockWebServer();
        relayMock2.start(13181);
    }

    @After
    public void tearDown() throws IOException {
        relayMock1.shutdown();
        relayMock2.shutdown();
    }

    @Test
    public void getRelayStatusTest() throws Exception {
        relayMock1.enqueue(new MockResponse()
                .setBody(ON_RESPONSE)
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(get("/rack/1/relay"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("status", is("ON")));
    }

    @Test
    public void setRelayONOFFTest() throws Exception {
        //first POST call to relay to off it
        relayMock1.enqueue(new MockResponse().setResponseCode(200));

        //subsequent GET call for status
        relayMock1.enqueue(new MockResponse()
                .setBody(OFF_RESPONSE)
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(post("/rack/1/relay/off"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("status", is("OFF")));

        //first POST call to relay to on it
        relayMock1.enqueue(new MockResponse().setResponseCode(200));

        //subsequent GET call for status
        relayMock1.enqueue(new MockResponse()
                .setBody(ON_RESPONSE)
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(post("/rack/1/relay/on"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("status", is("ON")));
    }

    @Test
    public void setRelayONOFFOnRelay2Test() throws Exception {
        //first POST call to relay to off it
        relayMock2.enqueue(new MockResponse().setResponseCode(200));

        //subsequent GET call for status
        relayMock2.enqueue(new MockResponse()
                .setBody(OFF_RESPONSE)
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(post("/rack/5/relay/off"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("status", is("OFF")));

        //first POST call to relay to on it
        relayMock2.enqueue(new MockResponse().setResponseCode(200));

        //subsequent GET call for status
        relayMock2.enqueue(new MockResponse()
                .setBody(ON_RESPONSE)
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(post("/rack/5/relay/on"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("status", is("ON")));
    }

    @Test
    public void setRelayTimedTest() throws Exception {
        //first POST call to relay to off it
        relayMock2.enqueue(new MockResponse().setResponseCode(200));

        mvc.perform(post("/rack/5/relay/timed?duration=5"))
                .andExpect(status().isOk());
        RecordedRequest recordedRequest = relayMock2.takeRequest();
        Assert.assertEquals("GET", recordedRequest.getMethod());
        Assert.assertEquals("/stateFull.xml?relay1State=2&pulseTime1=5", recordedRequest.getPath());
    }

    @Test
    public void setRelayONOFFOnInvertedRelayTest() throws Exception {
        //first POST call to relay to off it
        relayMock2.enqueue(new MockResponse().setResponseCode(200));

        //subsequent GET call for status
        relayMock2.enqueue(new MockResponse()
                .setBody(OFF_RESPONSE)
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(post("/rack/8/relay/off"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("status", is("OFF")));

        //first POST call to relay to on it
        relayMock2.enqueue(new MockResponse().setResponseCode(200));

        //subsequent GET call for status
        relayMock2.enqueue(new MockResponse()
                .setBody(ON_RESPONSE)
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(post("/rack/8/relay/on"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("status", is("ON")));
    }

    @Test
    public void getRelayUnavailableTest() throws Exception {
        relayMock1.enqueue(new MockResponse()
                .setBody(ON_RESPONSE)
                .setBodyDelay(5, TimeUnit.SECONDS));

        mvc.perform(get("/rack/1/relay"))
                .andExpect(status().is(HttpStatus.SERVICE_UNAVAILABLE.value()))
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    public void getRelayBadResponseTest() throws Exception {
        relayMock1.enqueue(new MockResponse()
                .setBody("asdg")
                .addHeader("Content-Type", "text/xml"));

        mvc.perform(get("/rack/1/relay"))
                .andExpect(status().is(HttpStatus.EXPECTATION_FAILED.value()))
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

}
