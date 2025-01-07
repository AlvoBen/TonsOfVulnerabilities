package com.tssap.dtr.client.lib.protocol.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * This class represents a single &lt;response&gt; tag in a DAV Multistatus response.
 * It provides the URL of the resource (or the URLs of the resources) to which a
 * DAV or DeltaV command like PROPFIND was applied, together with the requested
 * information like the values of certain properties of that resource, status
 * information, or collisions between that resource and another.
 */
public class ResourceElement extends Element {

	/** The status code from the <DAV:propstat> tag of a multistatus response */
	private int statusCode = -1;

	/** The status description from the <DAV:propstat> tag of a multistatus response */
	private String statusDescription;

	/** The response description or a DeltaV pre-/post-condition */
	private String errorCondition;

	/** An additional pre-or post-condition */
	private String extendedCondition;

	/** The resource that belongs to this response element */
	private String path;

	/** The hrefs that belong to this response element */
	private ArrayList paths;

	/** The number of associated hrefs */
	private int pathsSize = 0;

	/** A list of Collision instances */
	private ArrayList collisions;
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(ResourceElement.class);	

	/**
	 * Creates a new resource element with the specified path.
	 * @param path the path attribute of this resource.
	 */
	public ResourceElement(String path) {
		super("response", DAV.NAMESPACE);
		this.path = path;
		pathsSize = 1;
	}

	/**
	 * Returns the status code of this property.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Returns the status description of this element.
	 */
	public String getStatusDescription() {
		return statusDescription;
	}

	/**
	 * Retrieves the response description or a DeltaV pre-/post-condition.
	 */
	public String getErrorCondition() {
		return errorCondition;
	}

	/**
	 * Retrieves an additional pre-or post-condition
	 */
	public String getExtendedCondition() {
		return extendedCondition;
	}

	/**
	 * Retrieves the first href of the resource
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the number of paths of this response element.
	 */
	public int countPaths() {
		return pathsSize;
	}

	/**
	 * Retrieves the first href of the resource
	 * @return the path corrsponding to the given index,
	 * or null if the index is out of bounds.
	 */
	public String getPath(int index) {
		if (paths != null) { 
			return (String)paths.get(index);
		} else if (index == 0) {
			return path;		
		} else {
			return null;	
		}
	}

	/**
	 * Retrieves the href of the resource
	 * @return an iterator over path strings
	 */
	public Iterator getPaths() {
		if (paths!=null) {
			return paths.iterator();
		} else {
			return Collections.singletonList(path).iterator();
		}
	}

	/**
	 * Returns the number of properties of this response element.
	 */
	public int countProperties() {
		return countChildren();
	}

	/**
	 * Returns the property with the specified name. If this response
	 * elements has no properties or the property is not defined null is returned.
	 * Note, this method ignores the namespace of properties.
	 * @param propertyName the name of the property.
	 * @return The property corresponding to the given name.
	 */
	public PropertyElement getProperty(String propertyName) {
		return (PropertyElement) getChild(propertyName);
	}

	/**
	 * Returns the property with the specified name. If this response
	 * elements has no properties or the property is not defined null is returned.
	 * Note, this method checks if the property's namespace
	 * matches the specified namespace URI but namespace prefixes are
	 * ignored.
	 * @param propertyName the name of the property.
	 * @param namespaceURI the namespace URI of the property.
	 * @return The property corresponding to the given name.
	 */
	public PropertyElement getProperty(String propertyName, String namespaceURI) {
		return (PropertyElement) getChild(propertyName, namespaceURI);
	}

	/**
	 * Returns an enumeration of the properties of this response element.
	 */
	public Iterator getProperties() {
		return getChildren();
	}

	/**
	 * Returns the number of collisions of this response element.
	 */
	public int countCollisions() {
		return (collisions != null) ? collisions.size() : 0;
	}

	/**
	 * Returns the collisions with the specified index. If this response
	 * elements has no collisions the method returns null.
	 * Note, this method ignores the namespace of properties.
	 * @param index the index of the collision corresponding to their
	 * order in the multistatus response.
	 * @return The collision corresponding to the given index,
	 * or null if the no collision is available or the index is out of bounds.
	 */
	public Collision getCollision(int index) {
		return (collisions != null  &&  index < collisions.size()) ? (Collision) collisions.get(index) : null;
	}

	/**
	 * Returns an enumeration of the collisions of this response element.
	 * @return An enumeration of Collision instances, or null if no collisions
	 * exists.
	 */
	public Iterator getCollisions() {
		return (collisions != null) ? collisions.iterator() : null;
	}

	/** Sets the status of this element. The parameter status should be a
	 *  HTTP status line. It is parsed into its parts (status and status
	 *  description). The leading version identifier (if present) is ignored.
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
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Sets the status description of this element.
	 */
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	/**
	 * Sets the response description or a DeltaV pre-/post-condition.
	 */
	public void setErrorCondition(String condition) {
		this.errorCondition = condition;
	}

	/**
	 * Sets an additional pre-or post-condition.
	 */
	void setExtendedCondition(String description) {
		this.extendedCondition = description;
	}

	/**
	 * Add another path to the element
	 */
	void addPath(String path) {
		if (paths == null) {
			paths = new ArrayList();
			paths.add(this.path);
		}
		paths.add(path);
		pathsSize++;
	}

	void addCollision(Collision collision) {
		if (collisions == null) {
			collisions = new ArrayList();
		}
		collisions.add(collision);
	}

	/**
	 * Sets the statusCode, statusDescription and responseDescriprion attributes
	 * of those properties that do not yet have them. This method is used
	 * internally by MultiStatusEntity.endElement
	 */
	void touchChildrenStatus(String status, String responseDescription, String extendedDescription)
		throws NumberFormatException {
		if (firstChild() != null) {
			Tokenizer tokenizer = new Tokenizer(status);
			String token = tokenizer.nextToken();
			if (token.startsWith("HTTP")) {
				token = tokenizer.nextToken();
			}

			int statusCode;
			String statusDescription;
			statusCode = Integer.parseInt(token);
			statusDescription = tokenizer.lastToken();

			Iterator properties = getProperties();
			if (properties != null) {
				while (properties.hasNext()) {
					PropertyElement property = (PropertyElement) properties.next();
					if (property.getStatusCode() < 0) {
						property.setStatusCode(statusCode);
						property.setStatusDescription(statusDescription);
						property.setErrorCondition(responseDescription);
						property.setExtendedCondition(extendedDescription);
					}
				}
			}
		}
	}

}
