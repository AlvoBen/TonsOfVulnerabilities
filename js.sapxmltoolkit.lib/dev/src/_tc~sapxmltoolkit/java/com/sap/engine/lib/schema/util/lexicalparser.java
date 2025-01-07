package com.sap.engine.lib.schema.util;

import com.sap.engine.lib.xml.parser.helpers.UTF8Encoding;
import com.sap.engine.lib.xml.util.BASE64Decoder;
import com.sap.engine.lib.xml.Symbols;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-1-7
 * Time: 10:36:37
 * @deprecated Please use javax.xml.datatype.DatatypeFactory 
 */
@Deprecated
public final class LexicalParser {
	
  public static String parseURIReference(String value) {
    if(value == null) {
      return(null);
    }
    LexicalCharsTokenizer tokenizer = new LexicalCharsTokenizer(excapeNonExpectedURIChars(value), new String[]{"//"});
    if(!parseURIReference(tokenizer) || tokenizer.peek() != null) {
      return(null);
    }
    return(value);
  }
  
	private static String excapeNonExpectedURIChars(String uriReference) {
		StringBuffer buffer = new StringBuffer(uriReference);
		byte[] utf8Bytes = new byte[6];
		UTF8Encoding utf8Encoding = new UTF8Encoding(); 
		for(int i = 0; i < buffer.length(); i++) {
			char ch = buffer.charAt(i);
			if((ch >= 0x00 && ch <= 0x001f) || ch >= 0x007f || ch == '<' || ch == '>' || ch == '"' || ch == '{' || ch == '}' || ch == '|' || ch == '\\' || ch == '^' || ch == '\'' || (ch == 0x0020 && (i != 0 || buffer.charAt(i - 1) != '%'))) {
				int length = utf8Encoding.reverseEncode(utf8Bytes, ch);
				String escapedCharacterSequence = "";
				for(int j = 0; j < length; j++) {
					escapedCharacterSequence += "%" + Integer.toHexString((int)utf8Bytes[j] & 0xff);  
				}
				buffer.replace(i, i + 1, escapedCharacterSequence);
				i += escapedCharacterSequence.length() - 1;
			}
		}
		return(buffer.toString());
	}
  
