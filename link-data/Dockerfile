FROM adoptopenjdk/openjdk11:jre11u-nightly

COPY target/link-data.jar link-data.jar
COPY agent /usr/local/agent

ENV SW_AGENT_NAME "link-data"

ENTRYPOINT ["java","-javaagent:/usr/local/agent/skywalking-agent.jar","-jar","/link-data.jar"]