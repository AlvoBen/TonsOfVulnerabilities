package com.sap.engine.core.session;

import java.util.Properties;

public class DummyManager extends Manager{

  @Override
  protected void clearShmMonitoring() {  
  }
  
  @Override
  public void initProperties(Properties properties) {
    super.initProperties(properties);
  }
}
