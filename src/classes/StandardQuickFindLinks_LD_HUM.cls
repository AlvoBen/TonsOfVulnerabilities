/*******************************************************************************************************************************
Apex Class Name : StandardQuickFindLinks_LD_HUM
Version         : 1.0
Created On      : 03/03/2021
Function        : This Class serves to fetch quick find links for account record types. 
Test Class      : StandardQuickFindLinks_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Joel George                                            03/03/2021               initial version
* Kajal Namdev                                           05/05/2021                Add query limit in SOQL
*********************************************************************************************************************************/
public with sharing class StandardQuickFindLinks_LD_HUM 
{
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    public static List<Link__c> getLinks(string sKey)
    {
        List<Link__c> retLinks;        
        if(string.isNotBlank(sKey)){
            sKey = (sKey == 'Account.Group') ? 'Account.DisplayCustomLinksGA_VF_HUM' : sKey;
            retLinks = [SELECT Id, OwnerId, IsDeleted, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, 
                        SystemModstamp, LastViewedDate, LastReferencedDate, Active__c, Expression__c, Index__c, Layout_Section__c, 
                        Link_Action__c, Link_Label__c, RequireSSO__c, Target_Type__c, Url__c, Display_Hover_Over__c, 
                        Hover_Over_Text__c, Icon_Path__c, DefaultUrl__c 
                        FROM Link__c 
                        WHERE Layout_Section__r.Link_Layout__r.Target_Name__c=:sKey.escapeHtml4() ORDER BY Layout_Section__r.Index__c ASC, Index__c ASC LIMIT: limitValue];
        }
        return retLinks;
    }
}