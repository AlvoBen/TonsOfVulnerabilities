package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;


/**
 * This request class implements the DeltaV "BASELINE-CONTROL" request.
 */
public class BaselineControlRequest extends XMLRequest {

  private String sourceBaseline;

  /**
   * Creates a new BASELINE-CONTROL request.
   * @param path  the repository path of a collection to put under baseline
   * control.
   */
  public BaselineControlRequest(String path) {
    super("BASELINE-CONTROL", path);
  }

  /**
   * Creates a new BASELINE-CONTROL request.
   * @param path  the repository path of a collection to put under baseline
   * control.
   * @param sourceBaseline  the baseline from which the new baseline
   * should be initialized.
   */
  public BaselineControlRequest(String path, String sourceBaseline) {
    super("BASELINE-CONTROL", path);
    this.sourceBaseline = sourceBaseline;
  }

  /**
   * Prepares the request entity.
   * This method is called during execution of this request. Do not call
   * this method directly.
   * @return A request entity for this request.
   */
	public IRequestEntity prepareRequestEntity() {
    StringEntity body = null;
    if (sourceBaseline!=null) {
      body = new StringEntity("text/xml", "UTF-8");
	  body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
      body.append("<baseline-control").append(DAV.DEFAULT_XMLNS).append("><baseline><href>");
      body.append(Encoder.encodeXml(sourceBaseline));
      body.append("</href></baseline></baseline-control>");
    }
    setRequestEntity(body);
		return body;
	}

}
