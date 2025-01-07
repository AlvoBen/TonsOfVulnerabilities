package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.ContextObjectNameIterator;
import com.sap.engine.frame.core.thread.Transferable;
import com.sap.engine.frame.core.thread.TransferableExt;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.P4IOException;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.rmi_p4.StubBaseInfo;
import com.sap.engine.services.rmi_p4.all.ConnectionProfile;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.Connection;

import java.rmi.Remote;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class P4SessionProcessor {

  public static P4SessionProcessor thisProcessor = null;
  public P4ObjectBroker broker;
  public ApplicationServiceContext serviceContext;
  private P4MessageProcessor messageProcessor;
  public ClusterOrganizer organizer;
  protected int currentServerId;
  public byte[] currentId = new byte[4];
  private boolean isReady = false;
  private AtomicLong requestCount = new AtomicLong(0);
  private AtomicLong errorRequestCount = new AtomicLong(0);

  protected static P4SessionProcessor getSessionProcessor() {
    try {
      while (thisProcessor == null) {
        Thread.sleep(2000);
      }
    } catch (InterruptedException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4SessionProcessor.getSessionProcessor()", P4Logger.exceptionTrace(e));
      }
      return thisProcessor;
    }
    return thisProcessor;
  }

  public P4SessionProcessor(ApplicationServiceContext _baseContext) {
    thisProcessor = this;
    this.serviceContext = _baseContext;
    this.currentServerId = (_baseContext.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId());
    Convert.writeIntToByteArr(currentId, 0, currentServerId);
    this.organizer = new ClusterOrganizer(_baseContext.getClusterContext().getMessageContext(), _baseContext);
    isReady = true;
    broker = P4ObjectBroker.init();
    try {
      broker.setInitialObject("cocr", (new com.sap.engine.services.rmi_p4.ContextObjectClassReceiverImpl()));
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4SessionProcessor(ApplicationServiceContext)", P4Logger.exceptionTrace(ex));
      }
    }
    synchronized (this) {
      this.notifyAll();
    }

  }

  public int getServerId() {
    return currentServerId;
  }

  public synchronized void isReady() {
    while (!isReady) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        //$JL-EXC$
        return;
      }
    }
  }

  public ConnectionProfile[] getConnectionProfiles() {
    return organizer.getAllProfiles();
  }


  public long getRequestCount() {
    return this.requestCount.get();
  }

  public long getErrorRequestCount() {
    return this.errorRequestCount.get();
  }




  public void setInitialObject(String initName, Remote initObject) {
    try {
      P4RemoteObject toSet = broker.loadObject(initObject);
      broker.setInitialObject(initName, toSet);
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4SessionProcessor.setInitialObject(String, Remote)", P4Logger.exceptionTrace(ex));
      }
    }
  }

  public Connection getConnection(int serverID) throws P4IOException {
    return messageProcessor.getConnection(serverID);
  }

  public ApplicationServiceContext getServiceContext() {
    return serviceContext;
  }

  protected void stop() {
    organizer.unregister();
    thisProcessor = null;
    broker.close();
  }


  public ClusterOrganizer getClusterOrganizer() {
    return organizer;
  }

  public void setMessageProcessor(P4MessageProcessor messageProcessor) {
    this.messageProcessor = messageProcessor;
  }


  public P4MessageProcessor getMessageProcessor() {
    return messageProcessor;
  }

  public void incRequestCount() {
      requestCount.incrementAndGet();
  }

  public void incErrorRequestCount() {
      errorRequestCount.incrementAndGet();
  }

}

