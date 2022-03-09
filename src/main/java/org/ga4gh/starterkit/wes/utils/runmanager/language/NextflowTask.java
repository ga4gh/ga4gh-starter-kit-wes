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
    private String workdir;
}
