package com.sap.engine.lib.xml.parser.dtd;

import java.util.*;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 *   Used to represent a content spec of an element from the DTD.
 *
 *   Basically an automaton is a finite-state machine useful in recognizing
 * formal languages. An Automaton has States, some of which contain references
 * to one another. Here, for simplicity, every State has at most two references
 * to other States, and States (but not links) are named. There may be unnamed
 * States, i.e. named with null.
 *   The static method to produce an Automaton from a content spec is
 * getAutomaton(CharArray, DTDElement).
 *   Constructors with other Automata and an operator ('|', ',', '?', '*', '+')
 * are provided.
 *   The Automaton contains a method to check its determinism. By specification,
 * automata produced by expressions in content models must be deterministic.
 * Switch Options.warnNonDeterministicContentModel on, if you want this checking to be performed
 * in XMLValidator.
 *
 *   @version 4.0
 *   @author  Nick Nickolov
 *   @see     State
 */
final class Automaton {

  XMLValidator validator;
  DTDElement owner = null;
  State initialState = null;
  State finalState = null;

  private Automaton() {

  }

  Automaton(State initialState, State finalState) {
    this.initialState = initialState;
    this.finalState = finalState;
  }

  Automaton(CharArray name) {
    initialState = new State(name);
    finalState = new State();
    initialState.next1 = finalState;
  }

  Automaton(Automaton a, Automaton b, char op) // op can be '|' or ','
  throws ValidationException {
    State x;

    if (op == '|') {
      x = new State();
      x.next1 = a.initialState;
      x.next2 = b.initialState;
      a.finalState.next1 = b.finalState;
      initialState = x;
      finalState = b.finalState;
    } else if (op == ',') {
      a.finalState.next1 = b.initialState;
      initialState = a.initialState;
      finalState = b.finalState;
    } else {
      throw new ValidationException(validator, "Illegal parameter in constructor of Automaton, '" + op + "'.");
    }
  }

  Automaton(Automaton a, char op) // op can be '?', '*' or '+'
  throws ValidationException {
    State x;

    if (op == '?') {
      x = new State();
      x.next1 = a.initialState;
      x.next2 = a.finalState;
      initialState = x;
      finalState = a.finalState;
    } else if (op == '*') {
      initialState = a.finalState;
      initialState.next1 = a.initialState;
      x = new State();
      initialState.next2 = x;
      finalState = x;
    } else if (op == '+') {
      a.finalState.next1 = a.initialState;
      x = new State();
      a.finalState.next2 = x;
      initialState = a.initialState;
      finalState = x;
    } else {
      throw new ValidationException(validator, "Illegal parameter in constructor of Automaton, '" + op + "'.");
    }
  }

  void print() {
    LogWriter.getSystemLogWriter().println("--- AUTOMATON ---"); //$JL-SYS_OUT_ERR$
    initialState.print(0);
  }

  //   The following few methods are responsible for producing an instance of an
  // Automaton out of its content model. This was encapsulated in the AutomatonBuilder
  // class, but I decided to embed it here.
  static Automaton getAutomaton(XMLValidator validator, DTDElement owner, CharArray ca) throws ValidationException {
    Model model = new Model(validator, ca);
    Automaton ret = build(validator, model);
    model.skipOptionalWhitespace();
    model.confirmEndOfModel();
    ret.finalState.name = State.FINAL_STATE;
    ret.owner = owner;
    return ret;
  }

  static private Automaton build(XMLValidator validator, Model model) throws ValidationException {
    model.skipOptionalWhitespace();
    char ch = model.currentChar();

    if (Symbols.isInitialNameChar(ch)) {
      CharArray name = model.getName();
      validator.mentionedElementNames.add(name);
      ch = model.currentChar();

      if ((ch == '?') || (ch == '*') || (ch == '+')) {
        model.nextChar();
        return new Automaton(new Automaton(name), ch);
      } else {
        return new Automaton(name);
      }
    } else if (ch == '(') {
      model.nextChar();
      char type = ' '; // type of current bracketed expression
      // (' ' for none; '|' for choice; ',' for sequence)
      Automaton a = build(validator, model);
      ch = model.currentChar();

      while (true) {
        model.skipOptionalWhitespace();
        ch = model.currentChar();

        if (ch == ')') {
          break;
        } else if ((ch == '|') || (ch == ',')) {
          if (type == ' ') {
            type = ch;
          } else if (type != ch) {
            throw new ValidationException(validator, "A bracketed expression cannot contain" + " commas(',') and strokes('|') simultaneously." + model);
          }

          model.nextChar();
          a = new Automaton(a, build(validator, model), ch);
        } else {
          throw new ValidationException(validator, "Comma(',') or stroke('|')" + " or closing bracket(')') expected." + model);
        }
      }

      model.confirm(")");
      ch = model.currentChar();

      if ((ch == '?') || (ch == '*') || (ch == '+')) {
        model.nextChar();
        return new Automaton(a, ch);
      } else {
        return a;
      }
    } else if (model.finished()) {
      throw new ValidationException(validator, "Unexpected end of model." + model);
    } else {
      throw new ValidationException(validator, "Name or opening bracket('(') expected." + model);
    }
  }

  //----------
  static HashSet getNamedChildren(State x) {
    if (x == null) {
      return new HashSet();
    }

    if (x.name != null) {
      HashSet r = new HashSet();
      r.add(x);
      return r;
    }

    HashSet r = new HashSet();
    r.addAll(getNamedChildren(x.next1));
    r.addAll(getNamedChildren(x.next2));
    return r;
  }

  /*
   static HashSet processed = new HashSet();
   static boolean isDeterministic(State x) {
   if (processed.contains(x)) {
   return true;
   }
   HashSet children = getNamedChildren(x);
   Vector v = new Vector(); // names of children
   for (Iterator i = children.iterator(); i.hasNext(); ) {
   State y = (State) i.next();
   v.addElement(y.name);
   }
   if (!CharArray.areDistinct(v)) {
   return false;
   }
   processed.add(x);
   for (Iterator i = children.iterator(); i.hasNext(); ) {
   State y = (State) i.next();
   if (!isDeterministic(y)) {
   return false;
   }
   }
   return true;
   }
   boolean isDeterministic() {
   processed.clear();
   return isDeterministic(initialState);
   }
   */

}

