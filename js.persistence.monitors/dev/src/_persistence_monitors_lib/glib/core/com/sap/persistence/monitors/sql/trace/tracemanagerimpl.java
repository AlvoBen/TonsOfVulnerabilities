package com.sap.persistence.monitors.sql.trace;



import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.persistence.monitors.common.JmxUtil;
import com.sap.persistence.monitors.common.NodeSelection;
import com.sap.tc.logging.Location;



public class TraceManagerImpl implements TraceManager {

    
	/** The type which we use to register our MBean RemoteMethods . */
    public static final String MBEAN_TYPE = "SAP_ITSAMSQLTrace_MBeans";
    
    /** The name under which we register our RemoteMethods MBean . */
    public static final String MBEAN_NAME = "SQLTraceServiceMBean";
    
    /** used in constructor */


    private static final Location TRACE = 
        Location.getLocation(TraceManagerImpl.class.getName(),"persistence_monitors_lib","BC-JAS-PER-SQL");

    /**
     * The name of the MBeanServer property that denotes the SAP J2EE Engine
     * cluster node. 
     */
    protected static final String CLUSTERNODE_PROPERTYNAME = 
        "SAP_J2EEClusterNode";

    //-------------------
    // Private Constants -------------------------------------------------------
    //-------------------

    private static final String ATTRIB_IS_ON = "IsOn";
    private static final String ATTRIB_IS_BACK = "IsStackTrace";
    private static final String ATTRIB_PATTERN = "MethodNamePattern";
    private static final String ATTRIB_THRESHOLD = "Threshold";
    private static final String ATTRIB_TRACE_ID = "LastTraceId";
    
    /** Constants for invoking mbean methods with invoke(..) */
    private static final String INVOKE_SET_ONOFF  = "setOnOff";
    private static final String INVOKE_SWITCH_ON = "switchOn";
    private static final String INVOKE_HIGH_LEVEL = "switchOnHighLevel";
    private static final String INVOKE_SWITCH_OFF = "switchOff";
  

    //-------------------
    // Private Variables -------------------------------------------------------
    //-------------------
        
    
    private MBeanServerConnection myMbs = null;
    private boolean serverMode;
    private JmxUtil jmxUtil = null;
    
    //-------------
    // Constructor -------------------------------------------------------------
    //-------------
    
    public TraceManagerImpl(){
    	serverMode = true;
    	jmxUtil = new JmxUtil();
    }
    
    public TraceManagerImpl(MBeanServerConnection _mbs){
    	serverMode = false;
    	jmxUtil = new JmxUtil();
    	myMbs = _mbs;    
    }
    //----------------
    // Public Methods ---------------------------------------------------------
    //----------------

    /**
     * Get the MBean server refrence.
     * <p>
     * Method does a lookup using JNDI.
     * @return A reference to the MBeanServer object.
     */
    public void getMBeanServer() throws NamingException {
        if (this.myMbs == null) {
            InitialContext initCtx = new InitialContext();
            this.myMbs = (MBeanServer) initCtx.lookup("jmx");
        }
        if (this.myMbs == null) {
            TRACE.debugT("getMBeanServer: returning null.");
        } else {
            TRACE.debugT("getMBeanServer: returning not-null myMbs.");
        }
       
    }
    
    public void setMBeanServer(MBeanServerConnection _mbs)  {
      this.myMbs = _mbs;
    }
    
    private void updateMBeanServer() throws Exception {
    	if (serverMode){
    		getMBeanServer();
    	} else{
    		if (this.myMbs == null){
    			 TRACE.debugT(" MBeanServerConnection provided externally is null.");
    			  throw new TraceManagerException(
    					  TraceManagerException.EXTERNAL_ERROR);
    			  
                         
    		}
    	}
    }
    
    /**
     * Get the cluster node we are currently running on.
     * @return <code>null</code> in case the node could not be determined,
     * a string representing the node otherwise.
     */
   
    
    @SuppressWarnings("unchecked")
	private Set getGlobalNames() throws Exception {
    ObjectName global = null;
    global =   jmxUtil.getQuery(true,MBEAN_TYPE,MBEAN_NAME);;
    updateMBeanServer();
   
  
    
    Set names = myMbs.queryNames(global, null);
    if (names == null) {
        TRACE.infoT("getStatus: Could not find a MBean with "
            + "pattern " + global + " at queryNames.");
        return null;
    }
    
    return names;

    }

    
    
    
    
    public void switchOn(NodeSelection nodeSelection) throws Exception{
    	if (nodeSelection.isAllNodes()){
			switchOn(true,null);
		} else {
			 HashSet<String> nodeSet = nodeSelection.getNodeSet();
			switchOn(false,nodeSet);
		}
    }
    
    public void switchOnHighLevel(NodeSelection nodeSelection) throws Exception {
    	if (nodeSelection.isAllNodes()){
			switchOnHighLevel(true,null);
		} else {
		    HashSet<String> nodeSet = nodeSelection.getNodeSet();
			switchOnHighLevel(false,nodeSet);
		}
    }
    
