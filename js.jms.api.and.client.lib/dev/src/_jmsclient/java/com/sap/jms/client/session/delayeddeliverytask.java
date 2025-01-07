package com.sap.jms.client.session;

import java.util.TimerTask;

class DelayedDeliveryTask extends TimerTask {

	private JMSMessageConsumer consumer;

	public DelayedDeliveryTask(JMSMessageConsumer consumer) {
		this.consumer = consumer;
	}

	public void run(){
		if (consumer != null) {
			consumer.resume();
		}
	}
}

