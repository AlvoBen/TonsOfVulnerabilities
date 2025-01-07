package com.sap.engine.lib.xml.util;

import java.util.Hashtable;
import java.net.URLEncoder;
import java.net.URLDecoder;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 07-May-02, 10:44:40
 */
public final class StringUtils {

  public static final String[] CH_TO_ENT = new String[256];
  public static final Hashtable ENT_TO_CH = new Hashtable();

  static {
    CH_TO_ENT['&'] = "amp";
    CH_TO_ENT['\''] = "apos";
    CH_TO_ENT['"'] = "quot";
    CH_TO_ENT['<'] = "lt";
    CH_TO_ENT['>'] = "gt";
    char[] temp = new char[1];

    for (int i = 0; i < CH_TO_ENT.length; i++) {
      if (CH_TO_ENT[i] != null) {
        temp[0] = (char) i;
        ENT_TO_CH.put(CH_TO_ENT[i], new String(temp));
      }
    } 
  }

  private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', };

  private StringUtils() {

  }

  /**
   * Splits the parameter into whitespace-separated tokens
   * and returns the tokens.
   * The returned array has length equal to the number of tokens.
   */
  public static String[] split(String s0) {
    char[] s = s0.toCharArray();
    int ls = s.length;
    int nTokens = 0;
    boolean wsPrev = true;

    for (int i = 0; i < ls; i++) {
      boolean ws = isWhitespace(s[i]);

      if (wsPrev && !ws) {
        nTokens++;
      }

      wsPrev = ws;
    } 

    String[] tokens = new String[nTokens];
    nTokens = 0;
    wsPrev = true;
    int start = 0;

    for (int i = 0; i < ls; i++) {
      boolean ws = isWhitespace(s[i]);

      if (wsPrev && !ws) {
        start = i;
      } else if (!wsPrev && ws) {
        tokens[nTokens++] = s0.substring(start, i);
      }

      wsPrev = ws;
    } 

    if (!wsPrev) {
      tokens[nTokens++] = s0.substring(start);
    }

    return tokens;
  }

  public static boolean isWhitespace(char ch) {
    return (ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\r');
  }

  public static boolean isWhitespace(String s) {
    int ls = s.length();

    for (int i = 0; i < ls; i++) {
      if (!isWhitespace(s.charAt(i))) {
        return false;
      }
    } 

    return true;
  }

  public static String escapeXML(String s) {
    StringBuffer b = new StringBuffer();
    int ls = s.length();

    for (int i = 0; i < ls; i++) {
      char ch = s.charAt(i);

      if (ch < 256) {
        String ent = CH_TO_ENT[ch];

        if (ent == null) {
          b.append(ch);
        } else {
          b.append('&').append(ent).append(';');
        }
      } else {
        b.append("&#").append((int) ch).append(';');
      }
    } 

    return b.toString();
  }

  public static String unescapeXML(String s) {
    StringBuffer b = new StringBuffer();
    int ls = s.length();
    int i = 0;

    L0: while (i < ls) {
      char ch = s.charAt(i);
      try {
        if (ch == '&') {
          int indexOfColon = s.indexOf(';', i + 2);

          if (indexOfColon != -1) {
            String ent = s.substring(i + 1, indexOfColon);

            if (ent.charAt(0) == '#') {
              if (ent.charAt(1) == 'x') {
                i = indexOfColon + 1;
                ch = (char) Integer.parseInt(ent.substring(2), 16);
              } else {
                i = indexOfColon + 1;
                ch = (char) Integer.parseInt(ent.substring(1));
              }
            } else {
              String v = (String) ENT_TO_CH.get(ent);

              if (v != null) {
                ch = v.charAt(0);
                i = indexOfColon + 1;
              }
            }
          }
        }
      } catch (Exception e) {
        //$JL-EXC$
        // ignore
      }
      b.append(ch);
      i++;
    }

    return b.toString();
  }

  public static String escapeURL(String s) {
    // This should not be so, because URLEncoder.encode does not
    // support unicode (with 4 hex digits) escapes.
    return URLEncoder.encode(s); //$JL-I18N$
  }

  public static String unescapeURL(String s) {
    return URLDecoder.decode(s); //$JL-I18N$
  }

  public static String escapeJava(String s) {
    StringBuffer b = new StringBuffer();
    int ls = s.length();

    for (int i = 0; i < ls; i++) {
      char ch = s.charAt(i);

      switch (ch) {
        case '\'': {
          b.append("\\'");
          break;
        }
        case '\"': {
          b.append("\\\"");
          break;
        }
        case '\t': {
          b.append("\\t");
          break;
        }
        case '\n': {
          b.append("\\n");
          break;
        }
        case '\r': {
          b.append("\\r");
          break;
        }
        case '\f': {
          b.append("\\f");
          break;
        }
        case '\b': {
          b.append("\\b");
          break;
        }
        default: {
          if (ch > 256) {
            b.append("\\u" + HEX[(ch >>> 12) & 15] + HEX[(ch >>> 8) & 15] + HEX[(ch >>> 4) & 15] + HEX[ch & 15]);
          } else {
            b.append(ch);
          }
        }
      }
    } 

    return b.toString();
  }

  /*
   public static String escapeJNI(String s) {
   }
   public static String unescapeJNI(String s) {
   }
   public static String unescapeJava(String s) {
   }
   public static String escapeHTML(String s) {
   }
   public static String unescapeHTML(String s) {
   }
   */
//  public static void main(String[] args) throws Exception {
//    LogWriter.getSystemLogWriter().println(unescapeXML("abc&amper;lkds"));
//  }

}

