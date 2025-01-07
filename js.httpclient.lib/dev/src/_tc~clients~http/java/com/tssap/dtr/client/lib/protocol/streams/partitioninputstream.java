package com.tssap.dtr.client.lib.protocol.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.IResponseStream;

/**
 * A wrapper for response streams capable of cutting input data into
 * parts of predefined size or parts delimited by a certain boundary
 * pattern.  
 * This stream is used in the MIME/Multipart response parser to cut
 * multipart responses into individual parts.
 */
public class PartitionInputStream extends InputStream implements IResponseStream {

	private static final int DEFAULT_SIZE = 4*1024;
	
	private static final String END_OF_LINE = "\r\n";

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


	/** The underlying stream. */
	private IResponseStream in;

	/** true, if a part currently is started */
	private boolean readingPart;
	/** the boundary byte pattern */
	private byte[] partBoundary;
	/** the remaining length of the current part, or -1 if the length is unknown */
	private long partCount = -1L;
	/** index of character in boundary to match next with the input */
	private int matchNext;
	/** value of pos where a trailing part of the boundary pattern started */
	private int markBoundary = -1;

	/** common trace location*/
	private static Location TRACE = Location.getLocation(PartitionInputStream.class);

	/**
	 * Creates a PartitionInputStream on top of the given input stream.
	 * @param in   the underlying response stream.
	 */
	public PartitionInputStream(IResponseStream in) {
		this(in, DEFAULT_SIZE);
	}

	/**
	 * Creates a PartitionInputStream with the specified buffer size
	 * on top of the given input stream.
	 * @param in   the underlying response stream.
	 * @param size   the buffer size.
	 */
	public PartitionInputStream(IResponseStream in, int size) {
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
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("available()", ex);
//			throw ex;
//		}
		return in.available() + count - pos;
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
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("read()", ex);
//			throw ex;
//		}

		int avail = count - pos;

		if (avail <= 0) {
			avail = fill();
			if (avail <= 0) {
				return -1;
			}
		}

		int b = buf[pos++] & 0xff;
		--partCount;
		return b;
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
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("read(byte[],int,int)", ex);
//			throw ex;
//		}
		
		int cnt = readAvailable(b, off, len);
		int read = cnt;
		while ( cnt > 0 ) {
			cnt = readAvailable(b, off + read, len - read);
			// CHANGED by OK
			// CSN 2748966
			if (cnt > 0) read += cnt;
		}		

		return read;
	}

	/**
	 * Appends the current content of the stream to the specified string buffer.
	 * An attempt is made to read as many as bytes from the content stream as
	 * possible, but mutliple calls to this method may be necessary to read
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
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("read(StringBuffer,String)", ex);
//			throw ex;
//		}

		int cnt = readAvailable(b, enc);
		int read = cnt;
		while ( cnt > 0 ) {
			cnt = readAvailable(b, enc);
			// CHANGED by OK
			// CSN 2748966
			if (cnt > 0) read += cnt;
		}

		return read;
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
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("read(OutputStream)", ex);
//			throw ex;
//		}

		int cnt = readAvailable( destination );
		int read = cnt;
		while ( cnt > 0 ) {
			cnt = readAvailable( destination );
			// CHANGED by OK
			// CSN 2748966
			if (cnt > 0) read += cnt;
		}

		return read;
	}

	/**
	 * Reads a single line from the stream.
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
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("readLine(boolean,boolean)", ex);
//			throw ex;
//		}

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
							result = END_OF_LINE;
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
			IOException ex = new IOException("unexpected end of stream");
			TRACE.throwing("readLine(boolean,boolean)", ex);
			throw ex;
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
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("skip(long)", ex);
//			throw ex;
//		}

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

		return skipped;
	}

	/**
	 * Skips pending content. If the stream is reading a part,
	 * the method skips only this part.
	 * @throws IOException  if an I/O error occurs.
	 */
	public void skipContent() throws IOException {
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("skipContent()", ex);
//			throw ex;
//		}

		while (skipAvailable() > 0) {
		}
	}

	/**
	 * Closes this stream.
	 * Calls the corresponding method of the underlying response stream.
	 * @throws IOException   if an I/O error occurs.
	 */
	public void close() throws IOException {
//		if (in != null) {					
			in.close();
			readingPart = false;
			matchNext = 0;
			markBoundary = -1;			
//		}
	}
	
	/**
	 * Releases the stream for further use. 
	 * Calls the corresponding method of the underlying response stream and
	 * ends an currently started part.
	 * Note, this method can be used to interrupt the reading of the current
	 * and any further part in the input stream.
	 * @throws IOException   if an I/O error occurs.
	 */	
	public void release() throws IOException {
//		if (in == null) {
//			IOException ex = new IOException("stream is closed");
//			TRACE.throwing("release()", ex);
//			throw ex;
//		}
		in.release();
		readingPart = false;
		matchNext = 0;
		markBoundary = -1;	
	}	
	
	
	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the default location for this class.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	public void enableWireTrace() {
		in.enableWireTrace();
	}
	
