package com.sap.engine.lib.xml.parser.dtd;

import java.util.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Attribute;
import org.xml.sax.SAXNotSupportedException;

/**
 *   A helper structure, representing an Element from the DTD.
 *
 * @version 4.0
 * @author  Nick Nickolov
 * @see     DTDAttribute
 */
final public class DTDElement {

  // Values for the spec field
  public final static int UNSPECIFIED = 0;
  public final static int EMPTY = 1;
  public final static int ANY = 2;
  public final static int MIXED = 3;
  public final static int CHILDREN = 4;
  final static CharArray caAttXMLNS = new CharArray("xmlns:").setStatic();
  XMLValidator validator;
  CharArray name = new CharArray();
  CharArray contentSpec = new CharArray();
  Model model = null;
  int spec = UNSPECIFIED;
  Automaton automaton = null; // Used when spec == CHILDREN
  Vector mixedChildren = null; // Used when spec == MIXED
  Hashtable attributes = new Hashtable();
  boolean hasIdAttribute = false;
  boolean hasAttList = false;

  DTDElement(CharArray ca, XMLValidator validator) {
    name.set(ca);
    this.validator = validator;
  }

  int getContentSpec() {
    return spec;
  }

  void setContentSpec(CharArray s) throws ValidationException {
    contentSpec.set(s);
    model = new Model(validator, contentSpec);

    if (contentSpec.equals("EMPTY")) {
      spec = EMPTY;
      return;
    } else if (contentSpec.equals("ANY")) {
      spec = ANY;
      return;
    } else if (contentSpec.indexOf("#PCDATA") != -1) {
      spec = MIXED;
      final char[] v = {',', '?', '+', };

      for (int i = 0; i < v.length; i++) {
        if (contentSpec.indexOf(v[i]) != -1) {
          throw new ValidationException(validator, "Mixed content model cannot contain character '" + v[i] + "'.");
        }
      } 

      model.confirm("(");
      model.skipOptionalWhitespace();
      model.confirm("#PCDATA");
      model.skipOptionalWhitespace();
      int indOfClBr = contentSpec.indexOf(')');

      if (indOfClBr == -1) {
        throw new ValidationException(validator, "A mixed content model must contain character ')'");
      }

      int indOfAst = contentSpec.indexOf('*');

      if (indOfAst != -1) {
        if (indOfAst < indOfClBr) {
          throw new ValidationException(validator, "Character '*' is not allowed to precede " + "character ')' in a mixed content model.");
        }

        if (indOfAst > indOfClBr + 1) {
          throw new ValidationException(validator, "Character '*' must immediately follow " + "character ')' in a mixed content model.");
        }

        model.skipOptionalWhitespace();

        if (model.currentChar() != ')') {
          model.confirm("|");
          mixedChildren = model.getNames('|');
          validator.mentionedElementNames.addAll(mixedChildren);
        }

        model.confirm(")*");
      } else {
        model.confirm(")");
      }
    } else {
      spec = CHILDREN;
      automaton = Automaton.getAutomaton(validator, this, contentSpec);
      State x = new State();
      x.next1 = automaton.initialState;
      automaton.initialState = x;
      automaton.finalState.name.set(State.FINAL_STATE);
    }
  }

