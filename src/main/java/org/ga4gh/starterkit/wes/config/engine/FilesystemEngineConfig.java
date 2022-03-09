package org.ga4gh.starterkit.wes.config.engine;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class FilesystemEngineConfig extends AbstractEngineConfig {

    private String rundir;
    
}
