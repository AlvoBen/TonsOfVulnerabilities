/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.Validator;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.SapType;
import com.sap.sdo.api.types.ctx.HelperContexts;
import com.sap.sdo.api.types.ctx.Namespace;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.ValidationHelper;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.util.WeakValueHashMap;
import com.sap.sdo.impl.xml.XMLDocumentImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * @author D042774
 *
 */
public class SapHelperProviderImpl extends SapHelperProvider {

    public static HelperContextImpl _coreContext;

    /** This is to make the weak reference to a hard reference for the default HelperContext */
    public final Map<ClassLoader, HelperContextImpl> DEFAULT_CONTEXTS =
        new WeakHashMap<ClassLoader, HelperContextImpl>();
    private final Map<ClassLoader, Map<String, HelperContext>> INSTANCES =
        new WeakHashMap<ClassLoader, Map<String, HelperContext>>();

    
    public SapHelperProviderImpl() {
        super();
    }
    
    /**
     * Creates a new HelperContext.
     * The id of the HelperContext will be a UUID}.
     * @return helper context.
     * @see #getId()
     * @see UUID#randomUUID()
     */
    @Override
    protected HelperContext newContext() {
        return context(createId());
    }

    /**
     * Get or create instance of HelperContext by the id.
     * @param pId The id of the HelperContext.
     * @return helper context.
     */
    @Override
    protected HelperContext context(String pId) {
        return getContext(pId, getClassLoader(), null);
    }

    /**
     * Removes the HelperContext identified by its id. 
     * @param pId The id of the HelperContext to remove.
     * @return true if the HelperContext was found.
     */
    @Override
    protected boolean removeCxt(String pId, ClassLoader pClassLoader) {
        Map<String, HelperContext> idToContext = getIdToContextMap(pClassLoader);
        boolean result = idToContext.remove(pId) != null;
        if (result && pId.equals(DEFAULT_CONTEXT_ID)) {
            DEFAULT_CONTEXTS.remove(pClassLoader);
        }
        return result;
    }

    /**
     * Creates a new HelperContext.
     * The id of the HelperContext will be a UUID}.
     * @return helper context.
     * @see #getId()
     * @see UUID#randomUUID()
     */
    @Override
    protected HelperContext newContext(ClassLoader pClassLoader) {
        return context(createId(), pClassLoader);
    }

    /**
     * Get or create instance of HelperContext by the id.
     * @param pId The id of the HelperContext.
     * @return helper context.
     */
    @Override
    protected HelperContext context(String pId, ClassLoader pClassLoader) {
        return getContext(pId, pClassLoader, null);
    }

    /**
     * Get or create instance of HelperContext by the id.
     * @param pId The id of the HelperContext.
     * @return helper context.
     */
    @Override
    protected HelperContext context(String pId, HelperContext pParent) {
        return getContext(pId, getClassLoaderByParent(pParent), pParent);
    }

    /**
     * Removes the HelperContext identified by its id. 
     * @param pId The id of the HelperContext to remove.
     * @return true if the HelperContext was found.
     */
    @Override
    protected synchronized boolean removeCxt(String pId) {
        return removeCxt(pId, getClassLoader());
    }

    @Override
    protected String contextId(HelperContext pHelperContext) {
        return ((HelperContextImpl)pHelperContext).getId();
    }

    @Override
    protected boolean removeCxt(HelperContext pHelperContext) {
        final ClassLoader classLoader = getClassLoader();
        String id = contextId(pHelperContext);
        Map<String, HelperContext> idToContext = getIdToContextMap(classLoader);
        HelperContext helperContext = idToContext.get(id);
        if ((helperContext == null) || (helperContext == pHelperContext)) {
            idToContext.remove(id);
            if (id.equals(DEFAULT_CONTEXT_ID)) {
                DEFAULT_CONTEXTS.remove(classLoader);
            }
        }
        return true;
    }

    @Override
    protected Validator validator() {
        return ValidationHelper.getInstance();
    }

    @Override
    protected HelperContext defaultContext() {
        return defaultContext(getClassLoader());
    }

