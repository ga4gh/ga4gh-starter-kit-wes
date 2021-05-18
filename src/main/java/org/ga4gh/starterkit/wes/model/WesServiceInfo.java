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

/**
 * Extension of the GA4GH base service info specification to include WES-specific
 * properties
 */
public class WesServiceInfo extends ServiceInfo {

    /**
     * Supported workflow languages and versions
     */
    private HashMap<WorkflowType, Set<String>> workflowTypeVersions;

    /**
     * Supported workflow engines and versions
     */
    private HashMap<WorkflowEngine, String> workflowEngineVersions;

    /**
     * Instantiates a new WesServiceInfo object
     */
    public WesServiceInfo() {
        super();
        workflowTypeVersions = new HashMap<>();
        workflowEngineVersions = new HashMap<>();
        setAllDefaults();
    }

    /**
     * Sets all default properties
     */
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

    /**
     * Determine if a workflow language is supported by the service
     * @param workflowType Workflow language specification
     * @return true if workflow language is supported, false if not
     */
    public boolean isWorkflowTypeSupported(WorkflowType workflowType) {
        return workflowTypeVersions.get(workflowType) != null;
    }

    /**
     * Determine if a particular version of a workflow language is supported by the service
     * @param workflowType Workflow language specification
     * @param version Workflow language version
     * @return true if version is supported, false if not
     */
    public boolean isWorkflowTypeVersionSupported(WorkflowType workflowType, String version) {
        if (isWorkflowTypeSupported(workflowType)) {
            return workflowTypeVersions.get(workflowType).contains(version);
        }
        return false;
    }

    /**
     * Adds a new workflow language to the list of supported languages
     * @param workflowType workflow language specification to be supported
     */
    public void addWorkflowType(WorkflowType workflowType) {
        if (workflowTypeVersions.get(workflowType) == null) {
            workflowTypeVersions.put(workflowType, new HashSet<>());
        }
    }

    /**
     * Adds a new workflow language version to the list of supported versions for that language
     * @param workflowType Workflow language specification
     * @param version workflow language version to be supported
     */
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

    /* Setters and getters */

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
