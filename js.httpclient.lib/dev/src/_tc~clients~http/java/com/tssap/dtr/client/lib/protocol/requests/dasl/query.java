package com.tssap.dtr.client.lib.protocol.requests.dasl;

import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * Helper class used for DASL SEARCH requests to create a query.
 */
public class Query {

	/** The name attribute of the tag */
	private String op;
	/** The value attribute of the tag */
	private String value;
	/** The optional namespace/namespace prefix of the tag */
	private String ns;
	private String nsprefix;
	/** The list of children of this tag */
	private Query firstChild;
	/** The next neighbor of this tag */
	private Query next;

	private Query() {
	}

	private Query(String op) {
		this.op = op;
	}

	private Query(String op, String value) {
		this.op = op;
		this.value = value;
	}
	
	/**
	 * Allowed operators for creating property queries.
	 * @see Query#relationalOperator(String,String,String,String)
	 */
	public static class PropertyOperators {
		public static final String EQUAL = "eq";
		public static final String LOWER = "lt";
		public static final String LOWER_OR_EQUAL = "lte";
		public static final String GREATER = "gt";
		public static final String GREATER_OR_EQUAL = "gte";
		public static final String LIKE = "like";
	}

	/**
	 * Allowed operators for creating logical queries.
	 * @see Query#logicalOperator(String,Query,Query)
	 */	
	public static class LogicalOperators {
		public static final String AND = "and";
		public static final String OR = "or";
	}
	
	/**
	 * Returns this query as XML document fragment 
	 * following the DASL grammar.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		serialize(s);
		return s.toString();
	}

	/**
	 * Returns a &lt;contains&gt; query for the given literal.
	 */
	public static Query contains(String literal) {
		return new Query("contains", literal);
	}

	/**
	 * Returns a &lt;is-collection&gt; query.
	 */
	public static Query isCollection() {
		return new Query("is-collection");
	}

	/**
	 * Returns a &lt;is-defined&gt; query for the given property.
	 */
	public static Query isDefined(String propertyName) {
		return isDefined(propertyName, null);
	}	

	/**
	 * Returns a &lt;is-defined&gt; query for the given property.
	 */
	public static Query isDefined(String propertyName, String namespaceURI) {
		Query q = new Query("isdefined");
		Query prop = q.addChild("prop", null).addChild(propertyName, null);		
		int n = propertyName.indexOf(':');
		if (n>0) {
			prop.nsprefix = propertyName.substring(0,n);
		}	
		prop.ns = namespaceURI;
		return q;		
	}
	
	/**
	 * Returns a &lt;lower&gt; query for the given property and literal.
	 */
	public static Query lower(String propertyName, String literal) {
		return relationalOperator("lt", propertyName, literal);
	}
	
	/**
	 * Returns a &lt;lower&gt; query for the given property and literal.
	 */
	public static Query lower(String propertyName, String namespaceURI, String literal) {
		return relationalOperator("lt", propertyName, namespaceURI, literal);
	}	

	/**
	 * Returns a &lt;lower-equal&gt; query for the given property and literal.
	 */
	public static Query lowerEqual(String propertyName, String literal) {
		return relationalOperator("lte", propertyName, literal);
	}
	
	/**
	 * Returns a &lt;lower-equal&gt; query for the given property and literal.
	 */
	public static Query lowerEqual(String propertyName, String namespaceURI, String literal) {
		return relationalOperator("lte", propertyName, namespaceURI, literal);
	}	

	/**
	 * Returns a &lt;greater&gt; query for the given property and literal.
	 */
	public static Query greater(String propertyName, String literal) {
		return relationalOperator("gt", propertyName, literal);
	}
	
	/**
	 * Returns a &lt;greater&gt; query for the given property and literal.
	 */
	public static Query greater(String propertyName, String namespaceURI, String literal) {
		return relationalOperator("gt", propertyName, namespaceURI, literal);
	}	

	/**
	 * Returns a &lt;greater-equal&gt; query for the given property and literal.
	 */
	public static Query greaterEqual(String propertyName, String literal) {
		return relationalOperator("gte", propertyName, literal);
	}
	
