package com.sap.engine.services.jndi.cluster;

import com.sap.engine.services.jndi.persistent.Serializator;
import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 * Used as container for different objects. Provides a unified way to store
 * the data.
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class DirObject implements java.io.Serializable {

	private final static Location LOG_LOCATION = Location.getLocation(DirObject.class);

  /**
   * Attributes of the object
   */
  private Attributes attr = null;
  /**
   * The object itself
   */
  private Object obj = null; //$JL-SER$
  /**
   * Stores the class name
   */
  private String className = null;
  /**
   * serial version UID
   */
  static final long serialVersionUID = -7131494175083268593L;
  /**
   * Constructor
   */
  public DirObject() {

  }

  /**
   * Constructor
   *
   * @param attr Attributes of the object
   * @param obj The object to use
   */
  public DirObject(Attributes attr, Object obj) {
    this.attr = attr;
    this.obj = obj;
    this.className = (obj == null) ? "null" : (obj.getClass()).getName();
  }

  /**
   * Sets the class name
   *
   * @param className Class name to be set
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * Gets the class name
   *
   * @return The class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * Sets the object
   *
   * @param obj Object to be set
   */
  public void setObject(Object obj) {
    this.obj = obj;
  }

  /**
   * Gets the object
   *
   * @return Object stored
   */
  public Object getObject() {
    return obj;
  }

  /**
   * Sets new Attributes
   *
   * @param attr Attributes to be set
   */
  public void setAttributes(Attributes attr) {
    this.attr = attr;
  }

  /**
   * Gets the Attributes
   *
   * @return The attributes
   */
  public Attributes getAttributes() {
    return attr;
  }

  /**
   * Constructs a new DirObject
   *
   * @param attributes Attributes to be used
   * @param obj Object to use in the creation
   */
  public static byte[] getNewDirObject(Attributes attributes, Object obj) {
    DirObject dirObject = new DirObject(attributes, obj);
    String classname = dirObject.getClassName();
    byte[] result = null;
    byte[] tempAttr = null;
    byte[] tempClassName = null;
    byte[] tempObject = null;
    int offs = 0;
    int resOffs = 0;
    try {
      if (attributes == null) {
        attributes = new BasicAttributes();
      }

      tempAttr = Serializator.toByteArray(attributes);
      tempClassName = classname.getBytes();
      tempObject = Serializator.toByteArray(obj);
      result = new byte[4 + tempAttr.length + 4 + tempClassName.length + 4 + tempObject.length];
      offs = tempAttr.length;
      result[0] = (byte) (0x000000FF & offs);
      result[1] = (byte) (0x000000FF & (offs >> 8));
      result[2] = (byte) (0x000000FF & (offs >> 16));
      result[3] = (byte) (0x000000FF & (offs >> 24));
      System.arraycopy(tempAttr, 0, result, 4, offs);
      resOffs = offs + 4;
      offs = tempClassName.length;
      result[resOffs] = (byte) (0x000000FF & offs);
      result[resOffs + 1] = (byte) (0x000000FF & (offs >> 8));
      result[resOffs + 2] = (byte) (0x000000FF & (offs >> 16));
      result[resOffs + 3] = (byte) (0x000000FF & (offs >> 24));
      resOffs += 4;
      System.arraycopy(tempClassName, 0, result, resOffs, offs);
      resOffs += offs;
      offs = tempObject.length;
      result[resOffs] = (byte) (0x000000FF & offs);
      result[resOffs + 1] = (byte) (0x000000FF & (offs >> 8));
      result[resOffs + 2] = (byte) (0x000000FF & (offs >> 16));
      result[resOffs + 3] = (byte) (0x000000FF & (offs >> 24));
      resOffs += 4;
      System.arraycopy(tempObject, 0, result, resOffs, offs);
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
    }
    return result;
  }

  /**
   * Gets attributes from the data in byte array
   *
   * @param data Byte array to use
   * @return Attrubutes created
   */
  public static Attributes getAttributes(byte[] data) {
    int attributesLength = ((((data[3] << 8)) & 0xFF000000) | (((data[2] << 8)) & 0x00FF0000) | (((data[1] << 8)) & 0x0000FF00) | ((int) (data[0]) & 0x000000FF));
    byte[] attributesByteArray = new byte[attributesLength];
    System.arraycopy(data, 4, attributesByteArray, 0, attributesLength);
    try {
      return (Attributes) Serializator.toObject(attributesByteArray);
    } catch (javax.naming.NamingException jex) {
    	LOG_LOCATION.traceThrowableT(Severity.PATH, "", jex);
      return new BasicAttributes();
    }
  }

  /**
   * Adds attributes to the data in byte array
   *
   * @param data Byte array to use
   * @param attr Attributes to add
   * @return Data constructed
   */
  public static byte[] setAttributes(byte[] data, Attributes attr) {
    int attributesLength = ((((data[3] << 8)) & 0xFF000000) | (((data[2] << 8)) & 0x00FF0000) | (((data[1] << 8)) & 0x0000FF00) | ((int) (data[0]) & 0x000000FF));
    byte[] newAttributes = null;

    if (attr == null) {
      attr = new BasicAttributes();
    }

    try {
      newAttributes = Serializator.toByteArray(attr);
    } catch (javax.naming.NamingException jex) {
    	LOG_LOCATION.traceThrowableT(Severity.PATH, "", jex);
    }
    int totalSize = data.length - attributesLength + newAttributes.length;
    byte[] newData = new byte[totalSize];
    newData[0] = (byte) (0x000000FF & newAttributes.length);
    newData[1] = (byte) (0x000000FF & (newAttributes.length >> 8));
    newData[2] = (byte) (0x000000FF & (newAttributes.length >> 16));
    newData[3] = (byte) (0x000000FF & (newAttributes.length >> 24));
    System.arraycopy(newAttributes, 0, newData, 4, newAttributes.length);
    System.arraycopy(data, attributesLength + 4, newData, 4 + newAttributes.length, data.length - 4 - attributesLength); // !!!!!
    return newData;
  }

  /**
   * Gets the class name
   *
   * @param data Data to scan
   * @return The class name
   */
  public static String getClassName(byte[] data) {
    int lengthOffset = ((((data[3] << 8)) & 0xFF000000) | (((data[2] << 8)) & 0x00FF0000) | (((data[1] << 8)) & 0x0000FF00) | ((int) (data[0]) & 0x000000FF)) + 4;
    int classNameArrayLength = ((((data[lengthOffset + 3] << 8)) & 0xFF000000) | (((data[lengthOffset + 2] << 8)) & 0x00FF0000) | (((data[lengthOffset + 1] << 8)) & 0x0000FF00) | ((int) (data[lengthOffset]) & 0x000000FF));
    byte[] classNameByteArray = new byte[classNameArrayLength];
    System.arraycopy(data, lengthOffset + 4, classNameByteArray, 0, classNameArrayLength);
    return new String(classNameByteArray);
  }

  /**
   * Used for test&debug purposes
   *
   * @return String representation of the object
   */
  public String toString() {
    return "{ {OBJECT: " + obj + " }\n{ ATTRIBUTES: " + attr + " }";
  }

}

