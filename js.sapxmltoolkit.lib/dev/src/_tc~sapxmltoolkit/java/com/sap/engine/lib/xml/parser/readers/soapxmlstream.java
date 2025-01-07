package com.sap.engine.lib.xml.parser.readers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.xml.sax.InputSource;

import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.UTF8Encoding;
import com.sap.engine.lib.xml.parser.helpers.Encoding;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.IXMLStream;

public final class SOAPXMLStream implements IXMLStream {
  //max size of char arrays to be read
  //Aleksandar Aleksandrov 18.11.2004
  public static long MAX_SIZE = 100000;

  //protected AdvancedXMLStreamReader parent = null;
  protected int pos = 0;
  protected int ext = 0;
  public boolean eos = false;
  protected int a1, a2;
  protected int restBytes = 0;
  protected int totalRead = 0;
  protected CharArray id = null;
  protected CharArray name = null;
  //Location data
  //protected int iLevel = 0;
  protected int iRow = 1;
  protected int iCol = 1;
  //protected int iMarkRow = 0;
  //protected int iMarkCol = 0;
  protected boolean bLiteral = false;
  protected char lastchar;
  private int sid = -1;
  protected boolean bReadRaw = false;
  protected InputStream in = null;
  protected byte[] bytebuf = null;
  protected char[] buf = null;
  //protected byte[] bytebuf = null;
  //protected int len = -1;
  //protected char[]mapping = null;
  //protected boolean isUTF8 = true;
  protected int idx = 0;
  //protected int DEFAULT_EXT = 10000;
  protected int DEFAULT_EXT = 8192;
  private boolean specialEncoding = false;
  int x1;
  //byte utf8buf[] = new byte[10];
  //byte utf8buflen = 0;
  //int utf8left = 0;
  XMLParser xmlparser = null;
  private static EncodingHandler encodingHandler = new EncodingHandler();
  //private final static CharArray crUTF8 = new CharArray("utf-8");
//  protected  Encoding encoding = encodingHandler.getEncoding(UTF8Encoding.NAME);

  private boolean firstRead = true;
  //removed static declaration, because the UTF8Encoding class is not thread safe, and was causing
  //strange problems when used in multiple threads for parsing xml messages
  protected  Encoding encoding = new UTF8Encoding();

  public SOAPXMLStream() {
  }

  public SOAPXMLStream(XMLParser parent) {
  	this.xmlparser = parent;
  }

  public void reuse() {
  	pos = 0;
  	ext = DEFAULT_EXT;
  	eos = false;
  	restBytes = 0;
  	totalRead = 0;
  	iRow = 1;
  	iCol = 1;
  	bLiteral = false;
  	bReadRaw = false;
    firstRead = true;
  }

  //
  public char read() throws Exception {
  	if (pos == restBytes) {
  	  fetchData();
  	}
  
  	if (eos) {
  	  if (xmlparser != null) {
  		xmlparser.onDocumentEOF();
  	  }
        lastchar = 0;
  	  return 0;
  	}
  
  	lastchar = buf[pos];
  	pos++;
  	return lastchar;
  }
  

  public int readData(int off, int len) throws Exception {
  	int bb = in.read(bytebuf, off, len-6);
  	if (bb == -1) {
  		return -1;
  	}
  
  	int i=0;
  	int j=0;
  	char ch=0;
  	int x = 0;
    if (firstRead) {
      firstRead = false;
      handleBOM();
      i = pos;
      pos = 0;
      
    }
  	//for(; i<bb; i++) {
  	while (i < bb) {
  		x = bytebuf[i] & 0x0FF;
  		if ((x & 0x80) == 0 && specialEncoding == false) {
  			buf[j] = (char)x;
  		} else {
  			while ((ch=(char)encoding.process(bytebuf[i++])) == (char)Encoding.NEEDS_MORE_DATA) {
  				if (i==bb) {
  					bytebuf[i] = (byte)in.read();
  					bb++;					
  				}
  			}
  			i--;
  			if (ch == Encoding.UNSUPPORTED_CHAR) {
  				throw new UnsupportedEncodingException("XMLParser: Unsupported Character");
  			}
  			buf[j] = ch;
  		}
  		j++;
  		i++;
  	}
  
  	return j;
  }


  protected int getBufLen() {
	return buf.length;
  }

  public void setLastChar(int value) {
	lastchar = (char) value;
  }

  public char getLastChar() {
	//LogWriter.getSystemLogWriter().println("SOAP.getLastChar :" + (char)lastchar);
	return (char) lastchar;
  }

  protected void fetchData() throws Exception {
	pos = 0;
	a1 = getBufLen() - pos;
	a2 = readData(pos, a1);

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
	return null;
  }

  public int getSID() {
	return sid;
  }

  public void setSID(int i) {
	sid = i;
  }

  public void setReadRaw(boolean b) {
	bReadRaw = b;
  }

  public void setName(CharArray name) {
	this.name = name.copy();
  }

