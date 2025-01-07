package com.sap.sdm.util.log.simplelog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.sap.sdm.util.log.Trace;
import com.sap.sdm.util.log.TraceFactory;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
final class SimpleTraceFactory implements TraceFactory {

	private final static String SDMCLIENTPROPERTIESNAME = "SDMClient.properties";
	private final static String SDMCLIENTTRACEFILENAME = "SDMClientTraceFileName";

	private final static SimpleTraceFactory INSTANCE = new SimpleTraceFactory();

	private String tracefilename = null;
	private boolean isTraceOn = false;
	private FileWriter fileWriter = null;

	private SimpleTraceFactory() {
		Properties properties = new Properties();
		File propertiesFile = new File(SDMCLIENTPROPERTIESNAME);
		if (propertiesFile.exists() == true) {
			try {
				FileInputStream fis = new FileInputStream(propertiesFile);
				properties.load(fis);
				tracefilename = properties.getProperty(SDMCLIENTTRACEFILENAME);
				if (tracefilename != null) {
					isTraceOn = true;
					fileWriter = new FileWriter(tracefilename);
					fileWriter
							.write("=================================================="
									+ System.getProperty("line.separator"));
					Date currentTime = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy.MMMMM.dd 'at' hh:mm:ss a");
					fileWriter.write("SDMClient Trace  -  "
							+ formatter.format(currentTime)
							+ System.getProperty("line.separator"));
					fileWriter
							.write("=================================================="
									+ System.getProperty("line.separator"));
					fileWriter.flush();
				} else {
					// no tracing
					isTraceOn = false;
				}
			} catch (IOException ioE) {
				// no tracing
				isTraceOn = false;
			}
		} else {
			// no tracing
			isTraceOn = false;
		}
	}

	static TraceFactory getInstance() {
		return INSTANCE;
	}

	public boolean isTracingTurnedOn(Class forClass) {
		return this.isTraceOn;
	}

	public Trace getTrace(Class forClass) {
		return new SimpleTrace(forClass, fileWriter);
	}

}
