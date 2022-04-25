FROM openjdk:17

ARG JAR_DIR
WORKDIR /workspace

COPY spring-boot-loader ./
COPY dependencies ./
COPY snapshot-dependencies ./
COPY application ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]