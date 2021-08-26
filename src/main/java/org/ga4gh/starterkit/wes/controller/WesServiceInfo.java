package org.ga4gh.starterkit.wes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;
import org.springframework.http.MediaType;

/**
 * Service info controller, displays generic and WES-specific service info
 */
@RestController
@RequestMapping(WES_API_V1 + "/service-info")
public class WesServiceInfo {

    @Autowired
    org.ga4gh.starterkit.wes.model.WesServiceInfo wesServiceInfo;

    /**
     * Display service info
     * @return WES service info
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public org.ga4gh.starterkit.wes.model.WesServiceInfo getServiceInfo() {
        return wesServiceInfo;
    }
}
