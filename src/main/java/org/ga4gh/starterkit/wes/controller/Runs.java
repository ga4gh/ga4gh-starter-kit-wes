package org.ga4gh.starterkit.wes.controller;

import static org.ga4gh.starterkit.wes.constant.WesServerConstants.WES_API_PREFIX;

import java.util.ArrayList;
import java.util.List;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WES_API_PREFIX + "/runs")
public class Runs {

    @GetMapping
    public List<RunStatus> getRuns() {
        List<RunStatus> runs = new ArrayList<>() {{
            add(new RunStatus("80cf1f59-c7de-445a-a431-34f85734eef3", State.COMPLETE));
        }};
        return runs;
    }

}