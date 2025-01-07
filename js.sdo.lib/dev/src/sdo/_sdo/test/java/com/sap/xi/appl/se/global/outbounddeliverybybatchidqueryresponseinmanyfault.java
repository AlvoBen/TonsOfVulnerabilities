package com.sap.xi.appl.se.global;

/**
 * Exception class for service fault.
 */
@javax.xml.ws.WebFault(name = "StandardMessageFault", targetNamespace = "http://sap.com/xi/SAPGlobal20/Global", faultBean = "com.sap.xi.sapglobal20.global.StandardMessageFault")
public class OutboundDeliveryByBatchIDQueryResponseInManyFault extends java.lang.Exception {

  private com.sap.xi.sapglobal20.global.StandardMessageFault _OutboundDeliveryByBatchIDQueryResponseInManyFault;

  public OutboundDeliveryByBatchIDQueryResponseInManyFault(String message, com.sap.xi.sapglobal20.global.StandardMessageFault faultInfo){
    super(message);
    this._OutboundDeliveryByBatchIDQueryResponseInManyFault = faultInfo;
  }

  public OutboundDeliveryByBatchIDQueryResponseInManyFault(String message, com.sap.xi.sapglobal20.global.StandardMessageFault faultInfo, Throwable cause){
    super(message, cause);
    this._OutboundDeliveryByBatchIDQueryResponseInManyFault = faultInfo;
  }

  public com.sap.xi.sapglobal20.global.StandardMessageFault getFaultInfo(){
    return this._OutboundDeliveryByBatchIDQueryResponseInManyFault;
  }

}