	/**
	 * Returns a &lt;greater-equal&gt; query for the given property and literal.
	 */
	public static Query greaterEqual(String propertyName, String namespaceURI, String literal) {
		return relationalOperator("gte", propertyName, namespaceURI, literal);
	}	

	/**
	 * Returns an &lt;equal&gt; query for the given property and literal.
	 */
	public static Query equal(String propertyName, String literal) {
		return relationalOperator("eq", propertyName, literal);
	}
	
	/**
	 * Returns an &lt;equal&gt; query for the given property and literal.
	 */
	public static Query equal(String propertyName, String namespaceURI, String literal) {
		return relationalOperator("eq", propertyName, namespaceURI, literal);
	}	
	
	/**
	 * Returns a &lt;like&gt; query for the given property and literal.
	 */
	public static Query like(String propertyName, String literal) {
		return relationalOperator("like", propertyName, literal);
	}
	
	/**
	 * Returns a &lt;like&gt; query for the given property and literal.
	 */
	public static Query like(String propertyName, String namespaceURI, String literal) {
		return relationalOperator("like", propertyName, namespaceURI, literal);
	}		

	/**
	 * Returns a logical &lt;and&gt; relation of the given queries.
	 */
	public static Query and(Query left, Query right) {
		return logicalOperator("and", left, right);
	}

	/**
	 * Returns a logical &lt;or&gt; relation of the given queries.
	 */
	public static Query or(Query left, Query right) {
		return logicalOperator("or", left, right);
	}

	/**
	 * Returns a logical negation of the given query.
	 */
	public static Query not(Query left) {
		Query q = new Query("not");
		q.addChild(left);
		return q;
	}

	
	/**
	 * Returns a relational query with operator <code>op</code> for the given
	 * query and literal.
	 * @see Query.PropertyOperators
	 */
	public static Query relationalOperator(String op, String propertyName, String literal) {
		return relationalOperator(op, propertyName, null, literal);
	}	

	/**
	 * Returns a relational query with operator <code>op</code> for the given
	 * query and literal.
	 * @see Query.PropertyOperators
	 */
	public static Query relationalOperator(String op, String propertyName, String namespaceURI, String literal) {
		Query q = new Query(op);
		Query prop = q.addChild("prop", null).addChild(propertyName, null);		
		int n = propertyName.indexOf(':');
		if (n>0) {
			prop.nsprefix = propertyName.substring(0,n);
		}
		prop.ns = namespaceURI; 
		q.addChild("literal", literal);
		return q;
	}
	
	/**
	 * Returns a logical relation with operator <code>op</code> for the given
	 * queries.
	 * @see Query.LogicalOperators
	 */
	public static Query logicalOperator(String op, Query left, Query right) {
		Query q = new Query(op);
		q.addChild(left);
		q.addChild(right);
		return q;
	}

	/** 
	 * Appends a child to the list of children
	 */
	private Query addChild(Query query) {
		Query child = firstChild;
		if (child != null) {
			while (child.next != null) {
				child = child.next;
			}
			child.next = query;
		} else {
			firstChild = query;
		}
		return query;
	}
	
	/** 
	 * Appends a child to the list of children
	 */
	private Query addChild(String name, String value) {
		Query child = firstChild;
		Query newChild = new Query(name, value);
		if (child != null) {
			while (child.next != null) {
				child = child.next;
			}
			child.next = newChild;
		} else {
			firstChild = newChild;
		}
		return newChild;
	}


	/**
	 * Writes this query element as XML document fragment.
	 * @param buf target string buffer to append the resulting
	 * XML fragment
	 */	
	void serialize(StringBuffer buf) {
		buf.append("<").append(op);
		if (ns != null) {			
			buf.append(" xmlns:").append(nsprefix).append("=\"").append(ns).append("\"");
		}
		if (firstChild == null  &&  value == null) {
			buf.append("/>");
			return;
		} else {
			buf.append(">");
		}			
		
		if (value != null) {
			buf.append(Encoder.encodeXml(value));
		} else {
			Query child = firstChild;
			while (child != null) {
				child.serialize(buf);
				child = child.next;
			}
		}

		buf.append("</").append(op).append(">");
	}


}