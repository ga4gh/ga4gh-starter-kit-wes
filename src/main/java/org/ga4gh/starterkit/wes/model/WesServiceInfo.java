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
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.ORGANIZATION_NAME;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.ORGANIZATION_URL;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.SERVICE_TYPE_GROUP;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.SERVICE_TYPE_ARTIFACT;
import static org.ga4gh.starterkit.wes.constant.WesServiceInfoDefaults.SERVICE_TYPE_VERSION;

public class WesServiceInfo extends ServiceInfo {

    public WesServiceInfo() {
        super();
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
    }
}
