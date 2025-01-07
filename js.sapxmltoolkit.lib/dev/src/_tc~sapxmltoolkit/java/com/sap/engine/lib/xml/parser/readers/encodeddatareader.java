package com.sap.engine.lib.xml.parser.readers;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URLEncoder;

import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;
import com.sap.engine.lib.xml.parser.helpers.AdvancedXMLStreamReader;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Encoding;
import com.sap.engine.lib.xml.parser.helpers.UTF16Encoding;
import com.sap.engine.lib.xml.util.ReaderInputStream;

public class EncodedDataReader {

  protected AdvancedXMLStreamReader parent = null;
  protected int pos = 0;
  protected int ext = 0;
  public boolean eos = false;
  protected int a1, a2;
  protected int restBytes = 0;
  protected int totalRead = 0;
  protected CharArray id = null;
  protected CharArray name = null;
  //Location data
  protected int iLevel = 0;
  protected int iRow = 1;
  protected int iCol = 1;
  protected int iMarkRow = 0;
  protected int iMarkCol = 0;
  protected boolean bLiteral = false;
  protected int lastchar;
  private int sid = -1;
  protected boolean bReadRaw = false;
  protected InputStream in = null;
  protected char[] buf = null;
  private char[] bufToRead;
  protected byte[] bytebuf = null;
  //protected int len = -1;
  protected char[] mapping = null;
  protected boolean isUTF8 = true;
  protected int idx = 0;
  protected int DEFAULT_EXT = 10000;
  int x1;
  byte utf8buf[] = new byte[10];
  byte utf8buflen = 0;
  int utf8left = 0;
  private EncodingHandler encodingHandler = new EncodingHandler();
  private final static CharArray crUTF8 = new CharArray("utf-8").setStatic();
  public Encoding encoding = encodingHandler.getEncoding(crUTF8);
  
  private OutputStream debugOutputStream;

  public  EncodedDataReader() {
    //LogWriter.getSystemLogWriter().println("EncodedDataReader - newInsstnace");
    reuse();
  }

  public EncodedDataReader(AdvancedXMLStreamReader parent, InputStream in, CharArray id) {
    reuse(parent, in, id);
  }

  public EncodedDataReader(AdvancedXMLStreamReader parent, Reader rd, CharArray id) {
    reuse(parent, new ReaderInputStream(rd), id);
    //    this.in = new ReaderInputStream(rd);
    //    this.parent = parent;
    //    buf = new byte[ext];
    //    ext = DEFAULT_EXT;
    //    this.id = id;
    //    eos = false;
  }

  public EncodedDataReader reuse(AdvancedXMLStreamReader parent, InputStream in, CharArray id) {
    reuse();
    this.in = in;
    this.parent = parent;

    if (buf == null) {
      //          LogWriter.getSystemLogWriter().println("EncodedDataReader - newInsstnace");
      buf = new char[DEFAULT_EXT];
    }

    if (bytebuf == null) {
      bytebuf = new byte[DEFAULT_EXT];
    }

    ext = DEFAULT_EXT;
    this.id = id;
    eos = false;

    if (in instanceof ReaderInputStream) {
      setReadRaw(true);
    }

    initDebug();
    return this;
  }

  public EncodedDataReader reuse(AdvancedXMLStreamReader parent, Reader rd, CharArray id) {
    return reuse(parent, new ReaderInputStream(rd), id);
    //    this.in = new ReaderInputStream(rd);
    //    this.parent = parent;
    //    buf = new byte[ext];
    //    ext = DEFAULT_EXT;
    //    this.id = id;
    //    eos = false;
    //    return this;
  }

  protected void reuse() {
    pos = 0;
    ext = DEFAULT_EXT;
    eos = false;
    restBytes = 0;
    totalRead = 0;
    iLevel = 0;
    iRow = 1;
    iCol = 1;
    iMarkRow = 0;
    iMarkCol = 0;
    //CharArray id = null;
    bLiteral = false;
    bReadRaw = false;
    encoding = encodingHandler.getEncoding(crUTF8);
  }
  
  private void initDebug() {
    String debugDir = SystemProperties.getProperty("sapxmltoolkit.debug.dir");
    if (debugDir == null) {
      debugOutputStream = null;
      return;
    }
    File dir = new File(debugDir);
    dir.mkdirs();
    String systemID = String.valueOf(this.id);
    String threadName = Thread.currentThread().getName();    
    String fileName = URLEncoder.encode("_" + threadName + '_' + systemID); //$JL-I18N$
    String newFileName = fileName;
    synchronized (EncodedDataReader.class) {
      File f;
      do {
        f = new File(dir, newFileName);
        newFileName = fileName + '_' + System.currentTimeMillis();
      } while (f.exists());
      try {
        debugOutputStream = new FileOutputStream(f);
      } catch (IOException ioe) {
        //$JL-EXC$
        ioe.printStackTrace();
      }
    }
  }

