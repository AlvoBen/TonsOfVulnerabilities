/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInfoPasswordCoach_D_HUM
Version         : 1.0
Created On      : 05/22/2021
Function        : This class is used to perform DML related to popup operations on user associated forms
Test Class      : UserAssociatedInfoCoach_T_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

public without sharing class UserAssociatedInfoPasswordCoach_D_HUM{

    @AuraEnabled
    public static boolean performPopupPasswordDetailsUpdate(String popupDetailsWrapper,String recId)
    {   
    try{
        UserAssociatedInfoCoach_D_HUM.PopupDetailsWrapper popupWrap = (UserAssociatedInfoCoach_D_HUM.PopupDetailsWrapper) JSON.deserialize(popupDetailsWrapper, UserAssociatedInfoCoach_D_HUM.PopupDetailsWrapper.class);
        Account updatedAccountObj= new Account();
        updatedAccountObj.id = recId;
        updatedAccountObj.Account_Security_Access__c=popupWrap.password ;
        updatedAccountObj.Account_Security_Answer__c=popupWrap.answer;
        updatedAccountObj.Account_Security_EffectiveDate__c=popupWrap.effectiveDate ;
        updatedAccountObj.Account_Security_EndDate__c=popupWrap.terminatedDate ;
        updatedAccountObj.Account_Security_Question__c=popupWrap.question ;

        Database.SaveResult sr =Database.update(updatedAccountObj);

        return sr.isSuccess() ? true : false;
        
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'UserAssociatedInfoPasswordCoach_D_HUM', 'performPopupPasswordDetailsUpdate');
        return null;
    }
    
    }

}