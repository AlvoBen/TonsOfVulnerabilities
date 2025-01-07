/*****************************************************************************
 * Project:      SAP Logon Ticket
 *
 * Title:        InfoUnit
 * Description:  (see JavaDoc)
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 *
 * @author   Christian Becker
 * @version  1.1
 *
 * History:
 * v1.1 - supports different character-encodings when converting
 *        bytes arrays to String.
 *
 * v1.0 - initial version
 *
 ****************************************************************************/

package com.sap.security.api.ticket;

import java.io.*;
import java.util.*;

/** An InfoUnit is the basic data type for storing information in SAP
 *  logon tickets. You need to use it if you want to retrieve information
 *  from the ticket other than the those available by API calls like
 *  {@link TicketVerifier#getUser()}, {@link TicketVerifier#isValid()}.
 * 
 *  <p>
 *  <b>Format of InfoUnits on Disk:</b>
 *  <table>
 *    <tr>
 *      <td><b>Length</b></td><td><b>Type</b></td><td><b>Name</b></td>
 *    </tr>
 *    <tr>
 *      <td>1</td><td>INT1</td><td align="left">ID of InfoUnit (see ID_xxxx)</td>
 *    </tr>
 *    <tr>
 *      <td>2</td><td>INT2</td><td align="left">Length n of Content in bytes</td>
 *    </tr>
 *    <tr>
 *      <td>&lt;INT2 bytes&gt;</td><td>see below</td><td align="left">Content (Type depends on ID)</td>
 *    </tr>
 *  </table>
 *  <p>
 *  <b>Types of InfoUnits used in SAP Logon Ticket:</b><a name="id_table"/>
 *  <table>
 *    <tr>
 *      <td align="left"><b>ID</b></td><td><b>Type</b></td><td><b>Name</b></td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_USER</td><td>CHAR</td><td>User name</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_CREATE_CLIENT</td><td>CHAR</td><td>SystemID of issuing System</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_CREATE_NAME</td><td>CHAR</td><td>SystemID of issuing System</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_CREATE_TIME</td><td>CHAR</td><td>Creation time of Ticket as String "yyyymmddhhmm"</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_VALID_TIME</td><td>INT4</td><td>Valid time (hours)</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_VALID_TIME_MIN</td><td>INT4</td><td>Valid time (minutes)</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_RFC</td><td>CHAR</td><td>RFC Ticket</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_FLAGS</td><td>RAW</td><td>Flags</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_SIGNATURE</td><td>RAW</td><td>PKCS#7 Signature</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_LANGUAGE</td><td>CHAR</td><td>default language of the user</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_USER_UTF</td><td>UTF8</td><td>user name (utf-8 encoded)</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_AUTHSCHEME</td><td>UTF8</td><td>Specifies which autscheme has been satisfied during logon</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_RECIPIENT_CLIENT</td><td>UTF8</td><td>Specifies the recipient client of the assertion ticket.</td>
 *    </tr>
 *    <tr>
 *      <td align="left">ID_RECIPIENT_SID</td><td>UTF8</td><td>Specifies the recipient system of the assertion ticket.</td>
 *    </tr>
 *  </table>
 *  <p>
 *  The values from 0x20 to 0x40 are reserved for additional user id.
 *  Currently, only 0x20 is used (by a portal user id which might differ
 *  from the SAP system user id).
 *  For the above identifiers the data types are as follows. INT4 denotes a 4-byte
 *  integer. RAW denotes a byte array, UTF8 denotes UTF-8 encoding. CHAR means
 *  that the infounit's content is a string value, encoded using an SAP codepage
 *  that you can get from the ticket. In order to get a <code>java.lang.String</code>
 *  object, please use one of the following methods:
 *  <ul>
 *    <li>{@link #bytesToString(byte[], int, int, java.lang.String)}
 *    <li>{@link #bytesToString(byte[], java.lang.String)}
 *  </ul>
 * 
 *  By default, the tickets are created with SAP codepage 1100 (this is the
 *  equivalent of ISO 8859-1). However, you might get a ticket that has been
 *  created with a different codepage. For this case, here's a list of the
 *  most commonly used codepages:
 *     <a name="codepage_table"></a>
 *     <table>
 *      <tr><td><b>SAP codepage number</b></td><td><b>corresponding non-SAP codepage</b></td></tr>
 *      <tr><td>4110</td><td>UTF8</td></tr>
 *      <tr><td>1100</td><td>ISO8859_1</td></tr>
 *      <tr><td>1140</td><td>ISO8859_1</td></tr>
 *      <tr><td>1401</td><td>ISO8859_2</td></tr>
 *      <tr><td>1500</td><td>ISO8859_5</td></tr>
 *      <tr><td>1610</td><td>ISO8859_9</td></tr>
 *      <tr><td>1700</td><td>ISO8859_7</td></tr>
 *      <tr><td>1800</td><td>ISO8859_8</td></tr>
 *      <tr><td>1900</td><td>ISO8859_4</td></tr>
 *      <tr><td>8200</td><td>ISO2022JP</td></tr>
 *      <tr><td>8700</td><td>ISO8859_4</td></tr>
 *      <tr><td>0120</td><td>Cp500</td></tr>
 *      <tr><td>1103</td><td>Cp850</td></tr>
 *      <tr><td>1160</td><td>windows-1252</td></tr>
 *      <tr><td>1404</td><td>Cp1250</td></tr>
 *      <tr><td>1504</td><td>Cp1251</td></tr>
 *      <tr><td>1614</td><td>Cp1254</td></tr>
 *      <tr><td>1704</td><td>Cp1253</td></tr>
 *      <tr><td>1804</td><td>Cp1255</td></tr>
 *      <tr><td>1904</td><td>Cp1257</td></tr>
 *      <tr><td>8604</td><td>Cp874</td></tr>
 *      <tr><td>8704</td><td>Cp1256</td></tr>
 *      <tr><td>8000</td><td>SJIS</td></tr>
 *      <tr><td>8100</td><td>EUC_JP</td></tr>
 *      <tr><td>8300</td><td>Big5</td></tr>
 *      <tr><td>8600</td><td>TIS620</td></tr>
 *     </table>
 *    
 *  
 *  
 **/
