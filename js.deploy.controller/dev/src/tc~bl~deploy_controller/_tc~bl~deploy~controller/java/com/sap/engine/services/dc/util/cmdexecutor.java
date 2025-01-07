package com.sap.engine.services.dc.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class CmdExecutor {

	private CmdExecutor() {
	}

	public static CmdExecResult exec(String command, String[] envp, File dir)
			throws IOException, InterruptedException {
		final int result;
		final StringBuffer out = new StringBuffer();
		final StringBuffer err = new StringBuffer();

		final Process process = Runtime.getRuntime().exec(command, envp, dir);

		final StreamReaderThread outThread = new StreamReaderThread(process
				.getInputStream(), out);
		final StreamReaderThread errThread = new StreamReaderThread(process
				.getErrorStream(), err);

		outThread.start();
		errThread.start();

		result = process.waitFor();

		outThread.join();
		errThread.join();

		return new CmdExecResult(result, out, err);
	}

	private static class StreamReaderThread extends Thread {
		final StringBuffer sbOut;
		final InputStreamReader isReader;

		public StreamReaderThread(InputStream in, StringBuffer sbOut) {
			this.sbOut = sbOut;
			this.isReader = new InputStreamReader(in);
		}

		public void run() {
			int ch;
			try {
				while ((ch = isReader.read()) != -1) {
					sbOut.append((char) ch);
				}
			} catch (Exception e) {
				sbOut.append("\nRead error while reading the stream:"
						+ e.getMessage());
			}
		}
	}

	public static class CmdExecResult {
		private final int result;
		private final StringBuffer out;
		private final StringBuffer err;
		private final int hashCode;
		private final String toString;

		private CmdExecResult(int result, StringBuffer out, StringBuffer err) {
			this.result = result;
			this.out = out;
			this.err = err;

			final int offset = 17;
			this.hashCode = offset + this.result;

			this.toString = "The result is '" + this.result
					+ "', out text is '" + this.out + "', err text is '"
					+ this.err + "'.";
		}

		public int getResult() {
			return this.result;
		}

		public StringBuffer getOut() {
			return this.out;
		}

		public StringBuffer getErr() {
			return this.err;
		}

		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (this == obj) {
				return true;
			}

			if (this.getClass() != obj.getClass()) {
				return false;
			}

			final CmdExecResult otherCmdExecResult = (CmdExecResult) obj;

			if (this.getResult() != otherCmdExecResult.getResult()) {
				return false;
			}

			return true;
		}

		public int hashCode() {
			return this.hashCode;
		}

		public String toString() {
			return this.toString;
		}
	}

}
