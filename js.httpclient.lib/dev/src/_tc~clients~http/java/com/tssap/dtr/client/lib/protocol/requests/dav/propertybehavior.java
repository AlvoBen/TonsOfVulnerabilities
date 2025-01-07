package com.tssap.dtr.client.lib.protocol.requests.dav;

/**
 * This enumerator is used to define the behavior or properties
 * during DAV "MOVE" and "COPY requests.
 */
public final class PropertyBehavior {
	private final String name;
	private PropertyBehavior(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}

	public static final PropertyBehavior NONE = new PropertyBehavior("none");
	public static final PropertyBehavior OMIT = new PropertyBehavior("omit");
	public static final PropertyBehavior KEEP_ALIVE = new PropertyBehavior("keepalive");
}
