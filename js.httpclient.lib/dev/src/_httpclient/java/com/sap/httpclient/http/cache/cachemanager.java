package com.sap.httpclient.http.cache;

import com.sap.httpclient.http.cache.persistent.PersistentCacheObject;
import com.sap.httpclient.http.StatusLine;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.Parameters;
import com.sap.httpclient.exception.HttpException;
import com.sap.tc.logging.Location;

import java.util.*;
import java.io.File;
import java.io.IOException;

/**
 * @author: Mladen Droshev
 */


public class CacheManager {

  private static final Location LOG = Location.getLocation(CacheManager.class);

  public static CacheManager cManager = null;
  private static boolean stopedCache = true;

  public static final int DEFAULT_CACHE_MAX_ENTRIES = 4096;

  /* currently these values are unused */
  public static final int DEFAULT_DISK_SIZE = 100; // in MB
  public static final int DEFAULT_MEMORY_SIZE = 100; // in MB
  private int max_disk_size = DEFAULT_DISK_SIZE;
  private int max_memory_size = DEFAULT_MEMORY_SIZE;

  public static final long DEFAULT_MAX_AGE = 24 * 60 * 60;   // 1 day

  public static final String DEFAULT_CACHE_PATH = "tempCacheDir";
  public static final String TEMP_CACHE_SUF = "sapCache";
  private String cache_path_dir = DEFAULT_CACHE_PATH;

  private int hits = 0;

  HttpClientParameters params = null;
  protected Map<ItemID, CacheObject> cache = null;

  private static Set<String> fileCache = null;

  int maxEntries;

  public CacheManager(HttpClientParameters params) {
    this.maxEntries = DEFAULT_CACHE_MAX_ENTRIES;
    this.params = params;
    if (this.params != null) {
      this.max_disk_size = this.params.getInt(Parameters.CACHE_MAX_DISK_SIZE, DEFAULT_DISK_SIZE);
      this.max_memory_size = this.params.getInt(Parameters.CACHE_MAX_MEMORY_SIZE, DEFAULT_MEMORY_SIZE);
      if (this.params.getParameter(Parameters.CACHE_PATH_DIR) != null) {
        this.cache_path_dir = this.params.getParameter(Parameters.CACHE_PATH_DIR) + File.separator + TEMP_CACHE_SUF;
      }
    }
    cache = new HashMap<ItemID, CacheObject>(5);

    File f = new File(cache_path_dir);
    if (!f.exists()) {
      f.mkdirs();
    } else {
      /* load files' name */
      String[] fileNames = listCacheDir();
      if (fileNames != null && fileNames.length > 0) {
        for (String fileName : fileNames) {
          getFileCache().add(fileName);
        }
      }
    }

    if (LOG.beDebug()) {
      LOG.debugT("CacheManager is initialiazed. [MAX ENTRIES=" + maxEntries + "]");
    }
  }

  private static Set<String> getFileCache() {
    if (fileCache == null) {
      fileCache = new HashSet<String>();
    }
    return fileCache;
  }

  public static void createInstance(HttpClientParameters params) {
    if (cManager == null) {
      cManager = new CacheManager(params);
      stopedCache = false;
    }
  }

  public static CacheManager getInstance() throws HttpException {
    if (stopedCache || cManager == null) {
      throw new HttpException("The cache is stopped. Try to start it again.");
    }
    return cManager;
  }

  public static boolean isRunning() {
    return !stopedCache;
  }

  public synchronized void addEntry(CacheObject cEntry) {
    if (LOG.beDebug()) {
      LOG.debugT("Add Entry:" + cEntry);
    }
    if (cache.size() < this.maxEntries) {
      cache.put(cEntry.getID(), cEntry);
      this.hits += cEntry.searchCounter;
    } else {
      if (LOG.beDebug()) {
        LOG.debugT("reduce cache's objects in file system");
      }
      reduceCache();
      cache.put(cEntry.getID(), cEntry);
      this.hits += cEntry.searchCounter;
    }
  }

