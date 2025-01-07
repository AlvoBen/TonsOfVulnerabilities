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
package com.sap.sdo.impl.xml.stream.adapters.impl;

import javax.xml.stream.XMLStreamException;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class ReferencedElementAdapter extends AbstractElementAdapter implements ElementAdapter {

    /**
     * @param pAdapterPool
     */
    protected ReferencedElementAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    /**
     * @param uri
     * @param name
     * @param dataObject
     * @param value
     * @param parent
     * @param context
     */
    protected void fillIn(
        SdoProperty prop,
        DataObject dataObject,
        ChangeSummary changeSummary,
        AbstractElementAdapter parent,
        HelperContext context) {

        super.fillIn(prop.getUri(), prop.getXmlName(), null, null, parent, context);
        try {
            addAttribute(
                URINamePair.DATATYPE_URI, "ref",
                getReferenceHandler().generateReference(dataObject, changeSummary));
        } catch (XMLStreamException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public void clear() {
        super.clear();
        _adapterPool.returnAdapter(this);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#hasChild()
     */
    public boolean hasChild() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#nextChild()
     */
    public ElementAdapter nextChild() {
        return null;
    }


}
