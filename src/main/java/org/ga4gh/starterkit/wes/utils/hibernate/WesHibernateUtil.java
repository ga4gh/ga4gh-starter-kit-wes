package org.ga4gh.starterkit.wes.utils.hibernate;

import java.util.ArrayList;
import org.ga4gh.starterkit.common.hibernate.HibernateUtil;
import org.ga4gh.starterkit.wes.model.WesRun;

/**
 * Provides access to WES entities/tables in the database, enabling access, creation,
 * updating, and deleting of workflow runs and associated entities.
 */
public class WesHibernateUtil extends HibernateUtil {

    /**
     * Instantiates a WesHibernateUtil object with WES entities registered
     */
    public WesHibernateUtil() {
        super();
        setAnnotatedClasses(new ArrayList<>(){{
            add(WesRun.class);
        }});
    }
}
