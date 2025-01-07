package com.sap.engine.session;

public class DummyLifecycleManagedData2 extends DummyLifecycleManagedData{
	
	boolean isCalledInRequest;
	
	public DummyLifecycleManagedData2(long l) {
		super(l);
	}
	
	@Override
	public boolean isExpired() {
		long time = System.currentTimeMillis();
		System.out.println("isExpired() in TestLifececleManagedData2 is calles");
		System.out.println("Creation time:" + creationTime);
		System.out.println("Current time:" + System.currentTimeMillis());
		
		System.out.println("Time from creation:" + (System.currentTimeMillis() - creationTime));
		System.out.println("Time from creation:" + (System.currentTimeMillis() - creationTime)/1000);

		System.out.println("creationTime + expirationTime:" + (creationTime + expirationTime));
		System.out.println("creationTime + AppSessionTest.TIMEOUT:" + (creationTime + AppSessionTest.TIMEOUT));
		
		if(creationTime + expirationTime <= time && time < creationTime + AppSessionTest.TIMEOUT){
			isCalledInRequest = true;
		}
		System.out.println("isCalledInRequest is: " + isCalledInRequest);
		return super.isExpired();	
	}
	
	public boolean isCalledInRequest(){
		return isCalledInRequest;
	}
	
}
