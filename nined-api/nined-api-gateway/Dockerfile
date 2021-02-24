### BUILDER IMAGE
FROM maven:3.6-jdk-8 as builder
# create app folder for sources
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build
# download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
# copy source code
COPY src /build/src
# build application
RUN mvn package

### RUNTIME IMAGE
FROM openjdk:8-jdk-alpine as runtime
RUN apk add --update --no-cache curl
EXPOSE 8765
# Set app home folder
ENV APP_HOME /app
# to set JVM options
ENV JAVA_OPTS=""

# create base app folder
RUN mkdir $APP_HOME
# create folder to save configuration files
RUN mkdir $APP_HOME/config
# create folder for application logs
RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

WORKDIR $APP_HOME
# copy executable jar file from the builder image
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]