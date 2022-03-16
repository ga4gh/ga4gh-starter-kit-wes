package org.ga4gh.starterkit.wes.config;

import org.ga4gh.starterkit.wes.config.language.NextflowLanguageConfig;
import org.ga4gh.starterkit.wes.config.language.WdlLanguageConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WesServicePropsLanguages {
    
    private NextflowLanguageConfig nextflow;
    private WdlLanguageConfig wdl;

    public WesServicePropsLanguages() {
        nextflow = new NextflowLanguageConfig();
        wdl = new WdlLanguageConfig();
    }
}