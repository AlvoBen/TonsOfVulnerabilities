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
package com.sap.sdo.impl.types.builtin;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.CopyHelperImpl;
import com.sap.sdo.impl.objects.DataFactoryImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.Invoker;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperDelegator;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.DataGraphType.XsdType;
import com.sap.sdo.impl.types.java.JavaTypeFactory;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.util.TypeConverterList;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
/**
 *  Implements methods that instances of commonj.sdo.Type need, but which are not "anemic",
 *  that is, cannot be implemented using SDO getter and setter methods.  We allow "logical
 *  aspects" to be associated with particular types, and these aspects implement the methods
 *  that will be called from the object's invocation handler.
 */
public abstract class TypeLogic<S, D extends DataObject> extends DelegatingDataObject<D> implements SdoType<S>, Serializable {

    private static final long serialVersionUID = -6409105350999166351L;
    private final static Logger LOGGER = Logger.getLogger(TypeHelperDelegator.class.getName());

    static {
        LOGGER.setLevel(Level.FINE);
    }
    private transient D _delegate;
    protected transient List<SdoProperty> _props;
    protected transient List<SdoProperty> _keyProps;
    protected URINamePair _qName;
    protected transient int _csPropertyIndex = -1;
    protected transient int _idPropertyIndex = -1;
    protected transient int _xsdPropertyIndex = -1;
    private transient Map<String,SdoProperty> _nameToProp = new HashMap<String,SdoProperty>();
    private transient final Map<String,String> _javaNameToSdoName = new HashMap<String,String>();
    private transient Map<String,List<SdoProperty>> _xmlNameToElements;
    private transient Map<String,List<SdoProperty>> _xmlNameToAttributes;
    private transient Map<String,Object> _extraData = new HashMap<String,Object>();
    private static final Invoker DIRECT_INVOKER = new DirectInvoker(false);
    private static final Invoker DIRECT_MOD_INVOKER = new DirectInvoker(true);
    public static final Map<Method, Invoker> DATA_OBJECT_METHODS;
    static {
        /* TODO Methods and HashMaps doesn't really fit together, methods with
         * the same name on the same class have the same hashcode and equals
         * on Method is really slow.
         */
        Method[] dataObjectMethods = DataObjectDecorator.class.getMethods();
        Method[] objectMethods = Object.class.getMethods();
        DATA_OBJECT_METHODS = new HashMap<Method, Invoker> (dataObjectMethods.length + objectMethods.length);
        for (Method method : dataObjectMethods) {
            String methodName = method.getName();
            if (methodName.startsWith("set") || methodName.startsWith("create")
                || methodName.equals("unset") || methodName.equals("delete")
                || methodName.equals("detach")) {
                DATA_OBJECT_METHODS.put(method, DIRECT_MOD_INVOKER);
            } else {
                DATA_OBJECT_METHODS.put(method, DIRECT_INVOKER);
            }
        }
        for (Method method : objectMethods) {
            DATA_OBJECT_METHODS.put(method, DIRECT_INVOKER);
        }
    }
    private transient Map<Method,Invoker> _methodToInvoker;
    protected transient boolean _useCache = false;
    private transient List<SdoProperty> _singleOppositeProperties;

    public TypeLogic(D o) {
        _delegate = o;
    }

    /**
     * Creates a QName from the type's URI and Name properties.
     * @return
     */
    public URINamePair getQName() {
        return new URINamePair(getURI(), getName());
    }
    private DataObject getDataObject(Type t) {
        if (t instanceof TypeLogic) {
            return ((TypeLogic)t).getDelegate();
        }
        return (DataObject)t;
    }
    /**
     * Returns true if this type is either the same as or is a superclass of the type
     * supplied as an argument.
     * @param assignableFrom
     * @return
     */
    public boolean isAssignableType(final Type assignableFrom) {
        if (getDelegate() == getDataObject(assignableFrom)) {
            return true;
        }
        if (getDelegate() instanceof Type && URINamePair.DATAOBJECT.equalsUriName((Type)getDelegate())) {
            return !assignableFrom.isDataType();
        }
        final List<Type> baseTypes = assignableFrom.getBaseTypes();
        for (int i = 0; i < baseTypes.size(); i++) {
            if (isAssignableType(baseTypes.get(i))) {
                return true;
            }
        }
        return false;
    }
    public boolean isInstance(final Object o) {
        // Handle the name clash, access the hidden method DO.getType();
        if (o instanceof Property) {
            return getDelegate() instanceof PropertyType;
        }
        if (o instanceof DataObject) {
            return isAssignableType(((DataObject)o).getType());
        }
        if (isDataType()) {
            final Class ic = getInstanceClass();
            return ic.isInstance(o);
        }
        return false;
    }

    public boolean defined() {
        return true;
    }

    public void useCache() {
        _props = null;
        loadProperties();
        _qName = getQName();
        _useCache = true;
    }

