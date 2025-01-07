/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.lib;

import java.io.UnsupportedEncodingException;
import java.io.File;

import com.sap.engine.services.httpserver.lib.exceptions.HttpIllegalArgumentException;
import com.sap.engine.services.httpserver.server.Log;

public class ParseUtils {

  public static final String separator = "/";
  public static final byte[] separatorBytes = "/".getBytes();
  public static final char separatorChar = '/';
  public static final byte separatorByte = '/';

  private static final byte oppositeSlash = '\\';

  private static final byte[] ZERO_BYTE_ARRAY = new byte[0];

  private final static int IPADDRESSSIZE = 16;
  private final static int INT16SIZE = 2;
  
  public static String canonicalizeFS(String path) {
    boolean startsWithDoubleSlash = path.length() > 1
            && (path.charAt(0) == '\\' || path.charAt(0) == '/')
            && (path.charAt(1) == '\\' || path.charAt(1) == '/');
    String res = canonicalize(path);
    if (startsWithDoubleSlash) {
      res = File.separator + res;
    }
    return res.replace('/', File.separatorChar).replace('\\', File.separatorChar);
  }
  
  // TODO: Completely rewrite this method, cause it is patched a lot
  public static String canonicalize(String path) {
    char dest[], temp;
    int si, di, dots, omit;
    boolean slash;
//    boolean lastSlash = false; //for Windows

    dest = path.toCharArray();

    for(dots = omit = 0, slash = false, si = di = dest.length - 1; si >= 0; si--) {
      temp = dest[si];
      if (temp == oppositeSlash || temp == separatorChar) {
        if (slash) {
          if (dots == 2) {
            omit++;
          } else if (dots > 2) {
            if (omit > 0) {
              omit--;
            }
          } else if(dots == 0 && omit > 0) {
            omit--;
          }
        } else {
          slash = true;
          if (omit == 0) {
            dest[di--] = temp;
//            lastSlash = true; //for Windows
          } else {
            omit--;
          }
        }
        dots = 0;
      } else if (slash) {
        if (temp == '.') {
          ++dots;
        } else {
          slash = false;
          if (omit == 0) {
//            if (lastSlash) { //for Windows
//            while (dots > 1) { //for Windows
            while (dots > 0) {
                dest[di--] = '.';
                dots--;
              }
//            } //for Windows
            dest[di--] = temp;
          }
        }
      } else if (omit == 0) {
        dest[di--] = temp;
      }
    }

    //Removes last slashes if any
    int l = dest.length - 1;
    while (l >= 0 && (dest[l] == oppositeSlash || dest[l] == separatorChar)) { l--; }
    
    // If omit is bugger than 0 then canonicalized path is under the root
    if (omit > 0) {
      String pdir = "/..";
      StringBuffer sb = new StringBuffer();
      for (; omit > 0; omit--) { sb.append(pdir); }
      sb.append(dest, di + 1, l - di);
      return sb.toString();
    }
    return new String(dest, di + 1, l - di);
  }

  public static byte[] separatorsToFS(String path) {
    byte[] pathBytes = path.getBytes();
    return separatorsToFS(pathBytes);
  }
  
  public static byte[] separatorsToFSEncoding(String path,String encoding) throws UnsupportedEncodingException {
      byte[] pathBytes = path.getBytes(encoding);
      return separatorsToFS(pathBytes);
  }
  
  public static byte[] separatorsToSlash(String path) {
    byte[] pathBytes = path.getBytes();
    return separatorsToSlash(pathBytes);
  }

  /**
   * Convert the client's IP (currently stored in byte array) to string i.e. "10.55.71.50".
   * 
   * @param byteArrIp 
   * @return 
   */
  public static String ipToString(byte[] byteArrIp) {
    return new String(inetAddressByteToString(byteArrIp));
  }
  
