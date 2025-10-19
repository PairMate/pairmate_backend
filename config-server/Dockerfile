FROM openjdk:17-jdk-slim
WORKDIR /app

ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apt-get clean

# Private GitHub Repo 접근용 (환경변수 주입됨)
ARG GIT_USERNAME
ARG GIT_PASSWORD
ENV GIT_USERNAME=${GIT_USERNAME}
ENV GIT_PASSWORD=${GIT_PASSWORD}

# build/libs 안의 jar 파일을 모두 복사
COPY ./build/libs/*.jar ./build/libs/

# app.jar로 복사 (하나만 복사하도록 첫 번째 jar 선택)
RUN cp ./build/libs/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]