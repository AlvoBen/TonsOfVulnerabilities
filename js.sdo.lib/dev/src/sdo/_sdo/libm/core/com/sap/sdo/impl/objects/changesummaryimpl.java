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
package com.sap.sdo.impl.objects;

import static com.sap.sdo.impl.objects.GenericDataObject.UNSET;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sap.sdo.impl.objects.DataStrategy.State;
import com.sap.sdo.impl.objects.strategy.ReadOnlySequence;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.ChangeSummaryType;
import com.sap.sdo.impl.types.builtin.DataGraphType.XsdType;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.impl.xml.OrphanHandler;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;

public class ChangeSummaryImpl implements ChangeSummary, Serializable {

    private static final long serialVersionUID = 5050026125388598184L;
    private DataObjectDecorator _loggingRoot;
    private boolean _orphanHolder;
    private LoggingState _enabled = LoggingState.NEW;
    final private Set<DataObjectDecorator> _initialOrphans = new LinkedHashSet<DataObjectDecorator>();
    private List<DataObjectDecorator> _changedObjects = null;

    private enum LoggingState {NEW, LOGGING, ENDLOGGING}

    public ChangeSummaryImpl(DataGraph pDataGraph) {
        _loggingRoot = (DataObjectDecorator)pDataGraph.getRootObject();
        _orphanHolder = true;
    }

    public ChangeSummaryImpl(DataObjectDecorator pLoggingRoot) {
        _loggingRoot = pLoggingRoot;
        _orphanHolder =
            !((SdoType)pLoggingRoot.getType()).getOrphanHolderProperties().isEmpty();
        //TODO check this
//        DataObjectDecorator rootObject = (DataObjectDecorator)_loggingRoot.getRootObject();
//        if (rootObject.getDataGraph() != null) {
//            throw new IllegalStateException("DataObject has already a DataGraph");
//        }
//        rootObject.getInstance().setParent(this);
    }

    public boolean isLogging() {
        return _enabled == LoggingState.LOGGING;
    }

    public DataGraph getDataGraph() {
        return _loggingRoot.getDataGraph();
    }

    public List<DataObjectDecorator> getChangedDataObjects() {
        if (_changedObjects != null) {
            return _changedObjects;
        }
        return retrieveChangedDataObjects();
    }

    private List<DataObjectDecorator> retrieveChangedDataObjects() {
        List<DataObjectDecorator> changedDataObjects = new ArrayList<DataObjectDecorator>();
        if (_enabled != LoggingState.NEW) {
            fillChangedDataObjects((DataObjectDecorator)getRootObject(), changedDataObjects);
            fillDeletedDataObjects((DataObjectDecorator)getRootObject(), changedDataObjects);
            fillChangedOrphans((DataObjectDecorator)getRootObject(), changedDataObjects);
        }
        return changedDataObjects;
    }

    private void fillChangedDataObjects(DataObjectDecorator pDataObject, List<DataObjectDecorator> pChangedDataObjects) {
        fillChangedDataObjects(pDataObject, pChangedDataObjects, true);
    }

    private void fillChangedDataObjects(
        DataObjectDecorator pDataObject,
        List<DataObjectDecorator> pChangedDataObjects,
        boolean pContainmentTree) {

        if (pChangedDataObjects.contains(pDataObject)) {
            return;
        }
        GenericDataObject dataObject = pDataObject.getInstance();
        if (pContainmentTree && dataObject.getChangeSummary() != this) {
            return;
        }
        if (dataObject.isCreated() || dataObject.isModified()) {
            pChangedDataObjects.add(pDataObject);
        }
        List<Property> properties = dataObject.getInstanceProperties();
        int size = properties.size();
        for (int i = 0; i < size; i++) {
            Property property = properties.get(i);
            if (property.isContainment() && !property.getType().isDataType()) {
                PropValue<?> propValue = dataObject.getPropValue(i, false);
                if ((propValue != null) && propValue.isSet()) {
                    if (propValue.isMany()) {
                        for (DataObjectDecorator child: (List<DataObjectDecorator>)propValue.getValue()) {
                            fillChangedDataObjects(child, pChangedDataObjects, pContainmentTree);
                        }
                    } else if (propValue.getValue() instanceof DataObjectDecorator){
                        fillChangedDataObjects(
                            (DataObjectDecorator)propValue.getValue(),
                            pChangedDataObjects,
                            pContainmentTree);
                    }
                }
            }
        }
    }

