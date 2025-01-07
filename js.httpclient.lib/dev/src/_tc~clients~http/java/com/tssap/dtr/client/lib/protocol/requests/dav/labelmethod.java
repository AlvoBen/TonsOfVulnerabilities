package com.tssap.dtr.client.lib.protocol.requests.dav;


/**
 * This enumerator is used in DeltaV "LABEL" requests to define the
 * task of labeling (either adding, setting or removing a label).
 */
public final class LabelMethod {
	private final String name;
	private LabelMethod(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}

	public static final LabelMethod ADD_LABEL = new LabelMethod("add");
	public static final LabelMethod SET_LABEL = new LabelMethod("set");
	public static final LabelMethod REMOVE_LABEL = new LabelMethod("remove");
}
