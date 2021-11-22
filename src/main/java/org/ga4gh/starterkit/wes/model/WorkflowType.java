package org.ga4gh.starterkit.wes.model;

/**
 * Enumeration of the different workflow types/languages the can be supported by
 * the WES service. Valid values include:
 * <ul>
 *  <li>CWL: Common Workflow Language</li>
 *  <li>WDL: Workflow Description Language</li>
 *  <li>NEXTFLOW: Nextflow</li>
 * </ul>
 */
public enum WorkflowType {
    CWL,
    WDL,
    NEXTFLOW
}
