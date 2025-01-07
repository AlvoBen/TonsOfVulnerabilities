/*******************************************************************************************************************************
Apex Class Name     : AgentTriggerHandler_H_HUM 
Version             : 1.0
Created Date        : August 09 2019
Function            : This serves as Handler for Agent Trigger.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni                                          08/09/2019                 
*******************************************************************************************************************************/
public class AgentTriggerHandler_H_HUM 
{
    /**
    * processBeforeInsertAndUpdate
    * <p>
    * Prepopulate unique id on the agent.
    *
    * @param  List<case>    lstNewAgent
    * @return Void
    **/ 
    public static void processBeforeInsertAndUpdate(List<Softphone_Agent__c > lstNewAgent) 
    {
        for(Softphone_Agent__c  objAgent : lstNewAgent) 
        {
            if(!objAgent.Integration_Update__c && objAgent.Id == null) 
            {
                objAgent.DB2_Status__c = GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_INSERT;
            } 
            else if(!objAgent.Integration_Update__c && objAgent.Id != null) 
            { 
                string oldStatus = (string)trigger.oldmap.get(objAgent.Id).get('DB2_Status__c');  
                if(!objAgent.DB2_Sync__c && objAgent.DB2_Status__c != GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_DELETE) 
                {
                    objAgent.DB2_Status__c = GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_INSERT;
                }               
                else if(GLOBAL_CONSTANT_HUM.SOFTPHONE_STATUS.contains(oldStatus) && objAgent.DB2_Status__c != GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_DELETE) 
                {
                    objAgent.DB2_Status__c = GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_UPDATE;
                } 
                else if(objAgent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_DELETE) 
                {
                    objAgent.DB2_Status__c = GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_DELETE;
                } 
                else 
                {
                    objAgent.DB2_Status__c = oldStatus;
                }
            } 
            else if(objAgent.Integration_Update__c && (objAgent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED || objAgent.DB2_Status__c.contains(GLOBAL_CONSTANT_HUM.SOFTPHONE_FAILED))) 
            {
                objAgent.Integration_Update__c = false;
            }
        }
    }
    
    /**
    * processBeforeDelete
    * <p>
    * If its a manual user delete add error, agent record can be deleted only from code post sync with DB2.
    *
    * @param  List<case>    lstNewAgent
    * @return Void
    **/ 
    public static void processBeforeDelete(List<Softphone_Agent__c > lstOldAgent) 
    {
        for(Softphone_Agent__c  objAgent : lstOldAgent) 
        {
            if(objAgent.DB2_Sync__c && !objAgent.Integration_Update__c) 
            {
                objAgent.addError(GLOBAL_CONSTANT_HUM.SOFTPHONE_ERROR_MESSAGE);
            }
        }
    }
    
    /**
    * updateNoOfAgentsSoftLink
    * <p>
    * Count no of Agents per softphone links
    *
    * @param  List<case>    lstNewAgent
    * @return Void
    **/ 
    public static void updateNoOfAgentsSoftLink(List<Softphone_Agent__c > lstNewAgent,Map<Id,Softphone_Agent__c > mapOldAgents) 
    {   
        Set<ID> sftLinkID = new Set<ID>();
        Map<Id,SoftphoneLinks__c> mapSoftPhoneLink = new Map<Id,SoftphoneLinks__c>();
        list<Softphone_Agent__c> lstDelAgents = new list<Softphone_Agent__c>();

        for(Softphone_Agent__c  objAgent : lstNewAgent) 
        {
            if(mapOldAgents == null) 
            {
                sftLinkID.add(objAgent.Default_SoftphoneLinks__c);
            } 
            else if (objAgent.Default_SoftphoneLinks__c != mapOldAgents.get(objAgent.Id).Default_SoftphoneLinks__c) 
            {
                sftLinkID.add(mapOldAgents.get(objAgent.Id).Default_SoftphoneLinks__c);
                sftLinkID.add(objAgent.Default_SoftphoneLinks__c);
            }             
        }
        
        if(!sftLinkID.isEmpty()) 
        {
            for(AggregateResult objAG : [SELECT Count(Id), Default_SoftphoneLinks__c FROM Softphone_Agent__c  
                                          WHERE Default_SoftphoneLinks__c IN :sftLinkID Group By Default_SoftphoneLinks__c]) 
            {
                mapSoftPhoneLink.put(String.ValueOf(objAG.get('Default_SoftphoneLinks__c')),
                                     new SoftphoneLinks__c(Id=String.ValueOf(objAG.get('Default_SoftphoneLinks__c')),
                                                           Agent__c =Double.ValueOf(objAG.get('expr0'))));                              
            }
            
            for(Id sflId : sftLinkID) 
            {
                if(!mapSoftPhoneLink.containsKey(sflId)) 
                {
                    mapSoftPhoneLink.put(sflId, new SoftphoneLinks__c(Id=sflId,Agent__c=0));
                }
            }
            
            if(!mapSoftPhoneLink.values().isEmpty()) 
            {
                update mapSoftPhoneLink.values();
            }
        }
    }
}