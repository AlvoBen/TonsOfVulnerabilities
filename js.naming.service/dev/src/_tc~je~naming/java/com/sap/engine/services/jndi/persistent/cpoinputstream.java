/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistent;

import java.io.*;
import java.util.HashMap;

import com.sap.engine.interfaces.cross.ObjectReference;
import com.sap.engine.interfaces.cross.ObjectReferenceImpl;
///////import com.sap.engine.services.rmi_p4.StubBase;
///////import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.jndi.implclient.ClientContext;
import com.sap.engine.services.jndi.implclient.OffsetClientContext;
import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.Constants;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Input stream for CPO objects
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public abstract class CPOInputStream extends ObjectInputStream {

	private final static Location LOG_LOCATION = Location.getLocation(CPOInputStream.class);
	
  /**
   * Stores client context
   */
  protected ClientContext context = null;

  

  /**
   * Constructor
   *
   * @param bais Input stream to use
   * @param cc   Client Context to use
   * @throws IOException Thrown if a problem occurs.
   */
  public CPOInputStream(ByteArrayInputStream bais, ClientContext cc) throws java.io.IOException {
    super(bais);
    this.context = cc;
    enableResolveObject(true);
  }

  //  public SwapInputStream( ByteArrayInputStream bais,HomeKey home)throws IOException{
  //    super( bais ) ;
  //    this.home = home ;
  //    this.bais1 = bais ;
  //    enableResolveObject( true );
  //  }
  /**
   * Resolves class
   *
   * @param obj Object to be resolved
   * @return Resolved result
   */
  public Object resolveObject(Object obj) {
    try {
      if (obj instanceof ObjectReference) {
        this.context.setLastObj(true);

        //        PortableRemoteObject cpo = (PortableRemoteObject)CPOOutputStream.CPOTable.get(obj);
        //        cpo = null;
        //        if (context.isDestructive()) {
        //          CPOOutputStream.CPOTable.remove(obj);
        //        }
        //        if (cpo != null) {
        //          return cpo;
        //        } else {
        if (context.referenceFactory != null && obj instanceof ObjectReferenceImpl) {
          ((ObjectReferenceImpl) obj).setProtocolName(context.referenceFactory.protocolName());
        }

        Object robj = null;
        if (context instanceof OffsetClientContext) {
          ClassLoader applicationClassLoader = ((OffsetClientContext) context).getApplicationClassLoader();
          ClassLoader additionalClassLoader = ((OffsetClientContext) context).getAdditionalClassLoader();

          if (additionalClassLoader != null) {
            robj = ((ObjectReference) obj).toObject(additionalClassLoader, context.getRemoteContext());
          }

          if (robj == null) {
            robj = ((ObjectReference) obj).toObject(applicationClassLoader, context.getRemoteContext());

            if (robj == null) {
              robj = ((ObjectReference) obj).toObject(getClass().getClassLoader(), context.getRemoteContext());

              if (robj == null) {
                robj = ((ObjectReference) obj).toObject(context.commonLoader, context.getRemoteContext());
              }
            }
          }
        } else {
          robj = ((ObjectReference) obj).toObject(getClass().getClassLoader(), context.getRemoteContext());
          if (robj == null) {
            robj = ((ObjectReference) obj).toObject(context.commonLoader, context.getRemoteContext());
          }
        }

        if (robj == null) {
          robj = ((ObjectReference) obj).toObject(Thread.currentThread().getContextClassLoader(), context.getRemoteContext());
        }

        return (robj == null ? obj : robj);
        //        }
      } else {
        return obj;
      }
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      return obj;
    }
  }

  /**
   * Resolves class
   *
   * @param osc Object to be resolved
   * @return Resolved result
   * @throws ClassNotFoundException Thrown if a problem occurs.
   * @throws IOException            Thrown if a problem occurs.
   */
  protected abstract Class resolveClass(ObjectStreamClass osc) throws java.io.IOException, ClassNotFoundException;

  /**
   * Reads header
   *
   * @throws IOException Thrown if a problem occurs.
   */
  protected final void readStreamHeader() throws java.io.IOException {
    //     try {
    //        int c = read();
    //     } catch (StreamCorruptedException scex) {
    //       System.out.println(" wow---------"+ scex.getMessage());
    //     }
  }

  /**
   * Reads object
   *
   * @return Object read
   * @throws IOException            Thrown if a problem occurs.
   * @throws ClassNotFoundException Thrown if a problem occures.
   */
  protected final Object readObjectOverride() throws ClassNotFoundException, java.io.IOException {
    Object obj = readObject();
    return obj;
  }

}

