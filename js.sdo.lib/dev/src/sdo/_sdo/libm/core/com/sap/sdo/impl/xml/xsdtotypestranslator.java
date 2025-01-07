package com.sap.sdo.impl.xml;

import static com.sap.sdo.impl.types.builtin.TypeType.CHAR_ATTRIBUTE;
import static com.sap.sdo.impl.types.builtin.TypeType.CHAR_ELEMENT;
import static com.sap.sdo.impl.types.builtin.TypeType.CHAR_FORM;
import static com.sap.sdo.impl.types.builtin.TypeType.CHAR_GROUP;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.transform.Source;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.types.schema.All;
import com.sap.sdo.api.types.schema.Annotated;
import com.sap.sdo.api.types.schema.Attribute;
import com.sap.sdo.api.types.schema.AttributeGroupRef;
import com.sap.sdo.api.types.schema.ComplexContent;
import com.sap.sdo.api.types.schema.ComplexRestrictionType;
import com.sap.sdo.api.types.schema.ComplexType;
import com.sap.sdo.api.types.schema.Element;
import com.sap.sdo.api.types.schema.ExplicitGroup;
import com.sap.sdo.api.types.schema.ExtensionType;
import com.sap.sdo.api.types.schema.Facet;
import com.sap.sdo.api.types.schema.Group;
import com.sap.sdo.api.types.schema.GroupRef;
import com.sap.sdo.api.types.schema.Import;
import com.sap.sdo.api.types.schema.Include;
import com.sap.sdo.api.types.schema.LocalComplexType;
import com.sap.sdo.api.types.schema.LocalElement;
import com.sap.sdo.api.types.schema.LocalSimpleType;
import com.sap.sdo.api.types.schema.NamedAttributeGroup;
import com.sap.sdo.api.types.schema.NamedGroup;
import com.sap.sdo.api.types.schema.Restriction;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.types.schema.SimpleContent;
import com.sap.sdo.api.types.schema.SimpleExtensionType;
import com.sap.sdo.api.types.schema.SimpleRestrictionType;
import com.sap.sdo.api.types.schema.SimpleType;
import com.sap.sdo.api.types.schema.TopLevelAttribute;
import com.sap.sdo.api.types.schema.TopLevelComplexType;
import com.sap.sdo.api.types.schema.TopLevelElement;
import com.sap.sdo.api.types.schema.TopLevelSimpleType;
import com.sap.sdo.api.types.schema.Union;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.MetaDataType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeLogicFacade;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.java.NameConverter;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;

public class XsdToTypesTranslator {

    private static final String QUALIFIED = "qualified";
    private static final String IDREF = URINamePair.SCHEMA_URI + "#IDREF";
    private static final String IDREFS = URINamePair.SCHEMA_URI + "#IDREFS";
    private static final String ANY_URI = URINamePair.SCHEMA_URI + "#anyURI";
    private static final String SDO_URI = URINamePair.URI.toStandardSdoFormat();
    private static final String ANY_TYPE = URINamePair.SCHEMA_URI + "#anyType";
    private static final TypeHelper CORE_TYPE_HELPER = SapHelperProviderImpl.getCoreContext().getTypeHelper();
    private static final Property PROP_XML_NAME = getGlobalProperty(URINamePair.PROP_XML_NAME);
    private static final Property PROP_JAVA_PACKAGE = getGlobalProperty(URINamePair.PROP_JAVA_PACKAGE);
    private static final Property PROP_JAVA_INSTANCE_CLASS = getGlobalProperty(URINamePair.PROP_JAVA_INSTANCE_CLASS);
    private static final Property PROP_JAVA_EXTENDED_INSTANCE_CLASS = getGlobalProperty(URINamePair.PROP_JAVA_EXTENDED_INSTANCE_CLASS);
    private static final Property PROP_XML_ALIAS_NAME = getGlobalProperty(URINamePair.PROP_XML_ALIAS_NAME);
    private static final Property PROP_XML_SEQUENCE = getGlobalProperty(URINamePair.PROP_XML_SEQUENCE);
    private static final Property PROP_XML_PROPERTY_TYPE = getGlobalProperty(URINamePair.PROP_XML_PROPERTY_TYPE);
    private static final Property PROP_XML_READ_ONLY = getGlobalProperty(URINamePair.PROP_XML_READ_ONLY);
    private static final Property PROP_XML_OPPOSITE_PROPERTY = getGlobalProperty(URINamePair.PROP_XML_OPPOSITE_PROPERTY);
    private static final Property PROP_XML_MANY = getGlobalProperty(URINamePair.PROP_XML_MANY);
    private static final Property PROP_XML_DATA_TYPE = getGlobalProperty(URINamePair.PROP_XML_DATA_TYPE);
    private static final Property PROP_XML_STRING = getGlobalProperty(URINamePair.PROP_XML_STRING);
    private static final Property PROP_XML_ORPHAN_HOLDER = getGlobalProperty(URINamePair.PROP_XML_ORPHAN_HOLDER);
    private static final Property PROP_XML_KEY = getGlobalProperty(URINamePair.PROP_XML_KEY);
    private static final Property PROP_XML_EMBEDDED_KEY = getGlobalProperty(URINamePair.PROP_XML_EMBEDDED_KEY);
    private static final Property PROP_CTX_SCHEMA_REFERENCE = getGlobalProperty(URINamePair.PROP_CTX_SCHEMA_REFERENCE);
    private static final String FORM_SUFFIX = String.valueOf(CHAR_FORM);

    private Map<SchemaLocation, Schema> _schemas = new HashMap<SchemaLocation, Schema>();
    private Set<SchemaLocation> _schemaLocations = new HashSet<SchemaLocation>();
    private SchemaResolver _schemaResolver;
    final private Map _options = new HashMap();
    private final boolean _mixedCase;
    private final SapHelperContext _helperContext;
    private List<DataObject> _translatedTypes;
    private List<DataObject> _newDefinedTypes = new ArrayList<DataObject>();
    private boolean _defined = false;
    private final Map<URINamePair, DataObject> _hollowTypesByXsdName = new HashMap<URINamePair, DataObject>();
    private final Map<URINamePair, DataObject> _hollowAttributesByXsdName = new HashMap<URINamePair, DataObject>();
    private final Map<URINamePair, DataObject> _hollowElementsByXsdName = new HashMap<URINamePair, DataObject>();
    private final Map<URINamePair, SdoType> _finishedTypesByXsdName = new HashMap<URINamePair, SdoType>();
    private final Map<URINamePair, DataObject> _finishedAttributesByXsdName = new HashMap<URINamePair, DataObject>();
    private final Map<URINamePair, DataObject> _finishedElementsByXsdName = new HashMap<URINamePair, DataObject>();
    private final Map<URINamePair, NamedAttributeGroup> _attributeGroupsByXsdName = new HashMap<URINamePair, NamedAttributeGroup>();
    private final Map<URINamePair, NamedGroup> _groupsByXsdName = new HashMap<URINamePair, NamedGroup>();
    private final Map<DataObject, List<DataObject>> _deferredReferences = new HashMap<DataObject, List<DataObject>>();
    private final List<DataObject> _oppositeProperties = new ArrayList<DataObject>();
    private static Map<String, DataObject> _buildInFacets;

    public XsdToTypesTranslator(SapHelperContext pHelperContext, Map pOptions) {
        _helperContext = pHelperContext;
        if (pOptions != null) {
            _options.putAll(pOptions);
            _schemaResolver = (SchemaResolver)_options.get(SchemaResolver.class.getName());
        }
        if (_schemaResolver == null) {
            _schemaResolver = ((SapXsdHelper)_helperContext.getXSDHelper()).getDefaultSchemaResolver();
            _options.put(SchemaResolver.class.getName(), _schemaResolver);
        }
        _options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
        _mixedCase = !Boolean.FALSE.equals(_helperContext.getContextOption(
                            SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES));
    }

    public List<Type> defineTypes() throws IOException {
        translateSchemas();
        List<DataObject> createdTypes = getTypes();
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        for (DataObject typeObj : createdTypes) {
            Type type = typeHelper.getType(
                typeObj.getString(TypeConstants.URI),
                typeObj.getString(TypeConstants.NAME));
            if (type == null) {
                _newDefinedTypes.add(typeObj);
            }
        }
        List<Type> types = typeHelper.define(createdTypes);
        for (Entry<URINamePair, DataObject> entry: _finishedAttributesByXsdName.entrySet()) {
            typeHelper.defineOpenContentProperty(entry.getKey().getURI(), entry.getValue());
        }
        for (Entry<URINamePair, DataObject> entry: _finishedElementsByXsdName.entrySet()) {
            typeHelper.defineOpenContentProperty(entry.getKey().getURI(), entry.getValue());
        }
        _translatedTypes = (List)types;
        _defined = true;
        // mark this schemas as defined in the xsdHelper
        XSDHelperImpl xsdHelper = (XSDHelperImpl)_helperContext.getXSDHelper();
        for (SchemaLocation schemaLocation: _schemas.keySet()) {
            if (schemaLocation.getAbsoluteSchemaLocation() != null) {
                xsdHelper.addSchemaLocation(schemaLocation);
            }
        }
        return types;
    }

    public boolean isDefined() {
        return _defined;
    }

    public void translateSchemas() throws IOException {
        if (isTranslated()) {
            return;
        }
        loadReferencedSchemas();
        backupSchemas();
        createToplevelPropsAndTypes();
        while (!_hollowTypesByXsdName.isEmpty()) {
            URINamePair xsdTypeUnp = _hollowTypesByXsdName.keySet().iterator().next();
            finishToplevelType(xsdTypeUnp);
        }
        while (!_hollowAttributesByXsdName.isEmpty()) {
            URINamePair xsdPropertyUnp = _hollowAttributesByXsdName.keySet().iterator().next();
            finishToplevelAttribute(xsdPropertyUnp);
        }
        while (!_hollowElementsByXsdName.isEmpty()) {
            URINamePair xsdPropertyUnp = _hollowElementsByXsdName.keySet().iterator().next();
            finishToplevelElement(xsdPropertyUnp);
        }
        Set<DataObject> types = new TreeSet<DataObject>(TypesComparatorDO.INSTANCE);
        Set<Schema> schemas = new HashSet<Schema>(_schemas.values());
        for (Type type: _finishedTypesByXsdName.values()) {
            addType((DataObject)type, types, schemas);
        }
        for (DataObject property: _finishedAttributesByXsdName.values()) {
            addType(property.getDataObject(PropertyType.TYPE), types, schemas);
        }
        for (DataObject property: _finishedElementsByXsdName.values()) {
            addType(property.getDataObject(PropertyType.TYPE), types, schemas);
        }
        createOppositeProperties(types);
        _translatedTypes = new ArrayList<DataObject>(types);
    }

    private void backupSchemas() {
        CopyHelper copyHelper = _helperContext.getCopyHelper();
        Map<SchemaLocation, Schema> copiedSchemas = new HashMap<SchemaLocation, Schema>();
        for (Entry<SchemaLocation, Schema> entry: _schemas.entrySet()) {
            Schema schema = entry.getValue();
            if (schema.getContainer() != null) {
                // in that case the schema is part of an xml-document
                // make a copy for further processing
                schema = (Schema)copyHelper.copy(schema);
                copiedSchemas.put(entry.getKey(), schema);
            }
        }
        _schemas.putAll(copiedSchemas);
    }

