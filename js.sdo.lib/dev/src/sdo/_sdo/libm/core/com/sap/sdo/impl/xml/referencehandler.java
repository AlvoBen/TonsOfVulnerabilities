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
package com.sap.sdo.impl.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.DataObjectType;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.impl.xml.stream.adapters.impl.AbstractElementAdapter;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public abstract class ReferenceHandler {
    private final DataObject _rootObject;
    private final String _rootElementUri;
    private final String _rootElementName;
    private final OrphanHandler _orphanHandler;

    protected ReferenceHandler(XMLDocument pXmlDocument, OrphanHandler pOrphanHandler) {
        _rootObject = pXmlDocument.getRootObject();
        _rootElementUri = pXmlDocument.getRootElementURI();
        _rootElementName = pXmlDocument.getRootElementName();
        _orphanHandler = pOrphanHandler;
    }

    protected ReferenceHandler(AbstractElementAdapter pAbstractElementAdapter, OrphanHandler pOrphanHandler) {
        _rootObject = pAbstractElementAdapter.getDataObject();
        _rootElementUri = pAbstractElementAdapter.getNamespaceURI();
        _rootElementName = pAbstractElementAdapter.getLocalName();
        _orphanHandler = pOrphanHandler;
    }

    public String generateReference(
        DataObject pDataObject,
        ChangeSummary pChangeSummary) throws XMLStreamException {
        return generateReference(pDataObject, pChangeSummary, true);
    }


    public String generateReference(
        DataObject pDataObject,
        ChangeSummary pChangeSummary,
        boolean pAllowKeys) throws XMLStreamException {

        SdoType<Object> type = (SdoType<Object>)pDataObject.getType();
        Type keyType = type.getKeyType();
        if (keyType != null && keyType.isDataType()
                && (pAllowKeys || DataObjectType.getInstance() == type.getTypeForKeyUniqueness())) {
            Object key = DataObjectBehavior.getKey(pDataObject);
            if (key != null) {
                return (String)key;
            }
        }
        String path = generatePath(pDataObject, pChangeSummary);
        if (path.charAt(0) == '~') {
            DataObject container = _orphanHandler.getOrphanContainer(pDataObject);
            if (container != null) {
                for (Entry<OrphanHolder,List<DataObject>> entry : _orphanHandler.getHolderContent().entrySet()) {
                    int index = entry.getValue().indexOf(container);
                    if (index != -1) {
                        StringBuilder orphanPath =
                            new StringBuilder(generatePath(entry.getKey().getDataObject(), null));
                        orphanPath.append('/');
                        orphanPath.append(checkPrefix(entry.getKey().getProperty().getUri()));
                        orphanPath.append(entry.getKey().getProperty().getXmlName());
                        orphanPath.append('.');
                        orphanPath.append(index);
                        if (path.length() > 1) {
                            orphanPath.append('/');
                            orphanPath.append(path.substring(1));
                        }
                        return orphanPath.toString();
                    }
                }
            }
            return "";
        }
        return path;
    }

    private String generatePath(
        DataObject pDataObject,
        ChangeSummary pChangeSummary) throws XMLStreamException {

        final StringBuilder buf = new StringBuilder();
        final GenericDataObject gdo = ((DataObjectDecorator)pDataObject).getInstance();
        DataObject container = gdo.getContainer();
        if (pDataObject.equals(_rootObject)) {
            buf.append('#');
            if (!URINamePair.DATAGRAPH_TYPE.equalsUriName(gdo.getType())) {
                buf.append('/');
                buf.append(checkPrefix(_rootElementUri));
                buf.append(_rootElementName);
            }
        } else if (container != null) {
            buf.append(generatePath(container, pChangeSummary));
            buf.append('/');
            SdoProperty containmentProperty = (SdoProperty)gdo.getContainmentProperty();
            buf.append(checkPrefix(containmentProperty.getUri()));
            buf.append(containmentProperty.getXmlName());
            if (containmentProperty.isMany()
                    && !(containmentProperty.isOpenContent()
                        && container.getList(containmentProperty).size() == 1)) {
                buf.append(
                    generateIndex(
                        pDataObject,
                        container.getList(containmentProperty)));
            }
        } else if ((pChangeSummary != null) && pChangeSummary.isDeleted(pDataObject)) {
            List<DataObjectDecorator> changed = pChangeSummary.getChangedDataObjects();
            DataObject root = pChangeSummary.getRootObject();
            SdoType rootType = (SdoType)root.getType();
            int csIndex = rootType.getCsPropertyIndex();
            if (csIndex == -1) {
                root = (DataObject)pChangeSummary.getDataGraph();
                rootType = (SdoType)root.getType();
                csIndex = rootType.getCsPropertyIndex();
            }
            buf.append(generatePath(root, null));
            buf.append('/');
            SdoProperty sdoProperty = (SdoProperty)rootType.getProperties().get(csIndex);
            buf.append(checkPrefix(sdoProperty.getUri()));
            buf.append(sdoProperty.getXmlName());
            buf.append('/');
            buf.append(generateChangeSummarySubPath(pDataObject, pChangeSummary, gdo, changed));
        } else {
            buf.append('~');
        }
        return buf.toString();
    }

    /**
     * @param pDataObject
     * @param pChangeSummary
     * @param gdo
     * @param changed
     * @throws XMLStreamException
     */
    private String generateChangeSummarySubPath(
        DataObject pDataObject,
        ChangeSummary pChangeSummary,
        final GenericDataObject gdo,
        List<DataObjectDecorator> changed) throws XMLStreamException {

        final StringBuilder buf = new StringBuilder();
        final Map<Property,Integer> key = new HashMap<Property,Integer>();
        for (DataObjectDecorator decorator : changed) {
            GenericDataObject oldGdo = decorator.getInstance();
            if (pChangeSummary.isModified(decorator)) {
                if (DataObjectBehavior.isOrphan(oldGdo, pChangeSummary)) {
                    trackIndexForOrphans(oldGdo, key);
                }
                List<Setting> oldValues = pChangeSummary.getOldValues(decorator);
                for (Setting setting : oldValues) {
                    Property prop = setting.getProperty();
                    if (prop.isContainment()
                            && ((prop.isMany() && ((List)setting.getValue()).contains(pDataObject))
                                    || (!prop.isMany() && pDataObject.equals(setting.getValue())))) {
                        String elementUri;
                        String elementName;
                        if (decorator == _rootObject) {
                            elementUri = _rootElementUri;
                            elementName = _rootElementName;
                        } else {
                            elementUri =
                                oldGdo.getOldContainmentPropValue().getProperty().getUri();
                            elementName =
                                oldGdo.getOldContainmentPropValue().getProperty().getXmlName();
                        }
                        buf.append(checkPrefix(elementUri));
                        buf.append(elementName);
                        List<DataObjectDecorator> decorators = new ArrayList<DataObjectDecorator>();
                        for (DataObjectDecorator decorator2 : changed) {
                            if (decorator == decorator2) {
                                decorators.add(decorator2);
                                break;
                            } else if (elementName.equals(
                                    decorator2.getInstance().getOldContainmentPropValue().getProperty().getXmlName())) {
                                decorators.add(decorator2);
                            }
                        }
                        buf.append(generateIndex(decorator, decorators));
                        buf.append('/');
                        PropValue<?> oldContainmentPropValue = gdo.getOldContainmentPropValue();
                        buf.append(checkPrefix(oldContainmentPropValue.getProperty().getUri()));
                        buf.append(oldContainmentPropValue.getProperty().getXmlName());
                        if (oldContainmentPropValue.isMany()) {
                            buf.append(
                                generateIndex(
                                    pDataObject,
                                    (List)oldContainmentPropValue.getOldValue()));
                        }
                        return buf.toString();
                    }
                }
            } else if (pChangeSummary.isDeleted(decorator)) {
                if (pDataObject.equals(decorator)) {
                    // deleted orphan
                    OrphanHolder orphanHolder = _orphanHandler.getOrphanHolder(decorator);
                    if (orphanHolder != null) {
                        SdoProperty prop = orphanHolder.getProperty();
                        buf.append(checkPrefix(prop.getUri()));
                        buf.append(prop.getXmlName());
                        buf.append('.');
                        Integer index = key.get(prop);
                        if (index == null) {
                            index = 0;
                        }
                        buf.append(index);
                        key.put(prop, ++index);
                        return buf.toString();
                    }
                } else {
                    if (DataObjectBehavior.isOrphan(oldGdo, pChangeSummary)) {
                        trackIndexForOrphans(oldGdo, key);
                    }
                }
            }
        }
        return "";
    }

    private void trackIndexForOrphans(GenericDataObject oldGdo, Map<Property, Integer> orphanKey) {
        if (oldGdo.getOldContainmentPropValue() == null) {
            OrphanHolder orphanHolder = _orphanHandler.getOrphanHolder(oldGdo);
            if (orphanHolder != null) {
                Integer index = orphanKey.get(orphanHolder.getProperty());
                if (index == null) {
                    index = 0;
                }
                orphanKey.put(orphanHolder.getProperty(), ++index);
            }
        }
    }

    /**
     * @param pDataObject
     * @param gdo
     */
    private String generateIndex(final DataObject pDataObject, final List pValues) {
        final StringBuilder buf = new StringBuilder();
        int index = pValues.indexOf(pDataObject);
        buf.append('.');
        buf.append(index > 0 ? String.valueOf(index) : '0');
        return buf.toString();
    }

    public abstract String checkPrefix(String uri) throws XMLStreamException;
}
