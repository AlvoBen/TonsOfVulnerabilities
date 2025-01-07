/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.webservices.espbase.server.additions.attach;

import java.util.Set;

import com.sap.engine.interfaces.webservices.esp.ConfigurationContext;
import com.sap.engine.interfaces.webservices.esp.Message;
import com.sap.engine.interfaces.webservices.esp.ProtocolExtensions;
import com.sap.engine.interfaces.webservices.esp.ProviderProtocol;
import com.sap.engine.interfaces.webservices.runtime.MessageException;
import com.sap.engine.interfaces.webservices.runtime.ProtocolException;
import com.sap.engine.interfaces.webservices.runtime.RuntimeProcessException;
import com.sap.engine.services.webservices.espbase.attachment.Attachment;
import com.sap.engine.services.webservices.espbase.attachment.impl.AttachmentContainer;
import com.sap.engine.services.webservices.espbase.client.api.AttachmentHandler;
import com.sap.engine.services.webservices.espbase.messaging.MIMEMessage;
import com.sap.engine.services.webservices.espbase.server.ProviderContextHelper;
import com.sap.engine.services.webservices.espbase.server.additions.attach.exc.AttachmentProtocolResouceAccessor;
import com.sap.engine.services.webservices.espbase.server.runtime.ApplicationWebServiceContextImpl;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizationException;
import com.sap.tc.logging.Location;

/**
 * Javadoc goes here...
 * 
 * Copyright (c) 2006, SAP-AG
 * @author Dimitar Angelov
 * @version 1.0, 2006-3-10
 */
public class ProviderAttachmentProtocol implements AttachmentHandler, ProtocolExtensions, ProviderProtocol {
  
  public static final String PROTOCOL_NAME = "ProviderAttachmentHanlderProtocol";
  
  private static final Location LOC = Location.getLocation(ProviderAttachmentProtocol.class);
  private static final String BASE_SUBCONTEXT_NAME  =  ProviderAttachmentProtocol.PROTOCOL_NAME + "Context";
  private static final String OUTBOUND_PROCESSING_STARTED_PROP  =  "outbound-processing-started";
  private static final String OUTBOUND_ATTACHMENT_CONTAINER_PROP  =  "outbound-attachmentcontainer";
  private static final String USE_MESSAGE_FOR_OUTBOUND_ATTACHMENTS  =  "use-message-for-outbound-attachments";
  
  public static final ProviderAttachmentProtocol SINGLETON = new ProviderAttachmentProtocol();
  
  private ProviderAttachmentProtocol() {
  }
  
  public String getProtocolName() {
    return PROTOCOL_NAME;
  }

  public Attachment createAttachment() {
    //call this to ensure that the message in processing is MIMEMessage 
    return AttachmentContainer.createAttachment();
  }

  public Attachment getInboundAttachment(String cid) {
    ProviderContextHelper pCtx = getProviderContextHelperFromThread();
    //it is not allowed inbound attachments to be queried when outbound processing has started
    if (isOutboundProcessingStarted(pCtx)) {
      String locString;
      try {
        locString = LocalizableTextFormatter.formatString(AttachmentProtocolResouceAccessor.getResourceAccessor(),
                                                          AttachmentProtocolResouceAccessor.OUTBOUND_PROCESSING_HAS_STARTED);
      } catch (LocalizationException lE) {
        throw new RuntimeException(lE);
      }
      throw new IllegalStateException(locString);
    }
    
    MIMEMessage msg = getCheckedMessage(pCtx); 
    return msg.getAttachmentContainer().getAttachment(cid);
  }
  
  public Set getInboundAttachments() {
    ProviderContextHelper pCtx = getProviderContextHelperFromThread();
    //it is not allowed inbound attachments to be queried when outbound processing has started
    if (isOutboundProcessingStarted(pCtx)) {
      String locString;
      try {
        locString = LocalizableTextFormatter.formatString(AttachmentProtocolResouceAccessor.getResourceAccessor(),
                                                          AttachmentProtocolResouceAccessor.OUTBOUND_PROCESSING_HAS_STARTED);
      } catch (LocalizationException lE) {
        throw new RuntimeException(lE);
      }
      throw new IllegalStateException(locString);
    }
    
    MIMEMessage mimeMsg = getCheckedMessage(pCtx); 
    return mimeMsg.getAttachmentContainer().getAttachments();
  }
    
  public void addOutboundAttachment(Attachment a) {
    getOutboundAttachmentContainer().addAttachment(a);
  }
  
  public AttachmentContainer getOutboundAttachmentContainer() {
    ProviderContextHelper pCtx = getProviderContextHelperFromThread();
    MIMEMessage msg = getCheckedMessage(pCtx);
    return(isUseMsgForOutboundAttachments(pCtx) ? msg.getAttachmentContainer() : getAttachmentContainerFromCtx(pCtx));
  }

  public Attachment getOutboundAttachment(String cid) {
    return getOutboundAttachmentContainer().getAttachment(cid);
  }

  public Set getOutboundAttachments() {
    return getOutboundAttachmentContainer().getAttachments();
  }

  public int handleRequest(ConfigurationContext context) throws ProtocolException, MessageException {
    return ProviderProtocol.CONTINUE;
  }

