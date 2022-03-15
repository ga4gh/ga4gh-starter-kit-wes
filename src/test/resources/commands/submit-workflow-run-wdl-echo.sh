curl -X POST -H "Content-Type: multipart/form-data" \
    -F workflow_type=WDL \
    -F workflow_type_version=1.0 \
    -F workflow_url=https://raw.githubusercontent.com/ga4gh-tech-team/wdl-echo/v0.1.1/Dockstore.wdl \
    -F workflow_params='{"echo.message1":"FOO","echo.message2":"BAR","echo.message3":"HELLOWORLD"}' \
    http://localhost:4500/ga4gh/wes/v1/runs
