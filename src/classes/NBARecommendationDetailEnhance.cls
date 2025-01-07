/**************************************************************************** 
Apex Class Name  : NBARecommendationDetailEnhance
Version          : 1.0  
Created Date     : August 27, 2020
Function         : class used in alert recommendation stategies to fetch NBA recommendation details.
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Yogesh Gupta                                                               Original Version
* Suraj patil									11/02/2020					Defect 1904	
* Sayali Nemade                                 01/29/2021                  Checkmarx Issues Resolved - Changed without sharing to with sharing 
* Aishwarya Pawar               				05/13/2022                  REQ - 3285223 Alert Configuration on Plan Member Page

***************************************************************************************************/
//Calling this classfrom strategy for the file upload
public with sharing class NBARecommendationDetailEnhance {
    @InvocableMethod(label='GetNBARecommendationDetailEnhance ')
    public static List<List<Recommendation>> fetchRecommendationsDetail(List<NBADataContainerInput> inputLists){         
        List<Recommendation> listOfRecommendations = new List<Recommendation>();
        Set<Id> setOfRecommendationDetails = new Set<Id>();      
        String interatingWithType = '';
        string Allpages = 'All Pages';     
        String pageNameToCompare = inputLists[0].pageName;       
        String idOfInteraction = inputLists[0].interationId;  
        String contextId = inputLists[0].contextRecordId;            
        String sAccountId = inputLists[0].sAccountId;
        String calledFromLightning= inputLists[0].calledFromLightning;
        if(String.isNotBlank(idOfInteraction)){
            List<Interaction__c> listOfInteractions = [SELECT Id,  Interacting_With_type__c FROM Interaction__c Where  Id =: inputLists[0].interationId]; 
            interatingWithType = listOfInteractions.size() > 0 ? listOfInteractions[0].Interacting_With_type__c : '';
        }
                       
        List<Recommendation_Detail__c> recommendationDetails = inputLists[0].recommendationsDetailsTemp;                     
        List<Recommendation_Detail__c> listOfRecommendationDetails =  [SELECT Id, Name, Provider_NPIId__c, Account__c, Provider_TaxId__c, Recommendation__c 
                                                                         FROM Recommendation_Detail__c WHERE Start_Date__c <=: SYstem.today()
                                                                        AND End_Date__c >=: System.today()  AND ( Account__c =: sAccountId OR Enterprise_ID__c =: inputLists[0].enterpriseId) AND Active__c =true AND 
                                                                        (Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') AND Recommendation__r.Show_To__c IN ('Member','Group') AND Active__c = true];
       
		Map<String, String> mapOfRecommendationWithDetail = new Map<String, String>();
        for(Recommendation_Detail__c detail:  listOfRecommendationDetails){
            mapOfRecommendationWithDetail.put(detail.Recommendation__c, detail.Id);
        }
	   
        List<List<Recommendation>> listOfListofRecom = new List<List<Recommendation>>();
        if(String.isBlank(interatingWithType)){
            listOfRecommendations = [SELECT Id, Name,ActionReference, Description, AcceptanceLabel, RejectionLabel FROM Recommendation 
                                        WHERE (Location__c INCLUDES (:inputLists[0].pageName) OR Location__c INCLUDES (: Allpages)) AND Id IN: mapOfRecommendationWithDetail.keySet()  AND Active__c = true
                                        LIMIT 25]; 
        }
        else{
            listOfRecommendations = [SELECT Id, Name,ActionReference, Description, AcceptanceLabel, RejectionLabel FROM Recommendation 
                                        WHERE (Location__c INCLUDES (:inputLists[0].pageName) OR Location__c INCLUDES (: Allpages)) AND  Id IN: mapOfRecommendationWithDetail.keySet() AND
                                        Interacting_With_Type__c includes (:interatingWithType) AND Active__c = true 
                                        LIMIT 25];                                     
        }
		List<Recommendation> listOfRecommendationToReturn = new List<Recommendation>();
        for(Recommendation rec: listOfRecommendations)      {
            rec.externalId = mapOfRecommendationWithDetail.get(rec.Id);
            listOfRecommendationToReturn.add(rec);           
        }  
        List<String> lstGroupAccountIds = new List<String>();
        try
        {
        
        if(pageNameToCompare == 'Business Account'){
            lstGroupAccountIds.add(sAccountId);
        }
        else{
            if(calledFromLightning!='true'){
            
                List<Policy_Member__c> lstPolicyMembers =  new List<Policy_Member__c>();
                //1810953- Incident-0816714 Switch to fetch active or inactive policy	
                CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('1810953');
                if(objFunctionalitySwitch != null && objFunctionalitySwitch.IsON__c==true) {
                    lstPolicyMembers = [SELECT Id, Member__r.Recordtype.Name, Group_Number__c, Policy__r.Group_Name__c, (SELECT Id FROM cases__r limit 1), ETL_Record_Deleted__c FROM Policy_Member__c where (id =: contextId OR Member__c =: contextId)];
                }
                else{
                    lstPolicyMembers = [SELECT Id, Member__r.Recordtype.Name, Group_Number__c, Policy__r.Group_Name__c, (SELECT Id FROM cases__r limit 1), ETL_Record_Deleted__c FROM Policy_Member__c where (id =: contextId OR Member__c =: contextId) AND Status__c = 'Active'];
                }  
                if(lstPolicyMembers != Null && lstPolicyMembers.size() > 0){
                    for(Policy_Member__c oPolicyMember : lstPolicyMembers ){
                        if(pageNameToCompare == 'Person Account' && (!oPolicyMember.ETL_Record_Deleted__c || (oPolicyMember.ETL_Record_Deleted__c && oPolicyMember.Cases__R != NULL && (oPolicyMember.Cases__R).size() > 0)))
                        lstGroupAccountIds.add(oPolicyMember.Policy__r.Group_Name__c);
                        else if(pageNameToCompare != 'Person Account')
                        lstGroupAccountIds.add(oPolicyMember.Policy__r.Group_Name__c);
                    }
                }
            }else{
                
                List<MemberPlan> planMemberList =  new List<MemberPlan>();
                //1810953- Incident-0816714 Switch to fetch active or inactive policy	
                CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('1810953');
                if(objFunctionalitySwitch != null && objFunctionalitySwitch.IsON__c==true) {
                    planMemberList = [SELECT Id, Member.Recordtype.Name, Policy__r.Group_Name__c,ETL_Record_Deleted__c ,(SELECT Id FROM CaseMemberPlan__r limit 1) FROM MemberPlan where (id =:contextId  OR MemberId  =: contextId)];
                }
                else{
                    planMemberList = [SELECT Id, Member.Recordtype.Name, Policy__r.Group_Name__c,ETL_Record_Deleted__c ,(SELECT Id FROM CaseMemberPlan__r limit 1) FROM MemberPlan where (id =:contextId  OR MemberId  =: contextId) AND Status = 'Active'];
                }  
                if(planMemberList != Null && planMemberList.size() > 0){
                    for(MemberPlan memberPlanObj : planMemberList ){
                        if(pageNameToCompare == 'Person Account' && (!memberPlanObj.ETL_Record_Deleted__c || (memberPlanObj.ETL_Record_Deleted__c && memberPlanObj.CaseMemberPlan__r != NULL && (memberPlanObj.CaseMemberPlan__r).size() > 0)))
                        lstGroupAccountIds.add(memberPlanObj.Policy__r.Group_Name__c);
                        else if(pageNameToCompare != 'Person Account')
                        lstGroupAccountIds.add(memberPlanObj.Policy__r.Group_Name__c);
                    }
                }
                
            }
                
        }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'NBARecommendationDetailEnhance','fetchRecommendationsDetail');
        }
		if(lstGroupAccountIds.size() > 0){
			List<Recommendation_Detail__c> listOfRecommendationDetailsGM = new List<Recommendation_Detail__c>();
			if(String.isBlank(idOfInteraction)){
				listOfRecommendationDetailsGM = [SELECT Id, Name, Account__c, Recommendation__c,Recommendation__r.Name, Recommendation__r.Alert_Type__c, Recommendation__r.ActionReference, Recommendation__r.Description, Recommendation__r.AcceptanceLabel, Recommendation__r.RejectionLabel
																			FROM Recommendation_Detail__c where Account__c IN : lstGroupAccountIds AND Recommendation__r.Show_To__c = 'Group + Member' 
																			AND (Recommendation__r.Location__c INCLUDES (: Allpages) OR Recommendation__r.Location__c INCLUDES (:pageNameToCompare)) 
																			AND (Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') 
																			AND Start_Date__c <=: SYstem.today() AND End_Date__c >=: System.today() AND  Active__c = true AND Recommendation__r.Active__c = true];
			}else{                
				listOfRecommendationDetailsGM = [SELECT Id, Name, Account__c, Recommendation__c,Recommendation__r.Name, Recommendation__r.Alert_Type__c, Recommendation__r.ActionReference, Recommendation__r.Description, Recommendation__r.AcceptanceLabel, Recommendation__r.RejectionLabel
																			FROM Recommendation_Detail__c where Account__c IN : lstGroupAccountIds AND Recommendation__r.Show_To__c = 'Group + Member' 
																			AND (Recommendation__r.Location__c INCLUDES (: Allpages) OR Recommendation__r.Location__c INCLUDES (:pageNameToCompare)) 
																			AND (Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') 
																			AND Start_Date__c <=: SYstem.today() AND End_Date__c >=: System.today()
																			AND Recommendation__r.Interacting_With_Type__c includes (:interatingWithType) AND  Active__c = true AND Recommendation__r.Active__c = true];
			} 
			for(Recommendation_Detail__c recomDetail : listOfRecommendationDetailsGM )
			{
				Recommendation recom = new Recommendation();
				recom.Id = recomDetail.Recommendation__c;
				recom.externalId = recomDetail.id;
				recom.Name = recomDetail.Recommendation__r.Name ;
				recom.ActionReference = recomDetail.Recommendation__r.ActionReference; 
				recom.Description = recomDetail.Recommendation__r.Description; 
				recom.AcceptanceLabel = recomDetail.Recommendation__r.AcceptanceLabel ;
				recom.RejectionLabel = recomDetail.Recommendation__r.RejectionLabel ;
				listOfRecommendationToReturn.add(recom);
			}
		}
        listOfListofRecom.add(listOfRecommendationToReturn );  
        return listOfListofRecom;
        
    }
}