package com.tssap.dtr.client.lib.protocol.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to parse a string into tokens or parts.
 */
public final class Tokenizer {
	private char[] chars;
	private int pos = 0;

	/**
	 * Creates a tokenizer for the given string.
	 * @param s  the string to split into tokens
	 */
	public Tokenizer(String s) {
		chars = new char[s.length()];
		s.getChars(0, chars.length, chars, 0);
	}

	/**
	 * Returns the next token. This method uses <code>Character.isWhitespace(])</code>
	 * to split the specified string into tokens.
	 * @return the next token.
	 */
	public String nextToken() {
		if (pos == chars.length)
			return null;
		while (pos < chars.length && Character.isWhitespace(chars[pos])) {
			++pos;
		}
		int start = pos;
		while (pos < chars.length && !Character.isWhitespace(chars[pos])) {
			++pos;
		}
		return new String(chars, start, pos - start);
	}

	/**
	 * Returns the last token.
	 * @return the last token.
	 */
	public String lastToken() {
		if (pos == chars.length)
			return null;
		while (pos < chars.length && Character.isWhitespace(chars[pos])) {
			++pos;
		}
		int start = pos;
		pos = chars.length;
		return new String(chars, start, pos - start);
	}

	/**
	 * Divides the specified string <code>str</code> into parts where the parts
	 * are separated by given part separators. The list of part separators
	 * is provided in the parameter <code>partsSeperator</code>. The method
	 * recognized quoted strings and ignores part separators inside the quotes.
	 * @param str The string to parse.
	 * @param partSeparators The separator characters to search for.
	 * @return An array of strings that represent the parts of the input. If no parts
	 * could be found the result vector is empty.
	 * @throws IllegalArgumentException if <code>str</code> is null or no
	 * part separators are defined.
	 */
	public static List partsOf(String str, String partSeparators) {
		return partsOf(str, partSeparators, 0, -1);
	}

	/**
	 * Divides the specified string <code>str</code> into parts where the parts
	 * are separated by given part separators. The list of part separators
	 * is provided in the parameter <code>partsSeperator</code>. The method
	 * recognized quoted strings and ignores part separators inside the quotes.
	 * @param str The string to parse.
	 * @param partSeparators The separator characters to search for.
	 * @param off the startin offset in str
	 * @return An array of strings that represent the parts of the input. If no parts
	 * could be found the result vector is empty.
	 * @throws IllegalArgumentException if <code>str</code> is null or no
	 * part separators are defined.
	 */
	public static List partsOf(String str, String partSeparators, int off) {
		return partsOf(str, partSeparators, off, -1);
	}

	/**
	 * Divides the specified string <code>str</code> into parts where the parts
	 * are separated by given part separators. The list of part separators
	 * is provided in the parameter <code>partsSeperator</code>. The method
	 * recognized quoted strings and ignores part separators inside the quotes.
	 * @param str The string to parse.
	 * @param partSeparators the separator characters to search for.
	 * @param off the startin offset in str*
	 * @param pairSeperator the seperator character used to instantiate Pairs
	 * from the found parts. If pairSeperator is set to -1 the parts are returned
	 * as strings.
	 * @return An array of Pairs that represent the parts of the input. If no parts
	 * could be found the result vector is empty.
	 * @throws IllegalArgumentException if <code>str</code> is null or no
	 * part separators are defined.
	 */
	public static List partsOf(String str, String partSeparators, int off, int pairSeparator) {
		if (str == null || partSeparators == null || partSeparators.length() == 0) {
			throw new java.lang.IllegalArgumentException();
		}

		ArrayList result = new ArrayList();
		char[] s = str.toCharArray();
		int pos = off;
		int beginToken = 0;
		int endToken = 0;
		boolean quoted = false;
		boolean skipLeadingWS = true;
		boolean readingWS = true;

		while (pos < s.length) {
			char c = s[pos++];
			if (c == '"' || c == '\'') {
				quoted = !quoted;
			}
			if (!quoted) {
				if (Character.isWhitespace(c)) {
					readingWS = true;
				} else if (partSeparators.indexOf(c) != -1) {
					if (beginToken < endToken) {
						String token = new String(s, beginToken, endToken - beginToken);
						if (pairSeparator == -1) {
							result.add(token);
						} else {
							result.add(new Pair(token, (char) pairSeparator, true));
						}
						beginToken = endToken;
					}
					skipLeadingWS = true;
					readingWS = true;
				} else {
					if (readingWS && skipLeadingWS) {
						beginToken = pos - 1;
					}
					endToken = pos;
					skipLeadingWS = false;
					readingWS = false;
				}
			} else {
				endToken++;
			}
		}
		// add the last part
		if (beginToken < endToken) {
			String token = new String(s, beginToken, endToken - beginToken);
			if (pairSeparator == -1) {
				result.add(token);
			} else {
				result.add(new Pair(token, (char) pairSeparator, true));
			}
		}
		return result;
	}

}