    protected void loadProperties() {
        if (_props == null || !_useCache) {
            boolean multipleInheritance = false;
            List<SdoProperty> nonIndexedProps = new ArrayList<SdoProperty>();
            for (Object obj:getList(TypeType.BASE_TYPE)) {
                final Type baseType = (Type)obj;
                List<SdoProperty> baseTypeProps = baseType.getProperties();
                if (multipleInheritance) {
                    baseTypeProps = reindexedProperties(baseTypeProps, nonIndexedProps);
                }
                multipleInheritance = multipleInheritance || (baseTypeProps.size()>0);
                nonIndexedProps.addAll(baseTypeProps);
            }
            List<SdoProperty> declaredProperties = getList(TypeType.PROPERTY);
            SdoProperty[] indexedProps = new SdoProperty[nonIndexedProps.size() + declaredProperties.size()];
            for (Iterator<SdoProperty> it = declaredProperties.iterator(); it.hasNext();) {
                SdoProperty prop = it.next();
                int requestedIndex = prop.getRequestedIndex();
                if (requestedIndex >= 0) {
                    if (requestedIndex >= indexedProps.length) {
                        throw new IllegalArgumentException("property index: "
                            + requestedIndex + " at " + prop.getContainingType().getName()
                            + ' ' + prop.getName() + " must be < " + indexedProps.length);
                    }
                    if (indexedProps[requestedIndex] != null) {
                        throw new IllegalArgumentException(
                            "found more than one property with index: " + requestedIndex
                            + " - " + indexedProps[requestedIndex] + " <> " + prop);
                    }
                    indexedProps[requestedIndex] = prop;
                } else {
                    nonIndexedProps.add(prop);
                }

            }
            Iterator<SdoProperty> nonIndexedPropsIt = nonIndexedProps.iterator();
            for (int i = 0; i < indexedProps.length; i++) {
                if (indexedProps[i] == null) {
                    if (nonIndexedPropsIt.hasNext()) {
                        indexedProps[i] = nonIndexedPropsIt.next();
                    } else {
                        throw new IllegalArgumentException("Should never reach this " + indexedProps);
                    }
                }
            }
            setProperties(Arrays.asList(indexedProps));
        }
    }
    private List<SdoProperty> reindexedProperties(List<SdoProperty> newProps, List<SdoProperty> allProps) {
        List<SdoProperty> ret = new ArrayList<SdoProperty>(newProps.size());
        for (SdoProperty p: newProps) {
            if (!allProps.contains(p)) {
                ret.add(p);
            }
        }
        return ret;
    }
    public List getProperties() {
        loadProperties();
        return _props;
    }
    private void setProperties(final List<SdoProperty> baseTypeProps) {
        _xmlNameToElements = new HashMap<String,List<SdoProperty>>();
        _xmlNameToAttributes = new HashMap<String,List<SdoProperty>>();
        List<SdoProperty> props = new ArrayList<SdoProperty>(baseTypeProps.size());
        ArrayList keyProps = new ArrayList<SdoProperty>(baseTypeProps.size());
        for (SdoProperty prop:baseTypeProps) {
            final int index = props.size();
            prop.setIndex(index);
            props.add(prop);
            final String name = prop.getName();
            SdoProperty storedProp = _nameToProp.get(name);
            if (storedProp == null || (prop.isXmlElement() && !storedProp.isXmlElement())) {
                _nameToProp.put(name, prop);
            }
            if (!prop.isXmlElement() && !_nameToProp.containsKey('@'+name)) {
                _nameToProp.put('@'+name, prop);
            }
            final List<String> aliasNames = prop.getAliasNames();
            for (String aliasName: aliasNames) {
                if (!_nameToProp.containsKey(aliasName)) {
                    _nameToProp.put(aliasName, prop);
                }
            }
            final String javaName = prop.getJavaName();
            if (javaName != null) {
                _javaNameToSdoName.put(javaName, name);
            }
            Map<String,List<SdoProperty>> xmlNameToProps;
            if (prop.isXmlElement()) {
                xmlNameToProps = _xmlNameToElements;
            } else {
                xmlNameToProps = _xmlNameToAttributes;
            }
            String xmlName = prop.getXmlName();
            List<SdoProperty> xmlProps = xmlNameToProps.get(xmlName);
            if (xmlProps == null) {
                xmlProps = new ArrayList<SdoProperty>(1);
                xmlNameToProps.put(xmlName, xmlProps);
            }
            xmlProps.add(prop);
            Type propType = prop.getType();
            if (propType == ChangeSummaryType.getInstance()) {
                _csPropertyIndex = index;
            } else if (propType == XsdType.getInstance()) {
                _xsdPropertyIndex = index;
            }
            if (prop.isKey()) {
                keyProps.add(prop);
                if (keyProps.size() == 1) {
                    _idPropertyIndex = index;
                } else {
                    _idPropertyIndex = -1;
                }
            }
        }
        if (_xmlNameToElements.isEmpty()) {
            _xmlNameToElements = Collections.emptyMap();
        }
        if (_xmlNameToAttributes.isEmpty()) {
            _xmlNameToAttributes = Collections.emptyMap();
        }
        _props = Collections.unmodifiableList(props);
        if (keyProps.isEmpty()) {
            _keyProps = Collections.emptyList();
        } else {
            keyProps.trimToSize();
            _keyProps = Collections.unmodifiableList(keyProps);
        }
    }

