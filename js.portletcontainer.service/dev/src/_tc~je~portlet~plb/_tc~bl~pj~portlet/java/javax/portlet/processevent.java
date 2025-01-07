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
 * a specific event.
 * The <code>GenericPortlet</code> tries to dispatch to methods annotated 
 * with the tag <code>@ProcessEvent</code> for any received event.
 *
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ProcessEvent {
    /**
     * Event QName.
     * Must be in the format:<br> 
     * "{" + Namespace URI + "}" + local part.<br> 
     * If the Namespace URI equals(XMLConstants.NULL_NS_URI), 
     * only the local part is used 
     * (like the <code>javax.xml.namespace.QName.toString()</code> method).
     * 
     * @return event QName.
     */
    String qname() default "";
    
    /**
     * Local part of the event name.
     * The namespace for the events is either taken from the <code>default-event-namespace</code> element
     * in the portlet deployment descriptor, or if this element is not provided
     * the XML default namespace XMLConstants.NULL_NS_URI is used.
     * 
     * @return local part of the event name.
     */
    String name() default "";

}
