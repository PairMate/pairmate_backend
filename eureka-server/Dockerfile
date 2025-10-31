FROM pairmate-registry.kr.ncr.ntruss.com/pairmate-registry/openjdk:17-jdk-slim AS build
WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY common-libs ./common-libs
COPY eureka-server ./eureka-server

RUN chmod +x ./gradlew && ./gradlew :eureka-server:bootJar -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && apt-get clean
COPY --from=build /app/eureka-server/build/libs/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]