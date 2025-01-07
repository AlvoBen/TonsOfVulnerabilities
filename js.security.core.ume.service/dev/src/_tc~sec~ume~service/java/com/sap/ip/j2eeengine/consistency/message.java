/*
 * Created on 30.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.sap.ip.j2eeengine.consistency;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author d021770
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Message implements Serializable {
	private String topic_name;
	private String message;
	private Properties properties;

	private transient Topic topic;

	void setTopic(Topic topic) {
		this.topic = topic;
		this.topic_name = topic.getName();
	}

	Message(String topic, String message) {
		this.topic_name = topic;
		this.message = message;
	}

	Message(String topic, Properties message) {
		this.topic_name = topic;
		this.properties = message;
	}

	public Message(Topic topic, String message) {
		setTopic(topic);
		this.message = message;
	}

	public Message(Topic topic, Properties props) {
		setTopic(topic);
		this.properties = props;
	}

	public String toString() {
		return topic
			+ ": "
			+ (properties == null ? message : properties.toString());
	}

	public Topic getTopic() {
		return topic;
	}

	public String getTopicName() {
		return topic_name;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getMessage() {
		return message;
	}
}
