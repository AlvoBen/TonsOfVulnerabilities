package com.sap.security.api;

import java.util.Date;
import java.util.Locale;

/**
 * This interface handles UME Security Policy settings.
 *  
 * Note: It is not possible to change the current security policy settings
 *        with the setter methods at runtime. If you want to change these
 *        settings, change the configuration and restart the server.
 *
 *@author	Guenther Wannenmacher
 *@version    1.0
 */

public interface ISecurityPolicy {
	
	//password exception messages


	public final static String ACCOUNT_LOCKED_ADMIN = "ACCOUNT_LOCKED_ADMIN";
	public final static String ACCOUNT_LOCKED_LOGON = "ACCOUNT_LOCKED_LOGON";
	public final static String ALPHANUM_REQUIRED_FOR_PSWD = "ALPHANUM_REQUIRED_FOR_PSWD";
	public final static String CERT_GET_ERROR = "CERT_GET_ERROR";
	public final static String CERT_NOT_UNIQUE = "CERT_NOT_UNIQUE";
	public final static String CHANGE_PASSWORD_NOT_ALLOWED = "CHANGE_PASSWORD_NOT_ALLOWED";
	public final static String MISSING_NEW_PASSWORD = "MISSING_NEW_PASSWORD";
	public final static String MISSING_PASSWORD = "MISSING_PASSWORD";
	public final static String MISSING_PASSWORD_CONFIRM = "MISSING_PASSWORD_CONFIRM";
	public final static String MISSING_UID = "MISSING_UID";
	public final static String MIXED_CASE_REQUIRED_FOR_PSWD = "MIXED_CASE_REQUIRED_FOR_PSWD"; 
	public final static String NEW_PASSWORD_INVALID = "NEW_PASSWORD_INVALID";
	public final static String NEW_PASSWORDS_UNMATCHED = "NEW_PASSWORDS_UNMATCHED";
	public final static String NO_CERTIFICATE = "NO_CERTIFICATE";
	public final static String NO_USER_CERTIFICATE = "NO_USER_CERTIFICATE";
	public final static String NUMERIC_REQUIRED = "NUMERIC_REQUIRED";
	public final static String OLDPASSWORD_IN_NEWPASSWORD = "OLDPASSWORD_IN_NEWPASSWORD";
	public final static String PASSWORD_CONTAINED_IN_HISTORY = "PASSWORD_CONTAINED_IN_HISTORY";
	public final static String PASSWORD_EXPIRED = "PASSWORD_EXPIRED";
	public final static String PASSWORD_MAX_IDLE_TIME_EXCEEDED = "PASSWORD_MAX_IDLE_TIME_EXCEEDED";
	public final static String PASSWORD_TOO_LONG = "PASSWORD_TOO_LONG";
	public final static String PASSWORD_TOO_SHORT = "PASSWORD_TOO_SHORT";
	public final static String SAPSTAR_ACTIVATED = "SAPSTAR_ACTIVATED";
	public final static String SPEC_CHARS_REQUIRED_FOR_PSWD = "SPEC_CHARS_REQUIRED_FOR_PSWD";
	public final static String USER_AUTH_FAILED = "USER_AUTH_FAILED";
	public final static String USER_IS_CURRENTLY_NOT_VALID = "USER_IS_CURRENTLY_NOT_VALID";
	public final static String USERID_CONTAINED_IN_PASSWORD = "USERID_CONTAINED_IN_PASSWORD";
	public final static String USERID_NOT_FOUND = "USERID_NOT_FOUND";
	public final static String WRONG_OLD_PASSWORD = "WRONG_OLD_PASSWORD";
	public final static String WRONG_PASSWORD = "WRONG_PASSWORD";
	public final static String LOGONID_IS_NULL = "LOGONID_IS_NULL";
	public final static String LOGONID_TOO_LONG = "LOGONID_TOO_LONG";
	public final static String LOGONID_TOO_SHORT = "LOGONID_TOO_SHORT";
	public final static String WHITESPACES_NOT_ALLOWED = "WHITESPACES_NOT_ALLOWED";
	public final static String SPEC_CHARS_REQUIRED_FOR_LOGONID = "SPEC_CHARS_REQUIRED_FOR_LOGONID";
	public final static String MIXED_CASE_REQUIRED_FOR_LOGONID = "MIXED_CASE_REQUIRED_FOR_LOGONID";
	public final static String ALPHANUM_REQUIRED_FOR_LOGONID = "ALPHANUM_REQUIRED_FOR_LOGONID";

	

    /**
     *  Returns the PasswordSuccessfulCheckDateDefault attribute of the ISecurityPolicy object.
     *  Default value is 12/31/9999
     *
     *@return  The PasswordSuccessfulCheckDateDefault value
     */
	public Date getPasswordSuccessfulCheckDateDefault();

