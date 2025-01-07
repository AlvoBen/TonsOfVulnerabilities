package com.sap.engine.lib.xml.parser.dtd;

import org.xml.sax.SAXNotSupportedException;
import java.util.*;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.AdvancedXMLStreamReader;
import com.sap.engine.lib.xml.parser.AbstractXMLParser;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xml.parser.handlers.*;

/**
 *   The class, responsible for validating the XML document.
 * The parser calls its methods  when its validation option
 * is set on. Some of the methods throw a ValidationException
 * if the document is not valid.
 *
 *   The XMLValidator has the following options:
 *       printWarnings
 *       throwWarnings
 *       warnAttributeBeforeElement
 *       warnMoreThanOneAttList
 *       warnMoreThanOneAttribute
 *       warnUndefinedIdRefs
 *       warnDuplicatesInEnumerationAttributes
 *       warnDuplicatesInNotationAttributes
 *       warnElementMentionedButNotDeclared
 *       warnNonDeterministicContentModel
 * each of which is identified by its name with the prefix
 * "http://inqmy.com/dtd-validation/"
 *
 * @version 4.0
 * @author  Nick Nickolov
 */
public final class XMLValidator {

  AdvancedXMLStreamReader reader = null;
  CharArray rootElementName = new CharArray();
  AbstractXMLParser owner = null;
  boolean calledStartDTD = false;
  boolean calledEndDTD = false;
  boolean processingAttList = false;
  boolean startedRoot = false;
  boolean endedRoot = false;
  Hashtable elems = new Hashtable(11); // hashes names of elements to DTDElement objects
  HashSet notations = new HashSet(11); // contains names of notations declared in the DTD
  HashSet entities = new HashSet(11); // contains names of entities declared in the DTD
  HashSet mentionedElementNames = new HashSet(11); // Becomes unused after end of DTD
  Stack stackElems = new Stack();
  Stack stackStates = new Stack();
  Hashtable options = new Hashtable();
  Vector usedIds = new Vector();
  Vector referencedIds = new Vector();

  public XMLValidator(AbstractXMLParser owner, AdvancedXMLStreamReader reader) {
    this.reader = reader;
    this.owner = owner;
    init();
  }

  public XMLValidator(AbstractXMLParser owner) { // For compatibility with the current version of the parser. Will be removed.
    this.owner = owner;
    init();
  }

