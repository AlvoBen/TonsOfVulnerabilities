/******************************************************************************************************************************
Apex Class Name  : CRMRetail_VisitorInformation_LC_HUM
Version          : 1.0 
Created Date     : 06/25/2022
Function         : Controller to handle New Visitor Creation.
Modification Log :
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Mohamed Thameem	                              06/25/2022          Original Version - Home Page Modernization
*  Vinoth L                                       11/28/2022          REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront
*  Vinoth L										  06/24/2023		  User Story 4769775: - T1PRJ0154546 / SF / MF9 Storefront - Carry Forward First Time Visitor to Event Interactions
*********************************************************************************************************************************/
public with sharing class CRMRetail_VisitorInformation_LC_HUM {
    public static Id parentId;
    public class controllerException extends Exception {}     
    
    /*
    * Method Name   :    getVisitorInfoPageData
    * Description   :    Fetch all the details while loading a component.
    * Return Type   :    VisitorPageInfo_Wrapper
    * Parameters    :    --
    */
    @AuraEnabled
    public static VisitorPageInfo_Wrapper getVisitorInfoPageData()
    {
        VisitorPageInfo_Wrapper VisitorPageInfo = new VisitorPageInfo_Wrapper();
        VisitorPageInfo.stateList = CRMRetail_HomePage_LC_HUM.fetchStateValue();
        VisitorPageInfo.notificationList = getNotificationOptOutMultiPiklistValues();
        VisitorPageInfo.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
        VisitorPageInfo.switchMap = CRMRetail_HomePage_LC_HUM.fetchSwitchResults();
        
        if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE) || Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE) == Null
           || !Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION) || Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION) == Null)
        {
            VisitorPageInfo.cacheError = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CACHERESERT_ERROR;
        }
        
        return VisitorPageInfo;
    }
    
    /*
    * Method Name   :    handleNewVirtualVisitor
    * Description   :    handling new virtual visitor.
    * Return Type   :    Storefront_NotificationsWrapper_HUM
    * Parameters    :    Account account
    */
    @AuraEnabled
    Public Static Storefront_NotificationsWrapper_HUM handleNewVirtualVisitor (Account account) {
        Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId=new Map<String,Storefront_NotificationsWrapper_HUM>();
        
        Try 
        {       
            if(account.parentId != NULL){                
                parentId = account.parentId;
            }            
            account.parentId = null;
            account.GCM_Visitor_Barcode__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VIRTUAL_VISITOR_ID;
            CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, new List<account>{account}); 
          
            if(accessbilityObj.exceptionGenerated)
            {        
                HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITOR_METHOD);
                Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITOR_METHOD, accessbilityObj.exceptionToLog.getMessage());
            }
            else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
            {
                insert account;
                mapOfNotificationsWrapToAccId = newVirtualVisitorInteraction(account);
            }            
            else
            {
                String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT;
                String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
                String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
                HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITOR_METHOD,'','');
                Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITOR_METHOD, logError);
            }
        } 
        Catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITOR_METHOD);
            Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITOR_METHOD, ex.getMessage());
        }
        Return mapOfNotificationsWrapToAccId.values().get(0);
    }
    
    
    /*
    * Method Name   :    newVirtualVisitorInteraction
    * Description   :    to create new Virtual Visitor Interaction.
    * Return Type   :    Map<String,Storefront_NotificationsWrapper_HUM>	
    * Parameters    :    Account accRef
    */
    @AuraEnabled
    Public Static Map<String,Storefront_NotificationsWrapper_HUM> newVirtualVisitorInteraction (Account accRef) {
        Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId = new Map<String,Storefront_NotificationsWrapper_HUM>();
        Map<String,Map<String,String>> mapOfReqValforNotificationByAccId = new Map<String,Map<String,String>>();
        Map<String,String> mapOfVistorCatByAccId = new Map<String,String>();
        Map<String,String> mapOfVistorTypeByAccId = new Map<String,String>();
        String currLocationId = '';
        String interactionDate = '';
        String currLocationType = '';
        List<Storefront_Notification__C> lstOfNotification = new List<Storefront_Notification__C>();
        Try
        {
            Map<Id,Account> mapOfIdToAccount = new Map<Id,Account>();
            String sCurrLoc = CRMRetail_HomePage_LC_HUM.getorSetLocationInCache('',false);
            DateTime utcDateTime = CRMRetail_HomePage_LC_HUM.getUTCDateTime(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DISPLAYDATE);
            interactionDate = string.valueOf(date.newinstance(utcDateTime.year(), utcDateTime.month(), utcDateTime.day()));
            List<Storefront_Location__c> currLoc = [SELECT Id, Maximum_Occupancy__c,Location_Type__C FROM Storefront_Location__c WHERE Name =: sCurrLoc LIMIT 1];
            currLocationType = currLoc.get(0).Location_Type__C;
            currLocationId = currLoc.get(0).Id;
            List<Storefront_I_Reason__c> firstTimeCheckin = [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN];
            Storefront_Interaction__c intObj = new Storefront_Interaction__c();
            IntObj.Reason__c = firstTimeCheckin[0].Id;
            IntObj.Location__c = currLoc[0].Id;
            IntObj.Interaction_Date__c = utcDateTime;
            IntObj.Account__c = accRef.Id;
            IntObj.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
            IntObj.Visitor_ID__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VIRTUAL_VISITOR_ID;
            IntObj.CRM_Retail_Interaction_Visitor_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER;
            if(!HUMUtilityHelper.isCRMFunctionalityON('4769775')){
                IntObj.First_Time_Visitor__c = true; 
            }
            Map<String,Object> mpOfVstrIndicators = new Map<String,Object>();
            if(HUMUtilityHelper.isCRMFunctionalityON('4012388')){
                mpOfVstrIndicators.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_FRSTTIMEVSTR_TEXT,true);             
                IntObj.Storefront_Vstr_Indicator__c = JSON.serialize(mpOfVstrIndicators);
            }
            mapOfVistorCatByAccId.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY,intObj.Category__c);
            mapOfReqValforNotificationByAccId.put(accRef.Id,mapOfVistorCatByAccId);
            mapOfReqValforNotificationByAccId.get(accRef.Id).put( GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN);
            mapOfVistorTypeByAccId.put(accRef.Id,IntObj.CRM_Retail_Interaction_Visitor_Type__c);
            String todayDate = String.valueOf(Date.today());
            mapOfIdToAccount.put(accRef.Id,accRef);
            if(parentId != NULL){
                IntObj.inactiveMember__c = parentId; 
            }
            if(mapOfReqValforNotificationByAccId.size()>0 && mapOfVistorTypeByAccId.size()>0 && currLocationId!=NULL && interactionDate!=NULL && interactionDate==todayDate && currLocationType != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL){
                mapOfNotificationsWrapToAccId=new Storefront_Notification_Helper_HUM().generateNotificationData(mapOfReqValforNotificationByAccId, mapOfVistorTypeByAccId, currLocationId,interactionDate,mapOfIdToAccount);
                if(mapOfNotificationsWrapToAccId==NULL || mapOfNotificationsWrapToAccId.size()==0){
                    Storefront_NotificationsWrapper_HUM tempWrap=new Storefront_NotificationsWrapper_HUM();         
                    tempWrap.accountRec=accRef;
                    mapOfNotificationsWrapToAccId.put(accRef.Id,tempWrap);
                }
            }else{
                Storefront_NotificationsWrapper_HUM tempWrap=new Storefront_NotificationsWrapper_HUM();         
                tempWrap.accountRec=accRef;
                mapOfNotificationsWrapToAccId.put(accRef.Id,tempWrap);
            }
            if(currLocationType == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL){
                lstOfNotification = new Storefront_Notification_Helper_HUM().retrieveListOfNotification(currLocationId);			                                        	
                if(lstOfNotification != NULL && lstOfNotification.size() > 0){
                    new Storefront_Notification_Helper_HUM().checkNotificationBox(IntObj,lstOfNotification,mapOfIdToAccount);
                }      
            }
            else if(Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.containsKey(IntObj.Account__c)){
                IntObj.Veteran__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(IntObj.Account__c).Veteran__c;                    
                IntObj.Aging_In__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(IntObj.Account__c).Aging_In__c;
                IntObj.New_Member__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(IntObj.Account__c).New_Member__c;
                IntObj.SDoh__c = Storefront_Notification_Helper_HUM.mapOfAccIdToInteractions.get(IntObj.Account__c).SDoh__c;
            }
            
            CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, new List<Storefront_Interaction__c>{IntObj}); 
            
            if(accessbilityObj.exceptionGenerated)
            {        
                HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITORINTERACTION_METHOD);
                Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITORINTERACTION_METHOD, accessbilityObj.exceptionToLog.getMessage());
            }
            else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
            {
                insert IntObj;
            }            
            else
            {
                String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SINTERACTION;
                String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
                String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
                HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITORINTERACTION_METHOD,'','');
                Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITORINTERACTION_METHOD, logError);
            }
            
            
        } 
        Catch(Exception ex) {
            system.debug(ex);
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITORINTERACTION_METHOD);
            String error = CRMRetailVNC_Helper_HUM.getError(ex.getMessage());
            Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITORINTERACTION_METHOD,error);
        }
        return mapOfNotificationsWrapToAccId;
    }
      
    /*
    * Method Name   :    getNotificationOptOutMultiPiklistValues
    * Description   :    to get the picklist values.
    * Return Type   :    List <String>
    * Parameters    :    --
    */
    public static List <String> getNotificationOptOutMultiPiklistValues() {
        List<String> multiPickListValues = new List<String>();
        Schema.SObjectType objType = Schema.getGlobalDescribe().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT);
        Schema.DescribeSObjectResult objDescribe = objType.getDescribe();
        Schema.DescribeFieldResult objFieldInfo = objDescribe.fields.getMap().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NOTIFICATIONOPTOUT).getDescribe();
        List<Schema.PicklistEntry> picklistvalues = objFieldInfo.getPicklistValues();
        for(Schema.PicklistEntry plv: picklistvalues) {
            multiPickListValues.add(plv.getValue());
        }
        multiPickListValues.sort();
        return multiPickListValues;
    }
    
    /*
    * Method Name   :    logUIException
    * Description   :    This method to create a Error log for JS exceptions
    * Return Type   :    void
    * Parameters    :    String errMsg
    */
    @AuraEnabled
    public static void logUIException(String errMsg)
    {   
        AuraHandledException ex = New AuraHandledException(errMsg);
        ex.setMessage(errMsg);
        HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MODERNIZED_LOGUIEXCEPTION_METHOD);   
    }
    
    /*
    * Wrapper Name   :   VisitorPageInfo_Wrapper
    * Description   :    to return all the values when component get loaded.
    * Parameters    :    --
    */
    public class VisitorPageInfo_Wrapper
    {
        @AuraEnabled
        public list<string> stateList{get;set;}
        @AuraEnabled
        public list<string> notificationList{get;set;}
        @AuraEnabled
        public String recordTypeId{get;set;}
        @AuraEnabled
        public Map<String,Boolean> switchMap{get;set;}
        @AuraEnabled
        public String cacheError{get;set;}
        
        public VisitorPageInfo_Wrapper(){}
    }
}