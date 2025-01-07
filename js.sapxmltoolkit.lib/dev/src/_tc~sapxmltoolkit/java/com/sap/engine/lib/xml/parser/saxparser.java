package com.sap.engine.lib.xml.parser;

import java.io.*;
import java.util.Locale;

import org.xml.sax.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

import com.sap.engine.lib.schema.exception.SchemaValidationException;
import com.sap.engine.lib.xml.parser.dtd.ValidationException;
import com.sap.engine.lib.xml.parser.handlers.SAXDocHandler;

/**
 * Class description -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 * @deprecated Please use official JAXP API instead.
 */
@Deprecated
public class SAXParser extends Parser implements XMLReader, org.xml.sax.Parser {

  private ContentHandler contentHandler = null;
  private DocumentHandler documentHandler = null;
  private org.xml.sax.DTDHandler dtdHandler = null;
  private DeclHandler declHandler = null;
  private LexicalHandler lexicalHandler = null;
  private Locale locale = null;
  private SAXDocHandler saxHandler;

  public SAXParser() throws ParserException {
    super();
  }

  public SAXParser(XMLParser xmlParser) throws ParserException {
    super(xmlParser);
  }

  public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(JAXPProperties.PROPERTY_DECLARATION_HANDLER)) {
      return(declHandler);
    } else if (name.equals(JAXPProperties.PROPERTY_LEXICAL_HANDLER)) {
      return(lexicalHandler);
    } else if(name.equals(JAXPProperties.PROPERTY_XML_STRING)) {
      throw new SAXNotSupportedException("");
    } else {
      throw new SAXNotRecognizedException("");
    }
  }

  public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(JAXPProperties.PROPERTY_DECLARATION_HANDLER)) {
      declHandler = (DeclHandler)value;
    } else if (name.equals(JAXPProperties.PROPERTY_LEXICAL_HANDLER)) {
      lexicalHandler = (LexicalHandler)value;
    } else if (name.equals(JAXPProperties.PROPERTY_XML_STRING)) {
      throw new SAXNotSupportedException("");
    } else {
      super.setProperty(name, value);
    }
  }

  public LexicalHandler getLexicalHandler() {
    return lexicalHandler;
  }

  public DeclHandler getDeclHandler() {
    return declHandler;
  }

  public void setDTDHandler(org.xml.sax.DTDHandler handler) {
    dtdHandler = handler;
    if(saxHandler != null) {
      saxHandler.setDTDHandler(handler);
    }
  }

  public org.xml.sax.DTDHandler getDTDHandler() {
    return dtdHandler;
  }

  public void setContentHandler(ContentHandler handler) {
    contentHandler = handler;
    if(saxHandler != null) {
      saxHandler.setContentHandler(handler);
    }
  }

  public ContentHandler getContentHandler() {
    return contentHandler;
  }

  public void setDocumentHandler(DocumentHandler handler) {
    documentHandler = handler;
    if(saxHandler != null) {
      saxHandler.setDocumentHandler(handler);
    }
  }

  public DocumentHandler getDocumentHandler() {
    return documentHandler;
  }

  public void parse(InputSource input) throws IOException, SAXException {
    String sysId = input.getSystemId();
    String pubId = input.getPublicId();
    xmlParser.setSystemId(sysId);
    xmlParser.setPublicId(pubId);
    if (contentHandler != null) {
       contentHandler.setDocumentLocator(xmlParser);
    }
    if (documentHandler != null) {
      documentHandler.setDocumentLocator(xmlParser);
    }
    try {
      if (saxHandler == null) {
        saxHandler = new SAXDocHandler();
      }
      saxHandler.setLexicalHandler(lexicalHandler);
      saxHandler.setDocumentHandler(documentHandler);
      saxHandler.setDeclHandler(declHandler);
      saxHandler.setContentHandler(contentHandler);
      saxHandler.setDTDHandler(dtdHandler);
      saxHandler.setErrorHandler(errorHandler);
      super.parse(input, saxHandler);
		} catch (ParserException parserExc) {
			//$JL-EXC$
			//the exception handling is too complex for the test
			LocatorImpl loc = new LocatorImpl();
			loc.setPublicId(input.getPublicId());
			loc.setSystemId(parserExc.getSourceID());
			loc.setLineNumber(parserExc.getRow());
			loc.setColumnNumber(parserExc.getCol());
			if (contentHandler != null) {
				contentHandler.setDocumentLocator(loc);
			}
			if (documentHandler != null) {
				documentHandler.setDocumentLocator(loc);
			}
			if (errorHandler != null) {
				if (parserExc instanceof ValidationException) {
					errorHandler.error(new NestedSAXParseException("Validation Error: " + parserExc, loc, parserExc));
          errorHandler.fatalError(new NestedSAXParseException("Fatal Error: " + parserExc, loc, parserExc));
				} else {
					errorHandler.fatalError(new NestedSAXParseException("Fatal Error: " + parserExc, loc, parserExc));
					throw new NestedSAXParseException(parserExc.toString(), loc, parserExc);
				}
			} else {
				throw new NestedSAXParseException(parserExc.getMessage(), loc, parserExc);
			}
		} catch (SAXException saxExc) {
			throw saxExc;
		} catch (IOException ioExc) {
			throw ioExc;
		} catch (Exception exc) {
			throw new NestedSAXParseException("Generic Exception: ", exc);
		}
  }

  public void parse (String systemId) throws IOException, SAXException {
    parse(new InputSource(systemId));
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}
