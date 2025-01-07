package com.sap.engine.lib.deploy.sda;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class XmlFile {

	private static final Location location = Location
			.getLocation(XmlFile.class);

	protected void save(String file, Element rootEl) throws IOException,
			TransformerException {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(file);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(rootEl), new StreamResult(out));
		} catch (IOException ioe) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR, "Could not save "
					+ file + " file", ioe);
			throw ioe;
		} catch (TransformerException te) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR, "Could not save "
					+ file + " file", te);
			throw te;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {// $JL-EXC$
				// do nothing
			}
		}
	}

}