  /**
   * Encodes this <tt>ip</tt> into a sequence of bytes representing the given IP adress in  XXX.XXX.XXX.XXX format.
   * @param ip byte[] representation of the client IP adress
   * @return  If ip is null or has length!=4 an empty byte[] is returned , otherwise the resultant byte array
   */
  public static byte[] inetAddressByteToString(byte[] ip) {
    if (ip == null || !(ip.length == 4 || ip.length ==16)) {
      return ZERO_BYTE_ARRAY;
    }
    
    if (ip.length == 4) {
      byte[] ascii;

      int i, length, d1, d2, d3;
      length = 3;

      for (i = 3; i >= 0; i--) {
        length += (ip[i] < 0 || ip[i] > 99) ? 3 : ip[i] > 9 ? 2 : 1;
      }

      ascii = new byte[length];
      i = 3;
      while (true) {
        d2 = ip[i] & 0xff;
        d1 = d2 % 10;
        if (d2 >= 200) {
          d3 = 2;
          d2 = (d2 / 10) - 20;
        } else if (d2 >= 100) {
          d3 = 1;
          d2 = (d2 / 10) - 10;
        } else {
          d3 = 0;
          d2 = d2 / 10;
        }

        ascii[--length] = (byte) ('0' + d1);
        if (d2 > 0 || d3 > 0) {
          ascii[--length] = (byte) ('0' + d2);
          if (d3 > 0) {
            ascii[--length] = (byte) ('0' + d3);
          }
        }

        if (length > 0) {
          ascii[--length] = (byte) '.';
          i--;
        } else {
          break;
        }
      }
      return ascii;
    }
    
    //for IPv6
    char[] ipv6CharArray = ParseUtils.byteToTextIPv6(ip).toCharArray();
    byte[] ipv6ByteArray = new byte[ipv6CharArray.length];
    for (int i = 0; i < ipv6ByteArray.length; i++) {
      ipv6ByteArray[i] = (byte)(ipv6CharArray[i] & 0x00ff);
    }
    
    return ipv6ByteArray;
  }

  public static String convertAlias(String alias) {
//    if (alias.equals("/") || alias.equals("\\")) {
    if (alias.length() == 1 && (alias.charAt(0) == '/' || alias.charAt(0) == '\\')) {
      return ParseUtils.separator;
    }
    return alias.replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
  }

	// -------------------------- PRIVATE --------------------------

  private static byte[] separatorsToFS(byte[] path){
      byte[] pathBytes = new byte[path.length];
      System.arraycopy(path,0,pathBytes,0,path.length);
      int ptr = 0;
      boolean previousIsSlash = false;
      for (int i = 0; i < pathBytes.length; i++) {
        if (previousIsSlash && i > 1) {
          if (pathBytes[i] == '/' || pathBytes[i] =='\\') {
            continue;
          } else {
            previousIsSlash = false;
            pathBytes[ptr++] = pathBytes[i];
          }
        } else {
          if (pathBytes[i] == '/' || pathBytes[i] =='\\') {
            previousIsSlash = true;
            pathBytes[ptr++] = (byte)File.separatorChar;
          } else {
            pathBytes[ptr++] = pathBytes[i];
          }
        }
      }
      if (ptr != pathBytes.length) {
        byte[] tmp = new byte[ptr];
        System.arraycopy(pathBytes, 0, tmp, 0, ptr);
        pathBytes = tmp;
      }
      return pathBytes;

  }
  
  private static byte[] separatorsToSlash(byte[] path){
    byte[] pathBytes = new byte[path.length];
    System.arraycopy(path,0,pathBytes,0,path.length);
    int ptr = 0;
    boolean previousIsSlash = false;
    for (int i = 0; i < pathBytes.length; i++) {
      if (previousIsSlash) {
        if (pathBytes[i] == '/' || pathBytes[i] =='\\') {
          continue;
        } else {
          previousIsSlash = false;
          pathBytes[ptr++] = pathBytes[i];
        }
      } else {
        if (pathBytes[i] == '/' || pathBytes[i] =='\\') {
          previousIsSlash = true;
          pathBytes[ptr++] = (byte)ParseUtils.separatorChar;
        } else {
          pathBytes[ptr++] = pathBytes[i];
        }
      }
    }
    if (ptr != pathBytes.length) {
      byte[] tmp = new byte[ptr];
      System.arraycopy(pathBytes, 0, tmp, 0, ptr);
      pathBytes = tmp;
    }
    return pathBytes;
  }

   /**
	 * Checks if the given value contains CR or LF. If found throws exception.
	 * Used for filtering cookie fileds.
	 * 
	 * @param value
	 */
	public static void errorOnCRLF(String value) {
		if (value == null || value.length() == 0) {
			return;
		}
		if (value.indexOf("\r") > 0 || value.indexOf("\n") > 0) {
			Log.logWarning("ASJ.http.000003", 
			  "Cookie field contains CR or LF. Value is : [{0}].", new Object[]{value}, null, null, null);
			throw new HttpIllegalArgumentException(HttpIllegalArgumentException.IMPROPER_USE_OF_CRLF, new Object[] { value });
		}
	}
	
    
    /**
     * Converts IPv4 byte array to string
     *
     * @param ipbytearray - a byte array representing an IPv4 address
     * @return a String representing the IPv4 address
     */

