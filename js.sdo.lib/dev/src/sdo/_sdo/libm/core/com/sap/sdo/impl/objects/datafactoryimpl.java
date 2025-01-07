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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.strategy.enhancer.EnhancerDataObjectFactory;
import com.sap.sdo.impl.objects.strategy.enhancer.EnhancerDataStrategy;
import com.sap.sdo.impl.objects.strategy.pojo.PojoDataObjectFactory;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeAndContext;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeLogicFacade;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.builtin.DataGraphType.DataGraphLogic;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;

public class DataFactoryImpl implements SapDataFactory {
    
    private final HelperContext _helperContext;
    private final Map<ClassLoader, ClassLoader> _proxyClassLoaders = new HashMap<ClassLoader, ClassLoader>();
    
    private DataFactoryImpl(HelperContext pHelperContext) {
        _helperContext = pHelperContext;
    }

    public static DataFactory getInstance(HelperContext pHelperContext) {
        // to avoid illegal instances
        DataFactory dataFactory = pHelperContext.getDataFactory();
        if (dataFactory != null) {
            return dataFactory;
        }
        return new DataFactoryImpl(pHelperContext);
    }
    
    public DataObject create(String uri, String typeName) {
        TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
        TypeAndContext typeAndContext = typeHelper.getTypeAndContext(uri,typeName);
        if (typeAndContext==null) {
            throw new IllegalArgumentException("type "+new URINamePair(uri,typeName)+" not found");
        }
        return createDataObject(typeAndContext);
    }

    public DataObject create(Class interfaceClass) {
        Type t = _helperContext.getTypeHelper().getType(interfaceClass);
        if (t==null) {
            throw new IllegalArgumentException("type for class "+interfaceClass+" not found");
        }
        return create(t);
    }

    public DataObject create(Type type) {
        TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
        return createDataObject(typeHelper.getTypeAndContext((SdoType)type));
    }
    
    private DataObject createDataObject(TypeAndContext pTypeAndContext) {
        SdoType type = pTypeAndContext.getSdoType();
        if (type.isDataType() && !URINamePair.CHANGESUMMARY_TYPE.equalsUriName(type)) {
            return createWrapper(type, ((SdoType)type).getDefaultValue());
//          throw new IllegalArgumentException("cannot use data factory to create the data type "+type.getURI()+":"+type.getName());
        }
        if (type.isAbstract()) {
            throw new IllegalArgumentException("cannot create the abstract data type "+type.getURI()+":"+type.getName());
        }
        Class intf = type.getInstanceClass();
        if (intf == null) {
            return new GenericDataObject(pTypeAndContext);
        } else if (intf.isInterface()) {
            return facade(new GenericDataObject(pTypeAndContext),intf,type);
        } else {
            try {
                return (DataObject)EnhancerDataObjectFactory.createDataObject(intf, pTypeAndContext);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("unexpected exception",e);
            } 
        }
    }
    
    public DataObject cast(Object pojo) {
    	if (pojo == null) {
    		return null;
    	}
    	if (pojo instanceof DataObject) {
    		return (DataObject)pojo;
    	}
        final Type t = _helperContext.getTypeHelper().getType(pojo.getClass());
        if (t == null) {
            throw new IllegalArgumentException("type for class "+pojo.getClass()+" not found");
        }
        return PojoDataObjectFactory.createDataObject(t, pojo);
    }
    
    public DataObject project(DataObject pDataObject) {
        if (pDataObject == null) {
            return null;
        }
        try {
            GenericDataObject gdo = ((DataObjectDecorator)pDataObject).getInstance();
            return gdo.project(_helperContext);
        } catch (ClassCastException cce) {
//            throw new IllegalArgumentException("DataObject does not support projection "
//                + pDataObject, cce);
            return pDataObject;
        }
    }

    public DataObject createWrapper(Type type, Object value) {
        DataObject wrapper = create(OpenType.getInstance());
        final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
        propObj.setString(PropertyType.NAME, TypeType.VALUE);
        propObj.set(PropertyType.TYPE, type);
        propObj.setBoolean(PropertyType.MANY, false);
        propObj.setBoolean(PropertyType.getXmlElementProperty(), true);
        propObj.setBoolean(PropertyType.getSimpleContentProperty(),true);
        Property property = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        wrapper.set(property, value);
        return wrapper;
    }

