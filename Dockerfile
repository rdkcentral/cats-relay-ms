FROM amazoncorretto:17-alpine3.20

ENV RELAY_LOG /logs/

RUN mkdir /relayms
VOLUME /relayms

ADD relayms/config.yml /relayms/config.yml
ADD target/relay-ms.jar /opt/relay-ms.jar

CMD java -jar /opt/relay-ms.jar

EXPOSE 9090 9091
