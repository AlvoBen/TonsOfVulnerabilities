/*******************************************************************************************************************************************************
Apex Class Name  : DynamicLinksWrapper_H_HUM
Version          : 1.0
Created Date     : 11/04/2015
Function         : This is wrapper class to display links on side panel.
Test Class       : DynamicLinksWrapper_HT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            28356                   11/04/2015                  Original Version
* Manish Kumar                                      02/09/2016                  Added Link__c variable
************************************************************************************************************************************************/
public without sharing class DynamicLinksWrapper_H_HUM
{
    /*
    * Method Name : DynamicLinksWrapper_H_HUM
    * Description : This is constructor is used to initialize variables for this class
    * Return type : NA
    * Parameter   : NA
    */
    public DynamicLinksWrapper_H_HUM()
    {
        LinkID='';
        Link = NULL;
        LinkLabel='';
    }
    
    public string LinkID{get;set;}
    public Link__c Link{get;set;}
    public string LinkLabel{get;set;}
}