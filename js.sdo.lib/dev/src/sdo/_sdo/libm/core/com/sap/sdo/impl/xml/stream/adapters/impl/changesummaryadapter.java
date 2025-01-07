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

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.impl.xml.OrphanHolder;
import com.sap.sdo.impl.xml.stream.SdoNamespaceContext;
import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class ChangeSummaryAdapter extends AbstractElementAdapter implements ElementAdapter {

    private final List<DataObjectDecorator> _changedData = new ArrayList<DataObjectDecorator>();
    private ChangeSummary _cs;
    private int _index = 0;

    /**
     * @param pAdapterPool
     */
    protected ChangeSummaryAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    /**
     * @param prop
     * @param gdo
     * @param parent
     * @param ctx
     */
    protected void fillIn(
        SdoProperty prop,
        ChangeSummary cs,
        AbstractElementAdapter parent,
        HelperContext ctx) {

        super.fillIn(prop.getUri(), prop.getXmlName(), null, null, parent, ctx);
        _cs = cs;
        init();
    }

    @Override
    public void clear() {
        super.clear();
        _changedData.clear();
        _cs = null;
        _index = 0;
        _adapterPool.returnAdapter(this);
    }

    private void init() {
        final StringBuilder created = new StringBuilder();
        final StringBuilder deleted = new StringBuilder();

        try {
            for (DataObjectDecorator decorator : (List<DataObjectDecorator>)_cs.getChangedDataObjects()) {
                if (_cs.isCreated(decorator)) {
                    created.append(getReferenceHandler().generateReference(decorator, _cs, false));
                    created.append(' ');
                } else if (_cs.isDeleted(decorator)) {
                    DataObject container = decorator.getContainer();
                    if (container == null || !_cs.isDeleted(container)) {
                        deleted.append(getReferenceHandler().generateReference(decorator, _cs, false));
                        deleted.append(' ');
                    }
                    if (DataObjectBehavior.isOrphan(decorator, _cs)) {
                        // deleted orphan
                        OrphanHolder orphanHolder = getOrphanHandler().getOrphanHolder(decorator);
                        if (orphanHolder != null) {
                            _changedData.add(decorator.getInstance().getOldStateFacade());
                        }
                    }
                } else {
                    // modified
                    _changedData.add(decorator);
                }
            }
        } catch (XMLStreamException ex) {
            throw new IllegalArgumentException(ex);
        }

        if (created.length() > 0) {
            addAttribute(null, "create", created.toString().trim());
        }
        if (deleted.length() > 0) {
            addAttribute(null, "delete", deleted.toString().trim());
        }
        if (!_cs.isLogging()) {
            addAttribute(null, "logging", "false");
        }
        SdoNamespaceContext nsCtx = getNamespaceContext();
        if (nsCtx.getPrefix(URINamePair.DATATYPE_URI) == null) {
            nsCtx.addPrefix(PREFIX_SDO, URINamePair.DATATYPE_URI);
        }
    }



    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#hasChild()
     */
    public boolean hasChild() {
        return _index < _changedData.size();
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#nextChild()
     */
    public ElementAdapter nextChild() {
        DataObjectDecorator decorator = _changedData.get(_index++);
        String elementName = null;
        String elementUri = null;
        if (DataObjectBehavior.isOrphan(decorator, _cs)) {
            OrphanHolder orphanHolder = getOrphanHandler().getOrphanHolder(decorator);
            if (orphanHolder != null) {
                elementName = orphanHolder.getProperty().getXmlName();
                elementUri = orphanHolder.getProperty().getUri();
            }
        } else {
            SdoProperty containmentProperty = (SdoProperty)decorator.getContainmentProperty();
            if (containmentProperty != null) {
                elementName = containmentProperty.getXmlName();
                elementUri = containmentProperty.getUri();
            }
        }
        if (elementName == null) {
            AbstractElementAdapter rootElement = getRootElement();
            elementName = rootElement.getLocalName();
            elementUri = rootElement.getNamespaceURI();
        }
        if (_cs.isModified(decorator)) {
            ChangedElementAdapter changed = _adapterPool.getChangedElementAdapter();
            changed.fillIn(elementUri, elementName, _cs, decorator, this, _helperContext);
            return changed;
        } else {
            if (decorator.getType().isSequenced()) {
                SequencedElementAdapter orphan = _adapterPool.getSequencedElementAdapter();
                orphan.fillIn(elementUri, elementName, decorator.getInstance(), this, _helperContext);
                return orphan;
            } else {
                NonSequencedElementAdapter orphan = _adapterPool.getNonSequencedElementAdapter();
                orphan.fillIn(elementUri, elementName, decorator.getInstance(), this, _helperContext);
                return orphan;
            }
        }
    }

}
