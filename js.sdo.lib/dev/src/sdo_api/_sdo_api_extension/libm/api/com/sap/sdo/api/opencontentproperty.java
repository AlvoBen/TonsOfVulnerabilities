package com.sap.sdo.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OpenContentProperty {
	String name();
	boolean many() default false;
	boolean containment() default true;
}
