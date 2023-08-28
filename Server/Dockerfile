# (1) base-image
FROM openjdk:11

# (2) COPY에서 사용될 경로 변수
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar

# (3) jar 빌드 파일을 도커 컨테이너로 복사
COPY ${JAR_FILE} app.jar
COPY Server/src/main/resources/prometheus.pem /app/path/prometheus.pem

# (4) pem 파일의 권한 설정
RUN groupadd -r ubuntu && useradd -r -g ubuntu ubuntu \
    && chmod 400 /app/path/prometheus.pem && chown ubuntu:ubuntu /app/path/prometheus.pem

# (5) jar 파일 실행
ENTRYPOINT ["java","-jar","/app.jar"]
