package com.sap.engine.services.dc.manage.messaging.impl;

import com.sap.engine.services.dc.manage.messaging.Message;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class MessageImpl implements Message {

	private final int clusterId;
	private final int groupId;
	private final byte nodeType;
	private final int messageType;
	private final byte[] body;
	private final int offset;
	private final int length;

	private final String toString;

	MessageImpl(int clusterId, int messageType, byte[] body, int offset,
			int length) {
		this.clusterId = clusterId;
		this.messageType = messageType;
		this.body = body;
		this.offset = offset;
		this.length = length;

		this.groupId = -1;
		this.nodeType = -1;

		this.toString = "cluster id: " + this.clusterId + ", message type: "
				+ this.messageType + ", body: "
				+ (this.body == null ? "null" : new String(body))
				+ ", offset: " + this.offset + ", length: " + this.length
				+ ", group id (default value): " + this.groupId
				+ ", node type (default value): " + this.nodeType;
	}

	MessageImpl(int groupId, byte nodeType, int messageType, byte[] body,
			int offset, int length) {
		this.groupId = groupId;
		this.nodeType = nodeType;
		this.messageType = messageType;
		this.body = body;
		this.offset = offset;
		this.length = length;

		this.clusterId = -1;

		this.toString = "group id: " + this.groupId + ", node type"
				+ this.nodeType + ", message type: " + this.messageType
				+ ", body: " + (this.body == null ? "null" : new String(body))
				+ ", offset: " + this.offset + ", length: " + this.length
				+ ", cluster id (default value): " + this.clusterId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.manage.messaging.Message#getClusterId()
	 */
	public int getClusterId() {
		return this.clusterId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.manage.messaging.Message#getGroupId()
	 */
	public int getGroupId() {
		return this.groupId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.manage.messaging.Message#getNodeType()
	 */
	public byte getNodeType() {
		return this.nodeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.manage.messaging.Message#getMessageType()
	 */
	public int getMessageType() {
		return this.messageType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.manage.messaging.Message#getBody()
	 */
	public byte[] getBody() {
		return this.body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.manage.messaging.Message#getOffset()
	 */
	public int getOffset() {
		return this.offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.manage.messaging.Message#getLength()
	 */
	public int getLength() {
		return this.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.toString;
	}

}
