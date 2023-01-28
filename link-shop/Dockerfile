FROM adoptopenjdk/openjdk11:jre11u-nightly

COPY target/link-shop.jar link-shop.jar
COPY agent /usr/local/agent

ENV SW_AGENT_NAME "link-shop"

ENTRYPOINT ["java","-javaagent:/usr/local/agent/skywalking-agent.jar","-jar","/link-shop.jar"]