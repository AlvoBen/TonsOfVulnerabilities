/*
 * Created on 29.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.sap.ip.j2eeengine.consistency;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.timeout.TimeoutListener;
import com.sap.engine.services.timeout.TimeoutManager;
import com.sap.security.core.util.IClusterMessageListener;
import com.sap.security.core.util.IPlatformTools;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author d021770
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MessageBroker {

	private static Location myLoc = Location.getLocation(MessageBroker.class);
	private static Category myCat = Category.getCategory(LoggingHelper.SYS_SECURITY, "Usermanagement");


	private class Listener implements IClusterMessageListener {

		/**
		 * Receives a message which was sent by another cluster node.
		 * @param message The serializable object that represents the message.
		 */
		public void receiveMessage(Serializable message)
		{
			if (message instanceof CacheMessageContainer)
			{
				CacheMessageContainer cont = (CacheMessageContainer)message;
				Vector messages = cont.getContent();
				int size = messages.size();
				for (int i=0; i<size; i++)
				{
					MessageBroker.this.receive((Message)messages.elementAt(i));
				}
			}
		}
	}

	private TimeoutListenerImpl mTimeoutListener;
	private TimeoutManager	    mTimeoutManager;
	private long				mTimeout;
	private IPlatformTools      mPlatformTools;
	static volatile MessageBroker service;

	
	public MessageBroker(long timeout) {
		super();
		mTimeout = timeout;
	}
	

	public void start(IPlatformTools platformTools) throws ServiceException {
		mPlatformTools = platformTools;
		startup();
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.frame.ServiceFrame#stop()
	 */
	public void stop() throws ServiceRuntimeException {
		if (mTimeoutManager != null)
		{
			mTimeoutManager.unregisterTimeoutListener(mTimeoutListener);
		}
		mTimeoutListener = null;
		mTimeoutManager  = null;
	}

	private void startup() {
		String mn = "startup()";
		synchronized (MessageBroker.class) {
			this.service = this;
		}
		mPlatformTools.registerClusterMessageListener(new Listener());
		mTimeoutListener = new TimeoutListenerImpl();
		if (mTimeout > 0)
		{
			try
			{
				Properties props = new Properties();
				//with domain=true we receive root context, to receive offset context this property must not exist!
				props.setProperty("domain", "true");
				Context ctx = new InitialContext(props);
				mTimeoutManager = (TimeoutManager) ctx.lookup("timeout");
				mTimeoutManager.registerTimeoutListener(mTimeoutListener, mTimeout, mTimeout, -1, true);
			}
			catch (NamingException e)
			{
				LoggingHelper.traceThrowable(Severity.ERROR, myLoc, mn, e);
			}
		}
	}

	private void receive(Message message) {
		Topic t = queryTopic(message.getTopicName());
		if (t != null)
			t.receive(message);
	}
	
	private class TimeoutListenerImpl implements TimeoutListener 
	{
	   Vector mMessages;

	   public TimeoutListenerImpl()
	   {
			mMessages = new Vector();
	   }

	   public boolean check() 
	   {
		 return true;
	   }

	   public void addMessage(Message m)
	   {
		   synchronized (this)
		   {
			   mMessages.add(m);
		   }
	   }

	   public void timeout() 
	   {
	   		String mn = "timeout()";
			byte[] data = null;
			synchronized (this)
			{
				if (!mMessages.isEmpty())
				{
					mPlatformTools.sendClusterMessage(new CacheMessageContainer(mMessages));
					mMessages.clear();
				}
			}
	   }
	 }

	void send(Message message) {
		if (mTimeoutListener != null)
		{
			mTimeoutListener.addMessage(message);
			if (mTimeout <= 0)
			{
				mTimeoutListener.timeout();
			}
		}
	}

	void sendAndWait(Message message) {
		String mn = "sendAndWait(Message message)";
		Vector messages = new Vector();
		messages.add(message);
		mPlatformTools.sendClusterMessage(new CacheMessageContainer(messages));
	}

	void send(String topic, String message) {
		send(new Message(topic, message));
	}

	void send(String topic, Properties message) {
		send(new Message(topic, message));
	}

	void sendAndWait(String topic, String message) {
		sendAndWait(new Message(topic, message));
	}

	void sendAndWait(String topic, Properties message) {
		sendAndWait(new Message(topic, message));
	}

	private HashMap topics = new HashMap();

	synchronized Topic queryTopic(String name) {
		Topic t = (Topic) topics.get(name);
		return t;
	}

	synchronized Topic getTopic(String name) {
		Topic t = (Topic) topics.get(name);
		if (t == null)
			topics.put(name, t = new Topic(name));
		return t;
	}
}