  public void reduceCache() {
    int _size = this.cache.size();
    int frequent = this.hits / _size;
    int index = 0;

    if (LOG.beDebug()) {
      LOG.debugT("Current Cache Size[" + _size + "] Frequent[" + frequent + "]");
    }

    LinkedList<ItemID> idForRemove = new LinkedList<ItemID>();

    Set<ItemID> keys = this.cache.keySet();
    for (ItemID id : keys) {
      if (index <= (_size / 2)) {
        CacheObject co = this.cache.get(id);
        if (co.searchCounter <= frequent) {
          idForRemove.add(id);
          index++;
        }
      } else {
        break;
      }
    }

    if (LOG.beDebug()) {
      LOG.debugT("Store in file's system [" + idForRemove.size() + "] entries ");
    }

    for (ItemID id : idForRemove) {
      CacheObject co = this.cache.remove(id);
      try {
        storeOneObject(co);
      } catch (IOException e) {
        e.printStackTrace();
      }
      this.hits -= co.searchCounter;
    }

  }

  public synchronized CacheObject getCacheEntry(ItemID id) {
    CacheObject cObj = checkEntry(cache.get(id));
    try {
      cObj.increaseCounter();
      this.hits++;
      return cObj;
    } catch (Exception e) { // NullPointerException if the entry is expired
      cObj = searchLocal(id);
      if (cObj != null) {
        this.cache.put(cObj.getID(), cObj);
      }
      return cObj;
    }
  }

  private CacheObject checkEntry(CacheObject cObject) {
    if (cObject == null) {
      return null;
    }
    /* check by Cache-Control -> max-age */
    if (cObject.getMaxAge() != -1) {
      if ((cObject.getCreationTime() + cObject.getMaxAge()) <= System.currentTimeMillis()) {
        CacheObject co = cache.remove(cObject.getID());
        hits -= co.searchCounter;
        return null;
      }
    }

    /* check by Expiration Time */
    if (cObject.getExpirationTime() != -1 && (cObject.getExpirationTime() < System.currentTimeMillis())) {
      CacheObject co = cache.remove(cObject.getID());
      hits -= co.searchCounter;
      return null;
    }

    return cObject;
  }

  public synchronized CacheObject removeCacheEntry(ItemID id) {
    CacheObject co = cache.remove(id);
    hits -= co.searchCounter;
    return co;
  }

  public Map<ItemID, CacheObject> getCache() {
    return cache;
  }

  public static boolean isCacheable(HttpMethod httpMethod) {

    if (!checkStatusLine(httpMethod.getStatusLine())) {
      return false;
    }

    if (!checkMethodType(httpMethod)) {
      return false;
    }

    if (!checkHeaders(httpMethod)) {
      return false;
    }
    //.... other cases
    return true;

  }

  private static boolean checkStatusLine(StatusLine statusLine) {
    //todo -> check all available cases
    return !((statusLine == null) ||
            (statusLine.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) ||
            (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST));
  }

  private static boolean checkMethodType(HttpMethod httpMethod) {
    return (httpMethod.getName().equals(HttpMethod.METHOD_GET) ||
            httpMethod.getName().equals(HttpMethod.METHOD_HEAD));// ||
//            httpMethod.getName().equals(HttpMethod.METHOD_POST));
    // TODO implement post check
//       POST request. From RFC2616, section 9.5;
//       Responses to this method are not cacheable, unless the response
//       includes appropriate Cache-Control or Expires header fields. However,
//       the 303 (See Other) response can be used to direct the user agent to
//       retrieve a cacheable resource.

  }

