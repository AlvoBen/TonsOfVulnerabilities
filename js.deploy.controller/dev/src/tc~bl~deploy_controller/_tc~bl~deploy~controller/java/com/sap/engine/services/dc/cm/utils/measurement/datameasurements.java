package com.sap.engine.services.dc.cm.utils.measurement;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;

public class DataMeasurements {
	
	public final static String PRE_PHASE = "PrePhase";
	public final static String ONLINE = "Online";
	public final static String OFFLINE = "Offline";
	public final static String POST_ONLINE = "PostOnline";
	
	private final Map<String, DMeasurement> measurements = 
		new LinkedHashMap<String, DMeasurement>(4);
			
	public DataMeasurements() {}
	
	
	public void setPrePhaseMeasurement(final DMeasurement preProcessMeasurement) {
		measurements.put(PRE_PHASE, preProcessMeasurement);
	}	
	
	public void setOfflineMeasurement(final DMeasurement offlineMeasurement) {
		measurements.put(OFFLINE, offlineMeasurement);
	}
	
	public void setOnlineMeasurement(final DMeasurement onlineMeasurement) {
		measurements.put(ONLINE, onlineMeasurement);
	}
	
	public void setPostOnlineMeasurement(final DMeasurement postOnlineMeasurement) {
		measurements.put(POST_ONLINE, postOnlineMeasurement);
	}

	public Map<String, DMeasurement> getMeasurements() {
		return Collections.unmodifiableMap(measurements);
	}
	
	
	
	
	

}
