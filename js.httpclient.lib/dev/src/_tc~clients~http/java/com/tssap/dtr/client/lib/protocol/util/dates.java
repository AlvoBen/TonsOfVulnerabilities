package com.tssap.dtr.client.lib.protocol.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.sap.tc.logging.Location;

/**
 * This class provides some common date/time encoding/decoding methods used in HTTP and
 * WebDAV requests.
 */
public class Dates {

	/**
	 * The standard date encoding according to RFC 1123 used for request headers.
	 * Example: "Sun, 06 Nov 1994 08:49:37 GMT".
	 */
	public static final String RFC1123_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

	/**
	 * The standard date encoding according to ISO 8601. Used widely by DAV and DeltaV
	 * for date-like properties (e.g. "creationdate"). Based on the UTC timezone.
	 * Example: "1994-11-06T08:49:37Z"
	 */
	public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * The standard date encoding according to ISO 8601 but with an additional millisecond
	 * time fraction. Based on the UTC timezone.
	 * Example: "1994-11-06T08:49:37.340Z"
	 */
	public static final String ISO8601_DATE_FORMAT_WITH_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * Obsolete but widely used date encoding with two-digit year according to RFC 1036.
	 * Note, this date format is not Y2K compliant!
	 * Example: "Sunday, 06 Nov 94 08:49:37 GMT".
	 */
	public static final String RFC1036_DATE_FORMAT = "EEEEEEE, dd-MMM-yy HH:mm:ss 'GMT'";

	/**
	 * Date encoding according to the ANSI C library asctime() function.
	 * Example: "Sun Nov 06 08:49:37 1994"
	 */
	public static final String ASCTIME_DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";
		
	/**
	 * Simple date encoding, useful for display purposes. Always related
	 * to 'GMT' timezone.
	 * Example: "2002-11-15 08:11:20"
	 */
	public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		
	/**
	 * Simple date encoding with millisecond resolution, useful for display purposes.
	 * Always related to 'GMT' timezone.
	 * Example: "2002-11-15 08:11:20.176"
	 */
	public static final String SIMPLE_DATE_FORMAT_WITH_MILLIS = "yyyy-MM-dd HH:mm:ss.S";
		
	/**
	 * The default date encoding for request headers. Equals to RFC1123_DATE_FORMAT.
	 */
	public static final String DEFAULT_DATE_FORMAT = RFC1123_DATE_FORMAT;
	
		
	private static final String[] formats = {	
		DEFAULT_DATE_FORMAT,
		ISO8601_DATE_FORMAT_WITH_MILLIS,
		ISO8601_DATE_FORMAT,
		RFC1036_DATE_FORMAT,
		ASCTIME_DATE_FORMAT,
		SIMPLE_DATE_FORMAT_WITH_MILLIS,
		SIMPLE_DATE_FORMAT,
		// uncommon variants with local timezones
		"EEE, dd MMM yyyy HH:mm:ss zzz",
		"EEE MMM dd HH:mm:ss zzz yyyy",
		"EEEEEEE, dd-MMM-yy HH:mm:ss zzz",
		"EEE MMMMMM d HH:mm:ss yyyy"
	};
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(Dates.class);	
			
		
	/**
	 * Decodes the given date string. Tries to match the given date to the
	 * predefined date formats RFC1123, ISO8601 with milliseconds,
	 * ISO8601, RFC1036, ASCTIME, SIMPLE_DATE_FORMAT_WITH_MILLIS, 
	 * SIMPLE_DATE_FORMAT (in this order), and some uncommon variants of the former
	 * with local timezone setting.
	 * @param date  the date string to decode
	 * @return the decoded date, or null if the date does not match to any of the
	 * predefined date formats
	 */	
	public static Date valueOf(String date) throws ParseException {
		Date result = null;
		for (int i=0; i<formats.length  &&  result==null; ++i) {
			result = _valueOf(date, formats[i]);			
		}
		return result;
	}	
	
	/**
	 * Decodes the given date string. Tries to match the given date to the given
	 * format pattern.
	 * @param date  the date string to decode
	 * @param pattern  the format pattern to use
	 * @return the decoded date, or null if the date does not match to the given
	 */	
	public static Date valueOf(String date, String pattern) throws ParseException {
		return _valueOf(date, pattern);
	}	

	/**
	 * Decodes the given date string according to the given date format.
	 * @param date  the date string to decode
	 * @param format  the date format to apply
	 * @return the decoded date, or null if the date does not match the given date format.
	 */
	public static Date valueOf(String date, DateFormat dateFormat) {
		try {
			return dateFormat.parse(date);
		} catch (ParseException ex) {
			TRACE.catching("valueOf(String,DateFormat)", ex);
		}
		return null;
	}	

	/**
	 * Encodes the given date according to RFC1123 format. Note, the HTTP specification
	 * requires that any request headers containg date values being encoded in this
	 * format.
	 * @param date  the date to encode
	 * @return the encoded date in RFC1123 format 
	 */
	public static String toString(Date date) {
		DateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH);
		format.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
		return toString(date, format);
	}
	

	/**
	 * Encodes the given date according to the given format pattern
	 * @param date  the date to encode
	 * @param dateFormat  the date format to use
	 * @return the encoded date
	 */

	public static String toString(Date date, DateFormat dateFormat) {
		return dateFormat.format(date);
	}




	private static Date _valueOf(String date, String pattern) {
		try {
			DateFormat format = new SimpleDateFormat(pattern);
			format.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
			return format.parse(date);
		} catch (ParseException ex) {
			TRACE.catching("_valueOf(String,String)", ex);
		}
		return null;
	}

}
