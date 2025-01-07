package com.tssap.dtr.client.lib.protocol.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class to define query strings for URLs, especially
 * for POST requests. 
 */
public class Query {
	
	private List queries = new ArrayList();
	
	/**
	 * Creates an empty query.
	 */
	public Query() {
	}
	
	/**
	 * Creates a query from the given string.
	 * The string is decoded first.
	 * @param s  the string 
	 */
	public Query(String s) {
		if (s != null) {
			String query = Encoder.decodeQuery(s);
			List params = Tokenizer.partsOf(query, "&", 0, '=');
			for (Iterator it = params.iterator(); it.hasNext();) {
				Pair param = (Pair)it.next();
				appendQueryParameter(param);
			}
		}
	}
	
	/**
	 * Checks whether this query actually has a content.
	 * @return  true, if ther is at list a single query parameter
	 * defined
	 */
	public boolean isEmpty() {
		return queries.size() == 0;		
	}
	
	/**
	 * Appends the given query.
	 * @param query  the query to append
	 */
	public void appendQuery(Query query) {
		if (query != null) {
			for (Iterator it = query.iterator(); it.hasNext();) {
				Pair queryParam = (Pair)it.next();
				appendQueryParameter(queryParam);
			}
		}		
	}
		
	/**
	 * Appends the given name-value pair to the query.
	 * If a query param with the given name already exists,
	 * it is replaced. 
	 * @param name  the name of the query parameter
	 * @param value  the value of the query parameter
	 */
	public void appendQueryParameter(String name, String value) {
		appendQueryParameter(new Pair(name, value, '='));
	}

	/**
	 * Appends the given parameter to the query.
	 * If a query param with the given name already exists,
	 * it is replaced.  
	 * @param name  the name of the query parameter
	 * @param value  the value of the query parameter
	 */
	public void appendQueryParameter(Pair parameter) {
		Pair p = getQueryParameter(parameter.getName());
		if (p != null) {
			p.setValue(parameter.getValue());
		} else {
			queries.add(parameter);			
		}
	}
	
	/**
	 * Returns the parameter with the given name.
	 * @param name  the name of the query parameter
	 * @return  the query parameter as Pair, or null if
	 * no such parameter exists.
	 */
	public Pair getQueryParameter(String name) {
		for (Iterator it = queries.iterator(); it.hasNext();) {
			Pair q = (Pair)it.next();
			if (name.equalsIgnoreCase(q.getName())) {
				return q;
			}
		}
		return null;
	}
	
	/**
	 * Removes the given query parameter from the query
	 * @param name   the query parameter to remove
	 */
	public void removeQueryParameter(String name) {
		for (Iterator it = queries.iterator(); it.hasNext();) {
			Pair q = (Pair)it.next();
			if (name.equalsIgnoreCase(q.getName())) {
				it.remove();
			}
		}
	}

	/**
	 * Returns an iterator for the query parameters
	 * @return a list of Pair instances
	 */
	public Iterator iterator() {
		return queries.iterator();
	}
		
	
	/**
	 * Returns the string representation of a query according
	 * to RFC2616, i.e. as URL-encoded name-value pairs separated
	 * by "&" (ampersand) symbols
	 * @return the string representation
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (queries.size() > 0) {
			int i=0;
			for (Iterator it = queries.iterator(); it.hasNext(); ++i) {
				if (i>0) {
					sb.append("&");
				} 
				Pair param = (Pair)it.next();
				sb.append(Encoder.encodeQuery(param.getName()));
				sb.append("=");
				sb.append(Encoder.encodeQuery(param.getValue()));
			}
		}
		return sb.toString();
	}

}
