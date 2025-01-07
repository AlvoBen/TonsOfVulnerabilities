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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.builtin.TypeType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class OrphanHandler {
    private final DataObject _rootObject;

    private boolean _processed = false;
    private OrphanHolder _orphanHolderAnyType = null;
    private final List<DataObject> _registered = new ArrayList<DataObject>();
    private final Map<URINamePair, OrphanHolder> _orphanHolders = new HashMap<URINamePair, OrphanHolder>();
    private final Map<DataObject, DataObject> _orphans = new LinkedHashMap<DataObject, DataObject>();
    private final Map<OrphanHolder, List<DataObject>> _orphanHolderContent = new HashMap<OrphanHolder, List<DataObject>>();


    /**
     * @param pRootObject
     */
    public OrphanHandler(DataObject pRootObject) {
        super();
        if (pRootObject != null && pRootObject instanceof DataObjectDecorator) {
            _rootObject = ((DataObjectDecorator)pRootObject).getInstance();
        } else {
            _rootObject = pRootObject;
        }
    }

    public DataObject getOrphanContainer(DataObject pDataObject) {
        if (!_processed) {
            processOrphans();
        }
        return _orphans.get(pDataObject);
    }

    public Map<OrphanHolder,List<DataObject>> getHolderContent() {
        if (!_processed) {
            processOrphans();
        }
        return _orphanHolderContent;
    }

    /**
     * @param pGdo
     * @param pElement
     * @return
     */
    public List<DataObject> getOrphanList(GenericDataObject pGdo, SdoProperty pElement) {
        if (!_processed) {
            processOrphans();
        }
        return _orphanHolderContent.get(new OrphanHolder(pGdo, pElement));
    }

    /**
     */
    private void processOrphans() {
        _processed = true;
        traverseForOrphans(_rootObject);

        if (_orphanHolderAnyType != null) {
            _orphanHolderContent.put(
                _orphanHolderAnyType,
                new ArrayList<DataObject>(new LinkedHashSet<DataObject>(_orphans.values())));
        } else {
            Set<DataObject> containers = new LinkedHashSet<DataObject>(_orphans.values());
            for (DataObject container : containers) {
                OrphanHolder orphanHolder = findOrphanHolder(container.getType());
                if (orphanHolder != null) {
                    List<DataObject> content = _orphanHolderContent.get(orphanHolder);
                    if (content == null) {
                        content = new ArrayList<DataObject>();
                        _orphanHolderContent.put(orphanHolder, content);
                    }
                    content.add(container);
                }
            }
        }
        _registered.clear();
    }

    private OrphanHolder findOrphanHolder(Type type) {
        OrphanHolder orphanHolder = _orphanHolders.get(URINamePair.fromType(type));
        if (orphanHolder != null) {
            return orphanHolder;
        }
        List<Type> baseTypes = type.getBaseTypes();
        for (int i=0; i<baseTypes.size(); ++i) {
            orphanHolder = findOrphanHolder(baseTypes.get(i));
            if (orphanHolder != null) {
                break;
            }
        }
        return orphanHolder;
    }

    public OrphanHolder getOrphanHolder(DataObject pOrphan) {
        if (!_processed) {
            processOrphans();
        }
        if (_orphanHolderAnyType != null) {
            return _orphanHolderAnyType;
        } else {
            return findOrphanHolder(pOrphan.getType());
        }
    }

    private void traverseForOrphans(DataObject dataObject) {
        if (dataObject != null) {
            List<SdoProperty> properties = dataObject.getInstanceProperties();
            int size = properties.size();
            for (int i=0; i<size; ++i) {
                SdoProperty prop = properties.get(i);
                Type type = prop.getType();
                if (_orphanHolderAnyType == null && prop.isOrphanHolder()) {
                    // orphan holder
                    if (URINamePair.DATAOBJECT.equalsUriName(type)) {
                        _orphanHolderAnyType =  new OrphanHolder(dataObject, prop);
                        _orphanHolders.clear();
                    } else {
                        _orphanHolders.put(
                            URINamePair.fromType(type),
                            new OrphanHolder(dataObject, prop));
                    }
                }
                if (!type.isDataType() && dataObject.isSet(prop) && type != TypeType.getInstance()) {
                    if (!prop.isContainment()) {
                        // orphans
                        if (prop.isMany()) {
                            List<DataObject> data = dataObject.getList(prop);
                            for (int j=0; j<data.size(); ++j) {
                                registerOrphan(data.get(j));
                            }
                        } else {
                            registerOrphan(dataObject.getDataObject(prop));
                        }
                    } else if (prop.isXmlElement()) {
                        // recursion
                        if (prop.isMany()) {
                            List<DataObject> data = dataObject.getList(prop);
                            for (int j=0; j<data.size(); ++j) {
                                traverseForOrphans(data.get(j));
                            }
                        } else {
                            traverseForOrphans(dataObject.getDataObject(prop));
                        }
                    }
                }
            }
        }
    }

    /**
     * @param dataObject
     */
    private void registerOrphan(DataObject dataObject) {
        if (dataObject == null || _registered.contains(dataObject)) {
            return;
        }
        _registered.add(dataObject);

        List<DataObjectDecorator> path = new ArrayList<DataObjectDecorator>();
        DataObjectDecorator container = (DataObjectDecorator)dataObject.getContainer();
        while (container != null) {
            if (container.getInstance() == _rootObject) {
                // it's not an orphan
                return;
            }
            path.add(container);
            container = (DataObjectDecorator)container.getContainer();
        }
        if (!path.isEmpty()) {
            container = path.get(path.size()-1);
        }
        _orphans.put(dataObject, container != null ? container : dataObject);
        for (DataObjectDecorator orphan : path) {
            _registered.add(orphan);
            _orphans.put(orphan, container);
        }

        traverseForOrphans(dataObject);
    }
}
