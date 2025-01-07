package com.tssap.dtr.client.lib.protocol;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * This interface represents a generic output stream for writing HTTP requests.
 */
public interface IRequestStream {

	/**
	 * Writes the specified byte to this buffered output stream.
	 * This method stores the byte into the stream's buffer, flushing the buffer to
	 * the underlying socket output stream in "chunks" as needed.
	 * @param b  the byte or character.
	 * @throws IOExceptions - if an I/O error occurs or the stream was closed.
	 */
	void write(int b) throws IOException;

	/**
	 * Writes the specified byte to this output stream.
	 * @param b  the data.
	 * @throws IOExceptions - if an I/O error occurs or the stream was closed.
	 */
	void write(byte[] b) throws IOException;

	/**
	* Writes len bytes from the specified byte array starting at offset off to this output stream.
	* Ordinarily this method stores bytes from the given array into the stream's buffer, flushing the buffer to
	* the underlying socket output stream in "chunks" as needed.
	* If the requested length is at least as large as this stream's buffer,
	* however, then this method will first flush the buffer and then write the byte array directly
	* to the socket stream in chunks of the predefined maxium chunk size.<br/>
	* @param b  the data.
	* @param off  the start offset in the data.
	* @param len  the number of bytes to write.
	* @throws IOExceptions - if an I/O error occurs or the stream was closed.
	*/
	void write(byte[] b, int off, int len) throws IOException;

	/**
	 * Writes the specified string to this stream
	 * using the given encoding to convert the stream to a byte array.
	 * @param s  the string to write.
	 * @param enc  the character encoding to use to convert the string to a byte array.
	 * @throws IOExceptions - if an I/O error occurs or the stream was closed.
	 * @throws UnsupportedEncodingException - if the specified encoding is not supported.
	 */
	void write(String s, String enc) throws IOException, UnsupportedEncodingException;

	/**
	 * Writes the content of the input stream to the underlying output
	 * stream until the input stream is read out (read returns -1).
	 * @param source  the source stream.
		 * @throws IOExceptions - if an I/O error occurs or the stream was closed.
	 */
	void write(InputStream source) throws IOException;

	/**
	 * Flushes the stream's buffer to the underlying socket output stream.
	 * If chunking is enabled the buffer is written to the underlying stream
	 * before.
	 * @throws IOExceptions - if an I/O error occurs.
	 */
	void flush() throws IOException;

	/**
	 * Closes this output stream. If chunking mode is enabled the buffer is written
	 * to the underlying output stream and a "final" chunk is appended. Chunking mode
	 * is switched off. Then the underlying stream is flushed. If the stream is not a
	 * socket's output stream the close method of the underlying stream is called
	 * and the buffer released.
	 * @throws IOExceptions - if an I/O error occurs.
	 */
	void close() throws IOException;

	/**
	 * Converts this request stream to an ordinary output stream. 
	 * @return this request stream
	 */
	OutputStream asStream();

}