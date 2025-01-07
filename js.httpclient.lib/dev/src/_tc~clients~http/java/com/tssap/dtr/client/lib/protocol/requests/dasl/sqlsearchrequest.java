package com.tssap.dtr.client.lib.protocol.requests.dasl;

import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.MultiStatusRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This request class implements a DASL extension that allows to
 * issue SQL queries directly to the DTR.
 */
public class SQLSearchRequest extends MultiStatusRequest {

	private String query;

	/**
	 * Creates a new SQL search request containing the given
	 * SQL statement.
	 */
	public SQLSearchRequest(String path, String query) {
		super("SEARCH", path);
		this.query = query;
	}


	/**
	 * Returns the number of resource elements returned by the request.
	 * @return The number of resources.
	 */
	public int countResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.countResources() : 0;
	}

	/**
	 * Returns a resource from the response specified by index. The resources are provided
	 * in the order they occured in the multistatus response.
	 * @return The resource element that corresponds to the i-th &lt;DAV:response&gt;
	 * entry in the multistatus response, or null if no resource was
	 * reveived or the index is out of bounds.
	 */
	public ResourceElement getResource(int i) {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.getResource(i) : null;
	}

	/**
	 * Returns an enumeration of ResourceElement objects that were retrieved
	 * from the multistatus response. Each element contains the URL of
	 * the corresponding resource, the set of retrieved properties and optionally
	 * a human readable description of the response state.
	 */
	public Iterator getResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.getResources() : null;
	}

	// internal methods
	/**
	 * Prepares the request entity. Called by RequestBase.perform.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<searchrequest").append(DAV.DEFAULT_XMLNS).append(IMS.PREFIXED_XMLNS).append(">");
		body.append("<").append(IMS.PREFIX).append("dtr-ims-query-complete-fetch>");
		body.append(Encoder.encodeXml(query));
		body.append("</").append(IMS.PREFIX).append("dtr-ims-query-complete-fetch></searchrequest>");
		setRequestEntity(body);
		return body;
	}
}