package com.tssap.dtr.client.lib.protocol.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.impl.MultiStatusResponse;
import com.tssap.dtr.client.lib.protocol.impl.ResponseFactory;
import com.tssap.dtr.client.lib.protocol.requests.IResourceListener;

/**
 * This response entity is used to report the results of various
 * DAV and DeltaV requests, especially for the "PROPFIND" request
 * that retrieves properties of resources.
 */
public final class MultiStatusEntity extends SAXResponseEntity {

	// payload of the entity
	private ArrayList resources;
	private String responseDescription;

	private IResourceListener resourceListener;

	// state constants for the SAX state machine
	private static final int INIT = 0;
	private static final int RECOVERY_MODE = 1;
	private static final int FINAL = 2;
	private static final int MULTISTATUS = 3;
	private static final int MULTISTATUS_SEQUENCE = 4;
	private static final int RESPONSE = 5;
	private static final int MULTISTATUS_DESCRIPTION = 6;
	private static final int RESPONSE_HREF = 7;
	private static final int PROPSTAT = 8;
	private static final int RESPONSE_SEQUENCE = 9;
	private static final int RESPONSE_DESCRIPTION = 10;
	private static final int RESPONSE_FINAL = 11;
	private static final int PROPSTAT_SEQUENCE = 12;
	private static final int PROPSTAT_DESCRIPTION = 14;
	private static final int PROPSTAT_FINAL = 15;
	private static final int MULTISTATUS_FINAL = 16;
	private static final int PROP = 17;
	private static final int PROPSTAT_STATUS = 18;
	private static final int PROP_CHILD = 19;
	private static final int RESPONSE_HREF_SEQUENCE = 20;
	private static final int RESPONSE_STATUS = 21;
	private static final int COLLISION = 22;
	private static final int WORKSPACE_VERSION = 23;
	private static final int VCR = 24;
	private static final int ORIGINAL_VERSION = 25;
	private static final int COLLISION_TYPE = 26;
	private static final int COLLISION_FINAL = 27;
	private static final int COLLISION_SEQUENCE = 28;
	private static final int MULTISTATUS_ERROR_DESCRIPTION = 29;
	private static final int RESPONSE_ERROR_DESCRIPTION = 30;
	private static final int PROPSTAT_ERROR_DESCRIPTION = 31;

	// internal helper variables for the SAX state machine
	private int state;
	private int recoverState;
	private String recoverName;
	private Stack resourceStack;
	private ResourceElement resource;
	private PropertyElement property;
	//BEGIN CHANGE
	//private StringBuffer propertyValue = new StringBuffer();
	//END CHANGE

	// OK: should have been moved to XCM. But the stat machine is
	// too complex, so decided not to do it	
	private Collision collision;
	
	private Element child;
	private String href;
	private String description;
	private String extdescription;
	private String propertyStatus;
	private String rootTag;
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(MultiStatusEntity.class);	
			

	/**
	* Initializes the entity from a HTTP response.
	* Reads the Content-Length, Content-Type, Content-MD5, ETag and
	* Last-Modified headers from the response and extracts the character
	* encoding from the Content-Type header.
	 */
	public MultiStatusEntity(String path, IResponse response) {
		super.initialize(response);
		state = INIT;
		href = path;
	}
	
	/**
	 * The entity type string for MultiStatusEntities.
	 */
	public static final String ENTITY_TYPE = "MultiStatusEntity";
	
	/**
	 * Checks whether the given response entity is a MultiStatusEntity.
	 * @return true, if the entity is a MultiStatusEntity.
	 */
	public static boolean isMultiStatusEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as MultiStatusEntity.
	 * @return the entity casted to MultiStatusEntity, or null
	 * if the entity cannot be converted to MultiStatusEntity
	 */
	public static MultiStatusEntity valueOf(IResponseEntity entity) {
		return (isMultiStatusEntity(entity))? ((MultiStatusEntity)entity) : null;		
	}	

	/**
	* Returns the type of this entity, i.e. "MultiStatusEntity"
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}

	// payload

	/**
	 * Returns the number of resource elements stored in this entity.
	 * @return The number of resources.
	 */
	public int countResources() {
		return (resources != null) ? resources.size() : 0;
	}

