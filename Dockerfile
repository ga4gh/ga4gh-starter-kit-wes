FROM keinos/sqlite3:latest as dbbuilder

WORKDIR /usr/src/db

COPY database/sqlite/create-tables.sql create-tables.sql
RUN sqlite3 ./ga4gh-starter-kit.dev.db < create-tables.sql

FROM gradle:5.6.4-jdk12

WORKDIR /usr/src/db
COPY --from=dbbuilder /usr/src/db/ga4gh-starter-kit.dev.db ga4gh-starter-kit.dev.db

WORKDIR /usr/src/app

COPY build.gradle build.gradle
COPY gradle gradle
COPY gradlew gradlew
COPY gradle.properties gradle.properties
COPY settings.gradle settings.gradle
COPY src src

RUN ./gradlew
RUN ./gradlew bootJar
