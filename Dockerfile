FROM maven:3.8.4-openjdk-17 as mvn
WORKDIR /app
COPY . .
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /app
ARG project=AnonymousChat
ENV artifact=${project}-jar-with-dependencies.jar
ENV MASTERIP=127.0.0.1
ENV ID=0
COPY --from=mvn /app/target/${artifact} .

CMD /usr/bin/java -jar ${artifact} $MASTERIP $ID
