package com.tssap.dtr.client.lib.protocol.requests.dav;

/**
 * This enumerator is used in various DAV and DeltaV requests
 * to define a "Depth" header.
 */
public final class Depth {
	private final String name;
	private Depth(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}

	public static final Depth DEPTH_0 = new Depth("Depth:0");
	public static final Depth DEPTH_1 = new Depth("Depth:1");
	public static final Depth DEPTH_INFINITY = new Depth("Depth:infinity");
}
