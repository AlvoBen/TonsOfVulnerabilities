/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSyncQueueable_T_HUM 
Version             : 1.0
Created Date        : June 12 2020
Function            : Test class to test scenarios in  SoftPhoneSync classes.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Akshay Pai                                    		06/12/2020                  Initial Version
*******************************************************************************************************************************/
@isTest
public class SoftPhoneSyncQueueable_T_HUM 
{
    /*
	* Method Name : setupTestData
	* Description : This method creates test data for test class
	* Return type : None
	* Parameter   : None
	*/
    @testSetup
    public static void setupTestData()
    {
        Softphone_Sites__c spSite = new Softphone_Sites__c();
        spSite.Name = 'TestSite';
        insert spSite;
        System.assertEquals(spSite.Name, 'TestSite');
        
        SoftphoneLinks__c spLink = new SoftphoneLinks__c();
        spLink.Name = 'TestLink';
        spLink.Sites__c = spSIte.Id;
        spLink.Agent__c = 5;
        insert spLink;
        system.assertEquals(spLink.Name,'TestLink');
        
        SoftPhone_Agent__c spAgent = new SoftPhone_Agent__C();
        spAgent.Sites__c = spSite.Id;
        spAgent.Default_SoftphoneLinks__c = spLink.Id;
        spAgent.Alternate_SoftphoneLinks__c = spLink.Id;
        spAgent.Agent_Extension__c = 'SSS11';
        spAgent.User_Id__c = '99999';
        spAgent.DB2_Status__c =  GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED;
        spAgent.DB2_Error_Message__c = '';
        spAgent.DB2_Sync__c = true;
        spAgent.Integration_Update__c = true;
        insert spAgent;
        system.assertEquals(spAgent.DB2_Status__c, GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED);
        
        HUM_Webservice_Callout__c softphoneCallout = new HUM_Webservice_Callout__c();
        softphoneCallout.Name = 'AgentSoftPhoneDBSync';
        softphoneCallout.Timeout__c = '12000';
        softphoneCallout.Active__c = true;
        softphoneCallout.Certificate_Name__c = 'Humana_GuidanceCare_Alderaan';
        softphoneCallout.End_Point_URL__c = 'https://dev-crmservicews.humana.com/SoftPhoneIUDServiceREST/AgentSoftPhoneDBSync';
        softphoneCallout.Content_Type__c = 'application/json';
        softphoneCallout.Request_Type__c='POST';
        insert softphoneCallout;
        system.assertEquals(softphoneCallout.Name,'AgentSoftPhoneDBSync');
        
        HUM_Webservice_Callout__c softphoneCallout1 = new HUM_Webservice_Callout__c();
        softphoneCallout1.Name = 'AgentSoftPhoneGetDBAgents';
        softphoneCallout1.Timeout__c = '12000';
        softphoneCallout1.Active__c = true;
        softphoneCallout1.Certificate_Name__c = 'Humana_GuidanceCare_Alderaan';
        softphoneCallout1.End_Point_URL__c = 'https://dev-crmservicews.humana.com/SoftPhoneIUDServiceREST/AgentSoftPhoneGetDBAgents';
        softphoneCallout1.Content_Type__c = 'application/json';
        softphoneCallout1.Request_Type__c ='POST';
		softphoneCallout1.SampleParameter__c = '20';
        insert softphoneCallout1;
        system.assertEquals(softphoneCallout1.Name,'AgentSoftPhoneGetDBAgents'); 		
    }
    
    /*
	* Method Name : MockHttpResponseGenerator1
	* Description : This method creates valid mock responses for SoftPhoneSyncQueueable_S_HUM HTTP requests
	* Return type : None
	* Parameter   : None
	*/
    class MockHttpResponseGenerator1 implements HttpCalloutMock
    {
        Boolean hasResponded = false;
        public HttpResponse respond(HttpRequest request)
        {	
            string endPoint = request.getEndpoint();
            HttpResponse response = new HttpResponse();
            string body = '';
            
            if ( endPoint.contains('AgentSoftPhoneGetDBAgents'))
            {
                body = '{ "RecordsInDB":['+
                    '{ "UserId": "amb5529",'+
                    ' "AgentId": "1083359",'+
                    ' "AgentACDName": "",'+
                    ' "SiteName": "TestSite",'+
                    ' "DefaultSiteCode": "",'+
                    ' "DefaultSoftphoneLinkName": "TestLink",'+
                    ' "AlternateSoftphoneLinkName": "TestLink",'+
                    ' "DefaultServerName": "LOUCTCWPS43",'+
                    ' "DefaultLogicalId": "",'+
                    ' "LastUpdateTime": "2019-09-26 02:13:21.3382370000",'+
                    ' "IPAddressInd": "",'+
                    ' "SyncInd": "N"'+
                    '},'+
					'{ "UserId": "amb5530",'+
                    ' "AgentId": "amb5530",'+
                    ' "AgentACDName": "",'+
                    ' "SiteName": "TestSite1",'+
                    ' "DefaultSiteCode": "",'+
                    ' "DefaultSoftphoneLinkName": "TestLink",'+
                    ' "AlternateSoftphoneLinkName": "TestLink",'+
                    ' "DefaultServerName": "LOUCTCWPS43",'+
                    ' "DefaultLogicalId": "",'+
                    ' "LastUpdateTime": "2019-09-26 02:13:21.3382370000",'+
                    ' "IPAddressInd": "",'+
                    ' "SyncInd": "N"'+
                    '}'+
                    ']}'; 
            }
            else
			{
                body = '{"insertList": [],'+
                    '  "updateList": [],'+
                    '  "deleteList": [],'+
                    '  "syncList": ['+
                    '	{'+
                    '	 "UserId": "99999"'+
                    '	}]'+
                    '}';
            }
            response.setBody(body);
            return response;
        }
    }
    
