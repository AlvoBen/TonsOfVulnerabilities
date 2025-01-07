package com.tssap.dtr.client.lib.protocol.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.IResponseStream;

/**
 * A wrapper for socket input streams providing transparent access
 * to chunked and unchunked data streams.
 * <p>The behavior of this stream is similar to java.io.BufferedInputStream
 * but with some extensions. For example the whole content of the stream
 * can be written directly to either a StringBuffer or another
 * stream without additional buffering.</p>
 */
public class ChunkedInputStream extends InputStream implements IResponseStream {

	/** The default buffer size for this stream. The value is set to four
	 * times the Maximum Segment Size for Ethernet connections, i.e. 5840 bytes */
	public static final int DEFAULT_SIZE = ChunkedOutputStream.DEFAULT_MSS_ETHERNET * 4;

	/**
	 * The internal buffer array where the data is stored.
	 * When necessary, it may be replaced by another array of a different size.
	 */
	protected byte[] buf;

	/**
	 * The index one greater than the index of the last valid byte in the buffer.
	 * This value is always in the range 0 through buf.length; elements buf[0]
	 * through buf[count-1] contain buffered input data obtained from the
	 * underlying input stream.
	 */
	protected int count = 0;

	/**
	 * The current position in the buffer.
	 * This is the index of the next character to be read from the buf array.
	 * This value is always in the range 0 through count. If it is less than count,
	 * then buf[pos] is the next byte to be supplied as input; if it is equal to count,
	 * then the next read or skip operation will require more bytes to be read from the
	 * contained input stream
	 */
	protected int pos = 0;

	/** The underlying input stream */
	private InputStream in;

	/** The socket that manages in. */
	private Socket socket;

	/** True, if the stream reads a message body and a client is only allowed to read
	 * till the end of the body. */
	private boolean limited = false;

	/** The length of the response body, or -1 if the length is not defined */ 
	private long limit = -1L;

	/** The number of bytes till end of body is reached */
	private long toRead = -1L;
	
	/** True, if chunking is enabled */
	private boolean chunking = false;

	/** The number of bytes to read from the current chunk
	* -1 indicated that no next chunk is available. */
	private int chunkCount = -1;

	/** common trace location */
	private static final Location TRACE = Location.getLocation(ChunkedInputStream.class);
	
	/** 
	 * The default location for wire traces.
	 * Maps to <code>Location.getLocation(ChunkedInputStream.class)</code>
	 */
	public static final Location DEFAULT_WIRE_TRACE_LOCATION = TRACE;		
	
	
	/** wire trace location */
	private Location WIRE_TRACE_LOCATION = DEFAULT_WIRE_TRACE_LOCATION;

	/** True, if wire tracing is enabled */
	private boolean wireTraceEnabled = false;
	

	/**
	 * Creates a ChunkedInputStream as wrapper for a socket's input stream.
	 * The stream uses a default buffer size equal to <code>DEFAULT_SIZE</code>.
	 * @param socket the socket associated with this stream.
	 * @throws IOException - if an I/O error occurs.
	 */
	public ChunkedInputStream(Socket socket) throws IOException {
		this(socket, DEFAULT_SIZE);
	}

	/**
	 * Creates a ChunkedInputStream as wrapper for a socket's input stream.
	 * An internal buffer of the specified size is created.
	 * @param socket the socket associated with this stream.
	 * @param size the buffer size.
	 * @throws IOException  if an I/O error occurs.
	 */
	public ChunkedInputStream(Socket socket, int size) throws IOException {
		this.socket = socket;
		in = socket.getInputStream();
		buf = new byte[size];
	}
	
	/**
	 * Creates a ChunkedInputStream as wrapper for an ordinary input stream
	 * An internal buffer of the specified size is created.
	 * Note, this method is intended for testing purposes only.
	 * @param in  the underlying input stream
	 * @param size the buffer size.
	 * @throws IOException  if an I/O error occurs.
	 */
	public ChunkedInputStream(InputStream in, int size) throws IOException {
		this.in = in; 
		buf = new byte[size];
	}	

	/**
	 * Converts this stream to an ordinary input stream. 
	 * @return this
	 */	
	public InputStream asStream() {
		return this;
	}

