package com.sap.engine.session;

import com.sap.engine.session.data.LifecycleManagedData;

public class DummyLifecycleManagedData implements LifecycleManagedData{
	
	long expirationTime;
	long creationTime;
	boolean expireCalled;
	
	DummyLifecycleManagedData(long time){
		expirationTime = time * 1000;
		creationTime = System.currentTimeMillis();
		System.out.println("LifecycleManagedData created at: " + creationTime + " and with exp time: " + time);
	}
	
	public void expire(Object arg0) {
		expireCalled = true;
		System.out.println("expire() called");
	}
	
	public boolean isExpired() {
		boolean result = creationTime + expirationTime <= System.currentTimeMillis();
		System.out.println("result = " + result);
		return result;
	}
	
	public boolean isExpireCalled(){
	  System.out.println("isExpire() called");
		return expireCalled;
	}
	
}
