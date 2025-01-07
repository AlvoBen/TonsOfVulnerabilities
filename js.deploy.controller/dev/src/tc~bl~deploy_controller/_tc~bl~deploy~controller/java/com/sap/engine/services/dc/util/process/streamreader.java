package com.sap.engine.services.dc.util.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
class StreamReader extends Thread {

	private final InputStream is;
	private final Set<String> result;

	StreamReader(InputStream is) {
		this.is = is;
		this.result = new LinkedHashSet<String>();
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				addResult(line);
			}
		} catch (IOException ioEx) {
			addResult(ioEx);
		}
	}

	private void addResult(String line) {
		result.add(line);
	}

	private void addResult(Throwable th) {
		final StringWriter strWr = new StringWriter();
		th.printStackTrace(new PrintWriter(strWr));
		result.add(strWr.toString());
	}

	public Set<String> getResult() {
		return result;
	}

}
