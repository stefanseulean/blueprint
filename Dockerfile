FROM docker.tradeshift.net/tradeshift-docker-java10-jre:1343e53f00f0543d8f94127ed2b614c569cec9f3
VOLUME /tmp
# Install Java and add Java JCE Unlimited policy
RUN apt-get update && \
   apt-get install -y curl

ARG JAR_FILE
ADD ${JAR_FILE} blueprint-service.jar

RUN mkdir -p /etc/tradeshift /etc/tradeshift/blueprint-service /var/log/tradeshift /var/log/tradeshift/blueprint-service
VOLUME [ \
    "/etc/tradeshift/document-resolution-service", \
    "/var/log/tradeshift/document-resolution-service" \
]

# HEALTHCHECK --interval=5s --timeout=3s CMD curl -f http://localhost:8778/actuator/health
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dcom.sun.management.jmxremote","-Dcom.sun.management.jmxremote.port=9010","-Dcom.sun.management.jmxremote.local.only=false","-Dcom.sun.management.jmxremote.authenticate=false","-Dcom.sun.management.jmxremote.ssl=false", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar","/blueprint-service.jar"]