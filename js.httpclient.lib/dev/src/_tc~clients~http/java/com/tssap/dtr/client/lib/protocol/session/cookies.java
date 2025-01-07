package com.tssap.dtr.client.lib.protocol.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IConnection;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * Helper class used by <code>SessionContext</code> to manage cookies. 
 */
public class Cookies {

	private Map cookies;
	private Set rejectedDomains;
	private Privacy privacy = Privacy.FROM_ORIGINAL_HOST_ONLY;
	
	private static Location TRACE = Location.getLocation(Cookies.class);	
	
	/**
	 * Enumeration determining the privacy applied for accepting cookies
	 * from foreign hosts.
	 */
	public static class Privacy {
		private final String name;
		private Privacy(String name) { 
			this.name = name; 
		}
		public String toString() { 
			return name; 
		}

		/** Rejects cookies with domain attributes that don't equal the original host */
		public static final Privacy FROM_ORIGINAL_HOST_ONLY = 
			new Privacy("from original host only");
		/** Allows cookies with all domain attributes. Note, this may compromise the
		  * clients privacy and generally should be avoided. */
		public static final Privacy FROM_ALL_HOSTS = 
			new Privacy("from all hosts");
	}

	/** 
	 * Sets the privacy for storing cookies from foreign hosts.
	 * @param privacy  a privacy
	 */
	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
		TRACE.infoT(
			"setPrivacy(Privacy)", 
			"cookie privacy setting changed [privacy=\"{0}\"]", 
			new Object[]{privacy.toString()}
		);		
	}

	/**
	 * Returns the privacy for storing cookies from foreign hosts.
	 */
	public Privacy getPrivacy() {
		return privacy;
	}

	/**
	 * Returns a list of all cookies stored in this context.
	 * Note, changes to this list do not affect the internally
	 * stored list of cookies.
	 * @return A list of Cookie instances. This list may be
	 * empty, if no cookies currently are defined.
	 */
	public List getCookies() {
		List result = new ArrayList();
		if (cookies != null) {
			Iterator domains = cookies.keySet().iterator();
			while (domains.hasNext()) {
				String domain = (String)domains.next();
				List domainCookies = getCookies(domain);
				result.addAll(domainCookies);
			}
			
			
		}
		return result;
	}

	/**
	 * Returns a list of cookies matching the given domain qualifier.
	 * @param domain  a domain qualifier, like "sap.com".
	 * @return A list of Cookie instances matching the requested domain,
	 * or an empty list.
	 * @see ISessionContext#getCookies
	 */
	public List getCookies(String domain) {
		if (cookies != null) {
			Object entry = cookies.get(domain);
			if (entry != null) {
				if (entry instanceof ArrayList) {
					return (List) entry;
				} else {
					List singletonList = new ArrayList();
					singletonList.add(entry);
					return singletonList;
				}
			}
		}
		return new ArrayList();
	}

	/**
	 * Returns a list of cookies matching the given host and path.
	 * @param host  a host qualifier, like "www.sap.com".
	 * @param path  a URL prefix, like "/public/"
	 * @return A list of Cookie instanced matching the requested host and
	 * path, or an empty list.
	 * @see ISessionContext#getCookies(String,String)
	 */
	public List getCookies(String host, String path) {
		return getCookies(host, path, -1);
	}

	/**
	 * Returns a list of cookies matching the given host, path prefix and port.
	 * @param host  a host qualifier, like "www.sap.com".
	 * @param path  a URL prefix, like "/public/"
	 * @param port  a port, like "1080"
	 * @return A list of Cookie instances matching the requested domain,
	 * path and port, or an empty list.
	 * @see ISessionContext#getCookies
	 */
	public List getCookies(String host, String path, int port) {
		List matchingCookies = getCookies(Cookie.getDomainOf(host));
		if (matchingCookies.size() > 0) {
			for (int i = 0; i < matchingCookies.size(); ++i) {
				Cookie cookie = (Cookie) matchingCookies.get(i);
				if (!cookie.matches(host, path, port)) {
					matchingCookies.remove(i);
				}
			}
			if (matchingCookies.size() > 1) {
				Collections.sort(matchingCookies);
			}
		}
		return matchingCookies;
	}

	/**
	 * Sets or changes the value of the specified cookie. If the cookie
	 * does not already exist, it is inserted in the collection of cookies
	 * assigned to this context. Otherwise only the parameters of the cookie
	 * are updated. 
	 * If the cookie is invalid or the domain of the cookie is contained in the 
	 * rejected domains list the cookies is not inserted.
	 * If the <code>maxAge</code> parameter of <code>cookie</code>
	 * is set to zero, the cookie is discarded.
	 * @param cookie  the cookie to add or change.
	 */
	public void setCookie(Cookie cookie) {
		if (!cookie.isValid()) {
			if (TRACE.beWarning()) {
				//$JL-SEVERITY_TEST$
				TRACE.warningT(
					"setCookie(Cookie)", 
					"rejecting invalid cookie for domain \"{0}\": {1}", 
					new Object[]{cookie.getDomain(), cookie.getName()});
			}
			return;
		}

		if (rejectedDomains!=null && rejectedDomains.contains(cookie.getDomain())) {
			if (TRACE.beInfo()) {
				TRACE.infoT(
					"setCookie(Cookie)",
					"rejecting cookie for blocked domain \"{0}\": {1}", 
					new Object[]{cookie.getDomain(), cookie.getName()});
			}
			return;
		}			
		
		boolean accepted = false;
		if (cookies == null) {
			cookies = new HashMap();
		}			
						
		List domainCookies = getCookies(cookie.getDomain());
		if (domainCookies == null) {
			cookies.put(cookie.getDomain(), cookie);
			accepted = true;					
		} else {
			boolean updated = false;
			for (int i = 0; i < domainCookies.size(); ++i) {
				if (cookie.equals(domainCookies.get(i))) {
					updated = true;
					if (cookie.getMaxAge() == 0) {							
						domainCookies.remove(i);
						if (TRACE.beInfo()) {
							TRACE.infoT(
								"setCookie(Cookie)",
								"removing expired cookie for domain \"{0}\": {1}", 
								new Object[]{cookie.getDomain(), cookie.getName()});
						}							
					} else {
						domainCookies.set(i, cookie);
						if (TRACE.beInfo()) {
							TRACE.infoT(
								"setCookie(Cookie)",
								"updating cookie for domain \"{0}\": {1}", 
								new Object[]{cookie.getDomain(), cookie.getName()});
						}						
					}
				}
			}
			if (!updated) {
				domainCookies.add(cookie);
				accepted = true;									
			}
			cookies.put(cookie.getDomain(), domainCookies);				
		}
		
		if (accepted && TRACE.beInfo()) {
			TRACE.infoT(
				"setCookie(Cookie)",
				"accepting cookie for domain \"{0}\": {1}", 
				new Object[]{cookie.getDomain(), cookie.getName()}
			);	
		}
	}
	
	/**
	 * Adds the given list of cookies.
	 * @param cookies a list of Cookie instances
	 */
	public void setCookies(List cookies) {
		Iterator iter = cookies.iterator();
		while (iter.hasNext()) {
			Cookie cookie = (Cookie)iter.next();
			setCookie(cookie);
		}
	}

	/**
	 * Removes the specified cookie.
	 * @param cookie  the cookie to remove.
	 */
	public void removeCookie(Cookie cookie) {
		if (cookies != null) {
			List domainCookies = getCookies(cookie.getDomain());
			if (domainCookies != null) {
				for (int i = 0; i < domainCookies.size(); ++i) {
					if (cookie.equals(domainCookies.get(i))) {
						domainCookies.remove(i);
						if (TRACE.beInfo()) {
							TRACE.infoT(
								"removeCookie(Cookie)",
								"removing cookie for domain \"{0}\": {1}", 
								new Object[]{cookie.getDomain(), cookie.getName()});
						}							
					}
				}
				cookies.put(cookie.getDomain(), domainCookies);
			}
		}
	}

	/**
	 * Removes all cookies of the sepcified domain.
	 * @param domain  a domain qualifier, like "foobar.com".
	 */
	public void removeCookies(String domain) {
		if (cookies != null) {
			cookies.remove(domain);
			TRACE.infoT(
				"removeCookies(String)",
				"removing all cookies for domain \"{0}\"", 
				new Object[]{domain});				
		}
	}
	
	/**
	 * Removes all cookies.
	 */
	public void removeAll() {
		if (cookies != null) {
			cookies.clear();
			TRACE.infoT("removeAll()", "removing all cookies");			
		}
	}	

	/**
	 * Adds a list of domains for which
	 * cookies are rejected. For example adding the
	 * domain ".com" to this list would reject cookies
	 * with domain ".com", but not ".foobar.com".
	 * @param domain  a comma- (oder semicolon-) separated 
	 * list of domain qualifier, like ".local;.example.com;.example.org".
	 */
	public void rejectCookiesFor(String domains) {
		if (rejectedDomains == null) {
			rejectedDomains = new HashSet();
		}
		
		List parts = Tokenizer.partsOf(domains, ",;");
		for (int i=0; i < parts.size(); ++i) {
			String part = (String)parts.get(i);
			if (!part.startsWith(".")) {
				part = "." + part;
			}
			rejectedDomains.add(part);
		}			
		
		TRACE.infoT(
			"rejectCookiesFor(String)", 
			"block cookies for domains \"{0}\"", 
			new Object[]{domains}
		);
	}

	/**
	 * Removes an entry from the list of domains from
	 * which cookies are rejected.
	 * @param domain  a domain qualifier, like "foobar.com".
	 */
	public void allowCookiesFor(String domain) {
		if (rejectedDomains != null) {
			rejectedDomains.remove(domain);
			TRACE.infoT(
				"allowCookiesFor(String)", 
				"allow cookies for domain \"{0}\"", 
				new Object[]{domain}
			);			
		}		
	}
	
	/**
	 * Returns an unmodifiable list of rejected domains
	 * @return a list of strings
	 */
	public List rejectedDomains() {		
		List result = new ArrayList();
		if (rejectedDomains != null) {
			result.addAll(rejectedDomains); 
		}			 
		return result;
	}

	/**
	* Assigns a cookie header to the given request
	* if there are cookies matching the given the connection.
	* @param request  the request to which a cookie should be applied
	* @param connection  the connection that selects which cookies are to be sent
	* @see ISessionContext#applyCookies(IRequest,IConnection)
	*/
	public void applyCookies(IRequest request, IConnection connection) {
		if (cookies != null) {
			String path = connection.getAbsolutePath(request.getPath());
			List matchingCookies = 
				getCookies(connection.getHost(), path, connection.getPort());

			StringBuffer cookieHeader = new StringBuffer();
			for (int i = 0; i < matchingCookies.size(); ++i) {
				Cookie cookie = (Cookie) matchingCookies.get(i);
				if (cookie.getExpires() < 0
					|| cookie.getExpires() > System.currentTimeMillis()
					|| cookie.requiresSecurity()
					&& connection.isSecureProtocol()) {
					if (i > 0) {
						cookieHeader.append(',');
					}
					cookieHeader.append(cookie.toString());
				}
			}
			if (cookieHeader.length() > 0) {
				request.setHeader(Header.HTTP.COOKIE, cookieHeader.toString());
			}
		}
	}

	/**
	 * Extracts cookie headers from the specified response.
	 * @param response - a response with "Cookie" headers.
	 * @see ISessionContext#setupCookies(IResponse,IConnection)
	 */
	public void setupCookies(IResponse response, IConnection connection) {
		Header cookies = response.getHeader(Header.HTTP.SET_COOKIE);
		if (cookies != null) {
			setCookies(cookies, response, connection);
		}
		cookies = response.getHeader(Header.HTTP.SET_COOKIE2);
		if (cookies != null) {
			setCookies(cookies, response, connection);
		}
	}

	/**
	 * Searches for cookies matching the given name.
	 * Note, the name of a cookie is not a unique parameter. For example,
	 * a server might provide individual session cookies to separate
	 * URL namespaces.
	 * @param name  the name of the cookie to search for
	 * @return  a list of Cookie instances, or an empty list if no matching
	 * cookie has been found.
	 */
	public List searchCookiesByName(String name) {
		Iterator iter = getCookies().iterator();
		List result = new ArrayList();
		while (iter.hasNext()) {
			Cookie next = (Cookie)iter.next();
			if (name.equals(next.getName())) {
				result.add(next);
			}
		}
		return result;
	}



	private void setCookies(Header cookies, IResponse response, IConnection connection) {
		List parts = cookies.getParts();
		for (int i = 0; i < parts.size(); ++i) {
			Cookie newCookie = 
				new Cookie(
					(String) parts.get(i),
					connection.getUrl(response.getRequest().getPath()),
					privacy == Privacy.FROM_ALL_HOSTS
				);
			setCookie(newCookie);
		}
	}
	

}