    /**
     *  Sets the PasswordSuccessfulCheckDateDefault attribute of the ISecurityPolicy object.
     *  This value is used to enforce the PasswordMaxIdleTime policy if no successful logon date is
     *  available (because no logon succeeded yet).
     *
     *@param  newDate  The new PasswordSuccessfulCheckDateDefault value
	 *@deprecated
     */
	public void setPasswordSuccessfulCheckDateDefault(Date newDate);
	
    /**
     *  Sets the PasswordLastChangeDateDefault attribute of the ISecurityPolicy object.
     *  This value is used to enforce the PasswordExpiredDays policy if no password change 
     *  date is available (because it is not changed yet).
     *
     *@param  newDate  The new PasswordLastChangeDateDefault value
	 *@deprecated
     */
	public void setPasswordPasswordLastChangeDateDefault(Date newDate);

	/**
     *  Returns the PasswordLastChangeDateDefault (MM/DD/YYYY) attribute of the ISecurityPolicy object.
     *
     *@return  The PasswordLastChangeDateDefault value
     */
	public Date getPasswordLastChangeDateDefault();	
	
    /**
     *  Sets the PasswordMaxIdleTime(in days) attribute of the ISecurityPolicy object.
     *  If a password is not successfully used for the set period, the password is expired
     *  and can't be used for logon until a new password is set by an administrator.
     *  Default value is 0 which means no expiration.
     *
     *@param  time  The new PasswordMaxIdleTime value
	 *@deprecated
     */
    public void setPasswordMaxIdleTime(int time);
    
    /**
     *  Returns the PasswordMaxIdleTime(in days) attribute of the ISecurityPolicy object.
     *  Default value is 0 which means no expiration.
     *
     *@return  The PasswordMaxIdleTime value
     */
    public int  getPasswordMaxIdleTime();
	
    /**
     *  Check the validity of the password against the password policy
     *  Set the logonID of the user before with the method setUserName().
     *
     *@param  pass  password to check
     *@return       boolean is the password valid
     * @deprecated please use isPasswordValid(String pass, String logonId) 
     */
    public boolean isPasswordValid(String pass) throws InvalidPasswordException;

    /**
     *  Check the validity of the password against the password policy
     *
     *@param  pass    password to check
     *@param  logonId  logonID to check against the password.
     *@return       boolean is the password valid
     */
    public boolean isPasswordValid(String pass, String logonId) throws InvalidPasswordException;


    /**
     *  Check the validity of the password against the password policy.
     *  Set the logonID of the user before with the method setUserName().
     *
     *@param  pass  password to check
     *@return the error message why the password is not valid or <code>null</code>
     *@deprecated please use validatePassword(String pass, String uid)
     */
    public String validatePassword(String pass);

    /**
     *  Check the validity of the password against the password policy.
     *
     *@param  pass  password to check
     *@return the error message why the password is not valid or <code>null</code>
     */
    public String validatePassword(String pass, String uid);

    /**
     * Generate a password for this security policy.
     * @return  a valid password
     */
    public String generatePassword();

	/**
	 * Generate a password for this security policy.
	 * @param userid the user id to generate a password for
	 * @return  a valid password
	 */
	public String generatePassword(String userid);

    /**
     *  Gets the Number of Allowed Logon Attempts (AllowedLogonAttempts) attribute
     *  of the ISecurityPolicy object
     *
     *@return    The AllowedLogonAttempts value
     */
    public int getLockAfterInvalidAttempts();

    /**
     *  Sets the Number of Allowed Logon Attempts (AllowedLogonAttempts) attribute
     *  of the ISecurityPolicy object
     *
     *@param  count  The new AllowedLogonAttempts value
	 *@deprecated
     */
    public void setLockAfterInvalidAttempts(int count);

    /**
     *  Gets the AutoUnlockTime(in seconds) attribute of the ISecurityPolicy object
     *
     *@return    The AutoUnlockTime value
     */
    public int getAutoUnlockTime();

    /**
     *  Sets the AutoUnlockTime(in seconds) attribute of the ISecurityPolicy object
     *
     *@param  time  The new AutoUnlockTime value
	 *@deprecated
     */
    public void setAutoUnlockTime(int time);

    /**
     *  Gets the Password Minimum Length(PasswordMinLength) attribute of the
     *  ISecurityPolicy object
     *
     *@return    The PasswordMinLength value
     */
    public int getPasswordMinLength();

    /**
     *  Sets the Password Minimum Length(PasswordMinLength) attribute of the
     *  ISecurityPolicy object
     *
     *@param  length  The new PasswordMinLength value
	 *@deprecated
     */
    public void setPasswordMinLength(int length);

