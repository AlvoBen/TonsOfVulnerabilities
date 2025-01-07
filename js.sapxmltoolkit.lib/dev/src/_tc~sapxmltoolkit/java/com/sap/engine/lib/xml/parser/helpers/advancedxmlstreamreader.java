package com.sap.engine.lib.xml.parser.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

import org.xml.sax.InputSource;

import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.ParserEOFException;
import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;
import com.sap.engine.lib.xml.parser.readers.EncodedDataReader;
import com.sap.engine.lib.xml.util.CharArrayInputStream;
import com.sap.engine.lib.xml.util.ReaderInputStream;
import com.sap.engine.lib.xsl.xpath.IntVector;
import com.sap.engine.lib.xsl.xslt.pool.ObjectPool;

/**
 * Class descriptiwon -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
public final class AdvancedXMLStreamReader implements IXMLStream {

  private Vector sourceStack = new Vector();
  private int sourcePtr = -1;
  private int _pk_b;
  private boolean sourceChanged = false;
  //private UTF8ReaderPool utf8ReaderPool = new UTF8ReaderPool(15, 10);
  //private CharArrayReaderPool fCharArrayReaderPool = new CharArrayReaderPool(15, 10);
  private ObjectPool encodedDataReaderPool = new ObjectPool(EncodedDataReader.class, 15, 10);
  private int _cs_ptr = -20;
  private EncodedDataReader _cs_src = null;
  public XMLParser xmlParser = null;
  private int currentSID = 0;
  private IntVector finishedSid = new IntVector();
  private boolean insertSpaces = false;
  private boolean insertTrailingSpace = false;
  private boolean insertLeadingSpace = false;
  private boolean utf16beDetected = false;
  
  private int lowSurrogate = -1;

  public AdvancedXMLStreamReader(XMLParser xmlParser) {
    this.xmlParser = xmlParser;
  }

  public AdvancedXMLStreamReader reuse(XMLParser xmlParser) {
    this.xmlParser = xmlParser;
    sourceStack.clear();
    sourcePtr = -1;
    sourceChanged = false;
    _cs_ptr = -20;
    _cs_src = null;
    currentSID = 0;
    finishedSid.clear();
    insertSpaces = false;
    insertTrailingSpace = false;
    insertLeadingSpace = false;
    encodedDataReaderPool.releaseAllObjects();
    return this;
  }

  private EncodedDataReader getCurrentSource() {
    if (sourcePtr == _cs_ptr) {
      return _cs_src;
    } else {
      _cs_src = (EncodedDataReader) sourceStack.get(sourcePtr);
      _cs_ptr = sourcePtr;
      return _cs_src;
    }
  }

  public boolean getLiteral() {
    return ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).getLiteral();
  }

  public void setLiteral(boolean value) {
    ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).setLiteral(true);
  }

  public int getRow() {
    return ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).getRow();
  }

  public int getCol() {
    return ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).getCol();
  }

  public char getLastChar() {
    return (char) _pk_b;
  }

  public void setLastChar(int value) {
    _pk_b = value;
  }

  public int loadLastChar() throws Exception {
    return read();
  }

  private void reset(char[] oldChars, int length) {
    ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).reset(oldChars, length);
  }

  public char read() throws Exception {
    if (lowSurrogate != -1) {
      char c = (char) lowSurrogate;
      lowSurrogate = -1;
      return c;
    }
    do {
      //      if (insertSpaces) {
      //        if (insertLeadingSpace) {
      //          insertLeadingSpace = false;
      //          insertTrailingSpace = true;
      //          insertSpaces = false;
      //          return ' ';
      //        } else if (insertTrailingSpace) {
      //          insertTrailingSpace = false;
      //          return ' ';
      //        } else {
      //          insertSpaces = false;
      //          onEncodedDataReaderEOF();
      //        }
      //      }
      //      if (_cs_src.eos == true) {
      //        onEncodedDataReaderEOF();
      //      }
      if (sourceChanged) {
        sourceChanged = false;
        _pk_b = ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).getLastChar();
      } else {
        _pk_b = ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).read();
      }
    } while (sourceChanged == true);

    //    if (getRow() > 4720 || getRow() == 1) {
    //    if (getRow() > 4720) {
    // LogWriter.getSystemLogWriter().println("AXR.read(): " + (char)_pk_b + "=" + Integer.toHexString((int)_pk_b));
    //      LogWriter.getSystemLogWriter().println("AXR.read(): " + Integer.toHexString((int)_pk_b));
    //      LogWriter.getSystemLogWriter().println("AXR.read(): " + (char)_pk_b);
    //    }
    if (_pk_b >= 0x10000 && _pk_b <= 0x10FFFF) { //SASHO Unicode surrogates
      lowSurrogate = (_pk_b - 0x10000) % 0x400 + 0xdc00;
      _pk_b = (char) ((_pk_b - 0x10000) / 0x400 + 0xd800); 
    }
    return (char) _pk_b;
  }

  public boolean scanByte(char b) throws Exception {
    if (getLastChar() == b) {
      loadLastChar();
      return true;
    } else {
      return false;
    }
  }

  public boolean scanChars(char[] b) throws Exception {
    int l = b.length;

    for (int i = 0; i < l; i++) {
      if (b[i] != (char) _pk_b) {
        return false;
      }

      read();
    } 

    return true;
  }

  public boolean scanString(String b) throws Exception {
    int l = b.length();

    for (int i = 0; i < l; i++) {
      if (b.charAt(i) != _pk_b) {
        return false;
      }

      read();
    } 

    return true;
  }

  private void addSource(EncodedDataReader src) throws ParserException {
    //    if (sourcePtr > -1) {
     //LogWriter.getSystemLogWriter().println(">>>>>>>>>>>>>>>   AXR-adding source: " + src.getID());
//     try {
//       LogWriter.getSystemLogWriter().println(">>>>>>>>>>>>>>>   AXR-adding  currentSource=" + getCurrentSource().getID());
//     } catch (RuntimeException e) {
//     }
//     Thread.dumpStack();
    //    }
    for (int i = 1; i < sourceStack.size(); i++) { //Skip the first one, because some customers set the source systemID to the DTD. See DTDSystemID ATS test
      if (((EncodedDataReader) sourceStack.get(i)).getID().equals(src.getID())) {
        throw new ParserException("XMLError: You cannot read the same entity 2 times (recursion!!!)", getID(), getRow(), getCol());
      }
    } 

    if (sourcePtr > -1) {
      getCurrentSource().setLastChar(getLastChar());
      src.setLiteral(((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).getLiteral()); // for literal values in text
    }

    src.setSID(currentSID++);
    sourceStack.add(src);
    sourcePtr++;
  }

  public void addInputFromFile(CharArray fname, CharArray id) throws FileNotFoundException, Exception {
    //    FileInputStream fin = new FileInputStream(fname.toString());
    //    boolean done = false;
    //    try {
    //      if ((fin.read() == (int) 0xFF) && (fin.read()== (int) 0xFE)) {
    //        addSource(new UTF16Reader(this, fin, fname));
    //        done = true;
    //      }
    //    } catch (IOException e) {
    //    }
    //    if (done == false) {
    //      fin.close();
    //fin = new FileInputStream(fname.toString());
    InputStream in = null;
    try {
      URL url = new URL(fname.toString());
      in = url.openStream();
    } catch (Exception e) {
      //$JL-EXC$
      //e.printStackTrace();
      in = new FileInputStream(fname.toString());
    }

      addInputFromInputStream(in, id);
      //addInputFromInputStream(new FileInputStream(fname.toString()), id);
    //addSource(((EncodedDataReader)encodedDataReaderPool.getObject()).reuse(this, fin, fname));
    //    }
    //read();
  }

  public void addInputFromCharArray(CharArray src, CharArray id) throws Exception {
    //CharArrayInputStream cain = ((CharArrayInputStream)fCharArrayInputStreamPool.getObject()).reuse(src);
    CharArrayInputStream cain = new CharArrayInputStream(src);
    addInputFromInputStream(cain, id);
  }

  private Random rand = new Random();

  public void addInputSource(InputSource src) throws Exception {
    String systemId = src.getSystemId();

    if (systemId == null) {
      systemId = Integer.toString(rand.nextInt());
    }

    addInputSource(src, new CharArray(systemId));
  }

  public void addInputSource(InputSource src, CharArray id) throws Exception {
    if (src.getCharacterStream() != null) {
      addInputFromReader(src.getCharacterStream(), id);
    } else if (src.getByteStream() != null) {
      addInputFromInputStream(src.getByteStream(), id);
    } else if (src.getSystemId() != null) {
      addInputFromFile(new CharArray(src.getSystemId()), id);
    }
  }

  public void addInputFromInputStream(InputStream is, CharArray id) throws Exception {
    //    BufferedInputStream bin = new BufferedInputStream(is);
    //    byte sig[] = new byte[2];
    //    bin.mark(2);
    //    if (bin.read(sig, 0, 2) == 2 && sig[0] == (byte)0xFF && sig[1] == (byte)0xFE) {
    //      addSource(new UTF16Reader(this, bin, id));
    //    } else {
    //      bin.reset();
    addSource(((EncodedDataReader) encodedDataReaderPool.getObject()).reuse(this, is, id));
    //addSource( new EncodedDataReader().reuse(this, is, id)   );
    //    }
    setReadRaw(true);
    read();
    int bom = checkBOM();

    //    LogWriter.getSystemLogWriter().println("BOM is :" + bom);
    switch (bom) {
      case BOM_NOBOM:
      case BOM_BM_UTF8:
      case BOM_NOBM_UTF8: {
        setEncoding(UTF8Encoding.NAME);
        break;
      }
      case BOM_BM_UTF16LE:
      case BOM_NOBM_UTF16LE: {
        setEncoding(EncodingHandler.utf_16);
        break;
      }
      case BOM_BM_UTF16BE:
      case BOM_NOBM_UTF16BE: {
        utf16beDetected = true;
        setEncoding(EncodingHandler.utf_16be);
        break;
      }
      case BOM_EBCDIC: {
        //LogWriter.getSystemLogWriter().println("AXR: CP1047");
        setEncoding(EncodingHandler.CP1047);
        //setLastChar('<');
        break;
      }
      default: {
        throw new ParserException("Unknown Byte-Order-Mark. XML MUST begin either with WhiteSpace or with '<?xml', and not: '" + getLastChar() + read() + read() + "'", getID(), 1, 1);
      }
    }

    setReadRaw(false);
    if (bom != BOM_NOBOM /*&& bom != BOM_EBCDIC*/) {
      read();
    }
  }

  //  public void addInputFromInputStream(InputStream is, CharArray id, int len) throws Exception {
  //    BufferedInputStream bin = new BufferedInputStream(is);
  //    byte sig[] = new byte[2];
  //    bin.mark(2);
  //
  //    if (bin.read(sig, 0, 2) == 2 && sig[0] == (byte)0xFF && sig[1] == (byte)0xFE) {
  //      addSource(new UTF16Reader(this, bin, id, len));
  //    } else {
  //      bin.reset();
  //      addSource(utf8ReaderPool.getObject().reuse(this, bin, id, len));
  //    }
  //
  //    read();
  //  }
  public void addInputFromEntity(Entity ent) throws Exception {
    if (ent.isInternal()) {
//       LogWriter.getSystemLogWriter().println(">>>>>>>>>>>>>>>>>>>>>   Adding input from internal enitity: " + ent.getName() + "=" + ent.getValue());
      addInputFromCharArray(ent.getValue(), ent.getName());
    } else {
      if (xmlParser != null) {
        //        LogWriter.getSystemLogWriter().println("Resolved url:" + external);
        if (!xmlParser.getExternalGeneralEntities()) {
          xmlParser.docHandler.onCustomEvent(DocHandler.SKIPPED_ENTITY, ent.getName());
        } else {
//          LogWriter.getSystemLogWriter().println(">>>>>>>>>>>>>>>>>   AdvamcedXMLStreamReader.addInputFromEntity: Adding input from enitity: " + ent.getName() + ", pub=" + ent.getPub().toString() + ", sys=" + ent.getSys().toString() );
          
          InputSource isource = null;

          if (xmlParser.getEntityResolver() != null) {
            URL url = xmlParser.urlLoader.loadAndPush(ent.getSys().toString());
            isource = xmlParser.getEntityResolver().resolveEntity(ent.getPub().toString(), ent.getSys().toString());

            //          LogWriter.getSystemLogWriter().println("AXR.Addinput from ent: " + isource);
            if (isource == null) {
              //URL url = xmlParser.urlLoader.loadAndPush(ent.getSys().toString());
              InputStream urlStream = null;
              try {
                urlStream = url.openStream();
              } catch (IOException ioe) {
                //$JL-EXC$
                String systemId = url.toString();
//                LogWriter.getSystemLogWriter().println("AdvamcedXMLStreamReader.addInputFromEntity: systemID = " + systemId);
                //trying to load DTD from classloader - fix for petstore
                if (systemId.startsWith("http://localhost/")) {
                  ClassLoader loader = Thread.currentThread().getContextClassLoader();
                  String resourceName = systemId.substring(17); // "http://localhost/".length
//                  LogWriter.getSystemLogWriter().println("AdvamcedXMLStreamReader.addInputFromEntity: loader = " + loader + ", resource = " + resourceName);
                  urlStream = loader.getResourceAsStream(resourceName);
                  if (urlStream == null) {
                    throw new IOException("Failed to load resource from the context classloader of the current thread! Loading from classloader was caused by: " + ioe.toString());
                  }
                } else {
                  throw ioe;
                }
              }
              
              isource = new InputSource(urlStream);
              isource.setSystemId(url.toString());
              //            LogWriter.getSystemLogWriter().println("AXR.Addi2222nput from ent: " + isource + " " + isource.getSystemId());
            }
          } else {
            URL external = xmlParser.urlLoader.loadAndPush(ent.getSys().toString());
            isource = xmlParser.urlLoader.resolveEntity(ent.getPub().toString(), ent.getSys().toString());
          }

          //xmlParser.((getEntityResolver()==null)?urlLoader:getEntityResolver).resolveEntity();
          //addInputSource(isource, ent.getName());
          addInputSource(isource, ent.getSys());
          //ent.getSys()
          //addInput((xmlParser.getEntityResolver() == null)?xmlParser.urlLoader:xml
          //addInputFromInputStream(external.openStream(), ent.getName());
        }
      } else {
        addInputFromFile(ent.getSys(), ent.getName());
      }
    }

    getCurrentSource().setName(ent.getName());
  }

  //URL url = parent.urlLoader.
  public void addInputFromReader(Reader reader, CharArray id) throws Exception {
    addInputFromInputStream(new ReaderInputStream(reader), id);
  }

  //  public void addInputFromReader(Reader reader, CharArray id, int len) throws Exception {
  //    addInputFromInputStream(new ReaderInputStream(reader), id, len);
  //  }
  public void onEncodedDataReaderEOF() throws Exception {
    //      EncodedDataReader a = ((sourcePtr==_cs_ptr)? _cs_src : getCurrentSource());
    //    LogWriter.getSystemLogWriter().println("\nAXR-finishing source 1: " );
    //    if (insertTrailingSpace) {
    //      insertSpaces = true;
    //      insertTrailingSpace = true;
    //      insertLeadingSpace = false;
    //      return;
    //    }
    //    LogWriter.getSystemLogWriter().println("\nAXR-finishing source 2: " );
    EncodedDataReader finishingEncodedDataReader = ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource());

