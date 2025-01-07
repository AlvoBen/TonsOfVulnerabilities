/******************************************************************************************************************************
Apex Class Name  : CRMRetail_FirstTimeVisitor_B_HUM
Version          : 1.0 
Created Date     : 07/10/2023
Function         : Batch to identify & repopulate the first time attended events
Test Class 	     : CRMRetail_FirstTimeVisitor_T_HUM
Modification Log :
* Developer              Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Mohamed Thameem      	                	    07/10/2023           US - 4807673 : T1PRJ0154546 / SF / MF9 Storefront - One-Time Update of Event Interactions
* Vinoth L										07/24/2023			 US - 4891744 : T1PRJ0154546 / SF / MF9 Storefront - Additional Interaction Types to be excluded from First Time Visitor Logic
* Mohamed Thameem      	                	    08/01/2023           US - 4807673 : Code Fix
*********************************************************************************************************************************/
public class CRMRetail_FirstTimeVisitor_B_HUM implements Database.Batchable<sObject>,Database.stateful
{
    public String errorMesg = '\n\n';
    public Map<Id, String> mapError = new Map<Id, String>();
    public String visitorCheckInFirst = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN;
    List<String> lstOfInteractionTypes = HUMUtilityHelper.isCRMFunctionalityON('4891744') ? System.Label.CRMRetail_NonFirstTime_Interaction_Types.split(';') : (new List<String>{System.Label.CRMRetail_NonFirstTime_Interaction_Types.split(';')[0]});
    
    public Database.QueryLocator start(Database.BatchableContext bc)
    {    
        try
        {
            String inquiryType = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INQUIRY_INTERACTION_TYPE;
            String categoryScheduledVirtual = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL;
            String categoryScheduledOnsite = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE;            
         	String query = 'select id,First_Time_Visitor__c,Account__c,reason__r.Name from storefront_interaction__c where (reason__r.name = :visitorCheckInFirst OR (reason__r.name != :visitorCheckInFirst AND First_Time_Visitor__c = true AND (Storefront_Event_Starttime__c = NULL OR  Category__c = :categoryScheduledVirtual OR Category__c = :categoryScheduledOnsite OR Reason__r.Interaction_Type__r.Name IN: lstOfInteractionTypes))) AND Interaction_Date_Only__c > 2022-12-30';
			return Database.getQueryLocator(query);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CRMRetail_FirstTimeVisitor_B_HUM','start');
            return null;
        }
    }   
    
    public void execute(Database.BatchableContext bc, List<storefront_interaction__c> interaction)
    {    
        set<Id> accountIDs = new set<ID>();
        Map<Id,storefront_interaction__c> mapToUpdate = new Map<id,storefront_interaction__c>();
        Map<String,Date> mapOfAccToDate = new Map<String,Date>();
        CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj;
        
        try
        {
            for(storefront_interaction__c sInt : interaction)
            {
                if(sInt.reason__r.Name == visitorCheckInFirst)
                {
                    accountIDs.add(sInt.Account__c);
                    if(sInt.First_Time_Visitor__c)
                    {
                        sInt.First_Time_Visitor__c = false;
                        mapToUpdate.put(sInt.Id,sInt);
                    }
                }
                else if(sInt.First_Time_Visitor__c)
                {
                    sInt.First_Time_Visitor__c = false;
                    mapToUpdate.put(sInt.Id,sInt);
                }  
            }
            
            if(!accountIDs.isEmpty())        
            {
                For(storefront_interaction__c ic : [SELECT Id, Storefront_Event_Endtime__c, Storefront_Event_Starttime__c, Interaction_Date__c, Account__c, Location__c, Reason__c,Reason__r.name, First_Time_Visitor__c FROM Storefront_Interaction__c where (Storefront_Event_Starttime__c != null AND Storefront_Event_Endtime__c != null AND ( Category__c = '' OR Category__c = 'Virtual') ) AND Reason__r.Interaction_Type__r.Name NOT IN: lstOfInteractionTypes AND (Account__c IN :accountIDs) AND Interaction_Date_Only__c > 2022-12-30 order by Interaction_Date__c asc])
                {
                    if(mapOfAccToDate.containsKey(ic.Account__c))
                    {
                        if(mapOfAccToDate.get(ic.Account__c) == DateTime.valueOfGmt(String.valueof(ic.Interaction_Date__c)).DateGMT())
                        {
                            ic.First_Time_Visitor__c = true;
                            mapToUpdate.put(ic.Id,ic);
                        }
                        else if(ic.First_Time_Visitor__c)
                        {
                            ic.First_Time_Visitor__c = false;
                            mapToUpdate.put(ic.Id,ic);
                        }
                    }
                    else
                    {
                        mapOfAccToDate.put(ic.Account__c ,DateTime.valueOfGmt(String.valueof(ic.Interaction_Date__c)).DateGMT());
                        ic.First_Time_Visitor__c = true;
                        mapToUpdate.put(ic.Id,ic);
                    }
                }
            }
            
            if(!mapToUpdate.isEmpty())
            {
                accessbilityObj = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.UPDATABLE, mapToUpdate.values()); 
                if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
                {	
                    Database.SaveResult[] srList = Database.update(mapToUpdate.values(), false);
                    
                    for(Database.SaveResult sr : srList) 
                    {	
                        if (!sr.isSuccess())
                        {	
                            errorMesg = '';
                            for(Database.Error err : sr.getErrors()) 
                            {	
                                errorMesg+= err.getMessage()+',';  
                            }
                            errorMesg = errorMesg.substringBeforeLast(',');
                            mapError.put(sr.getId(), errorMesg);
                        }
                    }
                }
                else
                {
                    String error = (!accessbilityObj.isObjectAccessible) ? GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SINTERACTION : GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;
                    HUMExceptionHelper.logError(error, 'CRMRetail_FirstTimeVisitor_B_HUM', 'execute', '', '');
                }
            }  
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CRMRetail_FirstTimeVisitor_B_HUM','execute');
        }
    }    
    
    public void finish(Database.BatchableContext bc)
    {
        try
        {
            if(mapError != null && !mapError.isEmpty())
            {	
                String csvRow;
                List<String> csvRowValues = new List<String>();
                String csvColumnHeader = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ERROR_CSV_COLUMN_HEADER;
                for(id interactionId : mapError.keyset())
                {	
                    csvRow = interactionId + ',' + mapError.get(interactionId);
                    csvRowValues.add(csvRow);
                }
                String csvFile = csvColumnHeader + String.join(csvRowValues,'\n');
                ContentVersion cv = new ContentVersion();
                cv.Title = 'US4807673- Storefront:One-Time Update - Date - '+String.valueOf(Date.today());
                cv.PathOnClient = cv.Title + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CSV_FORMAT;
                cv.VersionData = Blob.valueOf(csvFile);
                cv.IsMajorVersion = false;
                Insert cv;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CRMRetail_FirstTimeVisitor_B_HUM','finish');
        }
    }      
}