package com.sap.sdo.testcase.internal.j1.sdo;


import com.sap.sdo.api.SdoPropertyMetaData;

public interface ITripReservation {
	@SdoPropertyMetaData(containment=true)
	IReservation getReservation();
	void setReservation(IReservation r);
	
	ICity getCity();
	void setCity(ICity s);
}
