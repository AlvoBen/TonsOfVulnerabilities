package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

/**
 * This request class implements the DAV "UNLOCK" request.
 */
public class UnlockRequest extends XMLRequest {

  public UnlockRequest(String path, String lockToken) {
    super("UNLOCK", path);
    setHeader(Header.DAV.LOCK_TOKEN, lockToken);
  }
}