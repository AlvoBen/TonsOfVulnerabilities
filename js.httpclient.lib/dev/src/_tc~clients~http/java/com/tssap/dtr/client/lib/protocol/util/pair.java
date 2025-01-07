package com.tssap.dtr.client.lib.protocol.util;

import java.util.Collections;
import java.util.List;

/**
 * This class represent a name-value pair.
 * Methods are provides to extract the parts of a name-value
 * pair and interpret that parts further.
 */
public class Pair {

	/** The name part of that pair */
	protected String name;
	/** The value part of that pair */
	protected String value;
	/** The "sub"-parts of the value */
	protected List parts;
	/** The separator character between name and value */
	protected char separator;
	/** The separator characters dividing value in "sub"-parts */
	protected String partSeparators;
	


	/**
	 * Creates a Pair instance with the specified name, value and
	 * separator character.
	 * @param name the name of this pair.
	 * @param value the value of this pair.
	 * @param separator the separator character to search for
	 */
	public Pair(String name, String value, char separator) {
		this.separator = separator;
		this.name = name;
		this.value = value;
	}

	/**
	 * Creates a Pair instance from the specified string and
	 * separator character.
	 * If the string does not contain the given separator, the
	 * whole string is assigned as name of the resulting pair.
	 * Leading and trailing whitespace is removed from both
	 * name and value part. Therefore, both name and value
	 * may contain the empty string.
	 * @param str the string to split
	 * @param separator the separator character to search for
	 */
	public Pair(String str, char separator) {
		this.separator = separator;
		initializeFrom(str, false);
	}
	
	/**
	 * Creates a Pair instance from the specified string and
	 * separator character.
	 * If the string does not contain the given separator, the
	 * whole string is assigned as name of the resulting pair.
	 * Leading and trailing whitespace is removed from both
	 * name and value part. Therefore, both name and value
	 * may contain the empty string. If <code>removeQuotes</code>
	 * is true, pairs of quotes surrounding the value (either ' or ")
	 * are removed.  
	 * @param str the string to split
	 * @param separator the separator character to search for
	 * @param removeQuotes  removes surrounding quotes (either ' or ") 
	 * from the pair's value
	 */
	public Pair(String str, char separator, boolean removeQuotes) {
		this.separator = separator;
		initializeFrom(str, removeQuotes);
	}
	

	/**
	 * Creates a Pair instance from the specified string and
	 * separator character.
	 * If the string does not contain the given separator, the
	 * whole string is assigned as name of the resulting pair.
	 * Leading and trailing whitespace is removed from both
	 * name and value part. Therefore, both name and value
	 * may contain the empty string.
	 * @param str the string to split
	 * @param separator the separator character to search for
	 */
	public static Pair valueOf(String str, char separator) {
		Pair pair = new Pair(separator);
		pair.initializeFrom(str, false);
		return pair;
	}
	
	/**
	 * Creates a Pair instance from the specified string and
	 * separator character.
	 * If the string does not contain the given separator, the
	 * whole string is assigned as name of the resulting pair.
	 * Leading and trailing whitespace is removed from both
	 * name and value part. Therefore, both name and value
	 * may contain the empty string. If <code>removeQuotes</code>
	 * is true, pairs of quotes surrounding the value (either ' or ")
	 * are removed.  
	 * @param str the string to split
	 * @param separator the separator character to search for
	 * @param removeQuotes  removes surrounding quotes (either ' or ") 
	 * from the pair's value
	 */
	public static Pair valueOf(String str, char separator, boolean removeQuotes) {
		Pair pair = new Pair(separator);
		pair.initializeFrom(str, removeQuotes);
		return pair;
	}	

	/** private constructor */
	private Pair(char separator) {
		this.separator = separator;	
	}

	/**
	 * Returns the name part of the pair.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name part of the pair.
	 * @param name the name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value part of the pair.
	 * @return The value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value part of the pair.
	 * @param value the value.
	 */
	public void setValue(String value) {
		this.value = value;
		if (parts != null) {
			parts.clear();
			parts = null;			
		}
	}

