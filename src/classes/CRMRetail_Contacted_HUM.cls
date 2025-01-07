/***********************************************************************************************************************************
* Apex Class Name  : CRMRetail_Contacted_HUM 
* Version          : 1.0
* Created Date     : 23rd Sept, 2021
* Function         : Controller Class for Contacted Button 
* Test Class       : CRMRetail_Contacted_T_HUM
**************************************************************************** 
* Modification Log:
* Developer Name                   Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta                                                     9/23/2021            Original Version 2561850
* Vinoth L                                                 9/23/2021            User Story 2635072 -T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Action Required Prompt
* Lakshmi Madduri                                          8/1/2022             Partial upsert to by pass trigger error 
* Vinoth L                                                 8/18/2022            User Story 3625171 -T1PRJ0154546 - MF9 - CRM Storefront: Recommend Events (Story 3)
* Vinoth L	                    						   8/29/2022           	User Story 3581472: T1PRJ0154546 / SF / MF9 Storefront: Ability to Create Follow Up Task From Visitor Check-Ins
* Vinoth L                                                 09/14/2022           Task Import Case Sensitivity Fix
* Vinoth L                                     			   11/28/2022           REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront
* Vinoth L                                                 01/05/2023           User Story 4107848: T1PRJ0154546 / SF / MF3 Storefront: Update Visitor ID & Type Population Logic
* Vinoth L                      				   		   02/28/2023           User Story 4349337: T1PRJ0154546 / SF / MF9 Storefront: Deprecate Old Home Page Components
**********************************************************************************************************************************/
public with sharing class CRMRetail_Contacted_HUM 
{
    private static Map<String,Map<String,String>> mapOfReqValforNotificationByAccId = new Map<String, Map<String, String>>();  
    private static Map<String,String> mapOfVistorTypeByAccId = new Map<String,String>();
    private static Task objTask = new Task();    
    private static String taskLocationType;
    private static List<Sobject> recordsForUpsert = new List<Sobject>();
    public static boolean isCalledFromContacted = false;
    public static String currLocationId ;
    private static Map<String,Object> mapOfKeyToNotificationData = new Map<String,Object>();
    
    /*
    * Method Name   :    fetchSwitchResults
    * Description   :    This method returns values of the switch records
    * Return Type   :    Map<String,Boolean>
    * Parameters    :    NA
    */
    @AuraEnabled
    public static Map<String,Boolean> fetchSwitchResults(){        
        Map<String,Boolean> mpOfSwitchResults = new Map<String,Boolean>();
        mpOfSwitchResults = CRMRetail_HomePage_LC_HUM.fetchSwitchResults();                    
        return mpOfSwitchResults;
    }
    /*
    * Method Name   :    getInteractionInfo
    * Description   :    This method is used to assemble the account and interaction data based on task records.
    * Return Type   :    Map<String,Object>
    * Parameters    :    Task Id        
    */
    @AuraEnabled
    public static Map<String,Object> getInteractionInfo(String taskRecordId){        
        try{
            if(HUMUtilityHelper.isCRMFunctionalityON('2561850')){                       
                Id taskLocationId; 
                String taskSubject = '';
                Id interactionReasonId;    
                List<Task> lstTasks = new List<Task>();
                List<Storefront_Interaction__c> lstInteractionToInsert = new List<Storefront_Interaction__c>();
                lstTasks = [Select id, WhatId,What.Name,account.recordType.Name,Subject,CRMRetail_Automated__c, CRM_Retail_Location__c,Status from Task where id =: taskRecordId LIMIT 1];                                
                if(!lstTasks.isEmpty()){
                  	objTask = lstTasks[0]; 
                }
                if(objTask != null)
                {                    
                    currLocationId =objTask.CRM_Retail_Location__c;
                    if(String.isBlank(objTask.WhatId) ||  String.isBlank(objTask.Subject) || String.isBlank(objTask.CRM_Retail_Location__c))
                    {
                        mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ISREQUIRED);
                        return mapOfKeyToNotificationData;
                    }
                    if(objTask.Status == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_STATUS)
                    {
                        mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_WARNING_KEY, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_MESSAGE);
                        return mapOfKeyToNotificationData;
                    }
                    if(!objTask.CRMRetail_Automated__c){                        
                        mapOfKeyToNotificationData.put(System.label.CRMRetail_Automated_Task,true);
                        return mapOfKeyToNotificationData;
                    }
                    else
                    {                        
                        Storefront_I_Reason__c sfReason;                            
                        Boolean switch_3573183 = HUMUtilityHelper.isCRMFunctionalityON(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SWITCH_CASE_SENSITIVITY);
                        if(switch_3573183){
                            for(Storefront_I_Reason__c intReason: [SELECT Id,Name,Inactive__c FROM Storefront_I_Reason__c WHERE Name =: objTask.Subject]){
                                if(intReason.Name.equals(objTask.Subject)){
                                    sfReason = intReason;
                                    break;
                                }
                            } 
                        }else{
                            sfReason = [SELECT Id,Name,Inactive__c FROM Storefront_I_Reason__c WHERE Name =: objTask.Subject LIMIT 1];
                        }
                        if(sfReason == NULL && switch_3573183){                            
                            mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY , GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INVALID_REASON);
                            return mapOfKeyToNotificationData;
                        }
                        else if(sfReason != NULL && !sfReason.Inactive__c){                            
                            interactionReasonId = sfReason.Id;                                    
                        } 
                        else{                            
                            mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY , GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INACTIVE_MESSAGE);
                            return mapOfKeyToNotificationData;
                        }                                                               
                        if(String.isNotBlank(objTask.WhatId))
                        {
                            Storefront_Interaction__c intRec = new Storefront_Interaction__c();
                            List<Storefront_Interaction__c> lstOfInterToInsert = new List<Storefront_Interaction__c>(); 
                            Boolean isDuplicate = false;                                                       
                            isCalledFromContacted = true; 
                            List<Object> lstOfObjs = new List<Object>();
                            Map<String, String> mapOfAccIdName = new Map<String,String>();
                            mapOfAccIdName.put(String.valueOf(objTask.WhatId),objTask.What.Name);
                            Map<String,String> mpOfExpRes = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(mapOfAccIdName,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_VCHECKIN_TYPE);
                            Map<String,Storefront_NotificationsWrapper_HUM> tempMap = new Map<String,Storefront_NotificationsWrapper_HUM>();
                            List<Storefront_Interaction__c> tmpLst = new List<Storefront_Interaction__c>();
                            if(mpOfExpRes != NULL){
                                if(mpOfExpRes.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INTDETAILS_KEY) && mpOfExpRes.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INTDETAILS_KEY) != NULL){
                                    tmpLst = (List<Storefront_Interaction__c>)JSON.deserialize(mpOfExpRes.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INTDETAILS_KEY),List<Storefront_Interaction__c>.class);
                                    lstOfInterToInsert.add(createInteractionInstance(tmpLst[0],interactionReasonId));
                                    lstOfInterToInsert.add(tmpLst[0]);
                                }                                    
                                if(mpOfExpRes.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIF_RECORD) && mpOfExpRes.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIF_RECORD) != NULL && mpOfExpRes.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIF_RECORD) != '{}'){
                                    tempMap = (Map<String,Storefront_NotificationsWrapper_HUM>)JSON.deserialize(mpOfExpRes.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIF_RECORD),Map<String,Storefront_NotificationsWrapper_HUM>.class);                                        
                                    if(tempMap != NULL && tempMap.size() > 0 && tempMap.containsKey(String.valueOf(objTask.WhatId)) && tempMap.get(String.valueOf(objTask.WhatId)) != NULL){
                                        mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_NOTIFICATIONDATA_KEY,tempMap.get(String.valueOf(objTask.WhatId)));                                            
                                    }                                        
                                }
                            }                                                        
                            if(lstOfInterToInsert != NULL && lstOfInterToInsert.size() > 0) {                                                                        
                                insertInteractions(lstOfInterToInsert);                                   
                            }
                        }
                    }                    
                }
            }
            else{
                mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_WARNING_KEY, system.label.CRMRetail_FeatureDisabled);
            }
        }
        catch(Exception e)
        {               
            mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY,System.Label.CRM_Retail_Error_Updating_Task);
            HUMExceptionHelper.logErrors(e,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_CONTACTED_CLASS,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_CONTACTED_METHODNAME);                         
        }
        return mapOfKeyToNotificationData;
    }
     /*
    * Method Name   :    createInteractionInstance
    * Description   :    This method is used to create the interaction for task contacted button
    * Return Type   :    Void    
    */
    @testVisible
    private static Storefront_Interaction__c createInteractionInstance(Storefront_Interaction__c taskInt,Id taskReasonId)
    {
        Storefront_Interaction__c objInteraction = new Storefront_Interaction__c();
        objInteraction.Account__c = taskInt.Account__c;
        objInteraction.Location__c = taskInt.Location__c;
        objInteraction.Interaction_Date__c = taskInt.Interaction_Date__c;
        objInteraction.Reason__c = taskReasonId;
        objInteraction.Category__c = taskInt.Category__c;
        objInteraction.Visitor_ID__c = taskInt.Visitor_ID__c;
        objInteraction.CRM_Retail_Interaction_Visitor_Type__c = taskInt.CRM_Retail_Interaction_Visitor_Type__c;
        objInteraction.Storefront_Event_Starttime__c = null;
        objInteraction.Storefront_Event_Endtime__c = null;
        objInteraction.Veteran__c = taskInt.Veteran__c;                            
        objInteraction.Aging_In__c =taskInt.Aging_In__c;
        objInteraction.New_Member__c = taskInt.New_Member__c;
        objInteraction.SDoh__c = taskInt.SDoh__c;
        Map<String,Object> mpIndicators = new Map<String,Object>();
        if(HUMUtilityHelper.isCRMFunctionalityON('4012388')){
            String vstrIndicator = taskInt.Storefront_Vstr_Indicator__c;
            mpIndicators = CRMRetail_HomePage_LC_HUM.parseVisitorIndicators(vstrIndicator);
            if(mpIndicators != NULL && mpIndicators.size() > 0){                            
                objInteraction.Storefront_Vstr_Indicator__c = GenericHelper_CRMRetail_H_HUM.serializeJSONStructure(mpIndicators,System.label.CRMRetail_Vstr_Indicator_Order);                                            
            }
        }else{
            objInteraction.Storefront_Vstr_Indicator__c = taskInt.Storefront_Vstr_Indicator__c;
        }
        return objInteraction;
    }
    
    /*
    * Method Name   :    insertInteractions
    * Description   :    This method is used to check the notification checkboxes for task management interaction
    * Return Type   :    Void
    * Parameters    :    List<Storefront_Interaction__c> - List of interactions to be inserted on click of contacted button,
    *                    Storefront_Interaction__c - matching visitor check-in interaction instance to be created,
    */
    @testVisible
    private static void insertInteractions(List<Storefront_Interaction__c> lstInteractionToIns)        
    {
        recordsForUpsert.addAll(lstInteractionToIns);                       
        List<Task> lstOfTask = new List<Task>();        
        objTask.Status = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_STATUS;
        objTask.Completed_Reason__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_REASON;        
        lstOfTask.add(objTask);   
        CRM_GenericHelper_H_HUM.accessibilityOutput objAccessOutput = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.UPDATABLE, lstOfTask);        
        if(objAccessOutput.isObjectAccessible)
        {
            List<string> objAccessfields = new List<String>(objAccessOutput.fieldsNotAccessible);
            if(objAccessOutput.areFieldsAccessible || (objAccessOutput.fieldsNotAccessible.size() ==1 && objAccessfields[0] == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ACCOUNTID)){                 
                if(recordsForUpsert.size()>0){
                    recordsForUpsert.addAll(lstOfTask);
                    Database.UpsertResult[] lstSaveResult = Database.upsert(recordsForUpsert,false);
                }
            }            
            else
            {                
                Set<String> removedFields =  objAccessOutput.fieldsNotAccessible;                                
                HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK + ' - ' +removedFields,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_CONTACTED_CLASS,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INSERTINTERACTIONS,'','');                                    
                mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK + ' - ' +removedFields);
            }            
        }
        else
        {            
            mapOfKeyToNotificationData.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK);
            HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INSERTINTERACTIONS,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INSERTINTERACTIONS,'','');                                
        }        
    }
    
    /*
    * Method Name   :    acknowledgeNotifications
    * Description   :    This method creates the notification interaction on acknowledgement
    * Return Type   :    Boolean
    * Parameters    :    String inputJSON - the interaction,notification and account JSON,List<String> accIds -account ids ,String currentLocation - location name
    */
    @AuraEnabled
    public static boolean acknowledgeNotifications(String inputJSON,List<String> accIds,String currentLocation){        
        Boolean isAcknowledged;
        isAcknowledged = CRMRetail_HomePage_LC_HUM.acknowledgeNotifications(inputJSON, accIds, currentLocation);        
        return isAcknowledged;
    }
    
}