    /*
	* Method Name : MockHttpResponseGenerator2
	* Description : This method creates error/blank response for SoftPhoneSyncQueueable_S_HUM HTTP requests
	* Return type : None
	* Parameter   : None
	*/
    class MockHttpResponseGenerator2 implements HttpCalloutMock
    {
        Boolean hasResponded = false;
        public HttpResponse respond(HttpRequest request)
        {	
            string endPoint = request.getEndpoint();
            HttpResponse response = new HttpResponse();
            string body = '';
            
            if ( endPoint.contains('AgentSoftPhoneGetDBAgents'))
            {   
                body = '{ "RecordsInDB":[]}'; 
            }
            else
			{   
                body = '{"insertList": [],'+
                    '  "updateList": [],'+
                    '  "deleteList": [],'+
                    '  "syncList": ['+
                    '	{'+
                    '	 "UserId": "99999",'+
                    '	 "ErrorDesc": "No Record With the Given UserId Exists."'+
                    '	}]'+
                    '}';
            }
            response.setBody(body);
            return response;
        }
    }
	
	/*
    * Method Name : TestforValid1
    * Description : This method tests flow without initial softphone agent list 
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestforValid1()
    {
        List<Softphone_Agent__c> listAgent = new List<Softphone_Agent__c>();
        Set<Softphone_Agent__c> agentsToUpdateSet = new Set<Softphone_Agent__c>();
		Set<Softphone_Agent__c> agentsFailedToInsertSet = new Set<Softphone_Agent__c>();
        
        Test.setMock(HttpCalloutMock.class, new MockHttpResponseGenerator1());
        Test.startTest();
        SoftPhoneSyncQueueable_S_HUM job = new SoftPhoneSyncQueueable_S_HUM(listAgent,agentsToUpdateSet,agentsFailedToInsertSet);
        ID jobID = System.enqueueJob(job);
        system.assertNotEquals(jobID, null);
        Test.stopTest();  
    }
	
    /*
    * Method Name : TestforValid2
    * Description : This method tests SoftPhoneSyncQueueable_S_HUM positive scenario
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestforValid2()
    {
        List<Softphone_Agent__c> listAgent = [Select id, User_Id__c,DB2_Status__c,DB2_Error_Message__c,Integration_Update__c from SoftPhone_Agent__c WHERE DB2_Status__c =: GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED limit 1];
        system.assertEquals(listAgent.size(), 1);
        Set<Softphone_Agent__c> agentsToUpdateSet = new Set<Softphone_Agent__c>();
		Set<Softphone_Agent__c> agentsFailedToInsertSet = new Set<Softphone_Agent__c>();
        
        Test.setMock(HttpCalloutMock.class, new MockHttpResponseGenerator1());
        Test.startTest();
        SoftPhoneSyncQueueable_S_HUM job = new SoftPhoneSyncQueueable_S_HUM(listAgent,agentsToUpdateSet,agentsFailedToInsertSet);
        ID jobID = System.enqueueJob(job);
        system.assertNotEquals(jobID, null);
        Test.stopTest();  
    } 
    
    /*
    * Method Name : TestforInValid1
    * Description : This method tests SoftPhoneSyncQueueable_S_HUM invalid/blank response scenario
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestforInValid1()
    {
        List<Softphone_Agent__c> listAgent = [Select id, User_Id__c,DB2_Status__c,DB2_Error_Message__c,Integration_Update__c from SoftPhone_Agent__c WHERE DB2_Status__c =: GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED limit 1];
        system.assertEquals(listAgent.size(), 1);
        Set<Softphone_Agent__c> agentsToUpdateSet = new Set<Softphone_Agent__c>();
		Set<Softphone_Agent__c> agentsFailedToInsertSet = new Set<Softphone_Agent__c>();
        
        Test.setMock(HttpCalloutMock.class, new MockHttpResponseGenerator2());
        Test.startTest();
        SoftPhoneSyncQueueable_S_HUM job = new SoftPhoneSyncQueueable_S_HUM(listAgent,agentsToUpdateSet,agentsFailedToInsertSet);
        ID jobID = System.enqueueJob(job);
        system.assertNotEquals(jobID, null);
        Test.stopTest();  
    }
    
    /*
    * Method Name : TestforValid2
    * Description : This method tests flow without initial softphone agent list and with invalid/blank response scenario
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestforInValid2()
    {
        List<Softphone_Agent__c> listAgent = new List<Softphone_Agent__c>();
        Set<Softphone_Agent__c> agentsToUpdateSet = new Set<Softphone_Agent__c>();
		Set<Softphone_Agent__c> agentsFailedToInsertSet = new Set<Softphone_Agent__c>();
        
        Test.setMock(HttpCalloutMock.class, new MockHttpResponseGenerator2());
        Test.startTest();
        SoftPhoneSyncQueueable_S_HUM job = new SoftPhoneSyncQueueable_S_HUM(listAgent,agentsToUpdateSet,agentsFailedToInsertSet);
        ID jobID = System.enqueueJob(job);
        system.assertNotEquals(jobID, null);
        Test.stopTest();  
    }
}