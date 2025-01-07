/****************************************************************************************
Apex Class Name :  GenericHelper_CRMRetail_H_HUM
Version: 1.0 
Created Date     : 06/27/2019
Function         : Logic of Waiver Date Popup on Home Page
Modification Log : 
* 	Developer               Code Review          Date              Description
* ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Chetan s                                    08/23/2019            REQ - 401378 Original Version.
*  Melkisan Selvaraj                           04/10/2020            801835 & 801756 CRM Storefront Storie helper methods added.
*  Melkisan Selvaraj                           04/17/2020            REQ - 931799 CRM Storefront Interaction First Time Visitor Indicator
*  Pradeep Dani                                06/05/2020            REQ - 942658 - Updating AOR display parameters.
*  Luke P. Cecil                               07/24/2020            User Story 1243084: PR00091869 - MF 11 - CRM Storefront Virtual Visitors: Interaction Category Validation
*  Luke P. Cecil                               08/03/2020            REQ 1243084 There is no duplicate flag for visitor check -in's 
*  Akshay Pai                                  07/15/2021            US-2360057 : Restrict Access to Virtual Locations
*  Vinoth L                      			   10/06/2021            User Story - 2578364 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Import Function
*  Abhishek Maurya               			   10/06/2021            User Story - 2578377 -CRM Storefront Task Management: Import File Confirmation
*  Vinoth L                      			   10/20/2021            User Story - 2578436 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Tasks
*  Abhishek Maurya               			   10/20/2021            User Story - 2578417 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Task Validation
*  Ekta Bist                     			   10/20/2021            User Story - 2578506 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Export Errors
*  Ekta Bist                     			   12/14/2021            User Story - 2956323 Task Import Break Fix
*  Vivek Sharma                                02/18/2022            User Story 2940968: T1PRJ0154546 MF9 Storefront Home Page: Missing 'Event' Interactions
*  Lakshmi Madduri                    		   2/22/2022             User Story 3104178 - T1PRJ0154546 MF9 Storefront Modernization - Create New Dashboards Tab
*  Vinoth L                                    3/18/2022             User Story 2792978 -T1PRJ0154546 / SF / MF9 Storefront - Search/Add Visitors: Inactive Accounts
*  Lakshmi Madduri                       	   6/14/2022             US-3258682 T1PRJ0154546 / SF / MF9 Storefront - SNP: Indicators Field - Data Population
*  Vinoth L                                    09/14/2022            Task Import Case Sensitivity Fix
*  Vinoth L                                    10/11/2022            User Story 3759560: T1PRJ0154546 / SF / MF4 Storefront: Add Account Information on Tasks
*  Vinoth L                                    11/28/2022            REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront
*  Vinoth L                                    02/28/2023            User Story 4349337: T1PRJ0154546 / SF / MF9 Storefront: Deprecate Old Home Page Components             
* Srinivasan								   02/03/2024            T1PRJ1371022 : MF: 5571284 / SF / Storefront - CxOne - HIGH/Medium- GenericHelper_CRMRetail_H_HUM VIT37935980 VIT37935988 VIT37935989
*  Robert Crispen2							   03/08/2024			 Copado Fix for T1PRJ1371022 : MF: 5571284 / SF / Storefront - CxOne - HIGH/Medium- GenericHelper_CRMRetail_H_HUM VIT37935980 VIT37935988 VIT37935989
****************************************************************************************************************************************************************************/
public with sharing class GenericHelper_CRMRetail_H_HUM {
    
    public class ServiceDownException extends Exception {}
    Public Static Boolean bypassTriggerLogic = false;
    
    public static Integer noOfKeys;
    public static Map<integer,String> mapOfMemberId = new Map<Integer,String>(); 
    public static List<Member_Id__c> lstOfMemberIds = new List<Member_Id__c>();
    public class accessibilityOutput{
        
        public Boolean isObjectAccessible = true;
        public Boolean areFieldsAccessible = true;
        public Boolean exceptionGenerated = false;
        public Exception exceptionToLog;
        public Set<String> fieldsNotAccessible = new Set<String>();
        public String objectType = '';
    }
    
    /*
* Method Name   : securityCheckFLS
* Description   : method to check FLS and object accessibility.     
* Parameters    : System.AccessType accessCheckType, List<SObject> sourceRecords  
* retunr type   : accessibilityOutput 
*/
    public static accessibilityOutput securityCheckFLS(System.AccessType accessCheckType, List<SObject> sourceRecords)        
    {
        accessibilityOutput accessbilityObj  = new accessibilityOutput();
        try
        {
            accessbilityObj.objectType = String.valueOf(sourceRecords.getSObjectType());
            SObjectAccessDecision securityDecision = Security.stripInaccessible(accessCheckType, sourceRecords);
            if(securityDecision.getRemovedFields().get(accessbilityObj.objectType) != null)
            {
                accessbilityObj.fieldsNotAccessible = securityDecision.getRemovedFields().get(accessbilityObj.objectType); 
                accessbilityObj.areFieldsAccessible = false;
            }
        }
        catch(NoAccessException e)
        {
            accessbilityObj.isObjectAccessible = false; 
        }
        catch(Exception e)
        {
            accessbilityObj.exceptionGenerated = true; 
            accessbilityObj.exceptionToLog = e;
        }
        return accessbilityObj;
    }    
    
    /*
* Method name : checkCustomPermissionAccess
* Description : This method check if the assoicated Custom Permission Exist with the logged in User
* Return Type : boolean
* Parameter   : String Developer Name of the Customer Permission.
*/
    public static boolean checkCustomPermissionAccess(String sDevName)
    {                
        boolean bAccessExists = false;        
        try
        {                        
            Id userId = UserInfo.getUserId();
            List<SetupEntityAccess> lstAccess = [SELECT SetupEntityId FROM SetupEntityAccess WHERE SetupEntityType='CustomPermission' AND ParentId IN (SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:userId)];            
            Set<Id> setCustomPermIds = new Set<Id>();
            for (SetupEntityAccess sea : lstAccess) {
                setCustomPermIds.add(sea.SetupEntityId);
            }      
            List<CustomPermission> lstCustomPerms = [SELECT Id, DeveloperName, MasterLabel FROM CustomPermission WHERE Id IN :setCustomPermIds];                        
            for (CustomPermission perm : lstCustomPerms) {                
                if (perm.DeveloperName.equals(sDevName))
                {
                    bAccessExists = true; 
                    break;
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'GenericHelper_CRMRetail_H_HUM ', 'checkCustomPermissionAccess');
        }        
        return bAccessExists;
    }
    public static Map<String,String> vldtSfntUsr(Id usrId){
        Map<String,String> mpInvldUsrError = new Map<String,String>();
        Boolean isSfntUsr;
        List<PermissionSetAssignment> lstPmsnStsAssgn = [SELECT Id, PermissionSetId, PermissionSet.Name, PermissionSet.ProfileId, PermissionSet.Profile.Name, AssigneeId, Assignee.Name FROM PermissionSetAssignment WHERE AssigneeId =: usrId AND (PermissionSet.Profile.Name = 'CRMRetail User' OR PermissionSet.Profile.Name  = 'Customer Care Specialist')];
        for(PermissionSetAssignment psa : lstPmsnStsAssgn){            
            if(psa.PermissionSet.Name != NULL && psa.PermissionSet.Name.startsWith('CRMRetail_')){                
                mpInvldUsrError.put('isValid','true');
                break;
            }else{
                mpInvldUsrError.put('isValid','false');
            }
        }
        return mpInvldUsrError;
    }
    public static List<User> rtnStfUsrs(){
        Set<String> stvldUsrRec = new Set<String>();
        Boolean isSfntUsr;
        List<PermissionSetAssignment> lstPmsnStsAssgn = [SELECT Id, PermissionSetId, PermissionSet.Name, PermissionSet.ProfileId, PermissionSet.Profile.Name, AssigneeId, Assignee.Name FROM PermissionSetAssignment WHERE PermissionSet.Profile.Name = 'CRMRetail User' OR (PermissionSet.Profile.Name  = 'Customer Care Specialist' AND PermissionSet.Name IN: GLOBAL_CONSTANT_STOREFRONT_HUM.SET_CRMRETAIL_PERMISSION_SETS)];
        if(!lstPmsnStsAssgn.isEmpty()){
            for(PermissionSetAssignment psa : lstPmsnStsAssgn){                              
                stvldUsrRec.add(psa.AssigneeId);
            }
        }
        List<User> lstStfUsrs;
        if(!stvldUsrRec.isEmpty()){
            lstStfUsrs = new List<User>();
            lstStfUsrs = [SELECT Id from User where Id IN: stvldUsrRec];
        }
        return lstStfUsrs;
    }
    /*
* Method Name   :    checkWaiverDate
* Description   :    Processes Existing visitor Interactions and validate Waiver Date.
* Return Type   :    List<Account>
* Parameters    :    Pass the Interaction record of existing visitors Checked in .
*/
    public Map<String, String> checkWaiverDate(List<Storefront_Interaction__c> insertedGCMInteractions) {
        Map<String, String> waiverResultList = new Map<String, String>();
        String waiverResult = null;
        Map<Id, Id> intAccountMap = new Map<Id, Id>();
        List<Id> accIdList = new List<Id>();
        List<Id> intIdList = new List<Id>();
        try{
            if(insertedGCMInteractions.size() > 0) {
                for(Storefront_Interaction__c interaction : insertedGCMInteractions) {
                    accIdList.add(interaction.Account__c);
                    intIdList.add(interaction.Id);
                    intAccountMap.put(interaction.Id, interaction.Account__c);
                }
                waiverResultList = GenericHelper_CRMRetail_H_HUM.processWaiverDateMatch(accIdList, intIdList, intAccountMap);
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT , 'checkWaiverDate');
            string sErrorMessage = 'Error in verifying WaiverDate of the Visitor. Please contact a Business Administrator.';
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'checkWaiverDate', sErrorMessage);
        }
        return waiverResultList;
    }
    /*
* Method Name   :    serializeJSONStructure
* Description   :    generates the JSON stgructure of key-value pair in the desired order
* Return Type   :    String
* Parameters    :    Map to serialize,string to be serialized
*/
    public static String serializeJSONStructure(Map<String,Object> mpToSerialize,String serializationOrder){        
        List<String> lstOfOrder = new List<String>();
        String serializedString;
        if(String.isNotBlank(serializationOrder)){
            lstOfOrder = serializationOrder.split(':');
        }          
        JSONGenerator jsObj = JSON.createGenerator(false);
        jsObj.writeStartObject();
        if(!lstOfOrder.isEmpty()){
            for(String str: lstOfOrder){  
                if(mpToSerialize != NULL && mpToSerialize.containsKey(str)){
                    jsObj.writeStringField(str,String.valueOf(mpToSerialize.get(str)));  
                }    
            }
            jsObj.writeEndObject();
            serializedString = jsObj.getAsString();            
        }
        return serializedString;
    }
    
    /*
* Method Name   :    getTaskAccountInfo
* Description   :    fetches the account information of the related task
* Return Type   :    CRMRetail_SearchResults_DTO_HUM
* Parameters    :    String - Task record Id
*/
    @AuraEnabled
    public static CRMRetail_Result_DTO_HUM getTaskAccountInfo(String recordId){
        Task tsk = new Task();
        List<Task> lstTsk = new List<Task>();
        Map<Id,Account> mapOfIdToAccount = new Map<Id,Account>();
        Map<String,String> mpOfIntVisitorTypeAndId = new Map<String,String>();
        CRMRetail_Result_DTO_HUM oResultWrapper = new CRMRetail_Result_DTO_HUM();
        CRMRetail_SearchResults_DTO_HUM taskAcc;
        try{
            if(String.isNotBlank(recordId)){                
                lstTsk = [SELECT WhatId,RecordType.Name from Task where Id=: recordId AND RecordType.Name=: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK];
                if(!lstTsk.isEmpty()){
                    tsk = lstTsk[0];
                }                
                if(tsk != NULL){
                    taskAcc = new CRMRetail_SearchResults_DTO_HUM();
                    mapOfIdToAccount= new Map<Id,Account>([SELECT Id,Name,Gender__c,Birthdate__c,PersonEmail,PersonHomePhone,PersonMailingAddress,Deceased_Date__c,(SELECT Id, Name,status__c,Effective_Date__c,End_Date__c,Member__C FROM Policy_Members__r WHERE Member__r.ETL_Record_Deleted__c = false AND ETL_Record_Deleted__c = false ORDER BY Status__c) from Account where Id=:tsk.WhatId]);                   
                    if(mapOfIdToAccount != NULL && mapOfIdToAccount.containsKey(tsk.WhatId) && mapOfIdToAccount.get(tsk.WhatId) != NULL){
                        taskAcc.id = mapOfIdToAccount.get(tsk.WhatId).Id;
                        taskAcc.Name = mapOfIdToAccount.get(tsk.WhatId).Name;
                        taskAcc.Gender = mapOfIdToAccount.get(tsk.WhatId).Gender__c;
                        taskAcc.Birthdate = mapOfIdToAccount.get(tsk.WhatId).Birthdate__c;
                        taskAcc.PersonEmail = mapOfIdToAccount.get(tsk.WhatId).PersonEmail;
                        taskAcc.PersonHomePhone = mapOfIdToAccount.get(tsk.WhatId).PersonHomePhone;
                        taskAcc.PersonMailingAddress = mapOfIdToAccount.get(tsk.WhatId).PersonMailingAddress;
                        if(String.isNotBlank(mapOfIdToAccount.get(tsk.WhatId).Deceased_Date__c)){
                            if(Date.parse(mapOfIdToAccount.get(tsk.WhatId).Deceased_Date__c) <= system.today()){
                                taskAcc.deceasedDate = mapOfIdToAccount.get(tsk.WhatId).Deceased_Date__c;
                            }
                        }
                        if(mapOfIdToAccount.get(tsk.WhatId).Policy_Members__r != NULL && mapOfIdToAccount.get(tsk.WhatId).Policy_Members__r.size() > 0){
                            Integer previousYear = Date.Today().year()-1;
                            Date rangeStartDate = Date.newInstance(previousYear, 01, 01);
                            Date dt = Date.today();
                            Date rangeEndDate = dt.addDays(-1);
                            Map<Id,CRMRetail_SearchResults_DTO_HUM> mpOfTempMbrAccounts = new Map<Id,CRMRetail_SearchResults_DTO_HUM>();
                            Map<Id,String> mapOfAccIdToDateType = new Map<Id,String>();
                            mapOfAccIdToDateType.put(mapOfIdToAccount.get(tsk.WhatId).Id, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_END_DATE);
                            for(Policy_Member__c pmRef: mapOfIdToAccount.get(tsk.WhatId).Policy_Members__r){                                
                                if(String.isBlank(pmRef.End_Date__c) || pmRef.End_Date__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_INVALIDENDDATE){
                                    mapOfAccIdToDateType.put(mapOfIdToAccount.get(tsk.WhatId).Id, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_EFFECTIVE_DATE);
                                }
                                if(!(taskAcc.membershipStatus == System.Label.CRMRetail_Member)){
                                    if(pmRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE){
                                        taskAcc.membershipStatus = System.Label.CRMRetail_Member;
                                    }else if(pmRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FUTURE_STATUS){ 
                                        taskAcc.membershipStatus = System.Label.CRMRetail_Future_Member_Label;
                                    }else if(!(taskAcc.membershipStatus == System.Label.CRMRetail_Future_Member_Label) && pmRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TERMED && Date.parse(pmRef.End_Date__c) >= rangeStartDate && Date.parse(pmRef.End_Date__c) <= rangeEndDate){                                         
                                        taskAcc.membershipStatus = System.Label.CRMRetail_InactiveMember_Label;
                                    } 
                                    mpOfTempMbrAccounts.put(pmRef.Member__c,taskAcc);
                                }
                            }
                            if(mpOfTempMbrAccounts != NULL && mpOfTempMbrAccounts.size() > 0){
                                mpOfTempMbrAccounts = fetchVisitorId(mpOfTempMbrAccounts,mapOfIdToAccount.get(tsk.WhatId).Policy_Members__r,mapOfAccIdToDateType);
                                taskAcc.visitorId = mpOfTempMbrAccounts.get(tsk.WhatId).visitorId;   
                            } 
                        }
                    }
                }
            }
            oResultWrapper.sResult = JSON.serialize(taskAcc);
        }
        catch(Exception ex){
            oResultWrapper.isError = true;
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GETTASKACCOUNTINFO_TEXT);
        }        
        return oResultWrapper;
    }
    /*
* Method Name   :    fetchVisitorId
* Description   :    fetches the visitor Id of the account
* Return Type   :    Map<Id,CRMRetail_TaskAccountInfo_DTO_HUM>
* Parameters    :    Map<Id,CRMRetail_TaskAccountInfo_DTO_HUM> - temporary account instances, List<Policy_Member__c> List of policy member references, Map<Id,String> Account Id to Date Type
*/
    public static Map<Id,CRMRetail_SearchResults_DTO_HUM> fetchVisitorId(Map<Id,CRMRetail_SearchResults_DTO_HUM> mpOfTempMbrAccounts, List<Policy_Member__c> lstPolMbrs,Map<Id,String> mapOfAccIdToDateType){        
        Date currentDate;
        Date previousDate;
        Map<Id,Policy_Member__c> accIdWithPolMemMap = new Map<Id, Policy_Member__c>();
        for(Policy_Member__c pmRef: lstPolMbrs){
            if(accIdWithPolMemMap.containsKey(pmRef.Member__c)){
                if(pmRef.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && !accIdWithPolMemMap.get(pmRef.Member__c).Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH)){
                    accIdWithPolMemMap.put(pmRef.Member__c, pmRef);
                    mpOfTempMbrAccounts.get(pmRef.Member__c).visitorId = pmRef.Name.Substring(0,pmRef.Name.length()-2);
                }else if((!pmRef.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && !accIdWithPolMemMap.get(pmRef.Member__c).Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH)) || (pmRef.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && accIdWithPolMemMap.get(pmRef.Member__c).Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH))){
                    if(mapOfAccIdToDateType.get(pmRef.Member__c) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_END_DATE){
                        currentDate = Date.parse(pmRef.End_date__c);
                        previousDate = Date.parse(accIdWithPolMemMap.get(pmRef.Member__c).End_date__c);
                    }else if(mapOfAccIdToDateType.get(pmRef.Member__c) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_EFFECTIVE_DATE){
                        currentDate = Date.parse(pmRef.Effective_Date__c);
                        previousDate = Date.parse(accIdWithPolMemMap.get(pmRef.Member__c).Effective_Date__c);
                    }
                    if((accIdWithPolMemMap.containsKey(pmRef.Member__c) && accIdWithPolMemMap.get(pmRef.Member__c).Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TERMED && pmRef.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE) || (pmRef.Status__c == accIdWithPolMemMap.get(pmRef.Member__c).Status__c && Math.abs(currentDate.daysBetween(Date.today())) < Math.abs(previousDate.daysBetween(Date.today())))){
                        accIdWithPolMemMap.put(pmRef.Member__c, pmRef);
                        mpOfTempMbrAccounts.get(pmRef.Member__c).visitorId = pmRef.Name.Substring(0,pmRef.Name.length()-2);
                    }
                }
            }else{
                accIdWithPolMemMap.put(pmRef.Member__c,pmRef);
                mpOfTempMbrAccounts.get(pmRef.Member__c).visitorId = pmRef.Name.Substring(0,pmRef.Name.length()-2);
            }
        }
        return mpOfTempMbrAccounts;
    }
    /*
* Method Name   :    getUserPermissionSet
* Description   :    gets all the permission sets assigned to the current user.
* Return Type   :    List<string>
* Parameters    :    NA
*/
    @AuraEnabled
    public static List<String> getUserPermissionSet() 
    {
        List<String> stList = new List<String>();
        try
        {
            for(PermissionSetAssignment assignment : [SELECT Id, PermissionSet.Name FROM PermissionSetAssignment WHERE AssigneeId = :Userinfo.getUserId()])
            {
                stList.add(assignment.PermissionSet.Name);
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETLOCATIONS);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETLOCATIONS, ex.getMessage());
        }
        return stList;
    }
    
    private static Map<String, String> processWaiverDateMatch(List<Id> accIdList, List<Id> intIdList, Map<Id, Id> intAccountMap) {
        List<Account> accList = new List<Account>();
        List<Storefront_Interaction__c> intList = new List<Storefront_Interaction__c>();
        Map<String, String> accountNameList = new Map<String, String>();
        Boolean Switch_1396838 = HUMUtilityHelper.isCRMFunctionalityON('1396838');
        accList = GenericHelper_CRMRetail_H_HUM.fetchAccountDetails(accIdList);
        if(!Switch_1396838)
            if(accList.size() > 0) 
            for(Account acc : accList) 
            if(acc.Waiver_Date__c == null) 
            for(string key : intAccountMap.keySet()) 
        {
            if(intAccountMap.get(key) == acc.Id) 
                intAccountMap.remove(key);
        }
        
        if(intAccountMap.size() > 0) {
            intList = GenericHelper_CRMRetail_H_HUM.fetchInteractionDetails(intAccountMap);
        }
        
        accountNameList = GenericHelper_CRMRetail_H_HUM.generateOutput(accList, intList, intAccountMap);
        
        return accountNameList;
    }
    
    private static List<Account> fetchAccountDetails(List<Id> accIdList) {
        List<Account> accList = new List<Account>();
        accList = [Select Id, Name, Waiver_Date__c from Account where Id in :accIdList];
        return accList;
    }
    
    private static List<Storefront_Interaction__c> fetchInteractionDetails(Map<Id, Id> intAccountMap) {
        List<Storefront_Interaction__c> intList = new List<Storefront_Interaction__c>();
        List<Id> intIdList = new List<Id>();
        for(String key : intAccountMap.keySet()) {
            intIdList.add(key);
        }
        if(intIdList.size() > 0) {
            intList = [Select Id, Name, Interaction_Date__c, Account__r.Id, Account__r.Name, Category__c 
                       from Storefront_Interaction__c where Id in :intIdList];    
        }
        return intList;
    }
    
    private static Map<String, String> generateOutput(List<Account> accList, List<Storefront_Interaction__c> intList, Map<Id, Id> intAccountMap) {
        Map<String, String> accountNameList = new Map<String, String>();
        Map<String, String> interimMap = new Map<String, String>();
        Boolean Switch_1396838 = HUMUtilityHelper.isCRMFunctionalityON('1396838');
        if(intList.size() > 0) {
            for(String key : intAccountMap.keySet()) {
                Id interactionId = key;
                Id accountId = intAccountMap.get(key);
                Integer months = 0;
                for(Storefront_Interaction__c interaction: intList) {
                    if(key == interaction.Id) {
                        for(Account account : accList) {
                            if(intAccountMap.get(key) == account.Id) {
                                Date accWaiverDate = account.Waiver_Date__c;
                                DateTime intDateTime = interaction.Interaction_Date__c;
                                Date interactionDate = date.newInstance(intDateTime.year(), intDateTime.month(), intDateTime.day());                                
                                if(account.Waiver_Date__c != null)
                                {
                                    Date waiverDate = date.newInstance(accWaiverDate.year(), accWaiverDate.month(), accWaiverDate.day());
                                    months = accWaiverDate.monthsBetween(interactionDate);
                                    if(accWaiverDate.day() > interactionDate.day()) {
                                        months--;
                                    }
                                    
                                }
                                if(!Switch_1396838 && months >= 36)
                                {
                                    interimMap.put('accountName', account.Name);
                                    interimMap.put('interactionDate', interactionDate.format());
                                    interimMap.put('interactionId', key);
                                    accountNameList.put(account.Id, JSON.serialize(interimMap));
                                }
                                
                                Date waiverExpiry;
                                try
                                {
                                    waiverExpiry = date.Parse(System.Label.CRMRETAIL_WAIVEREXPIRY_DATE);
                                } 
                                catch(Exception ex)
                                {
                                    waiverExpiry = date.newInstance(2020, 10, 01);
                                }
                                if((months >= 36 || account.Waiver_Date__c == null || account.Waiver_Date__c < waiverExpiry) && interaction.Category__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL && Switch_1396838) {
                                    interimMap.put('accountName', account.Name);
                                    interimMap.put('interactionDate', interactionDate.format());
                                    interimMap.put('interactionId', key);
                                    accountNameList.put(account.Id, JSON.serialize(interimMap));
                                }
                            }
                        }   
                    } 
                }
            }
        }
        return accountNameList;
    }
    
    public AORServiceResponseWrapper aorCalloutCRMRetail(String callOutServiceName, String accEnterpriseId) {
        HUM_Webservice_Callout__c crmretailServiceConfigObj = 
            HUM_Webservice_Callout__c.getValues(callOutServiceName);
        HttpRequest req = new HttpRequest();
        AORServiceResponseWrapper aorResponseWrapper = new AORServiceResponseWrapper();
        string sDate = System.now().format('yyyy-MM-dd');                
        
        try {
            req.setMethod(crmretailServiceConfigObj.Request_Type__c);  
            req.setHeader(Label.HUMServiceTesterController_content_type, crmretailServiceConfigObj.Content_Type__c);            
            req.setHeader(Label.HUMServiceTesterControllerConnection, Label.HUMServiceTesterController_keep_alive);
            
            req.setBody('{"producerBookOfBusinessRequest":{"Platform": "LV","BlockOfBusiness": "MA","EnterpriseID": "'+ accEnterpriseId + '","CiCustomerNumber": "0","BenSequenceNumber": "0","ApplicationId": "0","ProductLine": "0","StartEffectiveDate": "2010-01-01T10:11:00","EndEffectiveDate": "'+ sDate +'T10:11:00","StartTermDate": "'+ sDate +'T10:11:00","EndTermDate": "9999-12-31T10:11:00","Status": "ACTIVE"}}');
            req.setTimeout(Integer.valueOf(crmretailServiceConfigObj.Timeout__c));    
            if(!Test.isRunningTest())req.setClientCertificateName(crmretailServiceConfigObj.Certificate_Name__c);
            req.setEndpoint(crmretailServiceConfigObj.End_Point_URL__c + crmretailServiceConfigObj.Service_Name__c);
            
            Http http = new Http();
            HttpResponse response = http.send(req);
            if ((response != null && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && 
                 (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
                )) {
                    aorResponseWrapper.aorResponseDTO = (ProdBOBResponse_DTO_HUM)JSON.deserialize(response.getbody(), ProdBOBResponse_DTO_HUM.class);
                    aorResponseWrapper.aorErrored = false;
                }
            else {
                throw New ServiceDownException('SERVICEERROR'+response.getBody());
            }
        }
        catch(ServiceDownException ex) {            
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT,'SERVICE_FAILURE');
            errorLogList.add(logObj);
            string errorNumber='';
            string saveResID = '';
            
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
            for(Database.SaveResult sr : srList) 
            {    
                if(sr.isSuccess()) {
                    saveResID = sr.getId();
                }
            }
            
            List<Error_Log__c> srErrorLog = [Select Name from Error_Log__c where Id = :saveResID LIMIT 1];
            if(!srErrorLog.isEmpty())
            {
                errorNumber = srErrorLog[0].Name;            	
            }
            
            aorResponseWrapper.aorErrored = true;          
            aorResponseWrapper.aorResponseError = 'TESTFAILURE' + '*' + errorNumber  + '#';
        }
        catch (Exception ex)
        { 
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'aorCalloutCRMRetail');
            errorLogList.add(logObj);
            string errorNumber='';
            string saveResID = '';
            
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
            for(Database.SaveResult sr : srList) 
            {
                if(sr.isSuccess()) {
                    saveResID = sr.getId();
                }
            }
            
            List<Error_Log__c> srErrorLog = [Select Name from Error_Log__c where Id = :saveResID LIMIT 1];
            if(!srErrorLog.isEmpty())
            {
                errorNumber = srErrorLog[0].Name;
            }
            aorResponseWrapper.aorErrored = true;          
            aorResponseWrapper.aorResponseError = 'TESTFAILURE' + '*' + errorNumber  + '#';
        }
        return aorResponseWrapper; 
    }
    
    public Map<String, Map<String, String>> fetchVisitorIdType(List<String> accIdList) {
        List<Account> nonMemberAccList = new List<Account>();
        List<Member_Id__c> memberList = new List<Member_Id__c>();
        List<Account> acclistnew = new List<Account>();
        List<Id> memberAccIdList = new List<Id>();
        
        Map<String, Map<String, String>> resultMap = new Map<String, Map<String, String>>();
        resultMap.put('visitorid',New Map<String, String>());
        resultMap.put('visitortype',New Map<String, String>());
        Boolean Switch_1209425 = HUMUtilityHelper.isCRMFunctionalityON('1209425');
        if(accIdList.size() > 0) {
            acclistnew = [Select Id, Name, GCM_Visitor_Barcode__c, RecordType.Name from Account where Id in :accIdList];
            if(acclistnew.size() > 0) {
                for(Account accnew : acclistnew){
                    if(accnew.RecordType.Name == 'Member'){
                        memberAccIdList.add(accnew.Id);
                    }
                    else{
                        nonMemberAccList.add(accnew);
                    }
                }
            }
            if(memberAccIdList.size() > 0){
                if(Switch_1209425)
                    memberList = [Select Id, Name, Policy_Member__r.Member__r.Id, Policy_Member__r.ETL_Record_Deleted__c,
                                  Policy_Member__r.Member__r.GCM_Visitor_Barcode__c ,Policy_Member__r.Status__c
                                  from Member_Id__c where Policy_Member__r.Member__r.Id in :memberAccIdList
                                  AND Policy_Member__r.Status__c = 'Active'
                                  AND Policy_Member__r.ETL_Record_Deleted__c = false];
                else
                    memberList = [Select Id, Name, Policy_Member__r.Member__r.Id, 
                                  Policy_Member__r.Member__r.GCM_Visitor_Barcode__c 
                                  from Member_Id__c where Policy_Member__r.Member__r.Id in :memberAccIdList 
                                  AND Policy_Member__r.Status__c = 'Active'
                                  AND Source_Application_Code__c = 'LV'
                                  AND Policy_Member__r.ETL_Record_Deleted__c = false];
            }
            
            if(memberList.size() > 0) {
                for(Member_Id__c member : memberList) {
                    if(member.Policy_Member__r.Member__r.GCM_Visitor_Barcode__c != null && 
                       member.Policy_Member__r.Member__r.GCM_Visitor_Barcode__c != '') {
                           resultMap.get('visitorid').put(member.Policy_Member__r.Member__r.Id, member.Policy_Member__r.Member__r.GCM_Visitor_Barcode__c);
                       }
                    else {
                        resultMap.get('visitorid').put(member.Policy_Member__r.Member__r.Id, member.Name);              
                    }
                    resultMap.get('visitortype').put(member.Policy_Member__r.Member__r.Id, 'Member');
                }
            }
            
            if(nonMemberAccList.size() > 0) {
                for(Account acc : nonMemberAccList) {                    
                    resultMap.get('visitorid').put(acc.Id, acc.GCM_Visitor_Barcode__c);
                    if(Switch_1209425)
                        resultMap.get('visitortype').put(acc.Id, 'Non-Member');
                    else
                    {
                        String accName = acc.Name;
                        String accNameUC = accName.toUpperCase();
                        if(accNameUC.startsWith('UNKNOWN')) 
                        {
                            resultMap.get('visitortype').put(acc.Id, 'Unknown');
                        }
                        else 
                        {
                            resultMap.get('visitortype').put(acc.Id, 'Non-Member');
                        }
                    }
                }
            }
        }
        return resultMap;
    }
    
    public class AORServiceResponseWrapper {
        public ProdBOBResponse_DTO_HUM aorResponseDTO = new ProdBOBResponse_DTO_HUM();
        public String aorResponseError = '';
        public Boolean aorErrored = false;
    }
    /*
* Method Name   :    checkDuplicateInteractions
* Description   :    Identifies duplicate and new interactions
* Return Type   :    map of Id and Boolean
* Parameters    :    List<Storefront_Interaction__c> record being inserted on click of checkin/new interaction
*/
    @AuraEnabled
    Public Static map<String, boolean> checkDuplicateInteractions(list<Storefront_Interaction__c> lstInteractions) 
    {                 
        Boolean switch_931799 = HUMUtilityHelper.isCRMFunctionalityON('931799');
        map<string, boolean> retMap = new map<string, boolean>();
        try
        {            
            set<id> reasonIds = new set<id>();
            set<id> accountIds = new set<id>();
            set<string> visitiorIds = new set<string>();
            set<datetime> intStartTime = new set<datetime>();
            list<Storefront_Interaction__c> lstInteractionsQueried = new list<Storefront_Interaction__c>();
            set<string> uniqueKeySet = new set<string>();            
            id checkInFirstTimeReasonId ;
            string key = '';
            Date intDate;
            Id locationId;
            Set<String> categories = new set<String>();
            Boolean Switch_1243084 = HUMUtilityHelper.isCRMFunctionalityON('1243084');            
            string category = '';
            
            Storefront_I_Reason__c oSIR = [Select Id FROM Storefront_I_Reason__c WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN];            
            if(oSIR != null)
            {
                reasonIds.add(oSIR.Id);
                checkInFirstTimeReasonId = oSIR.Id;
                retMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTTIME_CHECKIN_REASON+'_'+oSIR.Id,true);                
            }           
            for(Storefront_Interaction__c oSI: lstInteractions){
                categories.add(oSI.Category__c);
                intDate = oSI.Interaction_Date__c.DateGMT();
                locationId = oSI.Location__c;
                reasonIds.add(oSI.Reason__c);
                accountIds.add(oSI.Account__c);
                visitiorIds.add(oSI.Visitor_ID__c);
                intStartTime.add(oSI.Storefront_Event_Starttime__c);
                if(Switch_1243084)
                {                    
                    if(oSI.Category__c == null)
                        category = '';
                    else
                        category = string.valueOf(oSI.Category__c);
                    key = string.valueOf(oSI.Interaction_Date__c.DateGMT())+
                        string.valueOf(oSI.Reason__c) + string.valueOf(oSI.Location__c)+ string.valueOf(oSI.Account__c)+ string.valueOf(oSI.Visitor_ID__c)+
                        string.valueOf(oSI.Storefront_Event_Starttime__c) + category;
                    
                    uniqueKeySet.add(string.valueOf(oSI.Interaction_Date__c.DateGMT())+
                                     checkInFirstTimeReasonId + oSI.Location__c+ oSI.Account__c+ oSI.Visitor_ID__c+
                                     string.valueOf(oSI.Storefront_Event_Starttime__c) + category);                                        
                }
                else
                {                     
                    key = string.valueOf(oSI.Interaction_Date__c.DateGMT())+
                        string.valueOf(oSI.Reason__c) + string.valueOf(oSI.Location__c)+ string.valueOf(oSI.Account__c)+ string.valueOf(oSI.Visitor_ID__c)+
                        string.valueOf(oSI.Storefront_Event_Starttime__c);
                    uniqueKeySet.add(string.valueOf(oSI.Interaction_Date__c.DateGMT())+
                                     checkInFirstTimeReasonId + oSI.Location__c+ oSI.Account__c+ oSI.Visitor_ID__c+
                                     string.valueOf(oSI.Storefront_Event_Starttime__c));                    
                }
                retMap.put(key, false);                
                uniqueKeySet.add(key);
                if(oSI.Reason__c == checkInFirstTimeReasonId && switch_931799)
                    retMap.put(oSI.Account__c, true);
            }            
            if(Switch_1243084)    
            {                
                lstInteractionsQueried = [SELECT Id,Reason__c,Location__c,Interaction_Date__c ,Account__c,Visitor_ID__c,Storefront_Event_Starttime__c,  Category__c FROM Storefront_Interaction__c 
                                          WHERE Reason__c =: reasonIds 
                                          AND (Location__c =: locationId OR  Reason__c =: checkInFirstTimeReasonId)
                                          AND DAY_ONLY(Interaction_Date__c)  =: intDate
                                          AND Account__c =: accountIds
                                          AND Visitor_ID__c =: visitiorIds
                                          AND (Storefront_Event_Starttime__c =: intStartTime OR Storefront_Event_Starttime__c = null)
                                          AND Category__c =: categories];
            }
            else
            {
                lstInteractionsQueried = [SELECT Id,Reason__c,Location__c,Interaction_Date__c ,Account__c,Visitor_ID__c,Storefront_Event_Starttime__c FROM Storefront_Interaction__c 
                                          WHERE Reason__c =: reasonIds 
                                          AND (Location__c =: locationId OR  Reason__c =: checkInFirstTimeReasonId)
                                          AND DAY_ONLY(Interaction_Date__c)  =: intDate
                                          AND Account__c =: accountIds 
                                          AND Visitor_ID__c =: visitiorIds
                                          AND (Storefront_Event_Starttime__c =: intStartTime OR Storefront_Event_Starttime__c = null )];                
            }
            if(!lstInteractionsQueried.isEmpty()){                
                for(Storefront_Interaction__c oCompSI:lstInteractionsQueried){                    
                    if(Switch_1243084)
                    {                        
                        if(oCompSI.Category__c == null)
                            category = '';
                        else
                            category = string.valueOf(oCompSI.Category__c);
                        key = string.valueOf(oCompSI.Interaction_Date__c.DateGMT())+
                            oCompSI.Reason__c + oCompSI.Location__c+ oCompSI.Account__c+ oCompSI.Visitor_ID__c+
                            string.valueOf(oCompSI.Storefront_Event_Starttime__c) + category;                        
                    }
                    else
                    {
                        key = string.valueOf(oCompSI.Interaction_Date__c.DateGMT())+
                            oCompSI.Reason__c + oCompSI.Location__c+ oCompSI.Account__c+ oCompSI.Visitor_ID__c+
                            string.valueOf(oCompSI.Storefront_Event_Starttime__c);                         
                    }
                    if(oCompSI.Reason__c == checkInFirstTimeReasonId && switch_931799)
                        retMap.put(oCompSI.Account__c, true);
                    if(uniqueKeySet.contains(key))
                    {                        
                        retMap.put(key, true);                        
                    }
                }                
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'checkDuplicateInteractions');
            string sErrorMessage = 'Error while checking for duplicate interactions.';
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'checkDuplicateInteractions', sErrorMessage);      
        }
        return retMap;
    }
    public static void checkAccessForDML(String dmlType, List<SObject> lstToProcessDMLs, String className, String methodName) 
    {
        accessibilityOutput accessbilityObj;
            Boolean isDeletable;
            if(dmlType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE)
            {
                String objectTypeToBeDeleted = lstToProcessDMLs[0].getSObjectType().getDescribe().getName(); 
                isDeletable = ((sObject)Type.forName(objectTypeToBeDeleted).newInstance()).getSObjectType().getDescribe().isDeletable();
                if(isDeletable)
                {
                    delete lstToProcessDMLs;
                }
                else{
                    String logError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + ' '+dmlType.toLowerCase() +' '+ objectTypeToBeDeleted;                
                    HUMExceptionHelper.logError(logError,className,methodName,'','');
                    Throw AuraErrorHandler_C_HUM.createAuraHandledException(className, methodName, logError);
                }
            }
            else
            {
                Switch on dmlType
                {
                    when 'INSERT'
                    {
                        accessbilityObj  = securityCheckFLS(AccessType.CREATABLE, lstToProcessDMLs); 					 
                    }
                    when 'UPDATE'
                    {
                        accessbilityObj  = securityCheckFLS(AccessType.UPDATABLE, lstToProcessDMLs);            		
                    }
                    when 'UPSERT'
                    {
                        accessbilityObj  = securityCheckFLS(AccessType.UPSERTABLE, lstToProcessDMLs);            	    
                    }        			                    	
                }
                if(accessbilityObj.exceptionGenerated)
                { 
                    HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog,className, methodName);
                    Throw AuraErrorHandler_C_HUM.createAuraHandledException(className,methodName, accessbilityObj.exceptionToLog.getMessage());
                }
                else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
                {
                    Switch on dmlType{                            
                        When 'INSERT'
                        {
                            insert lstToProcessDMLs;
                        }
                        When 'UPDATE'
                        {
                            update lstToProcessDMLs;                        
                        }
                        When 'UPSERT'
                        {
                            upsert lstToProcessDMLs;
                        }
                    }
                }            
                else
                {
                    String sObjectType = lstToProcessDMLs[0].getSObjectType().getDescribe().getName();
                    String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + ' '+dmlType.toLowerCase() +' '+ sObjectType;
                    String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + sObjectType + '-' + accessbilityObj.fieldsNotAccessible;                           
                    String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
                    HUMExceptionHelper.logError(logError,className,methodName,'','');
                    Throw AuraErrorHandler_C_HUM.createAuraHandledException(className, methodName, logError);
                }     
            }  
        }
    
    // Method is not referred to any Class so temprory commented.//Srinivasan Baskaran
    /*
    public static void checkforaccess(List<SObject> lstAtt){
        accessibilityOutput objAccessOutput;
        objAccessOutput = securityCheckFLS(AccessType.UPDATABLE, lstAtt);
        
        if(objAccessOutput.exceptionGenerated)
        {
            HUMExceptionHelper.logError(String.valueOf(objAccessOutput.exceptionToLog)  ,'Storefront_Interaction_H_HUM','processAttendanceOnInsert','','');                            
            throw AuraErrorHandler_C_HUM.createAuraHandledException('Storefront_Interaction_H_HUM','processAttendanceOnInsert', String.valueOf(objAccessOutput.exceptionToLog));
        }
        else{
            if(objAccessOutput.isObjectAccessible)
            {
                if(objAccessOutput.areFieldsAccessible)
                {
                    insert lstAtt;                                    
                }
                else
                {
                    Set<String> removedFields =  objAccessOutput.fieldsNotAccessible;
                    HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE + ' - ' +removedFields,'Storefront_Interaction_H_HUM','processAttendanceOnInsert','','');                                    
                    throw AuraErrorHandler_C_HUM.createAuraHandledException('Storefront_Interaction_H_HUM', 'processAttendanceOnInsert', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE + ' - ' +removedFields);
                }
            }
            else
            {
                HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE,'Storefront_Interaction_H_HUM','processAttendanceOnInsert','','');                                
                throw AuraErrorHandler_C_HUM.createAuraHandledException('Storefront_Interaction_H_HUM', 'processAttendanceOnInsert' , GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE);
            }
        }                   
    }  
*/ 
    /*
* Method Name   :    evaluateAttendance
* Description   :    Identifies maximum concurrent visitor count and sets peak time 
* Return Type   :    Attendance__c
* Parameters    :    Attendance__c record being updated on every visitor checkin
*/
    public static Attendance__c evaluateAttendance(Attendance__c att)
    {
        try
        {
            DateTime utcDateTime = system.now();
            if(att.Maximum_Concurrent_Visitors__c < att.Current_Attendance__c)
            {
                att.Maximum_Concurrent_Visitors__c = att.Current_Attendance__c;
                att.Peak_Time__c = Time.newInstance(utcDateTime.hour(), utcDateTime.minute(), utcDateTime.second(), utcDateTime.millisecond());
            }
            return att;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, 'evaluateAttendance');
            string sErrorMessage = 'Error while evaluate Attendance.';
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, 'evaluateAttendance', sErrorMessage);      
        }
    }
    
    /*
* Method Name   :    createAtte4ndance
* Description   :    Initiates Attendance__c instance to be inserted
* Return Type   :    Attendance__c
* Parameters    :    Location Id and Date in which Attendance__c record to be inserted
*/
    public static Attendance__c createAttendance(Id locId, Date dateValue)
    {
        try
        {
            DateTime utcDateTime = system.now();
            Attendance__c att = new Attendance__c(Current_Attendance__c = 1, First_Time_Visitors_Today__c = 0, Visitors_Today__c = 0, Location__c = locId, Date__c = dateValue, Maximum_Concurrent_Visitors__c = 1, Peak_Time__c = Time.newInstance(utcDateTime.hour(), utcDateTime.minute(), utcDateTime.second(), utcDateTime.millisecond()));
            return att;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM , 'createAttendance');
            string sErrorMessage = 'Error while creating Attendance record.';
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_HOMEPAGE_LC_HUM, 'createAttendance', sErrorMessage);      
        }
    }
    
    /*
* Method Name   :    deduceMaxVisitorLimit
* Description   :    Identifies the left over capacity of Location's Interaction occupancy
* Return Type   :    Decimal
* Parameters    :    NA
*/
    public static Decimal deduceMaxVisitorLimit()
    {        
        List<String> dateStringList = new List<String>();
        String currLoc = CRMRetail_HomePage_LC_HUM.getorSetLocationInCache(null,false);        
        Date dt;
        Decimal capacity = 0;
        Boolean isMaxReached = false;
        try
        {
            dt = getCurrentInteractionDate();
            if(dt == NULL){
                dt = date.today();
            }                        
            List<Storefront_Location__c> currLocationLst = [SELECT Id, Maximum_Occupancy__c FROM Storefront_Location__c WHERE Name =: currLoc LIMIT 1];
            List<Attendance__c> attList = [SELECT Id, Current_Attendance__c, Location__c, Date__c FROM Attendance__c WHERE Date__c =:dt AND Location__r.Name  =: currLoc LIMIT 1];                        
            if(attList.size() > 0)
            {                
                capacity = currLocationLst[0].Maximum_Occupancy__c - attList[0].Current_Attendance__c;
            }
            else
                capacity = currLocationLst[0].Maximum_Occupancy__c;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'deduceMaxVisitorLimit');  
        }
        return capacity;
    }
    
    /*
* Method Name   :    fetchSwitchResults
* Description   :    This method returns values of the switch records
* Return Type   :    Map<String,Boolean>
* Parameters    :    NA
*/
    public static Map<String,Boolean> fetchSwitchResults() 
    {
        Map<String,Boolean> switchMap = new Map<String,Boolean>();
        List<CRMFunctionality_ONOFF_Switch__c> switchList = new List<CRMFunctionality_ONOFF_Switch__c>();
        try
        {            
            for(CRMFunctionality_ONOFF_Switch__c OnOffswitch : [SELECT Id, Name, IsON__c FROM CRMFunctionality_ONOFF_Switch__c WHERE Name IN: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SWITCH_LIST])
            {                                
                switchMap.put('Switch_'+OnOffswitch.Name, OnOffswitch.IsOn__c);                
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'fetchSwitchResults');
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'fetchSwitchResults', ex.getMessage());
        }
        return switchMap;
    }
    
    /**
*  Called from method virtualLocationChecks and return the set of location id's
*  @name virtualLocationChecks
*  @param Set<Id> setLocationId
*  @param Set<Id> setNonVirtualId
*  @return set<Id>
*  @throws NA
*/
    public static Set<ID> getVirtualLocations(Set<Id> setLocationId)
    {
        Set<Id> setVirtualLocIds = new Set<Id>();
        
        if(setLocationId != null && !setLocationId.isEmpty())
        {   
            List<Storefront_Location__c> lstLoc = [SELECT ID, Location_Type__c FROM Storefront_Location__c WHERE ID IN : setLocationId AND Location_Type__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL];
            
            if(lstLoc != null && !lstLoc.isEmpty())
            {
                for (Storefront_Location__c loc : lstLoc)
                {
                    setVirtualLocIds.add(loc.id);
                }
            }
        }
        return setVirtualLocIds;
    }
    /**
*  Generic Method to return location records
*  @name getLocation
*  @param Set<String> setOfLocationName
*  @return Map<Id,Storefront_Location__c>
*  @throws NA
*/
    public static Map<Id,Storefront_Location__c> getLocation(Set<String> setOfLocationName){        
        Map<Id,Storefront_Location__c>  mpResult= new Map<Id,Storefront_Location__c>();
        if(setOfLocationName.size()>0){
            mpResult=  new Map<Id,Storefront_Location__c> ([SELECT Id,Name,Inactive_indicator__c FROM Storefront_Location__c WHERE Name IN:setOfLocationName AND Inactive_indicator__c = false]);
        }        
        return mpResult;
    }
    public static List<Member_ID__C> getMemberRecords(Map<integer,String> mapOfMemberIdsForQuery)
    {
        try
        {
            if(mapOfMemberIdsForQuery != null && !mapOfMemberIdsForQuery.isEmpty())
            {
                noOfKeys = mapOfMemberIdsForQuery.size();
                mapOfMemberId =mapOfMemberIdsForQuery.clone();
                if(noOfKeys>0){
                    queryMemberRecords(mapOfMemberId.get(noOfKeys));
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'getMemberRecords');
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'getMemberRecords', ex.getMessage());
        }
        return lstOfMemberIds;
    }
    
    private Static void queryMemberRecords(String searchStrVal)
    {
        List<Member_ID__C> lstMmberId = new List<Member_ID__C>();        
        try
        {            
            String searchQuery = 'FIND '+ searchStrVal + ' IN NAME FIELDS RETURNING Member_Id__c(Id, Name, ETL_Record_Deleted__c, Policy_Member__r.Member__r.PersonContactId,Policy_Member__r.Member__c,Policy_Member__r.Status__c, Policy_Member__r.ETL_Record_Deleted__c Where Policy_Member__r.Member__r.ETL_Record_Deleted__c = false AND Policy_Member__r.ETL_Record_Deleted__c = false AND Policy_Member__r.Status__c IN '+GLOBAL_CONSTANT_STOREFRONT_HUM.LIST_STRING_STATUSLIST+')';
            
            lstMmberId = search.query(searchQuery)[0];            
            if(lstMmberId != null){				
                lstOfMemberIds.addAll(lstMmberId);
            }
            noOfKeys--;
            if(noOfKeys>0){
                queryMemberRecords(mapOfMemberId.get(noOfKeys));
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'queryMemberRecords');
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GENERIC_HELPER_TEXT, 'queryMemberRecords', ex.getMessage());
        }
    }
    /**
*  Generic Method to return User records
*  @name getUserRecords
*  @param Set<String> setOfUserName
*  @return Map<Id,User>
*  @throws NA
*/
    public static Map<Id,User> getUserRecords(Set<String> setOfUserName){
        Map<Id,User>  mpResult= new Map<Id,User>();
        if(setOfUserName.size()>0){
            mpResult = new Map<Id,User> ([SELECT Id,Name,Alias,isActive,(SELECT Id,Assignee.alias, PermissionSet.Name FROM PermissionSetAssignments WHERE PermissionSet.Name IN: GLOBAL_CONSTANT_STOREFRONT_HUM.SET_CRMRETAIL_PERMISSION_SETS) FROM User WHERE Alias IN: setOfUserName AND isActive = true]);            
        }
        return mpResult;
    }
    /**
*  Generic Method to return User Interaction Reason
*  @name getIntReasonRecords
*  @param Set<String> setOfUserName
*  @return Map<Id,Storefront_I_Reason__C>
*  @throws NA
*/
    public static Map<Id,Storefront_I_Reason__C> getIntReasonRecords(Set<String> setOfReasnName){
        Map<Id,Storefront_I_Reason__C>  mpResult = new Map<Id,Storefront_I_Reason__C>();
        Map<Id,Storefront_I_Reason__C>  finalResultMap = new Map<Id,Storefront_I_Reason__C>();
        Map<String,Storefront_I_Reason__C>  comparableMap = new Map<String,Storefront_I_Reason__C>();
        Boolean switch_3573183 = HUMUtilityHelper.isCRMFunctionalityON(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SWITCH_CASE_SENSITIVITY);
        if(setOfReasnName.size()>0){
            mpResult = new Map<Id,Storefront_I_Reason__C> ([SELECT Id,Name,Inactive__c FROM Storefront_I_Reason__C WHERE Name IN:setOfReasnName AND Inactive__c=False]);            
            if(switch_3573183){
                for(Id Key : mpResult.keySet()){
                    comparableMap.put(mpResult.get(key).Name,mpResult.get(key));
                }
                for(String setValue: setOfReasnName){ 
                    if(comparableMap.containsKey(setValue)){
                        finalResultMap.put(comparableMap.get(setValue).Id,comparableMap.get(setValue));
                    }
                }
                return finalResultMap;
            }
        }
        return mpResult;
    }
    
    
    /**
*  Generic Method to return duplicate and non-duplicate tasks
*  @name checkTaskDuplicates
*  @param (Map<String,Task> mapOfTask
*  @return Map<String,Map<String,Task>>
*  @throws NA
*/
    public static Map<String,Map<String,Task>> checkTaskDuplicates(Map<String,Task> mapOfTask){
        List<Task> tasksToInsert =  new List<Task>();
        Set<Id> whatidSet = new Set<Id>();
        Set<Id> locationSet = new Set<Id>();
        Set<Id> assignedToSet = new Set<Id>();
        Set<String> SubjectSet = new Set<String>();
        Set<Date> ActivityDateSet = new Set<Date>();
        Map<String,Task> taskMapSfdc =  new Map<String,Task>();
        Map<String,Map<String,Task>> finalTaskMap =  new Map<String,Map<String,Task>>();
        for(Task trec: mapOfTask.values()){
            whatidSet.add(trec.WhatId);
            locationSet.add(trec.CRM_Retail_Location__c);
            assignedToSet.add(trec.OwnerId);
            SubjectSet.add(trec.Subject);
            ActivityDateSet.add(trec.ActivityDate);
        }
        Id crmRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK).getRecordTypeId();
        for(Task tsfdc : [Select WhatId,crm_retail_location__c,OwnerId,Subject,ActivityDate FROM Task WHERE Recordtypeid = :crmRecordTypeId AND isclosed != true AND WhatId IN :whatidSet AND crm_retail_location__c IN :locationSet
                          AND OwnerId IN :assignedToSet ]) {
                              if(SubjectSet.contains(tsfdc.Subject) && ActivityDateSet.contains(tsfdc.ActivityDate)){
                                  String keyRecSfdc = tsfdc.Subject+'@'+tsfdc.WhatId+'@'+tsfdc.CRM_Retail_Location__c+'@'+tsfdc.OwnerId+'@'+tsfdc.ActivityDate;
                                  taskMapSfdc.put(keyRecSfdc,tsfdc);  
                              }
                          }
        for(String keyRec : mapOfTask.keySet() ){
            
            if(taskMapSfdc.containsKey(keyRec))
            {
                if(finalTaskMap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY)){
                    finalTaskMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY).put(keyRec,mapOfTask.get(keyRec));
                }else{
                    finalTaskMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY,new Map<String,Task>{keyRec=>mapOfTask.get(keyRec)});
                }
            }else{
                
                if(finalTaskMap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY)){
                    finalTaskMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY).put(keyRec,mapOfTask.get(keyRec));
                }else{
                    
                    finalTaskMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY,new Map<String,Task>{keyRec=>mapOfTask.get(keyRec)});
                } 
            }            
        }
        return finalTaskMap;
    }
    
    public static String getHomeLocation(){        
        List<Storefront_Preference__c> listOfPref = [SELECT Id, Default_Location__c, Default_Location__r.name, User__c FROM Storefront_Preference__c where User__c=:UserInfo.getUserId() LIMIT 1];
        if(listOfPref!= null && !listOfPref.isEmpty() && listOfPref[0].Default_Location__c != null && listOfPref[0].Default_Location__r != null && listOfPref[0].Default_Location__r.name != null){
            return listOfPref[0].Default_Location__r.name;
        }
        else{
            return null;
        }
    }
    /*
* Method Name   :    getLocationMdtNames
* Description   :    get VNC Location names from the custom metadata.
* Return Type   :    Set<String>
* Parameters    :    NA
*/
    public static Set<String> getLocationMdtNames(){
        List<CRMRetail_DefLoc__mdt> locationMdtList = [SELECT Id, Location_Name__c FROM CRMRetail_DefLoc__mdt];
        Set<String> locationMdtNameSet = new Set<String>();
        for(CRMRetail_DefLoc__mdt locObj: locationMdtList){
            locationMdtNameSet.add(locObj.Location_Name__c);
        }
        return locationMdtNameSet;
    }
    /*
* Method Name   :    getCurrentInteractionDate
* Description   :    converts the interaction date into a proper date format
* Return Type   :    Date
* Parameters    :    NA
*/
    public static Date getCurrentInteractionDate(){
        Date currentInteractionDate;
        String currInteractionDate = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);         
        List<String> dateStringList = new List<String>();
        if(currInteractionDate != NULL){
            if(currInteractionDate.contains('/'))
            {
                currentInteractionDate = date.parse(currInteractionDate);
            }        
            else if(currInteractionDate.contains('-'))
            {
                dateStringList = currInteractionDate.split('-');
                currentInteractionDate = date.newInstance(Integer.valueOf(dateStringList[0]),Integer.valueOf(dateStringList[1]),Integer.valueOf(dateStringList[2]));
            }
        }        
        return currentInteractionDate;
    }
    
    /*
* Method Name   :    formConsolidatedServiceRequest
* Description   :    forms service request with policy member data
* Return Type   :    List<CRMRetail_ConsolidatedReq_DTO_HUM>
* Parameters    :    List<Account>, boolean
*/
    public static List<CRMRetail_ConsolidatedReq_DTO_HUM> formConsolidatedServiceRequest(List<Account> lstAccount,boolean checkForMEDProduct)
    {
        Map<Id,CRMRetail_ConsolidatedReq_DTO_HUM> mapOfAccIdServiceReqs = new Map<Id,CRMRetail_ConsolidatedReq_DTO_HUM>();
        List<Policy_Member__c> lstPolicyMembers = new List<Policy_Member__c>();
        for(Account objAcc : lstAccount)
        {
            if(objAcc.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
            {
                if(objAcc.Policy_Members__r.size()>0)
                {
                    lstPolicyMembers.addAll(objAcc.Policy_Members__r);
                }
                else
                {
                    CRMRetail_ConsolidatedReq_DTO_HUM objReq = new CRMRetail_ConsolidatedReq_DTO_HUM();
                    objReq.enterpriseId = (objAcc.Enterprise_ID__c != null) ? objAcc.Enterprise_ID__c : '';
                    objReq.accountId = objAcc.Id;
                    objReq.makeSNPRequest = false;
                    mapOfAccIdServiceReqs.put(objAcc.Id,objReq);
                }
            }
        }
        if(!lstPolicyMembers.isEmpty() && lstPolicyMembers.size()>0){
            Set<String> seOfMajorLOBAvaialble = new Set<String>();
            String sBusinessDesc = 'HUMRSOMedicare';//HUMConstants__c.getInstance(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_RSOMEDICARE).StringValue__c;
            for(Reference_Business_Line_Product_Grouping__c objGrouping :[SELECT Major_LOB__c FROM Reference_Business_Line_Product_Grouping__c Where ProductCode__c =: 'MED' /*GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_POLICY_PRODUCT_TYPE*/ AND Business_Description__c LIKE : sBusinessDesc])
            {
                seOfMajorLOBAvaialble.add(objGrouping.Major_LOB__c);
            }
            for(Policy_Member__c objPolicyMember : lstPolicyMembers)
            {
                CRMRetail_ConsolidatedReq_DTO_HUM objReq = new CRMRetail_ConsolidatedReq_DTO_HUM();
                if(checkForMEDProduct && objPolicyMember.Product__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_POLICY_PRODUCT_TYPE){
                    continue;
                }
                if(objPolicyMember.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE)
                {                    
                    if(objPolicyMember.Policy__r.Platform__c==GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LV && String.isNotBlank(objPolicyMember.Policy__r.Product__r.Major_LOB__c) && seOfMajorLOBAvaialble.contains(objPolicyMember.Policy__r.Product__r.Major_LOB__c) && (!mapOfAccIdServiceReqs.containsKey(objPolicyMember.Member__c) || !mapOfAccIdServiceReqs.get(objPolicyMember.Member__c).makeSNPRequest)){
                        String sSourceKey = objPolicyMember.Policy__r.Source_Cust_Cov_Key__c;	
                        objReq.enterpriseId = objPolicyMember.Member__r.Enterprise_ID__c;
                        objReq.accountId = objPolicyMember.Member__c;
                        objReq.memberId = objPolicyMember.Name.Substring(0,objPolicyMember.Name.length()-2);
                        objReq.groupId = sSourceKey.substring(0, 6);
                        objReq.benefitSequenceNumber = sSourceKey.substring(6, 9);
                        objReq.makeSNPRequest = true;
                        mapOfAccIdServiceReqs.put(objPolicyMember.Member__c,objReq);
                    }
                    else if(objPolicyMember.Member__r.Enterprise_ID__c != null && !mapOfAccIdServiceReqs.containsKey(objPolicyMember.Member__c)){
                        objReq.enterpriseId = objPolicyMember.Member__r.Enterprise_ID__c;
                        objReq.accountId = objPolicyMember.Member__c;
                        objReq.makeSNPRequest = false;
                        mapOfAccIdServiceReqs.put(objPolicyMember.Member__c,objReq);
                    }                        
                }                
                
            }
        }
        return mapOfAccIdServiceReqs.values();
    }
    
    /*
* Method Name   :    logFaultError
* Description   :    parses fault error body and forms error log
* Return Type   :    Error_Log__c
* Parameters    :    String, String, String, String, String, String
*/
    public static Error_Log__c logFaultError(String respFaultCode, String sClassName, String sMethodName, String sRecordId, String sObjectAPI, String sSeverity)
    {
        Error_Log__c objErrorLog = new Error_Log__c();
        objErrorLog.Class_Name__c = sClassName;
        objErrorLog.Method_Name__c = sMethodName;
        objErrorLog.Record_Id__c = sRecordId;
        objErrorLog.Object_Type__c = sObjectAPI;
        objErrorLog.Request_Sent__c = true;
        objErrorLog.Severity__c = sSeverity;
        objErrorLog.Type__c = GLOBAL_CONSTANT_HUM.Pharmacy_IntegrationError_HUM;
        if (!String.isBlank(respFaultCode))
        {            
            respFaultCode = respFaultCode.replace('-', '_');
            FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
            faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(respFaultCode, FaultResponse_DTO_HUM.class);
            if (faultResponse.Fault != null && faultResponse.Fault.detail != null)
            {
                if (faultResponse.Fault.detail.errorInfo != null)
                {
                    objErrorLog.Error_Code__c = faultResponse.Fault.detail.errorInfo.error_code;
                    objErrorLog.Error_Message__c = faultResponse.Fault.detail.errorInfo.error_message;
                }
                if (faultResponse.Fault.detail.transactionInfo != null)
                {
                    objErrorLog.Integration_Point__c = faultResponse.Fault.detail.transactionInfo.URL_in;
                    objErrorLog.Operation__c = faultResponse.Fault.detail.transactionInfo.transaction_id;
                }
            }
        }                
        return objErrorLog;
    }
    /*
* Method Name   :    createErrorLog
* Description   :    creates error log object instance
* Return Type   :    Void  
*/	
    public static Error_Log__c createErrorLog(String errorCode, String className, String methodName, String errorMsg, string errorType){
        Error_Log__c logError = new Error_Log__c();                        
        logError.Error_Code__c = errorCode;
        logError.class_name__c = className;
        logError.Method_Name__c = methodName;
        logError.Error_Message__c = errorMsg;
        logError.Type__c = errorType;
        return logError;
    }    
    
    public static Boolean FLSUpdateCheck(String objectName, List<String> fieldList){
        Boolean isUpdateable = true;
        Map<String,SObjectField> fieldMap = Schema.getGlobalDescribe().get(objectName).getDescribe().fields.getMap();
        
        for(String objField : fieldList)
        {
            if(!fieldMap.get(objField).getDescribe().isUpdateable())
            {
                isUpdateable = false;
            }
        }
        return isUpdateable;
    }    
    
    public static String curdFLSUCreateUpdateCheck(String objectName, List<String> fieldList, String dmlType)
    {
        String FLSError = '';
        List<String> notAccessiblefields = new List<String>();
        Schema.DescribeSObjectResult describeResult = Schema.getGlobalDescribe().get(objectName).getDescribe();
        
        Boolean curdCheck = dmlType == 'CREATE' ? describeResult.isCreateable() : (dmlType == 'UPDATE' ? describeResult.isUpdateable() : (dmlType == 'DELETE' ? describeResult.isDeletable() : (dmlType == 'UPSERT' ?(describeResult.isCreateable() && describeResult.isUpdateable()): false)));
            
            if(curdCheck)
        {
            Map<String,SObjectField> fieldMap = describeResult.fields.getMap();
            
            for(String objField : fieldList)
            {
                Boolean flsCheck = dmlType == 'CREATE' ? fieldMap.get(objField).getDescribe().isCreateable() : (dmlType == 'UPDATE' ? fieldMap.get(objField).getDescribe().isUpdateable() : (dmlType == 'UPSERT' ?(fieldMap.get(objField).getDescribe().isCreateable() && fieldMap.get(objField).getDescribe().isUpdateable()): false));
                    if(!flsCheck)
                {
                    notAccessiblefields.add(objField);
                }
            }
            if(!notAccessiblefields.isEmpty() && notAccessiblefields != null)
            {
                FLSError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + objectName + '-' + notAccessiblefields;
            }
        }
        else
        {
            FLSError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS +dmlType.toLowerCase() +' '+ objectName;
        }
        return FLSError;
    }    
}