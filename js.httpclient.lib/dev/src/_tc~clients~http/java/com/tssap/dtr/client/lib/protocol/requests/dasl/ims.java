package com.tssap.dtr.client.lib.protocol.requests.dasl;

/**
 * Interface defining commonly used constants for the namespace
 * used by SAP DASL extensions.
 */
public interface IMS {
	static final String NAMESPACE = "http://tssap.dtr.sap.com/ims";
	static final String PREFIX = "ims:";
	static final String DEFAULT_XMLNS = " xmlns=\"" + NAMESPACE + "\"";
	static final String PREFIXED_XMLNS = " xmlns:ims=\"" + NAMESPACE + "\"";
}
