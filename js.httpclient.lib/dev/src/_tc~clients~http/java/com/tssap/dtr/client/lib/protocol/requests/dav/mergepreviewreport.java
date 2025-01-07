package com.tssap.dtr.client.lib.protocol.requests.dav;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.Element;
import com.tssap.dtr.client.lib.protocol.entities.MergePreviewEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.entities.SAXResponseEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;
import java.util.Iterator;

/**
 * This request class implements the DeltaV "Merge Preview" report.
 */
public class MergePreviewReport extends XMLRequest {

	private String source;

	/**
	 * Creates a MergePreview report for the specified source and
	 * destination resources.
	 * @param path  the path of the merge destination.
	 * @param source  the path of the merge source.
	 */
	public MergePreviewReport(String path, String source) {
		super("REPORT", path);
		this.source = source;
	}

	/**
	 * Returns the number of preview elements stored in this entity.
	 * @return The number of preview elements.
	 */
	public int countResources() {
		MergePreviewEntity entity = MergePreviewEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.countResources() : 0;
	}

	/**
		 * Returns the preview entry specified by index. The previews are provided
		 * in the order they occured in the report response.
	 * @param i index of a preview entry.
		 * @return The preview entry that corresponds to the i-th
		 * entry in the merge preview report, or null if no preview entry was
		 * reveived.
	 */
	public Element getResource(int i) {
		MergePreviewEntity entity = MergePreviewEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.getResource(i) : null;
	}

	/**
		 * Returns an enumeration of Element objects that were retrieved
		 * from the report response.
	 */
	public Iterator getResources() {
		MergePreviewEntity entity = MergePreviewEntity.valueOf(getResponse().getEntity());
		return (entity != null)? entity.getResources() : null;			
	}

	/**
	 * Prepares the request entity.
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @return A request entity for this report.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<merge-preview").append(DAV.DEFAULT_XMLNS).append(">");
		body.append("<source><href>").append(Encoder.encodeXml(source)).append("</href></source>");
		body.append("</merge-preview>");
		setRequestEntity(body);
		return body;
	}

	/**
	 * Factory method for response entity. Called by XMLRequest.parse.
	 */
	protected SAXResponseEntity createResponseEntity(String path, IResponse response) {
		SAXResponseEntity entity = super.createResponseEntity(path, response);
		if (entity == null && response.isContentXML() && response.getStatus() == Status.OK) {
			entity = new MergePreviewEntity(response);
		}
		return entity;
	}

}