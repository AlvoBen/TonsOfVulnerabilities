package com.tssap.dtr.client.lib.protocol.streams;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.IResponseStream;

/**
 * A stream for reading HTTP responses providing transparent access
 * to chunked, unchunked and compressed data streams. Optionally the
 * stream may be used to calculate a message digest of the response body.
 */
public class ResponseStream extends InputStream implements IResponseStream {

	/** Either the compressing or digest stream, otherwise equals base */
	private InputStream in;

	/** The underlying chunked input stream */
	private ChunkedInputStream base;
	
	/** The optional compressed stream associated with the underlying input stream */
	private InflaterInputStream compressedStream;

	/** The optional digest stream associated with the underlying input stream */	
	private DigestInputStream digestStream;	
	

	/** The de-compression algorithmn used for the body, either gzip or deflate */
	private String compressionAlgorithm;

	/** true, if compression is enabled */
	private boolean compressionEnabled = false;

	/** The digest algorithm used for responses */
	private String digestAlgorithm;
	
	/** true, if digest calculation is enabled */
	private boolean digestEnabled = false;
		
	/** The previously calculated digest of the response body */
	private byte[] digest;
		
	/** client trace */
	private static final Location TRACE = Location.getLocation(ResponseStream.class);
	

	/**
	 * Creates a ResponseStream for the specified socket.
	 * The stream uses a default buffer size equal to the socket's
	 * underlying receive buffer.
	 * @param socket the socket associated with this stream.
	 * @throws IOException - if an I/O error occurs.
	 */
	public ResponseStream(Socket socket) throws IOException {
		base = new ChunkedInputStream(socket);
		in = base;
	}
	
	
	/**
	 * Creates a ResponseStream for the specified socket.
	 * The stream uses a buffer of the specified size.
	 * @param socket the socket associated with this stream.
	 * @param size the buffer size of the underlying ChunkedInputStream.
	 * @throws IOException  if an I/O error occurs.
	 */
	public ResponseStream(Socket socket, int size) throws IOException {
		base = new ChunkedInputStream(socket, size);
		in = base;
	}

	
	/**
	 * Creates a ResponseStream as wrapper for an ordinary input stream.
	 * Note, this method is intended for testing purposes only.
	 * @param in  the underlying input stream
	 * @param size the buffer size.
	 * @throws IOException  if an I/O error occurs.
	 */
	public ResponseStream(InputStream in, int size) throws IOException {
		base = new ChunkedInputStream(in, size);
		this.in = base;		
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
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("available()", ex);
			throw ex;	
		}		
		return in.available();
	}

	/**
	 * Reads the next byte of data from the input stream.
	 * The value byte is returned as an int in the range 0 to 255.
	 * If no byte is available because the end of the stream has been reached,
	 * the value -1 is returned.
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @return The next byte of data, or -1 if the end of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 * @see InputStream#read()
	 */
	public int read() throws IOException {
		if (in == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("read()", ex);
			throw ex;	
		}
		return in.read();
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
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("read(byte[],int,int)", ex);
			throw ex;	
		}
		
		int read = in.read(b, off, len);			
		return read;
	}

	/**
	 * Appends the current content of the stream to the specified string buffer.
	 * An attempt is made to read as many as bytes from the content stream as
	 * possible, but multiple calls to this method may be necessary to read
	 * out the content completely. The number of bytes actually read is returned
	 * as an integer.
	 * The parameter encoding is used to convert bytes from the underlying input
	 * stream to characters.
	 * The method blocks according to the timeout specified for the
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
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("read(StringBuffer,String)", ex);
			throw ex;	
		}
				
		if (in==base) {
			return base.read(b, enc);
		} else {
			byte[] sb = new byte[16 * 1024];
			int read = in.read(sb);			
			if (read <= 0) {
				return read;
			}
			
			b.append(new String(sb, 0, read, enc));
			
			while (in.available() > 0) {
				int cnt = in.read(sb);
				if (cnt <= 0) {
					break;
				}				
				b.append(new String(sb, 0, cnt, enc));
				read += cnt;
			}
			return read;
		}
	}

	/**
	 * Writes the current content of the stream to the destination stream.
	 * An attempt is made to read as many as bytes from the content stream as
	 * possible, but mutliple calls to this method may be necessary to read
	 * out the content completely. The number of bytes actually read is returned
	 * as an integer.
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param destination  an output stream to which the content of this stream
	 * should be copied.
	 * @return The number of bytes read from the stream, or -1 if the end
	 * of the stream is reached.
	 * @throws IOException  if an I/O error occurs.
	 */
	public int read(OutputStream destination) throws IOException {
		if (in == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("read(OutputStream)", ex);
			throw ex;	
		}
				
		if (in==base) {
			return base.read(destination);
		} else {
			byte[] sb = new byte[16 * 1024];
			int read = in.read(sb);
			if (read <= 0) {
				return read;
			}

			destination.write(sb, 0, read);
			while (in.available() > 0) {
				int cnt = in.read(sb);
				if (cnt <= 0) {
					break;
				}
				destination.write(sb, 0, cnt);
				read += cnt;
			}
			return read;
		}
	}

	/**
	 * Reads a single line directly from the socket's input stream.
	 * The settings for chunking, compression and message digest calculation have
	 * no influence on this method.
	 * Both "\n" and "\r\n" are recognized as line end.
	 * The method optionally may skip empty lines (otherwise empty line are reported as "\r\n")
	 * and leading and trailing whitespace. This method assumes ASCII encoding of the input stream.
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param skipEmptyLines  if true, line consisting only of line-end markers are ignored.
	 * @param skipWhitespace  if true, whitespace is trimmed off the result.
	 * @throws IOException  if an I/O error occurs or if the end of the stream was reached before the
	 * end of the line.
	 */
	public String readLine(boolean skipEmptyLines, boolean skipWhitespace) throws IOException {		
		return base.readLine(skipEmptyLines, skipWhitespace);
	}


	/**
	 * Skips n bytes from the stream.
	 * Returns the actual number of skipped
	 * bytes, or -1 if the end of the stream already was reached.
	 * The method blocks according to the timeout specified for the
	 * underlying socket.
	 * @param n  the number of bytes to be skipped.
	 * @throws IOException  if an I/O error occurs.
	 * @see InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		if (in == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("skip(long)", ex);
			throw ex;	
		}
		return in.skip(n);
	}

	/**
	 * Skips pending content.
	 * @throws IOException  if an I/O error occurs.
	 */
	public void skipContent() throws IOException {
		if (in == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("skipContent()", ex);
			throw ex;	
		}
				
		if (in==base) {
			base.skipContent();
		} else {
			byte[] sb = new byte[512];
			try {
				while (in.read(sb) > 0) {
				}
			} catch (EOFException ex) {
				// some servers send zipped empty body => be gracefully..only write a trace				
				TRACE.catching("skipContent()", ex);
			}
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
	 * @see InputStream#close()
	 */
	public void close() throws IOException {
		if ( in != null ) {
		 	if (in == base) {
				base.close();
		 	} else {
				in.close();
				in = base;
				compressedStream = null;
				digestStream = null;
				compressionEnabled = false;
				digestEnabled = false;							
			}
		}
	}
	
	/**
	 * Closes the stream and shuts the underlying socket input stream
	 * down. Any further write operation on this stream will be rejected.
	 */
	public void shutdown() {
		try {
			base.shutdown();
		} catch (Exception ex) {
			// if already shutdown, i/o problem or the operation
			// is unsupported (SSLSocket!) leave it to the 
			// garbage collector
			TRACE.catching("shutdown()", ex);	
		} finally {			
			compressedStream = null;
			in = null;
			base = null;
		}
	}	
	
	/**
	 * Resets the stream for further use. Skips pending stream content and
	 * disables limits, chunking, compression etc.
	 * @throws IOException   if an I/O error occurs.
	 */	
	public void release() throws IOException {
		if (in == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("release()", ex);
			throw ex;
		}
		close();
		disableLimit();	
		enableChunking(false);			
		disableWireTrace();				
	}

	/**
	 * Enables or disables chunking mode.
	 * @param enable   if true, chunking is enabled.
	 */
	public void enableChunking(boolean enable) {
		base.enableChunking(enable);
	}

	/**
	 * Checks whether the stream currently is in chunking mode.
	 * @return True, if chunking is enabled.
	 */
	public boolean chunking() {
		return base.chunking();
	}

	/**
	 * Enables a read limit.
	 * If chunking is enabled the limit parameter is
	 * ignored. The stream then reads until a "final" chunk is found in the input stream.
	 * @param limit  the length of the content, or -1.
	 */
	public void enableLimit(long limit) {
		base.enableLimit(limit);
	}

	/**
	 * Disables the read limit.
	 */
	public void disableLimit() {
		base.disableLimit();
	}


	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the default location for this class.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	public void enableWireTrace() {
		if (!compressionEnabled) {
			base.enableWireTrace();
		}
	}
	
	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the given location.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	public void enableWireTrace(Location location) {
		if (!compressionEnabled) {
			base.enableWireTrace(location);
		}
	}

	/**
	 * Disables the "wire" trace for this stream.
	 */
	public void disableWireTrace() {
		base.disableWireTrace();	
	}
	
	/**
	 * Checks whether the wire trace is enabled
	 * @return  true, if the trace is enabled
	 */
	public boolean isWireTraceEnabled() {
		return base.isWireTraceEnabled();
	}	
	

	/**
	 * Returns the content encoding currently used.
	 * @return Either "gzip" or "deflate".
	 */
	public String getCompressionAlgorithm() {
		return compressionAlgorithm;
	}

	/**
	 * Sets the content encoding algorithm used for the underlying stream.
	 * Currently the encondings "gzip" and "deflate" are supported (see RFC2616).
	 * @param algorithm  either "gzip" or "deflate".
	 */
	public void setCompressionAlgorithm(String algorithm) {
		this.compressionAlgorithm = algorithm.toLowerCase();					
	}

	/**
	 * Enables content encoding for this stream. 
	 * 
	 * @throws IOException  if reading from the underlying input stream
	 * failed, e.g. because the encoding of the stream does not match the
	 * selected encoding algorithm, or the selected algorithm is not
	 * supported.
	 */
	public void enableCompression() throws IOException {
		if (in == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("skip(long)", ex);
			throw ex;	
		}
		try {			
			if (compressionAlgorithm == null || compressionAlgorithm.equals("gzip")) {
				compressedStream = new GZIPInputStream(in);
				TRACE.debugT("enableCompression()", "GZIP decoding [enabled]");					
			} else if (compressionAlgorithm.equals("deflate")) {
				compressedStream = new InflaterInputStream(in);
				TRACE.debugT("enableCompression()", "DEFLATE decoding [enabled]");				
			} else {
				IOException ex = new IOException(
					"unsupported content coding " + compressionAlgorithm
				);
				TRACE.throwing("enableCompression()", ex);					
				throw ex;
			}

			in = compressedStream;
			compressionEnabled = true;
			base.disableWireTrace();
			
		} catch (EOFException ex) {
			// may occur if server sends only final chunk but claims content encoding		
			TRACE.catching("enableCompression()", ex);		
		}
	}


	/**
	 * Returns the digest algorithm used by the underlying input stream.
	 * @return The idenfifier of a digest algorithm, e.g. "MD5" or "SHA"
	 */
	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	/**
	 * Sets the digest algorithm for the underlying input stream.
	 * @param algorithm  the identifier of a digest algorithm, e.g. "MD5" or "SHA".
	 */
	public void setDigestAlgorithm(String algorithm) {
		this.digestAlgorithm = algorithm;		
	}

	/**
	 * Enables the calculation of a content digest for the 
	 * underlying input stream.
	 * @param enable  if true, the calculation of the digest is started,
	 * otherwise the calculation is finished. The digest then may be 
	 * retrieved with the <code>getContentDigest</code> method.
	 */
	public void enableDigest() throws IOException {
		if (in == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("skip(long)", ex);
			throw ex;	
		}		
		try {
			if (digestAlgorithm == null) {
				digestStream = new DigestInputStream(in, MessageDigest.getInstance("MD5"));
				TRACE.debugT("enableDigest()", "MD5 digest [enabled]");								
			} else {
				digestStream = new DigestInputStream(in, MessageDigest.getInstance(digestAlgorithm));
				TRACE.debugT("enableDigest()", "{0} digest [enabled]", new Object[]{digestAlgorithm});							
			}				
		} catch (NoSuchAlgorithmException ex) {
			TRACE.catching("enableDigest()", ex);
		}
		
		in = digestStream;
		digestEnabled = true;
		digest = null;			
	}

	/**
	 * Returns the message digest of the previously recorded input stream.
	 * In order to compare the result of this method with for eaxmple a "Content-MD5"
	 * response header it may be necessary to convert it to a "hexadecimal-encoded" string
	 * first (compare @see Encoder.toHexString(byte[])).
	 * Note, the digest is reset when the digest recording is switched on or off
	 * for the underlying input stream.
	 * @return The digest of the response body.
	 */
	public byte[] getDigest() {
		return digest;
	}	
	
	



}