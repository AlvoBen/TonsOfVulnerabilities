package com.sap.engine.services.deploy.server.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public final class StringUtils {
	private static final String DELIMITIERS = ";";
	private static final String EMPTY_STRING = "";
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private StringUtils() {
		// to prevent the instantiation.
	}

	/**
	 * Intern a string to the internal pool.
	 * 
	 * @param str
	 *            string to be interned. Can be null.
	 * @return interned string.
	 */
	public static String intern(final String str) {
		if (str == null) {
			return null;
		}
		return str.intern();
	}

	/**
	 * @param tokens
	 *            collection to hold tokens. Not null.
	 * @param source
	 *            source to be parsed. Not null.
	 */
	private static void collectTokens(final Collection<String> tokens,
			final String source) {
		final StringTokenizer tokenizer = new StringTokenizer(source,
				DELIMITIERS);
		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken();
			if (token != null && !token.trim().equals("")) {
				tokens.add(token);
			}
		}
	}

	/**
	 * Parse the source string to array of strings. As a delimiter we use
	 * semicolon.
	 * 
	 * @param source
	 *            the input string to be parsed. Can be null.
	 * @return array of strings. Cannot be null.
	 */
	public static String[] parse2String(final String source) {
		if (source == null) {
			return EMPTY_STRING_ARRAY;
		}
		final List<String> tokens = new ArrayList<String>();
		collectTokens(tokens, source);
		return (String[]) tokens.toArray(new String[tokens.size()]);
	}

	/**
	 * Parse the source string to unmodifiable list of strings. As a delimiter
	 * we use semicolon.
	 * 
	 * @param source
	 *            the input string to be parsed. Can be null.
	 * @return unmodifiable list of strings. Cannot be null.
	 */
	public static List<String> parse2List(final String source) {
		if (source == null) {
			return Collections.emptyList();
		}
		final List<String> tokens = new ArrayList<String>();
		collectTokens(tokens, source);
		return Collections.unmodifiableList(tokens);
	}

	/**
	 * @return Return a shared instance of an empty string.
	 */
	public static String emptyString() {
		return EMPTY_STRING;
	}

	/**
	 * @return Return a shared instance of an empty string array.
	 */
	public static String[] emptyStringArray() {
		return EMPTY_STRING_ARRAY;
	}

	/**
	 * Check whether a given string is empty.
	 * 
	 * @param str
	 *            the string to be checked. Can be null.
	 * @return true if the string is empty (null or with zero length).
	 */
	public static boolean isEmpty(final String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Parse the source to unmodifiable ordered set of strings. As a delimiter
	 * we use semicolon.
	 * 
	 * @param source
	 *            the input string to be parsed. Can be null.
	 * @return ordered set of string, which preserve the order in the input
	 *         string. Cannot be null.
	 */
	public static Set<String> parse2Set(final String source) {
		if (source == null) {
			return Collections.emptySet();
		}
		final Set<String> tokens = new LinkedHashSet<String>();
		collectTokens(tokens, source);
		return Collections.unmodifiableSet(tokens);
	}
	
	/**
	 * Gets cause message from given Throwable
	 * @param th
	 * @return
	 * NOTE: Same as in DeployController'S class com.sap.engine.services.dc.util.StringUtils (to be reused)
	 */
	public static String getCauseMessage(Throwable th) {
	        if (th == null) {
	          return null;
	        } 
	        if (th.getCause() != null) {
	          if (th.getCause().getCause() != null) {
	              return getCauseMessage(th.getCause());
	          } else {                
	            if (getMessage(th).indexOf(getMessage(th.getCause())) != -1) {
	              if ((getMessage(th.getCause()).indexOf("Hint") != -1) || 
	                  (getMessage(th.getCause()).indexOf("Solution") != -1) ||
	                  (getMessage(th.getCause()).indexOf("Reason") != -1)) {
	                return getMessage(th.getCause());
	              } else {
	                return getMessage(th);
	              }
	            } else {
	                return getMessage(th) + DSConstants.EOL_TAB_TAB + " -> " + getMessage(th.getCause());
	            }        
	          } 
	        } else {
	            return getMessage(th);
	        }
	      }
	   
	   
	/**
	 * Gets message from a Throwable
	 * @param th
	 * @return
	 * 
	 * NOTE: Same as in DeployController'S class com.sap.engine.services.dc.util.StringUtils (to be reused)
	 */
	private static String getMessage(Throwable th) {
	        if (th.getLocalizedMessage() != null) {
	          return th.getLocalizedMessage();
	        }
	        if (th.getMessage() != null) {
	          return th.getMessage();
	        }  
	        return th.getClass().toString();  
	    }


}