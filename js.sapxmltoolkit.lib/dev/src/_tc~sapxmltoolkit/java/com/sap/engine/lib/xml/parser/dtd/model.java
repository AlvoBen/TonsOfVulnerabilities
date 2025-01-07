package com.sap.engine.lib.xml.parser.dtd;

import java.util.*;
import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 *   The Model class is some kind of an "iterator" over CharArray.
 * It takes a CharArray as a parameter in its constructor, and
 * afterwards returns portions of it through the methods implemented
 * here, or confirms that certain seqences of characters are present.
 *
 *   @version 4.0
 *   @author  Nick Nickolov
 *   @see     CharArray
 */
final class Model {

  XMLValidator validator;
  char[] model;
  int modelLength;
  int index;
  static final char END_OF_MODEL = (char) (-1);

  Model(XMLValidator validator, CharArray modelCA) {
    this.validator = validator;
    model = (modelCA == null) ? (new char[0]) : modelCA.getChars();
    modelLength = model.length;
    index = 0;
  }

  char currentChar() {
    return (index < modelLength) ? model[index] : END_OF_MODEL;
  }

  void nextChar() {
    index++;
  }

  boolean finished() {
    return (index >= modelLength);
  }

  void confirm(String s) throws ValidationException {
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) != currentChar()) {
        throw new ValidationException(validator, "The string '" + s + "' was expected. " + this);
      }

      index++;
    } 
  }

  void confirm(CharArray ca) throws ValidationException {
    int l = ca.getSize();
    char[] a = ca.getChars();

    for (int i = 0; i < l; i++) {
      if (a[i] != currentChar()) {
        throw new ValidationException(validator, "The string '" + ca + "' was expected. " + this);
      }

      index++;
    } 
  }

  void confirmEndOfModel() throws ValidationException {
    if (!finished()) {
      throw new ValidationException(validator, "End of model expected. " + this);
    }
  }

  void skipOptionalWhitespace() {
    while (Symbols.isWhitespace(currentChar())) {
      index++;
    }
  }

  CharArray getName() throws ValidationException {
    char ch = currentChar();

    if (!Symbols.isInitialNameChar(ch)) {
      throw new ValidationException(validator, "Name expected. " + this);
    }

    CharArray ret = new CharArray();
    ret.append(ch);
    index++;

    while (true) {
      ch = currentChar();

      if (!Symbols.isNameChar(ch)) {
        break;
      }

      ret.append(ch);
      index++;
    }

    return ret;
  }

  Vector getNames() throws ValidationException {
    Vector v = new Vector();
    v.addElement(getName());

    while (true) {
      skipOptionalWhitespace();

      if (!Symbols.isInitialNameChar(currentChar())) {
        break;
      }

      v.addElement(getName());
    }

    return v;
  }

  Vector getNames(char separator) throws ValidationException {
    Vector v = new Vector();

    while (true) {
      skipOptionalWhitespace();
      v.addElement(getName());
      skipOptionalWhitespace();

      if (currentChar() != separator) {
        break;
      }

      index++;
    }

    return v;
  }

  CharArray getNmToken() throws ValidationException {
    char ch = currentChar();

    if (!Symbols.isNameChar(ch)) {
      throw new ValidationException(validator, "NmToken expected. " + this);
    }

    CharArray ret = new CharArray();
    ret.append(ch);
    index++;

    while (true) {
      ch = currentChar();
      if (!Symbols.isNameChar(ch)) {
        break;
      }
      ret.append(ch);
      index++;
    }

    return ret;
  }

  Vector getNmTokens() throws ValidationException {
    Vector v = new Vector();
    v.addElement(getNmToken());

    while (true) {
      skipOptionalWhitespace();

      if (!Symbols.isNameChar(currentChar())) {
        break;
      }

      v.addElement(getNmToken());
    }

    return v;
  }

  Vector getNmTokens(char separator) throws ValidationException {
    Vector v = new Vector();

    while (true) {
      skipOptionalWhitespace();
      v.addElement(getNmToken());
      skipOptionalWhitespace();

      if (currentChar() != separator) {
        break;
      }

      index++;
    }

    return v;
  }

  public String toString() {
    return ("(value='" + new String(model) + "', index=" + index + ")");
  }

}

