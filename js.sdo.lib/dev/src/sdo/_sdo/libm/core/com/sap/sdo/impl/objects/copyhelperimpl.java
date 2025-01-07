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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataStrategy.State;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.HelperContext;

public class CopyHelperImpl implements CopyHelper
{
    private static enum COPY_STRATEGY {COPY, REFERENCE, SKIP}

    private final HelperContext _helperContext;

    private CopyHelperImpl(HelperContext pHelperContext) {
        _helperContext = pHelperContext;
    }

    public static CopyHelper getInstance(HelperContext pHelperContext) {
        // to avoid illegal instances
        CopyHelper copyHelper = pHelperContext.getCopyHelper();
        if (copyHelper != null) {
            return copyHelper;
        }
        return new CopyHelperImpl(pHelperContext);
    }

    public DataObject copyShallow(DataObject pSource) {
        return copyDataObject(pSource, true);
    }

    public DataObject copy(DataObject pSource) {
        return copyDataObject(pSource, false);
    }

    public DataObject copyDataObject(DataObject pSource, boolean shallow) {
        Map<GenericDataObject, DataObjectDecorator> copies = null;
        if (!shallow) {
            copies = new HashMap<GenericDataObject, DataObjectDecorator>();
        }
        GenericDataObject source = ((DataObjectDecorator)pSource).getInstance();
        DataObjectDecorator target = copy(source, shallow, copies);
        //TODO copy ChangeSummary
//        ChangeSummaryResult changeSummaryResult = source.getInstance().getChangeSummaryResult();
//        if ((changeSummaryResult != null) && (changeSummaryResult.getChangeSummary() != null)) {
//            ChangeSummary changeSummary = changeSummaryResult.getChangeSummary();
//            DataObject loggingRoot = changeSummary.getRootObject();
//            DataObjectDecorator loggingRootCopy = (DataObjectDecorator)copies.get(loggingRoot);
//            if (loggingRootCopy == null) {
//                loggingRootCopy = target;
//            }
//            ChangeSummary changeSummaryCopy = new ChangeSummaryImpl(loggingRootCopy);
//            if (changeSummary.isLogging()) {
//                changeSummaryCopy.beginLogging();
//            }
//        }
        return target;
    }

    public Object copy(Object pSource, boolean shallow, Type pType, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        if (pSource == null) {
            return null;
        }
        if (pType.isDataType()) {
            return ((SdoType)pType).copy(pSource, shallow);
        }
        GenericDataObject source = ((DataObjectDecorator)pSource).getInstance();
        return copy(source, shallow, pCopies);
    }

    public DataObjectDecorator copy(GenericDataObject pSource, boolean pShallow, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        DataObjectDecorator target;
        if(!pShallow) {
            target = pCopies.get(pSource);
            if(target != null) {
                return target;
            }
        }
        target = (DataObjectDecorator)_helperContext.getDataFactory().create(pSource.getType());
        GenericDataObject targetGdo = target.getInstance();
        if (!pShallow) {
            pCopies.put(pSource, target);
        }
        copySequence(pSource, targetGdo, pShallow, pCopies);
        copyProperties(pSource, targetGdo, pShallow, pCopies);
        targetGdo.setXsiNilWithoutCheck(pSource.getXsiNil());
        if (!pShallow) {
            copyOldState(pSource, targetGdo, pCopies);
            Property containmentProperty = pSource.getContainmentProperty();
            DataObject container = pCopies.get(pSource.getContainer());
            targetGdo.setContainerWithoutCheck(container, containmentProperty);
        }
        return target;
    }

