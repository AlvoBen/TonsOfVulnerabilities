/*****************************************************************************************
Apex class Name    : GroupDetailSection_S_HUM
Version            : 1.0
Function           : This service class is used to request GBE Service 
Created On         : October 22 2015
Test Class         : GroupDetailSection_S_T_HUM

Modification Log:
*   Developer                   Code Review           Date               Description
* ------------------------------------------------------------------------------------------------------------------------------ 
*  SuryaKumari Medicherla       27320                10/22/2015             Original Version  
*  SuryaKumari Medicherla                            11/02/2015             Modified HUM_Webservice_Callout__c custom settings value
*  Praveen Kumar Parimi         28018                11/23/2015             Incorporated the Platform conditional check for Sub Group ID 
   																			and Ben Package ID.
********************************************************************************************************************************/
public with sharing class GroupDetailSection_S_HUM
{
    @testVisible private AsyncHUMGBEService.Asyncsoap asynchGBE = new AsyncHUMGBEService.Asyncsoap();
    
    /*
     * Method Name   :  GroupDetailSection_S_HUM
     * Description   :  This Constructor method is used initialize the variable and objects
     * Return Type   :  NA
     * Parameters    :  NA
    */
    public GroupDetailSection_S_HUM()
    {
        HUM_Webservice_Callout__c cstCalloutSetting = HUM_Webservice_Callout__c.getInstance('GBEService');
        asynchGBE.endpoint_x = cstCalloutSetting.End_Point_URL__c;
        asynchGBE.clientCertName_x = cstCalloutSetting.Certificate_Name__c;
        asynchGBE.timeout_x = Integer.valueof(cstCalloutSetting.Timeout__c);    
    }
    
    /*
     * Method Name   :  requestToServer
     * Description   :  This method is used to create request DTO and to call the GBE Service
     * Return Type   :  Object
     * Parameters    :  Account,Continuation,Boolean,String,String
    */
    public AsyncHUMGBEService.GetGroupInfoResponse_elementFuture requestToServer(Account objGrpAccount,Continuation con,Boolean bBenfitData,String sStartPosition,String sRecordsToreturn)
    {
        try
        {
            HUMGBEService.GroupDetailDTO grpDetail = new HUMGBEService.GroupDetailDTO();
            grpDetail.GroupId = objGrpAccount.Source_Customer_Key__c;
            if(!String.isBlank(objGrpAccount.Source_Platform_Code__c))
            {
            if(objGrpAccount.Source_Platform_Code__c.equalsIgnoreCase(HUMConstants.sLV_Value))
            {
            grpDetail.SubGroupId = HUMConstants.sZeroZeroOne;
            grpDetail.BenefitPackageId = '';
            }
            else
            {
            grpDetail.SubGroupId = objGrpAccount.Source_Customer_Key__c+ HUMConstants.sZeroOne;
            grpDetail.BenefitPackageId = HUMConstants.sDummyBenValue;
            }
            }            
            grpDetail.Platform = objGrpAccount.Source_Platform_Code__c;
            grpDetail.IncludeExchangeData = False;
            grpDetail.IncludeBenefitData = bBenfitData;
            grpDetail.IncludeVitalityIndicator = True;
            grpDetail.BenefitSequenceNumber = '';
            grpDetail.VitalityStartPosition = sStartPosition;
            grpDetail.VitalityRecordsToReturn = sRecordsToreturn;
            HUMGBEService.RequestGroupDTO req = new HUMGBEService.RequestGroupDTO();
            req.GroupDetailDTO  = grpDetail;
                
            return asynchGBE.beginGetGroupInfo(con,req);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_S_HUM', 'requestToServer');
        }
        return null;
    }
}