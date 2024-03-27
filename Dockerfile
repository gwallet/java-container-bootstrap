# Build stage
ARG JDK_VERSION=21
FROM docker.io/library/eclipse-temurin:${JDK_VERSION}-jdk AS builder

RUN apt-get update \
 && apt-get install -y make \
 && apt-get clean autoremove \
 && rm -rf /var/lib/apt/lists/*
WORKDIR /usr/local/src/app
COPY . .
ARG MAKEFLAGS=""
ENV MAVEN_ARGS="--batch-mode"
RUN make app/deps app app/jre

# Assembly stage
FROM debian:12-slim

COPY --from=builder \
  /usr/local/src/app/target/jre/ \
  /usr/local/lib/jre/
COPY --from=builder \
  /usr/local/src/app/target/app.jar \
  /usr/local/share/app/app.jar

ENV JAVA_HOME="/usr/local/lib/jre/"
ENV JAVA_TOOL_OPTIONS="-XX:-PrintCommandLineFlags"
ENV LANG="C.UTF-8"
ENV SERVER_PORT="8080"

EXPOSE 8080

RUN groupadd --system java \
 && useradd --system --shell /bin/false --home-dir /usr/local/lib/jre --no-create-home --gid java jre
USER jre
WORKDIR /usr/local/lib/jre

CMD [ \
  "/usr/local/lib/jre/bin/java", \
  "-showversion", \
  "-jar", "/usr/local/share/app/app.jar" \
]
