/*
 * $Id: LifecycleFactoryImpl.java,v 1.2 2004/04/19 23:50:09 jvisvanathan Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

/**
 * <p>Custom implementation of <code>LifecycleFactory</code> that
 * provides the portlet-specific <code>Lifecycle</code> by default.</p>
 */

public final class LifecycleFactoryImpl extends LifecycleFactory {

    // The Log instance for this class
    private static final Log log = LogFactory.getLog(LifecycleFactoryImpl.class);
    
    // ------------------------------------------------------------ Constructors


    public LifecycleFactoryImpl() {
        if (log.isTraceEnabled()) {
	    log.trace("Created LifecycleFactory " + this);
	}
        addLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE, new LifecycleImpl());
    }


    // ------------------------------------------------------ Instance Variables


    // Registered Lifecycle instances, keyed by lifecycle identifier
    private Map lifecycles = new HashMap();


    // ---------------------------------------------------------- Public Methods


    public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {

        if ((lifecycleId == null) || (lifecycle == null)) {
            throw new NullPointerException();
        }
        synchronized (lifecycles) {
            if (lifecycles.containsKey(lifecycleId)) {
                throw new IllegalArgumentException(lifecycleId);
            }
            lifecycles.put(lifecycleId, lifecycle);
        }
        if (log.isTraceEnabled()) {
	    log.trace("Added LifecycleId " + lifecycleId);
	}

    }


    public Lifecycle getLifecycle(String lifecycleId) {

        if (lifecycleId == null) {
            throw new NullPointerException();
        }
        synchronized (lifecycles) {
            Lifecycle lifecycle = (Lifecycle) lifecycles.get(lifecycleId);
            if (lifecycle != null) {
                if (log.isTraceEnabled()) {
	            log.trace("Returned " + lifecycle + " for lifecycleId "
                            + lifecycleId);
	        }
                return (lifecycle);
            } else {
                throw new IllegalArgumentException(lifecycleId);
            }
        }

    }


    public Iterator getLifecycleIds() {

        synchronized (lifecycles) {
            return (lifecycles.keySet().iterator());
        }

    }


}
