/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

/** 
 * ASN1 Parser. Dealing with v3Extensions of a X509Certificate. 
 * @see	    com.sap.engine.services.security.server.jaas.cclm.RuleData#GETUSERFROM_VAL_EXPERTMODE
 * @see	    com.sap.engine.services.security.server.jaas.cclm.RuleData;
 * @see	    com.sap.engine.services.security.server.jaas.cclm.RuleHelper;
 * @see	    com.sap.engine.services.security.server.jaas.ClientCertLoginModule;
 * 
 * @since   SP16
 * @version 1.00 2005-12-21
 * @author Rumen Barov i033802
 */

package com.sap.engine.services.security.server.jaas.cclm;

import iaik.asn1.ASN;
import iaik.asn1.ASN1Object;
import iaik.asn1.CodingException;
import iaik.asn1.OCTET_STRING;
import iaik.asn1.ObjectID;
import iaik.asn1.SEQUENCE;
import iaik.asn1.structures.GeneralName;
import iaik.asn1.structures.GeneralNames;
import iaik.x509.X509ExtensionInitException;
import iaik.x509.extensions.SubjectAltName;

import java.io.IOException;
import java.util.Enumeration;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.services.security.server.jaas.ClientCertLoginModule;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


/**
 * @author Rumen Barov i033802
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ASN1ParserOfv3 {

  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_CERTIFICATE_LOCATION);
  
  public static String getLevel( int iLevel ){
    StringBuffer sLevel = new StringBuffer( "" );
    for ( int i = 0; i < iLevel; i++ ) {
      sLevel.append( "\t" );
    }
    return sLevel.toString();
  }

  public static String octetString2String( iaik.asn1.OCTET_STRING octet ){
    String res = null;
    byte[] arrb = null;
    try {
      arrb = octet.getWholeValue();
    } catch ( IOException e ) {
      arrb = null;
    }
    res = new String( arrb );
    return res;
  }

  public static void printGeneralNamesError( ASN1Object ao ){
    try {
      GeneralNames gns = new GeneralNames( ao );
    } catch ( CodingException e ) {
      if (LOCATION.beInfo()) {
        LOCATION.infoT( "[*]" + e.getMessage() );
      }
    }
  }

  public static void printGeneralNameError( ASN1Object ao ){
    try {
      GeneralName gn = new GeneralName( ao );
    } catch ( CodingException e ) {
      if (LOCATION.beInfo()) {
        LOCATION.infoT("[ ]" + e.getMessage());
      }
    }
  }

  public static void dumpSimple( int iLevel, ASN1Object ao ){
    printGeneralNameError( ao );
    printGeneralNamesError( ao );
    String sLevel = getLevel( iLevel );
    
    if (LOCATION.beInfo()) {
      LOCATION.infoT(sLevel + ao);
      LOCATION.infoT(sLevel + "  type : " + ao.getAsnType());
    }
    //    System.out.println( sLevel + " cons.: " + ao.getComponentAt( i ).isConstructed() );
    Object value = ao.getValue();

    if (LOCATION.beInfo()) {
      LOCATION.infoT(sLevel + "  class: " + value.getClass());
      LOCATION.infoT(sLevel + "  value: " + value);
    }
    if ( value instanceof OCTET_STRING ) {
      value = octetString2String( (OCTET_STRING) value );
      
      if (LOCATION.beInfo()) {
        LOCATION.infoT(sLevel + "  converted to String is : " + value);
      }
    }

  }

  public static void dumpStructured( int iLevel, ASN1Object ao ){
    printGeneralNameError( ao );
    printGeneralNamesError( ao );
    if ( !ao.isConstructed() )
      try {
        throw new Exception( "Sequence is not constructed!" );
      } catch ( Exception e2 ) {
        if (LOCATION.beInfo()) {
          LOCATION.traceThrowableT(Severity.INFO, "", e2);
        }
      }
    String sLevel = getLevel( iLevel );

    String sComponentsCount = null;
    if ( ao.isConstructed() ) {
      try {
        sComponentsCount = "" + ao.countComponents();
      } catch ( CodingException e ) {
        if (LOCATION.beInfo()) {
          LOCATION.traceThrowableT(Severity.INFO, "", e);
        } //constructed should be countable (or able to split to general names?)
        sComponentsCount = "CANNOT_COUNT";
      }
      //      System.out.println( sLevel + "SEQUENCE-componentcount : " + sComponentsCount );
    }
    if ( ao.isConstructed() ) {
      try {
        if (LOCATION.beInfo()) {
          LOCATION.infoT(sLevel + "--+  components :" + sComponentsCount
              + "                       /string representation is : '" + ao + "'/");
        }
        
        for ( int i = 0; i < ao.countComponents(); i++ ) {
          //          System.out.println( sLevel + "  +-comp : " + i + "                            /string representation is : '" + ao.getComponentAt(i)  + "'/" );
          if (LOCATION.beInfo()) {
            LOCATION.infoT(sLevel + "  +-comp : " + i);
          }

          dumpAsn1Object( iLevel + 1, ao.getComponentAt( i ) );
        }
      } catch ( CodingException e1 ) {
        if (LOCATION.beInfo()) {
          LOCATION.traceThrowableT(Severity.INFO, "", e1);
        }
      }

    }
  }

  public static void dumpAsn1Object( int iLevel, ASN1Object ao ){
    String sLevel = getLevel( iLevel );

    iaik.asn1.ASN asn = ao.getAsnType();
    //    if ( ao.isA( iaik.asn1.ASN.SEQUENCE ) ) {
    //      dumpConstructed( iLevel, ao );
    //      return;
    //    }
    if ( ao.isConstructed() ) {
      dumpStructured( iLevel, ao );
      return;
    } else {
      dumpSimple( iLevel, ao );
      return;
    }

  }

  public static void dumpGeneralName( int iLevel, GeneralName gn ){
    String sLevel = getLevel( iLevel );
    int iType = gn.getType();
    Object oName = gn.getName();
    
    if (LOCATION.beInfo()) {
      LOCATION.infoT(sLevel + "type " + iType);
      LOCATION.infoT(sLevel + "" + oName.getClass());
      LOCATION.infoT(sLevel + "" + oName);
    }

    if ( 0 == iType || 3 == iType || 5 == iType ) {
      ASN1Object ao2 = (ASN1Object) oName;
      if ( ao2.isConstructed() ) {
        dumpStructured( iLevel + 1, ao2 );
      } else {
        dumpSimple( iLevel + 1, ao2 );
      }


      //       System.out.println( sLevel + " >>>" + oName );
      //        dumpAsn1Object( iLevel + 1, (ASN1Object) oName );
    } else {
      if (LOCATION.beInfo()) {
        LOCATION.infoT(sLevel + oName);
      }
    }
  }

  public static void dumpGeneralNames( int iLevel, GeneralNames gns ){

    String sLevel = getLevel( iLevel );
    Enumeration enm = gns.getNames();
    while ( enm.hasMoreElements() ) {
      GeneralName gn = (GeneralName) enm.nextElement();
      dumpGeneralName( iLevel, gn );

    }

  }

  public static void dumpSequenceOf2( int iLevel, ASN1Object ao ){
    if (LOCATION.beInfo()) {
      LOCATION.infoT("___");
    }
    //    dumpAsn1Object( iLevel, ao );
    try {
      for ( int i = 0; i < ao.countComponents(); i++ ) {
        //        System.out.println( getLevel( iLevel ) + " -> " + ao.getComponentAt( i ) );
        dumpAsn1Object( iLevel, ao.getComponentAt( i ) );
      }
    } catch ( CodingException e ) {
      if (LOCATION.beInfo()) {
        LOCATION.traceThrowableT(Severity.INFO, "", e);
      } //impossible - ao.isConstructed() !!!
    }

  }

  public static boolean isSequenceOf2( ASN1Object ao ){
    if ( !ao.isConstructed() ) {
      return false;
    }
    try {
      if ( 2 != ao.countComponents() ) {
        return false;
      }
      for ( int i = 0; i < ao.countComponents(); i++ ) {
        if ( ao.getComponentAt( i ).isConstructed() ) {
          return false;//must be a primitive element
        }
      }
    } catch ( CodingException e ) {
      if (LOCATION.beInfo()) {
        LOCATION.traceThrowableT(Severity.INFO, "", e);
      }
      //impossible - ao.isConstructed() !!!
      return false;
    }
    return true;

  }

  public static void dumpSequencesOf2( int iLevel, ASN1Object ao ){
    if ( isSequenceOf2( ao ) ) {
      dumpSequenceOf2( iLevel, ao );
    } else {
      try {
        for ( int i = 0; i < ao.countComponents(); i++ ) {
          dumpSequencesOf2( iLevel + 1, ao.getComponentAt( i ) );
        }
      } catch ( CodingException e ) {
        return;//no sequence of 2
      }
    }

  }

  /**
   * This methed extracts the String value from subjectAltName-Extension
   * 
   * @param x509cert an instance of iaik.x509.X509Certificate
   * @param arg indicates where to look for UserName possible alternatives are (rfc822Name, directoryName, OID=<other name oid>)
   * 
   * @return String value or null if it is not possible to determine the value
   * @throws X509ExtensionInitException
   * @throws IllegalArgumentException
   * @throws CodingException
   */
  public static String subjectAltNameToUserName( SubjectAltName ext, String arg ) throws IllegalArgumentException,
      X509ExtensionInitException, CodingException{
    GeneralNames gNames = ext.getGeneralNames();

    if ( null == ext ) {
      throw new IllegalArgumentException( "SbjectAltName cannot be null " );
    }

    if ( null == arg || arg.length() == 0 ) {
      throw new IllegalArgumentException( ClientCertLoginModule.class.toString() + " option "
          + RuleData.ATTRIBUTENAME_FLD + " can not be empty or null." );
    }


    Enumeration enm = gNames.getNames();
    if ( arg.equalsIgnoreCase( "rfc822Name" ) ) {
      while ( enm.hasMoreElements() ) {
        GeneralName gName = (GeneralName) enm.nextElement();
        if ( gName.getType() == GeneralName.rfc822Name ) {
          return gName.getName().toString();
        }
      }
      // if it was not possible to determine the name based on given rfc822Name
      return null;

    } else if ( arg.equalsIgnoreCase( "directoryName" ) ) {
      while ( enm.hasMoreElements() ) {
        GeneralName gName = (GeneralName) enm.nextElement();
        if ( gName.getType() == GeneralName.directoryName ) {
          return gName.getName().toString();
        }
      }
      // if it was not possible to determine the name based on given directoryName
      return null;

    } else if ( arg.toLowerCase().startsWith( "oid=" ) ) {
      while ( enm.hasMoreElements() ) {
        GeneralName gName = (GeneralName) enm.nextElement();
        if ( gName.getType() == GeneralName.otherName ) {

          //otherName is always a SEQUENCE
          SEQUENCE seq = (SEQUENCE) gName.getName();

          //the first element if the seqence should be O-ID
          if ( !seq.getComponentAt( 0 ).isA( ASN.ObjectID ) )
            throw new CodingException( "Attribute is not of type ObjectID !" );

          ObjectID oid = (ObjectID) seq.getComponentAt( 0 );
          if ( oid.equals( new ObjectID( arg.substring( 4 ).trim() ) ) ) {
            if ( seq.getComponentAt( 1 ).isStringType() ) {
              // this is not the case at UBS but just if somebody puts the String here
              return seq.getComponentAt( 1 ).getValue().toString();
            } else if ( seq.getComponentAt( 1 ).isConstructed() ) {
              if ( seq.getComponentAt( 1 ).getComponentAt( 0 ).isStringType() )
                return seq.getComponentAt( 1 ).getComponentAt( 0 ).getValue().toString();
              else
                throw new IllegalArgumentException( "StringType is missed inside of otherName !" );
            } else
              throw new IllegalArgumentException( "The value of otherName should be constructed type!" );

          }
        }
      }
    } else {
      throw new IllegalArgumentException( "Unsupported parameter " + arg );
    }

    //		if it was not possible to determine the name based on given OID
    return null;

  }

}