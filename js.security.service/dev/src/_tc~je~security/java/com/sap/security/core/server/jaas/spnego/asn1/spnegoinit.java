package com.sap.security.core.server.jaas.spnego.asn1;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import com.sap.security.core.server.jaas.spnego.IConstants;
import com.sap.security.core.server.jaas.spnego.util.Utils;

import iaik.asn1.ASN1Object;
import iaik.asn1.ASN1Type;
import iaik.asn1.BIT_STRING;
import iaik.asn1.CON_SPEC;
import iaik.asn1.CodingException;
import iaik.asn1.DerCoder;
import iaik.asn1.OCTET_STRING;
import iaik.asn1.ObjectID;
import iaik.asn1.SEQUENCE;

public class SpNegoInit extends BasicSpNegoType implements ASN1Type {
  private ObjectID[] mechTypeList;
  private boolean[] ctxFlags;
  private byte[] mechToken;

  public SpNegoInit() {
    reset();
  }

  /**
   * 
   */
  private void reset() {
    mechTypeList = null;
    ctxFlags = null;
    mechToken = null;
    this.setMechListMIC(null);
  }

  /**
   * @return
   */
  public boolean getFlag(int id) {
    return ctxFlags[id];
  }

  /**
   * @return
   */
  public byte[] getMechToken() {
    return mechToken;
  }

  /**
   * @return
   */
  public ObjectID[] getMechTypeList() {
    return mechTypeList;
  }

  /**
   * 
   * @param id
   * @param value
   */
  public void setFlag(int id, boolean value) {
    if (ctxFlags == null) {
      ctxFlags = new boolean[IConstants.SPNEGO_CTXFLAGS_MAXIDX];
    }

    if (id > ctxFlags.length) {
      throw new ArrayIndexOutOfBoundsException("Attempt to set not existing context flag.");
    }
    ctxFlags[id] = value;
  }

  /**
   * @param bytes
   */
  public void setMechToken(byte[] bytes) {
    mechToken = bytes;
  }

  /**
   * @param objectIDs
   */
  public void setMechTypeList(ObjectID[] objectIDs) {
    mechTypeList = objectIDs;
  }

  //
  // PROBABLY NEVER NEEDED
  //
  public ASN1Object toASN1Object() throws CodingException {
    SEQUENCE sequence = this.createMechTypeListFromOIDs();
    SEQUENCE sequence2 = new SEQUENCE();
    CON_SPEC conspec = null;
    OCTET_STRING octstring = null;

    sequence2.addComponent(sequence, 0);
    sequence2.addComponent(createBitStringFromCtxFlags(), 1);

    // add mech token if present
    if (mechToken != null) {
      octstring = new OCTET_STRING(mechToken);
      sequence2.addComponent(octstring, 2);
    }

    if (this.getMechListMIC() != null) {
      octstring = new OCTET_STRING(this.getMechListMIC());
      sequence2.addComponent(octstring, 3);
    }

    conspec = new CON_SPEC(0, sequence2);

    return conspec;
  }

