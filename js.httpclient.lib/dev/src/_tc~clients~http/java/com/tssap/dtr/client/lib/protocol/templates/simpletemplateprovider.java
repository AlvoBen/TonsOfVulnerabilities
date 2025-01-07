package com.tssap.dtr.client.lib.protocol.templates;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.*;
import com.tssap.dtr.client.lib.protocol.URL;

/**
 * Basic implementation of a template provider based on a simple
 * array list.
 */
public class SimpleTemplateProvider implements ITemplateProvider {

	private List templates = new ArrayList();

	/** client trace */
	private static Location TRACE = Location.getLocation(SimpleTemplateProvider.class);
	
	/**
	 * Creates a new empty temnplate provider.
	 */
	public SimpleTemplateProvider() {
	}

	/**
	 * Adds the connection template specified by <code>template</code> to the
	 * list of configured connection templates. Note, the Connection class
	 * for examples implements the interface IConnectionTemplate. Thus existing
	 * connections can be used as templates for others.
	 * @param template the connection template to add.
	 * @return the ID of the given connection template for later access.
	 */
	public int addConnectionTemplate(IConnectionTemplate template) {
		int templateID = -1;
		synchronized (templates) {
			templates.add(template);
			templateID = templates.size() - 1;
			if (TRACE.beInfo()) {
				TRACE.infoT("addConnectionTemplate(IConnectionTemplate)", 
				"template<" + templateID + "> " + template.toString());
			}
		}
		return templateID;
	}

	/**
	 * Returns a connection template for the specified <code>templateId</code>.
	 * @param templateId the ID for the connection template.
	 * @return the connection template for the given templateId.
	 * defined.
	 */
	public IConnectionTemplate getConnectionTemplate(int templateId) throws InvalidTemplateIDException {
		if (templateId < 0  ||  templateId >= templates.size()) {
			throw new InvalidTemplateIDException("Invalid template id");
		}
		return (IConnectionTemplate)templates.get(templateId);
	}
	
	/**
	 * Retrieves a list of id's of connection templates that matches
	 * the given url. The method takes protocol, host, port and
	 * probably a base path part of <code>url</code> into account.
	 * @param url the URL for which a matching template is searched.
	 * @return An array of matching template ids. If the size of that array
	 * is zero then no matching template could be found.
	 * @throws MalformedURLException if the given URL is not valid.
	 */	
	public int[] searchTemplate(String url) throws MalformedURLException {
		URL theURL = new URL(url);
		int count = 0;
		int numberTemplares = templates.size();
		BitSet resultSet = new BitSet(numberTemplares);
		for (int i = 0; i < numberTemplares; ++i) {
			IConnectionTemplate template = (IConnectionTemplate) templates.get(i);
			if (theURL.getHost().equals(template.getHost())
				&& theURL.getPort() == template.getPort()
				&& theURL.getProtocol().equals(template.getProtocol().toString())
				&& theURL.getPath().startsWith(template.getBasePath())) {
				resultSet.set(i);
				count++;
			}
		}
		int[] result = new int[count];
		if (count == 1) {
			result[0] = resultSet.length() - 1;
		} else if (count > 1) {
			for (int i = 0, j = 0; i < numberTemplares; ++i) {
				if (resultSet.get(i) == true) {
					result[j++] = i;
				}
			}
		}
		return result;
	}

	/**
	 * Returns an immutable list of connection templates handled by this
	 * provider.
	 * @return a list of IConnectionTemplate instances
	 */
	public List listConnectionTemplates() {
		return Collections.unmodifiableList(templates);
	}

}
