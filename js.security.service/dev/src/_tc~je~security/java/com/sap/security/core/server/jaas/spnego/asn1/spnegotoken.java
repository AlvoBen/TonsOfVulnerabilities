package com.sap.security.core.server.jaas.spnego.asn1;

import com.sap.security.core.server.jaas.spnego.IConstants;
import com.sap.security.core.server.jaas.spnego.util.Base64;

import iaik.asn1.ASN;
import iaik.asn1.ASN1Object;
import iaik.asn1.ASN1Type;
import iaik.asn1.CodingException;
import iaik.asn1.DerCoder;

/*
 *  Structure of the SPNego Token.
 *  All information is copied from
 *  http://msdn.microsoft.com/library/default.asp?url=/library/en-us/dnsecure/html/http-sso-2.asp
 *  1/ There are two types of SPNego tokens.
 *     o Init tokens
 *     o Targ tokens
 *     They have different encodings.
 * 
 *  2/ General encoding
 *     A SPNego token is an application token, i.e. it is not a UNIVERSAL
 *     ASN.1 token but a token that is only defined in this special context.
 *     It's general encoding is
 *     0x60 <DER encoded length of what follows> {
 *         <SP Nego OID>
 *         <SP Nego type specific> 
 *     }
 *     The <SP Nego type specific is one of the ones outlined in 3/ and 4/
 *     It can be recognized by the first byte: If it is 0xa0, then it is a 
 *     Init token, if it is 0xa1 then it is an init token.
 * 
 *  3/ Targ encoding
 * 
 *     0xa1 <DER encoded length> {
 *         NegTokenTarg      ::=  SEQUENCE {
 *              negResult      [0]  ENUMERATED {
 *                                      accept_completed (0),
 *                                       accept_incomplete (1),
 *                                       rejected (2) }  OPTIONAL,
 *              supportedMech  [1]  MechType             OPTIONAL,
 *              responseToken  [2]  OCTET STRING         OPTIONAL,
 *              mechListMIC    [3]  OCTET STRING         OPTIONAL
 *          }
 *     }
 * 
 *  4/ Init token encoding (in pseudo ASN.1 encoding)
 *     0xa0 <DER encoded length>
 *        SEQUENCE { -- following: mech type list (which is a SEQUENCE of mech type)
 *            SEQUENCE {
 *                OID1,
 *                OID2,
 *                ...
 *            }
 *            ContextFlags -- this is a BIT_STRING:
 *                         -- 0 - deleteFlag
 *                         -- 1 - mutualFlag
 *                         -- 2 - replayFlag
 *                         -- 3 - sequenceFlag
 *                         -- 4 - anonFlag
 *                         -- 5 - confFlag
 *                         -- 6 - integFlag
 *            mechToken  -- OCTET_STRING OPTIONAL
 *            mechListMIC -- Message Integrity Code OCTET_STRING OPTIONAL
 *        }
 * 
 *  5/ Examples
 */

public class SPNegoToken implements ASN1Type {
  private BasicSpNegoType basicSpNegoType;

  public SPNegoToken() {

  }

  public BasicSpNegoType getSpnego() {
    return basicSpNegoType;
  }

  public ASN1Object toASN1Object() throws CodingException {
    ASN1Object asn1object = new SpNegoASN1();
    asn1object.setValue(basicSpNegoType);

    return asn1object;
  }

  public void decode(ASN1Object asn1object) throws CodingException {
    basicSpNegoType = ((SpNegoASN1) asn1object).basicSpNegoType;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer(2000);

    buffer.append("\n");
    buffer.append("SPNego token {\n");
    buffer.append("  OID " + IConstants.OID_SP_NEGO.getID() + "\n");
    buffer.append(basicSpNegoType.toString());
    buffer.append("}\n");

    return buffer.toString();
  }

  public static void main(String[] args) throws Exception {
    ASN.register(new ASN(0x0, "SPNegoToken", ASN.APPLICATION), SpNegoASN1.class);

    byte[] bytes = Base64.decode(args[0]);

    ASN1Object asn1 = DerCoder.decode(bytes);
    SPNegoToken spnego = new SPNegoToken();
    spnego.decode(asn1);

    System.out.println(spnego);
  }

}