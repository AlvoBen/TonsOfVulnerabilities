/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.context;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.impl.data.DataHelperImpl;
import com.sap.sdo.impl.objects.CopyHelperImpl;
import com.sap.sdo.impl.objects.DataFactoryImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.EqualityHelperImpl;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.xml.XMLHelperImpl;
import com.sap.sdo.impl.xml.XSDHelperImpl;

import commonj.sdo.Property;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * This class represents a context that includes all instances of helpers
 * bundled by context class loader.
 * @author D042774
 *
 */
public class HelperContextImpl implements SapHelperContext, Serializable {

    private static final long serialVersionUID = -6182033683400572388L;

    private transient CopyHelper _copyHelper;
    private transient DataFactory _dataFactory;
    private transient DataHelper _dataHelper;
    private transient EqualityHelper _equalityHelper;
    private transient TypeHelper _typeHelper;
    private transient XMLHelper _xmlHelper;
    private transient XSDHelper _xsdHelper;
    private final String _id;
    private final HelperContextImpl _parentContext;
    private final transient WeakReference<ClassLoader> _classLoader;

    private Property _mappingStrategyProperty;

    private final Map<String,Object> _options = new HashMap<String,Object>();

    /**
     * Private constructor.
     * Initialize all instances of helpers of this context.
     *
     */
    protected HelperContextImpl(String pId, ClassLoader pClassLoader, HelperContextImpl pParent) {
        _id = pId;
        _parentContext = pParent;
        _classLoader = new WeakReference<ClassLoader>(pClassLoader);
    }

    void init() {
        _copyHelper = CopyHelperImpl.getInstance(this);
        _dataFactory = DataFactoryImpl.getInstance(this);
        _dataHelper = DataHelperImpl.getInstance(this);
        _equalityHelper = EqualityHelperImpl.getInstance(this);
        _xmlHelper = XMLHelperImpl.getInstance(this);
        _xsdHelper = XSDHelperImpl.getInstance(this);
        _typeHelper = TypeHelperImpl.getInstance(this);
    }


    public ClassLoader getClassLoader() {
        ClassLoader classLoader = _classLoader.get();
        if (classLoader == null) {
            throw new IllegalStateException(
                "ClassLoader of this HelperContext is outdated");
        }
        return classLoader;
    }

    /**
     * Returns the id of the HelperContext.
     * @return the id.
     */
    public String getId() {
        return _id;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.HelperContext#getCopyHelper()
     */
    public CopyHelper getCopyHelper() {
        return _copyHelper;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.HelperContext#getDataFactory()
     */
    public DataFactory getDataFactory() {
        return _dataFactory;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.HelperContext#getDataHelper()
     */
    public DataHelper getDataHelper() {
        return _dataHelper;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.HelperContext#getEqualityHelper()
     */
    public EqualityHelper getEqualityHelper() {
        return _equalityHelper;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.HelperContext#getTypeHelper()
     */
    public TypeHelper getTypeHelper() {
        return _typeHelper;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.HelperContext#getXMLHelper()
     */
    public XMLHelper getXMLHelper() {
        return _xmlHelper;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.HelperContext#getXSDHelper()
     */
    public XSDHelper getXSDHelper() {
        return _xsdHelper;
    }

    private Object readResolve() {
        return SapHelperProvider.getContext(_id);
    }

    @Override
    public String toString() {
        return _id;
    }

    public HelperContextImpl getParent() {
        return _parentContext;
    }

    public Object project(Object o) {
        if (!(o instanceof DataObjectDecorator)) {
            return o;
        }
        return ((DataObjectDecorator)o).getInstance().project(this);
    }
    public void setMappingStrategyProperty(Property p) {
        _mappingStrategyProperty = p;
    }

    public Property getMappingStrategyProperty() {
        return _mappingStrategyProperty;
    }

    public boolean isAssignableContext(HelperContext assignableFrom) {
        if (this == assignableFrom) {
            return true;
        }
        SapHelperContext parent = ((SapHelperContext)assignableFrom).getParent();
        if (parent == null) {
            return false;
        }
        return isAssignableContext(parent);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapHelperContext#getContextOption(java.lang.String)
     */
    public Object getContextOption(String key) {
        return _options.get(key);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapHelperContext#setContextOption(java.lang.String, java.lang.Object)
     */
    public void setContextOption(String key, Object value) {
        if (_options.containsKey(key)) {
            throw new IllegalStateException("Option '" + key + "' was already set!");
        }
        _options.put(key, value);
    }
}
