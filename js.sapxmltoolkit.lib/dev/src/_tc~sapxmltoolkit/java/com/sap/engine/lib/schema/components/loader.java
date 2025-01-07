package com.sap.engine.lib.schema.components;

import org.w3c.dom.*;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.xml.sax.*;

import java.io.InputStream;
import java.io.File;

import com.sap.engine.lib.jaxp.MultiSource;
import com.sap.engine.lib.schema.exception.SchemaComponentException;

/**
 * @author ivan-m
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface Loader {
	
  Schema load(String location) throws SchemaComponentException;

  Schema load(String namespace, String location) throws SchemaComponentException;

  Schema load(String[] locations) throws SchemaComponentException;

  Schema load(String[] namespaces, String[] locations) throws SchemaComponentException;

  Schema load(Node node) throws SchemaComponentException;
	
  Schema load(String namespace, Node node) throws SchemaComponentException;
	
  Schema load(Node[] nodes) throws SchemaComponentException;
	
  Schema load(String[] namespaces, Node[] nodes) throws SchemaComponentException;
	
  Schema load(Source source) throws SchemaComponentException;
	
  Schema load(String namespace, Source source) throws SchemaComponentException;
	
  Schema load(Source[] sources) throws SchemaComponentException;

  Schema load(String[] namespaces, Source[] sources) throws SchemaComponentException;

  Schema load(InputStream stream) throws SchemaComponentException;

  Schema load(String namespace, InputStream stream) throws SchemaComponentException;

  Schema load(InputStream[] streams) throws SchemaComponentException;

  Schema load(String[] namespaces, InputStream[] streams) throws SchemaComponentException;

  Schema load(InputSource source) throws SchemaComponentException;

  Schema load(String namespace, InputSource source) throws SchemaComponentException;

  Schema load(InputSource[] sources) throws SchemaComponentException;

  Schema load(String[] namespaces, InputSource[] sources) throws SchemaComponentException;

  Schema load(File file) throws SchemaComponentException;

  Schema load(String namespace, File file) throws SchemaComponentException;

  Schema load(File[] files) throws SchemaComponentException;

  Schema load(String[] namespaces, File[] files) throws SchemaComponentException;

  Schema load(Object schemaObj) throws SchemaComponentException;

  Schema load(String namespace, Object schemaObj) throws SchemaComponentException;
  
  Schema load(Object[] schemaObj) throws SchemaComponentException;
  
  Schema load(String[] namespaces, Object[] schemaObj) throws SchemaComponentException;

  Schema loadFromWSDLDocument(MultiSource source) throws SchemaComponentException;

  void setEntityResolver(EntityResolver entityResolver);
  
  EntityResolver getEntityResolver();

  void setUriResolver(URIResolver uriResolver);

  URIResolver getUriResolver();

  void setValidateXSDDoc(boolean validateXSDDoc);

  boolean getValidateXSDDoc();
  
  void setBackwardsCompatibilityMode(boolean backwardsCompatibilityMode);
  
  boolean getBackwardsCompatibilityMode();
  
  public void setLoadPatternRegularExpressions(boolean loadPatternRegularExpressions);
  
  public boolean getLoadPatternRegularExpressions();
}