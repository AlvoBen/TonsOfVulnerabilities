/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2_S_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : used to call methods in Pharmacy Service
Test Class       : PharmacyServiceV2_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma              27449            10/29/2015                    Original Version
* Ravikanth                                    07/28/2022                 User Story 3570705: T1PRJ0315551- SF - FR# 28  : WDI Consumer Migration - APIC and URL (From WDI 2.0 to WDI 3.0)  
***************************************************************************************************/
public with sharing class PharmacyServiceV2_S_HUM 
{
   /*
    * Method Name : SearchDrug
    * Description : This method is used to call SearchDrug in Pharmacy Service and return response
    * Return type : PharmacyServiceV2SearchRequest_H_HUM     
    * Paramater   : PharmacyServiceV2SearchResponse_H_HUM
    */
    public static PharmacyServiceV2SearchResponse_H_HUM SearchDrug(PharmacyServiceV2SearchRequest_H_HUM oSearchDrugRequest) 
    {
       PharmacyServiceV2SearchResponse_H_HUM oSearchDrugResponse = new PharmacyServiceV2SearchResponse_H_HUM();
       try
       {
          HUM_Webservice_Callout__c oSvcHeaders;
          CRMFunctionality_ONOFF_Switch__c wdiFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('3570705');
          if(wdiFunctionalitySwitch != null && wdiFunctionalitySwitch.IsON__c)
          {
              oSvcHeaders  = HUM_Webservice_Callout__c.getValues('RXDrugInfoServiceV3.0_SearchDrug');
          }
          else
          {
               oSvcHeaders  = HUM_Webservice_Callout__c.getValues('PharmacyService_V20_SearchDrug');
          }
           
          string sBody = JSON.serialize(oSearchDrugRequest);
          if(oSvcHeaders.Active__c && sBody!=null)
          {
            HttpResponse response = HUMCalloutUtilityHelper.callWebservice(oSvcHeaders.End_Point_URL__c, oSvcHeaders.Certificate_Name__c,sBody, oSvcHeaders.Request_Type__c, '', oSvcHeaders.Content_Type__c);
           
            if(response.getStatusCode() == 200 || response.getStatusCode() == 202)
            {
                oSearchDrugResponse = (PharmacyServiceV2SearchResponse_H_HUM)System.JSON.deserialize(response.getbody(), PharmacyServiceV2SearchResponse_H_HUM.class);
            }
          }
       }
       catch(Exception e)
       {    
           HUMExceptionHelper.logErrors(e,'PharmacyServiceV2_S_HUM','SearchDrug');
       } 
       
       return oSearchDrugResponse;
       
    }
    
    /*
    * Method Name : GetDrugDosage
    * Description : This method is used to call GetDrugDosage in Pharmacy Service and return response
    * Return type : PharmacyServiceV2DosageRequest_H_HUM     
    * Paramater   : PharmacyServiceV2DosageResponse_H_HUM
    */
    public static PharmacyServiceV2DosageResponse_H_HUM GetDrugDosage(PharmacyServiceV2DosageRequest_H_HUM oDrugDosageRequest) 
    {
       PharmacyServiceV2DosageResponse_H_HUM oDrugDosageResponse = new PharmacyServiceV2DosageResponse_H_HUM();
       try
       {
          
          HUM_Webservice_Callout__c oSvcHeaders;
          CRMFunctionality_ONOFF_Switch__c wdiFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('3570705');
          if(wdiFunctionalitySwitch != null && wdiFunctionalitySwitch.IsON__c)
          {
              oSvcHeaders  = HUM_Webservice_Callout__c.getValues('RXDrugInfoServiceV3.0_GetDrugDosage');
          }
          else
          {
               oSvcHeaders  = HUM_Webservice_Callout__c.getValues('PharmacyService_V20_GetDrugDosage');
          }
          string sBody = JSON.serialize(oDrugDosageRequest);
          if(oSvcHeaders.Active__c && sBody!=null)
          {
            HttpResponse response = HUMCalloutUtilityHelper.callWebservice(oSvcHeaders.End_Point_URL__c, oSvcHeaders.Certificate_Name__c,sBody, oSvcHeaders.Request_Type__c, '', oSvcHeaders.Content_Type__c);
            if(response.getStatusCode() == 200 || response.getStatusCode() == 202)
            {
                oDrugDosageResponse = (PharmacyServiceV2DosageResponse_H_HUM)System.JSON.deserialize(response.getbody(), PharmacyServiceV2DosageResponse_H_HUM.class);
            }
          }
       }
       catch(Exception e)
       {    
           HUMExceptionHelper.logErrors(e,'PharmacyServiceV2_S_HUM','GetDrugDosage');
       } 
       return oDrugDosageResponse;
    }
}