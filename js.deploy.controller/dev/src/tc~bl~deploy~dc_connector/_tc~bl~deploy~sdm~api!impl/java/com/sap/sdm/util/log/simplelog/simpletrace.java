package com.sap.sdm.util.log.simplelog;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.sap.sdm.util.log.Trace;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
class SimpleTrace extends Trace {

	private FileWriter fileWriter = null;
	private String className = null;

	SimpleTrace(Class forClass, FileWriter fileWriter) {
		this.fileWriter = fileWriter;
		this.className = forClass.getName();
	}

	public void entering(String methodName) {
		StringBuffer output = new StringBuffer();
		output.append(className);
		output.append(".");
		output.append(methodName);
		output.append(": Entering method");
		output.append(System.getProperty("line.separator"));
		try {
			fileWriter.write(output.toString());
			fileWriter.flush();
		} catch (IOException ioE) {// $JL-EXC$
		}
	}

	public void exiting() {
		StringBuffer output = new StringBuffer();
		output.append(className);
		output.append(": Exiting method");
		output.append(System.getProperty("line.separator"));
		try {
			fileWriter.write(output.toString());
			fileWriter.flush();
		} catch (IOException ioE) {// $JL-EXC$
		}
	}

	public void exiting(String methodName) {
		StringBuffer output = new StringBuffer();
		output.append(className);
		output.append(".");
		output.append(methodName);
		output.append(": Exiting method");
		output.append(System.getProperty("line.separator"));
		try {
			fileWriter.write(output.toString());
			fileWriter.flush();
		} catch (IOException ioE) {// $JL-EXC$
		}
	}

	public void debug(String debugInfo) {
		StringBuffer output = new StringBuffer();
		output.append(className);
		output.append(": debug \"");
		output.append(debugInfo);
		output.append("\"");
		output.append(System.getProperty("line.separator"));
		try {
			fileWriter.write(output.toString());
			fileWriter.flush();
		} catch (IOException ioE) {// $JL-EXC$
		}
	}

	public void debug(String debugInfo, Throwable throwable) {
		StringBuffer sb = new StringBuffer(debugInfo);
		if (throwable != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			throwable.printStackTrace(pw);

			sb.append(System.getProperty("line.separator"));
			sb.append(sw.toString());
		}

		debug(sb.toString());
	}
}
