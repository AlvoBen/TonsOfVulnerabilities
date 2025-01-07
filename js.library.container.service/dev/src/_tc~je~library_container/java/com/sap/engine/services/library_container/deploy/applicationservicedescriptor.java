/*
 * Created on Nov 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.library_container.deploy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sap.engine.lib.xml.StandardDOMParser;

/**
 * Helper class for parsing the META-INF\application-service.xml descriptor and
 * obtaining the actions scheduled in application libraries as application hooks
 * (pieces of code run before the start or after the stop of the application
 * depending on the action type)
 * 
 * @author I024067
 */
public class ApplicationServiceDescriptor {

	private static final String EL_ACTION = "action";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_CLASS_NAME = "class-name";
	private static final String ATTR_METHOD_NAME = "method-name";
	private static final String ATTR_FATAL = "fatal";

	private static final String EL_SUPPRESS_ERRORS = "suppress_errors";
	private static final String ATTR_MANIFEST_CLASSPATH_ERROR = "manifest_classpath_error";

	private static final String EL_HEAVY_CLASSLOADING_BEFORE_JAVA_EE5 = "enableHeavyClassloadingBeforeJavaEE5";

	private ArrayList<Action> actions = new ArrayList<Action>();
	private boolean ignoreManifestClasspathError = false;
	private boolean enableHeavyClassloadingBeforeJavaEE5 = false;

	private void addAction(String type, String className, String methodName,
			boolean isFatal) {
		Action n = new Action();
		n.type = type;
		n.className = className;
		n.methodName = methodName;
		n.isFatal = isFatal;
		actions.add(n);
	}

	public Action[] getActions() {
		Action[] result = new Action[actions.size()];
		actions.toArray(result);
		return result;
	}

	public Action[] getActionsByType(String type) {
		Action n = null;
		ArrayList<Action> list = new ArrayList<Action>();
		for (int i = 0; i < actions.size(); i++) {
			n = actions.get(i);
			if (n.type.equals(type)) {
				list.add(n);
			}
		}
		Action[] result = new Action[list.size()];
		list.toArray(result);
		return result;
	}

	public static ApplicationServiceDescriptor readXML(InputStream is,
			StandardDOMParser parser) throws SAXException, IOException {
		Document document = null;
		NodeList list = null;
		Element element = null;
		document = parser.parse(is);
		element = document.getDocumentElement();
		ApplicationServiceDescriptor descriptor = new ApplicationServiceDescriptor();
		descriptor.ignoreManifestClasspathError = readIgnoreManifestClasspathErrorElement(element);
		descriptor.enableHeavyClassloadingBeforeJavaEE5 = readHeavyClassloadingBeforeJavaEE5Element(element);

		list = element.getElementsByTagName(EL_ACTION);
		for (int i = 0; i < list.getLength(); i++) {
			readAction((Element) list.item(i), descriptor);
		}
		return descriptor;
	}

	private static final void readAction(Element action,
			ApplicationServiceDescriptor descriptor) {
		String type = action.getAttribute(ATTR_TYPE);
		String className = action.getAttribute(ATTR_CLASS_NAME);
		String methodName = null;
		boolean isFatal = true;
		if (action.hasAttribute(ATTR_METHOD_NAME)) {
			methodName = action.getAttribute(ATTR_METHOD_NAME);
		}
		if (action.hasAttribute(ATTR_FATAL)) {
			String fatal = action.getAttribute(ATTR_FATAL);
			isFatal = !(fatal.equalsIgnoreCase("false"));
		}
		descriptor.addAction(type, className, methodName, isFatal);
	}

	private static boolean readIgnoreManifestClasspathErrorElement(
			Element document) {
		NodeList list = document.getElementsByTagName(EL_SUPPRESS_ERRORS);
		if (list != null) {
			int length = list.getLength();
			if (length == 1) {
				Element el = (Element) list.item(0);
				if (el.hasAttribute(ATTR_MANIFEST_CLASSPATH_ERROR)) {
					return Boolean.parseBoolean(el
							.getAttribute(ATTR_MANIFEST_CLASSPATH_ERROR));
				}
			}
		}
		return false;
	}

	private static boolean readHeavyClassloadingBeforeJavaEE5Element(
			Element document) {
		NodeList list = document
				.getElementsByTagName(EL_HEAVY_CLASSLOADING_BEFORE_JAVA_EE5);
		if (list != null && list.getLength() > 0) {
			return true;
		}
		return false;
	}

	public boolean canIgnoreManifestClasspathError() {
		return ignoreManifestClasspathError;
	}

	public boolean isHeavyClassloadingBeforeJavaEE5Enabled() {
		return enableHeavyClassloadingBeforeJavaEE5;
	}

	private static final String getTextValue(Element element) {
		if (element == null) {
			return (new String());
		}

		Node node = element.getFirstChild();

		if ((node != null)
				&& (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE)) {
			// "node" is a TextNode
			String value = node.getNodeValue();
			return value.trim();
		} else {
			return (new String(""));
		}
	}

}