    private void copyOldState(GenericDataObject pSource, GenericDataObject pTarget, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        State changeState = pSource.getDataStrategy().getChangeState();
        if (changeState == State.UNCHANGED) {
            return;
        }
        pTarget.setChangeStateWithoutCheck(changeState);
        ChangeSummary changeSummary = pSource.getChangeSummary();
        boolean isSequenced = pSource.getType().isSequenced();
        if (isSequenced) {
            Sequence oldSequence = changeSummary.getOldSequence(pSource);
            for (int i = 0; i < oldSequence.size(); i++) {
                Property prop = oldSequence.getProperty(i);
                Type type = prop.getType();
                Object copy = copy(oldSequence.getValue(i), false, type, pCopies);
                pTarget.addToOldSequenceWithoutCheck(prop, copy);
            }
        }
        List<ChangeSummary.Setting> oldValues = changeSummary.getOldValues(pSource);
        for (ChangeSummary.Setting oldSetting: oldValues) {
            SdoProperty prop = (SdoProperty)oldSetting.getProperty();
            if (!isSequenced || !prop.isXmlElement()) {
                Type type = prop.getType();
                if (prop.isMany()) {
                    List sourceList = (List)oldSetting.getValue();
                    if (sourceList.isEmpty()) {
                        pTarget.setOldPropertyWithoutCheck(prop, Collections.emptyList());
                    } else {
                        for (Object sourceValue: sourceList) {
                            Object copy = copy(sourceValue, false, type, pCopies);
                            pTarget.addToOldPropertyWithoutCheck(prop, copy);
                        }
                    }
                } else {
                    Object copy;
                    if (oldSetting.isSet()) {
                        copy = copy(oldSetting.getValue(), false, type, pCopies);
                    } else {
                        copy = GenericDataObject.UNSET;
                    }
                    pTarget.setOldPropertyWithoutCheck(prop, copy);
                }
            }
        }
        DataObjectDecorator oldContainer = (DataObjectDecorator)changeSummary.getOldContainer(pSource);
        if (oldContainer != null) {
            DataObjectDecorator oldContainerCopy = pCopies.get(oldContainer.getInstance());
            if (oldContainerCopy != null) {
                pTarget.setOldContainerWithoutCheck(oldContainerCopy, changeSummary.getOldContainmentProperty(pSource));
            }
        }

    }

    private void copySequence(GenericDataObject pSource, GenericDataObject pTarget, boolean pShallow, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        Sequence sourceSequence = pSource.getSequence();
        ChangeSummary changeSummaryCopy = null;
        if (((SdoType)pTarget.getType()).getCsPropertyIndex() >= 0) {
            changeSummaryCopy = pTarget.getChangeSummary();
        }
        if (sourceSequence != null) {
            List<Setting> targetSequence = new ArrayList<Setting>(sourceSequence.size());
            for (int i = 0; i < sourceSequence.size(); i++) {
                Property property = sourceSequence.getProperty(i);
                Object sourceValue = sourceSequence.getValue(i);
                COPY_STRATEGY copyStrategy = getCopyStrategy(pShallow, property, sourceValue, pCopies);
                if (copyStrategy == COPY_STRATEGY.COPY) {
                    if (property == null) {
                        targetSequence.add(createSetting(null, sourceValue));
                    } else if (URINamePair.CHANGESUMMARY_TYPE.equalsUriName(property.getType())) {
                        copyChangeSummary((ChangeSummary)sourceValue, changeSummaryCopy);
                        targetSequence.add(createSetting(property, changeSummaryCopy));
                    } else {
                        Object targetValue = copy(sourceValue, pShallow, property.getType(), pCopies);
                        targetSequence.add(createSetting(property, targetValue));
                    }
                } else if (copyStrategy == COPY_STRATEGY.REFERENCE) {
                    targetSequence.add(createSetting(property, sourceValue));
                }
            }
            pTarget.getInstance().setSequenceWithoutCheck(targetSequence);
        }
    }

