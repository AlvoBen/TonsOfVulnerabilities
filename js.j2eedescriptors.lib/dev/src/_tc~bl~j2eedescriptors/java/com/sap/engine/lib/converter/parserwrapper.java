package com.sap.engine.lib.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sap.tc.logging.Severity;

/**
 * Threadsafe wrapper for {@link javax.xml.parsers.DocumentBuilder}.
 */
public class ParserWrapper implements ErrorHandler {

  private String currentFileName;
  private List fileExcPairList = new ArrayList();
  private DocumentBuilder docBuilder;

  public ParserWrapper(DocumentBuilder docBuilder) {
    this.docBuilder = docBuilder;
    docBuilder.setErrorHandler(this);
  }

  public synchronized Document parse(InputSource source, String fileName) throws ConversionException {
    fileExcPairList.clear();
    docBuilder.setErrorHandler(this);
    this.currentFileName = fileName;
    Document doc = null;
    try {
      doc = docBuilder.parse(source);
    } catch (SAXException e) {
      // $JL-EXC$ must have been handled by errorhandler already
    } catch (IOException e) {
      addToExceptionList(currentFileName, e,
          FileNameExceptionPair.SEVERITY_ERROR);
    }
    throwConversionExcIfNeeded();
    return doc;
  }

  public synchronized Document parse(InputStream stream, String fileName)
      throws ConversionException {
    fileExcPairList.clear();
    docBuilder.setErrorHandler(this);
    this.currentFileName = fileName;
    Document doc = null;
    try {
      doc = docBuilder.parse(stream);
    } catch (SAXException e) {
      // $JL-EXC$ must have been handled by errorhandler already
    } catch (IOException e) {
      addToExceptionList(currentFileName, e,
          FileNameExceptionPair.SEVERITY_ERROR);
    }
    throwConversionExcIfNeeded();
    return doc;
  }
  
  public void setEntityResolver(EntityResolver resolver) {
    docBuilder.setEntityResolver(resolver);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
   */
  public void error(SAXParseException exception) throws SAXException {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "SAX errorhandler registering error", exception);
    addToExceptionList(currentFileName, exception);
    // have to re-throw because parser will return null Document otherwise
    throw exception;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
   */
  public void fatalError(SAXParseException exception) throws SAXException {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "SAX errorhandler registering fatal error", exception);
    addToExceptionList(currentFileName, exception);
    // have to re-throw because parser will return null Document otherwise
    throw exception;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
   */
  public void warning(SAXParseException exception) throws SAXException {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "SAX errorhandler registering warning", exception);
    addToExceptionList(currentFileName, exception,
        FileNameExceptionPair.SEVERITY_WARNING);
    // don't re-throw because parsing could still succeed if we have warnings
    // only
  }

  private void addToExceptionList(String fileName, Throwable t) {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "adding exception to conversion exception list", t);
    fileExcPairList.add(new FileNameExceptionPair(t, fileName));
  }

  private void addToExceptionList(String fileName, Throwable t, int severity) {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "adding exception to conversion exception list", t);
    fileExcPairList.add(new FileNameExceptionPair(t, fileName, severity));
  }

  private void throwConversionExcIfNeeded() throws ConversionException {
    if (fileExcPairList.size() == 0) {
      return;
    }
    ConversionException ce = new ConversionException(
        (FileNameExceptionPair[]) fileExcPairList
            .toArray(new FileNameExceptionPair[0]));
    AbstractConverter.LOCATION.throwing("throwing conversion exception", ce);
    throw ce;
  }

}
