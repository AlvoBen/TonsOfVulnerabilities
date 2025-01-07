package com.sap.sdm.api.remote;

/**
 * Represents a parameter. A parameter is a name-value pair, whereat the value 
 * is typed. Additionally, a <code>Param</code> contains recommendations on the 
 * representation of its value and type-specific access methods to its assigned
 * value.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.params.Param</code>.
 */
public interface Param {

  /**
   * Returns the name of the parameter. The name of the parameter serves as an
   * ID for the parameter.
   *
   * @return a <code>String</code> containing the name of the parameter
   */
  public String getName() throws RemoteException;

  /**
   * Returns the display name of the parameter. The display name of the parameter
   * serves as an description for the parameter which could be used e.g. by
   * graphical user interfaces.
   *
   * @return a <code>String</code> description of the name of the parameter
   */
  public String getDisplayName() throws RemoteException;
  
  /**
   * Returns the type of the parameter. 
   * 
   * @return a <code>ParamType</code>, representing the type of the parameter
   * @see ParamType
   */
  public ParamType getType() throws RemoteException;

  /**
   * Returns the value of this <code>Param</code> as <code>Object</code>.
   * 
   * @return an <code>Object</code> representation of the value of this 
   *          <code>Param</code>; may be <code>null</code>
   */
  public Object getValueObject() throws RemoteException;

  /**
   * Sets the value of this <code>Param</code> as an <code>Object</code>.
   * 
   * @param paramValue the value to be set as an <code>Object</code>, whereat 
   *         <code>null</code> is an admitted value
   */
  public void setValue(Object paramValue) throws RemoteException;

  /**
   * Indicates whether this <code>Param</code> contains a value different
   * from <code>null</code>. For parameters of certain types, it is mandatory 
   * to check whether their value is different from <code>null</code>
   * before invoking their type-specific value access methods.
   * 
   * @return <code>true</code> if this <code>Param</code> contains a value 
   *          different from <code>null</code>; <code>false</code> otherwise
   */
  public boolean isValueSet() throws RemoteException;

  /**
   * Indicates whether to represent the value of this <code>Param</code> 
   * in clear text or not when exporting this <code>Param</code> to some
   * external interface.
   * 
   * @return <code>true</code> if the value must not be represented in clear 
   *          text; <code>false</code> otherwise
   */
  public boolean valueShallBeHidden() throws RemoteException;

  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.STRING</code>.
   * 
   * @return the <code>String</code> value of this <code>Param</code>, which
   *          may be <code>null</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.STRING</code>
   * @see com.sap.sdm.api.remote.ParamTypes#STRING
   */
  public String getValueString() throws RemoteException;

  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.BOOLEAN</code>.
   * 
   * @return the <code>boolean</code> value of this <code>Param</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.BOOLEAN</code>, or if its value 
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes#BOOLEAN
   */
  public boolean getValueBoolean() throws RemoteException;

  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.BYTE</code>.
   * 
   * @return the <code>byte</code> value of this <code>Param</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.BYTE</code>, or if its value 
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes#BYTE
   */
  public byte getValueByte() throws RemoteException;

  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.SHORT</code>.
   * 
   * @return the <code>short</code> value of this <code>Param</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.SHORT</code>, or if its value 
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes#SHORT
   */
  public short getValueShort() throws RemoteException;

  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.INT</code>.
   * 
   * @return the <code>int</code> value of this <code>Param</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.INT</code>, or if its value 
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes#INT
   */
  public int getValueInt() throws RemoteException;
  
  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.LONG</code>.
   * 
   * @return the <code>long</code> value of this <code>Param</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.LONG</code>, or if its value 
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes#LONG
   */
  public long getValueLong() throws RemoteException;

  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.FLOAT</code>.
   * 
   * @return the <code>float</code> value of this <code>Param</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.FLOAT</code>, or if its value 
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes#FLOAT
   */
  public float getValueFloat() throws RemoteException;

  /**
   * Returns the value of <code>Param</code> objects with type 
   * <code>ParamTypes.DOUBLE</code>.
   * 
   * @return the <code>double</code> value of this <code>Param</code>
   * @throws IllegalStateException if the type of this <code>Param</code> is
   *          different from <code>ParamTypes.DOUBLE</code>, or if its value 
   *          is <code>null</code>
   * @see com.sap.sdm.api.remote.ParamTypes#DOUBLE
   */
  public double getValueDouble() throws RemoteException;

}
