package com.sap.jms.protocol.notification;

import java.io.PrintStream;
import com.sap.jms.util.compat.PrintWriter;

/**
 * A dummy exception that carries the stack trace
 * from an exception which occured on the server.
*
*/
public class StackTraceException extends Exception
{
	static final long serialVersionUID = 4317249508160114033L;

	String m_server_stack_trace;

	protected StackTraceException(String server_stack_trace)
	{
		m_server_stack_trace = server_stack_trace;
	}

	public String toString()
	{
		return m_server_stack_trace;
	}

	/**
	 *  Prints the stack trace for this exception
	 *  @param the PrintWriter to use for output
	 */
	public void printStackTrace(PrintStream out)
	{
		printStackTrace(new PrintWriter(out));
	}

	/**
	 *  Prints the stack trace for this exception
	 *  @param the PrintWriter to use for output
	 */
	public void printStackTrace(PrintWriter out)
	{
		out.print(m_server_stack_trace);
		out.flush();
	}
}