    @Override
	protected HelperContext defaultContext(ClassLoader cl) {
        HelperContext context = DEFAULT_CONTEXTS.get(cl);
        if (context == null) {
            context = getContext(DEFAULT_CONTEXT_ID, cl, null);
        }
        return context;
	}
    
    @Override
    protected void serializeCxt(List<HelperContext> pHelperContexts, Writer pWriter) {
        HelperContext renderingContext = HelperProvider.getDefaultContext();
        XMLDocument xmlDocument = getContextDataObjects(pHelperContexts);
        try {
            renderingContext.getXMLHelper().save(xmlDocument, pWriter, getOptions());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        detacheTypesAndProps((com.sap.sdo.api.types.ctx.HelperContext)xmlDocument.getRootObject());
    }

    @Override
    protected void serializeCxt(List<HelperContext> pHelperContexts, OutputStream pOutputStream) {
        HelperContext renderingContext = HelperProvider.getDefaultContext();
        XMLDocument xmlDocument = getContextDataObjects(pHelperContexts);
        try {
            renderingContext.getXMLHelper().save(xmlDocument, pOutputStream, getOptions());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        detacheTypesAndProps((com.sap.sdo.api.types.ctx.HelperContext)xmlDocument.getRootObject());
    }
    
    private Map getOptions() {
        Map options = new HashMap();
        Map<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("sdox", URINamePair.DATATYPE_XML_URI);
        prefixes.put("sdoj", URINamePair.DATATYPE_JAVA_URI);
        prefixes.put("ctx", URINamePair.CTX_URI);
        options.put(SapXmlHelper.OPTION_KEY_PREFIX_MAP, prefixes);
        return options;
    }
    
    private XMLDocument getContextDataObjects(List<HelperContext> pHelperContexts) {
        DataFactory dataFactory = getCoreContext().getDataFactory();
        HelperContexts helperContexts = (HelperContexts)dataFactory.create(HelperContexts.class);
        helperContexts.setId(CORE_CONTEXT_ID);
        Map<HelperContext, com.sap.sdo.api.types.ctx.HelperContext> ctxMap = 
            new HashMap<HelperContext, com.sap.sdo.api.types.ctx.HelperContext>();
        ctxMap.put(getCoreContext(), helperContexts);
        for (HelperContext context: pHelperContexts) {
            com.sap.sdo.api.types.ctx.HelperContext contextDO = getContextDO(context, ctxMap);
            TypeHelperImpl typeHelper = (TypeHelperImpl)context.getTypeHelper();
            typeHelper.fillHelperContextDO(contextDO);
        }
        XMLDocumentImpl xmlDocument = new XMLDocumentImpl((DataObject)helperContexts, URINamePair.CTX_URI, "helperContexts");
        
        return xmlDocument;
    }
    
    private com.sap.sdo.api.types.ctx.HelperContext getContextDO(HelperContext pContext, Map<HelperContext, com.sap.sdo.api.types.ctx.HelperContext> pCtxMap) {
        DataFactory dataFactory = getCoreContext().getDataFactory();
        com.sap.sdo.api.types.ctx.HelperContext contextDO = pCtxMap.get(pContext);
        if (contextDO == null) {
            final HelperContextImpl context = (HelperContextImpl)pContext;
            contextDO = (com.sap.sdo.api.types.ctx.HelperContext)
                dataFactory.create(com.sap.sdo.api.types.ctx.HelperContext.class);
            pCtxMap.put(context, contextDO);
            contextDO.setId(context.getId());
            HelperContext parentContext = context.getParent();
            com.sap.sdo.api.types.ctx.HelperContext parentContextDO = getContextDO(parentContext, pCtxMap);
            parentContextDO.getHelperContext().add(contextDO);
        }
        return contextDO; 
    }
    
    @Override
    protected List<HelperContext> deserializeCxt(Reader pReader) {
        XMLHelper xmlHelper = getCoreContext().getXMLHelper();
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_XML_TYPES, SapXmlHelper.OPTION_VALUE_FALSE);
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
        try {
            XMLDocument xmlDocument = xmlHelper.load(pReader, null, options);
            return createCtx(xmlDocument);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @Override
    @Deprecated
    public void deserializeCxtInto(Reader pReader, HelperContext ctx) {
        XMLHelper xmlHelper = getCoreContext().getXMLHelper();
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_XML_TYPES, SapXmlHelper.OPTION_VALUE_FALSE);
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
        try {
            XMLDocument xmlDocument = xmlHelper.load(pReader, null, options);
            HelperContexts helperContexts = (HelperContexts)xmlDocument.getRootObject();
            List<com.sap.sdo.api.types.ctx.HelperContext> contextList = helperContexts.getHelperContext();
            if (contextList.size() != 1) {
                throw new IllegalArgumentException("only a single HelperContext is supported, found: " + contextList.size());
            }
            com.sap.sdo.api.types.ctx.HelperContext pContextDO = contextList.get(0);
            if (!pContextDO.getHelperContext().isEmpty()) {
                throw new IllegalArgumentException("HelperContext hierarchy is not supported");                
            }
            
            List<Namespace> namespaceDOs = pContextDO.getNamespace();
            if (!namespaceDOs.isEmpty()) {
                defineNamespaces(namespaceDOs, ctx);
            }
            detacheTypesAndProps(helperContexts);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    protected List<HelperContext> deserializeCxt(InputStream pInputStream) {
        XMLHelper xmlHelper = getCoreContext().getXMLHelper();
        try {
            XMLDocument xmlDocument = xmlHelper.load(pInputStream, null, null);
            return createCtx(xmlDocument);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private List<HelperContext> createCtx(XMLDocument xmlDocument) {
        HelperContexts helperContexts = (HelperContexts)xmlDocument.getRootObject();
        List<HelperContext> createdContexts = new ArrayList<HelperContext>();
        createCtx(helperContexts, null, createdContexts);
        detacheTypesAndProps(helperContexts);
        return createdContexts;
    }
    
    private void createCtx(com.sap.sdo.api.types.ctx.HelperContext pContextDO, HelperContext pParentContext, List<HelperContext> pCreatedContexts) {
        HelperContext helperContext = context(pContextDO.getId(), pParentContext);
        List<Namespace> namespaceDOs = pContextDO.getNamespace();
        List<com.sap.sdo.api.types.ctx.HelperContext> childContexts = pContextDO.getHelperContext();

        if (!namespaceDOs.isEmpty() || childContexts.isEmpty()) {
            pCreatedContexts.add(helperContext);
            defineNamespaces(namespaceDOs, helperContext);
        }

        for (com.sap.sdo.api.types.ctx.HelperContext childContext:  childContexts) {
            createCtx(childContext, helperContext, pCreatedContexts);
        }
    }
    
    private void defineNamespaces(List<Namespace> pNamespaceDOs, HelperContext pHelperContext) {
        Map<URINamePair, Type> types = new HashMap<URINamePair, Type>();       
        List<Property> properties = new ArrayList<Property>();
        
        for (Namespace namespaceDO: pNamespaceDOs) {
            for (Property property: namespaceDO.getProperty()) {
                properties.add(property);
            }
            for (Type type: namespaceDO.getNamespaceType()) {
                types.put(((SapType)type).getQName(), type);
                properties.addAll(type.getDeclaredProperties());
            }
        }
        TypeHelper typeHelper = pHelperContext.getTypeHelper();
        for (Type type: types.values()) {
            List<Type>baseTypes = type.getBaseTypes();
            if (!baseTypes.isEmpty()) {
                List<Type> realBaseTypes = new ArrayList<Type>(baseTypes.size());
                for (Type baseType: baseTypes) {
                    Type realType = getRealType(types, typeHelper, baseType);
                    if (realType != type) {
                        realBaseTypes.add(realType);
                    }
                }
                ((DataObject)type).set(TypeType.BASE_TYPE, realBaseTypes);
            }
        }
        
        for (Property property: properties) {
            Type type = property.getType();
            Type realType = getRealType(types, typeHelper, type);
            if (realType != type) {
                ((DataObject)property).set(PropertyType.TYPE, realType);
            }
        }
        typeHelper.define(new ArrayList(types.values()));
        for (Namespace namespaceDO: pNamespaceDOs) {
            for (Property property: namespaceDO.getProperty()) {
                typeHelper.defineOpenContentProperty(namespaceDO.getUri(), (DataObject)property);
            }
        }        
    }
    
    private void detacheTypesAndProps(com.sap.sdo.api.types.ctx.HelperContext pHelperContext) {
        List<Namespace> namespaces = pHelperContext.getNamespace();
        for (Namespace namespace: namespaces) {
            DataObject namespaceDo = (DataObject)namespace;
            List<Property> properties = new ArrayList<Property>(namespaceDo.getInstanceProperties());
            for (int i = 0; i < properties.size(); i++) {
                Property property = properties.get(i);
                if (property.isContainment()) {
                    namespaceDo.unset(property);
                }
            }
        }
        List<com.sap.sdo.api.types.ctx.HelperContext> helperContexts = pHelperContext.getHelperContext();
        for (com.sap.sdo.api.types.ctx.HelperContext helperContext: helperContexts) {
            detacheTypesAndProps(helperContext);
        }
    }

    private Type getRealType(Map<URINamePair, Type> types, TypeHelper typeHelper, Type type) {
        Type realType = typeHelper.getType(type.getURI(), type.getName());
        if (realType == null) {
            realType = types.get(((SapType)type).getQName());
        }
        if (realType == null) {
            throw new IllegalArgumentException("Type " + 
                ((SapType)type).getQName().toStandardSdoFormat() + " is unknown");
        }
        return realType;
    }
    
    //old stuff
    
    public static HelperContextImpl getCoreContext() {
        if (_coreContext == null) {
            _coreContext = new HelperContextImpl(CORE_CONTEXT_ID, HelperContextImpl.class.getClassLoader(), null);
            
            _coreContext.init();
        }
        return _coreContext;
    }
    

    /**
     * Creates a new HelperContext.
     * The id of the HelperContext will be a UUID}.
     * @return helper context.
     * @see #getId()
     * @see UUID#randomUUID()
     */
    @Override
    protected HelperContext newContext(HelperContext pParent) {
        return context(createId(), pParent);
    }

    private String createId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get or create instance of HelperContext by the id.
     * @param pId The id of the HelperContext.
     * @return helper context.
     */
    private synchronized HelperContext getContext(String pId, ClassLoader pClassLoader, HelperContext pParent) {
        if (pId == null) {
            throw new NullPointerException("pId is null");
        }
        if (pClassLoader == null) {
            throw new NullPointerException("pClassLoader is null");
        }
        HelperContextImpl parent;
        if (pParent == null) {
            parent = getCoreContext();
        } else {
            parent = (HelperContextImpl)pParent;
        }
        Map<String, HelperContext> idToContext = getIdToContextMap(pClassLoader);
        HelperContext helperContext = idToContext.get(pId);
        if (helperContext != null) {
            return helperContext;
        }
        HelperContextImpl helperContextImpl = new HelperContextImpl(pId, pClassLoader, parent);
        if (pId.equals(DEFAULT_CONTEXT_ID)) {
            DEFAULT_CONTEXTS.put(pClassLoader, helperContextImpl);
        }
        idToContext.put(pId, helperContextImpl);
        helperContextImpl.init();
        return helperContextImpl;
    }

    /**
     * Determines the id-to-context-map that is assigned to the current
     * ClassLoader.
     * @return The Map from id to HelperContext.
     */
    private Map<String, HelperContext> getIdToContextMap(ClassLoader loader) {
        Map<String, HelperContext> idToContext = INSTANCES.get(loader);
        if (idToContext == null) {
            idToContext = new WeakValueHashMap<String, HelperContext>();
            INSTANCES.put(loader, idToContext);
            idToContext.put(CORE_CONTEXT_ID, getCoreContext());
        }
        return idToContext;
    }
    
    public static ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }
    
    private ClassLoader getClassLoaderByParent(HelperContext pHelperContext) {
        if ((pHelperContext == null) || (pHelperContext == getCoreContext())) {
            return getClassLoader();
        }
        return ((HelperContextImpl)pHelperContext).getClassLoader();
    }

}
