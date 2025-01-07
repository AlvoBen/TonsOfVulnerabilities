/******************************************************************************************************************************
	Apex Class Name  : CRMRetail_HomePage_LC_HUM
	Version          : 1.0 
	Created Date     : 06/29/2022
	Function         : Controller for Interaction Display Criteria, Search/Add Visitor, Scan Card                    
	Test Class       : CRMRetail_HomePage_LT_HUM
	Modification Log :
	* Developer              Code Review            Date                 Description
	* ------------------------------------------------------------------------------------------------------------------------------                
	* Sahil Verma                                   06/29/2022           REQ-3450456 Original Version
	* Vivek Sharma	                                07/18/2022           REQ-3449791 Storefront Modernization (Business Admin/Super Admin) - Home (Left Side)
	* Vinoth L                                      09/12/2022           09/23 Observation Fix
	* Vinoth L                                      12/28/2022           REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Current CY in Storefront
	* Vinoth L                                      01/05/2023           User Story 4107848: T1PRJ0154546 / SF / MF3 Storefront: Update Visitor ID & Type Population Logic
	* Mohamed Thameem      	                     	01/12/2023           Request 3866581 - Mobile Scanner
	* Vinoth L                                      03/17/2023           DEF - 7421 : Phone Search Criteria Fix
	* Mohamed Thameem      	               	        06/12/2023           User Story 4695921: T1PRJ0154546 / SF/ MF9 Storefront - Allow Search By Phone Number Only
	* Vinoth L										06/24/2023			 User Story 4769775: - T1PRJ0154546 / SF / MF9 Storefront - Carry Forward First Time Visitor to Event Interactions
	* Mohamed Thameem      	                	    08/28/2023           User Story 5012149: T1PRJ0154546 / DP / MF9 Storefront - Update Guest Card # Logic
	* Mohamed Thameem      	                	    09/22/2023           User Story 5132829: T1PRJ0154546 / SF / MF9 Storefront - Include Preferred Name in Search
	* Vinoth L										10/03/2023			 User Story 5132659 Ability to Edit Mobile Location Field
	* Vinoth L										10/21/2023			 User Story 5225414: T1PRJ0154546 / SF / MF9 Storefront - Show Future Policy Members in Search Results
	* Mohamed Thameem                               11/01/2023           User Story 5290960: T1PRJ0154546 / SF / MF9 Storefront [DEFECT ID # 8261] - Update Guest Card # Logic
	*********************************************************************************************************************************/
	public with sharing class CRMRetail_HomePage_LC_HUM {
		public static Boolean switch_Attendance = HUMUtilityHelper.isCRMFunctionalityON(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE_SWITCH);
		public static Map<String,String> returnMap = new Map<String,String>();
		public static Map<String,String> mapOfVistorTypeByAccId=new Map<String,String>();
		public static Map<Id,Account> mapOfIDToAccount = new Map<Id,Account>();
		public static Map<String,String> mapOfAccIdToEnterpriseId=new Map<String,String>();
		public static Map<String,Map<String,String>> mapOfReqValforNotificationByAccId = new Map<String, Map<String, String>>();
		public static String sCacheLocation = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
		public static String sCacheinteractionDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
		public static String sCacheCurentDevice = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTDEVICE);
		public static Id expiredMemberAccId;
		public static boolean isInactiveMemberCheckin=false;
		private static Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId=new Map<String,Storefront_NotificationsWrapper_HUM>();
		private static List<CRMRetail_SearchResults_DTO_HUM> lstOfMemberSearched = new List<CRMRetail_SearchResults_DTO_HUM>();
		private static boolean isMobileLocationUpdated=false;
		/*
		* Method Name   :    loadLocationsandInteractionDate
		* Description   :    gets Interaction Date and all the active locations available in the CRM Retail Location Object and display them on the Home Page Location dropdown field. 
		* Return Type   :    Map<String,String>
		* Parameters    :    NA
		*/
		@AuraEnabled
		public static Map<String,String> loadLocationsandInteractionDate()
		{
			Map<String,String> mapOfIntDateandLocations = new Map<String,String>();
			mapOfIntDateandLocations.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CURRINTDATE,getOrSetInteractionDateInCache('',false));
			mapOfIntDateandLocations.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION,getorSetLocationInCache('',false));
			mapOfIntDateandLocations.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_LIST_OF_LOCATIONS,JSON.Serialize(getLocations()));
			If(switch_Attendance) mapOfIntDateandLocations.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CURR_ATT,deduceAttendance());
			return mapOfIntDateandLocations;
		}

		/*
		* Method Name   :    updateMobileCache
		* Description   :    to update device type in cache 
		* Return Type   :    VOID
		* Parameters    :    string device
		*/
		@AuraEnabled
		public static void updateMobileCache(string device)
		{
			Cache.Session.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTDEVICE,device);
			getOrSetInteractionDateInCache('',true);
		}
		
		/*
		* Method Name   :    getVisitorCardConfigs
		* Description   :    method to get all the custom settings for the visitor card configs. 
		* Return Type   :    List<string>
		* Parameters    :    NA
		*/
		@AuraEnabled
		public static Map<String,Object> getVisitorCardConfigs()
		{        
			Map<String,Object> visitorCardInfo = new Map<String,Object>();
			set<String> guestCards = new set<String>();
			List<Integer> minMaxList = new List<Integer>();
			
			for(CRMRetail_VisitorCardConfig__mdt config : CRMRetail_VisitorCardConfig__mdt.getall().values())
			{
				if(config.Switch_No__c==null || HUMUtilityHelper.isCRMFunctionalityON(config.Switch_No__c))
				{
					if(config.isGuestCard__c)
					{
						guestCards.add(config.Card_Initial__C);
					}
					
					if(config.Card_Initial__C == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HNC_SHORT)
					{
						minMaxList.add(HUMUtilityHelper.isCRMFunctionalityON('5290960') ? integer.valueOf(config.Maximun_Length__c) : GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HNC_TRIMMED_MAX_LENGTH);
					}
					else
					{
						minMaxList.add(integer.valueOf(config.Maximun_Length__c));
					}
					
					minMaxList.add(integer.valueOf(config.Minimum_Length__c));

				} 
			}
			
			minMaxList.sort();
			visitorCardInfo.put('guestCards',guestCards);
			visitorCardInfo.put('minLength',minMaxList[0]);
			visitorCardInfo.put('maxLength',minMaxList[minMaxList.size()-1]);        
			return visitorCardInfo;  
		}

		/*
		* Method Name   :    getLocations
		* Description   :    gets all the active locations available in the CRM Retail Location Object and display them on the Home Page Location dropdown field. 
		* Return Type   :    List<string>
		* Parameters    :    NA
		*/
		@AuraEnabled
		public static List<String> getLocations()
		{
			List<String> stList = new List<String>();
			set<String> stset = new set<String>();
			List<Storefront_Location__c> getLocation = new List<Storefront_Location__c>();
			try{
				getLocation = [SELECT Id, Name FROM Storefront_Location__c where Inactive_indicator__c != True ORDER BY NAME ASC];
				for(Storefront_Location__c loc : getLocation){
					stset.add(loc.Name);
				}
				stList.addAll(stset);
				stList.sort();           
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETLOCATIONS);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETLOCATIONS, ex.getMessage());            
			}
			return stList;
		}

		/*
		* Method Name   :    resetDefault
		* Description   :    restore Location & Interaction date in cache 
		* Return Type   :    Map<string,string>
		* Parameters    :    NA
		*/
		@AuraEnabled
		public Static Map<string,string> resetDefault() {
			
			Map<string,string> resetMap =  new Map<string,string>();
			
			string defaultLoc = getorSetLocationInCache(null, true);
			resetMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_DEFAULTLOCATION,defaultLoc);
			
			string defaultIntDate =  getOrSetInteractionDateInCache(null, true);
			resetMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_DEFAULTDATE,defaultIntDate);
			
			return resetMap;
		}
		/*
		* Method Name   :    getOrSetInteractionDateInCache
		* Description   :    gets or sets the selected value of Interaction date in cache 
		* Return Type   :    string
		* Parameters    :    Date which is selected on Interaction display criteria
		*/
		@AuraEnabled
		public Static String getOrSetInteractionDateInCache(String dateValue, Boolean isResetDefault) {
			String currIntDate = null;
			String attCount='';
			string sParsedDate = null;
			try {
				Boolean isIntDateInCache = (Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) ? true : false;
				if(String.isNotBlank(dateValue))
				{                
					if(isIntDateInCache){
						currIntDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
						if(currIntDate != dateValue) Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, dateValue);                    
						currIntDate = dateValue;
					}
					if(switch_Attendance){
						attCount = deduceAttendance();
						return attCount;
					}
					else{
						return null;
					}               
				}
				else{
					if(!isResetDefault)
					{
						if(!isIntDateInCache) {
							
							currIntDate = date.today().format();
							Date dIntDate = date.parse(currIntDate);
							sParsedDate = String.valueOf(dIntDate);
							Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, sParsedDate);
						}
						else {
							currIntDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
							if(currIntDate.contains('/')){
								Date dt = date.parse(currIntDate);
								sParsedDate = String.valueOf(dt);
								return sParsedDate;
							}else{
								return currIntDate;  
							}
						}
					}
					else {
						
						currIntDate = date.today().format();
						Date dIntDate = date.parse(currIntDate);
						sParsedDate = String.valueOf(dIntDate);
						Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, sParsedDate);
					} 
				}                                              
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DEDUCEINTDATECACHE);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DEDUCEINTDATECACHE, ex.getMessage());
			}
			return sParsedDate;
		}
		
		/*
		* Method Name   :    getorSetLocationInCache
		* Description   :    gets or sets the selected value of Location in cache 
		* Return Type   :    string
		* Parameters    :    Location which is selected on Interaction Display criteria Component
		*/
		@AuraEnabled
		Public Static String getorSetLocationInCache(String sNewLoc, Boolean isRestoreDefault) {
			String sLocation='';
			String currAtt = '';
			try{
				if(String.isNotBlank(sNewLoc))
				{
					sLocation = fetchNewLocation(sNewLoc);
					if(String.isNotBlank(sLocation)){
						Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, sLocation);
						if(switch_Attendance){
							currAtt = deduceAttendance();
							return currAtt;
						}
						else{
							return null;
						} 
						
					}
					else{
						sLocation = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NONE + '-' + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NONE;
						Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, sLocation);
						return null;
					}
				}
				else if(isRestoreDefault || !Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION))
				{
					sLocation = CRMRetail_HomePage_LC_HUM.getUserLocation();
					if(String.isBlank(sLocation)) {
						sLocation = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NONE + '-' + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NONE;
					}
					Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, sLocation);                
				}
				else{
					if(HUMUtilityHelper.isCRMFunctionalityON('5132659')){
						if(Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
							sLocation = CRMRetail_HomePage_LC_HUM.getUserLocation();
							if((String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION) != sLocation)
							{
								if(!isMobileLocationUpdated){
								sLocation = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
								}
								else{
									if(String.isBlank(sLocation)) {
										sLocation = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NONE + '-' + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NONE;
									}
									Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, sLocation);
									List<Storefront_Preference__c> locationList = [SELECT Id, Default_Location__r.Id, Default_Location__r.Name, user__c,CRMRetail_Mob_Loc_Updated__c from Storefront_Preference__c where user__c = :UserInfo.getUserId() AND Default_Location__C != NULL AND Default_Location__r.Inactive_indicator__c != True LIMIT 1];
									if(locationList.size() >0){
										locationList[0].CRMRetail_Mob_Loc_Updated__c = false;
										update locationList[0];
									}                        
								}
							}
						} 
					}
					else{
						sLocation = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
					}             
				}
				if(String.isNotBlank(sLocation)) sLocation = sLocation.substringAfter('-').trim();                 
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DEDUCELOCATIONCACHE);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DEDUCELOCATIONCACHE, ex.getMessage());
			}
			return sLocation;
		}
		
		/*
		* Method Name   :    getUserLocation
		* Description   :    gets the user default location from preferences object to display on the Home page Component. 
		* Return Type   :    string
		* Parameters    :    NA
		*/
		Private static String getUserLocation() 
		{
			String sLocation='';
			List<Storefront_Preference__c> locationList = [SELECT Id, Default_Location__r.Id, Default_Location__r.Name, user__c,CRMRetail_Mob_Loc_Updated__c from Storefront_Preference__c where user__c = :UserInfo.getUserId() AND Default_Location__C != NULL AND Default_Location__r.Inactive_indicator__c != True LIMIT 1];
			if(locationList.size() > 0) 
			{
				if(HUMUtilityHelper.isCRMFunctionalityON('5132659')) isMobileLocationUpdated = locationList[0].CRMRetail_Mob_Loc_Updated__c;
				sLocation = locationList[0].Default_Location__r.Id + '-'+locationList[0].Default_Location__r.Name;               
			}
			return sLocation;
		}
		/*
		* Method Name   :    deduceAttendance
		* Description   :    gets the Attendance based on the selected location and Interaction date on the Home page. 
		* Return Type   :    string
		* Parameters    :    NA
		*/
		@AuraEnabled
		Public Static String deduceAttendance() 
		{
			String currIntDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
			List<String> dateStringList = new List<String>();
			String currLoc = null;
			Date dt;
			String currAtt = '0';
			try 
			{           
				if(currIntDate.contains('/'))
				{
					dt = date.parse(currIntDate);
				}
				else if(currIntDate.contains('-'))
				{
					dateStringList = currIntDate.split('-');
					dt = date.newInstance(Integer.valueOf(dateStringList[0]),Integer.valueOf(dateStringList[1]),Integer.valueOf(dateStringList[2]));
				}
				else dt = date.today();
				if(dt <= date.today())
				{
					currLoc = getorSetLocationInCache('',false);
					List<Attendance__c> attList = [SELECT Id, Current_Attendance__c, Location__c, Date__c FROM Attendance__c WHERE Date__c =:dt AND Location__r.Name  =: currLoc LIMIT 1];
					if(attList.size() > 0)
					{
						if(attList[0].Current_Attendance__c != 0)
						{
							currAtt = String.valueof(attList[0].Current_Attendance__c);
						}
					}
				}
				else
					currAtt = 'future';
			}
			catch(Exception ex) 
			{
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, 'deduceAttendance');
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, 'deduceAttendance', ex.getMessage());
			}
			return currAtt;
		}
		/*
		* Method Name   :    fetchNewLocation
		* Description   :    method to fetch the recent Location changed by user and pass it to getorSetLocationInCache method.
		* Return Type   :    String
		* Parameters    :    Location Value selected by user on Home Page.
		*/
		Private Static String fetchNewLocation(String locationValue) {
			String sNewLocation='';
			List<Storefront_Location__c> locationList = [SELECT Id, Name from Storefront_Location__c where Name = :locationValue LIMIT 1];
			if(locationList.size() > 0) {
				sNewLocation = locationList[0].Id+'-'+locationList[0].Name;            
			}
			return sNewLocation;
		}
		/*
		* Method Name   :    saveDefaultLocationRecord
		* Description   :    Create the default location for that user if there is no location created for them when clicked on SaveAsHomeLocation Button.
		* Return Type   :    string
		* Parameters    :    Location name selected on the HomePage, which user want to set as Default.
		*/
		@AuraEnabled
		public static Storefront_Preference__c saveDefaultLocationRecord(string newLocationName)
		{
			Storefront_Preference__c finalRecord;
			CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj;
			try{
				Storefront_Location__c LocationId =[Select Id from Storefront_Location__c where Name =: newLocationName LIMIT 1];
				List<Storefront_Preference__c> insertRecord = new List<Storefront_Preference__c>();
				List<Storefront_Preference__c> results = [SELECT Id, Default_Location__c,Default_Location__r.Name,user__c 
											   from Storefront_Preference__c where user__c =:  UserInfo.getUserId()];
				if(results.isEmpty())
				{
					Storefront_Preference__c newrecord = new Storefront_Preference__c(Default_Location__c = LocationId.Id, user__c = UserInfo.getUserId());                
					if(HUMUtilityHelper.isCRMFunctionalityON('5132659')) newrecord.CRMRetail_Mob_Loc_Updated__c = true;
					accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, new List<Storefront_Preference__c>{newrecord}); 
					if(accessbilityObj.exceptionGenerated)
					{   
						HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION);
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION, accessbilityObj.exceptionToLog.getMessage());
					}
					else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
					{
						insert newrecord;
					}            
					else
					{
						String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SPREFERENCE;
						String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
						String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
						HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION,'','');
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION, logError);
					}
				}
				else if(results != null && !results.isEmpty())
				{
					for(Storefront_Preference__c res: results)
					{
						if(res.Default_Location__c == Null || res.Default_Location__c != LocationId.Id )
						{
							Storefront_Preference__c updaterecord = new Storefront_Preference__c(Id=res.Id, Default_Location__c = LocationId.Id, user__c = UserInfo.getUserId());
							if(HUMUtilityHelper.isCRMFunctionalityON('5132659')) updaterecord.CRMRetail_Mob_Loc_Updated__c = true;
							insertRecord.add(updaterecord);
						}
						
					}
					accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.UPDATABLE,insertRecord); 
								   

					if(accessbilityObj.exceptionGenerated)
					{ 
						HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION);
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION, accessbilityObj.exceptionToLog.getMessage());
					}
					else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
					{
						update insertRecord;
					}            
					else
					{
						String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SPREFERENCE;
						String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
						String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
						HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION,'','');
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION, logError);
					}
				}
				 finalRecord = [SELECT Id, Default_Location__c,Default_Location__r.Name,user__c,CRMRetail_Mob_Loc_Updated__c
											 from Storefront_Preference__c where user__c =:  UserInfo.getUserId() and 
											 Default_Location__r.Name =: newLocationName];            
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION, ex.getMessage());
			}
			  return finalRecord;
		}
		/*
		* Method Name   :    changeAttendanceCount
		* Description   :    Changes the Attendance count on click of '+' & '-' icon next to Attendance label on the Interaction display criteria component on Home Page.
		* Return Type   :    string
		* Parameters    :    Date selected on Interaction Display Criteria component & Icon's title attribute.
		*/
		@AuraEnabled
		Public Static String changeAttendanceCount(Date dateValue, String func)
		{
			String count = '';
			String LocationValue = getorSetLocationInCache('',false);
			try
			{
				List<Attendance__c> attList = [SELECT Id, Maximum_Concurrent_Visitors__c, Peak_Time__c, Current_Attendance__c, Visitors_Today__c, Location__c, Date__c FROM Attendance__c WHERE Date__c =: dateValue AND Location__r.Name  =: LocationValue LIMIT 1];       
				if(attList.size() > 0)
				{
					if(func == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INCREASE_TEXT)
					{
						attList[0].Current_Attendance__c = attList[0].Current_Attendance__c + 1;
					}
					else if(func == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_REDUCE_TEXT && attList[0].Current_Attendance__c > 0)
					{
						attList[0].Current_Attendance__c = attList[0].Current_Attendance__c - 1;
					}
					attList[0] = GenericHelper_CRMRetail_H_HUM.evaluateAttendance(attList[0]);
					update attList[0];
					count = string.valueof(attList[0].Current_Attendance__c);
				}
				else
				{
					Id locId = [SELECT Id, Name FROM Storefront_Location__c WHERE Name=:LocationValue LIMIT 1].Id;
					Attendance__c att = GenericHelper_CRMRetail_H_HUM.createAttendance(locId,dateValue);                
					CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, new List<Attendance__c>{att});             
					if(accessbilityObj.exceptionGenerated)
					{ 
						HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHANGEATTENDANCECOUNT_METHOD);
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHANGEATTENDANCECOUNT_METHOD, accessbilityObj.exceptionToLog.getMessage());
					}
					else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
					{
						insert att;                       
						count = string.valueof(att.Current_Attendance__c);
					}            
					else
					{
						String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SATTENDANCE;
						String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
						String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
						HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHANGEATTENDANCECOUNT_METHOD,'','');
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHANGEATTENDANCECOUNT_METHOD, logError);
					}                
				}
			}
			catch(Exception ex)
			{
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHANGEATTENDANCECOUNT_METHOD);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHANGEATTENDANCECOUNT_METHOD, ex.getMessage());
			}
			return count;
		}
		/*
		* Method Name   :    fetchSwitchResults
		* Description   :    This method returns values of the switch records
		* Return Type   :    Map<String,Boolean>
		* Parameters    :    NA
		*/
		@AuraEnabled
		public static Map<String,Boolean> fetchSwitchResults() 
		{
			Map<String,Boolean> switchMap = new Map<String,Boolean>();
			List<CRMFunctionality_ONOFF_Switch__c> switchList = new List<CRMFunctionality_ONOFF_Switch__c>();
			try
			{            
				for(CRMFunctionality_ONOFF_Switch__c OnOffswitch : [SELECT Id, Name, IsON__c FROM CRMFunctionality_ONOFF_Switch__c WHERE Name IN: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MODERNIZEDHOME_SWITCH_LIST])
				{                                
					switchMap.put('Switch_'+OnOffswitch.Name, OnOffswitch.IsOn__c);                
				}
			}
			catch(Exception ex) 
			{
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, 'fetchSwitchResults');
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, 'fetchSwitchResults', ex.getMessage());
			}
			return switchMap;
		}
		@AuraEnabled
		Public Static Account fetchAccountDetails(String accountId) {      
			Account accDetails;
			try{
			List<Account> listAccount = [SELECT Id, Name, GCM_Visitor_Barcode__c,Enterprise_ID__c FROM Account where Id= :accountId];
			if (listAccount.size() > 0)
			accDetails = listAccount[0];
			}
			catch(Exception ex) {
			String error = CRMRetailVNC_Helper_HUM.getError(ex.getMessage());
			HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_FETCHACCOUNTDETAILS_METHOD);
			throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_FETCHACCOUNTDETAILS_METHOD, error);
			}
			return accDetails;
		}
		
		@AuraEnabled
		Public Static Map<String, String> processCheckIn(String barcodeValue) {      
			try{
				fetchMemberOrVisitorAccount(barcodeValue);           
			}
			catch(Exception ex) {  
				String error = CRMRetailVNC_Helper_HUM.getError(ex.getMessage());
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_PROCESSCHECKIN);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_PROCESSCHECKIN, error);
			}
			
			return returnMap;
		}
		
		private Static void fetchMemberOrVisitorAccount(String barcodeValue) { 
			Boolean isAttendanceSwitch = fetchSwitchResults().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SWITH_ATTENDANCE_KEY);
			Boolean isMaxReached = false;        
			if(isAttendanceSwitch)
			{            
				isMaxReached = deduceMaxVisitorLimit();
			}
			if(!isMaxReached)
			{
				Boolean isGuestCard = false;
				for(String initial : (Set<String>)getVisitorCardConfigs().get('guestCards'))
				{
					if(barcodeValue.startsWith(initial))
					{
						isGuestCard = true;
					}
				}
				
				if(isGuestCard)
				{
					fetchVisitorId(barcodeValue);
				}
				else{								
					fetchMemberId(barcodeValue,false);             
				}
			}
			else
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISMAXREACHED,String.valueOf(isMaxReached));
		}
		
		public static boolean deduceMaxVisitorLimit()
		{    
			String sLocation = sCacheLocation.substringAfter('-').trim(); 
			List<Attendance__c> attList = new List<Attendance__c>();        
			List<Storefront_Location__c> lstCurrentLocation = [SELECT Id, Maximum_Occupancy__c,Location_Type__c FROM Storefront_Location__c WHERE Name =: sLocation LIMIT 1];                
			Decimal capacity = 0;
			Date interactionDate = GenericHelper_CRMRetail_H_HUM.getCurrentInteractionDate();
			Boolean isMaxReached = false;
			if(interactionDate == NULL){
				interactionDate = date.today();
			}  
			if(lstCurrentLocation.size()>0){
				attList= [SELECT Id, Current_Attendance__c, Location__c, Date__c FROM Attendance__c WHERE Date__c =:interactionDate AND Location__c  =: lstCurrentLocation[0].Id LIMIT 1];                        
				if(attList.size() > 0 && lstCurrentLocation[0].Maximum_Occupancy__c != null && attList[0].Current_Attendance__c != null)
				{                
					capacity = lstCurrentLocation[0].Maximum_Occupancy__c - attList[0].Current_Attendance__c;
				}
				else if(lstCurrentLocation[0].Maximum_Occupancy__c != null)
					capacity = lstCurrentLocation[0].Maximum_Occupancy__c;
			}       
			if(capacity <= 0 && lstCurrentLocation[0].Maximum_Occupancy__c != null){
				isMaxReached = true;
			}
			return isMaxReached;
		}
		
		 /*
		 * Method Name   :    processMemberIdSearch
		 * Description   :    initiates the member Id search
		 * Return Type   :    Map<String,String>
		 * Parameters    :    Member Id to be searched
		 */
		@AuraEnabled
		public static List<CRMRetail_SearchResults_DTO_HUM> processMemberIdSearch(String memberId){        
			fetchMemberId(memberId,true);
			return lstOfMemberSearched;  
		}
		 /*
		 * Method Name   :    fetchMemberId
		 * Description   :    Fetches Member account and updates the account map, inactivemember and InactiveMemberVisitorAccount
		 * Return Type   :    void
		 * Parameters    :    New Barcode value.

		 */
	Private  Static void fetchMemberId(String barcodeValue,Boolean isMemberSearchFlow) {
			Boolean isInactiveMember = false;
			Map<String, String> accountNameMap = new Map<String, String>();
			Account oInactiveMemberAcc;
			Boolean isActivePolicyExists=false;
			List<Member_Id__c> memberResults = fetchMemberResults(barcodeValue);              
			Map<String,String> mpOfMemIdToPolicy = new Map<String,String>();        
			if(memberResults.size() > 0) 
			{    
				for(Member_Id__c member : memberResults) 
				{	
					if(sCacheinteractionDate.contains('/')){
						Date dt = date.parse(sCacheinteractionDate);
						sCacheinteractionDate = String.valueOf(dt);
					}
					Date checkInDate = Date.valueOf(sCacheinteractionDate);
					Date validityDate = date.newInstance(checkInDate.year()-1,01,01);  
					Date dPolicyEndDate;
					Date dPolicyEffDate;
					Integer showFutureDateRange;
					if((isMemberSearchFlow && !mpOfMemIdToPolicy.containsKey(member.Policy_Member__r.Member__r.Id)) || !isMemberSearchFlow){
						if(String.isNotBlank(member.Policy_Member__r.End_Date__c) && member.Policy_Member__r.End_Date__c != GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_INVALIDENDDATE) {
							dPolicyEndDate  = Date.parse(member.Policy_Member__r.End_Date__c);
						} 
						if(String.isNotBlank(member.Policy_Member__r.Effective_Date__c)) {
							dPolicyEffDate  = Date.parse(member.Policy_Member__r.Effective_Date__c);
							showFutureDateRange = Integer.valueOf(System.Label.CRMRetail_Future_Policy_Results_Date_Range);
						}                    
						Boolean activeOrFutureCheck = HUMUtilityHelper.isCRMFunctionalityON('5225414') ? (member.Policy_Member__r.Status__c != GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TERMED) : (member.Policy_Member__r.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE);                    

						if (activeOrFutureCheck && member.Policy_Member__r.ETL_Record_Deleted__c == false && member.Policy_Member__r.Member__r.ETL_Record_Deleted__c == false)
						{                             

							if(isMemberSearchFlow){
								mpOfMemIdToPolicy.put(member.Policy_Member__r.Member__r.Id,member.Policy_Member__c);
								CRMRetail_SearchResults_DTO_HUM acctObj = prepareAccountSearchResults(member.Policy_Member__r.Member__r);
								acctObj.visitorId = member.Name;
								acctObj.inactiveCheck = false;                            
								if(HUMUtilityHelper.isCRMFunctionalityON('5225414')){
									if(member.Policy_Member__r.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FUTURE_STATUS && dPolicyEffDate > system.today() && dPolicyEffDate <= System.today()+showFutureDateRange){ 
										acctObj.isFutureMember = true;    
									}
									else if(member.Policy_Member__r.Status__c != GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE){
										continue;
									}    
								}                            

								lstOfMemberSearched.add(acctObj);                            
							}
							else{ 
								if(HUMUtilityHelper.isCRMFunctionalityON('5225414')){
									if(member.Policy_Member__r.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FUTURE_STATUS && dPolicyEffDate > system.today() && dPolicyEffDate <= System.today()+showFutureDateRange){
										returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISFUTUREMEMBER,'true');	
										break;
									}
									else if(member.Policy_Member__r.Status__c != GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE){
										break;
									}
								}

								accountNameMap.put(member.Policy_Member__r.Member__r.Id, member.Policy_Member__r.Member__r.Name);
								oInactiveMemberAcc=null; 
								isActivePolicyExists=true;                            
								isInactiveMember=false;                            

								returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSOFTDELETED,'false');
								break;
							}                        
						}					

						else if((!isActivePolicyExists || isMemberSearchFlow) && member.Policy_Member__r.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TERMED && (dPolicyEndDate != null && dPolicyEndDate>=validityDate && dPolicyEndDate<System.today()) && member.Policy_Member__r.ETL_Record_Deleted__c == false && member.Policy_Member__r.Member__r.ETL_Record_Deleted__c == false){                                                                                  
							if(isMemberSearchFlow){
								mpOfMemIdToPolicy.put(member.Policy_Member__r.Member__r.Id,member.Policy_Member__c);
								CRMRetail_SearchResults_DTO_HUM acctObj = prepareAccountSearchResults(member.Policy_Member__r.Member__r);                            
								acctObj.inactiveCheck = true;
								acctObj.visitorId = member.Name;
								lstOfMemberSearched.add(acctObj);                            
							}
							else{     
								oInactiveMemberAcc = member.Policy_Member__r.Member__r;
								isInactiveMember=true;					
								returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSOFTDELETED,'false');
							}
						}
						else 
						{                    
							if(!isInactiveMember && !isMemberSearchFlow){
								returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSOFTDELETED,'true;'+member.Policy_Member__r.Member__r.Name);
								oInactiveMemberAcc = member.Policy_Member__r.Member__r;
							}                          
						}                    
					}				
				}
			}
			if(isMemberSearchFlow && lstOfMemberSearched != NULL && lstOfMemberSearched.size() > 0){
				lstOfMemberSearched = getSimilarNonMemberRecords(lstOfMemberSearched);           
			}             
			if(oInactiveMemberAcc != null && !isMemberSearchFlow){
				String sResultNonMemSearch = fetchNonMemberAccOfInactiveMember(oInactiveMemberAcc);
				String sInactiveMemberAccDetails = JSON.serialize(oInactiveMemberAcc);
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INACTIVEMEMBER,sInactiveMemberAccDetails);
				if(sResultNonMemSearch !=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NORECORDFOUND){
					String sNonMemberResult = sResultNonMemSearch;
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INACTIVEMEMBERVISITORACCOUNT,sNonMemberResult);
				}     
			}
			if(accountNameMap.size() > 0 && !isMemberSearchFlow) {
				processInteractions(accountNameMap,'',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTDATE);
			}
			else {
				String noMatchError = (sCacheCurentDevice == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FORMFACTOR_MEDIUM || sCacheCurentDevice == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FORMFACTOR_SMALL)  ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOMATCHERROR_MOBILE : GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOMATCHERROR;
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR,noMatchError);
			}  
		}    
		Private Static void fetchVisitorId(String barcodeValue) 
		{     
			Map<String, String> accountNameMap = new Map<String, String>();
			List<Account> visitorList = [Select Id, Name, RecordType.Name, ETL_Record_Deleted__c,Waiver_Date__c,Veteran_Status__c,CRMRetailNotificationOptOut__c from Account where GCM_Visitor_Barcode__c = :barcodeValue LIMIT 1];
			if(visitorList.size() > 0) {
				for(Account visitor : visitorList) {
					accountNameMap.put(visitor.Id, visitor.Name);
					if(visitor.ETL_Record_Deleted__c)
					{
						returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSOFTDELETED,'true;'+visitor.Name);
					}
				}
			}  
			if(accountNameMap.size() > 0) {
				 processInteractions(accountNameMap,'',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTDATE);
			}
			else {
				String noMatchError = (sCacheCurentDevice == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FORMFACTOR_MEDIUM || sCacheCurentDevice == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FORMFACTOR_SMALL)  ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOMATCHERROR_MOBILE : GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOMATCHERROR;
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR,noMatchError);
			}
		}
		
		public Static List<Member_Id__c> fetchMemberResults(String barcodeValue)
		{
			integer iSearchLimit = GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_MEMBER_SEARCH_LIMIT;
			Id oMemberRecordType = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();          
			List<String> setOfRecordTYpe = new List<String>{'\''+oMemberRecordType+'\''};
			string searchQuery = 'FIND \'*' + String.escapeSingleQuotes(barcodeValue) 
				+ '*\' IN NAME FIELDS RETURNING Member_Id__c(Name, Policy_Member__r.Member__r.Id, ' 
				+ 'Policy_Member__r.Member__r.RecordType.Name, '
				+ 'Policy_Member__r.Member__r.ETL_Record_Deleted__c, '
				+ 'Policy_Member__r.ETL_Record_Deleted__c, '
				+ 'Policy_Member__r.Status__c, '
				+ 'Policy_Member__r.End_date__c, '
				+ 'Policy_Member__r.Effective_date__c, '
				+ 'Policy_Member__r.Member__r.Name, '
				+ 'Policy_Member__r.Member__r.CRMRetail_PreferredName__c, '
				+ 'Policy_Member__r.Member__r.FirstName, '
				+ 'Policy_Member__r.Member__r.LastName, '
				+ 'Policy_Member__r.Member__r.Birthdate__c, '
				+ 'Policy_Member__r.Member__r.PersonBirthdate, '
				+ 'Policy_Member__r.Member__r.PersonHomePhone, '
				+ 'Policy_Member__r.Member__r.Gender__c, '
				+ 'Policy_Member__r.Member__r.PersonMailingStreet, '
				+ 'Policy_Member__r.Member__r.PersonMailingCity, '
				+ 'Policy_Member__r.Member__r.PersonMailingState, '
				+ 'Policy_Member__r.Member__r.PersonMailingPostalCode, '
				+ 'Policy_Member__r.Member__r.Waiver_Date__c, '
				+ 'Policy_Member__r.Member__r.CRMRetailNotificationOptOut__c, '
				+ 'Policy_Member__r.Member__r.Veteran_Status__c '
				+ 'Where Policy_Member__c != NULL AND '
				+ 'Policy_Member__r.Member__r.RecordtypeId IN ' + setOfRecordTYpe 
				+ 'ORDER BY Policy_Member__r.Status__c LIMIT ' + iSearchLimit + ')';                  
			List<List<Member_Id__c>> memberResults = new List<List<Member_Id__c>>();
			memberResults = search.query(searchQuery);
			return memberResults[0];
		}
		
		private static String fetchNonMemberAccOfInactiveMember(Account acc)
		{
			String sResult='';
			List<Account> lstNonMember = new List<Account>();
			Map<Id,Account> mapOfExistingNonMemAccounts = new Map<Id,Account>();
			Map<Id,Account> mapOfAccounts = new Map<Id,Account>();
			String sName = acc.firstname + ' ' + acc.lastname;
			String accQuery = 'FIND \'' + String.escapeSinglequotes(sName) +'\' IN NAME FIELDS RETURNING Account(Id, Name,FirstName,LastName,Birthdate__c,Gender__c, PersonHomePhone, PersonMailingStreet, PersonMailingCity, PersonMailingPostalCode, Recordtype.name, PersonMailingState,ParentId ' +
				+ ' Where RecordType.Name = \''+ GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR +'\' AND PersonMailingPostalCode=\''+acc.PersonMailingPostalCode+'\')';
			List<List<Sobject>> lstNonMemberResult = search.query(accQuery);
			lstNonMember = (List<Account>) lstNonMemberResult[0];
			if(lstNonMember != null && lstNonMember.size()>0){
				for(Account objAcc : lstNonMember){
					if(objAcc.FirstName == acc.firstname && objAcc.LastName == acc.lastname && objAcc.Birthdate__c == acc.Birthdate__c){
						mapOfExistingNonMemAccounts.put(objAcc.Id,objAcc);
					}
				}
				if(!mapOfExistingNonMemAccounts.isEmpty()){
					Id randomAccId = (new List<Id>(mapOfExistingNonMemAccounts.keyset()))[0];
					if(mapOfExistingNonMemAccounts.size() > 1)
					{
						for(Storefront_Interaction__c objInt : [Select id,Account__c,inactiveMember__c, createddate from Storefront_Interaction__c where Account__c=:mapOfExistingNonMemAccounts.keyset() order by createddate desc limit 1])
						{                        
							sResult = JSON.serialize(mapOfExistingNonMemAccounts.get(objInt.Account__c));
						}
						if(String.isBlank(sResult)){
							sResult = JSON.serialize(mapOfExistingNonMemAccounts.get(randomAccId));
						}
					}
					else{
						sResult = JSON.serialize(mapOfExistingNonMemAccounts.get(randomAccId));
					}
				}
				else{
					sResult=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NORECORDFOUND;
				}			
			}
			else{
				sResult=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NORECORDFOUND;
			}
			return sResult;
		}
		
		@AuraEnabled
		public Static Map<String,String> initiateVisitorCheckIn(Map<String, String> mapOfAccIdName,String checkInType) {
			Boolean isAttendanceSwitch = switch_Attendance;
			Boolean isMaxReached = false;        
			if(isAttendanceSwitch && checkInType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKIN_TYPE)
			{                        
				isMaxReached = deduceMaxVisitorLimit();
			}
			if(!isMaxReached)
			{
				if(CRMRetail_Contacted_HUM.isCalledfromContacted){
					processInteractions(mapOfAccIdName,checkInType,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTDATE);    
				}else{
					processInteractions(mapOfAccIdName,checkInType,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DISPLAYDATE);    
				}
			}
			else
			{
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISMAXREACHED,String.valueOf(isMaxReached));
			}
			return returnMap;
		}
		private Static void processInteractions(Map<String, String> mapOfAccIdName,String checkInType,String dateOrigin) {        
			Boolean isSuccess = false;
			Map<String, String> mapOfAccIdIntReasonId = new Map<String, String>();    	
			Map<String, Map<String,String>> mpOfAccIdtoIntDetails = new Map<String, Map<String,String>>();
			String category;
			mapOfAccIdIntReasonId = deduceFirstTimeVisitor(mapOfAccIdName.keyset());         		
			if(mapOfAccIdName.size() > 0) {
				for(String accId : mapOfAccIdName.keyset()) {
					String sVal = mapOfAccIdIntReasonId.get(accId);
					List<String> lstInt = sVal.split('&&');                
					mpOfAccIdtoIntDetails.put(accId,new Map<String,String>());
					mpOfAccIdtoIntDetails.get(accId).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ACCNAME,accId);
					mpOfAccIdtoIntDetails.get(accId).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTREASON,lstInt[1]);
					mpOfAccIdtoIntDetails.get(accId).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INTREASONNAME,lstInt[0]);
					if(String.isNotBlank(checkInType)){
					   category = (checkInType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKIN_TYPE)? '' : GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL; 
					}
					mpOfAccIdtoIntDetails.get(accId).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY,category);                                
				}
			}
			if(mpOfAccIdtoIntDetails.size() > 0) {
				insertInteractions(mpOfAccIdtoIntDetails,dateOrigin);
			}
			else {
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_REASONERROR);
			}
		}
		
		Public Static Map<String, String> deduceFirstTimeVisitor(Set<String> accIds) {                             
			Boolean isFirstTime = false;
			Map<String, String> mapOfAccIdIntReasonId = new Map<String, String>();  
			Map<String,String> mapOfCheckInTypeReasonToId=new Map<String,String>();
			for(Storefront_I_Reason__c objIntReason :[SELECT Id,Name FROM Storefront_I_Reason__c WHERE (Name =:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN OR Name =:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN)] ){
				mapOfCheckInTypeReasonToId.put(objIntReason.Name, objIntReason.Id);
			}		 
			List<Storefront_Interaction__c> intList = [Select Id, Account__c,Reason__c,location__r.Name,Interaction_Date_Only__c,Interaction_Date__c from Storefront_Interaction__c where Account__c in :accIds and (Reason__r.Name =:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN OR Reason__r.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN) order by createddate DESC];
			for(String accId : accIds) {
				Map<String,String> mapOfVisitorCheckInType=new Map<String,String>();
				if(!mapOfReqValforNotificationByAccId.containsKey(accId)){
					mapOfReqValforNotificationByAccId.put(accId,mapOfVisitorCheckInType);                    
				}
				isFirstTime = false;
				if(intList.size() > 0) {
					for(Storefront_Interaction__c gcmI : intList) {
						if(accId == gcmI.Account__c) {
							isFirstTime = true;
							if(mapOfReqValforNotificationByAccId.containsKey(accId)){                                
								Integer offset = UserInfo.getTimezone().getOffset(gcmI.Interaction_Date__c); 
								Datetime interactionDateFormatted = gcmI.Interaction_Date__c.addSeconds(offset/1000);                             
								mapOfReqValforNotificationByAccId.get(accId).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION_NAME_AND_INTER_DATE,gcmI.location__r.Name+'@'+interactionDateFormatted.format(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_DATEFORMAT));
								break;   
							}     
						}
					}
				}
				else {
					isFirstTime = false;
				}
				if(isFirstTime) {
					mapOfAccIdIntReasonId.put(accId, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN + '&&' +mapOfCheckInTypeReasonToId.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN));
					if(mapOfReqValforNotificationByAccId.containsKey(accId)){
						mapOfReqValforNotificationByAccId.get(accId).put( GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN);
					}                   
				}
				else {
					mapOfAccIdIntReasonId.put(accId, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN+'&&'+mapOfCheckInTypeReasonToId.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN));
					if(mapOfReqValforNotificationByAccId.containsKey(accId)){
						mapOfReqValforNotificationByAccId.get(accId).put( GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN);
					}                          
				}
			}        
			return mapOfAccIdIntReasonId;
		}
		/*
		* Method Name   :    fetchStateValue
		* Description   :    Fetches the Dropdown values for State.
		* Return Type   :    List<String>
		* Parameters    :    NA.
		*/
		@AuraEnabled(cacheable=true)
		public static List<String> fetchStateValue()
		{        
			List<String> optionList = new List<String>();
			try {
				List<selectOption> options = new List<selectOption>();
				options = HUMUtilityHelper.getStateFullNameValue();
				for(selectOption option: options) {
					optionList.add(option.getValue());
				}  
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SEARCHVISITOR_C_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FETCHSTATEVALUE);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SEARCHVISITOR_C_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FETCHSTATEVALUE, ex.getMessage());
			}
			return optionList;
		}
		/*
		* Method Name   :    fetchLocationValue
		* Description   :    Fetch Location Value and Interaction Date from Lightning Cache.
		* Return Type   :    Map<String, String>
		* Parameters    :    NA.
		*/
		@AuraEnabled
		Public Static Map<String, String> fetchLocationValue() {
			String locationVal = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NONE;
			String interactionDate = '';
			Map<String, String> interactionCache = new Map<String, String>();    
			try {
				locationVal = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
				locationVal = locationVal.substringAfter('-').trim();
				interactionDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
				interactionCache.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LOCATION, locationVal);
				interactionCache.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTDATE, interactionDate);
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SEARCHVISITOR_C_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FETCHLOCATION);
				locationVal = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CACHERESERT_ERROR;
				throw AuraErrorHandler_C_HUM.createAuraHandledException(null, null, locationVal);
			}
			return interactionCache;
		}
		Private Static void insertInteractions(Map<String, Map<String,String>> mpOfAccIdtoIntDetails,String dateOrigin) {       
			List<Storefront_Interaction__c> gcmInteractionList = new List<Storefront_Interaction__c>();
			List<Storefront_Interaction__c> gcmNewInteractionList = new List<Storefront_Interaction__c>();
			List<Storefront_Notification__C> lstOfNotification = new List<Storefront_Notification__C>();
			gcmInteractionList = interactionsObj(mpOfAccIdtoIntDetails, dateOrigin);
			String sLoc = sCacheLocation.substringAfter('-').trim();
			if(sCacheinteractionDate.contains('/')){
				Date dt = date.parse(sCacheinteractionDate);
				sCacheinteractionDate = String.valueOf(dt);
			}
			List<Storefront_Location__c> lstCurrentLocation = [SELECT Id, Maximum_Occupancy__c,Location_Type__c FROM Storefront_Location__c WHERE Name =: sLoc LIMIT 1];        
			String currLocationId = lstCurrentLocation[0].Id;
			String currLocationType = lstCurrentLocation[0].Location_Type__c;    		
			boolean isDateCheckReq=true;
			Map<String,Object> mpOfVstrIndicators = new Map<String,Object>();
			if(sCacheinteractionDate != null && dateOrigin == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DISPLAYDATE){
				String todayDate = String.valueOf(Date.today());
				isDateCheckReq = (sCacheinteractionDate==todayDate);
			}   		
			if(mapOfReqValforNotificationByAccId.size()>0 && mapOfVistorTypeByAccId.size()>0 && currLocationId!=NULL && isDateCheckReq && currLocationType != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL){                            
				mapOfNotificationsWrapToAccId=new Storefront_Notification_Helper_HUM().generateNotificationData(mapOfReqValforNotificationByAccId, mapOfVistorTypeByAccId, currLocationId,sCacheinteractionDate,mapOfIDToAccount);                                     
			}
			if(gcmInteractionList.size() > 0) 
			{
				if(currLocationType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL){
					lstOfNotification = new Storefront_Notification_Helper_HUM().retrieveListOfNotification(currLocationId);
				}
				for(Storefront_Interaction__c sfInt : gcmInteractionList)
				{                
					sfInt.inactiveMember__c = expiredMemberAccId;                  	
					if(HUMUtilityHelper.isCRMFunctionalityON('4012388') && mapOfNotificationsWrapToAccId != NULL && mapOfNotificationsWrapToAccId.containsKey(sfInt.Account__c) && mapOfNotificationsWrapToAccId.get(sfInt.Account__c) != NULL && mapOfNotificationsWrapToAccId.get(sfInt.Account__c).fstTimeClndrVst){
						mpOfVstrIndicators.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_FRSTTIMEVSTR_TEXT,true);
						if(String.isNotBlank(sfInt.Storefront_Vstr_Indicator__c)){                    	
							mpOfVstrIndicators.putAll((Map<String,Object>)JSON.deserializeUntyped(sfInt.Storefront_Vstr_Indicator__c));                        
						}    
						sfInt.Storefront_Vstr_Indicator__c = GenericHelper_CRMRetail_H_HUM.serializeJSONStructure(mpOfVstrIndicators,System.label.CRMRetail_Vstr_Indicator_Order);                                      
					}
					if(currLocationType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL){                                	
						if(lstOfNotification != NULL && lstOfNotification.size() > 0){
							new Storefront_Notification_Helper_HUM().checkNotificationBox(sfInt,lstOfNotification,mapOfIDToAccount);
						}                    
					}
					else if(Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.containsKey(sfInt.Account__c)){
						sfInt.Veteran__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(sfInt.Account__c).Veteran__c;                    
						sfInt.Aging_In__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(sfInt.Account__c).Aging_In__c;
						sfInt.New_Member__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(sfInt.Account__c).New_Member__c;
						sfInt.SDoh__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(sfInt.Account__c).SDoh__c;
					}
					gcmNewInteractionList.add(sfInt);
					}                     
				}
				if(gcmNewInteractionList.size() > 0) 
				{
					if(CRMRetail_Contacted_HUM.isCalledFromContacted){
						returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INTDETAILS_KEY,JSON.serialize(gcmNewInteractionList));
						if(mapOfNotificationsWrapToAccId != NULL && mapOfNotificationsWrapToAccId.size() > 0){
							returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIF_RECORD,JSON.serialize(mapOfNotificationsWrapToAccId)); 
						}
					}else{
						commitInteractions(gcmNewInteractionList, mapOfNotificationsWrapToAccId, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_HOME);               
					}               
				}
			}
			Public Static List<Storefront_Interaction__c> interactionsObj(Map<String, Map<String,String>> mpOfAccIdtoIntDetails, String dateOrigin) {
			String locationVal = '';
			String locationId = '';
			String category='';
			List<Storefront_Interaction__c> intList = new List<Storefront_Interaction__c>();
			List<String> accIdList = new List<String>();
			Map<String,String> mapOfAccIdVisitorIdandType = new Map<String,String>();
			Map<String, Map<String, String>> resultMap = new Map<String, Map<String, String>>();
			
			Map<Id,Account> accountsById =new Map<Id,Account>();
			Date currentInteractionDate;
			if(CRMRetail_Contacted_HUM.isCalledFromContacted){
				currentInteractionDate= Date.today(); 
				locationId = CRMRetail_Contacted_HUM.currLocationId;
			}
			else{
				currentInteractionDate =  Date.ValueOf(sCacheinteractionDate);
			}  
			string accQuery1 = 'Select Id,FirstName,CRMRetail_PreferredName__c,Enterprise_ID__c,LastName,Birthdate__C,Name,Waiver_Date__c,CRMRetailNotificationOptOut__c,Veteran_Status__c,(SELECT Id, Name,ETL_Record_Deleted__C,Effective_Date__c,status__c,End_Date__c,Member__C,Product__c,Policy__r.Platform__c,Policy__r.Product_Type__c,Policy__r.Product__r.Major_LOB__c,Policy__r.Source_Cust_Cov_Key__c,Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c FROM Policy_Members__r WHERE Member__r.ETL_Record_Deleted__c = false AND ETL_Record_Deleted__c = false ';
			string accQuery2 = 'AND Product__c =';
			string accQuery3 = 'MED';
			string accQuery4 = ' ORDER BY Status__c),GCM_Visitor_Barcode__c,RecordType.Name from Account where Id IN (';
            if(!mpOfAccIdtoIntDetails.isEmpty()){
				List<Account> lstAccs = new List<Account>();
				if(CRMRetail_Contacted_HUM.isCalledFromContacted){
					accQuery1 = + accQuery1+accQuery2+'\''+accQuery3+'\''+accQuery4;				
				}else{
					accQuery1 = + accQuery1+accQuery4;
					}    
				if(string.isNotBlank(accQuery1)){
					Integer count = 0;
					If(mpOfAccIdtoIntDetails.keySet().size() >= 1){
						for(String key : mpOfAccIdtoIntDetails.keySet()) {
							count = count+1;
							if(mpOfAccIdtoIntDetails.keySet().size() == 1){
								accQuery1 = +accQuery1+'\''+key+'\'';
							}
							if(count == 1 && mpOfAccIdtoIntDetails.keySet().size() > 1){
								accQuery1 = + accQuery1+'\''+key+'\''+',';
							}
							if(count != 1 && count < mpOfAccIdtoIntDetails.keySet().size() && mpOfAccIdtoIntDetails.keySet().size() > 1){
								accQuery1 = + accQuery1+'\''+key+'\''+',';
							}
							if(count == mpOfAccIdtoIntDetails.keySet().size() && mpOfAccIdtoIntDetails.keySet().size() > 1){
								accQuery1 = + accQuery1+'\''+key+'\'';
							}
						}
						count = 0;
					}
					accQuery1 = + accQuery1+')';
					lstAccs = Database.query(accQuery1);
					if(!lstAccs.isEmpty()){
						mapOfIdToAccount= new Map<Id,Account>(lstAccs);  
					}
				}
			}
			mapOfAccIdVisitorIdandType=populateInteractionVistIdAndType(mapOfIdToAccount);
			List<CRMRetail_ConsolidatedReq_DTO_HUM> lstServiceReqs = GenericHelper_CRMRetail_H_HUM.formConsolidatedServiceRequest(mapOfIdToAccount.values(),false);
			if(mapOfAccIdVisitorIdandType.size()>0){
				try{
					if(!test.isRunningTest() && lstServiceReqs != null && !lstServiceReqs.isEmpty()){
						CRMRetail_SDoH_Helper_HUM.requestConsolidatedService(lstServiceReqs,false,currentInteractionDate);
					}
				}
				catch(Exception ex){
					HUMExceptionHelper.logErrors(ex,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_INTERACTIONSOBJ);
				}
			}
			for(String key : mpOfAccIdtoIntDetails.keySet()) {
				Storefront_Interaction__c IntObj = new Storefront_Interaction__c();
				Object data = mpOfAccIdtoIntDetails.get(key);
				Map<String, Object> dataMap = (Map<String, Object>) data;
				IntObj.Reason__c = String.valueOf(dataMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTREASON));
				if(!HUMUtilityHelper.isCRMFunctionalityON('4769775') && String.valueOf(dataMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INTREASONNAME)) == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN){
					IntObj.First_Time_Visitor__c = true;
				}
				else{
					IntObj.First_Time_Visitor__c = false;
				}
				IntObj.Location__c = sCacheLocation.split('-', 2)[0];
				IntObj.Interaction_Date__c = getUTCDateTime(dateOrigin);
				IntObj.Account__c = key;  
								
				if(mapOfAccIdVisitorIdandType.size()>0)
				{
					if(mapOfAccIdVisitorIdandType.containsKey(key) && mapOfAccIdVisitorIdandType.get(Key)!=NULL)
					{                                            
						IntObj.Visitor_ID__c= mapOfAccIdVisitorIdandType.get(key).split('&&')[1];
						IntObj.CRM_Retail_Interaction_Visitor_Type__c = mapOfAccIdVisitorIdandType.get(key).split('&&')[0];
						mapOfVistorTypeByAccId.put(key,IntObj.CRM_Retail_Interaction_Visitor_Type__c);
						mapOfReqValforNotificationByAccId.get(key).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITORID,IntObj.Visitor_ID__c);					
						if(IntObj.CRM_Retail_Interaction_Visitor_Type__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER){
							IntObj.Storefront_Vstr_Indicator__c = CRMRetail_SDoH_Helper_HUM.sJSONResponse;                    
							Map<String,Object> mpOfIndicatorToMetricValue = checkVisitorIndicatorEligibility(IntObj.Storefront_Vstr_Indicator__c); 
							if(mpOfIndicatorToMetricValue != NULL && mpOfIndicatorToMetricValue.containsKey(System.Label.CRMRetail_SNP_Text)){
								mapOfReqValforNotificationByAccId.get(key).put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INDICATOR_KEY,mpOfIndicatorToMetricValue.get(System.Label.CRMRetail_SNP_Text).toString());                                
							}                                                         
						}
					}                                   
				}
				if(dataMap.CONTAINSKEY(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY)){                              
					IntObj.Category__c = String.valueOf(dataMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY));                  
					category = IntObj.Category__c;
					if(mapOfReqValforNotificationByAccId.containsKey(key)){    
						mapOfReqValforNotificationByAccId.get(key).put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY,category);
					}
				}
				intList.add(IntObj);
			}
			return intList;
		}
		public static Map<String, String> commitInteractions(List<Storefront_Interaction__c> gcmNewInteractionList, Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId, String origin )  
		{
			Boolean recommendationSwitch = fetchSwitchResults().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_SWITCH);
			List<String> successintMap = new List<String>();
			CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE,gcmNewInteractionList);             
			if(accessbilityObj.exceptionGenerated)
			{             
				HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COMMITINTERACTION);
				Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COMMITINTERACTION, accessbilityObj.exceptionToLog.getMessage());
			}
			else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
			{
			Database.SaveResult[] lstSaveResult = Database.insert(gcmNewInteractionList,false);
			String sError='';
			Map<String, String> dupMap = new Map<String, String>();
			String recommendationDuplicateText =GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATIONDUPLICATE_TEXT;
			for (Database.SaveResult validrec : lstSaveResult)
			{              
				if(validrec.isSuccess()){                
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS,'true');
					String notifDetails = (mapOfNotificationsWrapToAccId != NULL && mapOfNotificationsWrapToAccId.size() > 0) ? JSON.serialize(mapOfNotificationsWrapToAccId.values()) : '[]';
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIFICATION_RECORDS, notifDetails);
				if(recommendationSwitch!=null && recommendationSwitch==true)
					successintMap.add(validrec.getId());
				}
				else{
						for(Database.Error err : validrec.getErrors())
						{                    
							sError =  err.getMessage();
							if(sError.containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY)){
								if(origin == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_HOME){
									returnMap.put(System.Label.CRMRetail_Duplicate_HUM,'true;'+sError.split(':', 2)[1]);
									returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS,'false');
								}else if(origin == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_EVENT_INTERACTIONS || origin == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION && !sError.containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECK_IN)){
									sError = err.getMessage().split(':',2)[1];
									String memberId = err.getMessage().split(':',2)[0].split('-',2)[1];
									String reasonName = sError.split(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ALREADY_ASSIGNED_TEXT,2)[1];
									if(dupMap.containsKey(memberId)){
										String duplicateText = dupMap.get(memberId);
										if(!duplicateText.contains(reasonName)){
											duplicateText = duplicateText + ', ' + reasonName;
										}
										dupMap.put(memberId,duplicateText);
									}
									else if (origin == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION)
									{
										dupMap.put(memberId,recommendationDuplicateText+reasonName);
									}
									else{
										dupMap.put(memberId,sError);
									}
									if(returnMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS) != 'true'){
										returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS,'false');
									}
								}
							}else if(String.valueOf(err.getStatusCode()).contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CUSTOM_VALIDATION_EXCEPTION)){
								returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR,sError);
							}else{
								HUMExceptionHelper.logError(sError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION,'','');
								Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEDEFLOCATION, sError);
							}
						}
						if(dupMap.size() > 0){
							returnMap.put(System.Label.CRMRetail_Duplicate_HUM,JSON.serialize(dupMap.Values()));
						}
					}
				}           
				if(recommendationSwitch!=null && recommendationSwitch==true && returnMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TRUE)
					returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUCCESSLIST,JSON.serialize(successintMap));
			}            
			else
			{
				String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SINTERACTION;
				String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
				String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
				HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COMMITINTERACTION,'','');
				Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COMMITINTERACTION, logError);
			} 
			return returnMap;
		}
		public static Map<String,String> populateInteractionVistIdAndType(Map<Id,Account> mapOfIdToAccount)
		{ 
			List<Account> listOfNonMemberAccount = new List<Account>();
			Map<String,String> mapOfAccVisitorTypeId = new Map<String,String>();       
			List<Policy_Member__c> listOfPolicyMembers=new List<Policy_Member__c>();
			if(mapOfIdToAccount.size()>0)
			{  
				for(Account accRef : mapOfIdToAccount.values()){
					if(accRef.RecordType.Name != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER){
						listOfNonMemberAccount.add(accRef);
					}
					else if(accRef.Policy_Members__r.size()>0){
						listOfPolicyMembers.addAll(accRef.Policy_Members__r);
					}else{   
						mapOfAccIdToEnterpriseId.put(accRef.Enterprise_ID__c,accRef.Id);
						mapOfAccVisitorTypeId.put(accRef.Id, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_AMPERSAND+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_OTHER);                    
					}
				}             
				if(listOfPolicyMembers.size()>0)
				{      
					String inctvPolDt;
					String finalVisitorId;
					for(Policy_Member__c plyMemRef : listOfPolicyMembers)
					{         
						if(plyMemRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE){
							Date effDate = Date.parse(plyMemRef.Effective_Date__c); 
							Date endDate;
							String tempEndDate = plyMemRef.End_Date__c;
							if(String.isNotBlank(tempEndDate)){
								List<String> splitedDate = tempEndDate.split('/');                    
								endDate = Date.newInstance(Integer.valueOf(splitedDate[2]),Integer.valueOf(splitedDate[0]),Integer.valueOf(splitedDate[1]));
							}                        
							if(plyMemRef.Product__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_POLICY_PRODUCT_TYPE && plyMemRef.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && effDate <= Date.today() && (endDate > Date.today() || endDate==NULL)){                            
								finalVisitorId = plyMemRef.Name.Substring(0,plyMemRef.Name.length()-2);
								mapOfAccVisitorTypeId.put(plyMemRef.Member__c, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_AMPERSAND+finalVisitorId);
								mapOfAccIdToEnterpriseId.put(plyMemRef.Member__r.Enterprise_ID__c,plyMemRef.Member__c);                             
								break;
							}
							else{
								 mapOfAccVisitorTypeId.put(plyMemRef.Member__c, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_AMPERSAND+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_OTHER);                    
								 mapOfAccIdToEnterpriseId.put(plyMemRef.Member__r.Enterprise_ID__c,plyMemRef.Member__c);
							}                         
						}
						else if(CRMRetail_Contacted_HUM.isCalledFromContacted){
							if(plyMemRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FUTURE_STATUS){	
								finalVisitorId = plyMemRef.Name.Substring(0,plyMemRef.Name.length()-2);
								mapOfAccVisitorTypeId.put(plyMemRef.Member__c, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_AMPERSAND+finalVisitorId);
								mapOfAccIdToEnterpriseId.put(plyMemRef.Member__r.Enterprise_ID__c,plyMemRef.Member__c); 
								break;
							}
							if(plyMemRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TERMED){                        
								Date prevEndDate = String.isNotBlank(inctvPolDt) ? Date.parse(inctvPolDt) : null;
								if(String.isBlank(inctvPolDt) || (prevEndDate != NULL && prevEndDate < Date.parse(plyMemRef.End_Date__c))){                            
									inctvPolDt = plyMemRef.End_Date__c;		
									finalVisitorId = plyMemRef.Name.Substring(0,plyMemRef.Name.length()-2);                             
									mapOfAccIdToEnterpriseId.put(plyMemRef.Member__r.Enterprise_ID__c,plyMemRef.Member__c);
									mapOfAccVisitorTypeId.put(plyMemRef.Member__c, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_OTHER+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_AMPERSAND+finalVisitorId);
								}else{
									continue;
								}                              
							}
						}
					}
					
				} 
				if(listOfNonMemberAccount.size()>0){                
					for(Account acc : listOfNonMemberAccount) {  
						mapOfAccVisitorTypeId.put(acc.Id, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_AMPERSAND+acc.GCM_Visitor_Barcode__C);                    
					}
				}
			}		    
			return mapOfAccVisitorTypeId;        
		}     
		/*
		* Method Name   :    getUTCDateTime
		* Description   :    Generate the Current Date or Interaction Date based on Click Origin.
		* Return Type   :    DateTime
		* Parameters    :    Name of the Place from which the CheckIn btn was clicked.
		*/
		Public Static DateTime getUTCDateTime(String dateOrigin){
			Datetime GMTDate = null;
			DateTime currDateTime = DateTime.now();
			Integer hours = currDateTime.hour(), minutes = currDateTime.minute(), seconds = currDateTime.second();
			String currInteractionDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
			Switch on dateOrigin {
				when 'displayDate' {
					List<String> dateString;
					Integer year, month, day;
					if(currInteractionDate.contains('/')) {
						dateString = currInteractionDate.split('/', 3);
						year = Integer.valueOf(dateString[2]);
						month = Integer.valueOf(dateString[0]);
						day = Integer.valueOf(dateString[1]);
					}
					else {                        
						dateString = currInteractionDate.split('-', 3);
						year = Integer.valueOf(dateString[0]);
						month = Integer.valueOf(dateString[1]);
						day = Integer.valueOf(dateString[2]);                                              	
					}
					GMTDate = Datetime.newInstance(year, month, day, hours, minutes, seconds);
				}
				when 'currentDate' {
					GMTDate = Datetime.newInstance(currDateTime.year(), currDateTime.month(), 
												   currDateTime.day(), currDateTime.hour(), 
												   currDateTime.minute(), currDateTime.second()); 
				}
			}
			return GMTDate;
		}
		/*
		* Method Name   :    checkVisitorIndicatorEligibility
		* Description   :    Conditionally checks the eligibility for Special Needs plan for a member
		* Return Type   :    Map<String,Object>
		* Parameters    :    The indicators that are applicable for the user
		*/
		public Static Map<String,Object> checkVisitorIndicatorEligibility(String interactionIndicator){                         
			Map<String,Object> mpOfIndicatorToMetricValue = new Map<String,Object>();         
			if(String.isNotBlank(interactionIndicator)){                    
				mpOfIndicatorToMetricValue = (Map<String,Object>)JSON.deserializeUntyped(interactionIndicator);             
			}        
			return mpOfIndicatorToMetricValue;
		} 
		@AuraEnabled
		public static Map<String, String> processInactiveMemberCheckin(String accountId,String accName,String expiredMemAccId,String checkInType,String dateOrigin)
		{ 
			Boolean isAttendanceSwitch = fetchSwitchResults().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SWITH_ATTENDANCE_KEY);
			Boolean isMaxReached = false;    
			Map<String, String> accountNameMap = new Map<String, String>();
			isInactiveMemberCheckin = true;
			if(String.isNotBlank(expiredMemAccId)){
				expiredMemberAccId = Id.valueOf(expiredMemAccId);
			} 
			accountNameMap.put(accountId,accName);
			if(isAttendanceSwitch && checkInType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKIN_TYPE)
			{                        
				isMaxReached = deduceMaxVisitorLimit();
			}
			if(!isMaxReached)
			{
				processInteractions(accountNameMap,checkInType,dateOrigin); 
			}
			else
			{
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISMAXREACHED,String.valueOf(isMaxReached));
			}
			
			return returnMap;
		}
		
		 @AuraEnabled
		Public Static Map<String, String> processNewCheckIn(String barcodeValue, Account newAccount) {
			try {
				if(String.isNotBlank(newAccount.ParentId)){
					expiredMemberAccId = Id.valueOf(newAccount.ParentId);
					newAccount.ParentId=null;
				}
				saveBarcode(barcodeValue, newAccount);
				if(returnMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STATUS) == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SUCCESS ) {
					processCheckIn(barcodeValue);                                
				}           
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_PROCESSNEWCHECKIN_METHOD);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, 
																		GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_PROCESSNEWCHECKIN_METHOD, ex.getMessage());
			}
			return returnMap;
		}
		
		Private Static void saveBarcode(String barcodeValue, Account newAccount) {
			Account emptyObj = new Account();
			Boolean isSuccess = false;
			if(emptyObj !== newAccount) {
				newAccount.GCM_Visitor_Barcode__c = barcodeValue;
						
				CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, new List<Account>{newAccount});             
				if(accessbilityObj.exceptionGenerated)
				{   
					HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEBARCODE);
					Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEBARCODE, accessbilityObj.exceptionToLog.getMessage());
				}
				else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
				{
					insert newAccount;
					isSuccess = true;
				}            
				else
				{
					String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT;
					String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
					String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
					HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEBARCODE,'','');
					Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SAVEBARCODE, logError);
				}
			}
			else {
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEWVISITORERROR);
			}
			returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STATUS, ((isSuccess) ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SUCCESS : GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FAILURE));
		}
		
		@AuraEnabled
		Public Static Map<String, String> updateBarcodeCheckIn(String barcodeValue, String accountId) {
			try {
				updateBarcode(barcodeValue, accountId);
				if(returnMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STATUS) == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SUCCESS ) {
					processCheckIn(barcodeValue);
				}
			}
			catch(Exception ex) {
				HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_UPDATEBARCODE);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, 
																		GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_UPDATEBARCODE, ex.getMessage());
			}
			return returnMap;
		}
		Private Static void updateBarcode(String barcodeValue, String accountId) {
			Boolean isSuccess = false;
			Account accObj = [Select Id, GCM_Visitor_Barcode__c from Account where Id = :accountId LIMIT 1];
			Account emptyObj = new Account();
			if(accObj != emptyObj) {
				accObj.GCM_Visitor_Barcode__c = barcodeValue;
						 
				CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.UPDATABLE, new List<Account>{accObj});             
				 
				if(accessbilityObj.exceptionGenerated)
					{   
						
						HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_UPDATEBARCODE_METHOD);
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_UPDATEBARCODE_METHOD, accessbilityObj.exceptionToLog.getMessage());
					}
					else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
					{
							update accObj;            
						isSuccess = true;
					}            
					else
					{
						String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT;
						String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
						String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
					   
						HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_UPDATEBARCODE_METHOD,'','');
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_UPDATEBARCODE_METHOD, logError);
					}
			}
			else {
				returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EMPTYACCOUNT_ERRMSG);
			}
			returnMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STATUS, ((isSuccess) ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SUCCESS : GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FAILURE));
		}  
		
		/*
		* Method Name   :    runProcessAccountSearchLogic
		* Description   :    method to process the search logic with Account informations
		* Return Type   :    List<CRMRetail_SearchResults_DTO_HUM>
		* Parameters    :    String inputList
		*/    
		@AuraEnabled
		public static List<CRMRetail_SearchResults_DTO_HUM> runProcessAccountSearchLogic(String inputList,Boolean singleFieldSearch)
		{      
			List<CRMRetail_SearchResults_DTO_HUM> searchResult = new List<CRMRetail_SearchResults_DTO_HUM>();
			Map<String,Object> mpOfSearchCriteria = new Map<String,Object>();
			mpOfSearchCriteria = (Map<String, Object>) JSON.deserializeUntyped(String.escapeSingleQuotes(inputList));
			if(singleFieldSearch)
			{
				if(mpOfSearchCriteria.containsKey(System.label.CRMRetail_Person_HomePhone_Key))
				{
					searchResult = runProcessWithPersonPhone(String.valueOf(mpOfSearchCriteria.get(System.label.CRMRetail_Person_HomePhone_Key)));
				}
			}
			else{
				String searchQuery = generateAccountSearchQueryLogic(mpOfSearchCriteria);
				List<List<Account>> lstOfaccounts= search.query(searchQuery);
				
				if(lstOfaccounts.size()>0 && lstOfaccounts[0].size()>0)
				{
					if(HUMUtilityHelper.isCRMFunctionalityON('5132829'))
					{
						List<Account> filteredAcc = new List<Account>();

						for(Account acc : lstOfaccounts[0])
						{
							String prefName = acc.CRMRetail_PreferredName__c != null ? acc.CRMRetail_PreferredName__c.toLowerCase() : '';
							String SearchValue = String.valueOf(mpOfSearchCriteria.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTNAME)).toLowerCase();
							String firstName = acc.FirstName != null ? acc.FirstName.toLowerCase() : '';
							
							if( prefName.contains(SearchValue) || firstName.contains(SearchValue))
							{
								filteredAcc.add(acc);
							}
						}

						lstOfaccounts[0] = filteredAcc;
					}
									
					if(lstOfaccounts[0].size()>0)
					{
						searchResult = processWithFetchedAccounts(lstOfaccounts[0]);
					}
				}
			}
			
			return searchResult;
			
		}  
		
		/*
		 * Method Name   :    runProcessWithPersonPhone
		* Description   :    method to process the search logic with personHomephone
		* Return Type   :    List<CRMRetail_SearchResults_DTO_HUM>
		* Parameters    :    String phone
		*/
		@AuraEnabled
		public static List<CRMRetail_SearchResults_DTO_HUM> runProcessWithPersonPhone(String phone)
		{            
			List<CRMRetail_SearchResults_DTO_HUM> searchResult = new List<CRMRetail_SearchResults_DTO_HUM>();
			if(String.isNotBlank(phone))
			{
				String fPhone = '(' + phone.substring(0, 3) + ') ' + phone.substring(3, 6) + '-' + phone.substring(6);   
				List<Account> accountList = [select ID, Name,CRMRetail_PreferredName__c, FirstName, LastName, middlename,Individual_First_Name__c,Individual_Last_Name__c,Gender__c, RecordType.Name, PersonBirthdate, Birthdate__c, PersonEmail,PersonMailingStreet,
											 PersonMailingCity, PersonMailingStateCode, PersonMailingState,PersonMailingPostalCode, PersonHomePhone, Home_Office_Account__c, ETL_Record_Deleted__c,RecordtypeId,Waiver_Date__c,
											 CRMRetailNotificationOptOut__c,Veteran_Status__c from account Where (Recordtype.Name =:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR OR Recordtype.Name =:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER) AND ETL_Record_Deleted__c = false AND (PersonHomePhone = :fPhone OR personhomephone =:phone) LIMIT :GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_PERSONPHONE_SEARCH_LIMIT];
			
				searchResult = processWithFetchedAccounts(accountList);
			}
			return searchResult;
		}
		
		/*
		* Method Name   :    processWithFetchedAccounts
		* Description   :    method to generate response data with accounts
		* Return Type   :    List<CRMRetail_SearchResults_DTO_HUM>
		* Parameters    :    List<Account> lstOfaccounts
		*/
		public static List<CRMRetail_SearchResults_DTO_HUM> processWithFetchedAccounts(List<Account> lstOfaccounts)
		{
			CRMRetail_SearchResults_DTO_HUM acctObj; 
			List<CRMRetail_SearchResults_DTO_HUM> lstOfAccountsRetrieved = new List<CRMRetail_SearchResults_DTO_HUM>();
			List<CRMRetail_SearchResults_DTO_HUM> lstOfMbrAcctsRetrieved = new List<CRMRetail_SearchResults_DTO_HUM>();
			List<CRMRetail_SearchResults_DTO_HUM> lstOfCRMVstrAcctsRetrieved = new List<CRMRetail_SearchResults_DTO_HUM>();
			Map<Id,CRMRetail_SearchResults_DTO_HUM> mpOfTempMbrAccounts = new Map<Id,CRMRetail_SearchResults_DTO_HUM>();
			Map<Id,Policy_Member__c> accIdWithPolMemMap = new Map<Id, Policy_Member__c>();
			Map<Id,String> mapOfAccIdToDateType = new Map<Id,String>();
			Set<Id> setOfActivePolicyAccIds = new Set<Id>();
			Integer previousYear = Date.Today().year()-1;
			Date rangeStartDate = Date.newInstance(previousYear, 01, 01);
			Date dt = GenericHelper_CRMRetail_H_HUM.getCurrentInteractionDate();
			Date rangeEndDate = dt.addDays(-1);
			Date dPolicyEffDate;
			Integer showFutureDateRange;
			
			for(Account acc: lstOfaccounts)
			{
				acctObj = prepareAccountSearchResults(acc);            
				if(acctObj.RecordTypeName == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER){
					mpOfTempMbrAccounts.put(acctObj.Id,acctObj);						
				}
				else if(acctObj.RecordTypeName == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR){
					lstOfCRMVstrAcctsRetrieved.add(acctObj);
				}
			}
		 
			Map<Id,Account> mpOfaccIdsToPolicies = new Map<Id,Account>([SELECT Id,Name,FirstName,LastName,CRMRetail_PreferredName__c,Birthdate__c,PersonHomePhone,PersonMailingState,Gender__c,PersonMailingPostalCode,PersonBirthdate,RecordTypeId,Waiver_Date__c,recordType.Name,Veteran_Status__c,CRMRetailNotificationOptOut__c,GCM_Visitor_Barcode__c,(SELECT Id,Name,Status__c, ETL_Record_Deleted__c,End_Date__c, Effective_Date__c FROM Policy_Members__r where ETL_Record_Deleted__c=false ORDER BY Status__c) FROM Account WHERE Id in :mpOfTempMbrAccounts.keyset()]);
			mpOfTempMbrAccounts.clear();
			for(Id key : mpOfaccIdsToPolicies.keySet()){
				if(mpOfaccIdsToPolicies.get(key).Policy_Members__r.size() > 0){                
					mapOfAccIdToDateType.put(mpOfaccIdsToPolicies.get(key).Id, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_END_DATE);
					for(Policy_Member__c pMemRef: mpOfaccIdsToPolicies.get(key).Policy_Members__r){
						if(String.isBlank(pMemRef.End_Date__c) || pMemRef.End_Date__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_INVALIDENDDATE){
							mapOfAccIdToDateType.put(mpOfaccIdsToPolicies.get(key).Id, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_EFFECTIVE_DATE);
							break;
						}                     
					}                
					acctObj = prepareAccountSearchResults(mpOfaccIdsToPolicies.get(key));                
					for(Policy_Member__c pMemRef: mpOfaccIdsToPolicies.get(key).Policy_Members__r){
						if(String.isNotBlank(pMemRef.Effective_Date__c)) {
							dPolicyEffDate  = Date.parse(pMemRef.Effective_Date__c);
							showFutureDateRange = Integer.valueOf(System.Label.CRMRetail_Future_Policy_Results_Date_Range);
						}
						if(!setOfActivePolicyAccIds.contains(key) && pMemRef.Status__c != NULL){
							if(pMemRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE){                    
								setOfActivePolicyAccIds.add(key);
								mpOfTempMbrAccounts.put(key,acctObj);
								mpOfTempMbrAccounts.get(key).inactiveCheck = false;
							}
							else if(HUMUtilityHelper.isCRMFunctionalityON('5225414') && pMemRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FUTURE_STATUS && dPolicyEffDate != NULL && showFutureDateRange != NULL && dPolicyEffDate > system.today() && dPolicyEffDate <= System.today()+showFutureDateRange){ 
								setOfActivePolicyAccIds.add(key);
								mpOfTempMbrAccounts.put(key,acctObj);
								mpOfTempMbrAccounts.get(key).isFutureMember = true;	    
							}
							else if(pMemRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TERMED && Date.parse(pMemRef.End_Date__c) >= rangeStartDate && Date.parse(pMemRef.End_Date__c) <= rangeEndDate){                    
								mpOfTempMbrAccounts.put(key,acctObj);
								mpOfTempMbrAccounts.get(key).inactiveCheck = true;
							}
							else{                    
								continue;
							}
						}
						if(accIdWithPolMemMap.containsKey(key)){
							if(pMemRef.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && !accIdWithPolMemMap.get(key).Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH)){
								accIdWithPolMemMap.put(key, pMemRef);
								mpOfTempMbrAccounts.get(key).visitorId = pMemRef.Name.Substring(0,pMemRef.Name.length()-2);
							}else if((!pMemRef.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && !accIdWithPolMemMap.get(key).Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH)) || (pMemRef.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && accIdWithPolMemMap.get(key).Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH))){
								Date currentDate;
								Date previousDate;
								if(mapOfAccIdToDateType.get(key) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_END_DATE){
									currentDate = Date.parse(pMemRef.End_date__c);
									previousDate = Date.parse(accIdWithPolMemMap.get(key).End_date__c);
								}else if(mapOfAccIdToDateType.get(key) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_EFFECTIVE_DATE){
									currentDate = Date.parse(pMemRef.Effective_Date__c);
									previousDate = Date.parse(accIdWithPolMemMap.get(key).Effective_Date__c);
								}
								if((accIdWithPolMemMap.containsKey(key) && accIdWithPolMemMap.get(key).Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TERMED && pMemRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE) || (pMemRef.Status__c == accIdWithPolMemMap.get(key).Status__c && Math.abs(currentDate.daysBetween(Date.today())) < Math.abs(previousDate.daysBetween(Date.today())))){
									accIdWithPolMemMap.put(key, pMemRef);
									mpOfTempMbrAccounts.get(key).visitorId = pMemRef.Name.Substring(0,pMemRef.Name.length()-2);
								}
							}
						}else{
							accIdWithPolMemMap.put(key,pMemRef);
							mpOfTempMbrAccounts.get(key).visitorId = pMemRef.Name.Substring(0,pMemRef.Name.length()-2);
						}
					}
				}
			}        
			if(mpOfTempMbrAccounts.size() > 0){            
				for(Id acctObjId : mpOfTempMbrAccounts.keySet()){
					lstOfMbrAcctsRetrieved.add(mpOfTempMbrAccounts.get(acctObjId));
				}
			}
			if(lstOfMbrAcctsRetrieved != NULL && lstOfMbrAcctsRetrieved.size() > 0){
				lstOfMbrAcctsRetrieved = getSimilarNonMemberRecords(lstOfMbrAcctsRetrieved);
				lstOfAccountsRetrieved.addAll(lstOfMbrAcctsRetrieved);            
			}
			if(lstOfCRMVstrAcctsRetrieved != NULL && lstOfCRMVstrAcctsRetrieved.size() > 0){
				lstOfAccountsRetrieved.addAll(lstOfCRMVstrAcctsRetrieved);
			}
			return lstOfAccountsRetrieved;
		}

		
		@testvisible private static List<CRMRetail_SearchResults_DTO_HUM> getSimilarNonMemberRecords(List<CRMRetail_SearchResults_DTO_HUM> accountList){                
			Map<Id,Account> mpOfInactiveIdToAccounts = new Map<Id,Account>();
			Set<Id> setOfIds = new Set<Id>();        
			Set<String> setOfSearchKeys = new Set<String>();         
			String searchKey;
			String nameKey;
			Set<String> setOfInactiveBirthdates = new Set<String>();
			Set<String> setOfInactiveZipcodes = new Set<String>();
			for(CRMRetail_SearchResults_DTO_HUM accIns : accountList){
				if(accIns.inactiveCheck){
					setOfInactiveBirthdates.add(accIns.Birthdate);
					setOfInactiveZipcodes.add(accIns.PersonMailingPostalCode);
					searchKey = '(';
					if(String.isNotBlank(accIns.FirstName) && String.isNotBlank(accIns.LastName))
					{
						searchKey += accIns.FirstName + ' AND ' + accIns.LastName +')';
						setOfSearchKeys.add(searchKey);
					}
					setOfIds.add(accIns.Id);
				}
			}        
			if(setOfSearchKeys != NULL && setOfSearchKeys.size() > 0){            
				nameKey = prepareQueryString(setOfSearchKeys); 		
				if(accountList != NULL && accountList.size() >0){            
					List<Account> lstOfMatchingAccounts = new List<Account>();      
					List<List<Account>> lstResults = new List<List<Account>>(); 
					Set<Id> stOfIds = new Set<Id>();
					mpOfInactiveIdToAccounts = new Map<Id,Account>([SELECT Id,Name,FirstName,LastName,Birthdate__c,Gender__c,PersonMailingPostalCode,PersonMailingStreet,PersonMailingCity,PersonMailingState,PersonHomePhone from Account where Id IN: setOfIds]);                    
					String searchQuery = 'FIND \'' + String.escapeSingleQuotes(nameKey) +'\' IN NAME FIELDS RETURNING Account (Id where RecordType.Name =\''+ GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR +'\' AND Birthdate__c IN : setOfInactiveBirthdates AND PersonMailingPostalCode IN :setOfInactiveZipcodes)';                                               
					lstResults = search.query(searchQuery);                
					for(Account acc: lstResults[0]){
						stOfIds.add(acc.Id);
					}            
					lstOfMatchingAccounts = [SELECT Id,Name,FirstName,LastName,Birthdate__c,PersonMailingPostalCode,PersonMailingState,PersonHomePhone,
											 (SELECT CreatedDate, Account__c from CRM_Retail_Interactions__r ORDER BY CreatedDate DESC LIMIT 1)
											 from Account where Id IN: stOfIds];	               	
					for(CRMRetail_SearchResults_DTO_HUM acc : accountList) {
						if(lstOfMatchingAccounts != NULL && lstOfMatchingAccounts.size() > 0){                        
							if(acc.inactiveCheck){
								for(Account accRef : lstOfMatchingAccounts){                                  
									if(acc.FirstName == accRef.FirstName && acc.LastName == accRef.LastName && acc.Birthdate == accRef.Birthdate__c && acc.PersonMailingPostalCode == accRef.PersonMailingPostalCode){                                                                        
										if(accRef.CRM_Retail_Interactions__r != NULL && accRef.CRM_Retail_Interactions__r.size() > 0){                                        
											if(acc.inactiveMemberVisitorAccount != NULL && acc.recentInteractionDate !=NULL){                                            
												if(accRef.CRM_Retail_Interactions__r.get(0).CreatedDate > acc.recentInteractionDate){                                            
													acc.inactiveMemberVisitorAccount = JSON.serialize(accRef);    
													acc.recentInteractionDate = accRef.CRM_Retail_Interactions__r.get(0).CreatedDate;
												}
											}
											else{                                       
												acc.inactiveMemberVisitorAccount = JSON.serialize(accRef);
												acc.recentInteractionDate = accRef.CRM_Retail_Interactions__r.get(0).CreatedDate;
											}
										}
										else if(acc.recentInteractionDate == NULL){                                        
											acc.inactiveMemberVisitorAccount = JSON.serialize(accRef);       
										}                                  
									}                               
								}                        
							}                         
						}
						acc.inactiveMember = JSON.serialize(mpOfInactiveIdToAccounts.get(acc.Id));                        
					}            
				} 
			}
			return accountList;
		}
		
		private static String generateAccountSearchQueryLogic(Map<String, Object> inputList) {         
			String searchQuery = '';
			List<String> lstRecordTypeName = new List<String>();
			lstRecordTypeName.add(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER);
			lstRecordTypeName.add(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR);

			String firstNameKey = HUMUtilityHelper.isCRMFunctionalityON('5132829') ? '' : inputList.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTNAME) + '* AND *';
			String sNameKey = '*' + firstNameKey + inputList.get('LastName') + '*';
					
			searchQuery = 'FIND \'' + String.escapeSingleQuotes(sNameKey) +'\' IN NAME FIELDS RETURNING Account(ID, Name, FirstName, LastName,CRMRetail_PreferredName__c, middlename,'+
				+ 'Individual_First_Name__c,Individual_Last_Name__c,Gender__c, RecordType.Name, PersonBirthdate, Birthdate__c, PersonEmail,PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
				+ 'PersonMailingPostalCode, PersonHomePhone, Home_Office_Account__c, ETL_Record_Deleted__c,RecordtypeId,Waiver_Date__c,CRMRetailNotificationOptOut__c,Veteran_Status__c' +
				+ ' Where (Recordtype.Name =\''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR+'\' OR Recordtype.Name =\''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+'\') AND ETL_Record_Deleted__c = false';        
			
			for(String key: inputList.keyset()) {
				if(key != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTNAME && key != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_LASTNAME) {
					if(inputList.get(key) != '') {
						String keyValue = String.valueOf(inputList.get(key));
						if(key.toLowerCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_PERSONMAILSTATE) {
							searchQuery = searchQuery + ' AND ' + key + ' = \'' + String.escapeSingleQuotes(keyValue.trim()) + '\'';
						}
						else if(key.toLowerCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_PERSONHOMEPHONE) {
							string fPhone = String.escapeSingleQuotes(keyValue.trim());
							fPhone = '(' + fPhone.substring(0, 3) + ') ' + fPhone.substring(3, 6) + '-' + fPhone.substring(6);                                             
							searchQuery = searchQuery + ' AND (' + key + ' = \'' + String.escapeSingleQuotes(keyValue.trim()) + '\' OR ' + key + ' = \'' + String.escapeSingleQuotes(fPhone) + '\')';    
						}
						else if(key.toLowerCase() == 'personbirthdate' /*'birthdate__c'*/) {                        
							searchQuery = searchQuery + ' AND (' + key + ' = ' + String.escapeSingleQuotes(keyValue.trim()) + ' OR Birthdate__c = \'' + morphDateToString(String.escapeSingleQuotes(keyValue.trim())) + '\')';                        
						}
						else {
							searchQuery = searchQuery + ' AND ' + key + ' like \'%' + String.escapeSingleQuotes(keyValue.trim()) + '%\'';  
						}
					}
				}
			}
			searchQuery = searchQuery + ')';        
			return searchQuery;        
		}
		@testvisible private static CRMRetail_SearchResults_DTO_HUM prepareAccountSearchResults(Account acc){
			CRMRetail_SearchResults_DTO_HUM acctObj = new CRMRetail_SearchResults_DTO_HUM();
			acctObj.Id = acc.Id;
			acctObj.FirstName = acc.FirstName;        
			acctObj.LastName = acc.LastName;        
			acctObj.Name = HUMUtilityHelper.isCRMFunctionalityON('5132829') && acc.CRMRetail_PreferredName__c != null ? acc.Name +' ('+acc.CRMRetail_PreferredName__c+')' : acc.Name;        
			acctObj.PersonMailingState = acc.PersonMailingState;        
			acctObj.PersonMailingPostalCode = acc.PersonMailingPostalCode;        
			acctObj.PersonHomePhone = acc.PersonHomePhone;        
			acctObj.PersonBirthdate = acc.PersonBirthdate;        
			acctObj.Birthdate = acc.Birthdate__c;                        
			acctObj.RecordTypeId = acc.RecordTypeId;        
			acctObj.Waiver_Date = acc.Waiver_Date__c;
			acctObj.CRMRetailNotificationOptOut = acc.CRMRetailNotificationOptOut__c;
			acctObj.Veteran_Status = acc.Veteran_Status__c;        
			acctObj.RecordTypeId = acc.RecordTypeId;
			acctObj.RecordTypeName = acc.recordType.Name;
			acctObj.Gender = acc.Gender__c;
			return acctObj;
		}   
		private static string morphDateToString(string inputDateValue) {        
			string sDateValue = '';
			string[] inputDateArray = inputDateValue.split('-');
			string inputDateYear = inputDateArray[0];
			string inputDateMonth = inputDateArray[1];
			string inputDateDay = inputDateArray[2];
			sDateValue = inputDateMonth + '/' + inputDateDay + '/' + inputDateYear;        
			return sDateValue;
		}
		private static String prepareQueryString(Set<String> setOfSearchKeys){
			String nameKey=' ';
			for(String searchKey: setOfSearchKeys){            
				nameKey += searchKey +' OR ';            
			}
			if(String.isNotBlank(nameKey)){
				nameKey = nameKey.substringBeforeLast(' OR ').trim();
			}
			return nameKey;
		}
		@AuraEnabled
		public static boolean acknowledgeNotifications(String inputJSON,List<String> accIds,String currentLocation){
			List<Account> listOfAccountToUpdate = new List<Account>();
			List<Storefront_Interaction__c> listOfInteraction=new List<Storefront_Interaction__c >();
			List<Object> sInputList = new List<Object>();
			Date formattedDate = null;
			Boolean isSuccess = false;
			Map<String,String> mapOfNotificationTypeReasonToId=new Map<String,String>();
			Map<String,Storefront_Interaction__c> mapOfAccIdToInteraction=new Map<String,Storefront_Interaction__c>();
			for(Storefront_Interaction__c intRef:[SELECT Id,Aging_In__c,New_Member__C,Veteran__c,SDoh__c,Account__c,Location__c,Storefront_Vstr_Indicator__c FROM Storefront_Interaction__c WHERE
												  CREATEDDATE=TODAY AND (Reason__r.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN 
																		 OR Reason__r.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN) 
												  AND Account__c IN:accIds AND Location__c=:currentLocation ORDER BY Createddate DESC]){
													  if(!mapOfAccIdToInteraction.containsKey(intRef.Account__c)){
														  mapOfAccIdToInteraction.put(intRef.Account__c,intRef); 
														  if(mapOfAccIdToInteraction.size() == accIds.size()){
															  break;
														  }
													  }                                                  
												  }
			for(Storefront_I_Reason__c resonRef : [SELECT Id,Name FROM Storefront_I_Reason__c WHERE Name IN:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTION_NOTIFICATION_FIELDS]){
				mapOfNotificationTypeReasonToId.put(resonRef.Name,resonRef.Id);
			}
		   
			try{
				Date tempDate=system.today();
				integer day = tempDate.day();
				integer month = tempDate.month();
				integer year = tempDate.year();
				formattedDate = Date.newInstance(year, month, day);            
				sInputList = (List<Object>) JSON.deserializeUntyped(inputJSON);            
				for(Object sInput : sInputList) {            
					Map<String, Object> data = (Map<String, Object>) sInput;                
					for(string key : data.keySet()) {
						Account accRef;
						String resonId;
						if(data.get(key) != NULL && Boolean.valueOf(String.valueOf(data.get(key)))){
							if(key==GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_WAIVERDATE_KEY){
								accRef=new Account();                        
								accRef.Id=String.valueOf(data.get('accId'));
								accRef.Waiver_Date__c=formattedDate;
								listOfAccountToUpdate.add(accRef);
								if(mapOfNotificationTypeReasonToId.size()>0 && mapOfNotificationTypeReasonToId.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_WAIVER_REQUIRED)){   
									resonId=mapOfNotificationTypeReasonToId.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_WAIVER_REQUIRED);
									listOfInteraction.add(createNotificationTypeInteraction(data,resonId,mapOfAccIdToInteraction));
								}       
							}
							
							else if(key==GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN_KEY){
								if(mapOfNotificationTypeReasonToId.size()>0 &&mapOfNotificationTypeReasonToId.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN)){
									resonId=mapOfNotificationTypeReasonToId.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_AGING_IN);
									listOfInteraction.add(createNotificationTypeInteraction(data,resonId,mapOfAccIdToInteraction));                               
								} 	   
							}
							else if(key==GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN_KEY){
								if(mapOfNotificationTypeReasonToId.size()>0 &&mapOfNotificationTypeReasonToId.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN)){
									resonId=mapOfNotificationTypeReasonToId.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VETERAN);
									listOfInteraction.add(createNotificationTypeInteraction(data,resonId,mapOfAccIdToInteraction));                               
								}
							} 
							else if(key==GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH){
								if(mapOfNotificationTypeReasonToId.size()>0 && mapOfNotificationTypeReasonToId.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH)){ 
									resonId=mapOfNotificationTypeReasonToId.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH);
									listOfInteraction.add(createNotificationTypeInteraction(data,resonId,mapOfAccIdToInteraction));                               
								}
							}
							else if(key==GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEW_MEMBER_KEY){
								if(mapOfNotificationTypeReasonToId.size()>0 && mapOfNotificationTypeReasonToId.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEW_MEMBER)){
									resonId=mapOfNotificationTypeReasonToId.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NEW_MEMBER);
									listOfInteraction.add(createNotificationTypeInteraction(data,resonId,mapOfAccIdToInteraction));                               
								}
							} 
						}
					}
				}
				CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj;
				if(listOfAccountToUpdate.size()>0)
				{                           
					 accessbilityObj = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.UPDATABLE, listOfAccountToUpdate);
							   
					if(accessbilityObj.exceptionGenerated)
					{      
						HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD);
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD, accessbilityObj.exceptionToLog.getMessage());
					}
					else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
					{
						update listOfAccountToUpdate;
						isSuccess=true;
					}            
					else
					{
						String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT;
						String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
						String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
						HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD,'','');
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD, logError);
					}
				}
				if(listOfInteraction.size()>0){
		   
					accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, listOfInteraction);             
					if(accessbilityObj.exceptionGenerated)
					{   
						HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD);
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD, accessbilityObj.exceptionToLog.getMessage());
					}
					else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
					{
						insert listOfInteraction;
						isSuccess=true;
					}            
					else
					{ 
						String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SINTERACTION;
						String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
						String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
						HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD,'','');
						Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD, logError);
					}
				}
			}
			catch(Exception ex) {            
				HUMExceptionHelper.logErrors(ex,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD);
				throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD, ex.getMessage());      
			} 
			return isSuccess;
		}
		private static Storefront_Interaction__C createNotificationTypeInteraction(Map<String,Object> data,String interReasonId,Map<String,Storefront_Interaction__C> mapOfAccIdToInteraction){
			Storefront_Interaction__C  interRef=new Storefront_Interaction__C();
			Boolean switch_3316885 = HUMUtilityHelper.isCRMFunctionalityON('3316885');
			String vstrIndicator;
			interRef.Reason__c = interReasonId;
			interRef.Category__c=GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOTIFICATION;
			interRef.Account__c=String.valueOf(data.get('accId'));
			interRef.Location__c=String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION));
			interRef.Interaction_Date__c=system.today();
			interRef.Interaction_Date__c=system.now();
			interRef.CRM_Retail_Interaction_Visitor_Type__c=String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_VISITOR_TYPE));
			interRef.Visitor_ID__c=String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_VISITOR_ID));
			if(mapOfAccIdToInteraction.containsKey(String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACC_ID)))){
				interRef.Aging_In__c=mapOfAccIdToInteraction.get(String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACC_ID))).Aging_In__c;
				interRef.New_Member__c=mapOfAccIdToInteraction.get(String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACC_ID))).New_Member__c; 
				interRef.SDoh__c=mapOfAccIdToInteraction.get(String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACC_ID))).SDoh__c; 
				interRef.Veteran__c=mapOfAccIdToInteraction.get(String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACC_ID))).Veteran__c; 
									   
			}
			if(switch_3316885){
				if(HUMUtilityHelper.isCRMFunctionalityON('4012388')){
					vstrIndicator = mapOfAccIdToInteraction.get(String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACC_ID))).Storefront_Vstr_Indicator__c;                
					Map<String,Object> mpIndicators = parseVisitorIndicators(vstrIndicator);
					if(mpIndicators != NULL && mpIndicators.size() > 0){
						interRef.Storefront_Vstr_Indicator__c = GenericHelper_CRMRetail_H_HUM.serializeJSONStructure(mpIndicators,System.label.CRMRetail_Vstr_Indicator_Order);
					}            
				}
				else{
					interRef.Storefront_Vstr_Indicator__c = mapOfAccIdToInteraction.get(String.valueOf(data.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ACC_ID))).Storefront_Vstr_Indicator__c;            
				}
			}
			return interRef; 
		}
		/*
		* Method Name   :    parseVisitorIndicators
		* Description   :    removed the first time indicators from the indicator field
		* Return Type   :    Map<String,Object>
		* Parameters    :    Indicator field value
		*/
		public static Map<String,Object> parseVisitorIndicators(String vstrIndicator){
			Map<String,Object> mpIndicators;
			if(String.isNotBlank(vstrIndicator)){
				mpIndicators = (Map<String,Object>)JSON.deserializeUntyped(vstrIndicator);
				if(mpIndicators.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_FRSTTIMEVSTR_TEXT)){
					mpIndicators.remove(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_FRSTTIMEVSTR_TEXT);
				}
			}
			return mpIndicators;
		}
	}