  public int read() throws Exception {
    if (restBytes == 0) {
      fetchData();
    }

    //    LogWriter.getSystemLogWriter().println("pos=" + pos +  " = " + Integer.toHexString(((int)buf[pos]) & 0x000000FF) + " = " + Integer.toHexString(buf[pos]));
    //    LogWriter.getSystemLogWriter().println("Reading pos = " + pos + ", buf[pos] = " + Integer.toHexString(buf[pos]) + ", eos = " + eos);
    if (eos) {
      parent.onEncodedDataReaderEOF();

      if (parent.xmlParser != null && parent.xmlParser.getCloseStreams()) {
        in.close();
      }
      
      in = null;

      return 0;
    }

    //    LogWriter.getSystemLogWriter().println("EDR : 1");
    restBytes--;

    if (buf[pos] == 0xA) {
      iRow++;
      iCol = 1;
    } else if (buf[pos] != 0xD) {
      iCol++;
    }

//    if (len > 0) {
//      len--;
//    }

    //    LogWriter.getSystemLogWriter().println("EDR : 2");
    if (bReadRaw) {
      x1 = (int) buf[pos];
    } else {
//      LogWriter.getSystemLogWriter().println(encoding.getClass());
//      LogWriter.getSystemLogWriter().println("EDR : 3 " + encoding + " buf[pos] = " + Integer.toHexString(buf[pos]));
      x1 = encoding.process((byte) buf[pos]);

      //      LogWriter.getSystemLogWriter().println("EDR 1: x1 = " + Integer.toHexString(x1));
      if (x1 == Encoding.NEEDS_MORE_DATA) {
        pos++;
        return read();
      } else if (x1 == Encoding.UNSUPPORTED_CHAR) {
        throw new ParserException("Unsupported character: " + Integer.toHexString(buf[pos]), parent.getID(), iRow, iCol);
      }

      //    LogWriter.getSystemLogWriter().println("EDR : 5");
    }

    pos++;
    //    else if (isUTF8) {
    //      if (utf8left > 0) {
    //        utf8buf[utf8buflen++] = buf[pos];
    //        //        LogWriter.getSystemLogWriter().println("idx=" + (utf8buflen-1) +  "-" + Integer.toHexString(((int)buf[pos]) & 0x000000FF));
    //        utf8left--;
    //        pos++;
    //        if (utf8left > 0) return read();
    //      }
    //      if (utf8left == 0 && utf8buflen > 0) {
    //        x1 = (char)parseUTF8toUCS4(utf8buf, 0, utf8buflen)[1];
    //        utf8buflen = 0;
    //        return x1;
    //      } else {
    //        x1 = (char)buf[pos];
    //      }
    //      if (buf[pos] < 0 && utf8left == 0) {
    //      //        LogWriter.getSystemLogWriter().println("Sutf 8: buf[pos]=" + buf[pos] + "  row:col=" + iRow + ":" + iCol);
    //        utf8left = getUTF8CharCount(buf[pos]);
    //        utf8buf[utf8buflen++] = buf[pos];
    //      //        LogWriter.getSystemLogWriter().println("idx=" + (utf8buflen-1) +  "-" + Integer.toHexString(((int)buf[pos]) & 0x000000FF));
    //        utf8left--;
    //        pos++;
    //        return read();
    //      }
    //    } else {
    //      idx = buf[pos];
    //      if (idx < 0) idx = 0x100 + idx;
    //      x1 = mapping[idx];
    //    }
    //
    //    pos ++;
    //     LogWriter.getSystemLogWriter().println("EncodedDataReader: x1 =" + Integer.toHexString(x1));
    return x1;
  }

