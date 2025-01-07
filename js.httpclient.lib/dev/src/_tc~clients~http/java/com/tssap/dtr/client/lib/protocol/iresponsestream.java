package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.sap.tc.logging.Location;

/**
 * This interface represents a generic input stream for reading HTTP responses.
 */
public interface IResponseStream {

	/**
	* Returns the number of bytes that can be read from this input stream
	* without blocking.
	* @return The number of bytes that can be read from this
	* input stream without blocking.
	* @throws IOException   if an I/O error occurs.
	* @see java.io.InputStream#available()
	*/
	int available() throws IOException;

	/**
	 * Reads the next byte of data from the input stream.
	 * The value byte is returned as an int in the range 0 to 255.
	 * If no byte is available because the end of the stream has been reached,
	 * the value -1 is returned.<br/>
	 * If this stream is used as a wrapper for
	 * a socket input stream the read method blocks according to the
	 * timeout specified by the socket.
	 * @return The next byte of data, or -1 if the end of the stream is reached.
	 * @throws IOException   if an I/O error occurs.
	 * @see java.io.InputStream#read()
	 */
	int read() throws IOException;

	/**
	 * Reads up to <code>len</code> bytes of data from the input stream 
	 * into an array of bytes.
	 * An attempt is made to read as many as <code>len</code> bytes, but a smaller 
	 * number may be read. The number of bytes actually read is returned as an 
	 * integer.<br/>
	 * If this stream is used as a wrapper for
	 * a socket input stream the read method blocks according to the
	 * timeout specified by the socket.
	 * @param b  the buffer into which the data is read.
	 * @param off  the start offset in array b at which the data is written.
	 * @param len  the maximum number of bytes to read.
	 * @throws IOException   if an I/O error occurs.
	 * @see java.io.InputStream#read(byte[],int,int)
	 */
	int read(byte[] b, int off, int len) throws IOException;

	/**
	 * Appends the current content of the stream to the specified string buffer.
	 * An attempt is made to read as many bytes from the content stream as
	 * possible, but multiple calls to this method may be 
	 * necessary to read out the content completely. The number of bytes actually 
	 * read is returned as an integer.<br/>  
	 * The parameter <code>enc</code> determines how the binary data read from the 
	 * underlying input stream is converted into characters.<br/>
	 * The method blocks according to the timeout specified for the underlying socket.
	 * @param b  the destination string buffer.
	 * @param enc  the encoding to use (for the set of valid encodings see Java specification).
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 * @throws UnsupportedEncodingException  if the specified encoding is not supported.
	 */		
	int read(StringBuffer b, String enc) throws IOException, UnsupportedEncodingException;

	/**
	 * Writes the current content of the stream to the given destination stream.
	 * An attempt is made to read as many bytes from the content stream as
	 * possible, but multiple calls to this method may be necessary to read
	 * out the content completely. The number of bytes actually read is returned
	 * as an integer.<br/>
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param destination  an output stream to which the content of this stream
	 * should be copied.
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 */
	int read(OutputStream destination) throws IOException;

	/**
	 * Skips n bytes from the stream.
	 * Returns the actual number of skipped
	 * bytes, or -1 if the end of the stream already was reached.<br/>
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param n  the number of bytes to be skipped.
	 * @return The actual number of bytes skipped.
	 * @throws IOException  if an I/O error occurs.
	 * @see java.io.InputStream#skip(long)
	 */		
	long skip(long n) throws IOException;

	/**
	 * Skips pending content.
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @throws IOException  if an I/O error occurs.
	 */
	void skipContent() throws IOException;

	/**
	* Closes this stream. All consecutive calls to any of the <code>read</code> or 
	* <code>skip</code> methods will return -1 afterwards.<br/>
	* The behavior of this method depends on whether the stream actually has
	* been created with reference to a socket. In this case the underlying
	* socket input stream is managed by the socket and this method only skips 
	* pending content. If the underlying input stream is not a socket's
	* input stream it is closed by this method.<br/> 
	* @throws IOException    if an I/O error occurs.
	* @see java.io.InputStream#close()
	*/
	void close() throws IOException;

	/**
	 * Releases the stream for further use. 
	 * Skips pending stream content and
	 * disables limits, chunking, compression, hash calculation etc. After
	 * calling this methods the response stream behaves again like an ordinary
	 * buffered input stream.
	 * @throws IOException   if an I/O error occurs.
	 */
	void release() throws IOException;

	/**
	 * Converts this response stream to an ordinary input stream. 
	 * @return this response stream
	 */
	InputStream asStream();
	
	
	/**
	 * Enables the "wire" trace for this stream. 
	 * The trace is written to the default location for this class.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */	
	void enableWireTrace();
	
	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the given location.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	void enableWireTrace(Location location);

	/**
	 * Disables the "wire" trace for this stream.
	 */
	void disableWireTrace();
	
	/**
	 * Checks whether the wire trace is enabled
	 * @return  true, if the trace is enabled
	 */
	boolean isWireTraceEnabled();	
}