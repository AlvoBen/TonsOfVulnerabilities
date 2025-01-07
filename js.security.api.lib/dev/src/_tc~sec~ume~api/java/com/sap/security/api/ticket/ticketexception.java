package com.sap.security.api.ticket;

import com.sap.security.api.UMException;

public class  TicketException extends UMException {

	private static final long serialVersionUID = 836427233198155913L;
	
    public TicketException() { super(); }

    public TicketException(String message) { super(message); }

}
