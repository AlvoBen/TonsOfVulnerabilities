package com.sap.engine.services.security.server.jaas.cclm;

/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

/**
 * Class RuleHelper is used for helping work with RuleData class.
 * (Converting data from and to RuleData fields, validity checks, etc. )
 *
 * @see	    com.sap.engine.services.security.server.jaas.cclm.RuleData;
 * 
 * @since   SP16
 * @version 1.01 2006-03-08
 * @author Rumen Barov i033802
 */


import iaik.asn1.CodingException;
import iaik.x509.X509ExtensionInitException;
import iaik.x509.extensions.SubjectAltName;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.security.auth.login.LoginException;

import com.sap.engine.services.security.server.jaas.ClientCertLoginModule;



public class RuleHelper {

  /**
   * Convert ClientCertLoginModule options to ClientCertLoginModule rules.
   * Here is an example:
   * Assume we have a certificate with the following subject:
   * 
   *      CN=Samuel Walther (00006472),OU=people,OU=CA,O=UBS,C=CH
   * 
   * The code below creates a rule which will let pass a certificate with the mentioned subject: 
   * 
   *   options.put( "Rule1.getUserFrom", "expertmode" );
   *   options.put( "Rule1.oid", "2.5.29.17" );
   *   options.put( "Rule1.AttributeName", "1.3.6.1.4.1.311.20.2.3" );
   *   options.put( "Rule1.filterIssuer", "" );
   *   options.put( "Rule1.filterSubject", "CN = Samuel Walther (00006472), O=UBS, OU= CA, OU=people" );
   *   Vector rules = new Vector();
   *   rules = RuleHelper.options2Rules( options );
   *
   * @param options
   * @return
   * @throws InvalidOptionsException
   */
  public static Vector options2Rules( Map options ) throws InvalidOptionsException{
    Vector res = new Vector();
    Set set = options.entrySet();
    Iterator it = set.iterator();
    while ( it.hasNext() ) {
      Map.Entry entry = (Map.Entry) it.next();

      if ( entry.getKey() instanceof String && entry.getValue() instanceof String ) {
        String key = (String) entry.getKey();
        String value = (String) entry.getValue();
        if ( null != key && null != value ) {
          if ( key.toLowerCase().startsWith( RuleData.PREFIX_RULE.toLowerCase() ) ) {
            String s = key.substring( RuleData.PREFIX_RULE.length() );
            int iPos = s.indexOf( '.' );
            if ( 0 < iPos ) {
              String sRuleNum = s.substring( 0, iPos );
              try {
                int iRuleNum = Integer.parseInt( sRuleNum, 10 ) - 1;//rules are one-based. subtract one to add to the zero-based vector
                if ( 0 <= iRuleNum ) {
                  if ( res.size() <= iRuleNum ) {
                    res.setSize( iRuleNum + 1 );
                  }
                  RuleData ruledata = (RuleData) res.get( iRuleNum );
                  if ( null == ruledata ) {
                    ruledata = new RuleData();
                  }
                  String sFieldName = s.substring( iPos + 1 );
                  ruledata.setField( sFieldName, value );//throws IllegalArgumentException
                  res.setElementAt( ruledata, iRuleNum );
                  continue; // AOK
                }
              } catch ( NumberFormatException e ) {
                throw new InvalidOptionsException( key + " - invalid number after '" + RuleData.PREFIX_RULE
                    + "' option.\n" + e.getMessage() );
              } catch ( IllegalArgumentException e ) {
                throw new InvalidOptionsException( e.getMessage() );
              }
            }
          }
        }
        throw new InvalidOptionsException( entry.toString() + " - invalid option." );
      }
    }

    //clean up the vector
    int i = 0;
    while ( i < res.size() ) {
      if ( null == res.get( i ) ) {
        res.remove( i );//TODO - synchronize this special feature with the documentation
      } else {
        i++;
      }

    }
    return res;
  }

  public static boolean checkOidName( String oid ) throws IllegalArgumentException{
    boolean res = true;

    if ( !res ) {
      throw new IllegalArgumentException( oid + " is an illegal OID." );
    }
    return res;
  }