  //  protected void resizeBuffer() {
  //    byte newbuf[] = new int[buf.length + ext];
  //    System.arraycopy(buf, 0, newbuf, 0, buf.length);
  //    buf = newbuf;
  //    ext*=2;
  //  }
  protected int readData(int off, int len) throws IOException {
    try {
      if (bufToRead != null) {
        int l = bufToRead.length;
        System.arraycopy(bufToRead, 0, buf, off, l);
        bufToRead = null;
        return l;
      }

      //    LogWriter.getSystemLogWriter().println("EDR: Before... off=" + off + ", len=" +len);
      if (in instanceof ReaderInputStream) {
        //LogWriter.getSystemLogWriter().println("EDR.readData: off=" + off + ", len=" + len);
        return ((ReaderInputStream) in).read(buf, off, len);
      } else {
        int bb = in.read(bytebuf, off, len);
        if (debugOutputStream != null) {
          if (bb == -1) {
            debugOutputStream.close();
          } else if (bb > 0) {
            debugOutputStream.write(bytebuf, 0, bb);
            debugOutputStream.flush();
          }
        }
        int bb2 = bb;
  
        while (bb2-- > 0) {
          buf[bb2] = (char) ((int) bytebuf[bb2] & 0xFF);
        }
  
        //System.arraycopy(bytebuf, off, buf, off, bb);
        return bb;
      }
  
      //    LogWriter.getSystemLogWriter().println("EDR: bb=" + bb + ", len=" + len);
      //        for (int i=0; i < bb; i++) {
      //          LogWriter.getSystemLogWriter().println((char)buf[i] + "=" + Integer.toHexString(buf[i]));
      //        }
    } catch (EOFException eof) { 
      //$JL-EXC$
      //thrown by IAIK socket streams
      if (debugOutputStream != null) {
        debugOutputStream.close();
      }
      return -1;
    }
  }

  protected void moveData(int offsetStart, int offsetEnd, int size) throws Exception {
    if ((offsetStart > offsetEnd) || (offsetStart + size <= offsetEnd)) {
      System.arraycopy(buf, offsetStart, buf, offsetEnd, size);
    } else if (offsetStart + size > offsetEnd) {
      byte bbb[] = new byte[size];
      System.arraycopy(buf, offsetStart, bbb, 0, size);
      System.arraycopy(bbb, 0, buf, offsetEnd, size);
    }
  }

  protected int getBufLen() {
    return buf.length;
  }

  public void setLastChar(int value) {
    lastchar = value;
  }

  public int getLastChar() {
    return lastchar;
  }

  protected void fetchData() throws Exception {
    //    LogWriter.getSystemLogWriter().println("------------------------IS:fetchData");
    //    if (getBufLen() == 0) {
    //      resizeBuffer();
    //    } else {
    pos = 0;
    //    }
    a1 = getBufLen() - pos;
    a2 = readData(pos, a1);
    
    while (a2 == 0) {
      Thread.sleep(5);
      a2 = readData(pos, a1);
    }
    
    //    LogWriter.getSystemLogWriter().println("----------------------------------IS:fetchData: a1 = " + a1 + " a2 = " + a2);
    if (a2 == -1) {
      eos = true;
      restBytes = 0;
    } else {
      restBytes = a2;
    }
  }

  public int getRow() {
    return iRow;
  }

  public int getCol() {
    return iCol;
  }

  public CharArray getID() {
    return id;
  }

  public int getRestBytes() {
    return restBytes;
  }

  public boolean getLiteral() {
    return bLiteral;
  }

  public void setLiteral(boolean value) {
    bLiteral = value;
  }

  public boolean isFinished() {
    return (eos && (restBytes <= 0));
  }

  public InputStream getInputStream() { // UTF8Reader and UTF16Reader override it
    return in;
  }

  public int getSID() {
    return sid;
  }

  public void setSID(int i) {
    sid = i;
  }

  public void setReadRaw(boolean b) {
    bReadRaw = b;

    if (in instanceof ReaderInputStream) {
      bReadRaw = true;
    } else if (encoding instanceof UTF16Encoding) {
      bReadRaw = false;
    }
  }

  public int[] parseUTF8toUCS4(byte[] data, int off, int len) {
    res[0] = len;

    if (len == 6) {
      res[1] = (data[off] & 0x1) << 30;
      res[1] |= (data[off + 1] & 0x3F) << 24;
      res[1] |= (data[off + 2] & 0x3F) << 18;
      res[1] |= (data[off + 3] & 0x3F) << 12;
      res[1] |= (data[off + 4] & 0x3F) << 6;
      res[1] |= (data[off + 5] & 0x3F);
    } else if (len == 5) {
      res[1] = (data[off] & 0x3) << 24;
      res[1] |= (data[off + 1] & 0x3F) << 18;
      res[1] |= (data[off + 2] & 0x3F) << 12;
      res[1] |= (data[off + 3] & 0x3F) << 6;
      res[1] |= (data[off + 4] & 0x3F);
    } else if (len == 4) {
      res[1] = (data[off] & 0x7) << 18;
      res[1] |= (data[off + 1] & 0x3F) << 12;
      res[1] |= (data[off + 2] & 0x3F) << 6;
      res[1] |= (data[off + 3] & 0x3F);
    } else if (len == 3) {
      res[1] = (data[off] & 0xF) << 12;
      res[1] |= (data[off + 1] & 0x3F) << 6;
      res[1] |= (data[off + 2] & 0x3F);
    } else if (len == 2) {
      res[1] = (data[off] & 0x1F) << 6;
      res[1] |= (data[off + 1] & 0x3F);
    } else if (len == 1) {
      res[1] = data[off];
    }

    return res;
  }

