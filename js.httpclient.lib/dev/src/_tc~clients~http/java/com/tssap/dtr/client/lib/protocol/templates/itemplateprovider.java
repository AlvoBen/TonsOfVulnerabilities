package com.tssap.dtr.client.lib.protocol.templates;

import java.net.MalformedURLException;
import java.util.List;

import com.tssap.dtr.client.lib.protocol.*;


/**
 * This interface represents a generic manager for connection templates.
 * It provides the basic functionality to add, retrieve, search and list
 * connection templates. Implementations of this instance may also provide
 * additional method, e.g. to replace or remove templates. 
 */
public interface ITemplateProvider {

	/**
	 * Adds the connection template specified by <code>template</code> to the
	 * list of configured connection templates. Note, the Connection class
	 * for examples implements the interface IConnectionTemplate. Thus existing
	 * connections can be used as templates for others.
	 * @param template the connection template to add.
	 * @return the ID of the given connection template for later access. 
	 */
	int addConnectionTemplate(IConnectionTemplate template);

	/**
	 * Returns a connection template for the specified <code>templateId</code>.
	 * @param templateId the ID for the connection template.
	 * @return the connection template for the given templateId.
	 * defined.
	 * @throws InvalidTemplateIDException  if the <code>templateId</code> is unknown
	 * or invalid.
	 */
	IConnectionTemplate getConnectionTemplate(int templateId) throws InvalidTemplateIDException;


	/**
	 * Retrieves a list of id's of connection templates that matches
	 * the given url. The method takes protocol, host, port and
	 * probably a base path part of <code>url</code> into account.
	 * @param url the URL for which a matching template is searched.
	 * @return An array of matching template ids. If the size of that array
	 * is zero then no matching template could be found.
	 * @throws MalformedURLException if the given URL is not valid.
	 */
	int[] searchTemplate(String url) throws MalformedURLException;


	/**
	 * Returns an immutable list of connection templates handled by this
	 * provider.
	 * @return a list of IConnectionTemplate instances
	 */
	List listConnectionTemplates();

}
