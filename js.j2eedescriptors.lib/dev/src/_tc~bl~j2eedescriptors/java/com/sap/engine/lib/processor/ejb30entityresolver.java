package com.sap.engine.lib.processor;


import com.sap.engine.lib.converter.CombinedEntityResolver;

import java.io.IOException;
import java.util.HashMap;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EJB30EntityResolver extends CombinedEntityResolver {
	
	private static final HashMap schemas = new HashMap();
	
	static {
		schemas.put("ejb-j2ee-engine_3_0.xsd",
			"com/sap/engine/lib/schema/ejb-j2ee-engine_3_0.xsd");
		schemas.put("ejb-jar_3_0.xsd",
			"com/sap/engine/lib/schema/ejb-jar_3_0.xsd");
		schemas.put("javaee_5.xsd",
			"com/sap/engine/lib/schema/javaee_5.xsd");
		schemas.put("javaee_web_services_1_2.xsd",
			"com/sap/engine/lib/schema/javaee_web_services_1_2.xsd");
		schemas.put("javaee_web_services_client_1_2.xsd",
			"com/sap/engine/lib/schema/javaee_web_services_client_1_2.xsd");
		schemas.put("xml.xsd",
			"com/sap/engine/lib/schema/xml.xsd");	
	}
	
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (this.schemas.containsKey(systemId)) {
			InputSource inputSource = new InputSource();
			inputSource.setByteStream(getClass().getClassLoader().getResourceAsStream((String) schemas.get(systemId)));
			inputSource.setSystemId(systemId);
			return(inputSource);
	    } else {
	    	return super.resolveEntity(publicId, systemId);
	    }
	}
}
