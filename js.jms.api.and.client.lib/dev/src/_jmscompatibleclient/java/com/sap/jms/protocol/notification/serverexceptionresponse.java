/**
 * ServerExceptionResponse.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;
import java.io.StringWriter;

import javax.jms.IllegalStateException;
import javax.jms.InvalidClientIDException;
import javax.jms.InvalidDestinationException;
import javax.jms.InvalidSelectorException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import javax.jms.ResourceAllocationException;
import javax.jms.TransactionInProgressException;
import javax.jms.TransactionRolledBackException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.JMSXAException;
import com.sap.jms.protocol.PacketImpl;

/**
 * This class encapsulates all exception responses which
 * could be send by the server.
 *
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ServerExceptionResponse extends PacketImpl {

	public static final byte EXCEPTION = 0x00;
	public static final byte JMS_EXCEPTION = 0x01;
	public static final byte ILLEGAL_STATE_EXCEPTION = 0x02;
	public static final byte INVALID_CLIENT_ID_EXCEPTION = 0x03;
	public static final byte INVALID_DESTINATION_EXCEPTION = 0x04;
	public static final byte INVALID_SELECTOR_EXCEPTION = 0x05;
	public static final byte JMS_SECURITY_EXCEPTION = 0x06;
	public static final byte MESSAGE_EOF_EXCEPTION = 0x07;
	public static final byte MESSAGE_FORMAT_EXCEPTION = 0x08;
	public static final byte MESSAGE_NOT_READABLE_EXCEPTION = 0x09;
	public static final byte MESSAGE_NOT_WRITEABLE_EXCEPTION = 0x10;
	public static final byte RESOURCE_ALLOCATION_EXCEPTION = 0x11;
	public static final byte TRANSACTION_IN_PROGRESS_EXCEPTION = 0x12;
	public static final byte TRANSACTION_ROLLED_BACK_EXCEPTION = 0x13;
	public static final byte JMS_XA_EXCEPTION = 0x14;

	public static final byte TYPE_ID = SERVER_EXCEPTION_RESPONSE;

	static final int POS_ERROR_CLASS = 0;
	static final int POS_STRING_ARRAY = POS_ERROR_CLASS + SIZEOF_BYTE;
	static final int SIZE = POS_STRING_ARRAY;

	/**
	 * Constructor for ServerExceptionResponse.
	 */
	public ServerExceptionResponse() {
		super();
	}

	/**
	 * Constructor for ServerExceptionResponse.
	 */
	public ServerExceptionResponse(Exception ex) throws JMSException {
		byte type;
		String reason;
		String msg = ex.getMessage();

		if (ex instanceof InvalidClientIDException) {
			type = INVALID_CLIENT_ID_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof IllegalStateException) {
			type = ILLEGAL_STATE_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof InvalidDestinationException) {
			type = INVALID_DESTINATION_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof InvalidSelectorException) {
			type = INVALID_SELECTOR_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof JMSSecurityException) {
			type = JMS_SECURITY_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof MessageEOFException) {
			type = MESSAGE_EOF_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof MessageFormatException) {
			type = MESSAGE_FORMAT_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof MessageNotReadableException) {
			type = MESSAGE_NOT_WRITEABLE_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof MessageNotWriteableException) {
			type = MESSAGE_NOT_WRITEABLE_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof ResourceAllocationException) {
			type = RESOURCE_ALLOCATION_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof TransactionInProgressException) {
			type = TRANSACTION_IN_PROGRESS_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof TransactionRolledBackException) {
			type = TRANSACTION_ROLLED_BACK_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else if (ex instanceof JMSXAException) {
			type = JMS_XA_EXCEPTION;
			reason = ((JMSXAException)ex).getErrorCode();
		}
		else if (ex instanceof JMSException) {
			type = JMS_EXCEPTION;
			reason = ((JMSException)ex).getErrorCode();
		}
		else {
			type = EXCEPTION;
			reason = "Unknown";
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		if (ex instanceof JMSException) {
			Exception ex2 = ((JMSException) ex).getLinkedException();
			if (ex2 != null) {
				ex2.printStackTrace(pw);
			}
		}
		pw.flush();

		setException(type, msg, reason, sw.toString());
	}

	/**
	 * Fills the packet
	 * @param msg the reason for the error
	 * @param errorCode the error code
	 * @throws JMSException if something went wrong
	 */
	private void setException(byte error_class, String msg, String errorCode, String stacktrace) throws JMSException {
		String[] value = new String[] { msg, errorCode, stacktrace };
		allocate(TYPE_ID, SIZE + strlenUTF8(value));
		setByte(POS_ERROR_CLASS, error_class);
		setUTF8Array(POS_STRING_ARRAY, value);
	}

	/**
	 *  Returns the error class
	 *  @return the error class
	 */
	public final byte getErrorClass() throws BufferUnderflowException {
		return getByte(POS_ERROR_CLASS);
	}

	/**
	 *  Returns the error text, i.e. the reason
	 *  @return the error text
	 */
	public final String getReason() throws BufferUnderflowException {
		String[] value = getStringArray(POS_STRING_ARRAY);
		return ((value != null) && (value.length >= 1)) ? value[0] : null;
	}

	/**
	 *  Returns the error code
	 *  @return the error code
	 */
	public final String getErrorCode() throws BufferUnderflowException {
		String[] value = getStringArray(POS_STRING_ARRAY);
		return ((value != null) && (value.length >= 2)) ? value[1] : null;
	}

	/**
	 *  Returns the error code
	 *  @return the error code
	 */
	public final String getStackTrace() throws BufferUnderflowException {
		String[] value = getStringArray(POS_STRING_ARRAY);
		return ((value != null) && (value.length >= 3)) ? value[2] : null;
	}


	public final JMSException getException() throws BufferUnderflowException {
		byte type = getByte(POS_ERROR_CLASS);
		String[] value = getStringArray(POS_STRING_ARRAY);
		String reason = (value != null && value.length >= 1) ? value[0] : null;
		String code = (value != null && value.length >= 2) ? value[1] : null;
		String trace = (value != null && value.length >= 3) ? value[2] : null;
		JMSException ex;

		switch (type) {
		case ILLEGAL_STATE_EXCEPTION :
			ex = new IllegalStateException(reason, code);
			break;
		case INVALID_CLIENT_ID_EXCEPTION :
			ex = new InvalidClientIDException(reason, code);
			break;
		case INVALID_DESTINATION_EXCEPTION :
			ex = new InvalidDestinationException(reason, code);
			break;
		case INVALID_SELECTOR_EXCEPTION :
			ex = new InvalidSelectorException(reason, code);
			break;
		case JMS_SECURITY_EXCEPTION :
			ex = new JMSSecurityException(reason, code);
			break;
		case MESSAGE_EOF_EXCEPTION :
			ex = new MessageEOFException(reason, code);
			break;
		case MESSAGE_FORMAT_EXCEPTION :
			ex = new MessageFormatException(reason, code);
			break;
		case MESSAGE_NOT_READABLE_EXCEPTION :
			ex = new MessageNotReadableException(reason, code);
			break;
		case MESSAGE_NOT_WRITEABLE_EXCEPTION :
			ex = new MessageNotWriteableException(reason, code);
			break;
		case RESOURCE_ALLOCATION_EXCEPTION :
			ex = new ResourceAllocationException(reason, code);
			break;
		case TRANSACTION_IN_PROGRESS_EXCEPTION :
			ex = new TransactionInProgressException(reason, code);
			break;
		case TRANSACTION_ROLLED_BACK_EXCEPTION :
			ex = new TransactionRolledBackException(reason, code);
			break;
		case JMS_XA_EXCEPTION :
			ex = new JMSXAException(reason, code);
			break;
		default :
			ex = new JMSException(reason, code);
		}

		if (trace != null)
		{
			StackTraceException ex2 = new StackTraceException(trace);
			ex.initCause(ex2);
			ex.setLinkedException(ex2);
		}

		return ex;
	}

	/**
	 *  Returns a string representation of the packet
	 *  @param out to writer to use to print the packet
	 */
	 protected void toString(PrintWriter out) throws Exception {
		super.toString(out);
		//----------------------------------------------------------------
		// Print Exception content
		//----------------------------------------------------------------
		out.println("------------------------------ Exception Content -------------------------------");
		out.printf("%30s %d\n%30s %s\n%30s %s\n%30s %s\n%30s\n", new Object[]{
				"ErrorClass:", new Byte(getErrorClass()),
				"ErrorCode:", getErrorCode(),
				"Exception:", getException().toString(),
				"Reason:", getReason(),
		"Trace:"});
		String trace = getStackTrace();
		if (trace != null) out.println(trace);
	 }
}
