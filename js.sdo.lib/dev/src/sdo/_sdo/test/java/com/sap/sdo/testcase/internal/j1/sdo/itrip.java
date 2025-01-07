package com.sap.sdo.testcase.internal.j1.sdo;


import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

import commonj.sdo.ChangeSummary;

public interface ITrip {
    long getId();
    void setId(long id);

    ICustomer getCustomer();
    void setCustomer(ICustomer customer);
    
    @SdoPropertyMetaData(containment=true)
    List<ITripReservation> getReservations();
    void setReservations(List<ITripReservation> reservations);
    
    ChangeSummary getChangeSummary();
}
