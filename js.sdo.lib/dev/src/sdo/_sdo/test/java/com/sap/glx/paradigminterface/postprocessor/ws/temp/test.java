package com.sap.glx.paradigmInterface.postprocessor.ws.temp;

import java.io.IOException;
import java.util.List;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import commonj.sdo.Type;

public class Test {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		SapHelperContext hc = (SapHelperContext) SapHelperProvider.getNewContext();
		SapTypeHelper sth = (SapTypeHelper) hc.getTypeHelper();
		InterfaceGenerator i = sth.createInterfaceGenerator("D:\\temp");
		i.setGenerateAnnotations(true);
		i.addSchemaLocation("http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702", "ws-securitypolicy-1.2.xsd");
		i.addPackage("http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702", "com.sap.glx.paradigmInterface.postprocessor.ws.wssx");
		i.addSchemaLocation("http://www.w3.org/2005/08/addressing", "ws-addr.xsd");
		i.addPackage("http://www.w3.org/2005/08/addressing", "com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr");
		SapXsdHelper sxh = (SapXsdHelper) hc.getXSDHelper();
		List<Type> types = sxh.define(WSSecXsd.xsd_wsa);
		types.addAll(sxh.define(WSSecXsd.xsd));
		try {
			i.generate(types);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
