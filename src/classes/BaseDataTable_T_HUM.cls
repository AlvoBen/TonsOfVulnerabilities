/**
   @Author      : Pradeepkumar Dani
   @name        : BaseDataTable_T_HUM
   @CreateDate  : 10/30/2017
   @Description : This test class ensures BaseDataTable_C_HUM coverage
   @Version     : 1.0
   @reference   : BaseDataTable_C_HUM
   
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pradeepkumar Dani         149513                  11/10/2017                     Original version 
 * Srivinas Damera           188503                  2/22/2018                      REQ - 335172 - Removal of Permission Set to Access Custom Case Page 
 * Jasmeen Shangari                                  04/29/2020                     Log error in Catch block to resolve Checkmarx error
********************************************************************************************************************************/
@isTest
private with sharing class BaseDataTable_T_HUM 
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert testUser;
        system.assertEquals(testUser.lastname=='test User1',true);
        
        system.runAs(testUser)
        { 
            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            system.assertNotEquals(objCase, null, 'Value not matched');
        }
    }
    
    /*
    * This method is used to test the detail redirection funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testCaseDetail()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
        
        case caseRecord = [Select id from case limit 1];
    	
    	BaseDataTable_C_HUM bd = new BaseDataTable_C_HUM();
    	
    	bd.setSObjectName('Case');
    	bd.getSObjectName();        	
    	
    	system.assertEquals(BaseDataTable_C_HUM.deleteRecord(caseRecord.Id, 'Case'), true, 'Record successfully deleted'); 
    	
    	try
    	{
    		boolean res = BaseDataTable_C_HUM.deleteRecord(caseRecord.Id, 'Attachment__c');
    	}
    	catch(Exception e)
    	{ 
			HUMExceptionHelper.logErrors(e,'BaseDataTable_T_HUM','testCaseDetail'); 			
		}
    }
}