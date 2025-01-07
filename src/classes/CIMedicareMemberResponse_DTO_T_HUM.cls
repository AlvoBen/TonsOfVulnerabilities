/**************************************************************************** 
Apex Class Name  : CIMedicareMemberResponse_DTO_T_HUM  
Version          : 1.0  
Created Date     : August 01, 2016
Function         : test class for CIMedicareMemberResponse_DTO_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth            34224           08/01/2016                    Original Version
***************************************************************************************************/
@isTest
private Class CIMedicareMemberResponse_DTO_T_HUM 
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
    * Test method for CIMedicareMemberResponse_DTO
    * @param None
    * @return Void
    */
    static testMethod void testCIMedicareMemberResponse_DTO_T()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
              CIMedicareMemberResponse_DTO_HUM oResponse = new CIMedicareMemberResponse_DTO_HUM();
              CIMedicareMemberResponse_DTO_HUM.MedicareMemberInfoResponse medMemInfoRes = new CIMedicareMemberResponse_DTO_HUM.MedicareMemberInfoResponse();
              CIMedicareMemberResponse_DTO_HUM.ErrorList errList = new CIMedicareMemberResponse_DTO_HUM.ErrorList(); 
              CIMedicareMemberResponse_DTO_HUM.Error err = new CIMedicareMemberResponse_DTO_HUM.Error();  
              system.assert(oResponse !=null);
          Test.StopTest();
        }
    }  
 }