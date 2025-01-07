package com.tssap.dtr.client.lib.protocol.requests.http;

import java.io.File;
import java.io.OutputStream;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseParser;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.FileEntity;
import com.tssap.dtr.client.lib.protocol.entities.StreamEntity;
import com.tssap.dtr.client.lib.protocol.requests.RequestBase;

/**
 * This request class implements the standard HTTP "GET" request.
 */
public class GetRequest extends RequestBase implements IResponseParser {

	private File file;
	private String label;
	private OutputStream destination;

	/**
	 * Creates a GET request for the specified resource. The content of this
	 * resource is delivered in a ByteArrayEntity.
	 * @param path  the path of a resource to retrieve.
	 */
	public GetRequest(String path) {
		super("GET", path);
		enableResponseEntityLog(false);
	}

	/**
	 * Creates a GET request for the specified resource and defines a file
	 * that should be used to store the content.
	 * @param path  the path of a resource to retrieve.
	 * @param file  a file to which the content of the resource
	 * should be written.
	 */
	public GetRequest(String path, File file) {
		super("GET", path);
		this.file = file;
		setDefaultParser(this);
		enableResponseEntityLog(false);		
	}

	/**
	 * Creates a GET request for the specified resource. The content of that
	 * resources is written to the given stream.
	 * @param path  the path of a resource to retrieve.
	 * @param destination  an output stream to which the content of the resource
	 * should be written.
	 */
	public GetRequest(String path, OutputStream destination) {
		super("GET", path);
		this.destination = destination;
		setDefaultParser(this);
		enableResponseEntityLog(false);		
	}

	/**
	 * Retrieve a certain version of a resource matching the given label.
	 * @param label  the label of a version.
	 */
	public void setApplyToLabel(String label) {
		this.label = label;
		if (label != null) {
			setHeader(Header.DAV.LABEL, label);
		}
	}

	/**
	 * Instantiates a response entity.
	 * If an OutputStream has been given in the constructor
	 * the response body is written directly from the response stream
	 * to this output stream. Otherwise if a File has been specified
	 * the response is stored directly in the corresponding file.
	 * <p>
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @param path  the path of the requested resource.
	 * @param response  the response to parse.
	 * @return A response entity, either a StreamEntity or a FileEntity.
	 */
	public IResponseEntity parse(String path, IResponse response) throws HTTPException {
		IResponseEntity entity = null;
		int status = response.getStatus();
		if (status == Status.OK) {
			if (destination != null) {
				entity = new StreamEntity(destination, response);
			} else if (file != null) {
				entity = new FileEntity(file, response);
			}
		}
		return entity;
	}

}
