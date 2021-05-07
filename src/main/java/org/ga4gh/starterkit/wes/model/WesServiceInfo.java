package org.ga4gh.starterkit.wes.model;

import org.ga4gh.starterkit.common.model.ServiceInfo;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.ID;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.NAME;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.DESCRIPTION;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.CONTACT_URL;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.DOCUMENTATION_URL;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.CREATED_AT;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.UPDATED_AT;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.ENVIRONMENT;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.VERSION;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.ORGANIZATION_NAME;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.ORGANIZATION_URL;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.SERVICE_TYPE_GROUP;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.SERVICE_TYPE_ARTIFACT;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.SERVICE_TYPE_VERSION;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.NEXTFLOW_VERSION;

public class WesServiceInfo extends ServiceInfo {

    private HashMap<WorkflowType, Set<String>> workflowTypeVersions;

    private HashMap<WorkflowEngine, String> workflowEngineVersions;

    public WesServiceInfo() {
        super();
        workflowTypeVersions = new HashMap<>();
        workflowEngineVersions = new HashMap<>();
        setAllDefaults();
    }

    private void setAllDefaults() {
        setId(ID);
        setName(NAME);
        setDescription(DESCRIPTION);
        setContactUrl(CONTACT_URL);
        setDocumentationUrl(DOCUMENTATION_URL);
        setCreatedAt(CREATED_AT);
        setUpdatedAt(UPDATED_AT);
        setEnvironment(ENVIRONMENT);
        setVersion(VERSION);
        getOrganization().setName(ORGANIZATION_NAME);
        getOrganization().setUrl(ORGANIZATION_URL);
        getType().setGroup(SERVICE_TYPE_GROUP);
        getType().setArtifact(SERVICE_TYPE_ARTIFACT);
        getType().setVersion(SERVICE_TYPE_VERSION);
        addWorkflowType(WorkflowType.NEXTFLOW);
        addWorkflowTypeVersion(WorkflowType.NEXTFLOW, NEXTFLOW_VERSION);
        addWorkflowEngineVersion(WorkflowEngine.NATIVE, "");
    }

    // Convenience API methods for workflowTypeVersions

    public boolean isWorkflowTypeSupported(WorkflowType workflowType) {
        return workflowTypeVersions.get(workflowType) != null;
    }

    public boolean isWorkflowTypeVersionSupported(WorkflowType workflowType, String version) {
        if (isWorkflowTypeSupported(workflowType)) {
            return workflowTypeVersions.get(workflowType).contains(version);
        }
        return false;
    }

    public void addWorkflowType(WorkflowType workflowType) {
        if (workflowTypeVersions.get(workflowType) == null) {
            workflowTypeVersions.put(workflowType, new HashSet<>());
        }
    }

    public void addWorkflowTypeVersion(WorkflowType workflowType, String version) {
        Set<String> versionsList = workflowTypeVersions.get(workflowType);
        if (versionsList != null) {
            versionsList.add(version);
        }
    }

    // Convenience API methods for workflowEngineVersions

    public boolean isWorkflowEngineSupported(WorkflowEngine workflowEngine) {
        return workflowEngineVersions.get(workflowEngine) != null;
    }

    public boolean isWorkflowEngineVersionSupported(WorkflowEngine workflowEngine, String version) {
        if (isWorkflowEngineSupported(workflowEngine)) {
            return workflowEngineVersions.get(workflowEngine).contains(version);
        }
        return false;
    }

    public void addWorkflowEngineVersion(WorkflowEngine workflowEngine, String version) {
        workflowEngineVersions.put(workflowEngine, version);
    }

    // Setters and getters

    public void setWorkflowTypeVersions(HashMap<WorkflowType, Set<String>> workflowTypeVersions) {
        this.workflowTypeVersions = workflowTypeVersions;
    }

    public HashMap<WorkflowType, Set<String>> getWorkflowTypeVersions() {
        return workflowTypeVersions;
    }

    public void setWorkflowEngineVersions(HashMap<WorkflowEngine, String> workflowEngineVersions) {
        this.workflowEngineVersions = workflowEngineVersions;
    }

    public HashMap<WorkflowEngine, String> getWorkflowEngineVersions() {
        return workflowEngineVersions;
    }
}
