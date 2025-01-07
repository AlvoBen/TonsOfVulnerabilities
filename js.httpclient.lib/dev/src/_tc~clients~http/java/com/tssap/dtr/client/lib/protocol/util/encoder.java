package com.tssap.dtr.client.lib.protocol.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * This class provides some common encoding/decoding algorithms, like "base64".
 */
public final class Encoder {
	
	/**
	 * Null-device. Returns always zero bytes.
	 * Used for readout of default char encoding. 
	 */
	private static class NullStream extends InputStream {
	   public int read() throws IOException {
		   return 0;
	   }
   }
	/**
	 * Encodes a URL path part. According to RFC2396 only a very limited subset of
	 * the ASCII character set is allowed in a path. Other ASCII characters must be
	 * encoded in a 3-character string "%HH" where HH is the two-digit
	 * hexadecimal representation of the character code. For unicode characters
	 * the string is converted to UTF-8 format first.
	 * This means in detail:
	 * <ul>
	 * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
	 *        and '0' through '9' remain the same.
	 *
	 * <li><p>The unreserved characters - _ . ! ~ * ' ( ) remain the same.
	 *
	 * <li><p>All other ASCII characters (including blanks) are converted into the
	 *        3-character string "%HH", where HH is the two-digit hexadecimal
	 *        representation of the character code.
	 *
	 * <li><p>All non-ASCII characters are encoded in two steps: first
	 *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
	 *        second each of these bytes is encoded as "%HH".
	 * </ul>
	 *
	 * @param s The string to be encoded
	 * @return The encoded string
	 */
	public static String encodePath(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') { // '0'..'9'
				sbuf.append((char) ch);
			} else if (ch == '/' || ch == '-' || ch == '_' || ch == '.' || ch == '!' || ch == '~' || ch == '$') {
				sbuf.append((char) ch);
			} else if (ch <= 0x007f) { // other ASCII
				sbuf.append(hex[ch]);
			} else if (ch <= 0x07FF) { // non-ASCII <= 0x7FF
				sbuf.append(hex[0xc0 | (ch >> 6)]);
				sbuf.append(hex[0x80 | (ch & 0x3F)]);
			} else { // 0x7FF < ch <= 0xFFFF
				sbuf.append(hex[0xe0 | (ch >> 12)]);
				sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
				sbuf.append(hex[0x80 | (ch & 0x3F)]);
			}
		}
		return sbuf.toString();
	}

	/**
	 * Decodes a URL. Replaces "%HH" sequences with their corresponding ASCII
	 * characters and applies a UTF-8 decoding.
	 *
	 * @param s The string to be decoded
	 * @return The decoded string
	 */
	public static String decodePath(String s) {
		StringBuffer sbuf = new StringBuffer();
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) { //$JL-ASSIGN$
			switch (ch = s.charAt(i)) {
				case '%' :
					ch = s.charAt(++i);
					int hb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					ch = s.charAt(++i);
					int lb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					b = (hb << 4) | lb;
					break;
				default :
					b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) { // 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
				if (--more == 0) {
					sbuf.append((char) sumb); // Add char to sbuf
				}
			} else if ((b & 0x80) == 0x00) { // 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b); // Store in sbuf
			} else if ((b & 0xe0) == 0xc0) { // 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1; // Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) { // 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2; // Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) { // 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3; // Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) { // 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4; // Expect 4 more bytes
			} else /*if ((b & 0xfe) == 0xfc)*/ { // 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5; // Expect 5 more bytes
			}
		}
		return sbuf.toString();
	}

	/**
	 * Encodes a query part of an URL.
	 * According to RFC2396 only a very limited subset of
	 * the ASCII character set is allowed in a query. Other ASCII characters must be
	 * encoded in a 3-character string "%HH" where HH is the two-digit
	 * hexadecimal representation of the character code. For unicode characters
	 * the string is converted to UTF-8 format first.
	 * This means in detail:
	 * <ul>
	 * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
	 *        and '0' through '9' remain the same.
	 *
	 * <li><p>The unreserved characters - _ . remain the same.
	 * 
	 * <li><p>Blanks are encoded as '+'.
	 *
	 * <li><p>All other ASCII characters (including blanks) are converted into the
	 *        3-character string "%HH", where HH is the two-digit hexadecimal
	 *        representation of the character code.
	 *
	 * <li><p>All non-ASCII characters are encoded in two steps: first
	 *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
	 *        second each of these bytes is encoded as "%HH".
	 * </ul>
	 *
	 * @param s The string to be encoded
	 * @return The encoded string
	 */
	public static String encodeQuery(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') { // '0'..'9'
				sbuf.append((char) ch);
			} else if (' ' == ch) { // blanks -> +
				sbuf.append('+');
			} else if (ch == '-' || ch == '_' || ch == '.') {
				sbuf.append((char) ch);
			} else if (ch <= 0x007f) { // other ASCII
				sbuf.append(hex[ch]);
			} else if (ch <= 0x07FF) { // non-ASCII <= 0x7FF
				sbuf.append(hex[0xc0 | (ch >> 6)]);
				sbuf.append(hex[0x80 | (ch & 0x3F)]);
			} else { // 0x7FF < ch <= 0xFFFF
				sbuf.append(hex[0xe0 | (ch >> 12)]);
				sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
				sbuf.append(hex[0x80 | (ch & 0x3F)]);
			}
		}
		return sbuf.toString();
	}
	
	/**
	 * Decodes a query string. Replaces "%HH" sequences with their corresponding ASCII
	 * characters, '+' signs by blanks and applies a UTF-8 decoding.
	 *
	 * @param s The string to be decoded
	 * @return The decoded string
	 */	
	public static String decodeQuery(String s) {
		StringBuffer sbuf = new StringBuffer();
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) { //$JL-ASSIGN$
			switch (ch = s.charAt(i)) {
				case '%' :
					ch = s.charAt(++i);
					int hb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					ch = s.charAt(++i);
					int lb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					b = (hb << 4) | lb;
					break;
				case '+' :
					b = ' ';
					break;
				default :
					b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) { // 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
				if (--more == 0) {
					sbuf.append((char) sumb); // Add char to sbuf
				}
			} else if ((b & 0x80) == 0x00) { // 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b); // Store in sbuf
			} else if ((b & 0xe0) == 0xc0) { // 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1; // Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) { // 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2; // Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) { // 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3; // Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) { // 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4; // Expect 4 more bytes
			} else /*if ((b & 0xfe) == 0xfc)*/ { // 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5; // Expect 5 more bytes
			}
		}
		return sbuf.toString();
	}	

	public static String encodeXml(String s) {
		if (s == null) {
			return null;
		}

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			switch (s.charAt(i)) {
				case '&' :
					buffer.append("&amp;");
					continue;
				case '<' :
					buffer.append("&lt;");
					continue;
				case '>' :
					buffer.append("&gt;");
					continue;
				case '\'' :
					buffer.append("&apos;");
					continue;
				case '"' :
					buffer.append("&quot;");
					continue;
				default :
					buffer.append(s.charAt(i));
			}
		}
		return buffer.toString();
	}

	/** The default character encoding of the platform */
	private static String defaultEncoding = (new InputStreamReader(new NullStream())).getEncoding();

	/**
	 * Base64 encode the string in buffer according to RFC 2045.
	 * Default character encoding is applied to buffer.
	 * @param buffer  the string to encode
	 * @return the encoded bytes
	 */
	public static String encodeBase64(String buffer) {
		return encodeBase64(buffer.getBytes());
	}

	/**
	 * Base64 encode the bytes in buffer according to RFC 2045.
	 * @param buffer  the byte array to encode.
	 * @return the encoded bytes
	 */
	public static String encodeBase64(byte[] buffer) {
		StringBuffer sb = new StringBuffer((buffer.length + 2) / 3 * 4);
		int group = 0;
		int mode = 0;
		int last = 0;
		for (int i = 0; i < buffer.length; ++i) {
			int b = buffer[i] & 0xff;
			switch (mode) {
				case 0 :
					++group;
					if (group == 19) {
						sb.append("\r\n");
						group = 0;
					}
					sb.append(map0[b]);
					last = (b & 0x03);
					mode = 1;
					break;
				case 1 :
					sb.append(map1[last | (b & 0xf0)]);
					last = (b & 0x0f);
					mode = 2;
					break;
				case 2 :
					sb.append(map2[last | (b & 0xc0)]);
					sb.append(map3[b]);
					mode = 0;
					break;
				default:
					break;
			}
		}
		switch (mode) {
			case 0 :
				break;
			case 1 :
				sb.append(map1[last]);
				sb.append("==");
				break;
			case 2 :
				sb.append(map2[last]);
				sb.append('=');
				break;
			default:
				break;				
		}
		return sb.toString();
	}

	/**
	 * Base64 decode the string in the buffer according to RFC 2045.
	 * @param buffer  the string to decode.
	 * @return the original bytes, or null if unexpected end of base64
	 * encoded string encountered
	 */
	public static byte[] decodeBase64(String buffer) {
		int mode = 0;
		int len = buffer.length();
		byte[] result = new byte[len];
		int bytes = 0;
		byte b = 0;
		int offset = 0;
		for (int i = 0; i < len; ++i) {
			char c = buffer.charAt(i);
			byte current = 0;
			if (c != '=') {
				current = (byte) rmap[c];
				if (current < 0) {
					continue;
				}
				if (mode != 0) {
					++bytes;
				}
			}
			switch (mode) {
				case 0 :
					b = (byte) ((current) << 2);
					mode = 1;
					break;
				case 1 :
					b |= (current & 0xf0) >>> 4;
					result[offset++] = b;
					b = (byte) ((current & 0x0f) << 4);
					mode = 2;
					break;
				case 2 :
					b |= (current & 0xfc) >>> 2;
					result[offset++] = b;
					b = (byte) ((current & 0x03) << 6);
					mode = 3;
					break;
				case 3 :
					b |= current;
					result[offset++] = b;
					mode = 0;
					break;
				default:
					break;					
			}
		}
		if (mode != 0) {
			return null;
		}

		byte[] bs = new byte[bytes];
		System.arraycopy(result, 0, bs, 0, bytes);
		return bs;
	}

	/**
	 * Hex encodes an integer.
	 * @param value  the integer to encode
	 * @return the value as hex string.
	 */
	public static String toHexString(int value) {
		char[] chars = new char[8];
		for (int i = 7; i >= 0; --i) {
			chars[i] = nibbleChar[value & 0x0f];
			value >>>= 4;
		}
		return new String(chars);
	}

	/**
	  * Hex encodes a byte array.
	  * @param buffer  the byte array to encode
	  * @return the buffer as hex string.
	  */
	public static String toHexString(byte[] buffer) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; ++i) {
			sb.append(nibbleChar[(((int) buffer[i]) & 0xf0) >> 4]);
			sb.append(nibbleChar[((int) buffer[i]) & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * Hex encodes an integer to a byte array.
	 * @param value  the integer to encode
	 * @param length the length of the resulting byte array
	 * @return the value as hexadecimal encoded byte array
	 */
	public static byte[] encodeHex(int value) {
		byte[] sb = new byte[8];
		for (int i = 8 - 1; i >= 0; --i) {
			sb[i] = (byte) nibbleChar[value & 0x0f];
			value >>>= 4;
		}
		return sb;
	}

	/**
	  * Hex encodes a byte array.
	  * @param buffer  the byte array to encode
	  * @return the buffer as hexadecimal encoded byte array
	  */
	public static byte[] encodeHex(byte[] buffer) {
		byte[] sb = new byte[buffer.length * 2];
		for (int i = 0, j = 0; i < buffer.length; ++i, j += 2) {
			sb[j] = (byte) nibbleChar[(((int) buffer[i]) & 0xf0) >> 4];
			sb[j + 1] = (byte) nibbleChar[((int) buffer[i]) & 0x0f];
		}
		return sb;
	}

	/**
	 * Decodes the hex number in the buffer starting at the given offset.
	 */
	public static int decodeHex(byte[] buffer, int off) {
		int sum = 0;
		byte c = buffer[off];
		int d = (c >= 0x30 && c < 0x70) ? digits[c - 0x30] : -1;
		if (d == -1)
			return -1;
		sum = d;
		int i = off + 1;
		int len = buffer.length;
		while (i < len && (c = buffer[i]) >= 0x30 && c < 0x70 && (d = digits[c - 0x30]) != -1) {
			sum <<= 4;
			sum += d;
			++i;
		}
		return sum;
	}

	/**
	 * Convert this String into bytes according to the specified character
	 * encoding, storing the result into a new byte array. If no encoding
	 * is specified the platform default encoding is used.
	 * @param enc  the name of a supported character encoding, or null if the
	 * platform default encoding should be used.
	 * @return The resultant byte array
	 * @throws UnsupportedEncodingException  If the named encoding is not supported
	 */
	public static byte[] getBytes(String str, String enc) throws UnsupportedEncodingException {
		String encoding = (enc == null || enc.length() == 0) ? defaultEncoding : enc;
		return str.getBytes(encoding);
	}

	/**
	 * Convert this StringBuffer into bytes according to the specified character
	 * encoding, storing the result into a new byte array. If no encoding
	 * is specified the platform default encoding is used.
	 * @param enc  the name of a supported character encoding, or null if the
	 * platform default encoding should be used.
	 * @return The resultant byte array
	 * @throws UnsupportedEncodingException  If the named encoding is not supported
	 */
	public static byte[] getBytes(StringBuffer str, String enc) throws UnsupportedEncodingException {
		String encoding = (enc == null || enc.length() == 0) ? defaultEncoding : enc;
		return str.toString().getBytes(encoding);
	}

	/**
	 * Returns the plattform specific default character encoding.
	 * @return a character encoding.
	 */
	public static String getDefaultEncoding() {
		return defaultEncoding;
	}


	// Maps bytes to hex characters
	private static final char[] nibbleChar =
		new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

	private static final byte[] digits =
		new byte[] {
			 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1, 
			-1, 10, 11, 12, 13, 14, 15,	-1, -1, -1, -1, -1, -1, -1, -1, -1, 
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	-1, -1, -1, 
			-1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1
		};

	// The map for the first byte of three (b1+b2+b3)
	// - just taking b1
	//
	private static final char[] map0 =
		new char[] {
		    'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B',	    
		    'C', 'C', 'C', 'C', 'D', 'D', 'D', 'D',	    
		    'E', 'E', 'E', 'E', 'F', 'F', 'F', 'F',
		    'G', 'G', 'G', 'G', 'H', 'H', 'H', 'H',	    
		    'I', 'I', 'I', 'I', 'J', 'J', 'J', 'J',	    
		    'K', 'K', 'K', 'K', 'L', 'L', 'L', 'L',
		    'M', 'M', 'M', 'M', 'N', 'N', 'N', 'N',	    
		    'O', 'O', 'O', 'O', 'P', 'P', 'P', 'P',	    
		    'Q', 'Q', 'Q', 'Q', 'R', 'R', 'R', 'R',
		    'S', 'S', 'S', 'S', 'T', 'T', 'T', 'T',	    
		    'U', 'U', 'U', 'U', 'V', 'V', 'V', 'V',	    
		    'W', 'W', 'W', 'W', 'X', 'X', 'X', 'X',
		    'Y', 'Y', 'Y', 'Y', 'Z', 'Z', 'Z', 'Z',	    
		    'a', 'a', 'a', 'a', 'b', 'b', 'b', 'b',	    
		    'c', 'c', 'c', 'c', 'd', 'd', 'd', 'd',
		    'e', 'e', 'e', 'e', 'f', 'f', 'f', 'f',	    
		    'g', 'g', 'g', 'g', 'h', 'h', 'h', 'h',	    
		    'i', 'i', 'i', 'i', 'j', 'j', 'j', 'j',
		    'k', 'k', 'k', 'k', 'l', 'l', 'l', 'l',	    
		    'm', 'm', 'm', 'm', 'n', 'n', 'n', 'n',	    
		    'o', 'o', 'o', 'o', 'p', 'p', 'p', 'p',
		    'q', 'q', 'q', 'q', 'r', 'r', 'r', 'r',	    
		    's', 's', 's', 's', 't', 't', 't', 't',	    
		    'u', 'u', 'u', 'u', 'v', 'v', 'v', 'v',
		    'w', 'w', 'w', 'w', 'x', 'x', 'x', 'x',	    
		    'y', 'y', 'y', 'y', 'z', 'z', 'z', 'z',	    
		    '0', '0', '0', '0', '1', '1', '1', '1',
		    '2', '2', '2', '2', '3', '3', '3', '3',	    
		    '4', '4', '4', '4', '5', '5', '5', '5',	    
		    '6', '6', '6', '6', '7', '7', '7', '7',
		    '8', '8', '8', '8', '9', '9', '9', '9',	    
		    '+', '+', '+', '+', '/', '/', '/', '/'
		};

	// The map for the first+second byte of three (b1+b2+b3)
	// - just taking (b1 & 0x03) + (b2 & 0xf0)
	//
	private static final char[] map1 =
		new char[] {
		    'A', 'Q', 'g', 'w', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'B', 'R', 'h', 'x', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'C', 'S', 'i', 'y', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    'D', 'T', 'j', 'z', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'E', 'U', 'k', '0', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'F', 'V', 'l', '1', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    'G', 'W', 'm', '2', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'H', 'X', 'n', '3', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'I', 'Y', 'o', '4', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    'J', 'Z', 'p', '5', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'K', 'a', 'q', '6', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'L', 'b', 'r', '7', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    'M', 'c', 's', '8', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'N', 'd', 't', '9', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'O', 'e', 'u', '+', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    'P', 'f', 'v', '/', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '
		};

	// The map for the second+third byte of three (b1+b2+b3)
	// - just taking (b2 & 0x0f) + (b3 & 0xc0)
	//
	private static final char[] map2 =
		new char[] {
		    'A', 'E', 'I', 'M', 'Q', 'U', 'Y', 'c',	    
		    'g', 'k', 'o', 's', 'w', '0', '4', '8',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'B', 'F', 'J', 'N', 'R', 'V', 'Z', 'd',
		    'h', 'l', 'p', 't', 'x', '1', '5', '9',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    'C', 'G', 'K', 'O', 'S', 'W', 'a', 'e',	    
		    'i', 'm', 'q', 'u', 'y', '2', '6', '+',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    'D', 'H', 'L', 'P', 'T', 'X', 'b', 'f',	    
		    'j', 'n', 'r', 'v', 'z', '3', '7', '/',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',	    
		    ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '		
		};

	// The map for the third byte of three (b1+b2+b3)
	// - just taking b3
	//
	private static final char[] map3 =
		new char[] {
		    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',	    
		    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',	    
		    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',	    
		    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',	    
		    'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		    'w', 'x', 'y', 'z', '0', '1', '2', '3',	    
		    '4', '5', '6', '7', '8', '9', '+', '/',	    
		    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',	    
		    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',	    
		    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',	    
		    'o', 'p', 'q', 'r', 's', 't', 'u', 'v',	    
		    'w', 'x', 'y', 'z', '0', '1', '2', '3',
		    '4', '5', '6', '7', '8', '9', '+', '/',	    
		    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',	    
		    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',	    
		    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',	    
		    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		    'o', 'p', 'q', 'r', 's', 't', 'u', 'v',	    
		    'w', 'x', 'y', 'z', '0', '1', '2', '3',	    
		    '4', '5', '6', '7', '8', '9', '+', '/',
		    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',	    
		    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',	    
		    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',	    
		    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',	    
		    'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		    'w', 'x', 'y', 'z', '0', '1', '2', '3',	    
		    '4', '5', '6', '7', '8', '9', '+', '/'		
		 };

	// reverse map the coding chars to 6-bit numbers
	//
	private static final int[] rmap =
		new int[] {
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
		    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
		    -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
		    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
		    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
		    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,			
		};

	private static final String[] hex = {
		"%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
		"%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f",
		"%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
		"%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f",
		"%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
		"%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f",
		"%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
		"%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
		"%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
		"%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f",
		"%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
		"%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
		"%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
		"%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f",
		"%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
		"%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
		"%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
		"%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
		"%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
		"%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f",
		"%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
		"%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af",
		"%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7",
		"%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf",
		"%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
		"%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf",
		"%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7",
		"%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df",
		"%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
		"%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
		"%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
		"%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"	
	};

}

