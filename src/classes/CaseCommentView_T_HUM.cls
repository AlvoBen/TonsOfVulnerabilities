/**********************************************************************************************************************************************************
Apex Class Name  : CaseCommentView_T_HUM
Version          : 1.0
Created Date     : MAY 4 2017
Function         : Test class for CaseCommentView_C_HUM
***********************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Joel George                                    05/04/2017                   Original Version
* Shreya Choodamani                              10/11/2017                   REQ-332390 : Updated the test class to increase the coverage
* Santhi Mandava                                 27/03/2018                   Removed comment body from SOQL where condition
************************************************************************************************************************************************************/

@isTest
private class CaseCommentView_T_HUM
{
   /*
    * This method creates test data for test class
    * @param None
    * @return None
    */

    @testSetup static void setUpTestData()
    {
        User usrHPS = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');   
        insert usrHPS;
        
        System.runAs(usrHPS)
        {
            Case objCase = new Case();
            objCase = HUMTestDataHelperTest.getCase();
            Id caseRTId = Schema.SObjectType.Case.RecordTypeInfosByName.get('HP Member Case').RecordTypeId;
            objCase.RecordTypeId = caseRTId;
            insert objCase;
            System.assertEquals(objCase.Interacting_With_Name__c, 'Test');

            CaseComment ccObj = new CaseComment();
            ccObj = HUMTestDataHelperTest.getCaseComm();
            ccObj.ParentId = objCase.Id;
            insert ccObj;
            System.assertEquals(ccObj.CommentBody, 'Test Comment');
        }
    }

    /*
     * Method Name : testCaseCommentView_C_HUM
     * Description : This method Is used To test the Controller
     * Return type : None
     * Parameter   : None
     */
    
     Static testMethod void testCaseCommentView_C_HUM()
     {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User2'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c, 'Agent');

            CaseComment ccObj = new CaseComment();
            ccObj = [Select Id,CommentBody from CaseComment where Parentid =: objCase.Id limit 1];
            System.assertEquals(ccObj.CommentBody, 'Test Comment');
            
            ApexPages.StandardController cs1 = new ApexPages.standardController(objCase);
            CaseCommentView_C_HUM cCommen = new CaseCommentView_C_HUM(cs1);
            Test.stopTest();
            
        }
    }
    
   /*
    * Method Name : testdeleteComment
    * Description : This method Is used To test the deleteComment method
    * Return type : None
    * Parameter   : None
    */
    
    Static testMethod void testdeleteComment()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c, 'Agent');

            CaseComment ccObj = new CaseComment();
            ccObj = [Select Id,CommentBody from CaseComment where ParentId =: objCase.Id limit 1];
            ccObj.CommentBody = '(Test)';
            update ccObj;
            System.assertEquals(ccObj.CommentBody, '(Test)');
        
            Test.setCurrentPage(page.CaseCommentView_VF_HUM);
            ApexPages.currentPage().getParameters().put('CommentId_d',ccObj.Id);
            System.assert(ccObj.Id<> null);
            
            ApexPages.StandardController cs1 = new ApexPages.standardController(objCase);
            CaseCommentView_C_HUM cCommen = new CaseCommentView_C_HUM(cs1);
            cCommen.deleteComment();
            
            Test.stopTest();
        }
    }
}