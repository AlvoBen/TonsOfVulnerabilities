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
package com.sap.sdo.testcase.external.helper;

import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

/**
 *
 * @author D042641
 */
public class SimpleHelperTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public SimpleHelperTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private List<Class> _allHelpers = Arrays.asList(new Class[] {
            CopyHelper.class, DataHelper.class, EqualityHelper.class,
            TypeHelper.class, XMLHelper.class, XSDHelper.class });

    @Test
    public void testAllHelpersAreInstanciable() throws Exception {
        final ErrorMessageHelper errMsgHelper = new ErrorMessageHelper();
        for (final Class clazz : _allHelpers) {
            try {
                final Field field = clazz.getField("INSTANCE");
                final Object helper = field.get(clazz);
                if (helper == null) {
                    errMsgHelper.addException("instance of " + clazz.getName()
                            + " is not instanciable.");
                }
            } catch (final SecurityException ex) {
                errMsgHelper.addException(ex);
            } catch (final IllegalArgumentException ex) {
                errMsgHelper.addException(ex);
            } catch (final NoSuchFieldException ex) {
                errMsgHelper.addException(ex);
            } catch (final IllegalAccessException ex) {
                errMsgHelper.addException(ex);
            }
        }
        if (errMsgHelper.isError()) {
            fail(errMsgHelper.getMessage());
        }
    }

    // TODO refactor out this class as util or helper class.
    private final class ErrorMessageHelper {
        private final StringBuffer _errorMsg = new StringBuffer("\n");

        public void addException(final Throwable pError) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            pError.printStackTrace(pw);
            _errorMsg.append(sw.toString()).append("\n");
        }

        public void addException(final String pInfo) {
            _errorMsg.append(pInfo).append("\n");
        }

        public String getMessage() {
            return _errorMsg.toString();
        }

        public boolean isError() {
            return _errorMsg.length() > 0;
        }
    }
}
