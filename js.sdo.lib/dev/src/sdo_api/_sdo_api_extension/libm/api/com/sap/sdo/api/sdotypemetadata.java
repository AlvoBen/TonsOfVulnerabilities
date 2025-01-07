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
public @interface SdoTypeMetaData {
	boolean open() default false;
	boolean sequenced() default false;
	String sdoName() default "";
	String uri() default "";
	boolean noNamespace() default false;
	boolean abstractDataObject() default false;
	OpenContentProperty[] openContentProperties() default {};
    boolean elementFormDefault() default false;
    boolean attributeFormDefault() default false;
    boolean mixed() default false;
}
