package com.sap.engine.services.dc.api.util;

import com.sap.engine.services.dc.api.util.measurement.DAMeasurement;
import com.sap.engine.services.dc.api.util.measurement.DAStatisticType;
import com.sap.engine.services.dc.api.util.measurement.impl.DAMeasurementImpl;
import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;
import com.sap.engine.services.dc.cm.utils.measurement.DStatisticType;

public final class MeasurementUtils {
	
	private MeasurementUtils(){}
	
	public static DAMeasurement map(DMeasurement dMeasurement){
		if (dMeasurement == null){
			return null;
		}
		return new DAMeasurementImpl(dMeasurement);
	}
	
	public static DAStatisticType mapStatisticType(final DStatisticType dStatisticType) {
		if (dStatisticType == null) {
			return null;			
		}
		
		DAStatisticType result = null;
		//time
		if (DStatisticType.ELAPSED_TIME_MS.equals(dStatisticType) ) {
			result = DAStatisticType.ELAPSED_TIME_MS;
		} else if (DStatisticType.ELAPSED_TIME_SECONDS.equals(dStatisticType) ) {
			result = DAStatisticType.ELAPSED_TIME_SECONDS;
		//cpu	
		} else if (DStatisticType.CPU_TIME_NANOSECONDS.equals(dStatisticType) ) {
			result = DAStatisticType.CPU_TIME_NANOSECONDS;
		} else if (DStatisticType.CPU_TIME_MICROSECONDS.equals(dStatisticType) ) {
			result = DAStatisticType.CPU_TIME_MICROSECONDS;
		} else if (DStatisticType.CPU_TIME_SECONDS.equals(dStatisticType) ) {
			result = DAStatisticType.CPU_TIME_SECONDS;
		//memory	
		} else if (DStatisticType.ALLOCATED_MEMORY_BYTES.equals(dStatisticType) ) {
			result = DAStatisticType.ALLOCATED_MEMORY_BYTES;
		//io file	
		} else if (DStatisticType.FILEIO_BYTES_READ.equals(dStatisticType) ) {
			result = DAStatisticType.FILEIO_BYTES_READ;
		} else if (DStatisticType.FILEIO_BYTES_WRITTEN.equals(dStatisticType) ) {
			result = DAStatisticType.FILEIO_BYTES_WRITTEN;
		} else if (DStatisticType.FILES_OPENED.equals(dStatisticType) ) {
			result = DAStatisticType.FILES_OPENED;
		} else if (DStatisticType.FILES_STILL_OPEN.equals(dStatisticType) ) {
			result = DAStatisticType.FILES_STILL_OPEN;
		//io net
		} else if (DStatisticType.NETIO_BYTES_READ.equals(dStatisticType) ) {
			result = DAStatisticType.NETIO_BYTES_READ;
		} else if (DStatisticType.NETIO_BYTES_WRITTEN.equals(dStatisticType) ) {
			result = DAStatisticType.NETIO_BYTES_WRITTEN;		
		//io sockets
		} else if (DStatisticType.SOCKETS_OPENED.equals(dStatisticType) ) {
			result = DAStatisticType.SOCKETS_OPENED;
		} else if (DStatisticType.SOCKETS_STILL_OPEN.equals(dStatisticType) ) {
			result = DAStatisticType.SOCKETS_STILL_OPEN;
		//io others
		} else if (DStatisticType.OTHERIO_BYTES_READ.equals(dStatisticType) ) {
			result = DAStatisticType.OTHERIO_BYTES_READ;
		} else if (DStatisticType.OTHERIO_BYTES_WRITTEN.equals(dStatisticType) ) {
			result = DAStatisticType.OTHERIO_BYTES_WRITTEN;
		}
		
		return result;
	}
	
	private static class LinkMesurement {
		private final DMeasurement dMeasurement; 
		private final DAMeasurementImpl parentDAMeasurementImpl;
		
		LinkMesurement(DAMeasurementImpl parentDAMeasurementImpl, DMeasurement dMeasurement ){
			this.dMeasurement = dMeasurement;
			this.parentDAMeasurementImpl = parentDAMeasurementImpl;
		}

		public DMeasurement getDMeasurement() {
			return dMeasurement;
		}

		public DAMeasurementImpl getParentDAMeasurementImpl() {
			return parentDAMeasurementImpl;
		}
	}

}
