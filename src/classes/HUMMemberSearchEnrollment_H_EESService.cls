/*****************************************************************************************************************************************************************************
Apex Class Name : HUMMemberSearchEnrollment_H_EESService
Version         : 1.0
Created On      : 10/21/2022
Function        : This class provide request body creation method for the enhanced EESService
Test Class      : HUMMemberSearchEnrollment_T_EESService

Modification Log: 
 * Developer Name           Code Review                  Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Nilesh Gadkar                                        10/21/2022                  Original Version
*/



public with sharing class HUMMemberSearchEnrollment_H_EESService {
    
    public static final string INVALID_COMBINATION = 'No results were found in the system with the matching criteria. Please search again using one of the following: First Name + Last Name + Birth Date + State OR SSN';
    public static final string INVALID_DOB = 'Please enter valid Date in the format MM/DD/YYYY in Birthdate field';
    public static final string MISSING_DOB = 'A Birthdate is required to search for CI/MTV members';
    public static final string INVALID_STATE = 'A State is required to search for CI/MTV members';
    public static final string NO_SSN = 'Please search using one of the following: First Name + Last Name + Birth Date + State OR SSN';
    public static final string NO_RESULT='No Result(s) found';

    Public Static final String HUMBirthDatePattern = '^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$';
    Public Static final String HUMLeapYearFebPattern  = '^(0[2])[- / .](0[1-9]|1[0-9]|2[0-9])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUM30DaysPattern = '^(1[1]|0[4]|0[6]|0[9])[- / .](0[1-9]|1[0-9]|2[0-9]|3[0])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUM31DaysPattern = '^(1[0]|1[2]|0[1]|0[3]|0[5]|0[7]|0[8])[- / .](0[1-9]|1[0-9]|2[0-9]|3[0-1])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUMNonLeapYearFebPattern = '^(0[2])[- / .](0[1-9]|1[0-9]|2[0-8])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String  HUMSearchEnrollmentFirstName = 'Please enter First Name';
    Public Static final String  HUMSearchEnrollmentLastName = 'Please enter Last Name';

    public static final string NAME_PATTERN = '^[a-zA-Z0-9-.\' , &]*$';
    public static final string DOB_PATTERN = '^[0-1]{1}[0-2]{1}/[0-3]{1}[0-9]{1}/[0-9]{4}';

     /*
   * Method Name   :    validateSearchEESServiceInput
   * Description   :    Method to validate search input prior to search Member in EES System                        
   * Return Type   :    void
   * Parameters    :    NA
   */
  public static boolean validateSearchEESServiceInput(string firstName, string lastName, string  dob, string stateCode, string ssn)
  {
    try 
    {
        boolean isFirstNameValid = String.isBlank(firstName) ? false : Pattern.matches(NAME_PATTERN, firstName)? true : false;
        boolean isLastNameValid = String.isBlank(lastName) ? false : Pattern.matches(NAME_PATTERN, lastName)? true : false;
        boolean isDOBValid = validateBirthdate(dob);//Pattern.matches(DOB_PATTERN, dob);
        boolean isStateValid = Pattern.matches('^[a-zA-Z]{2,2}+$', stateCode) ? true : false;
        boolean isSSNValid = Pattern.matches('^[0-9]{9}',ssn) ? true : Pattern.matches('^[0-9]{3}-[0-9]{3}-[0-9]{3}',ssn) ? true : false;
        boolean success = true;
        if(Pattern.matches('^[0]{9}',ssn))
        {
            isSSNValid = false;
        }
        if((isFirstNameValid && isLastNameValid && isDOBValid && isStateValid && string.isBlank(ssn))
        || (isSSNValid && string.isBlank(firstName) && string.isBlank(lastName) && string.isBlank(dob) && !isStateValid))
            {
                success = true;
            }
        else if(string.isNotEmpty(ssn))
            {
                if(string.isNotEmpty(firstName) || string.isNotEmpty(lastName) || string.isNotEmpty(dob) || isStateValid)
                {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING,INVALID_COMBINATION,true);   
                }
                else {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING,NO_RESULT,true);
                }
                success = false;
            }
        else if(string.isEmpty(ssn))
            {
                if(string.isEmpty(firstName) && string.isEmpty(lastName) && string.isEmpty(dob) && !isStateValid)
                    {
                        HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING,NO_SSN,true);
                    }
                else 
                    {
                        if(!isFirstNameValid) 
                            {
                                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING,HUMSearchEnrollmentFirstName,true);
                            }
                        if(!isLastNameValid)
                        {
                            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING,HUMSearchEnrollmentLastName,true);
                        }
                        if(String.isEmpty(dob))
                        {
                            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING,MISSING_DOB,true);
                        }
                        else if(!isDOBValid)
                        {
                            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING,INVALID_DOB,true);
                        }
                        if(!isStateValid) 
                        {
                            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.WARNING, INVALID_STATE,true);
                        }
                    }
                success = false;
            }
        return success;
    } 
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchEnrollment_H_EESService', 'validateSearchEESServiceInput');
        HUMExceptionHelper.bShowErrorOnPage = true;
        return false;
    }

  }

  @testVisible private static boolean validateBirthdateLeapYear(String sBirthdate)
    {
        try
        {
            string sDateValue = sBirthdate;
            List<string> lstDate = sDateValue.split('/');
            
            if(lstDate.size() != 3)  return false;
            
            
            Integer iDate = Integer.valueof(lstDate[2]);
            
            if(((0 == math.mod(iDate,4)) && (0 != math.mod(iDate,100)))||((0 == math.mod(iDate,4)) && (0 == math.mod(iDate,100)) && (0 == math.mod(iDate,400))))
            {
                if((!Pattern.matches(HUMLeapYearFebPattern, sBirthdate)) &&
                   (!Pattern.matches(HUM30DaysPattern, sBirthdate)) &&
                   (!Pattern.matches(HUM31DaysPattern, sBirthdate)))  return false;
            }
            else if((!Pattern.matches(HUMNonLeapYearFebPattern, sBirthdate)) &&
                    (!Pattern.matches(HUM30DaysPattern, sBirthdate)) &&
                    (!Pattern.matches(HUM31DaysPattern, sBirthdate)))
            {
                return false;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMMemberSearchEnrollment_H_EESService', 'validateBirthdateLeapYear');
            return false; 
        }
        return true;
    }
    
    public static boolean validateBirthdate(String sBirthdate)
    {
        try
        {
            if(!String.isBlank(sBirthdate))
            {
                if(!Pattern.matches(HUMBirthDatePattern, sBirthdate)) return false;
                
                
                if(!validateBirthdateLeapYear(sBirthdate))    return false;
                
                //for future date checking 
                if(Date.parse(sBirthdate) > System.today()) return false;
            }
            else {
                return false;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchEnrollment_H_EESService', 'validateSearchEESServiceInput');
            return false; 
        }
        return true;
    }

  /*
 * Method Name   :    ESSServiceRequestBody
 * Description   :    Build a request body                        
 * Return Type   :    JSON String
 * Parameters    :    NA
 */
public static string ESSServiceRequestBody(string firstName, string lastName, string  dob, string stateCode)
  {
      string jsonRequest;
      SearchFieldsWrapper gpfm = new SearchFieldsWrapper();
        gpfm.firstName = firstName;
        gpfm.lastName = lastName;
        gpfm.dob = dob;
        gpfm.stateCode = stateCode;
        gpfm.indicators = new indicatorsWrapper();
        RequestJSONBody rjb = new RequestJSONBody();
        rjb.GetPlatformMembers = gpfm;

        jsonRequest = JSON.serialize(rjb);
      return jsonRequest;
  }     
    class RequestJSONBody{
    SearchFieldsWrapper GetPlatformMembers {get;set;}
  }   
    class SearchFieldsWrapper
    {
         string firstName {get; set;}
         string lastName {get;set;}
         string dob {get;set;}
         string stateCode {get; set;}
         indicatorsWrapper indicators {get;set;}
    }
   class indicatorsWrapper {
    list<platform> platforms {get{if(platforms == null) 
        {
            platforms = new List<platform>(); 
            platform lv = new platform();
            lv.platform = 'LV';
            platform em = new platform();
            em.platform = 'EM';
            platforms.add(lv);
            platforms.add(em);
            
        } return platforms; }set;}
}
class platform 
    { 
        string platform {get; set; }
    }
}