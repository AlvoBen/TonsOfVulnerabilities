package javax.sdo.tck.staticTest;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.example.com/staticTest",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "ct1"
        )}
)
public interface ComplexType1  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getElem1();
    void setElem1(String pElem1);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        defaultValue = "false",
        propertyIndex = 1
    )
    boolean isBool();
    void setBool(boolean pBool);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getAttr1();
    void setAttr1(String pAttr1);

}
