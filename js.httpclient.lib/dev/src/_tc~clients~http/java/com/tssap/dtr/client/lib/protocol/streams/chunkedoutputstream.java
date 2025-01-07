package com.tssap.dtr.client.lib.protocol.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.IRequestStream;

/**
 * A wrapper for socket output streams providing transparent access
 * to chunked and unchunked data streams.
 * <p>The behavior of this stream is similar to <code>java.io.BufferedOutputStream</code>
 * but with some extensions. For example the whole content of a string or
 * another stream can be written directly to this stream without additional
 * buffering.</p>
 */
public class ChunkedOutputStream extends OutputStream implements IRequestStream {
	
	/** The default Maximum Segment Size for Ethernet connections (MTU=1500) */
	public static final int DEFAULT_MSS_ETHERNET = 1460;
	
	/** The default Maximum Segment Size for Analog Modem connections (MTU=576) */
	public static final int DEFAULT_MTU_MODEM = 536;
	
	/** The default Maximum Segment Size for PPPoE (point-to-point over ethernet, e.g. DSL)
	 *  connections (MTU=1492) */
	public static final int DEFAULT_MSS_PPPoE = 1452;	
	
	/** The maximum allowed Maximum Segment Size for TCP/IP(MTU=64K) */
	public static final int MAX_MSS_TCP_IP = 64*1024-40;		
	
	
	/** The internal buffer where the data is stored. */
	protected byte[] buf;

	/**
	 * The number of valid bytes in the buffer. This value is always in the
	 * range 0 through buf.length; elements buf[0] through buf[count-1]
	 * contain valid byte data.
	 */
	protected int count;
		
	/** The maximum count for buf */
	protected int size;	

	/** The underlying output stream */
	private OutputStream out;

	/** The socket that manages out. */
	private Socket socket;

	/** If true the stream uses "chunked" transfer encoding as defined in RFC2616 */
	private boolean chunking;

	/** The length of the stream */
	private long limit = -1L;

	/** The number of bytes already written to the stream */
	private long total = 0;
	
	private int offset = 0;


	/** client trace */
	private static final Location TRACE = Location.getLocation(ChunkedOutputStream.class);

	/** 
	 * The default location for wire traces.
	 * Maps to <code>Location.getLocation(ChunkedOutputStream.class)</code>
	 */
	public static final Location DEFAULT_WIRE_TRACE_LOCATION = TRACE;		


	/** wire trace */
	private Location WIRE_TRACE_LOCATION = DEFAULT_WIRE_TRACE_LOCATION;
	
	/** True, if wire tracing is enabled */
	private boolean wireTraceEnabled = false;
	private int traceOff;


	private static final byte[] CRLF = "\r\n".getBytes();
	// OK: <26.11.2004> CHANGED
	private static final byte[] LAST_CHUNK = "0\r\n\r\n".getBytes();
	private static final int OFFSET = Integer.toHexString(MAX_MSS_TCP_IP).getBytes().length + CRLF.length;
	private static final char[] HEX =
		new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', };
	
	
	/**
	 * Creates a new chunked output stream to write data to the specified
	 * underlying socket's output stream with a default buffer size equal to
	 * <code>DEFAULT_MSS_ETHERNET</code>, the usual maxmimum segment size for
	 * TCP/IP pakets on LAN/WAN connections.
	 * @param socket the socket associated with this stream.
	 * @throws IOException  if an I/O error occurs.
	 */
	public ChunkedOutputStream(Socket socket) throws IOException {
		this(socket, DEFAULT_MSS_ETHERNET);
	}

	/**
	 * Creates a new chunked output stream to write data to the specified
	 * underlying socket's output stream with a given buffer size.
	 * @param socket the socket associated with this stream.
	 * @param size  the size of the stream buffer.
	 * @throws IOException   if an I/O error occurs.
	 */
	public ChunkedOutputStream(Socket socket, int size) throws IOException {
		this.socket = socket;		
		out = socket.getOutputStream();
		setSize(size);							
	}

