package com.sap.engine.lib.xsl.xslt.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Encoding;
import com.sap.engine.lib.xml.parser.helpers.SingleByteEncoding;
import com.sap.engine.lib.xml.parser.helpers.UTF8Encoding;
import com.sap.engine.lib.xsl.xpath.StaticInteger;

/**
 * Title:
 * Description:  An abstract class whose derived classes are responsible for
 *               outputting characters or sequences of characters through a
 *               particular encoding.
 *               Derived classes: EncoderUTF8, EncoderUTF16, EncoderOther.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public final class Encoder {
  
  protected final static int DEFAULT_SIZE = 20000;

  protected DocHandlerSerializer owner = null;
  //protected static final String LINE_SEPARATOR = SystemProperties.getProperty("line.separator");
  protected static final String LINE_SEPARATOR = "\n";
  protected Writer writer = null;
  protected OutputStream stream = null;
  protected static final String CDATA_BEGIN = "<![CDATA[";
  protected static final String CDATA_END = "]]>";
  protected static final String CDATA_END_BEGIN = CDATA_END + CDATA_BEGIN;
  protected boolean escapingDisabled = false;
//  private char[] ca1 = new char[1];
  private byte[] bufwritebyte;// = new byte[20000];
  private char[] bufwritechar;// = new char[20000];
  private byte convarr[] = new byte[10];
  private int bufwriteidx = 0;
  private EncodingHandler encodingHandler = new EncodingHandler();
  private Encoding encoding = encodingHandler.getEncoding(new CharArray("UTF-8"));
  private CharArray chTempOutRef = new CharArray();

  private static final String[] CH_TO_ENT_TEXT = new String[256];
  static {
	  
    CH_TO_ENT_TEXT[0xD] = "&#xD;";
    CH_TO_ENT_TEXT['&'] = "&amp;";
    //CH_TO_ENT_TEXT['\''] = "&apos;";
    //CH_TO_ENT_TEXT['"'] = "&quot;";
    CH_TO_ENT_TEXT['<'] = "&lt;";
    CH_TO_ENT_TEXT['>'] = "&gt;";
  }
  public  Encoder() {

  }
  private void init() {
      writer = null;
      stream = null;
      escapingDisabled = false;
//      owner = null;
      chTempOutRef.clear();
  }

  protected void init(Writer writer, String enc) throws OutputException {
    init();
    bufwriteidx = 0;
    setWriter(writer);
    encoding = encodingHandler.getEncoding(new CharArray(enc));

    if (encoding == null) {
      encoding = encodingHandler.getEncoding(UTF8Encoding.NAME);
    }
    bufwritebyte = null;
    if (bufwritechar == null){
      bufwritechar = new char[DEFAULT_SIZE];
    }
    //encoding.set(enc.length() > 0 ? enc:"utf-8");
  }

  public void init(OutputStream outputStream, String enc) {
    init();
    bufwriteidx = 0;
    stream = outputStream;
    if (enc != null) {
      encoding = encodingHandler.getEncoding(new CharArray(enc));
    }

    if (encoding == null) {
      //throw new UnsupportedEncodingException();
      encoding = encodingHandler.getEncoding(UTF8Encoding.NAME);
    }
    bufwritechar = null;
    if (bufwritebyte == null){
      bufwritebyte = new byte[DEFAULT_SIZE];
    }
  }
  
  public void init(OutputStream out) {
    init (out, null);
  }

  protected void setWriter(Writer writer) {
    // Now we are not able to use the encoding requested,
    // have to use the encoding which is built-in in the writer.
    this.writer = writer;
  }

  protected String getEncodingName() {
    return encoding.getName().toString();
  }

  private void bufferWrite(int b) throws IOException {
    if (bufwriteidx == DEFAULT_SIZE) {
      if (stream != null) {
        stream.write(bufwritebyte);
      } else {
        writer.write(bufwritechar);
      }

      bufwriteidx = 0;
    }

    if (stream != null) {
      bufwritebyte[bufwriteidx++] = (byte) b;
    } else {
      bufwritechar[bufwriteidx++] = (char) b; //(char)(((int)b) & 0x00FF);
    }
  }

  private void bufferWriteStream(int b) throws IOException {
    if (bufwriteidx == DEFAULT_SIZE) {
      stream.write(bufwritebyte);
      bufwriteidx = 0;
    }
    bufwritebyte[bufwriteidx++] = (byte) b;
  }

  public void out(char ch) throws OutputException {
    try {
      makeDefaultEncodingCheck(ch);
    } catch (IOException e) {
      throw new OutputIOException(e);
    }
    //return this;
  }

  public void outDirect(char ch) throws OutputException {
    try {
      bufferWrite((byte) ch);
    } catch (IOException e) {
      throw new OutputIOException(e);
    }
    //return this;
  }

  public Encoder enableOutputEscaping() {
    escapingDisabled = false;
    return this;
  }

  public Encoder disableOutputEscaping() {
    escapingDisabled = true;
    return this;
  }

  public Encoder out(char[] ch, int start, int length) throws OutputException {
    for (int i = 0; i < length; i++) {
      makeDefaultEncodingCheck(ch[start + i]);
    } 

    return this;
  }

  public Encoder out(String s) throws OutputException {
    for (int i = 0, l = s.length(); i < l; i++) {
      makeDefaultEncodingCheck(s.charAt(i));
    } 

    return this;
  }

  private void makeDefaultEncodingCheck(char ch) throws OutputException {
    try {
      if (stream != null) {
        makeDefaultEncodingCheckStream(ch);
      } else {
        bufferWrite(ch);
      }
    } catch (Exception e) {
      throw new OutputException(e);
    }
  }
        
  private void makeDefaultEncodingCheckStream(char ch) throws OutputException {
    try {
	  if (ch < 0x80 && (encoding.getClass() == UTF8Encoding.class || encoding.getClass() == SingleByteEncoding.class)) {
	    bufferWriteStream((byte)ch);
	  } else {
        int len = encoding.reverseEncode(convarr, ch);
        if (len == Encoding.UNSUPPORTED_CHAR) {
          outCharRefDec(ch);
        } else {
          switch (len) {
            case 1: {
              bufferWrite(convarr[0]);
              break;
            }
            case 2: {
              bufferWrite(convarr[0]);
              bufferWrite(convarr[1]);
              break;
            }
            default: {
              for (int i = 0; i < len; i++) {
                bufferWrite(convarr[i]);
              } 
            }
          }
        }
		  }
    } catch (IOException e) {
      throw new OutputException(e);
    }
  }

  public void outEscaped(char ch) throws OutputException {
    if (escapingDisabled) {
      makeDefaultEncodingCheck(ch);
      return;
    }

    if (ch < 0xFF && CH_TO_ENT_TEXT[ch] != null) {
		  out(CH_TO_ENT_TEXT[ch]);
		  return;
    }
    makeDefaultEncodingCheck(ch);
  	//return this;
  }


  public Encoder outEscaped(CharArray ca) throws OutputException {
    return outEscaped(ca.getData(), ca.getOffset(), ca.length());
  }

  public Encoder outEscaped(String s) throws OutputException {
    if (escapingDisabled) {
      return out(s);
    }

    if (stream != null) {
      char ch;
      for (int i = 0; i < s.length(); i++) {
        ch = s.charAt(i);
        try {
         if (CH_TO_ENT_TEXT[ch] != null) {
           out(CH_TO_ENT_TEXT[ch]);
           continue;
         }
        } catch (ArrayIndexOutOfBoundsException e){
          //$JL-EXC$
          //Perfromance opitmiziation, in case the exception is thrown, the next row is in charge
        } 
        makeDefaultEncodingCheckStream(ch);
      }
      return this;
    }
 
    for (int i = 0; i < s.length(); i++) {
      outEscaped(s.charAt(i));
    } 

    return this;
  }



  public void outAttribute(String attrib, char delimiter) throws OutputException {
    int len = attrib.length();
    for (int i = 0; i < len; i++) {
      char ch = attrib.charAt(i);
      outAttribChar(ch, delimiter);
    }
      }

  private void outAttribChar(char ch, char delimiter) throws OutputException {
      switch (ch) {
        case '\'': {
          if (delimiter == '\'') {
            out("&apos;");
          } else {
            out('\'');
          }

          break;
        }
        case '\"': {
          if (delimiter == '\"') {
            out("&quot;");
          } else {
            out('\"');
          }

          break;
        }
        case '&': {
          out("&amp;");
          break;
        }
        case '<': {
          out("&lt;");
          break;
        }
        case '>': {
          out(">");
          break; // out("&gt;");   break;

        }
        case (char) 0x9: {
          out("&#x9;");
          break;
        }
        case (char) 0xA: {
          out("&#xA;");
          break;
        }
        case (char) 0xD: {
          out("&#xD;");
          break;
        }
        default: {
          makeDefaultEncodingCheck(ch);
          break;
        }
      }
  }
  
  public Encoder outAttribute(CharArray ca, char delimiter) throws OutputException {
    char[] data = ca.getData();
    int offset = ca.getOffset();
    int end = offset + ca.length();

    for (int i = offset; i < end; i++) {
      char ch = data[i];
      outAttribChar(ch, delimiter);
    } 

    return this;
  }

  public Encoder outEscaped(char[] ch, int start, int length) throws OutputException {
    if (escapingDisabled) {
      return out(ch, start, length);
    }

    int end = start + length;

    for (int i = start; i < end; i++) {
      outEscaped(ch[i]);
    } 

    return this;
  }

  public Encoder out(CharArray ca) throws OutputException {
    return out(ca.getData(), ca.getOffset(), ca.length());
  }

  public Encoder outDirect(char[] ch, int start, int len) throws OutputException {
	  try {
  	     for (int i=start; i<len; i++) {
		    bufferWrite((byte)ch[i]);
	     }
  	     return this;
	  } catch (Exception e) {
		  throw new OutputException(e);
	  }
  }

  public Encoder outDirect(CharArray ca) throws OutputException {
	  return outDirect(ca.getData(), ca.getOffset(), ca.getSize());
  }


  protected Encoder outCDATA(char[] ch, int start, int length) throws OutputException {
    out(CDATA_BEGIN);

    if (length < 3) {
      out(ch, start, length);
    } else {
      char prev2 = ch[start];
      char prev = ch[start + 1];
      outEscaped(prev2);
      outEscaped(prev);
      int end = start + length;

      for (int i = start + 2; i < end; i++) {
        char curr = ch[i];

        if ((prev2 == ']') && (prev == ']') && (curr == '>')) {
          out(CDATA_END_BEGIN);
        }

        outEscaped(curr);
        prev2 = prev;
        prev = curr;
      } 
    }

    out(CDATA_END);
    return this;
  }

  protected Encoder outln() throws OutputException {
    return out(LINE_SEPARATOR);
  }

  protected Encoder outSpace(int x) throws OutputException {
    for (int i = 0; i < x; i++) {
      out(' ');
    } 

    return this;
  }

  /**
   * Outputs Character Reference in the stream using hex code
   */
  protected Encoder outCharRefHex(char ch) throws OutputException {
    out("&#x");
    out(Integer.toHexString(ch));
    out(';');
    return this;
  }

  /**
   * Outputs Character Reference in the stream using decimal code
   */
  protected Encoder outCharRefDec(char ch) throws OutputException {
    out("&#");
    out(StaticInteger.intToCharArraySync(ch, chTempOutRef));
    out(';');
    return this;
  }

  /**
   * Outputs HTML Attribute and uses character references for URI's if uriflag==true
   */
  protected Encoder outHTMLAttribute(CharArray s, boolean uriflag, char delimiter) throws OutputException {
    char data[] = s.getData();
    int size = s.length();
    int off = s.getOffset();
    int totsize = size + off;
    boolean showEscaped;

    for (int i = off; i < totsize; i++) {
      showEscaped = true;

      if (data[i] == '<') {
        showEscaped = false;
      }

      if (((i + 1) < totsize) && (data[i] == '&') && (data[i + 1] == '{')) {
        showEscaped = false;
      }

      if (data[i] == delimiter) {
        outAttribChar(data[i], delimiter);
        continue;
      }

      if (showEscaped) {
        if (uriflag && (data[i] > 128)) {
          outCharRefHex(data[i]);
        } else if (uriflag && (data[i] == 0x20)) {
          out("%20");
          // removed due to the fact that the href and other uri attributes MUST be escaped, and not
          // escaping them is not fatal, because browsers support bad HTML but still it is not correct
          //        } else if (uriflag) {
          //          out(data[i]);
        } else {
          outEscaped(data[i]);
        }
      } else {
        out(data[i]);
      }
    } 

    return this;
  }

  public Encoder flush() throws OutputIOException {
    try {
      if (stream != null) {
        stream.write(bufwritebyte, 0, bufwriteidx);
      } else {
        writer.write(bufwritechar, 0, bufwriteidx);
        writer.flush();
      }

      bufwriteidx = 0;
    } catch (IOException e) {
      throw new OutputIOException(e);
    }
    return this;
  }

  public void close() throws OutputIOException {
    try {
      flush();
      if (stream != null) {
        stream.close();
      } else if (writer != null) {
        writer.close();
      }
    } catch (IOException e) {
      throw new OutputIOException(e);
    }
  }

  protected void setOwner(DocHandlerSerializer owner) {
    this.owner = owner;
  }

  protected DocHandlerSerializer getOwner() {
    return owner;
  }

  protected boolean isEscapingEnabled() {
    return !escapingDisabled;
  }

  protected void setEscaping(boolean b) {
    escapingDisabled = !b;
  }

}