  public CharArray getName() {
	return name;
  }

  public void setEncoding(CharArray enc) {
    if (!getEncoding().equalsIgnoreCase(enc)) {
      encoding = encodingHandler.getEncoding(enc);
    }
  }

  public boolean scanByte(char b) throws Exception {
	if (lastchar == b) {
	  read();
	  return true;
	} else {
	  return false;
	}
  }

  public boolean scanChars(char[] b) throws Exception {
	int l = b.length;

	if (l > 0 && b[0] != getLastChar()) {
	  return false;
	}

	for (int i = 1; i < l; i++) {
	  if (b[i] != read()) {
		return false;
	  }
	} 

	read();
	return true;
  }

  public boolean scanString(String b) throws Exception {
	int l = b.length();

	if (l > 0 && b.charAt(0) != getLastChar()) {
	  return false;
	}

	for (int i = 1; i < l; i++) {
	  if (b.charAt(i) != read()) {
		return false;
	  }
	} 

	read();
	return true;
  }
  
  private void handleBOM() throws ParserException {
    int bom = checkBOM();
    
    switch (bom) {
      case BOM_NOBOM:
      case BOM_BM_UTF8:
      case BOM_NOBM_UTF8: {
        setEncoding(UTF8Encoding.NAME);
        break;
      }
      case BOM_BM_UTF16LE:
      case BOM_NOBM_UTF16LE: {
        specialEncoding = true;
        setEncoding(EncodingHandler.utf_16);
        break;
      }
      case BOM_BM_UTF16BE: {
        specialEncoding = true;
        setEncoding(EncodingHandler.utf_16be);
        break;
      }
      case BOM_EBCDIC: {
        specialEncoding = true;
        setEncoding(EncodingHandler.CP1047);
        break;
      }
      default: {
        if (pos + 2 < restBytes) {
          throw new ParserException("Unknown Byte-Order-Mark. XML MUST begin either with WhiteSpace or with '<?xml', and not: '" + getLastChar() + buf[pos++] + buf[pos++] + "'", getID(), 1, 1);
        }
      }
    }
  }

  public void addInputFromInputStream(InputStream in, CharArray id) throws Exception {
  	reuse();
  	this.in = in;
  
  	if (buf == null) {
  	  buf = new char[DEFAULT_EXT];
  	  bytebuf = new byte[DEFAULT_EXT];
  	}
  
  	ext = DEFAULT_EXT;
  	this.id = id;
  	eos = false;
    
  	read();
  }

  public void addInputSource(InputSource src) throws Exception {
	throw new Exception("not Supported");
  }

  public void addInputSource(InputSource src, CharArray id) throws Exception {
	throw new Exception("not Supported");
  }

  public void addInputFromCharArray(CharArray src, CharArray id) throws Exception {
	throw new Exception("not Supported");
  }

  public void addInputFromReader(Reader reader, CharArray id) throws Exception {
	throw new Exception("not Supported");
  }

  public void addInputFromEntity(Entity ent) throws Exception {
	throw new Exception("not Supported");
  }

  //public boolean getLiteral();
  public int getSourceID() {
	return 1;
  }

  public boolean isFinished(int a) {
	return false;
  }

  public void clearFinished(int a) {

  }

  int cds1;

  public void scanCharData(CharArray carr) throws Exception {
  	char cs = getLastChar();
  	carr.clear();
    int startPos = pos;
  
  	switch (cs) {
  	  case '<':
  	  case '&': {
  		return;
  	  }
  	  default: {
      carr.append(cs);
  	  }
  	}
    int i=0;
  	    do {
  	      if (pos == restBytes) {
            carr.append(buf, startPos, pos - startPos);
    		    fetchData();
            startPos = pos;
   	        if (eos) {
    		      if (xmlparser != null) {
  	 	          xmlparser.onDocumentEOF();
  		        }
  		        return;
  	        }
  	      }
          cs = buf[pos++];
  	    } while (cs != '<' && cs != '&' && (carr.length() < MAX_SIZE) && cs != 0);
        if (cs == 0) {
          throw new Exception("Invalid char 0x00 in input found.");
        }
  
  	lastchar = cs;
    carr.append(buf, startPos, pos-startPos-1);
  }
  
//  private char[] cn_data;
//  private int cn_off;
//  private int cn_i;
//  private int cn_len;

