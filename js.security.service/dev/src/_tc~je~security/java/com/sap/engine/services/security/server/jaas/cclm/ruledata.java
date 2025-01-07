package com.sap.engine.services.security.server.jaas.cclm;


import iaik.asn1.ObjectID;
import java.util.*;


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
 * Class used for encapsulating ClientCertLoginModule rules data.  
 * 
 * @see	    com.sap.engine.services.security.server.jaas.cclm.RuleData;
 * 
 * @since   SP16
 * @version 1.01 2006-03-08
 * @author Rumen Barov i033802
 */

public class RuleData {

  //field names and values 
  public static final String PREFIX_RULE = "Rule";
  public static final String GETUSERFROM_FLD = "GetUserFrom";
  public static final String GETUSERFROM_VAL_SUBJECTNAME = "SubjectName";
  public static final String GETUSERFROM_VAL_WHOLECERT = "WholeCert";
  public static final String GETUSERFROM_VAL_EXPERTMODE = "ExpertMode";
  public static final String ATTRIBUTENAME_FLD = "AttributeName";
  public static final String OID_FLD = "OID";
  public static final String FILTERISSUER_FLD = "FilterIssuer";
  public static final String FILTERSUBJECT_FLD = "FilterSubject";
  public static final String SUBJ_ALTNAME_RFC822NAME = "rfc822Name";
  public static final String SUBJ_ALTNAME_OIDPREFIX = "oid=";
  public final static String LOGONWITHALIAS_FLD = "logonWithAlias";
  
  //fields
  protected String sGetUserFrom;
  protected String sAttributeName;
  protected ObjectID oid;
  protected Vector vectorFilterIssuer;
  protected Vector vectorFilterSubject;
  protected boolean logonWithAlias = false;


  public RuleData(){
    vectorFilterSubject = new Vector();
    vectorFilterIssuer = new Vector();
  }

  /** 
   * Compares all <code>String</code> fields ignoring the case. Filters are compared by their means 
   * (filterSubject for all elements, filterIssuer for elements and order) Uses <code>StringPair.equals</code>
   * No logic for skipping unnecessary fields - for example - when <code>GetUserFrom=wholecert AttributeName</code> is compared too!  
   **/

  public boolean equals( Object o ){
    if ( null == o ) {
      return false;
    }
    if ( ! ( o instanceof RuleData ) ) {
      return false;
    }

    RuleData r = (RuleData) o;
    if ( null != this.getGetUserFrom() && null != r.getGetUserFrom() ) {
      if ( !this.getGetUserFrom().equalsIgnoreCase( r.getGetUserFrom() ) ) {
        return false;
      }
    } else if ( this.getGetUserFrom() != r.getGetUserFrom() ) {
      return false;
    }

    if ( null != this.getAttributeName() && null != r.getAttributeName() ) {
      if ( !this.getAttributeName().equalsIgnoreCase( r.getAttributeName() ) ) {
        return false;
      }
    } else if ( this.getAttributeName() != r.getAttributeName() ) {
      return false;
    }

    if ( null != this.getOid() && null != r.getOid() ) {
      if ( !this.getOid().getName().equalsIgnoreCase( r.getOid().getName() ) ) {
        return false;
      }
    } else if ( this.getOid() != r.getOid() ) {
      return false;
    }

    if ( null != this.getFilterIssuer() && null != r.getFilterIssuer() ) {
      if ( !this.getFilterIssuer().equals( r.getFilterIssuer() ) ) {
        return false;
      }
    } else if ( this.getFilterIssuer() == r.getFilterIssuer() ) {
      return false;
    }

    if ( null != this.getFilterSubject() && null != r.getFilterSubject() ) {
      if ( ( !this.getFilterSubject().containsAll( r.getFilterSubject() ) )
          || this.getFilterSubject().size() != this.getFilterSubject().size() ) {
        return false;
      }
    }
    return true;
  }

