package com.sap.glx.paradigmInterface.postprocessor.ws.misc;

import com.sap.sdo.api.OpenContentProperty;
import com.sap.sdo.api.SdoTypeMetaData;

@SdoTypeMetaData(open=true,
		  uri="http://www.sap.com/webas/630/soap/features/security/policy",
		  openContentProperties={@OpenContentProperty(many=true, name="HTTPSSO2")},
		  elementFormDefault=true)
public interface HTTPSSO2 {

}