    private boolean isTranslated() {
        return _translatedTypes != null;
    }

    public List<DataObject> getTypes() {
        return _translatedTypes;
    }

    public List<DataObject> getNewDefinedTypes() {
        return _newDefinedTypes;
    }

    private void addType(DataObject pType, Set<DataObject> pTypes, Set<Schema> pSchemas) {
        if (pTypes.contains(pType)) {
            return;
        }
        DataObject schemaItem = pType.getDataObject(PROP_CTX_SCHEMA_REFERENCE);
        if (schemaItem == null) {
            if (pType instanceof ListSimpleType) {
                // TODO add also the ListSimpleType?
                addType((DataObject)((ListSimpleType)pType).getItemType(), pTypes, pSchemas);
                pTypes.add(pType);
            }
            return;
        } else {
            if (!pSchemas.contains(schemaItem.getRootObject())) {
                // type is not defined in these schemas
                return;
            }
        }
        pTypes.add(pType);
        for (DataObject baseType: (List<DataObject>)pType.getList(TypeType.BASE_TYPE)) {
            addType(baseType, pTypes, pSchemas);
        }
        TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
        for (Iterator<DataObject> it = pType.getList(TypeType.PROPERTY).iterator(); it.hasNext();) {
            DataObject property = it.next();
            if (typeHelper.isPropInBaseTypes((Type)pType, (Property)property)) {
                it.remove();
            } else {
                addType(property.getDataObject(PropertyType.TYPE), pTypes, pSchemas);
            }
        }
    }

    public Map<String, List<DataObject>> getNamespaceToProperties() {
        Map<String, List<DataObject>> namespaceToProperties = new HashMap<String, List<DataObject>>();
        addProperties(_finishedAttributesByXsdName, namespaceToProperties);
        addProperties(_finishedElementsByXsdName, namespaceToProperties);
        return namespaceToProperties;
    }

    private void addProperties(Map<URINamePair, DataObject> pPropertiesByXsdName, Map<String, List<DataObject>> pNamespaceToProperties) {
        for (Entry<URINamePair, DataObject> propertiesByXsdName: pPropertiesByXsdName.entrySet()) {
            List<DataObject> properties = pNamespaceToProperties.get(propertiesByXsdName.getKey().getURI());
            if (properties == null) {
                properties = new ArrayList<DataObject>();
                pNamespaceToProperties.put(propertiesByXsdName.getKey().getURI(), properties);
            }
            properties.add(propertiesByXsdName.getValue());
        }
    }

    public void addSchema(SchemaLocation pSchemaLocation, Schema pSchema) {
        if (isTranslated()) {
            throw new IllegalArgumentException("Schemas are already translated");
        }
        checkSchemaLocation(pSchemaLocation, pSchema);
        _schemas.put(pSchemaLocation, pSchema);
    }

    public void addSchemas(Map<SchemaLocation, Schema> pSchemas) {
        for (Entry<SchemaLocation, Schema> location2Schema: pSchemas.entrySet()) {
            addSchema(location2Schema.getKey(), location2Schema.getValue());
        }
    }

    public void addSchemaLocation(SchemaLocation pSchemaLocation) {
        if (isTranslated()) {
            throw new IllegalArgumentException("Schemas are already translated");
        }
        _schemaLocations.add(pSchemaLocation);
    }

    private void loadReferencedSchemas() throws IOException {
        Map<SchemaLocation, Schema> schemas = new HashMap<SchemaLocation, Schema>(_schemas);
        for (Entry<SchemaLocation, Schema> location2Schema: schemas.entrySet()) {
            loadReferencedSchemas(location2Schema.getKey(), location2Schema.getValue());
        }
        List<SchemaLocation> schemaLocations = new ArrayList<SchemaLocation>(_schemaLocations);
        for (SchemaLocation schemaLocation: schemaLocations) {
            loadSchema(schemaLocation);
        }
    }

    private void loadSchema(SchemaLocation pSchemaLocation) throws IOException {
        if (_schemas.containsKey(pSchemaLocation)) {
            return;
        }
        try {
            final String absoluteSchemaLocation = pSchemaLocation.getAbsoluteSchemaLocation();
            Object resolvedSchema = _schemaResolver.resolveImport(pSchemaLocation.getNameSpace(), absoluteSchemaLocation);
            if (resolvedSchema == null) {
                //skip import
                return;
            }
            XMLDocument document;
            if (resolvedSchema instanceof InputStream) {
                document = _helperContext.getXMLHelper().load((InputStream)resolvedSchema, absoluteSchemaLocation, _options);
            } else if (resolvedSchema instanceof Reader) {
                document = _helperContext.getXMLHelper().load((Reader)resolvedSchema, absoluteSchemaLocation, _options);
            } else if (resolvedSchema instanceof Source) {
                document = _helperContext.getXMLHelper().load((Source)resolvedSchema, absoluteSchemaLocation, _options);
            } else {
                throw new IllegalArgumentException("Unsupported result from SchemaResolver: " + resolvedSchema.getClass().getName());
            }
            DataObject schema = document.getRootObject();
            if (!(schema instanceof Schema)) {
                throw new IllegalArgumentException("Schema " + pSchemaLocation.getAbsoluteSchemaLocation() + " does not start with schema element");
            }
            loadSchema(pSchemaLocation, (Schema)schema);
        } catch (URISyntaxException e) {
            throw new XmlParseException(e);
        }
    }

    private void loadSchema(SchemaLocation pSchemaLocation, Schema pSchema) throws IOException {
        checkSchemaLocation(pSchemaLocation, pSchema);
        _schemas.put(pSchemaLocation, pSchema);
        loadReferencedSchemas(pSchemaLocation, pSchema);
    }

    private void checkSchemaLocation(SchemaLocation pSchemaLocation, Schema pSchema) {
        String internalNameSpace = pSchema.getTargetNamespace();
        if ((internalNameSpace != null) && !internalNameSpace.equals(pSchemaLocation.getNameSpace())) {
            throw new IllegalArgumentException("Namespaces doesn't match: external: "
                + pSchemaLocation.getNameSpace() + " internal: " + internalNameSpace);
        }
    }

    private void loadReferencedSchemas(SchemaLocation pSchemaLocation, Schema pSchema) throws IOException {
        List<Import> imports = pSchema.getImport();
        for (Import schemaImport: imports) {
            String schemaLocation = schemaImport.getSchemaLocation();
            if (schemaLocation == null) {
                continue;
            }
            try {
                String absoluteSchemaLocation = _schemaResolver.getAbsoluteSchemaLocation(schemaLocation, pSchemaLocation.getAbsoluteSchemaLocation());
                loadSchema(new SchemaLocation(schemaImport.getNamespace(), absoluteSchemaLocation));
            } catch (URISyntaxException e) {
                throw new XmlParseException(e);
            }
        }
        List<Include> includes = pSchema.getInclude();
        for (Include schemaInclude: includes) {
            String schemaLocation = schemaInclude.getSchemaLocation();
            try {
                String absoluteSchemaLocation = _schemaResolver.getAbsoluteSchemaLocation(schemaLocation, pSchemaLocation.getAbsoluteSchemaLocation());
                loadSchema(new SchemaLocation(pSchemaLocation.getNameSpace(), absoluteSchemaLocation));
            } catch (URISyntaxException e) {
                throw new XmlParseException(e);
            }
        }
    }

    private void createToplevelPropsAndTypes() {
        for (Entry<SchemaLocation, Schema> location2Schema: _schemas.entrySet()) {
            String nameSpace = location2Schema.getKey().getNameSpace();
            Schema schema = location2Schema.getValue();
            changeNamespace(schema, nameSpace);
            for (TopLevelSimpleType simpleType: schema.getSimpleType()) {
                String xsdName = simpleType.getName();
                if (((TypeHelperImpl)_helperContext.getTypeHelper()).getTypeByXmlName(nameSpace, xsdName) instanceof MetaDataType) {
                    // Don't generate built-in types
                    continue;
                }
                DataObject typeObj = _helperContext.getDataFactory().create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
                typeObj.setBoolean(TypeType.DATA_TYPE, true);
                setToplevelTypeName(typeObj, simpleType, nameSpace, xsdName);
                typeObj.set(PROP_CTX_SCHEMA_REFERENCE, simpleType);
                _hollowTypesByXsdName.put(new URINamePair(nameSpace, xsdName), typeObj);
            }
            for (TopLevelComplexType complexType: schema.getComplexType()) {
                String xsdName = complexType.getName();
                if (((TypeHelperImpl)_helperContext.getTypeHelper()).getTypeByXmlName(nameSpace, xsdName) instanceof MetaDataType) {
                    // Don't generate built-in types
                    continue;
                }
                DataObject typeObj = _helperContext.getDataFactory().create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
                typeObj.setBoolean(TypeType.DATA_TYPE, false);
                setToplevelTypeName(typeObj, complexType, nameSpace, xsdName);
                typeObj.set(PROP_CTX_SCHEMA_REFERENCE, complexType);
                _hollowTypesByXsdName.put(new URINamePair(nameSpace, xsdName), typeObj);
            }
            for (TopLevelAttribute globalAttribute: schema.getAttribute()) {
                DataObject propertyObj = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
                String xsdName = globalAttribute.getName();
                propertyObj.set(PropertyType.getSchemaReferenceProperty(), globalAttribute);
                _hollowAttributesByXsdName.put(new URINamePair(nameSpace, xsdName), propertyObj);
            }
            for (TopLevelElement globalElement: schema.getElement()) {
                DataObject propertyObj = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
                propertyObj.setBoolean(PropertyType.MANY, true);
                String xsdName = globalElement.getName();
                propertyObj.set(PropertyType.getSchemaReferenceProperty(), globalElement);
                _hollowElementsByXsdName.put(new URINamePair(nameSpace, xsdName), propertyObj);
            }
            for (NamedAttributeGroup attributeGroup: schema.getAttributeGroup()) {
                String xsdName = attributeGroup.getName();
                _attributeGroupsByXsdName.put(new URINamePair(nameSpace, xsdName), attributeGroup);
            }
            for (NamedGroup group: schema.getGroup()) {
                String xsdName = group.getName();
                _groupsByXsdName.put(new URINamePair(nameSpace, xsdName), group);
            }
        }
    }

    private void changeNamespace(Schema pSchema, String pTargetNamespace) {
        String targetNamespace = pSchema.getTargetNamespace();
        if (targetNamespace == null) {
            pSchema.setTargetNamespace(pTargetNamespace);
        }
        if (!pTargetNamespace.equals(targetNamespace) && pTargetNamespace.length() > 0) {
            changeQNames(pSchema, pTargetNamespace);
        }
    }

