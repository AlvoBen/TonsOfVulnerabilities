package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

/**
 * This request class implements the DeltaV "MKWORKSPACE" request
 * to create workspaces.
 */
public class MkWorkspaceRequest extends XMLRequest {

  public MkWorkspaceRequest(String path) {
    super("MKWORKSPACE", path);
  }

}
