﻿package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This request class implements the DAV "COPY" request.
 */
public class CopyRequest extends XMLRequest {

  private Depth depth = Depth.DEPTH_INFINITY;
  private boolean overwrite = true;
  private String label;
  private PropertyBehavior propertyBehavior = PropertyBehavior.NONE;
	private ArrayList properties;
  private HashMap namespaces;

  /**
   * Creates a COPY request for the specified resource to the given
   * destination.
   * @param path  the path of the source resource to be copied.
   * @param destination  the path to copy to.
   */
  public CopyRequest(String path, String destination) {
    super("COPY", path);
    this.setHeader(Header.DAV.DESTINATION, destination);
  }

  /**
   * Creates a COPY request for the specified resource to the given
   * destination.
   * @param path  the path of the source resource to be copied.
   * @param destination  the path to copy to.
   * @param depth  determines for collections if the members of the collection
   * are to be copied, too (depth infinity). Otherwise only the collection
   * itself is copied. Default is Depth.INFINITY. Adds a "Depth" header
   * to the request.
   */
  public CopyRequest(String path, String destination, Depth depth) {
    super("COPY", path);
    this.setHeader(Header.DAV.DESTINATION, destination);
    setDepth(depth);
  }

  /**
   * Determines for collections whether the members of the collection are
   * to copied, too. Otherwise only the collection
   * itself is copied. Adds a "Depth" header
   * to the request.
   * @param depth  either Depth.DEPTH_0 or Depth.DEPTH_INFINITY.
   * Default is Depth.DEPTH_INFINITY.
   */
  public void setDepth(Depth depth) {
    if (depth.equals(Depth.DEPTH_0)) {
      setHeader(Header.DAV.DEPTH, "0");
      this.depth = Depth.DEPTH_0;
    } else if (depth.equals(Depth.DEPTH_INFINITY)) {
      setHeader(Header.DAV.DEPTH, "infinity");
      this.depth = Depth.DEPTH_INFINITY;
    }
  }

  /**
   * Determines whether existing resources with same name
   * at the destination of the copy should be overwritten.
   * Adds an "Overwrite" header to the request.
   * @param overwrite if true, the copy overwrites resource at the destination.
   * Default is true.
   */
  public void setOverwrite(boolean overwrite) {
    this.overwrite = overwrite;
    this.setHeader(Header.DAV.OVERWRITE, ((overwrite)? "T" : "F"));
  }

  /**
   * Determines how properties on the resource are handled during the
   * copy operation. Setting <code>propertyBehavior</code> to
   * <code>PropertyBehavior.OMIT</code> instructs the server to use best
   * effort to copy properties but a failure to copy certain properties
   * must not cause the request to fail. The behavior
   * <code>PropertyBehavior.KEEP_ALIVE</code> allows to specify a list
   * of properties that must be "live" after they are copied to the
   * destination resource. If this behavior is set but the list leaved
   * empty, all live properties must fulfill that requirement.
   * @param propertyBehavior  either <code>PropertyBehavior.OMIT</code>,
   * <code>PropertyBehavior.KEEP_ALIVE</code> or <code>PropertyBehavior.NONE</code>.
   * The latter is the default and indicates that the server should apply
   * the default handling for copying properties.
   */
  public void setPropertyBehavior(PropertyBehavior propertyBehavior) {
    this.propertyBehavior = propertyBehavior;
  }

  /**
   * Adds another keep-alive property to the request. Sets the
   * property behavior <code>PropertyBehavior.KEEP_ALIVE</code>.
   * @param propertyName  the name of a property with proper namespace
   * prefix (despite for DAV standard properties).
   */
  public void addKeepAliveProperty(String propertyName) {
		if (properties==null) {
			properties = new ArrayList( );
		}
		properties.add(propertyName);
    setPropertyBehavior(PropertyBehavior.KEEP_ALIVE);
  }

  /**
   * Adds another keep-alive property to the request and specifies the namespace
   * URI used by this preoperty. Sets the
   * property behavior <code>PropertyBehavior.KEEP_ALIVE</code>.
   * @param propertyName  the name of a property with proper namespace
   * prefix (despite for DAV standard properties).
   * @param  namespaceURI  the URI of the property's namespace. The request uses
   * the namespace "DAV:" as default namespace.
   */
	public void addKeepAliveProperty(String propertyName, String namespaceURI) {
    int n = propertyName.indexOf(':');
    if (n>0) {
      addNamespace(propertyName.substring(0,n), namespaceURI);
    }
    addKeepAliveProperty(propertyName);
    setPropertyBehavior(PropertyBehavior.KEEP_ALIVE);
	}

  /**
   * Adds a set of keep-alive properties to the request. Sets the
   * property behavior <code>PropertyBehavior.KEEP_ALIVE</code>.
   * @param propertyNames  an array of names of properties with proper namespace
   * prefix (despite for DAV standard properties).
   */
	public void addKeepAliveProperty(String[] propertyNames) {
		if (properties==null) {
			properties = new ArrayList( );
		}
		for (int i=0; i<propertyNames.length; ++i) {
			properties.add(propertyNames[i]);
		}
    setPropertyBehavior(PropertyBehavior.KEEP_ALIVE);
	}

  /**
   * Adds a namespaces to the request specified by a namespaces prefix
   * and the URI that defines that namespace. The request defines the
   * namespace "DAV:" as default namespace. Properties belonging to
   * the default namespace do not need to explicitly use namespace prefixes.
   * @param prefix  a namespace prefix.
   * @param namespaceURI  the URI associated with the namespace.
   */
	public void addNamespace(String prefix, String namespaceURI) {
		if (namespaces==null) {
			namespaces = new HashMap( );
		}
		if (prefix.endsWith(":")) {
			namespaces.put(prefix.substring(0, prefix.length()-1), namespaceURI);			
		} else {
			namespaces.put(prefix, namespaceURI);
		}
	}


  /**
   * Applies the move to a certain version of a VCR matching the given label.
   * Adds a "Label" header to the request.
   * @param label  the label to match.
   */
	public void setApplyToLabel(String label) {
		this.label = label;
		if (label != null) {
			setHeader(Header.DAV.LABEL, label);
		}
	}

  /**
   * Prepares the request entity.
   * This method is called during execution of this request. Do not call
   * this method directly.
   * @return A request entity for this COPY request.
   */
	public IRequestEntity prepareRequestEntity() {
    StringEntity body = null;
    if (!propertyBehavior.equals(PropertyBehavior.NONE)) {
      body = new StringEntity("text/xml", "UTF-8");
	  body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
      body.append("<propertybehavior").append(DAV.DEFAULT_XMLNS);
      if (properties!=null && namespaces!=null && namespaces.size()>0) {
        Iterator keys = namespaces.keySet().iterator();
        while (keys.hasNext()) {
          String key = (String)keys.next();
          body.append(" xmlns:").append(key).append("=\"").append(namespaces.get(key)).append("\"");
        }
      }
      body.append(">");
      
      if (propertyBehavior.equals(PropertyBehavior.OMIT)) {
          body.append("<omit/>");
      } else if (propertyBehavior.equals(PropertyBehavior.KEEP_ALIVE)) {
          body.append("<keepalive>");
          if (properties==null) {
            body.append("*");
          } else {
            for (int i=0; i < properties.size(); ++i) {
              body.append("<href>").append((String)properties.get(i)).append("</href>");
            }
          }
          body.append("</keepalive>");
      }
      body.append("</propertybehavior>");
    }
    setRequestEntity(body);
		return body;
	}

}
