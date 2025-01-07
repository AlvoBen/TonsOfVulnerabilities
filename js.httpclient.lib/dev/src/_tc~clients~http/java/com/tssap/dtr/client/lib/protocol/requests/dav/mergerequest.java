package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;


/**
 * This request class implements the DeltaV "MERGE" request.
 */
public class MergeRequest extends XMLRequest {

  private String mergeSource;
  private boolean prohibitAutoMerge = false;
  private boolean prohibitAutoCheckout = false;
  private boolean checkinActivity = false;

  public MergeRequest(String mergeTarget, String mergeSource) {
    super("MERGE", mergeTarget);
    this.mergeSource = mergeSource;
  }

  public void prohibitAutoMerge(boolean prohibit) {
    this.prohibitAutoMerge = prohibit;
  }

  public void prohibitAutoCheckout(boolean prohibit) {
    this.prohibitAutoCheckout = prohibit;
  }

  public void checkinActivity(boolean checkinActivity) {
    this.checkinActivity = checkinActivity;
  }

  /**
   * Prepares the request entity. Called by RequestBase.perform.
   */
	public IRequestEntity prepareRequestEntity() {
    StringEntity body = null;
    body = new StringEntity("text/xml", "UTF-8");
	  body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
    body.append("<merge").append(DAV.DEFAULT_XMLNS).append("><source><href>");
    body.append(Encoder.encodeXml(mergeSource)).append("</href></source>");
    if (prohibitAutoMerge) {
      body.append("<no-auto-merge/>");
    }
    if (prohibitAutoCheckout) {
      body.append("<no-checkout/>");
    }
    if (checkinActivity) {
      body.append("<checkin-activity/>");
    }
    body.append("</merge>");
    setRequestEntity(body);
		return body;
	}


}
