package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

/**
 * This request class implements the DeltaV "UNCHECKOUT" request.
 */
public class UncheckoutRequest extends XMLRequest {

  public UncheckoutRequest(String path) {
    super("UNCHECKOUT", path);
  }
}
