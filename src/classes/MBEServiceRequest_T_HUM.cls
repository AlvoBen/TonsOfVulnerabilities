/**************************************************************************** 
Apex Class Name  : MBEServiceRequest_T_HUM
Version          : 1.0  
Created Date     : February 02,2016
Function         : test class for MBEServiceRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    29768               02/02/2015                    Original Version
***************************************************************************************************/
@isTest
private Class MBEServiceRequest_T_HUM
{
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    /*
     * Method Name : testMBEServiceRequest
     * Description : Test method for  MBEServiceRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMBEServiceRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            MBEServiceRequest_H_HUM oRequest = new MBEServiceRequest_H_HUM();
            MBEServiceRequest_H_HUM.GetMember getMember = new MBEServiceRequest_H_HUM.GetMember();
            MBEServiceRequest_H_HUM.MemberMasterList memMasterList = new MBEServiceRequest_H_HUM.MemberMasterList();
            MBEServiceRequest_H_HUM.MemberMaster memMaster = new MBEServiceRequest_H_HUM.MemberMaster();
            MBEServiceRequest_H_HUM.MemberMasterCriteriaList memMasCriList = new MBEServiceRequest_H_HUM.MemberMasterCriteriaList();
            MBEServiceRequest_H_HUM.MemberMasterCriteria memMasCriteria = new MBEServiceRequest_H_HUM.MemberMasterCriteria();
            MBEServiceRequest_H_HUM.Period per = new MBEServiceRequest_H_HUM.Period();
            system.assert(oRequest!=null);
          Test.StopTest();
        }
    }  
    
}