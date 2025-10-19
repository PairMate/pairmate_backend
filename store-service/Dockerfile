FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && apt-get clean
# build/libs 안의 jar 파일을 모두 복사
COPY ./build/libs/*.jar ./build/libs/
# app.jar로 복사 (하나만 복사하도록 첫 번째 jar 선택)
RUN cp ./build/libs/*.jar app.jar
EXPOSE 8082
# Add JVM timezone setting
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]