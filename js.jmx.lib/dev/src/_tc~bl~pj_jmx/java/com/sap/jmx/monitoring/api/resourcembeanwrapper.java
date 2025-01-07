/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx.monitoring.api;

import javax.management.AttributeChangeNotification;
import javax.management.AttributeNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.StandardMBean;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LoggingUtilities;
import com.sap.tc.logging.Severity;

/**
 * Wrapper for application monitoring resource MBeans. The wrapper is capable to handle MBeans of the
 * 6.20 application monitoring as well as arbitrary resource MBeans. It has to be registered with the
 * MBeanServer instead of the resource MBean. 
 * The advantages of using the ResourceMBeanWrapper are:
 * <ul>
 * <li>
 *   it allows MBean interface and implementation to reside in different packages
 * </li>
 * <li>
 *   it unifies the 6.20 and 6.30 notification model, i.e. re-writes a ReportNotification to become
 *   an AttributeChangeNotification
 * </li>
 * 
 * @author d025700
 */
public class ResourceMBeanWrapper
  extends StandardMBean
  implements NotificationEmitter, NotificationListener, MBeanRegistration {

  private static final Location LOCATION = Location.getLocation(ResourceMBeanWrapper.class);
  private static final String HANDLE_NOTIFICATION_METHOD =
    "handleNotification(javax.management.Notification,java.lang.Object)"; //$NON-NLS-1$
  private static final String UNABLE_TO_GET_ATTRIBUTE_VALUE =
    "unable to send AttributeChangeNotification, value cannot be retrieved from resource";

  private static final String VERSION_TYPE = VersionReportNotification.class.getName();
  private static final String CONFIGURATION_TYPE = ConfigurationReportNotification.class.getName();
  private static final String AVAILABILITY_TYPE = AvailabilityReportNotification.class.getName();
  private static final String FREQUENCY_TYPE = FrequencyReportNotification.class.getName();
  private static final String QUALITYRATE_TYPE = QualityRateReportNotification.class.getName();
  private static final String SIMPLEVALUE_TYPE = SimpleValueReportNotification.class.getName();
  private static final String STATE_TYPE = StateReportNotification.class.getName();
  private static final String TEXT_TYPE = TextReportNotification.class.getName();

  private static final String ATTR_AVAILABLE = "Available"; //$NON-NLS-1$
  private static final String ATTR_CONFIGURATION_PARAMETERS = "ConfigurationParameters"; //$NON-NLS-1$
  private static final String ATTR_NUMBER_OF_EVENTS = "NumberOfEvents"; //$NON-NLS-1$
  private static final String ATTR_HITS_N_TRIES = "HitsNTries"; //$NON-NLS-1$
  private static final String ATTR_VALUE = "Value"; //$NON-NLS-1$
  private static final String ATTR_STATE = "State"; //$NON-NLS-1$
  private static final String ATTR_TEXT = "Text"; //$NON-NLS-1$
  private static final String ATTR_VERSION = "Version"; //$NON-NLS-1$

  private static final String BOOLEAN = Boolean.TYPE.getName();
  private static final String CONFIGURATION_LIST = ConfigurationList.class.getName();
  private static final String INT = Integer.TYPE.getName();
  private static final String QUALITY_RATE_VALUE = QualityRateValue.class.getName();
  private static final String STATE_VALUE = StateValue.class.getName();
  private static final String STRING = String.class.getName();
  private static final String VERSION_INFO = VersionInfo.class.getName();

  private ObjectName myName;
  private long notificationNumber = 0;
  private final NotificationBroadcasterSupport broadcaster = new NotificationBroadcasterSupport();

  /**
   * Creates a wrapper for an arbitrary MBean.
   * @param mbean resource MBean to be wrapped
   * @param mbeanInterface Class of the MBean interface
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(Object mbean, Class mbeanInterface)
    throws NotCompliantMBeanException {
    super(mbean, mbeanInterface);
    if (mbean instanceof NotificationBroadcaster) {
      addListener(null);
    }
  }

  /**
   * Creates a wrapper for a StateResourceMBean.
   * @param mbean resource MBean to be wrapped
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(StateResourceMBean mbean) throws NotCompliantMBeanException {
    super((StateResourceMBean)checkMBean(mbean), StateResourceMBean.class);
    addListener(STATE_TYPE);
  }

  /**
   * Creates a wrapper for a TextResourceMBean.
   * @param mbean resource MBean to be wrapped
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(TextResourceMBean mbean) throws NotCompliantMBeanException {
    super((TextResourceMBean)checkMBean(mbean), TextResourceMBean.class);
    addListener(TEXT_TYPE);
  }

  /**
   * Creates a wrapper for a ConfigurationResourceMBean.
   * @param mbean resource MBean to be wrapped
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(ConfigurationResourceMBean mbean) throws NotCompliantMBeanException {
    super((ConfigurationResourceMBean)checkMBean(mbean), ConfigurationResourceMBean.class);
    addListener(CONFIGURATION_TYPE);
  }

  /**
   * Creates a wrapper for a VersionResourceMBean.
   * @param mbean resource MBean to be wrapped
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(VersionResourceMBean mbean) throws NotCompliantMBeanException {
    super((VersionResourceMBean)checkMBean(mbean), VersionResourceMBean.class);
    addListener(VERSION_TYPE);
  }

  /**
   * Creates a wrapper for a FrequencyResourceMBean.
   * @param mbean resource MBean to be wrapped
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(FrequencyResourceMBean mbean) throws NotCompliantMBeanException {
    super((FrequencyResourceMBean)checkMBean(mbean), FrequencyResourceMBean.class);
    addListener(FREQUENCY_TYPE);
  }

  /**
   * Creates a wrapper for a QualityRateResourceMBean.
   * @param mbean resource MBean to be wrapped
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(QualityRateResourceMBean mbean) throws NotCompliantMBeanException {
    super((QualityRateResourceMBean)checkMBean(mbean), QualityRateResourceMBean.class);
    addListener(QUALITYRATE_TYPE);
  }

  /**
   * Creates a wrapper for a SimpleValueResourceMBean.
   * @param mbean resource MBean to be wrapped
   * @throws javax.management.NotCompliantMBeanException
   */
  public ResourceMBeanWrapper(SimpleValueResourceMBean mbean) throws NotCompliantMBeanException {
    super((SimpleValueResourceMBean)checkMBean(mbean), SimpleValueResourceMBean.class);
    addListener(SIMPLEVALUE_TYPE);
  }

	/**
	 * Creates a wrapper for an AvailabilityResourceMBean.
	 * @param mbean resource MBean to be wrapped
	 * @throws javax.management.NotCompliantMBeanException
	 */
	public ResourceMBeanWrapper(AvailabilityResourceMBean mbean) throws NotCompliantMBeanException {
		super ((AvailabilityResourceMBean)checkMBean(mbean), AvailabilityResourceMBean.class);
		addListener(AVAILABILITY_TYPE);
	}

  /**
   * @see javax.management.NotificationEmitter#removeNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
   */
  public void removeNotificationListener(
    NotificationListener listener,
    NotificationFilter filter,
    Object handback)
    throws ListenerNotFoundException {
    broadcaster.removeNotificationListener(listener, filter, handback);
  }

  /**
   * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
   */
  public void addNotificationListener(
    NotificationListener listener,
    NotificationFilter filter,
    Object handback)
    throws IllegalArgumentException {
    broadcaster.addNotificationListener(listener, filter, handback);
  }

  /**
   * @see javax.management.NotificationBroadcaster#getNotificationInfo()
   */
  public MBeanNotificationInfo[] getNotificationInfo() {
    Object impl = getImplementation();
    if (impl instanceof NotificationBroadcaster) {
      return ((NotificationEmitter) getImplementation()).getNotificationInfo();
    }
    else {
      return new MBeanNotificationInfo[0];
    }
  }

  /**
   * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
   */
  public void removeNotificationListener(NotificationListener listener)
    throws ListenerNotFoundException {
    broadcaster.removeNotificationListener(listener);
  }

  /**
   * Not supported by this class.
   * @see javax.management.StandardMBean#setImplementation(java.lang.Object)
   */
  public void setImplementation(Object implementation) throws NotCompliantMBeanException {
    throw new UnsupportedOperationException();
  }

  /**
   * Checks whether the object is a NotificationEmitter
   * @param mbean the MBean to check
   * @return the MBean passed as parameter
   * @throws NotCompliantMBeanException in case the given MBean does not implement the NotificationEmitter interface
   */
  private static NotificationEmitter checkMBean(Object mbean) throws NotCompliantMBeanException {
    if (mbean instanceof NotificationEmitter) {
      return (NotificationEmitter) mbean;
    }
    else {
      throw new NotCompliantMBeanException(NotificationEmitter.class.getName() + " expected");
    }
  }

  /**
   * Adds the wrapper as a listener to the implementation.
   * @param handback
   */
  private synchronized void addListener(Object handback) {
    ((NotificationBroadcaster) getImplementation()).addNotificationListener(this, null, handback);
  }

  /**
   * Completes/creates AttributeChangeNotification before dispatching the incoming
   * notification to the listener.
   * @see javax.management.NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
   */
  public void handleNotification(Notification notification, Object handback) {
    // do completion for com.sap.jmx.monitoring.api.AttributeChangeNotification
    if (notification instanceof AttributeChangeNotification) {
      Object oldValue = ((AttributeChangeNotification) notification).getOldValue();
      Object newValue = ((AttributeChangeNotification) notification).getNewValue();
      if (oldValue != null && oldValue.equals(newValue) || oldValue == null && newValue == null) {
        // value has not been changed
        return;
      }
      notification.setSequenceNumber(getNotificationNumber());
      notification.setTimeStamp(System.currentTimeMillis());
    }
    else if (notification instanceof ReportNotification) {
    	// rewrite 6.20 notifications
      try {
        if (notification instanceof AvailabilityReportNotification) {
          notification = createAttributeChangeNotification(notification, ATTR_AVAILABLE, BOOLEAN);
        }
				else if (notification instanceof ConfigurationReportNotification) {
					notification = createAttributeChangeNotification(notification, ATTR_CONFIGURATION_PARAMETERS, CONFIGURATION_LIST);
				}
				else if (notification instanceof FrequencyReportNotification) {
					notification = createAttributeChangeNotification(notification, ATTR_NUMBER_OF_EVENTS, INT);
				}
				else if (notification instanceof QualityRateReportNotification) {
					notification = createAttributeChangeNotification(notification, ATTR_HITS_N_TRIES, QUALITY_RATE_VALUE);
				}
				else if (notification instanceof SimpleValueReportNotification) {
					notification = createAttributeChangeNotification(notification, ATTR_VALUE, INT);
				}
				else if (notification instanceof StateReportNotification) {
					notification = createAttributeChangeNotification(notification, ATTR_STATE, STATE_VALUE);
				}
				else if (notification instanceof TextReportNotification) {
					notification = createAttributeChangeNotification(notification, ATTR_TEXT, STRING);
				}
				else if (notification instanceof VersionReportNotification) {
					notification = createAttributeChangeNotification(notification, ATTR_VERSION, VERSION_INFO);
				}
      }
      catch (Exception e) {
    	  LoggingUtilities.logAndTrace(Severity.ERROR, Category.SYS_SERVER, LOCATION, e, 
    			  "ASJ.jmx_lib.000000", null, null, UNABLE_TO_GET_ATTRIBUTE_VALUE);
        return;
      }
    }
    if (notification != null
      && !(notification.getSource() instanceof ObjectName)
      && myName != null) {
      notification.setSource(myName);
    }
    broadcaster.sendNotification(notification);
  }

  /**
   * Helper to create an AttributeChangeNotification based on a ReportNotification.
   * @param notification
   * @param attrName
   * @param attrType
   * @return the AttributeChangeNotification
   * @throws AttributeNotFoundException
   * @throws MBeanException
   * @throws ReflectionException
   */
  private Notification createAttributeChangeNotification(
    final Notification notification,
    String attrName,
    String attrType)
    throws AttributeNotFoundException, MBeanException, ReflectionException {
    AttributeChangeNotification acn =
      new javax.management.AttributeChangeNotification(
        notification.getSource(),
        getNotificationNumber(),
        System.currentTimeMillis(),
        "Value of attribute " + attrName + " changed.",
        attrName,
        attrType,
        null,
        getAttribute(attrName));
    return acn;
  }

  /**
   * Creates the sequence number for notifications.
   * @return the sequence number
   */
  private synchronized long getNotificationNumber() {
    return notificationNumber++;
  }

  /**
   * @see javax.management.MBeanRegistration#postDeregister()
   */
  public void postDeregister() {
    // do nothing
  }

  /**
   * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
   */
  public void postRegister(Boolean arg0) {
    // do nothing
  }

  /**
   * @see javax.management.MBeanRegistration#preDeregister()
   */
  public void preDeregister() throws Exception {
    // do nothing
  }

  /**
   * Get own name.
   * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer, javax.management.ObjectName)
   */
  public ObjectName preRegister(MBeanServer mbs, final ObjectName name) throws Exception {
    myName = name;
    return name;
  }

}
