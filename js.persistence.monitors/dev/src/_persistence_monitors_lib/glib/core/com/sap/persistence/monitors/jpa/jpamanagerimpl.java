package com.sap.persistence.monitors.jpa;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.jmx.ObjectNameFactory;
import com.sap.tc.logging.Location;


/**
 * Utility class for accessing the remote nodes.
 * <p>
 * Copyright (c) 2004 SAP AG
 * @author Immo-Gert Birn
 */
public class JpaManagerImpl implements JpaManager {

    //------------------
    // Public Constants --------------------------------------------------------
    //------------------
    
	  /** The type which we use to register our MBean RemoteMethods (servlet). */
    public static final String MBEAN_TYPE = "SAP_ITSAMJPA_MBeans";
    
    /** The name under which we register our RemoteMethods MBean (servlet). */
    public static final String MBEAN_NAME = "JPAMonitorsMBean";

    

  

    private static final Location TRACE = 
        Location.getLocation(JpaManagerImpl.class);

    /**
     * The name of the MBeanServer property that denotes the SAP J2EE Engine
     * cluster node. 
     */
    protected static final String CLUSTERNODE_PROPERTYNAME = 
        "SAP_J2EEClusterNode";

    //-------------------
    // Private Constants -------------------------------------------------------
    //-------------------

    /** Constants for invoking mbean methods with getAttribute(..) */
    private static final String ATTRIB_GET_PERSISTENCEUNITS = "PersistenceUnits";
    /** Constants for invoking mbean methods with invoke(..) */
   
    private static final String INVOKE_START_MONITORING = "startMonitoring";
    private static final String INVOKE_STOP_MONITORING = "stopMonitoring";
    
    //-------------------
    // Private Variables -------------------------------------------------------
    //-------------------
        
    private ObjectName globalQuery = null;
    private ObjectName localQuery  = null;
    private MBeanServer myMbs = null;
    
    //-------------
    // Constructor -------------------------------------------------------------
    //-------------
    
    //----------------
    // Public Methods ---------------------------------------------------------
    //----------------

    /**
     * Get the MBean server refrence.
     * <p>
     * Method does a lookup using JNDI.
     * @return A reference to the MBeanServer object.
     */
    public MBeanServer getMBeanServer() throws NamingException {
        if (this.myMbs == null) {
            InitialContext initCtx = new InitialContext();
            this.myMbs = (MBeanServer) initCtx.lookup("jmx");
        }
        if (this.myMbs == null) {
            TRACE.debugT("getMBeanServer: returning null.");
        } else {
            TRACE.debugT("getMBeanServer: returning not-null myMbs.");
        }
        return this.myMbs;
    }
    
    
    
    /**
     * Get the cluster node we are currently running on.
     * @return <code>null</code> in case the node could not be determined,
     * a string representing the node otherwise.
     */
    public String getClusterNode() throws Exception {
        MBeanServer mbs = null;
        mbs = this.getMBeanServer();
        if (mbs == null) {
            TRACE.debugT("getClusterNode: MBeanServer is null => " 
                    + "getClusterNode returns null.");
            return null;
        }
        ObjectName local = this.getLocalQuery();
        if (local != null) {
            Set names = mbs.queryNames(local, null);
            if ( names.size() == 1 ) {
                ObjectName mbName = (ObjectName) names.toArray()[0];
                String node = mbName.getKeyProperty(
                    JpaManagerImpl.CLUSTERNODE_PROPERTYNAME);
                TRACE.debugT("getClusterNode: returning with " + node);
                return node;
            }
        }
        TRACE.debugT("localQuery is null => getClusterNode returns null.");
        return null;
    }

    /**
     * Get the object to query current node for SQLTrace MBean.
     * @return an <code>ObjectName</code> to query the MBean server
     * for SQLTrace MBean on current node. 
     */

    public ObjectName getLocalQuery() throws MalformedObjectNameException {
        if (this.localQuery == null) { 
            this.localQuery = ObjectNameFactory.getNameForServerChildPerNode(
                MBEAN_TYPE, MBEAN_NAME, null, null);
        }
        return this.localQuery;
    }

    /**
     * Get the Object to query all nodes for SQLTrace MBean.
     * @return an <code>ObjectName</code> to query the MBean server
     * for SQLTrace MBean on ALL nodes.
     */
    public ObjectName getGlobalQuery() throws MalformedObjectNameException {
        if (this.globalQuery == null) {
            this.globalQuery = 
                ObjectNameFactory.getPatternForServerChild(MBEAN_TYPE, null);
        }
        return this.globalQuery;
    }

    /**
     * Get the SQLTrace status for all nodes.
     * <p>
     * Calls method <code>RemoteMethodsMBean.getStatus()</code> on all nodes
     * and puts the result into a map. Caller has to react on exceptions.
     * @return a map with the cluster node (<code>String</code> value) as key
     * and the related <code>Status</code> object as value. Null is returned in
     * case no cluster nodes could be found.
     */
    public int getPersistenceUnitCount() throws Exception {
        
        int retour = 0;
        ObjectName global = null;
        global = this.getGlobalQuery();
        
        MBeanServer mbs = null;
        mbs = this.getMBeanServer();
        if (mbs == null) {
            TRACE.infoT("getDataSourceInfo: MBeanServer is null.");
            return -1;
        }
        
        Set names = mbs.queryNames(global, null);
        if (names == null) {
            TRACE.infoT("getDataSourceInfo: Could not find a MBean with "
                + "pattern " + global + " at queryNames.");
            return -1;
        }
        Iterator it = names.iterator();
        while (it.hasNext()) {
            ObjectName mbName = (ObjectName) it.next();
            TRACE.debugT("getStatusOnAllNodes: mbName is "+mbName+".");
            String currentNode = mbName.getKeyProperty(  JpaManagerImpl.CLUSTERNODE_PROPERTYNAME);
            TRACE.debugT("getDataSourceInfo: currentNode is "+currentNode);
            try {
               PersistenceUnitData[] persistenceUnitData = (PersistenceUnitData[])
                mbs.getAttribute(mbName,  ATTRIB_GET_PERSISTENCEUNITS);
               
               int len = persistenceUnitData.length;
               retour = retour + len;
               
            } catch (MBeanException e) {
                throw new JpaManagerException(
                        JpaManagerException.EXTERNAL_ERROR,
                       e);
            } catch (RuntimeMBeanException e) {
                throw new JpaManagerException(
                        JpaManagerException.EXTERNAL_ERROR,
                       e);
            }
        }
        return retour;
    }

    



   
   

    
}
 
 
 
 



