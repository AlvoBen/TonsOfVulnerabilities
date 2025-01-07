/*******************************************************************************************************************************
Apex Class Name : StandardQuickFindLinks_LT_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : This Class serves as test class to StandardQuickFindLinks_LC_HUM. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                             10/14/2020                initial version
* Ankima Srivastava										   03/19/2021				 fix to increase code coverage
*********************************************************************************************************************************/
@isTest
public with sharing class StandardQuickFindLinks_LT_HUM 
{
	/**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
        
        Link_Layout__c objlnklayout = new Link_Layout__c();
        objlnklayout.Target_Name__c = 'Account.Member';
        objlnklayout.Target_Type__c = 'sObject';
        objlnklayout.Active__c = true;
        insert objlnklayout;
        system.assertNotEquals(Null,objlnklayout.id);
        system.assertequals('Account.Member',objlnklayout.Target_Name__c);
        
        Link_Layout_Section__c lnklayoutsec1 = new Link_Layout_Section__c();
        lnklayoutsec1.Index__c = 1;
        lnklayoutsec1.Link_Layout__c = objlnklayout.id;
        lnklayoutsec1.Section_Name__c = 'Employer';
        insert lnklayoutsec1;
        system.assertNotEquals(Null,lnklayoutsec1.id);
        system.assertequals('Employer',lnklayoutsec1.Section_Name__c);
        
        Link__c objlnk = new Link__c();
        objlnk.Active__c = true;
        objlnk.Index__c = 1;
        objlnk.Layout_Section__c = lnklayoutsec1.Id;
        objlnk.Link_Action__c = 'Basic';
        objlnk.Link_Label__c = 'Google';
        objlnk.Target_Type__c = 'Window';
        objlnk.Url__c = 'https://www.google.com';
        objlnk.RequireSSO__c =true;
        insert objlnk;
        system.assertNotEquals(Null,objlnk.id);
        system.assertequals('https://www.google.com',objlnk.Url__c );
    }
    
    /**
    * Method to create test interactions retrival
    *<p>
    * @param            NULL
    * @return           void
    */
    private static testMethod void testLinks()
    {
        User objUser = [Select Id,Name from User LIMIT 1];
        system.runAs(objUser)
        {
            List<Link__c> lstLinks = StandardQuickFindLinks_LC_HUM.getLinks('Account.Member'); 
            system.assertequals(1, lstLinks.size() );
        }
    }
}