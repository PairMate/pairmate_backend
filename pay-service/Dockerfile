# 빌드 단계
FROM openjdk:17-jdk-slim as build
WORKDIR /app
COPY . .
# Gradle Wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 테스트 제외하고 빌드
RUN ./gradlew clean build -x test
FROM openjdk:17-jdk-slim
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apt-get clean

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8083

# JVM 타임존 설정 추가
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]