package com.tssap.dtr.client.lib.protocol.entities;

// OK: should be actually in XCM. But due to high complexity
// of separation of states in the state machine of SAX parser
// (see MultiStatusEntity) it stays here
public final  class CollisionState {

	private final String name;
	private CollisionState(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name;
	}

	public static final CollisionState ALL = new CollisionState("all");
	public static final CollisionState OPEN = new CollisionState("open");
	public static final CollisionState CLOSED = new CollisionState("closed");
	public static final CollisionState LOCKED = new CollisionState("locked");
}