    @SuppressWarnings("unchecked")
	private void switchOn(boolean onAllNodes, Set nodes) throws Exception {
		 Set names = getGlobalNames();
	     Iterator it = names.iterator();
	   
	        
	        
	     while (it.hasNext()) {
	            ObjectName mbName = (ObjectName) it.next();
	            TRACE.debugT("switchOnHighLevel: mbName is "+mbName+".");
	            String currentNode = mbName.getKeyProperty(
	                TraceManagerImpl.CLUSTERNODE_PROPERTYNAME);
	            if (onAllNodes || (nodes != null && nodes.contains(currentNode))) {
	                try {
	                     // invoke mbean method
	                    myMbs.invoke(mbName, INVOKE_SWITCH_ON ,
	                        new Object[] {},                       
	                        new String[] {}
	                        
	                    );
	                } catch (MBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e  );
	                } catch (RuntimeMBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e);
	                           
	                }
	            }
	        }
	    }
	 
	@SuppressWarnings("unchecked")
	private void switchOnHighLevel(boolean onAllNodes, Set nodes) throws Exception {
		 Set names = getGlobalNames();
	     Iterator it = names.iterator();
	   
	        
	        
	     while (it.hasNext()) {
	            ObjectName mbName = (ObjectName) it.next();
	            TRACE.debugT("switchOnHighLevel: mbName is "+mbName+".");
	            String currentNode = mbName.getKeyProperty(
	                TraceManagerImpl.CLUSTERNODE_PROPERTYNAME);
	            if (onAllNodes || (nodes != null && nodes.contains(currentNode))) {
	                try {
	                     // invoke mbean method
	                    myMbs.invoke(mbName, INVOKE_HIGH_LEVEL ,
	                        new Object[] {},                       
	                        new String[] {}
	                        
	                    );
	                } catch (MBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e  );
	                } catch (RuntimeMBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e);
	                           
	                }
	            }
	        }
	    }
		
	

	public void switchOnUserLevel(NodeSelection nodeSelection, String mPat, long tr) throws Exception {
		if (nodeSelection.isAllNodes()){
			setOnOff(true,null,false,false,mPat,tr);
		} else {
		    HashSet<String> nodeSet = nodeSelection.getNodeSet();
			setOnOff(false,nodeSet,false,false,mPat,tr);
		}
	}
	 
	@SuppressWarnings("unchecked")
	private void switchOff(boolean onAllNodes, Set nodes) throws Exception {
		 Set names = getGlobalNames();
	     Iterator it = names.iterator();
	   
	        
	        
	     while (it.hasNext()) {
	            ObjectName mbName = (ObjectName) it.next();
	            TRACE.debugT("switchOnHighLevel: mbName is "+mbName+".");
	            String currentNode = mbName.getKeyProperty(
	                TraceManagerImpl.CLUSTERNODE_PROPERTYNAME);
	            if (onAllNodes || (nodes != null && nodes.contains(currentNode))) {
	                try {
	                     // invoke mbean method
	                    myMbs.invoke(mbName, INVOKE_SWITCH_OFF ,
	                        new Object[] {},                       
	                        new String[] {}
	                        
	                    );
	                } catch (MBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e  );
	                } catch (RuntimeMBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e);
	                           
	                }
	            }
	        }
	    }
	
	/*public void switchOff(NodeSelection nodeSelection) throws Exception{
		if (nodeSelection.isAllNodes()){
			setOnOff(true,null,false,false,"",0);
		} else {
		    HashSet<String> nodeSet = nodeSelection.getNodeSet();
			setOnOff(false,nodeSet,false,false,"",0);
		}
		 
	}*/
	
	public void switchOff(NodeSelection nodeSelection) throws Exception{
    	if (nodeSelection.isAllNodes()){
			switchOff(true,null);
		} else {
			 HashSet<String> nodeSet = nodeSelection.getNodeSet();
			switchOff(false,nodeSet);
		}
    }
	
	public void setOnOff(NodeSelection nodeSelection,Boolean bOn, Boolean bStack, String mPat, long tr )throws Exception {
		if (nodeSelection.isAllNodes()){
			setOnOff(true,null,bOn,bStack,mPat,tr);
		} else {
		    HashSet<String> nodeSet = nodeSelection.getNodeSet();
			setOnOff(false,nodeSet,bOn,bStack,mPat,tr);
		}
	}
	
    @SuppressWarnings("unchecked")
	private void setOnOff(boolean onAllNodes, Set nodes, Boolean bOn, 
	            Boolean bStack, String mPat, long tr) throws Exception {

    	 Set names = getGlobalNames();
	     Iterator it = names.iterator();
	     String trString = ""+tr;
	        
	        
	     while (it.hasNext()) {
	            ObjectName mbName = (ObjectName) it.next();
	            TRACE.debugT("setOnOff: mbName is "+mbName+".");
	            String currentNode = mbName.getKeyProperty(
	                TraceManagerImpl.CLUSTERNODE_PROPERTYNAME);
	            if (onAllNodes || (nodes != null && nodes.contains(currentNode))) {
	                try {
	                     // invoke mbean method
	                    myMbs.invoke(mbName, INVOKE_SET_ONOFF, 
	                        new Object[] {
	                            bOn, bStack, mPat, trString
	                        },
	                        new String[] {
	                            "java.lang.Boolean",
	                            "java.lang.Boolean",
	                            "java.lang.String",
	                            "java.lang.String"
	                        }
	                    );
	                } catch (MBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e  );
	                } catch (RuntimeMBeanException e) {
	                    throw new TraceManagerException(
	                            TraceManagerException.EXTERNAL_ERROR,
	                            e);
	                           
	                }
	            }
	        }
	    }

	 
	 public SortedMap<String,NodeStatus> getStatus(NodeSelection nodeSelection) throws Exception{
		 SortedMap<String,NodeStatus> statusMap = null;
		 if (nodeSelection.isAllNodes()){
				statusMap = getStatus(true,null);
			} else {
			    statusMap = getStatus(false, nodeSelection.getNodeSet());
			}
		 
	        return statusMap;
	 }
	  
	 @SuppressWarnings("unchecked")
	private  SortedMap<String,NodeStatus>getStatus(boolean onAllNodes, Set nodes) throws Exception{
		  SortedMap<String,NodeStatus> statusMap = new TreeMap<String, NodeStatus>();
		  Set names = getGlobalNames();
	      Iterator it = names.iterator();
	      
	      while (it.hasNext()) {
	         ObjectName mbName = (ObjectName) it.next();
	         TRACE.debugT("getStatus: mbName is "+mbName+".");
	         String currentNode = mbName.getKeyProperty(
	            TraceManagerImpl.CLUSTERNODE_PROPERTYNAME);
	            TRACE.debugT("getStatus: current node is "+currentNode);
	            
	          
		     if (onAllNodes || (nodes != null && nodes.contains(currentNode))) {
		    	 TRACE.debugT("getStatus: current node is selected");
		      try {
	           	Boolean switchVal = null;
	           	Boolean backVal = null;
	           	String patVal = null;
	           	String traceIdVal = null;
	           	Long thresholdVal = null;
	           	
	           	String[] attribNameList = {ATTRIB_IS_ON, ATTRIB_IS_BACK, ATTRIB_PATTERN, ATTRIB_TRACE_ID, ATTRIB_THRESHOLD}; 
	            
	            	
	            AttributeList al =  myMbs.getAttributes(mbName, attribNameList);
	            Iterator ait = al.iterator();

	             while(ait.hasNext()){
	            	 Attribute at = (Attribute)ait.next();
	            	 String atName = at.getName();
	            	 
	            	if (atName.equalsIgnoreCase(ATTRIB_IS_ON)){
	            	
	                    switchVal = (Boolean)at.getValue();
	            	 }
	            	
	            	if (atName.equalsIgnoreCase(ATTRIB_IS_BACK)){
	            		backVal = (Boolean)at.getValue();
	            	}
	            	
	            	if (atName.equalsIgnoreCase(ATTRIB_PATTERN)){
	            		patVal = (String)at.getValue();
	            	}
	            	

	            	if (atName.equalsIgnoreCase(ATTRIB_TRACE_ID)){
	            		traceIdVal = (String)at.getValue();
	            	}
	           		
	            	
	            	if (atName.equalsIgnoreCase(ATTRIB_THRESHOLD)){
	            		
	            		Integer intVal = (Integer) at.getValue();
	            		thresholdVal = intVal.longValue();
	            	
	            	}
	            	
	           		
	           	 }
	            	 
	           	             
	         
	             NodeStatus status = new NodeStatusImpl(switchVal, traceIdVal, backVal, patVal, thresholdVal);
	            
	             statusMap.put(currentNode, status);
	             TRACE.debugT("getStatus: Status of node "+currentNode+" is obtained: "+switchVal.toString());
	           } catch (ReflectionException e) {
	                throw new TraceManagerException(
	                        TraceManagerException.EXTERNAL_ERROR,
	                       e);
	           } catch (RuntimeMBeanException e) {
	                throw new TraceManagerException(
	                        TraceManagerException.EXTERNAL_ERROR,
	                       e);
	           } // end try catch
		     
		     }   //end this is a selected node
		     else {
		    	 TRACE.debugT("getStatus: current node is not selected");
		     }
		     
	        }    //end nodeloop 
	        
	      TRACE.debugT("getStatus: Exititing method with "+statusMap.size()+" entries");
	      
	        return statusMap;
	 }
	
	 
	 public String getVersion(){
		 return "1.0";
	  }
	 
	 
	 public boolean ping(){
		 boolean ret;
		 try {
			 getGlobalNames();
			ret = true;
		} catch (Exception e) {
			ret = false;
		}
		
		return ret;
	 }


   
   

    
}
 
 
 
 



