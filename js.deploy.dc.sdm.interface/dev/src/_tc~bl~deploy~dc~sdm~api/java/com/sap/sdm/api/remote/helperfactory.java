package com.sap.sdm.api.remote;

import java.io.File;
import java.io.IOException;

import com.sap.sdm.api.remote.PrerequisiteErrorHandlingRule;

/**
 * Enables the client to create objects that are not provided by the SDM server.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.

 */
public interface HelperFactory {

  /**
   * Creates a <code>Param</code> object with the specified type, name, display
   * name, value and &quot;hidden&quot; attribute. The type has to correspond
   * to one of the permitted types specified by <code>ParamTypes</code>. The 
   * value has to match the specified type.
   * 
   * @param type the <code>ParamType</code> of the parameter
   * @param name the name of the parameter
   * @param displayName the display name of the parameter
   * @param value the value of the parameter; for primitive types, an instance
   *         of the corresponding wrapper class has to be provided; 
   *         <code>null</code> is a possible value
   * @param shallBeHidden indicates whether the value should be displayed or
   *         transported in clear text or decrypted
   * @return a <code>Param</code> with the specified properties
   * @throws IllegalArgumentException if <code>type</code> does not represent
   *          a permitted type or <code>value</code> does not match the 
   *          specified type
   * @throws NullPointerException if <code>type</code> or <code>name</code>
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes
   */
  public Param createParam(
    ParamType type,
    String name,
    String displayName,
    Object value,
    boolean shallBeHidden)
    throws RemoteException;

  /**
   * Creates a <code>Param</code> object with the specified type, name, 
   * value and &quot;hidden&quot; attribute. The type has to correspond
   * to one of the permitted types specified by <code>ParamTypes</code>. The 
   * value has to match the specified type. This is a deprecated version of the
   * method above which uses a new parameter <code>displayName</code>. 
   * Using this method means that the <code>displayName</code> will be set to
   * the value of <code>name</code> internally.
   * 
   * @param type the <code>ParamType</code> of the parameter
   * @param name the name of the parameter
   * @param value the value of the parameter; for primitive types, an instance
   *         of the corresponding wrapper class has to be provided; 
   *         <code>null</code> is a possible value
   * @param shallBeHidden indicates whether the value should be displayed or
   *         transported in clear text or decrypted
   * @return a <code>Param</code> with the specified properties
   * @throws IllegalArgumentException if <code>type</code> does not represent
   *          a permitted type or <code>value</code> does not match the 
   *          specified type
   * @throws NullPointerException if <code>type</code> or <code>name</code>
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes
   * @deprecated
   */
  public Param createParam(
    ParamType type,
    String name,
    Object value,
    boolean shallBeHidden)
    throws RemoteException;
    
  /**
   * Creates a <code>ParamType</code> object with the specified type. The type 
   * has to correspond to one of the permitted types specified by 
   * <code>ParamTypes</code>.
   * 
   * @param typeAsInt an <code>int</code> representation of the type;
   *         the permitted values are defined in <code>ParamTypes</code>
   * @return a <code>ParamType</code> 
   * @throws IllegalArgumentException if the value of <code>typeAsInt</code>
   *          is not permitted
   * @see com.sap.sdm.api.remote.ParamTypes
   */
  public ParamType createType(int typeAsInt) throws RemoteException;

  /**
   * Creates a <code>TargetSystem</code> object with the specified ID, type 
   * and an optional description. The type has to be one of the types returned 
   * by <code>SDMConfig</code>.
   * 
   * @param id a <code>String</code> defining the ID of the target system
   * @param type the <code>ServerType</code> of the target system
   * @param description a <code>String</code> providing a description of the
   *         target system; may be <code>null</code>
   * @return a <code>TargetSystem</code> with the specified properties
   * @throws IllegalArgumentException if <code>type</code> represents an 
   *          unknown server type
   * @throws NullPointerException if <code>id</code> or <code>type</code> is
   *          <code>null</code>
   * @see com.sap.sdm.api.remote.SDMConfig
   * @see com.sap.sdm.api.remote.ServerType
   * @see com.sap.sdm.api.remote.TargetSystem
   */
  public TargetSystem createTargetSystem(
    String id,
    ServerType type,
    String description)
    throws RemoteException;
    
