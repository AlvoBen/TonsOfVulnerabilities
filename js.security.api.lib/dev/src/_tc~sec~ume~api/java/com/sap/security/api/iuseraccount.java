package com.sap.security.api;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 *  Interface to get and set user account data.
 * 
 *  Copyright (c) 2001 Company: SAPMarkets, Inc
 *
 * 
 *  <p><b>NOTE</b>:  get methods returning an object may return null if a valid value is not
 *                   available unless specified otherwise in the respective method description.
 *                   As this interface�can be extended, this interface can be freely used, 
 *                   but must not be implemented.
 *
 *@author     Rajeev Madnawat
 *@created    July 31, 2001
 *@version    1.1
 */

public interface IUserAccount extends com.sap.security.api.IPrincipalMaint
{
        public static final int LOCKED_NO =0;
        public static final int LOCKED_AUTO =1;
        public static final int LOCKED_BY_ADMIN =2;

	
	public static final String    SECURITY_POLICY                = "SecurityPolicy";
	public static final String    SECURITY_POLICY_TYPE_DEFAULT   = "default";
	public static final String    SECURITY_POLICY_TYPE_TECHNICAL = "technical";
	public static final String    SECURITY_POLICY_TYPE_UNKNOWN   = "unknown";
	
        /**
         *  get logon uid (long uid)
         *
         *@return    The LogonUid value
         */
        public String getLogonUid();
        /**
         *  get valid from date
         *
         *@return    The ValidFromDate value
         */
        public Date getValidFromDate();
        /**
         *  Sets the ValidFromDate attribute of the IUserAccount object
         *
         *@param  date  The new ValidFromDate value
         */
        public void setValidFromDate( Date date );
        /**
         *  get valid to date
         *
         *@return    The ValidToDate value
         */
        public Date getValidToDate();

        /**
         *  get the user that belongs to this account
         *
         *@return    The user
         */
        public IUser getAssignedUser() throws UMException;

        /**
         *  Sets the ValidToDate attribute of the IUserAccount object
         *
         *@param  date  The new ValidToDate value
         */
        public void setValidToDate( Date date );
        /**
         *  Gets the Locked attribute of the IUserAccount object
         *@deprecated use {@link IUserAccount#isPasswordLocked()} and {@link IUserAccount#isUserAccountLocked()} instead
         *@return <code>true</code> if the user account is locked
         */
        public boolean isLocked();

        /**
         *  Sets the locked attribute of the IUserAccount object
         *
         * @param  lock  the lock value
         * @param reason specifies the lock reason
         */
        public void setLocked(boolean lock,int reason);

        /**
         * Returns the reason code for account lock.
         *@deprecated use {@link IUserAccount#isPasswordLocked()} and {@link IUserAccount#isUserAccountLocked()} instead
         * @return IUserAccount.LOCKED_NO - not locked, IUserAccount.LOCKED_BY_ADMIN - locked by admin,
         *         IUserAccount.LOCKED_AUTO - locked due to number of failed attempts.
         */
        public int getLockReason();


        /**
         *  get last failed logon time
         *
         *@return    The LastFailedLogonDate value
         */
        public java.util.Date getLastFailedLogonDate();
        /**
         *  set last logon time
         *
         *@param  timeStamp  The new LastFailedLogonDate value
         */
        public void setLastFailedLogonDate( java.util.Date timeStamp );
        /**
         *  get number of failed logon attempts.
         *
         *@return    The FailedLogonAttempts value
         */
        public int getFailedLogonAttempts();
        /**
         *  increase the number of failed logon attempts by 1
         *
         *@param  i  The new FailedLogonAttempts value
         */
        public void setFailedLogonAttempts( int i );
        /**
         *  set the number of failed logon attemps by a parameter
         */
        public void incrementFailedLogonAttempts();
        /**
         *  Description of the Method
         */
        public void resetFailedLogonAttempts();
        /**
         *  get last sucessful logon date
         *  NOTE: This attribute is not automatically updated during login.
         *@deprecated
         *@return    The LastSuccessfulLogonDate value
         */
        public java.util.Date getLastSuccessfulLogonDate();
        /**
         *  set last successful logon date without incrementing the
         *  number of logon counts
         *@deprecated
         *@param  timeStamp  The new LastSuccessfulLogonDate value
         */
        public void setLastSuccessfulLogonDate( java.util.Date timeStamp );

        /**
         *  get number of successful logon attempts
         *  NOTE: This attribute is not automatically updated during login.
         *@deprecated
         *@return    The SuccessfulLogonCounts value
         */
        public int getSuccessfulLogonCounts();

        /**
         *  increase the number of logon counts by 1
         *  and changes the last successful logon date 
         *  implicitly
         *@deprecated
         */
        public void incrementSuccessfulLogonCounts();

