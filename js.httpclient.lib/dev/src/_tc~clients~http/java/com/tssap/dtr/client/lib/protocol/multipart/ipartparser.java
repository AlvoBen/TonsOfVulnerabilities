package com.tssap.dtr.client.lib.protocol.multipart;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;

/**
 * Interfacing representing a parser for a multipart MIME message body.
 */
public interface IPartParser {
	
	/**
	 * Called by MultiPartEntity to determine whether the parser
	 * is able to deduce the length of the given part. If the parser
	 * returns true, the underlying partitioning stream does not try
	 * to determine the length of the path by searching for part
	 * boundaries. 
	 * @param part  the response part
	 * @return  true, if the part is self delimiting, i.e. the length
	 * can be determined implicitly while reading it. This is usually true
	 * for example for parts containing XML fragments.
	 */
	boolean isSelfDelimiting(IResponsePart part);
	
	/**
	 * Reads and parses the content of the specified response part
	 * and returns a response entity that provides access to
	 * the relevant entity parameters (like content length and
	 * type) and probably to the content itself.
	 * @param part  the response part to read and parse.
	 * @return A response entity that provides access to the parsed
	 * part body.
	 * @throws HTTPException if the parsing of the part failed.
	 */
	IResponseEntity parse(IResponsePart part) throws HTTPException;	
}