  /**
   * Creates a <code>DeployItem</code> for the specified archive.
   * 
   * @param archive a <code>File</code> specifying the archive
   * @return a <code>DeployItem</code> for the specified archive
   * @throws IOException if an I/O exception of some sort has occurred
   * @throws NullPointerException if <code>archive</code> is <code>null</code>
   */
  public DeployItem createDeployItem(File archive) 
    throws RemoteException, IOException;

  /**
   * Creates a <code>ComponentVersionHandlingRule</code> according to the specified
   * <code>int</code> represantation of the rule.
   * @param ruleAsInt a <code>int</code> specifying the type of rule wanted
   * @return a <code>ComponentVersionHandlingRule</code>
   * @throws IllegalArgumentException if the specified int represantation is unknown
   * @see com.sap.sdm.api.remote.ComponentVersionHandlingRules for possible values
   */
  public ComponentVersionHandlingRule createComponentVersionHandlingRule(int ruleAsInt)
    throws RemoteException;
    
  /**
   * Creates a <code>ErrorHandlingRule</code> according to the specified
   * <code>int</code> represantation of the rule.
   * This method is new with API client version 5.
   * 
   * @param ruleAsInt a <code>int</code> specifying the type of rule wanted
   * @return a <code>ErrorHandlingRule</code>
   * @throws IllegalArgumentException if the specified int represantation is unknown
   * @see com.sap.sdm.api.local.ErrorHandlingRule for possible values
   */
  public ErrorHandlingRule createErrorHandlingRule(int ruleAsInt)
    throws RemoteException;
    
  /**
   * Creates a <code>PrerequisiteErrorHandlingRule</code> according to the specified
   * <code>int</code> represantation of the rule.
   * This method is new with API client version 7.
   * 
   * @param ruleAsInt a <code>int</code> specifying the type of rule wanted
   * @return a <code>PrerequisiteErrorHandlingRule</code>
   * @throws IllegalArgumentException if the specified int represantation is unknown
   * @see com.sap.sdm.api.remote.PrerequisiteErrorHandlingRule for possible values
   */
  public PrerequisiteErrorHandlingRule createPrerequisiteErrorHandlingRule(int ruleAsInt);
  
  /**
   * Creates a <code>UnDeployItem</code>. The two arguments <code>vendor</code>
   * and <code>name</code> are used to identify the component line 
   * (development or software component) of the
   * deployment that should be undeployed.
   * 
   * Thus any deployment that belongs to the same component line will be
   * undeployed not matter what the current version (location/counter) of the
   * deployment is.
   * 
   * @param vendor the vendor of the component to be undeployed
   * @param name   the name of the component to be undeployed
   * @return a <code>UnDeployItem</code>
   * @throws IllegalArgumentException 
   *      if <code>vendor</code> or <code>name</code> was null
   */
  public UnDeployItem createUndeployItem(
    String vendor,
    String name) 
    throws RemoteException;

  /**
   * Creates a <code>UnDeployItem</code>. The four arguments <code>vendor</code>,
   * <code>name</code>, <code>location</code> and <code>counter</code> are used
   * to identify the component (development or software component) of the
   * deployment that should be undeployed.
   *  
   * @param vendor the vendor of the component to be undeployed
   * @param name   the name of the component to be undeployed
   * @param location the location of the component to be undeployed
   * @param counter the counter of the component to be undeployed
   * @return a <code>UnDeployItem</code>
   * @throws IllegalArgumentException 
   *   if <code>vendor</code>, <code>name</code>, <code>location</code> or
   * <code>counter</code> was null or <code>counter</code> had a wrong
   * format.
   */
  public UnDeployItem createUndeployItem(
    String vendor,
    String name,
    String location,
    String counter) 
    throws RemoteException;
    
}