        /**
         *  Sets the SuccessfulLogonCounts attribute of the IUserAccount object
         *@deprecated
         *@param  i  The new SuccessfulLogonCounts value
         */
        public void setSuccessfulLogonCounts( int i );
        /**
         *  indicator the need of force change password on next logon default: false if
         *  true, user need to change logon password on next logon
         *
         *@return    The PasswordChangeRequired value
         */
        public boolean isPasswordChangeRequired();
        /**
         *  Gets the LastPasswordChangedDate attribute of the IUserAccount object
         *
         *@return    The LastPasswordChangedDate value
         */
        public Date getLastPasswordChangedDate();
        /**
         *  Sets the PasswordChangeRequired attribute of the IUserAccount object.
         *  <p>Note: <code>IUserAccount.commit()<code> may raise UMException 
         *  for SAP System user with following exception text:
         *  <p>
         * 	<i>Attribute com.sap.security.core.usermanagement|->passwordchangerequired
         *  can only be modified by changing or resetting the password if any
         *  datasource of class com.sap.security.core.persistence.datasource.imp.R3Persistence
         *  is responsible for writing it.</i> 
         * 	
         *  <p> The exception may be raised in following cases
         *  <ol>
         * 	<li>if it is used without using {@link #setPassword(String, String)}
         *      or {@link #setPassword(String)} in the same 
         *  {@link com.sap.security.api.IPrincipalMaint#commit()} transaction.
         *  </li>
         *  <li>if following combination of <code>setPasswordChangeRequired<code> and 
         * 		<code>setPassword(...)</code>
         *    is used:
         *    <ul>
         *  	<li>{@link #setPassword(String)} and setPasswordChangeRequired(<code>false</code>) </li>
         *  	<li>{@link #setPassword(String, String)} and setPasswordChangeRequired(<code>true</code>) </li> 
         * 	  </ul>
         *  </li>
         *  </ol>
         *   
         *@param  chng  The new PasswordChangeRequired value
         */
        public void setPasswordChangeRequired( boolean chng );

		/**
		 * Checks whether the password is disabled.
		 * @return The result of the check
		 */
		public boolean isPasswordDisabled();


		/**
		 *  Disables the password.
		 */
		public void setPasswordDisabled();

        /**
	     *  Changes user password to newpass.  There is no need to know the old password.
	     *  This change of password will force the user to change the password on a
	     *  subsequent logon.  This is used mainly by the administrator during resetting
	     *  a password or adding an user, as opposed to the user changing the password
	     *  him/herself.
	     *  Note: If the password was disabled, it is enabled after this call.
         *  @param  pass  The new Password value
         */
        public void setPassword( String pass ) throws InvalidPasswordException;

        /**
	     *  Changes user password from oldpass to newpass.  The oldpass is validated
	     *  first, then the newpass is set for the user account.  This change of password
	     *  will not force user to change the password again on a subsequent logon.  This is
	     *  used mainly when the user changes the password him/herself, as opposed to
	     *  the administrator changing or resetting the password for the user.  Other
	     *  situation when this is used is when the password expired and the user is
	     *  forced to change the password.
         *  Note: Use IUserAccount.isPasswordDisabled() before calling this.
		 *        If the password is disabled, this method will result in a
 		 *        UMRuntimeException.
         *@param  oldpass  The new Password value
         *@param  newpass  The new Password value
         */
        public void setPassword( String oldpass, String newpass ) throws InvalidPasswordException;

        /**
         *  Returns the user's certificates
         *
         *@return                              certificate array of allowed
         *      certificates or <code>null</code> if the user doesn't have certificates
         *@exception  CertificateException     Description of Exception
         *@exception  UMException  UMException is thrown
         *            is  getCertificates operation is failed for some reason
         */
        public X509Certificate[] getCertificates() throws CertificateException, UMException;


        /**
         *  Stores the user's certificate and creates a mapping
         *
         *@param  certificate                  array of allowed certifiates, pass
         *      <code>null</code> to remove existing mapping
         *@exception  CertificateException     Description of Exception
         *@exception  UMException  UMException is thrown
         *            is setCertificates operation is failed for some reason
         */
        public void setCertificates( X509Certificate[] certificate ) throws CertificateException, UMException;


    /**
         *  Deletes the user's certificate
         *
         *@param  certificate                  array of allowed certifiates, pass
         *      <code>null</code> to remove existing mapping
         *@exception  CertificateException     Description of Exception
         *@exception  UMException  UMException is thrown
         *            is setCertificates operation is failed for some reason
         */
        public void deleteCertificates( X509Certificate[] certificate ) throws CertificateException, UMException;

        /**
         *  compares the stored password with the input password
         *  Note: Use IUserAccount.isPasswordDisabled() before calling this.
         *        If the password is disabled, this method will result in a
         *        UMRuntimeException.
         *
         *@param  pass  Password string
         *@return       true if match , false otherwise
         */
        public boolean checkPassword( String pass );

