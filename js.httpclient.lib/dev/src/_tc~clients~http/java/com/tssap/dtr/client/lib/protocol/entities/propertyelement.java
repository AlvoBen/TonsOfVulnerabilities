package com.tssap.dtr.client.lib.protocol.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * This class represents a DAV property in a DAV Multistatus response.
 * <p>DAV properties may either consists of simple string values or arbitrary XML
 * fragment. A typical example for a structured property is
 * the standard property &lt;DAV:supportedlock&gt;
 * (compare RFC 2518) that describes the types of locks a resource supports:
 * <code>
 * &lt;D:supportedlock&gt;<br>
 * &lt;D:lockentry&gt;<br>
 * &lt;D:lockscope&gt;&lt;D:exclusive/&gt;&lt;/D:lockscope&gt;<br>
 * &lt;D:locktype&gt;&lt;D:write/&gt;&lt;/D:locktype&gt;<br>
 * &lt;/D:lockentry&gt;<br>
 * &lt;D:lockentry&gt;<br>
 * &lt;D:lockscope&gt;&lt;D:shared/&gt;&lt;/D:lockscope&gt;<br>
 * &lt;D:locktype&gt;&lt;D:write/&gt;&lt;/D:locktype&gt;<br>
 * &lt;/D:lockentry&gt;<br>
 * &lt;/D:supportedlock&gt;<br>
 * </code>
 * <p>The inner tags like &lt;D:lockentry&gt; and &lt;D:locktype&gt; are represented
 * as instances of the Element class.</p>
 * <p>Each PropertyElement instance provides a status code and description and eventually
 * a DeltaV pre- or post-condition code in case that an error occured.</p>
 * <p>This class is also used in the response to DeltaV Expand Property Reports.
 * In this case the inner (expanded) properties can be accessed with the help
 * of the methods <code>getExpandedResource</code> and <code>getExpandedResources</code>.
 *
 * Copyright (c) SAP AG 2002
 * @author michael.ochmann@sap.com
 * @version $Id: Element.java,v 0.9 2002/05/16 10:44:00 jre Exp $
 */
public class PropertyElement extends Element {

	/** The status code from the &lt;DAV:propstat&gt; tag of a multistatus response */
	private int statusCode = -1;

	/** The status description from the &lt;DAV:propstat&gt; tag of a multistatus response */
	private String statusDescription;

	/** The response description or a DeltaV pre-/post-condition */
	private String errorCondition;

	/** XCM: An additional pre-or post-condition */
	private String extendedCondition;

	/** For expand property reports. A list of ResourceElement objects */
	private List expandedResources;
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(PropertyElement.class);	

	/**
	 * Creates a new element with given (qualified) name and namespace uri.
	 * @param qualifiedName the name of the element including a namespace prefix (optional).
	 * @param namespacesURI the namespace to which the property name belongs, given
	 * as an URI, e.g. 'DAV:'.
	 */
	public PropertyElement(String qualifiedName, String namespaceURI) {
		super(qualifiedName, namespaceURI);
	}

	/**
	 * Returns the status code of this property.
	 * @return A HTTP status code, e.g. '200'
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Returns the status description of this element.
	 * @return A HTTP status description, e.g. 'OK'
	 */
	public String getStatusDescription() {
		return statusDescription;
	}

	/**
	 * Retrieves the response description or a DeltaV pre-/post-condition.
	 * @return A DeltaV pre- or post-condition, e.g. 'DAV:cannot-modify-version'.
	 */
	public String getErrorCondition() {
		return errorCondition;
	}

	/**
	 * Retrieves an additional, non-standard pre-or post-condition.
	 * @return A non-standard pre- or post-condition
	 * used in XCM workspaces, e.g. 'xcm:activity-closed'.
	 */
	public String getExtendedCondition() {
		return extendedCondition;
	}

	/**
	 * Returns an exanded response element specified by index that was retrieved
	 * from an expand property report.
	 * The elements are provided in the order they occured in the expand property report.
	 * @param index the index number of the resource element to retrieve.
	 * @return The resource element that corresponds to the i-th &lt;DAV:response&gt;
	 * entry in the expand property report, or null if no resource was
	 * reveived.
	 */
	public ResourceElement getExpandedResource(int index) {
		return (expandedResources != null  &&  index < expandedResources.size())? 
			(ResourceElement) expandedResources.get(index) : null;
	}

	/**
	 * Returns an enumeration of exanded response elements that were retrieved
	 * from an expand property report. Each element contains the URL of
	 * the corresponding resource, the set of retrieved properties and optionally
	 * a human readable description of the response state.
	 * @return An enumeration of ResourceElement instances.
	 */
	public Iterator getExpandedResources() {
		return (expandedResources != null)? expandedResources.iterator() : null;
	}

	/**
	* Returns number of expanded response elements that were retrieved
	* from an expand property report.
	* @return number of resources
	 */
	public int countExpandedResources() {
		return (expandedResources != null) ? expandedResources.size() : 0;
	}

	/**
	 * Sets the status of this element. The parameter status should be a
	 * HTTP status line. It is parsed into its parts (status and status
	 * description). The leading version identifier (if present) is ignored.
	 * @param status a HTTP status line including status code and description, e.g.
	 * '200 OK'.
	 */
	public void setStatus(String status) {
		Tokenizer tokenizer = new Tokenizer(status);
		String token = tokenizer.nextToken();
		if (token.startsWith("HTTP")) {
			token = tokenizer.nextToken();
		}
		try {
			statusCode = Integer.parseInt(token);
			statusDescription = tokenizer.lastToken();
		} catch (NumberFormatException e) {
			TRACE.catching("setStatus(String)", e);
		}
	}

	/**
	 * Sets the status code of this element.
	 * @param status a HTTP status code, e.g. '200'
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Sets the status description of this element.
	 * @param statusDescription a HTTP status description, e.g. 'OK'.
	 */
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	/**
	 * Sets the response description or a DeltaV pre-/post-condition.
	 * @param description a DeltaV pre- or post-condition,
	 * e.g. 'DAV:cannot-modify-version'.
	 */
	public void setErrorCondition(String condition) {
		this.errorCondition = condition;
	}

	/**
	 * Sets an additional, non-standard pre-or post-condition.
	 * @param description a non-standard pre- or post-condition
	 * used in XCM workspaces, e.g. 'xcm:activity-closed'.
	 */
	public void setExtendedCondition(String condition) {
		this.extendedCondition = condition;
	}

	/**
	 * Adds an expanded resource to this property. Used for expand property
	 * reports only. Called by MultiStatusEntity.
	 * @param resource the resource element to add.
	 */
	void addExpandedResource(ResourceElement resource) {
		if (expandedResources == null) {
			expandedResources = new ArrayList();
		}
		expandedResources.add(resource);
	}

}
