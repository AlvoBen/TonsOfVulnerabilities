package com.sap.engine.lib.xml.parser.helpers;

import java.io.*;
import org.xml.sax.InputSource;

public interface IXMLStream {
  
  public final static int BOM_NOBM_UTF8 = 1;
  public final static int BOM_NOBM_UTF16LE = 2;
  public final static int BOM_BM_UTF8 = 3;
  public final static int BOM_BM_UTF16LE = 4;
  public final static int BOM_BM_UCS4LE = 5;
  public final static int BOM_EBCDIC = 6;
  public final static int BOM_BM_UTF16BE = 7;
  public final static int BOM_NOBM_UTF16BE = 8;
  public final static int BOM_UNKNOWN = -1;
  public final static int BOM_NOBOM = 0;  

  public char read() throws Exception;


  public char getLastChar() throws Exception;


  public CharArray getID();


  public int getRow();


  public int getCol();


  public void setLastChar(int ch);


  public void setLiteral(boolean b);


  public void setReadRaw(boolean b);


  public void setEncoding(CharArray c) throws com.sap.engine.lib.xml.parser.ParserException;


  public boolean scanByte(char ch) throws Exception;


  public boolean scanChars(char[] ch) throws Exception;


  public boolean scanString(String s) throws Exception;


  public void addInputFromInputStream(InputStream in, CharArray id) throws Exception;


  public void addInputSource(InputSource src) throws Exception;


  public void addInputSource(InputSource src, CharArray id) throws Exception;


  public void addInputFromCharArray(CharArray src, CharArray id) throws Exception;


  public void addInputFromReader(Reader reader, CharArray id) throws Exception;


  public void addInputFromEntity(Entity ent) throws Exception;


  public boolean getLiteral();


  public int getSourceID();


  public boolean isFinished(int a);


  public void clearFinished(int a);
  public boolean scanS() throws Exception;
  public CharArray getEncoding();

  public void close() throws IOException;
}

