package com.sap.engine.services.jndi.persistentimpl.memory;

/**
 * Object entity for use in memory implementation
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class Obj implements java.io.Serializable {
    /**
     * serial version UID
     */
    static final long serialVersionUID = -8061921011040167705L;
    /**
     * Name of the object
     */
    String name;
    /**
     * Data of the object
     */
    byte[] data;

    /**
     * Type of the object (replicated or not)
     */
    short type;
    /**
     * Name of the link
     */
    public String linkid = null;
    /**
     * ClusterID where the object was created
     */
    private int clusterID;

    /**
     * Constructor
     *
     * @param name Name of the object
     * @param data Data of the object
     * @param type Operation type
     * @param clID cluster ID
     */
    public Obj(String name, byte[] data, short type, int clID) {
        this.name = name;
        this.data = data;
        this.type = type;
        this.clusterID = clID;
    }

    /**
     * Gets the name of the object
     *
     * @return Name of the object
     */
    public String getName() {
        return name;
    }

    public short getType() {
        // return ((short)((((int)(data[2] << 8)) & 0x0000ff00) | ((int)(data[1]
        // & 0x000000ff))));
        return type;
    }

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
	}

}
