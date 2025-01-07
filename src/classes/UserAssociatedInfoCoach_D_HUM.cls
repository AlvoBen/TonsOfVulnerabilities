/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInfoCoach_D_HUM
Version         : 1.0
Created On      : 05/22/2021
Function        : This class is used to fetch results from Salesforce Database
Test Class      : UserAssociatedInfoCoach_T_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

public with sharing class UserAssociatedInfoCoach_D_HUM {
    
    public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    public static final Integer iLimitOne = GLOBAL_CONSTANT_CH_HUM.iLimitONe;
    
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
        String dateString = dt.format(GLOBAL_CONSTANT_CH_HUM.HUMDateFormat);
        return dateString ;
    }
    
    /**
*  Gets list of User Information using wrapper class list
*  @name getUserInformationDTO
*  @param String 
*  @return List <UserAssociatedInfoCoach_DTO_HUM>
*/
    public static List<UserAssociatedInfoCoach_DTO_HUM> getUserInfo(String sAccountId)
    {
        List<UserAssociatedInfoCoach_DTO_HUM>  lstUserInformation  = new List<UserAssociatedInfoCoach_DTO_HUM>();
        try
        {        
            List<Account> lstAccount = [select Account_Security_Access__c, Account_Security_Answer__c, Account_Security_EffectiveDate__c, Account_Security_EndDate__c, Account_Security_Question__c, Deceased_Date__c from Account where Id=: sAccountId Limit: iLimitOne];
            UserAssociatedInfoCoach_DTO_HUM userInfo;
            
            if(lstAccount.size() > 0){
                for(Account a:lstAccount){  
                    Boolean isDeceased = false;

                    userInfo = new UserAssociatedInfoCoach_DTO_HUM();
                    userInfo.DocumentType = GLOBAL_CONSTANT_CH_HUM.DOCUMENT_TYPE.get('PWD');
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
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInfoCoach_C_HUM', 'getUserInformationDTO');
        }
        return lstUserInformation;
    }

    public static List<Account> getAccount(String sAccountId)
    {
        return [select Enterprise_Id__c from Account where Id=: sAccountId  LIMIT: iLimitOne];
    }
    
    public static List<User> getUser(String userId)
    {
        return [Select Network_User_Id__c from User where ID=:userId ];
    }

    public static List<Policy_Member__c> getPolicyMember(Set<String> setValidMemTypes, Set<String> setValidPlatforms, String sAccountId)
    {
        return [Select Id, Name, Policy__r.Source_Cust_Cov_Key__c, 
        (Select  Name From Member_Ids__r Where Type__c IN : setValidMemTypes) 
        From Policy_Member__c
        Where Member__c =: sAccountId AND Policy__r.Platform__c IN : setValidPlatforms ORDER BY CreatedDate DESC LIMIT: limitValue];
    }

    @AuraEnabled
    public static boolean performPopupPasswordDetailsUpdate(String popupDetailsWrapper,String recId)
    {   
    try{
        return UserAssociatedInfoPasswordCoach_D_HUM.performPopupPasswordDetailsUpdate(popupDetailsWrapper, recId);   
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'UserAssociatedInfoCoach_D_HUM', 'performPopupPasswordDetailsUpdate');
        return null;
    }
    
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
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInfoCoach_D_HUM', 'getPopupOperationValue');
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