	/**
	 * Returns the resource specified by index. The resources are provided
	 * in the order they occured in the multistatus response.
	 * @return The resource element that corresponds to the i-th <DAV:response>
	 * entry in the multistatus response, or null if no resource was
	 * reveived or the index is out of bounds.
	 */
	public ResourceElement getResource(int i) {
		return (resources != null  &&  i < resources.size()) ? (ResourceElement)resources.get(i) : null;
	}

	/**
	 * Returns an enumeration of ResponseElement objects corresponding
	 * to the <DAV:response> tags found in the response.
	 * @return An enumeration of ResourceElement objects.
	 */
	public Iterator getResources() {
		return (resources != null) ? resources.iterator() : null;
	}

	/**
	 * Returns the optional response description reported in the
	 * <DAV:responsedescription> element of the multistatus response.
	 */
	public String getResponseDescription() {
		return responseDescription;
	}

	public void setResourceListener(IResourceListener listener) {
		resourceListener = listener;
	}
	

	// SAX parser handling

	/**
	* Event handler for the SAX parser. Never call this method directly.
	* Note: parser currently does not support properties with "mixed" content,
	* i.e. text elements with inserted tags (like HTML texts).
	*/
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (state) {
			case INIT :
				if (localName.equals("multistatus")) {
					rootTag = "multistatus";
					state = MULTISTATUS;
					return;
				} else if (localName.equals("prop")) { // for LOCK responses
					rootTag = "prop";
					resource = new ResourceElement(href);
					state = PROP;
				} else {
					throw new SAXException("MultiStatusEntity: Server Error, Multistatus Root Tag Expected");
				}
			case RECOVERY_MODE :
				return;
			case FINAL :
				throw new SAXException("MultiStatusEntity: Server Error, Multiple Root Tags Found");

			case MULTISTATUS :
				if (localName.equals("response")) {
					state = RESPONSE;
					return;
				}
				break;
			case MULTISTATUS_SEQUENCE :
				if (localName.equals("response")) {
					state = RESPONSE;
					return;
				} else if (localName.equals("responsedescription")) {
					state = MULTISTATUS_DESCRIPTION;
					return;
				}
				break;
			case MULTISTATUS_DESCRIPTION :
				if (localName.equals("error")) {
					state = MULTISTATUS_ERROR_DESCRIPTION;
					return;
				}
				break;
			case MULTISTATUS_ERROR_DESCRIPTION :
				responseDescription = qName;
				return;

			case RESPONSE :
				if (localName.equals("href")) {
					state = RESPONSE_HREF;
					return;
				}
				break;
			case RESPONSE_HREF :
				if (localName.equals("propstat")) {
					resource = new ResourceElement(href);
					propertyStatus = null;
					description = null;
					state = PROPSTAT;
					return;
				} else if (localName.equals("href")) {
					resource = new ResourceElement(href);
					state = RESPONSE_HREF_SEQUENCE;
					return;
				} else if (localName.equals("collision")) {
					resource = new ResourceElement(href);
					collision = new Collision();
					state = COLLISION;
					return;
				} else if (localName.equals("status")) {
					resource = new ResourceElement(href);
					state = RESPONSE_STATUS;
					return;
				}
				break;
			case RESPONSE_SEQUENCE :
				if (localName.equals("propstat")) {
					state = PROPSTAT;
					return;
				} else if (localName.equals("responsedescription")) {
					state = RESPONSE_DESCRIPTION;
					return;
				}
				break;
			case RESPONSE_HREF_SEQUENCE :
				if (localName.equals("href")) {
					return;
				} else if (localName.equals("status")) {
					state = RESPONSE_STATUS;
					return;
				}
				break;
			case COLLISION_SEQUENCE :
				if (localName.equals("collision")) {
					collision = new Collision();
					state = COLLISION;
					return;
				} else if (localName.equals("status")) {
					state = RESPONSE_STATUS;
					return;
				}
			case COLLISION_FINAL :
				break;

			case RESPONSE_STATUS :
				if (localName.equals("responsedescription")) {
					state = RESPONSE_DESCRIPTION;
					return;
				}
				break;

			case RESPONSE_DESCRIPTION :
				if (localName.equals("error")) {
					state = RESPONSE_ERROR_DESCRIPTION;
					return;
				}
				break;
			case RESPONSE_ERROR_DESCRIPTION :
				if (uri.equals(DAV.NAMESPACE)) {
					description = qName;
				} else {
					extdescription = qName;
				}
				return;

			case RESPONSE_FINAL :
				break;

			case PROPSTAT :
				if (localName.equals("prop")) {
					state = PROP;
					return;
				}
				break;
			case PROPSTAT_SEQUENCE :
				if (localName.equals("status")) {
					state = PROPSTAT_STATUS;
					return;
				}
				break;
			case PROPSTAT_STATUS :
				if (localName.equals("responsedescription")) {
					state = PROPSTAT_DESCRIPTION;
					return;
				}
				break;
			case PROPSTAT_DESCRIPTION :
				if (localName.equals("error")) {
					state = PROPSTAT_ERROR_DESCRIPTION;
					return;
				}
				break;
			case PROPSTAT_ERROR_DESCRIPTION :
				if (uri.equals(DAV.NAMESPACE)) {
					description = qName;
				} else {
					extdescription = qName;
				}
				return;

			case PROPSTAT_FINAL :
				break;

			case PROP :
				if (localName.equals("response")) {
					if (resourceStack == null) {
						resourceStack = new Stack();
					}
					resourceStack.push(resource);
					resourceStack.push(property);
					property = null;
					state = RESPONSE;
				} else {
					if (property == null) {
						property = new PropertyElement(qName, uri);
						return;
					} else {
						//BEGIN CHANGE
						//propertyValue.setLength(0);
						//propertyValue.append(getStringValue());
						//propertyValue.append('<').append(qName).append('>');
						//END CHANGE
						property.addChild(qName, uri);
						state = PROP_CHILD;
						child = property.lastChild();
					}
				}
				return;
			case PROP_CHILD :
				//BEGIN CHANGE
				//propertyValue.setLength(0);
				//propertyValue.append(getStringValue());
				//propertyValue.append('<').append(qName).append('>');
				//END CHANGE
				child.addChild(qName, uri);
				child = child.lastChild();
				return;

			case COLLISION :
				if (localName.equals("workspace-version")) {
					state = WORKSPACE_VERSION;
					return;
				}
				break;
			case WORKSPACE_VERSION :
				if (localName.equals("vcr")) {
					state = VCR;
					return;
				}
				break;
			case VCR :
				if (localName.equals("original-version")) {
					state = ORIGINAL_VERSION;
					return;
				}
				//falls through intentionally!
			case ORIGINAL_VERSION :
				if (localName.equals("collision-type")) {
					state = COLLISION_TYPE;
					return;
				}
				break;

			default :
				throw new SAXException("internal state error: startTag(" + rootTag + ") state(" + state + ")");
		}
		recoverName = localName;
		recoverState = state;
		clearBuffer();
		state = RECOVERY_MODE;
	}

	/**
	* Event handler for the SAX parser. Never call this method directly.
	*/
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (state) {
			case RECOVERY_MODE :
				if (!localName.equals(recoverName)) {
					break;
				}
				recoverName = null;
				state = recoverState;
				break;
			case MULTISTATUS :
				state = FINAL;
				break;
			case MULTISTATUS_DESCRIPTION :
				responseDescription = getStringValue();
				state = MULTISTATUS_FINAL;
				break;
			case MULTISTATUS_ERROR_DESCRIPTION :
				if (localName.equals("responsedescription")) {
					state = MULTISTATUS_FINAL;
				}
				break;

			case MULTISTATUS_SEQUENCE :
			case MULTISTATUS_FINAL :
				if (localName.equals("multistatus")) {
					state = FINAL;
				}
				break;

			case RESPONSE_HREF :
				href = getStringValue();
				break;
			case RESPONSE_HREF_SEQUENCE :
				resource.addPath(getStringValue());
				break;
			case RESPONSE_DESCRIPTION :
				resource.setErrorCondition(getStringValue());
				description = null;
				state = RESPONSE_FINAL;
				break;
			case RESPONSE_ERROR_DESCRIPTION :
				if (localName.equals("error")) {
					resource.setErrorCondition(description);
					resource.setExtendedCondition(extdescription);
					description = null;
					extdescription = null;
					break;
				} else if (localName.equals("responsedescription")) {
					state = RESPONSE_FINAL;
				}
				break;

			case RESPONSE_STATUS :
				if (localName.equals("status")) {
					resource.setStatus(getStringValue());
					break;
				}
				// falls through intentionally!
			case RESPONSE_SEQUENCE :
			case RESPONSE_FINAL :
				if (localName.equals("response")) {
					if (resourceStack == null || resourceStack.empty()) {
						storeResource(resource);
						resource = null;
						state = MULTISTATUS_SEQUENCE;
					} else {
						property = (PropertyElement) resourceStack.pop();
						property.addExpandedResource(resource);
						resource = (ResourceElement) resourceStack.pop();
						state = PROP;
					}
				}
				break;
			case PROPSTAT_STATUS :
				if (localName.equals("status")) {
					propertyStatus = getStringValue();
					break;
				}
				// falls through intentionally!
			case PROPSTAT_FINAL :
				if (localName.equals("propstat")) {
					if (propertyStatus != null) {
						try {
							resource.touchChildrenStatus(propertyStatus, description, extdescription);
						} catch (NumberFormatException ex) {
							TRACE.catching("endElement(String,String,String)", ex);
							// if the status line is invalid, set a 500 error
							resource.touchChildrenStatus("500 Internal Server Error", null, null);
						}
						propertyStatus = null;
						description = null;
						extdescription = null;
					}
					state = RESPONSE_SEQUENCE;
				}
				break;
			case PROPSTAT_DESCRIPTION :
				description = getStringValue();
				state = PROPSTAT_FINAL;
				break;
			case PROPSTAT_ERROR_DESCRIPTION :
				if (localName.equals("responsedescription")) {
					state = PROPSTAT_FINAL;
				}
				break;

			case PROP :
				if (localName.equals("prop")) {
					if (rootTag.equals("prop")) { // for lock responses
						if (property != null) {
							property.setStatusCode(Status.OK);
							property.setStatusDescription("OK");
							storeResource(resource);
							resource = null;
						}
						state = FINAL;
					} else {
						state = PROPSTAT_SEQUENCE;
					}
				} else if (qName.equals(property.getQualifiedName())) {
					if (property.firstChild() == null) {
						property.setValue(getStringValue());
					}
					resource.addChild(property);
					property = null;
				}
				break;

			case PROP_CHILD :
				if (child.firstChild() == null) {
					child.setValue(getStringValue());
				}
				if (child.getParent() != null) {
					child = child.getParent();
				} else {
					resource.addChild(property);
					property = null;
					state = PROP;
				}
				break;

			case WORKSPACE_VERSION :
				collision.setWorkspaceVersion(getStringValue());
				break;
			case VCR :
				if (localName.equals("vcr")) {
					collision.setVCR(getStringValue());
					break;
				}
				//falls through intentionally!
			case ORIGINAL_VERSION :
				if (localName.equals("original-version")) {
					collision.setOriginalVersion(getStringValue());
					break;
				}
				//falls through intentionally!
			case COLLISION_FINAL :
			case COLLISION_SEQUENCE :
				if (localName.equals("collision")) {
					resource.addCollision(collision);
					collision = null;
					state = COLLISION_SEQUENCE;
				} else if (localName.equals("response")) {
					storeResource(resource);
					resource = null;
					state = MULTISTATUS_SEQUENCE;
				}
				break;

			case COLLISION_TYPE :
				collision.setCollisionType(getStringValue());
				state = COLLISION_FINAL;
				break;

			default :
				throw new SAXException("internal state error: endTag(" + rootTag + ") state(" + state + ")");
		}
	}

	private void storeResource(ResourceElement resource) throws SAXException {
		boolean store = true;
		if (resourceListener != null) {
			try {
				store = resourceListener.notifyResource(resource, null);
			} catch (IOException e) {
				throw new SAXException("Evaluation of a resource element failed [" + resource.getPath() + "]", e);								
			} catch (HTTPException e) {
				throw new SAXException("Evaluation of a resource element failed [" + resource.getPath() + "]", e);
			}
		}
		if (store) {
			if (resources == null) {
				resources = new ArrayList();
			}
			resources.add(resource);
		}
	}

	static {
		ResponseFactory.registerEntityType("MultiStatusEntity", MultiStatusResponse.class);
	}

}
