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
package com.sap.sdo.impl.objects.strategy.enhancer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeAndContext;

import commonj.sdo.Type;

public class EnhancerDataObjectFactory {

    private EnhancerDataObjectFactory() {
    }

	public static <T> Object createDataObject(Class<T> pClass, TypeAndContext pTypeAndContext) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		EnhancerDataStrategy strategy = new EnhancerDataStrategy();
        GenericDataObject dataObject = new GenericDataObject(pTypeAndContext, strategy);
        Constructor ctr = getConstructorForEnhancedClass(pClass, pTypeAndContext.getSdoType());
        Object pojo = ctr.newInstance(new Object[]{strategy});
        strategy.setPojo(pojo);
        dataObject.setFacade((DataObjectDecorator)pojo);
		return pojo;
	}

	public static <T> Constructor getConstructorForEnhancedClass(Class<T> pClass, Type t) throws IOException, ClassNotFoundException, NoSuchMethodException {
		Map<Class,Class> map = (Map<Class,Class>)((SdoType)t).getExtraData("EnhancerFactory", "classToEnhancedClass");
        if (map == null) {
        	map = new HashMap<Class,Class>();
        	((SdoType)t).putExtraData("EnhancerFactory", "classToEnhancedClass", map);
        }
        Class enhancedClass = map.get(pClass);
        if (enhancedClass == null) {
        	enhancedClass = PojoClassEnhancer.createEnhancedClass(pClass);
        	map.put(pClass, enhancedClass);
        }
        Constructor ctr = enhancedClass.getConstructor(new Class[]{DataStrategy.class});
		return ctr;
	}

	public static EnhancerDataStrategy createProjection(Class pClass, GenericDataObject dataObject) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		EnhancerDataStrategy strategy = new EnhancerDataStrategy(dataObject);
        Constructor ctr = getConstructorForEnhancedClass(pClass, dataObject.getType());
        Object pojo = ctr.newInstance(new Object[]{strategy});
        strategy.setPojo(pojo);
        dataObject.setFacade((DataObjectDecorator) pojo);
        return strategy;
	}
}
