package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;

/**
 * This interface represents a parser for
 * processing of HTTP response bodies.
 */
public interface IResponseParser {

	/**
	 * Reads and parses the content of the specified response
	 * and returns a response entity that provides access to
	 * the relevant entity parameters (like content length and
	 * type) and probably to the content itself.
	 * @param path the URL of a resource to which the request was applied.	
	 * @param response  the response object from which the body
	 * is to be retrieved and parsed.
	 * @return A response entity that provides access to the parsed
	 * response body.
	 * @throws HTTPException if the parsing of the response body failed.
	 * @throws IOException if an i/o problem occured
	 * @see RequestBase#setResponseParser(String,IResponseParser)
	 * @see RequestBase#perform(IConnection)
	 */
	IResponseEntity parse(String path, IResponse response) 
	throws HTTPException, IOException;

}
