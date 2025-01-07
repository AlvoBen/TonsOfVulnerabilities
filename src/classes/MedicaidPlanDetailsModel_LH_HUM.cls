/*******************************************************************************************************************************
Apex Class Name : MedicaidPlanDetailsModel_LH_HUM
Version         : 1.0
Created On      : 05/01/2021
Function        : This is the helper layer class for Medicaid Plan Details section on the Policy Detail page
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  1.0 					             Ashish Kumar 								05/01/2021
******************************************************************************************************************************************************************************/

public with sharing class MedicaidPlanDetailsModel_LH_HUM {
    
    @AuraEnabled public MBEResponseV1_DTO_HUM mbeData;
    @AuraEnabled public MemberAncillaryResponse_DTO_HUM_V2 ancillaryData;
    @AuraEnabled public Map<String, String> mappingVsLabel;
    @AuraEnabled public String sMedicaidId;
    @AuraEnabled public Boolean bIsMCDPlan=true;
    
          /*
    *  This method  is used to return a map by getting the field mapping from custom metadata based state
    *  @name   mbeResponseMapping
    *  @param  String state of member Plan 
    *  @return Map<String,String> 
    */
    public static Map<String,String> mbeResponseMapping(String memberPlanState)
    {	
    return getMapFromMetaDataValues(memberPlanState, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MBE_TYPE);
    }
    
    /*
    *  This method  is used to return a map by getting the field mapping from custom metadata based state
    *  @name   ancillaryResponseMapping
    *  @param  String state of member Plan 
    *  @return Map<String,String> 
    */
    public static Map<String,String> ancillaryResponseMapping(String memberPlanState)
    {	
      return getMapFromMetaDataValues(memberPlanState,GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.ANCILLARY_TYPE);
    }
    
    /*
    *  This method  is used to return a map by getting the field mapping from custom metadata based on integration type
    *  @name   getMapFromMetaDataValues
    *  @param  String state of member Plan and String integrationType
    *  @return Map<String,String> 
    */
    public static Map<String,String> getMapFromMetaDataValues(String memberPlanState,String integrationType)
    {	
        Map<String,String> fieldMapping=new Map<String,String>();
        
        List<MedicaidDetailFieldMapping__mdt> listOfFieldMapping = [select id, Label__c, State__c, IntegrationType__c ,FieldMapping__c from MedicaidDetailFieldMapping__mdt where State__c=:memberPlanState and IntegrationType__c=:integrationType ];
        for(MedicaidDetailFieldMapping__mdt stateMetaValue : listOfFieldMapping)
        {
            fieldMapping.put(stateMetaValue.FieldMapping__c, stateMetaValue.Label__c);
        }
        return removeFieldFromMap(fieldMapping);
    }
    
    public static Map<String,String> removeFieldFromMap(Map<String,String> mappingToReturn)
    {
        //this is ancillary switch for checking priorMedicaidBeginDate and priorMedicaidid fields
        if(!HUMUtilityHelper.isCRMFunctionalityON('1896703'))
        {
            if(mappingToReturn!=null)
            {
                mappingToReturn.remove('priorMedicaidBeginDate');
                mappingToReturn.remove('priorMedicaidid');
                return mappingToReturn;
            }
        }
        return  mappingToReturn;
    }
}