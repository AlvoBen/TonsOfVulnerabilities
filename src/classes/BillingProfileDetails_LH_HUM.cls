/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfileDetails_LH_HUM
Version          : 1.0
Created Date     : 05/23/2022
Function         : This is used to return response from service and format the response.
Test Class 		 : BillingProfileDetails_LT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                                        05/23/2022                 Original Version
**********************************************************************************************************************************************/

public with sharing class BillingProfileDetails_LH_HUM
{
    /*
    * Method Name : getProfileStatusValues
    * Description : This method is used to create profile status list.
    * Return type : List<SelectOption>
    * Parameter   : NA
    */
    public static List<SelectOption> getProfileStatusValues()
    {
        Map<String, BillingProfileStatus_HUM__c> mapProfStatus = BillingProfileStatus_HUM__c.getAll();  
        List<SelectOption> lstProfileStatus = new List<SelectOption>();
        lstProfileStatus.Add(new SelectOption(mapProfStatus.get('All').Name,mapProfStatus.get('All').Name));
        lstProfileStatus.Add(new SelectOption(mapProfStatus.get('Active').Name,mapProfStatus.get('Active').Name));
        lstProfileStatus.Add(new SelectOption(mapProfStatus.get('Termed').Name,mapProfStatus.get('Termed').Name));
        return lstProfileStatus ;
    }
    
    /*
    * Method Name : getBillingProfilesInput
    * Description : This method is used to prepare service input.
    * Return type : BillingProfile_LDTO_HUM
    */
    @AuraEnabled
    public static BillingProfile_LDTO_HUM getBillingProfilesInput(String sUserId, String sPlatformCode,String sSearchID,String sIdType,String sObjName)
    {      
        HUM_Webservice_Callout__c obj = HUM_Webservice_Callout__c.getInstance('BillingServiceV2');
        BillingProfile_LDTO_HUM objSerInput = new BillingProfile_LDTO_HUM();
        objSerInput.iTimeout = ((obj != null && obj.Timeout__c != null) ? Integer.ValueOf(obj.Timeout__c)/1000:10);
        objSerInput.sUserID=sUserId;
        objSerInput.sPlatformCode = sPlatformCode;
        objSerInput.sIDType = sIdType ;
        objSerInput.sSearchID = sSearchID ;
        return objSerInput;
    }
    
    /*
    * Method Name : Search
    * Description : This method is used to filter billing profiles based on the selected status and term date.
    * Return type : List<BillingProfile_LDTO_HUM>
    * Parameter   : String ,String,List<BillingProfile_LDTO_HUM> 
    */
    public static List<BillingProfile_LDTO_HUM> Search(String sProfileStatus ,String sTermDate,List<BillingProfile_LDTO_HUM> lstInternalBillingProfiles)
    {
        List<BillingProfile_LDTO_HUM> lstBillingProfiles = new List<BillingProfile_LDTO_HUM>();
        If(sProfileStatus == GLOBAL_CONSTANT_HUM.HTTPREQUEST_AUTHCATEGORY_HUM  && String.isBlank(sTermDate))
        {
            lstBillingProfiles = lstInternalBillingProfiles;
        }
        else
        {
            For(BillingProfile_LDTO_HUM obj : lstInternalBillingProfiles)
            {
                If(sProfileStatus == GLOBAL_CONSTANT_HUM.HTTPREQUEST_AUTHCATEGORY_HUM && String.isNotBlank(sTermDate))
                {
                    If(obj.sEndDate == sTermDate)
                    {
                        lstBillingProfiles.Add(obj);
                    }
                }
                else if(sProfileStatus != GLOBAL_CONSTANT_HUM.HTTPREQUEST_AUTHCATEGORY_HUM && String.isBlank(sTermDate))
                {
                    If(obj.sStatus == sProfileStatus)
                    {
                        lstBillingProfiles.Add(obj);
                    }
                }
                else if(sProfileStatus != GLOBAL_CONSTANT_HUM.HTTPREQUEST_AUTHCATEGORY_HUM && String.isNotBlank(sTermDate))
                {
                    If(obj.sStatus == sProfileStatus && obj.sEndDate == sTermDate)
                    {
                        lstBillingProfiles.Add(obj);
                    }
                }
            }
        }
        lstBillingProfiles.sort();
        return lstBillingProfiles;
    }
}