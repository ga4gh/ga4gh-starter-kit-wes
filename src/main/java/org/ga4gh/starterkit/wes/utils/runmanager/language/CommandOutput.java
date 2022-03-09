package org.ga4gh.starterkit.wes.utils.runmanager.language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommandOutput {
    private int exitCode;
    private String stdout;
    private String stderr;
}
