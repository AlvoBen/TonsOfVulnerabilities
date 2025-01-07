/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfile_A_HUM 
Version          : 1.0
Created Date     : 10/29/2015
Function         : This is abstract helper class for GroupBillingProfile_C_HUM class
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                    10/29/2015                 Original Version
* Santhi Mandava            29422                    01/19/2016                 Changed class name as per standards. 
* Sayali Gavane										 02/10/2021					REQ -1625471 PR00094226 - CR AEP readiness -Research - Critical Security Issues (slide 11)		
************************************************************************************************************************************************/

public abstract with sharing class BillingProfile_A_HUM implements BillingProfileDetails_I_HUM
{
    public sObject objRecordData{get;set;}
    
    /*
    * Method Name : getRecordId
    * Description : This method is used get current record id
    * Return type : Id
    * Parameter   : NA
    */
    public Id getRecordId()
    {
        return (Id)ApexPages.CurrentPage().getParameters().get('Id').escapeHtml4();
    }
    
    /*
    * Method Name : getSObjName
    * Description : This method is used get current record's object name.
    * Return type : String 
    * Parameter   : Id
    */
    public String getSObjName(Id recID)
    {
        If(recID != null)
        {
           return recID.getSobjectType().getDescribe().getName();
        }
        return Null;
    }
    
    /*
    * Method Name : getUserNetworkId
    * Description : This method is used get the logged in users network id.
    * Return type : String 
    * Parameter   : NA
    */
    public String getUserNetworkId()
    {
        String sNUserId = '';
        User objUser = [Select Network_User_Id__c from User where Id =: UserInfo.getUserId()];
        If (objUser != null) sNUserId = objUser.Network_User_Id__c;
        return sNUserId;
    }
}