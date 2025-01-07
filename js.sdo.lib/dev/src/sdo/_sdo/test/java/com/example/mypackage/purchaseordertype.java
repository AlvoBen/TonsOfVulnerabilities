package com.example.myPackage;

import com.sap.sdo.api.SchemaInfo;
import com.sap.sdo.api.SdoTypeMetaData;

@SchemaInfo(schemaLocation="../../com/sap/sdo/testcase/schemas/sdoAnnotationsExample.xsd")
@SdoTypeMetaData(uri="http://www.example.com/IPO")
public interface PurchaseOrderType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true
    )
    Address getShipTo();
    void setShipTo(Address pShipTo);
    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true
    )
    Address getBillTo();
    void setBillTo(Address pBillTo);
    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true
    )
    String getComment();
    void setComment(String pComment);
    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true
    )
    Items getItems();
    void setItems(Items pItems);
    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoType = "http://www.example.com/IPO#MyGregorianDate"
    )
    com.sap.xml.datatype.GregorianCalendar getOrderDate();
    void setOrderDate(com.sap.xml.datatype.GregorianCalendar pOrderDate);

}
