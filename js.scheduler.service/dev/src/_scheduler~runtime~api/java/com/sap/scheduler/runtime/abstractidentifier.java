/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.runtime;

import java.io.Serializable;
import java.util.Arrays;

import com.sap.guid.GUID;
import com.sap.guid.GUIDFormatException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * XML Serializable
 */
public abstract class AbstractIdentifier implements Serializable, Cloneable {
    private byte[] byteGUID;

    private static final String notInitializedMsg = "This instance has not been initialized"
        + " with a valid GUID";

    /**
     * Serial Version UID. Used for enforcing binary compatibility.
     */
    public static final long serialVersionUID = 1;

    /**
     * Obtains a byte representation of this id. The return byte representation is a valid <code>GUID<code> and it
     * can be used to construct a new <code>GUID</code>.
     *
     * @return - a byte representation of this ID.
     */
    public byte[] getBytes() {
        if (byteGUID == null) throw new IllegalStateException(notInitializedMsg);
        return (byte[]) byteGUID.clone();
    }

    /**
     * Clones this <code>AbstractIdentifier</code> This is a deep copy operation.
     * @return the cloned copy.
     * @throws CloneNotSupportedException - thrown if some clone error occurred. Generally
     * this exception should not be thrown but it's left to allow subclasses to prohibit
     * clonning.
     */
    public Object clone() throws CloneNotSupportedException {
        AbstractIdentifier clone = (AbstractIdentifier)super.clone();
        if (byteGUID != null) { //if initialized
            clone.byteGUID = (byte[])byteGUID.clone();
        }
        return clone;
    }

    /**
     * This method sets the guid contained in this <code>AbstractIdentifier</code>.
     * @param byteGUID - a byte array representation of a <code>GUID</code>
     * @throws IllegalArgumentException - if the byte array does not represent a valid guid.
     * @throws NullPointerException - if <code>byteGUID</code> is null.
     */
    public void setBytes(final byte[] byteGUID) throws IllegalArgumentException {
        try {
            setBytesNoCloneAndCheck(new GUID(byteGUID).toBytes());
        } catch (GUIDFormatException gfe) {
            throw new IllegalArgumentException(gfe.getMessage());
        }
    }

    /**
     * Sets the byte representation of guid for this identity object. This method does not perform
     * any checking on the validity the passed byte array.
     * @param byteGUID - the byte array representation of guid.
     * @throws IllegalStateException - if this <code>AbstractIdentifier</code> has already been initialized.
     */
    protected void setBytesNoCloneAndCheck(final byte[] byteGUID) throws IllegalStateException {
        if (this.byteGUID != null) throw new IllegalStateException("This instance has already been initialized with a valid GUID");
        this.byteGUID = byteGUID;
    }

    /**
     * Compares another object to this <code>AbstractIdentifier</code> instance. Two abstract identifiers
     * are considered equal if they both contain the same guid.
     * @param other - the other object to compare to this one.
     * @return True if the two objects are of the same class and contain the same guid.
     */
    public boolean equals(Object other) {
        if (byteGUID == null) return false;
        if (other != null && other.getClass().equals(this.getClass())) {
            return Arrays.equals(byteGUID, ((AbstractIdentifier) other).byteGUID);
        }
        return false;
    }

    /*
     *
     */
    public int hashCode() {
        if(byteGUID == null) throw new IllegalStateException(notInitializedMsg);

        long replow = ((((long) ((byteGUID[15] & 0xff) |
                ((byteGUID[14] & 0xff) << 8) |
                ((byteGUID[13] & 0xff) << 16) |
                ((byteGUID[12] & 0xff) << 24))) & 0xffffffffL) |
                ((long) ((byteGUID[11] & 0xff) |
                        ((byteGUID[10] & 0xff) << 8) |
                        ((byteGUID[9] & 0xff) << 16) |
                        ((byteGUID[8] & 0xff) << 24))) << 32);

        long rephigh = ((((long) ((byteGUID[7] & 0xff) |
                ((byteGUID[6] & 0xff) << 8) |
                ((byteGUID[5] & 0xff) << 16) |
                ((byteGUID[4] & 0xff) << 24))) & 0xffffffffL) |
                ((long) ((byteGUID[3] & 0xFF) |
                        ((byteGUID[2] & 0xFF) << 8) |
                        ((byteGUID[1] & 0xFF) << 16) |
                        ((byteGUID[0] & 0xFF) << 24))) << 32);

        return (int) (rephigh ^ (rephigh >> 32) ^ replow ^ (replow >> 32));
    }
    