    public static String byteToTextIPv4(byte[] ipbytearray)
    {
        return (ipbytearray[0] & 0xff) + "." + (ipbytearray[1] & 0xff) + "." + (ipbytearray[2] & 0xff) + "." + (ipbytearray[3] & 0xff);
    }

    /**
     * Convert IPv6 address to String.
     *
     * @param ipbytearray -  a byte array of the IPv6 address
     * @return a String representing an IPv6 address
     */
    public static String byteToTextIPv6(byte[] ipbytearray)
    {
        StringBuffer sb = new StringBuffer(39);
        for (int i = 0; i < (IPADDRESSSIZE / INT16SIZE); i++) {
            sb.append(Integer.toHexString(((ipbytearray[i<<1]<<8) & 0xff00)
                                          | (ipbytearray[(i<<1)+1] & 0xff)));
            if (i < (IPADDRESSSIZE / INT16SIZE) -1 ) {
               sb.append(":");
            }
        }
        return sb.toString();
    }
    
    /*
     * Convert IPv6 presentation level address to network order binary form.
     *
     * Any part following a per-cent % is ignored.
     *
     * @param src a String IPv6
     * @return a byte array IPv6
     */
    public static byte[] textToNumericFormatV6(String src)
    {
        // Shortest valid string is "::", hence at least 2 chars
        if (src == null || src.length() < 2) {
            return null;
        }

        int colonp;
        char ch;
        boolean saw_xdigit;
        int val;
        char[] srcb = src.toCharArray();
        byte[] dst = new byte[INADDR16SZ];

        int srcb_length = srcb.length;
        int pc = src.indexOf ("%");
        if (pc == srcb_length -1) {
            return null;
        }

        if (pc != -1) {
            srcb_length = pc;
        }

        colonp = -1;
        int i = 0, j = 0;
        /* Leading :: requires some special handling. */
        if (srcb[i] == ':')
            if (srcb[++i] != ':')
                return null;
        int curtok = i;
        saw_xdigit = false;
        val = 0;
        while (i < srcb_length) {
            ch = srcb[i++];
            int chval = Character.digit(ch, 16);
            if (chval != -1) {
                val <<= 4;
                val |= chval;
                if (val > 0xffff)
                    return null;
                saw_xdigit = true;
                continue;
            }
            if (ch == ':') {
                curtok = i;
                if (!saw_xdigit) {
                    if (colonp != -1)
                        return null;
                    colonp = j;
                    continue;
                } else if (i == srcb_length) {
                    return null;
                }
                if (j + INT16SZ > INADDR16SZ)
                    return null;
                dst[j++] = (byte) ((val >> 8) & 0xff);
                dst[j++] = (byte) (val & 0xff);
                saw_xdigit = false;
                val = 0;
                continue;
            }
            if (ch == '.' && ((j + INADDR4SZ) <= INADDR16SZ)) {
                String ia4 = src.substring(curtok, srcb_length);
                /* check this IPv4 address has 3 dots, ie. A.B.C.D */
                int dot_count = 0, index=0;
                while ((index = ia4.indexOf ('.', index)) != -1) {
                    dot_count ++;
                    index ++;
                }
                if (dot_count != 3) {
                    return null;
                }
                byte[] v4addr = textToNumericFormatV4(ia4);
                if (v4addr == null) {
                    return null;
                }
                for (int k = 0; k < INADDR4SZ; k++) {
                    dst[j++] = v4addr[k];
                }
                saw_xdigit = false;
                break;  /* '\0' was seen by inet_pton4(). */
            }
            return null;
        }
        if (saw_xdigit) {
            if (j + INT16SZ > INADDR16SZ)
                return null;
            dst[j++] = (byte) ((val >> 8) & 0xff);
            dst[j++] = (byte) (val & 0xff);
        }

        if (colonp != -1) {
            int n = j - colonp;

            if (j == INADDR16SZ)
                return null;
            for (i = 1; i <= n; i++) {
                dst[INADDR16SZ - i] = dst[colonp + n - i];
                dst[colonp + n - i] = 0;
            }
            j = INADDR16SZ;
        }
        if (j != INADDR16SZ)
            return null;
        
        //this is removed because we store the IP in the IPv6 format and according preferredipv6format property
        //the corresponding value is returned.
//        byte[] newdst = convertFromIPv4MappedAddress(dst);
//        if (newdst != null) {
//            return newdst;
//        } else {
//            return dst;
//        }
        return dst;
    }
    
    
    private final static int INADDR4SZ = 4;
    private final static int INADDR16SZ = 16;
    private final static int INT16SZ = 2;

