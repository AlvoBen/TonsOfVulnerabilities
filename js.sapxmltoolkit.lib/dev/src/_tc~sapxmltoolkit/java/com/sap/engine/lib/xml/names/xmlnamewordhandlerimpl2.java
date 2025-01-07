package com.sap.engine.lib.xml.names;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      October 2001
 */
public final class XMLNameWordHandlerImpl2 implements XMLNameWordHandler {

  public static final int MODE_CLASS = 1;
  public static final int MODE_METHOD = 2;
  public static final int MODE_CONSTANT = 3;
  public static final int MODE_XML_STYLE = 4;
  private StringBuffer buffer = new StringBuffer();
  private int mode = MODE_CLASS;
  private boolean isFirst;

  public void setMode(int mode) {
    this.mode = mode;
  }

  public void start() {
    buffer.setLength(0);
    isFirst = true;
  }

  public void word(char[] a, int start, int end) {
    switch (mode) {
      case MODE_METHOD: {
        if (isFirst) {
          int l = buffer.length();
          buffer.append(a, start, end - start);

          if ((end - start == 1) || (!Character.isUpperCase(a[start + 1]))) {
            buffer.setCharAt(l, Character.toLowerCase(a[start]));
          }

          break;
        }


      }
      // falls through
      case MODE_CLASS: {
        buffer.append(a, start, end - start);
        break;
      }
      case MODE_CONSTANT: {
        if (!isFirst) {
          buffer.append('_');
        }

        for (int i = start; i < end; i++) {
          buffer.append(Character.toUpperCase(a[i]));
        } 

        break;
      }
      case MODE_XML_STYLE: {
        if (!isFirst) {
          buffer.append('-');
        }

        buffer.append(a, start, end - start);
        break;
      }
      default: {
        break;
      }
    }

    isFirst = false;
  }

  public void end() {

  }

  public String toString() {
    return buffer.toString();
  }

  public void loadInto(com.sap.engine.lib.xml.parser.helpers.CharArray ca) {
    ca.set(buffer);
  }

}

