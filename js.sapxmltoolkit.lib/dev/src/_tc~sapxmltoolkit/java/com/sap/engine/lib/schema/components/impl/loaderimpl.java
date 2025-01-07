package com.sap.engine.lib.schema.components.impl;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.*;

import com.sap.engine.lib.schema.components.Loader;
import com.sap.engine.lib.schema.components.Schema;
import com.sap.engine.lib.schema.components.impl.structures.SchemaImpl;
import com.sap.engine.lib.schema.components.impl.structures.SchemaStructuresLoader;
import com.sap.engine.lib.schema.components.impl.structures.SchemaVisualizationFrame;
import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.schema.exception.SchemaException;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.parser.DOMParser;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.xml.parser.JAXPProperties;
import com.sap.engine.lib.xml.parser.URLLoader;
import com.sap.engine.lib.jaxp.TransformerFactoryImpl;
import com.sap.engine.lib.jaxp.MultiSource;
import com.sap.engine.lib.jaxp.TransformerImpl;

import java.util.Hashtable;
import java.util.Vector;
import java.net.URL;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class LoaderImpl implements Loader, Constants {

  private static SchemaImpl xmlSDForXSDDoc;
  
  private EntityResolver entityResolver;
  private URIResolver uriResolver;
  private DOMParser parser;
  private TransformerImpl transformer;
  private boolean validateXSDDoc;
  private Hashtable locationToSchemasMapping;
  private Hashtable nsToSchemasMapping;
  private Hashtable nsToNsModifiedSchemaLocationsMapping;
  private boolean backwardsCompatibilityMode;
  private boolean loadPatternRegularExpressions;
  
  public LoaderImpl() {
    validateXSDDoc = true;
    backwardsCompatibilityMode = false;
    loadPatternRegularExpressions = true;
  }
  
  public void setLoadPatternRegularExpressions(boolean loadPatternRegularExpressions) {
    this.loadPatternRegularExpressions = loadPatternRegularExpressions;
  }
  
  public boolean getLoadPatternRegularExpressions() {
    return(loadPatternRegularExpressions);
  }
  
  public void setBackwardsCompatibilityMode(boolean backwardsCompatibilityMode) {
    this.backwardsCompatibilityMode = backwardsCompatibilityMode;
  }
  
  public boolean getBackwardsCompatibilityMode() {
    return(backwardsCompatibilityMode);
  }
  
  public void setValidateXSDDoc(boolean validateXSDDoc) {
    this.validateXSDDoc = validateXSDDoc;
  }

  public boolean getValidateXSDDoc() {
    return(validateXSDDoc);
  }

  public Schema load(String location) throws SchemaComponentException {
    return(load(null, location));
  }

  public Schema load(String namespace, String location) throws SchemaComponentException {
    try {
      init();
      SchemaImpl schema = determineSchema(produceSchemas_Location(null, location, null, namespace));
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(String[] locations) throws SchemaComponentException {
    return(load(new String[locations.length], locations));
  }

  public Schema load(String[] namespaces, String[] locations) throws SchemaComponentException {
    if(namespaces.length != locations.length) {
      throw new SchemaComponentException("ERROR : The count of namespaces has to be equal to the count of locations.");
    }
    try {
      init();
      SchemaImpl[][] schemas = new SchemaImpl[locations.length][];
      for(int i = 0; i < locations.length; i++) {
        schemas[i] = produceSchemas_Location(null, locations[i], null, namespaces[i]);
      }
      SchemaImpl schema = determineSchema(schemas);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(Node node) throws SchemaComponentException {
    return(load(null, node));
  }
  
  public Schema load(String namespace, Node node) throws SchemaComponentException {
    try {
      init();
      SchemaImpl schema = produceSchema_Node(node, namespace);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(Node[] nodes) throws SchemaComponentException {
    return(load(new String[nodes.length], nodes));
  }
  
  public Schema load(String[] namespaces, Node[] nodes) throws SchemaComponentException {
    try {
      init();
      SchemaImpl[] schemas = new SchemaImpl[nodes.length];
      for(int i = 0; i < nodes.length; i++) {
        schemas[i] = produceSchema_Node(nodes[i], namespaces[i]);
      }
      SchemaImpl schema = determineSchema(schemas);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }
	
  public Schema loadFromWSDLDocument(MultiSource source) throws SchemaComponentException {
  	try {
  	  init();
  	  SchemaImpl schema = determineSchema(produceSchemas_Source(source, null, null, true));
  	  SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(Source source) throws SchemaComponentException {
    return(load(null, source));
  }
  
  public Schema load(String namespace, Source source) throws SchemaComponentException {
    try {
      init();
      SchemaImpl schema = determineSchema(produceSchemas_Source(source, null, namespace, false));
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(Source[] sources) throws SchemaComponentException {
    return(load(new String[sources.length], sources));
  }
  
  public Schema load(String[] namespaces, Source[] sources) throws SchemaComponentException {
    try {
      init();
      SchemaImpl[][] schemas = new SchemaImpl[sources.length][];
      for(int i = 0; i < sources.length; i++) {
        schemas[i] = produceSchemas_Source(sources[i], null, namespaces[i], false);
      }
      SchemaImpl schema = determineSchema(schemas);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(InputStream inputStream) throws SchemaComponentException {
    return(load(null, inputStream));
  }
  
  public Schema load(String namespace, InputStream inputStream) throws SchemaComponentException {
    try {
      init();
      SchemaImpl schema = produceSchema_InputSource(new InputSource(inputStream), null, namespace);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(InputStream[] inputStreams) throws SchemaComponentException {
    return(load(new String[inputStreams.length], inputStreams));
  }
  
  public Schema load(String[] namespaces, InputStream[] inputStreams) throws SchemaComponentException {
    try {
      init();
      SchemaImpl[] schemas = new SchemaImpl[inputStreams.length];
      for(int i = 0; i < inputStreams.length; i++) {
        schemas[i] = produceSchema_InputSource(new InputSource(inputStreams[i]), null, namespaces[i]);
      }
      SchemaImpl schema = determineSchema(schemas);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(InputSource inputSource) throws SchemaComponentException {
    return(load(null, inputSource));
  }
  
  public Schema load(String namespace, InputSource inputSource) throws SchemaComponentException {
    try {
      init();
      SchemaImpl schema = produceSchema_InputSource(inputSource, null, namespace);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(InputSource[] inputSources) throws SchemaComponentException {
    return(load(new String[inputSources.length], inputSources));
  }
  
  public Schema load(String[] namespaces, InputSource[] inputSources) throws SchemaComponentException {
    try {
      init();
      SchemaImpl[] schemas = new SchemaImpl[inputSources.length];
      for(int i = 0; i < inputSources.length; i++) {
        schemas[i] = produceSchema_InputSource(inputSources[i], null, namespaces[i]);
      }
      SchemaImpl schema = determineSchema(schemas);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(File file) throws SchemaComponentException {
    return(load(null, file));
  }
  
  public Schema load(String namespace, File file) throws SchemaComponentException {
    try {
      init();
      SchemaImpl schema = determineSchema(produceSchemas_Location(null, retriveSchemaLocation(file), null, namespace));
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(File[] files) throws SchemaComponentException {
    return(load(new String[files.length], files));
  }
  
  public Schema load(String[] namespaces, File[] files) throws SchemaComponentException {
    try {
      init();
      SchemaImpl[][] schemas = new SchemaImpl[files.length][];
      for(int i = 0; i < files.length; i++) {
        schemas[i] = produceSchemas_Location(null, retriveSchemaLocation(files[i]), null, namespaces[i]);
      }
      SchemaImpl schema = determineSchema(schemas);
      SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

  public Schema load(Object schemaObj) throws SchemaComponentException {
    if(!schemaObj.getClass().isArray()) {
      return(load(null, schemaObj));
    }
    Schema result = null;
    if(schemaObj instanceof String[]) {
      result = load((String[])schemaObj);
    } else if(schemaObj instanceof Node[]) {
      result = load((Node[])schemaObj);
    } else if(schemaObj instanceof InputStream[]) {
      result = load((InputStream[])schemaObj);
    } else if(schemaObj instanceof InputSource[]) {
      result = load((InputSource[])schemaObj);
    } else if(schemaObj instanceof File[]) {
      result = load((File[])schemaObj);
    } else if(schemaObj instanceof Source[]) {
      result = load((Source[])schemaObj);
    } else if(schemaObj instanceof String[][]) {
      String[][] namespacesAndLocations = (String[][])schemaObj;
      result = load(namespacesAndLocations[0], namespacesAndLocations[1]);
    } else if(schemaObj instanceof Object[]) {
      result = load((Object[])schemaObj);
    } else {
      throw new SchemaComponentException("Unable to load schema. Schema source object with class : " + schemaObj.getClass().getName() + " is not supported.");
    }
    return(result);
  }

  public Schema load(String namespace, Object schemaObj) throws SchemaComponentException {
    Schema result = null;
    if(schemaObj instanceof String) {
      result = load(namespace, (String)schemaObj);
    } else if(schemaObj instanceof Node) {
      result = load(namespace, (Node)schemaObj);
    } else if(schemaObj instanceof InputStream) {
      result = load(namespace, (InputStream)schemaObj);
    } else if(schemaObj instanceof InputSource) {
      result = load(namespace, (InputSource)schemaObj);
    } else if(schemaObj instanceof File) {
      result = load(namespace, (File)schemaObj);
    } else if(schemaObj instanceof Source) {
      result = load(namespace, (Source)schemaObj);
    } else {
      throw new SchemaComponentException("Unable to load schema. Schema source object with class : " + schemaObj.getClass().getName() + " is not supported.");
    }
    return(result);
  }

  public Schema load(Object[] schemaObjs) throws SchemaComponentException {
    return(load(new String[schemaObjs.length], schemaObjs));
  }
  
  public Schema load(String[] namespaces, Object[] schemaObjs) throws SchemaComponentException {
    try {
      init();
  		SchemaImpl[][] schemas = new SchemaImpl[schemaObjs.length][];
	  	for(int i = 0; i < schemaObjs.length; i++) {
	  		Object schemaObj = schemaObjs[i];
	  		if(schemaObj instanceof String) {
	  			String location = (String)schemaObj;
          schemas[i] = produceSchemas_Location(null, location, null, namespaces[i]);
	  		} else if(schemaObj instanceof Node) {
	  			Node node = (Node)schemaObj;
          schemas[i] = new SchemaImpl[]{produceSchema_Node(node, namespaces[i])};
	  		} else if(schemaObj instanceof Source) {
	  			Source source = (Source)schemaObj;
          schemas[i] = produceSchemas_Source(source, null, namespaces[i], false);
	  		} else if(schemaObj instanceof InputStream) {
					InputSource source = new InputSource((InputStream)schemaObj);
          schemas[i] = new SchemaImpl[]{produceSchema_InputSource(source, null, namespaces[i])};
	  		} else if(schemaObj instanceof InputSource) {
					InputSource source = (InputSource)schemaObj;
          schemas[i] = new SchemaImpl[]{produceSchema_InputSource(source, null, namespaces[i])};
	  		} else if(schemaObj instanceof File) {
	  			File file = (File)schemaObj;
          schemas[i] = produceSchemas_Location(null, retriveSchemaLocation(file), null, namespaces[i]);
        } else {
					throw new SchemaComponentException("Unable to load schema. Schema source object with class : " + schemaObj.getClass().getName() + " is not supported.");
        }
      }
			SchemaImpl schema = determineSchema(schemas);
			SchemaStructuresLoader.loadBase(schema);
      return(schema);
    } finally {
      finish();
    }
  }

////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setEntityResolver(EntityResolver entityResolver) {
  	this.entityResolver = entityResolver;
  }

  public void setUriResolver(URIResolver uriResolver) {
    this.uriResolver = uriResolver;
  }

  public EntityResolver getEntityResolver() {
  	return(entityResolver);
  }

  public URIResolver getUriResolver() {
    return(uriResolver);
  }

  private void init() {
		locationToSchemasMapping = new Hashtable();
		nsToSchemasMapping = new Hashtable();	
		nsToNsModifiedSchemaLocationsMapping = new Hashtable();
  }

  private void finish() {
		locationToSchemasMapping = null;
		nsToSchemasMapping = null;
		nsToNsModifiedSchemaLocationsMapping = null;
  }

  private String createSchemaStructuresResolverRegistrationKey(String namespace, String location) {
    return(location == null || namespace == null ? null : "<" + namespace + "> : " + location);
  }

  public String canonicalizeLocation(String location) {
    String result = null;
    if(location != null) {
      if(location.startsWith("http:")) {
        return(canonicalizeHttpLocation(location));
      } else if(location.startsWith("file:")) {
        return(canonicalizeFileLocation(location, true));
      } else if(location.startsWith("jar:")) {
        return(canonicalizeJarLocation(location));
      }
      return(canonicalizeFileLocation(location, false));
    }
    return(null);
  }
  
  private String canonicalizeFileLocation(String location, boolean hasScheme) {
    if(hasScheme) {
      return(canonicalizeLocation("file", location, "/"));
    }
    location.replace('\\', '/');
    return("file:/" + location);
  }
  
  private static String canonicalizeHttpLocation(String location) {
    return(canonicalizeLocation("http", location, "//"));
  }

  private static String canonicalizeJarLocation(String location) {
    return(canonicalizeLocation("jar", location, ""));
  }
  
  private static String canonicalizeLocation(String scheme, String location, String schemeFromPathDelimiter) {
    int index = scheme.length() + 1;
    while(location.charAt(index++) == '/' && index < location.length());
    String canonicalizaedLocation = scheme + ":" + schemeFromPathDelimiter + location.substring(index - 1);
    return(canonicalizaedLocation.replace('\\', '/'));
  }
  
//  private String canonicalizeLocation(String location) {
//    return(Tools.canonicalizeLocation(location));
//  }

  private String retriveSchemaLocation(File file) throws SchemaComponentException {
    String schemaLocation = null;
    try {
      schemaLocation = file.getCanonicalPath();
    } catch(IOException ioExc) {
      throw new SchemaComponentException("ERROR : An error occured while tring to retrieve schema location.", ioExc);
    }
    return(schemaLocation);
  }

  private TransformerImpl determineTransformer() throws SchemaComponentException {
    if(transformer == null) {
      TransformerFactory factory = new com.sap.engine.lib.jaxp.TransformerFactoryImpl();
      try {
        transformer = (TransformerImpl)(factory.newTransformer());
        transformer.setAttribute(Features.FEATURE_NAMESPACES, Boolean.TRUE);
        transformer.setAttribute(Features.FEATURE_VALIDATION, new Boolean(validateXSDDoc));
        transformer.setAttribute(Features.FEATURE_READ_DTD, Boolean.FALSE);
        if(validateXSDDoc) {
          transformer.setAttribute(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE, SCHEMA_LANGUAGE);
          transformer.setAttribute(JAXPProperties.PROPERTY_SCHEMA_OBJECT, determineXSDForXSDDoc());
        }
      } catch(Exception exc) {
        throw new SchemaComponentException(exc);
      }
    }
    return(transformer);
  }

  private SchemaImpl determineSchema(SchemaImpl[] schemas) {
  	SchemaImpl schema = null;
  	if(schemas.length == 1) {
  		schema = schemas[0]; 
  	} else {
  		schema = new SchemaImpl(schemas);
  	}
  	return(schema);
  }
  
	private SchemaImpl determineSchema(SchemaImpl[][] schemas) {
		SchemaImpl schema = null;
		if(schemas.length == 1 && schemas[0].length == 1) {
			schema = schemas[0][0]; 
		} else {
			schema = new SchemaImpl(schemas);
		}
		return(schema);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public SchemaImpl[] produceSchemas_Location(String parentLocation, String location, String includingNamespace, String requiredNamespace) throws SchemaComponentException {
    try {
			if(uriResolver != null) {
				Source source = uriResolver.resolve(location, parentLocation == null ? "xsd://" : parentLocation); 
				if(source != null) {
					return(produceSchemas_Source(source, includingNamespace, requiredNamespace, false));
				}
      }
      if(entityResolver != null) {
        InputSource inputSource = entityResolver.resolveEntity(null, location);
        if(inputSource != null) {
          return(new SchemaImpl[]{produceSchema_InputSource(inputSource, includingNamespace, requiredNamespace)});
        }
      }
			return(new SchemaImpl[]{produceSchema_Location(parentLocation, location, includingNamespace, requiredNamespace)});
    } catch(Exception exc) {
      if(exc instanceof SchemaComponentException) {
        throw (SchemaComponentException)exc;
      } else {
        throw new SchemaComponentException(exc);
      }
    }
  }
  
  private SchemaImpl produceSchema_Location(String parentLocation, String location, String includingNamespace, String requiredNamespace) throws SchemaComponentException {
    try {
      URL baseURL = parentLocation == null ? null : new URL(parentLocation);
      URL schemaURL = URLLoader.fileOrURLToURL(baseURL, location);
      String canonicalizedLocation = canonicalizeLocation(schemaURL.toExternalForm());
      SchemaImpl schema = getRegisteredSchema(canonicalizedLocation, includingNamespace);
      if(schema == null) {
        Element schemaElement = retriveElement(new InputSource(schemaURL.openStream()));
        schema = createSchema(schemaElement, canonicalizedLocation, includingNamespace, requiredNamespace, false);  
      }
      return(schema);
    } catch(Exception exc) {
      if(exc instanceof SchemaComponentException) {
        throw (SchemaComponentException)exc;
      } else {
        throw new SchemaComponentException(exc);
      }
    }
  }

	private SchemaImpl produceSchema_InputSource(InputSource inputSource, String includingNamespace, String requiredNamespace) throws SchemaComponentException {
    if(inputSource.getByteStream() == null && inputSource.getCharacterStream() == null) {
      if(inputSource.getSystemId() == null) {
        throw new SchemaComponentException("ERROR : Empty input source. Input source without system id, input stream and reader detected.");
      }
      return(produceSchema_Location(null, inputSource.getSystemId(), includingNamespace, requiredNamespace));
    }
    try {
      String location = inputSource.getSystemId();
      String canonicalizedLocation = canonicalizeLocation(location);
			SchemaImpl schema = getRegisteredSchema(canonicalizedLocation, includingNamespace);
			if(schema == null) {
        Element schemaElement = retriveElement(inputSource);
				schema = createSchema(schemaElement, canonicalizedLocation, includingNamespace, requiredNamespace, false); 	
      }
			return(schema);
    } catch(Exception exc) {
      if(exc instanceof SchemaComponentException) {
        throw (SchemaComponentException)exc;
      } else {
        throw new SchemaComponentException(exc);
      }
    }
  }

	private SchemaImpl getRegisteredSchema(String canonicalizedLocation, String includingNamespace) {
		SchemaImpl schema = null;
		if(canonicalizedLocation != null) {
			if(includingNamespace != null && !includingNamespace.equals("")) {
				schema = getRegisteredNsUnmodifiedSchema(canonicalizedLocation);
				if(schema != null) {
					if(schema.getTargetNamespace().equals("")) {
						schema = getRegisteredNsModifiedSchema(canonicalizedLocation, includingNamespace);
				}
				} else {
					schema = getRegisteredNsModifiedSchema(canonicalizedLocation, includingNamespace);
			}
			} else {
				schema = getRegisteredNsUnmodifiedSchema(canonicalizedLocation);
			}	
		}
		return(schema);
	}
	
	private SchemaImpl getRegisteredNsUnmodifiedSchema(String canonicalizedLocation) {
		return((SchemaImpl)(locationToSchemasMapping.get(canonicalizedLocation)));
	}
	
	private SchemaImpl getRegisteredNsModifiedSchema(String canonicalizedLocation, String includingNamespace) {
		Hashtable locationToIncludedSchemasMapping = (Hashtable)(nsToNsModifiedSchemaLocationsMapping.get(includingNamespace));
		SchemaImpl schema = null;
		if(locationToIncludedSchemasMapping != null) {
			schema = (SchemaImpl)(locationToIncludedSchemasMapping.get(canonicalizedLocation)); 
		}
		return(schema);
	}
	
	private SchemaImpl createSchema(Element schemaElement, String canonicalizedLocation, String includingNamespace, String requiredNamespace, boolean fromWSDLDocument) throws SchemaComponentException {
		SchemaImpl schema = new SchemaImpl(schemaElement, this, canonicalizedLocation, includingNamespace);
		if(requiredNamespace != null && !schema.getTargetNamespace().equals(requiredNamespace)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Target namespace '" + requiredNamespace + "' is required.");
		}
		registerSchema(canonicalizedLocation, schema, includingNamespace, fromWSDLDocument);
		schema.collectComponents();
		return(schema);
	}
	
	private void registerSchema(String canonicalizedLocation, SchemaImpl schema, String includingNamespace, boolean fromWSDLDocument) {
		registerNs(schema);
		if(canonicalizedLocation != null && !fromWSDLDocument) {
			if(includingNamespace != null) {
				if(includingNamespace.equals(schema.getTargetNamespace())) {
					registerNsUnmodifiedSchema(canonicalizedLocation, schema);
				} else {
					registerNsModifiedSchema(canonicalizedLocation, schema, includingNamespace);
				}
			} else {
				registerNsUnmodifiedSchema(canonicalizedLocation, schema);
			}
		}
  }
	
	private void registerNsUnmodifiedSchema(String canonicalizedLocation, SchemaImpl schema) {
		locationToSchemasMapping.put(canonicalizedLocation, schema);
	}
	
	private void registerNsModifiedSchema(String canonicalizedLocation, SchemaImpl schema, String includingNamespace) {
		Hashtable locationToSchemaMapping = (Hashtable)(nsToNsModifiedSchemaLocationsMapping.get(includingNamespace));
		if(locationToSchemaMapping == null) {
			locationToSchemaMapping = new Hashtable();
			nsToNsModifiedSchemaLocationsMapping.put(includingNamespace, locationToSchemaMapping);
    }
		locationToSchemaMapping.put(canonicalizedLocation, schema);
	}
	
	private void registerNs(SchemaImpl schema) {
		Vector schemasCollector = (Vector)(nsToSchemasMapping.get(schema.getTargetNamespace()));
		if(schemasCollector == null) {
			schemasCollector = new Vector();
			nsToSchemasMapping.put(schema.getTargetNamespace(), schemasCollector);
		}
		schemasCollector.add(schema);
  }

	private SchemaImpl[] produceSchemas_Source(Source source, String includingNamespace, String requiredNamespace, boolean fromWSDLDocument) throws SchemaComponentException {
	    Source[] sources = null;
	    if(source instanceof MultiSource) {
	      MultiSource multiSource = (MultiSource)source;
	      sources = multiSource.getSources();
	    } else {
	      sources = new Source[]{source};
	    }
	    DOMResult domResult = new DOMResult();
	    SchemaImpl[] schemas = new SchemaImpl[sources.length];
	    for(int i = 0; i < sources.length; i++) {
	      Source schemaSource = sources[i];
	      if(schemaSource instanceof StreamSource && ((StreamSource)schemaSource).getInputStream() == null && ((StreamSource)schemaSource).getReader() == null) {
          if(((StreamSource)schemaSource).getSystemId() == null) {
            throw new SchemaComponentException("ERROR : Empty stream source. Stream source without system id, input stream and reader detected.");
          }
          schemas[i] = produceSchema_Location(null, schemaSource.getSystemId(), includingNamespace, requiredNamespace);
        } else {
  	      String location = schemaSource.getSystemId();
  	      String canonicalizedLocation = canonicalizeLocation(location);
  	      domResult.setNode(null);
  	      SchemaImpl schema = getRegisteredSchema(canonicalizedLocation, includingNamespace);
  	      if(schema == null) {
  	        Node node = null;
  	        if(schemaSource instanceof DOMSource) {
  	          node = ((DOMSource)schemaSource).getNode();
  	        } else {
  	          try {
  	            determineTransformer().transform(schemaSource, domResult);
  	          } catch(TransformerException transformerExc) {
  	            throw new SchemaComponentException("[location : '" + canonicalizedLocation + "'] ERROR : " + transformerExc.getMessage(), transformerExc);
  	          }
  	          node = domResult.getNode();
  	        }
  	        schema = createSchema(retriveElement(node), canonicalizedLocation, includingNamespace, requiredNamespace, fromWSDLDocument);
  	      }
  	      schemas[i] = schema;
  	    }
	    }
	    return(schemas);
	}

  private SchemaImpl produceSchema_Node(Node node, String requiredNamespace) throws SchemaComponentException {
		try {
			URL nodeURL = DOM.getLocation(node);
			String location = nodeURL == null ? null : nodeURL.toExternalForm();
			String canonicalizedLocation = canonicalizeLocation(location);
			SchemaImpl schema = getRegisteredSchema(canonicalizedLocation, null);
			if(schema == null) {
				Element schemaElement = retriveElement(node);
				schema = createSchema(schemaElement, canonicalizedLocation, null, requiredNamespace, false);  
			}
			return(schema);
    } catch(Exception exc) {
      if(exc instanceof SchemaComponentException) {
        throw (SchemaComponentException)exc;
			} else {
        throw new SchemaComponentException(exc);
      }
    }
  }

	public SchemaImpl[] produceSchemas_Namespace(String namespace) throws SchemaComponentException {
		try {
			SchemaImpl[] schemas = produceSchemas_Namespace_UriResolver(namespace);
			if(schemas == null) {
				Vector schemasCollector = (Vector)(nsToSchemasMapping.get(namespace));
				if(schemasCollector != null) {
					schemas = new SchemaImpl[schemasCollector.size()];
					schemasCollector.copyInto(schemas);
				}
			}
			return(schemas); 
    } catch(Exception exc) {
      if(exc instanceof SchemaComponentException) {
        throw (SchemaComponentException)exc;
			} else {
        throw new SchemaComponentException(exc);
			}
    }
  }

	public SchemaImpl[] produceSchemas_Namespace_UriResolver(String namespace) throws SchemaComponentException {
		try {
			if(uriResolver != null) {
				Source source = uriResolver.resolve(namespace, "xsd://");
				if(source != null) {
					return(produceSchemas_Source(source, null, namespace, false));
				}
			}
			return(null);
    } catch(Exception exc) {
      if(exc instanceof SchemaComponentException) {
        throw (SchemaComponentException)exc;
			} else {
        throw new SchemaComponentException(exc);
			}
		}
  }

  private DOMParser determineParser() throws SchemaComponentException {
    if(parser == null) {
      try {
        parser = new DOMParser();
        parser.setFeature(Features.FEATURE_NAMESPACES, true);
        parser.setFeature(Features.FEATURE_VALIDATION, validateXSDDoc);
        parser.setFeature(Features.FEATURE_READ_DTD, false);
        if(validateXSDDoc) {
          parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE, SCHEMA_LANGUAGE);
          parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_OBJECT, determineXSDForXSDDoc());
        }
      } catch(Exception exc) {
        throw new SchemaComponentException(exc);
      }
    }
    return(parser);
  }

  private SchemaImpl determineXSDForXSDDoc() throws SchemaComponentException {
   if(xmlSDForXSDDoc == null) {
     LoaderImpl loader = new LoaderImpl();
     loader.setValidateXSDDoc(false);
     loader.setEntityResolver(new SchemaForSchemaEntityResolver());
     xmlSDForXSDDoc = (SchemaImpl)(loader.load(SCHEMA_FOR_SCHEMA_FILE_NAME));
   }
   return(xmlSDForXSDDoc);
  }

  private Element retriveElement(InputSource inputSource) throws SchemaComponentException {
    try {
      DOMParser parser = determineParser();
      return(parser.parse(inputSource).getDocumentElement());
    } catch(Exception exc) {
      if(exc instanceof SchemaComponentException) {
        throw (SchemaComponentException)exc;
      } else {
        throw new SchemaComponentException(exc);
      }
    }
  }

  private Element retriveElement(Node node) {
    Element result = null;
    if(node instanceof Element) {
      result = (Element)node;
    } else if(node instanceof Document) {
      Document doc = (Document)node;
      result = doc.getDocumentElement();
    }
    return(result);
  }
}