public class InfoUnit {

    //---------------------------------------------------------------------
    //--- Constants -------------------------------------------------------

    /* IDs used in InfoUnits. */

    /**
     *  see <a href="#id_table">here</a> for details.
     */ 
    public static final int ID_USER          = 0x01;   // CHAR  User
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_CREATE_CLIENT = 0x02;   // CHAR  system client
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_CREATE_NAME   = 0x03;   // CHAR  system id
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_CREATE_TIME   = 0x04;   // CHAR  creation time
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_VALID_TIME    = 0x05;   // INT4  duration of validity
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_RFC           = 0x06;   // CHAR  is RFC ticket?
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_VALID_TIME_MIN= 0x07;   // INT4  duration of validity (Minutes)
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_FLAGS         = 0x08;   // RAW
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_SIGNATURE     = 0xff;   // RAW
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_LANGUAGE      = 0x09;   // CHAR
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_USER_UTF      = 0x0A;   // UTF8
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_CREATE_CLIENT_UTF
                                             = 0x0B;   // UTF8
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_CREATE_NAME_UTF
                                             = 0x0C;   // UTF8
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_CREATE_TIME_UTF
                                             = 0x0D;   // UTF8
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_LANGUAGE_UTF  = 0x0E;   // UTF8
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */    
    public static final int ID_AUTHSCHEME    = 0x88;    
    
    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_RECIPIENT_CLIENT     = 0x0F;

    /**
     *  see <a href="#id_table">here</a> for details.
     */
    public static final int ID_RECIPIENT_SID     = 0x10;

    /**
     *  Map used to convert an SAP codepage into a ISO or window codepage.
     *  For use by the java method {@link java.lang.String#getBytes(java.lang.String)}
     */
    public static  HashMap        codepageEncoding;       // Mapping codepage <-> encoding

    //---------------------------------------------------------------------
    //--- init codepages --------------------------------------------------

    /** @todo Das Codepage Encoding ist hier an einer zentralen Stelle und
     *  kann auf Dauer aus der Ticket Klasse rausgenommen werden  */

