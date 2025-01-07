package com.tssap.dtr.client.lib.protocol.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.IRequestStream;

public class RequestStream extends OutputStream implements IRequestStream {
	
	/** Either the compressing stream, otherwise equals base */
	private OutputStream out;

	/** The underlying chunked output stream */
	private ChunkedOutputStream base;
	
	/** The optional compressed stream associated with the underlying output stream */
	private DeflaterOutputStream compressedStream;	
	
	/** The compression algorithmn used for the body, either gzip or deflate */
	private String compressionAlgorithm;

	/** true, if compression is enabled */
	private boolean compressionEnabled = false;
		
	/** client trace */
	private static final Location TRACE = Location.getLocation(RequestStream.class);
			
		
	/**
	 * Creates a RequestStream for the specified socket.
	 * The stream uses a default buffer size equal to the socket's
	 * underlying receive buffer.
	 * @param socket the socket associated with this stream.
	 * @throws IOException - if an I/O error occurs.
	 */
	public RequestStream(Socket socket) throws IOException {
		base = new ChunkedOutputStream(socket);
		out = base;
	}
	
	/**
	 * Creates a RequestStream for the specified socket.
	 * The stream uses a buffer of the specified size.
	 * @param socket the socket associated with this stream.
	 * @param size the buffer size of the underlying ChunkedOutputStream.
	 * @throws IOException  if an I/O error occurs.
	 */
	public RequestStream(Socket socket, int size) throws IOException {
		base = new ChunkedOutputStream(socket, size);
		out = base;
	}	
	
	/**
	 * Creates a RequestStream as wrapper for an ordinary output stream.
	 * Note, this method is intended for testing purposes only.
	 * @param out  the underlying input stream
	 * @param size the buffer size.
	 * @throws IOException  if an I/O error occurs.
	 */	
	public RequestStream(OutputStream out, int size) throws IOException {
		base = new ChunkedOutputStream(out, size);
		this.out = base;
	}	
	
	
	/**
	 * Writes the specified byte to this stream.
	 * @param b  the byte or character.
	 * @throws IOException  if an I/O error occurs or the stream was closed.
	 */
	public void write(int b) throws IOException {
		out.write(b);
	}
	
	/**
	 * Writes len bytes from the specified byte array starting at offset off to this output stream.
	 * @param b  the data.
	 * @param off  the start offset in the data.
	 * @param len  the number of bytes to write.
	 * @throws IOException  if an I/O error occurs or the stream was closed.
	 */	
	public void write(byte[] b, int off, int len) throws IOException {	
		out.write(b, off, len);
	}

	/**
	 * Writes the specified string to this stream
	 * using the given encoding to convert the stream to a byte array.
	 * @param s  the string to write.
	 * @param enc  the character encoding to use to convert the string to a byte array.
	 * @throws IOException  if an I/O error occurs or the stream was closed.
	 * @throws UnsupportedEncodingException  if the specified encoding is not supported.
	 */
	public void write(String s, String enc) throws IOException, UnsupportedEncodingException {
		if (out==base) {
			base.write(s, enc);
		} else {
			out.write(s.getBytes(enc));
		}		
	}

	/**
	 * Writes the content of the input stream to the underlying output
	 * stream until the input stream is read out (read returns -1).
	 * @param source  the source stream.
	 * @throws IOException  if an I/O error occurs or the stream was closed.
	 */
	public void write(InputStream source) throws IOException {
		if (out==base) {
			base.write(source);
		} else {
			byte[] sb = new byte[16 * 1024];
			int read = source.read(sb);
			while (read > 0) {
				out.write(sb, 0, read);
				read = source.read(sb);
			}
		}
	}
	
	/**
	 * Flushes the stream's buffer to the underlying socket output stream.
	 * @throws IOException  if an I/O error occurs.
	 */
	public void flush() throws IOException {
		out.flush();
	}	
	
	/**
	 * Closes this output stream.
	 * If chunking mode is enabled the buffer is written
	 * to the underlying output stream and a "final" chunk is appended. Chunking mode
	 * is switched off and a given write limit is reset, respectively. 
	 * Finally the underlying stream is flushed.
	 * IMPORTANT NOTE: applications should not call this method directly 
	 * since the lifetime of the socket input stream is controlled
	 * by the corresponding <code>Connection</code>. In order to ensure
	 * this behavior this method but does not really close the stream
	 * but only closes any trailing chunk and afterwards flushes the
	 * stream to the underlying socket.  
	 * @throws IOException  if an I/O error occurs.
	 * @see ChunkedOutputStream#close()
	 */
	public void close() throws IOException {
		out.close();
	}	
	
	/**
	 * Converts this request stream to an ordinary output stream. 
	 * @return this
	 */	
	public OutputStream asStream() {
		return this;
	}	
	
	/**
	 * Closes the underlying socket stream and releases the buffer.
	 */
	public void shutdown() {
		try {
			close();				
			base.shutdown();
		} catch (Exception ex) {
			// if already shutdown, i/o problem or the operation
			// is unsupported (SSLSocket!) leave it to the 
			// garbage collector
			TRACE.catching("shutdown()", ex);
		} finally {
			compressedStream = null;			
			out = null;
			base = null;
		}
	}	
	
	/**
	 * Enables or disables chunking mode.
	 * @param enable   if true, chunking is enabled.
	 */
	public void enableChunking(boolean enable) throws IOException {
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
	public void enableLimit(long limit) throws IOException  {
		base.enableLimit(limit);
	}

	/**
	 * Disables the read limit.
	 */
	public void disableLimit() throws IOException {
		base.disableLimit();
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
		if (compressionAlgorithm == null || compressionAlgorithm.equals("gzip")) {
			compressedStream = new GZIPOutputStream(base);
			out = compressedStream;
			compressionEnabled = true;
			TRACE.debugT("enableCompression()", "GZIP encoding [enabled]");					
		} else if (compressionAlgorithm.equals("deflate")) {
			compressedStream = new DeflaterOutputStream(base);
			out = compressedStream;
			compressionEnabled = true;
			TRACE.debugT("enableCompression()", "DEFLATE encoding [enabled]");				
		} else {
			IOException ex = new IOException(
				"unsupported content coding " + compressionAlgorithm
			);
			TRACE.throwing("enableCompression()", ex);					
			throw ex;
		}
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

}
