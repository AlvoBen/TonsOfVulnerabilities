package com.sap.security.core.server.jaas.spnego.asn1;

import iaik.asn1.ASN1Object;
import iaik.asn1.ASN1Type;
import iaik.asn1.CON_SPEC;
import iaik.asn1.CodingException;
import iaik.asn1.DerCoder;
import iaik.asn1.ENUMERATED;
import iaik.asn1.OCTET_STRING;
import iaik.asn1.ObjectID;
import iaik.asn1.SEQUENCE;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import com.sap.security.core.server.jaas.spnego.util.Utils;

public class SpNegoTarg extends BasicSpNegoType implements ASN1Type {
  private int negResult;
  private ObjectID supportedMech;
  private byte[] responseToken;

  public SpNegoTarg() {
    reset();
  }

  public void reset() {
    negResult = -1;
    supportedMech = null;
    responseToken = null;
    setMechListMIC(null);
  }

  /**
   * @return
   */
  public int getNegResult() {
    return negResult;
  }

  /**
   * @return
   */
  public byte[] getResponseToken() {
    return responseToken;
  }

  /**
   * @return
   */
  public ObjectID getSupportedMech() {
    return supportedMech;
  }

  /**
   * @param b
   */
  public void setNegResult(int b) {
    negResult = b;
  }

  /**
   * @param bs
   */
  public void setResponseToken(byte[] bs) {
    responseToken = bs;
  }

  /**
   * @param objectID
   */
  public void setSupportedMech(ObjectID objectID) {
    supportedMech = objectID;
  }

  /* (non-Javadoc)
   * @see iaik.asn1.ASN1Type#toASN1Object()
   */
  public ASN1Object toASN1Object() throws CodingException {
    SEQUENCE sequence = new SEQUENCE();
    CON_SPEC conspec = null;

    // negotiation result
    if (negResult != -1) {
      sequence.addComponent(new CON_SPEC(0, new ENUMERATED(negResult), false));
    }

    // supported mechanism
    if (supportedMech != null) {
      sequence.addComponent(new CON_SPEC(1, supportedMech, false));
    }

    // response token
    if (responseToken != null) {
      sequence.addComponent(new CON_SPEC(2, new OCTET_STRING(responseToken), false));
    }

    // MIC
    if (this.getMechListMIC() != null) {
      sequence.addComponent(new CON_SPEC(3, new OCTET_STRING(this.getMechListMIC()), false));
    }
    conspec = new CON_SPEC(1, sequence);

    return conspec;
  }

  /* (non-Javadoc)
   * @see iaik.asn1.ASN1Type#decode(iaik.asn1.ASN1Object)
   */
  public void decode(ASN1Object asn1object) throws CodingException {
    try {
      CON_SPEC mainConSpec = (CON_SPEC) asn1object;
      SEQUENCE sequence = (SEQUENCE) mainConSpec.getComponentAt(0);

      OCTET_STRING octetstring = null;

      Enumeration enumeration = sequence.getComponents();

      while (enumeration.hasMoreElements()) {
        Object obj = enumeration.nextElement();
        CON_SPEC conspec = (CON_SPEC) obj;
        try {
          int tagnr = conspec.getAsnType().getTag();
          switch (tagnr) {
            // mech list
            case 0 :
              if (negResult != -1) {
                // has already been parsed => error
                throw new CodingException("negociation result has already been parsed.");
              }
              ENUMERATED enumerated = (ENUMERATED) conspec.getValue();
              negResult = (byte) ((Integer) enumerated.getValue()).intValue();
              break;

              // mech list                          
            case 1 :
              if (supportedMech != null) {
                // has already been parsed => error
                throw new CodingException("supported mech has already been parsed.");
              }
              supportedMech = (ObjectID) conspec.getValue();
              break;

              // mech token
            case 2 :
              if (responseToken != null) {
                // has already been parsed => error
                throw new CodingException("mech token has already been parsed.");
              }
              octetstring = (OCTET_STRING) conspec.getValue();
              responseToken = octetstring.getWholeValue();
              break;

              //mech list mic
            case 3 :
              if (getMechListMIC() != null) {
                // has already been parsed => error
                throw new CodingException("mech token has already been parsed.");
              }
              octetstring = (OCTET_STRING) conspec.getValue();
              setMechListMIC(octetstring.getWholeValue());
              break;

              // default => error
            default :
              throw new CodingException("unknown tag in context specific value.");
          }
        } catch (ClassCastException cce) {
          throw new CodingException("Error decoding. Found " + obj.getClass().getName());
        }
      }
    } catch (ClassCastException cce) {
      cce.printStackTrace();
      throw new CodingException("Unexpected ASN.1 encoding");
    } catch (IOException e) {
      e.printStackTrace();
      throw new CodingException("OCTET_STRING.getWholeValue() failed.");
    }
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer(2000);

    buffer.append("  Targ Token {\n");

    // negociation result 
    if (negResult != -1) {
      switch (negResult) {
        case 0 :
          buffer.append("    Neg Result accept_completed (0)");
          break;

        case 1 :
          buffer.append("    Neg Result accept_incomplete (1)");
          break;

        case 2 :
          buffer.append("    Neg Result rejected (2)");
          break;
      }
      buffer.append("\n");
    }

    // supported mechanism
    if (supportedMech != null) {
      buffer.append("    Supported Mech ");
      buffer.append(supportedMech.getID() + "\n");
    }

    // response token
    if (responseToken != null) {
      buffer.append("    Response Token ");
      appendOctetStringToSB(buffer, responseToken);
      buffer.append("\n");
    }

    // MIC
    if (this.getMechListMIC() != null) {
      buffer.append("    MIC ");
      appendOctetStringToSB(buffer, this.getMechListMIC());
      buffer.append("\n");
    }

    buffer.append("  }\n");

    if (responseToken == null) {
      buffer.append("  Response Token [null]\n");
    } else {
      ASN1Object asn1 = null;
      try {
        asn1 = DerCoder.decode(responseToken);
        Utils.dumpASN1Object(asn1, 1, new PrintWriter(System.out));
      } catch (CodingException e) {
        buffer.append("  Unparsable Octet String ");
        appendOctetStringToSB(buffer, responseToken);
      } catch (Exception e) {
        buffer.append("  Error during parsing");
      }
    }

    return buffer.toString();
  }

}