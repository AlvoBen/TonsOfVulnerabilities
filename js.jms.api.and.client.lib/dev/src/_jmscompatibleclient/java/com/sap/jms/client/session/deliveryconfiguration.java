package com.sap.jms.client.session;

import com.sap.jms.JMSConstants;

public class DeliveryConfiguration {

	private long deliveryDelay;
	private long deliveryAttempts;
	
	public DeliveryConfiguration() {
		deliveryAttempts = JMSConstants.DEFAULT_MAX_DELIVERY_ATTEMPTS;
		deliveryDelay = JMSConstants.DEFAULT_DELIVERY_INTERVAL;
	}

	public long getDeliveryDelay() {
		return deliveryDelay;
	}

	public void setDeliveryDelay(long deliveryDelay) {
		this.deliveryDelay = deliveryDelay;
	}

	public long getDeliveryAttempts() {
		return deliveryAttempts;
	}

	public void setDeliveryAttempts(long deliveryAttempts) {
		this.deliveryAttempts = deliveryAttempts;
	}
	
}
