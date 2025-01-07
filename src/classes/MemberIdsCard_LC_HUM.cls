/*******************************************************************************************************************************
Apex Class Name : MemberIdsCard_LC_HUM
Version         : 1.0
Created On      : 06/23/2021
Function        : This class is used for sending the memberplan fields data to MemberIdsCardHum LWC 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        06/23/2020               Original Version
*    1.1           Ritik Agarwal                                         06/29/2021               Added a ChangeSSN method for get last four digit of SSN
*    1.2           Ritik Agarwal                                         09/29/2021               Mask SSN 
*******************************************************************************************************************************************************************/

public with sharing class MemberIdsCard_LC_HUM {
     /*
    * Method name : fetchMemberIdCardData
    * Description : This method will return the MemberPlan object
    * Parameter   : membpln(id of current mmemberplan)
    *              pageName(LWC component name based on which it will query metadtaa to fetch
    *                       only those records which are related to caller component)
    */
    @AuraEnabled
    public Static Object fetchMemberIdCardData(String membpln,String pageName){
        Map<String,String> mpOfFieldApiVsLabel = new Map<String,String>();
        String memberPlanQuery = 'Select id';
        ObjectVSFieldNameMapping__mdt[] fldMappingsData = MemberIdsCard_LD_HUM.queryMemberIdCardData(pageName);
        
        for(ObjectVSFieldNameMapping__mdt objFldmaping : fldMappingsData){
            
            if(objFldmaping.ObjectTypeName__c==GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MEM_PLAN_OBJ){
                memberPlanQuery += ', '+objFldmaping.FieldAPIName__c;
                mpOfFieldApiVsLabel.put(objFldmaping.MasterLabel,objFldmaping.FieldAPIName__c); 
            }
            
            if(objFldmaping.ObjectTypeName__c==GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.ACCOUNT_OBJ){
                memberPlanQuery += ', Member.'+objFldmaping.FieldAPIName__c; 
                mpOfFieldApiVsLabel.put(objFldmaping.MasterLabel,'Member,'+objFldmaping.FieldAPIName__c);  
            }
        }
        memberPlanQuery += ' From MemberPlan where id=:membpln';
        MemberPlan memplan = Database.query(memberPlanQuery);
         List<MemberIdsCard_LS_HUM.MemberIdCardData> obj = (List<MemberIdsCard_LS_HUM.MemberIdCardData>) MemberIdsCard_LS_HUM.createMemberIdData(mpOfFieldApiVsLabel,memplan , fldMappingsData);
            if(obj==null){
                return null;
            }
        return changeSSN((List<MemberIdsCard_LS_HUM.MemberIdCardData>)obj);
    }
   
    /*
    * Method name : changeSSN
    * Description : This method will return the one to one mapping data with SSN last four digit value
    * Parameter   : model(list of MemberIdsCard_LS_HUM.MemberIdCardData)
    * return type : Object            
    */
    public static Object changeSSN(List<MemberIdsCard_LS_HUM.MemberIdCardData> model){
        for(MemberIdsCard_LS_HUM.MemberIdCardData card : model){
            if(card.label=='SSN' && String.valueOf(card.value).length()==9){
                card.value = 'XXX-XX-' + String.valueOf(card.value).right(4);
            }
        }
        return model;
    }
    
}