  void init() {
    options.put("http://inqmy.com/dtd-validation/printWarnings", Boolean.FALSE);
    options.put("http://inqmy.com/dtd-validation/reportWarnings", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/throwWarnings", Boolean.FALSE);
    options.put("http://inqmy.com/dtd-validation/warnAttributeBeforeElement", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/warnMoreThanOneAttList", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/warnMoreThanOneAttribute", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/warnUndefinedIdRefs", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/warnDuplicatesInEnumerationAttributes", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/warnDuplicatesInNotationAttributes", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/warnElementMentionedButNotDeclared", Boolean.TRUE);
    options.put("http://inqmy.com/dtd-validation/warnNonDeterministicContentModel", Boolean.FALSE);
  }

  public void reuse() {
    reader = null;
    rootElementName.clear();
    calledStartDTD = false;
    calledEndDTD = false;
    processingAttList = false;
    startedRoot = false;
    endedRoot = false;
    elems.clear();
    notations.clear();
    entities.clear();
    mentionedElementNames.clear();
    stackElems.clear();
    stackStates.clear();
    usedIds.clear();
    referencedIds.clear();
  }

  void printFeatureValues() {
    for (Iterator i = options.keySet().iterator(); i.hasNext();) {
      String s = (String) i.next();
      LogWriter.getSystemLogWriter().println(s + " -> " + (Boolean) options.get(s));
    } 
  }

  public void setFeature(String s, boolean b) throws SAXNotSupportedException {
    if (!options.containsKey(s)) {
      throw new SAXNotSupportedException(s);
    }

    options.put(s, new Boolean(b));
  }

  public boolean getFeature(String s) throws SAXNotSupportedException {
    if (!options.containsKey(s)) {
      throw new SAXNotSupportedException(s);
    }

    return ((Boolean) options.get(s)).booleanValue();
  }

  boolean getFeatureV(String s) throws ValidationException { // internal for the validator
    if (!options.containsKey(s)) {
      throw new ValidationException(this, "Option not supported: " + s);
    }

    return ((Boolean) options.get(s)).booleanValue();
  }

  public void setXMLReader(AdvancedXMLStreamReader r) {
    reader = r;
  }

  /*
   public void callEmptyElementTag(CharArray QNameCA, CharArray uriCA, CharArray prefixCA,
   String localName, Vector attList)
   throws ValidationException {
   callEmptyElementTag(QNameCA, attList);
   }
   public void callEmptyElementTag(CharArray QNameCA, Vector attList)
   throws ValidationException {
   startElement(QNameCA, attList);
   endElement(QNameCA);
   }
   */
  public void startElement(String uriString, CharArray localName, CharArray qName, Vector attList) throws ValidationException {
    localName = localName.copy();
    qName = qName.copy();

    //CharArray uriCA = new CharArray(uriString);
    if (!calledEndDTD) {
      throw new ValidationException(this, "There is no DTD defined");
    }

    if (endedRoot) {
      throw new ValidationException(this, "Root element already ended");
    }

    DTDElement f = (DTDElement) elems.get(qName);

    if (f == null) {
      throw new ValidationException(this, "Wrong tag <" + qName + ">. (Element not declared in DTD, " + qName + ")");
    }

    if (!startedRoot) {
      if (!qName.equals(rootElementName)) {
        throw new ValidationException(this, "Root element must be " + rootElementName);
      }

      startedRoot = true;
    } else {
      DTDElement e = (DTDElement) stackElems.peek();

      switch (e.spec) {
        case DTDElement.CHILDREN: {
          State x = (State) stackStates.pop();
          x = x.searchFor(qName);

          if (x == null) {
            throw new ValidationException(this, "Unexpected element, " + qName + ". It cannot appear here according to the content model of " + e.name);
          }

          stackStates.push(x);
          break;
        }
        case DTDElement.MIXED: {
          if (e.mixedChildren == null) {
            throw new ValidationException(this, "Check whether tag <" + e.name + "> is properly closed. (The element <" + e.name + "> cannot have sub-elements, because it is of type (#PCDATA))");
          }

          if (!e.mixedChildren.contains(qName)) {
            throw new ValidationException(this, "Element " + qName + " not found among the mixed children of element " + e.name);
          }

          break;
        }
        case DTDElement.EMPTY: {
          throw new ValidationException(this, "Element " + (e.name) + " is defined EMPTY, but contains " + qName);
        }
        case DTDElement.ANY: {
          // Do nothing
          break;
        }
        default: {
          throw new ValidationException(this, "DTDElement " + e.name + " has an invalid spec field");
        }
      }
    }

    stackElems.push(f);
    f.confirmAttributes(attList);

    if (f.spec == DTDElement.CHILDREN) {
      State x = f.automaton.initialState;
      stackStates.push(x);
    }
  }

  public void endElement(String uriString, CharArray localName, CharArray qName) throws ValidationException {
    try {
      //CharArray uriCA = new CharArray(uriString);
      if (!calledEndDTD) {
        throw new ValidationException(this, "There is no DTD defined.");
      }
  
      if (endedRoot) {
        throw new ValidationException(this, "Root element already ended.");
      }
  
      if (stackElems.empty()) {
        throw new ValidationException(this, "There is no tag to close.");
      }
  
      DTDElement e = ((DTDElement) stackElems.pop());
  
  //    if (!e.name.equals(qName)) {
  //      throw new ValidationException(this, "Tags are not nested properly. " + "Attempt to close '" + e.name + "' with '" + qName + "'.");
  //    }
  
      if (e.spec == DTDElement.CHILDREN) {
        State x = ((State) stackStates.pop()).searchFor(State.FINAL_STATE);
  
        if (x == null) {
          throw new ValidationException(this, "Element '" + e.name + "' cannot be closed here.");
        }
      }
    } finally {
      if (stackElems.empty()) {
        endedRoot = true;
      }
    }
  }

  public void startDocument() throws ValidationException {
    // Do nothing
  }

  public void endDocument() throws ValidationException, SAXNotSupportedException {
    if (!calledEndDTD) {
      throw new ValidationException(this, "DTD required.");
    }

    if (processingAttList) {
      throw new ValidationException(this, "Unexpected end of file while parsing ATTLIST.");
    }

    if (!endedRoot) {
      throw new ValidationException(this, "Some elements haven't been closed.");
    }

    if (getFeatureV("http://inqmy.com/dtd-validation/warnUndefinedIdRefs")) {
      Vector v = new Vector();
      v.addAll(referencedIds);
      v.removeAll(usedIds);

      if (!v.isEmpty()) {
        Warning.issue(this, "Some IDREF or IDREFS attributes have not " + "been used as ID attributes: " + v);
      }
    }
  }
  
  public Boolean charData(CharArray ca) throws ValidationException {
    if (stackElems.empty()) {
      throw new ValidationException(this, "XMLValidator is out of synch because of previous errors.");
    }
    
    if (ca.isWhitespace()) {
      return (((DTDElement) stackElems.peek()).spec == DTDElement.CHILDREN) ? Boolean.TRUE: Boolean.FALSE; // whitespace is ignored
    }

    someText();
    return Boolean.FALSE;
  }

  public void onCDSect(CharArray textCA) throws ValidationException {
    charData(textCA);
    someText();
  }

  void someText() throws ValidationException {
    if (!startedRoot || endedRoot || !calledEndDTD) {
      throw new ValidationException(this, "Check whether the open and close tags are properly nested. (CharData is not allowed here.)");
    }

    DTDElement e = (DTDElement) stackElems.peek();

    if ((e.spec == DTDElement.EMPTY) || (e.spec == DTDElement.CHILDREN)) {
      throw new ValidationException(this, "Check whether the open and close tags are properly nested. (CharData is not allowed here.)");
    }
  }

  public void onDTDElement(CharArray name, CharArray model) throws ValidationException {
    if (!calledStartDTD || calledEndDTD) {
      throw new ValidationException(this, "A DTD declaration out of DTD.");
    }

    DTDElement e = (DTDElement) elems.get(name);
    model.trim();

    if (e != null) {
      if (e.model != null) {
        throw new ValidationException(this, "Element '" + name + "' is defined twice in the DTD.");
      }

      e.setContentSpec(model);
    } else {
      e = new DTDElement(name, this);
      e.setContentSpec(model);
      elems.put(new CharArray(name), e);
    }
  }

  public void onDTDAttListStart(CharArray name) throws ValidationException, SAXNotSupportedException {
    if (!calledStartDTD || calledEndDTD) {
      throw new ValidationException(this, "A DTD declaration out of DTD.");
    }

    if (processingAttList) {
      throw new ValidationException(this, "Nested AttLists.");
    }

    if (getFeatureV("http://inqmy.com/dtd-validation/warnMoreThanOneAttList") && (elems.containsKey(name)) && (((DTDElement) elems.get(name)).hasAttList)) {
      Warning.issue(this, "Element '" + name + "' has more than one ATTLIST.");
    }

    if (!elems.containsKey(name)) {
      if (getFeatureV("http://inqmy.com/dtd-validation/warnAttributeBeforeElement")) {
        Warning.issue(this, "ATTLIST declaration before ELEMENT " + "declaration of element '" + name + "'.");
      }

      DTDElement e = new DTDElement(name, this);
      elems.put(name, e);
    }

    processingAttList = true;
  }

  public void onDTDAttListItem(CharArray name, CharArray attname, String type, String defDecl, CharArray vAttValue, String note) throws ValidationException, SAXNotSupportedException {
    if (!calledStartDTD || calledEndDTD) {
      throw new ValidationException(this, "A DTD declaration out of DTD.");
    }

    if (!processingAttList) {
      throw new ValidationException(this, "Nested ATTLISTs.");
    }

    DTDElement e = (DTDElement) elems.get(name);
    e.addAttribute(attname, new CharArray(type), new CharArray(defDecl), new CharArray(vAttValue), new CharArray(note));
  }

  public void onDTDAttListEnd() throws ValidationException {
    if (!calledStartDTD || calledEndDTD) {
      throw new ValidationException(this, "A DTD declaration out of DTD.");
    }

    if (!processingAttList) {
      throw new ValidationException(this, "Unexpected ATTLIST end.");
    }

    processingAttList = false;
  }

  public void onDTDEntity(Entity x) throws ValidationException {
    if (!calledStartDTD || calledEndDTD) {
      throw new ValidationException(this, "A DTD declaration out of DTD.");
    }

    entities.add(new CharArray(x.getName()));
  }

  public void onDTDNotation(CharArray nameCA, CharArray pubCA, CharArray sysCA) throws ValidationException {
    if (!calledStartDTD || calledEndDTD) {
      throw new ValidationException(this, "A DTD declaration out of DTD.");
    }

    notations.add(new CharArray(nameCA));
  }

  public void startDTD(CharArray name, CharArray pubCA, CharArray sysCA) throws ValidationException {
    if (calledEndDTD) {
      throw new ValidationException(this, "Only one DTD may be present.");
    }

    rootElementName.set(name);
    calledStartDTD = true;
  }

  public void endDTD() throws ValidationException, SAXNotSupportedException {
    if (!calledStartDTD) {
      throw new ValidationException(this, "DTD must be started before being ended.");
    }

    if (calledEndDTD) {
      throw new ValidationException(this, "DTD must be ended at most once.");
    }

    if (!elems.containsKey(rootElementName)) {
      throw new ValidationException(this, "Root element '" + rootElementName + "' does not have" + " an ELEMENT declaration.");
    }

    if (getFeatureV("http://inqmy.com/dtd-validation/warnElementMentionedButNotDeclared")) {
      HashSet h = (HashSet) mentionedElementNames.clone();

      // HashSet h will contains names of elements, mentioned but not declared in the dtd
      for (Enumeration enum1 = elems.keys(); enum1.hasMoreElements();) {
        CharArray ca = (CharArray) enum1.nextElement();
        h.remove(ca);
      } 

      if (!h.isEmpty()) {
        Warning.issue(this, "Some elements were mentioned in the DTD but were not declared, " + h);
      }
    }

    calledEndDTD = true;
    /*
     if (getFeatureV("http://inqmy.com/dtd-validation/warnNonDeterministicContentModel")) {
     for (Enumeration enum = elems.elements(); enum.hasMoreElements(); ) {
     DTDElement e = (DTDElement) enum.nextElement();
     if (e.automaton != null) {
     if (!e.automaton.isDeterministic()) {
     Warning.issue(this, "Automaton for " + e.name + " is not deterministic. Results from validation may not be proper.");
     }
     }
     }
     }
     */
  }

  public static void main(String[] args) throws Exception {
    XMLParser p = new XMLParser();
    p.setValidation(true);
    p.parse("c:/box/test7.xml", new EmptyDocHandler());
  }

  public int getElementContentSpec(CharArray name) {
    DTDElement el = (DTDElement) elems.get(name);

    if (el != null) {
      return el.getContentSpec();
    } else {
      return DTDElement.UNSPECIFIED;
    }
  }

  public int getElementContentSpec(String name) {
    DTDElement el = (DTDElement) elems.get(name);

    if (el != null) {
      return el.getContentSpec();
    } else {
      return DTDElement.UNSPECIFIED;
    }
  }

  public AbstractXMLParser getOwner() {
    return(owner);
  }

}