  private static boolean checkHeaders(HttpMethod method) {
    /* check for pragma header */
    Header pragma = method.getResponseHeader(Header.__PRAGMA);
    if (pragma != null && pragma.getValue().equalsIgnoreCase(Header.NO_CACHE)) {
      return false;
    }
    /* check for cache controls */
    Header cacheControl = method.getResponseHeader(Header.__CACHE_CONTROL);
    if (cacheControl != null &&
            (cacheControl.getValue().equalsIgnoreCase(Header.NO_CACHE) ||
                    cacheControl.getValue().equalsIgnoreCase(Header.NO_STORE))) {
      return false;
    }
    //other cases....
    return true;
  }

  public synchronized void storeCache() throws java.io.IOException {
    /* load previous saves/unload cached objects */
    loadSavedCacheObjects();

    /* clean old cache dir */
    cleanDir();

    /* store in files */
    for (CacheObject cObj : cache.values()) {
      storeOneObject(cObj);
    }
  }

  private void storeOneObject(CacheObject cObject) throws java.io.IOException {
    cObject = checkEntry(cObject);
    long expTime = System.currentTimeMillis() + DEFAULT_MAX_AGE;
    if (cObject.getExpirationTime() != -1) {
      expTime = cObject.getExpirationTime();
    } else if (cObject.getMaxAge() != -1) {
      expTime = cObject.getCreationTime() + cObject.getMaxAge();
    }

    new PersistentCacheObject(cObject, expTime, cache_path_dir);
  }

  private void loadSavedCacheObjects() throws java.io.IOException {
    /* load the unload cache objects */
    File[] fs = new File(cache_path_dir).listFiles();
    if (fs != null && fs.length > 0) {
      for (File file : fs) {
        loadFile(file);
      }
    }
  }

  private String[] listCacheDir() {
    File[] fs = new File(cache_path_dir).listFiles();
    if (fs == null || fs.length == 0) {
      return null;
    }
    ArrayList<String> result = new ArrayList<String>();
    for (File f : fs) {
      if (f.isFile()) {
        result.add(f.getName());
      }
    }
    return result.toArray(new String[0]);
  }

  public ItemID loadFile(File fName) {
    if (fName != null && fName.isFile()) {
      ItemID id = new ItemID(fName.getName());
      if (!cache.containsKey(id)) {
        PersistentCacheObject pObj = new PersistentCacheObject(id, cache_path_dir);
        pObj.load();
        if (pObj.getExpirationTime() > System.currentTimeMillis()) {
          CacheObject cObject = new CacheObject(id, pObj.getValue());
          if (pObj.getHeaderList() != null && pObj.getHeaderList().size() > 0) {
            cObject.setHeaders(pObj.getHeaderList());
          }
          cObject.setStatusLine(pObj.getStatusLine());
          cache.put(id, cObject);
          return id;
        }
      }
    }
    return null;
  }

  public void cleanDir() throws java.io.IOException {
    File dir_as_file = new File(cache_path_dir);
    File[] files = dir_as_file.listFiles();
    for (int i = files.length; --i >= 0;) {
      if (!files[i].delete()) {
        /*"WARNING: unable to delete file " + files[i].getName() + " in cache directory " + dir_as_file.getName());*/
      }
    }
  }

  public static void stop() throws Exception {
    getInstance().storeCache();
    cManager = null;
    stopedCache = true;
  }

  public CacheObject searchLocal(ItemID id) {
    File f = new File(cache_path_dir, id.toString());
    if (f.exists()) {
      PersistentCacheObject pObj = new PersistentCacheObject(id, cache_path_dir);
      if (pObj.getExpirationTime() > System.currentTimeMillis()) {
        CacheObject cObject = new CacheObject(id, pObj.getValue());
        if (pObj.getHeaderList() != null && pObj.getHeaderList().size() > 0) {
          cObject.setHeaders(pObj.getHeaderList());
        }
        cObject.setStatusLine(pObj.getStatusLine());
        return cObject;
      } else { // delete expired file
        f.delete();
      }
    }
    return null;
  }

  public static synchronized boolean isStored(String file) {
    return getFileCache().contains(file);
  }

}
