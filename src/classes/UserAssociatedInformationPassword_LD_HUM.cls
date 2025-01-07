/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInformationPassword_LD_HUM
Version         : 1.0
Created On      : 22/03/2021
Function        : This class is used to perform DML related to popup operations on user associated forms
Test Class      : UserAssociatedInformation_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 3.0                        Ashish Kumar                                         22/03/2021                  Added methods for getting details realted to popup operations 
* 3.1                        Ritik Agarwal                                         11/06/2021                  Added methods for DML on Memberplan and memberCache object 
******************************************************************************************************************************************************************************/

public without sharing class UserAssociatedInformationPassword_LD_HUM {

    @AuraEnabled
    public static boolean performPopupDetailsUpdate(String popupDetailsWrapper,String recId)
    {   
    try{
        UserAssociatedInformation_LD_HUM.PopupDetailsWrapper popupWrap = (UserAssociatedInformation_LD_HUM.PopupDetailsWrapper) JSON.deserialize(popupDetailsWrapper, UserAssociatedInformation_LD_HUM.PopupDetailsWrapper.class);
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
        HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformationPassword_LD_HUM', 'performPopupDetailsUpdate');
        return null;
    }
    
    }

/*
    * Method Name  : UpdateHasVitality()
    * Description  : Method to Update HasVitality field of Policy member object.
    * return Type  : null
    * Parameters : Object
    */
    public static void UpdateHasVitality(MemberPlan[] oPolicyMember){
        try{
            update oPolicyMember ;
        } catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'UserAssociatedInformationPassword_LD_HUM','UpdateHasVitality');
        }

    }
}