package com.sap.engine.services.dc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ICMInfo;
import com.sap.engine.services.dc.cm.dscr.TestInfo;
import com.sap.engine.services.dc.cm.web_disp.WDController;
import com.sap.engine.services.dc.cm.web_disp.WDControllerFactory;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.manage.messaging.Message;
import com.sap.engine.services.dc.manage.messaging.MessageSender;
import com.sap.engine.services.dc.manage.messaging.MessagingException;
import com.sap.engine.services.dc.manage.messaging.MessagingFactory;

public final class RollingUtils {

	private static final int UNKNOWN_PORT = -1;
	private static final String UNKNOWN_HOST = "unknown";
	private static final int MAX_MESSAGE_LENGTH = 7 * 1024 * 1024;// 7Mb < 8Mb

	// max
	// allowed
	// size

	public static final void sendMessage(int sendTo, Object body, int type)
			throws MessagingException {
		MessagingFactory messagingFactory = MessagingFactory.getInstance();
		MessageSender messageSender = messagingFactory.createMessageSender();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(body);
			oos.close();
			baos.close();
			int msgSize = baos.size();
			if (msgSize < MAX_MESSAGE_LENGTH) {
				byte[] bytes = baos.toByteArray();
				Message response = messagingFactory.createMessage(sendTo, type,
						bytes, 0, bytes.length);
				messageSender.sendToParticipant(response);
			} else {
				throw new MessagingException("The rolling message with type: "
						+ type
						+ " will not be distributed because of huge size: "
						+ msgSize);
			}
		} catch (IOException e) {
			throw new MessagingException("The rolling message  with type: "
					+ type + " will not be distributed.", e);
		}

	}

	public static final TestInfo createTestInfo(int instanceId) {
		InetSocketAddress address = ServiceConfigurer.getInstance()
				.getInstanceHttpAccessPoint(instanceId);
		ClusterDscrFactory clusterDscrFactory = ClusterDscrFactory
				.getInstance();
		ICMInfo icmInfo;
		if (address != null) {
			icmInfo = clusterDscrFactory.createICMInfo(address.getHostName(),
					address.getPort());
		} else {
			icmInfo = clusterDscrFactory.createICMInfo(UNKNOWN_HOST,
					UNKNOWN_PORT);
		}
		TestInfo testInfo = clusterDscrFactory.createTestInfo(icmInfo);
		return testInfo;
	}

}
