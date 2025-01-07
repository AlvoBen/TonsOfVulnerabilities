/****************************************************************************
Apex Class Name  : HUMCaseTaskBatchHelperTest
Version          : 1.0 
Created On       : 08/05/2015
Function         : Test class to HUMCaseTaskBatchHelper apex class

Modification Log :
* Developer Name          Code Review                   Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                        08/05/2015                 Original Version
***********************************************************************************************************************************/

@isTest
private class HUMCaseTaskBatchHelperTest
{
    /* Method name : setup
    * Description : This Method is used to setup test data.
    * Return Type : Void  
    * Parameter   : NA 
    */ 
	
    @testSetup static void setup()  
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched'); 
        List <Error_Log__c> lstErrorLogs = new List <Error_Log__c> ();
       system.runAs(oUser)
       {
            List<Work_Queue_Setup__c> lstWQS = new List<Work_Queue_Setup__c>();
            lstWQS.Add(new Work_Queue_Setup__c( name ='Cincinnati CRU', Batch_Update_Required__c = true));
            lstWQS.Add(new Work_Queue_Setup__c( name ='Cincinnati Calls', Batch_Update_Required__c = true));
            insert lstWQS ;
            
            List<Queue_View__c> lstViews = HUMTestDataHelperTest.insertTestDataForViews(lstWQS[0]);   
            HUMTestDataHelperTest.insertTestDataForViews(lstWQS[1]); 
            HUMTestDataHelperTest.insertTestDataForViewsItems(lstViews);
            HUMTestDataHelperTest.insertTestDataForQueuesPriority(lstWQS[0]);   
            
            HUMQueueViewBatchSettings__c oHumview = new HUMQueueViewBatchSettings__c();
            oHumview.name='QueueViewBatchSetup';
            oHumview.TriggerExecutionRequired__c= true;
            oHumview.BatchExecutionDate__c = datetime.valueof(system.now().AddDays(-4));
            insert oHumview;
            insert new HUM_Webservice_Callout__c( Name = 'HumCAServiceDesk' , Certificate_Name__c = 'TestCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
            lstErrorLogs.Add(new Error_Log__c(Error_Message__c = 'Test error message'));	
			lstErrorLogs.Add(new Error_Log__c(Error_Message__c = 'Test error messages'));		
			insert lstErrorLogs;
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
        }  
    }
    
      /*
     * Method Name : overAllMainTestMethod
     * Description : This is the main method which call all other and call the Helper methods of HUMUpdateCaseTaskHelper 
     * Return type : void
     * Paramater   : None
     */    
    private static testMethod void overAllMainTestMethod()
    {
    	User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
    	List <Error_Log__c> lstErrorLogs = [Select id, Name , Error_Message__c from Error_Log__c limit 2];
    	HUM_Webservice_Callout__c oCASvcAuth = [Select Name, End_Point_URL__c,Certificate_Name__c,Timeout__c from HUM_Webservice_Callout__c where Name = 'HumCAServiceDesk'];
    	List<Work_Queue_Setup__c> lstWQS = [Select id, Batch_Update_Required__c from Work_Queue_Setup__c];
    	Set<String> setString = new Set<String>();
    	system.runas(oUser)
    	{
    		//Test.setMock(WebServiceMock.class, new HUMCAServiceDeskResponseMockTest());
	    	Test.StartTest();
	    	//HUMCaseTaskBatchHelper.createCATicket(lstErrorLogs);
	        setString = HUMCaseTaskBatchHelper.getUpdatedQueueNames();
	        HUMCaseTaskBatchHelper.dmlResults = new List<Database.UpsertResult>();
	        HUMCaseTaskBatchHelper.processDMLResults();
	        Test.stoptest();
	        system.assert(setString != null);
	        
    	}    
    }
}