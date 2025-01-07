package com.sap.sdo.impl.objects;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

import com.sap.sdo.impl.objects.DataStrategy.State;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.impl.types.Invoker;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.DelegatingDataObjectDecorator;
import com.sap.sdo.impl.types.builtin.TypeLogic;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.ChangeSummary.Setting;

public class OldStateDataObject extends DelegatingDataObjectDecorator implements InternalDataObjectModifier, InvocationHandler {
    
    private static final long serialVersionUID = 6825792953679233572L;
    private DataObjectDecorator _facade;
    private static final Method GET_METHOD;
    static {
        try {
            GET_METHOD = DataObject.class.getMethod("get", new Class[]{Property.class});
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static DataObjectDecorator getOldStateFacade(final DataObjectDecorator pDelegate) {
        OldStateDataObject oldStateDataObject = new OldStateDataObject(pDelegate);
        Class interfaceClass = pDelegate.getType().getInstanceClass();
        final DataObjectDecorator facade;
        if (interfaceClass == null) {
            facade = (DataObjectDecorator)Proxy.newProxyInstance(
                DataObjectDecorator.class.getClassLoader(), new Class[]{DataObjectDecorator.class}, oldStateDataObject);
        } else {
            DataFactoryImpl dataFactory = (DataFactoryImpl)pDelegate.getInstance().getHelperContext().getDataFactory();
            facade = dataFactory.createProxy(oldStateDataObject, interfaceClass);
        }
        oldStateDataObject.setFacade(facade);
        return facade;
    }
    
    private OldStateDataObject(final DataObjectDecorator pDelegate) {
        super(pDelegate.getInstance());
    }

    public Object get(int propertyIndex) {
        Property property = getInstanceProperty(propertyIndex);
        return get(property);
    }

    public Object get(Property property) {
        if (property == null) {
            throw new NullPointerException("Property is null");
        }
        Setting oldSetting = getInstance().getOldValue(property);
        Object oldValue;
        if (oldSetting == null) {
            oldValue = wrapValue(property,getInstance().get(property));
        } else {
            oldValue = oldSetting.getValue();
        }
        return oldValue;
    }
    
    @Override
    public Object get(String path) {
        Property property = getInstanceProperty(path);
        return get(property);
    }

    public DataObject getContainer() {
        PropValue oldContainmentPropValue = getInstance().getOldContainmentPropValue();
        if (oldContainmentPropValue == null) {
            return null;
        }
        return ((DataObjectDecorator)oldContainmentPropValue.getDataObject()).getInstance().getOldStateFacade();
    }

    public Property getContainmentProperty() {
        PropValue oldContainmentPropValue = getInstance().getOldContainmentPropValue();
        if (oldContainmentPropValue == null) {
            return null;
        }
        return oldContainmentPropValue.getProperty();
    }

    public DataGraph getDataGraph() {
        DataObject rootObject = getRootObject();
        if (rootObject instanceof DataGraph) {
            return (DataGraph)rootObject;
        }
        return null;
    }

    public List getInstanceProperties() {
        return getInstance().getOldInstanceProperties();
    }

    public Property getInstanceProperty(String propertyName) {
        for (Property property: getInstance().getOldInstanceProperties()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
            if (property.getAliasNames().contains(propertyName)) {
                return property;
            }
        }
        return null;
    }

    private Property getInstanceProperty(int propertyIndex) {
        return getInstance().getOldInstanceProperties().get(propertyIndex);
    }

    public DataObject getRootObject() {
        DataObject container = getContainer();
        if (container == null) {
            return getFacade();
        }
        return container.getRootObject();
    }
    
    public ChangeSummary getChangeSummary() {
        int propertyIndex = ((SdoType)getInstance().getType()).getCsPropertyIndex();
        if (propertyIndex >= 0) {
            return (ChangeSummary)get(propertyIndex);
        }
        DataObject parent = getContainer();
        if (parent == null) {
            return null;
        }
        return parent.getChangeSummary();
    }


    public Sequence getSequence() {
        return getInstance().getOldSequence();
    }

    @Override
    public boolean isSet(Property property) {
        Setting oldSetting = getInstance().getOldValue(property);
        if (oldSetting == null) {
            return getInstance().isSet(property);
        }
        return oldSetting.isSet();
    }

    @Override
    public boolean isSet(int propertyIndex) {
        Property property = getInstanceProperty(propertyIndex);
        return isSet(property);
    }

    @Override
    public boolean isSet(String path) {
        Property property = getInstanceProperty(path);
        return isSet(property);
    }

    public void addPropertyValueWithoutCheck(Property pProperty, Object pValue) {
        getInstance().addOldPropertyValueWithoutCheck(pProperty, pValue);
        
    }

    public void unsetPropertyWithoutCheck(Property pProperty) {
        getInstance().unsetOldPropertyWithoutCheck(pProperty);
    }

    public void addToPropertyWithoutCheck(Property pProperty, Object pValue) {
        getInstance().addToOldPropertyWithoutCheck(pProperty, pValue);
    }

    public void addToSequenceWithoutCheck(Property pProperty, Object pValue) {
        getInstance().addToOldSequenceWithoutCheck(pProperty, pValue);
    }

    public void setChangeStateWithoutCheck(State pState) {
        getInstance().setChangeStateWithoutCheck(pState);
    }

    public void setContainerWithoutCheck(DataObject pContainer, Property pProperty) {
        getInstance().setOldContainerWithoutCheck(pContainer, pProperty);
        
    }

    public void setPropertyWithoutCheck(Property pProperty, Object pValue) {
        getInstance().setOldPropertyWithoutCheck(pProperty, pValue);
    }

    public void setSequenceWithoutCheck(List<Setting> pSettings) {
        getInstance().setOldSequenceWithoutCheck(pSettings);
    }

    public Property findOpenProperty(String pUri, String pXsdName, boolean pIsElement) {
        return getInstance().findOpenProperty(pUri, pXsdName, pIsElement);
    }

    public void trimMemory() {
        getInstance().trimMemory();
    }

    public static Object wrapValue(Property pProperty, Object pValue) {
        if (pProperty.getType().isDataType()) {
            return pValue;
        }
        if (pValue instanceof DataObjectDecorator) {
            return ((DataObjectDecorator)pValue).getInstance().getOldStateFacade();
        } else if (pProperty.isMany()) {
            if (pValue == null) {
                return Collections.EMPTY_LIST;
            }
            return new OldStateList((List)pValue);
        }
        return pValue;
    }
    
    public Object invoke(Object proxy, Method pMethod, Object[] args) throws Throwable { //$JL-EXC$
        Method method = pMethod;
        Class returnType = null;
        if (TypeLogic.DATA_OBJECT_METHODS.containsKey(pMethod) ) {
            String methodName = pMethod.getName();
            //This is a hack, to avoid to implement all getXXX(...) methods
            if ((methodName.length() >= 3) && methodName.startsWith("get") && (pMethod.getParameterTypes().length == 1)
                    && !methodName.equals("getProperty") && !methodName.equals("getInstanceProperty")) {
                returnType = pMethod.getReturnType();
                return getConvertedValue(returnType, args);
            }
            
        }
        Invoker invoker = ((SdoType)getInstance().getType()).getInvokerForMethod(method);
        if (!invoker.isModify()) {
            Object result = invoker.invoke(this, method, args);
            return result;
        }
        throw new UnsupportedOperationException("Old state is read-only");
    }
    
    private Object getConvertedValue(Class pReturnType, Object[] args) throws Throwable { //$JL-EXC$
        Property property = null;
        if (args[0] instanceof Integer) {
            property = getInstanceProperty((Integer)args[0]);
        } else if (args[0] instanceof String) {
            property = getInstanceProperty((String)args[0]);
        } else if (args[0] instanceof Property) {
            property = (Property)args[0];
        } else {
            throw new UnsupportedOperationException();
        }
        Object defaultValue;
        Class returnType;
        if (pReturnType.isPrimitive()) {
            if (pReturnType==Boolean.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_BOOLEAN;
                returnType = Boolean.class;
            } else if (pReturnType==Integer.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_INT;
                returnType = Integer.class;
            } else if (pReturnType==Character.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_CHAR;
                returnType = Character.class;
            } else if (pReturnType==Byte.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_BYTE;
                returnType = Byte.class;
            } else if (pReturnType==Short.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_SHORT;
                returnType = Short.class;
            } else if (pReturnType==Long.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_LONG;
                returnType = Long.class;
            } else if (pReturnType==Float.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_FLOAT;
                returnType = Float.class;
            } else if (pReturnType==Double.TYPE) {
                defaultValue = JavaSimpleType.DEFAULT_DOUBLE;
                returnType = Double.class;
            } else {
                throw new IllegalArgumentException("cannot access getter with return type " + pReturnType.getName());
            }
        } else {
            defaultValue = null;
            returnType = pReturnType;
        }
        Object result = null;
        if (property != null) {
            args[0] = property;
            Invoker invoker = ((SdoType)getInstance().getType()).getInvokerForMethod(GET_METHOD);
            result = invoker.invoke(this, GET_METHOD, args);
            if (result == null) {
                result = property.getDefault();
            }
            result = AbstractDataStrategy.convertValue(result, property, returnType);
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    public DataObjectDecorator getFacade() {
        return _facade;
    }

    public void setFacade(DataObjectDecorator pFacade) {
        _facade = pFacade;
    }

    @Override
    public InternalDataObjectModifier getInternalModifier() {
        return this;
    }

    private static class OldStateList extends AbstractList implements RandomAccess {
        
        private final List _delegateList;

        public OldStateList(final List pDelegateList) {
            _delegateList = pDelegateList;
        }

        @Override
        public Object get(int pIndex) {
            Object value = _delegateList.get(pIndex);
            if (value instanceof DataObjectDecorator) {
                return ((DataObjectDecorator)value).getInstance().getOldStateFacade();
            }
            return value;
        }

        @Override
        public int size() {
            return _delegateList.size();
        }
        
    }

}
