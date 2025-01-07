package com.sap.security.api.ticket;

/**
 * Title:        TicketExpiredException
 * Description:  Indicates that a ticket to be verified is expired
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author       Kai Ullrich
 * @version      1.0
 */

public class TicketVerifyException extends TicketException
{
	private static final long serialVersionUID = 1821967098590205415L;
	
    public TicketVerifyException (String msg)
    {
        super (msg);
    }
}
