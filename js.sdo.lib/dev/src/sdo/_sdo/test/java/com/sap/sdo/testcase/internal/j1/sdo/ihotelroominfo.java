package com.sap.sdo.testcase.internal.j1.sdo;


public interface IHotelRoomInfo {
	
	IHotel getHotel();
    String getRoom();
    String getPrice();
    long getRoomNumber();
    long getReservations();
    void setReservations(long reservations);
}
