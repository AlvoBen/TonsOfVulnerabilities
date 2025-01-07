/*****************************************************************************************************************************************************************************
Apex Class Name : HUMSearchFieldsHelper
Version         : 1.0
Created On      : 04/29/2014
Function        : This is wrapper class which is a copy of HUMSearchFieldsWrapper.
                  It contains all the fields which are part of search criteria as well as result fields.
Test Class      : HUMSearchFieldsHelperTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Tanushree Hazari         17122                       04/08/2014                  Original Version
* Kritika P Popat          17122                       04/08/2014                  Modified Validations according to new Requirement
* Shruthi Karanth          17122                       08/19/2014                  Added Group Number as a field to be encrypted
* Kritika P Popat          17122                       08/21/2014                  Modified Birth Date validation as per Defect-149259
* Manish Kumar             17722                       09/03/2014                  Added consumer id encryption.
* Manish Kumar             18362                       10/01/2014                  Code fix for Defect#155398
* Ranjeeth Nagishetty      18655                       10/18/2014                  Defect Fix related to Postal code Encryption Defect-158600
* Tanushree Hazari         18956                       11/04/2014                  Modified zip code validation for Member and Unknown Account as per REQ- 63682                                                                                      
* Palak Papneja            18956                       11/05/2014                  Modified the code as per defect 18956 
* Tanushree Hazari         19690                       11/26/2014                  Added Last Name validation as per REQ-70935    
* Pradeep Raju             19721                       12/05/2014                  Updated the class at line# 431 & 468 to make use of new custom labels for Defect:161683                                                        
* Santhi Mandava           19937                       12/10/2014                  Incorporated changes as per REQ-81851
* Rajasekhar Reddy M V     20104                       12/29/2014                  Modified the code as per defect# 165051
* Raja Sekhar Reddy M V    20281                       01/10/2015                  Modified Code for Defect#167586
* Manish Kumar             20281                       01/10/2015                  Modified Code for Defect#166184
* Pradeep Raju             21023                       02/18/2015                  Updated the class at line# 200 as per REQ-95965
* Pradeep Veeravali        21913                       03/24/2015                  Updated class at line#778 to add encryption to NPI for Provider
* Santhi Mandava           23667                       06/05/2015                  Fixed CC issue 105-Member id exact match is not working because of cipher cloud trailer part issue.
                                                                                   Replaced exact match with like search in case of member id and suffix.
* Santhi Mandava           348723                      04/02/2019                  Cleanup cipher cloud references
********************************************************************************************************************************************************************************/
public  virtual with sharing class HUMSearchFieldsHelper 
{
    @testVisible private Map<string, string> mapEncryptedResult = new Map<string, string>();
    
    
   /*
    * Method Name   :    HUMSearchFieldsHelper 
    * Description   :    This constructor is used for instantiating SearchWrapper inner class.    
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMSearchFieldsHelper()
    {
        oSearchWrapper = new HUMCustomSearchHelper();
    }
    
    public HUMCustomSearchHelper oSearchWrapper{get; set;}
    
   /*
    * Method Name   :    validateGroupNumber
    * Description   :    This method is used for validating Group Number field whether it is alphanumeric or not 
                         in member search screen  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateGroupNumber()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sGroupNumber))) && (!Pattern.matches(System.Label.HUMAlphaNumericPattern, oSearchWrapper.sGroupNumber))))
            {
                throw new HUMCustomException(system.Label.HumSearchGroupNumberAlphaNumericValidation); 
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateGroupNumber');
            return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validateMemberId
    * Description   :    This method is used for validating  ID field whether it is alphanumeric or not 
                         in Member search screen  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateMemberId()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sMemberId))) && (!Pattern.matches(System.Label.HUMAlphaNumericPattern, oSearchWrapper.sMemberId))))
            {
                throw new HUMCustomException(system.Label.HumSearchMemberIdAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateMemberId');
            return false; 
        } 
        return true;
    }
    
   /*
    * Method Name   :    memberIdLength
    * Description   :    This method is used for checking minimum 3 character length for ID field in member search screen
                         if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean memberIdLength()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sMemberId))) && (oSearchWrapper.sMemberId.length() < Integer.valueOf(System.Label.HUMMemberIdCharatcerLimit))))
            {
                throw new HUMCustomException(system.Label.HumSearchMemberIdCharacterValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'memberIdLength');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validateSuffixLength()
    * Description   :    This method is used for checking minimum 2 character length for suffix field in member search screen.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateSuffixLength()
    {
       try
       { 
            if(!String.isBlank(oSearchWrapper.sSuffix) && oSearchWrapper.sSuffix.length()<Integer.valueOf(System.Label.HUMSuffixCharacterLimit))
            {
                throw new HUMCustomException(system.Label.HUMSearchSuffixCharacterValidation);
            }
       }
       catch(Exception e)
       { 
           HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateSuffixLength');
           return false;
       }
       return true;
    }
    
    /*
    * Method Name   :    validateSuffix()
    * Description   :    This method is used for validating suffix field whether it is numeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateSuffix()
    {
       try
       { 
           if(!String.isBlank(oSearchWrapper.sSuffix) && !oSearchWrapper.sSuffix.isNumeric())
           {
               throw new HUMCustomException(system.Label.HumSearchSuffixNumericValidation);
           }
       }
       catch(Exception e)
       { 
           HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateSuffix');
           return false;
       }
       return true;
    }
    
   /*
    * Method Name   :    validateFirstName()
    * Description   :    This method is used for validating First Name whether it is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateFirstName()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sFirstName))) && (!Pattern.matches(System.Label.HUMAlphaNumericPattern, oSearchWrapper.sFirstName))))
            {
                throw new HUMCustomException(system.Label.HumSearchFirstNameAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
             HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateFirstName');
             return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validateLastName()
    * Description   :    This method is used for validating Last Name to allow alphanumeric,spaces and special characters
                         like ".", "-", "'", ","   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateLastName()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sLastName))) && (!Pattern.matches(System.Label.HUMLastNameAlphaNumericPattern, oSearchWrapper.sLastName))))
            {
                throw new HUMCustomException(system.Label.HumSearchLastNameAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
             HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateLastName');
             return false; 
        }
        return true;
    }
        
    
   /*
    * Method Name   :    lastNameLength
    * Description   :    This method is used for validating minimum 2 character length for Last Name if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean lastNameLength()
    {
        try
        {
            if(!String.isBlank(oSearchWrapper.sLastName) && oSearchWrapper.sLastName.length() < Integer.valueOf(System.Label.HUMFirstNameCharacterLimit))
            {
                throw new HUMCustomException(system.Label.HumSearchLastNameCharacterValidation );
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'lastNamelength');
            return false; 
        }
        return true;
    }
    
    /*
    * Method Name   :    validateBirthdateLeapYear
    * Description   :    This method is used for validating Birthdate field
                         data(considering leap year)   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    @testVisible private boolean validateBirthdateLeapYear()
    {
        try
        {
            string sDateValue = oSearchWrapper.sBirthdate;
            List<string> lstDate = sDateValue.split('/');
            
            if(lstDate.size() != 3)
            {
                throw new HUMCustomException(system.Label.HumMemberSearchBirthDateValidation);
            }
            Integer iDate = Integer.valueof(lstDate[2]);
            
            if(((0 == math.mod(iDate,4)) && (0 != math.mod(iDate,100)))||((0 == math.mod(iDate,4)) && (0 == math.mod(iDate,100)) && (0 == math.mod(iDate,400))))
            {
                if((!Pattern.matches(System.Label.HUMLeapYearFebPattern, oSearchWrapper.sBirthdate)) &&
                    (!Pattern.matches(System.Label.HUM30DaysPattern, oSearchWrapper.sBirthdate)) &&
                    (!Pattern.matches(System.Label.HUM31DaysPattern, oSearchWrapper.sBirthdate)))
                {
                    throw new HUMCustomException(system.Label.HumMemberSearchBirthDateValidation);
                }
            }
            else if((!Pattern.matches(System.Label.HUMNonLeapYearFebPattern, oSearchWrapper.sBirthdate)) &&
                    (!Pattern.matches(System.Label.HUM30DaysPattern, oSearchWrapper.sBirthdate)) &&
                    (!Pattern.matches(System.Label.HUM31DaysPattern, oSearchWrapper.sBirthdate)))
            {
                    throw new HUMCustomException(system.Label.HumMemberSearchBirthDateValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateBirthdateLeapYear');
            return false; 
        }
        return true;
    }
 
    /*
    * Method Name   :    validateBirthdate
    * Description   :    This method is used for validating Birthdate field data  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateBirthdate()
    {
        try
        {
            if(!String.isBlank(oSearchWrapper.sBirthdate))
            {
                if(!Pattern.matches(System.Label.HUMBirthDatePattern, oSearchWrapper.sBirthdate))
                {
                    throw new HUMCustomException(system.Label.HumMemberSearchBirthDateValidation);
                }
                
                if(!validateBirthdateLeapYear())    return false;
                
                //for future date checking 
                if(Date.parse(oSearchWrapper.sBirthdate) > System.today())
                {
                    throw new HUMCustomException(system.Label.HUMSearchBirthDateValidation);
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateBirthdate');
            return false; 
        }
        return true;
    }
    
         
   /*
    * Method Name   :    validateUnknownPostalCode
    * Description   :    This method is used for validating Postal Code for Unknown accounts.  
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateUnknownPostalCode()
    {
        try
        {
            if(!String.isBlank(oSearchWrapper.sPostalCode) && !Pattern.matches(System.Label.HUMUnknownZipcodePattern, oSearchWrapper.sPostalCode))
            {
                throw new HUMCustomException(system.Label.HumZipcodeCharacterLimit);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateUnknownPostalCode');
            return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validatePostalCode
    * Description   :    This method is used for validating Postal Code.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validatePostalCode()
    {
        try
        {
            if(!String.isBlank(oSearchWrapper.sPostalCode) && !Pattern.matches(System.Label.HUMNumericPattern, oSearchWrapper.sPostalCode))
            {
                throw new HUMCustomException(system.Label.HumZipcodeCharacterLimit);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validatePostalCode');
            return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validatePhone
    * Description   :    This method is used for validating Phone
                         contains only 10 digits & checking for only numerical values in Phone.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validatePhone()
    {
        try
        {
            if((!(String.isBlank(oSearchWrapper.sPhone))) && (oSearchWrapper.sPhone.length() < Integer.valueOf(System.Label.HUMPhoneNumberCharacterLimit)))
            {
                throw new HUMCustomException(system.Label.HumPhoneCharacterLimit);
            }
            if((!(String.isBlank(oSearchWrapper.sPhone)) && (!Pattern.matches(String.valueof(System.Label.HUMUSAPhonePattern), oSearchWrapper.sPhone))))
            {
                throw new HUMCustomException(system.Label.HumPhoneValidationForMember);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validatePhone');
            return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validateAgentId
    * Description   :    This method is used for validating Agent Id has numbers only.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateAgentId()
    {
        try
        {
            if(!(String.isBlank(oSearchWrapper.sAgentId)) && !(oSearchWrapper.sAgentId.isNumeric()))
            {
                throw new HUMCustomException(system.Label.HUMAgencySearchAgentIDValidation); 
            }
        }
        catch(Exception e)
        {  
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateAgentId');
            return false;
        }
        return true;
    }
    
    /*
    * Method Name   :    validateAgencyName()
    * Description   :    This method is used for validating Agency Name to allow alphanumeric,spaces and special characters
                         like ".", "-", "'"  in Agent/Broker search 
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateAgencyName()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sAgencyName))) && (!Pattern.matches(System.Label.HUMAlphaNumericPattern, oSearchWrapper.sAgencyName))))
            {
                throw new HUMCustomException(system.Label.HumSearchAgencyNameValidation);
            }
        }
        catch(Exception e)
        {
             HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateAgencyName');
             return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validateAgencySearch
    * Description   :    This method is used for checking if correct search
                         parameters are filled in for agency search.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateAgencySearch()
    {
        try
        {        
            if(String.isBlank(oSearchWrapper.sAgencyName) && String.isBlank(oSearchWrapper.sAgentId) && String.isBlank(oSearchWrapper.sTaxID) &&
            String.isBlank(oSearchWrapper.sLastName) && (oSearchWrapper.sState == System.Label.HUMStateDefaultValue))
            {
                            throw new HUMCustomException(System.Label.HUM_Agency_Search_Fields_Not_Entered);
            }
            
            if(((!String.isBlank(oSearchWrapper.sAgencyName) || (!String.isBlank(oSearchWrapper.sLastName))) && 
            oSearchWrapper.sState == System.Label.HUMStateDefaultValue && String.isBlank(oSearchWrapper.sTaxID) && String.isBlank(oSearchWrapper.sAgentId))||
            (oSearchWrapper.sState != System.Label.HUMStateDefaultValue && String.isBlank(oSearchWrapper.sAgencyName)) ||
            (String.isBlank(oSearchWrapper.sAgencyName) && oSearchWrapper.sState == System.Label.HUMStateDefaultValue &&
            String.isBlank(oSearchWrapper.sTaxID) && String.isBlank(oSearchWrapper.sAgentId)))
            {
                            throw new HUMCustomException(System.Label.HUMAgentSearchInvalidCombination);
            }
         } 
         catch(Exception e)
         { 
             HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateAgencySearch');
             return false;
         }  
         return true;  
    }
    
   /*
    * Method Name   :    validateGroupSearch
    * Description   :    This method is used for checking if correct search
                         parameters are filled in for Group Search.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateGroupSearch()
    {
        try
        {
            if(String.isEmpty(oSearchWrapper.sGroupNumber) && String.isEmpty(oSearchWrapper.sGroupName) && oSearchWrapper.sState == System.Label.HUMStateDefaultValue )
            {
                throw new HUMCustomException(System.Label.HUM_Group_Search_Fields_Not_Entered);  
            }
            
            if((!(String.isEmpty(oSearchWrapper.sGroupName)) && oSearchWrapper.sState == System.Label.HUMStateDefaultValue) ||
            (String.isEmpty(oSearchWrapper.sGroupName) && String.isEmpty(oSearchWrapper.sGroupNumber) && oSearchWrapper.sState != System.Label.HUMStateDefaultValue))
            {
                throw new HUMCustomException(System.Label.HUMGroupSearchInvalidCombination);  
            }
        }            
        catch(Exception e)
        { 
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateGroupSearch');
            return false;
                   
        }
        return true;
        
    }
    
    
   /*
    * Method Name   :    validateProviderSearch
    * Description   :    This method is used for checking if correct search
                         parameters are filled in for provider search.
    * Return Type   :    boolean
    * Parameters    :    NA
    */
     public boolean validateProviderSearch()
     {
         try 
         {
             if((String.isBlank(oSearchWrapper.sPostalCode)) && (String.isBlank(oSearchWrapper.sFacilityName)) &&
                (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sLastName)) &&
                (String.isBlank(oSearchWrapper.sTaxID)) && (oSearchWrapper.sState == System.Label.HUMStateDefaultValue) && 
                ((oSearchWrapper.sSpeciality == System.Label.HUMStateDefaultValue)) && (String.isBlank(oSearchWrapper.sNPI)))
               {
              
                    throw new HUMCustomException(system.Label.HUM_Provider_Search_Fields_Not_Entered);
               }
         }
         catch(Exception e)
         { 
              HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateProviderSearch');
              return false; 
         }
        return true;
     }
    
   /*
    * Method Name   :    validateTaxIDLength
    * Description   :    This method is used for checking character length for Tax ID field if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateTaxIDLength()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sTaxID))) && (oSearchWrapper.sTaxID.length() < Integer.valueOf(System.Label.HUMProviderSearchTaxIDValue))))
            {
                throw new HUMCustomException(System.Label.HumProviderSearchTaxIDLength);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateTaxIDLength');
            return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validateNPILength
    * Description   :    This method is used for checking character length for NPI field if it is not blank.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateNPILength()
    {
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sNPI))) && (oSearchWrapper.sNPI.length() < Integer.valueOf(System.Label.HUMProviderSearchNPIValue))))
            {
                throw new HUMCustomException(System.Label.HumProviderSearchNPILength);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateNPILength');
            return false; 
        }
        return true;
    }
    
   /*
    * Method Name   :    validateTaxID
    * Description   :    This method is used for validating Tax ID is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateTaxID()
    {
         try
         { 
            if(((!(String.isBlank(oSearchWrapper.sTaxID))) && (!Pattern.matches(System.Label.HUMAlphaNumericPattern,oSearchWrapper.sTaxID))))
            {
                throw new HUMCustomException(system.Label.HumSearchTaxIDAlphaNumericValidation);
            }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateTaxID');
              return false; 
         }
        return true;
    }
    
   /*
    * Method Name   :    validateNPI
    * Description   :    This method is used for validating NPI is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateNPI()
    {
         try
         {
            if(((!(String.isBlank(oSearchWrapper.sNPI))) && (!Pattern.matches(System.Label.HUMAlphaNumericPattern,oSearchWrapper.sNPI))))
            {
                throw new HUMCustomException(system.Label.HumSearchNPIAlphaNumericValidation);
            }
         }
         catch(Exception e)
         {
             HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper','validateNPI');
             return false; 
         }
         return true;
    }
    
   /*
    * Method Name   :    validateFacilityName
    * Description   :    This method is used for validating Facility Name is alphanumeric or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateFacilityName()
    { 
        try
        {
            if(((!(String.isBlank(oSearchWrapper.sFacilityName))) && (!Pattern.matches(System.Label.HUMFacilityNamePattern, oSearchWrapper.sFacilityName))))
            {
                throw new HUMCustomException(system.Label.HumSearchFacilityNameAlphaNumericValidation);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateFacilityName');
            return false; 
        }
       
        return true;
    }
    
   /*
    * Method Name   :    validateCBISFirstName
    * Description   :    This method is used for validating First Name is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateCBISFirstName()
    {
         try
         { 
            if((String.isBlank(oSearchWrapper.sFirstName)))
            {
               throw new HUMCustomException(System.Label.HUMSearchEnrollmentFirstName);  
            }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateCBISFirstName');
              return false; 
         }
        return true;
    }
    
   /*
    * Method Name   :    validateCBISLastName
    * Description   :    This method is used for validating Last Name is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateCBISLastName()
    {
         try
         { 
            if((String.isBlank(oSearchWrapper.sLastName)))
                {
                  throw new HUMCustomException(System.Label.HUMSearchEnrollmentLastName);  
                }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateCBISLastName');
              return false; 
         }
        return true;
    }
    
   /*
    * Method Name   :    validateCBISBirthDay
    * Description   :    This method is used for validating Birthdate is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateCBISBirthDay()
    {
         try
         { 
             if((String.isBlank(oSearchWrapper.sBirthdate)))  
                {
                  throw new HUMCustomException(System.Label.HUMSearchEnrollmentBirthDate);  
                }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateCBISBirthDay');
              return false; 
         }
        return true;
    }
    
   /*
    * Method Name   :    validateCBISState
    * Description   :    This method is used for validating State is blank or not.   
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public boolean validateCBISState()
    {
         try
         { 
            if((oSearchWrapper.sState.equals(System.Label.HUMNone)))
                {
                  throw new HUMCustomException(System.Label.HUMSearchEnrollmentState);  
                }
         }  
         catch(Exception e)
         {
              HUMExceptionHelper.logErrors(e, 'HUMSearchFieldsHelper', 'validateCBISState');
              return false; 
         }
        return true;
    }
   
    /*
     * Method Name   :    getMapEncryptedResult
     * Description   :    getter attribute variable for mapEncryptedResult
     * Return Type   :    Map<string, Map<string, string>>
     * Parameters    :    None
     */
    public Map<string, string> getMapEncryptedResult()
    {
        return mapEncryptedResult;
    }
}