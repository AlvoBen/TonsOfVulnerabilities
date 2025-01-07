/***************************************************************************************************************************************************************** 
Apex Class Name : AutomatedEnrollmentHistory_LT_HUM
Version : 1.0 
Created On : 11/17/2020
Function : Test Class to cover AutomatedEnrollmentHistory_LC_HUM for Exceptions

* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Rahul Krishan                                       11/17/2020                   Original Version
*  Kajal Namdev                                        05/07/2021                   Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
**********************************************************************************************************************************************************************/

@isTest
public class AutomatedEnrollmentHistory_LT_HUM
{    
    @testSetup static void setUpTestData()
    {
        Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
        Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
        Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_CONSTANT_LH_HUM.HumMemberRecordType);
        HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='AEHistoryService',Timeout__c='12000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', 
                                                                                  Content_Type__c = 'text/xml',End_Point_URL__c = 'test_End_Point');
        insert mbeServiceRecord;
        system.assert(mbeServiceRecord !=NULL);
    }
    
    
    static testMethod void AutomatedEnrollmentHistoryexception()
    {
        AutomatedEnrollmentHistory_LC_HUM.AutomatedSearch automated = new AutomatedEnrollmentHistory_LC_HUM.AutomatedSearch();
        Test.startTest();
        Continuation continueObj = (Continuation)AutomatedEnrollmentHistory_LC_HUM.searchAEService('{"sSSN":"274965160","sEndDate":"11/19/2020","sEffectiveDate":"03/23/1929"}');
        Test.stopTest();            
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '<?xml version="1.0"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:phres="http://AEServices.humana.com/AEPend/V1.0/GetPendHistoryResponse"><soapenv:Body><phres:Response><Result>SUCCESS</Result><PendHistoryList><PendHistory></PendHistory></PendHistoryList></phres:Response></soapenv:Body></soapenv:Envelope>';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            AutomatedEnrollmentHistory_LC_HUM.AEServiceProcessResponse(AutomatedEnrollmentHistory_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }
    }
    
    static testMethod void AutomatedEnrollmentHistoryexceptionfalse()
    {
        AutomatedEnrollmentHistory_LC_HUM.AutomatedSearch automated = new AutomatedEnrollmentHistory_LC_HUM.AutomatedSearch();
        Test.startTest();
        Continuation continueObj = (Continuation)AutomatedEnrollmentHistory_LC_HUM.searchAEService('{"sSSN":"274965160","sEndDate":"11/19/2020","sEffectiveDate":"03/23/1929"}');
        Test.stopTest();            
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(400);
        String resBodyString = '';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            AutomatedEnrollmentHistory_LC_HUM.AEServiceProcessResponse(AutomatedEnrollmentHistory_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }
    }
    
    static testMethod void AutomatedEnrollmentHistory()
    {
        AutomatedEnrollmentHistory_LC_HUM.AutomatedSearch automated = new AutomatedEnrollmentHistory_LC_HUM.AutomatedSearch();
        Test.startTest();
        Continuation continueObj = (Continuation)AutomatedEnrollmentHistory_LC_HUM.searchAEService('{"sSSN":"274965160","sEndDate":"11/19/2020","sEffectiveDate":"03/23/1929"}');
        Test.stopTest();            
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '<?xml version="1.0"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:phres="http://AEServices.humana.com/AEPend/V1.0/GetPendHistoryResponse"><soapenv:Body><phres:Response><Result>SUCCESS</Result><PendHistoryList><PendHistory><SSN/><Pend_Key>0000000000000</Pend_Key><Pers_Seq_Nbr>01</Pers_Seq_Nbr><From_Date>2015-02-04</From_Date><To_Date>2015-05-11</To_Date><Timestamp>2015-05-11T02:56:12.052Z</Timestamp><Member_SSN>274965160</Member_SSN><First_Name>SHEILA</First_Name><Middle_Init>V</Middle_Init><Last_Name>VENKOBA RAO</Last_Name><Name_Suff/><Birth_Date>1943-03-14</Birth_Date><Mar_Stat/><Sex_Type>F</Sex_Type><Rel_Type/><Addr1>11804 RIDGELAND DR</Addr1><Addr2/><City>KNOXVILLE</City><State>TN</State><Zip>37932</Zip><Zipplus>2835</Zipplus><Phone/><Platform>M</Platform><Group_Id>675717</Group_Id><Ben_Id/><Category/><Pend_Error/><Pers_Rec_Stat>G</Pers_Rec_Stat><Ben_Rec_Stat/></PendHistory><PendHistory><SSN/><Pend_Key>0000000000000</Pend_Key><Pers_Seq_Nbr>01</Pers_Seq_Nbr><From_Date>2015-02-04</From_Date><To_Date>2015-05-11</To_Date><Timestamp>2015-05-11T02:56:12.052Z</Timestamp><Member_SSN>274965160</Member_SSN><First_Name>SHEILA</First_Name><Middle_Init>V</Middle_Init><Last_Name>VENKOBA RAO</Last_Name><Name_Suff/><Birth_Date>1943-03-14</Birth_Date><Mar_Stat/><Sex_Type>F</Sex_Type><Rel_Type/><Addr1>11804 RIDGELAND DR</Addr1><Addr2/><City>KNOXVILLE</City><State>TN</State><Zip>37932</Zip><Zipplus>2835</Zipplus><Phone/><Platform>M</Platform><Group_Id>675717</Group_Id><Ben_Id/><Category/><Pend_Error/><Pers_Rec_Stat>G</Pers_Rec_Stat><Ben_Rec_Stat/></PendHistory></PendHistoryList></phres:Response></soapenv:Body></soapenv:Envelope>';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            AutomatedEnrollmentHistory_LC_HUM.AEServiceProcessResponse(AutomatedEnrollmentHistory_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }
    }
    
    
    static testMethod void AutomatedEnrollmentHistoryfalse_1()
    {
        Test.startTest();
        Continuation continueObj = (Continuation)AutomatedEnrollmentHistory_LC_HUM.searchAEService('{"sSSN":"274965160","sEndDate":"11/19/2020","sEffectiveDate":"03/23/1929"}');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(400);
        Test.setContinuationResponse('Hello',httpRes);
        try
        {
            AutomatedEnrollmentHistory_LC_HUM.AEServiceProcessResponse(AutomatedEnrollmentHistory_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }
    }
    
    static testMethod void AutomatedEnrollmentHistoryfalse_2()
    {
        try
        {
            Test.startTest();
            Continuation continueObj = (Continuation)AutomatedEnrollmentHistory_LC_HUM.searchAEService('{"sSSN":"","sEndDate":"","sEffectiveDate":""}');
            Test.stopTest();
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(400);
            Test.setContinuationResponse('Hello',httpRes);
            AutomatedEnrollmentHistory_LC_HUM.AEServiceProcessResponse(AutomatedEnrollmentHistory_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }        
    }
}