    @Override
    public Property getProperty(final String name) {
        loadProperties();
        return _nameToProp.get(name);
    }

    public S convertFromJavaClass(final Object data) {

        if (data == null) {
            return null;
        }

        if (isInstance(data)) {
            if (data instanceof DataObjectDecorator) {
                return (S)((DataObjectDecorator)data).getInstance().getFacade();
            }
            try {
                // only DataType == true left
                S valid = checkFacets((S)data);
                SdoType<S> convertingType = getConvertingType();
                if (convertingType != this) {
                    convertingType.convertFromJavaClass(valid);
                }
                return valid;
            } catch (RuntimeException e) {
                if ("".equals(data)) {
                    return null;
                }
                throw e;
            }
        }
        if (isDataType()) {
            S convertedData;
            SdoType<S> convertingType = getConvertingType();
            if (convertingType != this) {
                convertedData = convertingType.convertFromJavaClass(data);
            } else {
                convertedData = createInstance(data);
            }
            return checkFacets(convertedData);
        }
        Property simpleContentProperty = TypeHelperImpl.getSimpleContentProperty((Type)this);
        if (simpleContentProperty != null) {
            DataObject wrapper = getHelperContext().getDataFactory().create(this);
            wrapper.set(simpleContentProperty, data);
            return (S)wrapper;
        }
        if (this.equals(DataObjectType.getInstance())) {
            Type type = getHelperContext().getTypeHelper().getType(data.getClass());
            return (S)((DataFactoryImpl)getHelperContext().getDataFactory()).createWrapper(type, data);
        }
        throw new ClassCastException("Can not convert from " + data.getClass().getName() +
            " to " + toString());
    }

    private S createInstance(final Object data) {
        final Class<S> instanceClass = getInstanceClass();
        try {
            final Constructor<S> ctr = instanceClass.getConstructor(new Class[]{String.class});
            return ctr.newInstance(new Object[]{data.toString()});
        } catch (NoSuchMethodException e) { //$JL-EXC$
            throw new IllegalArgumentException("Could not find string constructor in instance class: "+instanceClass.getName()+" of type "+getString(TypeType.URI)+":"+getString(TypeType.NAME));
        } catch (IllegalArgumentException e) { //$JL-EXC$
            throw new IllegalArgumentException("Could not call string constructor in instance class: "+instanceClass.getName()+" of type "+getString(TypeType.URI)+":"+getString(TypeType.NAME));
        } catch (InstantiationException e) { //$JL-EXC$
            throw new IllegalArgumentException("Could not call string constructor in instance class: "+instanceClass.getName()+" of type "+getString(TypeType.URI)+":"+getString(TypeType.NAME));
        } catch (IllegalAccessException e) { //$JL-EXC$
            throw new IllegalArgumentException("Could not find string constructor in instance class: "+instanceClass.getName()+" of type "+getString(TypeType.URI)+":"+getString(TypeType.NAME));
        } catch (InvocationTargetException e) { //$JL-EXC$
            LOGGER.severe(e.getLocalizedMessage());
            throw new IllegalArgumentException("Exception invoking string constructor in instance class: "+instanceClass.getName()+" of type "+getString(TypeType.URI)+":"+getString(TypeType.NAME), e);
        }
    }

    private abstract class FacetCheck<C> {
        protected final C _checkValue;

        public FacetCheck(final C pCheckValue) {
            _checkValue = pCheckValue;
        }

        void check(S o) {
            if (o == null) {
                return;
            }
            if (fails(o)) {
                SdoType type = (SdoType)getFacets().getContainer();
                throw new IllegalArgumentException(
                    "Value '"+convertToJavaClass(o, String.class)+"' of type " + type.getQName().toStandardSdoFormat() +
                    " fails validation check: "+getClass().getSimpleName()+"="+_checkValue);
            }
        }

        protected abstract boolean fails(S o);
    }

