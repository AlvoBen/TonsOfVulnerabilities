/**************************************************************************** 
Apex Class Name  : UpdateInteractionResponseWrapper_T_HUM
Version          : 1.0  
Created Date     : 05/02/2019
Function         : test class for UpdateInteractionResponse_DTO_HUM
*****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar       354971             05/02/2019                Original Version
***************************************************************************************************/
@isTest
private Class UpdateInteractionResponseWrapper_T_HUM
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
     * Description : Test method for UpdateInteractionResponse_DTO_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testInteractionResponseWrapper()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
          UpdateInteractionResponse_DTO_HUM oWrap = new UpdateInteractionResponse_DTO_HUM();
          UpdateInteractionResponse_DTO_HUM.updateInteractionInfo oupdateInfo = new UpdateInteractionResponse_DTO_HUM.updateInteractionInfo();
          oupdateInfo.ErrorCode = 'testc';
          oupdateInfo.ErrorMessage = 'testmsg';
          oupdateInfo.InteractionNumber = '123';
          oupdateInfo.Success =false;
          oupdateInfo.CreatedCases = new list<String>{'12','23'};
          oWrap.UpdateInteractionResponse= oupdateInfo ;
          system.assert(oWrap!=null);         
          Test.StopTest();
        }
    }  
}