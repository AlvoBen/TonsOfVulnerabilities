/******************************************************************************************************************************
Apex Class Name  : CRMRetail_InteractionsEvents_LC_HUM
Version          : 1.0 
Created Date     : 07/18/2022
Function         : Controller for Interaction Events Page components                    
Test Class       : CRMRetail_InteractionsEvents_LC_T_HUM

Modification Log :
* Developer              Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Vivek Sharma                                 07/18/2022           REQ-3484064 Original Version
* Sahil Verma                                  08/05/2022           US-3551183: T1PRJ0154546 / SF / MF9 Storefront: Modernization - Interactions/Events - Ability to Search Visitor Check-Ins
* Vinoth L									   08/01/2022			REQ-3510484 T1PRJ0154546 / SF / MF9 Storefront: Recommend Events (Story 1)
* Vivek Sharma                                 08/22/2022           User Story 3581472: T1PRJ0154546 / SF / MF9 Storefront: Ability to Create Follow Up Task From Visitor Check-Ins
* Vinoth L									   09/11/2022			User Story 3791721: 09/23 Observations Fix	
* Navajit Sarkar                               09/27/2022           User Story 3782843: MF9 Storefront: Modernization - Interactions/Events - Ability to View Calendar Events
* Navajit Sarkar                               10/28/2022           User Story 3866537: T1PRJ0154546 / SF / MF5 Storefront: Reporting of recommended events
* Mohamed Thameem                              01/05/2023           User Story 2792916: T1PRJ0154546 / SF / MF9 Storefront Home Page: Visitor Interactions Search  
* Vinoth L                                     11/28/2022           REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront
* Vinoth L                                     03/13/2023           Health Educator - Populate SDoH Records fix
* Mohamed Thameem                              07/28/2023           User Story 3957580: T1PRJ0154546 / SF / MF9 Storefront: Modernization - Interactions/Events - Ability to Search Created By 
* Vinoth L                      			   08/26/2023           User Story 5012060: T1PRJ0154546 / DP / MF9 Storefront - Add preferred name on Account
*********************************************************************************************************************************/
public with sharing class CRMRetail_InteractionsEvents_LC_HUM {
    public static String sLocation = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
    public static String sSelectedDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
    public static Map<String,Map<String,String>> mapOfReqValforNotificationByAccId = new Map<String, Map<String, String>>();
    public static Map<String,String> mapOfVistorTypeByAccId=new Map<String,String>();
    public static Map<Id,Account> mapOfIdToAccount = new Map<Id,Account>(); 
    private static List<String> listOfAccEligibleForBellIcon=new List<String>(); 
    public static Set<String> setOfAccIdWithSDohCheckboxChecked=new Set<String>();
    public static Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId=new Map<String,Storefront_NotificationsWrapper_HUM>();  
    public static String currLocationName='';
    public static String currLocationId='';
    public static String currInteractionDate='';
    public static String currLocationType = ''; 
     public static Map<String,Boolean> switchMap = CRMRetail_HomePage_LC_HUM.fetchSwitchResults();
    public static Boolean recommendationSwitch = switchMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_SWITCH);
	public static Boolean createdByColumnSwitch = switchMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CREATEDBY_COLUMN_SWITCH);
	/*
	* Method Name   :    retrieveInteractions
	* Description   :    gets all the Interactions created for the selected Location and Date. 
	* Return Type   :    Map<string,object>
	* Parameters    :    pagination, sortedfield, sortedDirection, sortedtable. 
	*/
    @AuraEnabled
    public static Map<String,Object> retrieveInteractions(Integer numberOfRowsToSkip, String sortedBy, String sortedDirection,String sortedTable, String searchString, String searchType) 
	{ 
        Map<String,CRMRetail_Interaction_Search_Config__mdt> fieldMap = new Map<String,CRMRetail_Interaction_Search_Config__mdt>();
        Map<String,Object> returnMap = new Map<String, Object>();
                String dataFieldsWithSearchFields = 'Id, Name, Missing_Event__c,Storefront_Is_EventRecmded__c,inactiveMember__c, Account__r.CRMRetailNotificationOptOut__c,Storefront_Vstr_Indicator__c,Account__r.Birthdate__c,Account__r.Enterprise_Id__c,Account__r.FirstName,Account__r.LastName,Aging_In__c,SDoH__c,Veteran__C,New_Member__c,First_Time_Visitor__c,Reason__c,Reason__r.Name,Reason__r.isGo365Eligible__c,Account__c,Account__r.Name,Account__r.recordtype.Name,'+
						'Account__r.personContactId,Account__r.Waiver_Date__C,Account__r.Permission_to_Contact__C,Location__r.Name,Location__r.Location_Type__c,'+
						'LASTMODIFIEDDATE,LASTMODIFIEDBY.NAME,CreatedDate,CreatedBy.Name, Interaction_Date__c, Visitor_Id__c,'+ 
						'CRM_Retail_Interaction_Visitor_Type__c, Category__c, isAllDayEvent__c, Storefront_Event_Starttime__c, '+
						'Storefront_Event_Endtime__c' + (HUMUtilityHelper.isCRMFunctionalityON('5132829') ? ',Account__r.CRMRetail_PreferredName__c' : '');

        List<String> allSplittedFields = dataFieldsWithSearchFields.toLowerCase().replaceAll( '\\s+', '').split(',');
        
        for(CRMRetail_Interaction_Search_Config__mdt config : fetchSearchConfigMetaData())
        {
            fieldMap.put(config.MasterLabel,config);
            if(!allSplittedFields.contains(config.Field_Api__c?.tolowercase()))
            {
                dataFieldsWithSearchFields = dataFieldsWithSearchFields+','+config.Field_Api__c;
            }
        }
        
        try
		{
            sSelectedDate = formatDate(sSelectedDate);
			Date presentDate = Date.valueof(sSelectedDate);
			Time sTime = Time.newInstance(0, 0, 0, 0);
			DateTime dt = DateTime.newInstance(presentDate, sTime);
			Date NextDate = presentDate.addDays(1);
			DateTime dtNext = DateTime.newInstance(NextDate, sTime);        
			List<Storefront_Interaction__c> interactionList =new List<Storefront_Interaction__c>(); 
			List<Storefront_Interaction__c> return_Interactions =new List<Storefront_Interaction__c>();         
			Date todayDate = Date.today();
			List<Storefront_Interaction__c> listOfInteractions =new List<Storefront_Interaction__c>(); 
            Set<Id> setOfAccountIds = new Set<Id>();  
            List<AggregateResult> listOfRcmdInteractions =new List<AggregateResult>();     
			String intQuery=''; 
			List<Storefront_Interaction__C> lstCheckInInteractions = new List<Storefront_Interaction__C>();
			List<Storefront_Interaction__C> lstOnsiteInteractions = new List<Storefront_Interaction__C>();
			List<Storefront_Interaction__C> lstVirtualInteractions = new List<Storefront_Interaction__C>();
			List<String> permissionSetList = GenericHelper_CRMRetail_H_HUM.getUserPermissionSet();
			if(!permissionSetList.isEmpty()){
				if(permissionSetList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_BUSINESSADMIN) || permissionSetList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN))
				{
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_REQUIRED_PERMISSION_SET,true);   
				}
				else{
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_REQUIRED_PERMISSION_SET,false);
				}
			}
			sLocation = sLocation.split('-',2)[1];
			if(String.isNotBlank(sLocation) && sLocation != 'None'){                     
							 
					Storefront_Location__c currentLocation = [SELECT Id,Name,Location_Type__c FROM Storefront_Location__c WHERE Name =: sLocation LIMIT 1];                            
					intQuery = 'select '+dataFieldsWithSearchFields+' from Storefront_Interaction__c where Location__r.Name =: sLocation'+ 
						' AND Interaction_Date__c >=:dt AND Interaction_Date__c <: dtNext AND Category__c!=\'' +GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOTIFICATION +'\'';                 
					if(String.isNotBlank(sortedTable)){
						List<String> lstOfFilters= new List<String>{'\''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN+'\'','\''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN+'\''};
						if(sortedTable == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CHECK_IN.toLowercase()){
							intQuery+=' AND Reason__r.Name IN '+lstOfFilters;
						}	
						else if(sortedTable == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE.toLowercase()){
							intQuery+= ' AND (Category__c = \'\' OR Category__c='+'\''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE+'\')';
							intQuery+= ' AND Reason__r.Name NOT IN '+lstOfFilters;
						}						
						else if(sortedTable == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL.toLowercase()){
							List<String> lstOfCategoryFilters = new List<String>{'\''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL+'\'','\''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL+'\'','\''+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_RECORDED+'\''}; 
							intQuery+= ' AND Category__c IN '+lstOfCategoryFilters;
							intQuery+= ' AND Reason__r.Name NOT IN '+lstOfFilters;
						}						
					}
					if(String.isNotBlank(sortedBy)) {
                        sortedBy = String.escapeSingleQuotes(sortedBy);
						if(sortedBy != 'Account__r.Name') 
						{
							intQuery+=' ORDER BY '+ sortedBy;
							if(String.isNotBlank(sortedDirection)) {
                                sortedDirection = String.escapeSingleQuotes(sortedDirection);
								if(sortedDirection!='asc') {
									intQuery += ' DESC';
								} 
								else {
									intQuery += ' ASC';
								}
							}                        
						}
					} 
					else {
						if(createdByColumnSwitch != null && createdByColumnSwitch == true)                               
                            intQuery+=' ORDER BY CreatedDate DESC';  
                        else 
                            intQuery+=' ORDER BY LASTMODIFIEDDATE DESC';
					}                         
					interactionList = Database.query(intQuery);
                    if(HUMUtilityHelper.isCRMFunctionalityON('3551183') && String.isNotEmpty(searchString)){
                        searchString = String.escapeSingleQuotes(searchString);
                        List<Storefront_Interaction__c> lstInteraction = new List<Storefront_Interaction__c>();
                        for(Storefront_Interaction__c intList : interactionList){
                            searchType = String.escapeSingleQuotes(searchType);
                            String searchField = fieldMap.get(searchType)?.field_Api__C;

                            String searchValue;                                      
                            List<String> fieldList = searchField.split('\\.');
                            
                            Switch on fieldList.size()
                            {
                                when 1 { searchValue = String.valueOf(intList.get(searchField))?.toLowerCase()?.trim(); } 
                                when 2 { searchValue = String.valueOf(intList.getSobject(fieldList[0]).get(fieldList[1]))?.toLowerCase()?.trim(); }
                                when 3 { searchValue = String.valueOf(intList.getSobject(fieldList[0]).getSobject(fieldList[1]).get(fieldList[2]))?.toLowerCase()?.trim(); }
                            }
                                                        
                            if(searchValue.contains(searchString.toLowerCase().trim())){
                                lstInteraction.add(intList);
                            }
                        }
                        interactionList = lstInteraction;
                    } 
					if(sortedBy == 'Account__r.Name' && interactionList.size() > 0)
					{
						List<Storefront_Interaction_Sorting_H_HUM> intSortWrapList =new List<Storefront_Interaction_Sorting_H_HUM>();
						if(String.isNotBlank(sortedDirection)) {
							Storefront_Interaction_Sorting_H_HUM.sortingDirection=sortedDirection; 
						}
						for(Storefront_Interaction__c qdInts : interactionList)
						{
							intSortWrapList.add(new Storefront_Interaction_Sorting_H_HUM(qdInts));
						}
						if(intSortWrapList != NULL && intSortWrapList.size()> 0)
						{
							intSortWrapList.sort();
							for(Integer i = 0; i < intSortWrapList.size(); i++)
							{
								interactionList[i] = intSortWrapList[i].interRefComp;
							}
						}
					}
					if(numberOfRowsToSkip>0){
						for(Integer i = numberOfRowsToSkip; i < numberOfRowsToSkip + 10; i++) {
							if(i < interactionList.size()) {
								return_Interactions.add(interactionList[i]);                            
							}
						}
						if(sortedTable == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CHECK_IN.toLowercase()) {
							lstCheckInInteractions.addAll(return_Interactions);
							returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKIN_COUNT,interactionList.size());
						}
						else if(sortedTable == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE.toLowercase()) {
							lstOnsiteInteractions.addAll(return_Interactions);
							returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ONSITE_COUNT,interactionList.size());
						}
						if(sortedTable == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL.toLowercase()) {
							lstVirtualInteractions.addAll(return_Interactions);
							returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_VIRTUAL_COUNT,interactionList.size());
						}
					}
					else{
						Integer i=0,j=0,k=0;
						for(Storefront_Interaction__C objInt : interactionList){
							if(objInt.Reason__r.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN || objInt.Reason__r.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN){
								i++;
								if(i<=10) lstCheckInInteractions.add(objInt);
                                setOfAccountIds.add(objInt.Account__c);
							}
							else if(String.isBlank(objInt.Category__c) || objInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE){
								j++;
								if(j<=10) lstOnsiteInteractions.add(objInt);
							}
							else if(objInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL || objInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL || objInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_RECORDED){
								k++;
								if(k<=10) lstVirtualInteractions.add(objInt);
							}
						}
						returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKIN_COUNT,i);
						returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ONSITE_COUNT,j);
						returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_VIRTUAL_COUNT,k);
					}
					if(currentLocation != NULL && lstCheckInInteractions.size()>0 && todayDate == presentDate && currentLocation.Location_Type__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL){
						for(Storefront_Interaction__C interRef : lstCheckInInteractions){                        
							Map<String,String> mapOfReqValues = new Map<String,String>();
								if(!mapOfReqValforNotificationByAccId.containsKey(interRef.account__c)){      
									Map<String,Object> mpOfIndicatorToMetricValue = CRMRetail_HomePage_LC_HUM.checkVisitorIndicatorEligibility(interRef.Storefront_Vstr_Indicator__c);                                    
									if(mpOfIndicatorToMetricValue != NULL && mpOfIndicatorToMetricValue.containsKey(System.Label.CRMRetail_SNP_Text)){                        
										mapOfReqValues.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INDICATOR_KEY,mpOfIndicatorToMetricValue.get(System.Label.CRMRetail_SNP_Text).toString());
									} 
									mapOfReqValues.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY,interRef.Category__c);
									mapOfReqValues.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITORID,interRef.Visitor_Id__c);
									mapOfReqValues.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN,interRef.Reason__r.Name);
									mapOfReqValues.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE,currentLocation.Name+'@'+todayDate.format());
									mapOfReqValforNotificationByAccId.put(interRef.account__c,mapOfReqValues);
								}
								if(!mapOfVistorTypeByAccId.containsKey(interRef.account__c)){
									mapOfVistorTypeByAccId.put(interRef.account__c,interRef.CRM_Retail_Interaction_Visitor_Type__c);
								}                            
								if(interRef.SDoH__c){
									setOfAccIdWithSDohCheckboxChecked.add(interRef.account__c);
								}                   
						}
						if(mapOfReqValforNotificationByAccId.size()>0){
							mapOfIdToAccount= new Map<Id,Account>([Select Id,FirstName,CRMRetail_PreferredName__c,LastName,Enterprise_Id__c,Birthdate__C,Name,Waiver_Date__c,CRMRetailNotificationOptOut__c,Veteran_Status__c,(SELECT Id,Name,Effective_Date__c,status__c,End_Date__c,Member__C,Product__c,Policy__r.Product_Type__c 
																																												   FROM Policy_Members__r WHERE Member__r.ETL_Record_Deleted__c = false ),GCM_Visitor_Barcode__c,RecordType.Name from Account where Id in :mapOfReqValforNotificationByAccId.keySet()]);
							if(mapOfReqValforNotificationByAccId.size()>0 && mapOfVistorTypeByAccId.size()>0){
								mapOfNotificationsWrapToAccId=new Storefront_Notification_Helper_HUM().generateNotificationData(mapOfReqValforNotificationByAccId, mapOfVistorTypeByAccId, currentLocation.Id,String.valueOf(todayDate),mapOfIdToAccount); 
							}                
						} 
					}                                            
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SELECTED_LOCATION,sLocation);
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SELECTED_DATE,sSelectedDate);
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKIN_INT,JSON.serialize(lstCheckInInteractions));
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ONSITE_INT,JSON.serialize(lstOnsiteInteractions));
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_VIRTUAL_INT,JSON.serialize(lstVirtualInteractions));                
					if(mapOfNotificationsWrapToAccId.size()>0){
						returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIF_RECORD,JSON.serialize(mapOfNotificationsWrapToAccId));
						listOfAccEligibleForBellIcon.addAll(mapOfNotificationsWrapToAccId.keyset());
						returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ELIGIBLE_FOR_BELL_ICON,listOfAccEligibleForBellIcon);
					} 
                    if(recommendationSwitch != NULL && recommendationSwitch == true){                     	
                        if(presentDate != NULL && presentDate == Date.today()){
                            listOfRcmdInteractions = [SELECT Count(Id),Account__c from Storefront_Interaction__c where Account__c =: setOfAccountIds AND Location__c =:currentLocation.Id AND Interaction_Date__c=last_n_days:30 AND Reason__r.Name !=: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN AND Reason__r.Name !=: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN AND Category__c NOT IN: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SET_INTERACTION_CATEGORY_TYPES and reason__r.Interaction_Type__r.Storefront_Excld_Recmd__c = False group by Account__c order by count(id) desc];                   	
                            List<String> listOfAccEligibleForRecommendation = new List<String>();
                            if(listOfRcmdInteractions != NULL && listOfRcmdInteractions.size() > 0){
                                for(AggregateResult arr: listOfRcmdInteractions){
                                    Integer count = Integer.valueOf(arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EXPR0));
                                    if(count >= GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_ELIGIBILITY_COUNT){                                                                        
                                        listOfAccEligibleForRecommendation.add(String.valueOf(arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACCOUNT_KEY)));                                     
                                    }
                                }                           
                            } 
                            returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ELIGIBLE_FOR_RECOMMENDATION,listOfAccEligibleForRecommendation);
                        }   
                    }  
                    if(HUMUtilityHelper.isCRMFunctionalityON('3581472')){
                        Id taskRecId = Schema.SObjectType.Task.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKRECORDTYPENAME).getRecordTypeId();
                        returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_TASK_RECTYPEID_KEY,taskRecId);
                    }          
				}                				
			}            
			catch(Exception ex) 
			{
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETRIEVEINTERACTIONS);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETRIEVEINTERACTIONS, ex.getMessage());
			} 
        return returnMap;
    }
    /*
    * Method Name   :    deduceHEAssociation
    * Description   :    This method deduces Health educator record associated with the selected Interaction
    * Return Type   :    Boolean
    * Parameters    :    Interaction Id
    */
    @AuraEnabled
    public static Boolean deduceHEAssociation(String recordId) 
    {
        Boolean hasHERecord = false;
        try
        {
            List<CRMRetail_OOO_Tracking__c> trackingList = [SELECT Id, CRM_Retail_Interaction__c FROM CRMRetail_OOO_Tracking__c WHERE CRM_Retail_Interaction__c =: recordId];
            if(!trackingList.isEmpty())
            {
                hasHERecord = true;
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_DEDUCE_HE_ASSOCIATION);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_DEDUCE_HE_ASSOCIATION, ex.getMessage());
        }
        return hasHERecord;
    }
	/*
	* Method Name   :    getSDohAcronym
	* Description   :    method to get acronym from SDoh API call
	* Return Type   :    String
	* Parameters    :    Account Reference
	*/
    @AuraEnabled
    public static string getSDohAcronym(Account accRef){
        String acronym='';
        Date currentInteractionDate = Date.valueof(sSelectedDate);
        try{
            if(String.isNotBlank(accRef.Enterprise_ID__c)){
                CRMRetail_SDoH_Helper_HUM.getSDoHData(new Map<String,String>{accRef.Enterprise_ID__c=>accRef.Id}, false,currentInteractionDate);
                if(CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.containsKey(accRef.Enterprise_ID__c)){
                    acronym=' ('+CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.get(accRef.Enterprise_ID__c)+')';   
                }
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETSDOHACRONYM);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETSDOHACRONYM, ex.getMessage());
        }
        return acronym;  
    }
    /*
    * Method Name   :    createScheduledInteraction
    * Description   :    method to create visitor checkin and return instance.
    * Return Type   :    Storefront_Interaction__c
    * Parameters    :    List of selected Interaction,category,location.
    */
    @AuraEnabled
    public Static Storefront_Interaction__c createScheduledInteraction(List<Storefront_Interaction__c> interactionList, String category,Storefront_Location__c loc)
    {             
        try
        {            
            Storefront_Interaction__c visitorCheckinInteraction = new Storefront_Interaction__c();
            String locationId;
            Date interactionDate;
            String accountId;
            String intCategory;
            String visitorId;            
            String currInteractionDate='';                        
            String waiverDate='';
            Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId=new Map<String,Storefront_NotificationsWrapper_HUM>();                                    
            Map<Id,Storefront_I_Reason__c> mapOfCheckInTypeReasonToId=new Map<Id,Storefront_I_Reason__c>([SELECT Id,Name FROM Storefront_I_Reason__c WHERE (Name =:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN OR Name =:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN)]);
            Account acc;              
            Map<String,String> mapOfAccountToLastInteraction = new Map<String,String>();
            currLocationId=loc.Id;
            currLocationName = loc.Name;  
            currLocationType = loc.Location_Type__c;  
            Date currentInteractionDate = Date.valueof(sSelectedDate);                             
            for(Storefront_Interaction__c  sfInt: interactionList)
            {                    
                locationId = sfInt.Location__c;
                visitorId=sfInt.Visitor_ID__c;
                interactionDate = sfInt.Interaction_Date__c.date();
                accountId = sfInt.Account__c;
                if(sfInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL)
                    intCategory = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
                else if(sfInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE)
                    intCategory = '';                                                
            }
            acc = [SELECT Id,Enterprise_ID__c, CRMRetail_PreferredName__c,Name,FirstName,LastName, Waiver_Date__C,Birthdate__c,GCM_Visitor_Barcode__c,CRMRetailNotificationOptOut__c,(SELECT Id,Name,Effective_Date__c,status__c,End_Date__c,Member__C,Product__c,Policy__r.Platform__c,Policy__r.Product__r.Major_LOB__c,Policy__r.Source_Cust_Cov_Key__c,Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c,Policy__r.Product_Type__c FROM Policy_Members__r WHERE Member__r.ETL_Record_Deleted__c = false ),Veteran_Status__c,RecordType.Name,(Select Id,Reason__c, Account__c,location__r.Name,Interaction_Date_Only__c,Interaction_Date__c from CRM_Retail_Interactions__r where Reason__c IN: mapOfCheckInTypeReasonToId.keySet() ORDER BY CREATEDDATE DESC) FROM Account WHERE Id =: accountId LIMIT 1];                       
            mapOfIdToAccount.put(acc.id,acc);
            mapOfReqValforNotificationByAccId.put(acc.Id,mapOfAccountToLastInteraction);
            if(acc.CRM_Retail_Interactions__r!=null && acc.CRM_Retail_Interactions__r.size()>0){
                for(Storefront_Interaction__c intRef:acc.CRM_Retail_Interactions__r){
                    if(mapOfReqValforNotificationByAccId.containsKey(acc.Id)){                         
                        Integer offset = UserInfo.getTimezone().getOffset(intRef.Interaction_Date__c); 
                        Datetime interactionDateFormatted = intRef.Interaction_Date__c.addSeconds(offset/1000);     
                        mapOfAccountToLastInteraction.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE,intRef.location__r.Name+'@'+interactionDateFormatted.format(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_DATEFORMAT));
                        mapOfReqValforNotificationByAccId.put(acc.Id,mapOfAccountToLastInteraction);  
                        break;
                    }                      
                }   
            }
            
            String visitorCheckinId = [SELECT Id FROM Storefront_I_Reason__c WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN LIMIT 1].Id;
            List<Storefront_Interaction__c> visitorCheckinIntList = [SELECT Id,Veteran__C,Storefront_Vstr_Indicator__c FROM Storefront_Interaction__c WHERE Location__c =: locationId AND DAY_ONLY(Interaction_Date__c) =: interactionDate AND Account__c =: accountId AND (Reason__r.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN OR Reason__r.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN) AND Category__c =: intCategory];            
            if(visitorCheckinIntList.isEmpty())
            {                
                Integer offset = UserInfo.getTimezone().getOffset(system.now());
                Datetime currTime = system.now();
                List<String> dateString;
                Integer year, month, day;
                Integer hours = currTime.hour(), minutes = currTime.minute(), seconds = currTime.second();
                dateString = string.valueOf(interactionDate).split('-', 3);
                year = Integer.valueOf(dateString[0]);
                month = Integer.valueOf(dateString[1]);
                day = Integer.valueOf(dateString[2]);
                DateTime interactionDateTime = Datetime.newInstance(year, month, day,hours, minutes, seconds);                  
                if(acc.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER){                    
                    visitorCheckinInteraction = new Storefront_Interaction__c(Location__c = locationId, Interaction_Date__c = interactionDateTime, Account__c = accountId, Visitor_ID__c = visitorId, Reason__c = visitorCheckinId,Veteran__c=interactionList.get(0).Veteran__c,New_Member__C=interactionList.get(0).New_Member__C,Aging_In__C=interactionList.get(0).Aging_In__c,Storefront_Vstr_Indicator__c=interactionList.get(0).Storefront_Vstr_Indicator__c); 
                    Map<String,Object> mpOfIndicatorToMetricValue = CRMRetail_HomePage_LC_HUM.checkVisitorIndicatorEligibility(visitorCheckinInteraction.Storefront_Vstr_Indicator__c);                        
                        if(mpOfIndicatorToMetricValue != NULL && mpOfIndicatorToMetricValue.containsKey(System.Label.CRMRetail_SNP_Text)){                        
                            mapOfReqValforNotificationByAccId.get(acc.Id).put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INDICATOR_KEY,mpOfIndicatorToMetricValue.get(System.Label.CRMRetail_SNP_Text).toString());
                        } 
                    mapOfReqValforNotificationByAccId.get(acc.Id).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITORID,visitorId); 
                        List<CRMRetail_ConsolidatedReq_DTO_HUM> lstServiceReqs = GenericHelper_CRMRetail_H_HUM.formConsolidatedServiceRequest(new List<Account>{acc},true);
                        if(lstServiceReqs != null && !lstServiceReqs.isEmpty()){
                           CRMRetail_SDoH_Helper_HUM.requestConsolidatedService(lstServiceReqs,false,currentInteractionDate);
                        }                     
                    visitorCheckinInteraction.SDoH__c = CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.containsKey(mapOfIdToAccount.get(acc.Id).Enterprise_ID__c);                    
                }
                else{
                    visitorCheckinInteraction = new Storefront_Interaction__c(Location__c = locationId, Interaction_Date__c = interactionDateTime, Account__c = accountId, Visitor_ID__c = acc.GCM_Visitor_Barcode__c, Reason__c = visitorCheckinId,Veteran__c=interactionList.get(0).Veteran__c,New_Member__C=interactionList.get(0).New_Member__C,Aging_In__C=interactionList.get(0).Aging_In__c,SDoH__c=interactionList.get(0).SDoH__c); 
                    mapOfReqValforNotificationByAccId.get(acc.Id).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITORID,acc.GCM_Visitor_Barcode__c);            
                }
                String interactionCategory = category == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE ? null :category;
                mapOfReqValforNotificationByAccId.get(acc.Id).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY,interactionCategory);
                if(category == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL)  
                {
                    visitorCheckinInteraction.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;                     
                }                                
                if(acc.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR)
                {
                    visitorCheckinInteraction.CRM_Retail_Interaction_Visitor_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER;
                    mapOfVistorTypeByAccId.put(acc.Id,visitorCheckinInteraction.CRM_Retail_Interaction_Visitor_Type__c);                    
                }                    
                else if(acc.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                {
                    visitorCheckinInteraction.CRM_Retail_Interaction_Visitor_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER;   
                    mapOfVistorTypeByAccId.put(acc.Id,visitorCheckinInteraction.CRM_Retail_Interaction_Visitor_Type__c);
                }                  
            }   
            return visitorCheckinInteraction;   
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CREATE_SCH_INTERACTION);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CREATE_SCH_INTERACTION, ex.getMessage());
        }
    }    
    /*
    * Method Name   :    createNewInteractions
    * Description   :    method to Create interactions when clicked on New Interaction Button on Homepage.
    * Return Type   :    Boolean
    * Parameters    :    Selected Interaction,event rows from Visitor Interaction and Calender Components.
    */
    @AuraEnabled
    Public Static Map<String,String> createNewInteractions(String sinteractionList, String sintReasonList, String categoryType) 
    {      
        List<Storefront_Interaction__c> interactionList = (List<Storefront_Interaction__c>)System.JSON.deserialize(sinteractionList, List<Storefront_Interaction__c>.class);
        List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper> lstOfEvent = (List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper>)system.json.deserialize(sintReasonList,List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper>.class);
        Boolean isSuccess = false;
        DateTime utcDateTime = CRMRetail_HomePage_LC_HUM.getUTCDateTime(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DISPLAYDATE);        
        Set<Storefront_Interaction__c> IntList = new Set<Storefront_Interaction__c>();
        Map<String, String> returnMap = new Map<String, String>();
        string category = '';               
        Date currentInteractionDate = Date.valueof(sSelectedDate);                 
        String currLoc = sLocation.split('-',2)[1];
        Storefront_Interaction__c newInteraction = new Storefront_Interaction__c();
        Storefront_Interaction__c visitorCheckin = new Storefront_Interaction__c();
        Storefront_Location__c loc = [SELECT Id,Name, Maximum_Occupancy__c,Location_Type__c FROM Storefront_Location__c WHERE Name =: currLoc LIMIT 1];                                
        Map<String,String> mapOfEnterpriseIdToAccId = new Map<String,String>();
        Map<String,Object> mpIndicators = new Map<String,Object>();
        Boolean switch_4012388 = HUMUtilityHelper.isCRMFunctionalityON('4012388');         
        try {                                                                    
            for(CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper intReason : lstOfEvent) {               
                for(Storefront_Interaction__c interaction : interactionList) {
                    newInteraction = new Storefront_Interaction__c(); 
                    newInteraction.Account__c = interaction.Account__c;                    
                    newInteraction.Location__c = interaction.Location__c;
                    newInteraction.Reason__c = intReason.ReasonId;
                    newInteraction.IsAllDayEvent__c = intReason.IsAllDayEvent;
                    newInteraction.Visitor_ID__c = interaction.Visitor_ID__c;
                    newInteraction.CRM_Retail_Interaction_Visitor_Type__c = interaction.CRM_Retail_Interaction_Visitor_Type__c;                                                           
                    newInteraction.Veteran__c = interaction.Veteran__c;
                    newInteraction.New_Member__c = interaction.New_Member__c;
                    newInteraction.Aging_In__C = interaction.Aging_In__c;
                    newInteraction.SDoH__C = interaction.SDoH__C;  
                    if(switch_4012388){
                        String vstrIndicator = interaction.Storefront_Vstr_Indicator__c;
                        mpIndicators = CRMRetail_HomePage_LC_HUM.parseVisitorIndicators(vstrIndicator);
                        if(mpIndicators != NULL && mpIndicators.size() > 0){ 
                            newInteraction.Storefront_Vstr_Indicator__c = GenericHelper_CRMRetail_H_HUM.serializeJSONStructure(mpIndicators,System.label.CRMRetail_Vstr_Indicator_Order);
                    	}
                    }
                    else{
                        newInteraction.Storefront_Vstr_Indicator__c = interaction.Storefront_Vstr_Indicator__c;
                    }
                    if(intReason.ReasonName != NULL && intReason.ReasonName == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HEALTH_EDUCATOR_REASON && interaction.CRM_Retail_Interaction_Visitor_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER){
                        mapOfEnterpriseIdToAccId.put(interaction.Account__r.Enterprise_ID__c,interaction.Account__c);                        
                    }
					if(categoryType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NEW)
							newInteraction.Category__c = interaction.Category__c;
					else if(categoryType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SCH_ONSITE)
						newInteraction.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE;
					else if(categoryType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SCH_VIRTUAL)
						newInteraction.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL; 
                    else if(categoryType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION) 
                    {
                        if(HUMUtilityHelper.isCRMFunctionalityON('3866537')){
                            newInteraction.Storefront_Is_EventRecmded__c = true;
                        }
                        if(recommendationSwitch!=null && recommendationSwitch==true && intReason.IsvirtualEvent== false) 
                            newInteraction.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE;
                    }     
                    if(intReason.IsAllDayEvent == True){
                        if(categoryType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SCHEDULED.toLowercase())
                        {
                            newInteraction.Storefront_Event_Starttime__c = intReason.StartDateTime;
                            newInteraction.Storefront_Event_Endtime__c = intReason.EndDateTime;
                        }
                        else
                        {
                            newInteraction.Storefront_Event_Starttime__c = intReason.StartDateTime.addDays(1).addHours(4).addMinutes(59);
                            newInteraction.Storefront_Event_Endtime__c = intReason.EndDateTime.addDays(1).addHours(4).addMinutes(59);
                        }
                    }
                    else{
                        newInteraction.Storefront_Event_Starttime__c = intReason.StartDateTime;
                        newInteraction.Storefront_Event_Endtime__c = intReason.EndDateTime;
                    }
                    if(categoryType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SCHEDULED.toLowercase())
                    {
		    	        if(HUMUtilityHelper.isCRMFunctionalityON('3866537')){
                            newInteraction.Storefront_Is_EventRecmded__c = interaction.Storefront_Is_EventRecmded__c;
                        }
                        if(interaction.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL)
                        {
                            visitorCheckin = createScheduledInteraction(interactionList,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL,loc);                            
                            newInteraction.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;                            
                        }
                        else if(interaction.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE)
                        {
                            visitorCheckin = createScheduledInteraction(interactionList,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE,loc);
                            newInteraction.Category__c = '';                            
                        }                                            
                    }                    
                    if(newInteraction.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE || newInteraction.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL)
                    {
                        if(newInteraction.isAllDayEvent__c)
                        {
                            List<String> dateString;
                            Integer year, month, day;
                            Integer hours = intReason.StartDateTime.hour(), minutes = intReason.StartDateTime.minute(), seconds = intReason.StartDateTime.second();
                            dateString = string.valueOfGMT(intReason.StartDateTime).split('-', 3);
                            year = Integer.valueOf(dateString[0]);
                            month = Integer.valueOf(dateString[1]);
                            day = Integer.valueOf(dateString[2].substring(0,dateString[2].length()-9));
                            newInteraction.Interaction_Date__c = Datetime.newInstance(year, month, day,12, 0, 0);                            
                        }
                        else
                        {
                            newInteraction.Interaction_Date__c = newInteraction.Storefront_Event_Starttime__c;
                        }                        
                    }                    
                    else
                        newInteraction.Interaction_Date__c = utcDateTime;                                        
                    IntList.add(newInteraction);
                }                
            }       
            if(mapOfReqValforNotificationByAccId.size()>0 && mapOfVistorTypeByAccId.size()>0 && currLocationId!=NULL && currInteractionDate!=NULL && currLocationType != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL && categoryType != GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION){   
                mapOfNotificationsWrapToAccId=new Storefront_Notification_Helper_HUM().generateNotificationData(mapOfReqValforNotificationByAccId, mapOfVistorTypeByAccId, currLocationId,currInteractionDate,mapOfIdToAccount); 
            }
            if(IntList.size() > 0) 
            {
                if(switch_4012388 && mapOfNotificationsWrapToAccId != NULL && mapOfNotificationsWrapToAccId.containsKey(visitorCheckin.Account__c) && mapOfNotificationsWrapToAccId.get(visitorCheckin.Account__c) != NULL && mapOfNotificationsWrapToAccId.get(visitorCheckin.Account__c).fstTimeClndrVst){
                    mpIndicators.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_FRSTTIMEVSTR_TEXT,true); 
                    if(String.isNotBlank(visitorCheckin.Storefront_Vstr_Indicator__c)){                    	
                        mpIndicators.putAll((Map<String,Object>)JSON.deserializeUntyped(visitorCheckin.Storefront_Vstr_Indicator__c));                        
                    }   
                    visitorCheckin.Storefront_Vstr_Indicator__c = GenericHelper_CRMRetail_H_HUM.serializeJSONStructure(mpIndicators,System.label.CRMRetail_Vstr_Indicator_Order);    
                }
                if(visitorCheckin != new Storefront_Interaction__c()){
                    IntList.add(visitorCheckin);
                }                
                if(categoryType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION)
                    returnMap = CRMRetail_HomePage_LC_HUM.commitInteractions(new List<Storefront_Interaction__c>(IntList), mapOfNotificationsWrapToAccId, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION); 
                else   
                    returnMap = CRMRetail_HomePage_LC_HUM.commitInteractions(new List<Storefront_Interaction__c>(IntList), mapOfNotificationsWrapToAccId, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_EVENT_INTERACTIONS);                        
            }
            if(mapOfEnterpriseIdToAccId.size()>0){               
                  CRMRetail_SDoH_Helper_HUM.populateSDoHForHealthEducatorInteraction(mapOfEnterpriseIdToAccId,true,currentInteractionDate);                 
            }
        }
        catch(Exception ex) {            
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CREATEINT);
            String error = CRMRetailVNC_Helper_HUM.getError(ex.getMessage());
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CREATEINT,error);           
        }
        return returnMap;
    }
    /*
    * Method Name   :    getLocationAndDateValues
    * Description   :    Gets current Date from cache
    * Return Type   :    Map<String,Object>
    * Parameters    :    NA
    */
    @AuraEnabled
    public static Map<String,Object> getLocationAndDateValues(String sEventSelectedDate)
    {
		try
		{
			Map<String,Object> dtMap = new Map<String,Object>();
			List<String> dtStrList = new List<String>();			
			DateTime dt = Datetime.now();
			String dtString = dt.format();
			DateTime startDate;
			DateTime endDate;
			String location = '';
			
			string LName = sLocation;
			if(string.isNotEmpty(LName))
				location = LName.split('-')[0];            
            if(String.isNotBlank(sEventSelectedDate)){
                if(sEventSelectedDate.contains('/'))
				{
					dtStrList = sEventSelectedDate.split('/');
					sEventSelectedDate = dtStrList[2]+'-'+dtStrList[0]+'-'+dtStrList[1];                    
				}
                if(sEventSelectedDate.contains('-')){
					dtStrList = sEventSelectedDate.split('-');                    
				}
                startDate = DateTime.newInstance(integer.valueof(dtStrList[0]), integer.valueof(dtStrList[1]), integer.valueof(dtStrList[2]), dt.hour(),0,0);               
            }        
            if(dt.minute() != 0)
				startDate = startDate.addHours(1);
			endDate = startDate.addHours(1);
            TimeZone tz = UserInfo.getTimeZone();
                String sUserTimeZone = tz.getDisplayName();
			dtMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_STARTTIME,startDate.format('yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'',sUserTimeZone));
			dtMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ENDTIME,endDate.format('yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'',sUserTimeZone));
			dtMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION,location);            
			return dtMap;
		}
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETLOCATIONANDDATEVALUES);
            String error = CRMRetailVNC_Helper_HUM.getError(ex.getMessage());
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETLOCATIONANDDATEVALUES,error);           
        }
    }
	/*
    * Method Name   :    isNewEvtButtonVisisble
    * Description   :    This method decided visibility of the New Event button based on permissionset asignement
    * Return Type   :    Boolean
    * Parameters    :    NA
    */
    @AuraEnabled
    public static Map<String,String> isNewEvtButtonVisible() 
    {
        Boolean isEligible = false;
        String currIntDate = '';
        List<String> stList= new List<String>();
        sSelectedDate = formatDate(sSelectedDate);
        Map<String,String> retMap = new Map<String,String>();
        try
        {
            stList = GenericHelper_CRMRetail_H_HUM.getUserPermissionSet();
            if(!stList.isEmpty())
            if(stList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_BUSINESSADMIN) || stList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COORDINATOR) || stList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN))
            {
                isEligible = true;
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) 
            {
                currIntDate = date.today().format();
                Date dIntDate = date.parse(currIntDate);
                String sParsedDate = String.valueOf(dIntDate);
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, sParsedDate);
                currIntDate = sParsedDate;
            }
            else 
            {
                currIntDate = sSelectedDate;
            }
            retMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CURRINTDATE,currIntDate);
            retMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_IS_ELIGIBLE,String.valueOf(isEligible));
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISNEWEVTBUTTONVISIBLE);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISNEWEVTBUTTONVISIBLE, ex.getMessage());
        }
        return retMap;
    }
    /*
    * Method Name   :    getEvents
    * Description   :    gets all the Events created on the selected Location and Date on the Home Page and displays the events in Calender component of Home Page. 
    * Return Type   :    string
    * Parameters    :    interaction date of HomePage
    */
    @AuraEnabled
    public static List <Event> getEvents(String displayDate) {
        List<Event> eventlistone = new List<Event>();
        displayDate = formatDate(displayDate);
        Date presentDate = Date.valueof(displayDate);
        Datetime todayDateTime = DateTime.newInstance(presentDate.year(),presentDate.month(),presentDate.day(),00,00,00);
        Integer offset = UserInfo.getTimezone().getOffset(todayDateTime);
        Datetime AlldayDate = todayDateTime.addSeconds(offset/1000);
        DateTime tomDateTime = todayDateTime.adddays(1);
        try{            
            string LName = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
            LName = LName.split('-', 2)[1];
            List<Event> eventlist = [SELECT Id, Reason__r.Name,Reason__r.isGo365Eligible__c, StartDateTime, EndDateTime, Location, DurationInMinutes, IsAllDayEvent, Virtual_Attendee_Count__c, Known_Attendee_Count__c, Scheduled_Onsite_Attendee_Count__c, Scheduled_Virtual_Attendee_Count__c 
                                     FROM Event where what.Name = :LName AND ((StartDateTime >= :todayDateTime AND StartDateTime <= :tomDateTime AND IsAllDayEvent=false) OR  (StartDateTime =: AlldayDate AND IsAllDayEvent=true))
                                     AND Reason__c != null AND Reason__r.Inactive__c != True ORDER BY IsAllDayEvent ASC, StartDateTime ASC , Reason__r.Name ASC];            
            if(eventlist != null)
            {
                eventlistone.addAll(eventlist);
            }            
        }
        catch(Exception ex) {                        
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETEVENTS);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETEVENTS, ex.getMessage());
        } 

        return eventlistone;
    }
	/*
    * Method Name   :    deleteInteraction
    * Description   :    method to delte interaction when clicked on delete Interaction dropdown action on Homepage Interaction list.
    * Return Type   :    Storefront_Interaction__c
    * Parameters    :    Selected Interaction id from Visitor Interactions Component.
    */
    @AuraEnabled
    public static Boolean deleteInteraction(string interactionid) 
	{
		try
		{
			List<Storefront_Interaction__c> interactionList = new List<Storefront_Interaction__c>();
			Boolean isSuccess = false;
			
			if(!String.isEmpty(interactionid)) {
				interactionList = [SELECT ID,Name from Storefront_Interaction__c where ID =: interactionid limit 1];  
			}
			if(interactionList.size() > 0)
			{
				if(Schema.sObjectType.Storefront_Interaction__c.isDeletable()){
					delete interactionList;
					isSuccess = true;
				}
                else{
                     String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SINTERACTION;
                     HUMExceptionHelper.logError(objectError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DELETEINT,'','');
                     Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DELETEINT, objectError);
                }
			}                
			return isSuccess;
		}
        catch(Exception ex) {                        
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DELETEINT);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTIONEVENTCLS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DELETEINT, ex.getMessage());
        }       
    }

    /*
    * Method Name   :    fetchSearchConfigMetaData
    * Description   :    method to retrive all active search types
    * Return Type   :    List<CRMRetail_Interaction_Search_Config__mdt>
    * Parameters    :    String
    */
    @AuraEnabled
    public static List<CRMRetail_Interaction_Search_Config__mdt> fetchSearchConfigMetaData()
    {
        List<CRMRetail_Interaction_Search_Config__mdt> returnData = new List<CRMRetail_Interaction_Search_Config__mdt>();
        List<String> excludedTypes = new List<String>();
        
        if(!HUMUtilityHelper.isCRMFunctionalityON('3957580'))
        {
            excludedTypes.add('Created By');
        }
        
        returnData = [SELECT Id, MasterLabel, Label,order__c, Field_Api__c  FROM CRMRetail_Interaction_Search_Config__mdt where isActive__c = true and MasterLabel NOT IN:excludedTypes order by order__c asc];
        return returnData;
    }
    /*
    * Method Name   :    formatDate
    * Description   :    method to convert date from '/' to '-'
    * Return Type   :    String
    * Parameters    :    String
    */
    public static string formatDate(String sDate){
        if(sDate != null && sDate.contains('/'))
        {
            List<String> dtStrList = sDate.split('/');
            sDate = dtStrList[2]+'-'+dtStrList[0]+'-'+dtStrList[1];
        }
        return sDate;
    }
    @TestVisible
    private class EventsWrapper{
        @TestVisible Id ReasonId;
        @TestVisible  boolean IsAllDayEvent;
        @TestVisible DateTime StartDateTime;
        @TestVisible DateTime EndDateTime;
        @TestVisible String ReasonName;
        @TestVisible boolean IsvirtualEvent;
    }
}