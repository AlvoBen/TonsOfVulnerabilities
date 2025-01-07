/*
 * Created on 2005.2.16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import java.util.Enumeration;

import com.sap.engine.core.Framework;
import com.sap.engine.core.cache.impl.CacheManagerImpl;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.cluster.message.MultipleAnswer;
import com.sap.engine.frame.cluster.message.PartialResponseException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClusterCommunicator implements MessageListener {
  
  public static final int MT_START_BENCH     = 1;
  public static final int MT_INTERRUPT_BENCH = 2;
  public static final int MT_GET_STATUS      = 3;
  public static final int MT_SPREAD_CONFIG   = 4;
  public static final int MT_REDEFINE_REGION = 5;
  
  public static final byte ANSWER_STATUS_WORKING = 101;
  public static final byte ANSWER_STATUS_IDLE    = 102;
  
  private ClusterManager cM        = null;
  private MessageContext mC        = null;
  private boolean functional       = false;
  private BenchFramework framework = null;
  
  
  private int groupId = 0;
  private int clusterId = 0;
  
  private static final MessageAnswer emptyAnswer = new MessageAnswer();
  private static final byte[] emptyArray         = new byte[0];
  private static final byte[] statusWorking      = new byte[] {ANSWER_STATUS_WORKING};
  private static final byte[] statusIdle         = new byte[] {ANSWER_STATUS_IDLE};
  
  public ClusterCommunicator(BenchFramework framework) {
    try {
      this.framework = framework;
      cM = (ClusterManager) Framework.getManager("ClusterManager");
      mC = cM.getMessageContext("_CacheBench");
      mC.registerListener(this);
      groupId = cM.getClusterMonitor().getCurrentParticipant().getGroupId();
      clusterId = cM.getClusterMonitor().getCurrentParticipant().getClusterId(); 
      if (cM != null && mC != null) {
        functional = true;
      }
    } catch (NullPointerException npe) {
      CacheManagerImpl.traceT(npe);
      functional = false;
    } catch (ListenerAlreadyRegisteredException lare) {
      CacheManagerImpl.traceT(lare);
    }
  }
  
  private MultipleAnswer sendMessage(int scope, int messageType) throws Exception {
    return sendMessage(scope, messageType, emptyArray);
  }
  
  private MultipleAnswer sendMessage(int scope, int messageType, byte[] body) throws Exception {
    MultipleAnswer result = null;
    switch (scope) {
      case 1: // local
        final MessageAnswer singleAnswer1 = receiveWait(clusterId, messageType, body, 0, body.length);
        result = new MultipleAnswer() {
          public int[] participants() {
            int[] result = new int[1];
            result[0] = clusterId;
            return result;
          }
          public Enumeration answers() {
            Enumeration result = new Enumeration() {
              boolean first = true;
              public boolean hasMoreElements() {
                if (first) {
                  first = false;
                }
                return first;
              }
              public Object nextElement() {
                return singleAnswer1;
              }
            };
            return result;
          }
          public MessageAnswer getAnswer(int cId) throws ClusterException {
            if (cId != clusterId) {
              return null;
            } else {
              return singleAnswer1;
            }
          }
        };
        break;
      case 2: // instance
        final MultipleAnswer fromOthers = mC.sendAndWaitForAnswer(groupId, (byte)-1, messageType, body, 0, body.length, (long)2000);
        final MessageAnswer singleAnswer2 = receiveWait(clusterId, messageType, body, 0, body.length);
        result = new MultipleAnswer() {
          public int[] participants() {
            int[] result = new int[1 + fromOthers.participants().length];
            result[0] = clusterId;
            for (int i = 1; i < result.length; i++) {
              result[i] = fromOthers.participants()[i-1];
            }
            return result;
          }
          public Enumeration answers() {
            Enumeration result = new Enumeration() {
              boolean first = true;
              Enumeration delegate = fromOthers.answers();
              public boolean hasMoreElements() {
                if (first) {
                  first = false;
                  return true;
                }
                return delegate.hasMoreElements();
              }
              public Object nextElement() {
                if (first) {
                  return singleAnswer2;
                } else {
                  return delegate.nextElement();
                }
              }
            };
            return result;
          }
          public MessageAnswer getAnswer(int cId) throws ClusterException {
            if (cId != clusterId) {
              return fromOthers.getAnswer(cId);
            } else {
              return singleAnswer2;
            }
          }
        };
        break;
      case 3: // cluster
        result = mC.sendAndWaitForAnswer(0, (byte)-1, messageType, body, 0, body.length, (long)2000);
        break;
      default:
    }
    return result;
  }
  
  public String startBenchmark(int scope) {
    String result = null;
    MultipleAnswer answer = null;
    try {
      answer = sendMessage(scope, MT_START_BENCH);
      int[] answers = answer.participants();
      StringBuffer sb = new StringBuffer();
      sb.append("<OK> Benchmark Started - [");
      sb.append(answers[0]);
      for (int i = 1; i < answers.length; i++) {
        sb.append(", ");
        sb.append(answers[i]);
      }
      sb.append("]");
      result = sb.toString();
    } catch (PartialResponseException pre) {
      result = "<PARTIALLY FAILED> " + pre.toString();
      CacheManagerImpl.traceT(pre);
    } catch (Exception e) {
      result = "<FAILED> " + e.toString();
      CacheManagerImpl.traceT(e);
    }
    return result;
  }

  public String spreadConfig(int scope) {
    String result = null;
    MultipleAnswer answer = null;
    try {
      answer = sendMessage(scope, MT_SPREAD_CONFIG, framework.readConfiguration());
      int[] answers = answer.participants();
      StringBuffer sb = new StringBuffer();
      sb.append("<OK> Configuration Spreaded - [");
      sb.append(answers[0]);
      for (int i = 1; i < answers.length; i++) {
        sb.append(", ");
        sb.append(answers[i]);
      }
      sb.append("]");
      result = sb.toString();
    } catch (PartialResponseException pre) {
      result = "<PARTIALLY FAILED> " + pre.toString();
      CacheManagerImpl.traceT(pre);
    } catch (Exception e) {
      result = "<FAILED> " + e.toString();
      CacheManagerImpl.traceT(e);
    }
    return result;
  }

  public String redefineRegion(int scope) {
    String result = null;
    MultipleAnswer answer = null;
    try {
      answer = sendMessage(scope, MT_REDEFINE_REGION);
      int[] answers = answer.participants();
      StringBuffer sb = new StringBuffer();
      sb.append("<OK> Region Redefined - [");
      sb.append(answers[0]);
      for (int i = 1; i < answers.length; i++) {
        sb.append(", ");
        sb.append(answers[i]);
      }
      sb.append("]");
      result = sb.toString();
    } catch (PartialResponseException pre) {
      result = "<PARTIALLY FAILED> " + pre.toString();
      CacheManagerImpl.traceT(pre);
    } catch (Exception e) {
      result = "<FAILED> " + e.toString();
      CacheManagerImpl.traceT(e);
    }
    return result;
  }

  public String getStatus(int scope) {
    String result = null;
    MultipleAnswer answer = null;
    try {
      answer = sendMessage(scope, MT_GET_STATUS);
      int[] answers = answer.participants();
      StringBuffer sb = new StringBuffer();
      sb.append("==================================================================");
      for (int i = 0; i < answers.length; i++) {
        MessageAnswer single = answer.getAnswer(answers[i]);
        sb.append("\n [");
        sb.append(answers[i]);
        sb.append("] - ");
        byte status = single.getMessage()[0];
        switch (status) {
          case ANSWER_STATUS_WORKING:
            sb.append("<WORKING>");
            break;
          case ANSWER_STATUS_IDLE:
            sb.append("<IDLE>");
            break;
          default:
        }
      }
      sb.append("\n==================================================================");
      result = sb.toString();
    } catch (PartialResponseException pre) {
      result = "<PARTIALLY FAILED> " + pre.toString();
      CacheManagerImpl.traceT(pre);
    } catch (Exception e) {
      result = "<FAILED> " + e.toString();
      CacheManagerImpl.traceT(e);
    }
    return result;
  }
  
  public String interruptBenchmark(int scope) {
    String result = null;
    MultipleAnswer answer = null;
    try {
      answer = sendMessage(scope, MT_INTERRUPT_BENCH);
      int[] answers = answer.participants();
      StringBuffer sb = new StringBuffer();
      sb.append("<OK> Benchmark Interrupted - [");
      sb.append(answers[0]);
      for (int i = 1; i < answers.length; i++) {
        sb.append(", ");
        sb.append(answers[i]);
      }
      sb.append("]");
      result = sb.toString();
    } catch (PartialResponseException pre) {
      result = "<PARTIALLY FAILED> " + pre.toString();
      CacheManagerImpl.traceT(pre);
    } catch (Exception e) {
      result = "<FAILED> " + e.toString();
      CacheManagerImpl.traceT(e);
    }
    return result;
  }

  public void receive(int clusterId, int messageType, byte[] body, int length, int offset) {
    // not used
  }
  
  public MessageAnswer receiveWait(int clusterId, int messageType, byte[] body, int length, int offset) throws Exception {
    MessageAnswer result = emptyAnswer;
    if (functional) {
      switch (messageType) {
        case MT_START_BENCH: // start benchmark
          framework.executeBenchmark();
          break;
        case MT_INTERRUPT_BENCH: // interrupt benchmark
          framework.interruptBenchmark();
          break;
        case MT_GET_STATUS: // get status
          String status = framework.getStatus();
          result = "<WORKING>".equals(status) ? new MessageAnswer(statusWorking) : new MessageAnswer(statusIdle);
          break;
        case MT_SPREAD_CONFIG: // spread config
          framework.writeConfiguration(body);
          break;
        case MT_REDEFINE_REGION: // redefine region
          framework.redefineRegion();
          break;
        default:
      }
    }
    return result;
  }
  
  public boolean isFunctional() {
    return functional;
  }

  public void kill() {
    mC.unregisterListener();
  }

}
