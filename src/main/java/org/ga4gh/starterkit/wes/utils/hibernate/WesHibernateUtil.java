package org.ga4gh.starterkit.wes.utils.hibernate;

import java.util.ArrayList;

import org.ga4gh.starterkit.common.hibernate.HibernateUtil;
import org.ga4gh.starterkit.wes.model.WesRun;

public class WesHibernateUtil extends HibernateUtil {

    public WesHibernateUtil() {
        super();
        setAnnotatedClasses(new ArrayList<>(){{
            add(WesRun.class);
        }});
    }
}