    /*
     * Converts IPv4 address in text
     *
     * @param src a String IPv4 address in standard format
     * @return a byte array IPv4
     */
    public static byte[] textToNumericFormatV4(String src)
    {
        if (src.length() == 0) {
            return null;
        }

        byte[] res = new byte[INADDR4SZ];
        String[] s = src.split("\\.", -1);
        long val;
        try {
            switch(s.length) {
            case 1:
                /*
                 * if the string array is one (one number) the ip is taken from this number 
                 */

                val = Long.parseLong(s[0]);
                if (val < 0 || val > 0xffffffffL)
                    return null;
                res[0] = (byte) ((val >> 24) & 0xff);
                res[1] = (byte) (((val & 0xffffff) >> 16) & 0xff);
                res[2] = (byte) (((val & 0xffff) >> 8) & 0xff);
                res[3] = (byte) (val & 0xff);
                break;
            case 2:
                /*
                 * If the input is two parts, the second part is used as 24 bit
                 * (can be used for class A network address)
                 */

                val = Integer.parseInt(s[0]);
                if (val < 0 || val > 0xff)
                    return null;
                res[0] = (byte) (val & 0xff);
                val = Integer.parseInt(s[1]);
                if (val < 0 || val > 0xffffff)
                    return null;
                res[1] = (byte) ((val >> 16) & 0xff);
                res[2] = (byte) (((val & 0xffff) >> 8) &0xff);
                res[3] = (byte) (val & 0xff);
                break;
            case 3:
                /*
                 * If the input is 3 parts, third part - 16 bits
                 * Can be used for class B
                 */
                for (int i = 0; i < 2; i++) {
                    val = Integer.parseInt(s[i]);
                    if (val < 0 || val > 0xff)
                        return null;
                    res[i] = (byte) (val & 0xff);
                }
                val = Integer.parseInt(s[2]);
                if (val < 0 || val > 0xffff)
                    return null;
                res[2] = (byte) ((val >> 8) & 0xff);
                res[3] = (byte) (val & 0xff);
                break;
            case 4:
                /*
                 * Four bytes representation - from left to right
                 */
                for (int i = 0; i < 4; i++) {
                    val = Integer.parseInt(s[i]);
                    if (val < 0 || val > 0xff)
                        return null;
                    res[i] = (byte) (val & 0xff);
                }
                break;
            default:
                return null;
            }
        } catch(NumberFormatException e) {
            return null;
        }
        return res;
    }
    
    /*
     * Convert IPv4-Mapped address to IPv4 address. Both input and
     * returned value are in network order binary form.
     *
     * @param src a String IPv4-Mapped address
     * @return a byte array IPv4 address, or the input address if addr is NOT IPv4-Mapped address
     * 
     */
    public static byte[] convertFromIPv4MappedAddress(byte[] addr) {
        if (isIPv4MappedAddress(addr)) {
            byte[] newAddr = new byte[INADDR4SZ];
            System.arraycopy(addr, 12, newAddr, 0, INADDR4SZ);
            return newAddr;
        }
        return addr;
    }

    /**
     * Check if the InetAddress is an IPv4 mapped IPv6 address.
     *
     * @return a <code>boolean</code> true if InetAddress is
     * an IPv4 mapped IPv6 address; or false if address is IPv4 address.
     */
    private static boolean isIPv4MappedAddress(byte[] addr) {
        if (addr.length < INADDR16SZ) {
            return false;
        }
        if ((addr[0] == 0x00) && (addr[1] == 0x00) &&
            (addr[2] == 0x00) && (addr[3] == 0x00) &&
            (addr[4] == 0x00) && (addr[5] == 0x00) &&
            (addr[6] == 0x00) && (addr[7] == 0x00) &&
            (addr[8] == 0x00) && (addr[9] == 0x00) &&
            (addr[10] == (byte)0xff) &&
            (addr[11] == (byte)0xff))  {
            return true;
        }
        return false;
    }

    /**
     * @param src a String representing an IPv6 address in textual format
     * @return a boolean indicating whether src is an IPv6 literal address
     */
    public static boolean isIPv6LiteralAddress(String src) {
        return textToNumericFormatV6(src) != null;
    }




}