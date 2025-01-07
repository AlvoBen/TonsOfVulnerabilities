package com.sap.engine.applications.security.logon.beans;


public class ErrorBean {
	public static final String beanId = "error";
	
	String id;
	Object[] params;

	public ErrorBean(String id) {
		this(id, null);
	}

	public ErrorBean(String id, Object param) {
		this(id, new Object[] {param});
	}	
	
	public ErrorBean(String id, Object[] params) {
		this.id = id;
		this.params = params;
	}
	
  
}