    /**
     * provide interface facade over given data object
     */
    public DataObject facade(GenericDataObject d, Class intf, Type type) {
        if (intf == DataObject.class) {
            return d;
        }
    	final DataObjectDecorator f;
    	if (type instanceof IHasDelegator) {
    		try {
				Class<? extends DataObject> clazz = ((IHasDelegator)type).getFacadeClass();
				if (clazz.equals(TypeLogicFacade.class)) {
				    f = new TypeLogicFacade(d, _helperContext);
                } else {
                    Constructor<? extends DataObject> ctr = clazz.getConstructor(new Class[]{DataObject.class});
    				f = (DataObjectDecorator)ctr.newInstance(d);
                }
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(e);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException(e.getTargetException());
			}
    	} else {
    		f = createProxy(d, intf);
    	}
        d.setFacade(f);
        return f;
    }

    public DataObjectDecorator createProxy(InvocationHandler pDataObject, Class pInterface) {
        ClassLoader intfClassLoader = pInterface.getClassLoader();
        ClassLoader proxyClassLoader = _proxyClassLoaders.get(intfClassLoader);
        if (proxyClassLoader != null) {
            return createProxy(proxyClassLoader, pInterface, pDataObject);
        }
        DataObjectDecorator facade;
        proxyClassLoader = SapHelperProviderImpl.getClassLoader();
        try {
            facade = createProxy(proxyClassLoader, pInterface, pDataObject);
        } catch (IllegalArgumentException e1) {
            proxyClassLoader = intfClassLoader;
            try {
                facade = createProxy(proxyClassLoader, pInterface, pDataObject);
            } catch (IllegalArgumentException e2) {
                proxyClassLoader = new CombiClassLoader(proxyClassLoader);
                facade = createProxy(proxyClassLoader, pInterface, pDataObject);
            }
        }
        _proxyClassLoaders.put(intfClassLoader, proxyClassLoader);
        return facade;
    }
        
    private DataObjectDecorator createProxy(ClassLoader pClassLoader, Class pInterface, InvocationHandler pDataObject) {
       if (pInterface.isInterface()) {
    	   return (DataObjectDecorator)Proxy.newProxyInstance(pClassLoader,
    	            new Class[]{pInterface, DataObjectDecorator.class}, pDataObject);
       } else {

       	try {
       		GenericDataObject retDO = (GenericDataObject)pDataObject;
			EnhancerDataStrategy dataStrategy = EnhancerDataObjectFactory.createProjection(pInterface, retDO);
			for (Property p: (List<Property>)retDO.getType().getProperties()) {
				if (p.isMany()) {
					dataStrategy.setPojoValue(p, retDO.getList(p));
				} else {
					dataStrategy.setPojoValue(p, retDO.get(p));
				}
			}
			return (DataObjectDecorator)dataStrategy.getPojo();
       	} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("unexpected exception",e);
		} 
       }
    }
    
    private static class CombiClassLoader extends ClassLoader {
        
        private final ClassLoader _classLoader;
        private final int _hashCode;

        public CombiClassLoader(ClassLoader pClassLoader) {
            _classLoader = pClassLoader;
            _hashCode = pClassLoader.hashCode() ^ DataObjectDecorator.class.getClassLoader().hashCode();
        }

        @Override
        public Class<?> loadClass(String pName) throws ClassNotFoundException {
            try {
                return DataObjectDecorator.class.getClassLoader().loadClass(pName);
            } catch (ClassNotFoundException e) {
                return _classLoader.loadClass(pName);
            }
        }

        @Override
        public boolean equals(Object pObj) {
            if (!(pObj instanceof CombiClassLoader)) {
                return false;
            }
            return _classLoader.equals(((CombiClassLoader)pObj)._classLoader);
        }

        @Override
        public int hashCode() {
            return _hashCode;
        }
        
    }
}
