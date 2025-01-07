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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * @author D042774
 *
 */
public class ClassLoaderTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public ClassLoaderTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private enum Helpers {
        COPY {
            @Override
            String getHelper() {
                return CopyHelper.INSTANCE.toString();
            }
            @Override
            String getSecondHelper() {
                return HelperProvider.getCopyHelper().toString();
            }
            @Override
            String getContextHelper() {
                return HelperProvider.getDefaultContext().getCopyHelper().toString();
            }
        },
        DATA {
            @Override
            String getHelper() {
                return DataHelper.INSTANCE.toString();
            }
            @Override
            String getSecondHelper() {
                return HelperProvider.getDataHelper().toString();
            }
            @Override
            String getContextHelper() {
                return HelperProvider.getDefaultContext().getDataHelper().toString();
            }
        },
        EQUALITY {
            @Override
            String getHelper() {
                return EqualityHelper.INSTANCE.toString();
            }
            @Override
            String getSecondHelper() {
                return HelperProvider.getEqualityHelper().toString();
            }
            @Override
            String getContextHelper() {
                return HelperProvider.getDefaultContext().getEqualityHelper().toString();
            }
        },
        TYPE {
            @Override
            String getHelper() {
                return TypeHelper.INSTANCE.toString();
            }
            @Override
            String getSecondHelper() {
                return HelperProvider.getTypeHelper().toString();
            }
            @Override
            String getContextHelper() {
                return HelperProvider.getDefaultContext().getTypeHelper().toString();
            }
        },
        XML {
            @Override
            String getHelper() {
                return XMLHelper.INSTANCE.toString();
            }
            @Override
            String getSecondHelper() {
                return HelperProvider.getXMLHelper().toString();
            }
            @Override
            String getContextHelper() {
                return HelperProvider.getDefaultContext().getXMLHelper().toString();
            }
        },
        XSD {
            @Override
            String getHelper() {
                return XSDHelper.INSTANCE.toString();
            }
            @Override
            String getSecondHelper() {
                return HelperProvider.getXSDHelper().toString();
            }
            @Override
            String getContextHelper() {
                return HelperProvider.getDefaultContext().getXSDHelper().toString();
            }
        };

        abstract String getHelper();
        abstract String getSecondHelper();
        abstract String getContextHelper();
    };

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /*
     * @see TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    public void xtestApplicationSingleton() throws Exception {
        ClassLoader loader1 = new MyClassLoader(Thread.currentThread().getContextClassLoader());
        ClassLoader loader2 = new MyClassLoader(Thread.currentThread().getContextClassLoader());

        for (Helpers helper : Helpers.values()) {
            MyThread thread1 = new MyThread(helper);
            thread1.setContextClassLoader(loader1);
            thread1.start();

            MyThread thread2 = new MyThread(helper);
            thread2.setContextClassLoader(loader2);
            thread2.start();

            thread1.join();
            thread2.join();

            String t1helper = thread1.helper;
            String t1helper2 = thread1.helper2;
            String t1helper3 = thread1.helper3;
            String t2helper = thread2.helper;
            String t2helper2 = thread2.helper2;
            String t2helper3 = thread2.helper3;

            assertNotNull(t1helper);
            assertNotNull(t1helper2);
            assertNotNull(t1helper3);
            assertNotNull(t2helper);
            assertNotNull(t2helper2);
            assertNotNull(t2helper3);

            assertEquals(t1helper, t1helper2);
            assertEquals(t2helper, t2helper2);

            int t1index = t1helper.lastIndexOf('@');
            int t2index = t2helper.lastIndexOf('@');

            assertFalse(t1index < 0);
            assertFalse(t2index < 0);

            assertEquals(
                t1helper.substring(0, t1index),
                t2helper.substring(0, t2index));
            assertFalse(
                t1helper + " - " + t2helper + " tested with " + helper.name(),
                t1helper.substring(t1index).equals(t2helper.substring(t2index)));
            assertEquals(t1helper3, t1helper.substring(t1helper.indexOf("delegate: ")+10));
            assertEquals(t2helper3, t2helper.substring(t2helper.indexOf("delegate: ")+10));
        }
    }

    @Test
    public void testClassloaderGC() throws Exception {
        ClassLoader loader1 = new MyClassLoader(Thread.currentThread().getContextClassLoader());
        SapHelperContext ctx1 = (SapHelperContext)SapHelperProvider.getDefaultContext(loader1);
        HelperContext ctx2 = SapHelperProvider.getDefaultContext();
        assertNotSame(ctx1, ctx2);

        loader1 = null;

        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {}

        try {
            ctx1.getClassLoader();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals("ClassLoader of this HelperContext is outdated", ex.getMessage());
        }
    }

    class MyClassLoader extends ClassLoader {

        /**
         *
         */
        public MyClassLoader() {
            super();
        }

        /**
         * @param parent
         */
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }
    }

    class MyThread extends Thread {
        private final Helpers _helper;
        volatile private String helper = null;
        volatile private String helper2 = null;
        volatile private String helper3 = null;

        /**
         * @param pHelper
         *
         */
        public MyThread(Helpers pHelper) {
            super();
            _helper = pHelper;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            helper = _helper.getHelper();
            helper2  = _helper.getSecondHelper();
            helper3 = _helper.getContextHelper();
        }
    }
}
