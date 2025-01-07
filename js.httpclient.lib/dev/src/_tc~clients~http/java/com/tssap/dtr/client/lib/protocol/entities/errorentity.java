package com.tssap.dtr.client.lib.protocol.entities;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;

/**
 * This response entity is used to report DeltaV error conditions.
 */
public final class ErrorEntity extends SAXResponseEntity {

	private String errorCondition;
	private String extendedCondition;

	// internal helper variables for the SAX state machine
	private int state;

	// state constants for the SAX state machine
	private static final int INIT = 0;
	private static final int FINAL = 1;
	private static final int ERROR = 2;
	private static final int ERROR_ELEMENT = 3;

	/**
	 * The entity type string for ErrorEntity.
	 */
	public static final String ENTITY_TYPE = "ErrorEntity";
	
	/**
	 * Checks whether the given response entity is a ErrorEntity.
	 * @return true, if the entity is a ErrorEntity.
	 */	
	public static boolean isErrorEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as ErrorEntity.
	 * @return the entity casted to ErrorEntity, or null
	 * if the entity cannot be converted to ErrorEntity
	 */	
	public static ErrorEntity valueOf(IResponseEntity entity) {
		return (isErrorEntity(entity))? ((ErrorEntity)entity) : null;		
	}
	
	/**
	 * Initializes the entity from a HTTP response.
	 */
	public ErrorEntity(IResponse response) {
		super.initialize(response);
		state = INIT;
	}

	/**
	 * Returns the type of this entity, i.e. "ErrorEntity"
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}

	// payload
	/**
	  * Returns the DeltaV pre- or postcondition.
	  */
	public String getErrorCondition() {
		return errorCondition;
	}

	/**
	  * Returns an extended pre- or postcondition.
	  */
	public String getExtendedCondition() {
		return extendedCondition;
	}

	// SAX parser handling

	/**
	* Event handler for the SAX parser. Never call this method directly.
	*/
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (state) {
			case INIT :
				if (localName.equals("error")) { // deltaV errors
					state = ERROR;
					return;
				} else { // ACL errors use no common root tag for errors
					return;
				}
			case FINAL :
				throw new SAXException("ErrorEntity: Server Error, Multiple Root Tags Found");
			case ERROR :
				state = ERROR_ELEMENT;
				return;
			default :
				throw new SAXException("ErrorEntity: Client Error, Undefined Parser State");
		}
	}

	/**
	* Event handler for the SAX parser. Never call this method directly.
	*/
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (state) {
			case INIT :
				errorCondition = qName;
				state = FINAL;
				break;
			case ERROR_ELEMENT :
				if (localName.equals("error")) {
					state = FINAL;
					break;
				}
				if (uri.equals(DAV.NAMESPACE)) {
					errorCondition = qName;
				} else {
					extendedCondition = qName;
				}
				break;
			default :
				throw new SAXException("OptionEntity: Client Error, Undefined Parser State");
		}
	}
}
