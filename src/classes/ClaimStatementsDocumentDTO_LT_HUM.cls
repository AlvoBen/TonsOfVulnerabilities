/**************************************************************************** 
Apex Class Name  : ClaimStatementsDocumentDTO_LT_HUM
Version          : 1.0  
Created Date     : May 17th, 2016
Function         : Test class for ClaimStatementsDocument_DTO_HUM
******************************************************************************
Modification Log:
* Developer Name          Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar       27974            05/17/2016                  Original Version
***************************************************************************************************/
@isTest
private Class ClaimStatementsDocumentDTO_LT_HUM
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
            ClaimStatementsDocument_LDTO_HUM oDocument=new ClaimStatementsDocument_LDTO_HUM();
            ClaimStatementsDocument_LDTO_HUM.getDocumentResponse oDocResponse = new ClaimStatementsDocument_LDTO_HUM.getDocumentResponse();
            oDocResponse.getDocumentReturn='';
            oDocument.getDocumentResponse=oDocResponse;
            System.assertEquals(oDocResponse.getDocumentReturn, '');
            Test.StopTest();
        }
        
    } 
}