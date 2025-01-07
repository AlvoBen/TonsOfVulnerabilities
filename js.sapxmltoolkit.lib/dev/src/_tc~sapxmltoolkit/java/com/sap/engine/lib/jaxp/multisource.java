package com.sap.engine.lib.jaxp;

import javax.xml.transform.Source;
import java.util.Vector;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 24-Apr-02, 15:03:47
 */
public final class MultiSource implements Source {

  private Vector sources = new Vector();
  private String systemId; // might be used as a base systemId for all of the sources

  public MultiSource() {

  }

  public void addSource(Source source) {
    if (source != null) {
      sources.add(source);
    }
  }

  public void clear() {
    sources.clear();
  }

  /**
   * Returns a copy.
   */
  public Source[] getSources() {
    Source[] r = new Source[sources.size()];
    sources.toArray(r);
    return r;
  }

  public void setSystemId(String s) {
    this.systemId = s;
  }

  public String getSystemId() {
    return systemId;
  }

}

