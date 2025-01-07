/*******************************************************************************************************************************
Apex Class Name : GroupDetailDivisionSubgroup_LS_HUM
Version         : 1.0
Created On      : 11/03/2020
Function        : This class is the service layer class for GroupDetailDivisionSubgroup_LC_HUM
Test Class      : GroupDetailDivisionSubgroup_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     12/18/2020                Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
*******************************************************************************************************************************************************************/

public class GroupDetailDivisionSubgroup_LS_HUM 
{
    /*
    * Method Name   :  requestGroupInfo()
    * Description   :  This is the used to request Group Info Division response .
    * Return Type   :  Continuation
    * Parameters    :  objGrpAccount
    */
    public static Continuation requestGroupInfo(Account objGrpAccount)
    {
        if(!String.isblank(objGrpAccount.Source_Platform_Code__c) && !String.isblank(objGrpAccount.Source_Customer_Key__c))
        {  
            Integer iCount=1;
            Integer iMaxRecordsToDisplay=GLOBAL_CONSTANT_LH_HUM.MaxRecordToFetch;
            HUM_Webservice_Callout__c oSvcHeaderGBEService = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
            HUM_Webservice_Callout__c oSvcHeaderEDIData = HUM_Webservice_Callout__c.getValues('GetEDIData');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGBEService.Timeout__c)/1000;
            Continuation continuation = new Continuation(iTIMEOUT_INT_SECS);       
            continuation.continuationMethod = HUMVitalityServiceConstants.CONTINUATION_PROCESSRESPONSE_HUM;                    
            
            ContinuationStateWrapper stateWrapper = new ContinuationStateWrapper();
            
            String sRequestBody = GroupDetailDivisionSubgroup_LD_HUM.formRequestBody(String.valueOf(iCount),String.valueOf(iMaxRecordsToDisplay),objGrpAccount);
            stateWrapper.requestLabelGbe = continuation.addHttpRequest(GroupDetailDivisionSubgroup_LD_HUM.setuprequestToGBE(oSvcHeaderGBEService, sRequestBody));
            stateWrapper.sSourceCustomerKey = objGrpAccount.Source_Customer_Key__c;
            
            String sRequestBodyEDIData = GroupDetailDivisionSubgroup_LD_HUM.formEDIDataRequestBody(objGrpAccount);
            stateWrapper.requestLabelEDIData = continuation.addHttpRequest(GroupDetailDivisionSubgroup_LD_HUM.setupEDIDataRequest(oSvcHeaderEDIData, sRequestBodyEDIData));
            
            continuation.state = stateWrapper;            
            return continuation;   
        }
        return null;        
    }
    
    /*
    * Method Name   :  processResponseGroupInfo()
    * Description   :  This is the used to process Group Info Division response .
    * Return Type   :  GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo
    * Parameters    :  state
    */
    public static GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo processResponseGroupInfo(Object state)
    {
        GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo oGroupDetail;
        ContinuationStateWrapper oConState = (ContinuationStateWrapper)state;
        HttpResponse oResponseGbe = Continuation.getResponse(oConState.requestLabelGbe);         
        HttpResponse oResponseEDIdata = Continuation.getResponse(oConState.requestLabelEDIData);         
        GBEServiceResponse_H_HUM oResGbe = NULL;
        
        oResGbe = GroupDetailDivisionSubgroup_LD_HUM.checkResponse(oResponseGbe);
        if(oResGbe != NULL)
        {
            if(oResGbe.GroupInfo.Platform != NULL 
               && (oResGbe.GroupInfo.Platform.equals(GLOBAL_CONSTANT_LH_HUM.sMTV_Value) || oResGbe.GroupInfo.Platform.equals(GLOBAL_CONSTANT_LH_HUM.sLV_Value)  || oResGbe.GroupInfo.Platform.equals(GLOBAL_CONSTANT_LH_HUM.sCI_Value ) || oResGbe.GroupInfo.Platform.equals(GLOBAL_CONSTANT_LH_HUM.sCAS_Value) || oResGbe.GroupInfo.Platform.equals(GLOBAL_CONSTANT_LH_HUM.sEM_Value)))    
            {
                oGroupDetail = groupInfoDivisonData(oConState.sSourceCustomerKey, oResGbe);  
            }
        }  
        
        oGroupDetail.oEDIdata = GroupDetailDivisionSubgroup_LD_HUM.getEDIResponse(oResponseEDIdata);
        return oGroupDetail;
    }
    
    
    /*
    * Method Name   :  groupInfoDivisonData()
    * Description   :  This is the used to process Group Info Division Data.
    * Return Type   :  void
    * Parameters    :  NA
    */
    @testVisible private static GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo  groupInfoDivisonData(String sSourceCustomerKey, GBEServiceResponse_H_HUM oReturnedresponse)
    {    
        List<GroupDetailDivisionSubgroup_LH_HUM> lstVitalityRecords = new List<GroupDetailDivisionSubgroup_LH_HUM>();             
        GBEServiceResponse_H_HUM.DivisionList arrVitalityIndicatorDTO= NULL;
        if(oReturnedresponse.GroupInfo.DivisionList != NULL)
        {
            arrVitalityIndicatorDTO = oReturnedresponse.GroupInfo.DivisionList;
        }
        
        for(Integer i = 0; i < arrVitalityIndicatorDTO.division.size(); i++)
        {    
            
            if(arrVitalityIndicatorDTO != NULL && arrVitalityIndicatorDTO.division[i] != NULL)
            {  
                lstVitalityRecords.add(new GroupDetailDivisionSubgroup_LH_HUM(
                    sSourceCustomerKey,                            
                    Boolean.ValueOf(arrVitalityIndicatorDTO.division[i].VitalityIndicator), 
                    arrVitalityIndicatorDTO.division[i].SubGroupID, 
                    arrVitalityIndicatorDTO.division[i].SubGroupName,
                    arrVitalityIndicatorDTO.division[i].unitCount, 
                    arrVitalityIndicatorDTO.division[i].Unit, 
                    arrVitalityIndicatorDTO.division[i].EffectiveProvision));                                
            }
        }
        
        //Below code is for adding the Group Detail fields to the wrapper 
        GroupDetailMainInfo groupDetailMainInfo = new GroupDetailMainInfo();
        
        if(!String.ISBLANK(oReturnedresponse.GroupInfo.EffectiveDate))
        {
            groupDetailMainInfo.EffectiveDate = oReturnedresponse.GroupInfo.EffectiveDate;                 
        }
        if(!String.ISBLANK(oReturnedresponse.GroupInfo.NextRenewalDate))
        {
            groupDetailMainInfo.NextRenewalDate = oReturnedresponse.GroupInfo.NextRenewalDate;         
        }
        
        groupDetailMainInfo.EnrolledSubscriberCountMedical = oReturnedresponse.GroupInfo.EnrolledSubscriberCountMedical;
        groupDetailMainInfo.EnrolledSubscriberCountDental = oReturnedresponse.GroupInfo.EnrolledSubscriberCountDental;
        if(!String.isBlank(groupDetailMainInfo.EnrolledSubscriberCountDental) && groupDetailMainInfo.EnrolledSubscriberCountDental.trim().equals('0'))
        {
            groupDetailMainInfo.EnrolledSubscriberCountDental = ''; 
        }    
        if(!String.isBlank(groupDetailMainInfo.EnrolledSubscriberCountMedical) && groupDetailMainInfo.EnrolledSubscriberCountMedical.trim().equals('0')) 
        {
            groupDetailMainInfo.EnrolledSubscriberCountMedical = ''; 
        }
        groupDetailMainInfo.listGrouDivisionSubGroup=lstVitalityRecords;
        return groupDetailMainInfo;
    }
    
    public class GroupDetailMainInfo
    {
        @AuraEnabled public String EffectiveDate = '';
        @AuraEnabled public String EnrolledSubscriberCountDental = '';
        @AuraEnabled public String EnrolledSubscriberCountMedical = '';
        @AuraEnabled public String NextRenewalDate = '';
        @AuraEnabled public List<GroupDetailDivisionSubgroup_LH_HUM> listGrouDivisionSubGroup = NULL;
        @AuraEnabled public EDIResponse_DTO_HUM.EdiGroupDetails oEDIdata = NULL;
    }
    
    public class ContinuationStateWrapper
    {
        public String requestLabelGbe = '';
        public String requestLabelEDIData = '';
        public String sSourceCustomerKey = '';
    }
}