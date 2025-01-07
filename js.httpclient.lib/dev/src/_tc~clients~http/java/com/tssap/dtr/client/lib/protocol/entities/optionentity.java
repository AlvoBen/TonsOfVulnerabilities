package com.tssap.dtr.client.lib.protocol.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;

/**
 * This response entity is used to report the results of the standard
 * HTTP "OPTIONS" request including the various enhancements defined
 * by DAV and DeltaV.
 */
public final class OptionEntity extends SAXResponseEntity {

	/** A list of Element objects */
	private HashMap collections;

	// internal helper variables for the SAX state machine
	private int state;
	private int recoverState;
	private String recoverName;
	private Element collection;
	private Map functionalities;
	private List options;
	private String functionality;
	private String tagName;

	// state constants for the SAX state machine
	private static final int INIT = 0;
	private static final int FINAL = 1;
	private static final int RECOVERY_MODE = 2;
	private static final int OPTIONS_RESPONSE = 3;
	private static final int HREF_COLLECTION = 4;
	private static final int FUNCTIONALITY_SET = 10;
	private static final int FUNCTIONALITY = 15;
	private static final int OPTION = 20;

	/**
	 * The entity type string for OptionEntity.
	 */
	public static final String ENTITY_TYPE = "OptionEntity";
	
	/**
	 * Checks whether the given response entity is a OptionEntity.
	 * @return true, if the entity is a OptionEntity.
	 */	
	public static boolean isOptionEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as OptionEntity.
	 * @return the entity casted to OptionEntity, or null
	 * if the entity cannot be converted to OptionEntity
	 */	
	public static OptionEntity valueOf(IResponseEntity entity) {
		return (isOptionEntity(entity))? ((OptionEntity)entity) : null;		
	}

	/**
	 * Initializes the entity from a HTTP response.
	 */
	public OptionEntity(IResponse response) {
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

	/** Returns the specified collection set as an Element object containing
	 *  a list of <DAV:href> childs. The parameter collectionSet may be
	 *  either "workspace-collection-set" or "version-history-collection-set"
	 *  or "activity-collection-set".
	 */
	public Element getCollectionsSet(String collectionSet) {
		return (collections != null) ? (Element) collections.get(collectionSet) : null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List getFunctionalitySet() {
		return (functionalities!=null)? new ArrayList(functionalities.keySet()) : new ArrayList();
	}
	
	/**
	 * 
	 * @param functionality
	 * @return
	 */
	public List getFunctionality(String functionality) {
		return (List)functionalities.get(functionality);
	}	

	// SAX parser handling
	/**
	* Event handler for the SAX parser. Never call this method directly.
	*/
	public void startElement(String uri, String name, String qualifiedName, Attributes attributes)
		throws SAXException {
		switch (state) {
			case INIT :
				if (name.equals("options-response")) {
					state = OPTIONS_RESPONSE;
					return;
				} else {
					throw new SAXException("OptionEntity: Server Error, options-response Root Tag Expected");
				}
			case FINAL :
				throw new SAXException("OptionEntity: Server Error, Multiple Root Tags Found");
				
			case OPTIONS_RESPONSE :
				if (name.equals("workspace-collection-set")
					|| name.equals("version-history-collection-set")
					|| name.equals("activity-collection-set")) 
				{
					collection = new Element(qualifiedName, uri);
					tagName = name;
					state = HREF_COLLECTION;
					return;
				}
				if (name.equals("functionality-set")) {
					tagName = name;
					functionalities = new HashMap();
					state = FUNCTIONALITY_SET;
					return;
				}				
				break;
				
			case FUNCTIONALITY_SET :
				if (name.equals("functionality")) {
					tagName = name;					
					functionality = attributes.getValue("name");
					if (functionality == null) {
						throw new SAXException("OptionEntity: Server Error, Mandatory attribute 'name' " +							"missing in <functionality>");
					}
					options = new ArrayList();
					functionalities.put(functionality, options);										
					state = FUNCTIONALITY;
					return;
				}							
				break;
				
			case FUNCTIONALITY :
				if (name.equals("option")) {
					tagName = name;
					String option = attributes.getValue("name");
					if (option == null) {
						throw new SAXException("OptionEntity: Server Error, Mandatory attribute 'name' " +							"missing in <option>");
					}
					options.add(option);									
					state = OPTION;
					return;
				}			
				break;
				
			case HREF_COLLECTION :
				if (name.equals("href")) {
					return;
				}
				break;
				
			default :
				throw new SAXException("OptionEntity: Client Error, Undefined Parser State");
		}
		recoverName = name;
		recoverState = state;
		state = RECOVERY_MODE;
	}

	/**
	* Event handler for the SAX parser. Never call this method directly.
	*/
	public void endElement(String uri, String name, String qualifiedName) throws SAXException {
		switch (state) {
			case RECOVERY_MODE :
				if (!name.equals(recoverName)) {
					break;
				}
				recoverName = null;
				state = recoverState;
				break;
			case HREF_COLLECTION :
				if (name.equals(tagName)) {
					if (collections == null) {
						collections = new HashMap();
					}
					collections.put(tagName, collection);
					state = OPTIONS_RESPONSE;
				} else if (name.equals("href")) {
					collection.addChild(qualifiedName, getStringValue(), uri);
				}
				break;
				
			case FUNCTIONALITY_SET :
				state = OPTIONS_RESPONSE;
				break;				
				
			case FUNCTIONALITY :
				options = null;
				state = FUNCTIONALITY_SET;
				break;
				
			case OPTION :
				state = FUNCTIONALITY;
				break;				
				
			case OPTIONS_RESPONSE :
				state = FINAL;
				break;
				
			default :
				throw new SAXException("OptionEntity: Client Error, Undefined Parser State");
		}
	}
}