    /**
     *  Gets the Password Maximum Length(PasswordMaxLength) attribute of the
     *  ISecurityPolicy object
     *
     *@return    The PasswordMaxLength value
     */
    public int getPasswordMaxLength();

    /**
     *  Sets the Password Maximum Length(PasswordMaxLength) attribute of the
     *  ISecurityPolicy object
     *
     *@param  length  The new PasswordMaxLength value
	 *@deprecated
     */
    public void setPasswordMaxLength(int length);

    /**
     *  Return a boolean indicating whether the password need to be different from
     *  the user id(uid).
     *
     *@return    a boolean indicating whether the password need to be different
     *      from the user id(uid).
     */
    public boolean getUseridInPasswordAllowed();

    /**
     *  Sets the boolean indicating whether the password need to be different from
     *  the user id(uid).
     *
     *@param  pwnur  a boolean indicating whether the password need to be different
     *      from the user id(uid).
	 *@deprecated
     */
    public void setUseridInPasswordAllowed(boolean pwnur);

    /**
     *  Return a boolean indicating whether the password need to be different from
     *  the old password.
     *
     *@return    a boolean indicating whether the password need to be different
     *      from old password.
     */
    public boolean getOldInNewAllowed();

    /**
     * Returns a boolean indicating whether users who have a productive password 
     * that doesn't match the security policy settings have to change it during 
     * password logon.
     * 
     * @return whether the plolicy is enforced at logon or not.
     */
    public boolean getEnforcePolicyAtLogon();
    
    /**
     * Sets whether users who have a productive password that doesn't match the 
     * security policy settings have to change it during password logon.
     * @param enforcePolicyAtLogon specifies whether the plolicy is enforced at logon or not.
     * @deprected
     */
    public void setEnforcePolicyAtLogon(boolean enforcePolicyAtLogon);
    
    /**
     *  Sets the boolean indicating whether the password need to be different from
     *  the old password.
     *
     *@param allow   a boolean indicating whether the password need to be different
     *      from old password.
 	 *@deprecated
    */
    public void setOldInNewAllowed(boolean allow);

    /**
     *  Return a number indicating how many capical and lower case letters the password
     * must contain.
     *
     *@return    number of required both capical and lower case letters.
     */
    public int getPasswordMixCaseRequired();

    /**
     * Sets the number indicating how many capical and lower case letters the password
     * must contain.
     *
     *@param  number  the number of both capical and lower case letters.
	 *@deprecated
     */
    public void setPasswordMixCaseRequired(int number);

	/**
	 *  Return a number indicating how many capical and lower case letters the logon id
	 * must contain.
	 *
	 *@return    number of required both capical and lower case letters.
	 */
	public int getLogonIdLowerCaseRequired();

	/**
	 * Sets the number indicating how many capical and lower case letters the logon id
	 * must contain.
	 *
	 *@param  number  the number of both capical and lower case letters.
	 *@deprecated
	 */
	public void setLogonIdLowerCaseRequired(int number);

    /**
     * Return the number indicating how many alphabets and numeric values the password
     * must contain.
     *
     *@return    number of required both alphabets and numeric values.
     */
    public int getPasswordAlphaNumericRequired();

    /**
     * Sets the number indicating how many alphabets and numeric values the password
     * must contain.
     *
     *@param  number  the number of required both alphabets and numeric values.
	 *@deprecated
     */
    public void setPasswordAlphaNumericRequired(int number);

	/**
	 * Return the number indicating how many alphabets and numeric values the logon id
	 * must contain.
	 *
	 *@return    number of required both alphabets and numeric values.
	 */
	public int getLogonIdNumericDigitsRequired();

	/**
	 * Sets the number indicating how many alphabets and numeric values the logon id
	 * must contain.
	 *
	 *@param  number  the number of required both alphabets and numeric values.
	 *@deprecated
	 */
	public void setLogonIdNumericDigitsRequired(int number);

	/**
	 * Return the number indicating how many special characters the password
	 * must contain.
	 *
	 *@return    a number indicating whether the password has to include both
	 *      alphabets and numeric values
	 *  Return the boolean indicating whether the password has to include special
	 *  character
	 */
	public int getPasswordSpecialCharRequired();

	/**
	 * Sets the number indicating how many special characters the password
	 * must contain.
	 *
	 *@param  number  the number of required special characters.
	 *@deprecated
	 */
	public void setPasswordSpecialCharRequired(int number);

    /**
     * Return the numbers of days the password is set to expired. If the return value
     * is 0, that means the password does not expired.
     *
     *@return    The numbers of days the password is set to expired
     */
    public int getPasswordExpiredDays();

