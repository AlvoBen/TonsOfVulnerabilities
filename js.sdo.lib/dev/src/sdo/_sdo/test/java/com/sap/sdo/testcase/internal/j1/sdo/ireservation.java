package com.sap.sdo.testcase.internal.j1.sdo;


import java.util.Date;

public interface IReservation {
    long getId();
    void setId(long id);
    
    long getCno();
    void setCno(long id);
    
    String getReservationType();
    void setReservationType(String type);
    
    Date getArrival();
    void setArrival(Date arrival);

    Date getDeparture();
    void setDeparture(Date departure);

    IHotel getHotel();
    void   setHotel(IHotel hotel);
}