  public int handleResponse(ConfigurationContext context) throws ProtocolException {
    ProviderContextHelper pCtx = getProviderContextHelperFromThread();
    MIMEMessage msg = null; 
    try {
      Message m = pCtx.getMessage();
      if (! (m instanceof MIMEMessage)) { //works only on MIME messages
        return ProviderProtocol.CONTINUE;
      }
      msg = (MIMEMessage) pCtx.getMessage();
    } catch (RuntimeProcessException e) {
      throw new ProtocolException(e); 
    }
    
    AttachmentContainer ac = getAttachmentContainerFromCtx(pCtx);
    //move all attachments from ac into the message
    ac.putAll(msg.getAttachmentContainer());
    //mark that message should be used for stroring outbound attachments
    setUseMsgForOutboundAttachments(pCtx);
    //release context instance
    removeAttachmentContainerFromCtx(pCtx);
    return ProviderProtocol.CONTINUE;
  }

  public int handleFault(ConfigurationContext context) throws ProtocolException {
    return ProviderProtocol.CONTINUE;
  }

  public int afterDeserialization(ConfigurationContext ctx) throws ProtocolException, MessageException {
    return ProviderProtocol.CONTINUE;
  }

  public void beforeSerialization(ConfigurationContext ctx) throws ProtocolException {
    //mark that outbound processing has started
    setOutboundProcessingStarted((ProviderContextHelper) ctx);
  }

  public void afterHibernation(ConfigurationContext ctx) throws ProtocolException {
  }

  public void beforeHibernation(ConfigurationContext ctx) throws ProtocolException {
  }

  public void finishHibernation(ConfigurationContext ctx) throws ProtocolException {
  }

  public void finishMessageDeserialization(ConfigurationContext ctx) throws ProtocolException {
  }
  /**
   * Returns persistable subcontext denoted for usage by this protocol.
   * @param ctx
   */
  private ConfigurationContext getProcotolPersistentSubContext(ProviderContextHelper ctx) {
    return ctx.getPersistableContext().createSubContext(BASE_SUBCONTEXT_NAME);
  }
  
  private ConfigurationContext getProcotolDynamicSubContext(ProviderContextHelper ctx) {
    return ctx.getDynamicContext().createSubContext(BASE_SUBCONTEXT_NAME);
  }

  private ProviderContextHelper getProviderContextHelperFromThread() {
    return (ProviderContextHelper) ApplicationWebServiceContextImpl.getSingleton().getConfigurationContext();
  }

  private void setOutboundProcessingStarted(ProviderContextHelper pCtx) {
    ConfigurationContext ctx = getProcotolDynamicSubContext(pCtx);
    ctx.setProperty(OUTBOUND_PROCESSING_STARTED_PROP, "true");
  }
    
  private boolean isOutboundProcessingStarted(ProviderContextHelper pCtx) {
    return getProcotolDynamicSubContext(pCtx).getProperty(OUTBOUND_PROCESSING_STARTED_PROP) != null;
  }
  
  private AttachmentContainer getAttachmentContainerFromCtx(ProviderContextHelper pCtx) {
    ConfigurationContext ctx = getProcotolDynamicSubContext(pCtx);
    Object o = ctx.getProperty(OUTBOUND_ATTACHMENT_CONTAINER_PROP);
    if (o != null) {
      return (AttachmentContainer) o;
    }
    AttachmentContainer ac = new AttachmentContainer();
    ctx.setProperty(OUTBOUND_ATTACHMENT_CONTAINER_PROP, ac);
    return ac;
  }
  
  private void removeAttachmentContainerFromCtx(ProviderContextHelper pCtx) {
    ConfigurationContext ctx = getProcotolDynamicSubContext(pCtx);
    ctx.removeProperty(OUTBOUND_ATTACHMENT_CONTAINER_PROP);
  }
  
  /**
   * If context message is not MIMEMessage an exception is thrown, otherwise the MIMEMessage instance is returned.
   */
  private MIMEMessage getCheckedMessage(ProviderContextHelper pCtx) {
    Message msg = null; 
    try {
      msg = pCtx.getMessage();
    } catch (RuntimeProcessException e) {
      throw new RuntimeException(e); 
    }
    if ((msg == null) || (! (msg instanceof MIMEMessage))) {
      String locString;
      try {
        locString = LocalizableTextFormatter.formatString(AttachmentProtocolResouceAccessor.getResourceAccessor(),
                                                          AttachmentProtocolResouceAccessor.UNKNOWN_MESSAGE_TYPE, new Object[]{msg});
      } catch (LocalizationException lE) {
        throw new RuntimeException(lE);
      }
      throw new IllegalStateException(locString);
    }
    return (MIMEMessage) msg;
  }
  
  private void setUseMsgForOutboundAttachments(ProviderContextHelper pCtx) {
    ConfigurationContext ctx = getProcotolDynamicSubContext(pCtx);
    ctx.setProperty(USE_MESSAGE_FOR_OUTBOUND_ATTACHMENTS, "true");
  }
  
  private boolean isUseMsgForOutboundAttachments(ProviderContextHelper pCtx) {
    ConfigurationContext ctx = getProcotolDynamicSubContext(pCtx);
    return ctx.getProperty(USE_MESSAGE_FOR_OUTBOUND_ATTACHMENTS) != null;
  }
}
