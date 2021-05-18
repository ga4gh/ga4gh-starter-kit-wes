package org.ga4gh.starterkit.wes.utils.requesthandler;

import org.ga4gh.starterkit.common.exception.BadRequestException;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.RunTypeDetailsHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class GetRunLogRequestHandler implements RequestHandler<RunLog> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String runId;

    public GetRunLogRequestHandler() {

    }

    public GetRunLogRequestHandler prepare(String runId) {
        this.runId = runId;
        return this;
    }

    public RunLog handleRequest() {
        RunLog runLog = new RunLog();
        runLog.setRunId(runId);
        runLog.setState(State.UNKNOWN);
        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }
        runLog.setRequest(wesRun.toWesRequest());

        try {
            RunManager runManager = runManagerFactory.createRunLauncher(wesRun);
            RunTypeDetailsHandler runTypeDetailsHandler = runManager.getRunTypeDetailsHandler();
            runLog.setState(runTypeDetailsHandler.determineRunStatus().getState());
            runTypeDetailsHandler.completeRunLog(runLog);
        } catch (Exception ex) {
            throw new BadRequestException("Could not load WES run log");
        }
        return runLog;
    }

    /* Setters and getters */

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }
}