//    LogWriter.getSystemLogWriter().println(">>>>>>>>>>>>>>>>>>    AXR:onEncodedDataReaderEOF: "+ finishingEncodedDataReader.getID());
    if (xmlParser != null) {
      xmlParser.onEncodedDataReaderEOF(finishingEncodedDataReader);
    }

    finishedSid.add(finishingEncodedDataReader.getSID());

    if (sourcePtr == 0) {
      encodedDataReaderPool.releaseObject(finishingEncodedDataReader);

      if (xmlParser != null) {
        xmlParser.onDocumentEOF();
      }
    } else {
      do {
        //        EncodedDataReader finishingEncodedDataReader = ((sourcePtr==_cs_ptr)? _cs_src : getCurrentSource());
        //        xmlParser.onEncodedDataReaderEOF(finishingEncodedDataReader);
        InputStream streamToClose = finishingEncodedDataReader.getInputStream();

        if (streamToClose != null) {
          if (xmlParser != null && xmlParser.getCloseStreams()) {
            streamToClose.close();
          }
        }

        //        if (((sourcePtr==_cs_ptr)? _cs_src : getCurrentSource()) instanceof CharArrayReader) {
        //          fCharArrayInputStreamPool.releaseObject(((sourcePtr==_cs_ptr)? _cs_src : getCurrentSource()));
        encodedDataReaderPool.releaseObject(((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()));
        sourceStack.removeElementAt(sourcePtr);
        sourcePtr--;
        sourceChanged = true;
      } while (sourcePtr >= 0 && ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).isFinished());

      if (sourcePtr < 0) {
        throw new ParserEOFException("End of EncodedDataReader: " + ((sourcePtr == _cs_ptr) ? _cs_src : getCurrentSource()).getID() + " reached.");
      }
    }
  }

  public int getPtr() {
    return sourcePtr;
  }

  public boolean isFinished(int sid) {
    return finishedSid.contains(sid);
  }

  public void clearFinished(int sid) {
    finishedSid.removeElement(sid);
  }

  public int getSourceID() {
    return getCurrentSource().getSID();
  }

  public void insertSpaces() {
    insertLeadingSpace = true;
    insertTrailingSpace = false;
    insertSpaces = true;
  }

  public void setEncoding(CharArray enc) throws ParserException {
    if (utf16beDetected && (enc.toLowerCase().equals("utf-16") || enc.toLowerCase().equals("utf16"))) {
      getCurrentSource().setEncoding(new CharArray("utf-16be"));
    } else if (enc.toLowerCase().equals("iso-10646-ucs-2")) {
      if (utf16beDetected) {
        getCurrentSource().setEncoding(EncodingHandler.ISO_10646_UCS_2_BE);
      } else {
        getCurrentSource().setEncoding(EncodingHandler.ISO_10646_UCS_2_LE);
      }
    } else {
      getCurrentSource().setEncoding(enc);
    }
  }

  public void setReadRaw(boolean value) {
    getCurrentSource().setReadRaw(value);
  }

  protected int checkBOM() throws Exception {
    char b = getLastChar();

    if (b == 0x3C) {
      char[] oldChars = new char[4];
      int length = 0;
      oldChars[length++] = b;
      b = read();
      oldChars[length++] = b;
      /*if (b == 0x3F) {
        b = (byte)read();
        if (b == 0x78) {
          b = (byte)read();
          if (b == 0x6D) {
            read();
            return BOM_NOBM_UTF8;
          }
        }
      } else */if (b == 0) {
        b = read();
        oldChars[length++] = b;
        if (b == 0x3F) {
          b = read();
          oldChars[length++] = b;
          if (b == 0) {
       //            b = (byte)read();
            reset(oldChars, length); //The characters need to be read again. They are <? encoded into utf-16le
            return BOM_NOBM_UTF16LE;
          }
        }
      }
      reset(oldChars, length); //The characters may need to be read again. For example in UTF-8 encoding
      read();
      return BOM_NOBOM;
    } else if (b == 0xEF) {
      b = read();
      if (b == 0xBB) {
        b = read();
        if (b == 0xBF) {
          //read();
          return BOM_BM_UTF8;
        }
      }
    } else if (b == 0xFF) {
      b = read();
      if (b == 0xFE) {
        return BOM_BM_UTF16LE;

//        b = (byte)read();
//        if (b != 0) {
//          return BOM_BM_UTF16LE;
//        } else {
//          b = (byte)read();
//          if (b == 0) {
//            read();
//            return BOM_BM_UCS4LE;
//          }
//        }
      }
    } else if (b == 0xFE) {
      b = read();
      if (b == 0xFF) {
        return BOM_BM_UTF16BE;
      }
    } else if (b == 0) {
      char[] oldChars = new char[4];
      int length = 0;
      oldChars[length++] = b;
      b = read();
      oldChars[length++] = b;
      if (b == 0x3C) {
        b = read();
        oldChars[length++] = b;
        if (b == 0) {
          b = read();
          oldChars[length++] = b;
          if (b == 0x3F) {
//             b = (byte)read();
            reset(oldChars, length); //The characters need to be read again. They are <? encoded into utf-16le
            return BOM_NOBM_UTF16BE;
          }
        }
      }
      reset(oldChars, length); //The characters may need to be read again. For example in UTF-8 encoding
      read();
      return BOM_NOBOM;
    } else if (b == 0x4C) {
      char[] oldChars = new char[4];
      int length = 0;
      oldChars[length++] = b;
      b = read();
      oldChars[length++] = b;
      b = read();
      oldChars[length++] = b;
      b = read();
      oldChars[length++] = b;
      if (oldChars[0] == 0x4c && oldChars[1] == 0x6f && oldChars[2] == 0xa7 && oldChars[3] == 0x94) {
        reset(oldChars, length);
        return BOM_EBCDIC;
      } else {
        reset(oldChars, length-1); //The characters may need to be read again. For example in UTF-8 encoding
        read();
        return BOM_NOBOM;
      }
    } else {
      return BOM_NOBOM;
    }

    return BOM_UNKNOWN;
  }

  public CharArray getID() {
    return getCurrentSource().getID();
  }
  
  public boolean scanS() throws Exception {
    if (!Symbols.isWhitespace((char) _pk_b)) {
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
    return ((EncodedDataReader)getCurrentSource()).encoding.getName();
  }    

  public void close() throws IOException {
		EncodedDataReader currentDataReader = getCurrentSource();
		if(currentDataReader != null) {
			currentDataReader.closeInputStream();
		}
  }

}