  //  public static int X501Name2Map( String sX501Name, Map mapToAddTo ) throws IllegalArgumentException{
  //    int res = 0;
  //    Properties syntax = new Properties();
  //    syntax.setProperty( "jndi.syntax.direction", "left_to_right" );
  //    syntax.setProperty( "jndi.syntax.escape", "\\" );
  //    syntax.setProperty( "jndi.syntax.trimblanks", "true" );
  //    syntax.setProperty( "jndi.syntax.separator", "," );
  //    syntax.setProperty( "jndi.syntax.beginquote", "'" );
  //    syntax.setProperty( "jndi.syntax.endquote", "'" );
  //    syntax.setProperty( "jndi.syntax.beginquote2", "\"" );
  //    syntax.setProperty( "jndi.syntax.endquote2", "\"" );
  //    try {
  //      CompoundName n = new CompoundName( sX501Name.trim(), syntax );
  //      Enumeration enm = n.getAll();
  //      while ( enm.hasMoreElements() ) {
  //        String s = enm.nextElement().toString().trim();
  //        Map.Entry entry = AttributeTypeAndValue2MapEntry( s );
  //        mapToAddTo.put( entry.getKey(), entry.getValue() );
  //      }
  //    } catch ( InvalidNameException e ) {
  //      throw new IllegalArgumentException( "Cannot parse " + sX501Name + "\n Error is:" + e.getMessage() );
  //    }
  //    return res;
  //  }

  public static Vector X501Name2Vector( String sX501Name ) throws IllegalArgumentException{
    Properties syntax = new Properties();
    syntax.setProperty( "jndi.syntax.direction", "left_to_right" );
    syntax.setProperty( "jndi.syntax.escape", "\\" );
    syntax.setProperty( "jndi.syntax.trimblanks", "true" );
    syntax.setProperty( "jndi.syntax.separator", "," );
    syntax.setProperty( "jndi.syntax.beginquote", "'" );
    syntax.setProperty( "jndi.syntax.endquote", "'" );
    syntax.setProperty( "jndi.syntax.beginquote2", "\"" );
    syntax.setProperty( "jndi.syntax.endquote2", "\"" );
    Vector res = new Vector();
    try {
      CompoundName n = new CompoundName( sX501Name.trim(), syntax );
      Enumeration enm = n.getAll();
      while ( enm.hasMoreElements() ) {
        String s = enm.nextElement().toString().trim();
        Map.Entry entry = AttributeTypeAndValue2MapEntry( s );
        res.add( entry );
      }
    } catch ( InvalidNameException e ) {
      throw new IllegalArgumentException( "Cannot parse " + sX501Name + "\n Error is:" + e.getMessage() );
    }
    return res;
  }

  public static String Vector2X501Name( Vector v ) throws IllegalArgumentException{
    String res = "";
    String comma = "";
    if ( null == v || 0 == v.size() ) {
      return res;
    }
    for ( int i = 0; i < v.size(); i++ ) {
      Object o = v.elementAt( i );
      if ( ! ( o instanceof StringPair ) ) {
        throw new IllegalArgumentException( "The passed vector must contain elenents of type "
            + StringPair.class.getName() + ". Found " + o.getClass().getName() );
      }
      StringPair p = (StringPair) o;
      res = res + comma + p.getKey() + "=" + p.getValue();
      comma = ",";
    }
    return res;
  }

  public static java.util.Map.Entry AttributeTypeAndValue2MapEntry( String sAttributeTypeAndValue ){
    Properties syntax = new Properties();
    syntax.setProperty( "jndi.syntax.direction", "left_to_right" );
    syntax.setProperty( "jndi.syntax.escape", "\\" );
    syntax.setProperty( "jndi.syntax.trimblanks", "true" );
    syntax.setProperty( "jndi.syntax.separator", "=" );
    syntax.setProperty( "jndi.syntax.beginquote", "'" );
    syntax.setProperty( "jndi.syntax.endquote", "'" );
    syntax.setProperty( "jndi.syntax.beginquote2", "\"" );
    syntax.setProperty( "jndi.syntax.endquote2", "\"" );
    try {
      String attr, val;
      attr = val = null;
      CompoundName n = new CompoundName( sAttributeTypeAndValue.trim(), syntax );
      Enumeration enm = n.getAll();
      if ( enm.hasMoreElements() ) {
        attr = enm.nextElement().toString().trim();
      }

      if ( enm.hasMoreElements() ) {
        val = enm.nextElement().toString().trim();
      }

      if ( null != attr && null != val ) {
        //      mapToAddTo.put( attr, val );
        java.util.Map.Entry entry = new StringPair( attr, val );
        return entry;
      } else {
        throw new IllegalArgumentException( " Cannot parse at least one part of " + sAttributeTypeAndValue
            + "\n It is been parsed to '" + attr + "' and '" + val + "'." );
      }
    } catch ( InvalidNameException ein ) {
      throw new IllegalArgumentException( " Cannot parse ATTV : " + sAttributeTypeAndValue + "\n Error is : "
          + ein.getMessage() );
    }
  }