    private class MaxLength extends FacetCheck<Integer> {
        public MaxLength(Integer pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S o) {
            if (o instanceof List) {
                return ((List<?>)o).size() > _checkValue;
            }
            if (o instanceof byte[]) {
                return ((byte[])o).length > _checkValue;
            }
            return convertToJavaClass(o, String.class).length() > _checkValue;
        }
    }
    private class MinLength extends FacetCheck<Integer> {
        public MinLength(Integer pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S o) {
            if (o instanceof List) {
                return ((List<?>)o).size() < _checkValue;
            }
            if (o instanceof byte[]) {
                return ((byte[])o).length < _checkValue;
            }
            return convertToJavaClass(o, String.class).length() < _checkValue;
        }
    }
    private class Length extends FacetCheck<Integer> {
        public Length(Integer pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S o) {
            if (o instanceof List) {
                return ((List<?>)o).size() != _checkValue;
            }
            if (o instanceof byte[]) {
                return ((byte[])o).length != _checkValue;
            }
            return convertToJavaClass(o, String.class).length() != _checkValue;
        }
    }
    private class Enumeration extends FacetCheck<List<String>> {
        public Enumeration(List<String> pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S o) {
            String test = convertToJavaClass(o, String.class);
            for (String s: _checkValue) {
                if (test.equals(s)) {
                    return false;
                }
            }
            return true;
        }
    }
    private abstract class NumberFacetCheck extends FacetCheck<BigDecimal> {
        public NumberFacetCheck(BigDecimal pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S o) {
            if (!(o instanceof Number)) {
                return true;
            }
            BigDecimal bd = convertToJavaClass(o, BigDecimal.class);
            return fail(bd.compareTo(_checkValue));
        }

        protected abstract boolean fail(int compare);
    }
    private class MinExclusive extends NumberFacetCheck {

        public MinExclusive(BigDecimal pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fail(int compare) {
            return compare <= 0;
        }
    }
    private class MinInclusive extends NumberFacetCheck {

        public MinInclusive(BigDecimal pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fail(int compare) {
            return compare < 0;
        }
    }
    private class MaxExclusive extends NumberFacetCheck {

        public MaxExclusive(BigDecimal pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fail(int compare) {
            return compare >= 0;
        }
    }
    private class MaxInclusive extends NumberFacetCheck {

        public MaxInclusive(BigDecimal pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fail(int compare) {
            return compare > 0;
        }
    }
    private class Pattern extends FacetCheck<List<String>> {
        public Pattern(List<String> pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S o) {
            String test = convertToJavaClass(o, String.class);
            for (String s: _checkValue) {
                s = s.replace("\\i", "[\\p{Graph}]"); //TODO
                s = s.replace("\\c", "[\\p{Graph}]"); //TODO see http://www.w3.org/TR/xmlschema-2/#dt-regex
                if (test.matches(s)) {
                    return false;
                }
            }
            return true;
        }
    }
    private class TotalDigits extends FacetCheck<Integer> {
        public TotalDigits(Integer pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S pO) {
            if (!(pO instanceof Number)) {
                return true;
            }
            BigDecimal bd = convertToJavaClass(pO, BigDecimal.class);
            int digits = bd.precision();
            if (bd.scale() < 0) {
                digits -= bd.scale();
            }
            return digits > _checkValue;
        }
    }
    private class FractionDigits extends FacetCheck<Integer> {
        public FractionDigits(Integer pCheckValue) {
            super(pCheckValue);
        }

        @Override
        protected boolean fails(S pO) {
            if (!(pO instanceof Number)) {
                return true;
            }
            BigDecimal bd = new BigDecimal(convertToJavaClass(pO, String.class));
            return bd.scale() > _checkValue;
        }
    }

    private S checkFacets(S object) {
        if (object == null) {
            return object;
        }
        for (FacetCheck facet: getFacetChecks()) {
            facet.check(object);
        }
        return object;
    }

    private transient List<FacetCheck> _facetChecks;

    private List<FacetCheck> getFacetChecks() {
        if (_facetChecks == null) {
            DataObject facets = getFacets();
            if (facets != null) {
                _facetChecks = new ArrayList<FacetCheck>();
                if (facets.isSet(TypeType.FACET_MAXLENGTH)) {
                    _facetChecks.add(new MaxLength(facets.getInt(TypeType.FACET_MAXLENGTH)));
                }
                if (facets.isSet(TypeType.FACET_MINLENGTH)) {
                    _facetChecks.add(new MinLength(facets.getInt(TypeType.FACET_MINLENGTH)));
                }
                if (facets.isSet(TypeType.FACET_LENGTH)) {
                    _facetChecks.add(new Length(facets.getInt(TypeType.FACET_LENGTH)));
                }
                if (facets.isSet(TypeType.FACET_ENUMERATION)) {
                    _facetChecks.add(new Enumeration(facets.getList(TypeType.FACET_ENUMERATION)));
                }
                if (facets.isSet(TypeType.FACET_MAXINCLUSIVE)) {
                    _facetChecks.add(new MaxInclusive(facets.getBigDecimal(TypeType.FACET_MAXINCLUSIVE)));
                }
                if (facets.isSet(TypeType.FACET_MAXEXCLUSIVE)) {
                    _facetChecks.add(new MaxExclusive(facets.getBigDecimal(TypeType.FACET_MAXEXCLUSIVE)));
                }
                if (facets.isSet(TypeType.FACET_MININCLUSIVE)) {
                    _facetChecks.add(new MinInclusive(facets.getBigDecimal(TypeType.FACET_MININCLUSIVE)));
                }
                if (facets.isSet(TypeType.FACET_MINEXCLUSIVE)) {
                    _facetChecks.add(new MinExclusive(facets.getBigDecimal(TypeType.FACET_MINEXCLUSIVE)));
                }
                if (facets.isSet(TypeType.FACET_PATTERN)) {
                    _facetChecks.add(new Pattern(facets.getList(TypeType.FACET_PATTERN)));
                }
                if (facets.isSet(TypeType.FACET_TOTALDIGITS)) {
                    _facetChecks.add(new TotalDigits(facets.getInt(TypeType.FACET_TOTALDIGITS)));
                }
                if (facets.isSet(TypeType.FACET_FRACTIONDIGITS)) {
                    _facetChecks.add(new FractionDigits(facets.getInt(TypeType.FACET_FRACTIONDIGITS)));
                }
            } else {
                _facetChecks = Collections.emptyList();
            }
        }
        return _facetChecks;
    }

