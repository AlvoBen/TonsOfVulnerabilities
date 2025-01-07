package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

/**
 * This request class implements the DeltaV "MKACTIVITY" request
 * to create activities.
 */
public class MkActivityRequest extends XMLRequest {

  public MkActivityRequest(String path) {
    super("MKACTIVITY", path);
  }
}
