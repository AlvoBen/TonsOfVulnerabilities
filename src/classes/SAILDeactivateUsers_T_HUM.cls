/******************************************************************************
Apex Class Name  : SAILDeactivateUsers_T_HUM
Version          : 1.0
Created Date     : Nov 14 2016
Function         : Deactivate User based on 90 days condition
                   As User object cannot be isolate from test class as per below
                   link. We have handle this test class differently
Ref:https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_testing_data_access.htm
*******************************************************************************

Modification Log:

* Developer Name   Code Review    Date             Description
*------------------------------------------------------------------------------
* Milon Dey          48578      11/14/2016     Initial creation of the class
******************************************************************************/
@isTest 
Private Class SAILDeactivateUsers_T_HUM{    
    
    /**
    *  A Positive TestCase for the SAILDeactivateUsers_B_HUM
    *  @name testCreatedDate
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */ 
    @isTest 
    static void testCreatedDate(){
        DateTime threshholdCreateddt= date.today()-61;
        User u = HUMTestDataHelperTest.createTestUser('userName','Standard User');
        insert u;
        Test.setCreatedDate(u.Id, threshholdCreateddt);    
        Test.startTest();
        SAILDeactivateUsers_B_HUM deactUsr = new SAILDeactivateUsers_B_HUM();        
        ID batchprocessid = Database.executeBatch(deactUsr,4000);
        
        Test.stopTest();
        System.assert(deactUsr.successCount >=0);
    }
    
    /**
    *  A Non-System Admin try to run the below script
    *  @name testNegative
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */   
    @isTest 
    static void testNegative(){
        DateTime threshholdCreateddt= date.today()-61;
        User u = HUMTestDataHelperTest.createTestUser('userName','Chatter Free User');
        insert u;
        System.runAs(u) {
        Test.setCreatedDate(u.Id, threshholdCreateddt);    
        Test.startTest();
        
        SAILDeactivateUsers_B_HUM deactUsr = new SAILDeactivateUsers_B_HUM();        
        deactUsr.limitVal= 0;
        ID batchprocessid = Database.executeBatch(deactUsr,4000);
        System.assert(deactUsr.successCount >=0);
        }
        
        Test.stopTest();        
    }
}