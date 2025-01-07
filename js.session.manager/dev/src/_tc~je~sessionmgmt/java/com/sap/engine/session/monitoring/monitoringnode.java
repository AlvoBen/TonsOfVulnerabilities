package com.sap.engine.session.monitoring;

import java.io.Serializable;
import java.util.Map;

/**
 * @author    : Pavel Bonev  
 * Date: 2006-8-11
 */
public interface MonitoringNode extends Serializable {

  public static final int DESTRUCTION_LOGOFF = 0;
  public static final int DESTRUCTION_EXPIRED = 1;
  public static final int DESTRUCTION_EMPTY = 2;

  public String getID();

  public String getTitle();
  public void setTitle(String title);

  public Object getAttribute(String name);
  public void setAttribute(String name, Object attribute);

  /**
   * This method retuns map of <id, node> pairs.
   * If map contains SessionChunk instances as values it is possible
   * some of the values to be null (these ones that do not implement MonitoringNode)
   *
   * @return map
   */
  public Map<String, MonitoringNode> getChildNodes();
  public MonitoringNode getChildNode(String id);

  public void setDestructionReason(int reason);
  public int getDestructionReason();
}
