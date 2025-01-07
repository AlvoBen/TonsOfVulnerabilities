/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.webservices.espbase.client.bindings.impl;

import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.rpc.encoding.TypeMapping;

import com.sap.engine.interfaces.webservices.esp.ConfigurationContext;
import com.sap.engine.interfaces.webservices.esp.Message;
import com.sap.engine.services.webservices.espbase.ConfigurationContextImpl;
import com.sap.engine.services.webservices.espbase.client.bindings.ClientConfigurationContext;
import com.sap.engine.services.webservices.espbase.client.bindings.ClientServiceContext;
import com.sap.engine.services.webservices.espbase.client.bindings.DynamicServiceImpl;
import com.sap.engine.services.webservices.espbase.client.bindings.ParameterObject;
import com.sap.engine.services.webservices.espbase.client.bindings.StaticConfigurationContext;
import com.sap.engine.services.webservices.espbase.client.bindings.TransportBinding;
import com.sap.engine.services.webservices.espbase.client.dynamic.content.ObjectFactory;
import com.sap.engine.services.webservices.jaxrpc.exceptions.WebserviceClientException;
import commonj.sdo.helper.HelperContext;

/**
 * 
 * @version 1.0
 * @author Chavdar Baikov, chavdar.baikov@sap.com
 */
public class ClientConfigurationContextImpl extends ConfigurationContextImpl implements ClientConfigurationContext {

  public static final String PERSISTABLE_CONTEXT = "PersistableContext";
  public static final String DYNAMIC_CONTEXT = "DynamicContext";
  public static final String STATIC_CONTEXT = "StaticContext";
  public static final String APPLICATION_LOADER = "ApplicationLoader";
  public static final String OPERATION_NAME = "OperationName";
  public static final String OPERATION_PARAMS = "OperationParams";
  public static final String TYPE_MAPPING = "TypeMapping";
  public static final String SERVICE_CONTEXT = "ServiceContext";
  public static final String MESSAGE = "Message";
  public static final String TRANSPORT_BINDING = "TransportBinding";
  public static final String OBJECT_FACTORY = "ObjectFactory";
  public static final String JAXB_CONTEXT = "JaxBContext";
  public static final String ATTACHMENT_MARSHALLER = "AttachmentMarshaller";
  public static final String ATTACHMENT_UNMARSHALLER = "AttachmentUnmarshaller";
  public static final String HELPER_CONTEXT = "HelperContext";
  public static final String DESTINATION_NAME = "DestinationName"; 
  
  public HelperContext getHelperContext() {    
    return ((HelperContext) super.getProperty(HELPER_CONTEXT));
  }

  public void setHelperContext(HelperContext helper) {
    if (helper == null) {
      super.removeProperty(HELPER_CONTEXT);
    } else {
      super.setProperty(HELPER_CONTEXT,helper);
    }
  }

  public void setAttachmentMarshaller(AttachmentMarshaller attachmentMarshaller) {
    super.setProperty(ATTACHMENT_MARSHALLER, attachmentMarshaller);
  }

  public AttachmentMarshaller getAttachmentMarshaller() {
    return((AttachmentMarshaller)(super.getProperty(ATTACHMENT_MARSHALLER)));
  }
  
  public void setAttachmentUnmarshaller(AttachmentUnmarshaller attachmentUnmarshaller) {
    super.setProperty(ATTACHMENT_UNMARSHALLER, attachmentUnmarshaller);
  }
  
  public AttachmentUnmarshaller getAttachmentUnmarshaller() {
    return((AttachmentUnmarshaller)(super.getProperty(ATTACHMENT_UNMARSHALLER)));
  }
  
  public JAXBContext getJAXBContext() {
    return (JAXBContext) super.getProperty(JAXB_CONTEXT);
  }

  public void setJAXBContext(JAXBContext context) {
    super.setProperty(JAXB_CONTEXT,context);
  }

  /**
   * Returns client service context reference. 
   * @return
   */
  public ClientServiceContext getServiceContext() {
    return (ClientServiceContext) super.getProperty(SERVICE_CONTEXT);
  }

  /**
   * Default constructor.
   */
  public ClientConfigurationContextImpl() {
    super("ClientContext",null,ConfigurationContextImpl.NORMAL_MODE);    
    setDynamicContext(new ConfigurationContextImpl("DynamicConfig",null,ConfigurationContextImpl.NORMAL_MODE));
    setPersistableContext(new ConfigurationContextImpl("PersistableConfig",null,ConfigurationContextImpl.PRERSISTENT_MODE));
    setStaticContext(new StaticConfigurationContextImpl());
  }
  
  /**
   * Returns Transport binding used by this client.
   * @return
   */
  public TransportBinding getTransportBinding() {
     return (TransportBinding) super.getProperty(TRANSPORT_BINDING);
  }
  
  /**
   * Sets transpot binding used by this WSClient.
   * @param tbinding
   */
  public void setTransportBinding(TransportBinding tbinding) {
    super.setProperty(TRANSPORT_BINDING, tbinding);
  }
  
  /**
   * Returns the application class loader.
   * @return
   */
  public ClassLoader getClientAppClassLoader() {
    return (ClassLoader) super.getProperty(APPLICATION_LOADER);
  }
  
  /**
   * Returns current message that client sends/recieves.
   * @return
   */
  public Message getMessage() {
    return (Message) super.getProperty(MESSAGE);
  }
  
  /**
   * Sets client application classLoader.
   * @param classLoader
   */
  public void setClientApppClassLoader(ClassLoader classLoader) {
    super.setProperty(APPLICATION_LOADER,classLoader);
  }