        /**
         *  compares the stored password with the input password
	   	 * 
	   	 *  Possible return values are:
	   	 *  		ILoginConstants.CHECKPWD_OK
	   	 *  		ILoginConstants.CHECKPWD_WRONGPWD
	   	 *  		ILoginConstants.CHECKPWD_NOPWD
	   	 *  		ILoginConstants.CHECKPWD_PWDLOCKED
	   	 *  		ILoginConstants.CHECKPWD_PWDEXPIRED
         *
         *@param  pass  Password string
         *@return The corresponding return code
         */
        public int checkPasswordExtended( String pass ) throws UMException;
        
        /**
         * returns the creation date of this user account
         */
        public Date created();
        /**
         * returns the last modification date of this user account
         */
        public Date lastModified();

        /** returns lock date */
        public Date lockDate();

        /**
         *
         * @exception FeatureNotAvailableException if feature is not implemented
         * @return hashedPassword as string or null
         */
        public String getHashedPassword() throws UMException;

        /**
         *  set last logout date
         *
         *@param  timeStamp  The new LastSuccessfulLogonDate value
         *  if timeStamp is null a new Date object will be allocated
         *  and measured to the nearest millisecond.
         */
        public void setLastLogoutDate(Date timeStamp);

        /**
         *  get previous sucessful logon date
         *  NOTE: This attribute is not automatically updated during login.
         *@deprecated
         *@return    The PreviousSuccessfulLogonDate value
         */
        public java.util.Date getPreviousSuccessfulLogonDate();

        /**
         * Gets the list of all assigned roles of this principal including parent groups,
         *  grandparent groups,...
         * @param recursive if true returns all parent roles
         * @return all roles for this principal
         */
        public java.util.Iterator getRoles(boolean recursive);

        /**
         * Gets the list of all parent principals including parents, grandparents, ...
         *
         * @return all parent principals of this collection
         */
        public java.util.Iterator getParentGroups(boolean recursive);

       /**
         * Checks if the principal belongs to the passed roleId
         * This method does a recursive search, so if this principal belongs to a
         * collection which is a member of this collection, true is returned.
         * returns true if the principal is directly or indirectly (via role membership)
         * assigned.
         *
         * @param roleId the ID of the role
         */
        public boolean isMemberOfRole(String roleId, boolean recursive);

        /**
         * Checks if the principal belongs to the passed groupId
         * @param uniqueIdOfGroup the ID of the group
         * @param recursive This method does a recursive search, so if this principal belongs to a
         * collection which is a member of this collection, true is returned.
         * returns true if the principal is directly or indirectly (via role membership)
         * assigned.
         * @return true if this account is member of the specified group
         */
        public boolean isMemberOfGroup(String uniqueIdOfGroup, boolean recursive);

        /**
         * Assign this principal to the parent-group with id
         *
         * @param	uniqueIdOfGroup id of the group
         * @exception	UMException
         * @deprecated use {@link IUserMaint#addToGroup(String)} instead
         */
        public void addToGroup(String uniqueIdOfGroup) throws UMException;

        /**
         * Unassign this principal from the parent-group with id
         *
         * @param	uniqueIdOfGroup id of the parent group
         * @exception	UMException
         * @deprecated use {@link IUserMaint#removeFromGroup(String)} instead 
         */
        public void removeFromGroup(String uniqueIdOfGroup) throws UMException;

        /**
         * Assign this principal to the role with uniqueIdOfRole
         *
         * @param	uniqueIdOfRole       id of the role
         * @exception	UMException
         * @deprecated use {@link IUserMaint#addToRole(String)} instead 
         */
        public void addToRole(String uniqueIdOfRole) throws UMException;

        /**
         * Unassign this principal from role with id
         *
         * @param	uniqueIdOfRole       id of the role
         * @exception	UMException
         * @deprecated use {@link IUserMaint#removeFromRole(String)} instead 
         */
        public void removeFromRole(String uniqueIdOfRole) throws UMException;

        /**
         *  Gets the password locked attribute of the IUserAccount object
         *
         *@return <code>true</code> if the user account is locked
         */
        public boolean isPasswordLocked();

        /**
         *  Gets the locked attribute of the IUserAccount object
         *
         *@return <code>true</code> if the user account is locked
         */
        public boolean isUserAccountLocked();
        
        /**
         * Gets the unique id of the user which is assigned to this account.
         * If no user is assigned to this account, null is returned.
         *
         * @return The unique id of the user or null
         */
        public String getAssignedUserID();

        /***
         * Potential candidate in order to assign/unassign users to accounts we
         * would need following methods:
         *
         *     public String getAssignedUser();
         *     public boolean setAssignedUser(String uniqueIdOfUser) throws UMException
         *
         */
}
