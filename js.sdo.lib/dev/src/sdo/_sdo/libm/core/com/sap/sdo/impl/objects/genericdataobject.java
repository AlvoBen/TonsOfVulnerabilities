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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataStrategy.State;
import com.sap.sdo.impl.objects.path.IPathResolver;
import com.sap.sdo.impl.objects.path.AbstractPathResolver.PropertyNotDefinedException;
import com.sap.sdo.impl.objects.projections.DelegatingDataStrategy;
import com.sap.sdo.impl.objects.projections.ProjectionDataStrategy;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.impl.objects.strategy.NonSequencedDataStrategy;
import com.sap.sdo.impl.objects.strategy.OpenNonSequencedDataStrategy;
import com.sap.sdo.impl.objects.strategy.SequenceOfValueNodes;
import com.sap.sdo.impl.objects.strategy.enhancer.AbstractPojoDataStrategy;
import com.sap.sdo.impl.objects.strategy.enhancer.EnhancerDataStrategy;
import com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy;
import com.sap.sdo.impl.objects.strategy.pojo.ProjectingPojoDataStrategy;
import com.sap.sdo.impl.types.Invoker;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeAndContext;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.DataGraphType.XsdType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.HelperContext;


/**
 * The basic implementation of a Data Object.
 * The internal implemention of the data structure in encapsulated by the
 * {@link DataStrategy}. There are two implementations for sequenced and
 * non-sequenced DataObjects.
 *
 * @author D042807
 *
 */
public class GenericDataObject implements DataObjectDecorator, InternalDataObjectModifier, InvocationHandler, Serializable
{
    private static final long serialVersionUID = -2648476983452761779L;

    public static final Object UNSET = new Unset();

    /**
     * External facade (could be an interface proxy or this if there is no
     * special interface).
     */
    private DataObjectDecorator _facade = this;

    /**
     * This property is shared for datagraph and containmentPropValue.
     * @see #getDataGraph()
     * @see #getContainmentPropValue()
     */
    private PropValue _containmentPropValue;
    private DataStrategy _dataStrategy;

    private TypeAndContext _typeAndContext;
    protected GenericDataObject() {
    }
    /**
     * Creates an DataObject of the given Type. The Type can be refined to more
     * concrete types lates by {@link #refineType(Type)}. At the time of
     * construction the {@link DataStrategy} will be chosen.
     * @param pType The type of the DataObject.
     */
    public GenericDataObject(TypeAndContext pTypeAndContext) {
        _typeAndContext = pTypeAndContext;
        Type type = pTypeAndContext.getSdoType();
        if (type.isSequenced()) {
            _dataStrategy = new SequenceOfValueNodes(type);
        } else if (type.isOpen()) {
            _dataStrategy = new OpenNonSequencedDataStrategy(type);
        } else {
            _dataStrategy = new NonSequencedDataStrategy(type);
        }
        _dataStrategy.setDataObject(this);
        _facade = this;
        checkDefineSpecialProperties();
    }

    /**
     * Creates an DataObject of the given Type. The Type can be refined to more
     * concrete types lates by {@link #refineType(Type)}. At the time of
     * construction the {@link DataStrategy} will be chosen.
     * @param pType The type of the DataObject.
     * @param pDataStrategyClass The class of the DataStrategy.
     * @param pData The data container of the DataStrategy.
     */
    public GenericDataObject(TypeAndContext pTypeAndContext, AbstractDataStrategy pDataStrategy) {
        _typeAndContext = pTypeAndContext;
        _dataStrategy = pDataStrategy;
        _dataStrategy.setDataObject(this);
        _facade = this;
        checkDefineSpecialProperties();
    }

    /**
     * The facade is the the external repesentation of the DataObject. The
     * facade delegates the DataObject behavior to the GenericDataObject and
     * can implement a Java interface that represents th type.
     * In fact the facade is a proxy or if no interface is define the
     * GenericDataObject itsself.
     * @param facade The external representation.
     * @see #getFacade()
     * @see #_facade
     */
    public void setFacade(DataObjectDecorator facade) {
        if (facade != null) {
            if (facade.getInstance() != this) {
                throw new IllegalArgumentException("invalid facade");
            }
            _facade = facade;
        }
    }

    public Object get(String path) {
        return getConvertedValue(path, Object.class);
    }

    public void set(String path, Object value) {
        set(path, value, null);
    }