    static {
        // set Codepage mappings
        codepageEncoding = new HashMap();

        codepageEncoding.put("4110", "UTF8");

        codepageEncoding.put("1100", "ISO8859_1");
        codepageEncoding.put("1140", "ISO8859_1");
        codepageEncoding.put("1401", "ISO8859_2");
        codepageEncoding.put("1500", "ISO8859_5");
        codepageEncoding.put("1610", "ISO8859_9");
        codepageEncoding.put("1700", "ISO8859_7");
        codepageEncoding.put("1800", "ISO8859_8");
        codepageEncoding.put("1900", "ISO8859_4");
        codepageEncoding.put("8200", "ISO2022JP");
        codepageEncoding.put("8700", "ISO8859_4");

        // IBM Codepages
        codepageEncoding.put("0120", "Cp500");  // IBM 500 EBCDIC
        codepageEncoding.put("1103", "Cp850");  // IBM 850

        // Windows Codepages
        //codepageEncoding.put("1160", "windows-1252"); // not supported by java ?
        codepageEncoding.put("1404", "Cp1250");
        codepageEncoding.put("1504", "Cp1251");
        codepageEncoding.put("1614", "Cp1254");
        codepageEncoding.put("1704", "Cp1253");
        codepageEncoding.put("1804", "Cp1255");
        codepageEncoding.put("1904", "Cp1257");
        codepageEncoding.put("8604", "Cp874");
        codepageEncoding.put("8704", "Cp1256");

        // other
        codepageEncoding.put("8000", "SJIS");   // shift_jis
        codepageEncoding.put("8100", "EUC_JP");
        codepageEncoding.put("8300", "Big5");
        codepageEncoding.put("8600", "TIS620");

        // Experimental: I am not sure if these mappings are correct.
        codepageEncoding.put("8400", "ISO2022CN_GB");
        codepageEncoding.put("8500", "EUC_KR");          // ks_c_5601-1987
        codepageEncoding.put("4103", "UnicodeLittleUnmarked");  // ?

    }

    //---------------------------------------------------------------------
    //--- attributes ------------------------------------------------------

    private int    id;
    private byte[] content;



    //---------------------------------------------------------------------
    //--- constructors ----------------------------------------------------

    /**
     *  Utility method.
     */ 
    public InfoUnit(InputStream in, int id, int len) throws IOException  {
        this.id      = id;
        this.content = readRaw(in, len);
    }


    /**
     *  Utility method.
     */ 
    public InfoUnit(int id, byte[] data) {
        this.id      = id;
        this.content = data;
    }



    //---------------------------------------------------------------------
    //--- methods ---------------------------------------------------------

    /**
     *  Get the ID of this InfoUnit.
     *  See <a href="#id_table">identifiers</a>
     *  @return the identifier of the info unit.
     */
    public int getID() { return id; }


    /**
     *  Gets the content of an info unit.
     *  The contents are returned as a byte array.
     *  @return content as byte array.
     */ 
    public byte[] getContent() { return content; }


    /** Get the Content of this InfoUnit as String.
     *  @param encoding java codepage for encoding
     *  @exception UnsupportedEncodingException if the encoding is unknown
     **/
    public String getString(String encoding)
        throws UnsupportedEncodingException
    {
        return bytesToString(content, encoding);
    }


    /**
     *  Gets the content of an info unit as integer.
     *  This call makes only sense for
     *  those info units whose type is INT4 (see <a href="#id_table">table</a>).
     *  @return info unit's integer value
     */ 
    public int getInt() { return bytesToInt(content); }



    //---------------------------------------------------------------------
    //--- io --------------------------------------------------------------

    /**
     *  Utility method.
     */ 
    public void writeTo(OutputStream out)
        throws IOException
    {
        out.write(id);

        out.write((content.length >> 8) & 0xFF);
        out.write( content.length       & 0xFF);

        out.write(content);
    }


    /**
     *  Utility method.
     */ 
    public static InfoUnit readInfoUnit(InputStream in)
        throws IOException
    {
        int id  = in.read();   // read InfoUnit ID

        if(id < 0) { return null; }

        int len = bytesToInt(readRaw(in, 2));

        return new InfoUnit(in, id, len);
    }



    //---------------------------------------------------------------------
    //--- static utility methods ------------------------------------------

    /** Transform a byte array into an int.
     *  Makes only sense for units of type INT4.
     *  @param buffer byte array of length 4 from an info unit 
     */
    public static int bytesToInt(byte[] buffer) {
        return bytesToInt(buffer, 0, buffer.length);
    }