    public <T> T convertToJavaClass(final S data, final Class<T> targetType) {
        if (data == null) {
            return null;
        }
        if (targetType.isInstance(data)) {
            return (T)data;
        }
        if (isDataType()) {
            SdoType<S> convertingType = getConvertingType();
            if (convertingType != this) {
                return convertingType.convertToJavaClass(data, targetType);
            }
            if (targetType != String.class) {
                throw new IllegalArgumentException("Cannot cast to anything other than a string");
            }
            return (T)data.toString();
        }
        if (targetType.equals(DataObject.class)) {
            return (T)data;
        }
        if (targetType.equals(URINamePair.class)) {
            Type type = (Type)data;
            return (T)new URINamePair(type.getURI(), type.getName());
        }
        if (data instanceof DataObject) {
            DataObject dataObject = (DataObject)data;
            SdoProperty valueProperty = TypeHelperImpl.getSimpleContentProperty(dataObject);
            if (valueProperty != null) {
                SdoType<Object> simpleType = (SdoType<Object>)valueProperty.getType();
                return simpleType.convertToJavaClass(dataObject.get(valueProperty), targetType);
            }
            Sequence sequence = dataObject.getSequence();
            if (sequence != null) {
                StringBuilder text = null;
                for (int i = 0; i < sequence.size(); i++) {
                    if (sequence.getProperty(i) == null) {
                        if (text == null) {
                            text = new StringBuilder();
                        }
                        text.append(sequence.getValue(i));
                    } else {
                        text = null;
                        break;
                    }
                }
                if (text != null) {
                    return JavaSimpleType.STRING.convertToJavaClass(text.toString().trim(), targetType);
                }
            }

        }
        // this should only be used in toString()
        if (targetType == String.class) {
            return (T)(getURI() + '#' + getName());
        }
        throw new ClassCastException("Can not convert from " + data.getClass().getName() +
             " to " + targetType.getName());
    }

    private SdoType<S> _convertingType;

    /**
     * This is only useful for data types.
     * @return
     */
    private SdoType<S> getConvertingType() {
        if (_convertingType == null) {
            Class<S> instanceClass = getInstanceClass();
            if (instanceClass.isPrimitive()) {
                _convertingType = (SdoType<S>)getHelperContext().getTypeHelper().getType(instanceClass);
            } else {
                List<SdoType<?>> baseTypes = getBaseTypes();
                if (!baseTypes.isEmpty()) {
                    SdoType<?> baseType = baseTypes.get(0);
                    if (instanceClass.equals(baseType.getInstanceClass())) {
                        _convertingType = (SdoType<S>) baseType;
                    }
                }
                if (_convertingType == null) {
                    _convertingType = this;
                }
            }
            instanceClass = _convertingType.getInstanceClass();
            if (instanceClass == Boolean.class || instanceClass == boolean.class) {
                boolean trueFails = false;
                try {
                    checkFacets((S)Boolean.TRUE);
                } catch (IllegalArgumentException e) {
                    trueFails = true;
                }
                boolean falseFails = false;
                try {
                    checkFacets((S)Boolean.FALSE);
                } catch (IllegalArgumentException e) {
                    falseFails = true;
                }
                if (trueFails && falseFails) {
                    _convertingType = (SdoType<S>)JavaSimpleType.BOOLEAN_01;
                }
            }
        }
        return _convertingType;
    }

    public Property getPropertyFromJavaName(final String javaPropertyName) {
        loadProperties();
        final String ret = _javaNameToSdoName.get(javaPropertyName);
        if (ret != null) {
            return getProperty(ret);
        }
        return getProperty(javaPropertyName);
    }

