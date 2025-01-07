package com.sap.persistence.monitors.ws.trace_v1;

/**
 * Exception class for service fault.
 */
@javax.xml.ws.WebFault(name = "FaultInfo", targetNamespace = "http://sap.com/persistence/monitors/ws/types/common", faultBean = "com.sap.persistence.monitors.ws.types.common.TFaultInfo")
public class FaultInfoResponse extends java.lang.Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = 5691288082838349165L;
	
private com.sap.persistence.monitors.ws.types.common.TFaultInfo _FaultInfoResponse;

  public FaultInfoResponse(String message, com.sap.persistence.monitors.ws.types.common.TFaultInfo faultInfo){
    super(message);
    this._FaultInfoResponse = faultInfo;
  }

  public FaultInfoResponse(String message, com.sap.persistence.monitors.ws.types.common.TFaultInfo faultInfo, Throwable cause){
    super(message, cause);
    this._FaultInfoResponse = faultInfo;
  }

  public com.sap.persistence.monitors.ws.types.common.TFaultInfo getFaultInfo(){
    return this._FaultInfoResponse;
  }

}
