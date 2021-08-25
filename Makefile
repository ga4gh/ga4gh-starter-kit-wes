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
	@sqlite3 ${DEVDB} < database/sqlite/create-schema.migrations.sql

# populate the sqlite database with test data
.PHONY: sqlite-db-populate-dev-dataset
sqlite-db-populate-dev-dataset:
	@sqlite3 ${DEVDB} < database/sqlite/populate-dev-dataset.migrations.sql

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

.PHONY: docker-wes-builder-build
docker-wes-builder-build:
	docker build -t ${DOCKER_ORG}/ga4gh-starter-kit-wesbuilder .

.PHONY: docker-wes-nextflow-build
docker-wes-nextflow-build:
	docker build -t ${DOCKER_ORG}/${DOCKER_REPO}:${TAG}-nextflow --build-arg VERSION=${TAG} dockerfiles/nextflow

.PHONY: docker-wes-nextflow-publish
docker-wes-nextflow-publish:
	echo "toPublish"
