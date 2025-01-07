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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sap.sdo.api.helper.Validator;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * 
 * @author D042807
 *
 */
public class ValidationHelper implements Validator {
    
    private static ValidationHelper _instance = new ValidationHelper();
    
    private ValidationHelper() {
    }
    
    public static ValidationHelper getInstance() {
        return _instance;
    }
    
    public void validate(DataObject pRootObject) {
        try {
            validateTree(pRootObject);
        } catch (ValidationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Validates the constaints of a DataObject tree.
     * @param pRootObject The root of the DataObject tree.
     * @throws ValidationException
     * @see #validateConstraints(DataObject)
     */
    public static void validateTree(DataObject pRootObject) throws ValidationException {
        validateTree(pRootObject, new HashSet<DataObject>());
    }
    
    private static void validateTree(DataObject pDataObject, Set<DataObject> pValidated) throws ValidationException {
        if (pDataObject == null) {
            return;
        }
        if (pValidated.contains(pDataObject)) {
            return;
        } else {
            pValidated.add(pDataObject);
        }
        validateConstraints(pDataObject);
        List<Property> properties = pDataObject.getInstanceProperties();
        int size = properties.size();
        for (int i = 0; i < size; i++) {
            Property property = properties.get(i);
            if (!property.getType().isDataType()) {
                if (property.isMany()) {
                    List values = pDataObject.getList(i);
                    for (Object value: values) {
                        if (value instanceof DataObject) {
                        	validateTree((DataObject)value, pValidated);
                        }
                    }
                } else {
                    Object value = pDataObject.get(i);
                    if (value instanceof DataObject) {
                    	validateTree((DataObject)value, pValidated);
                    }
                }
            }
        }
    }
    
    /**
     * Validates the constraints of a DataObjects. Referenced DataObjects are
     * not validated.
     * @param pDataObject The DataObject to validate.
     * @throws ValidationException
     */
    public static void validateConstraints(DataObject pDataObject) throws ValidationException {
        try {
            validateContainer(pDataObject);
            validateProperties(pDataObject);
        } catch (ValidationException e) {
            throw e;
        } catch (RuntimeException e) { //$JL-EXC$
            throw new ValidationException(e);
        }
    }
    
    private static void validateContainer(DataObject pDataObject) throws ValidationException {
        Property property = pDataObject.getContainmentProperty();
        DataObject container = pDataObject.getContainer();
        if (property == null) {
            if (container != null) {
                throw new ValidationException("DataObject has no ContainmentProperty but a Container");
            }
        } else {
            if (container == null) {
                throw new ValidationException("DataObject has a ContainmentProperty but no Container");
            }
            // TODO:  Decide if this test is really necessary, since it's expensive
            /**
            if (property.isMany()) {
                List values = container.getList(property);
                if (!values.contains(pDataObject)) {
                    throw new ValidationException("DataObject is not contained by its ContainmentProperty");
                }
            } else {
                DataObject value = container.getDataObject(property);
                if (value != pDataObject) {
                    throw new ValidationException("DataObject is not contained by its ContainmentProperty");
                }
            }
            **/
        }
    }
    
    private static void validateProperties(DataObject pDataObject) throws ValidationException {
        List<Property> properties = pDataObject.getInstanceProperties();
        int size = properties.size();
        for (int i = 0; i < size; i++) {
            validateProperty(pDataObject, properties.get(i));
        }
    }

    private static void validateProperty(DataObject pDataObject, Property pProperty) throws ValidationException {
        if (pProperty.isMany()) {
            List<Object> values = pDataObject.getList(pProperty);
            for (Object value: values) {
                validateSinglePropertyValue(pDataObject, pProperty, value);
            }
        } else {
            Object value = pDataObject.get(pProperty);
            validateSinglePropertyValue(pDataObject, pProperty, value);
        }
    }
    
    private static void validateSinglePropertyValue(DataObject pDataObject, Property pProperty, Object pValue) throws ValidationException {
        if (pValue == null) {
            //somethig to check? don't know
            return;
        }
        Type propertyType = pProperty.getType();
        Class<?> instanceClass = propertyType.getInstanceClass();
        if (instanceClass!=null && !instanceClass.isInstance(pValue)) {
        	if (!instanceClass.isPrimitive() || !isNillableVersion(instanceClass,pValue.getClass())) {
            throw new ValidationException("Value of Property " + pProperty.getName() 
                + " has wrong class. Expected: " + instanceClass.getName() 
                + " but was " + pValue.getClass());
        	}
        }
        if (!propertyType.isDataType() && pValue instanceof DataObject) {
            DataObject valueDO = (DataObject)pValue;
            if (pProperty.isContainment()) {
                if (!pDataObject.equals(valueDO.getContainer())) {
                    throw new ValidationException("Value of containment property " 
                        + pProperty.getName() + " has wrong container");
                }
                if (!pProperty.equals(valueDO.getContainmentProperty())) {
                    throw new ValidationException("Value of containment property " 
                        + pProperty.getName() + " has wrong containment property");
                }
            }
            Property opposite = pProperty.getOpposite();
            if (opposite != null) {
                if (opposite.isMany()) {
                    List values = valueDO.getList(opposite);
                    if (!values.contains(pDataObject)) {
                        throw new ValidationException("DataObject is not contained by the opposite Property");
                    }
                } else {
                    DataObject value = valueDO.getDataObject(opposite);
                    if (value != pDataObject) {
                        throw new ValidationException("DataObject is not contained by the opposite Property");
                    }
                }
            }
        }
        
    }
    
    
    private static boolean isNillableVersion(Class primitiveClass, Class boxClass) {
		Type primType = SapHelperProviderImpl.getDefaultContext().getTypeHelper().getType(primitiveClass);
		if (!(primType instanceof JavaSimpleType)) {
			return false;
		}
        JavaSimpleType<?> nillableType = ((JavaSimpleType<?>)primType).getNillableType();
		return nillableType.getInheritedInstanceClass().isAssignableFrom(boxClass);
	}


	public static class ValidationException extends Exception {

        private static final long serialVersionUID = -8804235358426402457L;

        public ValidationException(String pMessage, Throwable pCause) {
            super(pMessage, pCause);
        }

        public ValidationException(String pMessage) {
            super(pMessage);
        }

        public ValidationException(Throwable pCause) {
            super(pCause);
        }
        
    }

}