  public int checkName(CharArray chr) throws Exception  {
  	char [] cn_data = chr.getData();
  	int cn_off = chr.getOffset();
  	int cn_i = cn_off + 1;
  	int cn_len = chr.length() + cn_off;
    int colonChar = -1;
    if (cn_data[cn_off] != getLastChar())  {
      throw new ParserException("Document is not well-formed: Expected End-tag \'" + chr + "\', received:" + getLastChar() +", on position: 0", getID(), getRow(), getCol());
    }
    if (restBytes-pos-1 > chr.length())  {
      while (cn_i < cn_len)  {
        if (cn_data[cn_i] != buf[pos++])  {
          throw new ParserException("Document is not well-formed: Expected End-tag \'" + chr + "\', received:" + buf[pos-1] +", on position:" + cn_i, getID(), getRow(), getCol());
        }
        if (cn_data[cn_i] == ':') {
          colonChar = cn_i;
        }
        cn_i++;
      }
    } else  {
      while (cn_i < cn_len)  {
        if (pos == restBytes) {
          fetchData();
          if (eos) {
              throw new ParserException("Document EOF, while reading end tag: " + chr, getID(), getRow(), getCol());
          }
        }
        if (cn_data[cn_i] != buf[pos++])  {
            throw new ParserException("Document is not well-formed: Expected End-tag \'" + chr + "\', received:" + buf[pos-1] +", on position:" + cn_i, getID(), getRow(), getCol());
        }
        cn_i++;
      }
    }
    lastchar = read();
    return colonChar;
  }
  

  public int scanName(CharArray chr) throws Exception {
    int colonChar = -1;
    char ch;
    boolean notdone = true;
    int startPos = pos;

    if (Character.isLetter(lastchar) || lastchar == '_' || lastchar == ':') {
        chr.append(lastchar);
    } else {
    	throw new Exception("Bad start character of name");
    }

    //Character.isLetter(ch) || ch == '_' || ch == ':'    
    do {
      
      if (pos == restBytes) {
        chr.append(buf, startPos, pos - startPos);
        fetchData();
        startPos = pos;
        if (eos) {
            return colonChar;
        }
      }
      
      ch = buf[pos++];
  
      if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <='z') || (ch >= '0' && ch <= '9')) {
        continue;
      }
      switch (ch) { //$JL-SWITCH$ 
        case ' ': //$JL-SWITCH$ 
          case '>': //$JL-SWITCH$ 
            case '=': //$JL-SWITCH$ 
              case '\"': //$JL-SWITCH$ 
                case '\'': //$JL-SWITCH$ 
                  notdone = false; continue;
        case ':': //$JL-SWITCH$ 
          colonChar = chr.length() + pos - startPos -1; 
        case '_': //$JL-SWITCH$ 
          case '.': //$JL-SWITCH$ 
            case '-': //$JL-SWITCH$ 
              case '|': //$JL-SWITCH$ 
                continue;
      }
    
      if (!Symbols.isLetterOrDigit(ch)) {
        notdone = false;
      }
  
    } while (notdone);

    lastchar = ch;
    chr.append(buf, startPos, pos-startPos-1);
    return colonChar;
  }
  
  public boolean scanS() throws Exception {
    if (!Symbols.isWhitespace((char)lastchar)) {
      return false;
    }

  	char ch_scans;
    while ((ch_scans = read()) == 0x20 || ch_scans == 0xD || ch_scans == 0xA || ch_scans == 0x9);
    return true;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.parser.helpers.IXMLStream#getEncoding()
   */
  public CharArray getEncoding() {
    return encoding.getName();
  }  

  protected int checkBOM() {
    //int a = 0;
    char b = (char)(bytebuf[pos++]&0x00FF);

    if (b == 0x3C) {
      b = (char)(bytebuf[pos++]&0x00FF);
      if (b == 0) {
        b = (char)(bytebuf[pos++]&0x00FF);
        if (b == 0x3F) {
          b = (char)(bytebuf[pos++]&0x00FF);
          if (b == 0) {
            pos = 0;
            return BOM_NOBM_UTF16LE;
          }
        }
      }
      pos = 0;
      return BOM_NOBOM;
    } else if (b == 0xEF) {
      b = (char)(bytebuf[pos++]&0x00FF);
      if (b == 0xBB) {
        b = (char)(bytebuf[pos++]&0x00FF);
        if (b == 0xBF) {
          return BOM_BM_UTF8;
        }
      }
    } else if (b == 0xFF) {
      b = (char)(bytebuf[pos++]&0x00FF);
      if (b == 0xFE) {
        return BOM_BM_UTF16LE;
      }
    } else if (b == 0xFE) {
      b = (char)(bytebuf[pos++]&0x00FF);
      if (b == 0xFF) {
        return BOM_BM_UTF16BE;
      }
    } else if (b == 0) {
      b = (char)(bytebuf[pos++]&0x00FF);
      if (b == 0x3C) {
        b = (char)(bytebuf[pos++]&0x00FF);
        if (b == 0) {
          b = (char)(bytebuf[pos++]&0x00FF);
          if (b == 0x3F) {
            pos = 0;
            return BOM_NOBM_UTF16BE;
          }
        }
      }
      pos = 0;
      return BOM_NOBOM;
    } else {
      pos = 0;
      return BOM_NOBOM;
    }

    return BOM_UNKNOWN;
  }
  

	public void close() throws IOException {
		if(in != null) {
			in.close();
		}	
	}
}

