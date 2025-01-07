/*******************************************************************************************************************************
Apex Class Name : Public class RecommendationInteraction_LC_HUM  
Version         : 1.0
Created On      : 03/28/2023 
Function        : This class is used to get recommendation for interaction
Test Class      : 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Nirmal Garg                                        03/28/2023                 Original Version
*******************************************************************************************************************************************************************/
public with sharing class RecommendationInteraction_LC_HUM {
    @AuraEnabled(cacheable=true)
    public static List<RecommendationInteractionDTO_LC_HUM> getRecommendationInfo(String InteractionId){
        try {
            List<Recommendation_Action__c> listRecommendationAction = [SELECT Id, Recommendation__r.Name, Alert_Name__c , Action__c, Action_Reason__c, Action_DateTime__c,Question_1_Response__c, Question_2_Response__c, Question_3_Response__c, Question_4_Response__c, Free_Text_Response1__c, Free_Text_Response2__c, Interaction__c, Question_1__c, Question_2__c, Question_3__c, Question_4__c, CreatedById, Recommendation_Detail__c FROM Recommendation_Action__c where Interaction__c =: InteractionId];
            User objUser;
            if(listRecommendationAction != null && listRecommendationAction.size() > 0){
                objUser = [SELECT Id, Network_User_Id__c FROM User where id =: listRecommendationAction[0].CreatedById];
            }
            
            List<RecommendationInteractionDTO_LC_HUM> listGuidanceAlertTableDTO;
            if(listRecommendationAction.size() > 0){
                listGuidanceAlertTableDTO = new List<RecommendationInteractionDTO_LC_HUM>();
                List<string> lstQue;
                List<string> lstAns;
                
                for(Recommendation_Action__c obj : listRecommendationAction ){
                    RecommendationInteractionDTO_LC_HUM objGuidanceAlertTableDTO = new RecommendationInteractionDTO_LC_HUM();
                    lstQue = New List<string>(); 
                    lstAns = New List<string>();
                    
                    objGuidanceAlertTableDTO.sAlertName = String.IsNotBlank(obj.Recommendation__r.Name) ? obj.Recommendation__r.Name : '';
                    objGuidanceAlertTableDTO.sActionTaken = String.IsNotBlank(obj.Action__c) ? obj.Action__c : '';
                    objGuidanceAlertTableDTO.sTermNotDeliveredReason = String.IsNotBlank(obj.Action_Reason__c) ? obj.Action_Reason__c : '';
                    objGuidanceAlertTableDTO.sActionDateTime = obj.Action_DateTime__c != null ? obj.Action_DateTime__c.format('yyyy/MM/dd hh:mm:ss') : '';
                    objGuidanceAlertTableDTO.sAssociateID = objUser.Network_User_Id__c;
                    
                    if(String.IsNotBlank(obj.Question_1__c)){
                        lstQue.add(obj.Question_1__c);
                        if(String.IsNotBlank(obj.Question_1_Response__c)) lstAns.add(obj.Question_1_Response__c); else lstAns.add(' ');
                    }
                    if(String.IsNotBlank(obj.Question_2__c)){
                        lstQue.add(obj.Question_2__c);
                        if(String.IsNotBlank(obj.Question_2_Response__c)) lstAns.add(obj.Question_2_Response__c); else lstAns.add(' ');
                    }
                    if(String.IsNotBlank(obj.Question_3__c)){
                        lstQue.add(obj.Question_3__c);
                        if(String.IsNotBlank(obj.Question_3_Response__c)) lstAns.add(obj.Question_3_Response__c); else lstAns.add(' ');
                    }
                    if(String.IsNotBlank(obj.Question_4__c)){
                        lstQue.add(obj.Question_4__c);
                        if(String.IsNotBlank(obj.Question_4_Response__c)) lstAns.add(obj.Question_4_Response__c); else lstAns.add(' ');
                    }
                    
                    if(lstQue != null) objGuidanceAlertTableDTO.lstQuestion = lstQue;
                    if(lstAns != null) objGuidanceAlertTableDTO.lstAnswer = lstAns;
                    if(lstQue.size() > 0)
                        objGuidanceAlertTableDTO.bQuestions = true;
                    else
                        objGuidanceAlertTableDTO.bQuestions = false;
                    listGuidanceAlertTableDTO.add(objGuidanceAlertTableDTO);
                }
            }
            return listGuidanceAlertTableDTO;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
	
	@AuraEnabled(cacheable=true)
    public static string checkAccountInteractionType(string interactionId){
        try {
            Map<String,Boolean> mapCheckAccountInteraction = new Map<String,Boolean>();
            List<Interaction_Member__c> lstInteractionMember = [SELECT Id, Interacting_About_Type__c, Interaction__r.CreatedDate, Interaction__r.Interacting_With_type__c, Interaction__r.Created_By_Queue__c from Interaction_Member__c where Interaction__c =: interactionId];
            if(!lstInteractionMember.isEmpty()){
                set<string> interactingWithTypeSet = new Set<String>{'Agent', 'Government', 'Group', 'Internal', 'Care Manager', 'Member', 'Member Representative or Caregiver','Other','Pharmacy','Provider','Unknown-Agent','Unknown-Group','Unknown-Member','Unknown-Provider'};
                if(interactingWithTypeSet.contains(lstInteractionMember[0].Interaction__r.Interacting_With_type__c) && (lstInteractionMember[0].Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM || lstInteractionMember[0].Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM)){
                    mapCheckAccountInteraction.put('bValidAccountType', true);
                    if(lstInteractionMember.size() == 1)
                    {
                        mapCheckAccountInteraction.put('bSingleInteractionMember', true);
                    }
                    else
                    {
                        mapCheckAccountInteraction.put('bSingleInteractionMember', false);
                    }
                }else{
                    mapCheckAccountInteraction.put('bValidAccountType', false);
                }
            }
            return JSON.serializePretty(mapCheckAccountInteraction);
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    public with sharing class RecommendationInteractionDTO_LC_HUM{
        @AuraEnabled
        public String sAlertName {get; set;}
        @AuraEnabled
        public String sActionTaken {get; set;}
        @AuraEnabled
        public String sTermNotDeliveredReason {get; set;}
        @AuraEnabled
        public String sActionDateTime {get; set;}
        @AuraEnabled
        public String sAssociateID {get; set;}
        @AuraEnabled
        public list<String> lstQuestion {get; set;}
        @AuraEnabled
        public list<String> lstAnswer {get; set;}
        @AuraEnabled
        public boolean bQuestions{get;set;}
    }
}