  public int hashCode(){
    return this.toString().hashCode();
  }

  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append("\n\t" + RuleData.GETUSERFROM_FLD + " -> " + sGetUserFrom);
    sb.append("\n\t" + RuleData.OID_FLD + " -> " + oid);
    sb.append("\n\t" + RuleData.ATTRIBUTENAME_FLD + " -> " + sAttributeName);
    sb.append("\n\t" + RuleData.FILTERSUBJECT_FLD + " -> " + vectorFilterSubject);
    sb.append("\n\t" + RuleData.FILTERISSUER_FLD + " -> " + vectorFilterIssuer);
    sb.append("\n\t" + RuleData.LOGONWITHALIAS_FLD + " -> " + logonWithAlias + "\n");
    return sb.toString();
  }

  public Properties toOptions(){
    return toOptions( 1 );
  }

  public Properties toOptions( int iRuleNum ){
    Properties res = new Properties();
    if ( 0 < iRuleNum ) {
      if ( this.isConsistent() ) {
        String ruleDot = RuleData.PREFIX_RULE + iRuleNum + ".";
        res.put( ruleDot + RuleData.GETUSERFROM_FLD, this.getGetUserFrom() );
        String attr = this.getAttributeName();

        if ( null != attr && 0 < attr.length() ) {
          res.put( ruleDot + RuleData.ATTRIBUTENAME_FLD, attr );
          ObjectID oid = this.getOid();
          if ( null != oid && 0 < oid.getName().length() ) {
            res.put( ruleDot + RuleData.OID_FLD, oid.getName() );
          }
        }

        String filtIssuer = RuleHelper.Vector2X501Name( this.getFilterIssuer() );
        if ( 0 < filtIssuer.length() ) {
          res.put( ruleDot + RuleData.FILTERISSUER_FLD, filtIssuer );
        }
        String filtSubject = RuleHelper.Vector2X501Name( this.getFilterSubject() );
        if ( 0 < filtSubject.length() ) {
          res.put( ruleDot + RuleData.FILTERSUBJECT_FLD, filtSubject );
        }
      }
    }
    return res;
  }

  public String getAttributeName(){
    return sAttributeName;
  }

  public void setAttributeName( String attributeName ) throws IllegalArgumentException{
    RuleHelper.checkValidAttributeName( attributeName );//throws
    this.sAttributeName = attributeName;
  }

  public Vector getFilterIssuer(){
    return vectorFilterIssuer;
  }

  public void setFilterIssuer( String filterIssuer ){
    vectorFilterIssuer = RuleHelper.X501Name2Vector( filterIssuer );
  }

  public Vector getFilterSubject(){
    return vectorFilterSubject;
  }

  public void setFilterSubject( String sValue ) throws IllegalArgumentException{
    Vector res = new Vector();
    res = RuleHelper.X501Name2Vector( sValue );
    vectorFilterSubject = res;
  }

  public void setFilterSubject( Vector filterSubject ){
    vectorFilterSubject = new Vector( filterSubject );//TODO Does the iterator iterate in the same order? Hope so. Typically does. 
  }

  public String getGetUserFrom(){
    return sGetUserFrom;
  }

  public void setGetUserFrom( String getUserFrom ) throws IllegalArgumentException{
    if ( getUserFrom.equalsIgnoreCase( RuleData.GETUSERFROM_VAL_EXPERTMODE )
        || getUserFrom.equalsIgnoreCase( RuleData.GETUSERFROM_VAL_SUBJECTNAME )
        || getUserFrom.equalsIgnoreCase( RuleData.GETUSERFROM_VAL_WHOLECERT ) ) {
      sGetUserFrom = getUserFrom;
    } else {
      throw new IllegalArgumentException( RuleData.GETUSERFROM_FLD + " field - cannot assign value :" + getUserFrom );
    }
  }

  public ObjectID getOid(){
    return oid;
  }

  public void setOid( ObjectID oid ){
    this.oid = oid;
  }

  public void setOid( String oid ) throws IllegalArgumentException{
    RuleHelper.checkOidName( oid );//may throw IllegalArgumentExcepton 
    this.oid = new ObjectID( oid );
  }

  public void setField( String sFieldName, String sValue ) throws IllegalArgumentException{
    try {
      if ( sFieldName.equalsIgnoreCase( RuleData.ATTRIBUTENAME_FLD ) ) {
        setAttributeName( sValue );
        return;
      }
      if ( sFieldName.equalsIgnoreCase( RuleData.FILTERISSUER_FLD ) ) {
        setFilterIssuer( sValue );
        return;
      }
      if ( sFieldName.equalsIgnoreCase( RuleData.FILTERSUBJECT_FLD ) ) {
        setFilterSubject( sValue );
        return;
      }
      if ( sFieldName.equalsIgnoreCase( RuleData.GETUSERFROM_FLD ) ) {
        setGetUserFrom( sValue );
        return;
      }
      if ( sFieldName.equalsIgnoreCase( RuleData.OID_FLD ) ) {
        setOid( sValue );
        return;
      }
      if ( sFieldName.equalsIgnoreCase( RuleData.LOGONWITHALIAS_FLD ) ) {
        setLogonWithAlias("true".equalsIgnoreCase(sValue));
        return;
      }
    } catch ( IllegalArgumentException e ) {
      throw new InvalidOptionsException( sFieldName + " '" + sValue + "' is not a legal option!\n" + e.getMessage() );
    }
    throw new InvalidOptionsException( sFieldName + " -> " + sValue + " : " + sFieldName
        + " is not a known option name! Allowed names are [" + RuleData.GETUSERFROM_FLD + ", "
        + RuleData.ATTRIBUTENAME_FLD + ", " + RuleData.OID_FLD + ", " + RuleData.FILTERISSUER_FLD + ", "
        + RuleData.FILTERSUBJECT_FLD + "]." );
  }


  /**
   * Check whether the rule data is consistent.
   * This includes the following steps:
   * 1. Check whether field GetUserFrom contains one of the allowed values
   * 2. Check if GetUserFrom = WholeCert. If so - stop checking. Everything is ok.
   * 3. Check the AttributeName and OID fields.
   * @throws InvalidOptionsException
   */
  public void checkConsistency() throws InvalidOptionsException{
    if ( !RuleData.GETUSERFROM_VAL_SUBJECTNAME.equalsIgnoreCase( sGetUserFrom )
        && !RuleData.GETUSERFROM_VAL_WHOLECERT.equalsIgnoreCase( sGetUserFrom )
        && !RuleData.GETUSERFROM_VAL_EXPERTMODE.equalsIgnoreCase( sGetUserFrom ) ) {
      throw new InvalidOptionsException( RuleData.GETUSERFROM_FLD + " has an invalid value : " + sGetUserFrom
          + "\n Rule dump:\n" );
    }

    if ( RuleData.GETUSERFROM_VAL_WHOLECERT.equalsIgnoreCase( sGetUserFrom ) ) {
      return;//no need to check other fields in this case. See the SDD for ClientCertLoginModule for details.
    }

    try {
      if ( RuleData.GETUSERFROM_VAL_SUBJECTNAME.equalsIgnoreCase( sGetUserFrom ) ) {
        RuleHelper.checkValidAttributeName( sAttributeName );//throws
      }

      if ( RuleData.GETUSERFROM_VAL_EXPERTMODE.equalsIgnoreCase( sGetUserFrom ) ) {
        if ( null == oid ) {
          throw new InvalidOptionsException( RuleData.OID_FLD + " cannot be null when " + RuleData.GETUSERFROM_FLD
              + " is set to " + RuleData.GETUSERFROM_VAL_EXPERTMODE );
        }
        RuleHelper.checkValidAttributeName( sAttributeName, getGetUserFrom() );//throws
        RuleHelper.checkOidName( oid.getName() );
      }
    } catch ( IllegalArgumentException e ) {
      throw new InvalidOptionsException( e.getMessage() );
    }
  }

  public boolean isConsistent(){
    try {
      checkConsistency();
    } catch ( IllegalArgumentException e ) {
      return false;
    }
    return true;
  }

  public boolean isFilterPassedByCert( java.security.cert.X509Certificate cert ){
    if ( !vectorFilterSubject.isEmpty() ) {
      if ( !isFilterSubjectPassed( cert ) )
        return false;
    }

    if ( !vectorFilterIssuer.isEmpty() ) {
      if ( !isFilterIssuerPassed( cert ) )
        return false;
    }
    return true;
  }

  public boolean isFilterSubjectPassed( Vector v ){
    if ( !v.containsAll( vectorFilterSubject ) ) {
      return false;
    }
    return true;
  }

  public boolean isFilterSubjectPassed( java.security.cert.X509Certificate cert ){
    String subjDN = cert.getSubjectDN().getName();
    Vector v = RuleHelper.X501Name2Vector( subjDN );
    return isFilterSubjectPassed( v );
  }

  public boolean isFilterIssuerPassed( java.security.cert.X509Certificate cert ){
    String issuerDN = cert.getIssuerDN().getName();
    Vector v = RuleHelper.X501Name2Vector( issuerDN );
    return isFilterIssuerPassed( v );
  }

  public boolean isFilterIssuerPassed( Vector v ){
    if ( vectorFilterIssuer.isEmpty() ) {
      return true;
    }

    if ( v.size() != vectorFilterIssuer.size() ) {
      return false;
    }

    for ( int i = 0; i < v.size(); i++ ) {
      Map.Entry e1 = (Map.Entry) v.get( i );
      Map.Entry e2 = (Map.Entry) vectorFilterIssuer.get( i );
      if ( !e1.equals( e2 ) ) {
        return false;
      }
    }
    return true;
  }


/**
 * @return Returns the bLogonWithAlias.
 */
public boolean isLogonWithAlias() {
	return logonWithAlias;
}
/**
 * @param logonWithAlias The bLogonWithAlias to set.
 */
public void setLogonWithAlias(boolean logonWithAlias) {
	this.logonWithAlias = logonWithAlias;
}
}
