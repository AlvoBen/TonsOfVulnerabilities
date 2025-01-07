/******************************************************************************************************************************
Apex Class Name  : Storefront_InteractionsWrapper_HUM
Version          : 1.0 
Created Date     : 04/10/2020 

Modification Log :
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Melkisan Selvaraj                             04/10/2020          801835 & 801756 CRM Storefront Stories - Original Version
*  Luke P. Cecil                                 07/24/2020          User Story 1243084: PR00091869 - MF 11 - CRM Storefront Virtual Visitors: Interaction Category Validation
*  Luke P. Cecil                                 08/03/2020          REQ 1243084 There is no duplicate flag for visitor check -in's 
*********************************************************************************************************************************/
public with sharing class Storefront_InteractionsWrapper_HUM 
{
    @AuraEnabled Public Storefront_Interaction__c interaction;
    @AuraEnabled Public String response = '';
    @AuraEnabled Public String newResponse = '';
    @AuraEnabled Public String dupResponse = '';
    @AuraEnabled Public List<String> dupResponseList = new List<String>();
    @AuraEnabled public Storefront_NotificationsWrapper_HUM newNotifiWrap;
    @AuraEnabled public List<Storefront_NotificationsWrapper_HUM> newNotifiWrapList;
    
    
    Public Storefront_InteractionsWrapper_HUM(Storefront_Interaction__c sfInteraction, String visitor, String reason, Boolean isInserted,Storefront_NotificationsWrapper_HUM notifiWrap)
    {
        interaction = sfInteraction;
        if(sfInteraction.Storefront_Event_Starttime__c != null)
        {
            DateTime startTime = Datetime.valueOfGMT(string.valueof(sfInteraction.Storefront_Event_Starttime__c));
            
            if(isInserted)
                response = visitor+' is already assigned to '+reason+'.';
            else
                response = visitor+' is successfully assigned to '+reason+'.';
        }
        else
        {
            if(isInserted)
                response = visitor+' is already assigned to '+reason+'.';
            else
                response = visitor+' is successfully assigned to '+reason+'.';
        }
        newNotifiWrap= notifiWrap;
        
    }   
    
    Public Storefront_InteractionsWrapper_HUM(Map<Id,List<Storefront_Interaction__c>> visitorIntMap, map<id,string> utilityMap, map<string, boolean> dupCheckMap,List<Storefront_NotificationsWrapper_HUM> notifiWraplst)
    {
        String reasonDupMsg = '';
        String reasonNewMsg = '';
        string category = '';
        Boolean Switch_1243084 = HUMUtilityHelper.isCRMFunctionalityON('1243084');
        for(id visitorId : visitorIntMap.keyset())
        {
            reasonDupMsg = '';
            string key = '';
            for(Storefront_Interaction__c sfInt : visitorIntMap.get(visitorId))
            {
                if(Switch_1243084)
                {
                    if(string.valueof(sfInt.Category__c) == null)
                        category = '';
                    else
                         category = string.valueOf(sfInt.Category__c);
                     key = string.valueOf(sfInt.Interaction_Date__c.DateGMT())+
                                string.valueOf(sfInt.Reason__c) + string.valueOf(sfInt.Location__c)+ string.valueOf(sfInt.Account__c)+ string.valueOf(sfInt.Visitor_ID__c)+
                                string.valueOf(sfInt.Storefront_Event_Starttime__c) + category;  
                }
                else
                {
                     key = string.valueOf(sfInt.Interaction_Date__c.DateGMT())+
                                string.valueOf(sfInt.Reason__c) + string.valueOf(sfInt.Location__c)+ string.valueOf(sfInt.Account__c)+ string.valueOf(sfInt.Visitor_ID__c)+
                                string.valueOf(sfInt.Storefront_Event_Starttime__c);
                }
                if(dupCheckMap.get(key))
                {
                    if(!reasonDupMsg.contains(utilityMap.get(sfInt.Reason__c))){
                        reasonDupMsg = reasonDupMsg+', '+utilityMap.get(sfInt.Reason__c)+', ';
                        reasonDupMsg = reasonDupMsg.removeEnd(', ');
                        reasonDupMsg = reasonDupMsg.removeStart(', ');
                    }
                } 
                else
                {
                    if(!reasonDupMsg.contains(utilityMap.get(sfInt.Reason__c))){
                        reasonNewMsg = reasonNewMsg+', '+utilityMap.get(sfInt.Reason__c)+', ';
                        reasonNewMsg = reasonNewMsg.removeEnd(', ');
                        reasonNewMsg = reasonNewMsg.removeStart(', ');
                    }
                    newResponse = utilityMap.get(sfInt.Account__c)+' is successfully assigned to '+reasonNewMsg+'.';
                }
            }
            if(reasonDupMsg != '')
            {
                dupResponse = utilityMap.get(visitorId)+' is already assigned to '+reasonDupMsg+'.';
				dupResponseList.add(dupResponse);
            }
        }
        newNotifiWrapList= notifiWraplst;
    }
}