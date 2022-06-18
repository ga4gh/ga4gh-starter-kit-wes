<img src="https://www.ga4gh.org/wp-content/themes/ga4gh-theme/gfx/GA-logo-horizontal-tag-RGB.svg" alt="GA4GH Logo" style="width: 400px;"/>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Java 11+](https://img.shields.io/badge/java-11+-blue.svg?style=flat-square)](https://www.java.com)

# GA4GH Starter Kit WES
Starter Kit server implementation of the GA4GH Workflow Execution Service (WES) specification

## Changelog

### v0.3.2
* Bug Fix - /runs endpoints returns list of completed runs
* Bug Fix - resolve drs uri when wes and drs are running as docker containers

### v0.3.1
* Bug Fix - Correct the version of nextflow that is installed in the Docker-Wes-Nextflow image

### v0.3.0
* Can execute WDL-based workflows via WES. Starter Kit WES acts as a relay server between the client and a running Cromwell server. Starter Kit WES passes workflow run requests to Cromwell, and pulls data from Cromwell to report on workflow run status

### v0.2.2
* Can specify that Nextflow workflow runs should be launched through Slurm execution engine via config file
* Can specify that Nextflow workflow runs should spawn Singularity containers instead of Docker containers
* Patched log4j dependencies to v2.16.0 to avoid [Log4j Vulnerability](https://www.cisa.gov/uscert/apache-log4j-vulnerability-guidance)
