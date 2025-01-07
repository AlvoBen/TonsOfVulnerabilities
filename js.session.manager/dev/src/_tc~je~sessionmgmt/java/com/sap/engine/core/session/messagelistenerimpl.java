package com.sap.engine.core.session;

import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.session.*;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;

import java.util.Properties;

public class MessageListenerImpl implements MessageListener {
  public static final String MESSAGE_LISTENER_REGISTRATION_NAME = "session.domain.message_listener.id";
  
  private String serverNodeID = null;

  public MessageListenerImpl() {
    ClusterManager clusterManager = (ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER);
    this.serverNodeID = ""+clusterManager.getClusterMonitor().getCurrentParticipant().getClusterId();
  }


  public void receive(int clusterId, int messageType, byte[] body, int offset, int length) {
  }

  public MessageAnswer receiveWait(int clusterId, int messageType, byte[] body, int offset, int length) {
    MessageAnswer result = null;

    switch (messageType) {
      case ClusterMessage.TYPE_CREATE : result = createMessageReceived(body, offset, length); break;
    }

    return result;
  }


  private MessageAnswer createMessageReceived(byte[] body, int offset, int length) {
    boolean failed = true;
    try {
      ClusterMessage message = new ClusterMessage(body, offset, length);
      Properties props = message.getProps();
      if (props != null) {
        failed = processCreateMessage(props.getProperty(ClusterMessage.SERVER_PROP),
                                      props.getProperty(ClusterMessage.CONTEXT_PROP),
                                      props.getProperty(ClusterMessage.DOMAIN_PROP),
                                      props.getProperty(ClusterMessage.SESSION_ID_PROP));
      }
    } catch (Exception e) {
      failed = true;
    }
    return buildAnswer(failed);
  }

  private boolean processCreateMessage(String serverID, String contextID, String domainPath, String jsessionID) {
    boolean res = true;
    if (!serverNodeID.equals(serverID)) {
      return false;
    }
    SessionContextFactory sessionContextFactory = SessionContextFactory.getInstance();
    SessionContext context = sessionContextFactory.getSessionContext(contextID, false);
    if (context != null) {
      SessionDomain domain = context.findSessionDomain(domainPath);

      if (domain != null ) {
        try {
          SessionHolder holder = domain.getSessionHolder(jsessionID);
          holder.getSession(); // this will create a RuntimeSessionModel
          holder.releaseAccess();
          // to do
        } catch (Exception e) {
          res = false;
        }
      } else {
        res = false;
      }
    } else {
      res = false;
    }

    return res;
  }

  private static MessageAnswer buildAnswer(boolean failed) {
    MessageAnswer answer = new MessageAnswer();
    byte[] resultBody = new byte[1];
    if (failed) {
      resultBody[0] = ClusterMessage.ANSWER_OK;
    } else {
      resultBody[0] = ClusterMessage.ANSWER_FAILED;
    }
    answer.setMessage(resultBody, 0, 1);

    return answer;
  }
}
