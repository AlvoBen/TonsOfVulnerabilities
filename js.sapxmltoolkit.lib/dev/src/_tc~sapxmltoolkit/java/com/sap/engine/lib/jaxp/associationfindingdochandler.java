package com.sap.engine.lib.jaxp;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xml.parser.helpers.*;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
final class AssociationFindingDocHandler extends com.sap.engine.lib.xml.parser.handlers.EmptyDocHandler {

  private Exception exception = new Exception();
  private Source source;
  private String media;
  private String title;
  private String charset;

  protected AssociationFindingDocHandler init(String media, String title, String charset) {
    this.media = media;
    this.title = title;
    this.charset = charset;
    return this;
  }

  public void startDocument() {
    source = null;
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    if (target.equals("xml-stylesheet")) {
      char[] d = data.getData();
      int p = data.getOffset();
      int end = p + data.length();
      String href = null;

      OUTER: while (true) {
        if (p >= end) {
          break  OUTER;
        }

        // Parsing the name
        while (isWhitespace(d[p])) {
          p++;

          if (p >= end) {
            break  OUTER;
          }
        }

        int name0 = p;

        while (!isWhitespace(d[p]) && (d[p] != '=')) {
          p++;

          if (p >= end) {
            break  OUTER;
          }
        }

        int name1 = p;

        while (isWhitespace(d[p]) || (d[p] == '=')) {
          p++;

          if (p >= end) {
            break  OUTER;
          }
        }

        // Parsing the value
        char delimiter = d[p];

        if ((delimiter != '\'') && (delimiter != '\"')) {
          break  OUTER;
        }

        int value0 = p + 1;
        p++;

        if (p >= end) {
          break  OUTER;
        }

        while (d[p] != delimiter) {
          p++;

          if (p >= end) {
            break  OUTER;
          }
        }

        int value1 = p;
        p++;
        String name = new String(d, name0, name1 - name0);
        String value = new String(d, value0, value1 - value0);

        if (name.equals("media")) {
          if (!areCompatible(value, media)) {
            return;
          }
        } else if (name.equals("title")) {
          if (!areCompatible(value, title)) {
            return;
          }
        } else if (name.equals("charset")) {
          if (!areCompatible(value, title)) {
            return;
          }
        } else if (name.equals("href")) {
          href = value;
        }
      }

      if (href != null) {
        source = new StreamSource(href);
        throw exception;
      }
    }
  }

  private boolean isWhitespace(char ch) {
    return ((ch == ' ') || (ch == 9) || (ch == 10) || (ch == 13));
  }

  private boolean areCompatible(String a, String b) {
    return ((a == null) || (b == null) || (a.equals("")) || (b.equals("")) || a.equals(b));
  }

  protected Source getAssociation() {
    return source;
  }

}

