package com.tssap.dtr.client.lib.protocol.requests.http;

import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

/**
 * This request class implements the HTTP "DELETE" request.
 */
public class DeleteRequest extends XMLRequest {

  /**
   * Creates a DELETE request for the resource specified by its relative
   * path.
   * @param path  the path of a resource to be deleted, e.g. a file, collection, activity,
   * working resource etc.
   */
  public DeleteRequest(String path) {
    super("DELETE", path);
  }
}
