/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSync_T_HUM 
Version             : 1.0
Created Date        : August 23 2019
Function            : Test class to test scenarios in  SoftPhoneSync classes.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                      08/23/2019                  Initial Version
*******************************************************************************************************************************/
@isTest
public class SoftPhoneSync_T_HUM 
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
        insert spSite;		//Softphone site creation
        System.assertEquals(spSite.Name, 'TestSite');
        
        SoftphoneLinks__c spLink = new SoftphoneLinks__c();
        spLink.Name = 'TestLink';
        spLink.Sites__c = spSIte.Id;
        spLink.Agent__c = 5;
        insert spLink;		//Softphone link creation
        system.assertEquals(spLink.Name,'TestLink');
        
        SoftPhone_Agent__c spAgent = new SoftPhone_Agent__C();
        spAgent.Sites__c = spSite.Id;
        spAgent.Default_SoftphoneLinks__c = spLink.Id;
        spAgent.Alternate_SoftphoneLinks__c = spLink.Id;
        spAgent.Agent_Extension__c = 'SSS11';
        spAgent.User_Id__c = '99999';
        spAgent.DB2_Status__c = 'Ready For Insert';
        spAgent.DB2_Sync__c = true;
        spAgent.DB2_Error_Message__c = 'Test Error Message';
        insert spAgent;		//SOftphone Agent creation
        system.assertEquals(spAgent.DB2_Status__c, 'Ready For Insert');
        
        SoftPhone_Agent__c spAgent2 = new SoftPhone_Agent__C();
        spAgent2.Sites__c = spSite.Id;
        spAgent2.Default_SoftphoneLinks__c = spLink.Id;
        spAgent2.Alternate_SoftphoneLinks__c = spLink.Id;
        spAgent2.Agent_Extension__c = 'ZUV09';
        spAgent2.User_Id__c = '22222';
        spAgent2.DB2_Status__c = 'Ready For Delete';
        insert spAgent2;		//SOftphone Agent creation
        system.assertEquals(spAgent2.DB2_Status__c, 'Ready For Delete');
        
        SoftPhone_Agent__c spAgent3 = new SoftPhone_Agent__C();
        spAgent3.Sites__c = spSite.Id;
        spAgent3.Default_SoftphoneLinks__c = spLink.Id;
        spAgent3.Alternate_SoftphoneLinks__c = spLink.Id;
        spAgent3.Agent_Extension__c = 'ZUV10';
        spAgent3.User_Id__c = '33333';
        spAgent3.DB2_Status__c = 'Ready For Update';
        insert spAgent3;		//SOftphone Agent creation
        system.assertEquals(spAgent3.DB2_Status__c, 'Ready For Update');
        
        HUMConstants__c constantsObj = new HUMConstants__c();
        constantsObj.Name = 'SOFTPHONE_SYNC_BATCH_SIZE';
        constantsObj.IntegerValue__c = 300;
        insert constantsObj;
        system.assertEquals(constantsObj.IntegerValue__c, 300);  
        
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
    }
    
    /*
    * Method Name : TestReadyForInsert
    * Description : This method creates test Agent insert scenario
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestReadyForInsert()
    {
        List<SoftPhone_Agent__c> SA1 = [Select id, Agent_Extension__c from SoftPhone_Agent__c WHERE DB2_Status__c = 'Ready For Insert' limit 1];
        system.assertEquals(SA1.size(), 1);
        ApexPAges.StandardSetController ssc = new ApexPages.StandardSetController(SA1);
        SoftPhoneSync_C_HUM controllerObj =new SoftPhoneSync_C_HUM(ssc);
        ApexPages.currentPage().getParameters().put('retURL','12345'); 
        system.assertEquals(ApexPages.currentPage().getParameters().get('retURL'),'12345');
        controllerObj.pageref();
    }  

     /*
    * Method Name : TestInvokeSoftPhoneServicePositive
    * Description : This method creates test positive flow of webservice callout
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestInvokeSoftPhoneServicePositive()
    {
        List<SoftPhone_Agent__c> SA1 = [Select id from SoftPhone_Agent__c WHERE DB2_Status__c != null limit 1]; 
        system.assertEquals(SA1.size(), 1);
        ApexPAges.StandardSetController ssc = new ApexPages.StandardSetController(SA1);
		SoftPhoneSync_C_HUM controllerObj =new SoftPhoneSync_C_HUM(ssc); 
        continuation con = (continuation)controllerObj.invokeSoftPhoneService();
        HttpResponse res = new HttpResponse();
        res.setBody('{ "insertList":[ { "AgentId":"ZUV11", "UserId":"99999" } ], "updateList":[ { "AgentId":"ZUV11", "UserId":"99999" } ], "deleteList":[ { "AgentId":"ZUV11", "UserId":"99999" } ] }');
		system.assertNotEquals(res, null);
        Test.setContinuationResponse(controllerObj.sConRequest, res);		//Sets mock response
        Test.invokeContinuationMethod(controllerObj, con);		//Invokes continuation method
    }
	
    /*
    * Method Name : TestInvokeSoftPhoneServiceNegative
    * Description : This method creates test negative flow of webservice callout
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestInvokeSoftPhoneServiceNegative()
    {
        List<SoftPhone_Agent__c> SA1 = [Select id from SoftPhone_Agent__c WHERE DB2_Status__c != null limit 1];
        system.assertEquals(SA1.size(), 1);
        ApexPAges.StandardSetController ssc = new ApexPages.StandardSetController(SA1);
		SoftPhoneSync_C_HUM controllerObj =new SoftPhoneSync_C_HUM(ssc);
        continuation con = (continuation)controllerObj.invokeSoftPhoneService();
        HttpResponse res = new HttpResponse();
        res.setBody('{ "insertList":[ { "AgentId":"SSS11", "UserId":"99999", "ErrorDesc":"SQL0803N" }], "updateList":[ { "AgentId":"SSS11", "UserId":"99999", "ErrorDesc":"SQL0803N" }], "deleteList":[ ] }');
        system.assertNotEquals(res, null);
        Test.setContinuationResponse(controllerObj.sConRequest, res);		//Sets mock response
        Test.invokeContinuationMethod(controllerObj, con);		//Invokes continuation method
    }
    
    /*
    * Method Name : TestBlankResponse
    * Description : This method creates test webservice callout with blank response
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void TestBlankResponse()
    {
        List<SoftPhone_Agent__c> SA1 = new List<SoftPhone_Agent__c>();  
        ApexPAges.StandardSetController ssc = new ApexPages.StandardSetController(SA1);
		SoftPhoneSync_C_HUM controllerObj =new SoftPhoneSync_C_HUM(ssc);
        SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2 requestDTOInner = new SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2();
        SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2 responseDTOInner = new SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2();
        continuation con = (continuation)controllerObj.invokeSoftPhoneService();
        HttpResponse res = new HttpResponse();   
        system.assertEquals(res.getStatus(), null);
        Test.setContinuationResponse(controllerObj.sConRequest, res);		//Sets mock response
        Test.invokeContinuationMethod(controllerObj, con);		//Invokes continuation method             
    }
}