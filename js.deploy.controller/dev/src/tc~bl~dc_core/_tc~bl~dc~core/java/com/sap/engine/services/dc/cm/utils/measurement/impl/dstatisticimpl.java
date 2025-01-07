package com.sap.engine.services.dc.cm.utils.measurement.impl;

import com.sap.engine.services.dc.cm.utils.measurement.DStatistic;
import com.sap.engine.services.dc.cm.utils.measurement.DStatisticType;

class DStatisticImpl implements DStatistic {
	
	private static final long serialVersionUID = 0L;
	
	private final DStatisticType type;
	private final Long value;
	
	DStatisticImpl(DStatisticType type, Long value){
		this.type = type;
		this.value = value;
	}

	public DStatisticType getType() {
		return this.type;
	}

	public Long getValue() {
		return this.value;
	}
	
	public boolean equals(final Object other) {

		if (other == null) {
			return false;
		}

		if (this == other) {
			return true;
		}

		if (this.getClass() != other.getClass()) {
			return false;
		}

		final DStatistic otherStatistic = (DStatistic) other;

		if (!getType().equals(otherStatistic.getType())) {
			return false;
		}

		if (!getValue().equals(otherStatistic.getValue())) {
			return false;
		}

		return true;
	}

	public String toString() {
		return "type[" + getType() + "], value[" + getValue() + "]";
	}
	
	public int hashCode(){
		return this.type.hashCode();
	}


}
