package com.tssap.dtr.client.lib.protocol.requests.dav;

import java.util.ArrayList;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.CheckoutResponseEntity;
import com.tssap.dtr.client.lib.protocol.entities.SAXResponseEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.XMLRequest;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This request class implements the DeltaV "CHECKOUT" request.
 */
public class CheckoutRequest extends XMLRequest {

	// variables defining the request parameters
	protected ArrayList activities;
	protected boolean applyToVersion = false;
	protected boolean unreserved = false;
	protected boolean allowForks = false;
	protected boolean newActivity = false;
	protected String label;

	/**
	 * Creates a CHECKOUT request for the specified resource, collection or
	 * version.
	 * @param path  the repository path of a checked in resource or collection,
	 * or a version.
	 */
	public CheckoutRequest(String path) {
		super("CHECKOUT", path);
	}

	/**
	 * Creates a CHECKOUT request for a resource with respect to an activity.
	 * If the applyToVersion flag is set the resource must be a VCR and a
	 * &lt;DAV:apply-to-version&gt; option then is added to the request.
	 * @param path  the repository path of a checked in resource or collection,
	 * or a version.
	 * @param activity  path of the activity to use for the checkout.
	 * @param applyToVersion  if true, the checkout is applied to the checked-in
	 * version of a VCR. The <code>path</code> parameter in this case must
	 * describe a VCR.
	 */
	public CheckoutRequest(String path, String activity, boolean applyToVersion) {
		this(path);
		addActivity(activity);
		setApplyToVersion(applyToVersion);
	}

	/**
	 * Creates a CHECKOUT request for a resources with respect to a set of activities.
	 * If the applyToVersion flag is set the resource must be a VCR and a
	 * &lt;DAV:apply-to-version&gt; option then is added to the request.
	 * @param activitySet  array of activities to use for the checkout.
	 * @param applyToVersion  if true, the checkout is applied to the checked-in
	 * version of a VCR. The <code>path</code> parameter in this case must
	 * describe a VCR.
	 */
	public CheckoutRequest(String path, String[] activitySet, boolean applyToVersion) {
		this(path);
		addActivitySet(activitySet);
		setApplyToVersion(applyToVersion);
	}

	/**
	 * Add a single activity to this request.
	 * @param activity  path of an activity to use for the checkout.
	 */
	public void addActivity(String activity) {
		if (activities == null) {
			activities = new ArrayList();
		}
		activities.add(activity);
	}

	/**
	 * Adds a set of activities to this request.
	 * @param activitySet  array of activities to use for the checkout.
	 */
	public void addActivitySet(String[] activitySet) {
		if (activities == null) {
			activities = new ArrayList();
		}
		for (int i = 0; i < activitySet.length; ++i) {
			activities.add(activitySet[i]);
		}
	}

	/**
	 * Requests that the server creates a new activity for that request.
	 * Adds a &lt;DAV:new/&gt; option to the &lt;DAV:activity-set&gt; tag of the request.
	 * @param newActivity  if true, the server should create a new activity.
	 */
	public void setCreateNewActivity(boolean newActivity) {
		this.newActivity = newActivity;
	}

	/**
	 * Sets whether this CHECKOUT request may create forks in the version history.
	 * Adds a &lt;DAV:fork-ok/&gt; option to the request.
	 * @param allowForks  if true, forks are allowed.
	 */
	public void setAllowForks(boolean allowForks) {
		this.allowForks = allowForks;
	}

	/**
	 * Sets whether this request should be applied to the checked-in version
	 * behind a VCR.
	 * Adds a &lt;DAV:apply-to-version/&gt; option to the request.
	 * @param applyToVersion  if true, the request is applied to the checked-in
	 * version of a VCR.
	 */
	public void setApplyToVersion(boolean applyToVersion) {
		this.applyToVersion = applyToVersion;
	}

	/**
	 * Sets whether the checkout should be handled as unreserved.
	 * Adds a &lt;DAV:unreserved/&gt; option to the request.
	 * @param unreserved  if true, the checkout is handled unreserved.
	 */
	public void setCheckoutUnreserved(boolean unreserved) {
		this.unreserved = unreserved;
	}

	/**
	 * Applies the checkout to a certain version of a VCR matching the given label.
	 * Adds a "Label" header to the request.
	 * @param label  the label to match.
	 */
	public void setApplyToLabel(String label) {
		this.label = label;
		if (label != null) {
			setHeader(Header.DAV.LABEL, label);
		}
	}

	/**
	 * Returns the value of a "Location" header in the response if
	 * the response status was "201 Created". This header reports
	 * the URL of a new working resource that has been created on the
	 * server following this request.
	 * @return The path of the freshly created working resource, or null if no
	 * such working resource has been created.
	 */
	public String getWorkingResource() {
		return getResponse().getHeaderValue(Header.HTTP.LOCATION);
	}

	/**
	 * Returns the value of an optional "Activity" header in the response if
	 * the response status was "201 Created". This header reports
	 * the URL of a new activity that has been created on the server following
	 * this request.
	 * <p>
	 * <b>Note</b><br/>
	 * This method is based on an enhancement to the DeltaV standard and may
	 * not work on all compliant servers.
	 * </p>
	 * @return The path of a freshly created activity, or null if no
	 * activity has been created.
	 */
	public String getActivity() {
		return (getResponse().getStatus() == Status.CREATED) ? getResponse().getHeaderValue("Activity") : null;
	}

	/**
	 * Prepares the request entity.
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @return A request entity for this CHECKOUT request.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<checkout").append(DAV.DEFAULT_XMLNS);
		body.append(">");

		if (allowForks) {
			body.append("<fork-ok/>");
		}
		if (applyToVersion) {
			body.append("<apply-to-version/>");
		}
		if (newActivity) {
			body.append("<activity-set>");
			body.append("<new/>");
			body.append("</activity-set>");
		} else if (activities != null) {
			body.append("<activity-set>");
			for (int i = 0; i < activities.size(); ++i) {
				body.append("<href>");
				body.append(Encoder.encodeXml((String) activities.get(i)));
				body.append("</href>");
			}
			body.append("</activity-set>");
		}
		if (unreserved && (newActivity || activities != null)) {
			body.append("<unreserved/>");
		}
		body.append("</checkout>");
		setRequestEntity(body);
		return body;
	}
	
	public String getCheckedoutVersionURL() {
		IResponseEntity entity = getResponse().getEntity();
		CheckoutResponseEntity coEntity = (entity != null) ? CheckoutResponseEntity.valueOf(entity) : null;
		return (coEntity != null) ? coEntity.getCheckedoutVersionURL() : null;
	}
	
	protected SAXResponseEntity createResponseEntity(String path,
			IResponse response) {
		SAXResponseEntity entity = super.createResponseEntity(path, response);
		if (entity == null && response.isContentXML() && response.getStatus() == Status.OK) {
			entity = new CheckoutResponseEntity(response);
		}
		return entity;		
	}	
}
