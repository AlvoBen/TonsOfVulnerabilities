/***************************************************************************************************************************************************************** 
Apex Class Name : TrackEnrollmentSearch_LT_HUM
Version : 1.0 
Created On : 11/17/2020
Function : Test Class to cover TrackEnrollmentSearch_LC_HUM

* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Rahul Krishan                                       11/17/2020                   Original Version
*  Kajal Namdev                                        05/07/2021                   Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
**********************************************************************************************************************************************************************/

@isTest public class TrackEnrollmentSearch_LT_HUM
{    
    @testSetup static void setUpTestData()
    {
        Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
        Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
        Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_CONSTANT_LH_HUM.HumMemberRecordType);
        List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
        lstConstants.add(new HUMConstants__c(Name='ENROLLMENT_TE',StringValue__c='UKM|00|TE|',IntegerValue__c=Null));
        lstConstants.add(new HUMConstants__c(Name='UNKNOWN_MEMBER',StringValue__c='Unknown Member',IntegerValue__c=Null));
        insert lstConstants;
        HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='TrackEnrollmentService',Timeout__c='12000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Test_Certificate', 
                                                                                  Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
        insert mbeServiceRecord;
        system.assert(mbeServiceRecord !=NULL);
    }
    
    
    static testMethod void trackEnrollmentSearch()
    {
        TrackEnrollmentSearch_LC_HUM.TrackEnroll track = new TrackEnrollmentSearch_LC_HUM.TrackEnroll();
        Test.startTest();
        Continuation continueObj = (Continuation)TrackEnrollmentSearch_LC_HUM.searchTrackEnrollmentSearvice('{"sSSN":"005279607","sEndDate":"11/17/2020","sEffectiveDate":"07/17/2020"}');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        String resBodyString = '<?xml version="1.0" encoding="UTF-8"?>';
        resBodyString += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:stres="http://enrollmentservices.humana.com/EnrollmentTracking/V1.0/SearchTrackResponse">';
        resBodyString += '<soapenv:Body>';
        resBodyString += '<stres:Response>';
        resBodyString += '<Result>Success</Result>';
        resBodyString += '<Error></Error>';
        resBodyString += '<Timestamp>2020-11-17T04:30:41</Timestamp>';
        resBodyString += '<TrackList>';
        resBodyString += '<Track>';
        resBodyString += '<Subscriber_SSN>005279607</Subscriber_SSN>';
        resBodyString += '<First_Name>LEWILLIAM</First_Name>';
        resBodyString += '<Last_Name>WOOLEMS</Last_Name>';
        resBodyString += '<Birth_Date>0001-01-01</Birth_Date>';
        resBodyString += '<Zip_Cd></Zip_Cd>';
        resBodyString += '<Group_Id>654671</Group_Id>';
        resBodyString += '<Source_Track_Id>2973506175</Source_Track_Id>';
        resBodyString += '<Recipient_Track_Id>2973506175</Recipient_Track_Id>';
        resBodyString += '<Source_System_Cd>MTV</Source_System_Cd>';
        resBodyString += '<Recipient_System_Cd>ODS</Recipient_System_Cd>';
        resBodyString += '<Source_Id_Type_Cd>TRAN</Source_Id_Type_Cd>';
        resBodyString += '<Recipient_Id_Type_Cd>TRAN</Recipient_Id_Type_Cd>';
        resBodyString += '<Status_Cd>CREATED</Status_Cd>';
        resBodyString += '<Trans_Rec_Type_Cd></Trans_Rec_Type_Cd>';
        resBodyString += '<Category_Id>01</Category_Id>';
        resBodyString += '<Line_Of_Coverage></Line_Of_Coverage>';
        resBodyString += '<Coverage_Type_Cd></Coverage_Type_Cd>';
        resBodyString += '<Track_Date>2020-10-08</Track_Date>';
        resBodyString += '<Track_Time>06.55.57</Track_Time>';
        resBodyString += '<Pend_Key>0</Pend_Key></Track>';
        resBodyString += '<Track>';
        resBodyString += '<Subscriber_SSN>005279607</Subscriber_SSN>';
        resBodyString += '<First_Name>LEWILLIAM</First_Name>';
        resBodyString += '<Last_Name>WOOLEMS</Last_Name>';
        resBodyString += '<Birth_Date>0001-01-01</Birth_Date>';
        resBodyString += '<Zip_Cd></Zip_Cd>';
        resBodyString += '<Group_Id>654671</Group_Id>';
        resBodyString += '<Source_Track_Id>2973506176</Source_Track_Id>';
        resBodyString += '<Recipient_Track_Id>2973506176</Recipient_Track_Id>';
        resBodyString += '<Source_System_Cd>MTV</Source_System_Cd>';
        resBodyString += '<Recipient_System_Cd>ODS</Recipient_System_Cd>';
        resBodyString += '<Source_Id_Type_Cd>TRAN</Source_Id_Type_Cd>';
        resBodyString += '<Recipient_Id_Type_Cd>TRAN</Recipient_Id_Type_Cd>';
        resBodyString += '<Status_Cd>CREATED</Status_Cd>';
        resBodyString += '<Trans_Rec_Type_Cd></Trans_Rec_Type_Cd>';
        resBodyString += '<Category_Id>01</Category_Id>';
        resBodyString += '<Line_Of_Coverage></Line_Of_Coverage>';
        resBodyString += '<Coverage_Type_Cd></Coverage_Type_Cd>';
        resBodyString += '<Track_Date>2020-10-08</Track_Date>';
        resBodyString += '<Track_Time>06.55.57</Track_Time>';
        resBodyString += '<Pend_Key>0</Pend_Key></Track></TrackList></stres:Response></soapenv:Body></soapenv:Envelope>';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        Object result = TrackEnrollmentSearch_LC_HUM.TrackEnrollmentSearviceProcessResponse(labelGetmember, TrackEnrollmentSearch_LC_HUM.contstate);
        system.assert(result!=NULL); 
    }
    
    static testMethod void trackEnrollmentSearchFalse1()
    {
        Test.startTest();
        Continuation continueObj = (Continuation)TrackEnrollmentSearch_LC_HUM.searchTrackEnrollmentSearvice('{"sSSN":"005279607","sEndDate":"11/17/2020","sEffectiveDate":"07/17/2020"}');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(400);
        String resBodyString ='';
        httpRes.setBody(resBodyString);  
        string reqType = 'Hello';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse('Hello',httpRes); 
        try
        {
            TrackEnrollmentSearch_LC_HUM.TrackEnrollmentSearviceProcessResponse(labelGetmember, TrackEnrollmentSearch_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);  
        }
        
    }
    
    static testMethod void trackEnrollmentSearchFalse2()
    {
        try
        {
            Test.startTest();
            Continuation continueObj = (Continuation)TrackEnrollmentSearch_LC_HUM.searchTrackEnrollmentSearvice('{"sSSN":""}');
            Test.stopTest();
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(400);
            httpRes.setBody(NULL);
            List<string> labelGetmember = NULL;
            Test.setContinuationResponse('',httpRes); 
            TrackEnrollmentSearch_LC_HUM.TrackEnrollmentSearviceProcessResponse(labelGetmember, NULL);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }
    }
    
    static testMethod void trackEnrollmentSearchfalse_3()
    {
        TrackEnrollmentSearch_LC_HUM.TrackEnroll track = new TrackEnrollmentSearch_LC_HUM.TrackEnroll();
        Test.startTest();
        Continuation continueObj = (Continuation)TrackEnrollmentSearch_LC_HUM.searchTrackEnrollmentSearvice('{"sSSN":"005279607","sEndDate":"11/17/2020","sEffectiveDate":"07/17/2020"}');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        String resBodyString ='';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            TrackEnrollmentSearch_LC_HUM.TrackEnrollmentSearviceProcessResponse(labelGetmember, TrackEnrollmentSearch_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }   
    } 
}