    public SdoProperty getPropertyFromXmlName(String pUri, final String pXmlName, boolean pIsElement) {
        loadProperties();
        List<SdoProperty> xmlProps;
        if (pIsElement) {
            xmlProps = _xmlNameToElements.get(pXmlName);
        } else {
            xmlProps = _xmlNameToAttributes.get(pXmlName);
        }
        if (xmlProps != null) {
            for (int i=0; i<xmlProps.size(); i++) {
                SdoProperty property = xmlProps.get(i);
                if (pUri.equals(property.getUri())) {
                    return property;
                }
            }

        }
        return null;
    }

    public Invoker getInvokerForMethod(Method pMethod) {
        Invoker invoker = DATA_OBJECT_METHODS.get(pMethod);
        if (invoker != null) {
            return invoker;
        }
        if (_methodToInvoker == null) {
            _methodToInvoker = new HashMap<Method, Invoker>();
        } else {
            invoker = _methodToInvoker.get(pMethod);
        }
        if (invoker == null) {
            final String javaMethodName = pMethod.getName();
            SdoProperty prop = getPropertyFromJavaMethodName(javaMethodName);
            int index = prop.getIndex(this);
            if (javaMethodName.startsWith("set")) {
                if (pMethod.getParameterTypes().length != 1) {
                    throw new UnsupportedOperationException("method "+javaMethodName);
                }
                invoker = new SetInvoker(index);
            } else {
                if (pMethod.getParameterTypes().length != 0) {
                    throw new UnsupportedOperationException("method "+javaMethodName);
                }
                final SdoType type = (SdoType)prop.getType();
                if (type.isDataType()) {
                    Class instanceClass = type.getInstanceClass();
                    if (prop.isMany()) {
                        Class itemClass = JavaTypeFactory.getTypeParameter(pMethod.getGenericReturnType());
                        if (itemClass != instanceClass) {
                            invoker = new GetConverterListInvoker(index, type, itemClass);
                        }
                    } else {
                        Class methodClass = pMethod.getReturnType();
                        if (methodClass != instanceClass) {
                            Object defaultValue = null;
                            if (methodClass.isPrimitive()) {
                                if (methodClass==Boolean.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_BOOLEAN;
                                    methodClass = Boolean.class;
                                } else if (methodClass==Integer.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_INT;
                                    methodClass = Integer.class;
                                } else if (methodClass==Character.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_CHAR;
                                    methodClass = Character.class;
                                } else if (methodClass==Byte.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_BYTE;
                                    methodClass = Byte.class;
                                } else if (methodClass==Short.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_SHORT;
                                    methodClass = Short.class;
                                } else if (methodClass==Long.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_LONG;
                                    methodClass = Long.class;
                                } else if (methodClass==Float.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_FLOAT;
                                    methodClass = Float.class;
                                } else if (methodClass==Double.TYPE) {
                                    defaultValue = JavaSimpleType.DEFAULT_DOUBLE;
                                    methodClass = Double.class;
                                } else {
                                    throw new IllegalArgumentException("cannot access getter with return type " + methodClass.getName());
                                }
                            }
                            Object propDefaultValue = prop.getDefault();
                            if (propDefaultValue != null) {
                                defaultValue = type.convertToJavaClass(propDefaultValue, methodClass);
                            }
                            invoker = new GetAnotherClassInvoker(index, defaultValue, type, methodClass);
                        }
                    }
                }
                if (invoker == null) {
                    invoker = new GetInvoker(index);
                }
            }
            _methodToInvoker.put(pMethod, invoker);
        }
        return invoker;
    }

    public boolean isMixedContent() {
        return getBoolean(TypeType.getMixedProperty());
    }

    public boolean isLocal() {
        return false;
    }

    public String getXmlName() {
        String xmlName = getString(TypeType.getXmlNameProperty());
        if (xmlName != null) {
            return xmlName;
        }
        return getName();
    }

    public String getXmlUri() {
        return getString(TypeType.URI);
    }

    public boolean getAttributeFormDefaultQualified() {
        return getBoolean(TypeType.getAttributeFormDefaultProperty());
    }

    public boolean getElementFormDefaultQualified() {
        return getBoolean(TypeType.getElementFormDefaultProperty());
    }

    public DataObject getFacets() {
        return getDataObject(TypeType.getFacetsProperty());
    }

    /**
     * Implements {@link SdoType#getIndexOfMatchingProperty.
     * @return the index of the first property with type ID, or -1 if none found.
     */
    public int getCsPropertyIndex() {
        return _csPropertyIndex;
    }

    public int getXsdPropertyIndex() {
        return _xsdPropertyIndex;
    }

    /**
     * Implements {@link SdoType#getIdPropertyIndex().
     * @return the index of the first property with type ID, or -1 if none found.
     */
    public int getIdPropertyIndex() {
        return _idPropertyIndex;
    }

    public String getId(final DataObject dataObject) {
        final int idPropId = getIdPropertyIndex();
        if (idPropId >= 0) {
            return dataObject.getString(idPropId);
        }
        if (dataObject instanceof DataObjectDecorator) {
            return String.valueOf(((DataObjectDecorator)dataObject).getInstance()._uniqueId());
        }
        return null;
    }

