package com.sap.security.api.ticket;

/**
 * Title:        WrongTicketModeException
 * Description:  Exception to indicate that a ticket is used in the wrong mode (create/verify)
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author       Kai Ullrich
 * @version      1.0
 */

public class WrongTicketModeException extends TicketException {

	private static final long serialVersionUID = 7075942836517531428L;
	
    public WrongTicketModeException (String msg)
    {
        super (msg);
    }
}
