package org.ga4gh.starterkit.wes.constant;

import static org.ga4gh.starterkit.common.constant.StarterKitConstants.ADMIN;
import static org.ga4gh.starterkit.common.constant.StarterKitConstants.GA4GH;
import static org.ga4gh.starterkit.common.constant.StarterKitConstants.WES;
import static org.ga4gh.starterkit.common.constant.StarterKitConstants.V1;

/**
 * WES API URL path/routing constants
 */
public class WesApiConstants {
    /**
     * Common API route to most (if not all) WES-related controller functions
     */
    public static final String WES_API_V1 = "/" + GA4GH + "/" + WES + "/" + V1;

    /**
     * Common API route to most (if not all) off-spec, administrative controller
     * functions for modifying WES-related entities
     */
    public static final String ADMIN_WES_API_V1 = "/" + ADMIN + WES_API_V1;
}
