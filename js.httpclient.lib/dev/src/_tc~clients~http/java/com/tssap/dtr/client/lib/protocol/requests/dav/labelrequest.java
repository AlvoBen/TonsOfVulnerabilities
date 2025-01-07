package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This request class implements the DeltaV "LABEL" request.
 */
public class LabelRequest extends XMLRequest {

  private String label;
  private String labelName;
  private LabelMethod method;
  private Depth depth = Depth.DEPTH_0;

  /**
   * Creates a LABEL request for the specified version.
   * @param path  the path of a version to label.
   * @param labelName  the label to set for the version.
   */
  public LabelRequest(String path, String labelName) {
    super("LABEL", path);
    this.labelName = labelName;
  }

  /**
   * Creates a LABEL request for the specified version.
   * @param path  the path of a version to label.
   * @param labelName  the label to set for the version.
   * @param method  determines whether the label should be added,
   * changed or removed.
   */
  public LabelRequest(String path, String labelName, LabelMethod method) {
    super("LABEL", path);
    this.labelName = labelName;
    this.method = method;
  }

  /**
   * Sets the depth for this request.
   * @param depth  determines for collections if the request should be
   * applied only to the collection itself (Depth.DEPTH_0), the internal
   * members of the collection (Depth.DEPTH_1), or to any members of the
   * collection hierarchy (Depth.INFINITY).
   * Default is Depth.DEPTH_INFINITY. Adds a "Depth" header to the request.
   */
  public void setDepth(Depth depth) {
    if (depth.equals(Depth.DEPTH_0)) {
      setHeader(Header.DAV.DEPTH, "0");
      this.depth = Depth.DEPTH_0;
    } else if (depth.equals(Depth.DEPTH_1)) {
      setHeader(Header.DAV.DEPTH, "1");
      this.depth = Depth.DEPTH_1;
    } else if (depth.equals(Depth.DEPTH_INFINITY)) {
      setHeader(Header.DAV.DEPTH, "infinity");
      this.depth = Depth.DEPTH_INFINITY;
    }
  }

  /**
   * Labels a certain version of a resource matching the given label.
   * @param label  a version label.
   */
	public void setApplyToLabel(String label) {
		this.label = label;
		if (label != null) {
			setHeader(Header.DAV.LABEL, label);
		}
	}

  /**
   * Prepares the request entity.
   * This method is called during execution of this request. Do not call
   * this method directly.
   * @return A request entity for this LABEL request.
   */
	public IRequestEntity prepareRequestEntity() {
    StringEntity body = null;
    body = new StringEntity("text/xml", "UTF-8");
    String tagName = "set";
    if (method.equals(LabelMethod.ADD_LABEL)) {
        tagName ="add";
    } else if (method.equals(LabelMethod.REMOVE_LABEL)) {
        tagName = "remove";
    }
	body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
    body.append("<label").append(DAV.DEFAULT_XMLNS).append("><").append(tagName).append("><label-name>");
    body.append(Encoder.encodeXml(labelName));
    body.append("</label-name></").append(tagName).append("></label>");
    setRequestEntity(body);
		return body;
	}

}
