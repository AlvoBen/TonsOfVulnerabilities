/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.interfaces;

import java.io.Serializable;

/**
 * Abstract base class for providing typesafe enumerations.
 *
 * @author  Stefan Gass, Bernd Follmeg
 * @version 1.0
 */
public abstract class Enum implements Cloneable, Serializable {
    /** Version UID for serialization */
    private static final long serialVersionUID = 1000;

    /** The ID of the Enum instance (may be useful for switch statements). */
    public final int id;

    /** The name of the Enum instance. */
    public final String name;

    /** The greatest ID that was assigned to any <code>Enum</code> instance. */
    protected static int m_greatestId = -1;

    /**
     * Constructs an <code>Enum</code> instance with the specified name.
     * The instance's ID is set automatically. It is calculated by the greatest
     * ID that was previously assigned to any <code>Enum</code> instance plus 1.
     * @param name the name of this instance.
     */
    protected Enum(String name) {
        super();
        this.id = ++m_greatestId;
        this.name = name;
    } //constructor Enum

    /**
     * Constructs an <code>Enum</code> instance with the specified ID and name.
     * @param id the ID of this instance.
     * @param name the name of this instance.
     */
    protected Enum(int id, String name) {
        super();
        this.id = id;
        this.name = name;

        if (id > m_greatestId)
            m_greatestId = id;
    } //constructor Enum

    /**
     *  Indicates whether some other object instance is "equal to" this one.
     *  @return <code>true</code> if this object instance is the same as the
     *  <code>object</code> argument; <code>false</code> otherwise.
     */
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (object == this)
            return true;
        if (!(object instanceof Enum))
            return false;

        Enum enu = (Enum) object;

        if (id != enu.id)
            return false;

        if (name != null) {
            if (!name.equals(enu.name))
                return false;
        } //if
        else if (enu.name != null)
            return false;

        return true;
    } //method equals

    /**
     *  Returns the ID for this instance.
     *  @return the ID for this instance.
     */
    public int getID() {
        return id;
    } //getId

    /**
     *  Returns the name for this instance.
     *  @return the name for this instance.
     */
    public String getName() {
        return name;
    } //getName

    /**
     *  Returns a hash code value for this instance. The hash code is identical
     *  to this instances id.
     *  @return a hash code value for this instance.
     */
    public int hashCode() {
        return id;
    } //method hashCode

    /**
     *  Returns a string representation of this instance.
     *  @return a string representation of this instance.
     */
    public String toString() {
        return name;
    } //method toString
} //class Enum