package org.ga4gh.starterkit.wes.utils.runlauncher.setup.type;

import org.ga4gh.starterkit.wes.model.WesRun;

public interface WorkflowTypeRunSetup {

    public void setWesRun(WesRun wesRun);
    public WesRun getWesRun();
    public String[] constructCommand() throws Exception;
    
}
