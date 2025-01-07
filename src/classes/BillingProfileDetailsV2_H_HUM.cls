/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfileDetail_S2_HUM
Version          : 1.0
Created Date     : 06/01/2018
Function         : This is used to return response from service and format the response.
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sumit Pharande             243694                  06/01/2018                Original Version
**********************************************************************************************************************************************/

public with sharing class BillingProfileDetailsV2_H_HUM
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
    * Return type : BillingProfile_DTO_HUM
    */
    public static BillingProfile_DTO_HUM getBillingProfilesInput(String sUserId, String sPlatformCode,String sSearchID,String sIdType,String sObjName)
    {
        HUM_Webservice_Callout__c obj = HUM_Webservice_Callout__c.getInstance('BillingServiceV2');
        BillingProfile_DTO_HUM objSerInput = new BillingProfile_DTO_HUM();
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
    * Return type : List<BillingProfile_DTO_HUM>
    * Parameter   : String ,String,List<BillingProfile_DTO_HUM> 
    */
    public static List<BillingProfile_DTO_HUM> Search(String sProfileStatus ,String sTermDate,List<BillingProfile_DTO_HUM> lstInternalBillingProfiles)
    {
        List<BillingProfile_DTO_HUM> lstBillingProfiles = new List<BillingProfile_DTO_HUM>();
        If(sProfileStatus == GLOBAL_CONSTANT_HUM.HTTPREQUEST_AUTHCATEGORY_HUM  && String.isBlank(sTermDate))
        {
            lstBillingProfiles = lstInternalBillingProfiles;
        }
        else
        {
            For(BillingProfile_DTO_HUM obj : lstInternalBillingProfiles)
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