  void addAttribute(CharArray attName, CharArray attType, CharArray attDefDecl, CharArray attDefValue0, CharArray attNote) throws ValidationException, SAXNotSupportedException {
    CharArray attDefValue = new CharArray(attDefValue0);
    attDefValue.trim(); // Attribute-value normalization

    if (attributes.containsKey(attName)) {
      if (validator.getFeature("http://inqmy.com/dtd-validation/warnMoreThanOneAttribute")) {
        Warning.issue(validator, "Duplicate declaration for attribute '" + attName + "' to element '" + name + "'.");
      }

      return;
    }

    DTDAttribute a = new DTDAttribute(validator, attName, attType, attDefDecl, attDefValue, this);
    attributes.put(new CharArray(attName), a);

    if (attType.equals("ID")) {
      if (hasIdAttribute) {
        throw new ValidationException(validator, "Element '" + name + "' has more than one " + "ID attribute.");
      }

      if (!attDefDecl.equals("#REQUIRED") && !attDefDecl.equals("#IMPLIED")) {
        throw new ValidationException(validator, "Attribute '" + attName + "' to element '" + name + "' is ID but is neither #REQUIRED, nor #IMPLIED.");
      }

      hasIdAttribute = true;
    } else if (attType.equals("IDREF")) {
      // Do nothing
    } else if (attType.equals("IDREFS")) {
      // Do nothing
    } else if (attType.equals("ENTITY")) {
      // Do nothing
    } else if (attType.equals("ENTITIES")) {
      // Do nothing
    } else if (attType.equals("NMTOKEN")) {
      // Do nothing
    } else if (attType.equals("NMTOKENS")) {
      // Do nothing
    } else if (attType.equals("NOTATION")) {
      Model model = new Model(validator, attNote);
      model.skipOptionalWhitespace();
      model.confirm("(");
      a.choices = model.getNmTokens('|');

      if (validator.getFeatureV("http://inqmy.com/dtd-validation/warnDuplicatesInNotationAttributes") && !CharArray.areDistinct(a.choices)) {
        Warning.issue(validator, "Duplicates in NOTATION " + a);
      }

      model.confirm(")");
    } else if (attType.equals("CDATA")) {
      // Do nothing
    } else if (attType.charAt(0) == '(') {
      Model model = new Model(validator, attNote);
      model.skipOptionalWhitespace();
      model.confirm("(");
      a.choices = model.getNmTokens('|');

      if (validator.getFeatureV("http://inqmy.com/dtd-validation/warnDuplicatesInEnumerationAttributes") && !CharArray.areDistinct(a.choices)) {
        Warning.issue(validator, "Duplicates in ENUMERATION " + a);
      }

      model.confirm(")");
    } else {
      throw new ValidationException(validator, "Unrecognized attribute type, '" + attType + "'.");
    }

    // Check for the default value to be valid
    if ((attDefValue != null) && (!attDefValue.equals(""))) {
      try {
        a.confirmValue(attDefValue);
      } catch (ValidationException e) {
        throw new ValidationException(validator, "Attribute default is not valid: " + e.getMessage());
      }
    }
  }

  private void confirmAttribute(CharArray name, CharArray value) throws ValidationException {
    // uncoment for skipping of attriubtes, whose name starts with 'xmlns:' - namespace attributes
    // should be alowed everywhere
    DTDAttribute a = (DTDAttribute) attributes.get(name);

    if (a == null && !name.startsWith(caAttXMLNS)) {
      throw new ValidationException(validator, "Element '" + this.name + "' has no attribute '" + name + "'");
    } else if (a != null) {
      a.confirmValue(value);
    }

    //    DTDAttribute a  = (DTDAttribute) attributes.get(name);
    //    if (a == null) {
    //      throw new ValidationException(validator, "Element '" + this.name + "' has no attribute '" + name + "'");
    //    }
  }

  void confirmAttributes(Vector v) throws ValidationException {
    Vector supplied = new Vector(); // Contaions the names of the supplied attributes

    for (Enumeration e = v.elements(); e.hasMoreElements();) {
      Attribute a = (Attribute) e.nextElement();
      CharArray qNameCA = new CharArray(a.getQNameStr());
      confirmAttribute(qNameCA, new CharArray(a.getValueStr()));

      if (supplied.contains(qNameCA)) {
        throw new ValidationException(validator, "Attribute '" + qNameCA + "' supplied more than once to element '" + name + "'");
      }

      supplied.addElement(qNameCA);
    } 

    for (Enumeration e = attributes.elements(); e.hasMoreElements();) {
      DTDAttribute a = (DTDAttribute) e.nextElement();

      if (a.defDecl.equals("#REQUIRED") && !supplied.contains(a.name)) {
        throw new ValidationException(validator, "Attribute '" + a.name + "' to element '" + this.name + "' is #REQUIRED but not supplied.");
      }
    } 
  }

  static String[] specStr = {"UNSPECIFIED", "EMPTY", "ANY", "MIXED", "CHILDREN", };

  public String toString() {
    return "Element(name=" + name + ", spec=" + specStr[spec] + ")";
  }

}

