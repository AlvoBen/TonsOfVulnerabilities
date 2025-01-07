package com.tssap.dtr.client.lib.protocol.templates;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Thrown when a ConnectionFactory cannot return a connection matching
 * a requested connection template. This may be due to an invalid or
 * unknown templateID or the manager was unable to create or retrieve
 * a matching template.
 */
public class TemplateException extends Exception {
	
	/** Unqiue ID for serialization */
	private static final long serialVersionUID = 3334668339862884880L;

	/**
	 * The embedded exception, or null.
	 */
	private Throwable cause;

	/**
	 * Create a new HTTPException without message.
	 */
	public TemplateException() {
		super();
		this.cause = null;
	}

	/**
	 * Create a new ProtocolException.
	 * @param message - the error or warning message.
	 */
	public TemplateException(String message) {
		super(message);
		this.cause = null;
	}

	/**
	 * Create a new HTTPException wrapping an existing exception.
	 *
	 * <p>The existing exception will be embedded in the new
	 * one, and its message will become the default message for
	 * the ConnectionException.</p>
	 * @param cause - the exception to be wrapped in this EmbeddedException.
	 */
	public TemplateException(Throwable cause) {
		super();
		this.cause = cause;
	}

	/**
	 * Create a new HTTPException from an existing exception.
	 *
	 * <p>The existing exception will be embedded in the new
	 * one, but the new exception will have its own message.</p>
	 * @param message - the detail message.
	 * @param cause - the exception to be wrapped in this ProtocolException.
	 */
	public TemplateException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}

	/**
	 * Return a detail message for this exception.
	 *
	 * <p>If there is an embedded exception, and if the EmbeddedException
	 * has no detail message of its own, this method will return
	 * the detail message from the embedded exception.</p>
	 * @return The error or warning message.
	 */
	public String getMessage() {
		StringBuffer message = new StringBuffer();
		if (super.getMessage() != null) {
			message.append(super.getMessage());
		}
		if (cause != null) {
			if (message.length() > 0) {
				message.append(" [reason: ").append(cause.getMessage()).append("]");
			} else {
				message.append(cause.getMessage());
			}
		}
		return message.toString();
	}

	/**
	 * Return the embedded exception, if any.
	 * @return The embedded exception, or null if there is none.
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * Prints this exception and its backtrace to the standard error stream.
	 * If the exception wraps another exception (or even a chain of
	 * exceptions) the stack traces of these execeptions are printed as
	 * well.
	 */
	public void printStackTrace() {
		if (cause != null) {
			System.err.println(getClass().getName() + ": " + getMessage());
			System.err.println(topOfStack());
			cause.printStackTrace();
			System.err.flush();
		} else {
			super.printStackTrace();
		}
	}

	/**
	 * Prints this exception and its backtrace to the given stream.
	 * If the exception wraps another exception (or even a chain of
	 * exceptions) the stack traces of these execeptions are printed as
	 * well.
	 * @param s  the output stream to use for backtracing.
	 */
	public void printStackTrace(PrintStream s) {
		if (cause != null) {
			s.println(getClass().getName() + ": " + getMessage());
			cause.printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
	}

	/**
	 * Prints this exception and its backtrace to the given stream.
	 * If the exception wraps another exception (or even a chain of
	 * exceptions) the stack traces of these execeptions are printed as
	 * well.
	 * @param s  the output stream writer to use for backtracing.
	 */
	public void printStackTrace(PrintWriter s) {
		if (cause != null) {
			s.println(getClass().getName() + ": " + getMessage());
			cause.printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
	}

	private String topOfStack() {
		class StackOutputStream extends OutputStream {
			StringBuffer secondLine = new StringBuffer();
			int line = 1;

			public void write(int b) throws IOException {
				if (b == '\n') {
					line++;
					return;
				}
				if (line == 2) {
					secondLine.append((char) b);
				}
			}
		};

		StackOutputStream outputStream = new StackOutputStream();
		super.printStackTrace(new PrintStream(outputStream));
		return outputStream.secondLine.toString();
	}

}