    private void fillDeletedDataObjects(DataObjectDecorator pDataObject, List<DataObjectDecorator> pChangedDataObjects) {
        GenericDataObject dataObject = pDataObject.getInstance();
        DataObjectDecorator oldStateDataObject = dataObject.getOldStateFacade();
        if (dataObject.isDeleted() && !pChangedDataObjects.contains(dataObject)) {
            pChangedDataObjects.add(oldStateDataObject);
        }

        List<SdoProperty> properties = oldStateDataObject.getInstanceProperties();
        for (SdoProperty property: properties) {
            if (property.isContainment() && !property.getType().isDataType()
                    && !property.isOrphanHolder()
                    && property.getType()!=ChangeSummaryType.getInstance()
                    && property.getType()!=XsdType.getInstance()) {
                if (property.isMany()) {
                    List<DataObjectDecorator> oldValue = oldStateDataObject.getList(property);
                    for (DataObjectDecorator child: oldValue) {
                        fillDeletedDataObjects(child, pChangedDataObjects);
                    }
                } else {
                    DataObjectDecorator oldValue = (DataObjectDecorator)oldStateDataObject.getDataObject(property);
                    if (oldValue != null) {
                        fillDeletedDataObjects(oldValue, pChangedDataObjects);
                    }
                }
            }
        }
    }

    private void fillChangedOrphans(DataObjectDecorator pDataObject, List<DataObjectDecorator> pChangedDataObjects) {
        if (!_orphanHolder) {
            return;
        }
        Set<DataObjectDecorator> originalOrphans = new LinkedHashSet<DataObjectDecorator>(_initialOrphans);
        Set<DataObject> currentOrphans = new HashSet<DataObject>();
        OrphanHandler orphanHandler = new OrphanHandler(pDataObject);
        Collection<List<DataObject>> orphanListCollection = orphanHandler.getHolderContent().values();
        for (List<DataObject> orphanList : orphanListCollection) {
            currentOrphans.addAll(orphanList);
            for (int i=0; i<orphanList.size(); ++i) {
                DataObjectDecorator orphan = (DataObjectDecorator)orphanList.get(i);
                GenericDataObject gdo = orphan.getInstance();
                if (gdo.isModified() || gdo.isCreated()) {
                    pChangedDataObjects.add(orphan);
                } else if (gdo.isDeleted()) {
                    // CREATE after DELETED results in MODIFIED
                    orphan.getInstance().setChangeStateWithoutCheck(State.CREATED);
                    pChangedDataObjects.add(orphan);
                } else if (!gdo.isLogging()){
                    orphan.getInstance().setChangeStateWithoutCheck(State.CREATED);
                    pChangedDataObjects.add(orphan);
                }
                List<Property> properties = gdo.getInstanceProperties();
                int size = properties.size();
                for (int j = 0; j < size; j++) {
                    SdoProperty property = (SdoProperty)properties.get(j);
                    if (property.isContainment() && !property.getType().isDataType()) {
                        PropValue<?> propValue = gdo.getPropValue(j, false);
                        if ((propValue != null) && propValue.isSet()) {
                            if (propValue.isMany()) {
                                for (DataObjectDecorator child: (List<DataObjectDecorator>)propValue.getValue()) {
                                    fillChangedDataObjects(child, pChangedDataObjects, false);
                                }
                            } else if (propValue.getValue() instanceof DataObjectDecorator){
                                fillChangedDataObjects(
                                    (DataObjectDecorator)propValue.getValue(),
                                    pChangedDataObjects,
                                    false);
                            }
                        }
                        if (!property.isOrphanHolder()
                                && property.getType()!=ChangeSummaryType.getInstance()
                                && property.getType()!=XsdType.getInstance()) {
                            if (property.isMany()) {
                                for (DataObjectDecorator child: (List<DataObjectDecorator>)gdo.getList(property)) {
                                    fillDeletedDataObjects(child, pChangedDataObjects);
                                }
                            } else {
                                DataObjectDecorator oldValue = (DataObjectDecorator)gdo.getDataObject(property);
                                if (oldValue != null) {
                                    fillDeletedDataObjects(oldValue, pChangedDataObjects);
                                }
                            }
                        }
                    }
                }
            }
        }
        originalOrphans.removeAll(currentOrphans);
        // deleted orphans
        for (DataObjectDecorator orphan : originalOrphans) {
            if (DataObjectBehavior.isOrphan(orphan, this) && !isContainedByOrphan(orphan, currentOrphans)) {
                orphan.getInstance().setChangeStateWithoutCheck(State.DELETED);
                pChangedDataObjects.add(orphan.getInstance().getOldStateFacade());
            }
        }
    }

