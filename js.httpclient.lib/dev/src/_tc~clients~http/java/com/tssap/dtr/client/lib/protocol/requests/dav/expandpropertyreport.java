package com.tssap.dtr.client.lib.protocol.requests.dav;

import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.*;

/**
 * This request class implements the DeltaV "Expand Property" report.
 */
public class ExpandPropertyReport extends MultiStatusRequest {

	/** The top level property definition */
	private ExpandPropertyDef top;

	/**
	 * Creates a expand property report for the specified resource
	 */
	public ExpandPropertyReport(String path) {
		super("REPORT", path);
		top = new ExpandPropertyDef("");
	}

	/**
	 * Adds another property tag with specified name attribute to the
	 * given parent tag. If parent is null the tag is append to the
	 * root tag.
	 * @param propertyName  the name of the property to retrieve.
	 * @param parent  the parent tag to which this tag belongs.
	 * @return A PropertyDef object that represents the new tag. This reference
	 * can be used to append children to that tag.
	 */
	public ExpandPropertyDef addPropertyExpand(String propertyName, ExpandPropertyDef parent) {
		ExpandPropertyDef property = new ExpandPropertyDef(propertyName);
		addPropertyExpand(property, parent);
		return property;
	}

	/**
	 * Adds another property tag with specified name attribute to the
	 * given parent tag. If parent is null the tag is append to the
	 * root tag.
	 * @param propertyName  the name of the property to retrieve.
	 * @param parent  the parent tag to which this tag belongs.
	 * @return A PropertyDef object that represents the new tag. This reference
	 * can be used to append children to that tag.
	 */
	public ExpandPropertyDef addPropertyExpand(String propertyName, String namespaceURI, ExpandPropertyDef parent) {
		ExpandPropertyDef property = new ExpandPropertyDef(propertyName, namespaceURI);
		addPropertyExpand(property, parent);
		return property;
	}

	/**
	 * Adds the specified property definition to the
	 * given parent tag. If parent is null the tag is append to the
	 * root tag.
	 * @param property  the description of the property to retrieve.
	 * @param parent  the parent tag to which this tag belongs.
	 */
	public void addPropertyExpand(ExpandPropertyDef property, ExpandPropertyDef parent) {
		if (parent != null) {
			parent.addChild(property);
		} else {
			top.addChild(property);
		}
	}
	
	
	/**
	 * Returns the number of resource elements stored in this entity.
	 * @return The number of resources.
	 */
	public int countResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null)? entity.countResources() : 0;
	}

	/**
	 * Returns the resource specified by index. The resources are provided
	 * in the order they occured in the multistatus response.
	 * @return The resource element that corresponds to the i-th <DAV:response>
	 * entry in the multistatus response, or null if no resource was
	 * reveived or the index is out of bounds.
	 */
	public ResourceElement getResource(int i) {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null)? entity.getResource(i) : null;
	}

	/**
	 * Returns an enumeration of ResourceElement objects that were retrieved
	 * from the multistatus response. Each element contains the URL of
	 * the corresponding resource, the set of retrieved properties and optionally
	 * a human readable description of the response state.
	 */
	public Iterator getResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null)? entity.getResources() : null;
	}

	/**
	 * Prepares the request entity.
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @return A request entity for this report.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<expand-property").append(DAV.DEFAULT_XMLNS);
		if (top.firstChild != null) {
			body.append(">");
			for (ExpandPropertyDef child = top.firstChild; child != null; child = child.next) {
				appendChildren(body, child);
			}
			body.append("</expand-property>");
		} else {
			body.append("/>");
		}
		setRequestEntity(body);
		return body;
	}

	/**
	 * Write the tag structure if the given property definition to the
	 * string entity.
	 */
	private void appendChildren(StringEntity body, ExpandPropertyDef property) {
		body.append("<property name=\"").append(property.name).append("\"");
		if (property.namespaceURI != null) {
			body.append(" namespace=\"").append(property.namespaceURI).append("\"");
		}

		ExpandPropertyDef child = property.firstChild;
		if (child == null) {
			body.append("/>");
		} else {
			body.append(">");
			while (child != null) {
				appendChildren(body, child);
				child = child.next;
			}
			body.append("</property>");
		}
	}
}