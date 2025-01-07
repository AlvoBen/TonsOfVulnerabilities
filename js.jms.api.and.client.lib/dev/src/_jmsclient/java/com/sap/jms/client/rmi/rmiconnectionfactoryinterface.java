package com.sap.jms.client.rmi;

import java.rmi.Remote;
import com.sap.jms.client.connection.ConnectionFactoryInterface;
import com.sap.engine.interfaces.cross.RedirectableExt;

/**
 * @author Desislav Bantchovski
 * @version 7.30 
 */

public interface RMIConnectionFactoryInterface extends ConnectionFactoryInterface, Remote, RedirectableExt {
	
	public static final int REDIRECTABLE_KEY_TIMESTAMP_OFFSET =  0;
    public static final int REDIRECTABLE_KEY_TIMESTAMP_LENGTH = 16;
    public static final String CrossObjectFactoryName = "JMSRemoteConnectionFactoryCrossObjectFactoryName";    

}
