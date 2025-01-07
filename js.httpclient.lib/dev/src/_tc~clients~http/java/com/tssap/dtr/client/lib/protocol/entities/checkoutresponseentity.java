package com.tssap.dtr.client.lib.protocol.entities;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;


public class CheckoutResponseEntity extends SAXResponseEntity {

	private static final int INIT = 0;
	private static final int FINAL = 1;
	private static final int CHECKOUT_RESPONSE = 2;
	private static final int CHECKED_OUT = 3;
	private static final int HREF = 4;
	
	private int state;
	
	private String checkedoutVersionURL;
	
	/**
	 * The entity type string for CheckoutResponseEntity.
	 */
	public static final String ENTITY_TYPE = "CheckoutResponseEntity";
	
	/**
	* Returns the type of this entity, i.e. "CheckoutResponseEntity"
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}
	
	/**
	 * Checks whether the given response entity is a CheckoutResponseEntity.
	 * @return true, if the entity is a CheckoutResponseEntity.
	 */	
	public static boolean isCheckoutResponseEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as CheckoutResponseEntity.
	 * @return the entity casted to CheckoutResponseEntity, or null
	 * if the entity cannot be converted to CheckoutResponseEntity
	 */	
	public static CheckoutResponseEntity valueOf(IResponseEntity entity) {
		return (isCheckoutResponseEntity(entity))? ((CheckoutResponseEntity)entity) : null;		
	}
	
	
	public CheckoutResponseEntity(IResponse response) {
		super.initialize(response);
		state = INIT;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		switch (state) {
			case INIT :
				if (uri.equals(DAV.NAMESPACE) && localName.equals("checkout-response")) {
					state = CHECKOUT_RESPONSE;
					return;
				} else {
					throw new SAXException("CheckoutResponseEntity: Server Error, DAV:checkout-response Root Tag Expected");
				}
			case CHECKOUT_RESPONSE :
				if (uri.equals(DAV.NAMESPACE) && localName.equals("checked-out")) {
					state = CHECKED_OUT;
					return;
				} else {
					throw new SAXException("CheckoutResponseEntity: Server Error, DAV:checked-out element");
				}
			case CHECKED_OUT:
				if (uri.equals(DAV.NAMESPACE) && localName.equals("href")) {
					state = HREF;
					return;
				} else {
					throw new SAXException("CheckoutResponseEntity: Server Error, DAV:href element");
				}
			case HREF:
				break;
		case FINAL :
			throw new SAXException("CheckoutResponseEntity: Server Error, Multiple Root Tags Found");
		default :
			throw new SAXException("CheckoutResponseEntity: Client Error, Undefined Parser State");
		}		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (state) {
			case HREF:
				checkedoutVersionURL = getStringValue();
				state = CHECKED_OUT;
				break;
			case CHECKED_OUT:
				state = CHECKOUT_RESPONSE;
				break;
			case CHECKOUT_RESPONSE:
				state = FINAL;
				break;
			default :
				throw new SAXException("CheckoutResponseEntity: Client Error, Undefined Parser State");
		}
	}
	
	public String getCheckedoutVersionURL() {
		return checkedoutVersionURL;
	}

}
