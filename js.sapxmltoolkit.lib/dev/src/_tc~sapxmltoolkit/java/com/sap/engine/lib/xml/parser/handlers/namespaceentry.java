package com.sap.engine.lib.xml.parser.handlers;

/**
 * Class description, -
 *
 * @author Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version 1.00
 */
import com.sap.engine.lib.xml.parser.helpers.CharArray;

public class NamespaceEntry {

  protected CharArray prefix = null;
  protected CharArray uri = null;
  protected int level = 0;
  protected NamespaceEntry neout = null;

  /**
   *
   */
  public void print() {
    if (neout != null) {
      neout.print();
    }
  }

  /**
   * Called when the xml scanner goes into a deeper level of the xml structure
   *
   */
  public void levelUp() {
    level++;

    if (neout != null) {
      neout.levelUp();
    }
  }

  /**
   * Called when the xml scanner goes a level higher into the xml structure,
   * this method checks, whether this namespace entriy has gone out of scope,
   * and if so, if there was a preview namespace with the same prefix, but
   * different uri, sets it, ot else sets that the the entry is no more valid,
   * and should be removed from the namspace handler
   *
   */
  public boolean levelDown() {
    level--;

    if (neout != null) {
      neout.levelDown();
    }

    boolean changedMapping = !isValid();

    if ((!isValid()) && (neout != null)) {
      prefix = neout.getPrefix();
      uri = neout.getUri();
      level = neout.getLevel();
      neout = neout.neout;
    }

    return changedMapping;
  }

  /**
   * Returns whether, the namespace is valid or not, a namespace is valid, if it
   * is in its scope (current element and descendats)
   *
   * @return
   */
  public boolean isValid() {
    return (level == 0) ? false : true;
  }

  /**
   * Constructs a namespaceentry with given prefix and uri
   *
   * @param   prefix
   * @param   uri
   */
  public NamespaceEntry(CharArray prefix, CharArray uri) {
    this.prefix = prefix;
    this.uri = uri;
  }

  /**
   * Returns the prefix of this entry
   *
   * @return
   */
  public CharArray getPrefix() {
    return prefix;
  }

  /**
   * returns the uri of this entry
   *
   * @return
   */
  public CharArray getUri() {
    return uri;
  }

  /**
   * returns the level of this entry
   *
   * @return
   */
  public int getLevel() {
    return level;
  }

  /**
   * Sets the namespace which, this namespace entry hides (e.g. both
   * namspaces had same prefixes, when the new namspace gets ivalid, then
   * the namspace handler has to set the previous, as current
   *
   * @param   neout
   */
  public void setPrevNamespace(NamespaceEntry neout) {
    this.neout = neout;
  }

  public String toString() {
    return "NamespaceEntry: prefix = " + prefix + " uri = \"" + uri + "\" level = " + level;
  }

}

