package org.ga4gh.starterkit.wes.utils.requesthandler;

import org.ga4gh.starterkit.common.exception.ConflictException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GetRunStatusRequestHandler implements RequestHandler<RunStatus> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String runId;

    public GetRunStatusRequestHandler() {

    }

    public GetRunStatusRequestHandler prepare(String runId) {
        this.runId = runId;
        return this;
    }

    public RunStatus handleRequest() {
        try {
            RunStatus runStatus = new RunStatus();
            runStatus.setRunId(runId);
            runStatus.setState(State.UNKNOWN);

            WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
            if (wesRun != null) {
                RunManager runManager = runManagerFactory.createRunLauncher(wesRun);
                runStatus = runManager.getRunStatus();
            }
            return runStatus;
        } catch (Exception ex) {
            throw new ConflictException("Could not access WorkflowRun state");
        }
    } 

    /* Setters and getters */

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }
    
}
