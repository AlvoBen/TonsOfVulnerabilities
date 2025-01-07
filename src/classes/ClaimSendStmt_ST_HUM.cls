/**************************************************************************************************************** 
Apex Class Name  : ClaimSendStmt_ST_HUM
Version          : 1.0  
Function         : Test class for ClaimSendStmt_S_HUM
******************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat           295053                09/28/2018                 Original Version
* Trina  Ghosh                                  07/27/2020                 US# 965352
* Rahul Chaurasia							 	03/05/2021		   		   REQ - 1905086 - T1PRJ0002606- SF- Mobius Services old version Decommission	

******************************************************************************************************************/
@isTest
private Class ClaimSendStmt_ST_HUM
{
   /*
    *  This method is used to prepare test data
    *  @name   prepareTestData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */  
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c oService = new HUM_Webservice_Callout__c();
            oService.Name = 'CommunicationReprint_V3';
            oService.Active__c = true;
            oService.Certificate_Name__c = 'testts';
            oService.End_Point_URL__c = 'https://testst-cmservicews.hum.com';
            oService.Timeout__c = '120000';
            oService.Content_Type__c = 'application/json';
            oService.Request_Type__c = 'POST';
            insert oService;
            system.assert(oService.id != null);
            system.assert(oService.name == 'CommunicationReprint_V3');
        }
    }
    
   /*
    *  This method is used to run positive scenarios data
    *  @name   runPositiveMedicalData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */  
    static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where Name = 'CommunicationReprint_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.StartTest();
            ClaimSendStmt_S_HUM objectS = new ClaimSendStmt_S_HUM();
            objectS.buildRequestMbr('', 'Sample', 'AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', '40404', '123456', 'H68518747', '00', 'LV', '1085445415', '1155004650122','Other','EMME');
            objectS.buildRequestDesign('', 'Sample', 'AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', '40404', '123456','Other','EMME');
            objectS.buildRequestOthr('', 'Sample', 'AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', '40404', '123456','Other','EMME');
            objectS.buildRequestProvider('', 'Sample', 'AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', '40404', '123456','1085445415', '1155004650122','Other','EMME');
            HttpRequest oRequest = new HttpRequest();
            objectS.setupRequest(oRequest,oServiceCallout,'', 'Sample', 'AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', 'Sample', '40404', '123456', 'H68518747', '00', 'LV', '1085445415', '1155004650122','123','123','Member','Other','EMME');
            Test.stopTest();  
        }
    }    
}