	/**
	 * Creates a ChunkedOutputStream as wrapper for an ordinary output stream
	 * An internal buffer of the specified size is created.
	 * Note, this method is intended for testing purposes only.
	 * @param out  the underlying input stream
	 * @param size the buffer size.
	 * @throws IOException  if an I/O error occurs.
	 */
	public ChunkedOutputStream(OutputStream out, int size) throws IOException {
		this.out = out;
		setSize(size);
	}	

	/**
	 * Converts this request stream to an ordinary output stream. 
	 * @return this
	 */	
	public OutputStream asStream() {
		return this;
	}


	/**
	 * Returns the currently selected buffer size.
	 * @return The buffer size in bytes.
	 */
	public int getSize() {
		return buf.length;
	}

	/**
	 * Sets a new size for the stream buffer.
	 * The buffer content is written to the output stream before 
	 * the size is changed.
	 * @param size  the new chunk size.
	 * @throws IOException if an I/O error occurs or the stream already is closed.
	 */
	public void setSize(int sz) throws IOException {
		if (out == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("setSize(int)", ex);
			throw ex;
		}
		
		if (count > 0) {
			writeBuffer();
		}		
		
		if (sz < DEFAULT_MTU_MODEM) {
			size = DEFAULT_MTU_MODEM;
		} else if (sz > MAX_MSS_TCP_IP) {
			size = MAX_MSS_TCP_IP;
		} else {
			size = sz;
		}
		buf = new byte[size];
	}

	/**
	 * Writes the specified byte to the stream.
	 * In chunking mode this method stores the given array in 
	 * the stream's buffer, flushing the buffer to the underlying socket output 
	 * stream "in chunks" when the content length exceeds the buffer's size.
	 * Otherwise the data is written directly to the underlying socket stream.
	 * If the content limit is exceeded by this write operation an IOException
	 * is thrown.  
	 * @param b  the byte or character.
	 * @throws IOException  if an I/O error occurs, the stream already is closed,
	 * or the content limit is reached.
	 */
	public void write(int b) throws IOException {
		if (out == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("write(int)", ex);
			throw ex;
		}
		
		if (count == size) {
			writeBuffer();
		}
		if (limit > 0) {
			++total;
			if (total > limit) {
				IOException ex = new IOException("number of bytes written to stream" +
					" exceeds content length");
				TRACE.throwing("write(int)", ex);
				throw ex;
			}
		}
		buf[offset+count] = (byte) b;
		++count;
	}

	/**
	 * Writes len bytes from the specified byte array starting at offset off to this 
	 * output stream. 
	 * In chunking mode this method stores the given array in 
	 * the stream's buffer, flushing the buffer to the underlying socket output 
	 * stream "in chunks" when the content length exceeds the buffer's size.
	 * Otherwise the data is written directly to the underlying socket stream.
	 * If the content limit is exceeded by this write operation an IOException
	 * is thrown.  
	 * @param b  the data.
	 * @param off  the start offset in the data.
	 * @param len  the number of bytes to write.
	 * @throws IOException  if an I/O error occurs, the stream already is closed,
	 * or <code>len</code> exceeds the remaining limit.
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		if (out == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("write(byte[],int,int", ex);
			throw ex;
		}

		if (count == size) {
			writeBuffer();
		}
		if (limit > 0) {
			total += len;
			if (total > limit) {
				IOException ex = new IOException("number of bytes written to stream" +
					" exceeds content length.");
				TRACE.throwing("write(byte[],int,int)", ex);
				throw ex;
			}
		}		
		int free = size - count;			
		if (len > free) {
			int pos = off;
			int cnt = len;
											
			System.arraycopy(b, off, buf, offset + count, free);
			count += free;
			writeBuffer();
			cnt -= free; 
			pos += free;
								
			while (cnt >= size) {
				System.arraycopy(b, pos, buf, offset, size);
				count = size;
				writeBuffer();
				cnt -= size;
				pos += size;
			}
			if (cnt > 0) {
				System.arraycopy(b, pos, buf, offset, cnt);
			}
			count = cnt;				
		} else {
			System.arraycopy(b, off, buf, offset + count, len);
			count += len;
		}
	}

	/**
	 * Writes the specified string to this stream
	 * using the given encoding to convert the stream to a byte array.
	 * In chunking mode this method stores the given array in 
	 * the stream's buffer, flushing the buffer to the underlying socket output 
	 * stream "in chunks" when the content length exceeds the buffer's size.
	 * Otherwise the data is written directly to the underlying socket stream.
	 * If the content limit is exceeded by this write operation an IOException
	 * is thrown.  
	 * @param s  the string to write.
	 * @param enc  the character encoding to use to convert the string to a byte array.
	 * @throws IOException  if an I/O error occurs, the stream already is closed,
	 * or the length of <code>s</code> exceeds the remaining limit.
	 * @throws UnsupportedEncodingException  if the specified encoding is not supported.
	 */
	public void write(String s, String enc) throws IOException, UnsupportedEncodingException {
		if (out == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("write(String,String)", ex);
			throw ex;
		}
		byte[] b = s.getBytes(enc);
		write(b, 0, b.length);
	}

