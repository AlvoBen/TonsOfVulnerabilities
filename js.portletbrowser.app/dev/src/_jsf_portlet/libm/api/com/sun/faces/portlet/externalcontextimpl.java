/*
 * $Id: ExternalContextImpl.java,v 1.3.22.1 2005/04/15 01:04:18 jayashri Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map;
import java.util.AbstractMap;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.Cookie;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


/**
 * <p>Concrete implementation of <code>ExternalContext</code> for use in
 * a portlet environment.</p>
 */

public class ExternalContextImpl extends ExternalContext {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Create a new instance wrapping the specified Portlet API objects.</p>
     *
     * @param context The <code>PortletContext</code> for this application
     * @param request The <code>PortletRequest</code> for this request
     * @param response The <code>PortletResponse</code> for this response
     */
    public ExternalContextImpl(PortletContext context,
                               PortletRequest request,
                               PortletResponse response) {
	this.context = context;
	this.request = request;
	this.response = response;
        if (log.isTraceEnabled()) {
	    log.trace("Created ExternalContext " + this);
	}
    }


    // ------------------------------------------------------ Instance Variables


    // The Log instance for this class
    private static final Log log =
	LogFactory.getLog(ExternalContextImpl.class);


    // The Portlet API objects we are wrapping
    private PortletContext context;
    private PortletRequest request;
    private PortletResponse response;


    // Our private map implementations (lazily instantiated)
    private PortletApplicationMap applicationMap = null;
    private PortletInitParameterMap initParameterMap = null;
    private PortletRequestHeaderMap requestHeaderMap = null;
    private PortletRequestHeaderValuesMap requestHeaderValuesMap = null;
    private PortletRequestMap requestMap = null;
    private PortletRequestParameterMap requestParameterMap = null;
    private PortletRequestParameterValuesMap requestParameterValuesMap = null;
    private PortletSessionMap sessionMap = null;


    // ---------------------------------------------------------- Public Methods


    public void dispatch(String path) throws IOException {
	if (log.isTraceEnabled()) {
	    log.trace("dispatchMessage(" + path + ")");
	}
	if (path == null) {
	    throw new NullPointerException();
	}
	if (!(request instanceof RenderRequest) ||
	    !(response instanceof RenderResponse)) {
	    throw new IllegalStateException(path);
	}
        System.out.println("response content type " + ((RenderResponse)response).getContentType());
	PortletRequestDispatcher prd = context.getRequestDispatcher(path);
	if (prd == null) {
	    throw new IllegalArgumentException(path);
	}
	try {
	    prd.include((RenderRequest) request, (RenderResponse) response);
	} catch (PortletException e) {
	    throw new FacesException(e);
	}
    }

    public String encodeActionURL(String url) {
	if (url == null) {
	    throw new NullPointerException();
	}
	return (response.encodeURL(url));
    }
    

    public String encodeNamespace(String name) {
	if (!(response instanceof RenderResponse)) {
	    throw new IllegalStateException();
	}
	return (((RenderResponse) response).getNamespace() + name);
    }


    // PENDING(craigmcc) - Currently identical to encodeActionURL()
    public String encodeResourceURL(String url) {
	if (url == null) {
	    throw new NullPointerException();
	}
	return (response.encodeURL(url));
    }
    

    public Map getApplicationMap() {
	if (applicationMap == null) {
	    applicationMap = new PortletApplicationMap(context);
	}
	return (applicationMap);
    }


    public String getAuthType() {
	return (request.getAuthType());
    }


    public Object getContext() {
	return (context);
    }


    public String getInitParameter(String name) {
	if (name == null) {
	    throw new NullPointerException();
	}
	return (context.getInitParameter(name));
    }


    public Map getInitParameterMap() {
	if (initParameterMap == null) {
	    initParameterMap = new PortletInitParameterMap(context);
	}
	return (initParameterMap);
    }
    

    public String getRemoteUser() {
	return (request.getRemoteUser());
    }


    public Object getRequest() {
	return (request);
    }


    public String getRequestContextPath() {
	return (request.getContextPath());
    }


    // PENDING(craigmcc) - Do we want to try to parse "Cookie" properties?
    public Map getRequestCookieMap() {
	return (Collections.unmodifiableMap(Collections.EMPTY_MAP));
    }
    

    public Map getRequestHeaderMap() {
	if (requestHeaderMap == null) {
	    requestHeaderMap = new PortletRequestHeaderMap(request);
	}
	return (requestHeaderMap);
    }
    

    public Map getRequestHeaderValuesMap() {
	if (requestHeaderValuesMap == null) {
	    requestHeaderValuesMap = new PortletRequestHeaderValuesMap(request);
	}
	return (requestHeaderValuesMap);
    }
    

