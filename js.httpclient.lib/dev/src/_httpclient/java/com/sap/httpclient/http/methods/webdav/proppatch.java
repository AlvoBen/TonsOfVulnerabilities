/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.http.methods.webdav;

import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.methods.StringRequestData;

import java.util.*;

/**
 * Implements the HTTP webdav PROPPATCH method.
 *
 * @author Nikolai Neichev
 */
public class PROPPATCH extends PropertyXMLRequest {

  /**
   * Used to store the properties to set
   */
  private ArrayList<SetPropElement> propertiesToSet = new ArrayList<SetPropElement>();

  /**
   * No-arg constructor.
   */
  public PROPPATCH() {
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public PROPPATCH(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"PROPPATCH"</tt>.
   *
   * @return <tt>"PROPPATCH"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_PROPPATCH;
  }

  /**
   * Adds a single property to the PROPPATCH method
   *
   * @param propertyName the property name including it's namespace prefix
   */
	public void addPropertyToRemove(String propertyName) {
    if (propertyName.indexOf(":") == -1) {
      propertyName = "D:" + propertyName;
    }
	  getProperties().add(propertyName);
	}

  /**
   * Adds all of the specified properties to the PROPPATCH method
   *
   * @param propertyNames the property names array
   */
	public void addPropertiesToRemove(String propertyNames[]) {
    for(String prop:propertyNames) {
      if (prop.indexOf(":") == -1) {
        prop = "D:" + prop;
      }
	    getProperties().add(prop);
    }
	}

  /**
   *
   * @param setPropElement the property set
   */
	public void addPropertyToSet(SetPropElement setPropElement) {
	  propertiesToSet.add(setPropElement);
	}

  /**
   * Clears all previously set properties for REMOVE
   */
  public void clearRemoveProps() {
    getProperties().clear();
  }

  /**
   * Clears all previously set properties for SET
   */
  public void clearSetProps() {
    propertiesToSet.clear();
  }

  /**
   * Clears all previously set properties
   */
  public void clearAll() {
    clearSetProps();
    clearRemoveProps();
    clearNamespaces();
  }

  /**
   * Prepares the request data.
   *
   * @return A request entity for this PROPFIND request.
   */
	public RequestData generateRequestData() { // WEBDAF.pdf (8.2 PROPPATCH)
    if (!isDataSet()) {
      StringBuilder body = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
      body.append("<D:propertyupdate xmlns:D=\"DAV:\"");
      if (!getNamespaces().isEmpty()) { // has specified namespaces
        String[] keys = new String[getNamespaces().size()];
        getNamespaces().keySet().toArray(keys);
        for (String key: keys) {
          body.append(" xmlns:").append(key).append("=\"").append(getNamespaces().get(key)).append("\"");
        }
      }
      body.append(">"); // propertyupdate
      if (!propertiesToSet.isEmpty()) { // has properties to set
        body.append("<D:set><D:prop>");
        for (Object propVal: propertiesToSet) {
          body.append(propVal.toString());
        }
        body.append("</D:prop></D:set>");
      }
      if (!getProperties().isEmpty()) { // has properties to remove
        body.append("<D:remove><D:prop>");
        for (Object propVal: getProperties()) {
          body.append("<").append(propVal.toString()).append("/>");
        }
        body.append("</D:prop></D:remove>");
      }
      body.append("</D:propertyupdate>");
      RequestData data = new StringRequestData(body.toString());
      setRequestData(data);
      return data;
    } else {
      return super.generateRequestData();
    }
	}

  protected void clearRequestBody() {
    super.clearRequestBody();
    clearAll(); // TODO maybe not
  }

  /**
   * Represents the elements structure for set property case of PROPPATCH
   */
  public static class SetPropElement {

    // the element name
    private String name;

		// the values, may be more than 1
    private Vector values = new Vector(1);

    /**
     * SetPropElement constructor
     *
     * @param name the name of the property
     */
    public SetPropElement(String name) {
      this.name = name;
    }

    /**
     * SetPropElement constructor
     *
     * @param name the name of the property
     * @param value the value/s of the property
     */
    public SetPropElement(String name, Object... value) {
      this.name = name;
      for (Object val: value) {
        if (val instanceof Collection) {
          values.addAll((Collection) val);
        } else {
          values.add(val);
        }
      }
    }

    /**
     * Adds another value to this property
     *
     * @param value the value
     */
    public void addValue(Object value) {
      values.add(value);
    }

    /**
     * Removes a value from this property
     * @param value the value to remove if found
     */
    public void removeValue(Object value) {
      Iterator iter = values.iterator();
      while (iter.hasNext()) {
        if (iter.next().equals(value)) {
          iter.remove();
        }
      }
    }

    /**
     * Checks if the property contains a specified value
     * @param value he specified value
     * @return <tt>true</tt> if set, <tt>false</tt> if not
     */
    public boolean isSet(Object value) {
      return values.contains(value);
    }

    /**
     * String representation of the property values
     * @return the values as String
     */
    public String toString() {
			StringBuilder builder = new StringBuilder("<");
			if (values.isEmpty()) {
				builder.append("/").append(name).append(">");	//	</name>
				return builder.toString();
      }
			builder.append(name).append(">"); //	<name>
			for (Object obj: values) {
        builder.append(obj.toString());
      }
			builder.append("</").append(name).append(">"); //	</name>
      return builder.toString();
    }

  }

//  public static void main(String[] args) {
//    PROPPATCH p = new PROPPATCH();
//    PROPPATCH.SetPropElement setPropJim = new PROPPATCH.SetPropElement("Z:Author", "Jim Whitehead");
//    PROPPATCH.SetPropElement setPropRoy = new PROPPATCH.SetPropElement("Z:Author", "Roy Fielding");
//    PROPPATCH.SetPropElement authors = new PROPPATCH.SetPropElement("Z:authors", setPropJim, setPropRoy);
//    p.addPropertyToSet(authors);
//    p.addNamespace("Z", "http://www.w3.com/standards/z39.50/");
//    p.addPropertyToRemove("Z:Copiright-Owner");
//    StringRequestData data = (StringRequestData) p.getRequesData();
//    System.out.println(data.getContent());
//  }

}