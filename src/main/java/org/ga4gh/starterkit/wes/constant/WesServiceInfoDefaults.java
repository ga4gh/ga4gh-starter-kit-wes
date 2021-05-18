package org.ga4gh.starterkit.wes.constant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WesServiceInfoDefaults {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final String ID = "org.ga4gh.starterkit.wes";

    public static final String NAME = "GA4GH Starter Kit WES Service";

    public static final String DESCRIPTION = "An open source, community-driven"
        + " implementation of the GA4GH Workflow Execution Service (WES)"
        + "API specification.";

    public static final String CONTACT_URL = "mailto:info@ga4gh.org";

    public static final String DOCUMENTATION_URL = "https://github.com/ga4gh/ga4gh-starter-kit-wes";

    public static final LocalDateTime CREATED_AT = LocalDateTime.parse("2020-01-15T12:00:00Z", DATE_FORMATTER);

    public static final LocalDateTime UPDATED_AT = LocalDateTime.parse("2020-01-15T12:00:00Z", DATE_FORMATTER);;

    public static final String ENVIRONMENT = "test";

    public static final String VERSION = "0.0.1";

    public static final String ORGANIZATION_NAME = "Global Alliance for Genomics and Health";

    public static final String ORGANIZATION_URL = "https://ga4gh.org";

    public static final String SERVICE_TYPE_GROUP = "org.ga4gh";

    public static final String SERVICE_TYPE_ARTIFACT = "wes";

    public static final String SERVICE_TYPE_VERSION = "1.0.1";

    public static final String NEXTFLOW_VERSION = "21.04.0";

}