  public int getUTF8CharCount(byte ch) {
    if ((ch & 0xFC) == 0xFC) {
      return 6;
    } else if ((ch & 0xF8) == 0xF8) {
      return 5;
    } else if ((ch & 0xF0) == 0xF0) {
      return 4;
    } else if ((ch & 0xE0) == 0xE0) {
      return 3;
    } else if ((ch & 0xC0) == 0xC0) {
      return 2;
    } else {
      return 1;
    }
  }

  public int[] res = new int[2];

  /*  public int[] parseUTF8toUCS4(byte[]data, int off, int len) {
   if ((data[off] & 0xC0) == 0xC0 && (data[off] & 0xE0) != 0xE0) { //first byte begins with 110 so there are two bytes
   //      LogWriter.getSystemLogWriter().println("data[off]=" + Integer.toHexString((int)data[off]) + " data[off+1]=" + Integer.toHexString((int)data[off+1]));
   res[0] = 2;
   res[1] = (data[off] & 0x1f)<<6;
   //      LogWriter.getSystemLogWriter().println("res[1]=" + (int)res[1]);
   res[1] |= data[off+1] & 0x3f;
   //      LogWriter.getSystemLogWriter().println("res[1]=" + (int)res[1]);
   } else {
   res[0] = 1;
   res[1] = data[off];
   }
   return res;
   }*/
  public void setName(CharArray name) {
    this.name = name.copy();
  }

  public CharArray getName() {
    return name;
  }

  public void setEncoding(CharArray enc) throws ParserException {
//    Thread.dumpStack();
    //    if (enc.equalsIgnoreCase("utf-8") || enc.length() == 0) {
    //      isUTF8 = true;
    //    } else {
    //      isUTF8 = false;
    //    }
    //LogWriter.getSystemLogWriter().println("Setting encoding to: "+ enc); 
    //Thread.dumpStack();
    if (in instanceof ReaderInputStream) {
      setReadRaw(true);
    } else if (encodingHandler.getEncoding(enc) != null) {
      encoding = encodingHandler.getEncoding(enc);
    } else if (enc != null && enc.length() > 0) {
//      encoding = encodingHandler.getEncoding(crUTF8);
//    } else {
      throw new com.sap.engine.lib.xml.parser.ParserException("Unrecognized encoding " + enc.toString(), iRow, iCol);
    }

    //    LogWriter.getSystemLogWriter().println("Setting encoding to: "+ encoding.getClass());
    //    if (encoding == null) {
    // The respective encoding has not been found and the parser
    // automatically switches to utf-8
    //      encoding = encodingHandler.getEncoding(crUTF8);
    //isUTF8 = true;
    //    }
  }

  public void reset(char[] oldChars, int length) {
    if (restBytes + length <= buf.length) {
      System.arraycopy(buf, pos, buf, length, restBytes);
      System.arraycopy(oldChars, 0, buf, 0, length);
      restBytes += length;
    } else {
      bufToRead = new char[length - pos];
      int l = buf.length - length;
      System.arraycopy(buf, pos + l, bufToRead, 0, bufToRead.length);
      System.arraycopy(buf, pos, buf, length, l);
      System.arraycopy(oldChars, 0, buf, 0, length);
      restBytes = buf.length;
    }
    iCol = 1;
    iRow = 1;
    pos = 0;
  }
  
  public void closeInputStream() throws IOException {
  	if(in != null) {
  		in.close();
  	}
    in = null;
  }
  
  public String toString() {
    StringBuffer ret = new StringBuffer();
    ret.append("EncodedDataReader #" + hashCode() + " { \r\n");
    
    ret.append("  encoding=" + encoding + "\r\n");
    ret.append("} \r\n");
    return ret.toString();
  }  
}

