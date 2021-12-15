# GA4GH Starter Kit WES
Starter Kit server implementation of the GA4GH Workflow Execution Service (WES) specification

## Changelog

### v0.2.2
* Can specify that Nextflow workflow runs should be launched through Slurm execution engine via config file
* Can specify that Nextflow workflow runs should spawn Singularity containers instead of Docker containers
* Patched log4j dependencies to v2.16.0 to avoid [Log4j Vulnerability](https://www.cisa.gov/uscert/apache-log4j-vulnerability-guidance)
