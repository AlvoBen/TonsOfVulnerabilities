package com.sap.pj.jmx.introspect;

import com.sap.pj.jmx.mbeaninfo.AdditionalInfoProviderMBean;

import javax.management.DynamicMBean;
import javax.management.JMRuntimeException;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.StandardMBean;

/**
 * @author Gregor Frey
 * @version 1.0
 */
public class MBeanData {
  /**
   * The originally registered object.
   */
  public final Object registeredObject;

  /**
   * Either the originally registered object or a wrapper for the standard MBean.
   */
  public final DynamicMBean dynamicMBean;

  /**
   * The normalized ObjectName of the MBean
   */
  public final ObjectName objectName;

  /**
   * True if the MBean implements the MBeanRegistration Interface
   */
  public final boolean registration;

  /**
   * 
   */
  public MBeanData(ObjectName objectName, Object object, DynamicMBean dynamicMBean, boolean registration) {
    this.objectName = objectName;
    this.registeredObject = object;
    this.dynamicMBean = dynamicMBean;
    this.registration = registration;
  }

  /**
   * Returns the ClassLoader of the object registered as MBean.
   */
  public ClassLoader getClassLoader() {
    if (registeredObject instanceof StandardMBean) {
      return ((StandardMBean) registeredObject).getImplementation().getClass().getClassLoader();
    } else if (registeredObject instanceof AdditionalInfoProviderMBean) {
      return ((AdditionalInfoProviderMBean) registeredObject).getImplementation().getClass().getClassLoader();
    } else {
      return registeredObject.getClass().getClassLoader();
    }
  }

  /**
   * @param mbean
   * @return
   */
  public static String getClassNameFromMBeanInfo(DynamicMBean mbean) throws NotCompliantMBeanException {
    String message;
    try {
      MBeanInfo info = mbean.getMBeanInfo();
      if (info != null) {
        String className = info.getClassName();
        if (className != null) {
          return className;
        }
        else {
          message = "Invocation of getMBeanInfo().getClassName() returned null, MBean: " + mbean.getClass().getName();
        }
      }
      else {
        message = "Invocation of getMBeanInfo() returned null, MBean: " + mbean.getClass().getName();
      }
    } catch (RuntimeException e) {
       throw new NotCompliantMBeanException("Runtime exception during invocation of getMBeanInfo().getClassName(), MBean " + mbean.getClass().getName() + ", Exception " + e.getClass().getName() + " " + e.getMessage());
    }
    throw new NotCompliantMBeanException(message);
  }

  /**
   * Returns the class name from the MBeanInfo.
   */
  public String getClassNameFromMBeanInfo() {
    try {
      return getClassNameFromMBeanInfo(dynamicMBean);
    }
    catch (NotCompliantMBeanException e) {
      throw new RuntimeMBeanException(new JMRuntimeException(e.getMessage()));
    }
  }

  /**
   * Creates the ObjectInstance for the MBean.
   */
  public ObjectInstance getObjectInstance() {
    // this cannot be cached since class name may change
    return new ObjectInstance(objectName, getClassNameFromMBeanInfo());
  }
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null) return false;
    if (!(object instanceof MBeanData)) return false;
    MBeanData md = (MBeanData) object;
    return objectName.equals(md.objectName);
  }


  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return objectName.hashCode();
  }


}
