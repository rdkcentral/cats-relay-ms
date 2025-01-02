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
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Node;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Relay port implementation for WebRelay-Quad-XWR4R1 devices.
 */
@Slf4j
@Schema(name = "WebRelayXWR4R1Port", description = "Relay port implementation for WebRelay-Quad-XWR4R1 devices")
public class WebRelayXWR4R1Port implements Relay {

    /**
     * Port number of the relay device.
     */
    protected Integer port;

    /**
     * Parent relay device.
     */
    protected RelayDevice parent;

    /**
     * Read timeout for the relay device.
     */
    protected Long readTimeout;

    /**
     * Constructor for WebRelayXWR4R1Port.
     * @param device RelayDevice
     * @param port Integer
     * @param readTimeout Long
     */
    public WebRelayXWR4R1Port(RelayDevice device, Integer port, Long readTimeout) {
        super();
        this.parent = device;
        this.port = port;
        this.readTimeout = readTimeout;
    }

    /**
     * Returns the relay port.
     * @return String
     */
    protected String getRelay() {
        return "relay" + port + "State";
    }

    /**
     * Returns the pulse time.
     * @return String
     */
    protected String getPulseTime() {
        return "pulseTime" + port;
    }

    /**
     * Returns the port number of the relay.
     * @return Integer
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Returns false as the port is not inverted.
     * @return Boolean
     */
    @Override
    public Boolean isInverted() {
        return false;
    }

    /**
     * Turn relay port on.
     */
    @Override
    public void on() {
        WebClient client = WebClient.create();
        log.info("url " + "http://" + parent.getHost() + ":" + parent.getPort());
        try {
            client.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host(parent.getHost())
                            .port(parent.getPort())
                            .path(WebRelayXWR4R1.STATE_PATH)
                            .queryParam(getRelay(), isInverted() ? 0 : 1)
                            .build())
                    .retrieve().onStatus(httpStatus -> httpStatus.isError(),
                    clientResponse ->
                            Mono.error(new BadDeviceException(clientResponse.statusCode()
                                    + HttpStatus.valueOf(clientResponse.statusCode().value()).getReasonPhrase())))
                    .bodyToMono(Void.class)
                    .share().block(Duration.ofSeconds(readTimeout));
        } catch (IllegalStateException ex) {
            throw new DeviceUnreachableException(ex);
        }
    }

    /**
     * Turn relay port off.
     */
    @Override
    public void off() {
        WebClient client = WebClient.create();
        try {
            client.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host(parent.getHost())
                            .port(parent.getPort())
                            .path(WebRelayXWR4R1.STATE_PATH)
                            .queryParam(getRelay(), isInverted() ? 1 : 0)
                            .build())
                    .retrieve().onStatus(httpStatus -> httpStatus.isError(),
                    clientResponse ->
                            Mono.error(new BadDeviceException(clientResponse.statusCode()
                                    + HttpStatus.valueOf(clientResponse.statusCode().value()).getReasonPhrase())))
                    .bodyToMono(Void.class)
                    .share().block(Duration.ofSeconds(readTimeout));
        } catch (IllegalStateException ex) {
            throw new DeviceUnreachableException(ex);
        }
    }

    /**
     * Turn relay port on for N seconds and turn it off.
     * @param seconds Integer
     */
    @Override
    public void timed(Integer seconds) {
        WebClient client = WebClient.create();
        try {
            client.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host(parent.getHost())
                            .port(parent.getPort())
                            .path(WebRelayXWR4R1.STATE_PATH)
                            .queryParam(getRelay(), 2)
                            .queryParam(getPulseTime(), seconds)
                            .build())
                    .header("Content-type", "text/xml")
                    .accept(MediaType.TEXT_PLAIN)
                    .retrieve().onStatus(httpStatus -> httpStatus.isError(),
                    clientResponse ->
                            Mono.error(new BadDeviceException(clientResponse.statusCode()
                                    + HttpStatus.valueOf(clientResponse.statusCode().value()).getReasonPhrase())))
                    .bodyToMono(Void.class)
                    .share().block(Duration.ofSeconds(readTimeout));
        } catch (IllegalStateException ex) {
            throw new DeviceUnreachableException(ex);
        }
    }

    /**
     * Return state of relay port.
     * @return Status
     */
    @Override
    public Status status() {
        return this.parent.status().get(port - 1);
    }

    /**
     * Return the port status of the relay.
     * @param child Node
     * @return Status
     */
    @Override
    public Status getPortStatus(Node child) {
        Status status;
        if (child.getTextContent().equals("0")) {
            status = Status.OFF;
        } else {
            status = Status.ON;
        }
        return status;
    }

}
