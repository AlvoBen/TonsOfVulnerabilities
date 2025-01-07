package com.sap.engine.services.deploy.server.dpl_info.module;

import java.io.IOException;

import com.sap.engine.services.deploy.container.util.StringUtils;

public class Resource implements java.io.Serializable {
	private static final long serialVersionUID = -8773118521330883161L;

	public enum AccessType {
		PUBLIC, PRIVATE
	}

	private String name;
	private String type;
	private AccessType accessType;

	/**
	 * @param name resource name. Not null.
	 * @param type resource type. Not null.
	 */
	public Resource(final String name, final String type) {
		this(name, type, AccessType.PUBLIC);
	}

	/**
	 * @param name resource name. Must not be null.
	 * @param type resource type. Must not be null.
	 * @param accessType access type. Must not be null.
	 * @throws NullPointerException in case that some of the required 
	 * parameters is null.
	 */
	public Resource(final String name, final String type, 
		final AccessType accessType) {
		if(name == null || type == null || accessType == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.type = type;
		this.accessType = accessType;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public AccessType getAccessType() {
		return accessType;
	}


	/* Compare two resources on the base of their name and type.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !getClass().equals(o.getClass())) {
			return false;
		}
		if(this == o) {
			return true;
		}
		final Resource other = (Resource)o;
		return name.equals(other.getName()) &&	type.equals(other.getType());
	}

	public int hashCode() {
		return 29 * name.hashCode() + type.hashCode();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type).append("/").append(name).append(" (")
			.append(accessType.toString().toLowerCase()).append(")");
		return StringUtils.intern(sb.toString());
	}

	private void readObject(java.io.ObjectInputStream in)
		throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.name = StringUtils.intern( this.name ); 
		this.type = StringUtils.intern( this.type );
	}
}