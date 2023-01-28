FROM adoptopenjdk/openjdk11:jre11u-nightly

COPY target/link-gateway.jar link-gateway.jar
COPY agent /usr/local/agent

ENV SW_AGENT_NAME "link-gateway"

ENTRYPOINT ["java","-javaagent:/usr/local/agent/skywalking-agent.jar","-jar","/link-gateway.jar"]