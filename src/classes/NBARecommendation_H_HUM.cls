/*******************************************************************************************************************************
Trigger Name    : NBARecommendation_H_HUM 
Version         : 1.0
Created On      : 09/07/2020
Function        : Hold the logic for Recommendation object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Yogesh Gupta                                    09/07/2020                original version
* Sayali Nemade                                   11/05/2020                REQ - 1041220 -- PR00090631 - MF 9 - MVP Ability to 
modify and store alerts in CRM Service 
* Sayali Nemade                                   11/17/2020                DF - 1995 REQ-1041220: Field Name and Updated Time Stamp field format corrected
* Sayali Nemade                                   11/19/2020                DF - 2016 REQ-1041220: Link Name and Path field are not updated in the History window
* Sayali Nemade                                   01/29/2021                Checkmarx Issues Resolved - Changed without sharing to with sharing 
* Apurva Urkude                                   21/04/2021                Req -  2164068- PRJ0002736 - 2021 MF6 - SF - Tech - CheckMarx vulnerabilities fix - NBA Pages
* Kinal Mangukiya                                 08/18/2023                US 4812228 - T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – NBA
*******************************************************************************************************************************/
public with sharing class NBARecommendation_H_HUM {

    Public static Integer sUpdatePriority;
    public static Boolean bPriorityIncrement;
    public static Boolean isFirstTime = true;
    
    /**
    *  Checks for duplicate recommendation records with same recommendation name 
    *  @name checkForDuplicateRecommendation
    *  @param lstNewRecommendations Holds the list of all Recommendation records, mapOldRecommendations to hold old records 
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateRecommendation(List<Recommendation> lstNewRecommendations, Map<Id, Recommendation> mapOldRecommendations)
    {
        Set<String> setOfRecommendationName = new Set<String>();
        for(Recommendation recom : lstNewRecommendations) {
            if((mapOldRecommendations != null && recom.Name.toUpperCase() != mapOldRecommendations.get(recom.Id).Name.toUpperCase())
            || (mapOldRecommendations == null && String.isNotBlank(recom.Name))) {
                setOfRecommendationName.add(recom.Name);
            }
        } 
        
        if(setOfRecommendationName.size() > 0) {
            
            List<Recommendation > listOfRecomExisting = [Select Id, Name From Recommendation Where Name IN :setOfRecommendationName WITH USER_MODE];
            
            Map<String, Recommendation> mapOfRecommendationWithName = new Map<String, Recommendation>();
            for(Recommendation rec : listOfRecomExisting) {
                mapOfRecommendationWithName.put(rec.Name, rec);
            }
            
            for(Recommendation rec : lstNewRecommendations) {
                if(mapOfRecommendationWithName.containsKey(rec.Name)) {
                    rec.Name.addError('Recommendation with same name already Exist');
                }
            }
        }        
    } 
    
    /**
    *  Track Field History for Recommendation Object 
    *  @name trackHistoryForRecommendation
    *  @param lstNewRecommendations Holds the list of all new Recommendation records, mapOldRecommendations to hold old records 
    *  @return void
    *  @throws NA
    */
    public static void trackHistoryForRecommendation(List<Recommendation> lstNewRecommendations, Map<Id, Recommendation> mapOldRecommendations)
    {
         Boolean sNBASwitchCall;
        sNBASwitchCall = HUMUtilityHelper.isCRMFunctionalityON('2164068');
        list<Recommendation_History__c> lstRecFieldTrackHistory = new list<Recommendation_History__c>();
        if(Trigger.isUpdate) {
                Integer endIndexOldValue;
                Integer endIndexNewValue;
                for(Recommendation recom: lstNewRecommendations) {
                    for (Schema.FieldSetMember fieldSetMem : SObjectType.Recommendation.fieldSets.getMap().get('RecommendationFieldSet').getFields()) {
                        String fieldName  = fieldSetMem.getFieldPath();
                        String fieldLabel = fieldSetMem.getLabel();
                        if(recom.get(fieldName) != mapOldRecommendations.get(recom.Id).get(fieldName)){
                            Recommendation_History__c recomhistory = new Recommendation_History__c();
                            recomhistory.Name = fieldLabel;
                            recomhistory.New_Value__c = String.ValueOf(recom.get(fieldName));
                            recomhistory.Old_Value__c = String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName));
                            recomhistory.Recommendation__c = recom.Id;
                            if(!String.isBlank(String.ValueOf(recom.get(fieldName))) && !String.isBlank(String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)))) {
                                endIndexOldValue = String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)).length() > 60 ? 59 : String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)).length();
                                endIndexNewValue = String.ValueOf(recom.get(fieldName)).length() > 60 ? 59 : String.ValueOf(recom.get(fieldName)).length();
                                recomhistory.External_ID__c = fieldLabel+'|'+recom.Name+'|'+String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)).substring(0,endIndexOldValue)+'|'+String.ValueOf(recom.get(fieldName)).substring(0,endIndexNewValue); 
                            }
                            else if(String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)) == null || String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)) == '') {
                                endIndexNewValue = String.ValueOf(recom.get(fieldName)).length() > 60 ? 59 : String.ValueOf(recom.get(fieldName)).length();
                                recomhistory.External_ID__c = fieldLabel+'|'+recom.Name+'|'+String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName))+'|'+String.ValueOf(recom.get(fieldName)).substring(0,endIndexNewValue); 
                            }
                            else {
                                endIndexOldValue = String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)).length() > 60 ? 59 : String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)).length();
                                recomhistory.External_ID__c = fieldLabel+'|'+recom.Name+'|'+String.ValueOf(mapOldRecommendations.get(recom.Id).get(fieldName)).substring(0,endIndexOldValue)+'|'+String.ValueOf(recom.get(fieldName));      }  
                            lstRecFieldTrackHistory.add(recomhistory);
                        }   
                    }
                } 
             if(sNBASwitchCall==true) {    
            SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.CREATABLE, lstRecFieldTrackHistory);
            lstRecFieldTrackHistory= securityDecision.getRecords();
            if(!lstRecFieldTrackHistory.isEmpty()) {
                insert lstRecFieldTrackHistory;
               }
            }
            else
            {
                if(!lstRecFieldTrackHistory.isEmpty()) {
                insert lstRecFieldTrackHistory;
            }
            }
        }
    }
    
    
    /**
    *  Priority Increment Insert for Recommendation Object 
    *  @name PriorityIncerementInsertRecommendation
    *  @param lstNewRecommendation Holds the list of all new Recommendation records
    *  @return void
    *  @throws NA
    */
    public static void PriorityIncerementInsertRecommendation(List<Recommendation> lstNewRecommendation)
    {
         Boolean sNBASwitchCall;
        sNBASwitchCall = HUMUtilityHelper.isCRMFunctionalityON('2164068');
        if(lstNewRecommendation[0].Priority__c != NULL && lstNewRecommendation[0].Active__c)
        {
            List<Recommendation> listofRecommendations = [SELECT Id, Priority__c, Description FROM Recommendation 
                                                          WHERE Priority__c >=: lstNewRecommendation[0].Priority__c WITH USER_MODE];
            List<Recommendation> listofRecommendationsToUpdate= new List<Recommendation>(); 
            if(listofRecommendations.size()>0)
            {
                for(Recommendation objUpdatePriority: listofRecommendations)
                {
                    sUpdatePriority = Integer.ValueOf(objUpdatePriority.Priority__c); 
                    sUpdatePriority = sUpdatePriority + 1; 
                    objUpdatePriority.Priority__c = sUpdatePriority;
                    
                    listofRecommendationsToUpdate.add(objUpdatePriority);
                }
                if(sNBASwitchCall==true)                {
                SObjectAccessDecision securityDecision = Security.stripInaccessible( AccessType.UPDATABLE, listofRecommendationsToUpdate);
                listofRecommendationsToUpdate= securityDecision.getRecords();
                if(listofRecommendationsToUpdate.size()>0)
                update listofRecommendationsToUpdate;
                 }
                else
                {
                 if(listofRecommendationsToUpdate.size()>0)      
                 update listofRecommendationsToUpdate;   
                }
           }
        }
    }
    
    /**
    *  Priority Increment Update for Recommendation Object 
    *  @name PriorityIncerementUpdateRecommendation
    *  @param lstNewRecommendation Holds the list of all new Recommendation records, lstOldRecommendation holds list of all old Recommendation records and mapOldRecommendations to hold old records  
    *  @return void
    *  @throws NA
    */
    public static void PriorityIncerementUpdateRecommendation(list<Recommendation> lstNewRecommendation,list<Recommendation> lstOldRecommendation, map<id,Recommendation> mapOldRecommendation)
    {
         Boolean sNBASwitchCall;
        sNBASwitchCall = HUMUtilityHelper.isCRMFunctionalityON('2164068');
        List<Recommendation> listofRecommendationsToUpdate= new List<Recommendation>();
        List<Recommendation> listofRecommendations = new List<Recommendation>();
        set<id> setRecommedantionId = mapOldRecommendation.keySet();
        
        if(lstNewRecommendation[0].Priority__c != NULL && lstOldRecommendation[0].Priority__c == NULL)
        {
            bPriorityIncrement = true;
            listofRecommendations = [SELECT Id, Priority__c, Description FROM Recommendation where id !=: lstOldRecommendation[0].id AND Priority__c >=: lstNewRecommendation[0].Priority__c WITH USER_MODE];
        }
        else if(lstNewRecommendation[0].Priority__c != NULL && lstOldRecommendation[0].Priority__c != NULL && lstOldRecommendation[0].Priority__c > lstNewRecommendation[0].Priority__c)
        {
            bPriorityIncrement = true;
            listofRecommendations = [SELECT Id, Priority__c, Description FROM Recommendation where Priority__c >=: lstNewRecommendation[0].Priority__c AND Priority__c <: lstOldRecommendation[0].Priority__c WITH USER_MODE];
        }
        else if(lstNewRecommendation[0].Priority__c != NULL && lstOldRecommendation[0].Priority__c != NULL && lstOldRecommendation[0].Priority__c < lstNewRecommendation[0].Priority__c)
        {
            bPriorityIncrement = false;
            listofRecommendations = [SELECT Id, Priority__c, Description FROM Recommendation where Priority__c <=: lstNewRecommendation[0].Priority__c AND Priority__c >: lstOldRecommendation[0].Priority__c WITH USER_MODE];
        }
        else if(lstOldRecommendation[0].Priority__c != NULL && lstOldRecommendation[0].Active__c && (lstNewRecommendation[0].Priority__c == NULL || !lstNewRecommendation[0].Active__c))
        {
            bPriorityIncrement = false;
            listofRecommendations = [SELECT Id, Priority__c, Description FROM Recommendation where Priority__c >: lstOldRecommendation[0].Priority__c WITH USER_MODE];
            if(!lstNewRecommendation[0].Active__c && lstOldRecommendation[0].Active__c)
            {
                lstNewRecommendation[0].Priority__c = NULL;
            }
        }
        if(listofRecommendations.size()>0)
            {
                for(Recommendation objUpdatePriority: listofRecommendations)
                {
                    if(bPriorityIncrement)
                    {
                        sUpdatePriority = Integer.ValueOf(objUpdatePriority.Priority__c); 
                        sUpdatePriority = sUpdatePriority + 1; 
                        objUpdatePriority.Priority__c = sUpdatePriority;
                    }
                    else if (!bPriorityIncrement)                    
                    {
                        sUpdatePriority = Integer.ValueOf(objUpdatePriority.Priority__c); 
                        sUpdatePriority = sUpdatePriority - 1; 
                        objUpdatePriority.Priority__c = sUpdatePriority;
                    }
                    listofRecommendationsToUpdate.add(objUpdatePriority);
                }
            if(sNBASwitchCall==true)
                {
                SObjectAccessDecision securityDecision = Security.stripInaccessible( AccessType.UPDATABLE, listofRecommendationsToUpdate);
                listofRecommendationsToUpdate= securityDecision.getRecords();
                if(listofRecommendationsToUpdate.size()>0) 
                update listofRecommendationsToUpdate;
               }
                else
                {
                   if(listofRecommendationsToUpdate.size()>0)      
                    update listofRecommendationsToUpdate; 
                }
           }
    }
    
    /**
    *  Priority Increment Delete for Recommendation Object 
    *  @name PriorityIncerementUpdateRecommendation
    *  @param lstOldRecommendation Holds the list of all old Recommendation records
    *  @return void
    *  @throws NA
    */
    public static void PriorityIncerementDeleteRecommendation(list<Recommendation> lstOldRecommendation)
    {
    Boolean sNBASwitchCall;
          sNBASwitchCall = HUMUtilityHelper.isCRMFunctionalityON('2164068');
        if(lstOldRecommendation[0].Priority__c != NULL && lstOldRecommendation[0].Active__c)
        {
            List<Recommendation> listofRecommendations = [SELECT Id, Priority__c, Description FROM Recommendation where Priority__c >: lstOldRecommendation[0].Priority__c WITH USER_MODE];
            List<Recommendation> listofRecommendationsToUpdate= new List<Recommendation>(); 
            if(listofRecommendations.size()>0)
            {
                for(Recommendation objUpdatePriority: listofRecommendations)
                {
                    sUpdatePriority = Integer.ValueOf(objUpdatePriority.Priority__c); 
                    sUpdatePriority = sUpdatePriority - 1; 
                    objUpdatePriority.Priority__c = sUpdatePriority;
                    
                    listofRecommendationsToUpdate.add(objUpdatePriority);
                }
               if(sNBASwitchCall==true)    {
                SObjectAccessDecision securityDecision = Security.stripInaccessible( AccessType.UPDATABLE, listofRecommendationsToUpdate);
                listofRecommendationsToUpdate= securityDecision.getRecords();
                if(listofRecommendationsToUpdate.size()>0)
                update listofRecommendationsToUpdate;
                 }
                else
                {
                   if(listofRecommendationsToUpdate.size()>0)      
                    update listofRecommendationsToUpdate; 
                }
           }
        }
    }
    
    /**
    *  Read Only for Recommendation Object 
    *  @name ReadOnlyForRecommendation
    *  @param lstNewRecommendations Holds the list of all new Recommendation records, mapOldRecommendations to hold old records 
    *  @return void
    *  @throws NA
    */
    public static void ReadOnlyForRecommendation(List<Recommendation> lstNewRecommendations, List<Recommendation> lstOldRecommendations)
    {
        User currentLoggedInUser = [SELECT id, Profile.Name, Profile.Id FROM User WHERE id =:Userinfo.getUserId() WITH USER_MODE];   
        String profileName = currentLoggedInUser.Profile.Name;
    
        if(!profileName.contains('System Administrator') && !profileName.contains('Deployment') && !profileName.contains('ETL API Access')) 
        {
            List<PermissionSetAssignment> lstofPermissionSet = [SELECT id, AssigneeId, PermissionSet.Name FROM PermissionSetAssignment 
                                                                WHERE PermissionSet.Name = 'CRMS_420_NBARecommendationAdmin'
                                                                AND AssigneeId =: Userinfo.getUserId()];
            if(lstofPermissionSet.size() == 0 ) {    
                if(Trigger.isInsert || Trigger.isUpdate) {
                    for (Recommendation recomm : lstNewRecommendations) {
                        recomm.addError('Insufficient Privileges, you have read only access');
                    }
                }
                else if(Trigger.isDelete) {
                    for (Recommendation recomm : lstOldRecommendations) {
                        recomm.addError('Insufficient Privileges, you have read only access');   
                    }
                }
            }
        }
    } 
}