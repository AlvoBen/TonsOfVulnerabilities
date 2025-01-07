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
package com.sap.engine.services.iiop.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.PortableInterceptor.IORInterceptor;

import com.sap.engine.services.iiop.internal.interceptors.InterceptorsStorage;
import com.sap.engine.services.iiop.csiv2.CSIIOP.TAG_SSL_SEC_TRANS;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.interfaces.csiv2.SimpleProfileInterface;

/**
 * This class is responsible for managing of CORBA objects' IOR.
 * The structure of IIOP IOR is:
 * <PRE>
 *   module IOP { // IDL
 *     //
 *     // Standard Protocol Profile tag values
 *     //
 *     typedef unsigned long ProfileId;
 *     const ProfileId TAG_INTERNET_IOP = 0;
 *     const ProfileId TAG_MULTIPLE_COMPONENTS = 1;
 *
 *     struct TaggedProfile {
 *       ProfileId tag;
 *       sequence <octet> profile_data;
 *     };
 *     //
 *     // an Interoperable Object Reference is a sequence of
 *     // object-specific protocol profiles, plus a type ID.
 *     //
 *     struct IOR {
 *     string type_id;
 *     sequence <TaggedProfile> profiles;
 *   };
 * </PRE>
 * The stringified (only with hex symbols) IOR is created by ORB.object_to_string(CORBAObject corba_object)
 * method. The host and port fields are valid only if the
 * <code>corba_object</code> is been connected before invoking
 * ORB.object_to_string() with ORB.connect(CORBAObject corba_object) method.
 *
 * @author Georgy Stanev, Nikolai Neichev, Ivan Atanassov
 * @version 4.0
 */
public final class IOR extends IORInfoImpl {

  private String typeID;
  private transient org.omg.CORBA.ORB orb;

  private IOR(org.omg.CORBA.ORB orb0) {   // only Null IOR
    orb = orb0;
    typeID = "";
  }

  public IOR(org.omg.CORBA.ORB orb0, String typeID0, String host, int port, byte[] objkey, byte verMajor, byte verMinor) {
    orb = orb0;
    typeID = typeID0;
    Profile profile = new Profile(orb, TAG_INTERNET_IOP.value, host, port, objkey);
    profile.setVersion(verMajor, verMinor);
    store.put(TAG_INTERNET_IOP.value, profile);
    invokeIORInterceptors();
  }

  public IOR(org.omg.CORBA.ORB orb0, CORBAInputStream is) {
    orb = orb0;
    typeID = is.read_string();
    int profileCount = is.read_long();
    if (profileCount != 0) {  //Null
      Profile[] profiles = new Profile[profileCount];

      for (int i = 0; i < profileCount; i++) {
        int tag = is.read_long();
        profiles[i] = new Profile(orb, tag, is);
        store.put(tag, profiles[i]);
      }
    }
  }

  public static IOR NULL_IOR(org.omg.CORBA.ORB orb0) {
    return new IOR(orb0);
  }

  // uses CORBAOutputStream, not java.io.OutputStream
  public void write_object(OutputStream os) {
    write_object(os, typeID);
  }

  public void write_object(OutputStream os, String id) {
    os.write_string(id);
    os.write_long(store.size());

    for (Profile aValue : store.values()) {
      os.write_octet_array(aValue.toByteArray(), 0, aValue.getByteArrayLength());
    }
  }

  public String getTypeID() {
    return typeID;
  }

  public Profile getProfile() {
    return store.get(TAG_INTERNET_IOP.value);
  }

  public Profile getProfile(int tag) {
    return store.get(tag);
  }

  public org.omg.CORBA.ORB getORB() {
    return orb;
  }

  public boolean isEquivalent(IOR other) {
    if (this.is_nil() || other.is_nil()) {
      return (this.is_nil() && other.is_nil());
    } else {
      return this.getProfile().isEquivalent(other.getProfile());
    }
  }

  public boolean is_nil() {
    return ((store.size() == 0) && (typeID.length() == 0));
  }

  private void invokeIORInterceptors() {
    for(IORInterceptor interceptor:InterceptorsStorage.getIORInterceptors()) {
      interceptor.establish_components(this);
    }

    for(IORInterceptor local_interceptor:InterceptorsStorage.getIORInterceptors(orb)) {
      local_interceptor.establish_components(this);
    }

    Profile profile = store.get(TAG_INTERNET_IOP.value);
    if (profile != null) {
      for (SimpleProfileInterface aComponent : profile.getComponents()) {
        if (aComponent.getTag() == TAG_SSL_SEC_TRANS.value) {
          profile.setPort(0);
        }
      }
    }
  }

}// IOR

