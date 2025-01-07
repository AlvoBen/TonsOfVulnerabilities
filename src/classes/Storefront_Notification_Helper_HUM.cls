/******************************************************************************************************************************
Apex Class Name  : Storefront_Notification_Helper_HUM
Version          : 1.0 
Created Date     : 03/04/2020 

Modification Log :
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*    Vinoth L					  				  03/24/2021	       Original Version : User Story - 196045- Identify Active Notifications
* 	 Vinoth L                                     04/01/2021           User Story 2046674- Notification Interaction Generation
*	 Vinoth L									  04/05/2021		   User Story 2071202- T1PRJ0001894 - MF 8 - CRM Storefront Notifications: Date Suppression
*	 Vinoth L									  04/05/2021		   User Story 2068463- T1PRJ0001894 - MF 8 - CRM Storefront Notifications: Determine Last Notification Date
*    Vinoth L                      				  04/22/2021           User Story 2144275 -CRM Storefront Notifications: New Member Eligibility for Action Required Prompt
*    Vinoth L                      				  04/29/2021           User Story 2119738 -CRM Storefront Notifications: Aging-In Eligibility for Action Required Prompt
*    Abhishek Maurya                              06/26/2021           User Story 2308421 -CRM Storefront SDoH: Action Required Prompt
*    Vinoth L                      				  07/11/2021           User Story 1717300 -CRM Storefront Notifications: T1PRJ0001894 - MF 8 - CRM Storefront Home Page: First Onsite Visit 
* 	 Vinoth L                      				  06/15/2022           User Story 3316885: T1PRJ0154546 / SF / MF9 Storefront - SNP: Action Required/Account Hover/Alert Icon
*    Vinoth L                                     09/12/2022           09/23 Observation Fix
* 	 Vinoth L                      				  11/28/2022           User Story 4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront       
*    Vinoth L                      				  01/05/2023           User Story 4107848: T1PRJ0154546 / SF / MF3 Storefront: Update Visitor ID & Type Population Logic
*    Mohamed Thameem                 			  01/27/2023           02/24 Observation Fix
*    Vinoth L                      				  02/28/2023           User Story 4349337: T1PRJ0154546 / SF / MF9 Storefront: Deprecate Old Home Page Components
*    Vinoth L                      			      08/26/2023           User Story 5012060: T1PRJ0154546 / DP / MF9 Storefront - Add preferred name on Account
*********************************************************************************************************************************/
public with sharing class Storefront_Notification_Helper_HUM 
{
    Map<String,Storefront_Notification__C> listOfNotificationsToAccId = new Map<String,Storefront_Notification__C>() ;     
    boolean isFirstTimeNotificationInActive = false;
    List<Storefront_Notification__C> listOfNotifications;
    List<Storefront_Interaction__C> listOfInteractions;
    boolean firstTimeNotification = false;
    Map<String,Storefront_Interaction__c> mapOfReasonToInteraction = new Map<String,Storefront_Interaction__c>();
    List<Id> listOfFollowUpNotificationsId = new List<Id>();              
    Map<String,String> mapOfNotificationTypeToLocation = new Map<String,String>();
    Map<String,Boolean> mapOfSuppressNotification = new Map<String,Boolean>(); 
	Set<String> setOfInactiveNotificationRecords = new Set<String>(); 
    Set<String> setOfLocationRecordAvailable=new Set<String>(); 
    public static Map<String,Storefront_Interaction__c> mapOfAccIdToInteractions=new Map<String,Storefront_Interaction__c>();
    private static Map<Id,Boolean> mapOfAccIdToFirstTimeVisitor = new Map<Id,Boolean>();                
    List<String> lstOfInteractionReasons = new List<String>();    
    public Map<String,Storefront_NotificationsWrapper_HUM> generateNotificationData(Map<String,Map<String,String>> mapOfReqValforNotificationByAccId,
                                                                                    Map<String,String> mapOfVistorTypeByAccId,String currentLocation,String interDate,Map<Id,Account> mapOfIdToAccount)
    {
        Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId = new Map<String,Storefront_NotificationsWrapper_HUM>();                
        if(mapOfVistorTypeByAccId.values() != NULL)
        {
            if(mapOfVistorTypeByAccId.keySet() != NULL){
                for(String accId : mapOfVistorTypeByAccId.keySet()){
                    if(mapOfVistorTypeByAccId.get(accId) != NULL && mapOfVistorTypeByAccId.get(accId) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_OTHER){
                        mapOfVistorTypeByAccId.put(accId,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER);
                    }
                }               
            }
            listOfNotifications = [SELECT Id,CRMRetail_Location__c, Notification_Type__c, Visitor_Type__c, Instructions__c,Inactive__C,Reset_Date__c,Suppression_Days__C,Low_Age_In_Years__c,High_Age_In_Years__c FROM Storefront_Notification__c WHERE Visitor_Type__c IN:mapOfVistorTypeByAccId.values()  AND (CRMRetail_Location__c=NULL OR CRMRetail_Location__c=:currentLocation) ORDER BY CRMRetail_Location__c NULLS LAST ];                                                                                      
            setOfLocationRecordAvailable=new Set<String>();
            map<id,List<Storefront_Interaction__c>> mapOfAccIdAndInteractions = new map <id,List<Storefront_Interaction__c>>();
            lstOfInteractionReasons.addAll(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTION_NOTIFICATION_FIELDS);
            lstOfInteractionReasons.add(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN);
            lstOfInteractionReasons.add(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN);
            listOfInteractions = [Select Id, Account__c,Reason__c,Reason__r.Name,location__r.Name,Category__c,Interaction_Date_Only__c from Storefront_Interaction__c where Account__C in :mapOfReqValforNotificationByAccId.keySet() AND (Reason__r.Name IN : lstOfInteractionReasons) order by createddate DESC];                                               
            map<id,Boolean> mapOfAccIdToFstClndrVst = new map<id,Boolean>();
            Boolean switch_4012388 = HUMUtilityHelper.isCRMFunctionalityON('4012388');
            for (Storefront_Interaction__c sfInt : listOfInteractions)
            {
                if(switch_4012388 && (!mapOfAccIdToFstClndrVst.containsKey(sfInt.Account__C) || (mapOfAccIdToFstClndrVst.containsKey(sfInt.Account__C) && mapOfAccIdToFstClndrVst.get(sfInt.Account__C)))){ 
                        if((sfInt.Reason__r.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN || sfInt.Reason__r.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN) && sfInt.Interaction_Date_Only__c.year() == system.today().year()){                                     
                            mapOfAccIdToFstClndrVst.put(sfInt.Account__C,false);
                        }
                        else{
                            mapOfAccIdToFstClndrVst.put(sfInt.Account__C, true);
                        }
                }
                if(sfInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOTIFICATION && GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTION_NOTIFICATION_FIELDS.contains(sfInt.Reason__r.Name)){
                    if (mapOfAccIdAndInteractions.keySet().contains(sfInt.Account__C)){                   
                        mapOfAccIdAndInteractions.get(sfInt.Account__C).add(sfInt);
                    }
                    else
                    {
                        mapOfAccIdAndInteractions.put(sfInt.Account__C, New List<Storefront_Interaction__c>{sfInt});
                    }  
                }
                else{
                    if(!mapOfAccIdToFirstTimeVisitor.containsKey(sfInt.Account__C)){
                        if(sfInt.Category__c != null){
                            mapOfAccIdToFirstTimeVisitor.put(sfInt.Account__C,TRUE);
                        }else{
                            mapOfAccIdToFirstTimeVisitor.put(sfInt.Account__C,FALSE);                            
                        } 
                    }else if(mapOfAccIdToFirstTimeVisitor.get(sfInt.Account__C)){
                        if(sfInt.Category__c == null)
                        {
                        	mapOfAccIdToFirstTimeVisitor.put(sfInt.Account__C,FALSE);                                
                        }                        
                    }
                }
            }                          
            if(listOfNotifications != NULL)
            {      				                
                for(String accId:mapOfReqValforNotificationByAccId.keySet())
                {   
                    if(switch_4012388 && (mapOfAccIdToFstClndrVst.isEmpty() || !mapOfAccIdToFstClndrVst.containsKey(accId))){
                        mapOfAccIdToFstClndrVst.put(accId, true);
                    }
                    if(mapOfIdToAccount.containsKey(accId)){
                        listOfNotificationsToAccId = new Map<String,Storefront_Notification__C>();
                        listOfFollowUpNotificationsId = new List<Id>();
                        mapOfReasonToInteraction = new Map<String,Storefront_Interaction__c>();
                        isFirstTimeNotificationInActive = false;                                               
                        firstTimeNotification = false;   
                        mapOfSuppressNotification = new Map<String,Boolean>();                    
                        if(mapOfAccIdAndInteractions.containsKey(accId) && mapOfAccIdAndInteractions.get(accId) != NULL){
                            prepareInteractionData(mapOfAccIdAndInteractions.get(accId));   
                        }                                            
                        Storefront_NotificationsWrapper_HUM tempWrap = new Storefront_NotificationsWrapper_HUM();  
                        Integer months = 0;        
                        Account accRef = new Account();
                        accRef.Id = accId;                  
                        accRef.Name = mapOfIdToAccount.get(accId).FirstName+' '+mapOfIdToAccount.get(accId).LastName;
                        accRef.Enterprise_ID__c=mapOfIdToAccount.get(accId).Enterprise_ID__c;
                        if(HUMUtilityHelper.isCRMFunctionalityON('5012060') && mapOfIdToAccount.get(accId).CRMRetail_PreferredName__c != NULL)
                        {
                          	accRef.CRMRetail_PreferredName__c = mapOfIdToAccount.get(accId).CRMRetail_PreferredName__c;      
                        }
                        tempWrap.accountRec = accRef;
                        tempWrap.interactionCateory = mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY);                        
                        tempWrap.visitorType = mapOfVistorTypeByAccId.get(accId);
                        tempWrap.currentLocation = currentLocation;
                        tempWrap.visitorId = mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITORID);
                        if(switch_4012388 && mapOfAccIdToFstClndrVst != NULL && mapOfAccIdToFstClndrVst.containsKey(accId) && mapOfAccIdToFstClndrVst.get(accId)){
                            tempWrap.fstTimeClndrVst = mapOfAccIdToFstClndrVst.get(accId);                                                         
                        }
                        Boolean switch_3316885 = HUMUtilityHelper.isCRMFunctionalityON('3316885');                          
                        if(switch_3316885 && mapOfReqValforNotificationByAccId.get(accId).containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INDICATOR_KEY) && mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INDICATOR_KEY) != NULL){                                
                            tempWrap.visitorIndicator = mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INDICATOR_KEY);								                            
                        }
                        if(CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.containsKey(mapOfIdToAccount.get(accId).Enterprise_ID__c)){
                        tempWrap.sdohAcronym=' ('+CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.get(mapOfIdToAccount.get(accId).Enterprise_ID__c)+')';    
                        }
                        if(mapOfReqValforNotificationByAccId.get(accId).containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN))
                        {
                            tempWrap.checkInType = mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN);
                        }
                        else
                        {
                            tempWrap.checkInType = mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN);
                        }                                               
                        for(Storefront_Notification__C notifRef : listOfNotifications)
                        {   
                            if(mapOfVistorTypeByAccId.get(accId) == notifRef.Visitor_Type__c)
                            {                                   
                                if(notifRef.Inactive__c && notifRef.CRMRetail_Location__c == currentLocation){                                    
                                    setOfInactiveNotificationRecords.add(notifRef.Notification_Type__c);                                 
                                }
                                else if(setOfInactiveNotificationRecords != NULL && setOfInactiveNotificationRecords.contains(notifRef.Notification_Type__c) && notifRef.CRMRetail_Location__c == NULL){                                    
                                    continue;
                                }
                                if(!notifRef.Inactive__c){                                    
                                    if(notifRef.CRMRetail_Location__c !=NULL){
                                        setOfLocationRecordAvailable.add(notifRef.Notification_Type__c);				
                                    	checkIndividualNotificationEligiblity(notifRef,accId,mapOfReqValforNotificationByAccId,tempWrap,mapOfIdToAccount);
                                    }else if(!setOfLocationRecordAvailable.contains(notifRef.Notification_Type__c)){
                                        	checkIndividualNotificationEligiblity(notifRef,accId,mapOfReqValforNotificationByAccId,tempWrap,mapOfIdToAccount); 
                                    }
                                }
                                else if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTTIME_VISITOR && notifRef.Inactive__c && tempWrap.checkInType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN){                                    
                                    isFirstTimeNotificationInActive = True;
                                    firstTimeNotification = True;                                    
                                    prepareData(accId,notifRef);	                                                                          
                                }                              
                            }                                                                            
                        }                         
                        if(listOfNotificationsToAccId != NULL && listOfNotificationsToAccId.size() > 0){                        
                            tempWrap.listOfNotificationRec = listOfNotificationsToAccId.values();
                        }                    
                        if((tempWrap.listOfNotificationRec == NULL || tempWrap.listOfNotificationRec.size() == 0 )&& tempWrap.previousInteractionDate != NULL)
                        {                           
                            continue;	   
                        } 
                        else if(firstTimeNotification && isFirstTimeNotificationInActive && tempWrap.listOfNotificationRec.size() == 1)
                        {                                 
                            continue;
                        }                        
                        else
                        { 
                            if(listOfFollowUpNotificationsId.size() > 0){
                                tempWrap.listOfFollowUpNotificationsId = listOfFollowUpNotificationsId;                                    
                            }     
                            if(tempWrap.listOfNotificationRec.size() > 0){                                
                                mapOfNotificationsWrapToAccId.put(accId,tempWrap);    
                            }                                                 
                        }                           
                    } 
                }
            } 
        } 		
        return mapOfNotificationsWrapToAccId;
    }
    Private void checkIndividualNotificationEligiblity(Storefront_Notification__C notifRef,Id accId,Map<String,Map<String,String>> mapOfReqValforNotificationByAccId,
                                                       Storefront_NotificationsWrapper_HUM tempWrap,Map<Id,Account> mapOfIdToAccount){                                                                          
        Date lastInteractionDate;
        Boolean isNotificationEligible = false;         
        if(!mapOfAccIdToInteractions.containsKey(accId)){
            mapOfAccIdToInteractions.put(accId,new Storefront_Interaction__c());                                                     
            }                                                   
        if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_WAIVER_REQUIRED  && tempWrap.interactionCateory != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL)
        {      
            Date waiverDate = mapOfIdToAccount.get(accId).Waiver_Date__C;          
            if(waiverDate != NULL){                
                lastInteractionDate = waiverDate;                    
            }              
            isNotificationEligible = true;                            
        }                            
        else if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTTIME_VISITOR && tempWrap.checkInType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN) 
        {    
            isNotificationEligible = false;            
            if(!mapOfReqValforNotificationByAccId.get(accId).containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE)){                                                      
                if(firstTimeNotification == false){                    
                    firstTimeNotification = true;                     
                }
                prepareData(accId,notifRef);
            }
            else{ 
                tempWrap.previousInteractionDate=mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE).split('@').get(1);                
                tempWrap.previousInteractionLocation=mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE).split('@').get(0);                
            }                                                                
        }
        else if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTTIME_VISITOR && mapOfAccIdToFirstTimeVisitor.containsKey(accId) 
                && mapOfAccIdToFirstTimeVisitor.get(accId) != NULL && mapOfAccIdToFirstTimeVisitor.get(accId) && (String.isBlank(tempWrap.interactionCateory) || tempWrap.interactionCateory == 'NULL')) 
        {             
            tempWrap.isPreviousAllVirtualInteration = True;  
            prepareData(accId,notifRef);
        }   
        else if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN) 
        {                                                                 
            if(validateVeteranType(mapOfIdToAccount,accId)){
            	isNotificationEligible = True; 
                mapOfAccIdToInteractions.get(accId).Veteran__c=true;
            }
            if(mapOfReasonToInteraction.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN)){                
                lastInteractionDate = mapOfReasonToInteraction.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN).Interaction_Date_Only__c;                  
            }            
        }
        else if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEW_MEMBER)                
        {                             
            if(validateNewMemberType(mapOfIdToAccount,accId)){
            	isNotificationEligible = True; 
                mapOfAccIdToInteractions.get(accId).New_Member__c=true;
            }
            if(mapOfReasonToInteraction.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEW_MEMBER)){
                lastInteractionDate = mapOfReasonToInteraction.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEW_MEMBER).Interaction_Date_Only__c;                  
            }                        
        } 
        else if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN){       		
            if(validateAgingInType(mapOfIdToAccount,accId,notifRef)){
            	isNotificationEligible = True; 
                mapOfAccIdToInteractions.get(accId).Aging_In__c=true;
            }
            if(mapOfReasonToInteraction.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN)){
                lastInteractionDate = mapOfReasonToInteraction.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN).Interaction_Date_Only__c;                  
            }                        	     
        }            
        else if(notifRef.Notification_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH){       		
            if(validateSDoHType(mapOfIdToAccount,accId)){
            	isNotificationEligible = True; 
                mapOfAccIdToInteractions.get(accId).SDoh__c=true;
            }
            if(mapOfReasonToInteraction.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH)){
                lastInteractionDate = mapOfReasonToInteraction.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH).Interaction_Date_Only__c;                  
            }                        	     
        }                                                               
        if(isNotificationEligible){                                                   
        	isNotificationEligible = dateSuppression(lastInteractionDate,notifRef);                                                   
        }        
        if(isNotificationEligible){                 
            prepareData(accId,notifRef);
            if(mapOfReqValforNotificationByAccId.get(accId).
               get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE)!=NULL){                   
                   tempWrap.previousInteractionDate=mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE).split('@').get(1);                  
                   tempWrap.previousInteractionLocation=mapOfReqValforNotificationByAccId.get(accId).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE).split('@').get(0);                   
            }            
        }                                                                
        lastInteractionDate=NULL;  
        isNotificationEligible = false;                                                   
    }
    @testVisible private void prepareInteractionData(List<Storefront_Interaction__C> listOfInteractions){
        mapOfReasonToInteraction = new Map<String,Storefront_Interaction__C>();        
        for(Storefront_Interaction__c sfInt : listOfInteractions){
            if(!mapOfReasonToInteraction.containsKey(sfInt.Reason__r.Name))
            {
                mapOfReasonToInteraction.put(sfInt.Reason__r.Name,sfInt);    
            }
            if(mapOfReasonToInteraction.size() == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTION_NOTIFICATION_FIELDS.size())
            {
                break;
            }                       
        }                
    }
    @testVisible private Boolean dateSuppression(Date lastInteractionDate,Storefront_Notification__c notifRef){                
        Date previousInteractionDate;                
        Boolean result = False;
        if(lastInteractionDate != NULL){            
            previousInteractionDate = date.newinstance(lastInteractionDate.year(), lastInteractionDate.month(), lastInteractionDate.day());
            Integer calendarDays = previousInteractionDate.daysBetween(Date.today());                          
            if(notifRef.Reset_Date__c != NULL && lastInteractionDate < notifRef.Reset_Date__c ){                 
                result = True;
            }                                    
            else {                
                if(notifRef.Suppression_Days__c == 0){ 	
                    mapOfSuppressNotification.put(notifRef.Notification_Type__c,True);				
                    result = False;  
                }
                else if(notifRef.Suppression_Days__c > 0){
                    if((notifRef.Reset_Date__c != NULL && lastInteractionDate > notifRef.Reset_Date__c) || notifRef.Reset_Date__c == NULL){
                        if(calendarDays >= notifRef.Suppression_Days__c){                             
                            listOfFollowUpNotificationsId.add(notifRef.Id);
                            result = True;   
                        }                        
                        else if(calendarDays < notifRef.Suppression_Days__c){   
                            mapOfSuppressNotification.put(notifRef.Notification_Type__c,True);                         
                            result = False;	        
                        }                        
                    }
                }
            }                                                                                               
        }
        else{                 
            result = True;
        }        
        return result;
    }       
    Private void prepareData(String accId,Storefront_Notification__C notifiRec){        
        if(!listOfNotificationsToAccId.containsKey(notifiRec.Notification_Type__c) && !mapOfSuppressNotification.containsKey(notifiRec.Notification_Type__c)){
            	listOfNotificationsToAccId.put(notifiRec.Notification_Type__c,notifiRec);    
        }        
    }
    Public List<Storefront_Notification__C> retrieveListOfNotification(Id currentLocation){
    	return [SELECT Id,CRMRetail_Location__c, Notification_Type__c, Visitor_Type__c,Inactive__C,Low_Age_In_Years__c,High_Age_In_Years__c FROM Storefront_Notification__c WHERE Inactive__C = False AND (CRMRetail_Location__c=NULL OR CRMRetail_Location__c=:currentLocation)  ORDER BY CRMRetail_Location__c NULLS LAST ];                                                              	             
    }
    Public void checkNotificationBox(Storefront_Interaction__C sfInt,List<Storefront_Notification__C> lstOfNotification,Map<Id,Account> mapOfIdToAccount){                
        for(Storefront_Notification__C sfNot : lstOfNotification){             
            List<Account> isNewMemberAccounts = new List<Account>();
        	isNewMemberAccounts.add(mapOfIdToAccount.get(sfInt.Account__c));
            if(sfNot.Notification_Type__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN) && validateVeteranType(mapOfIdToAccount,sfInt.Account__c) 
               && sfNot.Visitor_Type__c == sfInt.CRM_Retail_Interaction_Visitor_Type__C)
            {
                sfInt.Veteran__c = True;	    
            }             
            else if(sfNot.Notification_Type__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEW_MEMBER) && validateNewMemberType(mapOfIdToAccount,sfInt.Account__c) 
                    && sfNot.Visitor_Type__c == sfInt.CRM_Retail_Interaction_Visitor_Type__C)                   
            {
                sfInt.New_Member__c= True;	    
            } 
            else if(sfNot.Notification_Type__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN) && validateAgingInType(mapOfIdToAccount,sfInt.Account__c,sfNot) 
                    && sfNot.Visitor_Type__c == sfInt.CRM_Retail_Interaction_Visitor_Type__C)                   
            {
                sfInt.Aging_In__c= True;	    
            }     
            else if(sfNot.Notification_Type__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH) && validateSDoHType(mapOfIdToAccount,sfInt.Account__c) 
                    && sfNot.Visitor_Type__c == sfInt.CRM_Retail_Interaction_Visitor_Type__C)                   
            {
                sfInt.SDoH__c = True;	    
            }
        }        
    }     
    private static Boolean validateVeteranType(Map<Id,Account> mapOfIdToAccount,Id accId){
        if((mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c != NULL && !mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN))                         
           ||mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c == NULL){
               if(mapOfIdToAccount.get(accId).Veteran_Status__c != NULL && (mapOfIdToAccount.get(accId).Veteran_Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CONFIRMED_VETERAN
                  || mapOfIdToAccount.get(accId).Veteran_Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_IDENTIFIED_VETERAN)){
               		return True;
               }
        } 
        return False;
    }
    private static Boolean validateNewMemberType(Map<Id,Account> mapOfIdToAccount,Id accId){
        List<Account> isNewMemberAccounts = new List<Account>();
        isNewMemberAccounts.add(mapOfIdToAccount.get(accId));        
        if(isNewMemberCheck(isNewMemberAccounts) && ((mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c != NULL && !mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEWMEMBER))                         
           || mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c == NULL)    ){
                   return True;
        }
        return False;   
    }
    private static Boolean validateAgingInType(Map<Id,Account> mapOfIdToAccount,Id accId,Storefront_Notification__c notifRef){
        Double lowAgeInYears = notifRef.Low_Age_in_Years__c;
        Double highAgeInYears = notifRef.High_Age_In_Years__c; 
        Date visitorDOB;    
        Date dtToday;
        Decimal visitorAge;
        String accBirthDate = mapOfIdToAccount.get(accId).Birthdate__C;
        List<String> splitedDate = new List<String>();
        if(String.isNotBlank(accBirthDate) && lowAgeInYears != NULL && lowAgeInYears > 0){
        	splitedDate = accBirthDate.split('/');                     
        	visitorDOB = Date.newInstance(Integer.valueOf(splitedDate[2]),Integer.valueOf(splitedDate[0]),Integer.valueOf(splitedDate[1])); 
            dtToday = Date.today();
        	visitorAge = visitorDOB.monthsBetween(dtToday) / GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGE_CALCULATION;            
            visitorAge = visitorAge.round(System.roundingMode.HALF_UP);            
            visitorAge = visitorAge.setScale(1);                                    
            if((visitorDOB < dtToday) && ((mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c != NULL && !mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN))                         
               || mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c == NULL) && visitorAge >= lowAgeInYears && visitorAge <= highAgeInYears){
                       return True;
            }
        }                                       
        return False;
    }
    private static Boolean validateSDoHType(Map<Id,Account> mapOfIdToAccount,Id accId){
        if(((mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c != NULL && !mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH))                         
           || mapOfIdToAccount.get(accId).CRMRetailNotificationOptOut__c == NULL) && (CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.containsKey(mapOfIdToAccount.get(accId).Enterprise_ID__c)|| CRMRetail_InteractionsEvents_LC_HUM.setOfAccIdWithSDohCheckboxChecked.contains(accId))){
               return True;
        }
        return False;
    }
    /*
    * Method Name   :    isNewMemberCheck
    * Description   :    Generate New Member Result.
    * Parameters    :    list of Accounts.
    */
    @testvisible private static Boolean isNewMemberCheck(List<Account> listOfAccounts){ 		
        Date currentDate = Date.Today();                
        Date effDate;
        Date endDate;                               
		Integer policyCount;  
        Date comparisonDate;
        Boolean isNewMember;
        List<StorefrontNotification_Sorting_H_HUM> listOfPol;
        if(listOfAccounts != NULL){
        	for(Account accRef : listOfAccounts){
            isNewMember = False;            
        	if(accRef.Policy_Members__r.size() > 0 ){ 
				listOfPol = new List<StorefrontNotification_Sorting_H_HUM>();                
                for(Policy_Member__c polMemRef : accRef.Policy_Members__r){
                    if(polMemRef.Product__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_POLICY_PRODUCT_TYPE && GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LIST_OF_PRODUCT_TYPES.contains(polMemRef.Policy__r.Product_Type__c)){                        
                        listOfPol.add(new StorefrontNotification_Sorting_H_HUM(polMemRef));
                    }                                                            
                }
        	}            
            if(listOfPol != NULL && listOfPol.size() > 0){
                listOfPol.sort();   
                policyCount = 0;                
                for(StorefrontNotification_Sorting_H_HUM oneRef : listOfPol){                     
                    if(oneRef.policyMem.Effective_Date__c!=NULL){
                    effDate = Date.parse(oneRef.policyMem.Effective_Date__c); 
                    }
                    String tempEndDate = oneRef.policyMem.End_Date__C;
                    if(String.isNotBlank(tempEndDate)){
                    List<String> splitedDate = tempEndDate.split('/');                    
                    endDate = Date.newInstance(Integer.valueOf(splitedDate[2]),Integer.valueOf(splitedDate[0]),Integer.valueOf(splitedDate[1]));
                    }                       
                    policyCount++;
                    comparisonDate = DATE.newInstance(currentDate.year()-1,currentDate.month(),currentDate.day()); 				
                    if(effDate < comparisonDate){
                        comparisonDate = DATE.newInstance(comparisonDate.year()-1,comparisonDate.month(),comparisonDate.day());                           
                        if(effDate >= comparisonDate){
                            isNewMember = false;                               
                            break;                                 
                        }   
                        continue;
                    }                            
                    else if(effDate >= comparisonDate && (endDate > system.today()||endDate==Null))
                    {                            
                        isNewMember = True;                        
                        if(policyCount == listOfPol.size()){
                            break; 
                        }else{
                            continue;
                        }                                
                    }
                    else{
                        continue;
                    }                                                                                                         
                }                                    
            }             	
    	 }    
      }
      return isNewMember;
   }
}