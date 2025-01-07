package com.tssap.dtr.client.lib.protocol.requests.http;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.Element;
import com.tssap.dtr.client.lib.protocol.entities.OptionEntity;
import com.tssap.dtr.client.lib.protocol.entities.SAXResponseEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;

/**
 * This request class implements the standard HTTP "OPTIONS" request.
 * It also provides the various enhancements defined by DAV, DASL and DeltaV.
 */
public class OptionsRequest extends XMLRequest {

	/** the DAV header returnd by the server */
	protected String DAVHeader;

	/** the DASL header returnd by the server */
	protected Header DASLHeader;

	/**
	 * A bitwise OR combination of the constants WORKSPACE, VERSION_HISTORY
	 * and ACTIVITY, respectively
	 */
	protected int checkFeatures = 0;

	/**
	 * Query the server for a set of collections that may contain
	 *  workspaces.
	 */
	public static final int WORKSPACE = 1;

	/**
	 * Query the server for a set of collections that may contain
	 *  version histories.
	 */
	public static final int VERSION_HISTORY = 2;

	/**
	 * Query the server for a set of collections that may contain
	 *  activities.
	 */
	public static final int ACTIVITY = 4;
	
	/**
	 * Creates a OPTIONS request for the specified resource. Note,
	 * the path may be a single asterisk ("*") or the URL of a resource.
	 * This is the standard HTTP OPTIONS requests.
	 * @param path  the resource, for which the options are to be
	 * returned, or an asterisk ("*")
	 */
	public OptionsRequest(String path) {
		super("OPTIONS", path);
	}

	/**
	 * Creates a OPTIONS request for the specified resource. Note,
	 * the path must be the URL of a resource, usually the root
	 * path of the repository.
	 * @param path  the resource, for which the options are to be
	 * returned
	 * @param checkFeatures  a bitwise OR combination of the
	 * constants WORKSPACE, VERSION_HISTORY, ACTIVITY and
	 * FUNCTIONALITIES.
	 */
	public OptionsRequest(String path, int checkFeatures) {
		super("OPTIONS", path);
		this.checkFeatures = checkFeatures;
	}

	/**
	 * Returns the DAV header of the response in unparsed format.
	 */
	public String getDAVHeader() {
		if (DAVHeader == null) {
			DAVHeader = getResponse().getHeaderValue(Header.DAV.DAV);
		}
		return DAVHeader;
	}

	/**
	 * Returns the DASL header of the response in unparsed format.
	 */
	public String getDASLHeader() {
		if (DASLHeader == null) {
			DASLHeader = getResponse().getHeader(Header.DAV.DASL);
		}
		return (DASLHeader != null) ? DASLHeader.getValue() : null;
	}

	/**
	 * Checks if the specified resource (or the server at all) supports a
	 * certain feature.
	 * @param feature a string token as defined in the DAV, DeltaV or
	 * DASL specification.
	 * @return True, if the server supports the requested feature.
	 */
	public boolean supports(String feature) {
		return (getDAVHeader().indexOf(feature) != -1) || (getDASLHeader().indexOf(feature) != -1);
	}

	/**
	 * Checks if the specified resource (or the server at all) supports DAV.
	 * @return True, if the resource supports DAV.
	 */
	public boolean supportsDAV() {
		return (getDAVHeader() != null);
	}

	/**
	 * Checks if the server supports DASL.
	 * @return True, if the server supports DAV. Note, the DASL SEARCH
	 * command is not applied to individual resources, thus the OPTION
	 * request always targets the server as whole.
	 */
	public boolean supportsDASL() {
		return (getDASLHeader() != null);
	}

	/**
	 * Checks if the specified resource (or the server at all) supports DAV
	 * level 1 or 2.
	 * @param level either 1 or 2.
	 * @return True, if the resource supports the request level.
	 */
	public boolean supportsDAV(int level) {
		return (supportsDAV() && DAVHeader.indexOf(String.valueOf(level)) != -1);
	}