	/**
	 * Returns the separator character between name and value.
	 * @return The separator, e.g. ':' for HTTP headers.
	 */
	public char getSeparator() {
		return separator;
	}

	/**
	 * Returns separator characters used to divide the pair's value into parts.
	 * @return The separators, e.g. ",;" for HTTP headers.
	 */
	public String getPartSeparators() {
		return partSeparators;
	}

	/**
	 * Sets separator characters used to divide the pair's value into parts.
	 * @param partSeperators   one or more separator character, e.g. ",;"
	 */
	public void setPartSeparators(String partSeparators) {
		this.partSeparators = partSeparators;
		if (parts != null) {
			parts.clear();
			parts = null;			
		}		
	}

	/**
	 * Returns the parts of the pair's value.
	 * @return A non-modifiable list of String representing the 
	 * parts of the pair's value, or an empty list if no parts could be
	 * found.
	 */
	public List getParts() {
		if (parts == null) {
			parts = Tokenizer.partsOf(value, getPartSeparators());
		}
		return Collections.unmodifiableList(parts);
	}

	/**
	 * Appends a part to the value of this pair.
	 * @param part  a string to be appended to the value of this pair.
	 * @param separator  an optional separator string. If separator is null
	 * or an empty string, the first of any previously defined 
	 * <code>partSeparator</code> is used. If no part separator is available,
	 * the geiven part is simply appended to the pair value without any
	 * separator
	 */
	public void appendPart(String part, String separator) {
		if (value == null  || value.length()==0) {
			value = part;
		} else {
			if (separator != null && separator.length() > 0) {
				value += separator + part;
			} else if (partSeparators != null  && partSeparators.length()>0) {
				value += partSeparators.charAt(0) + part;
			} else {
				value += part;
			}
		}
		if (parts != null) {
			parts.add(part);
		}
	}

	/**
	 * Checks whether name and value of the compared pairs match.
	 * @param o  the pair to check for equality with this pair.
	 * @return True, if name and value of the pairs match.
	 */
	public boolean equals(Object o) {
		if (o != null  &&  o instanceof Pair) {
			Pair pair = (Pair)o;
			boolean equals = true;
			equals &= ((name != null)? name.equals(pair.getName()) : pair.getName()==null);
			equals &= ((value != null)? value.equals(pair.getValue()) : pair.getValue()==null);
			return equals;
		}
		return false;			
	}
	
	/**
	 * Returns a hash code value for the Pair.
	 * The <code>name</code> and <code>value</code> attributes
	 * are taken into account for hash calculation.
	 * @return  a hash code value for this Pair.
	 */
	public int hashCode() {
		int result = 17;
		result = (name != null)? 37*result + name.hashCode() : result;
		result = (value != null)? 37*result + value.hashCode() : result;
		return result;		
	}	

	/**
	 * Returns a string representation of the pair.
	 * @return The concatenation of name, separator and value if value is not empty, 
	 * otherwise only the name is returned without separator.
	 */
	public String toString() {
		if (value != null && value.length() > 0) {
			return name + getSeparator() + value;
		} else {
			return name;
		}
	}

	/**
	 * Initializes the pair from the given string.
	 */
	private void initializeFrom(String str, boolean removeQuotes) {
		if (str != null) {
			int pos = str.indexOf(getSeparator());
			if (pos > 0) {
				name = str.substring(0, pos).trim();
				if (pos < str.length()-1) {
					value = str.substring(pos + 1).trim();
				} else {
					value = "";
				}
			} else if (pos == 0) {
				name = "";
				value = str.substring(1).trim();
			} else {
				name = str;
				value = "";
			}
			if (removeQuotes) {
				if ( (value.startsWith("\"") && value.endsWith("\"")) || 
					 (value.startsWith("'")  && value.endsWith("'")))
				{
					value = value.substring(1, value.length() - 1);
				}
			}			
		}
	}
	

}
