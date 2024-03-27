[![ci](https://github.com/gwallet/java-container-bootstrap/actions/workflows/ci.yml/badge.svg)](https://github.com/gwallet/java-container-bootstrap/actions/workflows/ci.yml)

# Java Container Bootstrap

Bootstrap project (ie. empty shell) ready to ship Java application in
[OCI](https://opencontainers.org/) compatible container image.

## Build Instructions

### Requirements

- [JDK](https://dev.java/) ≥ 21
- [GNU Make](https://www.gnu.org/software/make/)
- [Podman](https://podman.io/) or [Docker](https://docs.docker.com/engine/reference/commandline/cli/)
- (Optional to run locally) [Podman Compose](https://github.com/containers/podman-compose)
  or [Docker Compose](https://docs.docker.com/compose/)

### 0. Don't know where to start? Here's how to show the available build targets:
```shell
make help
```
or, because help is the default target: 🙂
```shell
make
```

### 1. Building the JAR

```shell
make app
```

### 2. Testing the JAR

```shell
make app/IT
```
> NOTE: to shorten the feedback loop, `make app/test` only run unit tests

### 3. Building the OCI compatible container image (requires [Podman](https://podman.io/) or [Docker](https://docs.docker.com/engine/reference/commandline/cli/))

```shell
make docker/image
```

### 4. Running the container (requires [Podman Compose](https://github.com/containers/podman-compose) or [Docker Compose](https://docs.docker.com/compose/))

To start the whole stack:
```shell
make docker/up
```

To shut down the whole stack:
```shell
make docker/down
```

### 5. Publishing the container image

```shell
make docker/push
```
