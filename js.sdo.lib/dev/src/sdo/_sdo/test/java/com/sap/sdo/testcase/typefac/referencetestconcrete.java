package com.sap.sdo.testcase.typefac;


@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = false,
            name = "ReferenceTestConcrete"
        )}
)
public interface ReferenceTestConcrete extends ReferenceTestBase {
    
    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3,
        containment = true)
    ReferenceTestConcrete getConcreteProp();
    void setConcreteProp(ReferenceTestConcrete dataObject);
    
    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4)
    String getValue2();
    void setValue2(String value);
}
