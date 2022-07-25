# Configuration

The WES service can be configured with custom properties, affecting behavior at runtime. This includes customization of the WES database location, contents of the service info response, etc.

This document outlines how to configure the WES service.

## Overview

Configuration is done via YAML config file, which is specified on the command line via `-c` or `--config`. For example:

```
java -jar ga4gh-starter-kit-wes.jar -c /path/to/config.yml
```

If running with a custom properties file via docker, be sure to mount the config file into the container with `-v`. For example:

```
docker run -v /host/directory/with/config:/config -p 4500:4500 ga4gh/ga4gh-starter-kit-wes:latest java -jar ga4gh-starter-kit-wes.jar -c /config/config.yml
```

Each property has a default value. Any property that is not overridden in the YAML config will use its default.

## YAML config file

The YAML config file must contain a single root property of `wes`, under which all subconfigurations are located:

```
wes:
  serverProps:
    ...
  databaseProps:
    ...
  serviceInfo:
    ...
  wesServiceProps:
    ...
```

Under `wes`, the following configuration objects can be provided:

* `serverProps`: web service general runtime props
* `databaseProps`: specify WES database location, access/auth, and other database-related properties
* `serviceInfo`: customize the output of the `/service-info` response
* `wesServiceProps`: 

## serverProps

A valid `serverProps` configuration may look like the following:

```
wes:
  serverProps:
    scheme: https
    hostname: wes.genomics.com
    port: 80
```

`serverProps` supports the following configurable attributes:

| Attribute | Description | Default |
|-----------|-------------|---------|
| scheme | The URL scheme/protocol by which the service can be accessed. (`http` or `https`). Allows `http` in dev/local deployments, but real-world deployments should use `https` | http |
| hostname | The URL domain name (including subdomain and port) that this service is running at. Used to reference clients back to the service | localhost |
| publicApiPort | The networking port that will be open to requests to the public API, that is, all API endpoints outlined in GA4GH specifications. | 4500 |
| adminApiPort | The networking port that will be open to requests to the admin API, that is, API endpoints that are not outlined in GA4GH specs, but are required to create and edit data models. Traffic to the admin port should have stricter firewall rules compared to the public API port. | 4501 |
| logLevel | The log severity level. The program will output log messages encountered at the specified log level or higher. | DEBUG |
| logFile | Path to output file storing log output. If null, log output will be printed to console. | null |
| publicApiCorsAllowedOrigins | Customizes the Cross Origin Resource Sharing (CORS) header `Access-Control-Allow-Origin` for requests made to any public API endpoint. Allows content returned from the API to be rendered only on trusted websites. | ["http://localhost"] |
| publicApiCorsAllowedMethods | Customizes the CORS header `Access-Control-Allow-Methods` for requests made to any public API endpoint. Allows content returned from the API to be rendered only when a trusted HTTP method was used to request data. | [GET, POST, PUT, DELETE] |
| publicApiCorsAllowedHeaders | Customizes the CORS header `Access-Control-Allow-Headers` for requests made to any public API endpoint. Allows API content to be rendered on a website only when trusted HTTP headers are sent to the server. | [] |
| adminApiCorsAllowedOrigins | Customizes the Cross Origin Resource Sharing (CORS) header `Access-Control-Allow-Origin` for requests made to any admin API endpoint. Allows content returned from the API to be rendered only on trusted websites. | ["http://localhost"] |
| adminApiCorsAllowedMethods | Customizes the CORS header `Access-Control-Allow-Methods` for requests made to any admin API endpoint. Allows content returned from the API to be rendered only when a trusted HTTP method was used to request data. | [GET, POST, PUT, DELETE] |
| adminApiCorsAllowedHeaders | Customizes the CORS header `Access-Control-Allow-Headers` for requests made to any admin API endpoint. Allows API content to be rendered on a website only when trusted HTTP headers are sent to the server. | [] |
| disableSpringLogging | If true, does not output the default startup messages for Spring Boot web apps to console (i.e. Spring logo) | false |

## databaseProps

A valid `databaseProps` configuration may look like the following:

```
wes:
  databaseProps:
    url: jdbc:sqlite:./ga4gh-starter-kit.dev.db
    username: ga4ghuser
    password: mypa$$word123
    poolSize: 8
    showSQL: true
```

`databaseProps` supports the following configurable attributes:

| Attribute | Description | Default |
|-----------|-------------|---------|
| url | Java JDBC URL to the Data Connect database, indicating connection type and database location | jdbc:sqlite:./ga4gh-starter-kit.dev.db |
| username | Username with full access credentials to the Data Connect database | |
| password | Password for the above user | |
| poolSize | Database connection pool size | 1 |
| showSQL | If true, log SQL syntax for each database query | true |


## serviceInfo

A valid `serviceInfo` configuration may look like the following:

```
wes:
  serviceInfo:
    id: com.genomics.wes
    name: Genomics.com WES service
    description: This service serves data according to the WES protocol...
    contactUrl: mailto:info@genomics.com
    documentationUrl: https://wesdocs.genomics.com
    createdAt: 2021-05-25T12:00:00Z
    updatedAt: 2021-05-26T12:00:00Z
    environment: production
    version: 1.0.0
    organization:
      name: Genomics Company
      url: https://genomics.com
```

`serviceInfo` supports the following configurable attributes:

| Attribute | Description | Default |
|-----------|-------------|---------|
| id | unique identifier for the service in reverse domain name formant | org.ga4gh.starterkit.wes |
| name | short, human readable service name | GA4GH Starter Kit WES Service |
| description | longer, human readable description | An open source community-driven implementation of the GA4GH Data Repository Service (WES) API specification. |
| contactUrl | URL/email address users should contact with questions or issues about the service | mailto:info@ga4gh.org |
| documentationUrl | URL to where documentation about the service is hosted | https://github.com/ga4gh/ga4gh-starter-kit-wes |
| createdAt | timestamp of when the service was first started | 2020-01-15T12:00:00Z |
| updatedAt | timestamp of when the service was last updated | 2020-01-15T12:00:00Z |
| environment | describes what environment the service is running in (e.g. dev, test, prod) | test |
| version | the service version | 0.1.0 |
| organization.name | name of the organization hosting the service | Global Alliance for Genomics and Health |
| organization.url | URL to organization homepage | https://ga4gh.org |
