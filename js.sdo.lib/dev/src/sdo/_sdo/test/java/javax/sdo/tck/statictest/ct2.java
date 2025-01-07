package javax.sdo.tck.staticTest;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.example.com/staticTest"
)
public interface CT2  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    javax.sdo.tck.staticTest.ComplexType1 getCt1();
    void setCt1(javax.sdo.tck.staticTest.ComplexType1 pCt1);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<javax.sdo.tck.staticTest.ComplexType1> getCt1_list();
    void setCt1_list(java.util.List<javax.sdo.tck.staticTest.ComplexType1> pCt1_list);

}
