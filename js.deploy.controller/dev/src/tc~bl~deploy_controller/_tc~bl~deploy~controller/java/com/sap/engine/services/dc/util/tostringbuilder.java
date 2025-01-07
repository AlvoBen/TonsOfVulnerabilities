package com.sap.engine.services.dc.util;

import java.lang.reflect.Array;

/**
 * 
 * Title: J2EE Deployment Team Description: The class consists of operations
 * used for tracing an object and operations.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ToStringBuilder {

	private static final String DEFAULT_OPERATION_PREFIX = "Invoking operation ";
	private static final String DEFAULT_ARRAY_OBJECTS_SEPARATOR = "; ";
	private static final String EMPTY_STRING = "";

	private static ToStringBuilder instance;

	private String operationPrefix;
	private String arrayObjectsSeparator;

	protected ToStringBuilder() {
		operationPrefix = DEFAULT_OPERATION_PREFIX;
		arrayObjectsSeparator = DEFAULT_ARRAY_OBJECTS_SEPARATOR;
	}

	/**
	 * The class is implemented as a <i>Singleton</i>.
	 * 
	 * @return <code>ToStringBuilder</code>
	 */
	public static synchronized ToStringBuilder getInstance() {
		if (instance == null) {
			instance = new ToStringBuilder();
		}
		return instance;
	}

	/**
	 * Checks whether the specified argument is an array and if it is, a String
	 * representation of the data for its items is get and returned. If the
	 * argument is not an array then it is represented as a <code>String</code>
	 * and returned. If the argument is <i>null</i> then the <code>String</code>
	 * "null" is returned.
	 * 
	 * @param obj
	 *            spacified the object which will be represented as a
	 *            <code>String</code>.
	 * 
	 * @see com.sap.engine.services.dc.util.ToStringBuilder#arrayToString(Object)
	 * @see com.sap.engine.services.dc.util.ToStringBuilder#objectToString(Object)
	 * @return <code>String</code> "null" if the specified argument is null and
	 *         a <code>String</code> representation of it in the other cases.
	 */
	public String toString(Object obj) {
		if (obj == null) {
			return "null";
		}
		final Class clazz = obj.getClass();
		if (clazz.isArray()) {
			return arrayToString(obj);
		}
		return objectToString(obj);
	}

	/**
	 * Build a <code>String</code> object which represents an operation, which
	 * has no arguments and its result is not specified.
	 * 
	 * @param operationSignature
	 *            <code>String</code>, which specifies the operation name.
	 * @return <code>String</code> representation of the operation. <i>Example:
	 *         </i> Invoking operation x()
	 */
	public String operationToString(String operationSignature) {
		return operationToString(operationSignature, null);
	}

	/**
	 * Build a <code>String</code> object which represents an operation, which
	 * has arguments and its result is not specified.
	 * 
	 * @param operationSignature
	 *            <code>String</code>, which specifies the operation name.
	 * @param operationArguments
	 *            <code>Object[]</code>, which specifies the operation
	 *            arguments.
	 * @return <code>String</code> representation of the operation. <i>Example:
	 *         </i> Invoking operation x(java.lang.String "value")
	 */
	public String operationToString(String operationSignature,
			Object[] operationArguments) {
		return operationToString(operationSignature, operationArguments, null);
	}

	/**
	 * Build a <code>String</code> object which represents an operation, which
	 * has arguments and result.
	 * 
	 * @param operationSignature
	 *            <code>String</code>, which specifies the operation name.
	 * @param operationArguments
	 *            <code>Object[]</code>, which specifies the operation
	 *            arguments.
	 * @param operationResult
	 *            <code>Object</code>, which specifies the operation result.
	 * @return <code>String</code> representation of the operation. <i>Example:
	 *         </i> Invoking operation x(java.lang.String "value") :
	 *         java.lang.String "result"
	 */
	public String operationToString(String operationSignature,
			Object[] operationArguments, Object operationResult) {
		return getOperationPrefix()
				+ operationSignature
				+ "("
				+ (operationArguments == null ? ""
						: toString(operationArguments))
				+ ")"
				+ (operationResult == null ? "" : " : "
						+ toString(operationResult));
	}

	/**
	 * Build a <code>String</code> object which represents a void operation,
	 * which has no arguments.
	 * 
	 * @param operationSignature
	 *            <code>String</code>, which specifies the operation name.
	 * @return <code>String</code> representation of the operation. <i>Example:
	 *         </i> Invoking operation x() : void
	 */
	public String voidOperationToString(String operationSignature) {
		return voidOperationToString(operationSignature, null);
	}

	/**
	 * Build a <code>String</code> object which represents a void operation,
	 * which has arguments.
	 * 
	 * @param operationSignature
	 *            <code>String</code>, which specifies the operation name.
	 * @param operationArguments
	 *            <code>Object[]</code>, which specifies the operation
	 *            arguments.
	 * @return <code>String</code> representation of the operation. <i>Example:
	 *         </i> Invoking operation x(java.lang.String "value") : void
	 */
	public String voidOperationToString(String operationSignature,
			Object[] operationArguments) {
		return getOperationPrefix()
				+ operationSignature
				+ "("
				+ (operationArguments == null ? ""
						: toString(operationArguments)) + ") : void";
	}

	/**
	 * @param array
	 *            specifies the array which will be represented as a
	 *            <code>String</code>.
	 * 
	 * @return <code>String</code> representation of the specified argument. If
	 *         the argument is null then "null" is returned. In the other cases
	 *         the method iterates over the array and combines the
	 *         <code>String</code> representations of each array element, then
	 *         the combined value is returned.
	 */
	protected String arrayToString(Object array) {
		if (array == null)
			return "null";
		final int length = Array.getLength(array);
		final StringBuffer sbStringRepresentation = new StringBuffer();
		sbStringRepresentation.append("[ ");
		for (int i = 0; i < length; i++) {
			Object arrItem = Array.get(array, i);
			sbStringRepresentation.append(toString(arrItem));
			sbStringRepresentation.append(getArrayObjectsSeparator());
		}
		sbStringRepresentation.append("]");
		return sbStringRepresentation.toString();
	}

	/**
	 * @param obj
	 *            specifies the object.
	 * 
	 * @return <code>String</code> representation of the specified argument. If
	 *         the argument is null then "null" is returned. In the other cases
	 *         the method returns: obj.getClass().getName() + " \"" +
	 *         obj.toString() + "\"";
	 */
	protected String objectToString(Object obj) {
		if (obj == null) {
			return "null";
		}
		return obj.getClass().getName() + " \"" + obj.toString() + "\"";
	}

	/**
	 * @return <code>String</code>, which is prepended to the operation name.
	 * @see com.sap.engine.services.dc.util.ToStringBuilder#operationToString()
	 * @see com.sap.engine.services.dc.util.ToStringBuilder#voidOperationToString()
	 */
	public String getOperationPrefix() {
		return operationPrefix == null ? "" : operationPrefix;
	}

	/**
	 * @param operationPrefix
	 *            <code>String</code>, which specifies the new value for the
	 *            operation prefix.
	 */
	public void setOperationPrefix(String operationPrefix) {
		this.operationPrefix = operationPrefix;
	}

	/**
	 * @return <code>String</code>, which specifies how objects from an array
	 *         will be separated when the array data is represented as a
	 *         <code>String</code>.
	 */
	public String getArrayObjectsSeparator() {
		return arrayObjectsSeparator == null ? "" : arrayObjectsSeparator;
	}

	/**
	 * @param arrayObjectsSeparator
	 *            <code>String</code>, which specifies the new value for the
	 *            array separator.
	 */
	public void setArrayObjectsSeparator(String arrayObjectsSeparator) {
		this.arrayObjectsSeparator = arrayObjectsSeparator;
	}

	public String safeConcatString(String one, String other) {
		return (one != null ? one : EMPTY_STRING).concat(other != null ? other
				: EMPTY_STRING);
	}

}
