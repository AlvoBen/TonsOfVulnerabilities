/*
 * Created on Nov 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.library_container.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.xml.StandardDOMParser;

/**
 * @author I024067
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class EntityResolverImpl implements EntityResolver {

	private static final String DTD_BASE = "." + File.separatorChar + "dtd";

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {

		if (systemId.startsWith(StandardDOMParser.SAP_DTD_PREFIX)) {
			systemId = systemId.substring(StandardDOMParser.SAP_DTD_PREFIX
					.length());
		}

		File f = new File(DTD_BASE);
		String canonicName = null;
		try {
			canonicName = f.getCanonicalPath();
		} catch (IOException exc) {
			exc.printStackTrace();
		}

		FileInputStream in = new FileInputStream(canonicName + File.separator
				+ systemId);
		InputSource src = new InputSource(in);
		src.setSystemId(systemId);
		return src;
	}

}