	/**
	 * Enables the "wire" trace for this stream. The trace is
	 * written to the given location.
	 * Note, the wire trace becomes only active if the
	 * category of location is DEBUG.
	 */
	public void enableWireTrace(Location location) {
		in.enableWireTrace(location);
	}

	/**
	 * Disables the "wire" trace for this stream.
	 */
	public void disableWireTrace() {
		in.disableWireTrace();	
	}	
	
	/**
	 * Checks whether the wire trace is enabled
	 * @return  true, if the trace is enabled
	 */
	public boolean isWireTraceEnabled() {
		return in.isWireTraceEnabled();
	}		

	/**
	 * Starts a new part with the given length. 
	 * If this method is called while another part is started, that part
	 * is skipped.
	 * @param contentLength  the length of the part in bytes.
	 * @throws IOException   if an I/O error occurs.
	 */
	public void beginPart(long contentLength) throws IOException {
		if (readingPart) {
			skipContent();
			endPart(true);
		}
		readingPart = true;
		partCount = contentLength;
		partBoundary = null;
		matchNext = 0;
		markBoundary = -1;
	}	


	/**
	 * Starts a new part with the given boundary delimiter pattern.
	 * If this method is called while another part is started, that part
	 * is skipped.
	 * @param boundary  the byte pattern that delimits this part
	 * @throws IOException   if an I/O error occurs.
	 */
	public void beginPart(byte[] boundary) throws IOException {
		if (readingPart) {
			skipContent();
			endPart(true);
		}
		readingPart = true;
		partCount = -1;
		partBoundary = boundary;
		matchNext = 0;
		markBoundary = -1;
		
		if (boundary.length > buf.length) {
			byte[] newbuf = new byte[boundary.length];
			System.arraycopy(buf, pos, newbuf, 0, count-pos);
			buf = newbuf;
		}				
	}
	


	/**
	 * Starts a new part with the given boundary delimiter pattern
	 * and length. The part is supposed to have at least a
	 * length of <code>contentLength</code> bytes followed by the given boundary
	 * pattern. Any padding after the byte block of length <code>contentLength</code>
	 * and the first byte of the boundary pattern is skipped.
	 * If this method is called while another part is open, that part
	 * is skipped.
	 * @param boundary  the byte pattern that delimits this part
	 * @param contentLength  the length of the part in bytes 
	 * @throws IOException   if an I/O error occurs.
	 */
	public void beginPart(byte[] boundary, long contentLength) throws IOException {
		beginPart(boundary);
		partCount = contentLength;
	}
	
	/**
	 * Indicates the end of the current part. Remaining content of that part
	 * is skipped. This method is equivalent to <code>endPart(false)</code>
	 * @throws IOException   if an I/O error occurs.
	 */
	public void endPart() throws IOException {
		if (readingPart) {
			skipContent();
			readingPart = false;
			matchNext = 0;
			markBoundary = -1;			
		}
	}	