    /** Transform part of a byte array to an integer.
     *  Same as {@link #bytesToInt(byte[])} but with a byte array region
     *  instead of an entire byte array.
     *  @param buffer byte array
     *  @param offset offset into the array
     *  @param length should be equal to 4 
     */
    public static int bytesToInt(byte[] buffer, int offset, int length)
    {
        int x = 0;
        int z;

        for (int i=0; i<length; i++, offset++) {
            z = buffer[offset];
            if(z<0) { z += 256; } // transform signed byte --> unsigned byte

            x = (x << 8) + z;
        }

        return x;
    }

    /**
     *  Transforms an integer into a byte array.
     *  The int is encoded as an platform independant byte array.
     *  This function is the counterpart of {@link #bytesToInt(byte[])}
     *  @param i int to convert to
     *  @return byte array
     */
    public static byte [] IntToBytes (int i)
    {
        byte [] b = new byte [4];

        b [3] = (byte)(i % 256); i /= 256;
        b [2] = (byte)(i % 256); i /= 256;
        b [1] = (byte)(i % 256); i /= 256;
        b [0] = (byte)(i % 256); i /= 256;

        return b;
    }


    /**
     *  Converts a byte array into a string.
     *  The specified encoding is used.
     *  @param buffer encoded string
     *  @param encoding used encoding
     *                  This parameter will be passed to the
     *                  {@link InputStreamReader#InputStreamReader(InputStream,String)} constructor.
     *                  See also {@link TicketVerifier#getCodepage()} and <a href="#codepage_table">encodings</a>.
     *  @exception UnsupportedEncodingException if the encoding is unknown
     */ 
    public static String bytesToString(byte[] buffer, String encoding)
        throws UnsupportedEncodingException
    {
        return bytesToString(buffer, 0, buffer.length, encoding);
    }


    /**
     *  Converts a byte array into a string.
     *  Same as {@link #bytesToString(byte[], String)} but with a 
     *  byte array region instead of an entire byte array.
     *  @exception UnsupportedEncodingException if the encoding is unknown
     */ 
    public static String bytesToString(byte[] buffer, int offset, int length, String encoding)
        throws UnsupportedEncodingException
    {
        ByteArrayInputStream ba = new ByteArrayInputStream(buffer, offset, length);
        InputStreamReader    in;

        if(encoding == null) {
            in = new InputStreamReader(ba);
        } else {
            in = new InputStreamReader(ba, encoding);
        }


        StringBuffer s = new StringBuffer();

        try {

            int c = in.read();
            while(c > 0) {
                s.append((char)c);

                c = in.read();
            }

        }
        catch(IOException e) {
//          $JL-EXC$
            e.printStackTrace ();
            /* tracing in API not possible */
        }


        return s.toString();
    }


    /**
     *   Utility method. 
     */
    public static byte[] readRaw(InputStream in, int n)
        throws IOException
    {
        byte[] buffer = new byte[n];

        // read n chars
        int c;
        for(int i=0; i<n; i++) {
            c = in.read();

            if(c < 0) { break; } //? EOF-Error

            buffer[i] = (byte)c;
        }

        return buffer;
    }

    /**
     *  Converts a java String to a byte array.
     *  The specified SAP codepage is used as encoding. (This is the counter part of
     *  {@link #bytesToString(byte[], String)}).
     */ 
    public static byte [] jcharToSAPCP (String source, String SAPCodepage)
        throws UnsupportedEncodingException, IOException
    {
        String encoding = null;

        //System.out.println ("Calling jcharToSAPCP");

        if (null==(encoding=(String)codepageEncoding.get (SAPCodepage)))
            throw new UnsupportedEncodingException ("Java doesn't support SAP codepage " + SAPCodepage);

        ByteArrayOutputStream   baos     =  new ByteArrayOutputStream ();
        // If this call fails there is something wrong anyway
        OutputStreamWriter      CpWriter =  new OutputStreamWriter (baos, encoding);

        CpWriter.write (source);
        CpWriter.flush ();

        return baos.toByteArray();
    }

    /**
     *  Converts a java String to a byte array.
     *  Simply calls <code>return jcharToSAPCP (source, "4110");</code>
     *  see {@link #jcharToSAPCP(String, String)}.
     */
    public static byte [] jcharToUTF8  (String source)
      throws UnsupportedEncodingException, IOException
    {
        return jcharToSAPCP (source, "4110");
    }
}
