package com.tssap.dtr.client.lib.protocol.requests.http;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseParser;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.FileEntity;
import com.tssap.dtr.client.lib.protocol.entities.StreamEntity;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.RequestBase;
import com.tssap.dtr.client.lib.protocol.util.Encoder;
import com.tssap.dtr.client.lib.protocol.util.Pair;

/**
 * This request class implements the standard HTTP "POST" request.
 */
public class PostRequest extends RequestBase implements IResponseParser {

	private ArrayList params;
	private File file;
	private OutputStream destination;

	/**
	 * Creates a POST request for the specified resource. The content
	 * (if any) of this resource is delivered in a ByteArrayEntity.
	 * @param path  the path of the resource to post to.
	 */
	public PostRequest(String path) {
		super("POST", path);
		enableResponseEntityLog(false);		
	}

	/**
	 * Creates a POST request for the specified resource and defines a file
	 * that should be used to store the content (if any).
	 * @param path  the path of a resource to post to.
	 * @param file  a file to which the content of the resource
	 * should be written.
	 */
	public PostRequest(String path, File file) {
		this(path);
		this.file = file;
		setDefaultParser(this);
//		enableResponseEntityLog(false);		
	}

	/**
	 * Creates a POST request for the specified resource. The content of that
	 * resources (if any) is written to the given stream.
	 * @param path  the path of a resource to post to.
	 * @param destination  an output stream to which the content of the resource
	 * should be written.
	 */
	public PostRequest(String path, OutputStream destination) {
		this(path);
		this.destination = destination;
		setDefaultParser(this);
//		enableResponseEntityLog(false);		
	}

	/**
	 * Adds a request parameter.
	 * Request parameters are name-value pairs sent in the body of the
	 * POST request. Parameters usually are used to communicate the content
	 * of HTML forms to a servlet or CGI script. The javax.servlet.ServletRequest
	 * class provides methods to read out POST parameters.
	 * @param name  the name of the pair
	 * @param value  the value of the pair
	 */
	public void addParameter(String name, String value) {
		if (params == null) {
			params = new ArrayList();
		}
		params.add(new Pair(Encoder.encodePath(name), Encoder.encodePath(value), '='));
	}

	/**
	 * Prepares the request entity.
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @return A request entity for this PROPFIND request.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		if (params != null && params.size() > 0) {
			body = new StringEntity("application/x-www-form-urlencoded", null);
			for (int i = 0; i < params.size(); ++i) {
				if (i > 0) {
					body.append("&");
				}
				body.append(params.get(i).toString());
			}
			setRequestEntity(body);
		}
		return body;
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