    private void changeQNames(DataObject pSchemaItem, String pTargetNamespace) {
        for (SdoProperty property: (List<SdoProperty>)pSchemaItem.getInstanceProperties()) {
            if (pSchemaItem.isSet(property)) {
                if (!property.getType().isDataType()) {
                    if (property.isMany()) {
                        for (DataObject dataObject: (List<DataObject>)pSchemaItem.getList(property)) {
                            changeQNames(dataObject, pTargetNamespace);
                        }
                    } else {
                        DataObject dataObject = pSchemaItem.getDataObject(property);
                        changeQNames(dataObject, pTargetNamespace);
                    }
                } else if (URINamePair.SCHEMA_Q_NAME.equals(property.getXsdType())) {
                    if (property.isMany()) {
                        List<String> qNames = pSchemaItem.getList(property);
                        for (int i = 0; i < qNames.size(); i++) {
                            URINamePair qName = URINamePair.fromStandardSdoFormat(qNames.get(i));
                            if (qName.getURI().length() == 0) {
                                qName = new URINamePair(pTargetNamespace, qName.getName());
                                qNames.set(i, qName.toStandardSdoFormat());
                            }
                        }
                    } else {
                        URINamePair qName = URINamePair.fromStandardSdoFormat(pSchemaItem.getString(property));
                        if (qName.getURI().length() == 0) {
                            qName = new URINamePair(pTargetNamespace, qName.getName());
                            pSchemaItem.setString(property, qName.toStandardSdoFormat());
                        }
                    }
                }
            }
        }
    }

    private void setToplevelTypeName(DataObject pTypeObject, Annotated pTopLevelTypeSchemaItem, String pUri, String pXsdName) {
        String sdoName = pTopLevelTypeSchemaItem.getString(PROP_XML_NAME);
        if (sdoName == null) {
            pTypeObject.setString(TypeType.NAME, pXsdName);
        } else {
            pTypeObject.setString(TypeType.NAME, sdoName);
            pTypeObject.setString(TypeType.getXmlNameProperty(), pXsdName);
        }
    }

    private void setLocalTypeName(DataObject pTypeObject, Annotated pLocalTypeSchemaItem, final String pUri, String pArtificialName, String pAlias) {
        String name = pLocalTypeSchemaItem.getString(PROP_XML_NAME);
        if (name == null) {
            name = pArtificialName;
            pLocalTypeSchemaItem.setString(PROP_XML_NAME, name);
            if (pAlias != null) {
                List schemaAliases = new ArrayList(pLocalTypeSchemaItem.getList(PROP_XML_ALIAS_NAME));
                if (!schemaAliases.contains(pAlias)) {
                    schemaAliases.add(pAlias);
                    pLocalTypeSchemaItem.setList(PROP_XML_ALIAS_NAME, schemaAliases);
                }
            }
        }
        pTypeObject.set(TypeType.NAME, name);
    }


    private void setPropertyName(DataObject pSdoItem, Annotated pPropertySchemaItem, String pXsdName) {
        String sdoName = pPropertySchemaItem.getString(PROP_XML_NAME);
        if (sdoName == null) {
            pSdoItem.setString(PropertyType.NAME, pXsdName);
        } else {
            pSdoItem.setString(PropertyType.NAME, sdoName);
            pSdoItem.setString(PropertyType.getXmlNameProperty(), pXsdName);
        }
    }

    private void setJavaNameTagIfNecessary(DataObject pPropertyObject) {
        // I don't like that! It only works with luck.
        String sdoName = pPropertyObject.getString(PropertyType.NAME);
        if (_mixedCase) {
            String javaName = NameConverter.CONVERTER.toVariableName(sdoName);
            if (!javaName.equals(sdoName)) {
                pPropertyObject.set(PropertyType.getJavaNameProperty(), javaName);
            }
        }
    }

