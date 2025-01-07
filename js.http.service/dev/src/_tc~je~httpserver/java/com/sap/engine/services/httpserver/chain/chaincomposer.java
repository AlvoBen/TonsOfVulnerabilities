package com.sap.engine.services.httpserver.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible for the entire chain composition. There
 * could be many chain parts that have to extend this file and they have
 * to call <code>{@link register()}</code> or <code>{@link register(Filter)}
 * </code> methods to add them selves to the main request/response processing
 * chain.
 * 
 * <p>Every chain part has an extension point where a <code>Filter</code>
 * could be put in order this chain part to be extended with other one.
 * This could be done in two ways:
 * <ul>
 * <li><code>register()</code> method - it uses the first filter as
 * an extension filter</li>
 * <li><code>register(Filter)</code> method - it uses the passed filter
 * as an extension filter</li>
 * </ul></p>
 */
public abstract class ChainComposer {
  private static final List<ChainComposer> composers = new ArrayList<ChainComposer>();
  
  protected List<Filter> filters = new ArrayList<Filter>();
  protected int extensionIndex;
  private boolean extended;
  private int startIndex;
  
  private void extend(Filter extension) {
    if (extension == null && extended) {
      filters.remove(extensionIndex);
      extended = false;
    } else if (extension != null && !extended) {
      filters.add(extensionIndex, extension);
      extended = true;
    }
  }
  
  /**
   * Returns an iterator over this chain composer filters
   * 
   * @return
   * an <code>java.util.Iterator&lt;Filter&gt;</code> over this chain
   * composer filters
   */
  public Iterator<Filter> getFilters() {
    int count = 0;
    Iterator<Filter> iter = filters.iterator();
    while (count++ < startIndex && iter.hasNext()) { iter.next(); }
    return iter;
  }
  
  /**
   * Adds this chain part to the main chain by extending last registered
   * chain part with the first filter from this chain part
   */
  public void register() {
    startIndex = 1;
    register(filters.get(0));
  }
  
  /**
   * Adds this chain part to the main chain by extending last registered
   * chain part with the passed filter that has responsibility to pass the
   * request/response to this chain part
   */
  public void register(Filter extension) {
    if (!composers.isEmpty()) {
      composers.get(composers.size() - 1).extend(extension);
    }
    composers.add(this);
  }
  
  /**
   * Removes this chain part from the main chain, thus removing all
   * the chain parts that extend this one
   */
  public void unregister() {
    int i = composers.indexOf(this);
    if (i < 0) { return; }
    if (i > 0) { composers.get(i - 1).extend(null); }
    while(composers.size() > i) {
      composers.remove(i);
    }
  }
}
