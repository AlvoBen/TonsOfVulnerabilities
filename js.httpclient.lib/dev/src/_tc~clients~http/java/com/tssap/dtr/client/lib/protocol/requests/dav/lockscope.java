package com.tssap.dtr.client.lib.protocol.requests.dav;


/**
 * This enumerator is used in the DAV "LOCK" request
 * to define the scope of a lock (either "exclusive" or "shared").
 */
public final class LockScope {
	private final String name;
	private LockScope(String name) {
		this.name = name;
	}
	public String toString() {
		return name;
	}

	public static final LockScope EXCLUSIVE = new LockScope("exclusive");
	public static final LockScope SHARED = new LockScope("shared");
}
