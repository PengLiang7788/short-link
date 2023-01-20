FROM adoptopenjdk/openjdk11:jre11u-nightly
COPY target/link-data.jar link-data.jar
ENTRYPOINT ["java","-jar","/link-data.jar"]