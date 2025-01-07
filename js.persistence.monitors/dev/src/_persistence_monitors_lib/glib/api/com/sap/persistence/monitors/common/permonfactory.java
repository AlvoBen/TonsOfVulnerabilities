package com.sap.persistence.monitors.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.sap.persistence.monitors.sql.trace.TraceManager;
import com.sap.persistence.monitors.common.PerMonException;


public class PerMonFactory {
	
	private static final String traceManagerImpl = "com.sap.persistence.monitors.sql.trace.TraceManagerImpl";
	private static final String nodeSelImpl = "com.sap.persistence.monitors.common.NodeSelectionImpl";
	
    @SuppressWarnings("unchecked")
    public static TraceManager createTraceManager() throws PerMonException {
	    try {
            final Class tm = Class.forName(traceManagerImpl);
            final Constructor constructor = tm.getConstructor(new Class[] {});
            return (TraceManager)constructor.newInstance(new Object[] {});
            
        } catch (ClassNotFoundException e) {
            throw new PerMonException("Could not find the implementation class for TraceManager.",e);
        } catch (IllegalArgumentException e) {
            throw new PerMonException("Passed illegal arguments to the constructor of TraceManager.",e);

       
    } catch (InstantiationException e) {
        throw new PerMonException("Could not instantiate implementation class for TraceManager.",e);
    } catch (IllegalAccessException e) {
        throw new PerMonException("Could not access the implementation class for TraceManager.",e);
    } catch (InvocationTargetException e) {
        throw new PerMonException("Could not invoke the constructor of the implementation class for TraceManager.",e);
    } catch (SecurityException e) {
        throw new PerMonException("Could not access the implementation class for TraceManager.",e);
    } catch (NoSuchMethodException e) {
        throw new PerMonException("Could find constructor of the implementation class for TraceManager.",e);
    }

        
    }

	public static NodeSelection createNodeSelection() throws PerMonException {
		try {
            final Class<?> tm = Class.forName(nodeSelImpl);
            final Constructor<?> constructor = tm.getConstructor(new Class[] {});
            return (NodeSelection)constructor.newInstance(new Object[] {});
            
        } catch (ClassNotFoundException e) {
            throw new PerMonException("Could not find the implementation class for NodeSelection.",e);
        } catch (IllegalArgumentException e) {
            throw new PerMonException("Passed illegal arguments to the constructor of NodeSelection.",e);

       
    } catch (InstantiationException e) {
        throw new PerMonException("Could not instantiate implementation class for NodeSelection.",e);
    } catch (IllegalAccessException e) {
        throw new PerMonException("Could not access the implementation class for NodeSelection.",e);
    } catch (InvocationTargetException e) {
        throw new PerMonException("Could not invoke the constructor of the implementation class for NodeSelection.",e);
    } catch (SecurityException e) {
        throw new PerMonException("Could not access the implementation class for NodeSelection.",e);
    } catch (NoSuchMethodException e) {
        throw new PerMonException("Could find constructor of the implementation class for NodeSelection.",e);
    }

	}
}