// DirObject Serialized Form
// 
// [0]..[1] - TYPE (SHORT)
// [2]..[5] - ATTRIBUTES_BYTE_ARRAY_LENGTH (INT)
// [6]..[5+ATTRIBUTES_BYTE_ARRAY_LENGTH] - ATTRIBUTES BYTE ARRAY
// [6+ATTRIBUTES_BYTE_ARRAY_LENGTH]..[9+ATTRIBUTES_BYTE_ARRAY_LENGTH] - CLASSNAME_BYTE_ARRAY_LENGTH (INT)
// [10+ATTRIBUTES_BYTE_ARRAY_LENGTH]..[9+ATTRIBUTES_BYTE_ARRAY_LENGTH+CLASSNAME_BYTE_ARRAY_LENGTH] -
//      CLASSNAME BYTE ARRAY
// [10+ATTRIBUTES_BYTE_ARRAY_LENGTH+CLASSNAME_BYTE_ARRAY_LENGTH]
//      ..[13+ATTRIBUTES_BYTE_ARRAY_LENGTH+CLASSNAME_BYTE_ARRAY_LENGTH] - 
//      OBJECT_BYTE_ARRAY_LENGTH
// [14+ATTRIBUTES_BYTE_ARRAY_LENGTH+CLASSNAME_BYTE_ARRAY_LENGTH]
//      ..[13+ATTRIBUTES_BYTE_ARRAY_LENGTH+CLASSNAME_BYTE_ARRAY_LENGTH+OBJECT_BYTE_ARRAY_LENGTH] - 
//      OBJECT BYTE ARRAY

