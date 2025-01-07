package com.sap.engine.lib.xml.util;

import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xsl.xslt.QName;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
public final class DTMToDocHandler {

  private DTM dtm;
  private DocHandler h;

  public void process(DTM dtm, DocHandler h) throws Exception {
    this.dtm = dtm;
    h.startDocument();
    process(0);
    h.endDocument();
  }

  private void process(int x) throws Exception {
    int t = dtm.nodeType[x];

    switch (t) {
      case DTM.ELEMENT_NODE: {
        QName q = dtm.name[x];
        //String uri = q.uri.getString();
        h.startElementStart(q.uri, q.localname, q.rawname);
        x++;

        while ((x < dtm.size) && ((dtm.nodeType[x] == DTM.ATTRIBUTE_NODE) || (dtm.nodeType[x] == DTM.NAMESPACE_NODE))) {
          QName q2 = dtm.name[x];
          h.addAttribute(q2.uri, q2.prefix, q2.localname, q2.rawname, null, dtm.getStringValue(x));
          x++;
        }

        h.startElementEnd(false);

        for (int y = dtm.firstChild[x]; y != DTM.NONE; y = dtm.nextSibling[y]) {
          process(y);
        } 

        h.endElement(q.uri, q.localname, q.rawname, false);
        return;
      }
      case DTM.COMMENT_NODE: {
        h.onComment(dtm.getStringValue(x));
        return;
      }
      case DTM.DOCUMENT_NODE: // falls through
      case DTM.DOCUMENT_FRAGMENT_NODE: {
        process(x + 1);
        return;
      }
      case DTM.PROCESSING_INSTRUCTION_NODE: {
        h.onPI(dtm.name[x].rawname, dtm.getStringValue(x));
        return;
      }
      case DTM.TEXT_NODE: {
        h.charData(dtm.getStringValue(x), false);
        return;
      }
      default: {
        throw new RuntimeException("");
      }
    }
  }

}

