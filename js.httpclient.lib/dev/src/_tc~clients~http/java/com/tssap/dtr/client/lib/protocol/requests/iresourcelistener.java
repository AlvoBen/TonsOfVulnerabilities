package com.tssap.dtr.client.lib.protocol.requests;

import java.io.IOException;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseStream;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;

/**
 * Interface representing a listener of a multistatus response.
 */
public interface IResourceListener {

	/**
	 * This notifier is called before the response parser tries to
	 * read the first &lt;response&gt; element in a MultiStatusResponse or 
	 * ListCollectionReport. This allows for example to extract headers
	 * from the response that have influence on the evaluation of the
	 * individual resource elements.
	 * @param response  the response to parse
	 */
	void notifyResponse(IResponse response);

	/**
	 * This notifier is called when the response parser has completed a
	 * &lt;response&gt; element in a MultiStatusResponse or ListCollectionReport.
	 * The listener may decide whether the MultiStatusEntity should append
	 * the resource to its internal list of resources or not. For large
	 * responses with many entries the listener should evaluate the resource
	 * by itself and return false to save memory.
	 * @param resource  the ResourceElement corresponding to the last
	 * completed &lt;response&gt; tag.
	 * @return if true, the resource should be stored in the MultiStatusEntity,
	 * otherwise the resource is dropped.
	 */
	boolean notifyResource(ResourceElement resource, IResponseStream content)
	throws HTTPException, IOException;

}
