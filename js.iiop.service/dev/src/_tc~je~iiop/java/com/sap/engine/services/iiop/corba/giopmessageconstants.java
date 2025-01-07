/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA;

/**
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public interface GIOPMessageConstants {

  /********************* CONSTANTS ********************/
  /**  GIOP header has 5 fields of total length 12 bytes<br>
   *  <ul>
   *  <li>  magic[4]<br>
   *  <li>  GIOP_version[2]<br>
   *  <li>  byte_order ver1.0 | flags ver1.1[1]<br>
   *  <li>  message_type[1]<br>
   *  <li>  message_size[4]<br>
   *  </ul>
   */
  public static final int GIOP_HEADER_LENGTH = 12;
  /**  GIOP header starts with 4 capital letters 'G', 'I', 'O', 'P'  */
  public static final byte[] MAGIC = {0x47, 0x49, 0x4F, 0x50};
  /**  GIOP   minor version number, possible values 0, 1, 2  */
  public static final byte VERSION_MINOR = 2;
  /**  GIOP   major version number, possible values 1  */
  public static final byte VERSION_MAJOR = 1;
  /**  message byte order that follows GIOP header, in ver1.0 this field is
   boolean in 1.1 and 1.2 is octet*/
  public static final boolean LITTLE_ENDIAN = false; // true = LSB first
  /**  message   type <code>REQUEST</code>  */
  public static final byte REQUEST = 0;
  /**  message   type <code>REPLY</code>  */
  public static final byte REPLY = 1;
  /**  message   type <code>CANCEL_REQUEST</code>  */
  public static final byte CANCEL_REQUEST = 2;
  /**  message   type <code>LOCATE_REQUEST</code>  */
  public static final byte LOCATE_REQUEST = 3;
  /**  message   type <code>LOCATE_REPLY</code>  */
  public static final byte LOCATE_REPLY = 4;
  /**  message   type <code>CLOSE_CONNECTION</code>  */
  public static final byte CLOSE_CONNECTION = 5;
  /**  message   type <code>MESSAGE_ERROR</code>  */
  public static final byte MESSAGE_ERROR = 6;
  /**  message   type <code>FRAGMENT ver1.1 and 1.2 only</code>  */
  public static final byte FRAGMENT = 7;
  /**  message reply type <code>NO_EXCEPTION</code>, no errors encountered  */
  public static final int NO_EXCEPTION = 0;
  /**  message reply type <code>USER_EXCEPTION</code>, thrown exception is user
   defined  <br>
   Ref:[1]15-38*/
  public static final int USER_EXCEPTION = 1;
  /**  message reply type <code>SYSTEM_EXCEPTION</code>, error thrown by system
   follows a specific format <br>
   Ref:[1]15-38 */
  public static final int SYSTEM_EXCEPTION = 2;
  /**  message reply type <code>LOCATION_FORWARD</code>, error thrown by user  */
  public static final int LOCATION_FORWARD = 3;
  /**  the object specified in the corresponding <code>LocateRequest</code> message
   is unknown to the server  */
  public static final int UNKNOWN_OBJECT = 0;
  /**  this server (the originator of the <code>LocateReply</code> message) can
   directly receive requests for the specified object.  */
  public static final int OBJECT_HERE = 1;
  /**  a <code>LocateReply</code> body exists.  */
  public static final int OBJECT_FORWARD = 2;

  /****Constants indecated positions in the GIOP headers*****/
  /*Position of byte indecated VERSION_MAJOR in GIOP Header*/
  public static final int VERSION_MAJOR_POSITION = 4;
  /*Position of byte indecated VERSION_MINOR in GIOP Header*/
  public static final int VERSION_MINOR_POSITION = 5;
  /*Position of byte indecated byte order in GIOP Header*/
  public static final int BYTE_ORDER_POSITION = 6;
  /*Position of byte indecated MESSAGE_TYPE in GIOP Header*/
  public static final int MESSAGE_TYPE_POSITION = 7;

  public static final byte[] NAME_SERVICE_BYTES = "NameService".getBytes();
  public static final byte[] INIT_REQUEST_OPERATION = "INIT".getBytes();
}