	/**
	 * Writes the content of the input stream to the underlying output
	 * stream until the input stream is read out.
	 * In chunking mode this method stores the given array in 
	 * the stream's buffer, flushing the buffer to the underlying socket output 
	 * stream "in chunks" when the content length exceeds the buffer's size.
	 * Otherwise the data is written directly to the underlying socket stream.
	 * If the content limit is exceeded by this write operation an IOException
	 * is thrown. 
	 * @param source  the source stream.
	 * @throws IOException  if an I/O error occurs, the stream already is closed,
	 * or the length of <code>source</code> exceeds the remaining limit.
	 */
	public void write(InputStream source) throws IOException {
		if (out == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("write(InputStream)", ex);
			throw ex;
		}
		
		int read = source.read(buf, offset + count, size - count);
		while (read >= 0) {
			if (limit > 0) {
				total += read;
				if (total > limit) {
					IOException ex = new IOException("number of bytes written to stream" +
						" exceeds content length.");
					TRACE.throwing("write(InputStream)", ex);
					throw ex;
				}
			}				
			count += read;
			if (count == size) {
				writeBuffer();
			}
			read = source.read(buf, offset + count, size - count);
		}
	}

	/**
	 * Flushes the stream's buffer to the underlying socket output stream.
	 * @throws IOException  if an I/O error occurs or the stream already is closed.
	 */
	public void flush() throws IOException {
		if (out == null) {
			IOException ex = new IOException("stream is closed");
			TRACE.throwing("write(InputStream)", ex);
			throw ex;
		}
		writeBuffer();
		// OK: <26.11.2004> ADDED
		if (chunking) sendZeroChunk();
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
	 */
	public void close() throws IOException {
		if (out != null) {
			enableChunking(false);
			disableLimit();				
			flush();	
		}
	}
	

	/**
	 * Closes the stream and shuts the underlying socket output stream
	 * down. Any further write operation on this stream will be rejected
	 * with an <code>IOException</code>.
	 */
	public void shutdown() {
		try {
			close();
			socket.shutdownOutput();
		} catch (Exception ex) {
			// if already shutdown, i/o problem or the operation
			// is unsupported (SSLSocket!) leave it to the 
			// garbage collector
			TRACE.catching("shutdown()", ex);
		} finally {
			TRACE.debugT("shutdown()", "socket output stream [shutdown]");
			out = null;
			buf = null;
		}
	}

	/**
	 * Enables or disables chunking. The buffer content
	 * is written to the output stream before chunking mode is switched.
	 * If chunking is switched off, a final chunk is appended.<br/>
	 * If a limit was set before <code>disableLimit()</code> is
	 * called before chunking is enabled.
	 * @param enable  true, if chunking should be enabled, or false,
	 * if chunking should be disabled.
	 * @throws IOException   if an I/O error occurs or the stream already is closed.
	 */
	public void enableChunking(boolean enable) throws IOException {
		if ((chunking && enable) || (!chunking && !enable))
			return;
		if (enable) {
			if (limit>0) {
				disableLimit();
			}		
			writeBuffer();
			size -= OFFSET;
// OK: <26.11.2004> CHANGED
			size -= CRLF.length;	
			offset = OFFSET;
			buf[OFFSET - 2] = CRLF[0];
			buf[OFFSET - 1] = CRLF[1];
			buf[OFFSET + size] = CRLF[0];
			buf[OFFSET + size + 1] = CRLF[1];
			traceOff = OFFSET;							
		} 
		else if (chunking) {
//			if (count == size) {
				writeBuffer();
//			}
			sendZeroChunk();
			size += OFFSET;
// OK: <26.11.2004> CHANGED
			size += CRLF.length;	
			offset = 0;	
			traceOff = 0;		
		}
		chunking = enable;
	}

	/**
	 * Checks whether chunking currently is enabled for this stream.
	 * @return true, if chunking is enabled.
	 */
	public boolean chunking() {
		return chunking;
	}

	/**
	 * Enables a write limit.<br/> 
	 * Note, if the stream is in chunking mode, <code>enableChunking(false)</code>
	 * is called before setting the limit. 
	 * @param limit  the length of the content.
	 * @throws IOException   if an I/O error occurs or the stream already is closed.
	 */
	public void enableLimit(long limit) throws IOException {
		if (chunking) {
			enableChunking(false);
		}
		this.limit = limit;
		this.total = 0;
	}

	/**
	 * Disables the read limit. The number of bytes actually written to the
	 * stream must match the previously defined limit. If that number was
	 * less than the given site this method throws an IOException.
	 * @throws  IOException  if the number of byte written to the stream
	 * was less than the given limit.
	 */
	public void disableLimit() throws IOException {
		writeBuffer();		
		if (limit > 0) {
			if (total < limit) {
				IOException ex = new IOException("number of bytes written to stream " +					"less than specified limit.");
				TRACE.throwing("disableLimit()", ex);
				throw ex;						
			}
		}
		this.limit = -1L;		
	}
	
	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the default location for this class.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	public void enableWireTrace() {
		wireTraceEnabled = true;
		traceOff = count;
	}
	
	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the given location.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	public void enableWireTrace(Location location) {
		enableWireTrace();
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
	 * Writes the buffer content to the output stream. In chunking mode
	 * the length of the chunk is prepended.
	 * @throws IOException  if an I/O error occurs.
	 */
	private void writeBuffer() throws IOException {
		if (count > 0) {
			int off = offset;
			int cnt = count;
			if (chunking) {
				int n = count;
				int i = OFFSET - 3;								
				for (; n > 0 && i >= 0; --i) {
					buf[i] = (byte)HEX[n & 0x0f];
					n >>>= 4;
				}
				off = i + 1; // Start position of the chunk length
				cnt = count + OFFSET - off;

				// OK: <26.11.2004> CHANGED
				buf[OFFSET + count] = CRLF[0];
				buf[OFFSET + count + 1] = CRLF[1];

				cnt += CRLF.length;
			}
			
			out.write(buf, off, cnt);
			
			if (wireTraceEnabled && WIRE_TRACE_LOCATION.beDebug() && cnt>traceOff) 
			{
				if (chunking) {
					WIRE_TRACE_LOCATION.debugT("writeBuffer()", Integer.toString(count));
				}
				WIRE_TRACE_LOCATION.debugT("writeBuffer()", new String(buf, traceOff, cnt-traceOff));
				traceOff = (chunking)? OFFSET : 0;
			}						
			count = 0;					
		}			
	}
	
	// OK: <26.11.2004> ADDED
	/**
	 * Sends a trailing chunk
	 * @throws IOException
	 */
	private void sendZeroChunk() throws IOException {
		out.write(LAST_CHUNK, 0, LAST_CHUNK.length);
	}
}
