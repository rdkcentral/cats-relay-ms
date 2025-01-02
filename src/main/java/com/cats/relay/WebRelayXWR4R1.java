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

import com.cats.exceptions.BadDeviceException;
import com.cats.exceptions.DeviceUnreachableException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Relay implementation for WebRelay-Quad devices.
 * @see <a href="http://www.controlbyweb.com/webrelay-quad/webrelay-quad_users_manual.pdf">WebRelay-Quad Documentation</a>
 */
@Slf4j
@Schema(name = "WebRelayXWR4R1", description = "Relay implementation for WebRelay-Quad-XWR4R1 devices")
public class WebRelayXWR4R1 implements RelayDevice {

    /**
     * Path to the stateFull.xml file on the relay device.
     */
    public static String STATE_PATH = "/stateFull.xml";

    /**
     * List of relays on the relay device.
     */
    protected List<Relay> relays = new ArrayList<>();

    /**
     * List of inverted relays on the relay device.
     */
    protected List<Boolean> invertRelays = new ArrayList<>();

    /**
     * Maximum number of ports on the relay device.
     */
    Integer maxPort;

    /**
     * IP address/host address of the relay device.
     */
    String host;

    /**
     * Port number of the relay device.
     */
    Integer port;

    /**
     * Device ID of the relay device.
     */
    String deviceId;

    /**
     * Type of the relay device.
     */
    String type;

    /**
     * Read timeout for the relay device.
     */
    Long readTimeout;


    /**
     * Constructor for WebRelayXWR4R1.
     * @param deviceId - Device ID of the relay device.
     * @param host - IP address/host address of the relay device.
     * @param port - Port number of the relay device.
     * @param maxPort - Maximum number of ports on the relay device.
     * @param type - Type of the relay device.
     * @param readTimeout - Read timeout for the relay device.
     */
    public WebRelayXWR4R1(String deviceId, String host, Integer port, Integer maxPort, String type, Long readTimeout) {
        super();
        this.host = host;
        this.port = port;
        this.maxPort = maxPort;
        this.deviceId = deviceId;
        this.type = type;
        this.readTimeout = readTimeout;
        for (int i = 1; i <= 4; i++) {
            //relays.add(new WebRelayXWR4R1Port(this, target, i,relayNames.get(i-1)));
            relays.add(new WebRelayXWR4R1Port(this, i,readTimeout));
        }
    }


    /**
     * Constructor for WebRelayXWR4R1.
     * @param deviceId - Device ID of the relay device.
     * @param host - IP address/host address of the relay device.
     * @param port - Port number of the relay device.
     * @param maxPort - Maximum number of ports on the relay device.
     * @param invertRelays - List of inverted relays on the relay device.
     * @param type - Type of the relay device.
     * @param readTimeout - Read timeout for the relay device.
     */
    public WebRelayXWR4R1(String deviceId, String host, Integer port, Integer maxPort,
                          List<Boolean> invertRelays, String type, Long readTimeout) {
        super();
        this.host = host;
        this.port = port;
        this.maxPort = maxPort;
        this.deviceId = deviceId;
        this.invertRelays = invertRelays;
        this.type = type;
        this.readTimeout = readTimeout;
        for (int i = 1; i <= maxPort; i++) {
            if (invertRelays != null
                    && (i-1) < this.invertRelays.size() // assume false for any relays not explicitly inverted
                    && this.invertRelays.get(i-1)) {
                //relays.add(new InverseWebRelayXWR4R1Port(this, target, i,relayNames.get(i-1)));
                relays.add(new InverseWebRelayXWR4R1Port(this, i, readTimeout));
            }
            else {
                //relays.add(new WebRelayXWR4R1Port(this, target, i, relayNames.get(i-1)));
                relays.add(new WebRelayXWR4R1Port(this, i, readTimeout));
            }
        }
    }


    /**
     * Returns the relay at the specified index.
     * @param i
     * @return
     */
    @Override
    public Relay relay(Integer i) {
        return relays.get(i - 1);
    }


    /**
     * Returns the list of relays.
     * @return List<Relay>
     */
    @Override
    public List<Relay> relays() {
        return relays;
    }
    
    /**
     * Returns the IP address/host address of this relay device.
     */
    @Override
    public String getHost() {
        return host;
    }

    /**
     * Returns the device ID of this relay device.
     */
    @Override
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Returns the port number of this relay device.
     */
    @Override
    public Integer getPort() {
        return port;
    }

    /**
     * Returns the maximum number of ports on this relay device.
     */
    @Override
    public Integer getMaxPort() {
        return maxPort;
    }

    /**
     * Returns the type of this relay device.
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Determines the status for each relay device.
     * @return List<Status>
     * @throws BadDeviceException
     */
    @Override
    public List<Status> status() {
        WebClient client = WebClient.create();

        try {
            String xml = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host(getHost())
                            .port(getPort())
                            .path(STATE_PATH)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            clientResponse ->
                                    Mono.error(new BadDeviceException(clientResponse.statusCode()
                                            + HttpStatus.valueOf(clientResponse.statusCode().value()).getReasonPhrase())))
                    .bodyToMono(String.class)
                    .share().block(Duration.ofSeconds(readTimeout));

            try {
                List<Status> relayStatuses = parse(xml);
                log.debug("STATUS={}", relayStatuses.toString());
                return relayStatuses;
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                log.error("Parsing exception on relay status", ex);
                throw new BadDeviceException("Parsing exception on relay status", ex);
            }
        }catch (IllegalStateException ex){
            throw new DeviceUnreachableException(ex);
        }
    }

    /**
     * Parses the XML response from the relay device.
     * @param xml - XML response from the relay device.
     * @return List<Status>
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    protected List<Status> parse(String xml) throws ParserConfigurationException, SAXException, IOException {
        log.debug("xml = " + xml);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        Document xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        NodeList nodeList = xmlDocument.getElementsByTagName("datavalues");
        NodeList children = nodeList.item(0).getChildNodes();
        List<Status> status = new ArrayList<>();
        
        for (int i = 0; i < relays.size(); i++) {
            status.add(relays.get(i).getPortStatus(children.item(i)));
        }
        return status;
    }
}