  /**
   * Returns the dynamic configuration context.
   * @return
   */
  public ConfigurationContext getDynamicContext() {
    return (ConfigurationContext) super.getProperty(DYNAMIC_CONTEXT);
  }
  
  /**
   * Sets dynamic context.
   * @param context
   */
  public void setDynamicContext(ConfigurationContext context) {
    super.setProperty(DYNAMIC_CONTEXT,context);  
  }

  /**
   * Returns current operation name.
   * @return
   */
  public String getOperationName() {
    return (String) super.getProperty(OPERATION_NAME);
  }
  
  /**
   * Sets invoked operation name.
   * @param operationName
   */
  public void setOperationName(String operationName) {
    super.setProperty(OPERATION_NAME,operationName);
  }
  
  /**
   * Sets invoked operation and parameters.
   * @param operationName
   * @param params
   */
  public void setInvokedOperation(String operationName, ParameterObject[] params) {
    setOperationName(operationName);
    setOperationParams(params);
  }
  
  /**
   * Clears the invoked operation.
   *
   */
  public void clearInvokedOperation() {
    super.removeProperty(OPERATION_NAME);
    super.removeProperty(OPERATION_PARAMS);
  }

  /**
   * Returns current operation parameters.
   * @return
   */
  public ParameterObject[] getOperationParameters() {    
    return (ParameterObject[]) super.getProperty(OPERATION_PARAMS);
  }
  
  /**
   * Sets operation parameters.
   * @param parameters
   */
  public void setOperationParams(ParameterObject[] parameters) {
    super.setProperty(OPERATION_PARAMS,parameters);
  }

  /**
   * Returns persistable subcontext.
   * @return
   */
  public ConfigurationContext getPersistableContext() {
    return (ConfigurationContext) super.getProperty(PERSISTABLE_CONTEXT);
  }
  
  /**
   * Sets persistable context.
   * @param context
   */
  public void setPersistableContext(ConfigurationContext context) {
    super.setProperty(PERSISTABLE_CONTEXT,context);
  }

  /**
   * Returns Static configuration context.
   * @return
   */
  public StaticConfigurationContext getStaticContext() {
    return (StaticConfigurationContext) super.getProperty(STATIC_CONTEXT);
  }
  
  /**
   * Sets static configuration context.
   * @param context
   */
  public void setStaticContext(StaticConfigurationContext context) {
    super.setProperty(STATIC_CONTEXT,context);
  }

  /**
   * Returns reference to the type mapping framework.
   * @return
   */
  public TypeMapping getTypeMaping() {
    return (TypeMapping) super.getProperty(TYPE_MAPPING);
  }
  
  /**
   * Sets type mapping framework.
   * @param typeMapping
   */
  public void setTypeMapping(TypeMapping typeMapping) {
    super.setProperty(TYPE_MAPPING,typeMapping);
  }  

  /* (non-Javadoc)
   * @see com.sap.engine.services.webservices.espbase.client.bindings.ClientConfigurationContext#getObjectFactory()
   */
  public ObjectFactory getObjectFactory() {
    return (ObjectFactory) super.getProperty(OBJECT_FACTORY);
  }
  /* (non-Javadoc)
   * @see com.sap.engine.services.webservices.espbase.client.bindings.ClientConfigurationContext#setObjectFactory(com.sap.engine.services.webservices.espbase.client.dynamic.content.ObjectFactory)
   */
  public void setObjectFactory(ObjectFactory factory) {
    super.setProperty(OBJECT_FACTORY,factory);
  }
  
  public ClientConfigurationContextImpl copy() {
    ClientConfigurationContextImpl copy = new ClientConfigurationContextImpl();
    copyProperty(ATTACHMENT_MARSHALLER, copy);
    copyProperty(ATTACHMENT_UNMARSHALLER, copy);
    copyProperty(APPLICATION_LOADER, copy);
    copyProperty(JAXB_CONTEXT, copy);
    copyProperty(OBJECT_FACTORY, copy);
    copyProperty(TYPE_MAPPING, copy);
    copy.setTransportBinding(new SOAPTransportBinding());
    copyProperty(SERVICE_CONTEXT, copy);
    copyProperty(HELPER_CONTEXT, copy);
    copyConfigurationContext(getPersistableContext(), copy.getPersistableContext());
    copyConfigurationContext(getDynamicContext(), copy.getDynamicContext());
    copyStaticContext((StaticConfigurationContextImpl)(copy.getStaticContext()));
    return(copy);
  }
  
  private void copyStaticContext(StaticConfigurationContextImpl dstStaticContext) {
    StaticConfigurationContextImpl srcStaticContext = (StaticConfigurationContextImpl)getStaticContext();
    dstStaticContext.setDTConfig(srcStaticContext.getDTConfig());
    dstStaticContext.setRTConfig(srcStaticContext.getRTConfig());
    dstStaticContext.setInterfaceData(srcStaticContext.getInterfaceData());    
  }
  
  private void copyProperty(String key, ClientConfigurationContextImpl copy) {
    Object value = getProperty(key);
    if(value != null) {
      copy.setProperty(key, value);
    }
  }
  
  private void copyConfigurationContext(ConfigurationContext srcConfigCtx, ConfigurationContext destinationConfigCtx) {
    Iterator iterator = srcConfigCtx.properties();
    while (iterator.hasNext()) {
      String key = (String)(iterator.next());
      Object value = srcConfigCtx.getProperty(key);
      destinationConfigCtx.setProperty(key, value);      
    }
  }
}
