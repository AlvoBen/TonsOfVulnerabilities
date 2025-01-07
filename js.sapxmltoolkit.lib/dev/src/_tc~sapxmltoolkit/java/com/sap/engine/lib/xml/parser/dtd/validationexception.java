package com.sap.engine.lib.xml.parser.dtd;

import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 *
 * @version 4.0
 * @author  Nick Nickolov
 */
final public class ValidationException extends ParserException {

  ValidationException(XMLValidator validator, String s) {
    super(s, ((validator != null) && (validator.reader != null)) ? validator.reader.getID() : new CharArray("Unknown Source"), ((validator != null) && (validator.reader != null)) ? validator.reader.getRow() : 0, ((validator != null) && (validator.reader != null)) ? validator.reader.getCol() : 0);
  }

}

