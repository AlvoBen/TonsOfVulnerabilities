package com.sap.security.core.server.jaas.spnego;

import iaik.asn1.ObjectID;
import org.ietf.jgss.GSSContext;

public class SpNegoState {

  public GSSContext gsscontext;
  public int negstate;
  public ObjectID mechanism; // mechanism used for the gss context establishment.

  public SpNegoState() {
    gsscontext = null;
    negstate = IConstants.SPNEGO_NEG_ACCEPT_INITIAL;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer(500);
    sb.append("negstate= ");
    if (IConstants.SPNEGO_NEG_ACCEPT_INITIAL == negstate) {
      sb.append("initial");
    } else if (IConstants.SPNEGO_NEG_ACCEPT_INCOMPLETE == negstate) {
      sb.append("incomplete");
    } else if (IConstants.SPNEGO_NEG_ACCEPT_COMPLETED == negstate) {
      sb.append("completed");
    } else if (IConstants.SPNEGO_NEG_REJECTED == negstate) {
      sb.append("rejected");
    } else {
      sb.append("undefined");
    }

    sb.append(", mechanism.oid= " + ((mechanism != null) ? "" + mechanism.getID() : "" + mechanism));

    return sb.toString();
  }
}
