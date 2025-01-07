package com.sap.engine.lib.xml.parser.dtd;

import org.xml.sax.SAXNotSupportedException;

import com.sap.engine.lib.log.LogWriter;

/**
 *   Encapsulates a static method to issue warnings in an appropriate way (as set in
 * XMLValidator.options). Warnings may be printed, or thrown, or hushed.
 *
 * @version 4.0
 * @author  Nick Nickolov
 */
final class Warning {

  private Warning() {

  }

  static void issue(XMLValidator validator, String s) throws ValidationException, SAXNotSupportedException {
    if (validator.getFeature("http://inqmy.com/dtd-validation/printWarnings")) {
      LogWriter.getSystemLogWriter().println("Validation warning: " + s); //$JL-SYS_OUT_ERR$
    }

    if (validator.getFeature("http://inqmy.com/dtd-validation/reportWarnings")) {
      try {
        validator.owner.onWarning("Validation warning: " + s);
      } catch (Exception e) {
        throw new ValidationException(validator, e.toString());
      }
    }

    if (validator.getFeature("http://inqmy.com/dtd-validation/throwWarnings")) {
      throw new ValidationException(validator, "Validation warning: " + s);
    }
  }

}

