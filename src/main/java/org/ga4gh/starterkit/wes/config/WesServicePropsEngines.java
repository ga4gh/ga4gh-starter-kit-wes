package org.ga4gh.starterkit.wes.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ga4gh.starterkit.wes.config.engine.NativeEngineConfig;
import org.ga4gh.starterkit.wes.config.engine.SlurmEngineConfig;

public class WesServicePropsEngines {
        
    @JsonProperty("native")
    private NativeEngineConfig nativeConfig;

    private SlurmEngineConfig slurm;

    public WesServicePropsEngines() {
        nativeConfig = new NativeEngineConfig();
    }

    public void setNativeConfig(NativeEngineConfig nativeConfig) {
        this.nativeConfig = nativeConfig;
    }

    public NativeEngineConfig getNativeConfig() {
        return nativeConfig;
    }

    public void setSlurm(SlurmEngineConfig slurm) {
        this.slurm = slurm;
    }

    public SlurmEngineConfig getSlurm() {
        return slurm;
    }
}