    public Locale getRequestLocale() {
	return (request.getLocale());
    }
    

    public Iterator getRequestLocales() {
        return (new LocalesIterator(request.getLocales()));
    }


    public Map getRequestMap() {
	if (requestMap == null) {
	    requestMap = new PortletRequestMap(request);
	}
	return (requestMap);
    }


    public Map getRequestParameterMap() {
	if (requestParameterMap == null) {
	    requestParameterMap = new PortletRequestParameterMap(request);
	}
	return (requestParameterMap);
    }
    

    public Iterator getRequestParameterNames() {
	return (request.getParameterMap().keySet().iterator());
    }


    public Map getRequestParameterValuesMap() {
	if (requestParameterValuesMap == null) {
	    requestParameterValuesMap = new PortletRequestParameterValuesMap(request);
	}
	return (requestParameterValuesMap);
    }
    

    // PENDING(craigmcc) - Alternate interpretation in portlet environment?
    public String getRequestPathInfo() {
	return (null);
    }
    

    // PENDING(craigmcc) - Alternate interpretation in portlet environment?
    public String getRequestServletPath() {
	return (null);
    }
    

    public URL getResource(String path) throws MalformedURLException {
	if (path == null) {
	    throw new NullPointerException();
	}
	return (context.getResource(path));
    }


    public InputStream getResourceAsStream(String path) {
	if (path == null) {
	    throw new NullPointerException();
	}
	return (context.getResourceAsStream(path));
    }



    public Set getResourcePaths(String path) {
	if (path == null) {
	    throw new NullPointerException();
	}
	return (context.getResourcePaths(path));
    }


    public Object getResponse() {
	return (response);
    }

     public String getResponseCharacterEncoding() {
         if (response instanceof RenderResponse){
             return ((RenderResponse)response).getCharacterEncoding();
         }else{
             throw new IllegalStateException("Not in render phase");
         }
        
    }
    
    public String getResponseContentType() {
        if (response instanceof RenderResponse){
             return ((RenderResponse)response).getContentType();
         }else{
             throw new IllegalStateException("Not in render phase");
         }
    }

    public Object getSession(boolean create) {
	return (request.getPortletSession(create));
    }


    public Map getSessionMap() {
	if (sessionMap == null) {
	    sessionMap = new PortletSessionMap(request);
	}
	return (sessionMap);
    }


    public Principal getUserPrincipal() {
	return (request.getUserPrincipal());
    }


    public boolean isUserInRole(String role) {
	if (role == null) {
	    throw new NullPointerException();
	}
	return (request.isUserInRole(role));
    }


    public void log(String message) {
	if (message == null) {
	    throw new NullPointerException();
	}
	context.log(message);
    }


    public void log(String message, Throwable exception) {
	if ((message == null) || (exception == null)) {
	    throw new NullPointerException();
	}
	context.log(message, exception);
    }

    public void setResponse(Object response) {
	if (response instanceof RenderResponse) {
	    this.response = (RenderResponse) response;
	}
    }
    
    public void setRequest(Object request) {
	if (request instanceof RenderRequest) {
	    this.request = (RenderRequest) request;
	}
    }
    
    public void redirect(String path) throws IOException {
	if (log.isTraceEnabled()) {
	    log.trace("redirectMessage(" + path + ")");
	}
	if (path == null) {
	    throw new NullPointerException();
	}
	if (!(request instanceof ActionRequest) ||
	    !(response instanceof ActionResponse)) {
	    throw new IllegalStateException(path);
	}
        try {
            ((ActionResponse) response).sendRedirect(path);
        } catch (Exception e) {
            throw new FacesException(e);
        }
    }

}
// ------------------------------------------------------------- Private Classes


 class LocalesIterator implements Iterator {

    public LocalesIterator(Enumeration locales) {
        this.locales = locales;
    }
    private Enumeration locales;

    public boolean hasNext() { return locales.hasMoreElements(); }
    public Object next() { return locales.nextElement(); }
    public void remove() { throw new UnsupportedOperationException(); }

}

abstract class BaseContextMap extends AbstractMap{

    // Unsupported by all Maps.
    public void clear() {
        throw new UnsupportedOperationException();
    }


    // Unsupported by all Maps.
    public void putAll(Map t) {
        throw new UnsupportedOperationException();
    }