	/**
	 * Indicates the end of the current part. Remaining content of that part
	 * is skipped. The stream is positioned at the beginning of the
	 * delimiting boundary pattern (if any), unless <code>skipBoundary</code>
	 * is set to <code>true</code>. In this case the boundary string is skipped
	 * from the input stream too.
	 * @param skipBoundary  if true, the boudary pattern following the end of the part
	 * body is skipped.
	 * @throws IOException   if an I/O error occurs.
	 */
	public void endPart(boolean skipBoundary) throws IOException {
		if (readingPart) {
			endPart();			
			if (skipBoundary  &&  partBoundary != null) {
				skip(partBoundary.length);
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

		if (readingPart) {
			if (partCount == 0) {
				return -1;
			} else if (partCount > 0) {
				avail = (avail < partCount) ? avail : (int)partCount;
			} else if (matchNext > 0) {
				avail = 0;
			}
		}

		if (avail <= 0) {
			avail = fill();
			if (avail <= 0) {
				return -1;
			}
		}

		int cnt = (avail < len) ? avail : len;

		System.arraycopy(buf, pos, b, off, cnt);

		pos += cnt;
		partCount -= cnt;

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
	private int readAvailable(StringBuffer b, String enc)
		throws IOException, UnsupportedEncodingException {
		int avail = count - pos;

		if (readingPart) {
			if (partCount == 0) {
				return -1;
			} else if (partCount > 0) {
				avail = (avail < partCount) ? avail : (int)partCount;
			} else if (matchNext > 0) {
				avail = 0;
			}
		}

		if (avail <= 0) {
			avail = fill();
			if (avail <= 0) {
				return -1;
			}
		}

		b.append(new String(buf, pos, avail, enc));
		
		pos += avail;
		partCount -= avail;
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

		if (readingPart) {
			if (partCount == 0) {
				return -1;
			} else if (partCount > 0) {
				avail = (avail < partCount) ? avail : (int)partCount;
			} else if (matchNext > 0) {
				avail = 0;
			}
		}

		if (avail <= 0) {
			avail = fill();
			if (avail <= 0) {
				return -1;
			}
		}

		destination.write(buf, pos, avail);
		
		pos += avail;
		partCount -= avail;
		return avail;
	}

	/**
	 * Skips the currently available stream content.
	 * Fills the stream buffer before if necessary.
	 * @throws IOException  if an I/O error occurs.
	 */
	private long skipAvailable() throws IOException {
		long avail = count - pos;

		if (readingPart) {
			if (partCount == 0) {
				return -1;
			} else if (partCount > 0) {
				avail = (avail < partCount) ? avail : (int)partCount;
			} else if (matchNext > 0) {
				avail = 0;
			}
		}

		if (avail <= 0) {
			avail = fill();
			if (avail <= 0) {
				return -1;
			}
		}

		pos += avail;
		partCount -= avail;
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

		if (readingPart) {
			if (partCount == 0) {
				return -1;
			} else if (partCount > 0) {
				avail = (avail < partCount) ? avail : (int)partCount;
			} else if (matchNext > 0) {
				avail = 0;
			}
		}

		if (avail <= 0) {
			avail = fill();
			if (avail <= 0) {
				return -1;
			}
		}

		long cnt = (avail < n) ? avail : n;
		pos += cnt;
		partCount -= cnt;
		return cnt;
	}

	/**
	 * Fills the buffer.
	 * @return the number of bytes available after filling the buffer.
	 * Note, this number may be smaller then buf.length if either the predefined limit,
	 * a part boundary, the end of the current chunk or the end of the stream was reached.
	 * @throws IOException  if an I/O error occurs, the stream ended
	 * in the middle of a chunk, a chunk was not well-formatted or the read
	 * limit could not be fulfilled.
	 */
	private int fill() throws IOException {
		
		if (markBoundary < 0) {
			pos = 0;
			count = 0;
			int n = in.read(buf, 0, buf.length);
			if (n < 0) {
				return -1;
			}
			count = n;
		} else {
			pos = 0;			
			count -= markBoundary;
			System.arraycopy(buf, markBoundary, buf, 0, count);
			int n = in.read(buf, count, buf.length-count);
			if (n < 0) {
				return -1;
			}
			count += n;							
		}		
							
		if (readingPart  &&  count > 0) {
			if (partCount > 0) {
				return (count < partCount) ? count : (int)partCount;
			} else {
				int cnt = indexOf(partBoundary) - pos;
				if (cnt >= 0) {
					if (markBoundary < 0) {
						partCount = cnt;
					}
					return cnt;					
				}				
			}			
		}
		
		return count;
	}


	/**
	 * Searches the buffer for the occurence of the given byte 
	 * array.
	 * @param b  the pattern to search for
	 * @return  the index of the pattern in the buffer, or -1
	 * if the pattern could not be found. 
	 * If an index dufferent from -1 is returned and <code>matchNext > 0</code>
	 * only a leading part of the pattern has been found and the
	 * value of <code>matchNext</code> indicates up to which index of 
	 * the pattern the matching was sucessful.
	 */
	private int indexOf(byte[] b) {
		int i = pos + matchNext;		
		byte first = b[matchNext];		
		
		start : 
		while (true) {
			while (i < count  &&  buf[i] != first) {
				i++;
			}
			if (i == count) {
				markBoundary = -1;
				return -1;
			}

			++matchNext;
			int j = i + 1;			
			while (j < count  &&  matchNext < partBoundary.length) {
				if (buf[j++] != b[matchNext++]) {
					i++;
					matchNext = 0;
					continue start;
				}
			}
			if (matchNext < partBoundary.length) {
				markBoundary = i;
			} else if (markBoundary >=0 ) {
				i = pos;
				matchNext = 0;
				markBoundary = -1;
			} else {
				matchNext = 0;
				markBoundary = -1;
			}
			return i;
		}
	}

//	public static void main(String[] args) throws Exception {
//		FileInputStream file = new FileInputStream(new File("c:\\TEST\\MULTIPART.txt"));
//		ChunkedInputStream base = new ChunkedInputStream(file, 1024);
//		PartitionInputStream in = new PartitionInputStream(base, 10);
//		
//		byte[] boundary = "--partpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpartpart\r\n".getBytes();
//		
//		in.beginPart(boundary);
//		in.readLine(true, true);
//		byte[] buf = new byte[1234];
//		int cnt = -1;
//		while (  (cnt = in.read(buf)) >= 0) {
//			System.out.write(buf, 0, cnt);
//		}
//		in.endPart(false);
//		
//		in.beginPart(300);
//		buf = new byte[10];
//		cnt = -1;
//		while (  (cnt = in.read(buf)) >= 0) {
//			System.out.write(buf, 0, cnt);
//		}	
//		in.endPart(false);		
//	}



}