    /**
     * Obtains a hex-string representation of this <code>GUID</code>
     * @return a hex-string representation of this <code>GUID</code>
     */
    public String toString() {
    	try {
    		return new GUID(byteGUID).toHexString();
    	} catch (GUIDFormatException gfe) {
    		final String message = "GUIDFormatException should never be thrown from this code." +
    				" The passed byte arry must be a valid GUID as its obtained by a call to" +
    				"GUID.toBytes(). Most probably this is an error in the GUID Library.";
    		Location.getLocation(AbstractIdentifier.class)
				.traceThrowableT(Severity.ERROR, message, gfe);
    		throw new RuntimeException(message, gfe);
    	}
    }

    /**
     * This is helper method used by ancestor to implement creation of an instance of the given
     * ancestor class from a byte array representation of a guid.
     * @param byteGUID - a byte array representation of a guid.
     * @param cf - <code>ConcreteInstanceFactory</code> used to create instance of the concrete
     * ancestor.
     * @return Null if <code>byteGUID<code> is null. Otherwise this method returns the object
     * return by <code>cf.createNotInitialized()</code>, holding the guid represented by
     * <code>byteGUID</code>
     * @throws IllegalStateException - if the supplied byte array does not contain a valid guid.
     */
    protected static AbstractIdentifier parseID(byte[] byteGUID, ConcreteInstanceFactory cf) {
        if (byteGUID == null) return null;
        AbstractIdentifier concreteInstance = cf.createNotInitialized();
        try {
            concreteInstance.setBytesNoCloneAndCheck(new GUID(byteGUID).toBytes());
            return concreteInstance;
        } catch (GUIDFormatException gfe) {
            throw new IllegalArgumentException(gfe.getMessage());
        }
    }

    /**
     * This is helper method used by heir to implement creation of an instance of the given
     * heir class from a hex-string representation of a guid.
     * @param stringGUID - a hex-string representation of a guid.
     * @param cf - <code>ConcreteInstanceFactory</code> used to create instance of the concrete
     * heir.
     * @return Null if <code>stringGUID<code> is null. Otherwise this method returns the object
     * return by <code>cf.createNotInitialized()</code>, holding the guid represented by
     * <code>stringGUID</code>
     * @throws IllegalStateException - if the supplied hex-string does not contain a valid guid.
     */
    protected static AbstractIdentifier parseID(String stringGUID, ConcreteInstanceFactory cf) {
        if (stringGUID == null) return null;
        AbstractIdentifier concreteInstance = cf.createNotInitialized();
        try {
            concreteInstance.setBytesNoCloneAndCheck(GUID.parseHexGUID(stringGUID).toBytes());
            return concreteInstance;
        } catch (GUIDFormatException gfe) {
            String text = gfe.getMessage();
            if ( text == null) {
              text = "GUID '"+stringGUID+"' is not a valid GUID.";
            }
            throw new IllegalArgumentException(text);
        }
    }

    /**
     * A helper interface. When implemented by heir classes it allows <code>AbstractIdentifier</code> to obtain
     * a new instance of the given concrete heir in static methods of <code>AbstractIdentifier</code>.
     */
    public static interface ConcreteInstanceFactory {
        /**
         * Creates a new instance of the given heir class. The returned <code>AbstractIdentifier</code> must not
         * hold any identity and thus a call to <code>setBytesNoCloneAndCheck</code> and <code>setBytes</code>
         * should pass without throwing <code>IllegalStateException</code?
         * @return the newly created not initialized heir class.
         */
        AbstractIdentifier createNotInitialized();
    }
}


