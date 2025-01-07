package com.tssap.dtr.client.lib.protocol.requests;

import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.SAXResponseEntity;

/**
 * Base class for requests that return a WebDAV MultiStatus response.
 */
public class MultiStatusRequest extends XMLRequest {

	/** An optional event listener for multistatus responses */
	private IResourceListener resourceListener;

	/**
	 * Creates a request with specified method and relative path.
	 * @param method the protocol command, e.g. "GET", "CHECKIN" etc.
	 * @param path the URL of a resource to which the request applies.
	 */
	public MultiStatusRequest(String method, String path) {
		super(method, path);
	}

	/**
	 * Creates a request with specified method and relative path and defines
	 * a request entity.
	 * @param method the protocol command, e.g. "GET", "CHECKIN" etc.
	 * @param path the URL of a resource to which the request applies.
	 * @param entity a request entity providing the request body and some
	 * parameters like content length and type.
	 */
	public MultiStatusRequest(String method, String path, IRequestEntity entity) {
		super(method, path, entity);
	}
	

	/**
	 * Defines a listener that should be called during parsing of a multistatus
	 * response to evaluate resource elements.
	 * @param listener  the listener to install
	 */
	public void setMultiStatusResourceListener(IResourceListener listener) {
		resourceListener = listener;
	}


	
	protected SAXResponseEntity createResponseEntity(String path, IResponse response) {
		SAXResponseEntity entity = super.createResponseEntity(path, response);
		if (resourceListener != null) {
			((MultiStatusEntity) entity).setResourceListener(resourceListener);
		}					
		return entity;
	}	
	
	
	
}

