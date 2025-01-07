/*===========================================================================*/
/*                                                                           */
/*  (C) Copyright SAP AG, Walldorf  1998                                     */
/*                                                                           */
/*===========================================================================*/

package com.sap.sdm.is.cs.cmd.client.impl.ni;

/*===========================================================================*/

import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This class supplies a container for storing a SAP route string. and various
 * methos for retrieving information about the string. Used in the
 * <code>NiSocket</code> class On creating an object of this class the syntax of
 * the SAP route string is checked.
 * 
 * @author Harald Mueller
 * @version 1.0
 */
public class SapRouteString {
	/**
	 * Vector to hold the sap route substrings
	 */
	private Vector entries;

	/**
	 * Create an empty SAP route string
	 */
	public SapRouteString() {
		entries = new Vector();
	}

	/**
	 * Create a <code>SapRouteString</code> from a SAP route string If the
	 * parameter is not a SAP route string, i.e. starts with /H/, a "normal"
	 * hostname is assumed. Then the SapRouteString contains only one entry.
	 * 
	 * @param host
	 *            the SAP route string
	 * @exception SapRouteStringFormatException
	 *                if the SAP route string has not a valid format
	 * @see SapRouteSubString
	 * @see SapRouteStringParser
	 */
	public SapRouteString(String host, int port)
			throws SapRouteStringFormatException {
		this();
		SapRouteStringParser parser = new SapRouteStringParser(host, port);

		for (; parser.hasMoreElements();) {
			entries.addElement(parser.nextElement());
		}

		Trace.print(Trace.CONTROL_FLOW,
				"SapRouteString.<init>: SapRouteString constructed: " + this);

	}

	/**
	 * Returns the number of SAP route substrings.
	 * 
	 * @return the number of SAP route substrings
	 * @see SapRouteSubString
	 */
	public final int getNuOfRoutes() {
		return entries.size();
	}

	/**
	 * Returns an Enumeration to access the SAP route substrings. It is
	 * guaranteed that the ordering is correct, i.e. first element returned by
	 * the enumeration is the first SAP route substring, etc ...
	 * 
	 * @return Enumeration returning the SAP route substrings
	 * @see java.util.Enumeration
	 */
	public synchronized Enumeration getRoutes() {
		return new SapRouteStringEnumerator(this);
	}

	/**
	 * Return the first subroute of the SAP route string
	 * 
	 * @return the first subroute of the SAP route string
	 * @see SapRouteSubString
	 */
	public final synchronized SapRouteSubString getFirstRoute() {
		return (SapRouteSubString) entries.firstElement();
	}

	/**
	 * Return the last subroute of the SAP route string
	 * 
	 * @return the last subroute of the SAP route string
	 * @see SapRouteSubString
	 */
	public final synchronized SapRouteSubString getLastRoute() {
		return (SapRouteSubString) entries.lastElement();
	}

	/**
	 * Return the route substring of the SAP route string with index pos
	 * 
	 * @return the subroute of the SAP route string with index pos As for array
	 *         0 <= pos < getNuOfRoutes(), i.e. fist subroute has index 0.
	 * @see SapRouteSubString
	 */
	public final synchronized SapRouteSubString getRoute(int pos) {
		return (SapRouteSubString) entries.elementAt(pos);
	}

	/**
	 * Returns the <code>SapRouteString</code> as SAP route string like
	 * (/H/<host>/S/<port>/W/<password>)
	 */
	public final synchronized String toString() {
		String retval = "";

		for (Enumeration e = getRoutes(); e.hasMoreElements();) {
			retval = retval + (SapRouteSubString) e.nextElement();
		}
		return new String(retval);
	}

}

/**
 * Implementation of the Enumeration for SapRouteString It is guaranteed that
 * the ordering of the returned SapRouteSubStrings is correct.
 */
final class SapRouteStringEnumerator implements Enumeration {
	SapRouteString saproutes;
	int count;

	SapRouteStringEnumerator(SapRouteString sr) {
		saproutes = sr;
		count = 0;
	}

	public boolean hasMoreElements() {
		return count < saproutes.getNuOfRoutes();
	}

	public Object nextElement() {
		synchronized (saproutes) {
			if (count < saproutes.getNuOfRoutes()) {
				return saproutes.getRoute(count++);
			}
		}
		throw new NoSuchElementException("SapRouteStringEnumerator");
	}
}
