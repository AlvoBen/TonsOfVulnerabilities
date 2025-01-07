/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package test.suite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.internal.runners.InitializationError;
import org.junit.runners.Suite;

/**
 * @author D042774
 *
 */
public class SdoSuite extends Suite {
    /**
     * The <code>SuiteClasses</code> annotation specifies the classes to be run when a class
     * annotated with <code>@RunWith(Suite.class)</code> is run.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface OptionalClasses {
        public Class<?>[] value();
    }

    /**
     * @param pKlass
     * @throws InitializationError
     */
    public SdoSuite(Class<?> pKlass) throws InitializationError {
        super(pKlass, collectTests(pKlass));
    }

    /**
     * @param pKlass
     * @return
     * @throws InitializationError
     */
    private static Class<?>[] collectTests(Class<?> pKlass) throws InitializationError {
        // default behavior from org.junit.runners.Suite
        SuiteClasses annotation= pKlass.getAnnotation(SuiteClasses.class);
        if (annotation == null) {
            throw new InitializationError(
                String.format("class '%s' must have a SuiteClasses annotation",
                pKlass.getName()));
        }

        String perforceSrcRoot = System.getProperty("PerforceSrcRoot");
        Class<?>[] suiteClasses = annotation.value();
        if (perforceSrcRoot != null) {
            OptionalClasses optional= pKlass.getAnnotation(OptionalClasses.class);
            if (optional != null) {
                Class<?>[] optionalClasses = optional.value();
                Class<?>[] temp = new Class<?>[suiteClasses.length + optionalClasses.length];
                System.arraycopy(suiteClasses, 0, temp, 0, suiteClasses.length);
                System.arraycopy(optionalClasses, 0, temp, suiteClasses.length, optionalClasses.length);
                return temp;
            }
        }

        return suiteClasses;
    }
}
