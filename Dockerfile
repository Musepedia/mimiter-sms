FROM openjdk:8

ARG PORT=16005
ARG WORKDIR=/opt/mimiter-sms

WORKDIR ${WORKDIR}
COPY target/lib ./lib

ENV TZ=Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE ${PORT}

COPY target/*.jar sms.jar

ENTRYPOINT java -jar sms.jar