  public void decode(ASN1Object asn1object) throws CodingException {
    try {
      CON_SPEC mainConSpec = (CON_SPEC) asn1object;
      SEQUENCE mainSequence = (SEQUENCE) mainConSpec.getValue();

      OCTET_STRING octetstring = null;

      Enumeration enumeration = mainSequence.getComponents();

      while (enumeration.hasMoreElements()) {
        Object obj = enumeration.nextElement();
        CON_SPEC conspec = (CON_SPEC) obj;
        try {
          int tagnr = conspec.getAsnType().getTag();
          switch (tagnr) {
            // mech list
            case 0 :
              if (mechTypeList != null) {
                // has already been parsed => error
                throw new CodingException("mech list has already been parsed.");
              }
              SEQUENCE sequence2 = (SEQUENCE) conspec.getValue();
              parseOIDsFromMechTypeList(sequence2);
              break;

              // ctx flags                          
            case 1 :
              if (ctxFlags != null) {
                // has already been parsed => error
                throw new CodingException("context flags have already been parsed.");
              }
              BIT_STRING bitstring = (BIT_STRING) conspec.getValue();
              parseCtxFlagsFromBitString(bitstring);
              break;

              // mech token
            case 2 :
              if (mechToken != null) {
                // has already been parsed => error
                throw new CodingException("mech token has already been parsed.");
              }
              octetstring = (OCTET_STRING) conspec.getValue();
              mechToken = octetstring.getWholeValue();
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

    buffer.append("  Init Token {\n");
    buffer.append("    Supported Mechs {\n");
    for (int i = 0; i < mechTypeList.length; i++) {
      buffer.append("      " + mechTypeList[i].getID() + "\n");
    }
    buffer.append("    }\n");
    if (ctxFlags != null) {
      buffer.append("    Context Flags ");
      for (int i = 0; i < ctxFlags.length; i++) {
        buffer.append(ctxFlags[i] ? '1' : '0');
      }
    }
    if (mechToken != null) {
      buffer.append("    Mech Token ");
      appendOctetStringToSB(buffer, mechToken);
      buffer.append("... [" + mechToken.length + " bytes]\n");
    }

    if (getMechListMIC() != null) {
      buffer.append("    MIC ");
      appendOctetStringToSB(buffer, getMechListMIC());
      buffer.append("... [" + getMechListMIC().length + " bytes]\n");
    }
    buffer.append("  }\n");

    if (mechToken == null) {
      buffer.append("  Response Token [null]\n");
    } else {
      ASN1Object asn1 = null;
      try {
        asn1 = DerCoder.decode(mechToken);
        Utils.dumpASN1Object(asn1, 1, new PrintWriter(System.out));
      } catch (CodingException e) {
        buffer.append("  Unparsable Octet String ");
        appendOctetStringToSB(buffer, mechToken);
        buffer.append("... [" + mechToken.length + " bytes]\n");
      } catch (Exception e) {
        buffer.append("  Error during parsing");
      }
    }

    return buffer.toString();
  }

  ////////////////////////////////////////////////////////////////////////    
  //    
  //    
  //    H E L P E R  M E T H O D S
  //    
  //    
  ////////////////////////////////////////////////////////////////////////    

  /**
   * 
   * for a given BIT_STRING generates a boolean[] 
   * representing the array of bits  
   * and sets it to this.ctfFlags 
   * 
   * @param ctxFlags
   */
  private void parseCtxFlagsFromBitString(BIT_STRING ctxFlagsBitString) {

    /* OLD CODE 
    byte[] bytes = (byte[]) ctxFlagsBitString.getValue();
    this.ctxFlags = new boolean[8*bytes.length - ctxFlagsBitString.bitsNotValid()];
    int count = ctxFlags.length;
    int bac = 0;
    
    for (int i = 0; i < bytes.length; i++) {
    	int byteValue = ((int) bytes[i]) & 0xff;
      for (int j = 128; j > 0 && bac < count; j >>= 1) {
        this.ctxFlags[bac++] = ((byteValue & j) != 0);
      }
    }
    */

    String binaryString = ctxFlagsBitString.getBinaryString();
    boolean[] result = new boolean[binaryString.length()];

    for (int i = 0; i < binaryString.length(); i++) {
      result[i] = (binaryString.charAt(i) == '1');
    }

    this.ctxFlags = result;

  }

  private BIT_STRING createBitStringFromCtxFlags() {

    /*  OLD CODE
    int[] d = new int[1];
    byte[] bytes = null;
    
    bytes = Utils.createByteArrayFromBool(this.ctxFlags, d);
    return new BIT_STRING(bytes, d[0]);
    */

    return new BIT_STRING(this.ctxFlags);

  }

  /**
   * @param mechTypeList
   */
  private void parseOIDsFromMechTypeList(SEQUENCE mechTypeListSequence) {
    Vector v = new Vector(3);
    Enumeration e = mechTypeListSequence.getComponents();

    while (e.hasMoreElements()) {
      v.add(e.nextElement());
    }

    mechTypeList = (ObjectID[]) v.toArray(new ObjectID[0]);
  }

  private SEQUENCE createMechTypeListFromOIDs() {
    SEQUENCE sequence = new SEQUENCE();

    for (int i = 0; i < mechTypeList.length; i++) {
      sequence.addComponent(mechTypeList[i], i);
    }

    return sequence;
  }
}
