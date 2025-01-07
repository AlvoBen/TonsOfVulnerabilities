package com.sap.engine.services.dc.cm.utils.measurement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.sap.engine.services.accounting.measurement.AMeasurement;
import com.sap.engine.services.accounting.measurement.statistic.AStatistic;
import com.sap.engine.services.accounting.measurement.statistic.AStatisticType;
import com.sap.engine.services.dc.cm.CM;

public final class MeasurementUtils {
	
	private MeasurementUtils(){}
	
	private final static MeasurementFactory measurementFactory = MeasurementFactory.getInstance();
	
	public static DMeasurement build(final DataMeasurements dataMeasurements, final String sessionId) {
		if (dataMeasurements == null) {
			return null;
		}
		
		final DMeasurement result = measurementFactory.createMeasurement("Deploy with sessionId [" + sessionId + "]", 
				CM.SERVICE_NAME, new HashSet<DStatistic>(), false);
		final Iterator measurementsIter = dataMeasurements.getMeasurements().values().iterator();
		while(measurementsIter.hasNext()) {
			final DMeasurement measurement = (DMeasurement) measurementsIter.next();
			measurementFactory.addChild(result, measurement);
		}	
		return result;
	}
	
	/**
	 * Maps accounting measurement to deploy measurement.
	 * 
	 * @param aMeasurement accounting measurement
	 * @return deploy measurement
	 */
	public static DMeasurement map(final AMeasurement aMeasurement){
		//TODO review that because we have OOM for large tree of measurements 
		if (true) {
			return null;
		}
		//end of TODO
		
		if (aMeasurement == null) {
			return null;
		}
		
		final LinkedList<LinkMesurement> fifoStack = new LinkedList<LinkMesurement>();
		DMeasurement result = null;
		boolean isResult = false;
		fifoStack.add(new LinkMesurement(null, aMeasurement));
		while ( fifoStack.size() > 0 ) {
			final LinkMesurement linkMeasurement = fifoStack.poll();
			final DMeasurement parentDMeasurement = map(linkMeasurement);
			if (!isResult) {
				isResult = true;
				result = parentDMeasurement;
			}
			final AMeasurement measurement = linkMeasurement.getAMeasurement();
			final Iterator<AMeasurement> childrenIterator = measurement.getChildrenMeasurments().iterator();
			while(childrenIterator.hasNext()) {
				AMeasurement childAMeasurement = childrenIterator.next();
				fifoStack.add(new LinkMesurement(parentDMeasurement, childAMeasurement));				
			}
		}
		
		return result;		
	}
	
	private static DMeasurement map(LinkMesurement linkMeasurement){
		final AMeasurement aMeasurement = linkMeasurement.getAMeasurement();
		final DMeasurement parentDMeasurement = linkMeasurement.getParentDMeasurement();
		
		final DMeasurement result = measurementFactory.createMeasurement(
				aMeasurement.getTagName(),aMeasurement.getDcName(),
				mapStatistics(aMeasurement.getStatistics()),aMeasurement.hasNewThreadStarted()
				);
		
		if ( parentDMeasurement != null) {
			measurementFactory.addChild(parentDMeasurement, result);			
		}
		return result;
	}
	
	private static Set<DStatistic> mapStatistics(final Set<AStatistic> aStatistics) {
		if (aStatistics == null) {
			return null;			
		}
		
		final Iterator<AStatistic> statisticsIterator = aStatistics.iterator();
		final Set<DStatistic> result = new HashSet<DStatistic>();
		while(statisticsIterator.hasNext()) {
			final AStatistic aStatistic = statisticsIterator.next();
			DStatistic statistic = measurementFactory.createStatistic(
					mapStatisticType(aStatistic.getType()),
					aStatistic.getValue());
			result.add(statistic);
		}	
		return result;
	}
	
	private static DStatisticType mapStatisticType(final AStatisticType aStatisticType) {
		if (aStatisticType == null) {
			return null;			
		}
		
		DStatisticType result = null;
		//time
		if (AStatisticType.ELAPSED_TIME_MS.equals(aStatisticType) ) {
			result = DStatisticType.ELAPSED_TIME_MS;
		} else if (AStatisticType.ELAPSED_TIME_SECONDS.equals(aStatisticType) ) {
			result = DStatisticType.ELAPSED_TIME_SECONDS;
		//cpu	
		} else if (AStatisticType.CPU_TIME_NANOSECONDS.equals(aStatisticType) ) {
			result = DStatisticType.CPU_TIME_NANOSECONDS;
		} else if (AStatisticType.CPU_TIME_MICROSECONDS.equals(aStatisticType) ) {
			result = DStatisticType.CPU_TIME_MICROSECONDS;
		} else if (AStatisticType.CPU_TIME_SECONDS.equals(aStatisticType) ) {
			result = DStatisticType.CPU_TIME_SECONDS;
		//memory	
		} else if (AStatisticType.ALLOCATED_MEMORY_BYTES.equals(aStatisticType) ) {
			result = DStatisticType.ALLOCATED_MEMORY_BYTES;
		//io file	
		} else if (AStatisticType.FILEIO_BYTES_READ.equals(aStatisticType) ) {
			result = DStatisticType.FILEIO_BYTES_READ;
		} else if (AStatisticType.FILEIO_BYTES_WRITTEN.equals(aStatisticType) ) {
			result = DStatisticType.FILEIO_BYTES_WRITTEN;
		} else if (AStatisticType.FILES_OPENED.equals(aStatisticType) ) {
			result = DStatisticType.FILES_OPENED;
		} else if (AStatisticType.FILES_STILL_OPEN.equals(aStatisticType) ) {
			result = DStatisticType.FILES_STILL_OPEN;
		//io net
		} else if (AStatisticType.NETIO_BYTES_READ.equals(aStatisticType) ) {
			result = DStatisticType.NETIO_BYTES_READ;
		} else if (AStatisticType.NETIO_BYTES_WRITTEN.equals(aStatisticType) ) {
			result = DStatisticType.NETIO_BYTES_WRITTEN;		
		//io sockets
		} else if (AStatisticType.SOCKETS_OPENED.equals(aStatisticType) ) {
			result = DStatisticType.SOCKETS_OPENED;
		} else if (AStatisticType.SOCKETS_STILL_OPEN.equals(aStatisticType) ) {
			result = DStatisticType.SOCKETS_STILL_OPEN;
		//io others
		} else if (AStatisticType.OTHERIO_BYTES_READ.equals(aStatisticType) ) {
			result = DStatisticType.OTHERIO_BYTES_READ;
		} else if (AStatisticType.OTHERIO_BYTES_WRITTEN.equals(aStatisticType) ) {
			result = DStatisticType.OTHERIO_BYTES_WRITTEN;
		}
		
		return result;
	}
	
	private static class LinkMesurement {
		private final AMeasurement aMeasurement; 
		private final DMeasurement parentDMeasurement;
		
		LinkMesurement(DMeasurement parentDMeasurement, AMeasurement aMeasurement ){
			this.aMeasurement = aMeasurement;
			this.parentDMeasurement = parentDMeasurement;
		}

		public AMeasurement getAMeasurement() {
			return aMeasurement;
		}

		public DMeasurement getParentDMeasurement() {
			return parentDMeasurement;
		}
	}

}