  public static GregorianCalendar parseDateTimeZone(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-", "Z", "z", "+", "-"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseDateTimeZone(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static GregorianCalendar parseDateTime(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-", "T", "t", "+", "-", ":", "Z", "z"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseDateTime(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static Duration parseDuration(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-", "P", "T", "Y", "M", "D", "H", "M", "S", "p", "t", "y", "m", "d", "h", "m", "s"});
    Duration duration = new Duration();
    if(!parseDuration(tokenizer, duration) || tokenizer.peek() != null) {
      return(null);
    }
    return(duration);
  }

  public static GregorianCalendar parseDayTimeZone(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-", "Z", "z", "-", "+"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseDayTimeZone(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static GregorianCalendar parseMonthTimeZone(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-", "Z", "z", "-", "+"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseMonthTimeZone(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static GregorianCalendar parseMonthDayTimeZone(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-", "Z", "z", "+", "-"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseMonthDayTimeZone(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static GregorianCalendar parseYearTimeZone(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"Z", "z", "+", "-"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseYearTimeZone(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static GregorianCalendar parseYearMonthTimeZone(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-", "Z", "z", "+", "-"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseYearMonthTimeZone(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static GregorianCalendar parseTime(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{":", "Z", "z", "+", "-"});
    GregorianCalendar calendar = createEmptyCalendar();
    if(!parseTime(tokenizer, calendar) || tokenizer.peek() != null) {
      return(null);
    }
    return(calendar);
  }

  public static byte[] parseBase64Binary(String value) {
    if(value == null) {
      return(null);
    }
    return(BASE64Decoder.decode(value.getBytes())); //$JL-I18N$
  }

  public static Boolean parseBoolean(String value) {
    if(value == null) {
      return(null);
    }
    if(value.toLowerCase(Locale.ENGLISH).equals("true") || value.equals("1")) {
      return(new Boolean("true"));
    }
    if(value.toLowerCase(Locale.ENGLISH).equals("false") || value.equals("0")) {
      return(new Boolean("false"));
    }
    return(null);
  }

  public static BigDecimal parseDecimal(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"+", "-", "."});    
    return(parseDecimal(tokenizer) ? new BigDecimal(value) : null);
  }
  
  private static boolean parseDecimal(LexicalTokenizer tokenizer) {
    String token = tokenizer.peek();
    if(token == null) {
      return(false);
    }
    if(token.equals("+") || token.equals("-")) {
      tokenizer.next();
    }
    int index = tokenizer.getIndex();
    if(parseDecimalWithFractionPart(tokenizer)) {
      return(tokenizer.next() == null);
    }
    tokenizer.setIndex(index);
    return(parseDecimalFractionPart(tokenizer) && tokenizer.next() == null);
  }
  
  private static boolean parseDecimalWithFractionPart(LexicalTokenizer tokenizer) {
    if(!parseNumber(tokenizer)) {
      return(false);
    }
    if(tokenizer.peek() != null) {
      return(parseDecimalFractionPart(tokenizer));
    }
    return(true);
  }
  
  private static boolean parseDecimalFractionPart(LexicalTokenizer tokenizer) {
    String token = tokenizer.next();
    if(!".".equals(token)) {
      return(false);
    }
    return(parseNumber(tokenizer));
  }

  public static BigDecimal parseDouble(String value) {
    if(value == null) {
      return(null);
    }
    try {
      Double doubleValue = new Double(value);
    } catch(NumberFormatException exc) {
      //$JL-EXC$
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseFloat(String value) {
    if(value == null) {
      return(null);
    }
    try {
      Float doubleValue = new Float(value);
    } catch(NumberFormatException exc) {
      //$JL-EXC$
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static String parseQName(String value) {
    if(value == null) {
      return(null);
    }
    LexicalCharsTokenizer tokenizer = new LexicalCharsTokenizer(value, null);
    if(!parseQName(tokenizer) || tokenizer.peek() != null) {
      return(null);
    }
    return(value);
  }

  public static String parseNormalizedString(String value) {
    if(value == null) {
      return(null);
    }
    for(int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if(ch == '\n' || ch == '\t' || ch == '\r') {
        return(null);
      }
    }
    return(value);
  }

  public static BigDecimal parseInteger(String value) {
    if(value == null) {
      return(null);
    }
    try {
      new BigInteger(value);
    } catch(NumberFormatException numberFormExc) {
      //$JL-EXC$
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static String parseToken(String value) {
    if(value == null) {
      return(null);
    }
    if(value.equals("")) {
      return(value);
    }
    if(value.charAt(0) == ' ') {
      return(null);
    }
    if(value.length() > 1 && value.charAt(value.length() - 1) == ' ') {
      return(null);
    }
    for(int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if(ch == ' ') {
        if(i - 1 >= 0 && value.charAt(i - 1) == ' ') {
          return(null);
        }
      } else if(ch == '\t' || ch == '\n') {
        return(null);
      }
    }
    return(value);
  }

  public static BigDecimal parseNonPositiveInteger(String value) {
    if(parseLimitedInt_UpperLimit(value, "0") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseNonNegativeInteger(String value) {
    if(parseLimitedInt_LowerLimit(value, "0") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseLong(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "-9223372036854775808", "9223372036854775807") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static String parseLanguage(String value) {
    if(value == null) {
      return(null);
    }
    LexicalTokenizer tokenizer = new LexicalTokenizer(value, new String[]{"-"});
    if(!parseLanguage(tokenizer)) {
      return(null);
    }
    return(value);
  }

  public static String parseName(String value) {
    if(value == null) {
      return(null);
    }
    LexicalCharsTokenizer tokenizer = new LexicalCharsTokenizer(value, null);
    if(!parseName(tokenizer) && tokenizer.peek() != null) {
      return(null);
    }
    return(value);
  }

  public static String parseNmtoken(String value) {
    if(value == null) {
      return(null);
    }
    LexicalCharsTokenizer tokenizer = new LexicalCharsTokenizer(value, null);
    if(!parseNmtoken(tokenizer) && tokenizer.peek() != null) {
      return(null);
    }
    return(value);
  }

  public static BigDecimal parseNegativeInteger(String value) {
    if(parseLimitedInt_UpperLimit(value, "-1") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseInt(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "-2147483648", "2147483647") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseUnsignedLong(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "0", "18446744073709551615") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parsePositiveInteger(String value) {
    if(parseLimitedInt_LowerLimit(value, "1") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static String parseNCName(String value) {
    if(value == null) {
      return(null);
    }
    LexicalCharsTokenizer tokenizer = new LexicalCharsTokenizer(value, null);
    if(!parseNCName(tokenizer) || tokenizer.peek() != null) {
      return(null);
    }
    return(value);
  }

  public static BigDecimal parseShort(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "-32768", "32767") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseUnsignedInt(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "0", "4294967295") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseByte(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "-128", "127") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseUnsignedShort(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "0", "65535") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static BigDecimal parseUnsignedByte(String value) {
    if(parseLimitedInt_LowerAndUpperLimit(value, "0", "255") == null) {
      return(null);
    }
    return(new BigDecimal(value));
  }

  public static byte[] parseHexBinary(String value) {
    byte[] result = null;
    if(value != null) {
      if(value.length() == 0) {
        result = new byte[0];
      }
      int size = value.length()/2;
      result = new byte[size];
      int pos = 0;
      for (int i = 0; i < size; i++) {
        char highbit = value.charAt(pos);
        char lowbit = value.charAt(pos+1);
        byte highbits = 0;
        byte lowbits = 0;
        if (highbit >= '0' && highbit <= '9') {
          highbits = (byte) (9 - ('9' - highbit));
        }
        if (highbit >= 'a' && highbit <= 'f') {
          highbits = (byte) (15 - ('f' - highbit));
        }
        if (highbit >= 'A' && highbit <= 'F') {
          highbits = (byte) (15 - ('F' - highbit));
        }
        if (lowbit >= '0' && lowbit <= '9') {
          lowbits = (byte) (9 - ('9' - lowbit));
        }
        if (lowbit >= 'a' && lowbit <= 'f') {
          lowbits = (byte) (15 - ('f' - lowbit));
        }
        if (lowbit >= 'A' && lowbit <= 'F') {
          lowbits = (byte) (15 - ('F' - lowbit));
        }
        result[i] = (byte) ((highbits << 4) | (lowbits));
        pos += 2;
      }
    }
    return(result);
  }

  public static boolean parseXPathSelector(String value) {
    if(value == null) {
      return(false);
    }
    LexicalCharsTokenizer tokenizer = new LexicalCharsTokenizer(value, new String[]{".//", "/", ".", "*", ":", "|", "_", "-"});
    if(!parseXPathSelector(tokenizer) || tokenizer.peek() != null) {
      return(false);
    }
    return(true);
  }

  public static boolean parseXPathField(String value) {
    if(value == null) {
      return(false);
    }
    LexicalCharsTokenizer tokenizer = new LexicalCharsTokenizer(value, new String[]{".//", "/", ".", "*", ":", "@", "_", "-", "|"});
    if(!parseXPathField(tokenizer) || tokenizer.peek() != null) {
      return(false);
    }
    return(true);
  }

  private static boolean parseXPathFieldPath(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    String token = tokenizer.next();
    if(token == null || !token.equals(".//")) {
      tokenizer.setIndex(index);
    }
    while(true) {
      if(tokenizer.peek() != null) {
        index = tokenizer.getIndex();
        boolean result = parseXPathStep(tokenizer);
        if(result) {
          token = tokenizer.next();
          result = token != null && token.equals("/");
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    index = tokenizer.getIndex();
    if(!parseXPathStep(tokenizer)) {
      tokenizer.setIndex(index);
      token = tokenizer.next();
      if(token == null || !token.equals("@")) {
        return(false);
      }
      if(!parseXPathNameTest(tokenizer)) {
        return(false);
      }
    }
    return(true);
  }

  private static boolean parseXPathSelector(LexicalCharsTokenizer tokenizer) {
    if(!parseXPathSelectorPath(tokenizer)) {
      return(false);
    }
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        String token = tokenizer.next();
        boolean result = token != null && token.equals("|");
        if(result) {
          result = parseXPathSelectorPath(tokenizer);
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseXPathField(LexicalCharsTokenizer tokenizer) {
    if(!parseXPathFieldPath(tokenizer)) {
      return(false);
    }
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        String token = tokenizer.next();
        boolean result = token != null && token.equals("|");
        if(result) {
          result = parseXPathFieldPath(tokenizer);
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseXPathSelectorPath(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    String token = tokenizer.next();
    if(token == null || !token.equals(".//")) {
      tokenizer.setIndex(index);
    }
    if(!parseXPathStep(tokenizer)) {
      return(false);
    }
    while(true) {
      if(tokenizer.peek() != null) {
        index = tokenizer.getIndex();
        token = tokenizer.next();
        boolean result = token != null && token.equals("/");
        if(result) {
          result = parseXPathStep(tokenizer);
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseXPathStep(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    String token = tokenizer.next();
    if(token != null && token.equals(".")) {
      return(true);
    }
    tokenizer.setIndex(index);
    if(!parseXPathNameTest(tokenizer)) {
      return(false);
    }
    return(true);
  }

  private static boolean parseXPathNameTest(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseQName(tokenizer)) {
      tokenizer.setIndex(index);
      String token = tokenizer.next();
      if(token == null) {
        return(false);
      }
      if(!token.equals("*")) {
        tokenizer.setIndex(index);
        if(!parseNCName(tokenizer)) {
          return(false);
        }
        token = tokenizer.next();
        if(token == null || !token.equals(":")) {
          return(false);
        }
        token = tokenizer.next();
        if(token == null || !token.equals("*")) {
          return(false);
        }
      }
    }
    return(true);
  }

  private static BigInteger parseLimitedInt_LowerAndUpperLimit(String value, String lowerLimit, String upperLimit) {
    BigInteger result = null;
    try {
      result = new BigInteger(removePositiveSign(value));
    } catch(NumberFormatException numberFormExc) {
      //$JL-EXC$
      return(null);
    }
    if(result.compareTo(new BigInteger(lowerLimit)) < 0 || result.compareTo(new BigInteger(upperLimit)) > 0) {
      return(null);
    }
    return(result);
  }
  
  private static String removePositiveSign(String value) {
    return(value.startsWith("+") ? value.substring(1) : value);
  }

  private static BigInteger parseLimitedInt_UpperLimit(String value, String upperLimit) {
    BigInteger result = null;
     try {
       result = new BigInteger(value);
     } catch(NumberFormatException numberFormExc) {
       //$JL-EXC$
       return(null);
     }
     if(result.compareTo(new BigInteger(upperLimit)) > 0) {
      return(null);
    }
    return(result);
  }

  private static BigInteger parseLimitedInt_LowerLimit(String value, String lowerLimit) {
    BigInteger result = null;
     try {
       result = new BigInteger(value);
     } catch(NumberFormatException numberFormExc) {
       //$JL-EXC$
       return(null);
     }
     if(result.compareTo(new BigInteger(lowerLimit)) < 0) {
      return(null);
    }
    return(result);
  }

  private static BigDecimal parseLimitedDecimal(String value, String lowerLimit, String upperLimit) {
    BigDecimal result = parseDecimal(value);
    if(result == null) {
      return(null);
    }
    if(result.compareTo(new BigDecimal(lowerLimit)) <= 0 || result.compareTo(new BigDecimal(upperLimit)) >= 0) {
      return(null);
    }
    return(result);
  }

  private static boolean parseYearMonthTimeZone(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    if(!parseYearMonth(tokenizer, calendar)) {
      return(false);
    }
    return(parseTimeZone(tokenizer, calendar) && tokenizer.peek() == null);
  }

  private static boolean parseYearTimeZone(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    if(!parseYear(tokenizer, calendar)) {
      return(false);
    }
    return(parseTimeZone(tokenizer, calendar) && tokenizer.peek() == null);
  }

  private static boolean parseMonthDayTimeZone(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    String token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    if(!parseMonth(tokenizer, calendar)) {
      return(false);
    }
    String monthToken = tokenizer.current();
    token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    if(!parseDay(tokenizer, calendar, monthToken, null)) {
      return(false);
    }
    return(parseTimeZone(tokenizer, calendar) && tokenizer.peek() == null);
  }

  private static boolean parseMonthTimeZone(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    String token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    if(!parseMonth(tokenizer, calendar)) {
      return(false);
    }
    token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    return(parseTimeZone(tokenizer, calendar) && tokenizer.peek() == null);
  }

  private static boolean parseDayTimeZone(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    String token = null;
    int counter = 0;
    while("-".equals(tokenizer.next()) && ++counter < 3);
    if(counter < 3) {
      return(false);
    }
    if(!parseDay(tokenizer, calendar, null, null)) {
      return(false);
    }
    return(parseTimeZone(tokenizer, calendar) && tokenizer.peek() == null);
  }

  private static boolean parseDateTime(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    if(!parseDate(tokenizer, calendar)) {
      return(false);
    }
    String token = tokenizer.next();
    if(token == null || !token.toLowerCase(Locale.ENGLISH).equals("t")) {
      return(false);
    }
    return(parseTime(tokenizer, calendar) && tokenizer.peek() == null);
  }

  private static boolean parseDateTimeZone(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    if(!parseDate(tokenizer, calendar)) {
      return(false);
    }
    return(parseTimeZone(tokenizer, calendar) && tokenizer.peek() == null);
  }

  private static boolean parseNumber(LexicalTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || token.length() == 0) {
      return(false);
    }
    for(int i = 0; i < token.length(); i++) {
      char ch = token.charAt(i);
      if(ch < '0' || ch > '9') {
        return(false);
      }
    }
    return(true);
  }
  
  private static int parseNumber(LexicalTokenizer tokenizer, int filedLength) {
    String token = tokenizer.next();
    if(token == null) {
      return(-1);
    }
    if(token.length() != filedLength) {
      return(-1);
    }
    try {
      return(Integer.parseInt(token));
    } catch(NumberFormatException numberFormExc) {
      //$JL-EXC$
      return(-1);
    }
  }

  private static boolean parseYear(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    String token = tokenizer.next();
    if(token == null) {
      return(false);
    }
    if(token.length() < 4) {
      return(false);
    }
    if(token.equals("0000")) {
      return(false);
    }
    try {
      int year = Integer.parseInt(token);
      calendar.set(Calendar.YEAR, year);
    } catch(NumberFormatException numberFormExc) {
      //$JL-EXC$
      return(false);
    }
    return(true);
  }

  private static boolean parseMonth(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    int month = parseNumber(tokenizer, 2);
    if(month < 1 || month > 13) {
      return(false);
    }
    switch(month) {
      case(1) : {
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        break;
      }
      case(2) : {
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        break;
      }
      case(3) : {
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        break;
      }
      case(4) : {
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        break;
      }
      case(5) : {
        calendar.set(Calendar.MONTH, Calendar.MAY);
        break;
      }
      case(6) : {
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        break;
      }
      case(7) : {
        calendar.set(Calendar.MONTH, Calendar.JULY);
        break;
      }
      case(8) : {
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        break;
      }
      case(9) : {
        calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        break;
      }
      case(10) : {
        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        break;
      }
      case(11) : {
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        break;
      }
      case(12) : {
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        break;
      }
    }
    return(true);
  }

  private static boolean parseDay(LexicalTokenizer tokenizer, GregorianCalendar calendar, String monthToken, String yearToken) {
    int day = parseNumber(tokenizer, 2);
    if(day < 1) {
      return(false);
    }
    int month = monthToken == null ? -1 : Integer.parseInt(monthToken);
    if(month == 2) {
      int year = yearToken == null ? -1 : Integer.parseInt(yearToken);
      int dayLimit = year < 0 || calendar.isLeapYear(year) ? 29 : 28; 
      if(day > dayLimit) {
        return(false);
      }
    } else if(month == -1) {
      if(day > 31) {
        return(false);
      }
    } else if(month < 8) {
      if(month % 2 != 0) {
        if(day > 31) {
          return(false);
        }
      } else if(day > 30) {
        return(false);
      }
    } else if(month % 2 != 0) {
      if(day > 30) {
        return(false);
      }
    } else if(day > 31) {
      return(false);
    }
    calendar.set(Calendar.DAY_OF_MONTH, day);
    return(true);
  }

  private static boolean parseHour(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    int hour = parseNumber(tokenizer, 2);
    if(hour == -1 || hour > 24) {
      return(false);
    }
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    return(true);
  }

  private static boolean parseMinute(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    int minute = parseNumber(tokenizer, 2);
    if(minute == -1 || minute > 59) {
      return(false);
    }
    calendar.set(Calendar.MINUTE, minute);
    return(true);
  }

  private static boolean parseSeconds(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
  	String secondsToken = tokenizer.next();
  	if(secondsToken == null) {
  		return(false);
  	}
  	float seconds = -1;
  	try {
  		seconds = Float.parseFloat(secondsToken);
  	} catch(NumberFormatException numberFormExc) {
  		return(false);
  	}
    if(seconds < 0 || seconds > 60) {
      return(false);
    }
    calendar.set(Calendar.SECOND, (int)seconds);
    return(true);
  }

  private static boolean parseTimeZone(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    if(tokenizer.peek() != null) {
      int index = tokenizer.getIndex();
      String token = tokenizer.next();
      if(token.toLowerCase(Locale.ENGLISH).equals("z")) {
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
      } else {
        tokenizer.setIndex(index);
        String sign = tokenizer.next();
        if(sign == null) {
          return(false);
        }
        if(!sign.equals("-") && !sign.equals("+")) {
          return(false);
        }
        int hour = parseNumber(tokenizer, 2);
        if(hour == -1) {
          return(false);
        }
        token = tokenizer.next();
        if(token == null || !token.equals(":")) {
          return(false);
        }
        int minute = parseNumber(tokenizer, 2);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT" + sign + hour + ":" + minute));
        return(true);
      }
    }
    return(true);
  }

  private static boolean parseYearMonth(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    if(!parseYear(tokenizer, calendar)) {
      return(false);
    }
    String token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    return(parseMonth(tokenizer, calendar));
  }

  private static boolean parseDate(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    String yearToken = tokenizer.peek();
    if(!parseYearMonth(tokenizer, calendar)) {
      return(false);
    }
    String monthToken = tokenizer.current();
    String token = tokenizer.next();
    if(token == null || !token.equals("-")) {
      return(false);
    }
    return(parseDay(tokenizer, calendar, monthToken, yearToken));
  }

  private static boolean parseTime(LexicalTokenizer tokenizer, GregorianCalendar calendar) {
    if(!parseHour(tokenizer, calendar)) {
      return(false);
    }
    String token = tokenizer.next();
    if(token == null || !token.equals(":")) {
      return(false);
    }
    if(!parseMinute(tokenizer, calendar)) {
      return(false);
    }
    token = tokenizer.next();
    if(token == null || !token.equals(":")) {
      return(false);
    }
    if(!parseSeconds(tokenizer, calendar)) {
      return(false);
    }
    return(parseTimeZone(tokenizer, calendar));
  }

  private static boolean parseURIReference(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    boolean result = parseAbsoluteURI(tokenizer);
    if(!result) {
      tokenizer.setIndex(index);
      result = parseRelativeURI(tokenizer);
    }
    if(!result) {
      tokenizer.setIndex(index);
    }
    index = tokenizer.getIndex();
    String token = tokenizer.next();
    result = token != null && token.equals("#");
    if(result) {
      result = parseFragment(tokenizer);
    }
    if(!result) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseAbsoluteURI(LexicalCharsTokenizer tokenizer) {
    if(!parseSchema(tokenizer)) {
      return(false);
    }
    String token = tokenizer.next();
    if(token == null || !token.equals(":")) {
      return(false);
    }
    int index = tokenizer.getIndex();
    boolean result = parseHierPart(tokenizer);
    if(!result) {
      tokenizer.setIndex(index);
      return(parseOpaquePart(tokenizer));
    }
    return(true);
  }

  private static boolean parseRelativeURI(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    boolean result = parseNetPath(tokenizer);
    if(!result) {
      tokenizer.setIndex(index);
      result = parseAbsPath(tokenizer);
      if(!result) {
        tokenizer.setIndex(index);
        if(!parseRelPath(tokenizer)) {
          return(false);
        }
      }
    }
    index = tokenizer.getIndex();
    String token = tokenizer.next();
    result = token != null && token.equals("?");
    if(result) {
      result = parseQuery(tokenizer);
    }
    if(!result) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseHierPart(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    boolean result = parseNetPath(tokenizer);
    if(!result) {
      tokenizer.setIndex(index);
      if(!parseAbsPath(tokenizer)) {
        return(false);
      }
    }
    index = tokenizer.getIndex();
    String token = tokenizer.next();
    result = token != null && token.equals("?");
    if(result) {
      result = parseQuery(tokenizer);
    }
    if(!result) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseOpaquePart(LexicalCharsTokenizer tokenizer) {
    if(!parseURIcNoSlash(tokenizer)) {
      return(false);
    }
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        if(!parseURIC(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseURIcNoSlash(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    boolean result = parseUnreserved(tokenizer);
    if(!result) {
      tokenizer.setIndex(index);
      result = parseEscaped(tokenizer);
      if(!result) {
        tokenizer.setIndex(index);
        String token = tokenizer.next();
        if(token == null || !(token.equals(";") || token.equals("?") || token.equals(":") || token.equals("@") || token.equals("&") || token.equals("=") || token.equals("+") || token.equals("$") || token.equals(","))) {
          return(false);
        }
      }
    }
    return(true);
  }

  private static boolean parseNetPath(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || !token.equals("//")) {
      return(false);
    }
    if(!parseAuthority(tokenizer)) {
      return(false);
    }
    int index = tokenizer.getIndex();
    if(!parseAbsPath(tokenizer)) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseAbsPath(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || !token.equals("/")) {
      return(false);
    }
    return(parsePathSegments(tokenizer));
  }

  private static boolean parseRelPath(LexicalCharsTokenizer tokenizer) {
    if(!parseRelSegment(tokenizer)) {
      return(false);
    }
    int index = tokenizer.getIndex();
    if(!parseAbsPath(tokenizer)) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseRelSegment(LexicalCharsTokenizer tokenizer) {
    boolean result = false;
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        boolean parseResult = parseUnreserved(tokenizer);
        if(!parseResult) {
          tokenizer.setIndex(index);
          parseResult = parseEscaped(tokenizer);
          if(!parseResult) {
            tokenizer.setIndex(index);
            String token = tokenizer.next();
            parseResult = token != null && (token.equals(";") || token.equals("@") || token.equals("&") || token.equals("=") || token.equals("+") || token.equals("$") || token.equals(","));
          }
        }
        if(parseResult) {
          result = true;
        } else {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(result);
  }

  private static boolean parseSchema(LexicalCharsTokenizer tokenizer) {
    if(!parseAlpha(tokenizer)) {
      return(false);
    }
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        boolean result = parseAlpha(tokenizer);
        if(!result) {
          tokenizer.setIndex(index);
          result = parseDigit(tokenizer);
          if(!result) {
            tokenizer.setIndex(index);
            String token = tokenizer.next();
            result = token != null && (token.equals("+") || token.equals("-") || token.equals("."));
          }
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseAuthority(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseServer(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseRegName(tokenizer)) {
        return(false);
      }
    }
    return(true);
  }

  private static boolean parseRegName(LexicalCharsTokenizer tokenizer) {
    boolean result = false;
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        boolean parseResult = parseUnreserved(tokenizer);
        if(!parseResult) {
          tokenizer.setIndex(index);
          parseResult = parseEscaped(tokenizer);
          if(!parseResult) {
            tokenizer.setIndex(index);
            String token = tokenizer.next();
            parseResult = token != null && (token.equals("$") || token.equals(",") || token.equals(";") || token.equals(":") || token.equals("@") || token.equals("&") || token.equals("=") || token.equals("+"));
          }
        }
        if(parseResult) {
          result = true;
        } else {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(result);
  }

  private static boolean parseServer(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    boolean result = parseUserInfo(tokenizer);
    if(result) {
      String token = tokenizer.next();
      result = token != null && token.equals("@");
    }
    if(!result) {
      tokenizer.setIndex(index);
    }
    index = tokenizer.getIndex();
    if(!parseHostPort(tokenizer)) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseUserInfo(LexicalCharsTokenizer tokenizer) {
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        boolean parseResult = parseUnreserved(tokenizer);
        if(!parseResult) {
          tokenizer.setIndex(index);
          parseResult = parseEscaped(tokenizer);
          if(!parseResult) {
            tokenizer.setIndex(index);
            String token = tokenizer.next();
            parseResult = token != null && (token.equals(";") || token.equals(":") || token.equals("&") || token.equals("=") || token.equals("+") || token.equals("$") || token.equals(","));
          }
        }
        if(!parseResult) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseHostPort(LexicalCharsTokenizer tokenizer) {
    if(!parseHost(tokenizer)) {
      return(false);
    }
    int index = tokenizer.getIndex();
    String token = tokenizer.next();
    boolean result = token != null && token.equals(":");
    if(result) {
      result = parsePort(tokenizer);
    }
    if(!result) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseHost(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseHostName(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseIPv4Address(tokenizer)) {
        tokenizer.setIndex(index);
        if(!parseIPv6Reference(tokenizer)) {
          return(false);
        }
      }
    }
    return(true);
  }

  private static boolean parseHostName(LexicalCharsTokenizer tokenizer) {
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        boolean result = parseDomainLabel(tokenizer);
        if(result) {
          String token = tokenizer.next();
          result = token != null && token.equals(".");
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    if(!parseTopLabel(tokenizer)) {
      return(false);
    }
    int index = tokenizer.getIndex();
    String token = tokenizer.next();
    if(token == null || !token.equals(".")) {
      tokenizer.setIndex(index);
    }
    return(true);
  }

  private static boolean parseDomainLabel(LexicalCharsTokenizer tokenizer) {
    if(!parseAlphaNum(tokenizer)) {
      return(false);
    }
    boolean result = false;
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        result = parseAlphaNum(tokenizer);
        if(!result) {
          tokenizer.setIndex(index);
          String token = tokenizer.next();
          result = token != null && token.equals("-");
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseTopLabel(LexicalCharsTokenizer tokenizer) {
    if(!parseAlpha(tokenizer)) {
      return(false);
    }
    boolean result = false;
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        result = parseAlphaNum(tokenizer);
        if(!result) {
          tokenizer.setIndex(index);
          String token = tokenizer.next();
          result = token != null && token.equals("-");
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseIPv4Address(LexicalCharsTokenizer tokenizer) {
    for(int i = 0; i < 4; i++) {
      boolean hasDigit = false;
      while(true) {
        if(tokenizer.peek() != null) {
          int index = tokenizer.getIndex();
          if(parseDigit(tokenizer)) {
            hasDigit = true;
          } else {
            tokenizer.setIndex(index);
            break;
          }
        } else {
          break;
        }
      }
      if(!hasDigit) {
        return(false);
      }
      if(i < 3) {
        String token = tokenizer.next();
        if(token == null || !token.equals(".")) {
          return(false);
        }
      }
    }
    return(true);
  }

  private static boolean parsePort(LexicalCharsTokenizer tokenizer) {
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        if(!parseDigit(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parsePath(LexicalCharsTokenizer tokenizer) {
    if(tokenizer.peek() != null) {
      int index = tokenizer.getIndex();
      if(!parseAbsPath(tokenizer)) {
        tokenizer.setIndex(index);
        if(!parseOpaquePart(tokenizer)) {
          tokenizer.setIndex(index);
        }
      }
    }
    return(true);
  }

  private static boolean parsePathSegments(LexicalCharsTokenizer tokenizer) {
    if(!parseSegment(tokenizer)) {
      return(false);
    }
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        String token = tokenizer.next();
        boolean result = token != null && token.equals("/");
        if(result) {
          result = parseSegment(tokenizer);
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseSegment(LexicalCharsTokenizer tokenizer) {
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        if(!parsePChar(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        String token = tokenizer.next();
        boolean result = token != null && token.equals(";");
        if(result) {
          result = parseParam(tokenizer);
        }
        if(!result) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseParam(LexicalCharsTokenizer tokenizer) {
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        if(!parsePChar(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parsePChar(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseUnreserved(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseEscaped(tokenizer)) {
        tokenizer.setIndex(index);
        String token = tokenizer.next();
        if(token == null || !(token.equals(":") || token.equals("@") || token.equals("&") || token.equals("=") || token.equals("+") || token.equals("$") || token.equals(","))) {
          return(false);
        }
      }
    }
    return(true);
  }

  private static boolean parseQuery(LexicalCharsTokenizer tokenizer) {
    while(true) {
      int index = tokenizer.getIndex();
      if(!parseURIC(tokenizer)) {
        tokenizer.setIndex(index);
        break;
      }
    }
    return(true);
  }

  private static boolean parseFragment(LexicalCharsTokenizer tokenizer) {
    while(true) {
      int index = tokenizer.getIndex();
      if(!parseURIC(tokenizer)) {
        tokenizer.setIndex(index);
        break;
      }
    }
    return(true);
  }

  private static boolean parseURIC(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseReserved(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseUnreserved(tokenizer)) {
        tokenizer.setIndex(index);
        if(!parseEscaped(tokenizer)) {
          return(false);
        }
      }
    }
    return(true);
  }

  private static boolean parseReserved(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    return(token != null && (token.equals(";") || token.equals("/") || token.equals("?") || token.equals(":") || token.equals("@") || token.equals("&") || token.equals("=") || token.equals("+") || token.equals("$") || token.equals(",") || token.equals("[") || token.equals("]")));
  }

  private static boolean parseUnreserved(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseAlphaNum(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseMark(tokenizer)) {
        return(false);
      }
    }
    return(true);
  }

  private static boolean parseMark(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    return(token != null && (token.equals("-") || token.equals("_") || token.equals(".") || token.equals("!") || token.equals("~") || token.equals("*") || token.equals("'") || token.equals("(") || token.equals(")")));
  }

  private static boolean parseEscaped(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || !token.equals("%")) {
      return(false);
    }
    if(!parseHex(tokenizer)) {
      return(false);
    }
    if(!parseHex(tokenizer)) {
      return(false);
    }
    return(true);
  }

  private static boolean parseHex(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseDigit(tokenizer)) {
      tokenizer.setIndex(index);
      String token = tokenizer.next();
      if(token == null || !(token.equals("A") || token.equals("B") || token.equals("C") || token.equals("D") || token.equals("E") || token.equals("F") || token.equals("a") || token.equals("b") || token.equals("c") || token.equals("d") || token.equals("e") || token.equals("f"))) {
        return(false);
      }
    }
    return(true);
  }

  private static boolean parseAlphaNum(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseAlpha(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseDigit(tokenizer)) {
        return(false);
      }
    }
    return(true);
  }

  private static boolean parseAlpha(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseLowAlpha(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseUpAlpha(tokenizer)) {
        return(false);
      }
    }
    return(true);
  }

  private static boolean parseLowAlpha(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || token.length() != 1) {
      return(false);
    }
    char ch = token.charAt(0);
    return(Character.getType(ch) == Character.LOWERCASE_LETTER);
  }

  private static boolean parseUpAlpha(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || token.length() != 1) {
      return(false);
    }
    char ch = token.charAt(0);
    return(Character.getType(ch) == Character.UPPERCASE_LETTER);
  }

  private static boolean parseDigit(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || token.length() != 1) {
      return(false);
    }
    char ch = token.charAt(0);
    return(Character.getType(ch) == Character.DECIMAL_DIGIT_NUMBER);
  }

  private static boolean parseIPv6Reference(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null || !token.equals("[")) {
      return(false);
    }
    if(!parseIPv4Address(tokenizer)) {
      return(false);
    }
    token = tokenizer.next();
    if(token == null || !token.equals("]")) {
      return(false);
    }
    return(true);
  }

  private static boolean parseDuration(LexicalTokenizer tokenizer, Duration duration) {
    int index = tokenizer.getIndex();
    boolean result = parseNegativeDuration(tokenizer, duration);
    if(!result) {
      tokenizer.setIndex(index);
      result = parsePositiveDuration(tokenizer, duration);
    }
    return(result);
  }

  private static boolean parseNegativeDuration(LexicalTokenizer tokenizer, Duration duration) {
    String token = tokenizer.next();
    if(token == null) {
      return(false);
    }
    if(token.equals("-")) {
      duration.setPositiveDuration(false);
      return(parseValueDuration(tokenizer, duration));
    } else {
      return(false);
    }
  }

  private static boolean parsePositiveDuration(LexicalTokenizer tokenizer, Duration duration)  {
    duration.setPositiveDuration(true);
    return(parseValueDuration(tokenizer, duration));
  }

  private static boolean parseValueDuration(LexicalTokenizer tokenizer, Duration duration)  {
    String token = tokenizer.next();
    int index = tokenizer.getIndex();
    if(token == null) {
      return(false);
    }
    if(!token.toLowerCase(Locale.ENGLISH).equals("p")) {
      return(false);
    }
    index = tokenizer.getIndex();
    boolean result = parseYearsDuration(tokenizer, duration);
    if(!result) {
      tokenizer.setIndex(index);
    }
    if(tokenizer.peek() != null) {
      index = tokenizer.getIndex();
      result = parseMonthsDuration(tokenizer, duration);
      if(!result) {
        tokenizer.setIndex(index);
      }
      if(tokenizer.peek() != null) {
        index = tokenizer.getIndex();
        result = parseDaysDuration(tokenizer, duration);
        if(!result) {
          tokenizer.setIndex(index);
        }
        if(tokenizer.peek() != null) {
          result = parseTimeDuration(tokenizer, duration);
        }
      }
    }
    return(result && tokenizer.peek() == null);
  }

  private static boolean parseYearsDuration(LexicalTokenizer tokenizer, Duration duration) {
    int yearDuration = (int)parseDuration(tokenizer, "y");
    if(yearDuration != -1) {
      duration.setYearsDuration(yearDuration);
      return(true);
    }
    return(false);
  }


  private static boolean parseMonthsDuration(LexicalTokenizer tokenizer, Duration duration) {
    int yearDuration = (int)parseDuration(tokenizer, "m");
    if(yearDuration != -1) {
      duration.setMonthsDuration(yearDuration);
      return(true);
    }
    return(false);
  }

  private static boolean parseDaysDuration(LexicalTokenizer tokenizer, Duration duration) {
    int yearDuration = (int)parseDuration(tokenizer, "d");
    if(yearDuration != -1) {
      duration.setDaysDuration(yearDuration);
      return(true);
    }
    return(false);
  }

  private static boolean parseTimeDuration(LexicalTokenizer tokenizer, Duration duration) {
    int index = tokenizer.getIndex();
    String token = tokenizer.next();
    if(duration.getYearsDuration() != 0 || duration.getMonthsDuration() != 0 || duration.getDaysDuration() != 0) {
      if(token == null || !token.toLowerCase(Locale.ENGLISH).equals("t")) {
        return(false);
      }
    }
    if(!token.toLowerCase(Locale.ENGLISH).equals("t")) {
      tokenizer.setIndex(index);
    }
    index = tokenizer.getIndex();
    boolean result = parseHoursDuration(tokenizer, duration);
    if(!result) {
      tokenizer.setIndex(index);
    }
    if(tokenizer.peek() != null) {
      index = tokenizer.getIndex();
      result = parseMinutesDuration(tokenizer, duration);
      if(!result) {
        tokenizer.setIndex(index);
      }
      if(tokenizer.peek() != null) {
        result = parseSecondsDuration(tokenizer, duration);
      }
    }
    return(result);
  }

  private static boolean parseHoursDuration(LexicalTokenizer tokenizer, Duration duration) {
    int hoursDuration = (int)parseDuration(tokenizer, "h");
    if(hoursDuration != -1) {
      duration.setHoursDuration(hoursDuration);
      return(true);
    }
    return(false);
  }

  private static boolean parseMinutesDuration(LexicalTokenizer tokenizer, Duration duration) {
    int minutesDuration = (int)parseDuration(tokenizer, "m");
    if(minutesDuration != -1) {
      duration.setMinutesDuration(minutesDuration);
      return(true);
    }
    return(false);
  }

  private static boolean parseSecondsDuration(LexicalTokenizer tokenizer, Duration duration) {
    double secondsDuration = parseDuration(tokenizer, "s");
    if(secondsDuration != -1) {
      duration.setSecondsDuration(secondsDuration);
      return(true);
    }
    return(false);
  }

  private static double parseDuration(LexicalTokenizer tokenizer, String designateIdetifier) {
    String duration = tokenizer.next();
    double durationValue = -1;
    if(duration == null) {
      return(-1);
    }
    try {
      durationValue = Double.parseDouble(duration);
      String designator = tokenizer.next();
      if(designator == null) {
        return(-1);
      }
      if(!designator.toLowerCase(Locale.ENGLISH).equals(designateIdetifier)) {
        return(-1);
      }
    } catch(NumberFormatException nimberFormExc) {
      //$JL-EXC$
      return(-1);
    }
    return(durationValue);
  }

  private static boolean parseQName(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    boolean result = parsePrefix(tokenizer);
    if(result) {
      String token = tokenizer.next();
      result = token != null && token.equals(":");
    }
    if(!result) {
      tokenizer.setIndex(index);
    }
    return(parseLocalPart(tokenizer));
  }

  private static boolean parsePrefix(LexicalCharsTokenizer tokenizer) {
    return(parseNCName(tokenizer));
  }

  private static boolean parseLocalPart(LexicalCharsTokenizer tokenizer) {
    return(parseNCName(tokenizer));
  }

  private static boolean parseNCName(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    boolean result = parseLetter(tokenizer);
    if(!result) {
      tokenizer.setIndex(index);
      String token = tokenizer.next();
      if(token == null || !token.equals("_")) {
        return(false);
      }
    }
    while(true) {
      if(tokenizer.peek() != null) {
        index = tokenizer.getIndex();
        if(!parseNCNameChar(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseNCNameChar(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseLetter(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseDigit(tokenizer)) {
        tokenizer.setIndex(index);
        if(!parseCombinigChar(tokenizer)) {
          tokenizer.setIndex(index);
          if(!parseExtender(tokenizer)) {
            tokenizer.setIndex(index);
            String token = tokenizer.next();
            return(token != null && (token.equals(".") || token.equals("-") || token.equals("_")));
          }
        }
      }
    }
    return(true);
  }

  private static boolean parseLetter(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseBaseChar(tokenizer)) {
      tokenizer.setIndex(index);
      return(parseIdeographic(tokenizer));
    }
    return(true);
  }

  private static boolean parseBaseChar(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null) {
      return(false);
    }
    return(Symbols.isBaseChar(token.charAt(0)));
  }

  private static boolean parseIdeographic(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null) {
      return(false);
    }
    return(Symbols.isIdeographic(token.charAt(0)));
  }

  private static boolean parseCombinigChar(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null) {
      return(false);
    }
    return(Symbols.isCombiningChar(token.charAt(0)));
  }

  private static boolean parseExtender(LexicalCharsTokenizer tokenizer) {
    String token = tokenizer.next();
    if(token == null) {
      return(false);
    }
    return(Symbols.isExtender(token.charAt(0)));
  }

  private static boolean parseLanguage(LexicalTokenizer tokenizer) {
    if(!parsePrimaryTag(tokenizer)) {
      return(false);
    }
    boolean isFirst = true;
    while(true) {
    	String token = tokenizer.next();
    	if(token != null) {
    		int index = tokenizer.getIndex();
    		if(token.equals("-")) {
    			index = tokenizer.getIndex();
    			boolean result = parseSubtag(tokenizer, isFirst);
    			if(!result) {
    				tokenizer.setIndex(index);
    				return(false);
    			} else if(isFirst) {
						isFirst = false;
    			}
    		} else {
    			tokenizer.setIndex(index);
    			return(false);
    		}
    	} else {
    		break;
    	}
    }
    return(true);
  }

  private static boolean parsePrimaryTag(LexicalTokenizer tokenizer) {
  	String primaryTag = tokenizer.next();
  	if(primaryTag == null) {
  		return(false);
  	}
		if(primaryTag.length() == 2) {
			String loweredCasePrimaryTag = primaryTag.toLowerCase(Locale.ENGLISH);
			if(loweredCasePrimaryTag.equals("aa") ||
				 loweredCasePrimaryTag.equals("ab") ||
				 loweredCasePrimaryTag.equals("af") ||
				 loweredCasePrimaryTag.equals("am") ||
				 loweredCasePrimaryTag.equals("ar") ||
				 loweredCasePrimaryTag.equals("as") ||
				 loweredCasePrimaryTag.equals("ay") ||
				 loweredCasePrimaryTag.equals("az") ||
				 
				 loweredCasePrimaryTag.equals("ba") ||
				 loweredCasePrimaryTag.equals("be") ||
				 loweredCasePrimaryTag.equals("bg") ||
				 loweredCasePrimaryTag.equals("bh") ||
				 loweredCasePrimaryTag.equals("bi") ||
				 loweredCasePrimaryTag.equals("bn") ||
				 loweredCasePrimaryTag.equals("bo") ||
				 loweredCasePrimaryTag.equals("br") ||
				 
				 loweredCasePrimaryTag.equals("ca") ||
				 loweredCasePrimaryTag.equals("co") ||
				 loweredCasePrimaryTag.equals("cs") ||
				 loweredCasePrimaryTag.equals("cy") ||
				 
				 loweredCasePrimaryTag.equals("da") ||
				 loweredCasePrimaryTag.equals("de") ||
				 loweredCasePrimaryTag.equals("dz") ||
				 
				 loweredCasePrimaryTag.equals("el") ||
				 loweredCasePrimaryTag.equals("en") ||
				 loweredCasePrimaryTag.equals("eo") ||
				 loweredCasePrimaryTag.equals("es") ||
				 loweredCasePrimaryTag.equals("et") ||
				 loweredCasePrimaryTag.equals("eu") ||
				 
				 loweredCasePrimaryTag.equals("fa") ||
				 loweredCasePrimaryTag.equals("fi") ||
				 loweredCasePrimaryTag.equals("fj") ||
				 loweredCasePrimaryTag.equals("fo") ||
				 loweredCasePrimaryTag.equals("fr") ||
				 loweredCasePrimaryTag.equals("fy") ||
				 
				 loweredCasePrimaryTag.equals("ga") ||
				 loweredCasePrimaryTag.equals("gd") ||
				 loweredCasePrimaryTag.equals("gl") ||
				 loweredCasePrimaryTag.equals("gn") ||
				 loweredCasePrimaryTag.equals("gu") ||
				 
				 loweredCasePrimaryTag.equals("ha") ||
				 loweredCasePrimaryTag.equals("he") ||
				 loweredCasePrimaryTag.equals("hi") ||
				 loweredCasePrimaryTag.equals("hr") ||
				 loweredCasePrimaryTag.equals("hu") ||
				 loweredCasePrimaryTag.equals("hy") ||
				 
				 loweredCasePrimaryTag.equals("ia") ||
				 loweredCasePrimaryTag.equals("id") ||
				 loweredCasePrimaryTag.equals("ie") ||
				 loweredCasePrimaryTag.equals("ik") ||
				 loweredCasePrimaryTag.equals("in") ||
				 loweredCasePrimaryTag.equals("is") ||
				 loweredCasePrimaryTag.equals("it") ||
				 loweredCasePrimaryTag.equals("iw") ||
				 loweredCasePrimaryTag.equals("iu") ||
				 
				 loweredCasePrimaryTag.equals("ja") ||
				 loweredCasePrimaryTag.equals("ji") ||
				 loweredCasePrimaryTag.equals("jw") ||
				 
				 loweredCasePrimaryTag.equals("ka") ||
				 loweredCasePrimaryTag.equals("kk") ||
				 loweredCasePrimaryTag.equals("kl") ||
				 loweredCasePrimaryTag.equals("km") ||
				 loweredCasePrimaryTag.equals("kn") ||
				 loweredCasePrimaryTag.equals("ko") ||
				 loweredCasePrimaryTag.equals("ks") ||
				 loweredCasePrimaryTag.equals("ku") ||
				 loweredCasePrimaryTag.equals("ky") ||
				 
				 loweredCasePrimaryTag.equals("la") ||
				 loweredCasePrimaryTag.equals("ln") ||
				 loweredCasePrimaryTag.equals("lo") ||
				 loweredCasePrimaryTag.equals("lt") ||
				 loweredCasePrimaryTag.equals("lv") ||
				 	 
				 loweredCasePrimaryTag.equals("mg") ||
				 loweredCasePrimaryTag.equals("mi") ||
				 loweredCasePrimaryTag.equals("mk") ||
				 loweredCasePrimaryTag.equals("ml") ||
				 loweredCasePrimaryTag.equals("mn") ||
				 loweredCasePrimaryTag.equals("mo") ||
				 loweredCasePrimaryTag.equals("mr") ||
				 loweredCasePrimaryTag.equals("ms") ||
				 loweredCasePrimaryTag.equals("mt") ||
				 loweredCasePrimaryTag.equals("my") ||
				 
				 loweredCasePrimaryTag.equals("na") ||
				 loweredCasePrimaryTag.equals("ne") ||
				 loweredCasePrimaryTag.equals("nl") ||
				 loweredCasePrimaryTag.equals("no") ||
				 
				 loweredCasePrimaryTag.equals("oc") ||
				 loweredCasePrimaryTag.equals("om") ||
				 loweredCasePrimaryTag.equals("or") ||
				 
				 loweredCasePrimaryTag.equals("pa") ||
				 loweredCasePrimaryTag.equals("pl") ||
				 loweredCasePrimaryTag.equals("ps") ||
				 loweredCasePrimaryTag.equals("pt") ||
				 
				 loweredCasePrimaryTag.equals("qu") ||
				 
				 loweredCasePrimaryTag.equals("rm") ||
				 loweredCasePrimaryTag.equals("rn") ||
				 loweredCasePrimaryTag.equals("ro") ||
				 loweredCasePrimaryTag.equals("ru") ||
				 loweredCasePrimaryTag.equals("rw") ||
				 
				 loweredCasePrimaryTag.equals("sa") ||
				 loweredCasePrimaryTag.equals("sd") ||
				 loweredCasePrimaryTag.equals("sg") ||
				 loweredCasePrimaryTag.equals("sh") ||
				 loweredCasePrimaryTag.equals("si") ||
				 loweredCasePrimaryTag.equals("sk") ||
				 loweredCasePrimaryTag.equals("sl") ||
				 loweredCasePrimaryTag.equals("sm") ||
				 loweredCasePrimaryTag.equals("sn") ||
				 loweredCasePrimaryTag.equals("so") ||
				 loweredCasePrimaryTag.equals("sq") ||
				 loweredCasePrimaryTag.equals("sr") ||
				 loweredCasePrimaryTag.equals("ss") ||
				 loweredCasePrimaryTag.equals("st") ||
				 loweredCasePrimaryTag.equals("su") ||
				 loweredCasePrimaryTag.equals("sv") ||
				 loweredCasePrimaryTag.equals("sw") ||
				 
				 loweredCasePrimaryTag.equals("ta") ||
				 loweredCasePrimaryTag.equals("te") ||
				 loweredCasePrimaryTag.equals("tg") ||
				 loweredCasePrimaryTag.equals("th") ||
				 loweredCasePrimaryTag.equals("ti") ||
				 loweredCasePrimaryTag.equals("tk") ||
				 loweredCasePrimaryTag.equals("tl") ||
				 loweredCasePrimaryTag.equals("tn") ||
				 loweredCasePrimaryTag.equals("to") ||
				 loweredCasePrimaryTag.equals("tr") ||
				 loweredCasePrimaryTag.equals("ts") ||
				 loweredCasePrimaryTag.equals("tt") ||
				 loweredCasePrimaryTag.equals("tw") ||
				 
				 loweredCasePrimaryTag.equals("ug") ||
				 loweredCasePrimaryTag.equals("uk") ||
				 loweredCasePrimaryTag.equals("ur") ||
				 loweredCasePrimaryTag.equals("uz") ||
				 
				 loweredCasePrimaryTag.equals("vi") ||
				 loweredCasePrimaryTag.equals("vo") ||
				 
				 loweredCasePrimaryTag.equals("wo") ||
				 
				 loweredCasePrimaryTag.equals("xh") ||
				 
				 loweredCasePrimaryTag.equals("yo") ||
				 loweredCasePrimaryTag.equals("yi") ||
				 
				 loweredCasePrimaryTag.equals("za") ||
				 loweredCasePrimaryTag.equals("zh") ||
				 loweredCasePrimaryTag.equals("zu")) {
				return(true);	 	
			}
		} else if(primaryTag.length() == 1) {
			if(primaryTag.equals("i") || primaryTag.equals("x")) {
				return(true);
			}
		}
		return(false);
  }
  
	private static boolean parseSubtag(LexicalTokenizer tokenizer, boolean isFirst) {
		String subtag = tokenizer.next();
		if(subtag != null && (subtag.length() >= 1 || subtag.length() <= 8)) {
			for(int i = 0; i < subtag.length(); i++) {
				char ch = subtag.charAt(i);
				int type = Character.getType(ch);
				if(type != Character.UPPERCASE_LETTER && type != Character.LOWERCASE_LETTER) {
					return(false);
				}
			}
			return(true);
		}
		return(false);
	}

//  private static boolean parseSubtag(LexicalTokenizer tokenizer, boolean isFirst) {
//  	String subtag = tokenizer.next();
//  	if(subtag == null) {
//  		return(false);
//  	}
//  	if(subtag.length() == 2 && isFirst) {
//  		String upperCasedSubtag = subtag.toUpperCase();
//  		if(upperCasedSubtag.equals("AF") ||
//				 upperCasedSubtag.equals("AL") ||
//				 upperCasedSubtag.equals("DZ") ||
//				 upperCasedSubtag.equals("AS") ||
//				 upperCasedSubtag.equals("AD") ||
//				 upperCasedSubtag.equals("AO") ||
//				 upperCasedSubtag.equals("AI") ||
//				 upperCasedSubtag.equals("AQ") ||
//				 upperCasedSubtag.equals("AG") ||
//				 upperCasedSubtag.equals("AR") ||
//				 upperCasedSubtag.equals("AM") ||
//				 upperCasedSubtag.equals("AW") ||
//				 upperCasedSubtag.equals("AU") ||
//				 upperCasedSubtag.equals("AT") ||
//				 upperCasedSubtag.equals("AZ") ||
//				 upperCasedSubtag.equals("BS") ||
//				 upperCasedSubtag.equals("BH") ||
//				 upperCasedSubtag.equals("BD") ||
//				 upperCasedSubtag.equals("BB") ||
//				 upperCasedSubtag.equals("BY") ||
//				 upperCasedSubtag.equals("BE") ||
//				 upperCasedSubtag.equals("BZ") ||
//				 upperCasedSubtag.equals("BJ") ||
//				 upperCasedSubtag.equals("BM") ||
//				 upperCasedSubtag.equals("BT") ||
//				 upperCasedSubtag.equals("BO") ||
//				 upperCasedSubtag.equals("BA") ||
//				 upperCasedSubtag.equals("BW") ||
//				 upperCasedSubtag.equals("BV") ||
//				 upperCasedSubtag.equals("BR") ||
//				 upperCasedSubtag.equals("IO") ||
//				 upperCasedSubtag.equals("BN") ||
//				 upperCasedSubtag.equals("BG") ||
//				 upperCasedSubtag.equals("BF") ||
//				 upperCasedSubtag.equals("BI") ||
//				 upperCasedSubtag.equals("KH") ||
//				 upperCasedSubtag.equals("CM") ||
//				 upperCasedSubtag.equals("CA") ||
//				 upperCasedSubtag.equals("CV") ||
//				 upperCasedSubtag.equals("KY") ||
//				 upperCasedSubtag.equals("CF") ||
//				 upperCasedSubtag.equals("TD") ||
//				 upperCasedSubtag.equals("CL") ||
//				 upperCasedSubtag.equals("CN") ||
//				 upperCasedSubtag.equals("CX") ||
//				 upperCasedSubtag.equals("CC") ||
//				 upperCasedSubtag.equals("CO") ||
//				 upperCasedSubtag.equals("KM") ||
//				 upperCasedSubtag.equals("CD") ||
//				 upperCasedSubtag.equals("CG") ||
//				 upperCasedSubtag.equals("CK") ||
//				 upperCasedSubtag.equals("CR") ||
//				 upperCasedSubtag.equals("CI") ||
//				 upperCasedSubtag.equals("HR") ||
//				 upperCasedSubtag.equals("CU") ||
//				 upperCasedSubtag.equals("CY") ||
//				 upperCasedSubtag.equals("CZ") ||
//				 upperCasedSubtag.equals("DK") ||
//				 upperCasedSubtag.equals("DJ") ||
//				 upperCasedSubtag.equals("DM") ||
//				 upperCasedSubtag.equals("DO") ||
//				 upperCasedSubtag.equals("TL") ||
//				 upperCasedSubtag.equals("EC") ||
//				 upperCasedSubtag.equals("EG") ||
//				 upperCasedSubtag.equals("SV") ||
//				 upperCasedSubtag.equals("GQ") ||
//				 upperCasedSubtag.equals("ER") ||
//				 upperCasedSubtag.equals("EE") ||
//				 upperCasedSubtag.equals("ET") ||
//				 upperCasedSubtag.equals("FK") ||
//				 upperCasedSubtag.equals("FO") ||
//				 upperCasedSubtag.equals("FJ") ||
//				 upperCasedSubtag.equals("FI") ||
//				 upperCasedSubtag.equals("FR") ||
//				 upperCasedSubtag.equals("FX") ||
//				 upperCasedSubtag.equals("GF") ||
//				 upperCasedSubtag.equals("PF") ||
//				 upperCasedSubtag.equals("TF") ||
//				 upperCasedSubtag.equals("GA") ||
//				 upperCasedSubtag.equals("GM") ||
//				 upperCasedSubtag.equals("GE") ||
//				 upperCasedSubtag.equals("DE") ||
//				 upperCasedSubtag.equals("GH") ||
//				 upperCasedSubtag.equals("GI") ||
//				 upperCasedSubtag.equals("GR") ||
//				 upperCasedSubtag.equals("GL") ||
//				 upperCasedSubtag.equals("GD") ||
//				 upperCasedSubtag.equals("GP") ||
//				 upperCasedSubtag.equals("GU") ||
//				 upperCasedSubtag.equals("GT") ||
//				 upperCasedSubtag.equals("GN") ||
//				 upperCasedSubtag.equals("GW") ||
//				 upperCasedSubtag.equals("GY") ||
//				 upperCasedSubtag.equals("HT") ||
//				 upperCasedSubtag.equals("HM") ||
//				 upperCasedSubtag.equals("HN") ||
//				 upperCasedSubtag.equals("HK") ||
//				 upperCasedSubtag.equals("HU") ||
//				 upperCasedSubtag.equals("IS") ||
//				 upperCasedSubtag.equals("IN") ||
//				 upperCasedSubtag.equals("ID") ||
//				 upperCasedSubtag.equals("IR") ||
//				 upperCasedSubtag.equals("IQ") ||
//				 upperCasedSubtag.equals("IE") ||
//				 upperCasedSubtag.equals("IL") ||
//				 upperCasedSubtag.equals("IT") ||
//				 upperCasedSubtag.equals("JM") ||
//				 upperCasedSubtag.equals("JP") ||
//				 upperCasedSubtag.equals("JO") ||
//				 upperCasedSubtag.equals("KZ") ||
//				 upperCasedSubtag.equals("KE") ||
//				 upperCasedSubtag.equals("KI") ||
//				 upperCasedSubtag.equals("KP") ||
//				 upperCasedSubtag.equals("KR") ||
//				 upperCasedSubtag.equals("KW") ||
//				 upperCasedSubtag.equals("KG") ||
//				 upperCasedSubtag.equals("LA") ||
//				 upperCasedSubtag.equals("LV") ||
//				 upperCasedSubtag.equals("LB") ||
//				 upperCasedSubtag.equals("LS") ||
//				 upperCasedSubtag.equals("LR") ||
//				 upperCasedSubtag.equals("LY") ||
//				 upperCasedSubtag.equals("LI") ||
//				 upperCasedSubtag.equals("LT") ||
//				 upperCasedSubtag.equals("LU") ||
//				 upperCasedSubtag.equals("MO") ||
//				 upperCasedSubtag.equals("MK") ||
//				 upperCasedSubtag.equals("MG") ||
//				 upperCasedSubtag.equals("MW") ||
//				 upperCasedSubtag.equals("MY") ||
//				 upperCasedSubtag.equals("MV") ||
//				 upperCasedSubtag.equals("ML") ||
//				 upperCasedSubtag.equals("MT") ||
//				 upperCasedSubtag.equals("MH") ||
//				 upperCasedSubtag.equals("MG") ||
//				 upperCasedSubtag.equals("MR") ||
//				 upperCasedSubtag.equals("MU") ||
//				 upperCasedSubtag.equals("YT") ||
//				 upperCasedSubtag.equals("MX") ||
//				 upperCasedSubtag.equals("FM") ||
//				 upperCasedSubtag.equals("MD") ||
//				 upperCasedSubtag.equals("MC") ||
//				 upperCasedSubtag.equals("MN") ||
//				 upperCasedSubtag.equals("MS") ||
//				 upperCasedSubtag.equals("MA") ||
//				 upperCasedSubtag.equals("MZ") ||
//				 upperCasedSubtag.equals("MM") ||
//				 upperCasedSubtag.equals("NA") ||
//				 upperCasedSubtag.equals("NR") ||
//				 upperCasedSubtag.equals("NP") ||
//				 upperCasedSubtag.equals("NL") ||
//				 upperCasedSubtag.equals("AN") ||
//				 upperCasedSubtag.equals("NC") ||
//				 upperCasedSubtag.equals("NZ") ||
//				 upperCasedSubtag.equals("NI") ||
//				 upperCasedSubtag.equals("NE") ||
//				 upperCasedSubtag.equals("NG") ||
//				 upperCasedSubtag.equals("NU") ||
//				 upperCasedSubtag.equals("NF") ||
//				 upperCasedSubtag.equals("MP") ||
//				 upperCasedSubtag.equals("NO") ||
//				 upperCasedSubtag.equals("OM") ||
//				 upperCasedSubtag.equals("PK") ||
//				 upperCasedSubtag.equals("PW") ||
//				 upperCasedSubtag.equals("PS") ||
//				 upperCasedSubtag.equals("PA") ||
//				 upperCasedSubtag.equals("PG") ||
//				 upperCasedSubtag.equals("PY") ||
//				 upperCasedSubtag.equals("PE") ||
//				 upperCasedSubtag.equals("PH") ||
//				 upperCasedSubtag.equals("PN") ||
//				 upperCasedSubtag.equals("PL") ||
//				 upperCasedSubtag.equals("PT") ||
//				 upperCasedSubtag.equals("PR") ||
//				 upperCasedSubtag.equals("QA") ||
//				 upperCasedSubtag.equals("RE") ||
//				 upperCasedSubtag.equals("RO") ||
//				 upperCasedSubtag.equals("RU") ||
//				 upperCasedSubtag.equals("RW") ||
//				 upperCasedSubtag.equals("KN") ||
//				 upperCasedSubtag.equals("LC") ||
//				 upperCasedSubtag.equals("VC") ||
//				 upperCasedSubtag.equals("WS") ||
//				 upperCasedSubtag.equals("SM") ||
//				 upperCasedSubtag.equals("ST") ||
//				 upperCasedSubtag.equals("SA") ||
//				 upperCasedSubtag.equals("SN") ||
//				 upperCasedSubtag.equals("SC") ||
//				 upperCasedSubtag.equals("SL") ||
//				 upperCasedSubtag.equals("SG") ||
//				 upperCasedSubtag.equals("SK") ||
//				 upperCasedSubtag.equals("SI") ||
//				 upperCasedSubtag.equals("SB") ||
//				 upperCasedSubtag.equals("SO") ||
//				 upperCasedSubtag.equals("ZA") ||
//				 upperCasedSubtag.equals("GS") ||
//				 upperCasedSubtag.equals("ES") ||
//				 upperCasedSubtag.equals("LK") ||
//				 upperCasedSubtag.equals("SH") ||
//				 upperCasedSubtag.equals("PM") ||
//				 upperCasedSubtag.equals("SD") ||
//				 upperCasedSubtag.equals("SR") ||
//				 upperCasedSubtag.equals("SJ") ||
//				 upperCasedSubtag.equals("SZ") ||
//				 upperCasedSubtag.equals("SE") ||
//				 upperCasedSubtag.equals("CH") ||
//				 upperCasedSubtag.equals("SY") ||
//				 upperCasedSubtag.equals("TW") ||
//				 upperCasedSubtag.equals("TJ") ||
//				 upperCasedSubtag.equals("TZ") ||
//				 upperCasedSubtag.equals("TH") ||
//				 upperCasedSubtag.equals("TG") ||
//				 upperCasedSubtag.equals("TK") ||
//				 upperCasedSubtag.equals("TO") ||
//				 upperCasedSubtag.equals("TT") ||
//				 upperCasedSubtag.equals("TN") ||
//				 upperCasedSubtag.equals("TR") ||
//				 upperCasedSubtag.equals("TM") ||
//				 upperCasedSubtag.equals("TC") ||
//				 upperCasedSubtag.equals("TV") ||
//				 upperCasedSubtag.equals("UG") ||
//				 upperCasedSubtag.equals("UA") ||
//				 upperCasedSubtag.equals("AE") ||
//				 upperCasedSubtag.equals("GB") ||
//				 upperCasedSubtag.equals("US") ||
//				 upperCasedSubtag.equals("UM") ||
//				 upperCasedSubtag.equals("UY") ||
//				 upperCasedSubtag.equals("UZ") ||
//				 upperCasedSubtag.equals("VU") ||
//				 upperCasedSubtag.equals("VA") ||
//				 upperCasedSubtag.equals("VE") ||
//				 upperCasedSubtag.equals("VN") ||
//				 upperCasedSubtag.equals("VG") ||
//				 upperCasedSubtag.equals("VI") ||
//				 upperCasedSubtag.equals("WF") ||
//				 upperCasedSubtag.equals("EH") ||
//				 upperCasedSubtag.equals("YE") ||
//				 upperCasedSubtag.equals("YU") ||
//				 upperCasedSubtag.equals("ZM") ||
//				 upperCasedSubtag.equals("ZW")) {
//				 	return(true);
//  		} else {
//  			return(false);
//		  }
//  	} else {
//  		if(subtag.length() < 1 || subtag.length() > 8) {
//  			return(false);
//  		}
//			for(int i = 0; i < subtag.length(); i++) {
//				char ch = subtag.charAt(i);
//				int type = Character.getType(ch);
//				if(type != Character.UPPERCASE_LETTER && type != Character.LOWERCASE_LETTER) {
//					return(false);
//				}
//			}
//			return(true);
//  	}
//  }

  private static boolean parseAlphas(LexicalCharsTokenizer tokenizer) {
    boolean hasAlpha = false;
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        if(!parseAlpha(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        } else {
          hasAlpha = true;
        }
      } else {
        break;
      }
    }
    return(hasAlpha);
  }

  private static boolean parseName(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseLetter(tokenizer)) {
      tokenizer.setIndex(index);
      String token = tokenizer.next();
      if(token == null || !(token.equals("_") || token.equals(":"))) {
        return(false);
      }
    }
    while(true) {
      if(tokenizer.peek() != null) {
        index = tokenizer.getIndex();
        if(!parseNameChar(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        }
      } else {
        break;
      }
    }
    return(true);
  }

  private static boolean parseNameChar(LexicalCharsTokenizer tokenizer) {
    int index = tokenizer.getIndex();
    if(!parseLetter(tokenizer)) {
      tokenizer.setIndex(index);
      if(!parseDigit(tokenizer)) {
        tokenizer.setIndex(index);
        if(!parseCombinigChar(tokenizer)) {
          tokenizer.setIndex(index);
          if(!parseExtender(tokenizer)) {
            tokenizer.setIndex(index);
            String token = tokenizer.next();
            if(token == null || !(token.equals(".") || token.equals("-") || token.equals("_") || token.equals(":"))) {
              return(false);
            }
          }
        }
      }
    }
    return(true);
  }

  private static boolean parseNmtoken(LexicalCharsTokenizer tokenizer) {
    boolean hasNameChar = false;
    while(true) {
      if(tokenizer.peek() != null) {
        int index = tokenizer.getIndex();
        if(!parseNameChar(tokenizer)) {
          tokenizer.setIndex(index);
          break;
        } else {
          hasNameChar = true;
        }
      } else {
        break;
      }
    }
    return(hasNameChar);
  }

  private static GregorianCalendar createEmptyCalendar() {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.clear();
    return(calendar);
  }
}
