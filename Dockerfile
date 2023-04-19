#
# BUILD
#
FROM maven:3.9.1-amazoncorretto-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


#
# PACKAGE
#
FROM amazoncorretto:17
MAINTAINER takeawalk.com
COPY --from=build /home/app/target/*.jar takeawalk.jar
ENTRYPOINT ["java","-jar","/takeawalk.jar"]