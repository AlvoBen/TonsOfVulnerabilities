package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This request class implements the DeltaV "VERSION-CONTROL" request.
 */
public class VersionControlRequest extends XMLRequest {

  private String version;

  public VersionControlRequest(String path) {
    super("VERSION-CONTROL", path);
  }

  public VersionControlRequest(String path, String version) {
    super("VERSION-CONTROL", path);
    this.version = version;
  }


  /**
   * Prepares the request entity. Called by RequestBase.perform.
   */
	public IRequestEntity prepareRequestEntity() {
    StringEntity body = null;
    if (version!=null) {
      body = new StringEntity("text/xml", "UTF-8");
	  body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
      body.append("<version-control").append(DAV.DEFAULT_XMLNS).append("><version><href>");
      body.append(Encoder.encodeXml(version));
      body.append("</href></version></version-control>");
    }
    setRequestEntity(body);
		return body;
	}
}
