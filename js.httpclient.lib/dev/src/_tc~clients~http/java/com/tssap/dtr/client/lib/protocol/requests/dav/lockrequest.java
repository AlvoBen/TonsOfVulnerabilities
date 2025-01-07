package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.entities.SAXResponseEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;


/**
 * This request class implements the DAV "LOCK" request.
 */
public class LockRequest extends XMLRequest {

  private LockScope lockScope;
  private String owner;
  private Depth depth = Depth.DEPTH_INFINITY;
  private StringBuffer timeoutHeader;
  private boolean refreshLock = false;

  /**
   * Creates a LOCK request for the specified resource.
   * @param path  the path of a resource, e.g. a file or collection.
   * @param lockScope  determines whether this lock is a shared or exclusive
   * lock.
   */
  public LockRequest(String path, LockScope lockScope) {
    super("LOCK", path);
    this.lockScope = lockScope;
  }

  /**
   * Creates a LOCK request for the specified resource.
   * @param path  the path of a resource, e.g. a file or collection.
   * @param refreshLock  if true, the timeout of an already existing lock is
   * refreshed.
   */
  public LockRequest(String path, boolean refreshLock) {
    super("LOCK", path);
    this.refreshLock = refreshLock;
  }

  /**
   * Sets the owner of this lock.
   * @param owner  the id of the owner of this lock.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * Sets the depth for this request.
   * @param depth  determines for collections if the request should be
   * applied only to the collection itself (Depth.DEPTH_0),
   * or to any members of the collection hierarchy (Depth.INFINITY).
   * Default is Depth.DEPTH_INFINITY. Adds a "Depth" header to the request.
   */
  public void setDepth(Depth depth) {
    if (depth.equals(Depth.DEPTH_0)) {
      setHeader(Header.DAV.DEPTH, "0");
      this.depth = Depth.DEPTH_0;
	  } else if (depth.equals(Depth.DEPTH_INFINITY)) {
      setHeader(Header.DAV.DEPTH, "infinity");
      this.depth = Depth.DEPTH_INFINITY;
	  }
  }

  /**
   * Adds a lock timeout specifier to this request.
   * @param timeout  determined whether this timeout should be last infinite or
   * for a certain period of time only.
   * @param seconds  determined the duration of a finite timeout in seconds.
   */
  public void addTimeout(LockTimeout timeout, int seconds) {
    if (timeoutHeader==null) {
      timeoutHeader = new StringBuffer();
    }
    if (timeoutHeader.length()>0) {
      timeoutHeader.append(", ");
    }
    timeoutHeader.append(timeout);
    if (timeout==LockTimeout.SECONDS) {
      timeoutHeader.append(seconds);
    }
    setHeader(Header.DAV.TIMEOUT, timeoutHeader.toString());
  }

  /**
   * Prepares the request entity.
   * This method is called during execution of this request. Do not call
   * this method directly.
   * @return A request entity for this LOCK request.
   */
	public IRequestEntity prepareRequestEntity() {
    if (refreshLock) {
      return null;
    }

    StringEntity body = null;
    body = new StringEntity("text/xml", "UTF-8");
	body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
    body.append("<lockinfo").append(DAV.DEFAULT_XMLNS).append(">");
    body.append("<lockscope><").append(lockScope.toString()).append("></lockscope>");
    body.append("<locktype><write/></locktype>");
    if (owner!=null) {
      body.append("<owner><href>").append(Encoder.encodeXml(owner)).append("</href></owner>");
    }
    body.append("</lockinfo>");
    setRequestEntity(body);
		return body;
	}

  /**
   * Factory method for response entity. Called by XMLRequest.parse.
   */
  protected SAXResponseEntity createResponseEntity(String path, IResponse response) {
    SAXResponseEntity entity = super.createResponseEntity(path, response);
    if (entity==null && response.isContentXML() && response.getStatus()==Status.OK) {
      entity = new MultiStatusEntity(path, response);
    }
		return entity;
  }

}