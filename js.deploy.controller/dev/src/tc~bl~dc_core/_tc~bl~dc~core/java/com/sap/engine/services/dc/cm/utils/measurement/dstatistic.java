package com.sap.engine.services.dc.cm.utils.measurement;

import java.io.Serializable;

public interface DStatistic extends Serializable {
	
	DStatisticType getType();
	
	Long getValue();

}
