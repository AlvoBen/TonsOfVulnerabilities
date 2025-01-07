package com.sap.engine.services.deploy.logging;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.sap.engine.services.deploy.server.ExceptionConstants;

public class ExceptionConstantsTest {
	Map<String,String> logConstants = new HashMap<String,String>();
	ResourceBundle messages; 
	Set<String> excludedFieldNames = new HashSet<String>();
		
	@Before
	public void init(){
		
		// excluded fields, for ATS compatibility
		excludedFieldNames.add("$VRc");
		excludedFieldNames.add("serialVersionUID");
		
		// get all the declared public static fields of type SoftwareType
		Class c = ExceptionConstants.class;
		Field fields[] = c.getDeclaredFields();
	
		
		for(Field field: fields){
			
			String fieldName = field.getName();
			
			if(excludedFieldNames.contains( fieldName ) ) {
				System.err.println("WARNING skipping field for ATS compatibility : " + fieldName );//$JL-SYS_OUT_ERR$
				continue;
			}
			// all the fields of this class should be public static final String
			int mod = field.getModifiers();
			if( Modifier.isPublic(mod) && 
					Modifier.isStatic(mod) && 
					Modifier.isFinal(mod) &&
					field.getType().equals(String.class) ){
				
				// get the value of the
				try{
					logConstants.put(fieldName , (String) field.get(null));	
				} catch(IllegalAccessException e){
					e.printStackTrace();
					fail("Unexpected exception: " + e);
				}
				
			} else {
				String message = "All fields of class " + c.getName() + " should be declared as 'public static final String'." +
				"The field that does not conform to this is: " + fieldName;
				fail(message);
			}
		}
		
		String language = new String("en");
        String country = new String("US");
        Locale currentLocale = new Locale(language, country);
        messages = ResourceBundle.getBundle("com/sap/engine/services/deploy/DeployResourceBundle", currentLocale);
        }
	
	@Test
	public void testAvailabilityOfKeys(){
		
		Set<String> fields = logConstants.keySet();
		for(String field:fields){
			String key = logConstants.get(field);
			try{
				
				String text = messages.getString(key);
				System.out.println("Found text: " + text);//$JL-SYS_OUT_ERR$

			} catch (MissingResourceException e){
				fail("Cannot find message text for field " + field + " and key " + key +
						"\nException: " + e);
			}
		}
		
		
	}
}