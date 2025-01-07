package com.sap.engine.session.usr;

public abstract class JCoSessionBridge {

  protected abstract void passivateConnections();

  protected abstract void restoreConnections();

  protected abstract void releaseConnections();

  protected abstract void clear();
}
