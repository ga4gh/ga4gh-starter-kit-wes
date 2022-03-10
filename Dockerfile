##################################################
# DATABASE CONTAINER
##################################################
FROM keinos/sqlite3:latest as dbbuilder

# To enable creating files [see line 12]
USER root 

WORKDIR /usr/src/db

COPY database/sqlite/create-tables.sql create-tables.sql
RUN sqlite3 ./ga4gh-starter-kit.dev.db < create-tables.sql

##################################################
# GRADLE CONTAINER (was gradle:5.6.4-jdk12 originally)
##################################################
FROM gradle:7.3.3-jdk11 as gradleimage

WORKDIR /home/gradle/source

COPY build.gradle build.gradle
COPY gradlew gradlew
COPY settings.gradle settings.gradle
COPY src src

RUN gradle wrapper
RUN ./gradlew bootJar

##################################################
# FINAL CONTAINER
##################################################
FROM adoptopenjdk/openjdk12:jre-12.0.2_10-alpine

USER root

ARG VERSION

WORKDIR /usr/src/db
COPY --from=dbbuilder /usr/src/db/ga4gh-starter-kit.dev.db ga4gh-starter-kit.dev.db

WORKDIR /usr/src/app
COPY --from=gradleimage /home/gradle/source/build/libs/ga4gh-starter-kit-wes-${VERSION}.jar ga4gh-starter-kit-wes-${VERSION}.jar