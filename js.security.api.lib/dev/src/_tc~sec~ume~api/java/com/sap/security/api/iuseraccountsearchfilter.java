package com.sap.security.api;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * This interface acts as a container for text values to be used as a search
 * filter when calling
 * {@link com.sap.security.api.IUserAccountFactory#search(IUserAccountSearchFilter)}.
 * All attributes are initialized with <code>null</code>.
 * 
 * @author d037363
 * @version 1.0
 */
public interface IUserAccountSearchFilter extends IPrincipalSearchFilter
{
    
   public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IUserAccountSearchFilter.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";   

    /**
     * Set the logonid attribute value to match in the useraccount search
     * @param logonUid The value which is searched in the logonid field of useraccounts
     * @param mode The constants defined in 
	 * {@link com.sap.security.api.ISearchAttribute}
     * @param caseSensitive Set case sensitivity
     */
    public void setLogonUid(String logonUid, int mode, boolean caseSensitive);

    /**
     * Get the logonid attribute value to match in the account search
     * @return The value of the logonid field
     */
    public String getLogonUid();

    /**
     * Get the date after the user logged in attribute value to match in the account search
     * @return The value of the date after the user logged in field
     */
    public Date getLoggedInBetweenDate1();
    
    /**
     * Get the date before the user logged in attribute value to match in the account search
     * @return The value of the date before the user logged in field
     */
    public Date getLoggedInBetweenDate2();
    
    /**
     * Get the date after the user is valid from attribute value to match in the account search
     * @return The value of the date after the user is valid from field
     */
    public Date getValidFromBetweenDate1();
    
    /**
     * Get the date before the user is valid from attribute value to match in the account search
     * @return The value of the date before the user is valid from field
     */
    public Date getValidFromBetweenDate2();
    
    /**
     * Get the date after the user is valid to attribute value to match in the account search
     * @return The value of the date after the user is valid to in field
     */
    public Date getValidToBetweenDate1();
    
    /**
     * Get the date before the user is valid to in attribute value to match in the account search
     * @return The value of the date before the user is valid to field
     */
    public Date getValidToBetweenDate2();

    /**
     * Get the date after the user logged out attribute value to match in the account search
     * @return The value of the date after the user logged out field
     */
    public Date getLogoutBetweenDate1();
    
    /**
     * Get the date before the user logged out attribute value to match in the account search
     * @return The value of the date before the user logged out field
     */
    public Date getLogoutBetweenDate2();

    /**
     * Get the date after the password has been changed attribute value to match in the account search
     * @return The value of the date after the password has been changed field
     */
   public Date getPasswordChangeBetweenDate1();
   
    /**
     * Get the date before the password has been changed attribute value to match in the account search
     * @return The value of the date before the password has been changed field
     */
    public Date getPasswordChangeBetweenDate2();
    
    /**
     * Get the date after the logon failed attribute value to match in the account search
     * @return The value of the date after the logon failed field
     */
    public Date getFailedLogonBetweenDate1();
    
    /**
     * Get the date before the logon failed attribute value to match in the account search
     * @return The value of the date before the logon failed field
     */
    public Date getFailedLogonBetweenDate2();
    
    /**
     * Get the date after the account has been created attribute value to match in the account search
     * @return The value of the date after the account has been created logged in field
     */
    public Date getCreatedBetweenDate1();
    
    /**
     * Get the date before the account has been created attribute value to match in the account search
     * @return The value of the date before the account has been created field
     */
    public Date getCreatedBetweenDate2();
    
    /**
     * Get the failed logon attempts attribute value to match in the account search
     * @return The value of the failed logon attempts field
     */
    public Integer getFailedLogonAttempts();
    
    /**
     * Get the locked attribute value to match in the account search
     * @return The value of locked field
     */
    public Boolean isLocked();
    
    /**
     * Get the password changed required attribute value to match in the account search
     * @return The value of password changed required field
     */
    public Boolean isPasswordChangeRequired();
    
    /**
     * Get the lock reason attribute value to match in the account search
     * @return The value of the lock reason field
     */
    public Integer getLockReason();
    
    /**
     * Set the lock reason attribute value to match in the useraccount search. Following 
     * constants are valid lock reasons:
     * {@link com.sap.security.api.IUserAccount#LOCKED_NO}
     * {@link com.sap.security.api.IUserAccount#LOCKED_AUTO}
     * {@link com.sap.security.api.IUserAccount#LOCKED_BY_ADMIN}
     * @param val reason The value which is searched in the lock reason field of useraccounts
     */
    public void setLockReason( int val);
    
    /**
     * Set the locked attribute value to match in the useraccount search.
     * @param lock The value which is searched in the locked field of useraccounts
     */
    public void setLocked(boolean lock);
    
    /**
     * Set the password changed required attribute value to match in the useraccount search.
     * @param passwordchangerequired The value which is searched in the password changed required field of useraccounts
     */    
    public void setPasswordChangeRequired(boolean passwordchangerequired);
    
    /**
     * Set the failed logon attempts required attribute value to match in the useraccount search.
     * @param val The value which is searched in the failed logon attempts field of useraccounts
     */      
    public void setFailedLogonAttempts(int val);
    
    /**
     * Set the dates between the account has been created as attribute value to match in the useraccount search.
     * 
     * note: date1 and date2 must not be null!
     * 
     * @param date1 The first date value which is searched in the create between field of useraccounts
     * @param date2 The second date value which is searched in the create between field of useraccounts
     */
    public void setCreateBetween(Date date1, Date date2);

    /**
     * Set the dates between the account has been logged in as attribute value to match in the useraccount search.
     * 
     * note: date1 and date2 must not be null!
     * 
     * @param date1 The first date value which is searched in the loggeg in field of useraccounts
     * @param date2 The second date value which is searched in the logged in field of useraccounts
     */
    public void setLoggedInBetween(Date date1,Date date2);

    /**
     * Set the dates between the logon failed as attribute value to match in the useraccount search.
     * @param date1 The first date value which is searched in the failed logon between field of useraccounts
     * @param date2 The second date value which is searched in the failed logon between field of useraccounts
     */
    public void setFailedLogonBetween(Date date1, Date date2);

    /**
     * Set the dates between the password has been created as attribute value to match in the useraccount search.
     * 
     * note: date1 and date2 must not be null!
     * 
     * @param date1 The first date value which is searched in the passsword changed between field of useraccounts
     * @param date2 The second date value which is searched in the password changed between field of useraccounts
     */
    public void setPasswordChangeBetween(Date date1, Date date2);

    /**
     * Set the dates between the account is valid from as attribute value to match in the useraccount search.
     * 
     * <p>Note: date1 and date2 must not be null!
     * 
     * @param date1 The first date value which is searched in the valid from between field of useraccounts
     * @param date2 The second date value which is searched in the valid from between field of useraccounts
     */
    public void setValidFromBetween(Date date1, Date date2);

    /**
     * Set the dates between the account is valid to as attribute value to match in the useraccount search.
     * 
     * <p>Note: date1 and date2 must not be null!
     * 
     * @param date1 The first date value which is searched in the valid to between field of useraccounts
     * @param date2 The second date value which is searched in the valid to between field of useraccounts
     */
    public void setValidToBetween(Date date1, Date date2);

    /**
     * Set the dates between the account logged out as attribute value to match in the useraccount search.
     * 
     * <p>Note: date1 and date2 must not be null!
     * 
     * @param date1 The first date value which is searched in the logout between field of useraccounts
     * @param date2 The second date value which is searched in the logout between field of useraccounts
     */
    public void setLogoutBetween(Date date1, Date date2);
    
    /**
     * Sets the certificate attribute value to match in the account search
     * @param certificate The certificate to match
     * @throws CertificateException
     * @throws UMException
     */
    public void setCertificate(X509Certificate certificate) throws CertificateException, UMException;
    
    /**
     * Get the certificate attribute value to match in the account search
     * @return The value of the certificate field or <code>null</code>
     */
    public X509Certificate getCertificate();
}
