/**************************************************************************** 
Apex Class Name  : CreateInteractionResponseWrapper_T_HUM
Version          : 1.0  
Created Date     : 04/24/2019
Function         : test class for CreateInteractionResponse_DTO_HUM
*****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar                         04/24/2019                Original Version
***************************************************************************************************/
@isTest
private Class CreateInteractionResponseWrapper_T_HUM
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
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
    /*
     * Method Name : testInteractionRequestWrapper
     * Description : Test method for CreateInteractionRequest_DTO_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testInteractionResponseWrapper()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
          CreateInteractionResponse_DTO_HUM  oWrap = new CreateInteractionResponse_DTO_HUM ();
          CreateInteractionResponse_DTO_HUM.CreateInteractionInfo oIntInfo = new CreateInteractionResponse_DTO_HUM.CreateInteractionInfo();
          oIntInfo.ErrorCode = 'testc';
          oIntInfo.ErrorMessage = 'testmsg';
          oIntInfo.InteractionNumber = '123';
          oIntInfo.Success =false;
          oIntInfo.CreatedCases = new list<String>{'12','23'};
          oWrap.CreateInteractionInfo = oIntInfo;
          system.assert(oWrap!=null);         
          Test.StopTest();
        }
    }  
}