    private void set(String path, Object value, Class pClass) {
        PropValue propValue;
        int index = -1;
        if (isPropertyName(path)) {
            Property property = getInstanceProperty(path);
            if (property == null) {
                if (!getType().isOpen()) {
                    throw new IllegalArgumentException("Path " + path
                        + " can not be resolved on data object " + this);
                }
                createOpenProperty(path, value, pClass);
                return;
            }
            propValue = getPropValue(property, true, false);
        } else {
            IPathResolver pathResolver;
            try {
                pathResolver = XPathHelper.pathResolver(this, path);
                propValue = pathResolver.getPropValue();
                if (propValue == null) {
                    throw new IllegalArgumentException("Path " + path
                        + " can not be resolved on data object " + this);
                }
            } catch (PropertyNotDefinedException e) {
                if (!e.isEndOfPath()) {
                    throw e;
                }
                GenericDataObject dataObject = ((DataObjectDecorator)e.getDataObject()).getInstance();
                if (!dataObject.getType().isOpen()) {
                    throw e;
                }
                dataObject.createOpenProperty(e.getPropertyName(), value, pClass);
                return;
            }
            if (!pathResolver.isPlain()) {
                index = pathResolver.getIndex();
            }
        }
        try {
            if (index < 0) {
                propValue.setValue(value);
            } else {
                propValue.set(index, value);
            }
        } catch (ClassCastException e) {
            throw e;
        } catch (NumberFormatException e) {
            //TODO we hate this, but is is defined in spec
            ClassCastException cce = new ClassCastException(e.getMessage());
            cce.initCause(e);
            throw cce;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (IndexOutOfBoundsException e) {
            throw e;
        } catch (RuntimeException e) {
           //TODO we hate this, but is is defined in spec
            ClassCastException cce = new ClassCastException(e.getMessage());
            cce.initCause(e);
            throw cce;
        }
    }
    private boolean isPropertyName(String pPath) {
        return (pPath.charAt(pPath.length() -1) != ']') && (pPath.indexOf('/') < 0) && (pPath.lastIndexOf('.') < 0);
    }

    /**
     * Enhancement of the specification: creates an open {@link Property} on
     * demand. The {@link Type} of the propety is guessed from the value.
     * @param pPropertyName The new name of the new open property.
     * @param pValue The value of the new Property.
     * @see #guessType(Object)
     */
    public void createOpenProperty(String pPropertyName, Object pValue, Class pClass) {
        if (pValue == null) {
            throw new NullPointerException("Value is null");
        }
        boolean containment = false;
        // guess a type
        Type type = null;
        boolean isMany = false;
        if (pValue instanceof List) {
            if (((List)pValue).isEmpty()) {
                throw new IllegalArgumentException(
                    "Can not create open property. List is empty. Can not guess type.");
            }
            isMany = true;
            for (Object singleValue: (List)pValue) {
                if (type == null) {
                    type = guessType(singleValue, null);
                    containment = !type.isDataType();
                } else {
                    if (type != guessType(singleValue, null)) {
                        throw new IllegalArgumentException(
                            "Can not create open property, types in list are not equal");
                    }
                }
                if (containment && ((DataObject)singleValue).getContainer()!=null) {
                    containment = false;
                }
            }
        } else {
            type = guessType(pValue, pClass);
            containment = !type.isDataType() && (((DataObject)pValue).getContainer() == null);
        }
        HelperContext helperContext = getHelperContext();
        DataObject propertyDO = helperContext.getDataFactory().create(PropertyType.getInstance());
        propertyDO.set(PropertyType.NAME, pPropertyName);
        propertyDO.set(PropertyType.TYPE, type);
        propertyDO.setBoolean(PropertyType.MANY, isMany);
        propertyDO.setBoolean(PropertyType.CONTAINMENT, containment);
        Property valueProp = TypeHelperImpl.getSimpleContentProperty(this);
        propertyDO.setBoolean(PropertyType.getXmlElementProperty(), valueProp == null);
        set(helperContext.getTypeHelper().defineOpenContentProperty(null,propertyDO), pValue);
    }

    public HelperContext getHelperContext() {
        return _typeAndContext.getHelperContext();
    }

    private Type guessType(Object pValue, Class pClass) {
        Type type = null;
        if (pValue instanceof DataObject) {
            type = ((DataObject) pValue).getType();
        } else {
            Class instanceClass = pClass;
            if (instanceClass == null) {
                instanceClass = pValue.getClass();
            }
            type = getHelperContext().getTypeHelper().getType(instanceClass);
        }
        if (type==null) {
            type = JavaSimpleType.OBJECT;
        }
        return type;
    }

    public boolean isSet(String path) {
        if (isPropertyName(path)) {
            Property property = getInstanceProperty(path);
            if (property == null) {
                return false;
            }
            PropValue propValue = getPropValue(property, false, false);
            if (propValue == null) {
                return false;
            }
            return propValue.isSet();
        }
        try {
            IPathResolver pathResolver = XPathHelper.pathResolver(this, path);
            PropValue<?> propValue = pathResolver.getPropValue();
            return propValue.isSet();
        } catch (PropertyNotDefinedException e) {
            if (e.isEndOfPath()) {
                DataObject lastFoundDoInPath = e.getDataObject();
                if (lastFoundDoInPath.getType().isOpen()) {
                    return false;
                }
            }
            throw e;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public void unset(String path) {
        IPathResolver pathResolver = XPathHelper.pathResolver(this, path);
        PropValue<?> propValue = pathResolver.getPropValue();
        //TODO error handling
        if (propValue != null) {
            if (pathResolver.isPlain()) {
                propValue.unset();
            } else {
                List<Object> list = (List<Object>)propValue.getValue();
                list.remove(pathResolver.getIndex());
            }
        }
    }

    public boolean getBoolean(String path) {
        return convertNull(getConvertedValue(path, Boolean.class), JavaSimpleType.DEFAULT_BOOLEAN);
    }

    public byte getByte(String path) {
        return convertNull(getConvertedValue(path, Byte.class), JavaSimpleType.DEFAULT_BYTE);
    }

    public char getChar(String path) {
        return convertNull(getConvertedValue(path, Character.class), JavaSimpleType.DEFAULT_CHAR);
    }

    public double getDouble(String path) {
        return convertNull(getConvertedValue(path, Double.class), JavaSimpleType.DEFAULT_DOUBLE);
    }

    public float getFloat(String path) {
        return convertNull(getConvertedValue(path, Float.class), JavaSimpleType.DEFAULT_FLOAT);
    }

    public int getInt(String path) {
        return convertNull(getConvertedValue(path, Integer.class), JavaSimpleType.DEFAULT_INT);
    }

    public long getLong(String path) {
        return convertNull(getConvertedValue(path, Long.class), JavaSimpleType.DEFAULT_LONG);
    }

    public short getShort(String path) {
        return convertNull(getConvertedValue(path, Short.class), JavaSimpleType.DEFAULT_SHORT);
    }

    public byte[] getBytes(String path) {
        return getConvertedValue(path, byte[].class);
    }

    public BigDecimal getBigDecimal(String path) {
        return getConvertedValue(path, BigDecimal.class);
    }

    public BigInteger getBigInteger(String path) {
        return getConvertedValue(path, BigInteger.class);
    }

    public DataObject getDataObject(String path) {
        return getConvertedValue(path, DataObject.class);
    }

    public Date getDate(String path) {
        return getConvertedValue(path, Date.class);
    }

    public String getString(String path) {
        return getConvertedValue(path, String.class);
    }

    public List getList(String path) {
        return getConvertedValue(path, List.class);
    }

    @Deprecated
    public Sequence getSequence(String path) {
        return getDataObject(path).getSequence();
    }

    public void setBoolean(String path, boolean value) {
        set(path, value, boolean.class);
    }

    public void setByte(String path, byte value) {
        set(path, value, byte.class);
    }

    public void setChar(String path, char value) {
        set(path, value, char.class);
    }

    public void setDouble(String path, double value) {
        set(path, value, double.class);
    }

    public void setFloat(String path, float value) {
        set(path, value, float.class);
    }

    public void setInt(String path, int value) {
        set(path, value, int.class);
    }

    public void setLong(String path, long value) {
        set(path, value, long.class);
    }

    public void setShort(String path, short value) {
        set(path, value, short.class);
    }

    public void setBytes(String path, byte[] value) {
        set(path, value, byte[].class);
    }

    public void setBigDecimal(String path, BigDecimal value) {
        set(path, value, BigDecimal.class);
    }

    public void setBigInteger(String path, BigInteger value) {
        set(path, value, BigInteger.class);
    }

    public void setDataObject(String path, DataObject value) {
        set(path, value, null);
    }

    public void setDate(String path, Date value) {
        set(path, value, Date.class);
    }

    public void setString(String path, String value) {
        set(path, value, String.class);
    }

    public void setList(String path, List value) {
        set(path, value, null);
    }

    public Object get(int propertyIndex) {
        PropValue propValue = getPropValue(propertyIndex, true);
        return propValue.getValue();
    }

    public void set(int propertyIndex, Object pValue) {
        PropValue propValue = getPropValue(propertyIndex, true);
        propValue.setValue(pValue);
    }

    public boolean isSet(int propertyIndex) {
        PropValue<?> propValue = getPropValue(propertyIndex, false);
        if (propValue == null) {
            return false;
        }
        return propValue.isSet();
    }

    public void unset(int propertyIndex) {
        PropValue<?> propValue = getPropValue(propertyIndex, false);
        if (propValue != null) {
            propValue.unset();
        }
    }

    public boolean getBoolean(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Boolean.class), JavaSimpleType.DEFAULT_BOOLEAN);
    }

    public byte getByte(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Byte.class), JavaSimpleType.DEFAULT_BYTE);
    }

    public char getChar(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Character.class), JavaSimpleType.DEFAULT_CHAR);
    }

    public double getDouble(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Double.class), JavaSimpleType.DEFAULT_DOUBLE);
    }

    public float getFloat(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Float.class), JavaSimpleType.DEFAULT_FLOAT);
    }

    public int getInt(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Integer.class), JavaSimpleType.DEFAULT_INT);
    }

    public long getLong(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Long.class), JavaSimpleType.DEFAULT_LONG);
    }

    public short getShort(int propertyIndex) {
        return convertNull(getConvertedValue(propertyIndex, Short.class), JavaSimpleType.DEFAULT_SHORT);
    }

    public byte[] getBytes(int propertyIndex) {
        return getConvertedValue(propertyIndex, byte[].class);
    }

    public BigDecimal getBigDecimal(int propertyIndex) {
        return getConvertedValue(propertyIndex, BigDecimal.class);
    }

    public BigInteger getBigInteger(int propertyIndex) {
        return getConvertedValue(propertyIndex, BigInteger.class);
    }

    public DataObject getDataObject(int propertyIndex) {
        return getConvertedValue(propertyIndex, DataObject.class);
    }

    public Date getDate(int propertyIndex) {
        return getConvertedValue(propertyIndex, Date.class);
    }

    public String getString(int propertyIndex) {
        return getConvertedValue(propertyIndex, String.class);
    }

    public List getList(int propertyIndex) {
        return getConvertedValue(propertyIndex, List.class);
    }

    @Deprecated
    public Sequence getSequence(int propertyIndex) {
        return getDataObject(propertyIndex).getSequence();
    }

    public void setBoolean(int propertyIndex, boolean value) {
        set(propertyIndex, value);
    }

    public void setByte(int propertyIndex, byte value) {
        set(propertyIndex, value);
    }

    public void setChar(int propertyIndex, char value) {
        set(propertyIndex, value);
    }

    public void setDouble(int propertyIndex, double value) {
        set(propertyIndex, value);
    }

    public void setFloat(int propertyIndex, float value) {
        set(propertyIndex, value);
    }

    public void setInt(int propertyIndex, int value) {
        set(propertyIndex, value);
    }

    public void setLong(int propertyIndex, long value) {
        set(propertyIndex, value);
    }

    public void setShort(int propertyIndex, short value) {
        set(propertyIndex, value);
    }

    public void setBytes(int propertyIndex, byte[] value) {
        set(propertyIndex, value);
    }

    public void setBigDecimal(int propertyIndex, BigDecimal value) {
        set(propertyIndex, value);
    }

    public void setBigInteger(int propertyIndex, BigInteger value) {
        set(propertyIndex, value);
    }

    public void setDataObject(int propertyIndex, DataObject value) {
        set(propertyIndex, value);
    }

    public void setDate(int propertyIndex, Date value) {
        set(propertyIndex, value);
    }

    public void setString(int propertyIndex, String value) {
        set(propertyIndex, value);
    }

    public void setList(int propertyIndex, List value) {
        set(propertyIndex, value);
    }

    public Object get(Property property) {
        PropValue propValue = getPropValue(property, true, false);
        if (propValue == null) {
            return property.getDefault();
        }
        return propValue.getValue();
    }

    public void set(Property property, Object pValue) {
        PropValue propValue = getPropValue(property, true, true);
        propValue.setValue(pValue);
    }

    public boolean isSet(Property property) {
        PropValue<?> propValue = getPropValue(property, false, false);
        if (propValue == null) {
            return false;
        }
        return propValue.isSet();
    }

    public void unset(Property property) {
        PropValue<?> propValue = getPropValue(property, false, false);
        if (propValue != null) {
            propValue.unset();
        }
    }

    public boolean getBoolean(Property property) {
        return convertNull(getConvertedValue(property, Boolean.class), JavaSimpleType.DEFAULT_BOOLEAN);
    }

    public byte getByte(Property property) {
        return convertNull(getConvertedValue(property, Byte.class), JavaSimpleType.DEFAULT_BYTE);
    }

    public char getChar(Property property) {
        return convertNull(getConvertedValue(property, Character.class), JavaSimpleType.DEFAULT_CHAR);
    }

    public double getDouble(Property property) {
        return convertNull(getConvertedValue(property, Double.class), JavaSimpleType.DEFAULT_DOUBLE);
    }

    public float getFloat(Property property) {
        return convertNull(getConvertedValue(property, Float.class), JavaSimpleType.DEFAULT_FLOAT);
    }

    public int getInt(Property property) {
        return convertNull(getConvertedValue(property, Integer.class), JavaSimpleType.DEFAULT_INT);
    }

    public long getLong(Property property) {
        return convertNull(getConvertedValue(property, Long.class), JavaSimpleType.DEFAULT_LONG);
    }

    public short getShort(Property property) {
        return convertNull(getConvertedValue(property, Short.class), JavaSimpleType.DEFAULT_SHORT);
    }

    public byte[] getBytes(Property property) {
        return getConvertedValue(property, byte[].class);
    }

    public BigDecimal getBigDecimal(Property property) {
        return getConvertedValue(property, BigDecimal.class);
    }

    public BigInteger getBigInteger(Property property) {
        return getConvertedValue(property, BigInteger.class);
    }

    public DataObject getDataObject(Property property) {
        return getConvertedValue(property, DataObject.class);
    }

    public Date getDate(Property property) {
        return getConvertedValue(property, Date.class);
    }

    public String getString(Property property) {
        return getConvertedValue(property, String.class);
    }

    public List getList(Property property) {
        return getConvertedValue(property, List.class);
    }

    @Deprecated
    public Sequence getSequence(Property property) {
        return getDataObject(property).getSequence();
    }

    public void setBoolean(Property property, boolean value) {
        set(property, value);
    }

    public void setByte(Property property, byte value) {
        set(property, value);
    }

    public void setChar(Property property, char value) {
        set(property, value);
    }

    public void setDouble(Property property, double value) {
        set(property, value);
    }

    public void setFloat(Property property, float value) {
        set(property, value);
    }

    public void setInt(Property property, int value) {
        set(property, value);
    }

    public void setLong(Property property, long value) {
        set(property, value);
    }

    public void setShort(Property property, short value) {
        set(property, value);
    }

    public void setBytes(Property property, byte[] value) {
        set(property, value);
    }

    public void setBigDecimal(Property property, BigDecimal value) {
        set(property, value);
    }

    public void setBigInteger(Property property, BigInteger value) {
        set(property, value);
    }

    public void setDataObject(Property property, DataObject value) {
        set(property, value);
    }

    public void setDate(Property property, Date value) {
        set(property, value);
    }

    public void setString(Property property, String value) {
        set(property, value);
    }

    public void setList(Property property, List value) {
        set(property, value);
    }

    public DataObject createDataObject(String propertyName) {
        Property p = getInstanceProperty(propertyName);
        if (p != null) {
            return createDataObject(p);
        }
        throw new IllegalArgumentException("data object of _type "
                + getType() + " has no property by name "
                + propertyName);
    }

    public DataObject createDataObject(int propertyIndex) {
        Property prop = _dataStrategy.getPropertyByIndex(propertyIndex);
        if (prop != null) {
            return createDataObject(prop);
        }
        throw new IllegalArgumentException("data object of _type "
                + getType() + " has no property by index "
                + propertyIndex);
    }

    public DataObject createDataObject(Property property) {
        return createDataObject(property, property.getType());
    }

    public DataObject createDataObject(String propertyName,
            String namespaceURI, String typeName) {
        Property prop = getInstanceProperty(propertyName);
        if (prop != null) {
            return createDataObject(prop, namespaceURI, typeName);
        }
        throw new IllegalArgumentException("data object of _type "
                + getType() + " has no property by name "
                + propertyName);
    }

    public DataObject createDataObject(int propertyIndex, String namespaceURI,
            String typeName) {
        Property prop = _dataStrategy.getPropertyByIndex(propertyIndex);
        if (prop != null) {
            return createDataObject(prop, namespaceURI, typeName);
        }
        throw new IllegalArgumentException("data object of _type "
                + getType() + " has no property by index "
                + propertyIndex);
    }

    private DataObject createDataObject(Property property, String namespaceURI,
            String typeName) {
        Type type = getHelperContext().getTypeHelper().getType(namespaceURI, typeName);
        return createDataObject(property, type);
    }

    public DataObject createDataObject(Property property, Type type) {
        return _dataStrategy.createDataObject(property, type);
    }

    public void delete() {
        _dataStrategy.delete();
    }

    public DataObject getContainer() {
        PropValue<?> containmentPropValue = getContainmentPropValue();
        if (containmentPropValue == null) {
            return null;
        }
        return containmentPropValue.getDataObject().getFacade();
    }

    public Property getContainmentProperty() {
        PropValue<?> containmentPropValue = getContainmentPropValue();
        if (containmentPropValue == null) {
            return null;
        }
        return containmentPropValue.getProperty();
    }

    public DataGraph getDataGraph() {
        DataObject parent = getContainer();
        if (parent == null) {
            return null;
        }
        if (parent instanceof DataGraph) {
            return (DataGraph)parent;
        }
        return parent.getDataGraph();
    }

    public Type getType() {
        return _typeAndContext.getSdoType();
    }

    public Sequence getSequence() {
        return _dataStrategy.getSequence();
    }

    public List<Property> getInstanceProperties() {
        return _dataStrategy.getInstanceProperties();
    }

    public Property getInstanceProperty(String pNameOrAlias) {
        return _dataStrategy.getPropertyByNameOrAlias(pNameOrAlias);
    }

    @Deprecated
    public Property getProperty(String pNameOrAlias) {
        return getInstanceProperty(pNameOrAlias);
    }

    public DataObject getRootObject() {
        DataObject container = getContainer();
        if (container == null) {
            return getFacade();
        }
        return container.getRootObject();
    }

    public ChangeSummary getChangeSummary() {
        int propertyIndex = ((SdoType)getType()).getCsPropertyIndex();
        if (propertyIndex >= 0) {
            return (ChangeSummary)get(propertyIndex);
        }
        PropValue<?> containmentPropValue = getContainmentPropValue();
        if (containmentPropValue == null) {
            return null;
        }
        return containmentPropValue.getDataObject().getChangeSummary();
    }


    public void detach() {
        PropValue<?> containmentPropValue = getContainmentPropValue();
        if (containmentPropValue != null) {
            containmentPropValue.remove(this.getFacade());
        }
    }

    /**
     * Internal method to remove the parent form the DataObject.
     */
    public void internDetach() {
        if (getContainmentPropValue() != null) {
            _dataStrategy.setChangeState(State.DELETED, true);
            _containmentPropValue = null;
        }
    }

    /**
     * Internal method to attach this DataObject to a PropValue. The change
     * state will be set to created if it gets a new ChangsSummary.
     * @param pPropValue The new comtainment PropValue.
     * @param pContainerIsOpposite True if the current Property is an opposite.
     */
    public void internAttach(PropValue<?> pPropValue, boolean pContainerIsOpposite) {
        if (getContainmentPropValue() != pPropValue) {
            if (!pContainerIsOpposite) {
                detach();
            }
            _containmentPropValue = pPropValue;
            DataStrategy parentDataStrategy = pPropValue.getDataObject().getDataStrategy();
            if (parentDataStrategy.isInitialScope() || parentDataStrategy.getChangeState() == State.CREATED) {
                _dataStrategy.setChangeState(State.CREATED, false);
            }
        }
    }

    public GenericDataObject getInstance() {
        return this;
    }

    public InternalDataObjectModifier getInternalModifier() {
        return this;
    }

    public Property findOpenProperty(String pUri, String pXsdName, boolean pIsElement) {
        return _dataStrategy.findOpenProperty(pUri, pXsdName, pIsElement);
    }

    /**
     * Returns the external representation of the GenericDataObject.
     * @return The Proxy or the GenericDataObject itself.
     * @see #setFacade(DataObjectDecorator)
     */
    public DataObjectDecorator getFacade() {
        if (_facade == null) {
            Class interfaceClass = getType().getInstanceClass();
            if ((interfaceClass != null)) {
                DataFactoryImpl dataFactory = (DataFactoryImpl)getHelperContext().getDataFactory();
                _facade = dataFactory.createProxy(this, interfaceClass);
            } else {
                _facade = this;
            }
        }
        return _facade;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        asString(buffer);
        return buffer.toString();
    }

    /**
     * To support {@link #toString() with a better performance.
     * @param pBuffer The StringBuilder.
     */
    public void asString(StringBuilder pBuffer) {
        pBuffer.append('[').append(((SdoType)getType()).getQName().toString()).append(']');
        _dataStrategy.asString(pBuffer);
    }

    /**
     * Only equal if it is the same instance or if it the
     * {@link #setFacade(DataObjectDecorator) facade} of this GenericDataObject.
     * For a deep equality check use
     * {@link commonj.sdo.helper.EqualityHelper#equal(DataObject, DataObject)}.
     * @param pObject the reference object with which to compare.
     * @return true if it is the same instance or the assigned fassade.
     */
    @Override
    public boolean equals(Object pObject) { //$JL-EQUALS$
        // with super implementation of hashcode all constraints are fulfilled
        if (pObject == this) {
            return true;
        }
        if (pObject instanceof DataObjectDecorator) {
            return ((DataObjectDecorator)pObject).getInstance() == this;
        }
        return false;
    }

    /**
     * Finds the PropValue for a property index. PropValues are created lazy.
     * If the PropValue is not already created and the parameter pCreate is
     * false, the PropValue will not be created. The return value will be null
     * in that case.
     * @param pPropertyIndex The index of the Property.
     * @param pCreate True to force lazy creation, false to avoid lazy creation.
     * @return The PropValue or null if not found.
     * @see DataStrategy#getPropValue(int, Property, boolean)
     */
    public PropValue<?> getPropValue(int pPropertyIndex, boolean pCreate) {
        return _dataStrategy.getPropValue(pPropertyIndex, null, pCreate);
    }

    /**
     * Finds the PropValue for a {@link Property}. PropValues are created lazy.
     * If the PropValue is not already created and the parameter pCreate is
     * false, the PropValue will not be created. The return value will be null
     * in that case.
     * @param pProperty The Property.
     * @param pCreate True to force lazy creation, false to avoid lazy creation.
     * @return The PropValue or null if the Property is valid but not already created.
     * @throws IllegalArgumentException if the property is nit valid for this Type.
     * @see DataStrategy#indexOfProperty(Property, boolean)
     * @see DataStrategy#getPropValue(int, Property, boolean)
     */
    public PropValue<?> getPropValue(Property pProperty, boolean pCreate, boolean pCreateOpenContent) {
        if (pProperty == null) {
            throw new NullPointerException("Property is null");
        }
        // for many valued Properties create a PropValue to get a List
        boolean createOpenContent = pCreate && (pCreateOpenContent || pProperty.isMany());
        boolean removeIfUnset = createOpenContent && !pCreateOpenContent;
        State changeState = null;
        if (removeIfUnset) {
            changeState = _dataStrategy.getChangeState();
        }
        int propIndex = _dataStrategy.indexOfProperty(pProperty, createOpenContent);
        if (propIndex < 0) {
            if (createOpenContent) {
                throw new IllegalArgumentException("Can not find property "
                    + pProperty.getName());
            } else {
                return null;
            }
        }
        final PropValue<?> propValue = _dataStrategy.getPropValue(propIndex, pProperty, pCreate);
        if (removeIfUnset && (propValue!= null) && pProperty.isOpenContent() && !propValue.isSet()) {
            // make empty open content Lists invisible
            _dataStrategy.internUnset(pProperty, false); //TODO check save changes option
            if (changeState != _dataStrategy.getChangeState()) {
                setChangeStateWithoutCheck(changeState);
            }
        }
        return propValue;
    }

    /**
     * Returns the instance Properties at the point when logging began.
     * @return The old instance Properties.
     * @see DataStrategy#getOldInstanceProperties()
     * @see DataObject#getInstanceProperties()
     */
    public List<Property> getOldInstanceProperties() {
        return _dataStrategy.getOldInstanceProperties();
    }

    /**
     * Returns the value of a Property at the point when logging began or null
     * if it was not changed.
     * @param pProperty The Property.
     * @return The old value as Setting.
     * @see DataStrategy#getOldValue(Property)
     * @see ChangeSummary#getOldValue(DataObject, Property)
     */
    public Setting getOldValue(Property pProperty) {
        return _dataStrategy.getOldValue(pProperty);
    }

    /**
     * Returns the old Properties that has changed since the logging has begun.
     * @return The list of changed Properties.
     * @see DataStrategy#getOldChangedProperties()
     */
    public List<Property> getOldChangedProperties() {
        return _dataStrategy.getOldChangedProperties();
    }

    /**
     * Finds the PropValue by the properties name or alias.
     * @param pPath The name or alias of the property.
     * @return The PropValue.
     */
    public PropValue<?> getPropValueByPropNameOrAlias(String pPropertyName) {
        Property property = getInstanceProperty(pPropertyName);
        if (property == null) {
            throw new IllegalArgumentException("Property " + pPropertyName
                + " can not be resolved");
        }
        return getPropValue(property, true, false);
    }

    /**
     * Finds a value by a path and converts it to the given class.
     * @param <C> The expected result class.
     * @param pPath The path to the value.
     * @param pClass The expected result class.
     * @return The converted value or null if not found.
     * @throws ClassCastException if value can not be converted into the result class.
     */
    protected <C> C getConvertedValue(String pPath, Class<C> pClass) {
        if (pPath == null) {
            throw new NullPointerException("path is null");
        }
        PropValue<?> propValue;
        int index = -1;
        if (isPropertyName(pPath)) {
            Property property = getInstanceProperty(pPath);
            if (property == null) {
                return null;
            }
            propValue = getPropValue(property, true, false);
        } else {
            IPathResolver pathResolver;
            try {
                pathResolver = XPathHelper.pathResolver(this, pPath);
                propValue = pathResolver.getPropValue();
            } catch (RuntimeException e) {
                return null;
            }
            if (!pathResolver.isPlain()) {
                index = pathResolver.getIndex();
            }
        }
        try {
            if (index < 0) {
                return propValue.getConvertedValue(pClass);
            }
            return propValue.getConvertedValue(index, pClass);
        } catch (ClassCastException cce) {
            throw cce;
        } catch (RuntimeException e) {
            //TODO we hate this, but is is defined in spec
            ClassCastException cce = new ClassCastException(e.getMessage());
            cce.initCause(e);
            throw cce;
        }
    }

    /**
     * Finds a value by the {@link Property} and converts it to the given class.
     * @param <C> The expected result class.
     * @param pProperty The property.
     * @param pClass The expected result class.
     * @return The converted value or null if not found.
     * @throws ClassCastException if value can not be converted into the result class.
     */
    protected <C> C getConvertedValue(Property pProperty, Class<C> pClass) {
        if (pProperty == null) {
            throw new NullPointerException("Property is null");
        }
        try {
            PropValue<?> propValue = getPropValue(pProperty, true, false);
            if (propValue == null) {
                return AbstractDataStrategy.convertValue(pProperty.getDefault(), pProperty, pClass);
            }
            return propValue.getConvertedValue(pClass);
        } catch (ClassCastException cce) {
            throw cce;
        } catch (RuntimeException e) { //$JL-EXC$
            // should return null instead of throwing an exception
        }
        return null;
    }

    /**
     * Finds a value by the property index and converts it to the given class.
     * @param <C> The expected result class.
     * @param pPropIndex The index of the property.
     * @param pClass The expected result class.
     * @return The converted value or null if not found.
     * @throws ClassCastException if value can not be converted into the result class.
     */
    public <C> C getConvertedValue(int pPropIndex, Class<C> pClass) {
        try {
            PropValue<?> propValue = getPropValue(pPropIndex, true);
            return propValue.getConvertedValue(pClass);
        } catch (ClassCastException cce) {
            throw cce;
        } catch (RuntimeException e) { //$JL-EXC$
            // should return null instead of throwing an exception
        }
        return null;
    }

    /**
     * Returns the containment PropValue that encapsulates the containment
     * Property and the container.
     * @return The containment PropValue.
     * @see #getContainmentProperty()
     * @see #getContainer()
     */
    public PropValue<?> getContainmentPropValue() {
        return _containmentPropValue;
    }

    /**
     * This is an internal method, that is called by the ChangeSummary to
     * remove open properties. The changes are not tracked by the ChangeSummary.
     * @param pProperty The Property.
     * @see DataStrategy#internUnset(Property, boolean)
     * @see #getInstanceProperties()
     */
    public void internUnset(Property pProperty) {
        _dataStrategy.internUnset(pProperty, false);
    }

    /**
     * Calculates a unique id of the DataObject. In fact it is the
     * {@link Object#hashCode() hash code}.
     * @return The unique id.
     */
    public int _uniqueId() {
        // assert(super.getClass().equals(Object.class));
        return super.hashCode();
    }

    /**
     * Returns the containment {@link PropValue} at the point when logging began.
     * @return The old containment PropValue.
     * @see GenericDataObject#getContainmentPropValue()
     * @see DataStrategy#getOldContainmentPropValue()
     */
    public PropValue<?> getOldContainmentPropValue() {
        return _dataStrategy.getOldContainmentPropValue();
    }

    /**
     * Returns the containment {@link PropValue} at the point when logging began.
     * @return The old containment PropValue.
     * @see GenericDataObject#getContainmentPropValue()
     * @see DataStrategy#getOldContainmentPropValue()
     */
    public Sequence getOldSequence() {
        return _dataStrategy.getOldSequence();
    }

    /**
     * Returns true, if the DataObject was modified since the logging has begun.
     * Returns false, if it is unchanged, created or deleted.
     * @return true, if modified.
     */
    public boolean isModified() {
        return _dataStrategy.getChangeState() == State.MODIFIED;
    }

    /**
     * Returns true, if the DataObject was created since the logging has begun.
     * @return true, if created.
     */
    public boolean isCreated() {
        return _dataStrategy.getChangeState() == State.CREATED;
    }

    /**
     * Returns true, if the DataObject was deleted since the logging has begun.
     * @return true, if deleted.
     */
    public boolean isDeleted() {
        return _dataStrategy.getChangeState() == State.DELETED;
    }

    /**
     * Clears the old values.
     * @see DataStrategy#resetChangeSummary()
     */
    public void resetChangeSummary() {
        _dataStrategy.resetChangeSummary();
    }

    /**
     * Sets the value of a {@link Property} without any containment or opposite
     * checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#setPropertyWithoutCheck(Property, Object)
     */
    public void setPropertyWithoutCheck(Property pProperty, Object pValue) {
        _dataStrategy.setPropertyWithoutCheck(pProperty, pValue);
    }

    /**
     * Adds a value to a multi-valued {@link Property} without any containment
     * or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced multi-valued properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#addToPropertyWithoutCheck(Property, Object)
     */
    public void addToPropertyWithoutCheck(Property pProperty, Object pValue) {
        _dataStrategy.addToPropertyWithoutCheck(pProperty, pValue);
    }

    /**
     * Sets the {@link Sequence} without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pSettings The new List of Property-value-pairs.
     * @see DataStrategy#setSequenceWithoutCheck(List)
     */
    public void setSequenceWithoutCheck(List<Setting> pSettings) {
        _dataStrategy.setSequenceWithoutCheck(pSettings);
    }

    /**
     * Adds a new value to the {@link Sequence} without any containment or
     * opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#addToSequenceWithoutCheck(Property, Object)
     */
    public void addToSequenceWithoutCheck(Property pProperty, Object pValue) {
        _dataStrategy.addToSequenceWithoutCheck(pProperty, pValue);
    }

    /**
     * Sets the new container and containment property without containment or
     * opposite checks.
     * @param pContainer The new container.
     * @param pProperty The new containment property.
     */
    public void setContainerWithoutCheck(DataObject pContainer, Property pProperty) {
        if ((pContainer == null) || (pProperty == null)) {
            if ((getContainmentProperty() != null)) {
                setContainerWithoutCheck(null);
            }
        } else {
            GenericDataObject dataObject = ((DataObjectDecorator)pContainer).getInstance();
            setContainerWithoutCheck(dataObject.getPropValue(pProperty, true, true));
        }
    }

    /**
     * Sets the new containment PropValue without containment or
     * opposite checks.
     * @param pPropValue The new containment PropValue.
     */
    public void setContainerWithoutCheck(PropValue pPropValue) {
        _containmentPropValue = pPropValue;
    }

    public void addPropertyValueWithoutCheck(Property pProperty, Object pValue) {
        final SdoType type = (SdoType)getType();
        final SdoProperty property = (SdoProperty)pProperty;
        Object value = pValue;
        GenericDataObject valueDo = null;
        if (value instanceof DataObjectDecorator) {
            valueDo = ((DataObjectDecorator)value).getInstance();
        }
        PropValue<?> propValue;
        if (type.isSequenced() && (property.isXmlElement())) {
            propValue = _dataStrategy.addToSequenceWithoutCheck(property, value);
        } else {
            if (property.isMany()) {
                propValue = _dataStrategy.addToPropertyWithoutCheck(property, value);
            } else {
                propValue = _dataStrategy.setPropertyWithoutCheck(property, value);
            }
        }
        if ((valueDo != null) && property.isContainment()) {
            valueDo.setContainerWithoutCheck(propValue);
            if (valueDo.getDataStrategy().getChangeState()!= State.UNCHANGED && getOldValue(pProperty) == null) {
                valueDo.setOldContainerWithoutCheck(this, pProperty);
            }
        }
    }

    public void addOldPropertyValueWithoutCheck(Property pProperty, Object pValue) {
        final SdoType type = (SdoType)getType();
        final SdoProperty property = (SdoProperty)pProperty;
        Object value = pValue;
        GenericDataObject valueDo = null;
        if (value instanceof DataObjectDecorator) {
            valueDo = ((DataObjectDecorator)value).getInstance();
            value = valueDo;
        }
        if (type.isSequenced() && (property.isXmlElement())) {
            addToOldSequenceWithoutCheck(property, value);
        } else {
            if (property.isMany()) {
                addToOldPropertyWithoutCheck(property, value);
            } else {
                setOldPropertyWithoutCheck(property, value);
            }
        }
        if ((valueDo != null) && property.isContainment()) {
            valueDo.setOldContainerWithoutCheck(this, pProperty);
        }
    }

    public void unsetPropertyWithoutCheck(Property pProperty) {
        if (!getType().isSequenced() || !((SdoProperty)pProperty).isXmlElement()) {
            if (pProperty.isMany()) {
                setPropertyWithoutCheck(pProperty, Collections.emptyList());
            } else {
                setPropertyWithoutCheck(pProperty, UNSET);
            }
        }
    }

    public void unsetOldPropertyWithoutCheck(Property pProperty) {
        if (!getType().isSequenced() || !((SdoProperty)pProperty).isXmlElement()) {
            if (pProperty.isMany()) {
                setOldPropertyWithoutCheck(pProperty, Collections.emptyList());
            } else {
                setOldPropertyWithoutCheck(pProperty, UNSET);
            }
        }
    }

    /**
     * Sets the {@link State change state} directly without any checks.
     * @param pState The new change state.
     * @see DataStrategy#setChangeStateWithoutCheck(State)
     */
    public void setChangeStateWithoutCheck(State pState) {
        _dataStrategy.setChangeStateWithoutCheck(pState);
    }

    /**
     * Sets the old value of a {@link Property} for the {@link commonj.sdo.ChangeSummary}
     * without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#setOldPropertyWithoutCheck(Property, Object)
     */
    public void setOldPropertyWithoutCheck(Property pProperty, Object pValue) {
        _dataStrategy.setOldPropertyWithoutCheck(pProperty, pValue);
    }

    /**
     * Adds an old value to a multi-valued {@link Property} for the
     * {@link commonj.sdo.ChangeSummary} without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for non-sequenced multi-valued properties.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The new value.
     * @see DataStrategy#addToOldPropertyWithoutCheck(Property, Object)
     */
    public void addToOldPropertyWithoutCheck(Property pProperty, Object pValue) {
        _dataStrategy.addToOldPropertyWithoutCheck(pProperty, pValue);
    }

    /**
     * Sets the old {@link Sequence} for the {@link commonj.sdo.ChangeSummary}
     * without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pSettings The List of Property-value-pairs.
     * @see DataStrategy#setSequenceWithoutCheck(List)
     */
    public void setOldSequenceWithoutCheck(List<Setting> pSettings) {
        _dataStrategy.setOldSequenceWithoutCheck(pSettings);
    }

    /**
     * Adds a value to the old {@link Sequence} for the {@link commonj.sdo.ChangeSummary}
     * without any containment or opposite checks.
     * If this method is used, containment and opposite have to be maintained
     * manually.
     * This method is only supported for sequenced data objects.
     * @param pProperty The Property the value is assigned to.
     * @param pValue The value.
     * @see DataStrategy#addToOldSequenceWithoutCheck(Property, Object)
     */
    public void addToOldSequenceWithoutCheck(Property pProperty, Object pValue) {
        _dataStrategy.addToOldSequenceWithoutCheck(pProperty, pValue);
    }

    /**
     * Sets the old container and containment property for the
     * {@link commonj.sdo.ChangeSummary} without maintainig the containment
     * property.
     * @param pContainer The old container.
     * @param pProperty The old containment property.
     * @see DataStrategy#setOldContainerWithoutCheck(DataObject, Property)
     */
    public void setOldContainerWithoutCheck(DataObject pContainer, Property pProperty) {
        _dataStrategy.setOldContainerWithoutCheck(pContainer, pProperty);
    }

    /**
     * Returns true if the read-only mode is enabled.
     * @return true if the read-only mode is enabled.
     * @see DataStrategy#isReadOnlyMode()
     */
    public boolean isReadOnlyMode() {
        return _dataStrategy.isReadOnlyMode();
    }

    /**
     * Set it to false to disable the read-only checks.
     * @param pActivated true, to enable and false to disable.
     * @see DataStrategy#setReadOnlyMode(boolean)
     */
    public void setReadOnlyMode(boolean pActivated) {
        _dataStrategy.setReadOnlyMode(pActivated);
    }

    /**
     * Determines if this object is in scope of a ChangeSummary and if it is
     * logging.
     * @return true if logging is enabled.
     */
    public boolean isLogging() {
        return _dataStrategy.isInitialScope();
    }

    /**
     * Returns the default value if the value is null. So auto-unboxing does not
     * throw a {@link NullPointerException}.
     * @param <T> The wrapper class of a simple type.
     * @param pValue The value or null.
     * @param pDefault The default value of the simple type.
     * @return The value if not null, the default value otherwhise.
     */
    private <T>T convertNull(T pValue, T pDefault) {
        if (pValue == null) {
            return pDefault;
        }
        return pValue;
    }

    /**
     * Refines the type of the DataObject to a more concrete type. The refinement
     * of the type is only for internal use.
     * @param pType The new type.
     * @throws IllegalArgumentException if the new type is no extension of the current type.
     */
    public void refineType(SdoType pType) {
        final Type oldType = getType();
        if (pType == oldType) {
            return;
        }
        if (pType.isAssignableType(oldType)) {
            return;
        }
        Class oldInterfaceClass = oldType.getInstanceClass();
        if (!URINamePair.MIXEDTEXT_TYPE.equalsUriName(oldType)) { //TODO is could be a bug
            if (!((SdoType)oldType).isAssignableType(pType)) {
                throw new IllegalArgumentException("Cannot change type from "+oldType+" to "+pType);
            }
        }
        TypeHelperImpl typeHelper = (TypeHelperImpl)getHelperContext().getTypeHelper();
        _typeAndContext = typeHelper.getTypeAndContext(pType);

        _dataStrategy = _dataStrategy.refineType(oldType, pType);
        Class interfaceClass = pType.getInstanceClass();
        if ((interfaceClass != null) && (interfaceClass != oldInterfaceClass)) {
            DataFactoryImpl dataFactory = (DataFactoryImpl)getHelperContext().getDataFactory();
            final DataObjectDecorator f = dataFactory.createProxy(this, interfaceClass);
            setFacade(f);
        }
        checkDefineSpecialProperties();
    }

    public void simplifyOpenContent() {
        _dataStrategy = _dataStrategy.simplifyOpenContent();
    }

    public void stealCurrentState(DataObjectDecorator pCurrentStateObject) {
        refineType((SdoType)pCurrentStateObject.getType());
        GenericDataObject currentStateGdo = pCurrentStateObject.getInstance();
        DataStrategy newDataStrategy = currentStateGdo.getDataStrategy();
        newDataStrategy.stealChangeState(_dataStrategy);
        _dataStrategy = newDataStrategy;
        _dataStrategy.setDataObject(this);
    }

    private void checkDefineSpecialProperties() {
        boolean readOnlyActivated = isReadOnlyMode();
        setReadOnlyMode(false);
        int propertyIndex = ((SdoType)getType()).getCsPropertyIndex();
        if ((propertyIndex >= 0) && (get(propertyIndex) == null)) {
            ChangeSummary changeSummary = new ChangeSummaryImpl(this);
            set(propertyIndex, changeSummary);
        }
        setReadOnlyMode(readOnlyActivated);
    }

    private static class Unset implements Serializable {

        private static final long serialVersionUID = -4463537482806889436L;

        private Unset() {
        }

        private Object readResolve() {
            return GenericDataObject.UNSET;
        }

        @Override
        public String toString() {
            return "[UNSET]";
        }

    }

    public DataObjectDecorator getOldStateFacade() {
        return _dataStrategy.getOldStateFacade();
    }

    public DataStrategy getDataStrategy() {
        return _dataStrategy;
    }
    private final static Logger LOGGER = Logger.getLogger(GenericDataObject.class.getName());

    public Object invoke(Object proxy, Method pMethod, Object[] args)
            throws Throwable { //$JL-EXC$
        Invoker invoker = ((SdoType)getType()).getInvokerForMethod(pMethod);
        return invoker.invoke(this, pMethod, args);
    }
    public Object project() {
        Class targetClass = getType().getInstanceClass();
        if (targetClass==null || targetClass.isInterface()) {
            throw new RuntimeException("The project() method can only be used for classes that have POJO facades.");
        }
        return recurseProjection(targetClass, new HashMap<GenericDataObject,Object>());
    }
    public  <T> T recurseProjection(Class<T> targetClass, Map<GenericDataObject,Object> map) {
        if (map.containsKey(this)) {
            return (T)map.get(this);
        }
        try {
            T projection = targetClass.newInstance();
            map.put(this, projection);
            PojoDataStrategy dataStrategy = new ProjectingPojoDataStrategy(projection,null);
            dataStrategy.setDataObject(this);
            for (Property p: (List<Property>)getType().getProperties()) {
                if (p.getType().isDataType()) {
                    if (p.isMany()) {
                        dataStrategy.setPojoValue(p, new ArrayList(getList(p)));
                    } else {
                        dataStrategy.setPojoValue(p, get(p));
                    }
                } else if (p.isMany()) {
                    List ret = new ArrayList();
                    for (DataObjectDecorator o: (List<DataObjectDecorator>)getList(p)) {
                        ret.add(o.getInstance().recurseProjection(dataStrategy.getTargetClass(p), map));
                    }
                    dataStrategy.setPojoValue(p, ret);
                } else {
                    DataObjectDecorator dod = (DataObjectDecorator)getDataObject(p);
                    if (dod!=null) {
                        GenericDataObject o = dod.getInstance();
                        dataStrategy.setPojoValue(p, o.getInstance().recurseProjection(dataStrategy.getTargetClass(p), map));
                    }
                }
            }
            return projection;
        } catch (InstantiationException e) {
            throw new RuntimeException("could not project",e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("could not project",e);
        }
    }
    public DataObject project(HelperContext pTargetCtx) {
        SapHelperContext myCtx = (SapHelperContext)getHelperContext();
        if (myCtx.equals(pTargetCtx)) {
            return getFacade();
        }
        //TODO: Hierarchical structure of Contexts....
        if (myCtx.isAssignableContext(pTargetCtx)) {
            //that is save, but resulting data graph has objects of different HelperContexts
            return getFacade();
        }
        SapHelperContext typeCtx = (SapHelperContext)((SdoType)getType()).getHelperContext();
        if (typeCtx.isAssignableContext(pTargetCtx)) {
            //in most of that cases no projection is needed,
            //but objects of types could be added that are unknown to the root objects HelperContexts
            return getFacade();
        }
        DelegatingDataStrategy dataStrategy = getDelegatingDataStrategy();
        GenericDataObject tmp = dataStrategy.findProjection(pTargetCtx);
        if (tmp != null) {
            return tmp.getFacade();
        }

        Type myType = getType();
        TypeAndContext typeAndContext = ((TypeHelperImpl)pTargetCtx.getTypeHelper()).getTypeAndContext(myType.getURI(), myType.getName());
        Type type = typeAndContext.getSdoType();
        if (type == null) {
            throw new IllegalArgumentException("Type " + myType.getURI() + '#'
                + myType.getName() + " is unknown in HelperContext " + pTargetCtx);
        }
        final GenericDataObject retDO = new GenericDataObject(typeAndContext, new ProjectionDataStrategy(this, dataStrategy));
        dataStrategy.addProjection(retDO);
        DataObjectDecorator ret = retDO;
        DataFactoryImpl dataFactory = (DataFactoryImpl)myCtx.getDataFactory();
        Class targetClass = type.getInstanceClass();
        if (targetClass != null) {
            ret = (DataObjectDecorator)dataFactory.facade(retDO, targetClass, type);
            if (!targetClass.isInterface()) {
                fillIn(ret, retDO);
            }
        }
        int[] map = ((ProjectionDataStrategy)retDO.getDataStrategy()).getPropertyMap(retDO.getType(),myType);
        List<Property> instanceProperties = retDO.getInstanceProperties();
        List<Property> myProperties = myType.getProperties();
        for (int i=0; i < instanceProperties.size(); i++) {
            SdoProperty sdoP = (SdoProperty)instanceProperties.get(i);
            if (sdoP.isContainment()) {
                Property originalProp;
                if (i < myProperties.size()) {
                    originalProp = myProperties.get(map[i]);
                } else {
                    if (((SapHelperContext)sdoP.getHelperContext()).isAssignableContext(pTargetCtx)) {
                        originalProp = sdoP;
                    } else {
                        throw new IllegalArgumentException("Property " + sdoP.getName() + " is unknown in context " + pTargetCtx);
                    }
                }
                if (!sdoP.isMany()) {
                    DataObject o = getDataObject(originalProp);
                    attachProjection(retDO, pTargetCtx, sdoP, o);
                } else {
                    for (DataObject o: (List<DataObject>)getList(originalProp)) {
                        attachProjection(retDO, pTargetCtx,sdoP,o);
                    }
                }
            }
        }
        return ret;
    }

    public boolean getXsiNil() {
        return _dataStrategy.getXsiNil();
    }

    /**
     * Defines if the DataObject is nil. In case of null, for {@link #getXsiNil()}
     * the default behavior is applied.
     * @param pXsiNil true/false or null for unknown.
     */
    public void setXsiNilWithoutCheck(Boolean pXsiNil) {
        _dataStrategy.setXsiNilWithoutCheck(pXsiNil);
    }

    /**
     * Defines if the DataObject is nil.
     * @param pXsiNil true/false.
     */
    public void setXsiNil(boolean pXsiNil) {
        _dataStrategy.setXsiNil(pXsiNil);
    }

    public Property getInstanceProperty(String uri, String xsdName, boolean isElement) {
        return _dataStrategy.getProperty(uri, xsdName, isElement);
    }

    public void trimMemory() {
        _dataStrategy.trimMemory();
    }

    private DelegatingDataStrategy getDelegatingDataStrategy() {
        if (_dataStrategy instanceof ProjectionDataStrategy) {
            return ((ProjectionDataStrategy)_dataStrategy).getDelegate();
        }
        if (!(_dataStrategy instanceof DelegatingDataStrategy)) {
            _dataStrategy = new DelegatingDataStrategy((AbstractDataStrategy)_dataStrategy);
        }
        return (DelegatingDataStrategy)_dataStrategy;
    }
    private void fillIn(DataObjectDecorator projection, GenericDataObject retDO) {
        AbstractPojoDataStrategy dataStrategy = new EnhancerDataStrategy(retDO);
        dataStrategy.setPojo(projection);
        dataStrategy.setDataObject(retDO);
        for (Property p: (List<Property>)retDO.getType().getProperties()) {
                if (p.isMany()) {
                    dataStrategy.setPojoValue(p, retDO.getList(p));
                } else {
                    dataStrategy.setPojoValue(p, retDO.get(p));
                }
        }


    }
    private static void attachProjection(GenericDataObject parentDO, HelperContext ctx, SdoProperty sdoP, DataObject o) {
        if (o instanceof DataObjectDecorator) {
            DataObject subDO = ((DataObjectDecorator)o).getInstance().project(ctx);
            GenericDataObject gdo = ((DataObjectDecorator)subDO).getInstance();
            PropValue propValue = parentDO.getPropValue(sdoP, true, true);
            gdo.internAttach(propValue, false);
        }
    }
}
