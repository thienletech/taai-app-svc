# syntax=docker/dockerfile:experimental

FROM alpine:latest as build-stage
RUN apk update
RUN apk update && \
    apk add --no-cache openjdk17 wget findutils && \
    apk add --no-cache libc6-compat 
RUN mkdir -p /workspace/app
WORKDIR /workspace/app

COPY . /workspace/app
COPY gradlew /workspace/app
RUN chmod +x gradlew 
RUN ./gradlew clean build

RUN apk update && apk add --no-cache binutils

RUN jlink \
    --add-modules ALL-MODULE-PATH \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre

FROM alpine:latest as prod-stage

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=build-stage /jre $JAVA_HOME

RUN mkdir -p /workspace/app
COPY --from=build-stage /workspace/app/build/libs/taai-app-svc-0.0.1-SNAPSHOT.jar /workspace/app/

WORKDIR /workspace/app
EXPOSE 5000

CMD ["java", "-jar", "/workspace/app/taai-app-svc-0.0.1-SNAPSHOT.jar"]

#ENTRYPOINT ["/bin/sh"]