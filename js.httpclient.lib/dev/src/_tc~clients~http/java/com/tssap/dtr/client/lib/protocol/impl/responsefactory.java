package com.tssap.dtr.client.lib.protocol.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.IResponse;

/**
 */
public class ResponseFactory {
	
	private static HashMap entityTypes = new HashMap();
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(ResponseFactory.class);	
	
	public static IResponse createResponse(String entityType, IResponse base) {
		Class responseClass = (Class)entityTypes.get(entityType);
		IResponse result = base;
		if ( responseClass != null ) {
			try {
				Constructor constructor 
					= responseClass.getDeclaredConstructor(new Class[]{IResponse.class} );
				result = (IResponse)constructor.newInstance(new Object[]{base});				
			} catch (Exception ex) {
				TRACE.catching("createResponse(String,IResponse)", ex);				
			}			
		}
		return result;
	}
	
	public static void registerEntityType(String entityType, Class responseClass) {
		entityTypes.put(entityType, responseClass);
	}

}
