/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MultipleAnswer;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.session.ClusterEnv;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.SessionException;

import java.util.Properties;
import java.util.Enumeration;
import java.io.IOException;

/**
 * This class is used for creatng session on a specified server node
 *
 * @author Nikolai Neichev
 */
public class ClusterEnvImpl extends ClusterEnv {

  /**
   * Sends cluster message to the specified server node in order to create a session with the specified session Id
   * @param domain the domain from witch the session creation is requested
   * @param serverNodeID the specified server ID
   * @param sessionID the specified session ID
   * @throws com.sap.engine.session.SessionException is
   */
  public void createSessionOnNode(SessionDomain domain, String serverNodeID, String sessionID) throws SessionException {
    Properties props = new Properties();
    props.put(ClusterMessage.CONTEXT_PROP, domain.getEnclosingContext().getName());
    props.put(ClusterMessage.DOMAIN_PROP, domain.path());
    props.put(ClusterMessage.SERVER_PROP, serverNodeID);
    props.put(ClusterMessage.SESSION_ID_PROP, sessionID);
    ClusterMessage message = new ClusterMessage(props);
    byte[] bytes;
    try {
      bytes = message.getMessageBytes();
    } catch (IOException e) {
      throw new SessionException(e);
    }
    ClusterManager clusterManager = (ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER);
    MessageContext messageContext = clusterManager.getMessageContext(MessageListenerImpl.MESSAGE_LISTENER_REGISTRATION_NAME);
    boolean okFlag = false;
    try {
      MultipleAnswer answers = messageContext.sendAndWaitForAnswer(0, ClusterElement.SERVER, ClusterMessage.TYPE_CREATE, bytes, 0, bytes.length, 0);
      Enumeration enumerat = answers.answers();
      while (enumerat.hasMoreElements()) {
        MessageAnswer answer = (MessageAnswer) enumerat.nextElement();
        if (ClusterMessage.checkIfAnswerOK(answer)) {
          okFlag = true;
          break;
        }
      }
    } catch (ClusterException e) {
      throw new SessionException(e);
    }
    if (!okFlag) {
      throw new SessionException("No valid MessageAnswer has been received !");
    }
  }
}
