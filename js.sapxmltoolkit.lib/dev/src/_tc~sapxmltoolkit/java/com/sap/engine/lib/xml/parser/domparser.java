package com.sap.engine.lib.xml.parser;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.Document;
import org.xml.sax.*;
import org.xml.sax.helpers.LocatorImpl;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.dom.DOMDocHandler1;
import com.sap.engine.lib.xml.dom.DocumentImpl;
import com.sap.engine.lib.xml.parser.dtd.ValidationException;

/**
 * Class description - parses a file, ot input stream to a DOM tree
 *
 * @author Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version 1.00
 * @deprecated Please use JDK JAXP instead.
 */
@Deprecated
public class DOMParser extends Parser {

  private Document document = null;
  private boolean isIgnoringComments = false;
  private Class documentClass = null;

  /**
   * Default Constructor
   *
   * @exception   Exception  If the underlying XMLParser fails to initialize
   */
  public DOMParser() throws Exception {
    super();
    xmlParser.setWellformed(true);
    try {
      documentClass = Class.forName("com.sap.engine.lib.xml.dom.DocumentImpl");
    } catch (NoClassDefFoundError e) {
      //$JL-EXC$
      try {
        documentClass = new DocumentImpl().getClass();
      } catch (Throwable t) {
        //$JL-EXC$
        //this should not happen, as the DOMParser is bound with the implementation
        LogWriter.getSystemLogWriter().println("DOMParser: could not load DocumentImpl, and get it's Class"); //$JL-SYS_OUT_ERR$
        t.printStackTrace();
      }
    }
    setErrorHandler(new ErrorHandler() {
      public void error(SAXParseException arg0) throws SAXException {
        throw arg0;
      }
      public void fatalError(SAXParseException arg0) throws SAXException {
        throw arg0;
      }
      public void warning(SAXParseException arg0) throws SAXException {}
    });
  }

  public DOMParser(XMLParser xmlParser) throws ParserException {
    super(xmlParser);
  }

  public void setIgnoringComments(boolean b) {
    isIgnoringComments = b;
  }

  /**
   * Parses an url or file to DOM Document
   *
   * @param       name  URI of the xml document
   * @return      the DOM Document created
   * @exception   IOException on bad file name, or other IO Error
   * @exception   SAXException on parse error
   */
  public Document parse(String systemId) throws IOException, SAXException {
    return parse(new InputSource(systemId));
  }

  /**
   * Parses an input specifiead by this input source
   *
   * @param       name  the name of the input file
   * @return      the DOM Document created
   * @exception   IOException on bad file name, or other IO Error
   * @exception   SAXException on parse error
   */
  public Document parse(InputSource input) throws SAXException, IOException {
    try {
      DOMDocHandler1 domdoc = null;
      if (document == null) {
        domdoc = new DOMDocHandler1((DocumentImpl) documentClass.newInstance());
      } else {
        domdoc = new DOMDocHandler1(document);
      }
      domdoc.setErrorHandler(errorHandler);
      domdoc.setIgnoringComments(isIgnoringComments);
      domdoc.setXMLValidator(xmlParser.getXMLValidator());

      super.parse(input, domdoc);
      document = domdoc.getDocument();

      if (document instanceof DocumentImpl) {
        document.normalize();
      } else {
        try {
          document.normalize();
        } catch (NoSuchMethodError e) {
          //$JL-EXC$
          //it is not a problem if this method is not invoked
        }
      }
      Document result = document;
      document = null;
      
      return result;
    } catch (Exception e) {
      //$JL-EXC$
      //the exception handling is too complex for the test

      Locator loc = new LocatorImpl();
      if (e instanceof ParserException) {
        ((LocatorImpl) loc).setSystemId(((ParserException) e).getSourceID());
        ((LocatorImpl) loc).setLineNumber(((ParserException) e).getRow());
        ((LocatorImpl) loc).setColumnNumber(((ParserException) e).getCol());
        if (loc.getSystemId() == null || loc.getSystemId().equals(":main:")) {
          ((LocatorImpl) loc).setSystemId(((Locator) xmlParser).getSystemId());
        }
      } else {
        loc = (Locator) xmlParser;
      }
      if (errorHandler != null) {
        if (e instanceof ValidationException) {
          errorHandler.error(new NestedSAXParseException("Validation Error: " + e, loc, e));
        } else if (e instanceof IOException) {
          throw (IOException) e;
        } else {
          errorHandler.fatalError(new NestedSAXParseException("Fatal Error: " + e, loc, e));
          if (e instanceof UnsupportedEncodingException) {
            throw new NestedSAXParseException(e.toString(), loc, e);
          } else if (e instanceof IOException) {
            throw (IOException) e;
          } else {
            throw new NestedSAXParseException(e.toString(), loc, e);
          }
        }

        return null;
      } else {
        if (e instanceof IOException) {
          throw (IOException) e;
        } else {
          //e.printStackTrace();
          throw new NestedSAXParseException(e.toString(), loc, e);
        }
      }
    } finally {
      if (xmlParser != null) {
        xmlParser.clearDocHandler();
      }
    }
  }

