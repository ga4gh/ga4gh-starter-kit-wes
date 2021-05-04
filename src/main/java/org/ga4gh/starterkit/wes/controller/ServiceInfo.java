package org.ga4gh.starterkit.wes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;

import org.ga4gh.starterkit.wes.model.WesServiceInfo;
import org.springframework.http.MediaType;

@RestController
@RequestMapping(WES_API_V1 + "/service-info")
public class ServiceInfo {

    @Autowired
    WesServiceInfo wesServiceInfo;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public WesServiceInfo getServiceInfo() {
        return wesServiceInfo;
    }
}
