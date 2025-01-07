package com.sap.jms.util;

import java.util.HashMap;
import java.util.Map;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class Logging {

    /** 
     * Config changes <code>Category</code> 
     */
    public static final Category CATEGORY_CONFIG_CHANGE = Category.getCategory(Category.getRoot(), "System/Changes/Properties");
    /** 
     * Empty <code>Object</code> array 
     */
    public static final Object[] EMPTY_ARRAY = new Object[] {};
    /** 
     * Cache of <code>Location</code> objects 
     */
    private static final Map<Class<? extends Object>, Location> cacheLocations = new HashMap<Class<? extends Object>, Location>(); 

    /**
     * Default constructor - private in order to prevent instantiation, because
     * all the methods in the class should be static
     */
    private Logging() {
    }

    /**
     * Gets <code>Location</code> from the cache if available or returns new
     * <code>Location</code> and puts it in the cache
     * 
     * @param o <code>Object</code> reference
     * @return <code>Location</code> object
     */
    private static Location useLocation(Object o) {
        Class<? extends Object> classRef = o.getClass();
        Location location = cacheLocations.get(classRef);
            
        if (location == null) {
            location = Location.getLocation(classRef);
            cacheLocations.put(classRef, location);
        }
        return location;
    }

    /**
     * Determines if a message with provided severity will pass severity check
     * for Location and will be written
     * 
     * @param o
     *            used to get fully qualified name of the class
     * @param severity
     *            Severity to check against
     * @return true, if the logging is applicable for the given severity and
     *         location
     */
    public static boolean isWritable(Object o, int severity) {
        return SimpleLogger.isWritable(severity, useLocation(o));
    }

    /**
     * Determines if a message with provided severity will pass severity check
     * for Category and will be written
     * 
     * @param severity
     *            Severity to check against
     * @return true, if the logging is applicable for the given severity and
     *         category
     */
    public static boolean isCustomerWritable(int severity) {
        return SimpleLogger.isWritable(severity, Category.SYS_SERVER);
    }

    /**
     * Determines if a message with provided severity will pass severity check
     * for Category and will be written
     * 
     * @param severity
     *            Severity to check against
     * @return true, if the logging is applicable for the given severity and
     *         category
     */
    public static boolean isConfigChangeWritable(int severity) {
        return SimpleLogger.isWritable(severity, CATEGORY_CONFIG_CHANGE);
    }

    /**
     * Goes to developer log
     * 
     * @param o
     *            used to get fully qualified name of the class
     * @param severity
     *            severity of the trace message that is issued
     * @param args
     *            comma separated list of arguments to print (e.g. string,
     *            integer, long etc)
     */
    public static void log(Object o, int severity, Object... args) {
        log(null, o, severity, args);
    }

    /**
     * Goes to developer log
     * 
     * @param messageID
     *            messageID in format ASJ.JMS.000000
     * @param o
     *            used to get fully qualified name of the class
     * @param severity
     *            severity of the trace message that is issued
     * @param args
     *            comma separated list of arguments to print (e.g. string,
     *            integer, long etc)
     */
    public static void log(String messageID, Object o, int severity, Object... args) {

        Location location = useLocation(o);
        
        StringBuilder buffer = new StringBuilder();
        for (Object s : args) {
            buffer.append(s);
        }

        SimpleLogger.trace(severity, location, buffer.toString());
    }

    /**
     * Goes to customer log
     * 
     * @param o
     *            used to get fully qualified name of the class
     * @param severity
     *            severity of the log message that is issued
     * @param args
     *            comma separated list of arguments to print (e.g. string,
     *            integer, long etc)
     */
    public static void customerLog(Object o, int severity, Object... args) {
        customerLog(null, o, severity, args);
    }

    /**
     * Goes to customer log
     * 
     * @param messageID
     *            messageID in format ASJ.JMS.000000
     * @param o
     *            used to get fully qualified name of the class
     * @param severity
     *            severity of the log message that is issued
     * @param args
     *            comma separated list of arguments to print (e.g. string,
     *            integer, long etc)
     */
    public static void customerLog(String messageID, Object o, int severity, Object... args) {

        Location location = useLocation(o);

        StringBuilder buffer = new StringBuilder();
        for (Object s : args) {
            buffer.append(s);
        }

        SimpleLogger.log(severity, Category.SYS_SERVER, location, messageID, buffer.toString());
    }

    /**
     * Goes to developer trace log
     * 
     * @param o
     *            used to get fully qualified name of the class
     * @param exc
     *            the exception itself
     */
    public static void exception(Object o, Throwable exc) {
        exception(o, exc, EMPTY_ARRAY);
    }

    /**
     * Goes to developer trace log
     * 
     * @param o
     *            used to get fully qualified name of the class
     * @param exc
     *            the exception itself
     * @param args
     *            comma separated list of arguments to print (e.g. string,
     *            integer, long etc)
     */
    public static void exception(Object o, Throwable exc, Object... args) {

        Location location = useLocation(o);

        StringBuilder buffer = new StringBuilder();
        for (Object s : args) {
            buffer.append(s);
        }
        SimpleLogger.traceThrowable(Severity.ERROR, location, buffer.toString(), exc);
    }

    /**
     * Goes to developer trace log
     * 
     * @param messageID
     *            messageID in format ASJ.JMS.000000
     * @param o
     *            used to get fully qualified name of the class
     * @param exc
     *            the exception itself
     * @param message
     *            The message text itself with placeholders
     * @param args
     *            placeholder arguments
     */
    public static void exception(String messageID, Object o, Throwable exc, String message, Object... args) {
        SimpleLogger.traceThrowable(Severity.ERROR, useLocation(o), exc, messageID, message, args);
    }

    /**
     * Goes to config changes log
     * 
     * @param o
     *            used to get fully qualified name of the class
     * @param severity
     *            severity of the log message that is issued
     * @param messageID
     *            messageID in format ASJ.JMS.000000
     * @param args
     *            comma separated list of arguments to print (e.g. string,
     *            integer, long etc)
     */
    public static void configChangeLog(String messageID, Object o, int severity, Object... args) {

        Location location = useLocation(o);

        StringBuilder buffer = new StringBuilder();
        for (Object s : args) {
            buffer.append(s);
        }

        SimpleLogger.log(severity, CATEGORY_CONFIG_CHANGE, location, messageID, buffer.toString());
    }
}
