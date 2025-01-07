package com.sap.sdo.testcase.internal.j1.sdo;


public interface ICustomer {
    long        getId();
    void 		setId(long pId);
    String      getTitle();
    void		setTitle(String pTitle);
    String      getFirstname();
    void		setFirstname(String pFirstName);
    String      getName();
    void		setName(String pName);
    ICity 		getCity();
    void		setCity(ICity city);
    String      getAddress();
    void		setAddress(String address);
}
