package com.sap.sdm.is.cs.remoteproxy.client;

import com.sap.sdm.is.cs.cmd.client.CmdClient;

/**
 * @author Christian Gabrisch 07.08.2003
 */
public interface CmdClientGetter {
	public CmdClient get() throws Exception;

	public class Exception extends java.lang.Exception {
		public Exception(String message) {
			super(message);
		}
	}
}
