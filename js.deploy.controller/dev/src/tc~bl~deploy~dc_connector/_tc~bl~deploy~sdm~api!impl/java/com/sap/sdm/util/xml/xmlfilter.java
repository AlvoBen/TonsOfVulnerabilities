package com.sap.sdm.util.xml;

/**
 * This class provides methods responsible for:
 * <ul>
 * <li>replacing some characters in XML String before transmitting it. In such
 * case the opposite side XML Parser will build aprropriate XML elements with no
 * confusion</li>
 * <li>
 * replacing on the other side some character consequences with valid
 * character(only one)</li>
 * </ul>
 * Code character set is based on ISO-8859-1.
 * 
 * @author Lalo Ivanov
 * 
 */
public final class XMLFilter {

	public final static String AMP = "&amp;";
	public final static String GT = "&gt;";
	public final static String LT = "&lt;";
	public final static String QUOT = "&quot;";

	public final static String BEGIN_CDATA = "<![CDATA[";
	public final static String END_CDATA = "]]>";

/**
   * Replaces characters like '<', '>', '"' in XML, so XML
   * Parsers not to be confused i.e. from attribute values
   * and tag contents containing such char values.
   * @return plain text containing no special characters
   *          in terms of XML parsing
   */
	public static String filterOutput(String xml) {
		StringBuffer filtered = new StringBuffer((int) (xml.length() * 1.1));
		char c;
		for (int i = 0; i < xml.length(); i++) {
			c = xml.charAt(i);
			switch (c) {
			case '<':
				filtered.append(LT);
				break;
			case '>':
				filtered.append(GT);
				break;
			case '"':
				filtered.append(QUOT);
				break;
			case '&':
				filtered.append(AMP);
				break;
			case 0xa9: // copyright sign
				filtered.append("(c)");
				break;
			// /////////////////////////////////////
			// Some German characters - do not have
			// backward shift
			// /////////////////////////////////////
			case 0xc4: // A umlaut
				filtered.append("A");
				break;
			case 0xe4: // a umlaut
				filtered.append("a");
				break;
			case 0xd6: // O umlaut
				filtered.append("O");
				break;
			case 0xf6: // o umlaut
				filtered.append("o");
				break;
			case 0xdc: // U umlaut
				filtered.append("U");
				break;
			case 0xfc: // u umlaut
				filtered.append("u");
				break;
			default:
				filtered.append(c);
			}
		}
		return (filtered.toString());
	}

	public static String checkAndFilterOutput(String text) {
		if (!text.startsWith(BEGIN_CDATA) || !text.endsWith(END_CDATA)) {
			return filterOutput(text);
		} else {
			return text;
		}
	}

	/**
	 * Should be used with care since escaping several times given input XML
	 * String will bring different result.\ As an example : "&amp;amp;"
	 */
	public static String filterInput(String text) {
		StringBuffer filtered = new StringBuffer((int) (text.length() * 0.9));
		int counter = 0;
		while (counter < text.length()) {
			char c = text.charAt(counter);
			if (c == '&') {
				if (text.regionMatches(true, counter, AMP, 0, AMP.length())) {
					filtered.append('&');
					counter += AMP.length();
				} else if (text
						.regionMatches(true, counter, LT, 0, LT.length())) {
					filtered.append('<');
					counter += LT.length();
				} else if (text
						.regionMatches(true, counter, GT, 0, GT.length())) {
					filtered.append('>');
					counter += GT.length();
				} else if (text.regionMatches(true, counter, QUOT, 0, QUOT
						.length())) {
					filtered.append('"');
					counter += QUOT.length();
				} else {
					filtered.append('&');
					counter++;
				}
			} else {
				filtered.append(c);
				counter++;
			}
		}

		return filtered.toString();
	}

	public static String checkAndFilterInput(String text) {
		if (text.startsWith(BEGIN_CDATA) && text.endsWith(END_CDATA)) {
			return text.substring(BEGIN_CDATA.length(), text.length()
					- END_CDATA.length());
		} else {
			return filterInput(text);
		}
	}
}