    public S copy(final S o, final boolean shallow) {
        if (isDataType()) {
            SdoType<S> convertingType = getConvertingType();
            if (convertingType != this) {
                return convertingType.copy(o, shallow);
            } else {
                return createInstance(o);
            }
        }
        final CopyHelperImpl copyHelper = (CopyHelperImpl)getHelperContext().getCopyHelper();
        return (S)copyHelper.copyDataObject((DataObject)o, shallow);
    }

    public S getDefaultValue() {
        if (isDataType()) {
            List<SdoType<?>> baseTypes = getBaseTypes();
            if (baseTypes.size() > 0) {
                return (S)baseTypes.get(0).getDefaultValue();
            }
        }
        return null;
    }
    public List getAliasNames() {
        return getList(TypeType.ALIAS_NAME);
    }
    public List getBaseTypes() {
        return getList(TypeType.BASE_TYPE);
    }
    public List getDeclaredProperties() {
        loadProperties();
        return getList(TypeType.PROPERTY);
    }
    public Class<S> getInstanceClass() {
        String javaClass = getString(TypeType.getJavaClassProperty());
        if (javaClass != null) {
            try {
                return JavaSimpleType.CLASS.convertFromJavaClass(javaClass);
            } catch (IllegalArgumentException e) { //$JL-EXC$
            }
        }
        return null;
    }
    public String getName() {
        return getString(TypeType.NAME);
    }

    public String getURI() {
        final String uri = getString(TypeType.URI);
        if (uri == null || uri.length() == 0) {
            return null;
        }
        return uri;
    }
    public boolean isAbstract() {
        return getBoolean(TypeType.ABSTRACT);
    }
    public boolean isDataType() {
        return getBoolean(TypeType.DATA_TYPE);
    }
    public boolean isOpen() {
        return getBoolean(TypeType.OPEN);
    }
    public boolean isSequenced() {
        return getBoolean(TypeType.SEQUENCED);
    }
    @Override
    protected D getDelegate() {
        return _delegate;
    }

    /**
     * This method collects the properties, that have a single value and an
     * opposite with many values. This is usefull to find referncing properties
     * if a value was changed to clear the index maps.
     * @see com.sap.sdo.impl.objects.strategy.PropertyChangeContext#clearIndexMaps()
     */
    public List<SdoProperty> getSingleOppositeProperties() {
        if (_singleOppositeProperties == null) {
            _singleOppositeProperties = new ArrayList<SdoProperty>();
            for (SdoProperty property: _props) {
                if (!property.isMany() && (property.getOpposite() != null) && property.getOpposite().isMany()) {
                    _singleOppositeProperties.add(property);
                }
            }
            if (_singleOppositeProperties.isEmpty()) {
                _singleOppositeProperties = Collections.emptyList();
            }
        }
        return _singleOppositeProperties;
    }

    private Object readResolve() {
        return getHelperContext().getTypeHelper().getType(_qName.getURI(), _qName.getName());
    }


    public SdoType getSdoType() {
        return this;
    }

    private static class GetInvoker implements Invoker {

        private final int _propertyIndex;

        public GetInvoker(final int pPropertyIndex) {
            _propertyIndex = pPropertyIndex;
        }

        public int getPropertyIndex() {
            return _propertyIndex;
        }

        public Object invoke(DataObject pDataObject, Method pMethod, Object[] pArgs) {
            return pDataObject.get(_propertyIndex);
        }

        public boolean isModify() {
            return false;
        }

    }

    private static class GetAnotherClassInvoker extends GetInvoker {

        private final Object _default;
        private SdoType _type;
        private Class _class;

        public GetAnotherClassInvoker(int pPropertyIndex, Object pDefault, SdoType pType, Class pClass) {
            super(pPropertyIndex);
            _default = pDefault;
            _type = pType;
            _class = pClass;
        }

        @Override
        public Object invoke(DataObject pDataObject, Method pMethod, Object[] pArgs) {
            Object result = super.invoke(pDataObject, pMethod, pArgs);
            if (result == null) {
                return _default;
            }
            return _type.convertToJavaClass(result, _class);
        }

    }

    private static class GetConverterListInvoker<C,T> extends GetInvoker {

        private SdoType<T> _type;
        private Class<C> _itemClass;

        public GetConverterListInvoker(int pPropertyIndex, SdoType<T> pType, Class<C> pItemClass) {
            super(pPropertyIndex);
            _type = pType;
            _itemClass = pItemClass;
        }

        @Override
        public Object invoke(DataObject pDataObject, Method pMethod, Object[] pArgs) {
            List<T> result = (List<T>)super.invoke(pDataObject, pMethod, pArgs);
            return new TypeConverterList<C,T>(result, _type, _itemClass);
        }

    }

    private static class SetInvoker implements Invoker {

