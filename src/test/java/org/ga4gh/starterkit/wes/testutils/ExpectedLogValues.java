package org.ga4gh.starterkit.wes.testutils;

import java.util.List;

public class ExpectedLogValues {

    private String expName;
    private List<String> expCmd;
    private String expStdoutMd5;
    private String expStderrMd5;
    private Integer expExitCode;

    public ExpectedLogValues() {

    }

    public void setExpName(String expName) {
        this.expName = expName;
    }

    public String getExpName() {
        return expName;
    }

    public void setExpCmd(List<String> expCmd) {
        this.expCmd = expCmd;
    }

    public List<String> getExpCmd() {
        return expCmd;
    }

    public void setExpStdoutMd5(String expStdoutMd5) {
        this.expStdoutMd5 = expStdoutMd5;
    }

    public String getExpStdoutMd5() {
        return expStdoutMd5;
    }

    public void setExpStderrMd5(String expStderrMd5) {
        this.expStderrMd5 = expStderrMd5;
    }

    public String getExpStderrMd5() {
        return expStderrMd5;
    }

    public void setExpExitCode(Integer expExitCode) {
        this.expExitCode = expExitCode;
    }

    public Integer getExpExitCode() {
        return expExitCode;
    }
}
