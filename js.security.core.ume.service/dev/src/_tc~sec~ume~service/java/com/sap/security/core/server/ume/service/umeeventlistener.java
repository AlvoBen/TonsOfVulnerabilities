package com.sap.security.core.server.ume.service;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.container.event.ContainerEventListener;

public class UMEEventListener implements ContainerEventListener{

	private Set mListeners;
	private int mMask;
	private Set mInterfaces;
	private UMEServiceFrame mServiceFrame;
	
	private UMEEventListener()
	{
	}
	
	public UMEEventListener(UMEServiceFrame umeService)
	{
		mServiceFrame = umeService;
		mListeners = new HashSet();
		mMask = 0;
		mInterfaces = new HashSet();
	}
	
	public synchronized void addEventListener(int mask, Set interfaces, ContainerEventListener listener)
	{
		boolean wasEmpty = mListeners.isEmpty();
		mMask = mMask | mask;
		mInterfaces.addAll(interfaces);
		mListeners.add(listener);
		UMEServiceFrame.myLoc.infoT("addEventListener", listener.toString()+" added to UMEEventListener");
		
		if (!wasEmpty)
		{
			mServiceFrame.getServiceContext().getServiceState().unregisterContainerEventListener();
			UMEServiceFrame.myLoc.infoT("addEventListener", "Registered UMEEventListener removed.");
		}
		mServiceFrame.getServiceContext().getServiceState().registerContainerEventListener(mMask,mInterfaces,this);
	}

	public synchronized void removeEventListener(ContainerEventListener listener)
	{
		mListeners.remove(listener);
		UMEServiceFrame.myLoc.infoT("removeEventListener", listener.toString()+" removed from UMEEventListener");
		if (mListeners.isEmpty())
		{
			mServiceFrame.getServiceContext().getServiceState().unregisterContainerEventListener();
			UMEServiceFrame.myLoc.infoT("removeEventListener", "Registered UMEEventListener removed.");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#beginContainerStop()
	 */
	public void beginContainerStop() {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.beginContainerStop();
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#beginServiceStop(java.lang.String)
	 */
	public void beginServiceStop(String arg0) {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.beginServiceStop(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#containerStarted()
	 */
	public void containerStarted() {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.containerStarted();
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#interfaceAvailable(java.lang.String, java.lang.Object)
	 */
	public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.interfaceAvailable(interfaceName, interfaceImpl);
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#interfaceNotAvailable(java.lang.String)
	 */
	public void interfaceNotAvailable(String interfaceName) {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.interfaceNotAvailable(interfaceName);
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceNotStarted(java.lang.String)
	 */
	public void serviceNotStarted(String arg0) {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.serviceNotStarted(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceStarted(java.lang.String, java.lang.Object)
	 */
	public void serviceStarted(String arg0, Object arg1) {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.serviceStarted(arg0, arg1);
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#serviceStopped(java.lang.String)
	 */
	public void serviceStopped(String arg0) {
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			listener.serviceStopped(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperties(java.util.Properties)
	 */
	public boolean setServiceProperties(Properties arg0) {
		boolean result = true;
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			result = listener.setServiceProperties(arg0) && result;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperty(java.lang.String, java.lang.String)
	 */
	public boolean setServiceProperty(String arg0, String arg1) {
		boolean result = true;
		for (java.util.Iterator iter=mListeners.iterator(); iter.hasNext();)
		{
			ContainerEventListener listener = (ContainerEventListener)iter.next();
			result = listener.setServiceProperty(arg0, arg1) && result;
		}
		return result;
	}	
}
