package com.sap.engine.services.deploy.server.remote;

import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.server.TransactionStatistics;
import com.sap.engine.services.deploy.server.utils.DSConstants;

/**
 * This class extends TransactionStatistics in order to add a serializable
 * response to it. It is intended to contain the response from the remote
 * server nodes.
 * 
 * @author Emil Dinchev
 *
 */
public final class MessageResponse extends TransactionStatistics {
	private static final long serialVersionUID = 1L;

	private final Object response;
	
	/**
	 * The constructor of an message response.
	 * @param senderId the ID of the server which sends this response. Must be
	 * valid server ID.
	 * @param warnings the occurred warnings during the processing of the
	 * corresponding message. Can be null.
	 * @param errors the occurred errors during the processing of the
	 * corresponding message. Can be null.
	 * @param response The serializable response. Can be null.
	 */
	public MessageResponse(final int senderId, final String[] warnings, 
		final String[] errors, Object response) {
		super(senderId);
		if(warnings != null) {
			for(final String warning : warnings) {
				addWarning(warning);
			}
		}

		if(errors != null) {
			for(final String error : errors) {
				addError(error);
			}
		}
		this.response = response;
	}

	/**
	 * The constructor used in case when an error occurs during the processing
	 * of the message.  
	 * @param senderId the ID of the server which sends this response. Must be
	 * valid server ID.
	 * @param error the occurred error during the processing of the message.
	 */
	public MessageResponse(final int senderId, final String error) {
		super(senderId);
		addError(error);
		response = null;
	}

	/**
	 * @return the response of the corresponding message. Can be null. 
	 */
	public Object getResponse() {
		return response;
	}
	
	@Override
    public String toString() {
		final StringBuilder sb = new StringBuilder(super.toString());
		sb.append(DSConstants.TAB).append("response=")
			.append(response).append(CAConstants.EOL);
		return sb.toString();
	}
}