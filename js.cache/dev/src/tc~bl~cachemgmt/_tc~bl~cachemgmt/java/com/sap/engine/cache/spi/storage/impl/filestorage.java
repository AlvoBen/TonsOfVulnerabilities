package com.sap.engine.cache.spi.storage.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import com.sap.engine.cache.util.Serializator;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;

/**
 * @author Petev, Petio, i024139
 */
public class FileStorage extends HashMapStorage {
  
  private LinkedList queue = null;
  private Thread workerThread = null;
  private Worker worker = null;
  
  public void start() {
    super.start();
    queue = new LinkedList();
    worker = new Worker();
    workerThread = new Thread(worker);
    workerThread.setDaemon(true);
    workerThread.start();
  }

  public Pluggable getInstance() throws PluginException {
    return new FileStorage();
  }

  public void flush() throws CacheException {
    if (hashmap.size() == 0) {
      return;
    }
    super.flush();
    synchronized (queue) {
      if (hashmap.size() == 0) {
        return;
      }
      Iterator keys = null;
      keys = keySet().iterator();
      while (keys.hasNext()) {
        String key = (String) keys.next();
        String fileName = null;
        Node node = null;
        Object _object = hashmap.get(key);
        Object _attributes = attributes.get(key);
        Object _systemAttributes = systemAttributes.get(key);
        if (_object != null) {
          fileName = key + ".obj";
          node = new Node(key, fileName, _object);
          node.write();
        }
        if (_attributes != null) {
          fileName = key + ".a";
          node = new Node(key, fileName, _attributes);
          node.write();
        }
        if (_systemAttributes != null) {
          fileName = key + ".sa";
          node = new Node(key, fileName, _systemAttributes);
          node.write();
        }
      }
    }
  }

  public String getName() {
    return "FileStorage";
  }


  private class Node {
    
    private String fileName;
    private String key;
    private Object object;
    
    public Node(String key, String fileName, Object object) {
      this.fileName = fileName;
      this.object = object;
      this.key = key;
    }
    
    private byte[] getBytes() throws IOException {
      return Serializator.toByteArray(object);
    }
    
    public void write() {
      fileName = regionName + "\\" + fileName;
      int backSlashPos = fileName.lastIndexOf("\\"); 
      if (backSlashPos != -1) {
        File dir = new File(fileName.substring(0, backSlashPos));
        dir.mkdirs();
      }
      boolean exception = false;
      File file = new File(fileName);
      try {
				FileOutputStream fos = new FileOutputStream(file);
        try {
					fos.write(getBytes());
          fos.flush();
          fos.close();
				} catch (IOException e1) {
          LogUtil.logT(e1);
          exception = true;

				}
			} catch (FileNotFoundException e) {
        exception = true;
        LogUtil.logT(e);
			}
      if (!exception) {
        remove(key);
      }
    }

  }
  
  public Map getAttributes(String key, boolean copy) {
    Object superObject = super.getAttributes(key, copy);
    if (superObject == null) {
      superObject = read(key + ".a");
    }
    return (Map) superObject;
  }

  public Map getSystemAttributes(String key, boolean copy) {
    Object superObject = super.getSystemAttributes(key, copy);
    if (superObject == null) {
      superObject = read(key + ".sa");
    }
    return (Map) superObject;
  }

  public Object get(String key, boolean copy) {
    Object superObject = super.get(key, copy);
    if (superObject == null) {
      superObject = read(key + ".obj");
    }
		return superObject;
  }

  private Object read(String fileName) {
    fileName = regionName + "\\" + fileName;
    File file = new File(fileName);
    Object result = null;
    try {
      FileInputStream fis = new FileInputStream(file);
      try {
        int fileSize = (int) file.length();
        byte[] bytes = new byte[fileSize];
        fis.read(bytes, 0, fileSize);
        fis.close();
        result = Serializator.toObject(bytes);
      } catch (IOException e1) {
        LogUtil.logT(e1);
      } catch (ClassNotFoundException e) {
        LogUtil.logT(e);
			}
    } catch (FileNotFoundException e) {
      LogUtil.logT(e);
    }
    return result;
  }

  
  public void stop() {
    super.stop();
    worker.stop();
    queue = null;
    worker = null;
    workerThread = null;
  }
  
  private class Worker implements Runnable {
    
    public boolean stop = false;

    public void stop() {
      while (!queue.isEmpty()) {
        synchronized (this) {
          try {
            wait();
          } catch (InterruptedException e) {
            LogUtil.logTInfo(e);
            // ok, nothing we can do
          }
          stop = true;
          synchronized (queue) {
            queue.notify();
          }
        }
        return;
      }
      stop = true;
      synchronized (queue) {
        queue.notify();
      }
    }
    
    public void run() {
      while (!stop) {
        synchronized (queue) {
          if (queue.isEmpty()) {
            synchronized (this) {
              notify(); // notify stop
            }
            try {
              queue.wait();
              if (stop) {
                break;
              }
            } catch (InterruptedException e) {
              LogUtil.logTInfo(e);
            }
          }
          Node node = null;
          synchronized (queue) {
            boolean noNode = false;
            try {
              node = (Node)queue.removeFirst();
            } catch (NoSuchElementException nsee) {
              LogUtil.logTInfo(nsee);
              noNode = true;
            }
            if (!noNode) {
              node.write();
            } 
          }
        }
      }
    }
    
  }

}
