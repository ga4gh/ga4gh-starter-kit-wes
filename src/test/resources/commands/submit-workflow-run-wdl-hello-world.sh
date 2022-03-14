curl -X POST -H "Content-Type: multipart/form-data" \
    -F workflow_type=WDL \
    -F workflow_type_version=1.0 \
    -F workflow_url=https://raw.githubusercontent.com/ga4gh-tech-team/wdl-hello-world/v0.1.0/Dockstore.wdl \
    -F workflow_params='{}' \
    http://localhost:4500/ga4gh/wes/v1/runs