    private void copyProperties(GenericDataObject pSource, GenericDataObject pTarget, boolean pShallow, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        List<Property> props = pSource.getInstanceProperties();
        int size = props.size();
        for (int i = 0; i < size; i++) {
            SdoProperty property = (SdoProperty) props.get(i);
            if (((pSource.getSequence() == null) || !property.isXmlElement()) && pSource.isSet(i)) {
                Object sourceValue = pSource.get(i);
                COPY_STRATEGY copyStrategy = getCopyStrategy(pShallow, property, sourceValue, pCopies);
                if (copyStrategy != COPY_STRATEGY.SKIP) {
                    if (property.isMany()) {
                        List<Object> sourceList = (List<Object>)sourceValue;
                        List<Object> targetList = new ArrayList<Object>(sourceList.size());
                        for (Object singleSourceValue: sourceList) {
                            copyStrategy = getCopyStrategy(pShallow, property, singleSourceValue, pCopies);
                            if (copyStrategy == COPY_STRATEGY.COPY) {
                                Object singleTargetValue = copy(singleSourceValue, pShallow, property.getType(), pCopies);
                                targetList.add(singleTargetValue);
                            } else if (copyStrategy == COPY_STRATEGY.REFERENCE) {
                                targetList.add(singleSourceValue);
                            }
                        }
                        pTarget.getInstance().setPropertyWithoutCheck(property, targetList);
                    } else {
                        if (copyStrategy == COPY_STRATEGY.COPY) {
                            if (URINamePair.CHANGESUMMARY_TYPE.equalsUriName(property.getType())) {
                                copyChangeSummary((ChangeSummary)sourceValue, pTarget.getChangeSummary());
                            } else {
                                Object targetValue = copy(sourceValue, pShallow, property.getType(), pCopies);
                                pTarget.getInstance().setPropertyWithoutCheck(property, targetValue);
                            }
                        } else if (copyStrategy == COPY_STRATEGY.REFERENCE) {
                            pTarget.getInstance().setPropertyWithoutCheck(property, sourceValue);
                        }
                    }
                }
            }
        }
    }

    private void copyChangeSummary(ChangeSummary pSource, ChangeSummary pTarget) {
        if (pSource.isLogging()) {
            ((ChangeSummaryImpl)pTarget).beginLoggingWithoutCheck();
        }
    }

    private COPY_STRATEGY getCopyStrategy(boolean pShallow, Property pProperty, Object pValue, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        if (pProperty == null) { // The text property
            return COPY_STRATEGY.COPY;
        }
        if (pProperty.getType().isDataType()) {
            return COPY_STRATEGY.COPY;
        }
        if (pShallow) {
            return COPY_STRATEGY.SKIP;
        }
        if (pProperty.isContainment()) {
            return COPY_STRATEGY.COPY;
        }
        if (pProperty.isMany()) {
            // return a strategy != SKIP to analyse the single DataObjects later
            return COPY_STRATEGY.COPY;
        }
        if (URINamePair.CHANGESUMMARY_TYPE.equalsUriName(pProperty.getType())) {
            return COPY_STRATEGY.COPY;
        }
        if (isInCopyScope((DataObject)pValue, pCopies)) {
            return COPY_STRATEGY.COPY;
        }
        //TODO I don't like this check. Clarify if this is necessary.
        if (isOutsideContainmentTree((DataObject)pValue, pCopies)) {
            throw new IllegalArgumentException("Property value is not in containment tree: "
                + pProperty.getName() + ": " + pValue);
        }
        if (pProperty.getOpposite() == null) {
            return COPY_STRATEGY.REFERENCE;
        }
        return COPY_STRATEGY.SKIP;
    }

    private boolean isInCopyScope(DataObject pDataObject, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        if (pDataObject == null) {
            return false;
        }
        if (pCopies.containsKey(pDataObject)) {
            return true;
        }
        return isInCopyScope(pDataObject.getContainer(), pCopies);
    }

    private boolean isOutsideContainmentTree(DataObject pDataObject, Map<GenericDataObject, DataObjectDecorator> pCopies) {
        if (pDataObject == null) {
            return false;
        }
        DataObject root = ((DataObject)pCopies.keySet().iterator().next()).getRootObject();
        DataObject propertyRoot = pDataObject.getRootObject();
        return propertyRoot != root;
    }


    private Setting createSetting(Property pProperty, Object pValue) {
        return new SettingImpl(pProperty, pValue);
    }

}