    private void setInstanceClass(DataObject pTypeObject, Annotated pSchemaItem, Schema pSchema) {
        String packageName = pSchema.getString(PROP_JAVA_PACKAGE);
        if (packageName != null) {
            pTypeObject.setString(TypeType.getPackageProperty(), packageName);
        }
        String className = pSchemaItem.getString(PROP_JAVA_INSTANCE_CLASS);
        if (className == null) {
            className = pSchemaItem.getString(PROP_JAVA_EXTENDED_INSTANCE_CLASS);
        }
        boolean isDataType = pTypeObject.getBoolean(TypeType.DATA_TYPE);
        if (className != null) {
            pTypeObject.setString(TypeType.getJavaClassProperty(), className);
        } else {
            if (isDataType) {
                List<Type> baseTypes = pTypeObject.getList(TypeType.BASE_TYPE);
                if (!baseTypes.isEmpty()) {
                    Class instanceClass = baseTypes.get(0).getInstanceClass();
                    instanceClass = optimizeInstanceClass(pTypeObject, instanceClass, pSchemaItem);
                    pTypeObject.setString(TypeType.getJavaClassProperty(), instanceClass.getName());
                    ((TypeLogicFacade)pTypeObject).setInstanceClass(instanceClass); //TODO use this on SdoType
                }
            } else {
                if (packageName != null) {
                    String javaName;
                    if (_mixedCase) {
                        javaName = NameConverter.CONVERTER.toClassName(pTypeObject.getString(TypeType.NAME));
                    } else {
                        String typeName = pTypeObject.getString(TypeType.NAME);
                        javaName = Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1);
                    }

                    className = packageName + '.' + javaName;
                    pTypeObject.setString(TypeType.getJavaClassProperty(), className);
                }
            }
        }
    }

    private static final BigDecimal MAX_INT_PLUS1 = BigDecimal.valueOf(Integer.MAX_VALUE + 1l);
    private static final BigDecimal MIN_INT_MINUS1 = BigDecimal.valueOf(Integer.MIN_VALUE - 1l);
    private static final int MAX_INT_LENGTH = String.valueOf(Integer.MAX_VALUE).length();

    /**
     * See Java-SDO-Spec-v2.1.0-FINAL chapter 9.2.2
     * @param pBaseTypeClass
     * @return
     */
    public Class optimizeInstanceClass(DataObject pTypeObject, Class pBaseTypeClass, Annotated pSchemaItem) {
        if (pBaseTypeClass == long.class || pBaseTypeClass == Long.class || pBaseTypeClass == BigInteger.class) {
            DataObject facets = pTypeObject.getDataObject(TypeType.getFacetsProperty());
            if (facets != null) {
                boolean lowerBoundariesChecked;
                boolean upperBoundariesChecked;
                if (optimizeIntTotalDigits(facets)
                    || optimizeIntEnumeration(facets)) {
                    lowerBoundariesChecked = true;
                    upperBoundariesChecked = true;
                } else {
                    lowerBoundariesChecked = optimizeIntMinInclusive(facets)
                        || optimizeIntMinExclusive(facets);
                    upperBoundariesChecked = optimizeIntMaxInclusive(facets)
                        || optimizeIntMaxExclusive(facets);
                }
                if (lowerBoundariesChecked && upperBoundariesChecked) {
                    if (Boolean.TRUE.equals(isNullable(pSchemaItem))) {
                        return Integer.class;
                    }
                    return int.class;
                }
            }
        }
        return pBaseTypeClass;
    }

    private boolean optimizeIntTotalDigits(DataObject pFacets) {
        Object totalDigits = pFacets.get(TypeType.FACET_TOTALDIGITS);
        return (totalDigits != null) && ((Integer)totalDigits < MAX_INT_LENGTH);
    }

    private boolean optimizeIntEnumeration(DataObject pFacets) {
        final List<String> enumValues = pFacets.getList(TypeType.FACET_ENUMERATION);
        if (enumValues.isEmpty()) {
            return false;
        }
        for (String enumValue: enumValues) {
            try {
                Integer.parseInt(enumValue);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean optimizeIntMinInclusive(DataObject pFacets) {
        BigDecimal minInclusive = pFacets.getBigDecimal(TypeType.FACET_MININCLUSIVE);
        return (minInclusive != null) && (minInclusive.compareTo(MIN_INT_MINUS1) > 0);
    }

    private boolean optimizeIntMaxInclusive(DataObject pFacets) {
        BigDecimal maxInclusive = pFacets.getBigDecimal(TypeType.FACET_MAXINCLUSIVE);
        return (maxInclusive != null) && (maxInclusive.compareTo(MAX_INT_PLUS1) < 0);
    }

    private boolean optimizeIntMinExclusive(DataObject pFacets) {
        BigDecimal minExclusive = pFacets.getBigDecimal(TypeType.FACET_MINEXCLUSIVE);
        return (minExclusive != null) && (minExclusive.compareTo(MIN_INT_MINUS1) >= 0);
    }

    private boolean optimizeIntMaxExclusive(DataObject pFacets) {
        BigDecimal maxExclusive = pFacets.getBigDecimal(TypeType.FACET_MAXEXCLUSIVE);
        return (maxExclusive != null) && (maxExclusive.compareTo(MAX_INT_PLUS1) <= 0);
    }

    /**
     * Tries to find out, if the surrounding property is nillable.
     * @param pSchemaItem The schema item to explore.
     * @return true for nillable, false for not nillable, null for don't know.
     */
    private Boolean isNullable(Annotated pSchemaItem) {
        if (pSchemaItem instanceof Attribute) {
            return Boolean.FALSE;
        }
        if (pSchemaItem instanceof Element) {
            return ((Element)pSchemaItem).isNillable();
        }
        DataObject container = pSchemaItem.getContainer();
        if (container instanceof Annotated) {
            return isNullable((Annotated)container);
        }
        return null;
    }

    private void translateSimpleType(DataObject pTypeObject, SimpleType pSimpleType, Schema pSchema) {
        pTypeObject.setString(TypeType.URI, pSchema.getTargetNamespace());
        Restriction restriction = pSimpleType.getRestriction();
        com.sap.sdo.api.types.schema.List list = pSimpleType.getList();
        Union union = pSimpleType.getUnion();
        if (restriction != null) {
            translateSimpleTypeRestriction(pTypeObject, restriction, pSchema);
        } else if (list != null) {
            translateSimpleTypeList(pTypeObject, list, pSchema);
        } else if (union != null) {
            translateSimpleTypeUnion(pTypeObject, union, pSchema);
        } else {
            throw new IllegalArgumentException("Invalid simpleType " + getUriNameStringOfType(pTypeObject));
        }
        List aliasNames = pSimpleType.getList(PROP_XML_ALIAS_NAME);
        pTypeObject.getList(TypeType.ALIAS_NAME).addAll(aliasNames);
        setInstanceClass(pTypeObject, pSimpleType, pSchema);
    }

    private void translateSimpleTypeRestriction(DataObject pTypeObject, Restriction pRestriction, Schema pSchema) {
        String base = pRestriction.getBase();
        LocalSimpleType baseSimpleType = pRestriction.getSimpleType();
        SdoType baseType;
        if (base != null) {
            baseType = getTypeByXsdUnp(base);
            setBuildInFacets(pTypeObject, base);
            baseType = checkForNillableVersion(baseType, pRestriction);
            URINamePair baseUri = URINamePair.fromStandardSdoFormat(base);
            if (URINamePair.SCHEMA_BASE64BINARY.equals(baseUri)
                    || URINamePair.SCHEMA_Q_NAME.equals(baseUri)) {
                pTypeObject.setString(TypeType.getSpecialBaseTypeProperty(), base);
            }
        } else if (baseSimpleType != null) {
            String artificialName = pTypeObject.getString((TypeType.NAME)) + "*Base";
            baseType = createLocalSimpleType(baseSimpleType, pSchema, artificialName, null);
        } else {
            throw new IllegalArgumentException("Invalid simpleType restriction " + getUriNameStringOfType(pTypeObject));
        }
        pTypeObject.getList(TypeType.BASE_TYPE).add(baseType);
        // copy facets from BaseType
        DataObject baseTypeFacets = (DataObject)baseType.get(TypeType.getFacetsProperty());
        if (baseTypeFacets != null) {
            baseTypeFacets = _helperContext.getCopyHelper().copy(baseTypeFacets);
            pTypeObject.setDataObject(TypeType.getFacetsProperty(), baseTypeFacets);
        }

        setFacet(pTypeObject, TypeType.FACET_MINEXCLUSIVE, pRestriction.getMinExclusive());
        setFacet(pTypeObject, TypeType.FACET_MININCLUSIVE, pRestriction.getMinInclusive());
        setFacet(pTypeObject, TypeType.FACET_MAXEXCLUSIVE, pRestriction.getMaxExclusive());
        setFacet(pTypeObject, TypeType.FACET_MAXINCLUSIVE, pRestriction.getMaxInclusive());
        setFacet(pTypeObject, TypeType.FACET_TOTALDIGITS, pRestriction.getTotalDigits());
        setFacet(pTypeObject, TypeType.FACET_FRACTIONDIGITS, pRestriction.getFractionDigits());
        setFacet(pTypeObject, TypeType.FACET_LENGTH, pRestriction.getLength());
        setFacet(pTypeObject, TypeType.FACET_MINLENGTH, pRestriction.getMinLength());
        setFacet(pTypeObject, TypeType.FACET_MAXLENGTH, pRestriction.getMaxLength());
        setFacet(pTypeObject, TypeType.FACET_ENUMERATION, pRestriction.getEnumeration());
//            setFacet(pTypeObject, TypeType.FACET_, restriction.getWhiteSpace()); TODO facet WhiteSpace
        setFacet(pTypeObject, TypeType.FACET_PATTERN, pRestriction.getPattern());
    }

    private Map<String, DataObject> getBuildInFacets() {
        if (_buildInFacets == null) {
            _buildInFacets = new HashMap<String, DataObject>();
            DataFactory dataFactory = _helperContext.getDataFactory();
            SdoType facetsType = TypeType.getFacetsType();
            DataObject facetsObject;

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#nonNegativeInteger", facetsObject);
            facetsObject.setInt(TypeType.FACET_MININCLUSIVE, 0);

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#positiveInteger", facetsObject);
            facetsObject.setInt(TypeType.FACET_MININCLUSIVE, 1);

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#nonPositiveInteger", facetsObject);
            facetsObject.setInt(TypeType.FACET_MAXINCLUSIVE, 0);

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#negativeInteger", facetsObject);
            facetsObject.setInt(TypeType.FACET_MAXINCLUSIVE, -1);

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#unsignedLong", facetsObject);
            facetsObject.setInt(TypeType.FACET_MININCLUSIVE, 0);
            facetsObject.setString(TypeType.FACET_MAXINCLUSIVE, "18446744073709551615");

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#unsignedInt", facetsObject);
            facetsObject.setInt(TypeType.FACET_MININCLUSIVE, 0);
            facetsObject.setLong(TypeType.FACET_MAXINCLUSIVE, 4294967295L);

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#unsignedShort", facetsObject);
            facetsObject.setInt(TypeType.FACET_MININCLUSIVE, 0);
            facetsObject.setInt(TypeType.FACET_MAXINCLUSIVE, 65535);

            facetsObject = dataFactory.create(facetsType);
            _buildInFacets.put(URINamePair.SCHEMA_URI + "#unsignedByte", facetsObject);
            facetsObject.setInt(TypeType.FACET_MININCLUSIVE, 0);
            facetsObject.setInt(TypeType.FACET_MAXINCLUSIVE, 255);
        }
        return _buildInFacets;
    }

    private void setBuildInFacets(DataObject pTypeObject, String pXsdType) {
        DataObject facetsObject = getBuildInFacets().get(pXsdType);
        if (facetsObject == null) {
            return;
        }
        facetsObject = _helperContext.getCopyHelper().copy(facetsObject);
        pTypeObject.setDataObject(TypeType.getFacetsProperty(), facetsObject);
    }

    private void translateSimpleContentRestriction(DataObject pTypeObject, SimpleRestrictionType pRestriction, Schema pSchema) {
        String base = pRestriction.getBase();
        if (base == null) {
            throw new IllegalArgumentException("Invalid simple content restriction " + getUriNameStringOfType(pTypeObject));
        }
        SdoType baseType = getTypeByXsdUnp(base);
        if (baseType.isDataType()) {
            //this is invalid, but we are tolerant
            addSimpleContentProperty(pTypeObject, baseType, pRestriction, base);
        } else {
            setBaseTypeProperties(pTypeObject, baseType);
        }
        translateAttrDecls(pTypeObject, pRestriction, pSchema, null);
    }

    private void translateSimpleExtensionType(DataObject pTypeObject, SimpleExtensionType pExtension, Schema pSchema) {
        String base = pExtension.getBase();
        if (base == null) {
            throw new IllegalArgumentException("Invalid simple content extension " + getUriNameStringOfType(pTypeObject));
        }
        SdoType baseType = getTypeByXsdUnp(base);
        if (baseType.isDataType()) {
            addSimpleContentProperty(pTypeObject, baseType, pExtension, base);
        } else {
            setBaseTypeProperties(pTypeObject, baseType);
        }
        translateAttrDecls(pTypeObject, pExtension, pSchema, null);
    }

    /**
     * If this type could be in a nillable element and it repersents a primitive,
     * it will be replaced by the nillable version.
     * @param pType The type to replace if necessary.
     * @param pSchemaItem The schema item where the type is used (to find the surrounding element)
     * @return The replaced type or the same type.
     */
    private SdoType checkForNillableVersion(SdoType pType, Annotated pSchemaItem) {
        if (pType instanceof JavaSimpleType && pType.getInstanceClass().isPrimitive()) {
            if (!Boolean.FALSE.equals(isNullable(pSchemaItem))) {
                pType = ((JavaSimpleType)pType).getNillableType();
            }
        }
        return pType;
    }

    private void setBaseTypeProperties(DataObject pTypeObject, SdoType<?> pBaseType) {
        pTypeObject.getList(TypeType.BASE_TYPE).add(pBaseType);
        if (pBaseType.isOpen()) {
            pTypeObject.setBoolean(TypeType.OPEN, true);
        }
        if (pBaseType.isSequenced()) {
            pTypeObject.setBoolean(TypeType.SEQUENCED, true);
        }
        if (pBaseType.isMixedContent()) {
            pTypeObject.setBoolean(TypeType.getMixedProperty(), true);
        }
        URINamePair specialBaseType = pBaseType.getSpecialBaseType();
        if (specialBaseType != null) {
            pTypeObject.setString(
                TypeType.getSpecialBaseTypeProperty(),
                specialBaseType.toStandardSdoFormat());
        }
    }

    private void addSimpleContentProperty(DataObject pTypeObject, SdoType pBaseType, Annotated pSchemaItem, String baseTypeUnp) {
        boolean nillable = !Boolean.FALSE.equals(isNullable(pSchemaItem));
        SdoType baseType = pBaseType;
        if (nillable && baseType instanceof JavaSimpleType) {
            baseType = ((JavaSimpleType)baseType).getNillableType();
        }
        DataObject propertyObj = pTypeObject.createDataObject(TypeType.PROPERTY);
        propertyObj.set(PropertyType.NAME, TypeType.VALUE);
        propertyObj.set(PropertyType.TYPE, baseType);
        propertyObj.set(PropertyType.MANY, false);
        propertyObj.set(PropertyType.NULLABLE, nillable);
        propertyObj.set(PropertyType.getSimpleContentProperty(), true);
        propertyObj.set(PropertyType.getXmlElementProperty(), true);
        propertyObj.set(PROP_CTX_SCHEMA_REFERENCE, pSchemaItem);
        setXsdType(propertyObj, baseType, baseTypeUnp);
    }

    private void setXsdType(DataObject pPropertyObject, SdoType pType, String pXsdTypeUnp) {
        URINamePair newXsdUnp = getXsdType(pType, pXsdTypeUnp);
        if (newXsdUnp != null) {
            pPropertyObject.setString(PropertyType.getXsdTypeProperty(), pXsdTypeUnp);
        }
    }

    private void setXsdType(DataObject pPropertyObject, SdoType pDataType) {
        if (pDataType instanceof ListSimpleType) {
            URINamePair xsdTypeUnp = ((ListSimpleType)pDataType).getXsdType();
            if (xsdTypeUnp != null) {
                pPropertyObject.setString(PropertyType.getXsdTypeProperty(), xsdTypeUnp.toStandardSdoFormat());
            }
        } else {
            for (SdoType<?> baseType: (List<SdoType<?>>)pDataType.getBaseTypes()) {
                setXsdType(pPropertyObject, baseType);
            }
        }
    }

    /**
     * Returns the URINamePair of the <code>pXsdTypeUnp</code> parameter if the
     * roundtrip from XSD to SDO and back would result another type and null
     * otherwhise.
     * @param pType The SDO-type.
     * @param pXsdTypeUnp The URINamePair-String of the XSD-type.
     * @return The URINamePair of pXsdTypeUnp or null.
     */
    private URINamePair getXsdType(SdoType pType, String pXsdTypeUnp) {
        URINamePair newXsdUnp = SchemaTypeFactory.getInstance().getXsdName(pType.getQName());
        final URINamePair xsdTypeUnp = URINamePair.fromStandardSdoFormat(pXsdTypeUnp);
        if ((newXsdUnp != null) && !xsdTypeUnp.equals(newXsdUnp)) {
            return xsdTypeUnp;
        }
        return null;
    }

    private SdoType getTypeByXsdUnp(String pXsdTypeUnp) {
        URINamePair xsdTypeUnp = URINamePair.fromStandardSdoFormat(pXsdTypeUnp);
        SdoType buildInType = (SdoType)((TypeHelperImpl)_helperContext.getTypeHelper()).getTypeByXmlName(xsdTypeUnp.getURI(), xsdTypeUnp.getName());
        if (buildInType instanceof MetaDataType) {
            return buildInType;
        }
        SdoType type = _finishedTypesByXsdName.get(xsdTypeUnp);
        if (type != null) {
            return type;
        }
        type = finishToplevelType(xsdTypeUnp);
        if (type != null) {
            return type;
        }

        if (buildInType != null) {
            return buildInType;
        }
        throw new IllegalArgumentException("Schema is not closed, Type " + xsdTypeUnp + " is unknown");
    }

    private DataObject getPropertyByXsdUnp(String pXsdPropertyUnp, boolean pIsElement) {
        URINamePair xsdPropertyUnp = URINamePair.fromStandardSdoFormat(pXsdPropertyUnp);
        DataObject propertyObj;
        if (pIsElement) {
            propertyObj = _finishedElementsByXsdName.get(xsdPropertyUnp);
        } else {
            propertyObj = _finishedAttributesByXsdName.get(xsdPropertyUnp);
        }
        if (propertyObj != null) {
            return propertyObj;
        }
        if (pIsElement) {
            propertyObj = finishToplevelElement(xsdPropertyUnp);
        } else {
            propertyObj = finishToplevelAttribute(xsdPropertyUnp);
        }
        if (propertyObj != null) {
            return propertyObj;
        }
        propertyObj = (DataObject)((TypeHelperImpl)_helperContext.getTypeHelper()).getOpenContentPropertyByXmlName(xsdPropertyUnp.getURI(), xsdPropertyUnp.getName(), pIsElement);
        if (propertyObj != null) {
            return propertyObj;
        }
        throw new IllegalArgumentException("Schema is not closed, global property " + xsdPropertyUnp + " is unknown");
    }

    private NamedGroup getGroupByXsdUnp(String pXsdGroupUnp) {
        URINamePair xsdGroupUnp = URINamePair.fromStandardSdoFormat(pXsdGroupUnp);
        NamedGroup group = _groupsByXsdName.get(xsdGroupUnp);
        if (group != null) {
            return group;
        }
        throw new IllegalArgumentException("Schema is not closed, group " + pXsdGroupUnp + " is unknown");
    }

    private NamedAttributeGroup getAttributeGroupByXsdUnp(String pXsdAttributeGroupUnp) {
        URINamePair xsdAttributeGroupUnp = URINamePair.fromStandardSdoFormat(pXsdAttributeGroupUnp);
        NamedAttributeGroup attributeGroup = _attributeGroupsByXsdName.get(xsdAttributeGroupUnp);
        if (attributeGroup != null) {
            return attributeGroup;
        }
        throw new IllegalArgumentException("Schema is not closed, attribute group " + pXsdAttributeGroupUnp + " is unknown");
    }

    private SdoType finishToplevelType(URINamePair pXsdTypeUnp) {
        DataObject typeObj = _hollowTypesByXsdName.remove(pXsdTypeUnp);
        if (typeObj == null) {
            return null;
        }
        _finishedTypesByXsdName.put(pXsdTypeUnp, (SdoType)typeObj);
        DataObject toplevelType = typeObj.getDataObject(PROP_CTX_SCHEMA_REFERENCE);
        if (typeObj.getBoolean(TypeType.DATA_TYPE)) {
            TopLevelSimpleType simpleType = (TopLevelSimpleType)toplevelType;
            translateSimpleType(typeObj, simpleType, (Schema)simpleType.getContainer());
        } else {
            TopLevelComplexType complexType = (TopLevelComplexType)toplevelType;
            translateComplexType(typeObj, complexType, (Schema)complexType.getContainer());
        }
        return (SdoType)typeObj;
    }

    private DataObject finishToplevelAttribute(URINamePair pXsdPropertyUnp) {
        DataObject propertyObj = _hollowAttributesByXsdName.remove(pXsdPropertyUnp);
        if (propertyObj == null) {
            return null;
        }
        _finishedAttributesByXsdName.put(pXsdPropertyUnp, propertyObj);
        DataObject toplevelProperty = propertyObj.getDataObject(PROP_CTX_SCHEMA_REFERENCE);
        TopLevelAttribute attribute = (TopLevelAttribute)toplevelProperty;
        translateAttributeProperty(propertyObj, attribute, (Schema)attribute.getContainer(), "");
        return propertyObj;
    }

    private DataObject finishToplevelElement(URINamePair pXsdPropertyUnp) {
        DataObject propertyObj = _hollowElementsByXsdName.remove(pXsdPropertyUnp);
        if (propertyObj == null) {
            return null;
        }
        _finishedElementsByXsdName.put(pXsdPropertyUnp, propertyObj);
        DataObject toplevelProperty = propertyObj.getDataObject(PROP_CTX_SCHEMA_REFERENCE);
        TopLevelElement element = (TopLevelElement)toplevelProperty;
        translateElementProperty(propertyObj, element, (Schema)element.getContainer(), "");
        List<DataObject> deferredReferencingProperties = _deferredReferences.remove(propertyObj);
        if (deferredReferencingProperties != null) {
            for (DataObject referencingProperty: deferredReferencingProperties) {
                Element referencingElement = (Element)referencingProperty.getDataObject(PROP_CTX_SCHEMA_REFERENCE);
                Schema schema = (Schema)referencingElement.getRootObject();
                translateElementProperty(referencingProperty, referencingElement, schema, referencingProperty.getContainer().getString(TypeConstants.NAME));
            }
        }
        return propertyObj;
    }

    private SdoType createLocalSimpleType(LocalSimpleType pSimpleType, Schema pSchema, String pArtificialName, String pAliasName) {
        DataObject typeObj = _helperContext.getDataFactory().create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        typeObj.setBoolean(TypeType.DATA_TYPE, true);
        setLocalTypeName(typeObj, pSimpleType, pSchema.getTargetNamespace(), pArtificialName, pAliasName);
        typeObj.set(PROP_CTX_SCHEMA_REFERENCE, pSimpleType);
        translateSimpleType(typeObj, pSimpleType, pSchema);
        return (SdoType)typeObj;
    }

    private SdoType createLocalComplexType(LocalComplexType pComplexType, Schema pSchema, String pArtificialName, String pAliasName) {
        DataObject typeObj = _helperContext.getDataFactory().create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        typeObj.setBoolean(TypeType.DATA_TYPE, false);
        setLocalTypeName(typeObj, pComplexType, pSchema.getTargetNamespace(), pArtificialName, pAliasName);
        typeObj.set(PROP_CTX_SCHEMA_REFERENCE, pComplexType);
        translateComplexType(typeObj, pComplexType, pSchema);
        return (SdoType)typeObj;
    }

    private DataObject createLocalAttribute(Attribute pAttribute, Schema pSchema, String pTypeNamePrefix) {
        DataObject propertyObj = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propertyObj.set(PROP_CTX_SCHEMA_REFERENCE, pAttribute);
        translateAttributeProperty(propertyObj, pAttribute, pSchema, pTypeNamePrefix);
        return propertyObj;
    }

    private DataObject createLocalElement(LocalElement pElement, Schema pSchema, boolean many, String pTypeNamePrefix) {
        DataObject propertyObj = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propertyObj.setBoolean(PropertyType.MANY, many);
        propertyObj.set(PROP_CTX_SCHEMA_REFERENCE, pElement);
        translateElementProperty(propertyObj, pElement, pSchema, pTypeNamePrefix);
        return propertyObj;
    }

    private void setFacet(DataObject pType, String pFacetName, List<? extends Facet> pFacetValue) {
        if (pFacetValue.isEmpty()) {
            return;
        }

        DataObject facetObj = pType.getDataObject(TypeType.getFacetsProperty());
        if (facetObj == null) {
            facetObj = pType.createDataObject(TypeType.getFacetsProperty());
        }
        Property facetProp = facetObj.getInstanceProperty(pFacetName);
        if (facetProp.isMany()) {
            List facetList = new ArrayList(pFacetValue.size());
            for (Facet facet: pFacetValue) {
                facetList.add(facet.getValue());
            }
            facetObj.setList(facetProp, facetList);
        } else {
            if (pFacetValue.size() != 1) {
                throw new IllegalArgumentException("Facet has more than one value: "
                    + pFacetName + " on Type " + getUriNameStringOfType(pType));
            }
            facetObj.set(facetProp, pFacetValue.get(0).getValue());
        }
    }

    private void translateSimpleTypeList(DataObject pTypeObject, com.sap.sdo.api.types.schema.List pList, Schema pSchema) {
        String itemTypeUnp = pList.getItemType();
        LocalSimpleType simpleType = pList.getSimpleType();
        SdoType itemType;
        URINamePair xsdType = null;
        String typeNamePrefix = pTypeObject.getString((TypeType.NAME));
        if (itemTypeUnp != null) {
            itemType = getTypeByXsdUnp(itemTypeUnp);
            xsdType = getXsdType(itemType, itemTypeUnp);
        } else if (simpleType != null) {
            String artificialName = typeNamePrefix + "*Item";
            itemType = createLocalSimpleType(simpleType, pSchema, artificialName, null);
        } else {
            throw new IllegalArgumentException("Invalid simpleType list " + getUriNameStringOfType(pTypeObject));
        }
        String uri = pSchema.getTargetNamespace();
        String artificialName = typeNamePrefix + "*List";
        ListSimpleType listSimpleType = new ListSimpleType(itemType, new URINamePair(uri, artificialName), _helperContext);
        listSimpleType.setXsdType(xsdType);
        pTypeObject.getList(TypeType.BASE_TYPE).add(listSimpleType);
    }

    private void translateSimpleTypeUnion(DataObject pTypeObject, Union pUnion, Schema pSchema) {
        SdoType commonBaseType = null;
        List<String> memberTypes = pUnion.getMemberTypes();
        if (memberTypes != null) {
            for (String memberType: pUnion.getMemberTypes()) {
                SdoType unionType = getTypeByXsdUnp(memberType);
                if (commonBaseType == null) {
                    commonBaseType = unionType;
                } else {
                    commonBaseType = getCommonType(commonBaseType, unionType);
                }
            }
        }
        int i = 0;
        for (LocalSimpleType simpleType: pUnion.getSimpleType()) {
            i++;
            String artificialName = pTypeObject.getString(TypeType.NAME) + "*Union" + i;
            SdoType unionType = createLocalSimpleType(simpleType, pSchema, artificialName, null);
            if (commonBaseType == null) {
                commonBaseType = unionType;
            } else {
                commonBaseType = getCommonType(commonBaseType, unionType);
            }
        }
        pTypeObject.getList(TypeType.BASE_TYPE).add(commonBaseType);
    }

    private void translateComplexType(DataObject pTypeObject, ComplexType pComplexType, Schema pSchema) {
        pTypeObject.setString(TypeType.URI, pSchema.getTargetNamespace());
        pTypeObject.setBoolean(TypeType.ABSTRACT, pComplexType.isAbstract());

        Boolean sequenced = (Boolean)pComplexType.get(PROP_XML_SEQUENCE);
        if (Boolean.TRUE.equals(sequenced)) {
            pTypeObject.setBoolean(TypeType.SEQUENCED, true);
        }

        if (pComplexType.isMixed()) {
            if (Boolean.FALSE.equals(sequenced)) {
                throw new IllegalArgumentException("Mixed type " + getUriNameStringOfType(pTypeObject) + " must not be sequence=\"false\"");
            }
            pTypeObject.setBoolean(TypeType.SEQUENCED, true);
            pTypeObject.setBoolean(TypeType.getMixedProperty(), true);
            pTypeObject.setBoolean(TypeType.OPEN, true); //Spec. 2.1.0 76/6
        }

        ComplexContent complexContent = pComplexType.getComplexContent();
        SimpleContent simpleContent = pComplexType.getSimpleContent();
        if (complexContent != null) {
            translateComplexContent(pTypeObject, complexContent, pSchema);
        } else if (simpleContent != null){
            translateSimpleContent(pTypeObject, simpleContent, pSchema);
        } else {
            GroupRef group = pComplexType.getGroup();
            All all = pComplexType.getAll();
            ExplicitGroup choice = pComplexType.getChoice();
            ExplicitGroup complexTypeSequence = pComplexType.getComplexTypeSequence();
            List<DataObject> properties = Collections.emptyList();
            if (group != null){
                properties = translateGroupRef(pTypeObject, group, pSchema, false);
            } else if (all != null){
                properties = translateGroup(pTypeObject, all, pSchema, false, null);
                if (properties.size() > 1) {
                    pTypeObject.setBoolean(TypeType.SEQUENCED, true);
                }
            } else if (choice != null){
                properties = translateChoice(pTypeObject, choice, pSchema, false, null);
            } else if (complexTypeSequence != null) {
                properties = translateGroup(pTypeObject, complexTypeSequence, pSchema, false, null);
            }
            pTypeObject.setList(TypeType.PROPERTY, properties);
            translateAttrDecls(pTypeObject, pComplexType, pSchema, null);
        }

        if (Boolean.FALSE.equals(sequenced)) {
            pTypeObject.setBoolean(TypeType.SEQUENCED, false);
        }
        List aliasNames = pComplexType.getList(PROP_XML_ALIAS_NAME);
        pTypeObject.getList(TypeType.ALIAS_NAME).addAll(aliasNames);
        setInstanceClass(pTypeObject, pComplexType, pSchema);
        boolean elementFormDefault = QUALIFIED.equals(pSchema.getElementFormDefault());
        pTypeObject.setBoolean(TypeType.getElementFormDefaultProperty(), elementFormDefault);
        boolean attributeFormDefault = QUALIFIED.equals(pSchema.getAttributeFormDefault());
        pTypeObject.setBoolean(TypeType.getAttributeFormDefaultProperty(), attributeFormDefault);
    }

    private void translateSimpleContent(DataObject pTypeObject, SimpleContent pSimpleContent, Schema pSchema) {
        SimpleRestrictionType restriction = pSimpleContent.getRestriction();
        SimpleExtensionType extension = pSimpleContent.getExtension();
        if (restriction != null) {
            translateSimpleContentRestriction(pTypeObject, restriction, pSchema);
        } else if (extension != null) {
            translateSimpleExtensionType(pTypeObject, extension, pSchema);
        } else {
            throw new IllegalArgumentException("Invalid simpleContent " + getUriNameStringOfType(pTypeObject));
        }
    }

    private void translateAttrDecls(DataObject pTypeObject, Annotated pSchemaItem, Schema pSchema, String pTypeNamePrefix) {
        // use sequence to keep the order of the attributes
        Sequence sequence = pSchemaItem.getSequence();
        for (int i = 0; i < sequence.size(); i++) {
            Property property = sequence.getProperty(i);
            DataObject schemaItem = (DataObject)sequence.getValue(i);
            String element = property.getName();
            if (element.equals("attribute")) {
                String typeNamePrefix = pTypeNamePrefix;
                if (typeNamePrefix == null) {
                    typeNamePrefix = pTypeObject.getString(TypeConstants.NAME);
                }
                DataObject attributeProperty = createLocalAttribute((Attribute)schemaItem, pSchema, typeNamePrefix);
                addAttributeProperty(pTypeObject, attributeProperty);
            } else if (element.equals("attributeGroup")) {
                AttributeGroupRef attributeGroupRef = (AttributeGroupRef)schemaItem;
                String ref = attributeGroupRef.getRef();
                NamedAttributeGroup attributeGroup = getAttributeGroupByXsdUnp(ref);
                Schema schema = (Schema)attributeGroup.getContainer();
                translateAttrDecls(pTypeObject, attributeGroup, schema, CHAR_GROUP + attributeGroup.getName());
            } else if (element.equals("anyAttribute")) {
                pTypeObject.setBoolean(TypeType.OPEN, true);
            }
        }
    }

    private void translateComplexContent(DataObject pTypeObject, ComplexContent pComplexContent, Schema pSchema) {
        if (pComplexContent.isMixed()) {
            pTypeObject.setBoolean(TypeType.SEQUENCED, true);
            pTypeObject.setBoolean(TypeType.getMixedProperty(), true);
            pTypeObject.setBoolean(TypeType.OPEN, true); //Spec. 2.1.0 76/6
        }
        ComplexRestrictionType complexRestriction = pComplexContent.getRestriction();
        ExtensionType extensionType = pComplexContent.getExtension();
        if (complexRestriction != null) {
            translateComplexRestriction(pTypeObject, complexRestriction, pSchema);
        } else if (extensionType != null) {
            translateExtensionType(pTypeObject, extensionType, pSchema);
        } else {
            throw new IllegalArgumentException("Invalid complexContent " + getUriNameStringOfType(pTypeObject));
        }
    }

    private void translateComplexRestriction(DataObject pTypeObject, ComplexRestrictionType pComplexRestriction, Schema pSchema) {
        String base = pComplexRestriction.getBase();
        if (base == null) {
            throw new IllegalArgumentException("Invalid restriction, base is not set on " + getUriNameStringOfType(pTypeObject));
        }
        SdoType baseType = getTypeByXsdUnp(base);
        setBaseTypeProperties(pTypeObject, baseType);
        List<DataObject> newProperties = translateParticle(pTypeObject, pComplexRestriction, pSchema, false, true, null);
        filterNewPropertiesUpdateExisting(baseType.getProperties(), newProperties, pTypeObject, false);
        pTypeObject.setList(TypeType.PROPERTY, newProperties);
        translateAttrDecls(pTypeObject, pComplexRestriction, pSchema, null);
    }

    private void translateExtensionType(DataObject pTypeObject, ExtensionType pExtensionType, Schema pSchema) {
        String base = pExtensionType.getBase();
        if (base == null) {
            throw new IllegalArgumentException("Invalid extension, base is not set on " + getUriNameStringOfType(pTypeObject));
        }
        SdoType baseType;
        if (base.equals(ANY_TYPE)) {
            baseType = (SdoType)_helperContext.getTypeHelper().getType(URINamePair.MIXEDTEXT_TYPE.getURI(), URINamePair.MIXEDTEXT_TYPE.getName());
        } else {
            baseType = getTypeByXsdUnp(base);
        }
        setBaseTypeProperties(pTypeObject, baseType);
        List<DataObject> newProperties = translateParticle(pTypeObject, pExtensionType, pSchema, false, true, null);
        filterNewPropertiesUpdateExisting(baseType.getProperties(), newProperties, pTypeObject, true);
        pTypeObject.setList(TypeType.PROPERTY, newProperties);
        translateAttrDecls(pTypeObject, pExtensionType, pSchema, null);
    }

    private List<DataObject> translateGroup(DataObject pTypeObject, Group pGroup, Schema pSchema, boolean pMany, String pTypeNamePrefix) {
        boolean many = pMany || isMany(pGroup);
        return translateParticle(pTypeObject, pGroup, pSchema, many, true, pTypeNamePrefix);
    }

    private List<DataObject> translateChoice(DataObject pTypeObject, ExplicitGroup pChoice, Schema pSchema, boolean pMany, String pTypeNamePrefix) {
        boolean many = pMany || isMany(pChoice);
        return translateParticle(pTypeObject, pChoice, pSchema, many, false, pTypeNamePrefix);
    }

    private List<DataObject> translateParticle(DataObject pTypeObject, Annotated pSchemaItem, Schema pSchema, boolean pMany, boolean pTwoSingleToMany, String pTypeNamePrefix) {
        List<DataObject> properties = new ArrayList<DataObject>();
        Sequence sequence = pSchemaItem.getSequence();
        for (int i = 0; i < sequence.size(); i++) {
            Property property = sequence.getProperty(i);
            DataObject schemaItem = (DataObject)sequence.getValue(i);
            String element = property.getName();
            if (element.equals("element")) {
                String typeNamePrefix = pTypeNamePrefix;
                if (typeNamePrefix == null) {
                    typeNamePrefix = pTypeObject.getString(TypeConstants.NAME);
                }
                DataObject elementProperty = createLocalElement((LocalElement)schemaItem, pSchema, pMany, typeNamePrefix);
                mergeElementProperties(properties, elementProperty, pTypeObject, pTwoSingleToMany);
            } else if (element.equals("group")) {
                final List<DataObject> newProperties = translateGroupRef(pTypeObject, (GroupRef)schemaItem, pSchema, pMany);
                mergeElementProperties(properties, newProperties, pTypeObject, pTwoSingleToMany);
            } else if (element.equals("all")) {
                final List<DataObject> newProperties = translateGroup(pTypeObject, (All)schemaItem, pSchema, pMany, pTypeNamePrefix);
                if (newProperties.size() > 1) {
                    pTypeObject.setBoolean(TypeType.SEQUENCED, true);
                }
                mergeElementProperties(properties, newProperties, pTypeObject, pTwoSingleToMany);
            } else if (element.equals("choice")) {
                final List<DataObject> newProperties = translateChoice(pTypeObject, (ExplicitGroup)schemaItem, pSchema, pMany, pTypeNamePrefix);
                mergeElementProperties(properties, newProperties, pTypeObject, pTwoSingleToMany);
            } else if (element.equals("sequence")) {
                final List<DataObject> newProperties = translateGroup(pTypeObject, (ExplicitGroup)schemaItem, pSchema, pMany, pTypeNamePrefix);
                mergeElementProperties(properties, newProperties, pTypeObject, pTwoSingleToMany);
            } else if (element.equals("any")) {
                pTypeObject.setBoolean(TypeType.OPEN, true);
                pTypeObject.setBoolean(TypeType.SEQUENCED, true);
            }
        }
        if (pMany && (properties.size() > 1)) {
            //for repeating particles save the order of the properties in the sequence
            pTypeObject.set(TypeType.SEQUENCED, true);
        }
        return properties;
    }

    private void addAttributeProperty(DataObject pTypeObject, DataObject pPropertyObject) {
        SdoType propType = (SdoType)pPropertyObject.getDataObject(PropertyType.TYPE);
        boolean hasType = true;
        if (propType == JavaSimpleType.OBJECT) {
            // if original attribute has no type
            Attribute attribute = (Attribute)pPropertyObject.getDataObject(PropertyType.getSchemaReferenceProperty());
            if ((attribute.getRef() == null) && (attribute.getAttributeType() == null)
                && (attribute.getSimpleType() == null) &&
                (attribute.getDataObject(PROP_XML_PROPERTY_TYPE) == null)) {
                if ("prohibited".equals(attribute.getUse())) {
                    // property is not defined on that level
                    return;
                }
                hasType = false;
            }
        }
        // Note that the following test does not work perectly, because type definition
        // could have cycles, so it is repeated at the end of the translation process
        // in addType(DataObject, Set, Set). The test is also done here because it's
        // better to erase overriding properties as early as possible.
        final String xmlName = ((SdoProperty)pPropertyObject).getXmlName();
        String uri = getUri((SdoProperty)pPropertyObject, (SdoType)pTypeObject);

        SdoProperty existingProperty = ((SdoType)pTypeObject).getPropertyFromXmlName(uri, xmlName, false);
        if (existingProperty == null) {
            pTypeObject.getList(TypeType.PROPERTY).add(pPropertyObject);
        } else {
            if (!hasType) {
                return;
            }
            SdoType existingPropType = (SdoType)existingProperty.getType();
            if (!existingPropType.isAssignableType(propType) &&
                !existingPropType.getInstanceClass().isAssignableFrom(propType.getInstanceClass())
                && (existingPropType.getInstanceClass() != List.class)) {
                throw new IllegalArgumentException("Illegal extension of attribute " +
                    existingProperty.getName() + " on type " + getUriNameStringOfType(pTypeObject)
                    + " existing Type " + existingPropType.getQName().toStandardSdoFormat()
                    + " new Type " + propType.getQName().toStandardSdoFormat());
            }
        }
    }

    private void mergeElementProperties(List<DataObject> pExistingProperties, List<DataObject> pNewProperties, DataObject pTypeObject, boolean pTwoSingleToMany) {
        filterNewPropertiesUpdateExisting(pExistingProperties, pNewProperties, pTypeObject, pTwoSingleToMany);
        pExistingProperties.addAll(pNewProperties);
    }

    private void mergeElementProperties(List<DataObject> pExistingProperties, DataObject pNewProperty, DataObject pTypeObject, boolean pTwoSingleToMany) {
        boolean isNewProperty = checkIfNewPropertyUpdatesExisting(pExistingProperties, pTypeObject, pTwoSingleToMany, pNewProperty);
        if (isNewProperty) {
            pExistingProperties.add(pNewProperty);
        }
    }

    private void filterNewPropertiesUpdateExisting(List<DataObject> pExistingProperties, List<DataObject> pNewProperties, DataObject pTypeObject, boolean pTwoSingleToMany) {
        for (Iterator<DataObject> it = pNewProperties.iterator(); it.hasNext(); ) {
            DataObject newProperty = it.next();
            boolean isNewProperty = checkIfNewPropertyUpdatesExisting(pExistingProperties, pTypeObject, pTwoSingleToMany, newProperty);
            if (!isNewProperty) {
                it.remove();
            }
        }
     }

    private boolean checkIfNewPropertyUpdatesExisting(List<DataObject> pExistingProperties, DataObject pTypeObject, boolean pTwoSingleToMany, DataObject newProperty) {
        // Note that the following test does not work perectly, because type definition
        // could have cycles, so it is repeated at the end of the translation process
        // in addType(DataObject, Set, Set). The test is also done here because it's
        // better to erase overriding properties as early as possible.
        boolean isNewProperty = true;
        for (DataObject existingProperty: pExistingProperties) {
            if (isSameProperty(existingProperty, newProperty, pTypeObject)) {
                isNewProperty = false;
                mergeElementProperty(existingProperty, newProperty, pTypeObject, pTwoSingleToMany);
                if (!pTypeObject.getBoolean(TypeType.SEQUENCED)) {
                    // If the property is not the last existing element property, the type must be sequenced
                    for (int i = pExistingProperties.size() - 1; i >= 0; i--) {
                        SdoProperty property = (SdoProperty)pExistingProperties.get(i);
                        if (property.isXmlElement()) {
                            if (property != existingProperty) {
                                pTypeObject.setBoolean(TypeType.SEQUENCED, true);
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
        return isNewProperty;
    }

    private boolean isSameProperty(DataObject pExistingProperty, DataObject pNewProperty, DataObject pTypeObject) {
        SdoProperty prop1 = (SdoProperty)pExistingProperty;
        SdoProperty prop2 = (SdoProperty)pNewProperty;
        if (!prop1.getXmlName().equals(prop2.getXmlName())) {
            return false;
        }
        if (prop1.isXmlElement() != prop2.isXmlElement()) {
            return false;
        }
        SdoType type1 = (SdoType)prop1.getContainingType();
        SdoType type2 = (SdoType)pTypeObject;
        if (type1 == null) {
            type1 = type2;
        }
        String uri1 = getUri(prop1, type1);
        String uri2 = getUri(prop2, type2);
        return uri1.equals(uri2);
    }

    public String getUri(SdoProperty pProp, SdoType containingType) {
        URINamePair ref = pProp.getRef();
        if (ref != null) {
            return ref.getURI();
        }
        Boolean formQualified = pProp.getFormQualified();
        if (formQualified == null) {
            if (pProp.isXmlElement()) {
                formQualified = containingType.getElementFormDefaultQualified();
            } else {
                formQualified = containingType.getAttributeFormDefaultQualified();
            }
        }
        if (!formQualified) {
            return "";
        }
        return containingType.getXmlUri();
    }

    private void mergeElementProperty(DataObject targetPropertyObject, DataObject pPropertyObject, DataObject pTypeObject, boolean pTwoSingleToMany) {
        SdoProperty targetProperty = (SdoProperty)targetPropertyObject;
        if (!targetProperty.isXmlElement()) {
            throw new IllegalArgumentException("Name conflict between attribute and element with name " +
                targetProperty.getName() + " on type " + getUriNameStringOfType(pTypeObject));
        }
        URINamePair targetRef = targetProperty.getRef();
        URINamePair ref = ((SdoProperty)pPropertyObject).getRef();
        String typeUri = ((SdoType)targetProperty.getType()).getXmlUri();
        if (!getUri(targetRef, typeUri).equals(getUri(ref, typeUri))) {
            throw new IllegalArgumentException("Namespace conflict at element with name " +
                targetProperty.getName() + " on type " + getUriNameStringOfType(pTypeObject));
        }

        if (pTwoSingleToMany && !targetProperty.isMany() && !pPropertyObject.getBoolean(PropertyType.MANY)) {
            checkPropertyDefined(targetProperty);
            targetPropertyObject.setBoolean(PropertyType.MANY, true);
        } else if (!targetProperty.isMany() && pPropertyObject.getBoolean(PropertyType.MANY)) {
            checkPropertyDefined(targetProperty);
            targetPropertyObject.setBoolean(PropertyType.MANY, true);
        }
        if ((targetRef != null) && targetRef.equals(ref)) {
            return;
        }
        SdoType targetType = (SdoType)targetProperty.getType();
        SdoType commonType = getCommonType(targetType, (SdoType)pPropertyObject.getDataObject(PropertyType.TYPE));
        if (targetType != commonType) {
            checkPropertyDefined(targetProperty);
            targetPropertyObject.set(PropertyType.TYPE, commonType);
        }
    }

    private String getUri(URINamePair pRef, String pTypeUri) {
        if (pRef != null) {
            return pRef.getURI();
        }
        return pTypeUri;
    }

    public SdoType getCommonType(SdoType pTargetType, SdoType pType) {
        return ((TypeHelperImpl)_helperContext.getTypeHelper()).getCommonType(pTargetType, pType);
    }

    private void checkPropertyDefined(SdoProperty pTargetProperty) {
        if (pTargetProperty.defined()) {
            throw new IllegalArgumentException("Schema forces that property " + pTargetProperty.getName()
                + "of type " + ((SdoType)pTargetProperty.getType()).getQName().toStandardSdoFormat()
                + " must be changed, but it is already finalized");
        }
    }

    private List<DataObject> translateGroupRef(DataObject pTypeObject, GroupRef pGroupRef, Schema pSchema, boolean pMany) {
        boolean many = pMany || isMany(pGroupRef);
        String ref = pGroupRef.getRef();
        NamedGroup group = getGroupByXsdUnp(ref);
        Schema schema = (Schema)group.getContainer();
        return translateGroup(pTypeObject, group, schema, many, CHAR_GROUP + group.getName());
    }

    private boolean isMany(Annotated pSchemaItem) {
        String maxOccurs = pSchemaItem.getString("maxOccurs");
        return (maxOccurs != null) && !maxOccurs.equals("1");
    }

    private void translateElementProperty(DataObject pPropertyObject, Element pElement, Schema pSchema, String pTypeNamePrefix) {
        pPropertyObject.setBoolean(PropertyType.getXmlElementProperty(), true);
        boolean containment;
        String ref = pElement.getRef();
        String elementType = pElement.getElementType();
        LocalComplexType complexType = pElement.getComplexType();
        LocalSimpleType simpleType = pElement.getSimpleType();
        String substitutionGroup = pElement.getSubstitutionGroup();
        SdoType sdoType = getSdoType(pElement);
        if (ref != null) {
            pPropertyObject.setString(PropertyType.getReferenceProperty(), ref);
            DataObject referencedProperty = getPropertyByXsdUnp(ref, true);
            String sdoName = pElement.getString(PROP_XML_NAME);
            URINamePair refUnp = URINamePair.fromStandardSdoFormat(ref);
            if (sdoName == null) {
                pPropertyObject.setString(PropertyType.NAME, refUnp.getName());
            } else {
                pPropertyObject.setString(PropertyType.NAME, sdoName);
                pPropertyObject.setString(PropertyType.getXmlNameProperty(), refUnp.getName());
            }
            pPropertyObject.setString(PropertyType.getUriProperty(), refUnp.getURI());
            final DataObject typeObj = referencedProperty.getDataObject(PropertyType.TYPE);
            if (typeObj != null) {
                //TODO a referenced element can also have its local xml name (xs:defRef) ???
                String xsdName = referencedProperty.getString(PropertyType.getXmlNameProperty());
                if (xsdName != null) {
                    // referenced property has sdo-name and xsd-name
                    pPropertyObject.setString(PropertyType.getXmlNameProperty(), xsdName);
                    if (sdoName == null) {
                        pPropertyObject.setString(PropertyType.NAME, referencedProperty.getString(PropertyType.NAME));
                    }
                }
                if (sdoType == null) {
                    pPropertyObject.setDataObject(PropertyType.TYPE, typeObj);
                } else {
                    pPropertyObject.set(PropertyType.TYPE, sdoType);
                }
                copyProperty(referencedProperty, pPropertyObject, PropertyType.CONTAINMENT);
                copyProperty(referencedProperty, pPropertyObject, PropertyType.READ_ONLY);
                copyProperty(referencedProperty, pPropertyObject, PropertyType.DEFAULT);
                copyProperty(referencedProperty, pPropertyObject, PropertyType.ALIAS_NAME);
                if (referencedProperty.isSet(PropertyType.OPPOSITE_INTERNAL)) {
                    pPropertyObject.set(PropertyType.OPPOSITE_INTERNAL, referencedProperty.get(PropertyType.OPPOSITE_INTERNAL));
                    _oppositeProperties.add(pPropertyObject);
                }
                copyProperty(referencedProperty, pPropertyObject, PropertyType.JAVA_CLASS);
                copyProperty(referencedProperty, pPropertyObject, PropertyType.NULLABLE);
            } else {
                // referenced element is not finished
                List<DataObject> referencingProperties = _deferredReferences.get(referencedProperty);
                if (referencingProperties == null) {
                    referencingProperties = new ArrayList<DataObject>();
                    _deferredReferences.put(referencedProperty, referencingProperties);
                }
                referencingProperties.add(pPropertyObject);
            }
        } else {
            String xsdName = pElement.getName();
            setPropertyName(pPropertyObject, pElement, xsdName);
            final boolean nillable = pElement.isNillable();
            SdoType type = null;
            if (sdoType != null) {
                type = sdoType;
                containment = !type.isDataType();
            } else if (elementType != null) {
                String propertyType = pElement.getString(PROP_XML_PROPERTY_TYPE);
                if (propertyType != null) {
                    type = getTypeByXsdUnp(propertyType);
                    pPropertyObject.setString(PropertyType.getXsdTypeProperty(), elementType);
                    containment = false;
                } else {
                    type = getTypeByXsdUnp(elementType);
                    if (nillable && (type instanceof JavaSimpleType)) {
                        type = ((JavaSimpleType)type).getNillableType();
                    }
                    setXsdType(pPropertyObject, type, elementType);
                    containment = !type.isDataType();
                }
            } else if (simpleType != null) {
                String propName = pPropertyObject.getString(PropertyType.NAME);
                String artificialName = pTypeNamePrefix + CHAR_ELEMENT + propName + getOppositeFormPostfix(pElement.getForm(), pSchema.getElementFormDefault());
                type = createLocalSimpleType(simpleType, pSchema, artificialName, propName);
                setXsdType(pPropertyObject, type);
                containment = false;
            } else if (complexType != null) {
                String propName = pPropertyObject.getString(PropertyType.NAME);
                String artificialName = pTypeNamePrefix + CHAR_ELEMENT + propName + getOppositeFormPostfix(pElement.getForm(), pSchema.getElementFormDefault());
                type = createLocalComplexType(complexType, pSchema, artificialName, propName);
                containment = true;
            } else {
                type = (SdoType)_helperContext.getTypeHelper().getType(URINamePair.DATAOBJECT.getURI(), URINamePair.DATAOBJECT.getName());
                containment = true;
            }
            if (substitutionGroup != null) {
                DataObject substitutedProperty = getPropertyByXsdUnp(substitutionGroup, true);
                String substitute = getUriNameStringOfProperty(pPropertyObject, pSchema);
                substitutedProperty.getList(PropertyType.getSubstitutesProperty()).add(substitute);
            }
            pPropertyObject.setBoolean(PropertyType.CONTAINMENT, containment);
            pPropertyObject.set(PropertyType.TYPE, type);
            pPropertyObject.setBoolean(PropertyType.NULLABLE, nillable);
            String defaultValue = pElement.getDefault();
            if (defaultValue != null) {
                pPropertyObject.setString(PropertyType.DEFAULT, defaultValue);
            }
            defaultValue = pElement.getFixed();
            if (defaultValue != null) {
                pPropertyObject.setString(PropertyType.DEFAULT, defaultValue);
            }
            String form = pElement.getForm();
            boolean formQualified;
            if (form != null) {
                formQualified = QUALIFIED.equals(form);
                pPropertyObject.setBoolean(PropertyType.getFormQualifiedProperty(), formQualified);
            } else {
                formQualified = QUALIFIED.equals(pSchema.getElementFormDefault());
            }
            if (formQualified) {
                pPropertyObject.setString(PropertyType.getUriProperty(), pSchema.getTargetNamespace());
            }
        }
        boolean many;
        Boolean sdoMany = (Boolean)pElement.get(PROP_XML_MANY);
        if (sdoMany != null) {
            many = sdoMany;
        } else {
            many = pPropertyObject.getBoolean(PropertyType.MANY) || isMany(pElement);
        }
        pPropertyObject.setBoolean(PropertyType.MANY, many);
        List aliasNames = pElement.getList(PROP_XML_ALIAS_NAME);
        pPropertyObject.getList(PropertyType.ALIAS_NAME).addAll(aliasNames);
        Boolean readOnly = (Boolean)pElement.get(PROP_XML_READ_ONLY);
        if (readOnly != null) {
            pPropertyObject.setBoolean(PropertyType.READ_ONLY, readOnly);
        }
        String oppositeProperty = pElement.getString(PROP_XML_OPPOSITE_PROPERTY);
        if (oppositeProperty != null) {
            pPropertyObject.setString(PropertyType.OPPOSITE_INTERNAL, oppositeProperty);
            _oppositeProperties.add(pPropertyObject);
        }
        Boolean orphanHolder = (Boolean)pElement.get(PROP_XML_ORPHAN_HOLDER);
        if (Boolean.TRUE.equals(orphanHolder)) {
            pPropertyObject.setBoolean(PropertyType.getOrphanHolderProperty(), true);
        }
        Boolean key = (Boolean)pElement.get(PROP_XML_KEY);
        if (Boolean.TRUE.equals(key)) {
            pPropertyObject.setBoolean(PropertyType.KEY, true);
        }
        Boolean embeddedKey = (Boolean)pElement.get(PROP_XML_EMBEDDED_KEY);
        if (Boolean.TRUE.equals(embeddedKey)) {
            pPropertyObject.setBoolean(PropertyType.KEY, true);
        }
        setJavaNameTagIfNecessary(pPropertyObject);
    }

    private String getOppositeFormPostfix(String pForm, String pFormDefault) {
        if (pForm != null) {
            boolean formQualified = QUALIFIED.equals(pForm);
            boolean formDefaultQualified = QUALIFIED.equals(pFormDefault);
            if (formQualified != formDefaultQualified) {
                return FORM_SUFFIX;
            }
        }
        return "";
    }

    private void translateAttributeProperty(DataObject pPropertyObject, Attribute pAttribute, Schema pSchema, String pTypeNamePrefix) {
        pPropertyObject.setBoolean(PropertyType.getXmlElementProperty(), false);
        pPropertyObject.setBoolean(PropertyType.CONTAINMENT, false);
        pPropertyObject.setBoolean(PropertyType.MANY, false);
        String attributeType = pAttribute.getAttributeType();
        LocalSimpleType simpleType = pAttribute.getSimpleType();
        String ref = pAttribute.getRef();
        SdoType sdoType = getSdoType(pAttribute);
        if (ref != null) {
            DataObject referencedProperty = getPropertyByXsdUnp(ref, false);
            String sdoName = pAttribute.getString(PROP_XML_NAME);
            if (sdoName == null) {
                pPropertyObject.setString(PropertyType.NAME, referencedProperty.getString(PropertyType.NAME));
            } else {
                pPropertyObject.setString(PropertyType.NAME, sdoName);
                pPropertyObject.setString(PropertyType.getXmlNameProperty(), referencedProperty.getString(PropertyType.NAME));
            }
            String xsdName = referencedProperty.getString(PropertyType.getXmlNameProperty());
            if (xsdName != null) {
                pPropertyObject.setString(PropertyType.getXmlNameProperty(), xsdName);
            }
            if (sdoType == null) {
                pPropertyObject.setDataObject(PropertyType.TYPE, referencedProperty.getDataObject(PropertyType.TYPE));
            } else {
                pPropertyObject.set(PropertyType.TYPE, sdoType);
            }
            copyProperty(referencedProperty, pPropertyObject, PropertyType.READ_ONLY);
            copyProperty(referencedProperty, pPropertyObject, PropertyType.DEFAULT);
            copyProperty(referencedProperty, pPropertyObject, PropertyType.ALIAS_NAME);
            copyProperty(referencedProperty, pPropertyObject, PropertyType.OPPOSITE_INTERNAL);
            copyProperty(referencedProperty, pPropertyObject, PropertyType.JAVA_CLASS);
            pPropertyObject.setString(PropertyType.getReferenceProperty(), ref);
        } else {
            String xsdName = pAttribute.getName();
            setPropertyName(pPropertyObject, pAttribute, xsdName);
            SdoType type;
            if (sdoType != null) {
                type = sdoType;
            } else if (attributeType != null) {
                String propertyType = pAttribute.getString(PROP_XML_PROPERTY_TYPE);
                if (propertyType != null) {
                    type = getTypeByXsdUnp(propertyType);
                    pPropertyObject.setString(PropertyType.getXsdTypeProperty(), attributeType);
                    String oppositeProperty = pAttribute.getString(PROP_XML_OPPOSITE_PROPERTY);
                    if (oppositeProperty != null) {
                        pPropertyObject.setString(PropertyType.OPPOSITE_INTERNAL, oppositeProperty);
                    }
                } else {
                    type = getTypeByXsdUnp(attributeType);
                    setXsdType(pPropertyObject, type, attributeType);
                }
            } else if (simpleType != null) {
                String propName = pPropertyObject.getString(PropertyType.NAME);
                String artificialName = pTypeNamePrefix + CHAR_ATTRIBUTE + propName + getOppositeFormPostfix(pAttribute.getForm(), pSchema.getAttributeFormDefault());
                type = createLocalSimpleType(simpleType, pSchema, artificialName, propName);
                setXsdType(pPropertyObject, type);
            } else {
                type = JavaSimpleType.OBJECT;
            }
            pPropertyObject.set(PropertyType.TYPE, type);
            String defaultValue = pAttribute.getDefault();
            if (defaultValue != null) {
                pPropertyObject.setString(PropertyType.DEFAULT, defaultValue);
            }
            defaultValue = pAttribute.getFixed();
            if (defaultValue != null) {
                pPropertyObject.setString(PropertyType.DEFAULT, defaultValue);
            }
            String form = pAttribute.getForm();
            boolean formQualified;
            if (form != null) {
                formQualified = QUALIFIED.equals(form);
                pPropertyObject.setBoolean(PropertyType.getFormQualifiedProperty(), formQualified);
            } else {
                formQualified = QUALIFIED.equals(pSchema.getAttributeFormDefault());
            }
            if (formQualified) {
                pPropertyObject.setString(PropertyType.getUriProperty(), pSchema.getTargetNamespace());
            }
        }
        List aliasNames = pAttribute.getList(PROP_XML_ALIAS_NAME);
        pPropertyObject.getList(PropertyType.ALIAS_NAME).addAll(aliasNames);
        Boolean readOnly = (Boolean)pAttribute.get(PROP_XML_READ_ONLY);
        if (readOnly != null) {
            pPropertyObject.setBoolean(PropertyType.READ_ONLY, readOnly);
        }
        Boolean key = (Boolean)pAttribute.get(PROP_XML_KEY);
        if (Boolean.TRUE.equals(key)) {
            pPropertyObject.setBoolean(PropertyType.KEY, true);
        } else if (key == null && attributeType != null
            && attributeType.equals(URINamePair.SCHEMA_ID.toStandardSdoFormat())) {
            pPropertyObject.setBoolean(PropertyType.KEY, true);
        }
        setJavaNameTagIfNecessary(pPropertyObject);
    }

    private void copyProperty(DataObject pSource, DataObject pTarget, int pPropertyIndex) {
        if (pSource.isSet(pPropertyIndex)) {
            pTarget.set(pPropertyIndex, pSource.get(pPropertyIndex));
        }
    }

    private SdoType getSdoType(Annotated pPropertySchemaItem) {
        String dataType = pPropertySchemaItem.getString(PROP_XML_DATA_TYPE);
        boolean string = pPropertySchemaItem.getBoolean(PROP_XML_STRING);
        SdoType sdoType = null;
        if (dataType != null) {
            sdoType = getTypeByXsdUnp(dataType);
        } else if (string) {
            sdoType = JavaSimpleType.STRING;
        }
        return sdoType;
    }

    private static Property getGlobalProperty(URINamePair pUriNamePair) {
        return CORE_TYPE_HELPER.getOpenContentProperty(pUriNamePair.getURI(), pUriNamePair.getName());
    }

    private String getUriNameStringOfType(DataObject pType) {
        return ((SdoType)pType).getQName().toStandardSdoFormat();
    }

    private String getUriNameStringOfProperty(DataObject pProperty, Schema pSchema) {
        URINamePair unp = new URINamePair(pSchema.getTargetNamespace(), pProperty.getString(PropertyType.NAME));
        return unp.toStandardSdoFormat();
    }

    private void createOppositeProperties(Set<DataObject> pUnfinishedTypes) {
        for (DataObject propertyObj : _oppositeProperties) {
            DataObject containingType = propertyObj.getContainer();
            if (containingType != null) {
                final Type propertyType = (Type)propertyObj.get(PropertyConstants.TYPE);
                if (pUnfinishedTypes.contains(propertyType)) {
                    String oppositePropName = propertyObj.getString(PropertyConstants.OPPOSITE_INTERNAL);
                    if (propertyType.getProperty(oppositePropName) == null) {
                        DataObject oppositeProperty =
                            ((DataObject)propertyType).createDataObject(TypeConstants.PROPERTY);
                        oppositeProperty.setString(PropertyConstants.NAME, oppositePropName);
                        oppositeProperty.set(PropertyConstants.TYPE, containingType);
                        oppositeProperty.setBoolean(PropertyConstants.CONTAINMENT, false);
                        oppositeProperty.setString(PropertyConstants.OPPOSITE_INTERNAL, propertyObj.getString(PropertyConstants.NAME));
                    }
                }
            }
        }
    }
}
