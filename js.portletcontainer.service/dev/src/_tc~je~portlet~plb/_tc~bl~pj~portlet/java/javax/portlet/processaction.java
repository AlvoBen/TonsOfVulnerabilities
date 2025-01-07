/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for marking a method for processing
 * a specific action.
 * The <code>GenericPortlet</code> tries to dispatch to methods annotated 
 * with the tag <code>@ProcessAction</code> for any received
 * <code>processAction</code> call.
 *
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ProcessAction {
	/**
	 * Name of the action.
	 * Must be set on the 
	 * <code>ActionURL</code> as value of the 
	 * parameter <code>javax.portlet.action</code>.
	 * 
	 * @return  action name
	 */
	String name();
}
