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
@Target(ElementType.METHOD)
public @interface SdoPropertyMetaData {
	String sdoType() default "";
	String sdoName() default "";
	String opposite() default "";
	String defaultValue() default "";
	boolean containment() default false;
	Bool nullable() default Bool.UNSET;
    String[] aliasNames() default {};
    int propertyIndex() default -1;
	XmlPropertyMetaData xmlInfo() default @XmlPropertyMetaData;
}