  public static boolean isSubsetOf( Map set, Map subset ){
    Set entries = subset.entrySet();
    Iterator iter = entries.iterator();

    while ( iter.hasNext() ) {
      //enum al entries in the subset
      Map.Entry entry = (Map.Entry) iter.next();
      String value;
      try {
        value = (String) set.get( (String) entry.getKey() );
      } catch ( ClassCastException e ) {
        return false;
      } catch ( NullPointerException e ) {
        return false;
      }

      if ( 0 != value.compareToIgnoreCase( (String) entry.getValue() ) ) {
        return false;
      }
    }

    return true;
  }

  public static void checkValidAttributeName( String s ) throws IllegalArgumentException{
    s = s.trim();
    if ( 0 == s.length() ) {
      throw new IllegalArgumentException( "Length of an " + RuleData.ATTRIBUTENAME_FLD + " cannot be zero." );
    }

    //    if ( !Character.isJavaIdentifierStart( s.charAt( 0 ) ) )
    //      throw new IllegalArgumentException( "The first char of the AttributeName '" + s
    //          + "' must be a Java identifier start." );
    //
    //    for ( int i = 1; i < s.length(); i++ ) {
    //      if ( Character.isJavaIdentifierPart( s.charAt( i ) ) ) {
    //        throw new IllegalArgumentException( "The char at pos " + i + " of the AttributeName '" + s
    //            + "' must be a Java identifier part." );
    //      }
    //    }
  }

  public static void checkValidAttributeName( String attrName, String getUserFrom ) throws InvalidOptionsException,
      IllegalArgumentException{
    if ( ( null != attrName ) && ( null != getUserFrom ) ) {
      if ( 0 < attrName.length() && 0 < getUserFrom.length() ) {
        if ( RuleData.GETUSERFROM_VAL_EXPERTMODE.equalsIgnoreCase( getUserFrom ) ) {
          if ( attrName.equalsIgnoreCase( RuleData.SUBJ_ALTNAME_RFC822NAME ) ) {
            return;
          }
          if ( attrName.toLowerCase().startsWith( RuleData.SUBJ_ALTNAME_OIDPREFIX.toLowerCase() ) ) {
            checkValidAttributeName( attrName.substring( RuleData.SUBJ_ALTNAME_OIDPREFIX.length() ) );
            return;
          }
          throw new InvalidOptionsException( "When " + RuleData.GETUSERFROM_FLD + " equals "
              + RuleData.GETUSERFROM_VAL_EXPERTMODE + " then " + RuleData.ATTRIBUTENAME_FLD + " must be equal to '"
              + RuleData.SUBJ_ALTNAME_RFC822NAME + "' or must start with '" + RuleData.SUBJ_ALTNAME_OIDPREFIX + "'!" );
        }
      }
    }
    checkValidAttributeName( attrName );
  }


  public static String getUsernameFromSubject( java.security.cert.X509Certificate cert, String attributeName ) 
      throws IllegalArgumentException, LoginException {
  	
    String res = null;

    String subjectDN = cert.getSubjectDN().getName();

    if ( null == attributeName || 0 == attributeName.trim().length() ) {
      throw new IllegalArgumentException( "The passed " + RuleData.ATTRIBUTENAME_FLD + " is empty or null." );
    }

    attributeName = attributeName.trim();
    Vector v = RuleHelper.X501Name2Vector( subjectDN );
    if ( 0 == v.size() ) {
      throw new IllegalArgumentException( "The parsed SubjectDN contains zero elements. SubjectDN is '" + subjectDN
          + "'" );
    }
    boolean bResSet = false;
    for ( int i = 0; i < v.size(); i++ ) {
      StringPair entry = (StringPair) v.get( i );
      if ( attributeName.equalsIgnoreCase( (String) entry.getKey() ) ) {
        res = (String) entry.getValue();
        bResSet = true;
        break;
      }
    }

    if ( !bResSet ) {
      throw new LoginException("The attribute " + attributeName + " is not found in subject of the provided certificate: " + 
      		subjectDN + "\r\n" + ClientCertLoginModule.class + " failed to authenticate a user.");
    }

    return res;
  }

  public static String getUsernameFromExtension( iaik.x509.V3Extension ext, String sAttributeName )
      throws X509ExtensionInitException, IllegalArgumentException, CodingException{
    String res = null;
    res = ASN1ParserOfv3.subjectAltNameToUserName( (SubjectAltName) ext, sAttributeName );
    return res;
  }
}