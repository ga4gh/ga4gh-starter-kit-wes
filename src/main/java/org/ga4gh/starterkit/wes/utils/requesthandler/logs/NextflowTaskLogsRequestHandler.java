package org.ga4gh.starterkit.wes.utils.requesthandler.logs;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NextflowTaskLogsRequestHandler implements RequestHandler<String> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String channel;
    private String runId;
    private String subdirA;
    private String subdirB;

    public NextflowTaskLogsRequestHandler prepare(String channel, String runId,
        String subdirA, String subdirB
    ) {
        this.channel = channel;
        this.runId = runId;
        this.subdirA = subdirA;
        this.subdirB = subdirB;
        return this;
    }

    public String handleRequest() {
        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }
        RunManager runManager = runManagerFactory.createRunLauncher(wesRun);
        String channelFileSuffix = channel.equals("stdout") ? "out" : "err";
        Path filePath = Paths.get("work", subdirA, subdirB, ".command." + channelFileSuffix);
        
        try {
            return runManager.getRunEngineDetailsHandler().getRequestedFileContents(filePath.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    /* Setters and getters */

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public void setSubdirA(String subdirA) {
        this.subdirA = subdirA;
    }

    public String getSubdirA() {
        return subdirA;
    }

    public void setSubdirB(String subdirB) {
        this.subdirB = subdirB;
    }

    public String getSubdirB() {
        return subdirB;
    }
}
