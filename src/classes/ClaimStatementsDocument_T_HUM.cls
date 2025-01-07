/**************************************************************************** 
Apex Class Name  : ClaimStatementsDocument_T_HUM
Version          : 1.0  
Created Date     : May 19th, 2016
Function         : Test class for ClaimStatementsDocument_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar         27974             05/19/2016                 Original Version            
* Muralidhar Kollu            34263             07/27/2016                 PPV defect D-00473 - added user friendly error message
* Prasanthi Kandula                             11/25/2019                 REQ - 416044 Replaced references for 'MobiusService_GetDocument' webservice callout with MobiusService_GetMobiusDocument
***************************************************************************************************/
 @isTest
 private Class ClaimStatementsDocument_T_HUM
 {
 /*
  * Method Name : prepareTestData
  * Description : Used to create wrapper class data
  * Return type : void
  * Paramater   : NA
  */
  @testSetup static void prepareTestData() 
  {
      User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
      insert oCCSUser;
      system.assertEquals(oCCSUser.LastName,'test User1');
      system.runAs (oCCSUser) 
      {
          HUM_Webservice_Callout__c oMobiusGetDocument = new HUM_Webservice_Callout__c();      
          oMobiusGetDocument.name='MobiusService_GetMobiusDocument';
          oMobiusGetDocument.Active__c=true;
          oMobiusGetDocument.Certificate_Name__c='Humana_GuidanceCare_Venus';
          oMobiusGetDocument.End_Point_URL__c='https://int-crmservicews.humana.com/MobiusService/GetDocument';
          oMobiusGetDocument.Timeout__c='120000';
          oMobiusGetDocument.Content_Type__c='application/json';
          oMobiusGetDocument.Request_Type__c='POST';
          insert oMobiusGetDocument;
          system.assert(oMobiusGetDocument.id!=null);
          system.assert(oMobiusGetDocument.name == 'MobiusService_GetMobiusDocument');       
      }
  }
  
 /*
   * Method Name : runPositiveDataPlatformLV
   * Description : Used to run scenerios of test data
   * Return type : void
   * Paramater   : NA
   */ 
   static testMethod void runPositiveDataPlatformLV()
   {    
     User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
     system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');           
     system.runAs (oCCSUser) 
     { 
     Test.StartTest();  
     Test.setCurrentPage(Page.ClaimStatementsDocument_VF_HUM);
     ApexPages.currentPage().getParameters().put('DocumentKey','AFP VIEWDIRECTSS|AFPTEST   |20150915015451|61510030760012015080120150831P|10'); 
     ClaimStatementsDocument_C_HUM oClaimDoc=new ClaimStatementsDocument_C_HUM();
     oClaimDoc.sDocumentKey='AFP VIEWDIRECTSS|AFPTEST   |20150915015451|61510030760012015080120150831P|10';
     System.assertEquals(oClaimDoc.sDocumentKey,'AFP VIEWDIRECTSS|AFPTEST   |20150915015451|61510030760012015080120150831P|10');
     oClaimDoc.sDocumentReturn = 'test';
     oClaimDoc.initiateDocumentRequest();  
     Continuation objCon = new Continuation(120);
     objCon.continuationMethod='processDocumentResponse';
     oClaimDoc.processDocumentResponse();
     Test.StopTest();  
      
     }  
  } 
}