	/**
	 * Returns a comma-separated list of impermissible passwords. The list contains character
	 * combinations or terms, where the asterisk (*) and question mark (?) can be used as
	 * placeholders. Asterisk (*) stands for a character sequence, and the question mark (?)
	 * for a single character. 
	 * @return impermissible password pattern 
	 */
	public String getPasswordImpermissiblePattern();

	/**
	 * Gets the PasswordHistory attribute of the SecurityPolicy object. If the return value
	 * is 0, that means that no password history is used.
	 *
	 * @return The PasswordHistory value
	 */
	public int getPasswordHistory();

    /**
     *  Sets the numbers of days the password is to be expired. If the value is set
     * to be 0, that means the password will not expired.
     *
     *@param  days  The numbers of days the password is set to expired
	 *@deprecated
     */
    public void setPasswordExpiredDays(int days);

	/**
	 * Sets the PasswordHistory attribute of the SecurityPolicy object
	 *
	 * @param length  The new PasswordHistory value
	 *@deprecated
	 */
	public void setPasswordHistory(int length);

    /**
     *  Return the boolean value indicating whether the password is allowed to
     *  be changed.
     *
     *@return    A boolean value indicating whether the password is allowed to
     *      be changed
     */
    public boolean getPasswordChangeAllowed();

    /**
     *  Sets the boolean value indicating whether the password is allowed to be changed.
     *
     *@param  pca  The boolean value indicating whether the password is allowed to
     *      changed
	 *@deprecated
     */
    public void setPasswordChangeAllowed(boolean pca);

	/**
	 *  Return the boolean value indicating whether the password is enforced to
	 *  change at the first logon.
	 *
	 *@return    A boolean value indicating whether the password is enforced to
	 *     change
	 *@deprecated
	 */
	public boolean getPasswordChangeRequired();

	/**
	 *  Sets the boolean value indicating whether the password is enforced to change.
	 *
	 *@param  pca  The boolean value indicating whether the password is enforced to 
	 *      change
	 *@deprecated
	 */
	public void setPasswordChangeRequired(boolean pca);
	
    /**
     *  Return the integer value indicating the minimum length of a logon id.
     *
     *@return    The integer value indicating the minimum length of a logon id.
     */
    public int getLogonIdMinLength();

    /**
     *  Sets the integer value indicating the minimum length of a logon id.
     *
     *@param  length  integer value indicating the minimum length of a logon id.
	 *@deprecated
     */
    public void setLogonIdMinLength(int length);

    /**
     *  Return the integer value indicating the maximum length of a logon id.
     *
     *@return    The integer value indicating the maximum length of a logon id.
     */
    public int getLogonIdMaxLength();

    /**
     *  Sets the integer value indicating the maximum length of a logon id.
     *
     *@param  length  integer value indicating the maximum length of a logon id.
	 *@deprecated
     */
    public void setLogonIdMaxLength(int length);

	/**
	 *  Return a number indicating how many special characters the logon id
	 * must contain.
	 *
	 *@return    number of required special characters.
	 */
	public int getLogonIdSpecialCharRequired();

	/**
	 * Sets the number indicating how many special characters the logon id
	 * must contain.
	 *
	 *@param  number  the number of special characters.
	 *@deprecated
	 */
	public void setLogonIdSpecialCharRequired(int number);


    /**
     *  Check the validity of the logon id against the security policy
     *
     *@param  	logonId  logonid to check
     *@return  	boolean true if the logon id is valid
     *@throws	InvalidLogonIdException if the logon id is invalid
     */
    public boolean isLogonIdValid(String logonId) throws InvalidLogonIdException;

	/**
	 * Generate a logon id for this security policy.
	 * @return  a valid logon id
	 * @throws 	InvalidLogonIdException if security policy configuration
	 * 			cannot be fullfilled
	 */
	public String generateLogonId() throws InvalidLogonIdException;

	/**
	 *@deprecated
	 */
	public boolean getCertLogonRequired();

	/**
	 *@deprecated
	 */
	public void setCertLogonRequired(boolean clr);

	/**
	 *@deprecated
	 */
	public int getCookieLifeTime();

	/**
	 *@deprecated
	 */
	public void setCookieLifeTime(int lifeTime);

	/**
	 *@deprecated
	 */
	public void setUserName(String id);

	/**
	 * Return a localized password exception message for the given key.
	 * @param locale the locale
	 * @param ex the exception which hold the key as message
	 * @return the localized message or <null> when not available
	 */
	public String getLocalizedMessage (Locale locale, Exception ex);
	
	/**
	 * Return a localized password exception message for the given key.
	 * @param locale the locale
	 * @param key the key
	 * @return the localized message or <null> when not available
	 */
	public String getLocalizedMessage (Locale locale, String key);

}
