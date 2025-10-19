# ---------- BUILD ----------
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY ../gradlew ../settings.gradle ../build.gradle ./
COPY ../gradle ./gradle
COPY ../common-libs ./common-libs
COPY . ./config-server
RUN chmod +x ./gradlew && ./gradlew :config-server:bootJar -x test

# ---------- RUN ----------
FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && apt-get clean
COPY --from=build /app/config-server/build/libs/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]