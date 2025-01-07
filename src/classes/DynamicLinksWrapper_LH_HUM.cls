/*******************************************************************************************************************************************************
Apex Class Name  : DynamicLinksWrapper_LH_HUM
Version          : 1.0
Created Date     : 06/02/2021  
Function         : This is wrapper class to display links on side panel.
Test Class       : DynamicLinksWrapper_LT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Alwal                               06/02/2021                  Original Version
************************************************************************************************************************************************/
public without sharing class DynamicLinksWrapper_LH_HUM
{
    /*
    * Method Name : DynamicLinksWrapper_H_HUM
    * Description : This is constructor is used to initialize variables for this class
    * Return type : NA
    * Parameter   : NA
    */
    public DynamicLinksWrapper_LH_HUM()
    {
        LinkID='';
        Link = NULL;
        LinkLabel='';
    }
    
    @AuraEnabled public  string LinkID{get;set;}
    @AuraEnabled public  Link__c Link{get;set;}
    @AuraEnabled public  string LinkLabel{get;set;}
}