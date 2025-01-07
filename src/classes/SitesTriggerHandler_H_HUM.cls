/*******************************************************************************************************************************
Apex Class Name     : SitesTriggerHandler_H_HUM 
Version             : 1.0
Created Date        : August 11 2019
Function            : This serves as Handler for Agent Trigger.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                      08/11/2019                 
*******************************************************************************************************************************/
public class SitesTriggerHandler_H_HUM 
{
    /**
    * processBeforeDelete
    * <p>
    * Allow sites to delete only when there are no agents related to site.
    * @param  List<Softphone_Sites__c>    lstOldSites
    * @return Void
    **/ 
   public static void processBeforeDelete(List<Softphone_Sites__c> lstOldSites) 
   {
        for(Softphone_Sites__c objSite : lstOldSites) 
		{
            if(objSite.Agents__c > 0 || objSite.SoftphoneLinks__c > 0) 
			{
                objSite.addError(GLOBAL_CONSTANT_HUM.SOFTPHONE_SITES_ERROR_MESSAGE);
            }
        }
    }
    
    /**
    * processBeforeUpdateAndInsert
    * <p>
    * populate external Id on sites object.
    * @param  List<Softphone_Sites__c>    lstOldSites
    * @return Void
    **/ 
    public static void processBeforeUpdateAndInsert(List<Softphone_Sites__c> lstNewSites) 
    {
        for(Softphone_Sites__c objSite : lstNewSites) 
        {
            objSite.Sites_External_Id__c = objSite.Name;
        }
    }
}