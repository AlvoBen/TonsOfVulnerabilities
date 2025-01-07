﻿package com.sap.engine.services.iiop.csiv2.interceptors;

import org.omg.CORBA.TSIdentification;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.Encoding;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitializer;
import com.sap.engine.services.iiop.server.CorbaServiceFrame;

public class SecurityInitializer extends org.omg.CORBA.LocalObject implements ORBInitializer {

  public SecurityInitializer() {

  }

  /**
   * This method is called during ORB initialization.
   * @param the info object that provides initialization attributes
   *        and operations by which interceptors are registered.
   */
  public void pre_init(org.omg.PortableInterceptor.ORBInitInfo info) {

  }

  /**
   * This method is called during ORB initialization.
   * @param the info object that provides initialization attributes
   *        and operations by which interceptors are registered.
   */
  public void post_init(org.omg.PortableInterceptor.ORBInitInfo info) {
    CodecFactory codecFactory = info.codec_factory();
    Encoding enc = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);
    Codec codec = null;
    try {
      codec = codecFactory.create_codec(enc);
    } catch (UnknownEncoding e) {
      throw new INTERNAL(0, CompletionStatus.COMPLETED_NO);
    }
// register the interceptors.
    try {
      ORB orb = ORB.init();
      info.add_server_request_interceptor(new SecurityServerRequestInterceptor(orb, codec));
      info.add_client_request_interceptor(new SecurityClientRequestInterceptor(orb, codec));
    } catch (DuplicateName e) {
      throw new INTERNAL(0, CompletionStatus.COMPLETED_NO);
    }
  }

}

