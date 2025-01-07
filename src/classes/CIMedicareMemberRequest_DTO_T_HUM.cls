/**************************************************************************** 
Apex Class Name  : CIMedicareMemberRequest_DTO_T_HUM  
Version          : 1.0  
Created Date     : August 01, 2016
Function         : test class for CIMedicareMemberRequest_DTO_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth           34224 	         08/01/2016                    Original Version
***************************************************************************************************/
@isTest
private Class CIMedicareMemberRequest_DTO_T_HUM 
{

    /*
    * This is a test method to setup common test data for all test method
    * @param None
    * @return Void
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
    * Test method for CIMedicareMemberRequest_DTO
    * @param None
    * @return Void
    */
    static testMethod void testCIMedicareMemberRequest_DTO_T()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
              CIMedicareMemberRequest_DTO_HUM oResponse = new CIMedicareMemberRequest_DTO_HUM();
              CIMedicareMemberRequest_DTO_HUM.CIMedicareMember CIMediMem = new CIMedicareMemberRequest_DTO_HUM.CIMedicareMember(); 
              system.assert(oResponse !=null);
          Test.StopTest();
        }
    }  
 }