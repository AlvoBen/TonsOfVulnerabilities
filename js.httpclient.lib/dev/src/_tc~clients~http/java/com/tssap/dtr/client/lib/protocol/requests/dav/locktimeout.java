package com.tssap.dtr.client.lib.protocol.requests.dav;

/**
 * This enumerator is used in the DAV "LOCK" request
 * to define the timeout behavior of a lock.
 */
public final class LockTimeout {
	private final String name;
	private LockTimeout(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}

	public static final LockTimeout SECONDS = new LockTimeout("Second-");
	public static final LockTimeout INFINITE = new LockTimeout("Infinite");
}
