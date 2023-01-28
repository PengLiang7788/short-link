FROM adoptopenjdk/openjdk11:jre11u-nightly

COPY target/link-account.jar link-account.jar
COPY agent /usr/local/agent

ENV SW_AGENT_NAME "link-account"

ENTRYPOINT ["java","-javaagent:/usr/local/agent/skywalking-agent.jar","-jar","/link-account.jar"]