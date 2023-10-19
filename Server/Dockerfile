# (1) base-image
FROM openjdk:11-jre-slim AS runtime-stage

# (2) COPY에서 사용될 경로 변수
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar

# (3) jar 빌드 파일을 도커 컨테이너로 복사
COPY ${JAR_FILE} app.jar
COPY src/main/resources/prometheus.pem prometheus.pem
COPY start.sh start.sh

# (4) pem 파일의 권한 설정
RUN groupadd -r ubuntu && useradd -r -g ubuntu ubuntu \
    && chmod 400 prometheus.pem && chown ubuntu:ubuntu prometheus.pem \
    && chmod +x start.sh  # 스크립트에 실행 권한 부여

# (5) jar 파일 실행
CMD ["./start.sh"]
