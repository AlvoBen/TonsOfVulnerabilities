package com.sap.engine.services.httpserver.chain;


public interface HostChain extends ServerChain {
  /**
   * Gives access to this chain <code>{@link HostScope}</code>
   * 
   * @return
   * the <code>HostScope</code> of this chain
   */
  public HostScope getHostScope();
}
