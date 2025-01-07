package com.sap.engine.session;

import com.sap.engine.session.data.SessionChunk;

public class DummySessionChunk implements SessionChunk{
	String name;
	Object data;
	
	public DummySessionChunk() {
		name = "blabla";
		data = new Object();
	}
	
	public DummySessionChunk(String name){
		this.name = name;
		data = new Object();
	}
	
	public DummySessionChunk(String name, Object o){
		this.name = name;
		data = o;
	}
}
