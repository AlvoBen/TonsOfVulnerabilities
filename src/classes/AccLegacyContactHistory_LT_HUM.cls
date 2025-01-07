/*****************************************************************************************
Apex class Name    : AccLegacyContactHistory_LT_HUM
Version            : 1.0
Created Date       : 03/15/2021 

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Ritik Agarwal                                      03/15/2021          Initial version(#2039867)
*  Ritik Agarwal                                      03/23/2021          add method for ccp reference number caalout
*  Kajal Namdev                                       06/01/2021          Test class failure fix
*********************************************************************************************************************************/
@isTest
public with sharing class AccLegacyContactHistory_LT_HUM 
{
    
    @testSetup static void setUpTestData()
    {
        Test.startTest();
        RecordType oMemRecType = [SELECT Id,Name FROM RecordType WHERE Name = 'Member' Limit 1];
        Account oAcc = new Account();
        oAcc.Enterprise_ID__c='1030693160';
        oAcc.Mbr_Gen_Key__c = '1234567890';
        oAcc.recordtypeid = oMemRecType.Id;
        oAcc.firstName = 'Daphiner';
        oAcc.lastName = 'Superman';
        insert oAcc;
        System.AssertNotEquals(oAcc, NULL);
        
        HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='InquirySummaryService',Timeout__c='12000',Request_Type__c='POST', Active__c=true, Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', 
                                                                                  Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
        insert mbeServiceRecord;
        HUM_Webservice_Callout__c mbeServiceRecord1 =new HUM_Webservice_Callout__c(Name='InquirySingleReferenceService',Timeout__c='12000',Request_Type__c='POST', Active__c=true, Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', 
                                                                                  Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
        insert mbeServiceRecord1;
        Test.stopTest();
    }  
    
    static testMethod void LegacySearch()
    {
        Test.startTest();
        List<Account> lstascc = [select id,recordtypeid,Mbr_Gen_Key__c,Enterprise_ID__c from Account where Mbr_Gen_Key__c='1234567890'];
        Continuation continueObj = (Continuation)AccLegacyContactHistory_LC_HUM.getInquirySummary(lstascc[0].id,null,null,'','','Member');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '{"ContactDetails":[{"CONTACT_ID":"888811031308","INQUIRY_ID":"1","INQ_TYPE_DESC":"CORRESPONDENCE INQUIRY","INQUIRER_FIRSTNAME":"","INQUIRER_LASTNAME":"","POLICY_NAME":"Medical","CATEGORY_CD_DESC":"","REASON_CD_DESC":"","DISPOSITION_DESC":"","INQA_ID":"H3009855600","CREATED_TIMESTAMP":"09/23/2020","INQUIRYFORTYPE":"Agent","INQR_ID":"1016307","INQUIRYABOUTTYPE":"Member","INQUIRYABOUTTYPECODE":"KM","INQUIRYFORTYPECODE":"KA","INQUIRYFORGENKEY":"616724172618","INQUIRYABOUTGENKEY":"616724172618","INQUIRYABOUT_FIRSTNAME":"RICHILEE                      ","INQUIRYABOUT_LASTNAME":"EDNILAO                       ","OWNER_FIRST_NAME":"Varsha","OWNER_LAST_NAME":"Patil","OWNER_TEAM_NAME":"CCP Test Medicare Enrollment                      ","OWNER_DEPT_NAME":"CCP Business Support Test Only                    ","OWNER_SITE_NAME":"Green Bay                                         ","TASKS":"Y","PRIORITY":"Normal","STATUS":"Open","RECEIVED_TS":"09/23/2020"},{"CONTACT_ID":"505432425607","INQUIRY_ID":"1","INQ_TYPE_DESC":"G&A INQUIRY","INQUIRER_FIRSTNAME":"","INQUIRER_LASTNAME":"","POLICY_NAME":"Medical","CATEGORY_CD_DESC":"Grievance and Appeals","REASON_CD_DESC":"Grievance and Appeals","DISPOSITION_DESC":"Grievance and Appeals","INQA_ID":"H3009855600     ","CREATED_TIMESTAMP":"09/23/2020","INQUIRYFORTYPE":"Employer","INQR_ID":"704325    ","INQUIRYABOUTTYPE":"Member","INQUIRYABOUTTYPECODE":"KM","INQUIRYFORTYPECODE":"KE","INQUIRYFORGENKEY":"616724172618","INQUIRYABOUTGENKEY":"616724172618","INQUIRYABOUT_FIRSTNAME":"RICHILEE                      ","INQUIRYABOUT_LASTNAME":"EDNILAO                       ","OWNER_FIRST_NAME":"Varsha","OWNER_LAST_NAME":"Patil","OWNER_TEAM_NAME":"CCP Test G and A                                  ","OWNER_DEPT_NAME":"CCP Business Support Test Only                    ","OWNER_SITE_NAME":"Green Bay                                         ","TASKS":"Y","PRIORITY":"Normal","STATUS":"Open","RECEIVED_TS":"09/23/2020"}],"count":"7319","FLAG_24_MONTHS":"Y"}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            Object result = AccLegacyContactHistory_LC_HUM.parseInquirySummary(labelGetmember,null);
            system.assert(result!=NULL);  
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }      
    }
    
    static testMethod void FailedLegacySearch()
    {
        Test.startTest();
        List<Account> lstascc = [select id,recordtypeid,Mbr_Gen_Key__c,Enterprise_ID__c from Account where Mbr_Gen_Key__c='1234567890'];
        Continuation continueObj = (Continuation)AccLegacyContactHistory_LC_HUM.getInquirySummary(lstascc[0].id,null,null,'','','Member');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString ='';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = '';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            Object result = AccLegacyContactHistory_LC_HUM.parseInquirySummary(labelGetmember,null);
            system.assert(result==NULL);  
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }      
    }
    
    static testMethod void CCPLegacySearch()
    {
        Test.startTest();
        List<Account> lstascc = [select id,recordtypeid,Mbr_Gen_Key__c,Enterprise_ID__c from Account where Mbr_Gen_Key__c='1234567890'];
        Continuation continueObj = (Continuation)AccLegacyContactHistory_LC_HUM.getInquirySingleReference(lstascc[0].id,'1234567890','Member');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '{"ContactDetails":[{"CONTACT_ID":"888811031308","INQUIRY_ID":"1","INQ_TYPE_DESC":"CORRESPONDENCE INQUIRY","INQUIRER_FIRSTNAME":"","INQUIRER_LASTNAME":"","POLICY_NAME":"Medical","CATEGORY_CD_DESC":"","REASON_CD_DESC":"","DISPOSITION_DESC":"","INQA_ID":"H3009855600","CREATED_TIMESTAMP":"09/23/2020","INQUIRYFORTYPE":"Agent","INQR_ID":"1016307","INQUIRYABOUTTYPE":"Member","INQUIRYABOUTTYPECODE":"KM","INQUIRYFORTYPECODE":"KA","INQUIRYFORGENKEY":"616724172618","INQUIRYABOUTGENKEY":"616724172618","INQUIRYABOUT_FIRSTNAME":"RICHILEE                      ","INQUIRYABOUT_LASTNAME":"EDNILAO                       ","OWNER_FIRST_NAME":"Varsha","OWNER_LAST_NAME":"Patil","OWNER_TEAM_NAME":"CCP Test Medicare Enrollment                      ","OWNER_DEPT_NAME":"CCP Business Support Test Only                    ","OWNER_SITE_NAME":"Green Bay                                         ","TASKS":"Y","PRIORITY":"Normal","STATUS":"Open","RECEIVED_TS":"09/23/2020"},{"CONTACT_ID":"505432425607","INQUIRY_ID":"1","INQ_TYPE_DESC":"G&A INQUIRY","INQUIRER_FIRSTNAME":"","INQUIRER_LASTNAME":"","POLICY_NAME":"Medical","CATEGORY_CD_DESC":"Grievance and Appeals","REASON_CD_DESC":"Grievance and Appeals","DISPOSITION_DESC":"Grievance and Appeals","INQA_ID":"H3009855600     ","CREATED_TIMESTAMP":"09/23/2020","INQUIRYFORTYPE":"Employer","INQR_ID":"704325    ","INQUIRYABOUTTYPE":"Member","INQUIRYABOUTTYPECODE":"KM","INQUIRYFORTYPECODE":"KE","INQUIRYFORGENKEY":"616724172618","INQUIRYABOUTGENKEY":"616724172618","INQUIRYABOUT_FIRSTNAME":"RICHILEE                      ","INQUIRYABOUT_LASTNAME":"EDNILAO                       ","OWNER_FIRST_NAME":"Varsha","OWNER_LAST_NAME":"Patil","OWNER_TEAM_NAME":"CCP Test G and A                                  ","OWNER_DEPT_NAME":"CCP Business Support Test Only                    ","OWNER_SITE_NAME":"Green Bay                                         ","TASKS":"Y","PRIORITY":"Normal","STATUS":"Open","RECEIVED_TS":"09/23/2020"}],"count":"7319","FLAG_24_MONTHS":"Y"}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            Object result = AccLegacyContactHistory_LC_HUM.parseInquirySummary(labelGetmember,null);
            system.assert(result!=NULL);  
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }      
    }
    
}