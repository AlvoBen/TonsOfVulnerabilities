/**********************************************************************************************************************
Apex Class: HUMCaseActionRelatedListControllerTest
Description: Test class for HUMCaseActionRelatedListController
Created By: Chaitanya Kumar
Created On:  July 14 2014

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer         Code Review         Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Chaitanya Kumar    17353              08/14/2014               Original version
* Chaitanya Kumar    17840              09/10/2014               Made changes according to Humana standards 
* Santhi Mandava     20576              01/28/2015               Code Modified to fix test class failure Defect # 164139 
* Santhi Mandava                        03/12/2019               Removed "DpaaSWebServiceMockImpl" usage.  
***********************************************************************************************************************/
@isTest
private class HUMCaseActionRelatedListControllerTest {
    
     /*
    * Method Name : constructorPositive
    * Description : Initialize variables
    * Return type : Void
    * Paramater   : NA
    */
    static testMethod void constructorPositive() 
    {
      Case oCase =HUMTestDataHelperTest.getCase();
      insert oCase;
      
      Case_Action__c caseAction = HUMTestDataHelperTest.getCaseAction('Test Template');
      insert caseAction;
      
      Case_Action_Association__c caseAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
      caseAssociation.Case_Action__c =caseAction.id;
      caseAssociation.case__c=oCase.Id;
      insert caseAssociation;
      
      ApexPages.Standardcontroller sc = new ApexPages.StandardController(oCase);
      HUMCaseActionRelatedListController casecontroller;
      
      Test.StartTest();
         casecontroller= new HUMCaseActionRelatedListController (sc);
      Test.stopTest();
      
      System.assertEquals(caseController.listProcess.size(),1);
        
    }
}