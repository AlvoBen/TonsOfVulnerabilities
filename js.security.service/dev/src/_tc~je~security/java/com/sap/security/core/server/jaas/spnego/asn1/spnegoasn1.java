package com.sap.security.core.server.jaas.spnego.asn1;

import iaik.asn1.ASN;
import iaik.asn1.ASN1Object;
import iaik.asn1.CON_SPEC;
import iaik.asn1.CodingException;
import iaik.asn1.DerCoder;
import iaik.asn1.ObjectID;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sap.security.core.server.jaas.spnego.IConstants;

/**
 *  Implements the ASN.1 encoding of an SP Nego token.
 *  For documentation, see http://msdn.microsoft.com/library/en-us/dnsecure/html/http-sso-2.asp?fram=true
 */
public class SpNegoASN1 extends ASN1Object {
  BasicSpNegoType basicSpNegoType;

  public SpNegoASN1() {

  }

  /* (non-Javadoc)
   * @see iaik.asn1.ASN1Object#setValue(java.lang.Object)
   */
  public void setValue(Object obj) {
    if (obj instanceof BasicSpNegoType) {
      basicSpNegoType = (BasicSpNegoType) obj;
    } else {
      throw new IllegalArgumentException("Type must be BasicSpNegoType");
    }
  }

  /* (non-Javadoc)
   * @see iaik.asn1.ASN1Object#getValue()
   */
  public Object getValue() {
    return basicSpNegoType;
  }

  /* (non-Javadoc)
   * @see iaik.asn1.ASN1Object#encode(java.io.OutputStream)
   */
  protected void encode(OutputStream os) throws IOException {

  }

  /* (non-Javadoc)
   * @see iaik.asn1.ASN1Object#decode(int, java.io.InputStream)
   */
  protected void decode(int count, InputStream inputStream) throws IOException, CodingException {
    Object obj;
    int tokenid, tagnr;
    long length;
    CON_SPEC conspec;
    ASN asntype;
    SpNegoInit spNegoInit = null;
    SpNegoTarg spNegoTarg = null;

    // Next must come the SPNego OID.
    obj = DerCoder.decode(inputStream);
    if (obj instanceof ObjectID) {
      ObjectID oid = (ObjectID) obj;
      if (!oid.equals(IConstants.OID_SP_NEGO)) {
        throw new CodingException("SPNego OID expected. Found " + oid.getID());
      }
    } else {
      throw new CodingException("SPNego OID expected.");
    }

    obj = DerCoder.decode(inputStream);
    if (!(obj instanceof CON_SPEC)) {
      throw new CodingException("Context specific expected. Found :" + obj.getClass().getName());
    }

    conspec = (CON_SPEC) obj;
    // Get tag to find out whether it is Init or Targ
    asntype = conspec.getAsnType();
    tagnr = asntype.getTag();

    if (0 == tagnr) {
      // It is an Init token
      spNegoInit = new SpNegoInit();
      spNegoInit.decode((CON_SPEC) obj);
      basicSpNegoType = spNegoInit;
    } else if (1 == tagnr) {
      // It is an Targ token
      spNegoTarg = new SpNegoTarg();
      spNegoTarg.decode((ASN1Object) obj);
      basicSpNegoType = spNegoTarg;
    } else {
      throw new CodingException("tagnr 0 or 1 expected. Found " + tagnr);
    }
  }
}
