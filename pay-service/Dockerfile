FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && apt-get clean
COPY ./build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]