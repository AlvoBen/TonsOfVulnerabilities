﻿

  /*
This file is generated by Code Generator
to wrap datatypes of attributes of CIMClass SAP_ITSAMP4ManagementService
WARNING:DO NOT CHANGE THE CODE MANUALLY. 
*/
package com.sap.engine.services.rmi_p4.jmx.model;

import java.util.Date;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.ObjectName;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.openmbean.*;

  public class SAP_ITSAMP4ManagementServiceWrapper implements DynamicMBean    {   
 
 /* The wrapped object to be exposed as MBean */  
  private SAP_ITSAMP4ManagementService mbean;
  
  /* MBeanInfo */
  private static final MBeanInfo mbeanInfo;
  
 /* Initialize MBeanInfo */
  static {
    // attributes
    
       MBeanAttributeInfo[] attributeInfo = new MBeanAttributeInfo[4]; 
    
		  	
   
		     attributeInfo[ 0]		     
		      = new MBeanAttributeInfo("CommunicationProtocolName",	 					
	 					"java.lang.String","Description:",true,false,false);                               
        	  	
   
		     attributeInfo[ 1]		     
		      = new MBeanAttributeInfo("ElementName",	 					
	 					"java.lang.String","Description:A user-friendly name for the object. This property allows each instance to define a user-friendly name IN ADDITION TO its key properties/identity data, and description information. Note that ManagedSystemElement's Name property is also defined as a user-friendly name. But, it is often subclassed to be a Key. It is not reasonable that the same property can convey both identity and a user friendly name, without inconsistencies. Where Name exists and is not a Key (such as for instances of LogicalDevice), the same information MAY be present in both the Name and ElementName properties.",true,false,false);                               
        	  	
   
		     attributeInfo[ 2]		     
		      = new MBeanAttributeInfo("Caption",	 					
	 					"java.lang.String","Description:The Caption property is a short textual description (one- line string) of the object.",true,false,false);                               
        	  	
   
		     attributeInfo[ 3]		     
		      = new MBeanAttributeInfo("Description",	 					
	 					"java.lang.String","Description:The Description property provides a textual description of the object.",true,false,false);                               
        
// operations
MBeanParameterInfo[] signature;
MBeanOperationInfo[] operationInfo = new MBeanOperationInfo[3];

	
// for method GetRemoteObjectsForClusterNode
 signature = new MBeanParameterInfo[1];
signature[0] = new MBeanParameterInfo("ClusterNodeId", "java.lang.String","Description:");
operationInfo[0] = new MBeanOperationInfo("GetRemoteObjectsForClusterNode", "Description:", signature, "[Ljavax.management.openmbean.CompositeData;", MBeanOperationInfo.UNKNOWN);
	
// for method GetRemoteObjectsForInstance
 signature = new MBeanParameterInfo[1];
signature[0] = new MBeanParameterInfo("InstanceName", "java.lang.String","Description:");
operationInfo[1] = new MBeanOperationInfo("GetRemoteObjectsForInstance", "Description:", signature, "[Ljavax.management.openmbean.CompositeData;", MBeanOperationInfo.UNKNOWN);
	
// for method GetRemoteObjectsForCluster
 signature = new MBeanParameterInfo[0];
operationInfo[2] = new MBeanOperationInfo("GetRemoteObjectsForCluster", "Description:", signature, "[Ljavax.management.openmbean.CompositeData;", MBeanOperationInfo.UNKNOWN);




mbeanInfo = new MBeanInfo("SAP_ITSAMP4ManagementService", "SAP_ITSAMP4ManagementService MBean", attributeInfo, null, operationInfo , null);

  }


	 /*
	  * Wraps given SAP_ITSAMP4ManagementService and provides a javax.management.DynamicMBean interface on top
   */
       public  SAP_ITSAMP4ManagementServiceWrapper(SAP_ITSAMP4ManagementService mbean){
              this.mbean = mbean;
       }
       
    /*
	  * Creates an empty wrapper for SAP_ITSAMP4ManagementService
   */
    public SAP_ITSAMP4ManagementServiceWrapper() 
		{
		}
			  /*
   * Sets a new SAP_ITSAMP4ManagementService to be wrapped and returns the old one
   */
  public SAP_ITSAMP4ManagementService setManagedObject(SAP_ITSAMP4ManagementService mbean) {
    SAP_ITSAMP4ManagementService old = this.mbean;
    this.mbean = mbean;
    return old;
  }

  /*
   * Returns the currently wrapped SAP_ITSAMP4ManagementService
   */
  public SAP_ITSAMP4ManagementService getManagedObject() {
    return this.mbean;
  }
		
		
    /*
   * @see javax.management.DynamicMBean#getAttributes(java.lang.String)
   */
      public AttributeList getAttributes(String[] attributes) {
      
     AttributeList list = new AttributeList();
//		Check attributeNames to avoid NullPointerException later on
		 if (attributes == null) {
			 throw new RuntimeOperationsException(
				 new IllegalArgumentException(
					 "attributeNames[] cannot be null"),
				 "Cannot invoke a getter of SAP_ITSAMShortDumpManagementService");
		 }
//		if attributeNames is empty, return an empty result list
		 if (attributes.length == 0)
				 return list;

          if(attributes != null)
            {
                for (int i = 0; i < attributes.length; i++)
                {
                    String attribute = attributes[i];
                    try
                    {
                        Object result = getAttribute(attribute);
                        list.add(new Attribute(attribute, result));
                    }
                    catch (AttributeNotFoundException ignored)
                    {
                    //$JL-EXC$
                    }
                    catch (MBeanException ignored)
                    {
                    //$JL-EXC$
                    }
                    catch (ReflectionException ignored)
                    {
                    //$JL-EXC$
                    }
                }
    
            }
            return list;
	   }


 /**
  * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
  */
	  public AttributeList setAttributes(AttributeList attributes) {
		//		Check attributesto avoid NullPointerException later on
		 if (attributes == null) {
			 throw new RuntimeOperationsException(
				 new IllegalArgumentException(
					 "AttributeList attributes cannot be null"),
				 "Cannot invoke a setter of SAP_ITSAMShortDumpManagementService" );
		 }
		
		AttributeList list = new AttributeList();
		
//		if attributeNames is empty, nothing more to do
		 if (attributes.isEmpty())
			 return list;
	            if (attributes != null)
		            {
		                for (int i = 0; i < attributes.size(); ++i)
		                {
		                    Attribute attribute = (Attribute) attributes.get(i);
		                    try
		                    {
		                        setAttribute(attribute);
		                        list.add(attribute);
		                    }
		                    catch (AttributeNotFoundException ignored)
		                    {
		                    //$JL-EXC$
		                    }
		                    catch (InvalidAttributeValueException ignored)
		                    {
		                    //$JL-EXC$
		                    }
		                    catch (MBeanException ignored)
		                    {
		                    //$JL-EXC$
		                    }
		                    catch (ReflectionException ignored)
		                    {
		                    //$JL-EXC$
		                    }
		                }
		            }
		    
		            return list;
	  }
    
     /**
   * @see javax.management.DynamicMBean#getMBeanInfo()
   */
	public MBeanInfo getMBeanInfo() {
     return mbeanInfo;
	  }   
    
 /**
   * @see javax.management.DynamicMBean\#setAttribute(javax.management.Attribute)
   */    
    public void setAttribute(Attribute attr) throws AttributeNotFoundException, InvalidAttributeValueException,
      MBeanException, ReflectionException {
      String name = attr.getName();
          
         }             
  
   /**
   * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
   */
     public Object invoke(String operationName, Object param[], String signature[])
    throws MBeanException, ReflectionException {
     if (operationName == null) {
        throw new RuntimeOperationsException(
            new IllegalArgumentException(
                "Operation name cannot be null"),
            "Cannot invoke a null operation in " + "SAP_ITSAMP4ManagementService");
            }
            
            if(operationName.equalsIgnoreCase("GetRemoteObjectsForClusterNode") && (signature[0].equals("java.lang.String") || signature[0].equalsIgnoreCase("string")))
            {
            try{return getCDataArrForSAP_ITSAMCrossServiceRemoteObject(mbean.GetRemoteObjectsForClusterNode(
	    	(String) param[0]));
			} catch (OpenDataException e1){
			e1.printStackTrace();
			return e1;
			}
            }
            
            if(operationName.equalsIgnoreCase("GetRemoteObjectsForInstance") && (signature[0].equals("java.lang.String") || signature[0].equalsIgnoreCase("string")))
            {
            try{return getCDataArrForSAP_ITSAMCrossServiceRemoteObject(mbean.GetRemoteObjectsForInstance(
	    	(String) param[0]));
			} catch (OpenDataException e1){
			e1.printStackTrace();
			return e1;
			}
            }
            
            if(operationName.equalsIgnoreCase("GetRemoteObjectsForCluster") && signature.length==0)
            {
            try{return getCDataArrForSAP_ITSAMCrossServiceRemoteObject(mbean.GetRemoteObjectsForCluster(
	    	));
			} catch (OpenDataException e1){
			e1.printStackTrace();
			return e1;
			}
            }
            
            else { 
        // unrecognized operation name:
        throw new ReflectionException(
            new NoSuchMethodException(operationName), 
            "Cannot find the operation " + operationName +
                " in " + "SAP_ITSAMP4ManagementService");
    }
  }
   
     /*
   * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
   */
public Object getAttribute(String arg0 ) throws AttributeNotFoundException, MBeanException,   ReflectionException {
             if (arg0 == null) 
  throw new RuntimeOperationsException(
	 new IllegalArgumentException("Attribute name cannot be null"), 
	 "Cannot invoke a getter of " + "SAP_ITSAMP4ManagementService" +
		 " with null attribute name");  
		     
            if(arg0.equalsIgnoreCase("CommunicationProtocolName"))
	   	    return mbean.getCommunicationProtocolName();
					    
            if(arg0.equalsIgnoreCase("ElementName"))
	   	    return mbean.getElementName();
					    
            if(arg0.equalsIgnoreCase("Caption"))
	   	    return mbean.getCaption();
					    
            if(arg0.equalsIgnoreCase("Description"))
	   	    return mbean.getDescription();
					    
     else {
           throw(new AttributeNotFoundException("Cannot find " + arg0+ " attribute"));
             }
             
            
  }
 
// Conversion methods for CompositeData type Class SAP_ITSAMCrossServiceRemoteObject 
  public static CompositeData getCDataForSAP_ITSAMCrossServiceRemoteObject(com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject metric) throws OpenDataException {
			  if(metric==null){
				  return null;
				}else{
					 String[] attrnames={"Key","JavaClassName","Reference","RemoteObjectDetails","Redirectable","InstanceID","ClusterNodeID","Caption","Description","ElementName"};
					 Object[] attrobj={metric.getKey(),metric.getJavaClassName(),metric.getReference(),metric.getRemoteObjectDetails(),new Boolean(metric.getRedirectable()),metric.getInstanceID(),metric.getClusterNodeID(),metric.getCaption(),metric.getDescription(),metric.getElementName()};

					return new CompositeDataSupport(getCTypeForSAP_ITSAMCrossServiceRemoteObject(),attrnames,attrobj);
							}
		}
public static com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject getSAP_ITSAMCrossServiceRemoteObjectForCData(CompositeData data) {
			 if(data==null){
				return null;
			}else{							
				
				
			/*throw an exception if the value of any key property is null in CData*/
				
				
			/*Initializes the read-only properties to default values if the value is null in CData with in the constructor*/				
				

				com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject result = new com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject((String) data.get("Key"),(String) data.get("JavaClassName"),(String) data.get("Reference"),(String) data.get("RemoteObjectDetails"),data.get("Redirectable")==null? false : ((Boolean) data.get("Redirectable")).booleanValue(),(String) data.get("Caption"),(String) data.get("Description"),(String) data.get("ElementName"));
		         
		         if (data.get("InstanceID") != null) {
			   result.setInstanceID((String) data.get("InstanceID"));
						}if (data.get("ClusterNodeID") != null) {
			   result.setClusterNodeID((String) data.get("ClusterNodeID"));
						} 
					  return result;
					}
		  }
		 
		 public static CompositeType getCTypeForSAP_ITSAMCrossServiceRemoteObject() throws OpenDataException
		 {
		 				 String[] itemNames={"Key","JavaClassName","Reference","RemoteObjectDetails","Redirectable","InstanceID","ClusterNodeID","Caption","Description","ElementName"};
                	String[] itemDescriptions={"Description:","Description:","Description:","Description:","Description:","Description:","Description:","Description:The Caption property is a short textual description (one- line string) of the object.","Description:The Description property provides a textual description of the object.","Description:A user-friendly name for the object. This property allows each instance to define a user-friendly name IN ADDITION TO its key properties/identity data, and description information. Note that ManagedSystemElement's Name property is also defined as a user-friendly name. But, it is often subclassed to be a Key. It is not reasonable that the same property can convey both identity and a user friendly name, without inconsistencies. Where Name exists and is not a Key (such as for instances of LogicalDevice), the same information MAY be present in both the Name and ElementName properties."};
               OpenType[] itemTypes={SimpleType.STRING,SimpleType.STRING,SimpleType.STRING,SimpleType.STRING,SimpleType.BOOLEAN,SimpleType.STRING,SimpleType.STRING,SimpleType.STRING,SimpleType.STRING,SimpleType.STRING };
		
				return new CompositeType("SAP_ITSAMCrossServiceRemoteObject","Description:ManagedElement is an abstract class that provides a common superclass (or top of the inheritance tree) for the non-association classes in the CIM Schema.",itemNames,itemDescriptions,itemTypes);
	  } 


public static com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject[] getSAP_ITSAMCrossServiceRemoteObjectArrForCData(CompositeData[] cd){
			if(cd==null){
			return null;
			}else{
				  com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject[] arr = new com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject[cd.length] ;
				for(int i=0;i< cd.length;i++)
				arr[i] =  getSAP_ITSAMCrossServiceRemoteObjectForCData(cd[i]);
				return arr;
					}
	}

	
public static CompositeData[] getCDataArrForSAP_ITSAMCrossServiceRemoteObject(com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject[] crr) throws OpenDataException{
			if(crr==null){
			return null;
			}else{
     	CompositeDataSupport[] cd = new CompositeDataSupport[crr.length]; 
     	for(int i=0;i<crr.length;i++)
     	cd[i]=(CompositeDataSupport) getCDataForSAP_ITSAMCrossServiceRemoteObject(crr[i]);
     	return cd;
					 }
     }

}
 