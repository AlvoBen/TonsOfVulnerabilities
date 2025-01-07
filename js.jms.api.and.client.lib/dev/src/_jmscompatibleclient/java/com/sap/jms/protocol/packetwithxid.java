package com.sap.jms.protocol;

import javax.transaction.xa.Xid;

/**
 * @author margarit-k
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface PacketWithXID {
  
  Xid getXID() throws BufferUnderflowException;
  
}
