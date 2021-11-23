package org.ga4gh.starterkit.wes.controller;

import org.ga4gh.starterkit.wes.app.WesServerConstants;
import org.ga4gh.starterkit.wes.app.WesServerYamlConfigContainer;
import org.ga4gh.starterkit.wes.config.WesServiceProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/temp")
public class TempController {

    @Autowired
    @Qualifier(WesServerConstants.FINAL_WES_CONFIG_CONTAINER)
    private WesServerYamlConfigContainer configContainer;

    @GetMapping(path = "/inspect/wes-service-props")
    public WesServiceProps inspectWesServiceProps() {
        return configContainer.getWes().getWesServiceProps();
    }
}
