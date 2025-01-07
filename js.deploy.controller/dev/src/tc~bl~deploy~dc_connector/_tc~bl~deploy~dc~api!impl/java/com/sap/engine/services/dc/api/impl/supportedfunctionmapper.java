package com.sap.engine.services.dc.api.impl;

import java.util.HashMap;
import java.util.Map;

class SupportedFunctionMapper {
	private static Map deployResultStatus = new HashMap();
	
	static{
		deployResultStatus.put(
				com.sap.engine.services.dc.api.SupportedFunction.BATCH_VALIDATION, 
				com.sap.engine.services.dc.cm.SupportedFunction.BATCH_VALIDATION);
		
		deployResultStatus.put(
				com.sap.engine.services.dc.api.SupportedFunction.UNDEPLOYMENT_OF_EMPTY_SCA, 
				com.sap.engine.services.dc.cm.SupportedFunction.UNDEPLOYMENT_OF_EMPTY_SCA);
		
		deployResultStatus.put(
				com.sap.engine.services.dc.api.SupportedFunction.MEASUREMENT, 
				com.sap.engine.services.dc.cm.SupportedFunction.MEASUREMENT);
		
	}
	
	static com.sap.engine.services.dc.cm.SupportedFunction mapSupportedFunction(
			com.sap.engine.services.dc.api.SupportedFunction supportedFunction){
		return (com.sap.engine.services.dc.cm.SupportedFunction)deployResultStatus.get(supportedFunction);
	}

}