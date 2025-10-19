FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY ../gradlew ../settings.gradle ../build.gradle ./
COPY ../gradle ./gradle
COPY ../common-libs ./common-libs
COPY . ./user-service
RUN chmod +x ./gradlew && ./gradlew :user-service:bootJar -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && apt-get clean
COPY --from=build /app/user-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]