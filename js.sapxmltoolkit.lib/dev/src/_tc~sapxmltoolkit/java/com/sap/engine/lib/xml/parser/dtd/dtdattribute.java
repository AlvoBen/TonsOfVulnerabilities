package com.sap.engine.lib.xml.parser.dtd;

import java.util.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 *   A helper structure, representing an Attribute to an Element from the DTD.
 *
 * @version 4.0
 * @author  Nick Nickolov
 * @see     DTDElement
 */
final class DTDAttribute {

  XMLValidator validator;
  CharArray name = new CharArray();
  DTDElement owner = null;
  CharArray defValue = new CharArray(); // default value
  CharArray type = new CharArray(); // "CDATA", "NMTOKENS", "ID", ...
  CharArray defDecl = new CharArray(); // "#IMPLIED", "#FIXED", ...
  Vector choices = null; // used when type is ENUMERATION or NOTATION

  DTDAttribute(XMLValidator validator, CharArray name, CharArray type, CharArray defDecl, CharArray defValue, DTDElement owner) {
    this.validator = validator;
    this.name.set(name);
    this.type.set(type);
    this.defDecl.set(defDecl);
    this.defValue.set(defValue);
    this.owner = owner;
  }

  void confirmValue(CharArray v0) throws ValidationException {
    CharArray v = new CharArray(v0);
    v.trim();

    if (defDecl.equals("#FIXED")) {
      if (!v.equals(defValue)) {
        throw new ValidationException(validator, "Attribute '" + name + "' to element '" + owner.name + "' was #FIXED with '" + defValue + "' but supplied with '" + v + "'.");
      }

      return;
    }

    Model model = new Model(validator, v);

    if (type.equals("ID")) {
      if (!v.isName()) {
        throw new ValidationException(validator, "Attribute values of type ID must" + " match the name production. " + this);
      }

      if (validator.usedIds.contains(v)) {
        throw new ValidationException(validator, "ID '" + v + "' has already been used. " + this);
      }

      validator.usedIds.addElement(v);
    } else if (type.equals("IDREF")) {
      if (!v.isName()) {
        throw new ValidationException(validator, "IDREF attribute values must match the " + "name production. " + this);
      }

      validator.referencedIds.addElement(v);
    } else if (type.equals("IDREFS")) {
      try {
        for (Enumeration e = model.getNmTokens().elements(); e.hasMoreElements();) {
          CharArray ca = (CharArray) e.nextElement();

          if (!ca.isName()) {
            if (!v.isName()) {
              throw new ValidationException(validator, "IDREFS attribute values must match " + "the names production. ");
            }
          }

          validator.referencedIds.addElement(ca);
        } 
      } catch (ValidationException e) {
        throw new ValidationException(validator, e.getMessage() + this);
      }
    } else if (type.equals("ENTITY")) {
      if (!v.isName()) {
        throw new ValidationException(validator, "ENTITY attribute values must match the " + "name production. " + this);
      }

      if (!validator.entities.contains(v)) {
        throw new ValidationException(validator, "ENTITY '" + v + "' used as an attribute value, but not declared in DTD");
      }
    } else if (type.equals("ENTITIES")) {
      Vector vNames;
      try {
        vNames = model.getNames();
      } catch (ValidationException e) {
        throw new ValidationException(validator, "ENTITIES attribute values must match " + "the names production. ");
      }

      for (Enumeration e = vNames.elements(); e.hasMoreElements();) {
        CharArray ca = (CharArray) e.nextElement();

        if (!validator.entities.contains(ca)) {
          throw new ValidationException(validator, "ENTITY '" + ca + "' used as an attribute value, but not declared in DTD");
        }
      } 
    } else if (type.equals("NMTOKEN")) {
      try {
        model.getNmToken();
        model.confirmEndOfModel();
      } catch (ValidationException e) {
        throw new ValidationException(validator, e.getMessage() + this);
      }
    } else if (type.equals("NMTOKENS")) {
      try {
        model.getNmToken();

        while (true) {
          if (model.finished()) {
            break;
          }

          model.skipOptionalWhitespace();
          model.getNmToken();
        }
      } catch (ValidationException e) {
        throw new ValidationException(validator, e.getMessage() + this);
      }
    } else if (type.equals("NOTATION")) {
      if (!choices.contains(v)) {
        throw new ValidationException(validator, "Value '" + v + "' not found in NOTATION list. " + this);
      }
    } else if (type.equals("CDATA")) {
      // Do nothing
    } else if (type.equals("ENUMERATION") || (!validator.getOwner().isBackwardsCompatibilityMode() && choices.size() != 0)) {
      if (!choices.contains(v)) {
        throw new ValidationException(validator, "Value not found in ENUMERATION list. " + this);
      }
    }
  }

  public String toString() {
    return ("(Attribute '" + name + "' to element '" + owner.name + "', type='" + type + "', defDecl='" + defDecl + "', defValue='" + defValue + "')");
  }

}

