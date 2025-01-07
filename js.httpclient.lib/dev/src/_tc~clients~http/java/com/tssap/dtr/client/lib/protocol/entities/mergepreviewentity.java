package com.tssap.dtr.client.lib.protocol.entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;

/**
 * This response entity is used to report the results of the DeltaV
 * "Merge Preview" report.
 */
public class MergePreviewEntity extends SAXResponseEntity {

	private ArrayList previews;

	// state constants for the SAX state machine
	private static final int INIT = 0;
	private static final int RECOVERY_MODE = 1;
	private static final int FINAL = 2;
	private static final int MERGE_PREVIEW = 3;
	private static final int MERGE_PREVIEW_ENTRY = 4;

	// internal helper variables for the SAX state machine
	private int state;
	private int recoverState;
	private String recoverName;
	private Element preview;
	private Element child;
	
	/**
	 * The entity type string for MergePreviewEntity.
	 */	
	public static final String ENTITY_TYPE = "MergePreviewEntity";
	
	/**
	 * Checks whether the given response entity is a MergePreviewEntity.
	 * @return true, if the entity is a MergePreviewEntity.
	 */		
	public static boolean isMergePreviewEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as MergePreviewEntity.
	 * @return the entity casted to MergePreviewEntity, or null
	 * if the entity cannot be converted to MergePreviewEntity
	 */		
	public static MergePreviewEntity valueOf(IResponseEntity entity) {
		return (isMergePreviewEntity(entity))? ((MergePreviewEntity)entity) : null;		
	}	

	/**
	 * Initializes the entity from a HTTP response.
	 */
	public MergePreviewEntity(IResponse response) {
		super.initialize(response);
		state = INIT;
	}

	/**
	* Returns the type of this entity, i.e. "MergePreviewEntity"
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return "MergePreviewEntity";
	}

	/**
	 * Returns the number of preview elements stored in this entity.
	 * @return The number of elements.
	 */
	public int countResources() {
		return (previews != null) ? previews.size() : 0;
	}

	/**
	 * Returns the preview entry specified by index. The previews are provided
	 * in the order they occured in the report response.
	 * @return The preview entry that corresponds to the i-th
	 * entry in the merge preview report, or null if no preview entry was
	 * reveived.
	 */
	public Element getResource(int index) {
		return (previews != null  && index < previews.size()) ?
			 (Element) previews.get(index) : null;
	}

	/**
	 * Returns an enumeration of Element objects that were retrieved
	 * from the multistatus response.
	 */
	public Iterator getResources() {
		return (previews != null) ? previews.iterator() : null;
	}

	// SAX parser handling

	/**
	* Event handler for the SAX parser. Never call this method directly.
	*/
	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attributes) throws SAXException {

		switch (state) {
			case INIT :
				if (localName.equals("merge-preview-report")) {
					state = MERGE_PREVIEW;
					return;
				} else {
					throw new SAXException("MergePreviewEntity: Server Error, merge-preview-report Root Tag Expected");
				}
			case RECOVERY_MODE :
				return;
			case FINAL :
				throw new SAXException("MergePreviewEntity: Server Error, Multiple Root Tags Found");

			case MERGE_PREVIEW :
				if (localName.equals("update-preview")
					|| localName.equals("conflict-preview")
					|| localName.equals("ignore-preview")) {

					preview = new Element(qName, uri);
					if (previews == null) {
						previews = new ArrayList();
					}

					previews.add(preview);
					child = preview;
					state = MERGE_PREVIEW_ENTRY;
					return;
				}
				break;

			case MERGE_PREVIEW_ENTRY :
				child = child.addChild(qName, uri);
				return;

			default :
				throw new SAXException("MergePreviewEntity: Client Error, Undefined Parser State");
		}

		recoverName = localName;
		recoverState = state;
		state = RECOVERY_MODE;
	}

	/**
	* Event handler for the SAX parser. Never call this method directly.
	*/
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
		switch (state) {
			case RECOVERY_MODE :
				if (!localName.equals(recoverName)) {
					break;
				}
				recoverName = null;
				state = recoverState;
				break;

			case MERGE_PREVIEW :
				if (localName.equals("merge-preview-report")) {
					state = FINAL;
				}
				break;

			case MERGE_PREVIEW_ENTRY :
				if (child.firstChild() == null) {
					child.setValue(getStringValue());
				}
				if (child.getParent() != null) {
					child = child.getParent();
				} else {
					state = MERGE_PREVIEW;
				}

				break;

			default :
				throw new SAXException("MergePreviewEntity: Client Error, Undefined Parser State");
		}
	}
}