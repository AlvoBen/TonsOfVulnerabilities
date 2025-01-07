/*******************************************************************************************************************************
Apex Class Name : NBA_GetRecommendations.cls
Version         : 1.0
Created On      : 10/13/2020
Function        : Class is used as base class to display recommendation on screen flow.

Modification Log:
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Suraj Patil									10/13/2020					Original version
* Sayali Nemade                                 01/29/2021                  Checkmarx Issues Resolved - Changed without sharing to with sharing 	
* Aishwarya Pawar                               10/19/2021                  REQ-268408, REQ-2684091 and REQ- 2684061
* Nirmal Garg								    11/26/2021				    Defect 4215 - removed dependency from global constant and hum constant
* Kinal Mangukiya                               08/18/2023                  US 4812228 - T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – NBA
* Jonathan Dickinson			                10/02/2023				    User Story 5110926: T1PRJ0891339  2023 Arch Remediation - SF - TECH - Incident - INC2536276 - CRM - Unable to open Alerts for one user
*****************************************************************************************************************************************/

public with sharing class NBA_GetRecommendations
{
	Public Static final String UNKNOWNACCOUNT_PIPECHARACTER_HUM = '|';
    @InvocableMethod(label='Get Recommendations For Flow')
    public static list<list<NBA_Recommendation_Wrapper>> getListOfRecommendationsToDisplay(list<list<string>> lstRecommendationActionIds)
    {
        List<String>  lstRecommendationIds = lstRecommendationActionIds[0];
        Map<String, String> mapRecommendation_DetailAndAction = new Map<String, String>();
        Map<String, String> mapRecommendationAndAction = new Map<String, String>();
        List<String> lstRecommendationActrionsToQuery = new List<String>();
        String TotalNumberOfRecommendation;
        if(lstRecommendationIds.size() > 0){
            if(lstRecommendationIds[0].contains('[')){
            for(string str  :lstRecommendationIds[0].split(',')){
                str = str.replace('[','');
                str = str.replace(']','');
                str = str.replace(' ','');
                lstRecommendationActrionsToQuery.add(str);
                
            }
            }else{
                lstRecommendationActrionsToQuery=lstRecommendationIds;
            }
        }
        if(lstRecommendationActrionsToQuery.size() > 0)
        {
            List<Recommendation_Action__c> lstRecommendationAction = [SELECT Id, Recommendation__c, Recommendation_Detail__c FROM Recommendation_Action__c WHERE Id =: lstRecommendationActrionsToQuery AND Action__c NOT IN ('Delivered', 'Termed') WITH USER_MODE];
            TotalNumberOfRecommendation = String.ValueOf(lstRecommendationAction.size());
            for(Recommendation_Action__c objRecommendationAction : lstRecommendationAction){
                if(string.IsNotBlank(objRecommendationAction.Recommendation_Detail__c) && string.IsNotBlank(objRecommendationAction.Recommendation__c)){
                    mapRecommendation_DetailAndAction.put(objRecommendationAction.Recommendation_Detail__c, objRecommendationAction.id);
                }
                else if(string.IsNotBlank(objRecommendationAction.Recommendation__c)){
                    mapRecommendationAndAction.put(objRecommendationAction.Recommendation__c, objRecommendationAction.id);
                }
            }
        }
        list<list<NBA_Recommendation_Wrapper>> lstRecommendationsToDisplay = new  list<list<NBA_Recommendation_Wrapper>>(); 
        List<NBA_Recommendation_Wrapper> lstRecommendations = new List<NBA_Recommendation_Wrapper>();
        NBA_Recommendation_Wrapper objrecommendation;
        
        if(!mapRecommendation_DetailAndAction.isEmpty())
        {
            list<Recommendation_Detail__c> lstRecommendationDetail = [SELECT Id, External_Id__c, Alert_Content_1__c, Alert_Content_2__c, Alert_Content_3__c, Alert_Content_4__c, Alert_Content_5__c, Alert_Content_6__c, Alert_Content_7__c, Alert_Content_8__c, Alert_Content_9__c, Alert_Content_10__c, Recommendation__c, Recommendation__r.name, Recommendation__r.message__C, Recommendation__r.Question_1__c, Recommendation__r.Question_2__c, Recommendation__r.Question_3__c, Recommendation__r.Question_4__c, Recommendation__r.Free_Text_Question_1__c, Recommendation__r.Free_Text_Question_2__c, Recommendation__r.Priority__c, Recommendation__r.Alert_Type__c, Recommendation__r.Termable__c, Recommendation__r.Link_Name_1__c, Recommendation__r.Link_Name_2__c, Recommendation__r.Link_Name_3__c, Recommendation__r.Link_Name_4__c, Recommendation__r.Link_Name_5__c, Recommendation__r.Link_Path_1__c, Recommendation__r.Link_Path_2__c, Recommendation__r.Link_Path_3__c, Recommendation__r.Link_Path_4__c, Recommendation__r.Link_Path_5__c FROM Recommendation_Detail__c where id =: mapRecommendation_DetailAndAction.keySet()];
			
            for(Recommendation_Detail__c objRecommendationDetail: lstRecommendationDetail)
            {
                objrecommendation = new NBA_Recommendation_Wrapper();
                objrecommendation.Name = String.IsNotBlank(objRecommendationDetail.Recommendation__r.name) ? objRecommendationDetail.Recommendation__r.name : '';
				String externalIdToSet =  objRecommendationDetail.External_Id__c;

				if(externalIdToSet != null && (externalIdToSet.endsWith('AM')|| externalIdToSet.endsWith('PM'))){
					externalIdToSet = externalIdToSet.substringBeforeLast(UNKNOWNACCOUNT_PIPECHARACTER_HUM);
				}

				objrecommendation.ExternalIdToSet = externalIdToSet;

                String message = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Message__c) ? objRecommendationDetail.Recommendation__r.Message__c : '';
                if(String.IsNotBlank(message)){
                    message  = message.replace('[Alert_Content_1__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_1__c) ? objRecommendationDetail.Alert_Content_1__c : '');
                    message  = message.replace('[Alert_Content_2__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_2__c) ? objRecommendationDetail.Alert_Content_2__c : '');
                    message  = message.replace('[Alert_Content_3__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_3__c) ? objRecommendationDetail.Alert_Content_3__c : '');
                    message  = message.replace('[Alert_Content_4__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_4__c) ? objRecommendationDetail.Alert_Content_4__c : '');
                    message  = message.replace('[Alert_Content_5__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_5__c) ? objRecommendationDetail.Alert_Content_5__c : '');
                    message  = message.replace('[Alert_Content_6__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_6__c) ? objRecommendationDetail.Alert_Content_6__c : '');
                    message  = message.replace('[Alert_Content_7__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_7__c) ? objRecommendationDetail.Alert_Content_7__c : '');
                    message  = message.replace('[Alert_Content_8__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_8__c) ? objRecommendationDetail.Alert_Content_8__c : '');
                    message  = message.replace('[Alert_Content_9__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_9__c) ? objRecommendationDetail.Alert_Content_9__c : '');
                    message  = message.replace('[Alert_Content_10__c]', string.isNotBlank(objRecommendationDetail.Alert_Content_10__c) ? objRecommendationDetail.Alert_Content_10__c : '');
                }
                objrecommendation.Message = String.IsNotBlank(message) ? message : '';
                objrecommendation.Question_1 = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Question_1__c) ? objRecommendationDetail.Recommendation__r.Question_1__c : '';
                objrecommendation.Question_2 = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Question_2__c) ? objRecommendationDetail.Recommendation__r.Question_2__c : '';
                objrecommendation.Question_3 = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Question_3__c) ? objRecommendationDetail.Recommendation__r.Question_3__c : '';
                objrecommendation.Question_4 = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Question_4__c) ? objRecommendationDetail.Recommendation__r.Question_4__c : '';
                objrecommendation.Free_Text_Question_1 = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Free_Text_Question_1__c) ?objRecommendationDetail.Recommendation__r.Free_Text_Question_1__c : '';
                objrecommendation.Free_Text_Question_2 = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Free_Text_Question_2__c) ? objRecommendationDetail.Recommendation__r.Free_Text_Question_2__c : '';
                objrecommendation.Priority = objRecommendationDetail.Recommendation__r.Priority__c;
                objrecommendation.AlertType = objRecommendationDetail.Recommendation__r.Alert_Type__c;
                objrecommendation.Termable = String.IsNotBlank(objRecommendationDetail.Recommendation__r.Termable__c) ? objRecommendationDetail.Recommendation__r.Termable__c : '';
                objrecommendation.RecommendationDetailId = objRecommendationDetail.id;
                objrecommendation.RecommendationId = objRecommendationDetail.Recommendation__c;
                objrecommendation.RecommendationActionId = mapRecommendation_DetailAndAction.get(objRecommendationDetail.id);
                objrecommendation.TotalRecommendation = TotalNumberOfRecommendation;
                
                if(String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Name_1__c) && String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Path_1__c)){
                    objrecommendation.Link_Name_1 = objRecommendationDetail.Recommendation__r.Link_Name_1__c;
                    objrecommendation.Link_Path_1 = objRecommendationDetail.Recommendation__r.Link_Path_1__c;
                }
                
                if(String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Name_2__c) && String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Path_2__c)){
                    objrecommendation.Link_Name_2 = objRecommendationDetail.Recommendation__r.Link_Name_2__c;
                    objrecommendation.Link_Path_2 = objRecommendationDetail.Recommendation__r.Link_Path_2__c;
                }
                
                if(String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Name_3__c) && String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Path_3__c)){
                    objrecommendation.Link_Name_3 = objRecommendationDetail.Recommendation__r.Link_Name_3__c;
                    objrecommendation.Link_Path_3 = objRecommendationDetail.Recommendation__r.Link_Path_3__c;
                }
				
                if(String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Name_4__c) && String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Path_4__c)){
                    objrecommendation.Link_Name_4 = objRecommendationDetail.Recommendation__r.Link_Name_4__c;
                    objrecommendation.Link_Path_4 = objRecommendationDetail.Recommendation__r.Link_Path_4__c;
                }
                
                if(String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Name_5__c) && String.IsNotBlank(objRecommendationDetail.Recommendation__r.Link_Path_5__c)){
                    objrecommendation.Link_Name_5 = objRecommendationDetail.Recommendation__r.Link_Name_5__c;
                    objrecommendation.Link_Path_5 = objRecommendationDetail.Recommendation__r.Link_Path_5__c;
                }
            lstRecommendations.add(objrecommendation);
            }
        }
        if(!mapRecommendationAndAction.isEmpty())
        {
            list<Recommendation> lstRecommendation = [SELECT id, Name, Message__c, Question_1__c, Question_2__c,Question_3__c, Question_4__c, Free_Text_Question_1__c, Free_Text_Question_2__c, Priority__c, Termable__c, Alert_Type__c,Link_Name_1__c, Link_Name_2__c, Link_Name_3__c, Link_Name_4__c, Link_Name_5__c, Link_Path_1__c, Link_Path_2__c, Link_Path_3__c, Link_Path_4__c, Link_Path_5__c, ExternalId  FROM Recommendation where id =: mapRecommendationAndAction.keySet() WITH USER_MODE];

				for(Recommendation objRecommendationRecord : lstRecommendation){
					objrecommendation = new NBA_Recommendation_Wrapper();
					objrecommendation.Name= String.IsNotBlank(objRecommendationRecord.Name) ? objRecommendationRecord.Name: '';
					objrecommendation.Message= String.IsNotBlank(objRecommendationRecord.Message__c) ? objRecommendationRecord.Message__c : '';
					objrecommendation.Question_1 = String.IsNotBlank(objRecommendationRecord.Question_1__c) ? objRecommendationRecord.Question_1__c : '';
					objrecommendation.Question_2 = String.IsNotBlank(objRecommendationRecord.Question_2__c) ? objRecommendationRecord.Question_2__c : '';
					objrecommendation.Question_3 = String.IsNotBlank(objRecommendationRecord.Question_3__c) ? objRecommendationRecord.Question_3__c : '';
					objrecommendation.Question_4 = String.IsNotBlank(objRecommendationRecord.Question_4__c) ? objRecommendationRecord.Question_4__c : '';
					objrecommendation.Free_Text_Question_1 = String.IsNotBlank(objRecommendationRecord.Free_Text_Question_1__c) ?objRecommendationRecord.Free_Text_Question_1__c : '';
					objrecommendation.Free_Text_Question_2 = String.IsNotBlank(objRecommendationRecord.Free_Text_Question_2__c) ? objRecommendationRecord.Free_Text_Question_2__c : '';
					objrecommendation.Priority = objRecommendationRecord.Priority__c;
					objrecommendation.AlertType = objRecommendationRecord.Alert_Type__c;
					objrecommendation.Termable = String.IsNotBlank(objRecommendationRecord.Termable__c) ? objRecommendationRecord.Termable__c : '';
					objrecommendation.RecommendationDetailId = String.isNotBlank(objRecommendationRecord.ExternalId) ? objRecommendationRecord.ExternalId : '';
					objrecommendation.RecommendationActionId = mapRecommendationAndAction.get(objRecommendationRecord.id);
					objrecommendation.TotalRecommendation = TotalNumberOfRecommendation;
					
					
					if(String.IsNotBlank(objRecommendationRecord.Link_Name_1__c) && String.IsNotBlank(objRecommendationRecord.Link_Path_1__c)){
						objrecommendation.Link_Name_1 = objRecommendationRecord.Link_Name_1__c;
						objrecommendation.Link_Path_1 = objRecommendationRecord.Link_Path_1__c;
					}
					
					if(String.IsNotBlank(objRecommendationRecord.Link_Name_2__c) && String.IsNotBlank(objRecommendationRecord.Link_Path_2__c)){
						objrecommendation.Link_Name_2 = objRecommendationRecord.Link_Name_2__c;
						objrecommendation.Link_Path_2 = objRecommendationRecord.Link_Path_2__c;
					}
					
					if(String.IsNotBlank(objRecommendationRecord.Link_Name_3__c) && String.IsNotBlank(objRecommendationRecord.Link_Path_3__c)){
						objrecommendation.Link_Name_3 = objRecommendationRecord.Link_Name_3__c;
						objrecommendation.Link_Path_3 = objRecommendationRecord.Link_Path_3__c;
					}
					
					if(String.IsNotBlank(objRecommendationRecord.Link_Name_4__c) && String.IsNotBlank(objRecommendationRecord.Link_Path_4__c)){
						objrecommendation.Link_Name_4 = objRecommendationRecord.Link_Name_4__c;
						objrecommendation.Link_Path_4 = objRecommendationRecord.Link_Path_4__c;
					}
					
					if(String.IsNotBlank(objRecommendationRecord.Link_Name_5__c) && String.IsNotBlank(objRecommendationRecord.Link_Path_5__c)){
						objrecommendation.Link_Name_5 = objRecommendationRecord.Link_Name_5__c;
						objrecommendation.Link_Path_5 = objRecommendationRecord.Link_Path_5__c;
					}
					 lstRecommendations.add(objrecommendation);  
				}
            }

            list<NBA_Recommendation_Wrapper> lstRecommendationWithPriority = new list<NBA_Recommendation_Wrapper>();
			list<NBA_Recommendation_Wrapper> lstRecommendationEmptyPriority = new list<NBA_Recommendation_Wrapper>();

            for (NBA_Recommendation_Wrapper oRecom : lstRecommendations){
                if(oRecom.Priority != null) {
                    lstRecommendationWithPriority.add(oRecom);
                } else {
                    lstRecommendationEmptyPriority.add(oRecom);
                }
            }

            lstRecommendationWithPriority.sort();
        
            for ( Integer i = 0; i < lstRecommendationWithPriority.size(); i++){
                lstRecommendationWithPriority[i].RecommendationCount = String.valueof(i + 1);
            }

            lstRecommendations = lstRecommendationWithPriority;
			
			Integer sPriorityListSize = lstRecommendationWithPriority.size();

            for(NBA_Recommendation_Wrapper objRecomWrapper : lstRecommendationEmptyPriority){
                sPriorityListSize =  sPriorityListSize  + 1;
                objRecomWrapper.RecommendationCount = String.valueof(sPriorityListSize);
                lstRecommendations.add(objRecomWrapper);
            }

            lstRecommendationsToDisplay.add(lstRecommendations);

            return lstRecommendationsToDisplay;
    }
}