package com.sap.engine.applications.security.logon.beans;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class ResourceBean {
	private static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	
	private Locale locale = null;
	private ResourceBundle resourceBundle = null;
	private MessageFormat formatter = null;
	
	private static ClassLoader MY_LOADER = ResourceBean.class.getClassLoader();

	ResourceBean(Locale locale, String baseName, ServletContext context) {
		if (myLoc.bePath()) {
			myLoc.entering("ResourceBean", new Object[]{locale, baseName});
		}

		if (locale != null) {
			this.locale = locale;
		} else {
			this.locale = Locale.getDefault();
		}

		resourceBundle = getBundle(baseName, this.locale, context);
		
    formatter = new MessageFormat("");
    formatter.setLocale(this.locale);
	}

	public String get(String key) {
	  try {
		return this.resourceBundle.getString(key);
	  } catch(MissingResourceException ex) {
	    myLoc.traceThrowableT(Severity.ERROR, "ResourceBean.get", "Resource (" + key + ") not found in properties files-" + ex.getKey(), ex);
	    return "";
	  }
	} 

	public Locale getLocale() {
		return locale;
	} 

	public String parsePattern(String newPattern) {
		StringBuffer segments = new StringBuffer(newPattern.length());
		
		for (int i = 0; i < newPattern.length(); ++i) {
			char ch = newPattern.charAt(i);
			
			if (ch == '\'') {
				if (i + 1 == newPattern.length()) {
					segments.append(ch).append(ch);
				} else {
					if (newPattern.charAt(i + 1) == '\'') {
						segments.append(ch); // handle doubles
						++i;
					} else {
						segments.append(ch).append(ch);
					}
				}
			} else {
				segments.append(ch);
			}
		}
		return segments.toString();
	} 

	private static ResourceBundle getBundle(String baseName, Locale locale, ServletContext context) {
		//    baseName + "_" + language1 + "_" + country1 + "_" + variant1 
		//    baseName + "_" + language1 + "_" + country1 
		//    baseName + "_" + language1 
		String lang = locale.getLanguage();
		
		if (lang != null && lang.length() == 0) {
			lang = null;
		}
		
		String country = locale.getCountry();
		
		if (country != null && country.length() == 0) {
			country = null;
		}
		
		String variant = locale.getVariant();
		
		if (variant != null && variant.length() == 0) {
			variant = null;
		}
		
		String bundleName = baseName;
		ResourceBundle bundle = null;
		
		if (lang != null && country != null && variant != null) {
			bundleName = baseName + "_" + lang + "_" + country	+ "_" + variant + ".properties";
			bundle = getBundle(baseName, context);
		}
		
		if (bundle == null && lang != null && country != null) {
			bundleName = baseName + "_" + lang + "_" + country + ".properties";
			bundle = getBundle(bundleName, context);
		}

		if (bundle == null && lang != null) {
			bundleName = baseName + "_" + lang + ".properties";
			bundle = getBundle(bundleName, context);
		}

		if (bundle == null) {
			bundleName = baseName + ".properties";
			bundle = getBundle(bundleName, context);
		}
		
		return bundle;
	}
	
	private static InputStream getPropFileInputStream(String name, ServletContext context) {
		InputStream in = null;
		
		in = context.getResourceAsStream("/WEB-INF/" + name);
		
		if (in == null) {
			in = context.getResourceAsStream("/WEB-INF/classes/" + name);
		}
		
		if (in == null) {
			in = MY_LOADER.getResourceAsStream(name);
		}
		
		return in;
	}

	private static ResourceBundle getBundle(String bundleName, ServletContext context)	{
	  ResourceBundle bundle = null;
	  
		InputStream stream = getPropFileInputStream(bundleName, context);
		
		if (stream != null) {
			try {
			  bundle = new PropertyResourceBundle(stream);
			} catch (IOException ex) {
			  myLoc.traceThrowableT(Severity.DEBUG, "Failed to load " + bundleName, ex);			  
			}
		}

		return bundle;
	}
	
	public Enumeration<String> getIds() {
		return this.resourceBundle.getKeys();
	}
	
	public String print(ErrorBean message) {
		try {
      String msgTemplate = this.resourceBundle.getString(message.id);
      String msg = parsePattern(msgTemplate);
      formatter.applyPattern(msg);
			return formatter.format(message.params);
		} 
		catch (MissingResourceException ex) {
			//myLoc.traceThrowableT(Severity.DEBUG, "print", "Message ID (" + message.id + ") not found in properties files-" + ex.getKey(), ex);
			myLoc.debugT("Message ID (" + message.id + ") not found in properties files-" + ex.getKey());
			return message.id;
		} 
		catch (Exception ex) {
			myLoc.traceThrowableT(Severity.ERROR, "print", "Message cannot be formatted. Message ID (" + message.id + "). Reason: " + ex.getMessage(), ex);
			return "Wrong message (ID = " + message.id + ")";
		}
	} // print
	
}
