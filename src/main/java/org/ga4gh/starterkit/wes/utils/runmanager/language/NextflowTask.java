package org.ga4gh.starterkit.wes.utils.runmanager.language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NextflowTask {

    private String process;
    private String startTime;
    private String completeTime;
    private String exitCode;
    private String absoluteWorkdir; // e.g. /path/to/wes_runs/work/e6/f8/fa/e6f8faa2-1da3-4620-bc45-a2ff95735a74/work/
    private String relativeWorkdir; // e.g. work/e6/f8/fa/e6f8faa2-1da3-4620-bc45-a2ff95735a74/work/
}
