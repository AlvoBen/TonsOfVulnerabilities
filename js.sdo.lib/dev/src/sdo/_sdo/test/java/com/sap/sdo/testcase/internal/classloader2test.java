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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertNotSame;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.ContainerInf;

import commonj.sdo.impl.HelperProvider;

/**
 * @author D042774
 *
 */
public class ClassLoader2Test extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public ClassLoader2Test(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    ClassLoader _classLoader;

    @Before
    public void setUp() throws Exception {
        _classLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(_classLoader);
    }

    @Test
    public void testDifferentClassLoaders() throws Exception {
        URL intfUrl = getUrl(ContainerInf.class);
        ClassLoader intfLoader = new URLClassLoader(new URL[]{intfUrl}, null);
        Class containerInf = intfLoader.loadClass("com.sap.sdo.testcase.typefac.ContainerInf");
        assertNotSame(getClass().getClassLoader(), containerInf.getClassLoader());

        ClassLoader sdoLoader = new SubsetUrlClassLoader(Collections.singleton(intfUrl));
        Thread.currentThread().setContextClassLoader(sdoLoader);

        Class helperProviderClass = sdoLoader.loadClass(HelperProvider.class.getName());
        Method getDefaultContext = helperProviderClass.getMethod("getDefaultContext");
        Object helperContext = getDefaultContext.invoke(null);
        assertNotSame(_helperContext, helperContext);

        Method getDataFactory = helperContext.getClass().getMethod("getDataFactory");
        Object dataFactory = getDataFactory.invoke(helperContext);
        assertNotSame(_helperContext.getDataFactory(), dataFactory);

        Method create = dataFactory.getClass().getMethod("create", Class.class);
        Object container = create.invoke(dataFactory, containerInf);
        System.out.println(container.toString());

    }

    private URL getUrl(Class pClass) throws MalformedURLException {
        String name = pClass.getName();
        String file = name.replace('.', '/') + ".class";
        final URL fileUrl = pClass.getClassLoader().getResource(file);
        String absoluteFile = fileUrl.toString();
        String rootUrl = absoluteFile.substring(0, absoluteFile.lastIndexOf(file));
        if (rootUrl.startsWith("jar:")) {
            rootUrl = rootUrl.substring(4, rootUrl.length()-2);
        }
        return new URL(rootUrl);
    }

    private class SubsetUrlClassLoader extends URLClassLoader {

        private final Set<URL> _excludeUrls;

        public SubsetUrlClassLoader(Set<URL> pExcludeUrls) {
            super(new URL[]{}, null);
            _excludeUrls = pExcludeUrls;
        }

        @Override
        public Class<?> loadClass(String pName) throws ClassNotFoundException {
            try {
                return super.loadClass(pName);
            } catch (ClassNotFoundException e) {
                Class clss = Class.forName(pName);
                try {
                    URL url = getUrl(clss);
                    if (_excludeUrls.contains(url)) {
                        throw new ClassNotFoundException(pName);
                    }
                    addURL(url);
                    System.out.println(url);
                } catch (MalformedURLException e1) {
                    throw new ClassNotFoundException(pName, e1);
                }
            }
            return super.loadClass(pName);
        }

    }

}