	/**
	 * Checks if the specified resource (or the server at all) supports
	 * versioning.
	 * @return True, if the resource supports the DeltaV version control feature.
	 */
	public boolean supportsVersionControl() {
		return (supports("version-control"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV checkout-in-place feature.
	 * @return True, if the resource supports the DeltaV checkout-in-place feature.
	 */
	public boolean supportsCheckoutInPlace() {
		return (supports("checkout-in-place"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV version history feature.
	 * @return True, if the resource supports the DeltaV version history feature.
	 */
	public boolean supportsVersionHistories() {
		return (supports("version-history"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV workspace feature.
	 * @return True, if the resource supports the DeltaV server workspace feature.
	 */
	public boolean supportsWorkspaces() {
		return (supports("workspace"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV update feature.
	 * @return True, if the resource supports the DeltaV update feature.
	 */
	public boolean supportsUpdate() {
		return (supports("update"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV label feature.
	 * @return True, if the resource supports the DeltaV label feature.
	 */
	public boolean supportsLabels() {
		return (supports("label"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV working resource feature.
	 * @return True, if the resource supports the DeltaV client workspace feature.
	 */
	public boolean supportsWorkingResources() {
		return (supports("working-resource"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV merge feature.
	 * @return True, if the resource supports the DeltaV merge feature.
	 */
	public boolean supportsMerge() {
		return (supports("merge"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV baseline feature.
	 * @return True, if the resource supports the DeltaV baseline feature.
	 */
	public boolean supportsBaselines() {
		return (supports("baseline"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV activity feature.
	 * @return True, if the resource supports the DeltaV activity feature.
	 */
	public boolean supportsActivities() {
		return (supports("activity"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DeltaV versioned collection feature.
	 * @return True, if the resource supports the DeltaV versioned collections feature.
	 */
	public boolean supportsVersionedCollections() {
		return (supports("version-controlled-collection"));
	}

	/**
	 * Checks if the specified resource (or the server at all) supports the
	 * DASL query feature
	 * @return True, if the resource supports the DASL SEARCH command.
	 */
	public boolean supportsSearch() {
		return (supports("search"));
	}

	/**
	 * Returns a list of supported query grammars for DASL aware servers.
	 * @return A list of grammar URIs (see DASL draft) containing at least
	 * the entry "<DAV:basicsearch>", or null, if the server doesn not
	 * support DASL.
	 */
	public String[] getSupportedQueryGrammars() {
		getDASLHeader();
		return (DASLHeader != null) ? (String[]) DASLHeader.getParts().toArray() : null;
	}

	/**
	 * Returns the collection set for the specified feature if the OPTIONS
	 * request has been sucessful and the server returned the necessary
	 * information. Otherwise this method returns null.
	 * @param feature a string token as defined in the DeltaV specification.
	 * @return An Element instance containing a list of <href> children
	 * representing collection URLs where objects of the requested feature
	 * could be created.
	 */
	public Element getCollectionSet(int feature) {
		Element collectionSet = null;
		if ("OptionEntity".equals(getResponse().getEntityType())) {
			String set = null;
			switch (feature) {
				case WORKSPACE :
					set = "workspace-collection-set";
					break;
				case VERSION_HISTORY :
					set = "version-history-collection-set";
					break;
				case ACTIVITY :
					set = "activity-collection-set";
					break;
				default:
					throw new IllegalArgumentException("Unknown feature: " + feature);				
			}
			if (set != null) {
				collectionSet = ((OptionEntity) getResponse().getEntity()).getCollectionsSet(set);
			}
		}
		return collectionSet;
	}
	
	/**
	 * Prepares the request entity. Called by RequestBase.perform.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		if (checkFeatures != 0) {
			body = new StringEntity("text/xml", "UTF-8");
			body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
			body.append("<options").append(DAV.DEFAULT_XMLNS).append(">");
			if ((checkFeatures & WORKSPACE) != 0) {
				body.append("<workspace-collection-set/>");
			}
			if ((checkFeatures & VERSION_HISTORY) != 0) {
				body.append("<version-history-collection-set/>");
			}
			if ((checkFeatures & ACTIVITY) != 0) {
				body.append("<activity-collection-set/>");
			}
			body.append("</options>");
		}
		setRequestEntity(body);
		return body;
	}

	/**
	 * Factory method for response entity. Called by XMLRequest.parse.
	 */
	protected SAXResponseEntity createResponseEntity(String path, IResponse response) {
		SAXResponseEntity entity = super.createResponseEntity(path, response);
		if (entity == null && response.isContentXML() && response.getStatus() == Status.OK) {
			entity = new OptionEntity(response);
		}
		return entity;
	}
	
	
//	public static void main(String[] args) throws Exception 
//	{
//		List features = new ArrayList();
//		features.add("undelete-method");
//		features.add("activity-query-report");
//		
//		//OptionsRequest req = new OptionsRequest("/", null, true);	
//		OptionsRequest req = new OptionsRequest("/", FUNCTIONALITIES);
//		req.prepareRequestEntity();
//		
//		Response resp = new Response(req);
//		String body = "<options-response xmlns=\"DAV:\" xmlns:XCM=\"XCM:\"><XCM:functionality-set><XCM:functionality name=\"activity-query-report\"><XCM:option name=\"contains-collection-only\"/><XCM:option name=\"dummy-test\"/></XCM:functionality><XCM:functionality name=\"undelete-method\"><XCM:option name=\"depth-infinity-undelete\"/></XCM:functionality></XCM:functionality-set></options-response>";
//		String s = "HTTP/1.1 200 OK\r\n"
//				 + "Server: SAP J2EE Engine/6.30\r\n"
//				 + "Content-Length: "
//				 + body.length() 
//				 + "\r\n"
//				 + "Content-Type: text/xml\r\n"
//				 + "Date: Wed, 07 Apr 2004 09:29:24 GMT\r\n"
//				 + "\r\n"
//				 + body;
//		
//		ByteArrayInputStream base = new ByteArrayInputStream(s.getBytes());
//		ResponseStream in = new ResponseStream(base, 1024);
//		resp.initialize(in);
//		OptionEntity entity = (OptionEntity)req.parse("/", resp);
//		
//		List functionalities = entity.getFunctionalitySet();
//		List options = entity.getFunctionality("activity-query-report");
//		options = entity.getFunctionality("undelete-method");
//	}

}
