/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.xml.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * @author D042774
 *
 */
public class SdoNamespaceContext implements NamespaceContext {
    private final List<String> _ns = new ArrayList<String>();
    private final SdoNamespaceContext _parent;
    private String _defaultNsUri;

    /**
     * @param _scope
     * @param nsUri
     */
    public SdoNamespaceContext(SdoNamespaceContext parent) {
        super();
        _parent = parent;

    }

    /**
     *
     */
    public SdoNamespaceContext() {
        this(null);
        _defaultNsUri = XMLConstants.NULL_NS_URI;
    }

    public void initRootCtx() {
        _ns.add("xsi");
        _ns.add(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        initSchemaCtx();
    }

    public void initSchemaCtx() {
        _ns.add("xsd");
        _ns.add(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public void addPrefix(String prefix, String uri) {
        if (prefix.length() == 0) {
            _defaultNsUri = uri;
        } else {
            _ns.add(prefix);
            _ns.add(uri);
        }
    }


    /**
     * @param pIndex
     * @return
     */
    public String getNamespaceURI(int pIndex) {
        return _ns.get((pIndex * 2) + 1);
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("illegal prefix: null");
        }
        if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
            return XMLConstants.XML_NS_URI;
        }
        if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        return internGetNamespaceURI(prefix);
    }

    protected String internGetNamespaceURI(String prefix) {
        if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix) && _defaultNsUri != null) {
            return _defaultNsUri;
        } else {
            for (int i=0; i<_ns.size(); i+=2) {
                if (_ns.get(i).equals(prefix)) {
                    return _ns.get(i + 1);
                }
            }
        }
        if (_parent != null) {
            return _parent.internGetNamespaceURI(prefix);
        }
        return XMLConstants.NULL_NS_URI;
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
     */
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("illegal namespaceURI: null");
        }
        if (namespaceURI.equals(_defaultNsUri)) {
            return XMLConstants.DEFAULT_NS_PREFIX;
        }
        if (XMLConstants.XML_NS_URI.equals(namespaceURI)) {
            return XMLConstants.XML_NS_PREFIX;
        }
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
        return internGetPrefix(namespaceURI);
    }

    protected String internGetPrefix(String namespaceURI) {
        for (int i=1; i<_ns.size(); i+=2) {
            if (_ns.get(i).equals(namespaceURI)) {
                return _ns.get(i - 1);
            }
        }
        if (_parent != null) {
            return _parent.internGetPrefix(namespaceURI);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
     */
    public Iterator<String> getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("illegal namespaceURI: null");
        }
        String prefix;
        if (namespaceURI.equals(_defaultNsUri)) {
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
        } else if (XMLConstants.XML_NS_URI.equals(namespaceURI)) {
            prefix = XMLConstants.XML_NS_PREFIX;
        } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            prefix = XMLConstants.XMLNS_ATTRIBUTE;
        } else {
            prefix  = null;
        }
        if (prefix != null) {
            return Collections.singleton(prefix).iterator();
        }
        Set<String> prefixes = new HashSet<String>();
        internGetPrefixes(namespaceURI, prefixes);
        return Collections.unmodifiableCollection(prefixes).iterator();
    }

    protected void internGetPrefixes(String namespaceURI, Set<String> prefixes) {
        for (int i=1; i<_ns.size(); i+=2) {
            if (_ns.get(i).equals(namespaceURI)) {
                prefixes.add(_ns.get(i - 1));
            }
        }
        if (_parent != null) {
            _parent.internGetPrefixes(namespaceURI, prefixes);
        }
    }

    /**
     * @param index
     * @return
     */
    public String getNamespacePrefix(int index) {
        return _ns.get(index * 2);
    }

    /**
     * @return
     */
    public int getNamespaceDeclarationCount() {
        return _ns.size() / 2;
    }

    public boolean isRootCtx() {
        return _parent == null;
    }
}
