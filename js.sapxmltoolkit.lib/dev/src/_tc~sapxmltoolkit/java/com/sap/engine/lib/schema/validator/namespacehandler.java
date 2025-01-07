package com.sap.engine.lib.schema.validator;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import com.sap.engine.lib.log.LogWriter;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-9-8
 * Time: 16:30:37
 * To change this template use Options | File Templates.
 */
public final class NamespaceHandler {

  private Vector prefixMappingsCollector;

  protected NamespaceHandler() {
    prefixMappingsCollector = new Vector();
  }

  protected void levelUp() {
    prefixMappingsCollector.add(null);
  }

  protected void levelDown() {
    prefixMappingsCollector.remove(prefixMappingsCollector.size() - 1);
  }

  protected void addUri(String prefix, String uri) {
    Hashtable prefixMappings = (Hashtable)(prefixMappingsCollector.lastElement());
    if(prefixMappings == null) {
      prefixMappings = new Hashtable();
      prefixMappingsCollector.set(prefixMappingsCollector.size() - 1, prefixMappings);
    }
    prefixMappings.put(prefix, uri);
  }

  protected String getUri(String prefix) {
    for(int i = prefixMappingsCollector.size() - 1; i >= 0; i--) {
      Hashtable prefixMappings = (Hashtable)(prefixMappingsCollector.get(i));
      if(prefixMappings != null) {
        String uri = (String)(prefixMappings.get(prefix));
        if(uri != null) {
          return(uri);
        }
      }
    }
    return(null);
  }
  
  protected String getPrefix(String uri) {
		for(int i = prefixMappingsCollector.size() - 1; i >= 0; i--) {
			Hashtable prefixMappings = (Hashtable)(prefixMappingsCollector.get(i));
			if(prefixMappings != null) {
				Enumeration enum1 = prefixMappings.keys();
				while(enum1.hasMoreElements()) {
					String prefix = (String)(enum1.nextElement());
					if(prefixMappings.get(prefix).equals(uri)) {
						return(prefix);
					}
				} 
			}
		}
		return(null);
  }
}
