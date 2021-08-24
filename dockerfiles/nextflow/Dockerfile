FROM ga4gh/ga4gh-starter-kit-wesbuilder as wesbuilder

FROM adoptopenjdk/openjdk12:jre-12.0.2_10-debian

ARG VERSION

# INSTALL NEXTFLOW
WORKDIR /usr/src/dependencies/nextflow
RUN curl -s https://get.nextflow.io | bash
RUN chmod +x nextflow
ENV PATH="/usr/src/dependencies/nextflow:${PATH}"

WORKDIR /usr/src/app
COPY --from=wesbuilder /usr/src/db/ga4gh-starter-kit.dev.db ga4gh-starter-kit.dev.db
COPY --from=wesbuilder /usr/src/app/build/libs/ga4gh-starter-kit-wes-${VERSION}.jar ga4gh-starter-kit-wes.jar

ENTRYPOINT [ "java", "-jar", "ga4gh-starter-kit-wes.jar" ]
