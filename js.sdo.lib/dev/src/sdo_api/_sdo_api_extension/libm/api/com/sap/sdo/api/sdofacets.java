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
package com.sap.sdo.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SdoFacets {
    int length() default -1;
    int minLength() default -1;
    int maxLength() default -1;
    int minInclusive() default Integer.MIN_VALUE;
    int minExclusive() default Integer.MIN_VALUE;
    int maxInclusive() default Integer.MAX_VALUE;
    int maxExclusive() default Integer.MAX_VALUE;
    int totalDigits() default -1;
    int fractionDigits() default -1;
    String[] enumeration() default {};
    String[] pattern() default {};
}
