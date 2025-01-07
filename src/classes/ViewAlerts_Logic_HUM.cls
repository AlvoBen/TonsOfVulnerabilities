/*****************************************************************************************
Apex class Name    : ViewAlerts_Logic_HUM
Version            : 1.0
Function           : Alerts Logic Functionality  
Created Date       : 06/30/2022 
Test Class         : AccountDetailViewAlerts_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
    Abhishek Mangutkar                                   06/30/2022          Initial version
    Ceasar Sabarre                                            01/24/2023              REQ-4153700  Alerts - Interaction id logic fix for NBA
    Nilesh Gadkar                                        05/04/2023          User Story 3421330: FR09.09: CRM Service must create and display a command alert notification on the Person Account Page when the HRA flag/indicator = N
    Kinal Mangukiya                                      05/05/2023          User Story 4534222: T1PRJ0891339 - 2023 Arch Remediation - TECH - CRM Service Error Trending(SFDC)
    Swapnali Sonawane                                    06/16/2023          US-4548314 MCD-NS-LA for HRA and HNA for Command Alert
    Nilesh Gadkar					 					 07/13/2023	     	 User Story 4789267: T1PRJ0881713 - FR09.09: Display command alert notification on the Person Account Page only for Active and Future state policies when the HRA flag/indicator = N
 *********************************************************************************************************************************/

