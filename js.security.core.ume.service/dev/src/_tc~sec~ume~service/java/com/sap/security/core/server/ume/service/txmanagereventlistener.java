package com.sap.security.core.server.ume.service;

import java.util.Properties;

import com.sap.engine.frame.container.event.ContainerEventListener;

public class TxManagerEventListener implements ContainerEventListener
{
    public final static String TX_MANAGER_INTERFACE = "transactionext";

    private UMEServiceFrame mServiceFrame;
	private TxManagerEventListener()
	{
	}
	
	public TxManagerEventListener(UMEServiceFrame serviceFrame)
	{
		mServiceFrame = serviceFrame;
	}
		/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#beginContainerStop()
	 */
	public void beginContainerStop() {
		//not used
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#beginServiceStop(java.lang.String)
	 */
	public void beginServiceStop(String arg0) {
		//not used
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#containerStarted()
	 */
	public void containerStarted() {
		//not used
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#interfaceAvailable(java.lang.String, java.lang.Object)
	 */
	public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
		if (interfaceName.equals(TX_MANAGER_INTERFACE)) 
		{
			mServiceFrame.setTxManagerAvailable(true);
			mServiceFrame.myLoc.infoT("interfaceAvailable", "Start using TxManager for transaction handling.");
			//keep in mind this that the type of interfaceImpl is TransactionManagerExtension
		} 
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#interfaceNotAvailable(java.lang.String)
	 */
	public void interfaceNotAvailable(String interfaceName) {
		if (interfaceName.equals(TX_MANAGER_INTERFACE)) 
		{
			mServiceFrame.setTxManagerAvailable(false);
			mServiceFrame.myLoc.infoT("interfaceNotAvailable", "Stop using TxManager for transaction handling.");
			//keep in mind this that the type of interfaceImpl is TransactionManagerExtension
		} 
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#markForShutdown(long)
	 */
	public void markForShutdown(long arg0) {
		//not used
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceNotStarted(java.lang.String)
	 */
	public void serviceNotStarted(String arg0) {
		//not used
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceStarted(java.lang.String, java.lang.Object)
	 */
	public void serviceStarted(String arg0, Object arg1) {
		//not used
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceStopped(java.lang.String)
	 */
	public void serviceStopped(String arg0) {
		//not used
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperties(java.util.Properties)
	 */
	public boolean setServiceProperties(Properties arg0) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperty(java.lang.String, java.lang.String)
	 */
	public boolean setServiceProperty(String arg0, String arg1) {
		return false;
	}
}