    private boolean isContainedByOrphan(DataObjectDecorator orphan, Set<DataObject> currentOrphans) {
        DataObject container = orphan.getContainer();
        if (container != null) {
            GenericDataObject decorator = ((DataObjectDecorator)container).getInstance();
            if (currentOrphans.contains(decorator)) {
                return true;
            }
            return isContainedByOrphan(decorator, currentOrphans);
        }
        return false;
    }

    public boolean isCreated(DataObject pDataObject) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        return dataObject.isCreated();
    }

    public boolean isDeleted(DataObject pDataObject) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        return dataObject.isDeleted();
    }

    public List<Setting> getOldValues(DataObject pDataObject) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        List<Property> oldProperties = dataObject.getOldChangedProperties();
        List<Setting> settings = new ArrayList<Setting>(oldProperties.size());
        for (Property property: oldProperties) {
            Setting setting = getOldValue(dataObject, property);
            if (setting == null) {
                throw new NullPointerException("Null setting:" + property.getName());
            }
            settings.add(setting);
        }
        return settings;
    }

    public void beginLogging() {
        if(_enabled != LoggingState.LOGGING) {
            resetChangeSummary();
            _enabled = LoggingState.LOGGING;
            _changedObjects = null;
            calculateOrphans();
            setScope(_loggingRoot.getInstance(), _orphanHolder, new HashSet<GenericDataObject>());
        }
    }

    public void beginLoggingWithoutCheck() {
        _enabled = LoggingState.LOGGING;
        _changedObjects = null;
        calculateOrphans();
        setScope(_loggingRoot.getInstance(), _orphanHolder, new HashSet<GenericDataObject>());
    }

    private void calculateOrphans() {
        _initialOrphans.clear();
        OrphanHandler orphanHandler = new OrphanHandler(_loggingRoot);
        Collection<List<DataObject>> orphans = orphanHandler.getHolderContent().values();
        for (List<DataObject> list : orphans) {
            _initialOrphans.addAll((List)list);
        }
    }

    private void setScope(GenericDataObject pDataObject, boolean pOrphan, Set<GenericDataObject> pGraph) {
        if (pGraph.contains(pDataObject)) {
            return;
        }

        pGraph.add(pDataObject);
        pDataObject.getDataStrategy().setInitalScope(true);
        List<Property> props = pDataObject.getInstanceProperties();
        for (int i=0; i<props.size(); ++i) {
            SdoProperty property = (SdoProperty)props.get(i);
            if (!property.getType().isDataType() && pDataObject.isSet(property)
                    && (pOrphan || property.isContainment())) {
                if (property.isMany()) {
                    List<DataObjectDecorator> values = pDataObject.getList(property);
                    for (int j=0; j<values.size(); ++j) {
                        setScope(values.get(j).getInstance(), pOrphan, pGraph);
                    }
                } else {
                    setScope(
                        ((DataObjectDecorator)pDataObject.getDataObject(property)).getInstance(),
                        pOrphan,
                        pGraph);
                }
            }
        }
    }

    private void resetChangeSummary() {
        List<DataObjectDecorator> changedDataObjects = getChangedDataObjects();
        for (DataObjectDecorator dataObject: changedDataObjects) {
            dataObject.getInstance().resetChangeSummary();
        }
    }

    public void endLogging() {
        _enabled = LoggingState.ENDLOGGING;
        _changedObjects  = retrieveChangedDataObjects();
    }

    public boolean isModified(DataObject pDataObject) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        return dataObject.isModified();
    }

    public DataObject getRootObject() {
        return _loggingRoot;
    }

    public Setting getOldValue(DataObject pDataObject, Property pProperty) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        return dataObject.getOldValue(pProperty);
    }

    public DataObject getOldContainer(DataObject pDataObject) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        PropValue<?> containmentPropValue = dataObject.getOldContainmentPropValue();
        if (containmentPropValue == null) {
            return null;
        }
        return containmentPropValue.getDataObject();
    }

    public Property getOldContainmentProperty(DataObject pDataObject) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        PropValue<?> containmentPropValue = dataObject.getOldContainmentPropValue();
        if (containmentPropValue == null) {
            return null;
        }
        return containmentPropValue.getProperty();
    }

    public Sequence getOldSequence(DataObject pDataObject) {
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        return dataObject.getOldSequence();
    }

    public void undoChanges() {
        undoChanges(getRootObject(), new HashSet<DataObject>());
        _changedObjects = null;
        calculateOrphans();
        setScope(_loggingRoot.getInstance(), _orphanHolder, new HashSet<GenericDataObject>());
    }

    public void undoChanges(DataObject pDataObject, Set<DataObject>pTouched) {
        if ((pDataObject == null) || pTouched.contains(pDataObject)) {
            return;
        }
        pTouched.add(pDataObject);
        GenericDataObject dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        //for very freaky trees, if pDataObject is not reached by containment:
        DataObject oldContainer = getOldContainer(dataObject);
        boolean containerWasCreated = oldContainer != null && isCreated(oldContainer);
        undoChanges(oldContainer, pTouched);

        // reset the old container
        if (oldContainer == dataObject.getContainer() && containerWasCreated && !dataObject.isCreated()) {
            oldContainer = null;
        }
        dataObject.setContainerWithoutCheck(oldContainer, getOldContainmentProperty(dataObject));

        //xsi:nil doesn't survive ChangeSummary
        dataObject.setXsiNilWithoutCheck(null);
        // reset the old sequence
        Sequence oldSequence = getOldSequence(dataObject);
        if (oldSequence != null) {
            dataObject.setSequenceWithoutCheck(((ReadOnlySequence)oldSequence).getSequenceAsList());
        }

        // reset the old non sequenced properties
        List<Setting> oldPropertySettings = getOldValues(dataObject);
        for (Setting oldSetting: oldPropertySettings) {
            SdoProperty property = (SdoProperty)oldSetting.getProperty();
            if (oldSequence == null || !property.isXmlElement()) {
                if (oldSetting.isSet()) {
                    dataObject.setPropertyWithoutCheck(property, oldSetting.getValue());
                } else {
                    dataObject.setPropertyWithoutCheck(property, UNSET);
                }
            }
        }

        // reset the referenced DataObjects
        List<Property> properties = dataObject.getInstanceProperties();
        int size = properties.size();
        for (int i = 0; i < size; i++) {
            Property property = properties.get(i);
            if (!property.getType().isDataType()) {
                if (property.isMany()) {
                    List<DataObject> dataObjects = dataObject.getList(i);
                    for (DataObject childDataObject: dataObjects) {
                        undoChanges(childDataObject, pTouched);
                    }
                } else {
                    DataObject childDataObject = dataObject.getDataObject(i);
                    undoChanges(childDataObject, pTouched);
                }
            }
        }

        // remove open properties that were created in the meantime
        List<Property> oldProperties = dataObject.getOldInstanceProperties();
        int typePropSize = dataObject.getType().getProperties().size();
        List<Property> oldOpenProperties = oldProperties.subList(typePropSize, oldProperties.size());
        List<Property> removeOpenProperties = new ArrayList<Property>();
        for (int i = typePropSize; i < size; i++) {
            Property property = properties.get(i);
// This doesn't work for any reason
//            if (!oldOpenProperties.contains(property)) {
//                removeOpenProperties.add(property);
//            }
            // begin workaround
            String propName = property.getName();
            boolean found = false;
            for (Property oldOpenProperty: oldOpenProperties) {
                if (propName.equals(oldOpenProperty.getName())) {
                    if (!oldOpenProperty.isMany()) {
                        found = true;
                    } else {
                        Setting setting = getOldValue(dataObject, oldOpenProperty);
                        if (setting == null || !((List<?>)setting.getValue()).isEmpty()) {
                            found = true;
                        }
                    }
                    break;
                }
            }
            if (!found) {
                removeOpenProperties.add(property);
            }
            // end workaround
        }
        for (Property property: removeOpenProperties) {
            dataObject.internUnset(property);
        }
        dataObject.resetChangeSummary();
    }

    /**
     * @param pValue
     */
    public void addDeletedOrphansWithoutCheck(List<DataObjectDecorator> pValue) {
        for (int i=0; i<pValue.size(); ++i) {
            _initialOrphans.add(pValue.get(i));
        }
    }

}