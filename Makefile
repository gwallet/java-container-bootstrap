default: help

.PHONY: help
##	This help screen
help:
	@printf "Available targets:\n\n"
	@awk '/^[a-zA-Z\-_0-9%\\\/]+:/ { \
				helpMessage = match(lastLine, /^##\s(.*)/); \
				if (helpMessage) { \
						helpCommand = $$1; \
						helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
						gsub("\\\\", "", helpCommand); \
						gsub(":+$$", "", helpCommand); \
						printf "  \x1b[32;01m%-16s\x1b[0m %s\n", helpCommand, helpMessage; \
				} \
		} \
		{ lastLine = $$0 }' $(MAKEFILE_LIST) | sort -u
	@printf "\n"
	@printf "Available ENV:\n\n"
	@awk '/[A-Z_]+\s+\?=/ { \
				helpMessage = match(lastLine, /^##\s(.*)/); \
				if (helpMessage) { \
						envVar = $$1; \
						helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
						gsub("\\\\", "", envVar); \
						gsub("?=+$$", "", envVar); \
						printf "  \x1b[32;01m%-32s\x1b[0m %s\n", envVar, helpMessage; \
				} \
		} \
		{ lastLine = $$0 }' $(MAKEFILE_LIST) | sort -u
	@awk '/ifndef [A-Z_]+/ { \
				helpMessage = match(lastLine, /^##\s(.*)/); \
				if (helpMessage) { \
						envVar = $$2; \
						helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
						gsub("ifndef+$$", "", envVar); \
						gsub("\\\\", "", envVar); \
						printf "  \x1b[32;01m%-32s\x1b[0m %s\n", envVar, helpMessage; \
				} \
		} \
		{ lastLine = $$0 }' $(MAKEFILE_LIST) | sort -u
	@printf "\n"

# Set SILENT to 's' if --quiet/-s/--silent set, otherwise ''.
SILENT := $(findstring s,$(word 1, $(MAKEFLAGS)))

GIT := $(shell command -v git 2>/dev/null)
ifdef GIT
	GIT_WORKTREE_STATE := $(shell git status --porcelain --untracked-files=no | grep -q . && echo "DIRTY" || echo "CLEAN" )
endif

-include Makefile.maven

##	Version of the JDK provided by the container base image, default value is ${maven.compiler.target} from `pom.xml`
DOCKER_BASE_IMAGE_JDK_VERSION ?= $(MAVEN_JDK_VERSION)
##	Version of the container image, default value is 'yyyyMMdd_${git refs}' if worktree is clean, otherwise ${version}_yyyyMMdd with ${version} from `pom.xml`
ifndef DOCKER_IMAGE_VERSION
	ifeq ($(GIT_WORKTREE_STATE),CLEAN)
		DOCKER_IMAGE_VERSION := $(shell git describe 2>/dev/null | grep '^[[:digit:]]\+\.[[:digit:]]\+\(\.[[:digit:]]\+\)\?$$' || date +"%Y%m%d_%H%M")_$(shell git rev-parse --short HEAD)
	else
		DOCKER_IMAGE_VERSION := $(MAVEN_APP_VERSION)_$(shell date +"%Y%m%d_%H%M")
	endif
endif

-include Makefile.docker
