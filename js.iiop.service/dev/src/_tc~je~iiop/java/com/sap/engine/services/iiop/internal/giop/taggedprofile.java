/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.giop;

/**
 * This class contains information sent in some Request GIOP messages.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class TaggedProfile {

  private int profile_id;
  private byte[] profile_data;

  public TaggedProfile() {
    profile_id = 0;
    profile_data = new byte[0];
  }

  public TaggedProfile(int id, byte[] data) {
    profile_id = id;
    profile_data = new byte[data.length];
    System.arraycopy(data, 0, profile_data, 0, data.length);
  }

  public int getProfileID() {
    return profile_id;
  }

  public byte[] getProfileData() {
    byte[] profile_data0 = new byte[profile_data.length];
    System.arraycopy(profile_data, 0, profile_data0, 0, profile_data.length);
    return profile_data0;
  }

  public int length() {
    return profile_data.length;
  }

}

