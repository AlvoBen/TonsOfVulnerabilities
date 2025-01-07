/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInformation_LD_HUM
Version         : 1.0
Created On      : 11/06/2020
Function        : This class is used to fetch results from Salesforce Database
Test Class      : UserAssociatedInformation_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0                         Rahul Krishan 
* 2.0                         Ashish Kumar                                         04/02/2021 
* 3.0                         Ashish Kumar                                         22/03/2021                  Added methods for getting details realted to popup operations 
* 4.0                         Kajal Namdev                                         05/07/2021                  Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
* 5.0      					  Kajal Namdev                                         08/18/2021                  Removed limit from single record query
* 6.0                         Kajal Namdev                                         08/20/2021          	 Added limitOne in query
* 6.0                         Joel George                                          11/10/2021          	 Removing unnecessary catch block to improve code coverage
******************************************************************************************************************************************************************************/

public with sharing class UserAssociatedInformation_LD_HUM {
    
    public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    public static final Integer iLimitOne = GLOBAL_CONSTANT_LH_HUM.iLimitONe;
    
    /**
*  convert Date to String
*  @name setDateToString
*  @param Date
*  @return String
*/  
    public static String setDateToString(Date dUserInputDate)
    {       
        Date d = dUserInputDate;
        Datetime dt = Datetime.newInstance(d.year(), d.month(),d.day());
        String dateString = dt.format(GLOBAL_CONSTANT_LH_HUM.HUMDateFormat);
        return dateString ;
    }
    
    /**
*  Gets list of User Information using wrapper class list
*  @name getUserInformationDTO
*  @param String 
*  @return List <UserAssociatedInformation_DTO_HUM>
*/
    public static List<UserAssociatedInformation_DTO_HUM> getUserInfo(String sAccountId)
    {
        List<UserAssociatedInformation_DTO_HUM>  lstUserInformation  = new List<UserAssociatedInformation_DTO_HUM>();
        try
        {        
            List<Account> lstAccount = [select Account_Security_Access__c, Account_Security_Answer__c, Account_Security_EffectiveDate__c, Account_Security_EndDate__c, Account_Security_Question__c, Deceased_Date__c from Account where Id=: sAccountId Limit: iLimitOne];
            UserAssociatedInformation_DTO_HUM userInfo;
            
            if(lstAccount.size() > 0){
                for(Account a:lstAccount){  
                    Boolean isDeceased = false;
                    
                    userInfo = new UserAssociatedInformation_DTO_HUM();
                    userInfo.DocumentType = GLOBAL_CONSTANT_LH_HUM.DOCUMENT_TYPE.get('PWD');
                    if(String.isBlank(lstAccount[0].Deceased_Date__c))
                    {
                        userInfo.EffectiveDate = a.Account_Security_EffectiveDate__c != null?setDateToString(a.Account_Security_EffectiveDate__c):'';
                        userInfo.TerminationDate = a.Account_Security_EndDate__c != null?setDateToString(a.Account_Security_EndDate__c):'';
                        userInfo.SecurityQuestion = a.Account_Security_Question__c == null?'': a.Account_Security_Question__c;
                        userInfo.Answer = a.Account_Security_Answer__c == null?'':a.Account_Security_Answer__c;
                        userInfo.UserValue = a.Account_Security_Access__c == null ?'':a.Account_Security_Access__c;
                        userInfo.isDeceased=isDeceased;
                    }
                    else
                    {
                        isDeceased = true;
                        userInfo.isDeceased=isDeceased;
                    }
                    
                    if(userInfo.TerminationDate != '')
                    {
                        userInfo.UserValue = a.Account_Security_Access__c == null || a.Account_Security_EndDate__c < Date.Today()?'':a.Account_Security_Access__c;
                    }
                    if(String.isBlank(userInfo.UserValue) || isDeceased == true) 
                    {
                        userInfo.EffectiveDate = '';
                        userInfo.TerminationDate = '';
                        userInfo.SecurityQuestion = '';
                        userInfo.Answer = '';
                        userInfo.UserValue ='';
                    }
                    userInfo.PersonofAuthority = '';
                    userInfo.IndexNumber = 1;     
                    lstUserInformation.add(userInfo);
                }
            } 
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_LC_HUM', 'getUserInformationDTO');
        }
        return lstUserInformation;
    }

    public static List<Account> getAccount(String sAccountId)
    {
        return [select Enterprise_Id__c from Account where Id=: sAccountId Limit: iLimitOne];
    }
    
    public static List<User> getUser(String userId)
    {
        return [Select Network_User_Id__c from User where ID=:userId ];
    }

    @AuraEnabled
    public static boolean performPopupDetailsUpdate(String popupDetailsWrapper,String recId)
    {   
		return UserAssociatedInformationPassword_LD_HUM.performPopupDetailsUpdate(popupDetailsWrapper, recId);
    
    }

    @AuraEnabled
    public static PopupDetailsWrapper getPopupOperationValue(String sAccountId)
    {	
        try{
            Account accRecord = [select Account_Security_Access__c, Account_Security_Answer__c, Account_Security_EffectiveDate__c, Account_Security_EndDate__c, Account_Security_Question__c, Deceased_Date__c from Account where Id=: sAccountId Limit: iLimitOne];
            PopupDetailsWrapper wrapPopup= new PopupDetailsWrapper();
            if(accRecord!=null)
            {	
                wrapPopup.question = accRecord.Account_Security_Question__c;	
                wrapPopup.answer = accRecord.Account_Security_Answer__c;
                wrapPopup.password = accRecord.Account_Security_Access__c;
                wrapPopup.effectiveDate= accRecord.Account_Security_EffectiveDate__c;
                wrapPopup.terminatedDate= accRecord.Account_Security_EndDate__c;
            }
            return wrapPopup;  
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_LD_HUM', 'getPopupOperationValue');
            return null;
        }
    }
    
    public class PopupDetailsWrapper{

        @AuraEnabled public String question;
        @AuraEnabled public String answer;
        @AuraEnabled public String password;
        @AuraEnabled public Date effectiveDate;
        @AuraEnabled public Date terminatedDate;
    }
}