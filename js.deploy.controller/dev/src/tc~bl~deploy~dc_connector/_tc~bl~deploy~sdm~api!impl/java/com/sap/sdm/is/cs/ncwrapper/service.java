package com.sap.sdm.is.cs.ncwrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * A wrapper for <code>com.sap.bc.cts.tp.net.Service</code>.
 * 
 * See documentation of the wrapped class for further information.
 * 
 * @author Java Change Management May 17, 2004
 */
public interface Service {
	public void serve(InputStream in, OutputStream out)
			throws InterruptedIOException, IOException;

	public void endIt(InputStream in, OutputStream out);

	public String toString();

	public void setNumber(int number);

	public int getNumber();
}
