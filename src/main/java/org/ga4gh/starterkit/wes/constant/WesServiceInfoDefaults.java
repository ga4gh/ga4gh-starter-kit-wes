package org.ga4gh.starterkit.wes.constant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Default values for the WES service info response 
 */
public class WesServiceInfoDefaults {

    /**
     * Date serialization format according to ISO 8601
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Date formatter to fulfill ISO 8601 serialization
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    /**
     * Default service id
     */
    public static final String ID = "org.ga4gh.starterkit.wes";

    /**
     * Default service name
     */
    public static final String NAME = "GA4GH Starter Kit WES Service";

    /**
     * Default service description
     */
    public static final String DESCRIPTION = "An open source, community-driven"
        + " implementation of the GA4GH Workflow Execution Service (WES)"
        + "API specification.";

    /**
     * Default service contact URL
     */
    public static final String CONTACT_URL = "mailto:info@ga4gh.org";

    /**
     * Default service documentation URL
     */
    public static final String DOCUMENTATION_URL = "https://github.com/ga4gh/ga4gh-starter-kit-wes";

    /**
     * Default service creation time
     */
    public static final LocalDateTime CREATED_AT = LocalDateTime.parse("2020-01-15T12:00:00Z", DATE_FORMATTER);

    /**
     * Default service last updated time
     */
    public static final LocalDateTime UPDATED_AT = LocalDateTime.parse("2020-01-15T12:00:00Z", DATE_FORMATTER);;

    /**
     * Default service environment
     */
    public static final String ENVIRONMENT = "test";

    /**
     * Default service version
     */
    public static final String VERSION = "0.3.2";

    /**
     * Default service organization name
     */
    public static final String ORGANIZATION_NAME = "Global Alliance for Genomics and Health";

    /**
     * Default service organization URL
     */
    public static final String ORGANIZATION_URL = "https://ga4gh.org";

    /**
     * Default service type group
     */
    public static final String SERVICE_TYPE_GROUP = "org.ga4gh";

    /**
     * Default service type artifact
     */
    public static final String SERVICE_TYPE_ARTIFACT = "wes";

    /**
     * Default service type version
     */
    public static final String SERVICE_TYPE_VERSION = "1.0.1";

    /**
     * Default nextflow version used
     */
    public static final String NEXTFLOW_VERSION = "21.04.0";

}
