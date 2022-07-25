<img src="https://www.ga4gh.org/wp-content/themes/ga4gh-theme/gfx/GA-logo-horizontal-tag-RGB.svg" alt="GA4GH Logo" style="width: 400px;"/>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Java 11+](https://img.shields.io/badge/java-11+-blue.svg?style=flat-square)](https://www.java.com)

# GA4GH Starter Kit WES
Starter Kit server implementation of the GA4GH [Workflow Execution Service (WES) specification](https://github.com/ga4gh/workflow-execution-service-schemas)

## Running the WES service

### Docker

We recommend running the WES service as a docker container for most contexts. Images can be downloaded from [docker hub](https://hub.docker.com/repository/docker/ga4gh/ga4gh-starter-kit-wes). To download the image and run a container:

Pull the image:
```
docker pull ga4gh/ga4gh-starter-kit-wes:latest
```

Run container with default settings:
```
docker run -p 4500:4500 ga4gh/ga4gh-starter-kit-wes:latest
```

OR, run container with config file overriding defaults
```
docker run -p 4500:4500 ga4gh/ga4gh-starter-kit-wes:latest java -jar ga4gh-starter-kit-wes.jar -c path/to/config.yml
```

### Native

The service can also be installed locally in cases where docker deployments are not possible, or for development of the codebase. Native installations require:
* Java 11+
* Gradle 7.3.2+
* SQLite (for creating the dev database)

First, clone the repository from Github:
```
git clone https://github.com/ga4gh/ga4gh-starter-kit-wes.git
cd ga4gh-starter-kit-wes
```

The service can be run in development mode directly via gradle:

Run with all defaults
```
./gradlew bootRun
```

Run with config file
```
./gradlew bootRun --args="--config path/to/config.yml"
```

Alternatively, the service can be built as a jar and run:

Build jar:
```
./gradlew bootJar
```

Run with all defaults
```
java -jar build/libs/ga4gh-starter-kit-wes-${VERSION}.jar
```

Run with config file
```
java -jar build/libs/ga4gh-starter-kit-wes-${VERSION}.jar --config path/to/config.yml
```

### Confirm server is running

Whether running via docker or natively on a local machine, confirm the WES API is up running by visiting its `service-info` endpoint, you should receive a valid `ServiceInfo` response.

```
GET http://localhost:4500/ga4gh/wes/v1/service-info

Response:
{
    "id": "org.ga4gh.starterkit.wes",
    "name": "GA4GH Starter Kit WES Service",
    "description": "An open source, community-driven implementation of the GA4GH Workflow Execution Service (WES)API specification.",
    "contactUrl": "mailto:info@ga4gh.org",
    "documentationUrl": "https://github.com/ga4gh/ga4gh-starter-kit-wes",
    "createdAt": "2020-01-15T12:00:00Z",
    "updatedAt": "2020-01-15T12:00:00Z",
    "environment": "test",
    "version": "0.3.2",
    "type": {
        "group": "org.ga4gh",
        "artifact": "wes",
        "version": "1.0.1"
    },
    "organization": {
        "name": "Global Alliance for Genomics and Health",
        "url": "https://ga4gh.org"
    },
    "workflow_type_versions": {
        "WDL": [
            "1.0"
        ],
        "NEXTFLOW": [
            "21.04.0"
        ]
    },
    "workflow_engine_versions": {
        "NATIVE": "1.0.0"
    }
}
```

## Development

Additional setup steps to run the WES server in a local environment for development and testing.

### Setup dev database

A local SQLite database must be set up for running the WES service in a development context. If `make` and `sqlite3` are already installed on the system `PATH`, this database can be created and populated with a dev dataset by simply running: 

```
make sqlite-db-refresh
```

This will create a SQLite database named `ga4gh-starter-kit.dev.db` in the current directory.

If `make` and/or `sqlite` are not installed, [this file](./database/sqlite/create-tables.sql) contains SQLite commands for creating the database schema, and [this file](./database/sqlite/add-dev-dataset.sql) contains SQLite commands for populating it with the dev dataset.

Confirm the WES service can connect to the dev database by submitting a `WES id` to the `/objects/{object_id}` endpoint. For example, a `WES id` of `b8cd0667-2c33-4c9f-967b-161b905932c9` represents a root `WES bundle` for a phenopacket test dataset:

```
GET http://localhost:4500/ga4gh/wes/v1/objects/b8cd0667-2c33-4c9f-967b-161b905932c9

Response:
{
    "id": "b8cd0667-2c33-4c9f-967b-161b905932c9",
    "description": "Open dataset of 384 phenopackets",
    "created_time": "2021-03-12T20:00:00Z",
    "name": "phenopackets.test.dataset",
    "size": 143601,
    "updated_time": "2021-03-13T12:30:45Z",
    "version": "1.0.0",
    "self_uri": "wes://localhost:4500/b8cd0667-2c33-4c9f-967b-161b905932c9",
    "contents": [
        {
            "name": "phenopackets.mundhofir.family",
            "wes_uri": [
                "wes://localhost:4500/1af5cdcf-898c-4dbc-944e-1ac95e82c0ea"
            ],
            "id": "1af5cdcf-898c-4dbc-944e-1ac95e82c0ea"
        },
        {
            "name": "phenopackets.zhang.family",
            "wes_uri": [
                "wes://localhost:4500/355a74bd-6571-4d4a-8602-a9989936717f"
            ],
            "id": "355a74bd-6571-4d4a-8602-a9989936717f"
        },
        {
            "name": "phenopackets.cao.family",
            "wes_uri": [
                "wes://localhost:4500/a1dd4ae2-8d26-43b0-a199-342b64c7dff6"
            ],
            "id": "a1dd4ae2-8d26-43b0-a199-342b64c7dff6"
        },
        {
            "name": "phenopackets.lalani.family",
            "wes_uri": [
                "wes://localhost:4500/c69a3d6c-4a28-4b7c-b215-0782f8d62429"
            ],
            "id": "c69a3d6c-4a28-4b7c-b215-0782f8d62429"
        }
    ]
}
```

## Configuration

Please see the [Configuration page](./CONFIGURATION.md) for instructions on how to configure the WES service with custom properties.


## Changelog

### v0.3.2
* Bug Fix - /runs endpoints returns list of completed runs
* Bug Fix - resolve wes uri when wes and wes are running as docker containers

### v0.3.1
* Bug Fix - Correct the version of nextflow that is installed in the Docker-Wes-Nextflow image

### v0.3.0
* Can execute WDL-based workflows via WES. Starter Kit WES acts as a relay server between the client and a running Cromwell server. Starter Kit WES passes workflow run requests to Cromwell, and pulls data from Cromwell to report on workflow run status

### v0.2.2
* Can specify that Nextflow workflow runs should be launched through Slurm execution engine via config file
* Can specify that Nextflow workflow runs should spawn Singularity containers instead of Docker containers
* Patched log4j dependencies to v2.16.0 to avoid [Log4j Vulnerability](https://www.cisa.gov/uscert/apache-log4j-vulnerability-guidance)
