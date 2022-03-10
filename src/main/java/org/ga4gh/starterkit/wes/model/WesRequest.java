package org.ga4gh.starterkit.wes.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inferred from WES specification, contains original submission parameters from
 * a workflow run request. Known as 'RunRequest' in WES spec.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WesRequest {

    // Key/value mapping of workflow input parameters
    private Map workflowParams;

    // Workflow language specification (e.g. CWL, WDL, Nextflow)
    private WorkflowType workflowType;

    // Workflow language specification version
    private String workflowTypeVersion;

    // Key/value mapping of tags
    private Map<String, String> tags;

    // Key/value mapping of workflow engine-specific parameters
    private Map<String, String> workflowEngineParameters;

    // URL to workflow source
    private String workflowUrl;
}
