package com.tssap.dtr.client.lib.protocol;

/**
 * Interface defining commonly used constants for the DAV namespace.
 */
public interface DAV {
	
	/** The URI for the DAV protocol namespace ("DAV:"). */
	static final String NAMESPACE = "DAV:";
	/** The XML namespace prefix used in DAV protocol requests ("D:"). */
	static final String PREFIX = "D:";
	/** Predefined default xmlns attribute used in DAV protocol requests 
	 * (xmlns="DAV:").*/
	static final String DEFAULT_XMLNS = " xmlns=\"" + NAMESPACE + "\"";
	/** Predefined prefixed xmlns attribute used in DAV protocol requests
	 * (xmlns:D="DAV:").*/	
	static final String PREFIXED_XMLNS = " xmlns:D=\"" + NAMESPACE + "\"";
}