        private final int _propertyIndex;

        public SetInvoker(final int pPropertyIndex) {
            _propertyIndex = pPropertyIndex;
        }

        public int getPropertyIndex() {
            return _propertyIndex;
        }

        public Object invoke(DataObject pDataObject, Method pMethod, Object[] pArgs) {
            pDataObject.set(_propertyIndex, pArgs[0]);
            return null;
        }

        public boolean isModify() {
            return true;
        }

    }

    private static class DirectInvoker implements Invoker {

        private final boolean _isModify;

        public DirectInvoker(final boolean pIsModify) {
            _isModify = pIsModify;
        }

        public int getPropertyIndex() {
            return -1;
        }

        public Object invoke(DataObject pDataObject, Method pMethod, Object[] pArgs) throws Throwable {
            try {
                return pMethod.invoke(pDataObject, pArgs);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        public boolean isModify() {
            return _isModify;
        }

    }

    public Object getExtraData(String group, String item) {
        final String key = group + '#' + item;
        return _extraData.get(key);
    }
    public Object putExtraData(String group, String item, Object value) {
        final String key = group + '#' + item;
        return _extraData.put(key,value);
    }
    public SdoProperty getPropertyFromJavaMethodName(final String javaMethodName) {
        int offset;

        if (javaMethodName.startsWith("get")) {
            offset = 3;
        } else if (javaMethodName.startsWith("set")) {
            offset = 3;
        } else if (javaMethodName.startsWith("is")) {
            offset = 2;
        } else {
            throw new UnsupportedOperationException("method "+javaMethodName);
        }
        StringBuilder b = new StringBuilder(javaMethodName.substring(offset));
        b.setCharAt(0, Character.toLowerCase(b.charAt(0)));
        SdoProperty prop = (SdoProperty)getPropertyFromJavaName(b.toString());
        return prop;
    }

    public List<SdoProperty> getOrphanHolderProperties() {
        List<SdoProperty> orphanHolders = new ArrayList<SdoProperty>();
        List<SdoProperty> properties = getProperties();
        int size = properties.size();
        for (int i=0; i<size; ++i) {
            SdoProperty prop = properties.get(i);
            if (prop.isOrphanHolder()) {
                orphanHolders.add(prop);
            }
        }
        return orphanHolders;
    }

    public Type getKeyType() {
        SdoType keyType = (SdoType)_delegate.get(TypeType.KEY_TYPE);
        if (keyType != null) {
            return keyType;
        }
        List<SdoType> baseTypes = getBaseTypes();
        for (int i = 0; i < baseTypes.size(); i++) {
            Type baseKeyType = baseTypes.get(0).getKeyType();
            if (baseKeyType != null) {
                return baseKeyType;
            }
        }
        return null;
    }

    public SdoType getTypeForKeyUniqueness() {
        if (_keyProps.size() == 1 &&
                URINamePair.SCHEMA_ID.equals(_keyProps.get(0).getXsdType())) {
            return DataObjectType.getInstance();
        }
        if (_delegate.isSet(TypeType.KEY_TYPE)) {
            return this;
        }
        List<SdoType> baseTypes = getBaseTypes();
        for (int i = 0; i < baseTypes.size(); i++) {
            SdoType typeForKeyUniqueness = baseTypes.get(0).getTypeForKeyUniqueness();
            if (typeForKeyUniqueness != null) {
                return typeForKeyUniqueness;
            }
        }
        return null;
    }

    public List<SdoProperty> getKeyProperties() {
        return _keyProps;
    }

    public Boolean hasXmlFriendlyKey() {
        if (_keyProps.isEmpty()) {
            return null;
        }
        for (int i = 0; i < _keyProps.size(); i++) {
            SdoProperty keyProperty = _keyProps.get(i);
            if (keyProperty.isOppositeContainment()) {
                if (!Boolean.TRUE.equals(((SdoType)keyProperty.getType()).hasXmlFriendlyKey())) {
                    return false;
                }
            } else {
                if (keyProperty.isXmlElement()) {
                    return false;
                }
                SdoType propType = (SdoType)keyProperty.getType();
                if (!propType.isDataType()) {
                    Type keyType = propType.getKeyType();
                    if (keyType == null || !keyType.isDataType()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public SdoProperty getSimpleContentValueProperty() {
        SdoProperty valueProperty = (SdoProperty)getProperty(TypeType.VALUE);
        if ((valueProperty != null) &&
            Boolean.TRUE.equals(valueProperty.get(PropertyType.getSimpleContentProperty()))) {
            return valueProperty;
        }
        return null;
    }

    public URINamePair getSpecialBaseType() {
        String specialBaseType = getString(TypeType.SPECIAL_BASE_TYPE);
        if (specialBaseType != null) {
            return URINamePair.fromStandardSdoFormat(specialBaseType);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Type: " + getQName().toStandardSdoFormat() + ' ' + super.toString();
    }

}
