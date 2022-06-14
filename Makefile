ORG := $(shell cat build.gradle | grep "^group" | cut -f 2 -d ' ' | sed "s/'//g")
REPO := $(shell cat settings.gradle | grep "rootProject.name" | cut -f 3 -d ' ' | sed "s/'//g")
TAG := $(shell cat build.gradle | grep "^version" | cut -f 2 -d ' ' | sed "s/'//g")
IMG := ${ORG}/${REPO}:${TAG}
DEVDB := ga4gh-starter-kit.dev.db
JAR := ga4gh-starter-kit-wes.jar
DOCKER_ORG := ga4gh
DOCKER_REPO := ga4gh-starter-kit-wes

Nothing:
	@echo "No target provided. Stop"

# remove local dev db
.PHONY: clean-sqlite
clean-sqlite:
	@rm -f ${DEVDB}

# remove local jar
.PHONY: clean-jar
clean-jar:
	@rm -f ${JAR}

# remove runs in wes_runs directory
.PHONY: clean-runs
clean-runs:
	@rm -r wes_runs/*

# remove local dev db and jar
.PHONY: clean-all
clean-all: clean-sqlite clean-jar

# create the sqlite database
.PHONY: sqlite-db-build
sqlite-db-build: clean-sqlite
	@sqlite3 ${DEVDB} < database/sqlite/create-tables.sql

# populate the sqlite database with test data
.PHONY: sqlite-db-populate-dev-dataset
sqlite-db-populate-dev-dataset:
	@sqlite3 ${DEVDB} < database/sqlite/add-dev-dataset.sql

.PHONY: sqlite-db-refresh
sqlite-db-refresh: clean-sqlite sqlite-db-build # sqlite-db-populate-dev-dataset

# create jar file
.PHONY: jar-build
jar-build:
	@./gradlew bootJar

# execute jar file
.PHONY: jar-run
jar-run:
	java -jar ${JAR}

# Docker Build

.PHONY: docker-build-wes-builder
docker-build-wes-builder:
	docker build -t ga4gh/ga4gh-starter-kit-wesbuilder --build-arg VERSION=${TAG} -f docker/dockerfiles/Dockerfile-Wes-Builder .

.PHONY: docker-build-wes-standalone
docker-build-wes-standalone:
	docker build -t ga4gh/ga4gh-starter-kit-wes:${TAG} --build-arg VERSION=${TAG} -f docker/dockerfiles/Dockerfile-Wes-Standalone .

.PHONY: docker-build-wes-nextflow
docker-build-wes-nextflow:
	docker build --no-cache -t ga4gh/ga4gh-starter-kit-wes:${TAG}-nextflow --build-arg VERSION=${TAG} -f docker/dockerfiles/Dockerfile-Wes-Nextflow .

.PHONY: docker-build-cromwell-docker
docker-build-cromwell-docker:
	docker build -t ga4gh/cromwell-docker:latest --build-arg VERSION=${TAG} -f docker/dockerfiles/Dockerfile-Cromwell-Docker .

# Docker Push

.PHONY: docker-push-wes-standalone
docker-push-wes-standalone:
	docker image push ga4gh/ga4gh-starter-kit-wes:${TAG}

.PHONY: docker-push-wes-nextflow
docker-push-wes-nextflow:
	docker image push ga4gh/ga4gh-starter-kit-wes:${TAG}-nextflow

.PHONY: docker-push-cromwell-docker
docker-push-cromwell-docker:
	docker image push ga4gh/cromwell-docker:latest
