/**************************************************************************** 
Apex Class Name  : AVFTemplateRequest_DTO_T_HUM  
Version          : 1.0  
Created Date     : August 01, 2016
Function         : test class for AVFTemplateRequest_DTO_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth           34224            08/01/2016                    Original Version
***************************************************************************************************/
@isTest
private Class AVFTemplateRequest_DTO_T_HUM 
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
    * Test method for AVFTemplateRequest_DTO
    * @param None
    * @return Void
    */
    static testMethod void testAVFTemplateRequest_DTO()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
              AVFTemplateRequest_DTO_HUM oResponse = new AVFTemplateRequest_DTO_HUM();
              AVFTemplateRequest_DTO_HUM.SaveTemplateRequest saveTemReq = new AVFTemplateRequest_DTO_HUM.SaveTemplateRequest(); 
              system.assert(oResponse !=null);
          Test.StopTest();
        }
    }  
 }