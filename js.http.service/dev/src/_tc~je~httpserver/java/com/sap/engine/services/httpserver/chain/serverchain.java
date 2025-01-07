package com.sap.engine.services.httpserver.chain;


/**
 * Gives access to the <code>ServerScope</code>
 *
 */
public interface ServerChain extends Chain {
  /**
   * Gives access to surrounding <code>ServerScope</code>
   * 
   * @return
   * the <code>ServerScope</code> of this chain
   */
  public ServerScope getServerScope();
}