	/**
	 * Returns the number of bytes that can be read from this input stream
	 * without blocking.
	 * @return The number of bytes that can be read from this
	 * input stream without blocking.
	 * @throws IOException  if an I/O error occurs.
	 * @see InputStream#available()
	 */
	public int available() throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");
		}
		return in.available() + count - pos;
	}

	/**
	 * Reads the next byte of data from the input stream.
	 * The value byte is returned as an int in the range 0 to 255.
	 * If no byte is available because the end of the stream has been reached,
	 * the value -1 is returned.<br/>
	 * The method blocks according to the timeout specified for the
	* underlying socket.
	 * @return The next byte of data, or -1 if the end of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 * @see InputStream#read()
	 */
	public int read() throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");		
		}
		
		if (pos >= count) {
			fill();
		}
		if (pos >= count) {
			return -1;
		}
		return buf[pos++] & 0xff;
	}

	/**
	 * Reads up to len bytes of data from the input stream into an array of bytes.
	 * An attempt is made to read as many as len bytes, but a smaller number may be read,
	 * possibly zero. The number of bytes actually read is returned as an integer.
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param b  the buffer into which the data is read.
	 * @param off  the start offset in array b at which the data is written.
	 * @param len  the maximum number of bytes to read.
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 * @see InputStream#read(byte[],int,int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");
		}

		int read = readAvailable(b, off, len);
		if (read <= 0) {
			return read;
		}
		while (read < len && in.available() > 0) {
			int cnt = readAvailable(b, off + read, len - read);
			if (cnt <= 0) {
				break;
			}
			read += cnt;
		}

		if (read > 0 && TRACE.beDebug()) {
			TRACE.debugT(
				"read(byte[],int,int)", 
				"{0} bytes read",
				new Object[]{Integer.toString(read)}
			);
		}
		return read;
	}

	/**
	 * Appends the current content of the stream to the specified string buffer.
	 * An attempt is made to read as many as bytes from the content stream as
	 * possible, but mutliple calls to this method may be necessary to read
	 * out the content completely. The number of bytes actually read is returned
	 * as an integer. The parameter encoding is used to convert bytes from the underlying input
	 * stream to characters. The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param b  the destination string buffer.
	 * @param enc  the encoding to use (for the set of valid encodings see Java specification).
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 * @throws UnsupportedEncodingException  if the specified encoding is not supported.
	 */
	public int read(StringBuffer b, String enc) throws IOException, UnsupportedEncodingException {
		if (in == null) {
			throw new IOException("stream is closed");
		}
		if (!limited) {
			return -1;
		}

		int read = readAvailable(b, enc);
		if (read <= 0) {
			return read;
		}
		while (in.available() > 0) {
			int cnt = readAvailable(b, enc);
			if (cnt <= 0) {
				break;
			}
			read += cnt;
		}

		if (read > 0 && TRACE.beDebug()) {
			TRACE.debugT(
				"read(StringBuffer,String)", 
				"{0} bytes read",
				new Object[]{Integer.toString(read)}
			);
		}
		return read;
	}

	/**
	 * Writes the current content of the stream to the destination stream.
	 * An attempt is made to read as many as bytes from the content stream as
	 * possible, but mutliple calls to this method may be necessary to read
	 * out the content completely. The number of bytes actually read is returned
	 * as an integer. The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param destination  an output stream to which the content of this stream
	 * should be copied.
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 */
	public int read(OutputStream destination) throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");
		}
		if (!limited) {
			return -1;
		}

		int read = readAvailable(destination);
		if (read <= 0) {
			return read;
		}
		while (in.available() > 0) {
			int cnt = readAvailable(destination);
			if (cnt <= 0) {
				break;
			}
			read += cnt;
		}

		if (read > 0 && TRACE.beDebug()) {
			TRACE.debugT(
				"read(OutputStream)", 
				"{0} bytes read",
				new Object[]{Integer.toString(read)}
			);
		}
		return read;
	}

	/**
	 * Reads a single line unbuffered from the stream.
	 * Both "\n" and "\r\n" are recognized as line end.
	 * The method optionally may skip empty lines (otherwise empty line are reported as "\r\n")
	 * and leading and trailing whitespace. This method assumes ASCII encoding of the input stream.
	 * The method blocks according to the timeout specified for the
	 * underlying socket. End of stream is also interpreted as line end.
	 * @param skipEmptyLines  if true, line consisting only of line-end markers are ignored.
	 * @param skipWhitespace  if true, whitespace is trimmed off the result.
	 * @throws IOException  if an I/O error occurs. 
	 */
	public String readLine(boolean skipEmptyLines, boolean skipWhitespace) throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");
		}

		StringBuffer b = new StringBuffer();
		String result = null;
		boolean done = false;
		while (!done) {
			int next = in.read();
			switch (next) {
				case -1 :
					if (skipWhitespace) {
						result = b.toString().trim();
					} else {
						result = b.toString();
					}
					done = true;
					break;			
				case '\n' :
					if (b.length() == 0) {
						if (!skipEmptyLines) {
							result = "\r\n";
							done = true;
						}
						break;
					} 
					if (skipWhitespace) {
						result = b.toString().trim();
					} else {
						result = b.toString();
					}
					done = true;
					break;
				case '\r' :
					break;
				default :
					b.append((char) next);
			}
		}
		if (result == null) {
			throw new IOException("unexpected end of stream");
		}
		if (wireTraceEnabled && WIRE_TRACE_LOCATION.beDebug()) {
			WIRE_TRACE_LOCATION.debugT("readLine(boolean,boolean)", result);
		}
		return result;
	}

	/**
	 * Skips n bytes from the stream.
	 * Returns the actual number of skipped
	 * bytes, or -1 if the end of the stream already was reached.
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param n  the number of bytes to be skipped.
	 * @return The actual number of bytes skipped.
	 * @throws IOException  if an I/O error occurs.
	 * @see InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");
		}
		
		long skipped = skipAvailable(n);
		if (skipped <= 0) {
			return skipped;
		}
		while (skipped < n && in.available() > 0) {
			long cnt = skipAvailable(n);
			if (cnt <= 0) {
				break;
			}
			skipped += cnt;
		}
		if (TRACE.beDebug()) {
			TRACE.debugT(
				"skip(long)", 
				"{0} bytes skipped",
				new Object[]{Long.toString(skipped)}
			);
		}
		return skipped;
	}

	/**
	 * Skips pending content.
	 * @throws IOException  if an I/O error occurs.
	 */
	public void skipContent() throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");
		}
		
		if (!limited) {
			return;
		}
		while (skipAvailable() > 0) {
		}
	}

	/**
	 * Closes this output stream.
	 * Skips remaining stream content. 
	 * IMPORTANT NOTE: applications should not call this method directly 
	 * since the lifetime of the socket input stream is controlled
	 * by the corresponding <code>Connection</code>. In order to ensure
	 * this behavior this method only skips remaining content in the
	 * stream but does not really close the stream. If a read limit has
	 * been set or chunked mode has been enabled before any further read
	 * operation on that stream will return -1 to indicate end of stream
	 * reached. However, after disabling limit or chunked mode, respectively,
	 * the stream may be used for further input operations.  
	 * @throws IOException   if an I/O error occurs.
	 */
	public void close() throws IOException {
		if ( in != null ) {
			skipContent();
		}
	}
	
	/**
	 * Closes the stream and shuts the underlying socket input stream
	 * down. Any further read operation on this stream will be rejected
	 * with an <code>IOException</code>.
	 */
	public void shutdown() {
		try {
			if (socket != null) {
				socket.shutdownInput();
			}
		} catch (Exception ex) {
			// if already shutdown, i/o problem or the operation
			// is unsupported (SSLSocket!) leave it to the 
			// garbage collector
			TRACE.catching("shutdown()", ex);			
		} finally {
			TRACE.debugT("shutdown()", "socket input stream [shutdown]");
			in = null;
			buf = null;
		}			
	}	
	
	/**
	 * Releases the stream for further use. Skips pending stream content and
	 * disables limits, chunking and wire traces.
	 * @throws IOException   if an I/O error occurs.
	 */
	public void release() throws IOException {
		if (in == null) {
			throw new IOException("stream is closed");
		}
		close();
		disableLimit();	
		enableChunking(false);			
		disableWireTrace();
	}


	/**
	 * Enables or disables chunking mode.
	 * Limit is disabled before switching chunking on.
	 * @param enable   if true, chunking is to be enabled.
	 */
	public void enableChunking(boolean enable) {
		chunking = enable;
		chunkCount = 0;
		toRead = -1L;
		limited = enable;
	}

	/**
	 * Checks whether the stream currently is in chunking mode.
	 * @return True, if chunking is enabled.
	 */
	public boolean chunking() {
		return chunking;
	}

	/**
	 * Enables a read limit.
	 * Chunking is disabled before setting the limit.
	 * @param limit  the length of the content.
	 */
	public void enableLimit(long limit) {
		this.limit = limit;
		this.toRead = limit;		
		chunking = false;
		limited = true;
	}

	/**
	 * Disables the read limit.
	 */
	public void disableLimit() {
		this.toRead = -1L;
		limited = false;				
	}


	/**
	 * Enables the "wire" trace for this stream. 
	 * The trace is written to the default location for this class,
	 * or to the location set in a previous call to
	 * <code>enableWireTrace(Location)</code>.
	 * Note, the wire trace becomes only active if the
	 * category of the location is DEBUG.
	 */	
	public void enableWireTrace() {
		wireTraceEnabled = true;
	}
	
	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the given location. The constant
	 * <code>DEFAULT_WIRE_TRACE_LOCATION</code> may be used
	 * to reset the trace location to the default value.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	public void enableWireTrace(Location location) {
		wireTraceEnabled = true;
		if (location != null) {
			WIRE_TRACE_LOCATION = location;
		}
	}

	/**
	 * Disables the "wire" trace for this stream.
	 */
	public void disableWireTrace() {
		wireTraceEnabled = false;		
	}
	
	/**
	 * Checks whether the wire trace is enabled
	 * @return  true, if the trace is enabled
	 */
	public boolean isWireTraceEnabled() {
		return wireTraceEnabled;
	}
	

	/**
	 * Reads the length attribute of the next chunk. Ignores chunk parameters.
	 * @return The length of the next chunk in bytes.
	 * @throws IOException  if an I/O error occurs.
	 * @throws NumberFormatException  if the chunk size is not convertible to integer.
	 */
	private int readChunkLength() throws IOException, NumberFormatException {
		int result = -1;
		String s = readLine(true, true);
		int chunkExt = s.indexOf(';');
		if (chunkExt != -1) {
			result = Integer.parseInt(s.substring(0, chunkExt), 16);
		} else {
			result = Integer.parseInt(s, 16);
		}
		return result;
	}

	/**
	 * Fills the buffer.
	 * @throws IOException  if an I/O error occurs, the stream ended
	* in the middle of a chunk, a chunk was not well-formatted or the read
	* limit could not be fulfilled.
	 */
	private void fill() throws IOException {
		if (chunking) {
			fillChunked();
		} else {
			fillUnchunked();
		}
	}

	/**
	 * Reads another chunk from the input stream. if chunking is enabled,
	 * the method reads the chunk length. if the chunk length is lower than
	 * the size of the buffer the chunk is read at once, otherwise only a portion
	 * of the chunk is read to the buffer. if chunking is disabled the method
	 * calls fillUnchunked.
	 * @throws IOException  if an I/O error occurs, the stream ended
	 * in the middle of a chunk, a chunk was not well-formatted or the read
	 * limit could not be fulfilled.
	 */
	private void fillChunked() throws IOException {
		pos = 0;
		count = 0;
		if (chunkCount < 0) {
			return;
		}
		if (chunkCount == 0) {
			try {
				chunkCount = readChunkLength();
			} catch (NumberFormatException e) {
				TRACE.catching("fillChunked()", e);
				throw new IOException("Malformed chunk size [not a number]");
			}
		}
		if (chunkCount > 0) {
			int n = (chunkCount < buf.length) ? chunkCount : buf.length;
			int cnt = in.read(buf, 0, n);
			if (cnt > 0) {
				count = cnt;
				chunkCount -= cnt;
				if (wireTraceEnabled && WIRE_TRACE_LOCATION.beDebug()) {
					WIRE_TRACE_LOCATION.debugT("fillChunked()", new String(buf, 0, cnt));
				}
			} else {
				throw new IOException("Unexpected end of chunked body.");					
			}
		} else {
			chunkCount = -1;
		}
	}

	/**
	* Fills the buffer assuming that the data stream is not chunked.
	* Asssumes that the buffer has been read out (i.e. pos>=count).
	* The condition <code>count==pos</code> indicates end of stream.
	*
	* @throws IOException  if an I/O error occurs or the stream ended
	* before an applied read limit could be fulfilled.
	*/
	private void fillUnchunked() throws IOException {
		pos = 0;
		count = 0;
		if (limited) {
			if (toRead <= 0) {
				return;
			}
			int n = (toRead < buf.length) ? (int) toRead : buf.length;
			int cnt = in.read(buf, 0, n);
			if (cnt > 0) {
				count = cnt;
				toRead -= cnt;
				if (wireTraceEnabled && WIRE_TRACE_LOCATION.beDebug()) {
					WIRE_TRACE_LOCATION.debugT("fillUnchunked()", new String(buf, 0, cnt));
				}
			} else {
				throw new IOException("Unexpected end of response. Mismatch between 'Content-Length'" +					"header returned by server and actual length of body [retrieved: " + 
					Long.toString(limit-toRead) + ", expected: " + Long.toString(limit) + "]");
			}
		} else {
			int cnt = in.read(buf, 0, buf.length);
			if (cnt > 0) {
				count = cnt;
				if (wireTraceEnabled && WIRE_TRACE_LOCATION.beDebug()) {
					WIRE_TRACE_LOCATION.debugT("fillUnchunked()", new String(buf, 0, cnt));
				}
			}
		}
	}

	/**
	 * Reads the currently available stream content into the specified buffer.
	 * Fills the stream buffer before if necessary.
	 * @param b  the buffer into which the data is read.
		  * @param off  the start offset in array b at which the data is written.
		  * @param len  the maximum number of bytes to read.
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
		  * @throws IOException  if an I/O error occurs.
	 */
	private int readAvailable(byte[] b, int off, int len) throws IOException {
		int avail = count - pos;
		if (avail <= 0) {
			if (!limited && len >= buf.length) {
				return in.read(b, off, len);
			}
			fill();
			avail = count - pos;
			if (avail <= 0) {
				return -1;
			}
		}
		int cnt = (avail < len) ? avail : len;
		System.arraycopy(buf, pos, b, off, cnt);
		pos += cnt;
		return cnt;
	}

	/**
	 * Appends the currently available stream content to the specified StringBuffer.
	 * Fills the stream buffer before if necessary.
	 * @param b  the destination string buffer.
	 * @param enc  the encoding to use (for the set of valid encodings see Java specification).
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 * @throws UnsupportedEncodingException  if the specified encoding is not supported.
	 */
	private int readAvailable(StringBuffer b, String enc) throws IOException, UnsupportedEncodingException {
		int avail = count - pos;
		if (avail <= 0) {
			fill();
			avail = count - pos;
			if (avail <= 0) {
				return -1;
			}
		}
		b.append(new String(buf, 0, avail, enc));
		pos += avail;
		return avail;
	}

	/**
	 * Writes the currently available stream content into the specified output stream.
	 * Fills the stream buffer before if necessary.
	 * @param destination  an output stream to which the content of this stream
	 * should be copied.
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 */
	private int readAvailable(OutputStream destination) throws IOException {
		int avail = count - pos;
		if (avail <= 0) {
			fill();
			avail = count - pos;
			if (avail <= 0) {
				return -1;
			}
		}
		destination.write(buf, 0, avail);
		pos += avail;
		return avail;
	}

	/**
	 * Skips the currently available stream content.
	 * Fills the stream buffer before if necessary.
	 * @throws IOException  if an I/O error occurs.
	 */
	private long skipAvailable() throws IOException {
		long avail = count - pos;
		if (avail <= 0) {
			fill();
			avail = count - pos;
			if (avail <= 0) {
				return -1;
			}
		}
		pos += avail;
		return avail;
	}

	/**
	 * Skips up to n bytes from the currently available stream.
	 * Fills the stream buffer before if necessary.
	 * @param n  the number of bytes to be skipped.
	 * @return The actual number of bytes skipped.
	 * @throws IOException  if an I/O error occurs.
	 */
	private long skipAvailable(long n) throws IOException {
		long avail = count - pos;
		if (avail <= 0) {
			fill();
			avail = count - pos;
			if (avail <= 0) {
				return -1;
			}
		}
		long cnt = (avail < n) ? avail : n;
		pos += cnt;
		return cnt;
	}


}
