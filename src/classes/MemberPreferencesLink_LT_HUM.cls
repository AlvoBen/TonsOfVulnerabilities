/**************************************************************************************************************************************************
Apex class Name    :  MemberPreferencesLink_LT_HUM
Version            :  1.0
Created Date       :  18/01/2023 
Function           :  This is the test class for MemberPreferencesLink_LS_HUM and MemberPreferencesLink_LC_HUM controller class

Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 *  Vishal Shinde                                     18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 ************************************************************************************************************************************************************************************/

@isTest
public with sharing class MemberPreferencesLink_LT_HUM
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
            MemberPreferencesLink_LS_HUM oMemPreferenceLink = new MemberPreferencesLink_LS_HUM();
            MemberPreferencesLink_LDTO_HUM oMemPreferenceResponse = new MemberPreferencesLink_LDTO_HUM();
            HttpRequest oRequest = new HttpRequest();
            String sResponse ='';
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oServiceCallout.Timeout__c)/1000;
            System.assertEquals(oServiceCallout.Certificate_Name__c,'Humana_GuidanceCare_Venus');
            String sMemGenKey = '9582004256425';
            MemberPreferencesLink_LC_HUM.initiateRequest(sMemGenKey);
            String Response = '{ "EncryptResponse":{ "EncryptResult":{ "EncryptValue":"5spBhgm7Q6RDw+Mfydllig==" } } }';
            
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
            
            MemberPreferencesLink_LC_HUM.initiateRequest('9582004256425');
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
            MemberPreferencesLink_LC_HUM.processResponse(new List<String>{MemberPreferencesLink_LC_HUM.sRequest},'State');
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive'); 
            String sJson ='{ "EncryptResponse":{ "EncryptResult":{ "EncryptValue":"5spBhgm7Q6RDw+Mfydllig==" } } }';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200);
            MemberPreferencesLink_LC_HUM.responseParse(oResponse);
            Test.setContinuationResponse(MemberPreferencesLink_LC_HUM.sRequest,oResponse);
            MemberPreferencesLink_LC_HUM.sMemEncryptedValue ='5spBhgm7Q6RDw+Mfydllig==';
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
			MemberPreferencesLink_LC_HUM.initiateRequest('');
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod= 'processResponse';
            MemberPreferencesLink_LC_HUM.processResponse(new List<String>{MemberPreferencesLink_LC_HUM.sRequest},'State');
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  =  '{ "EncryptResponse":{ }}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(203); 
            MemberPreferencesLink_LC_HUM.responseParse(oResponse); 
             
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
            MemberPreferencesLink_LC_HUM.initiateRequest('9582004256425');
            Endpoint_Url_HUM__c oMemberPrefernceLink = new Endpoint_Url_HUM__c();
            oMemberPrefernceLink.name='MemberDetails';
            oMemberPrefernceLink.Remote_Url__c= 'https://testst-cmservicews.hum.com/';
            insert oMemberPrefernceLink;  
            system.assert(oMemberPrefernceLink.id!=null);
            system.assert(oMemberPrefernceLink.Name == 'MemberDetails');
            MemberPreferencesLink_LC_HUM.sRedirectMember ='redirecMmeberView';
         }
      }
 }