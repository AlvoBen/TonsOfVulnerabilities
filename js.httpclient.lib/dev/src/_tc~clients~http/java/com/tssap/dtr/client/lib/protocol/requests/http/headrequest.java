package com.tssap.dtr.client.lib.protocol.requests.http;

import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.requests.RequestBase;

/**
 * This request class implements the standard HTTP "Head" request.
 */
public class HeadRequest extends RequestBase {

	private String label;

  /**
   * Creates a GET request for the specified resource.
   * @param path  the path of a resource to retrieve.
   */
	public HeadRequest(String path) {
		super("HEAD", path);
	}

  /**
   * Retrieve a certain version of a resource matching the given label.
   * @param label  the label of a version.
   */
	public void setApplyToLabel(String label) {
		this.label = label;
		if (label != null) {
			setHeader(Header.DAV.LABEL, label);
		}
	}

}

