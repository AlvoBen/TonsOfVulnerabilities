package com.sap.engine.session.monitoring.impl;

import com.sap.engine.core.Names;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.engine.session.monitoring.MonitoredObject;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.usr.UserContext;
import com.sap.engine.session.usr.ClientSession;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.exec.ExecutionDetails;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.data.share.HashtableImpl;
import com.sap.engine.session.data.share.QueueImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.*;

/**
 * @author: Mladen Droshev
 * Date: 2007-4-19
 */
public class UserContextMonitoringNode extends AbstractMonitoringNode {

  static Location loc = Location.getLocation(UserContextMonitoringNode.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public UserContextMonitoringNode(String path, ClientContextImpl context) {
    super(path, context);
    this.ID = (context).getClientId();
  }

  public Map<String, MonitoringNode> getChildNodes() {
    if (loc.bePath()) {
      loc.entering("getChildNodes()");
    }

    HashMap<String, MonitoringNode> result = new HashMap<String, MonitoringNode>();
    Object ref = this.getReferent();
    if (ref != null) {
      Set _sessions = ((ClientContextImpl) ref).getAppSessions();
      if (_sessions.size() > 0) {
        for (Object _session : _sessions) {
          if (_session instanceof MonitoredObject) {
            result.put(_session.toString(), ((MonitoredObject) _session).getMonitoredObject());
          } else {

            if (loc.bePath()) {
              loc.pathT("Object<" + _session + "> get from UserContext is not MonitoredObject");
            }

            try {
              result.put(_session.toString(), new AppSessionMonitoringNode(((ClientSession) _session).getSessionId(), _session));
            } catch (Exception e) {
              loc.warningT("Exception : " + e.getMessage());
              if (loc.beDebug()) {
                loc.traceThrowableT(Severity.WARNING, "", e);
              }
            }
          }
        }
      }
    } else {
      if (loc.bePath()) {
        loc.pathT("The reference to the object is reset.");
      }
    }

    if (loc.bePath()) {
      loc.exiting("getChildNodes()");
    }
    return result;
  }

  public MonitoringNode getChildNode(String id) {
    if (loc.bePath()) {
      loc.entering("getChildNode(<" + id + ">)");
    }

    Object ref = this.getReferent();
    if (ref != null) {
      Set _sessions = ((ClientContextImpl) ref).getAppSessions();
      if (_sessions.size() > 0) {
        for (Object _session : _sessions) {
          if (_session instanceof MonitoredObject) {
            return ((MonitoredObject) _session).getMonitoredObject();
          }

          if (loc.bePath()) {
            loc.pathT("Object<" + _session + "> get from UserContext is not MonitoredObject");
          }

          try {
            if (_session != null && ((ClientSession) _session).getClientId().equalsIgnoreCase(id)) {
              return new AppSessionMonitoringNode(((ClientSession) _session).getSessionId(), _session);
            }
          } catch (Exception e) {
            loc.warningT("Exception : " + e.getMessage());
            if (loc.beDebug()) {
              loc.traceThrowableT(Severity.WARNING, "", e);
            }
          }
        }
      }
    }

    if (loc.bePath()) {
      loc.pathT("The reference to the object is reset.");
    }
    return null;
  }

  public WeakHashMap<Object, HashtableImpl> getSharedHashtables() {
    Object ref = this.getReferent();
    if (ref != null) {
      return ((UserContext) ref).getHashtables();
    }
    return null;
  }

  public WeakHashMap<Object, QueueImpl> getSharedQueues() {
    Object ref = this.getReferent();
    if (ref != null) {
      return ((UserContext) ref).getQueues();
    }
    return null;
  }

  public static int getHashtablesSize() {
    if (loc.bePath()) {
      loc.entering("getHashtablesSize()");
    }
    int size = 0;
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();

    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object o : userCotnexts) {
        WeakHashMap<Object, HashtableImpl> _h = ((UserContext) o).getHashtables();
        Set<Object> keys = _h.keySet();
        for (Object key : keys) {
          HashtableImpl userHash = _h.get(key);
          if (userHash != null) {
            size += userHash.size();
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("getHashtablesSize()");
    }
    return size;
  }

  public static int getQueuesSize() {
    if (loc.bePath()) {
      loc.entering("getQueuesSize()");
    }
    int size = 0;
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();

    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, QueueImpl> _h = ((UserContext) userContext).getQueues();
        Set keys = _h.keySet();
        for (Object key : keys) {
          QueueImpl userQueue = _h.get(key);
          if (userQueue != null) {
            size += userQueue.size();
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("getQueuesSize()");
    }
    return size;
  }

  public static String[] listLoaders() {
    if (loc.bePath()) {
      loc.entering("listLoaders()");
    }
    HashSet<String> loaders = new HashSet<String>();
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {

        WeakHashMap<Object, QueueImpl> _q = ((UserContext) userContext).getQueues();
        Set<Object> keys = _q.keySet();
        if (keys != null) {
          for (Object key : keys) {
            QueueImpl userQueue = _q.get(key);
            loaders.add(userQueue.getLoaderName());
          }
        }

        WeakHashMap<Object, HashtableImpl> _h = ((UserContext) userContext).getHashtables();
        keys = _h.keySet();
        if (keys != null) {
          for (Object key : keys) {
            HashtableImpl userHash = _h.get(key);
            loaders.add(userHash.getLoaderName());
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("listLoaders()");
    }

    return loaders.toArray(new String[0]);
  }

  public static String[] listHashtableLoaders() {
    if (loc.bePath()) {
      loc.entering("listHashtableLoaders()");
    }
    HashSet<String> loaders = new HashSet<String>();
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, HashtableImpl> _h = ((UserContext) userContext).getHashtables();
        Set keys = _h.keySet();
        if (keys != null) {
          for (Object key : keys) {
            HashtableImpl userHash = _h.get(key);
            loaders.add(userHash.getLoaderName());
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("listHashtableLoaders()");
    }
    return loaders.toArray(new String[0]);
  }

  public static String[] listQueueLoaders() {
    if (loc.bePath()) {
      loc.entering("listQueueLoaders()");
    }
    HashSet<String> loaders = new HashSet<String>();
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, QueueImpl> _q = ((UserContext) userContext).getQueues();
        Set keys = _q.keySet();
        if (keys != null) {
          for (Object key : keys) {
            QueueImpl userQueue = _q.get(key);
            loaders.add(userQueue.getLoaderName());
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("listQueueLoaders()");
    }
    return loaders.toArray(new String[0]);
  }

  public static String[] listHashtableClasses() {
    if (loc.bePath()) {
      loc.entering("listHashtableClasses()");
    }
    HashSet<String> _clazes = new HashSet<String>();
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, HashtableImpl> _h = ((UserContext) userContext).getHashtables();
        Set keys = _h.keySet();
        if (keys != null) {
          for (Object key : keys) {
            HashtableImpl userHash = _h.get(key);
            String[] loaders = listHashtableLoaders();
            if (loaders != null && loaders.length > 0) {
              for (String loaderName : loaders) {
                if (userHash != null && userHash.getLoaderName().indexOf(loaderName) != -1) {
                  _clazes.add(userHash.getClassName());
                }
              }
            }
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("listHashtableClasses()");
    }
    return _clazes.toArray(new String[0]);

  }

  public static String[] listQueueClasses() {
    if (loc.bePath()) {
      loc.entering("listQueueClasses()");
    }
    HashSet<String> _clazes = new HashSet<String>();
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, QueueImpl> _q = ((UserContext) userContext).getQueues();
        Set keys = _q.keySet();
        if (keys != null) {
          for (Object key : keys) {
            QueueImpl userQueue = _q.get(key);
            String[] loaders = listHashtableLoaders();
            if (loaders != null && loaders.length > 0) {
              for (String loaderName : loaders) {
                if (userQueue != null && userQueue.getLoaderName().indexOf(loaderName) != -1) {
                  _clazes.add(userQueue.getClassName());
                }
              }
            }
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("listQueueClasses()");
    }
    return _clazes.toArray(new String[0]);

  }

  public static String[] listClassesPerLoader(String loaderName) {
    if (loc.bePath()) {
      loc.entering("listClassesPerLoader(<" + loaderName + ">)");
    }
    String[] hashClasses = listHashClassesPerLoader(loaderName);
    String[] queueClasses = listQueueClassesPerLoader(loaderName);
    HashSet<String> result = new HashSet<String>();
    if (hashClasses != null && hashClasses.length > 0) {
      result.addAll(Arrays.asList(hashClasses));
    }
    if (queueClasses != null && queueClasses.length > 0) {
      result.addAll(Arrays.asList(queueClasses));
    }
    if (loc.bePath()) {
      loc.exiting("listClassesPerLoader()");
    }
    return result.toArray(new String[0]);
  }

  public static String[] listHashClassesPerLoader(String loaderName) {
    if (loc.bePath()) {
      loc.entering("listHashClassesPerLoader(<" + loaderName + ">)");
    }
    HashSet<String> classes = new HashSet<String>();
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, HashtableImpl> _h = ((UserContext) userContext).getHashtables();
        Set keys = _h.keySet();
        if (keys != null) {
          for (Object key : keys) {
            HashtableImpl userHash = _h.get(key);
            if (userHash != null && userHash.getLoaderName().indexOf(loaderName) != -1) {
              classes.add(userHash.getClassName());
            }
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("listHashClassesPerLoader()");
    }
    return classes.toArray(new String[0]);

  }

  public static String[] listQueueClassesPerLoader(String loaderName) {
    if (loc.bePath()) {
      loc.entering("listQueueClassesPerLoader(<" + loaderName + ">)");
    }
    HashSet<String> classes = new HashSet<String>();
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, QueueImpl> _q = ((UserContext) userContext).getQueues();
        Set keys = _q.keySet();
        if (keys != null) {
          for (Object key : keys) {
            QueueImpl userQueue = _q.get(key);
            if (userQueue != null && userQueue.getLoaderName().indexOf(loaderName) != -1) {
              classes.add(userQueue.getClassName());
            }
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("listQueueClassesPerLoader()");
    }
    return classes.toArray(new String[0]);

  }

  public static int getSize(String className, String loaderName) {
    if (loc.bePath()) {
      loc.entering("getSize(<" + className + ">,<" + loaderName + ">)");
    }
    int size = 0;
    size += getQueueSize(className, loaderName);
    size += getHashSize(className, loaderName);
    if (loc.bePath()) {
      loc.exiting("getSize()");
    }
    return size;
  }

  public static int getHashSize(String className, String loaderName) {
    int size = 0;
    if (loc.bePath()) {
      loc.entering("getHashSize(<" + className + ">,<" + loaderName + ">)");
    }

    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, QueueImpl> _q = ((UserContext) userContext).getQueues();
        Set keys = _q.keySet();
        if (keys != null) {
          for (Object key : keys) {
            QueueImpl userQueue = _q.get(key);
            if (userQueue != null && userQueue.getLoaderName().indexOf(loaderName) != -1 && userQueue.getClassName().indexOf(className) != -1) {
              size += userQueue.size();
            }
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("getHashSize()");
    }
    return size;
  }

  public static int getQueueSize(String className, String loaderName) {
    if (loc.bePath()) {
      loc.entering("getQueueSize(<" + className + ">,<" + loaderName + ">)");
    }
    int size = 0;
    Collection<ClientContextImpl> userCotnexts = ClientContextImpl.clientContexts();
    if (userCotnexts != null && userCotnexts.size() > 0) {
      for (Object userContext : userCotnexts) {
        WeakHashMap<Object, HashtableImpl> _h = ((UserContext) userContext).getHashtables();
        Set<Object> keys = _h.keySet();
        if (keys != null) {
          for (Object key : keys) {
            HashtableImpl userHash = _h.get(key);
            if (userHash != null && userHash.getLoaderName().indexOf(loaderName) != -1 && userHash.getClassName().indexOf(className) != -1) {
              size += userHash.size();
            }
          }
        }
      }
    }
    if (loc.bePath()) {
      loc.exiting("getQueueSize()");
    }
    return size;
  }

  public String getPersistency(){
    Object obj = this.getReferent();
    if(obj != null){
      return ((ClientContextImpl)obj).getPersistentModel();
    }
    return null;
  }

  public String getUser(){
    Object obj = this.getReferent();
    if(obj != null){
      return ((ClientContextImpl)obj).getUser();
    }
    return null;

  }

  public int getAppSessionSize(){
    Object obj = this.getReferent();
    if(obj != null){
      return ((ClientContextImpl)obj).appSessionsSize();
    }
    return 0;
    
  }

  public int getSecuritySessionSize(){
    Object obj = this.getReferent();
    if(obj != null){
      return ((ClientContextImpl)obj).runtimeSessionsSize();
    }
    return 0;

  }
  
  public String getIP() {	 
  	Object obj = this.getReferent();
  	if(obj != null){
  		return ((ClientContextImpl)obj).getIP();
  	}
  	return null;
  }

  public Date getCreationTime() {
  	Object obj = this.getReferent();
  	if(obj != null){
  	  return new Date(((ClientContextImpl)obj).getCreationTime());
  	}
  	return null;
  }  
  
  public Date getLastAcessed() {
  	Object obj = this.getReferent();
  	if(obj != null){
  		return new Date(((ClientContextImpl)obj).getLastAcessed());
  	}
  	return null;
  }
  
  public String getRootContextID() {
    Object obj = this.getReferent();
    if (obj != null) {
      return (String) ((ClientContextImpl) obj).getRootContextID();
    }
    return null;
  }
}
