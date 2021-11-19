package org.ga4gh.starterkit.wes.model;

/**
 * Enumeration of the different workflow engines that can be supported by the WES
 * service. A workflow engine indicates a system where a pipeline process is run
 * and supervised. Valid values include:
 * <ul>
 *  <li>
 *      NATIVE: The run is launched on the same host machine as the WES service,
 *      no additional computational resources are leveraged aside from what is 
 *      available to the host machine.
 * </li>
 *  <li>
 *      LSF: IBM's Load Sharing Facility. A batch job scheduling engine common to
 *      high-performance compute (HPC) environments. Jobs are submitted and monitored
 *      via the "bsub" suite of tools. 
 *  </li>
 * </ul>
 */
public enum WorkflowEngine {
    NATIVE,
    // LSF,
    SLURM
}
