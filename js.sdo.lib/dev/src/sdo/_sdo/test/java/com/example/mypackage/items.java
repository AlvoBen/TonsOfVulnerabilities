package com.example.myPackage;

public interface Items  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true
    )
    java.util.List<Item> getItem();
    void setItem(java.util.List<Item> pItem);

}
