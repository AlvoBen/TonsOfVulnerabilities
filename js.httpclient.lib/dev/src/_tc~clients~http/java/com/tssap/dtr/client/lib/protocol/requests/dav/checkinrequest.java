package com.tssap.dtr.client.lib.protocol.requests.dav;

import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.PropertyElement;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.*;

/**
 * This request class implements the DeltaV "CHECKIN" request.
 */
public class CheckinRequest extends MultiStatusRequest {

	// variables defining the request parameters
	private boolean keepCheckedOut = false;
	private boolean allowForks = false;

	/**
	 * Creates a CHECKIN request for the specified resource or activity.
	 * @param path  the repository path of a checked out VCR, version or
	 * an activity.
	 */
	public CheckinRequest(String path) {
		super("CHECKIN", path);
	}

	/**
	 * Adds a &lt;DAV:fork-ok/&gt; option to the request.
	 * @param allowForks  if true, the option is added to the request.
	 */
	public void setAllowForks(boolean allowForks) {
		this.allowForks = allowForks;
	}

	/**
	 * Adds a &lt;DAV:keep-checked-out/&gt; option to the request.
	 * @param keepCheckedOut  if true, the option is added to the request
	 */
	public void setKeepCheckedOut(boolean keepCheckedOut) {
		this.keepCheckedOut = keepCheckedOut;
	}

	/**
	 * Returns the number of resource elements stored in this entity.
	 * @return The number of resources.
	 */
	public int countResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.countResources() : 0;
	}

	/**
	 * Returns the resource specified by index. The resources are provided
	 * in the order they occured in the multistatus response.
	 * @return The resource element that corresponds to the i-th <DAV:response>
	 * entry in the multistatus response, or null if no resource was
	 * reveived or the index is out of bounds.
	 */
	public ResourceElement getResource(int i) {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.getResource(i) : null;
	}

	/**
	 * Returns an enumeration of ResourceElement objects that were retrieved
	 * from the multistatus response. Each element contains the URL of
	 * the corresponding resource, the set of retrieved properties and optionally
	 * a human readable description of the response state.
	 */
	public Iterator getResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.getResources() : null;
	}

	/**
	 * Returns the value of a "Location" header in the response if
	 * the response status was "201 Created". This header reports
	 * the URL of a new version resource that has been created on the
	 * server following this request. Note if the checkin was applied
	 * to a whole activity this method will always return null. In this
	 * case the names of the new versions is reported in the response
	 * body.
	 * @return The value of the "Location" header in the response, or null.
	 */
	public String getNewVersion() {
		return (getResponse().getStatus() == Status.CREATED) ? getResponse().getHeaderValue("Location") : null;
	}

	/**
	 * Returns the value of the &t;DAV:checked-in&gt; property of the specified
	 * i-th resource element.
	 * The value of this property gives the URL of the new version of the resource
	 * that has been created by checking in an activity the resource belongs to.
	 * <p>
	 * <b>Note</b><br/>
	 * This method is based on an enhancement to the DeltaV standard and may
	 * not work on all compliant servers.
	 * </p>
	 * @param i the index of the corresponding <response>
	 * @return The value of the &t;DAV:checked-in&gt; property of the specified
	 * i-th resource element, or null.
	 */
	public String getNewVersion(int i) {
		String version = null;
		if (getResponse().getEntityType() == "MultiStatusEntity") {
			ResourceElement res = ((MultiStatusEntity) getResponse().getEntity()).getResource(i);
			if (res != null) {
				PropertyElement checkinProperty = res.getProperty("checked-in");
				if (checkinProperty != null) {
					version = checkinProperty.getValue();
				}
			}
		}
		return version;
	}

	/**
	 * Returns the values of the &t;DAV:checked-in&gt; properties of all
	 * resource elements.
	 * <p>
	 * <b>Note</b><br/>
	 * This method is based on an enhancement to the DeltaV standard and may
	 * not work on all compliant servers.
	 * </p>
	 */
	public Iterator getNewVersions() {
		return new Iterator() {
			private Iterator resources = getResources();
			public boolean hasNext() {
				return (resources != null) ? resources.hasNext() : false;
			}
			public Object next() {
				Object next = null;
				if (resources != null) {
					ResourceElement res = (ResourceElement) resources.next();
					PropertyElement checkinProperty = res.getProperty("checked-in");
					if (checkinProperty != null) {
						next = checkinProperty.getValue();
					}
				}
				return next;
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Prepares the request entity.
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @return A request entity for this CHECKIN request.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		if (allowForks || keepCheckedOut) {
			body = new StringEntity("text/xml", "UTF-8");
			body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
			body.append("<checkin").append(DAV.DEFAULT_XMLNS).append(">");
			if (allowForks) {
				body.append("<fork-ok/>");
			}
			if (keepCheckedOut) {
				body.append("<keep-checked-out/>");
			}
			body.append("</checkin>");
		}
		setRequestEntity(body);
		return body;
	}

}
