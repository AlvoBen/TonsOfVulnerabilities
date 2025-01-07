package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

/**
 * This request class implements the DAV "MKCOL" request to
 * create collections.
 */
public class MkColRequest extends XMLRequest {

  public MkColRequest(String path) {
    super("MKCOL", path);
  }
}
