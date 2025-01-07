package com.tssap.dtr.client.lib.protocol.entities;

// OK: should be actually in XCM. But due to high complexity
// of separation of states in the state machine of SAX parser
// (see MultiStatusEntity) it stays here
public final  class CollisionType {

	private final String name;
	private CollisionType(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}

	public static final CollisionType ALL = new CollisionType("all");
	public static final CollisionType DEFAULT = new CollisionType("default");

	public static final CollisionType CYCLIC_MERGE = new CollisionType("CyclicMerge");	
	public static final CollisionType NAME_CLASH = new CollisionType("Name");
	public static final CollisionType PRED_SUCC = new CollisionType("PredSucc");	
}