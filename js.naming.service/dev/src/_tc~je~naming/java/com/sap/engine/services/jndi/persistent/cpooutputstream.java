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

import com.sap.engine.services.jndi.implclient.ClientContext;
import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;

import javax.rmi.PortableRemoteObject;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import com.sap.engine.interfaces.cross.ObjectReference;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class CPOOutputStream extends ObjectOutputStream {
	
	private final static Location LOG_LOCATION = Location.getLocation(CPOOutputStream.class);

    /**
     * Hashtable for CPOs
     */
    protected static ConcurrentHashMapObjectObject CPOTable = new ConcurrentHashMapObjectObject();
    /**
     * Stores client context
     */
    private ClientContext context = null;

    /**
     * Constructor
     *
     * @param baos Swap's BAO Stream
     * @param cc   Client context to work with
     * @throws IOException Thrown if a problem occures.
     */
    public CPOOutputStream(ByteArrayOutputStream baos, ClientContext cc) throws java.io.IOException {
        super(baos);
        this.context = cc;
        enableReplaceObject(true);
    }

    /**
     * Writes header
     *
     * @throws IOException Thrown if a problem occures.
     */
    public void writeStreamHeader() throws java.io.IOException {
        //  write( (int)-120 );
    }

    /**
     * Replaces an object
     *
     * @param obj Object to set
     * @return Result from the operation
     */
    public Object replaceObject(Object obj) {
        try {
            if (obj instanceof Remote) {
                P4ObjectBroker broker = P4ObjectBroker.init();
                broker.setURLList(broker.loadObject((Remote) obj));
                // map reference to CPO in hashtable. For persistence bind the reference
                //ObjectReference cref = ((PortableRemoteObject)obj).getReference();
                //       ObjectReference cref = JNDIFrame.crossContext.getReference((Remote)obj, JNDIFrame.PROTOCOL_USED);
                ObjectReference cref = null;
                if (context.referenceFactory != null) {
                    cref = context.referenceFactory.getObjectReference((Remote) obj);
                } else {
                    cref = (ObjectReference) PortableRemoteObject.narrow(obj, ObjectReference.class);
                }

                //String cref = (new Object()).toString();
                if (context.remoteReferenceHash == null) {
                    context.remoteReferenceHash = new ConcurrentHashMapObjectObject();
                }

                context.remoteReferenceHash.put(cref, obj);
                //       this.CPOTable.put(cref, obj);
                //       System.out.println("\n \n \n \n CLUSTER PORTABLE OBJECT");
                // REGISTER IN CPOFACTORY HERE!!!!!! -> RETURN SOME SPECIAL FLAG
                this.context.setLastObj(true);
                //       System.out.println("CREF" + obj);
                return cref;
            } else if (obj instanceof UnicastRemoteObject) {
                // remove upon stopping the server
                this.context.setLastObj(true);
            } else if (obj instanceof Remote) {
                // remove upon stopping the server
                this.context.setLastObj(true);
            }

            return obj;
        } catch (Exception e) {
            LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            return null;
        }
    }

    /**
     * Anotates class
     *
     * @param cl Class to anotate
     * @throws IOException Thrown if a problem occures.
     */
    protected void annotateClass(Class cl) throws java.io.IOException {
        ClassLoader loader = cl.getClassLoader();
        String loaderName = null;
        if (loader != null) {
            loaderName = JNDIFrame.getLoaderName(loader);
        }
        if (loaderName != null) {
            writeObject(loaderName);
        } else {
            writeObject("NoName");
        }
    }

    /**
     * Anotates proxy class
     *
     * @param cl Class to anotate
     * @throws IOException Thrown if a problem occures.
     */
    protected void annotateProxyClass(Class cl) throws java.io.IOException {
        ClassLoader loader = cl.getClassLoader();
        String loaderName = null;

        if (loader != null) {
            loaderName = JNDIFrame.getLoaderName(loader);
        }
        if (loaderName != null) {
            writeObject(loaderName);
        } else {
            writeObject("NoName");
        }
    }

    /**
     * Writes object
     *
     * @param obj Object to write
     * @throws IOException Thrown if a problem occures.
     */
    protected final void writeObjectOverride(Object obj) throws java.io.IOException {
        //System.out.println("\n writing...");
        writeObject(obj);
    }

}

