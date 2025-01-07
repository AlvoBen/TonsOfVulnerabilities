package com.sap.pj.jmx.introspect;

import javax.management.MBeanConstructorInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

/**
 * A subclass of {@link javax.management.StandardMBean <code>StandardMBean</code>} for the use
 * inside the MBeanServer implementation.
 *
 * @author    d025700
 */
public class StandardMBeanWrapper extends StandardMBean {

  /**
   * Calls {@link javax.management.StandardMBean#StandardMBean(Object, Class)
   * <code>super(implementation, null)</code>}.
   *
   * @see javax.management.StandardMBean#StandardMBean(Object, Class)
   */
  public StandardMBeanWrapper(Object implementation)
          throws NotCompliantMBeanException {
    super(implementation, null);
  }

  /**
   * @see javax.management.StandardMBean#StandardMBean(Class)
   */
  protected StandardMBeanWrapper(Class mbeanInterface) throws NotCompliantMBeanException {
    super(mbeanInterface);
  }

  /**
   * Always returns the <var>ctors</var> object.
   * @see javax.management.StandardMBean#getConstructors(MBeanConstructorInfo[], Object)
   */
  protected MBeanConstructorInfo[] getConstructors(MBeanConstructorInfo[] ctors, Object impl) {
    return ctors;
  }
}
