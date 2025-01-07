package com.sap.engine.services.rmi_p4;

import com.sap.engine.interfaces.cross.BrokerFactory;
import com.sap.engine.interfaces.cross.ObjectBrokerInterface;

import java.util.Properties;

/**
 * Author: Asen Petrov
 * Date: 2006-2-15
 * Time: 15:23:29
 */
public class P4BrokerFactory implements BrokerFactory {

  public ObjectBrokerInterface getBroker(Properties p) {
    return (ObjectBrokerInterface)P4ObjectBroker.init(p);
  }
}
