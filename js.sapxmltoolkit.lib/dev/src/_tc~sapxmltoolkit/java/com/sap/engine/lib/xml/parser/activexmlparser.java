package com.sap.engine.lib.xml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.parser.handlers.EmptyDocHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.CharArrayStack;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader;
import com.sap.engine.lib.xsl.xslt.InternalAttribute;
import com.sap.engine.lib.xsl.xslt.InternalAttributeList;

/**
 *    This class is called ActiveXMLParser because it allows a certain kind of
 *  user interaction with the parsing process. The user takes responsibility
 *  to carry out the parsing to its end by consecutive invokations of the
 *  next() method.
 *    An instance of this class is obtained with an InuptSource object as a
 *  constructor agument. Before next() can be called it is necessary to
 *  initialize the parser through invocation of begin(). When next() returns
 *  EOF it is consistent (and expected) to invoke end(). When the parser is in
 *  its END states it is consistent to invoke getLocalName, getUri and
 *  getQName. These methods can be called also in the START state in addition
 *  to the getAttributes method. In the CHARS state it is only consistent to
 *  call getValue().
 *  Here is an of the possible use of this class:
 *
 *  <code>
 *  File xml = new File("c:/some_dir/some_xml_file.xml");
 *  ActiveXMLParser r = new ActiveXMLParser(new FileInputStream(xml));
 *
 *  int i;
 *  while ((i = r.next()) != ActiveXMLParser.EOF) {
 *    switch(i) {
 *      case ActiveXMLParser.START: {
 *        // do something
 *        break;
 *      }
 *
 *      case ActiveXMLParser.END: {
 *        // do something else
 *        break;
 *      }
 *      case ActiveXMLParser.CHARS: {  // prints charData sections
 *        // do a third thing
 *        break;
 *      }
 *
 *
 *  </code>
 *  A mush slower alternative (usually two to three times) is the class
 *  com.sap.engine.lib.jaxrpc.impl0.XMLReaderImpl0.
 *
 *  @author Bogomil Kovachev, bogosmith@yahoo.com
 *  @version January 2002
 */
public final class ActiveXMLParser extends EmptyDocHandler implements XMLParserConstants, XMLTokenReader {

  /*  A XMLReader is in this state before it is initialized (with begin()).*/
  public static final int NEWBORN = -1;
  /* The initial state of a XMLReader.*/
  public static final int INITIAL = 0;
  /* The state denoting the start tag of an element.*/
  public static final int START = 1;
  /* The state denoting the end tag of an element.*/
  public static final int END = 2;
  /* The state denoting the character content of an element.*/
  public static final int CHARS = 3;
  /* The state denoting a processing instruction.*/
  public static final int PI = 4;
  /* The state denoting that the end of the document has been reached.*/
  public static final int COMMENT = 5;
  /* The state denoting that the end of the document has been reached.*/
  public static final int EOF = 6;
  /**
   * If a tag is an opening and a closing tag simultaneously
   * i.e. something like this: <sometag atr1="val" atr2="val"/>,
   * then XMLParser.checkMarkup() uses only one step to pass through it.
   * Therefore it is necessary in such cases to simulate a START state,
   * when the parser actually has finished reading the element.
   */
  private boolean shouldFakeEnd = false;
  
  private boolean normalize = false;
  private boolean wasEnd = false;
  /* This is the number of attributes on the current element. */
  private int attributeCount = 0;
  /* <?xml is allowed only in the first Processing Instruction */
  private boolean textDeclAllowed = true;
  private boolean endOfRootReached = false;
  /* The InputStream from which the XML document is read. */
  private InputStream xmlInputStream = null;
  private InputSource xmlInputSource = null;
  private Reader      xmlReader      = null;
  /* The current state of the reader. */
  private int currentState = NEWBORN;
  /* The parser ... it parses. */
  private XMLParser parser = null;
  /* Another DocHandler, whose on methods will be invoked. */
  private DocHandler docHandler = null;
  /* Hashtable with namespace mappings */
  private Hashtable prefixMapping;

  private CharArray uri = null;
  private CharArray localName = null;
  private CharArray qName = null;
  private static final char XPATH_SEPARATOR = '/';
  private boolean append = false;
  /* Contains the current character data. */
  private CharArray charData = new CharArray();

