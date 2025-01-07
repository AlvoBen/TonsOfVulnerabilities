/*******************************************************************************************************************************
Apex Class Name 	: SoftphoneLinksTriggerHandler_H_HUM 
Version           	: 1.0
Created Date    	: August 11 2019
Function          	: This serves as Handler for SoftphoneLinks Trigger.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj           	                       	 08/11/2019                 
*******************************************************************************************************************************/
public class SoftphoneLinksTriggerHandler_H_HUM 
{
    /**
    * processBeforeDelete
    * <p>
    * Allow sites to delete only when there are no agents related to site.
    * @param  List<SoftphoneLinks__c>    lstSoftphoneLinks
    * @return Void
    **/ 
    public static void processBeforeDelete(List<SoftphoneLinks__c> lstSoftphoneLinks) 
    {
        for(SoftphoneLinks__c objSFL : lstSoftphoneLinks) 
        {
            if(objSFL.Agent__c > 0) 
            {
                objSFL.addError(GLOBAL_CONSTANT_HUM.SOFTPHONE_LINKS_ERROR_MESSAGE);
            }
        }
    }

    /**
    * processBeforeInsert
    * <p>
    * populate Default_Server_Agents__c on Links object.
    * @param  List<SoftphoneLinks__c>    lstSoftphoneLinks
    * @return Void
    **/ 
    public static void processBeforeInsert(List<SoftphoneLinks__c> lstSoftphoneLinks) 
    {
        Map<String, Integer> SiteServerCountMap = new Map<String,Integer>();
        Map<Id,Softphone_Sites__c> siteExtIdMap = null;
        Set<Id> setSites = new Set<Id>();
        Set<String> setServers = new Set<String>();
        for(SoftphoneLinks__c objSFL : lstSoftphoneLinks) 
        {
            setSites.add(objSFL.Sites__c);
            setServers.add(objSFL.ServerName__c);
            SiteServerCountMap.put(objSFL.Sites__c+'|'+objSFL.ServerName__c,0);
        }
      
        Integer count;
        for(SoftphoneLinks__c objSFL : [SELECT Sites__c, Sites__r.Name, ServerName__c,Default_Server_Agents__c,Agent__c 
                                        FROM SoftphoneLinks__c WHERE ServerName__c in :setServers AND Sites__c in :setSites])
        {     
            count =0;
            if(null != SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c))
            {
                count = SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c);
                SiteServerCountMap.put(objSFL.Sites__c+'|'+objSFL.ServerName__c,Integer.valueof(objSFL.Agent__c)+count );
            }            
        }
        siteExtIdMap = new Map<Id,Softphone_Sites__c>([SELECT Id, Name FROM Softphone_Sites__c WHERE Id IN: setSites]);
        for(SoftphoneLinks__c objSFL : lstSoftphoneLinks) 
        {
            objSFL.Default_Server_Agents__c = SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c);
            objSFL.Softphone_External_Id__c = objSFL.Name + '|' + siteExtIdMap.get(objSFL.Sites__c).Name;
        }        
    }

    /**
    * processBeforeUpdate
    * <p>
    * populate Default_Server_Agents__c on Links object.
    * @param  List<SoftphoneLinks__c>    lstSoftphoneLinks,Map<Id,SoftphoneLinks__c > mapOldLinks
    * @return Void
    **/ 
    public static void processBeforeUpdate(List<SoftphoneLinks__c> lstSoftphoneLinks, Map<Id,SoftphoneLinks__c > mapOldLinks) 
    {
        TriggerAvoidRecursion_H_HUM.run = false;
        Map<String, Integer> SiteServerCountMap = new Map<String,Integer>();
        Set<Id> setSites = new Set<Id>();
        Set<String> setServers = new Set<String>();
        List<SoftphoneLinks__c> ltsLinksToUpdate= new List<SoftphoneLinks__c>();
        Integer count;
        for(SoftphoneLinks__c  objSFL : lstSoftphoneLinks) 
        {
            setServers.add(objSFL.ServerName__c);
            setSites.add(objSFL.Sites__c);
            if (objSFL.Sites__c != mapOldLinks.get(objSFL.Id).Sites__c) 
            {
                setSites.add(mapOldLinks.get(objSFL.Id).Sites__c);
            }
            
            if (objSFL.ServerName__c != mapOldLinks.get(objSFL.Id).ServerName__c) 
            {
                setServers.add(mapOldLinks.get(objSFL.Id).ServerName__c);
            }
            count = 0;
            if( null != SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c))
            {
                count = SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c);                
            } 
           SiteServerCountMap.put(objSFL.Sites__c+'|'+objSFL.ServerName__c,Integer.valueof(objSFL.Agent__c)+count );
        }
        
        for(SoftphoneLinks__c objSFL: [select name,Sites__c,ServerName__c,Default_Server_Agents__c,Agent__c 
                                       from SoftphoneLinks__c where ServerName__c in :setServers and Sites__c in :setSites and ID Not IN :lstSoftphoneLinks])
        {     
            count =0;
            if( null != SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c))
            {
                count = SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c);                
            } 
            SiteServerCountMap.put(objSFL.Sites__c+'|'+objSFL.ServerName__c,Integer.valueof(objSFL.Agent__c)+count );
            ltsLinksToUpdate.add(objSFL);
        }
        for(SoftphoneLinks__c objSFL : lstSoftphoneLinks) 
        {
            objSFL.Default_Server_Agents__c = SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c);
        }
        for(SoftphoneLinks__c objSFL : ltsLinksToUpdate) 
        {
            objSFL.Default_Server_Agents__c = SiteServerCountMap.get(objSFL.Sites__c+'|'+objSFL.ServerName__c);            
        }
        if(!ltsLinksToUpdate.isEmpty())
            update ltsLinksToUpdate;        
    }
}