public With sharing class ViewAlerts_Logic_HUM {
    
    public static final string sBusinessAccount = 'Business Account';
    Public static final string sPersonAccount = 'Person Account';
    Public Static final String sMember = 'Member';
    Public Static final String sAccount = 'Account';
    
    Public Static final String INTERACTIONID_HUM = '&interactionId=';
    Public Static final string STRING_INTERACTIONID_HUM = 'c__interactionId=';
	Public Static final Integer NUMBER_CONSTANT_FIFTEEN_HUM = 15;
    Public Static final Integer NUMBER_CONSTANT_THIRTYTHREE_HUM = 33;
    Public Static final Integer NUMBER_CONSTANT_MINUSONE_HUM = -1;
    
    public static AlertDetails_DTO_HUM init(String sAccountPageName, String sOtherPageName, String sAccountId, String polId, String URLString, Boolean bCalledFromLightning){  
            AlertDetails_DTO_HUM alerts = new AlertDetails_DTO_HUM();
        try{
            alerts.listRecommendationActionIds = new list<string>();
            alerts.mapOfRecommendationActionsIdWithType = new Map<String, String>();  
            alerts.listcommandRecActionIds = new list<string>();
            alerts.isContactHandlingAlert = false;
            alerts.isGuidanceAlert = false;  
            alerts.isCommandAlert = false;
            alerts.sFirstTimeCaller = 'true';
            alerts.sViewAccountOrMember = sMember;
            
            
			set<string> recTypeAccountSet = new Set<String>{'Agent/Broker', 'Group', 'Provider', 'Unknown Agent/Broker', 'Unknown Group', 'Unknown Provider'};
			set<string> recTypeCaseSet = new Set<String>{'Case', 'Case Edit' , 'Case Closed Edit'};
            if(sAccountPageName != null) {
                 if(recTypeAccountSet.contains(sAccountPageName)){
                    sAccountPageName = sBusinessAccount;
                    alerts.sViewAccountOrMember = 'Account';
                 }
                 else if(!recTypeCaseSet.contains(sAccountPageName)){
                     sAccountPageName= sPersonAccount;
                 }
             }
            //Check if string passed is an instance of an ID ; will return false if strAccountIDValue is null, empty and not ID
            if(sAccountId instanceOf Id && Id.valueOf(sAccountId).getsObjectType() == Schema.Account.sObjectType)
            {                
                List<account> lstAccount = AccountDetailViewAlerts_LD_HUM.getAccountList(sAccountId);
                if(lstAccount.size()>0){
                    alerts.enterpriseId = lstAccount[0].Enterprise_Id__C;
                    alerts.sAge = String.isNotBlank(lstAccount[0].Birthdate__c) ? lstAccount[0].Birthdate__c.replace('-','/').replace('9999','4000').replace('0001','1700') : null;
                }
            }
            if(String.isNotBlank(alerts.sAge)) {
                Date Mem_Birthdate = Date.parse(alerts.sAge );
                alerts.sInquiuredAboutAge = Integer.valueOf(Mem_Birthdate.daysBetween(Date.Today())/365.2425);
            } 
            
            if(bCalledFromLightning){
               string[] lstUrlString = String.isNotBlank(urlString) &&  urlString.contains(STRING_INTERACTIONID_HUM) ? urlString.split(STRING_INTERACTIONID_HUM) : new String[]{};
                 alerts.interactionId= lstUrlString.size() > 1 ? lstUrlString[1].left(18) : null;
            }
            else{
                if(String.isNotBlank(URLString)){
                Integer iStartPos = URLString.indexOf(INTERACTIONID_HUM);
                 if (iStartPos != NUMBER_CONSTANT_MINUSONE_HUM && !URLString.endsWith(INTERACTIONID_HUM)) {
                     //Fetching Interaction Details for ID mentioned in the URL.
                         alerts.interactionId= URLString.substring(iStartPos + NUMBER_CONSTANT_FIFTEEN_HUM, iStartPos + NUMBER_CONSTANT_THIRTYTHREE_HUM);                       
                 }
                  
                }
                
            }
            if(string.isNotBlank( alerts.interactionId) &&  alerts.interactionId.length() == 18){
                if( alerts.interactionId.startswith(Schema.SObjectType.Interaction__c.keyprefix)){
                    alerts.bInteractionId = true;
                }else{
                    alerts.bInteractionId = false;
                }
            }else{
                alerts.bInteractionId = false;
            }
            
            if(!Test.isRunningTest()){
                if(string.IsNotBlank(sOtherPageName) || string.IsNotBlank(sAccountPageName)){ 
                   	Map<String, Integer> IssueStateWithDays = new Map<String, Integer>();
                    Map<String, Integer> HRAIssueStateWithDays = new Map<String, Integer>();
					For(Recommendation__mdt state: [Select Issue_State__c,Days__c from Recommendation__mdt]){
							IssueStateWithDays.put(state.Issue_State__c, Integer.valueOf(state.Days__c));    
					} 
			        For(HRA__mdt state: [Select Issue_State__c,Days__c from HRA__mdt]){
			                HRAIssueStateWithDays.put(state.Issue_State__c, Integer.valueOf(state.Days__c));    
			        }                    
                    String MCDProductEligible = 'false';
                    String HRAEligible='false';   
                    Map<Integer, Integer> showAlerts; 
                    Integer showAlertCount=0;                
					if(bCalledFromLightning){
						// for lightning
						if(IssueStateWithDays.size()>0){
							list<MemberPlan> memberPlanList = [SELECT Id,Redetermination_Date__c,Issue_State__c,EffectiveFrom,EffectiveTo,Member.HRA_HNA_Completed__c FROM MemberPlan where MemberId =: sAccountId and Issue_State__c IN:  IssueStateWithDays.keySet() and Product_Type__c='MCD' and Member_Coverage_Status__c !='Termed'];
                            if(memberPlanList.size()>0){
                                for(MemberPlan memberObj: memberPlanList){
                                        String HRAJson = memberObj.Member.HRA_HNA_Completed__c;
                                        String HRAFlag='';
                                        if(!String.isBlank(HRAJson))
                                        {
                                            HRAFlag = getHRAStatus(HRAJson);
                                        }
                                        if(HRAFlag=='N' && memberObj.EffectiveFrom!=null){                                        
                                            showAlerts = checkDays(memberObj.EffectiveFrom, HRAIssueStateWithDays.get(memberObj.Issue_State__c));
                                            Set<Integer> days = showAlerts.keySet();
                                            Integer daysBetween=0;
                                            for(Integer i : days)
                                            {
                                                daysBetween = i;
                                                break;
                                            }
                                            if(0<=daysBetween & daysBetween<=HRAIssueStateWithDays.get(memberObj.Issue_State__c)){
                                                HRAEligible = 'true';
                                                showAlertCount = showAlerts.get(daysBetween);
                                                break;
                                            }
                                        }
                                }
                                if(memberPlanList.size()>0){
                                    for(MemberPlan memberObj: memberPlanList){
                                    if(memberObj.Redetermination_Date__c!= null){
                                            Integer daysBetween = Integer.valueOf(Date.Today().daysBetween(memberObj.Redetermination_Date__c));
                                            if(0<=daysBetween & daysBetween<=IssueStateWithDays.get(memberObj.Issue_State__c)){
                                                MCDProductEligible = 'true';
                                                break;
                                            } 
                                            }								
                                        }  
                                    }
                                }
                        }
						if(polId == null)
						{
							alerts.mapOfRecommendationActionsIdWithType = NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(sAccountId, alerts.interactionId, sAccountPageName, alerts.enterpriseId, sAccountId,alerts.sInquiuredAboutAge, alerts.sFirstTimeCaller, MCDProductEligible,HRAEligible,showAlertCount,true, alerts.bInteractionId); 
							alerts.listRecommendationActionIds = new List<String>(alerts.mapOfRecommendationActionsIdWithType.keySet());                    
						}
						else
						{
							list<Case> sCount = [SELECT Id FROM Case where Member_Plan_Id__c =: polId and  Origin in ('Web Chat','Inbound Call') limit 1 ] ;
							if(sCount.size() >0) {
								alerts.sFirstTimeCaller = 'False';
							}							
							alerts.mapOfRecommendationActionsIdWithType = NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(polId, alerts.interactionId, sOtherPageName, alerts.enterpriseId, sAccountId ,alerts.sInquiuredAboutAge, alerts.sFirstTimeCaller,MCDProductEligible,HRAEligible,showAlertCount,true, alerts.bInteractionId);  
							alerts.listRecommendationActionIds = new List<String>(alerts.mapOfRecommendationActionsIdWithType.keySet()); 								
						}
					}
					else{
						//for classic
                        if(IssueStateWithDays.size()>0){
			                list<Policy_Member__c> policyMemberList=new list<Policy_Member__c>();
                            if (HUMUtilityHelper.isCRMFunctionalityON('4789267')){
                                policyMemberList = [SELECT Id,Redetermination_Date__c,Issue_State__c,Effective_Date__c,Member__r.HRA_HNA_Completed__c  FROM Policy_Member__c where Member__c =: sAccountId and Issue_State__c IN:  IssueStateWithDays.keySet() and Product_Type__c='MCD'and Status__c = 'Active'];
                            }
                            else{
			    	            policyMemberList = [SELECT Id,Redetermination_Date__c,Issue_State__c,Effective_Date__c,Member__r.HRA_HNA_Completed__c  FROM Policy_Member__c where Member__c =: sAccountId and Issue_State__c IN:  IssueStateWithDays.keySet() and Product_Type__c='MCD' ];
			                }
							if(policyMemberList.size()>0){
                                if(HUMUtilityHelper.isCRMFunctionalityON('3421330')){ 
                                    for(Policy_Member__c policyObj: policyMemberList){
                                        String HRAJson = policyObj.Member__r.HRA_HNA_Completed__c;
                                        String HRAFlag='';
                                        if(!String.isBlank(HRAJson))
                                        {
                                            HRAFlag = getHRAStatus(HRAJson);
                                        }
                                        if(HRAFlag=='N' && policyObj.Effective_Date__c!=null){
                                            showAlerts = checkDays(policyObj.Effective_Date__c, HRAIssueStateWithDays.get(policyObj.Issue_State__c));
                                            Set<Integer> days = showAlerts.keySet();
                                            Integer daysBetween=0;
                                            for(Integer i : days)
                                            {
                                                daysBetween = i;
                                                break;
                                            }
                                            if(0<=daysBetween & daysBetween<=HRAIssueStateWithDays.get(policyObj.Issue_State__c)){
                                                HRAEligible = 'true';
                                                showAlertCount = showAlerts.get(daysBetween);
                                                break;
                                            }
                                        }
								   }
                                }
								for(Policy_Member__c policyObj: policyMemberList){
									if(policyObj.Redetermination_Date__c != null){
										Integer daysBetween = Integer.valueOf(Date.Today().daysBetween(policyObj.Redetermination_Date__c));
										if(0<=daysBetween & daysBetween<= IssueStateWithDays.get(policyObj.Issue_State__c)){
											MCDProductEligible = 'true';
											break;
										}
									}   
								}  
							}  
						}
						if(polId == null){ 
							alerts.mapOfRecommendationActionsIdWithType = NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(sAccountId, alerts.interactionId, sAccountPageName, alerts.enterpriseId, sAccountId,alerts.sInquiuredAboutAge, alerts.sFirstTimeCaller, MCDProductEligible,HRAEligible,showAlertCount,false, alerts.bInteractionId); 
							alerts.listRecommendationActionIds = new List<String>(alerts.mapOfRecommendationActionsIdWithType.keySet());                    
						}
						else{                      
							list<Case> sCount = [SELECT Id FROM Case where Policy_Member__c =: polId and  Origin in ('Web Chat','Inbound Call') limit 1 ] ;
							if(sCount.size() >0) {
								alerts.sFirstTimeCaller = 'False';
							}
							alerts.mapOfRecommendationActionsIdWithType = NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(polId, alerts.interactionId, sOtherPageName, alerts.enterpriseId, sAccountId ,alerts.sInquiuredAboutAge, alerts.sFirstTimeCaller, MCDProductEligible,HRAEligible,showAlertCount,false, alerts.bInteractionId);  
							alerts.listRecommendationActionIds = new List<String>(alerts.mapOfRecommendationActionsIdWithType.keySet());                            
						}
                	}
                }
            }
            
            if(string.IsNotBlank(sAccountPageName)){
                alerts.sPageName = sAccountPageName;
            }
            else if(string.IsNotBlank(sOtherPageName)){
                alerts.sPageName = sOtherPageName;
            }
            
            //Checking if the Recommednation Retrun has Guidance and contact handling Alerts
            for(String rActionId: alerts.mapOfRecommendationActionsIdWithType.keySet()){
                if(alerts.mapOfRecommendationActionsIdWithType.get(rActionId) == 'Contact Handling'){
                      alerts.isContactHandlingAlert = true; 
                }
                if(alerts.mapOfRecommendationActionsIdWithType.get(rActionId) == 'Guidance'){
                     alerts.isGuidanceAlert = true; 
                }
                if(alerts.mapOfRecommendationActionsIdWithType.get(rActionId) == 'Command'){
                    alerts.isCommandAlert = true;                     
                    alerts.listcommandRecActionIds.add(rActionId);                     alerts.listRecommendationActionIds.remove(alerts.listRecommendationActionIds.indexOf(rActionId));                                
                }
            }
            alerts.sAlertCount = alerts.listRecommendationActionIds.size();
            
            alerts.isError = false;
            return alerts;
                    
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e,'ViewAlerts_Logic_HUM','init');
            alerts.isError = true;
            alerts.sViewAccountOrMember=e.getMessage()+'  '+e.getCause()+'  '+string.valueOf(e.getLineNumber());
            return alerts;
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
            HUMExceptionHelper.logErrors(ex,'ViewAlerts_Logic_HUM','getHRAStatus');
            return '';
        }
    }  
    public static Map<Integer, Integer> checkDays(String EffectiveDate, Integer alertDays)
    {
        Map<Integer, Integer> showAlert = new Map<Integer, Integer>();
        Date startDate=Date.parse(EffectiveDate);
        String d1=Date.Today().format();
        Date todayDate=Date.parse(d1);
        //Date endDate = startDate.addDays(alertDays);
        Integer diff = startDate.daysBetween(todayDate);
        if(diff <= alertDays)
        {
            showAlert.put(diff, 3);
        }
        else {
            showAlert.put(math.mod(diff, alertDays) , 1);
        }
        return showAlert;
    }

    public static Map<Integer, Integer> checkDays(Date EffectiveDate, Integer alertDays)
    {
        Map<Integer, Integer> showAlert = new Map<Integer, Integer>();
        Integer diff =  Integer.valueOf(EffectiveDate.daysBetween(Date.Today()));
        if(diff <= alertDays)
        {
            showAlert.put(diff, 3);
        }
        else {
            showAlert.put(math.mod(diff, alertDays) , 1);
        }
        return showAlert;
    }

    public static AlertDetails_DTO_HUM updateRecommnedationBasedOnAction(String lstRecommendationActionId, Boolean bCalledFromLightning){
        AlertDetails_DTO_HUM alerts = new AlertDetails_DTO_HUM();
        alerts.isContactHandlingAlert = false; 
        alerts.isGuidanceAlert = false;
        List<String> actionIds = new  List<String>();
		if(String.isNotBlank(lstRecommendationActionId)){
            for(string str  :lstRecommendationActionId.split(',')){
                str = str.replace('[','');
                str = str.replace(']','');
                str = str.replace(' ','');
                str = str.replace('"','');
                actionIds.add(str);

            }
		}
        List<Recommendation_Action__c> recommendationAction = [SELECT Id, Name, Action__c,Alert_Type__c, Recommendation__c,Interaction__c FROM Recommendation_Action__c 
                                                                 WHERE (Action__c = NULL OR Action__c = 'Acknowledged')
                                                                 AND Id IN: actionIds ];

        alerts.listRecommendationActionIds = new list<string>();
        for(Recommendation_Action__c rAction : recommendationAction ){
                alerts.listRecommendationActionIds.add(rAction.Id); 
				if(rAction.Alert_Type__c == 'Contact Handling') {
                   alerts.isContactHandlingAlert = true; 
               }
               else if(rAction.Alert_Type__c == 'Guidance') {
                   alerts.isGuidanceAlert = true; 
               }
               
        }
        
        if(recommendationAction != null && recommendationAction.size() > 0 && string.isNotBlank(recommendationAction[0].Interaction__c)) alerts.bInteractionId = true;
        else alerts.bInteractionId = false;
        
        alerts.sAlertCount = alerts.listRecommendationActionIds.size();
        
        return alerts;
    }
}