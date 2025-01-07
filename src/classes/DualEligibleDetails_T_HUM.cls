/*****************************************************************************************
Apex class Name    : DualEligibleDetails_T_HUM
Version            : 1.0
Function           : This is the Test class for DualEligibleDetails_C_HUM
Created Date       : 05/18/2019 
Main Class         : DualEligibleDetails_C_HUM

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Suresh Sunkari                                    05/18/2019          Test Class for DualEligibleDetails_C_HUM.
*********************************************************************************************************************************/    
        
@isTest
public class DualEligibleDetails_T_HUM{
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
            HUM_Webservice_Callout__c oHUMMoopService = new HUM_Webservice_Callout__c();
            oHUMMoopService.name='MBEPLUSServiceREST';
            oHUMMoopService.Active__c=true;
            oHUMMoopService.Certificate_Name__c='testts';
            oHUMMoopService.End_Point_URL__c='https://testst-cmservicews.hum.com';
            oHUMMoopService.Timeout__c='120000';
            oHUMMoopService.Content_Type__c='application/json';
            oHUMMoopService.Request_Type__c='POST';
            insert oHUMMoopService ;
            system.assert(oHUMMoopService.id!=null);
            system.assert(oHUMMoopService.name == 'MBEPLUSServiceREST');
            
        
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
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='MBEPLUSServiceREST' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
                 
            Test.StartTest();
                    
            Test.setCurrentPage(Page.DualEligibleDetails_VF_HUM);
            DualEligibleDetails_C_HUM oMoopDetail = new DualEligibleDetails_C_HUM();
            DualEligibleDetails_S_HUM oMoopDetail1 = new DualEligibleDetails_S_HUM();
            oMoopDetail.initiateRequest();
            HttpRequest oRequest = new HttpRequest();
            oMoopDetail.sPersonId = '1097094766';
            oMoopDetail1.setupRequest(oRequest,oServiceCallout,oMoopDetail.sPersonId);
            Continuation objCon = New Continuation(120);
            objCon.addHttpRequest(oRequest);
            objCon.continuationMethod='process';
            oMoopDetail.processResponse();
            
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            
            String response ='{"GetMemberResponse": {"Body": {"DualStatusList": [{"EffectiveDate": "2019-01-31","EndDate": "2019-01-31","Value": "QI","Code": "06","Indicator": "N"},{"EffectiveDate": "2019-01-31","EndDate": "2019-01-31","Value": "QI","Code": "06","Indicator": "N"}],"DeemingPeriodList": [{"EffectiveDate": "2019-06-01","EndDate": "2019-06-30","Indicator": true}]}}}';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse !=null);
            oMoopDetail.responseParse(oResponse);
            
            
            HttpResponse oResponse1 = New HttpResponse();
            oResponse1.setHeader('Content-Type', 'application/json');
            oResponse1.setHeader('SOAPAction', '');    
            oResponse1.setHeader('Connection','keep-alive');
            oResponse1.setBody('{"GetMemberResponse": {"Body": {"DualStatusList": [{"EffectiveDate": "2019-01-31","EndDate": "2019-01-31","Value": "QI","Code": "06","Indicator": "N"},{"EffectiveDate": "2019-01-31","EndDate": "2019-01-31","Value": "QI","Code": "06","Indicator": "N"}],"DeemingPeriodList": []}}}');
            oMoopDetail.responseParse(oResponse1);
            oResponse1.setStatusCode(0); 
            oMoopDetail.responseParse(oResponse1);
            oMoopDetail.sPersonId = 'Test';
            oMoopDetail.initiateRequest();
            
            
            HttpResponse oResponse2 = New HttpResponse();
            oResponse2.setHeader('Content-Type', 'application/json');
            oResponse2.setHeader('SOAPAction', '');    
            oResponse2.setHeader('Connection','keep-alive');
            oResponse2.setBody(' {"GetMemberResponse": {"Body": {"DualStatusList": [], "DeemingPeriodList": []}}}');
            oMoopDetail.responseParse(oResponse2);
            oResponse2.setStatusCode(0); 
            oMoopDetail.responseParse(oResponse2);      
            oMoopDetail.sPersonId = 'Test';
            oMoopDetail.initiateRequest();
        
        }
        }
        
  
    }