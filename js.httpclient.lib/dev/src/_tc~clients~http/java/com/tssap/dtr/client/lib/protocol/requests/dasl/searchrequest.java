package com.tssap.dtr.client.lib.protocol.requests.dasl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.MultiStatusRequest;
import com.tssap.dtr.client.lib.protocol.requests.dav.Depth;

/**
 * This request class implements the DASL standard "Search" method.
 */
public class SearchRequest extends MultiStatusRequest {

	/** The list of property names to be send in the profind request*/
	private ArrayList properties;
	/** Table of namespaces used by this request */
	private HashMap namespaces;
	/** The "from" for the query */
	private String from;
	/** The depth option of this request. */
	private Depth depth = Depth.DEPTH_INFINITY;
	/** The where clause of this request */
	private Query whereClause;
	/** List of order clauses */
	private ArrayList orderClauses;

	/** internal class reprenting order clauses */
	private class OrderClause {
		public String propertyName;
		public Ordering order;
		public CaseSensitivity sensitivity;
		public OrderClause(String propertyName, Ordering order, CaseSensitivity sensitivity) {
			this.propertyName = propertyName;
			this.order = order;
			this.sensitivity = sensitivity;
		}
	}

	/**
	 * Create an empty DASL query in the specified colection.
	 * @param path  the entity to which this request is directed, usually
	 * the root path of the repository or an index server.
	 * @param from  the path of a collection to search.
	 */
	public SearchRequest(String path, String from) {
		super("SEARCH", path);
		this.from = from;
	}

	/**
	 * Create an empty DASL query in the specified collection with the specified depth.
	 * @param path  the entity to which this request is directed, usually
	 * the root path of the repository or an index server.
	 * @param from  the path of a collection to search.
	 * @param depth  determines if the request should be
	 * applied only to the collection itself (Depth.DEPTH_0), the internal
	 * members of the collection (Depth.DEPTH_1), or to any members of the
	 * collection hierarchy (Depth.INFINITY). Default is Depth.DEPTH_INFINITY.
	 */
	public SearchRequest(String path, String from, Depth depth) {
		super("SEARCH", path);
		this.from = from;
		this.depth = depth;
	}

	/**
	 * Adds a single property select to the request.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 */
	public void addSelectProperty(String propertyName) {
		if (properties == null) {
			properties = new ArrayList();
		}
		properties.add(propertyName);
	}

	/**
	 * Adds a single property select to the request and specifies the namespace
	 * URI used by this element.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param namespaceURI  the namespace the property belongs to (given as
	 * an URI).
	 */
	public void addSelectProperty(String propertyName, String namespaceURI) {
		int n = propertyName.indexOf(':');
		if (n > 0) {
			addNamespace(propertyName.substring(0, n), namespaceURI);
		}
		addSelectProperty(propertyName);
	}

	/**
	 * Adds a set of property selects to the request.
	 * @param propertyNames  an array of property names with namespace
	 * prefixes (despite for properties that belong to the default namespace "DAV:").
	 */
	public void addSelectProperty(String[] propertyNames) {
		if (properties == null) {
			properties = new ArrayList();
		}
		for (int i = 0; i < propertyNames.length; ++i) {
			properties.add(propertyNames[i]);
		}
	}

	/**
	 * Adds a namespaces to the request specified by a namespaces prefix
	 * and the URI that defines that namespace. The namespace is "DAV:"
	 * predefined and must not be set with this method.
	 * @param prefix  a namespace prefix.
	 * @param namespaceURI  the URI associated with the namespace.
	 */
	public void addNamespace(String prefix, String namespaceURI) {
		if (namespaces == null) {
			namespaces = new HashMap();
		}
		if (prefix.endsWith(":")) {
			namespaces.put(prefix.substring(0, prefix.length() - 1), namespaceURI);
		} else {
			namespaces.put(prefix, namespaceURI);
		}
	}

	/**
	 * Adds a where clause to the request.
	 * @param whereClause  a Query instance specifying a set of conditions.
	 */
	public void addWhereClause(Query whereClause) {
		this.whereClause = whereClause;
	}

	/**
	 * Adds an order clause to the request. The order is based on
	 * the specified property and order direction. The specified property
	 * must be in the set of properties defined in the select clause.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param order  either Ordering.ASCENDING ot Ordering.DESCENDING.
	 * @param sensivitiy  determine if the ordering should obey the case of
	 * property values.
	 */
	public void addOrderClause(String propertyName, Ordering order, CaseSensitivity sensitivity) {
		if (orderClauses == null) {
			orderClauses = new ArrayList();
		}
		orderClauses.add(new OrderClause(propertyName, order, sensitivity));
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
	 * @return A request entity for this SEARCH request.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<searchrequest").append(DAV.DEFAULT_XMLNS);
		if (namespaces != null && namespaces.size() > 0) {
			Iterator keys = namespaces.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				body.append(" xmlns:").append(key).append("=\"").append(namespaces.get(key)).append("\"");
			}
		}
		body.append("><basicsearch>");

		body.append("<select>");
		if (properties != null && properties.size() > 0) {
			body.append("<prop>");
			for (int i = 0; i < properties.size(); ++i) {
				body.append("<").append(properties.get(i)).append("/>");
			}
			body.append("</prop>");
		} else {
			body.append("<allprop/>");
		}
		body.append("</select>");

		body.append("<from><scope><href>").append(from).append("</href>");
		body.append("<depth>").append(convertDepth(depth)).append("</depth></scope></from>");

		if (whereClause != null) {
			body.append("<where>").append(whereClause.toString()).append("</where>");
		}

		if (orderClauses != null) {
			body.append("<orderby>");
			for (int i = 0; i < orderClauses.size(); ++i) {
				OrderClause orderClause = (OrderClause) orderClauses.get(i);
				body.append("<order casesensitive=").append(orderClause.sensitivity).append("\"");
				body.append("<prop><").append(orderClause.propertyName).append("/></prop>");
				body.append("<").append(orderClause.order).append("/>");
				body.append("</order>");
			}
			body.append("</orderby>");
		}

		body.append("</basicsearch></searchrequest>");
		setRequestEntity(body);
		return body;
	}

	/** converts Depth enumerator value to DASL format */
	private static String convertDepth(Depth depth) {
		if (depth == Depth.DEPTH_0) {
			return "0";
		} else if (depth == Depth.DEPTH_1) {
			return "1";
		} else {
			return "infinity";
		}
	}
}