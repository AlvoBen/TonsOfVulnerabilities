package com.sap.engine.services.jndi.cosnaming;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;

public class NamingContextExtImpl extends NamingContextImpl implements NamingContextExt {

  private static final char[] escapeExceptions = {';', '/', ':', '?', '@', '&', '=', '+', '$', ',', '-', '_', '.', '!', '~', '*', '\u0092', '(', ')'};
  String[] ids = {"IDL:omg.org/CosNaming/NamingContextExt:1.0", "IDL:omg.org/CosNaming/NamingContext:1.0"};
  static final long serialVersionUID = 3133912374574004838L;

  public NamingContextExtImpl() {
    super();
  }

  public NamingContextExtImpl(String root) {
    super(root);
  }

  public String[] _ids() {
    return ids;
  }

  public org.omg.CORBA.Object resolve_str(String sn) throws NotFound, CannotProceed, InvalidName {
    return super.resolve(to_name(sn));
  }

  public NameComponent[] to_name(String sn) throws InvalidName {
    return super.toNameComponent(sn);
  }

  public String to_string(NameComponent[] n) throws InvalidName {
    return super.toString(n);
  }

  public String to_url(String addr, String sn) throws InvalidAddress, InvalidName {
    if (addr == null || addr.length() == 0) {
      throw new InvalidAddress();
    }
    if (sn == null) {
      sn = "";
    }
    addr = escapeURL(addr);
    sn = escapeURL(sn);
    return "corbaname://" + addr + "#" + sn;
  }

  private static String escapeURL(String s) {
    StringBuffer sb = new StringBuffer();
    int size = s.length();

    for (int c = 0; c < size; c++) {
      char ch = s.charAt(c);

      if (isEscapable(ch)) {
        sb.append(escape(ch));
      } else {
        sb.append(ch);
      }
    } 

    return sb.toString();
  }

  private static boolean isEscapable(char ch) {
    if (Character.isLetterOrDigit(ch)) {
      return false;
    }

    for (int c = 0; c < escapeExceptions.length; c++) {
      if (ch == escapeExceptions[c]) {
        return false;
      }
    } 

    return true;
  }

  private static String escape(char ch) {
    String hex = "0123456789abcdef";
    int charAsInt = ((int) ch) & 0x00FF;
    return "%" + hex.charAt(charAsInt >> 4) + hex.charAt(charAsInt & 0x000F);
  }

}

