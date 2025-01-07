package com.example.myPackage;

import com.sap.sdo.api.SdoTypeMetaData;

@SdoTypeMetaData(uri="http://www.example.com/IPO")
public interface Item  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true
    )
    String getProductName();
    void setProductName(String pProductName);
    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoType = "http://www.example.com/IPO#quantity"
    )
    java.math.BigInteger getQuantity();
    void setQuantity(java.math.BigInteger pQuantity);
    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "USPrice"
    )
    java.math.BigDecimal getUsPrice();
    void setUsPrice(java.math.BigDecimal pUsPrice);
    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true
    )
    String getComment();
    void setComment(String pComment);
    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "com.sap.sdo.impl.types.simple.YearMonthDaySimpleType@157fb52 ({commonj.sdo}YearMonthDay)"
        ),
        containment = true
    )
    String getShipDate();
    void setShipDate(String pShipDate);
    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        containment = true,
        sdoType = "http://www.example.com/IPO#SKU"
    )
    com.example.SKU getPartNum();
    void setPartNum(com.example.SKU pPartNum);

}
