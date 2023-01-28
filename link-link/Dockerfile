FROM adoptopenjdk/openjdk11:jre11u-nightly

COPY target/link-link.jar link-link.jar
COPY agent /usr/local/agent

ENV SW_AGENT_NAME "link-link"

ENTRYPOINT ["java","-javaagent:/usr/local/agent/skywalking-agent.jar","-jar","/link-link.jar"]