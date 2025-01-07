package com.sap.engine.lib.xml.parser.dtd;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 *
 * @version 4.0
 * @author  Nick Nickolov
 * @see     Automaton
 */
final class State {

  CharArray name = null;
  State next1 = null;
  State next2 = null;
  static final CharArray FINAL_STATE = new CharArray("(final state)");

  State() {

  }

  State(CharArray ca) {
    name = new CharArray(ca);
  }

  State searchFor(CharArray ca, int depth) throws ValidationException {
    if (depth > 200) {
      throw new ValidationException(null, "You are probably using a nondeterministic " + "model like (a*|b*)*, try to replace it with an equivalent " + "deterministic one, e.g. (a|b)*");
    }

    State r1 = null;
    State r2 = null;

    if ((depth != 0) && (name != null)) {
      if (name.equals(ca)) {
        r1 = this;
      } else {
        r1 = null;
      }
    } else {
      if (next1 != null) {
        r1 = next1.searchFor(ca, depth + 1);
      }

      if (next2 != null) {
        r2 = next2.searchFor(ca, depth + 1);
      }
    }

    State r = null;

    if (r1 != null) {
      r = r1;
    } else if (r2 != null) {
      r = r2;
    }

    return r;
  }

  State searchFor(CharArray ca) throws ValidationException {
    State r = searchFor(ca, 0);
    return r;
  }

  void print(int d) { // For debugging purposes
    if (d > 7) {
      return;
    }

    LogWriter.getSystemLogWriter().print("=== ");

    for (int i = 0; i < d; i++) {
      LogWriter.getSystemLogWriter().print("  ");
    } 

    LogWriter.getSystemLogWriter().println(name.toString());

    if (next1 != null) {
      next1.print(d + 1);
    }

    if (next2 != null) {
      next2.print(d + 1);
    }
  }

}

