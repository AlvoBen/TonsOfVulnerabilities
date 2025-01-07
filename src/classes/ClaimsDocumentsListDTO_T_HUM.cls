/**************************************************************************** 
Apex Class Name  : ClaimsDocumentsListDTO_T_HUM
Version          : 1.0  
Created Date     : May 18th 2016
Function         : Test class for ClaimsDocumentsList_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar       27974            05/18/2016                Original Version
***************************************************************************************************/
@isTest
private Class ClaimsDocumentsListDTO_T_HUM
{
    /*
     * Method Name : prepareTestData
     * Description : Create common test Data
     * Return type : void
     * Paramater   : NA
     */
     @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    }
    /*
     * Method Name : runPositiveData
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runPositiveData()
    {    
    User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
        Test.StartTest();
         ClaimsDocumentsList_DTO_HUM oDocList=new ClaimsDocumentsList_DTO_HUM();   
         oDocList.sDocumentKey='AFP VIEWDIRECTSS|AFPTEST   |20150915015451|61510030760012015080120150831P|10';
         System.assertEquals(oDocList.sDocumentKey, 'AFP VIEWDIRECTSS|AFPTEST   |20150915015451|61510030760012015080120150831P|10');
          oDocList.sPrintedDate='2014-11-14T06:32:58';
         System.assertEquals(oDocList.sPrintedDate, '2014-11-14T06:32:58');
          oDocList.sTopicName='PersonId';
         System.assertEquals(oDocList.sTopicName, 'PersonId');
           oDocList.sStatementBeginDate='';
         System.assertEquals(oDocList.sStatementBeginDate, '');
           oDocList.sStatementEndDate='';
         System.assertEquals(oDocList.sStatementEndDate, '');
          oDocList.sStatementType='XBSI';
         System.assertEquals(oDocList.sStatementType, 'XBSI');
         oDocList.sPersonId='1038533017';
         System.assertEquals(oDocList.sPersonId, '1038533017');
       
        Test.StopTest();
        }   
    }

}