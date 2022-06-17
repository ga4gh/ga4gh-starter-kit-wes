package org.ga4gh.starterkit.wes.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RunsListResponse {
    private List<RunStatus> runs;

    public RunsListResponse(List<RunStatus> runs) {
        this.runs = runs;
    }
}
