/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfileDetails_H_HUM
Version          : 1.0
Created Date     : 12/19/2015
Function         : This is helper class to handle common functionalities.
Test Class       : BillingProfileDetails_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                    10/29/2015                 Original Version
* Santhi Mandava            29422                    01/19/2016                 Implemented billing phase 2 stories. 
************************************************************************************************************************************************/


public with sharing class BillingProfileDetails_H_HUM
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
    public static BillingProfile_DTO_HUM getBillingProfilesInput(String sObjName,BillingProfile_A_HUM oPageController,String sSearchID,String sIdType)
    {
        HUM_Webservice_Callout__c obj = HUM_Webservice_Callout__c.getInstance('BillingProfiles');

        BillingProfile_DTO_HUM objSerInput = new BillingProfile_DTO_HUM();
        objSerInput.iTimeout = ((obj != null && obj.Timeout__c != null) ? Integer.ValueOf(obj.Timeout__c)/1000:10);
        objSerInput.sUserID = oPageController.getUserNetworkId();
        if(!Test.isRunningTest()) objSerInput.sPlatformCode = (sObjName == HUMConstants.OBJECT_ACCOUNT?((Account)oPageController.objRecordData).Source_Platform_Code__c :((Policy_Member__c)oPageController.objRecordData).Policy_Platform__c);
        If(String.isBlank(sIdType))
        {
             if(!Test.isRunningTest()) objSerInput.sIDType = (sObjName == HUMConstants.OBJECT_ACCOUNT? 'GROUPID' :'CONTRID');
        }
        else
        {
            objSerInput.sIDType = sIdType ;
        }
        
        If(String.isBlank(sSearchID))
        {
             if(!Test.isRunningTest()) objSerInput.sSearchID = (sObjName == HUMConstants.OBJECT_ACCOUNT?((Account)oPageController.objRecordData).Source_Customer_Key__c :((Policy_Member__c)oPageController.objRecordData).Source_Coverage_ID__c);
        }
        else
        {
            objSerInput.sSearchID = sSearchID ;
        }
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
        If(sProfileStatus == HUMConstants.sBillingProfStatus  && String.isBlank(sTermDate))
        {
            lstBillingProfiles = lstInternalBillingProfiles;
        }
        else
        {
            For(BillingProfile_DTO_HUM obj : lstInternalBillingProfiles)
            {
                If(sProfileStatus == HUMConstants.sBillingProfStatus && !String.isBlank(sTermDate))
                {
                    If(obj.sEndDate == sTermDate)
                    {
                        lstBillingProfiles.Add(obj);
                    }
                }
                else if(sProfileStatus != HUMConstants.sBillingProfStatus && String.isBlank(sTermDate))
                {
                    If(obj.sStatus == sProfileStatus)
                    {
                        lstBillingProfiles.Add(obj);
                    }
                }
                else if(sProfileStatus != HUMConstants.sBillingProfStatus && !String.isBlank(sTermDate))
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