  /**
   * Parses a BufferedInputStream to DOM
   *
   * @param   in  the input stream
   * @return     the DOM Document created
   * @exception   ParserException  on parser error
   */
  public Document parse(InputStream in) throws SAXException, IOException {
    return parse(new InputSource(in));
  }

  public boolean getFeature(String name) throws SAXNotRecognizedException {
    if (name.equals(Features.FEATURE_EXTERNAL_GENERAL_ENTITIES)) {
      throw new SAXNotRecognizedException("");
    } else if (name.equals(Features.FEATURE_EXTERNAL_PARAMETER_ENTITIES)) {
      throw new SAXNotRecognizedException("");
    } else if (name.equals(Features.FEATURE_STRING_INTERNING)) {
      throw new SAXNotRecognizedException("");
    } else {
      try {
        return(super.getFeature(name));
      } catch(SAXNotSupportedException exc) {
        throw new SAXNotRecognizedException(exc.getMessage());
      }
    }
  }

  public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
    if (name.equals(Features.FEATURE_EXTERNAL_GENERAL_ENTITIES)) {
      throw new SAXNotRecognizedException("");
    } else if (name.equals(Features.FEATURE_EXTERNAL_PARAMETER_ENTITIES)) {
      throw new SAXNotRecognizedException("");
    } else if (name.equals(Features.FEATURE_STRING_INTERNING)) {
      throw new SAXNotRecognizedException("");
    } else {
      try {
        super.setFeature(name, value);
      } catch(SAXNotSupportedException exc) {
        throw new SAXNotRecognizedException(exc.getMessage());
      }
    }
  }

  /**
   * @param   name
   * @return
   * @exception   SAXNotRecognizedException
   */
  public Object getProperty(String name) throws SAXNotRecognizedException {
    try {
      return(super.getProperty(name));
    } catch(Exception exc) {
      throw new SAXNotRecognizedException(exc.getMessage());
    }
    //throw new SAXNotRecognizedException("");
  }

  public void setProperty(String name, Object value) throws SAXNotRecognizedException {
    try {
      if (name.equals(JAXPProperties.PROPERTY_DOCUMENT_CLASS_NAME_STRING)) {
        setDocumentClassName((String) value);
      } else if (name.equals(Features.MAX_REFERENCES)) {
        xmlParser.setMaximumProcessingReferences(value);
      } else if (name.indexOf("/features/") > -1) {
        setFeature(name, Features.createBooleanValue(value));
      } else {
        super.setProperty(name, value);
      }
    } catch (SAXException e) {
      throw new SAXNotRecognizedException(e.toString());
    }
  }

  /**
   * Sets the <code>Document</code> implementation to use when creating the DOM-tree.
   * Use this options if you like to use some different DOM-nodes, for example you want your
   * <code>Element</code> node to do something specific.
   * <p>How to do it: <br>
   *    1.Extend <code>com.sap.engine.lib.xml.dom.DocumentImpl</code>
   *    2.Overload only this <code>createXXX</code> methods, which you would like
   *      to return your own objects.
   *    3.Extend the class which you would like to enhance. You must extend a class
   *      from the <code>com.inqmy.xml.dom</code> package. If you implement if from
   *      <code>org.w3c.dom</code> then the result is undefined. So if you want to
   *      have a different <code>Element</code>, just extend <code>com.sap.engine.lib.xml.dom.ElementImpl</code>,
   *      and overload <code>createElement</code> and <code>createElementNS</code> in your
   *      class and set this class to the parser
   *
   * @param   name   a String representing the class name which shoud be used as a <code>Document</code>
   *                 the default is <code>com.sap.engine.lib.xml.dom.DocumentImpl</code>
   * @exception   SAXException  thrown if the class could not be instantiated
   */
  public void setDocumentClassName(String name) throws SAXException {
    try {
      Object docClass = Class.forName(name).newInstance();

      if (!(docClass instanceof DocumentImpl)) {
        throw new ClassCastException("The class must extend com.sap.engine.lib.xml.dom.DocumentImpl");
      }

      this.documentClass = Class.forName(name);
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  /**
   * Returns the <code>Class</code> that will be used to create the DOM tree.
   * This class MUST extend <code>com.sap.engine.lib.xml.dom.DocumentImpl
   *
   * @return     the <code>Class</code> that will be used to create the DOM tree.
   */
  public Class getDocumentClass() {
    return this.documentClass;
  }

  public Document getDocument() {
    LogWriter.getSystemLogWriter().println("This method cannot be used any more due to MemoryLeaking! Use parse(...) instead."); //$JL-SYS_OUT_ERR$
    throw new RuntimeException("This method cannot be used any more due to MemoryLeaking! Use parse(...) instead.");
    //return null;
  }

  public void setDocument(Document doc) {
    this.document = doc;

    if (doc != null && this.document.getDocumentElement() != null) {
      this.document.removeChild(document.getDocumentElement());
    }
  }
}

