package org.ga4gh.starterkit.wes.model;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Directly from WES specification, contains complete information associated with
 * a workflow run, including supplied parameters, status, log information, and
 * outputs
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RunLog {

    // Run identifier
    private String runId;

    // Initial run request parameters
    private WesRequest request;

    // Workflow run state
    private State state;

    // Log info at run level
    private WesLog runLog;

    // Log info at individual task level
    private List<WesLog> taskLogs;

    // References to output file locations produced during the workflow run
    private Map<String, String> outputs;
}
