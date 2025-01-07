/************************************************************************** 
Apex Class Name  : NBADynamicStrategy_S_HUM 
Version          : 1.0  
Created Date     : August 27, 2020
Function         : Class to execute recommendation Strategy.
*****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Yogesh Gupta                                                            Original Version
* Suraj patil                                   11/02/2020                Defect 1904
* Satyam Malviya                                11/1/2020                 Req - 1509134 - PR00090631 - MF12 - MVP - Guidance Alert on RCC Quick Start
* Satyam Malviya                                11/1/2020                 Req - 1509050 - PR00090631 - MF 12 - MVP - Guidance Alert on Humana Pharmacy Quick Start+ page
* Satyam Malviya                                11/1/2020                 Req - 1499646 - PR00090631- MF 12 - MVP - Contact Handling Alert Icon on the Member Account Page 
* Apurva Urkude                                 21/04/2021                Req -  2164068- PRJ0002736 - 2021 MF6 - SF - Tech - CheckMarx vulnerabilities fix - NBA Pages
* Aishwarya Pawar								19/07/2021		 		  User Story 2328633 T1PRJ00022885/96118 - MF 13 - 1st Time Caller Alert
* Aishwarya Pawar								07/29/2021		 		  DF- 3453 - REQ-2328633-T1PRJ00022885-First Time Caller" alert displayed.
* Nirmal Garg										  11/26/2021				Defect 4215 - removed dependency from global constant and hum constant
* Aishwarya Pawar								27/4/2022				  REQ 2948386– Classic - CRM90 Day Recertification Notification in Medicaid Details Section for OH MCD
* Aishwarya Pawar                				05/13/2022                REQ - 3285223 Alert Configuration on Plan Member Page
* Aishwarya Pawar                               05/13/2022                REQ - 3127063  Lightning - CRM90 Day Recertification Notification in Medicaid Details Section  for OH MCD 
* Pinky Vijur                                   10/03/2022                User Story 3468588: T1PRJ0054938 - FR 09.17 CRM Service must create and display an Command Alert on the Person Account Page for members who are on the OH Medicaid Pending Plan
* Nilesh Gadkar                                 05/04/2023  		  User Story 3421330: FR09.09: CRM Service must create and display a command alert notification on the Person Account Page when the HRA flag/indicator = N
* Nilesh Gadkar                                 05/31/2023                DF-7710 US3421330_T1PRJ0881713_User is able to see alert on person account page more than once when the member is having a plan with effective date more than 90days 
* Swapnali Sonawane                             06/16/2023                US-4548314 MCD-NS-LA for HRA and HNA for Command Alert
* Nilesh Gadkar					                07/13/2023		          User Story 4789267: T1PRJ0881713 - FR09.09: Display command alert notification on the Person Account Page only for Active and Future state policies when the HRA flag/indicator = N
***************************************************************************************************/
public with sharing class NBADynamicStrategy_S_HUM {
		public static final Integer STRATEGY_MAXRESULTS = 25;
		public static Map<String,String> ExecuteNBAStrategy(String contextRecordId, String interactionId, String sPageName, String enterpriseId, String accountId, Integer sAge, String sFirstTimeCaller ,String MCDProductEligible ,String HRAEligible,Integer showAlertCount,Boolean bCalledFromLightning , Boolean bInteractionId) {   
        try{
        Boolean Switch_3421330 = HUMUtilityHelper.isCRMFunctionalityON('3421330');
        Boolean Switch_4789267 = HUMUtilityHelper.isCRMFunctionalityON('4789267');
        string workQueue ='';
        Integer limitOffers;
        Interaction__c oIntRec = new Interaction__c();
        String Allpages = 'All Pages';  
        Boolean isGuidanceAlerts =  true;
        ConnectApi.NBAStrategyInput nBAStrategyInputData =  new ConnectApi.NBAStrategyInput();
        List<Recommendation> listOfRecommendationToReturn = new List<Recommendation>();
        Map<String, String> mapOfDetailWithRecommendationId = new Map<String, String>();
        List<String> listOfRecommendationDetailsIdsToReturn = new List<String>();
        List<Recommendation_Action__c> listOfRecommendationActionFromStrategy =  new List<Recommendation_Action__c>();
        Map<String, String> mapOfRecomIdWithExternalId = new Map<String, String>();
        Map<String, String> mapOfRActionIdWithType = new Map<String, String> ();
        
        //Mapping the request to execute strategy based on the input parameter                
        nBAStrategyInputData.contextRecordId = contextRecordId;
        nBAStrategyInputData.maxResults = STRATEGY_MAXRESULTS ;
        Map<String, String> mapOfDataString = new Map<String, String>();
        mapOfDataString.put('interationIdRequest' ,interactionId); 
        mapOfDataString.put('contextRecordIdRequest' ,contextRecordId ); 
        mapOfDataString.put('pageNameRequest' ,sPageName); 
        mapOfDataString.put('enterpriseIdRequest' , enterpriseId);
        mapOfDataString.put('accountIdRequest' , accountId);  
        mapOfDataString.put('Age' , String.valueof(sAge));
        mapOfDataString.put('FirstTimeCaller' , sFirstTimeCaller );
        mapOfDataString.put('MCDProductEligible' , MCDProductEligible );
        mapOfDataString.put('HRAEligible' , HRAEligible );
        mapOfDataString.put('CalledFromLightning' , String.valueOf(bCalledFromLightning) );
        mapOfDataString.put('bInteractionId' , String.valueOf(bInteractionId) );
        nBAStrategyInputData.strategyContext = mapOfDataString;
        nBAStrategyInputData.debugTrace = true;
        ConnectApi.NBARecommendations returnNBARecommendations ;               
       
        
		List<User> listOfUsers = [Select Id, Name, Current_Queue__c From User Where Id =: UserInfo.getUserId()];      
        //Checking if the interaction is created or not                     
        if(string.isNotBlank(interactionId) && interactionId.length()==18) {
            List<Interaction__c> lstInt = [SELECT Id, CreatedBy.Current_Queue__c, Interacting_With_type__c, Interacting_With__c FROM Interaction__c WHERE Id =: interactionId ];
            if(lstInt != null && lstInt.size() > 0){
    			oIntRec = lstInt[0];
   				workQueue = oIntRec.CreatedBy.Current_Queue__c;
			}
            else{
                if(listOfUsers != null  && listOfUsers.size() != 0) { 
            	workQueue = listOfUsers[0].Current_Queue__c;
        		}
            }            
        }
        else if(listOfUsers != null  && listOfUsers.size() != 0) { 
            workQueue = listOfUsers[0].Current_Queue__c;
        }
        //checking if the Interacting With an Interacting About is the same member
        if(oIntRec.Interacting_With__c !=accountId){
        	mapOfDataString.put('FirstTimeCaller' , 'False' );    
        }
            mapOfDataString.put('bPendingPlan' , 'false');
    if(Id.valueof(contextRecordId).getsObjectType()== schema.Account.sObjectType){
        List<Policy_Member__c> polMems = [SELECT ID FROM Policy_Member__c WHERE Member__c =: contextRecordId AND Group_Number__c =: System.label.ID_Card_Pending_Plans AND Status__c != 'Termed'];
        if(polMems.size()>0){
            mapOfDataString.put('bPendingPlan' , 'true');
        }
    }
    else if(Id.valueof(contextRecordId).getsObjectType()== schema.Policy_Member__c.sObjectType){
        List<Policy_Member__c> polMems = [SELECT ID FROM Policy_Member__c WHERE Id=: contextRecordId AND Group_Number__c =: System.label.ID_Card_Pending_Plans AND Status__c != 'Termed'];
        if(polMems.size()>0){
            mapOfDataString.put('bPendingPlan' , 'true');
        }
    }
    else if(Id.valueof(contextRecordId).getsObjectType()== schema.MemberPlan.sObjectType){
        List<MemberPlan> polMems = [SELECT ID FROM MemberPlan WHERE Id=: contextRecordId AND Policy_Member__r.Group_Number__c =: System.label.ID_Card_Pending_Plans AND Policy_Member__r.Status__c != 'Termed'];
        if(polMems.size()>0){
            mapOfDataString.put('bPendingPlan' , 'true');
        }
    }

                
		//Checking the recordId For which we need to execute Strategy
        if(Id.valueof(contextRecordId).getsObjectType()== schema.Account.sObjectType){
            returnNBARecommendations = ConnectApi.NextBestAction.executeStrategy('Account_Strategy', nBAStrategyInputData);   
        }
        else if(Id.valueof(contextRecordId).getsObjectType()== schema.Policy_Member__c.sObjectType){
             returnNBARecommendations = ConnectApi.NextBestAction.executeStrategy('Policy_Member_Strategy', nBAStrategyInputData); 
        }
        else if(Id.valueof(contextRecordId).getsObjectType()== schema.MemberPlan.sObjectType){
             returnNBARecommendations = ConnectApi.NextBestAction.executeStrategy('Plan_Member_Strategy', nBAStrategyInputData);
        }
       
        
        Set<Id> setOfAllRecomIds =  new Set<Id>();
        for(ConnectApi.NBARecommendation curReturnedRec : returnNBARecommendations.recommendations) {                    
             setOfAllRecomIds.add(((ConnectApi.NBANativeRecommendation) curReturnedRec.target).id);
        }
		
        List<Work_Queue_Setup__c> lstWorkQueue = [SELECT Id, Classification_Type__c, Classification_Type__r.Is_Active__c, Classification_Type__r.Guidance_Alert_Limit__c, Work_Queue_Name__c 
                                                    FROM Work_Queue_Setup__c WHERE Name =:workQueue LIMIT 1];
        if(lstWorkQueue.size() > 0) {
            limitOffers = (lstWorkQueue.get(0).Classification_Type__r.Is_Active__c ? (Integer)lstWorkQueue.get(0).Classification_Type__r.Guidance_Alert_Limit__c : GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM); 
        }
        
        Set<Id> setOfWorkQueueSetUpIds = new Set<Id>();
        for(Work_Queue_Setup__c wQSetup : lstWorkQueue){
           setOfWorkQueueSetUpIds.add(wQSetup.Id);
        }
        
        List<Recommendation_Work_Queue_Juntion__c> listOfRecWorkQueueJunction = [Select Id, Name, Recommendation__c, Work_Queue_Setup__c
                                                                                 From Recommendation_Work_Queue_Juntion__c
                                                                                 Where Recommendation__c IN: setOfAllRecomIds];
        Set<Id> setOfRecomIds = new Set<Id>(); 
        Set<Id> setOfRecomIdsToRemove = new Set<Id>(); 
        
        for(Recommendation_Work_Queue_Juntion__c junction : listOfRecWorkQueueJunction) {
            if(setOfWorkQueueSetUpIds!= null && setOfWorkQueueSetUpIds.contains(junction.Work_Queue_Setup__c))
                setOfRecomIds.add(junction.Recommendation__c);
        
            else {
                setOfRecomIdsToRemove.add(junction.Recommendation__c);
        
            }
        }                                                                    
        for(ConnectApi.NBARecommendation curReturnedRec : returnNBARecommendations.recommendations) {                    
             if(setOfRecomIds.contains(((ConnectApi.NBANativeRecommendation) curReturnedRec.target).id) || !setOfRecomIdsToRemove.contains(((ConnectApi.NBANativeRecommendation) curReturnedRec.target).id))
             {
                mapOfRecomIdWithExternalId.put(((ConnectApi.NBANativeRecommendation) curReturnedRec.target).id, curReturnedRec.ExternalId);                       
             }
        }
        list<Recommendation_Action__c> lstRecomDynamicCriteraAction = new list<Recommendation_Action__c>();                            
        lstRecomDynamicCriteraAction  = [SELECT Id,Alert_Type__c,Action_DateTime__c, Recommendation__c, Action__c,Alert_Name__c  FROM Recommendation_Action__c where Recommendation__c IN: mapOfRecomIdWithExternalId.keySet() AND Recommendation_Detail__c = null AND Interacting_About__c =: accountId AND Action__C IN ('Termed','Delivered','Acknowledged')];
        if(lstRecomDynamicCriteraAction.size() >0) {
          Integer daysBetween,productDayLimit,alertCount;
          String IssueState, EffectiveDate;
          Account Hraaccount = [SELECT Id, Group_Number__c, Recordtype.Name,HRA_HNA_Completed__c FROM Account where id =: accountId limit 1];
          for(Recommendation_Action__c value : lstRecomDynamicCriteraAction ) {
              daysBetween = Integer.valueOf(Date.Today().daysBetween(value.Action_DateTime__c.date()));
              if(value.Alert_Type__c == 'Command' && daysBetween <- 90){
                  continue;
              }else if(value.Alert_Type__c == 'Command' && daysBetween > -90 && value.Alert_Name__c != System.Label.MCDPENDEDPLAN &&  value.Alert_Name__c=='MCD - Redetermination'){
                  mapOfRecomIdWithExternalId.remove(value.Recommendation__c);
              }else if(value.Alert_Type__c != 'Command' && value.Action__c != 'Acknowledged'){
                  mapOfRecomIdWithExternalId.remove(value.Recommendation__c);  
              }
              else if(Switch_3421330 && value.Alert_Type__c == 'Command' && value.Alert_Name__c =='MCD - HRA'){
                String HRAJson = Hraaccount.HRA_HNA_Completed__c;
                String HRAFlag='';
                if(!String.isBlank(HRAJson))
                {
                    HRAFlag = getHRAStatus(HRAJson);
                }
                Map<String, Integer> HRAIssueStateWithDays = new Map<String, Integer>();
                For(HRA__mdt state: [Select Issue_State__c,Days__c from HRA__mdt]){
                    HRAIssueStateWithDays.put(state.Issue_State__c, Integer.valueOf(state.Days__c));    
                }
                //for classic  
                if(bCalledFromLightning == false)
                {
                    list<Policy_Member__c> policyMemberList = new list<Policy_Member__c>();
                    if(Switch_4789267)
                    {
                        policyMemberList = [SELECT Id,Issue_State__c,Effective_Date__c FROM Policy_Member__c where Member__c =: accountId and Issue_State__c IN:  HRAIssueStateWithDays.keySet() and Product_Type__c='MCD' and Status__c = 'Active' ];
                    }
                    else
                    {
                    	policyMemberList = [SELECT Id,Issue_State__c,Effective_Date__c FROM Policy_Member__c where Member__c =: accountId and Issue_State__c IN:  HRAIssueStateWithDays.keySet() and Product_Type__c='MCD' ];
                    }
                    if(policyMemberList.size() > 0)
                    {
                        productDayLimit = getDayLimit(policyMemberList[0].Issue_State__C);
                        EffectiveDate = policyMemberList[0].Effective_Date__c;  
                        alertCount = getAlertCount(showAlertCount, EffectiveDate, productDayLimit, accountId);                  
                    }
                }
                else{
                    list<MemberPlan> memberPlanList = [SELECT Id,Issue_State__c,EffectiveFrom FROM MemberPlan where MemberId =: accountId and Issue_State__c IN:  HRAIssueStateWithDays.keySet() and Product_Type__c='MCD' and Member_Coverage_Status__c !='Termed'];
                    if(memberPlanList.size()>0){
                        productDayLimit = getDayLimit(memberPlanList[0].Issue_State__C);
                        String effectiveFrom = memberPlanList[0].EffectiveFrom.format();
                        alertCount = getAlertCount(showAlertCount, effectiveFrom, productDayLimit, accountId);                   
                    }
                }
                if(checkHRAAlert(alertCount,showAlertCount,HRAFlag,daysBetween))
                {		
                    mapOfRecomIdWithExternalId.remove(value.Recommendation__c); 
                } 
              }
           }          
        }   
        List<Recommendation> listOfRecommendations = new List<Recommendation>();
        if(oIntRec!= null && oIntRec.Interacting_With_type__c != null){
            listOfRecommendations = [SELECT Id,  Name,Show_To__c, Alert_Type__c, Location__c,  Interacting_With_Type__c, Active__c FROM 			Recommendation 
                                                        Where Id IN: mapOfRecomIdWithExternalId.keySet() AND Active__c = true 
                                                        AND (Location__c INCLUDES (:sPageName) OR Location__c INCLUDES (: Allpages))
                                                         AND Interacting_With_Type__c includes (:oIntRec.Interacting_With_type__c)
                                                           order by Priority__c];
        
        }
        else{
            listOfRecommendations = [SELECT Id,  Name,Show_To__c, Alert_Type__c, Location__c,  Interacting_With_Type__c, Active__c FROM Recommendation 
                                                        Where Id IN: mapOfRecomIdWithExternalId.keySet() AND Active__c = true 
                                                        AND (Location__c INCLUDES (:sPageName) OR Location__c INCLUDES (: Allpages)) order by Priority__c];
            
            
        }
		
        Account objAccount = [SELECT Id, Group_Number__c, Recordtype.Name FROM Account where id =: accountId limit 1]; 
        for(Recommendation recom : listOfRecommendations){
        if((objAccount.Recordtype.Name == 'Member' && (recom.Show_To__c == 'Member' || recom.Show_To__c == 'Group + Member')) || (objAccount.Recordtype.Name == 'Group' && (recom.Show_To__c == 'Group' || recom.Show_To__c == 'Group + Member')))
        {
            if(recom.Alert_Type__c == 'Guidance' ){
                if(limitOffers > 0) {                               
                limitOffers--;            
                Recommendation_Action__c recommendationAction = new Recommendation_Action__c();
                recommendationAction .Recommendation__c = recom.Id;
                recommendationAction .Recommendation_Detail__c = mapOfRecomIdWithExternalId.get(recom.Id);
                recommendationAction .Interaction__c = interactionId;
                recommendationAction .Associate_Current_Queue__c = listOfUsers[0].Current_Queue__c;
                recommendationAction .Interacting_About__c =  accountId;
                recommendationAction .Alert_Name__c = recom.Name;
                recommendationAction .Alert_Type__c = recom.Alert_Type__c;
                listOfRecommendationActionFromStrategy.add(recommendationAction );                
                }
            }
            else{
            
                Recommendation_Action__c recommendationAction = new Recommendation_Action__c();
                recommendationAction .Recommendation__c = recom.Id;
                recommendationAction .Recommendation_Detail__c = mapOfRecomIdWithExternalId.get(recom.Id);
                recommendationAction .Interaction__c = interactionId;
                recommendationAction .Associate_Current_Queue__c = listOfUsers[0].Current_Queue__c;
                recommendationAction .Interacting_About__c =  accountId;
                recommendationAction .Alert_Name__c = recom.Name;
                recommendationAction .Alert_Type__c = recom.Alert_Type__c;             
                listOfRecommendationActionFromStrategy.add(recommendationAction );               
                
                }
           }                      
        }
        List<Recommendation_Action__c> listOfFianlRecommendationActionToInsert =  new List<Recommendation_Action__c>();
        Map<String, String> mapOfRActionIdWithIsContactHandling = new Map<String, String> ();
        if(string.IsNotBlank(interactionId )){
            list<Interaction_Member__c> lstInteractionMembers = [SELECT Id, Name, Interacting_About__c, Interaction__c FROM Interaction_Member__c where interaction__c =: interactionId AND Interacting_About__c =: accountId];
            list<Recommendation_Action__c> lstRecomAction = new list<Recommendation_Action__c>();
            if(lstInteractionMembers.size() > 0){
                lstRecomAction = [SELECT Id, Interacting_About__c, Recommendation_Detail__c, Recommendation__c,  Alert_Viewed_DateTime__c, Interaction__c,Action__c, Alert_Type__c FROM Recommendation_Action__c where Interaction__c =: interactionId AND Interacting_About__c =: accountId];
            }
            else{
                lstRecomAction = [SELECT Id, Interacting_About__c, Recommendation_Detail__c, Recommendation__c,  Alert_Viewed_DateTime__c, Interaction__c,Action__c, Alert_Type__c FROM Recommendation_Action__c where Interaction__c =: interactionId];
            }
            Map<String,Recommendation_Action__c> mapRecomAndRecomAction = New Map<String,Recommendation_Action__c>();
            for(Recommendation_Action__c objListRecomAction : lstRecomAction){
                mapRecomAndRecomAction.put(objListRecomAction.Recommendation__c, objListRecomAction);
            }
            
            For(Recommendation_Action__c objRecomAction : listOfRecommendationActionFromStrategy){
                if(mapRecomAndRecomAction.Keyset().contains(objRecomAction.Recommendation__c))
                {
                    Recommendation_Action__c oRecomAction = mapRecomAndRecomAction.get(objRecomAction.Recommendation__c);
                    if(string.IsBlank(oRecomAction.Action__c) || oRecomAction.Action__c == '' || oRecomAction.Action__c == 'Acknowledged' || oRecomAction.Action__c == 'None')
                    {
                        mapOfRActionIdWithIsContactHandling.put(oRecomAction.Id, oRecomAction.Alert_Type__c);  
                    }
                }
                else{
                    listOfFianlRecommendationActionToInsert.add(objRecomAction);
                }
            }
        }
        else{
            listOfFianlRecommendationActionToInsert.addAll(listOfRecommendationActionFromStrategy);
        }
        SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.CREATABLE, listOfFianlRecommendationActionToInsert);
        listOfFianlRecommendationActionToInsert= securityDecision.getRecords();
        if(listOfFianlRecommendationActionToInsert.size() > 0){   
             insert listOfFianlRecommendationActionToInsert; 
        }
        for(Recommendation_Action__c recomAction : listOfFianlRecommendationActionToInsert){

            mapOfRActionIdWithIsContactHandling.put(recomAction.Id, recomAction.Alert_Type__c);       
        } 
        return mapOfRActionIdWithIsContactHandling;
        }
        catch (Exception e){
            HUMExceptionHelper.logErrors(e,'NBADynamicStrategy_S_HUM','ExecuteNBAStrategy');
            Map<String, String> mapOfRActionIdWithIsContactHandling = new Map<String, String> ();
            return mapOfRActionIdWithIsContactHandling;
        }
    }
    public static string getHRAStatus(string HRAJson)
    {
        try
        {
            HumHRAHNA_DTO HRAResponse;
            HRAResponse = (HumHRAHNA_DTO)system.json.deserialize(HRAJson,HumHRAHNA_DTO.class);
            return HRAResponse.HRAStatus;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'NBADynamicStrategy_S_HUM','getHRAStatus');
            return '';
        }
    }  
    public static Boolean checkHRAAlert(Integer alertCount,Integer showAlertCount,String HRAFlag,Integer daysBetween )
    {
        
        if((alertCount>=showAlertCount && HRAFlag=='N') ||( daysBetween <- 90 ) || HRAFlag=='Y')
            return true;
        else {
            return false;
        }
    }
    public static Integer getAlertCount(Integer showAlertCount, String EffectiveDate, Integer productDayLimit, String accountId)
    {
        Datetime startDate, endDate;
        if(showAlertCount == 3)
        {
            Date d1 = Date.parse(EffectiveDate);
            String d2 = Date.Today().format();
            Date todayDate=Date.parse(d2);
            String s1 = '' + todayDate.addDays(1);
            startDate = DateTime.ValueofGmt('' + d1);
            endDate = DateTime.valueOfGmt(s1);
        }
        else {
            Date d1 = Date.parse(EffectiveDate);
            String d2 = Date.Today().format();
            Date todayDate = Date.parse(d2);
            String s1 = ''+ todayDate.addDays(1);
            Integer daysDiff = d1.daysBetween(todayDate);
            Integer reminder = daysDiff / productDayLimit;
            Integer daysToAdd = productDayLimit * reminder;
            d1=d1.addDays(daysToAdd+1);
            startDate = DateTime.valueOfGmt('' + d1);
            endDate = DateTime.valueOfGmt(s1);
            if(startDate == endDate)
            {
                startDate = startDate.addDays(-1);
            }
        }
        list<Recommendation_Action__c> hraactionlist=[SELECT Id, Action__c, Alert_Name__c, Alert_Type__c, Recommendation__c FROM Recommendation_Action__c where Alert_Type__c='Command' and Alert_Name__c='MCD - HRA' and Action__c='Acknowledged' and Interacting_About__c =: accountId and Action_DateTime__c >=: startDate  and Action_DateTime__c <=: endDate ];
        if(hraactionlist.size() > 0)
            return hraactionlist.size();
        else {
            return 0;
        }
    }

    public static Integer getDayLimit(String IssueState)
    {
        List<HRA__mdt> state= [Select Issue_State__c,Days__c from HRA__mdt where Issue_State__C =: IssueState limit 1];
        if(state.size() > 0)
            return Integer.valueOf(state[0].Days__c);
        else {
            return 0;
        }
    }
       
}