  /* Contains the current comment data. */
  private CharArray commentData = new CharArray();

  /* This Vector stores the attributes of the current element. */
  //private AttributesImpl attribs = new AttributesImpl();
  private InternalAttributeList attribs = new InternalAttributeList();
  private boolean firstElementPassed = false;
  private int depthReached = 0;
  private String location;
  private boolean hasDTD = false;

  public ActiveXMLParser(InputSource xmlInputSource) {
    this();
    this.xmlInputSource = xmlInputSource;
  }

  public ActiveXMLParser(XMLParser parser) {
    this();
    this.parser = parser;

  }

  public ActiveXMLParser(InputStream xmlInputStream) {
    this();
    this.xmlInputStream = xmlInputStream;
  }

  public ActiveXMLParser(Reader xmlReader) {
    this();
    this.xmlReader = xmlReader;
  }


  public ActiveXMLParser() {
    prefixMapping = new Hashtable();
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocation() {
    return(location);
  }

  public void reuse(InputStream stream,InputSource source, Reader reader) {
    this.xmlInputStream = stream;
    this.xmlInputSource = source;
    this.xmlReader = reader;
    //    prefixMapping.clear();
    //  	firstElementPassed = false;
    //  	depthReached = 0;
    //  	shouldFakeEnd = false;
    //  	attributeCount = 0;
    //		textDeclAllowed = true;
    //  	endOfRootReached = false;
    //		currentState = NEWBORN;
  }

  public void init(InputStream stream) {
    reuse(stream,null,null);
  }
  
  

  public void init(InputSource input) {
    reuse(null,input,null);
    
  }

  public void init(Reader reader) {
    reuse(null,null,reader);    
  }

  public void setDocHandler(DocHandler docHandler) {
    this.docHandler = docHandler;
  }


  /**
   *  Commences the parsing.
   *
   * @exception   ParserException
   */
  public int begin() throws ParserException {
    try {
      parser = getParser();
      //      if (parser == null) {
      //        InputSource source = new InputSource(xmlInputStream);
      //        parser.activeParse(source, this);
      //      } else {
      this.firstElementPassed = false;
      this.shouldFakeEnd = false;
      this.endOfRootReached = false;


      if (xmlInputSource == null) {
        if (xmlReader != null) {
          xmlInputSource = new InputSource(xmlReader);
        }
        if (xmlInputStream != null) {
          xmlInputSource = new InputSource(xmlInputStream);
        }

      }

      //InputSource source = new InputSource(xmlInputStream);
      parser.activeParse(xmlInputSource, this);
      //      }
      currentState = INITIAL;
      prefixMapping.clear();
      return currentState;
    } catch (Exception e) {
      throw new ParserException(e);
    }
  }

  //  public void reuse(InputStream xmlInputStream) throws ParserException {
  //    this.xmlInputStream = xmlInputStream;
  //  }
  /**
   *  Forces the parser to make a forward step in the parsing process.
   *
   * @return the next state of the parser
   */
  public int next() throws ParserException {
    try {
    	if (wasEnd) {
    		charData.clear();
    	}
      if (endOfRootReached) {
        parser.scanS();
      }

      //      if(!firstElementPassed) {
      //        firstElementPassed = true;
      //        return START;
      //      }

      if (shouldFakeEnd) {
        shouldFakeEnd = false;
        currentState = END;
        //        shouldStacksBePopped = true;
        return END;
      }

      if (parser.getDocFinished()) {
        currentState = EOF;
        parser.finalizeActiveParse();
        return EOF;
      }

      //      if (shouldStacksBePopped) {
      //        uris.pop();
      //        localNames.pop();
      //        qNames.pop();
      //        shouldStacksBePopped = false;
      //      }
      int i = parser.checkMarkup();

      switch (i) {
        case M_PI: {
          parser.scanPI(textDeclAllowed);
          textDeclAllowed = false;
          return PI;
        }
        case M_LT: {
          parser.scanElement();
          return START;
        }
        case M_COMMENT: {
          parser.scanComment();
          return COMMENT;
        }
        case M_CHDATA: {
          //          if(endOfRootReached ){
          //            throw new ParserException("Character data not allowed here!", -1, 0);
          //          }
          parser.scanCharData();
          return CHARS;
        }
        case M_ENDEL: {
          parser.scanEndTag();

          //          if ( qNames.size() < 2) {
          if (depthReached < 1) {
            endOfRootReached = true;
            parser.finalizeActiveScan();
          }

          //          shouldStacksBePopped = true;
          return END;
        }

        case M_CONTREF: {
          parser.handleContentReference(false);
          //Reference ref = parser.scanReference();
          return CHARS;
        }

        case M_DOCTYPE: {
          parser.scanDTD();
          return next();
        }
        
        case M_CDSECT: {
        	parser.scanCDSect();
        	return CHARS;
        }

        default: {
          throw new ParserException(" Unrecognized case: " + i, -1, 0);
        }
      }
    } catch (ParserException e) {
      throw e;
    } catch (Exception e) {
      throw new ParserException(e);
    }
  }

  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {
    //LogWriter.getSystemLogWriter().println("ActiveXMLParser startElement " + localName);
    if (docHandler != null) { docHandler.startElementStart(uri, localName, qname); }
    this.uri = uri;
    this.localName = localName;
    this.qName = qname;
    attributeCount = 0;
    attribs.clear();
    charData.clear();
    depthReached++;
    wasEnd = false;
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    if (docHandler != null) {docHandler.addAttribute(uri, prefix, localName, qname, type, value);}
    //attribs.addAttribute(uri.toString(), localName.toString(), qname.toString(), type, value.toString());
    //LogWriter.getSystemLogWriter().println("ActiveXMLParser ... Adding attribute: uri=" + uri + " prefix=" + prefix +  " qname=" + qname + " localName=" + localName + " value=" + value);
    attribs.addAttribute(uri, prefix, qname, localName, value);
  }

  public void startElementEnd(boolean isEmpty) throws Exception {
    if (docHandler != null){docHandler.startElementEnd(isEmpty);}
    currentState = START;

    if (isEmpty) {
      shouldFakeEnd = true;
    }

    return;
  }



  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {
    if (docHandler != null){docHandler.endElement(uri, localName, qname, isEmpty);}
    this.uri = uri;
    this.localName = localName;
    this.qName = qname;

    if (isEmpty == false) {
      currentState = END;
    }
		wasEnd = true;
    depthReached--;
    return;
  }

  public void startDocument() throws Exception {
    if (docHandler != null){docHandler.startDocument();}
    currentState = INITIAL;
    return;
  }

  public void endDocument() throws Exception {
    if (docHandler != null){docHandler.endDocument();}
    currentState = EOF;
    return;
  }

  public void charData(CharArray carr, boolean bDisableOutputEscaping) throws Exception {
    if (docHandler != null){docHandler.charData(carr, bDisableOutputEscaping);}
    if (append || normalize) {
      charData.append(carr);
    } else {
      charData = carr;
    }
    currentState = CHARS;
    return;
  }

  /* Unaware of, curretly! */
  public void onXMLDecl(String version, String encoding, String ssdecl) throws Exception {
    if (docHandler != null){docHandler.onXMLDecl(version, encoding, ssdecl);}
    return;
  }

  /* Unaware of, curretly! */
  public void onPI(CharArray target, CharArray data) throws Exception {
    if (docHandler != null){docHandler.onPI(target, data);}
  }

  public void onComment(CharArray text) throws Exception {
    if (docHandler != null){docHandler.onComment(text);}
//    if (append || normalize) {
//      charData.append(text);
//    } else {
//      charData = text;
//    }
//    currentState = COMMENT;
		commentData = text;
  	currentState = COMMENT;
    return;
  }
  /* Unaware of, curretly! */
  public void onCDSect(CharArray text) throws Exception {
    if (docHandler != null){docHandler.onCDSect(text);}
  }

  public void onDTDElement(CharArray name, CharArray model) throws Exception {
    if (docHandler != null){docHandler.onDTDElement(name, model);}
  }

  public void onDTDAttListStart(CharArray name) throws Exception {
    if (docHandler != null) {docHandler.onDTDAttListStart(name);}
  }

  public void onDTDAttListItem(CharArray name, CharArray attname, String type, String defDecl, CharArray vAttValue, String note) throws Exception {
    if (docHandler != null) {docHandler.onDTDAttListItem(name, attname, type, defDecl, vAttValue, note);}
  }

  public void onDTDAttListEnd() throws Exception {
    if (docHandler != null) {docHandler.onDTDAttListEnd();}
  }

  public void onDTDEntity(Entity entity) throws Exception {
    if (docHandler != null){docHandler.onDTDEntity(entity);}
  }

  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) throws Exception {
    if (docHandler != null){docHandler.onDTDNotation(name, pub, sys);}
  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {
    hasDTD = true;
    if (docHandler != null){docHandler.startDTD(name, pub, sys);}
  }

  public void endDTD() throws Exception {
    if (docHandler != null){docHandler.endDTD();}
  }

  public void onContentReference(Reference ref) throws Exception {
    if (docHandler != null){docHandler.onContentReference(ref);}
  }

  /**
   *  Should be called after the contents of the whole document are read.
   */
  public void end() throws ParserException {
    if (this.currentState != EOF) {
      throw new ParserException("EOF not reached yet!", 0, 0);
    }

    try {
      parser.finalizeActiveParse();
    } catch (Exception e) {
      throw new ParserException(e.toString(), 0, 0);
    }
    location = null;
    hasDTD = false;
  }

  public boolean hasDTD() {
    return(hasDTD);
  }

  public int getState() {
    return currentState;
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current qualified name
   */
  public String getQName() {
    //    return qNames.peek().toString();
    return qName.toString();
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current qualified name as a CharArray
   */
  public CharArray getQNameCharArray() {
    return qName;
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current local name
   */
  public String getLocalName() {
    //    return (String)localNames.peek().toString();
    return localName.toString();
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current local name as a CharArray
   */
  public CharArray getLocalNameCharArray() {
    return localName;
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current URI
   */
  public String getURI() {
    //    return (String)uris.peek().toString();
    return uri.toString();
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current URI as a CharArray
   */
  //  public CharArray getURIAsCharrArray() {
  //    return new CharArray(uri);
  //  }
  public CharArray getURICharArray() {
    return new CharArray(uri);
  }

  /**
   *  Consistent only in the START state.
   *
   *  @return the attributes of the current element
   */
  public Attributes getAttributes() {
    if (this.currentState != XMLTokenReader.STARTELEMENT) {
      return null;
    }

    AttributesImpl attr = new AttributesImpl();
    for (int i = 0; i < attribs.size(); i ++) {
      InternalAttribute ia = attribs.get(i);
      attr.addAttribute((ia.uri == null ? null : ia.uri.toString()), (ia.localName == null ? null : ia.localName.toString()), (ia.qname == null ? null : ia.qname.toString()), "CDATA", (ia.value == null ? null : ia.value.toString()));
    }
    return attr;
  }

  public InternalAttributeList getInternalAttributeList() {
    return attribs;
  }

  /**
   *  Consistent only in the CHARS state.
   *
   *  @return the current Character Data
   */
  public String getValue() {
  	if (currentState == COMMENT) {
  		return commentData.toString();
  	}
    return charData.toString();
  }

  public CharArray getValueCharArray() {
    return charData;
  }

  public boolean isWhitespace() {
    return charData.isWhitespace();
  }

  /**
   * Returns dom representation of current element.
   */
  public  Element getDOMRepresentation(Document document) throws ParserException {
    if (this.getState() != XMLTokenReader.STARTELEMENT) {
      return null;
    }
    Element element = document.createElementNS(this.getURI(), this.getQName());
    Enumeration enum1 = prefixMapping.keys();
    while (enum1.hasMoreElements()) {
      String key = (String) enum1.nextElement();
      Stack mappings = (Stack) prefixMapping.get(key);
      String namespace = (String) mappings.peek();
      if (namespace.length() != 0) {
        if (key.length() == 0) {
          element.setAttribute("xmlns",namespace);
        } else {
          element.setAttribute("xmlns:"+key,namespace);
        }
      }

    }

    Attributes attrs = this.getAttributes();
    int attrsLeght = attrs.getLength();
    String attribUri, attribQName, attribValue;
    for (int i = 0; i < attrsLeght; i++) {
      attribUri = attrs.getURI(i);
      attribQName = attrs.getQName(i);
      attribValue = attrs.getValue(i);
      if (!attribUri.equals(XMLParserConstants.sXMLNSNamespace) && (!DOM.qnameToLocalName(attribQName).equals("xmlns"))) {
        element.setAttributeNS(attribUri, attribQName, attribValue);
      }
    }
    int code;
    while (true) {
      code = this.next();
      switch (code) {
        case XMLTokenReader.COMMENT: {
          element.appendChild(element.getOwnerDocument().createComment(getValue()));
          break;
        }
        case XMLTokenReader.CHARS: {
          element.appendChild(element.getOwnerDocument().createTextNode(getValue()));
          break;
        }
        case XMLTokenReader.STARTELEMENT: {
          element.appendChild(getDOMRepresentation(document));
          break;
        }
        case XMLTokenReader.ENDELEMENT: {
          return element;
        }
        case XMLTokenReader.EOF: {
          throw new ParserException("Unexpexted EOF.",0,0);
        }
      }
    }
  }

  public int moveToNextElementStart() throws ParserException {
    int code;

    while (true) {
      code = this.next();
      if (code == XMLTokenReader.STARTELEMENT) {
        return XMLTokenReader.STARTELEMENT;
      } else if (code == XMLTokenReader.EOF) {
        return XMLTokenReader.EOF;
      }
    }
  }


  public void startPrefixMapping(CharArray prefix, CharArray uri) throws Exception {
    if (docHandler != null){docHandler.startPrefixMapping(new CharArray(prefix), new CharArray(uri));}
    String stringPrefix = prefix.toString();
    String stringURI = uri.toString();
    Stack mappings = (Stack) prefixMapping.get(stringPrefix);

    if (mappings == null) {
      mappings = new Stack();
      prefixMapping.put(stringPrefix, mappings);
    }

    mappings.add(stringURI);
  }

  public void endPrefixMapping(CharArray prefix) throws Exception {
    if (docHandler != null){docHandler.endPrefixMapping(new CharArray(prefix));}
    String stringPrefix = prefix.toString();
    Stack mappings = (Stack) prefixMapping.get(stringPrefix);

    if (mappings != null) {
      mappings.pop();

      if (mappings.isEmpty()) {
        prefixMapping.remove(stringPrefix);
      }
    }
  }

  /**
   * Returns namespace mapping ot this prefix on current place
   */
  public String getURI(String prefix) {
    //LogWriter.getSystemLogWriter().println("ActiveXMLParser prefixes: " + prefixMapping);
    Stack mappings = (Stack) prefixMapping.get(prefix);

    if (mappings != null) {
      return (String) mappings.peek();
    }

    return null;
  }

  public XMLParser getParser() throws ParserException {
    if (parser == null) {
      parser = new XMLParser();
      parser.setSoapProcessing(false);
      parser.setNamespaces(true);
    }

    return parser;
  }

  public void setParser(XMLParser parser) {
    this.parser = parser;
  }


  public String getPrefixMapping(String prefix) {
    return getURI(prefix);
  }

  /**
   * Passes characters. Stops if elemed start or end is met.
   */
  public void passChars() throws ParserException {
    while (currentState != XMLTokenReader.STARTELEMENT && currentState == XMLTokenReader.CHARS) {
      next();
    }
  }
  
  public void setNormalize(boolean normalize) {
  	this.normalize = normalize;
  }

	public boolean getNormalize() {
		return(this.normalize);
	}

//  public static void main(String[] args) throws Exception {
//    ActiveXMLParser parser = new ActiveXMLParser(new FileInputStream("D:/develop/schema_test/jaxp/normalize.xml"));
//    parser.begin();
//    //parser.setNormalize(true);
//    int code = -1;
//    while ((code = parser.next()) != parser.EOF) {
//    	if(code == START) {
//        while((code = parser.next()) != END) {
//          if(code == CHARS) {
//            LogWriter.getSystemLogWriter().println("element : " + parser.getLocalName() + " value : |" + parser.getValue() + "|");
//          }
//        }
//    	}
//    }
//  }

  public CharArray getValuePassCharsCA() throws ParserException {
    append = true;
    while (currentState == XMLTokenReader.CHARS) {
      next();
    }
    append = false;
    return charData;
  }

  public String getValuePassChars() throws ParserException {
    append = true;
    while (currentState == XMLTokenReader.CHARS) {
      next();
    }
    append = false;
    return charData.toString();
  }
  
  /**
   * Returns parent of current node.
   * @return
   */
  public String getParentElement() {
    CharArrayStack stack = parser.getElementStack();
    if (this.currentState == XMLTokenReader.STARTELEMENT) {
      CharArray current = new CharArray(stack.getTop());
      stack.matchTop(new CharArray(current));
      if (stack.isEmpty()) {
        stack.put(current);
        return null;
      } else {
        char[] top = stack.getTop();
        stack.put(current);
        return new String(top);
      }
    } else {
      if (stack.isEmpty()) {
        return null;
      } else {
        return new String(stack.getTop());
      }
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader#getCurrentLevel()
   */
  public int getCurrentLevel() {
    // TODO Auto-generated method stub
    return 0;
  }

  public void setEntityResolver(EntityResolver resolver) {
    parser.setEntityResolver(resolver);
  }

  
  //  public void end() {
  //  	return;
  //  }
  //  public static void main(String[] args) throws Exception {
  //    File xml = new File("d:/xml/examples/soap/Example3.xml");
  //    long l1 = System.currentTimeMillis();
  //
  //    File xml = new File("d:/xml/examples/jlb/ext1.xml");
  //    XMLReaderImpl1 r = new XMLReaderImpl1(new FileInputStream(xml));
  //    r.begin();
  //    int i;
  //    while ((i = r.next()) != com.sap.engine.lib.jaxrpc.XMLReader.EOF) {
  //      switch(i) {
  //        case com.sap.engine.lib.jaxrpc.XMLReader.START: {
  //          LogWriter.getSystemLogWriter().println(r.getQName());
  //          Attributes a = r.getAttributes();
  //          for(int j = 0; j < a.getLength(); j++) {
  //            LogWriter.getSystemLogWriter().println("  +--" + a.getQName(j) + " : " + a.getValue(j));
  //          }
  //
  //          break;
  //        }
  //
  //        case com.sap.engine.lib.jaxrpc.XMLReader.CHARS: {
  //          LogWriter.getSystemLogWriter().println(r.getValue());
  //          break;
  //        }
  //
  //        default :
  //          break;
  //
  //      }
  //
  //    }
  //    r.end();
  //  long l2 = System.currentTimeMillis();
  //  LogWriter.getSystemLogWriter().println((l2 - l1)*1000);
  //  }

  public List<String[]> getEndedPrefixMappings() {
    // TODO Auto-generated method stub
    return null;
  }

  public Map<String, String> getNamespaceMappings() {
    // TODO Auto-generated method stub
    return null;
  }

  public List<String> getPrefixesOnLastStartElement() {
    // TODO Auto-generated method stub
    return null;
  }

  public void writeTo(Writer writer) throws ParserException, IOException {
    throw new UnsupportedOperationException();
  }

  public void setProperty(String key, Object value) {
    // TODO Auto-generated method stub
    
  }

}

//class Temp extends Thread {
//  private Object lock = null;
//  private JAXRPCStyleDocHandler d = null;
//  private int i = -1;
//
//  public Temp (Object lock, JAXRPCStyleDocHandler d) {
//    this.lock = lock;
//    this.d = d;
//  }
//
//  public void run () {
//    try {
//      while (true) {
//        int j = d.showCurrentState();
//        if (i != j) {
//          LogWriter.getSystemLogWriter().println("State changed from " + i + " to " + j);
//          i = j;
//        }
//        Thread.sleep(200);
//
//        synchronized (lock) {
//          lock.notify();
//          LogWriter.getSystemLogWriter().println("Notified!");
//        }
//
//        if (j == 5) {
//          break;
//        }
//
//        if (j == 1) {
//          LogWriter.getSystemLogWriter().println("New element is:" + d.showCurrentQname());
//        }
//
//      }
//    } catch (InterruptedException ie) {
//      ie.printStackTrace();
//    }
//  }
//
//}