    // Supported by maps if overridden
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }


    static class Entry implements Map.Entry {

        // immutable Entry
        private final Object key;
        private final Object value;


        Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }


        public Object getKey() {
            return key;
        }


        public Object getValue() {
            return value;
        }


        // No support of setting the value
        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }


        public int hashCode() {
            return ((key == null ? 0 : key.hashCode()) ^
                (value == null ? 0 : value.hashCode()));
        }


        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Map.Entry))
                return false;

            Map.Entry input = (Map.Entry) obj;
            Object inputKey = input.getKey();
            Object inputValue = input.getValue();

            if (inputKey == key ||
                (inputKey != null && inputKey.equals(key))) {
                if (inputValue == value ||
                    (inputValue != null && inputValue.equals(value))) {
                    return true;
                }
            }
            return false;
        }
    }
} 

 class PortletApplicationMap extends BaseContextMap {


    public PortletApplicationMap(PortletContext context) {
        this.context = context;
    }


    private PortletContext context = null;
 

    public void clear() {
        Iterator keys = keySet().iterator();
        while (keys.hasNext()) {
            context.removeAttribute((String) keys.next());
        }
    }


    public boolean containsKey(Object key) {
        return (context.getAttribute(key(key)) != null);
    }


    public boolean containsValue(Object value) {
        if (value == null) {
            return (false);
        }
        Enumeration keys = context.getAttributeNames();
        while (keys.hasMoreElements()) {
            Object next = context.getAttribute((String) keys.nextElement());
            if (next == value) {
                return (true);
            }
        }
        return (false);
    }

    public Set entrySet() {
        Set entries = new HashSet();
        for (Enumeration e = context.getAttributeNames();
             e.hasMoreElements();) {
            String key = (String) e.nextElement();
            entries.add(new Entry(key, context.getAttribute(key)));
        }
        return entries;
    }


    public boolean equals(Object o) {
        return (context.equals(o));
    }


    public Object get(Object key) {
        return (context.getAttribute(key(key)));
    }


    public int hashCode() {
        return (context.hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys = context.getAttributeNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        if (value == null) {
            return (remove(key));
        }
        String skey = key(key);
        Object previous = context.getAttribute(skey);
        context.setAttribute(skey, value);
        return (previous);
    }


    public void putAll(Map map) {
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            context.setAttribute(key, map.get(key));
        }
    }


    public Object remove(Object key) {
        String skey = key(key);
        Object previous = context.getAttribute(skey);
        context.removeAttribute(skey);
        return (previous);
    }


    public int size() {
        int n = 0;
        Enumeration keys = context.getAttributeNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys = context.getAttributeNames();
        while (keys.hasMoreElements()) {
            list.add(context.getAttribute((String) keys.nextElement()));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }


}


 class PortletInitParameterMap extends BaseContextMap {


    public PortletInitParameterMap(PortletContext context) {
        this.context = context;
    }


    private PortletContext context = null;


    public void clear() {
        throw new UnsupportedOperationException();
    }


    public boolean containsKey(Object key) {
        return (context.getInitParameter(key(key)) != null);
    }


    public boolean containsValue(Object value) {
        Iterator values = values().iterator();
        while (values.hasNext()) {
            if (value.equals(values.next())) {
                return (true);
            }
        }
        return (false);
    }

    public Set entrySet() {
        Set entries = new HashSet();

        for (Enumeration e = context.getInitParameterNames();
             e.hasMoreElements();) {
            String initParamName = (String) e.nextElement();
            entries.add(new Entry(initParamName,
                                  context.getInitParameter(
                                      initParamName)));
        }
        return entries;
    }


    public boolean equals(Object o) {
        return (context.equals(o));
    }


    public Object get(Object key) {
        return (context.getInitParameter(key(key)));
    }


    public int hashCode() {
        return (context.hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys = context.getInitParameterNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }


    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }


    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }


    public int size() {
        int n = 0;
        Enumeration keys = context.getInitParameterNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys = context.getInitParameterNames();
        while (keys.hasMoreElements()) {
            list.add(context.getInitParameter((String) keys.nextElement()));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }


}


 class PortletRequestHeaderMap extends BaseContextMap {


    public PortletRequestHeaderMap(PortletRequest request) {
        this.request = request;
    }


    private PortletRequest request = null;


    public void clear() {
        throw new UnsupportedOperationException();
    }


    public boolean containsKey(Object key) {
        return (request.getProperty(key(key)) != null);
    }


    public boolean containsValue(Object value) {
        Iterator values = values().iterator();
        while (values.hasNext()) {
            if (value.equals(values.next())) {
                return (true);
            }
        }
        return (false);
    }


    public Set entrySet() {
        Set set = new HashSet();
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            set.add(request.getProperty((String) keys.nextElement()));
        }
        return (set);
    }
    
    public boolean equals(Object o) {
        return (request.equals(o));
    }


    public Object get(Object key) {
        return (request.getProperty(key(key)));
    }


    public int hashCode() {
        return (request.hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }


    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }


    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }


    public int size() {
        int n = 0;
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            list.add(request.getProperty((String) keys.nextElement()));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }


}


 class PortletRequestHeaderValuesMap extends BaseContextMap {


    public PortletRequestHeaderValuesMap(PortletRequest request) {
        this.request = request;
    }


    private PortletRequest request = null;


    public void clear() {
        throw new UnsupportedOperationException();
    }


    public boolean containsKey(Object key) {
        return (request.getProperty(key(key)) != null);
    }


    public boolean containsValue(Object value) {
        if (!(value instanceof String[])) {
            return (false);
        }
        String test[] = (String[]) value;
        Iterator values = values().iterator();
        while (values.hasNext()) {
            String actual[] = (String[]) values.next();
            if (test.length == actual.length) {
                boolean matched = true;
                for (int i = 0; i < test.length; i++) {
                    if (!test[i].equals(actual[i])) {
                        matched = false;
                        break;
                    }
                }
                if (matched) {
                    return (true);
                }
            }
        }
        return (false);
    }


    public Set entrySet() {
        Set set = new HashSet();
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            set.add(request.getProperties((String) keys.nextElement()));
        }
        return (set);
    }
    
    public boolean equals(Object o) {
        return (request.equals(o));
    }


    public Object get(Object key) {
        List list = new ArrayList();
        Enumeration values = request.getProperties(key(key));
        while (values.hasMoreElements()) {
            list.add((String) values.nextElement());
        }
        return (((String[]) list.toArray(new String[list.size()])));
    }


    public int hashCode() {
        return (request.hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }


    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }


    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }


    public int size() {
        int n = 0;
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys = request.getPropertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            List list1 = new ArrayList();
            Enumeration values = request.getProperties(key);
            while (values.hasMoreElements()) {
                list1.add((String) values.nextElement());
            }
            list.add(((String[]) list1.toArray(new String[list1.size()])));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }


}


 class PortletRequestMap extends BaseContextMap {


    public PortletRequestMap(PortletRequest request) {
        this.request = request;
    }


    private PortletRequest request = null;
 

    public void clear() {
        Iterator keys = keySet().iterator();
        while (keys.hasNext()) {
            request.removeAttribute((String) keys.next());
        }
    }


    public boolean containsKey(Object key) {
        return (request.getAttribute(key(key)) != null);
    }


    public boolean containsValue(Object value) {
        if (value == null) {
            return (false);
        }
        Enumeration keys = request.getAttributeNames();
        while (keys.hasMoreElements()) {
            Object next = request.getAttribute((String) keys.nextElement());
            if (next == value) {
                return (true);
            }
        }
        return (false);
    }

    public Set entrySet() {
        Set entries = new HashSet();
        for (Enumeration e = request.getAttributeNames();
             e.hasMoreElements();) {
            String key = (String) e.nextElement();
            entries.add(new Entry(key, request.getAttribute(key)));
        }
        return entries;
    }


    public boolean equals(Object o) {
        return (request.equals(o));
    }


    public Object get(Object key) {
        return (request.getAttribute(key(key)));
    }


    public int hashCode() {
        return (request.hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys = request.getAttributeNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        if (value == null) {
            return (remove(key));
        }
        String skey = key(key);
        Object previous = request.getAttribute(skey);
        request.setAttribute(skey, value);
        return (previous);
    }


    public void putAll(Map map) {
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            request.setAttribute(key, map.get(key));
        }
    }


    public Object remove(Object key) {
        String skey = key(key);
        Object previous = request.getAttribute(skey);
        request.removeAttribute(skey);
        return (previous);
    }


    public int size() {
        int n = 0;
        Enumeration keys = request.getAttributeNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys = request.getAttributeNames();
        while (keys.hasMoreElements()) {
            list.add(request.getAttribute((String) keys.nextElement()));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }


}


 class PortletRequestParameterMap extends BaseContextMap {


    public PortletRequestParameterMap(PortletRequest request) {
        this.request = request;
    }


    private PortletRequest request = null;


    public void clear() {
        throw new UnsupportedOperationException();
    }


    public boolean containsKey(Object key) {
        return (request.getParameter(key(key)) != null);
    }


    public boolean containsValue(Object value) {
        Iterator values = values().iterator();
        while (values.hasNext()) {
            if (value.equals(values.next())) {
                return (true);
            }
        }
        return (false);
    }

    public Set entrySet() {
        Set entries = new HashSet();
        for (Enumeration e = request.getParameterNames();
             e.hasMoreElements();) {
            String paramName = (String) e.nextElement();
            entries.add(new Entry(paramName, request.getParameter(paramName)));
        }
        return entries;
    }


    public boolean equals(Object o) {
        return (request.equals(o));
    }


    public Object get(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return (request.getParameter(key(key)));
    }


    public int hashCode() {
        return (request.hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }


    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }


    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }


    public int size() {
        int n = 0;
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            list.add(request.getParameter((String) keys.nextElement()));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }


}


 class PortletRequestParameterValuesMap extends BaseContextMap {


    public PortletRequestParameterValuesMap(PortletRequest request) {
        this.request = request;
    }

    private PortletRequest request = null;

    public void clear() {
        throw new UnsupportedOperationException();
    }


    public boolean containsKey(Object key) {
        return (request.getParameter(key(key)) != null);
    }

    public boolean containsValue(Object value) {
        Iterator values = values().iterator();
        while (values.hasNext()) {
            if (value.equals(values.next())) {
                return (true);
            }
        }
        return (false);
    }
    
    public Object get(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return request.getParameterValues(key.toString());
    }


    public Set entrySet() {
        Set entries = new HashSet();
        for (Enumeration e = request.getParameterNames();
             e.hasMoreElements();) {
            String paramName = (String) e.nextElement();
            entries.add(
                new Entry(paramName, request.getParameterValues(paramName)));
        }
        return entries;
    }


    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PortletRequestParameterValuesMap))
            return false;
        return super.equals(obj);
    }


    public int hashCode() {
        return (request.hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }


    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }


    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }


    public int size() {
        int n = 0;
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            list.add(request.getParameterValues((String) keys.nextElement()));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }


}


 class PortletSessionMap extends BaseContextMap {


    public PortletSessionMap(PortletRequest request) {
        this.request = request;
    }


    private PortletRequest request = null;
 

    public void clear() {
        Iterator keys = keySet().iterator();
        while (keys.hasNext()) {
            request.getPortletSession().removeAttribute((String) keys.next());
        }
    }


    public boolean containsKey(Object key) {
        return (request.getPortletSession().getAttribute(key(key)) != null);
    }


    public boolean containsValue(Object value) {
        if (value == null) {
            return (false);
        }
        Enumeration keys =
        request.getPortletSession().getAttributeNames(PortletSession.PORTLET_SCOPE);
        while (keys.hasMoreElements()) {
            Object next = request.getPortletSession().getAttribute((String) keys.nextElement());
            if (next == value) {
                return (true);
            }
        }
        return (false);
    }

    public Set entrySet() {
        Set entries = new HashSet();
        PortletSession session = request.getPortletSession();
        for (Enumeration e = session.getAttributeNames();
             e.hasMoreElements();) {
            String key = (String) e.nextElement();
            entries.add(new Entry(key, session.getAttribute(key)));
        }
        return entries;
    }


    public boolean equals(Object o) {
        return (request.getPortletSession().equals(o));
    }


    public Object get(Object key) {
        return (request.getPortletSession().getAttribute(key(key)));
    }


    public int hashCode() {
        return (request.getPortletSession().hashCode());
    }


    public boolean isEmpty() {
        return (size() < 1);
    }


    public Set keySet() {
        Set set = new HashSet();
        Enumeration keys =
        request.getPortletSession().getAttributeNames(PortletSession.PORTLET_SCOPE);
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return (set);
    }


    public Object put(Object key, Object value) {
        if (value == null) {
            return (remove(key));
        }
        String skey = key(key);
        Object previous = request.getPortletSession().getAttribute(skey);
        request.getPortletSession().setAttribute(skey, value);
        return (previous);
    }


    public void putAll(Map map) {
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            request.getPortletSession().setAttribute(key, map.get(key));
        }
    }


    public Object remove(Object key) {
        String skey = key(key);
        Object previous = request.getPortletSession().getAttribute(skey);
        request.getPortletSession().removeAttribute(skey);
        return (previous);
    }


    public int size() {
        int n = 0;
        Enumeration keys =
        request.getPortletSession().getAttributeNames(PortletSession.PORTLET_SCOPE);
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return (n);
    }


    public Collection values() {
        List list = new ArrayList();
        Enumeration keys =
        request.getPortletSession().getAttributeNames(PortletSession.PORTLET_SCOPE);
        while (keys.hasMoreElements()) {
            list.add(request.getPortletSession().getAttribute((String) keys.nextElement()));
        }
        return (list);
    }


    private String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }
}
 
 

