package com.sap.sdm.is.cs.ncwrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A wrapper for <code>com.sap.bc.cts.tp.net.NetComm</code>.
 * 
 * See documentation of the wrapped class for further information.
 * 
 * @author Java Change Management May 17, 2004
 */
public interface NetComm {
	public String getEOCS();

	public OutputStream getOutputStream();

	public InputStream getInputStream();

	public String receive() throws IOException;

	public void send(String str);
}
