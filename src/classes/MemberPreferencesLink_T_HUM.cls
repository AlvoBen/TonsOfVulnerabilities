/**************************************************************************************************************************************************
Apex class Name    :  MemberPreferencesLink_T_HUM
Version            :  1.0
Created Date       :  03/08/2018
Function           :  This is the test class for MemberPreferencesLink_S_HUM and MemberPreferencesLink_C_HUM controller class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------
 *  Shivam Anand                                      03/08/2018           REQ- 349581- Add Member Preferences Link to CRM Service Person Account Page
 *  Vijaya Koriprolu                                  03/10/2018           REQ - 349580- Add Member Preferences Link on CRM Policy Member Page                                  09/03/2018
 *  Santhi Mandava                                    03/12/2019           Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
*****************************************************************************************************************************/
@isTest
public with sharing class MemberPreferencesLink_T_HUM
{
    /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        System.runAs(oCCSUser) 
        {
            Test.StartTest();
            HUM_Webservice_Callout__c oMemberPrefernceLink = new HUM_Webservice_Callout__c();
            oMemberPrefernceLink.name='EncryptRestService';
            oMemberPrefernceLink.Active__c=true;
            oMemberPrefernceLink.Certificate_Name__c='Humana_GuidanceCare_Venus';
            oMemberPrefernceLink.End_Point_URL__c='https://testst-cmservicews.hum.com/';
            oMemberPrefernceLink.Timeout__c='12000';
            oMemberPrefernceLink.Content_Type__c='application/json';
            oMemberPrefernceLink.Request_Type__c='POST';
            insert oMemberPrefernceLink;
            system.assert(oMemberPrefernceLink.id!=null);
            system.assert(oMemberPrefernceLink.name == 'EncryptRestService');
            Test.stopTest(); 
        }
    }
    
    /*
    * Method Name : runPositiveData
    * Description : Used to run positive scenarios of test data
    * Return type : void
    * Paramater   : NA
    */ 
    Static testMethod void runPositiveData()   
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        {   
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='EncryptRestService' limit 1];   
            MemberPreferencesLink_S_HUM oMemPreferenceLink = new MemberPreferencesLink_S_HUM();
            MemberPreferencesLink_DTO_HUM oMemPreferenceResponse = new MemberPreferencesLink_DTO_HUM();
            HttpRequest oRequest = new HttpRequest();
            String sResponse ='';
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oServiceCallout.Timeout__c)/1000;
            System.assertEquals(oServiceCallout.Certificate_Name__c,'Humana_GuidanceCare_Venus'); 
            Test.setCurrentPage(Page.MemberPreferencesLink_VF_HUM);
            String Response = '{ "EncryptResponse":{ "EncryptResult":{ "EncryptValue":"5spBhgm7Q6RDw+Mfydllig==" } } }';
            String sMemGenKey = '9582004256425';
            Test.StartTest();
            oRequest = oMemPreferenceLink.setupRequest(oRequest,oServiceCallout,sMemGenKey);
            oMemPreferenceResponse  = oMemPreferenceLink.processResponse(Response);
            system.assertNotEquals(oMemPreferenceResponse,NULL);

            Test.stopTest();
       }
}
 
     /*
     * Method Name : memberInitiate
     * Description : This method tests the initiate request and process response
     * Return type : void
     * Parameter   : None
     */ 
    Static testMethod void memberInitiate() 
    {
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser)
        {
            Test.startTest();
            PageReference pageRef = Page.MemberPreferencesLink_VF_HUM;
            Test.setCurrentPage(pageRef);
            Apexpages.currentPage().getParameters().put('MemberGenKey','9582004256425');
            MemberPreferencesLink_C_HUM oMember = new MemberPreferencesLink_C_HUM();
            oMember.sMemGenKey = '9582004256425';
            oMember.initiateRequest();
            HUM_Webservice_Callout__c oMemberPrefernceLink = new HUM_Webservice_Callout__c();
            oMemberPrefernceLink.name='EncryptRestService';
            oMemberPrefernceLink.Active__c=true;
            oMemberPrefernceLink.Certificate_Name__c='MemberCertificate';
            oMemberPrefernceLink.End_Point_URL__c='https://testst-cmservicews.hum.com/';
            oMemberPrefernceLink.Timeout__c='12000';
            oMemberPrefernceLink.Content_Type__c='application/json';
            oMemberPrefernceLink.Request_Type__c='POST';
            insert oMemberPrefernceLink;  
            system.assert(oMemberPrefernceLink.id!=null);
            system.assert(oMemberPrefernceLink.Name == 'EncryptRestService');
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod= 'processResponse';
            oMember.processResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive'); 
            String sJson ='{ "EncryptResponse":{ "EncryptResult":{ "EncryptValue":"5spBhgm7Q6RDw+Mfydllig==" } } }';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            oMember.responseParse(oResponse);
            system.assert(oMember!=null);        
            oMember.sMemEncryptedValue ='5spBhgm7Q6RDw+Mfydllig==';
          }
    }
         
          /*
     * Method Name : testLogErrors
     * Description : This method is used to test List lstLogErrors getters setters
     * Return type : void
     * Paramater   : NA
     */
   static testMethod void testLogErrors()
      {    
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser)
         { 
           Test.StartTest();
           PageReference pageRef = Page.MemberPreferencesLink_VF_HUM;
           Test.setCurrentPage(pageRef);
           Apexpages.currentPage().getParameters().put('MemberGenKey','9582004256425');
           MemberPreferencesLink_C_HUM oMem = new MemberPreferencesLink_C_HUM();
           System.assert(oMem.lstLogErrors.size() == 0);
           List<Error_Log__c> lstLog = new List<Error_Log__c>();
           Error_Log__c error = new Error_Log__c(Status__c = 'testStatus');
           lstLog.add(error);
           oMem.lstLogErrors = lstLog;
           System.assert(oMem.lstLogErrors.size() > 0);
           Test.stopTest();  
        }       
      } 
      
       /*
     * Method Name : printMemberPreferenceException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    Static testMethod void printMemberPreferenceException() 
    {
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser)
        {
            Test.startTest();
            PageReference pageRef = Page.MemberPreferencesLink_VF_HUM;
            Test.setCurrentPage(pageRef);
            Apexpages.currentPage().getParameters().put('MemberGenKey','9582004256425');
            MemberPreferencesLink_C_HUM oMem = new MemberPreferencesLink_C_HUM();
            oMem.initiateRequest();
            oMem.processResponse();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  =  '{ "EncryptResponse":{ }}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(203); 
            oMem.responseParse(oResponse); 
        }
    }
    
       /*
     * Method Name : printSummarException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    Static testMethod void redirectMemberPreferenceView() 
    {
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser)
        {
            Test.startTest();
            PageReference pageRef = Page.MemberPreferencesLink_VF_HUM;
            Test.setCurrentPage(pageRef);
            Apexpages.currentPage().getParameters().put('MemberGenKey','9582004256425');
            MemberPreferencesLink_C_HUM oMember = new MemberPreferencesLink_C_HUM();
            oMember.sMemGenKey = '9582004256425';
            oMember.initiateRequest();
            Endpoint_Url_HUM__c oMemberPrefernceLink = new Endpoint_Url_HUM__c();
            oMemberPrefernceLink.name='MemberDetails';
            oMemberPrefernceLink.Remote_Url__c= 'https://testst-cmservicews.hum.com/';
            insert oMemberPrefernceLink;  
            system.assert(oMemberPrefernceLink.id!=null);
            system.assert(oMemberPrefernceLink.Name == 'MemberDetails');
            oMember.sRedirectMember ='redirecMmeberView';
         }
      }
 }