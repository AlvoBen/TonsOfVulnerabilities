package com.sap.glx.paradigmInterface.postprocessor.ws.misc;

import com.sap.sdo.api.OpenContentProperty;
import com.sap.sdo.api.SdoTypeMetaData;

@SdoTypeMetaData(open=true,
		  uri="http://schemas.xmlsoap.org/ws/2004/09/policy",
		  openContentProperties={@OpenContentProperty(many=true, name="UsingPolicy")},
		  elementFormDefault=true)
public interface UsingPolicy {
			  
}
