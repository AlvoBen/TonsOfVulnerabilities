/*
 * Created on 29.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.sap.ip.j2eeengine.consistency;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author d021770
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Topic {
	public interface Listener {
		public void receive(Message message);
	}

	static Topic getTopic(String name) {
		return MessageBroker.service.getTopic(name);
	}

	private String name;
	private HashSet reg = new HashSet();

	Topic(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	synchronized public void register(Listener l) {
		reg.add(l);
	}

	synchronized public void unregister(Listener l) {
		reg.remove(l);
	}

	public void send(String message) {
		MessageBroker.service.send(name, message);
	}

	public void send(Properties message) {
		MessageBroker.service.send(name, message);
	}

	public void sendAndWait(String message) {
		MessageBroker.service.sendAndWait(name, message);
	}

	public void sendAndWait(Properties message) {
		MessageBroker.service.sendAndWait(name, message);
	}

	synchronized void receive(Message message) {
		message.setTopic(this);
		Iterator i = reg.iterator();
		while (i.hasNext()) {
			Listener l = (Listener) i.next();
			l.receive(message);
		}
	}
}
