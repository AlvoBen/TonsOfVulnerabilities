/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for marking a method for handling
 * a specific portlet mode in the render phase.
 * The <code>GenericPortlet</code> tries to dispatch to methods annotated 
 * with the tag <code>@RenderMode</code> for any received render call.
 *
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface RenderMode {
	/**
	 * Name of the render mode.
	 * 
	 * @return  render mode name
	 */
	String name();

}
