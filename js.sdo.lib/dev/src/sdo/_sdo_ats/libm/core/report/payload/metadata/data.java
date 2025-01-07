package report.payload.metadata;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "payload_metadata.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "DATA",
    uri = "http://www.sap.com/abapxml"
)
public interface Data  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "REQUEST_PATH",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getRequestPath();
    void setRequestPath(String pRequestPath);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "REQUEST_HOST",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getRequestHost();
    void setRequestHost(String pRequestHost);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "0",
        sdoName = "REQUEST_PORT",
        propertyIndex = 2
    )
    int getRequestPort();
    void setRequestPort(int pRequestPort);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "URL",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getUrl();
    void setUrl(String pUrl);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "LOGON_TIMESTAMP",
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getLogonTimestamp();
    void setLogonTimestamp(String pLogonTimestamp);

}
