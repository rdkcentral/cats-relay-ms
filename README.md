# Relay Microservice

The Relay Microservice is a RESTful service that provides an interface to control relay devices hosted on a rack. This microservice supports control of electrical devices through sending simple commands to connected relays in order to activate a given switch (or group of switches). 

## Development Setup

Build using `mvn clean install`

Run using `java -jar target/relay-ms.jar`


### Running Locally

`mvn spring-boot:run`


Once running, application will be locally accessible at http://localhost:9090/relay/


<br><br>

## Building

Build the project using `mvn clean install`.
Copy the built jar file into the corresponding directory structure as required by the Dockerfile.

    docker build -t="/relayms" .


<br><br>


## Deploying

Copy the `config.yml` file to the `/opt/data/relayms`.
Specify the host and port for the relay devices this microservice will service in the config.yml file. Relay device ports can be configured to be inverted in the config.yml file. 

 
```
#This means slot 33 on the rack will correspond to Relay Device 1.
relayOffset: 32
relays:
      - host: 192.168.100.131
        port: 80
        deviceId: 1
        type: XWR4R1
        maxPort: 4
        invertRelays:
          - false
          - false
          - false
          - false
```

Also provide environment variable `RELAY_LOG` which specifies where log files are required.


<br><br>

## NGINX Configuration

NGINX is used to support a unified path for communication to the rack microservices as well as communication between the rack microservices. NGINX configuration for relay-ms can be found at [relay.conf](conf/relay.conf). This configuration file is used to route requests to the relay microservice.


<br><br>


## Supported Relay Hardware

Each relay device specified in the config.yml file must also include a type. The currently supported type(s) are listed below:

| Hardware Name          | Hardware Type Identifier | Connection Protocol | Documentation                                                            |
|------------------------| --- |---------------------|--------------------------------------------------------------------------|
 WebRelay-Quad X-WR-4R1 | XWR4R1 | HTTP & Telnet       | http://www.controlbyweb.com/webrelay-quad/webrelay-quad_users_manual.pdf |


<br><br>


# Access the Swagger Documentation

The Swagger Documentation for the Relay Microservice can be accessed at https://localhost:9090/relay/swagger-ui.html when running locally. Default swagger path is **/relay/swagger-ui.html**.


<br><br>


## Custom Slot Mapping

Relay-MS offers the ability to customize any slot's device and outlet reference.
This allows for flexibility in slot capability for non-traditional rack deployments.
For instance, say you have a single 4 port relay device and 5 slots on your rack.
If you want device 5 to have relay capability but it is not necessary for device 2,
you could create a slot mapping that allows for this case with the following JSON:

    {
        "slots": {
            "1": "1:1",
            "3": "1:2",
            "4": "1:3",
            "5": "1:4",
        }   
    }

This would be stored as `mappings.json` in the `/relayms` directory by default.


<br><br>


### Relay Health Check

    GET http://localhost:9090/relay/actuator/health 

