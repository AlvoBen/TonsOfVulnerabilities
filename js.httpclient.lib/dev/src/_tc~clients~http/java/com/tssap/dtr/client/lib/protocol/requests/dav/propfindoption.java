package com.tssap.dtr.client.lib.protocol.requests.dav;

/**
 * This enumerator defines the mode of operation of the DAV "PROPFIND" request.
 * The possible values allow to create PROPFIND requests for certain properties 
 * (value PROPERTIES), for the names of the properties only (value PROPERTY_NAMES),
 * and for the values of all DAV properties (value ALL_PROPERTIES).
 */
public final class PropfindOption {
	private final String name;
	private PropfindOption(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}
		
	public static final PropfindOption PROPERTIES= new PropfindOption("prop");
	public static final PropfindOption ALL_PROPERTIES = new PropfindOption("allprop");
	public static final PropfindOption PROPERTY_NAMES = new PropfindOption("propname");
}
