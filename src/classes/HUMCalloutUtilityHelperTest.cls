/********************************************************************************************************************************************
Apex Class Name  : HUMCalloutUtilityHelperTest
Version          : 1.0
Created On       : May 07 2015
Function         : Test class to HUMCalloutUtilityHelper Apex class

Modification Log :
* Developer Name          Code Review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma              22901              05/07/2015             Original Version
* Sharan Shanmugam           23024              05/12/2015             Made changes to cover the parsing xml block.
* Gargey Sharma              23473              05/27/2015             Added method TestfetchingElements to fetch child elements from xml
* Shruthi Karanth            34224              08/03/2016             Updated the response from xml to json inTestcallWebservice method
* Abhishek Maurya            2308421            06/26/2021             CRM Storefront SDoH: Action Required Prompt
********************************************************************************************************************************************/

@isTest
private class HUMCalloutUtilityHelperTest
{
    /*
    * Method name : TestcallWebservice
    * Description : Test Method to callWebservice
    * Return Type : void
    * Parameter   : NA
    */
    private static testMethod void TestcallWebservice()
    {   
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            HUMCalloutUtilityHelper calloutHelper = new HUMCalloutUtilityHelper();
            HUM_Webservice_Callout__c sampleRequest = new HUM_Webservice_Callout__c(Name='AVFTemplate',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TemplateService_V1.0/SaveAVFTemplate', Timeout__c='12000', Request_Type__c='GET', Content_Type__c='application/xml');
            insert sampleRequest;
            System.assert(sampleRequest.id != null);
            
            string Body = '{"TemplateResponse": {"Errors": {"Error": [{"Code": "00000","Description": "SUCCESSFUL UPDATE TO TMRAVF1"}]}}}';
            String strXml = '<?xml version="1.0" encoding="UTF-8"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">'+
                     '<soapenv:Header xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"/>'+
                     '<soapenv:Body><AVFTemplateResponse xmlns="http://tempuri.org/"><Errors>'+
                     '<Error code="00000" description="SUCCESSFUL UPDATE TO TMRAVF1"/></Errors></AVFTemplateResponse>'+
                     '</soapenv:Body></soapenv:Envelope>';
            HttpResponse response = new HttpResponse();     
            Test.startTest();
            	Test.setMock(HttpCalloutMock.class, new HUMAVFTemplateMockHttpResponseTest());
                response = HUMCalloutUtilityHelper.callWebService(sampleRequest.End_Point_URL__c, sampleRequest.Certificate_Name__c, Body, sampleRequest.Request_Type__c, sampleRequest.SOAP_Action__c, sampleRequest.Content_Type__c);
                XmlStreamReader xsr = new XmlStreamReader(strXml);
                HUMCalloutUtilityHelper.parseXML(xsr, 'Error');
            Test.stopTest();
            system.assert(response != null);
        }
    }
    
    /*
    * Method name : TestfetchingElements
    * Description : Test Method for methods to fetch child elements from xml
    * Return Type : void
    * Parameter   : NA
    */
    private static testMethod void TestfetchingElements()
    {   
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
        	List<HUM_Webservice_Callout__c> lstWebserviceCallout = new List<HUM_Webservice_Callout__c>();
	        lstWebserviceCallout.add( new HUM_Webservice_Callout__c(Name='VirtualSwitch_Get',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/VirtualConfigDetails/get', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C'));
	        lstWebserviceCallout.add( new HUM_Webservice_Callout__c(Name='VirtualSwitch_Update',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/VirtualConfigDetails/update', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C'));        
	        
	        insert lstWebserviceCallout; 
            HUMCalloutUtilityHelper calloutHelper = new HUMCalloutUtilityHelper();
            HUM_Webservice_Callout__c sampleRequest = new HUM_Webservice_Callout__c();
            sampleRequest.End_Point_URL__c= 'https://dev-crmservicews.humana.com/VirtualConfigDetails/get';
            sampleRequest.Request_Type__c='POST';
            sampleRequest.Content_Type__c='text/xml';
            sampleRequest.SOAP_Action__c='sampleAction';
            sampleRequest.Certificate_Name__c='TestCertificate';
            string Body = '<?xml version="1.0" encoding="UTF-8"?>SampleRequestBody<>';
            HttpResponse response = new HttpResponse();
            Test.startTest();
            	Test.setMock(HttpCalloutMock.class, new HUMVirtualSwitchGetMockHttpResponseTest());
                response = HUMCalloutUtilityHelper.callWebService(sampleRequest.End_Point_URL__c, sampleRequest.Certificate_Name__c, Body, sampleRequest.Request_Type__c, sampleRequest.SOAP_Action__c, sampleRequest.Content_Type__c);
                List<Dom.XMLNode> lstChildElements = HUMCalloutUtilityHelper.fetchChildElements(response.getBodyDocument().getRootElement());
                String Status = HUMCalloutUtilityHelper.fetchChild(response.getBodyDocument().getRootElement() , 'Status');
            Test.stopTest();
            system.assert(response != null);
        }
    }
    /*
    * Method name : TestcallWebservice_negative
    * Description : Test Method to callWebservice
    * Return Type : void
    * Parameter   : NA
    */
    private static testMethod void TestcallWebservice_negative()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            HUMCalloutUtilityHelper calloutHelper = new HUMCalloutUtilityHelper();
            HUM_Webservice_Callout__c sampleRequest = new HUM_Webservice_Callout__c();
            sampleRequest.End_Point_URL__c='https://dev-crmservicews.humana.com/CIMedicareMember';
            sampleRequest.Request_Type__c='POST';
            sampleRequest.Content_Type__c='text/xml';
            sampleRequest.SOAP_Action__c='sampleAction';
            sampleRequest.Certificate_Name__c='TestCertificate';
            string Body='<?xml version="1.0" encoding="UTF-8"?>SampleRequestBody<>';
            
            HttpResponse response = new HttpResponse();     
            Test.startTest();
                response = HUMCalloutUtilityHelper.callWebService(sampleRequest.End_Point_URL__c, sampleRequest.Certificate_Name__c, Body, sampleRequest.Request_Type__c, sampleRequest.SOAP_Action__c, sampleRequest.Content_Type__c);
            Test.stopTest();
            system.assert(response == null);
        }
    }
    /*
    * Method name : TestLogError
    * Description : Test Log Error Method
    * Return Type : void
    * Parameter   : NA
    */
    @isTest
    private static void TestLogError()
    {
        
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            List<Error_Log__c> listOfErrorLog = new List<Error_Log__c>();
            HttpResponse res = new HttpResponse();
            res.setStatusCode(400);
            FaultResponse_DTO_HUM.errorInfo errorObj=new FaultResponse_DTO_HUM.errorInfo();
            errorObj.error_code='300';
            errorObj.error_message='Something Went Wrong';
            
            FaultResponse_DTO_HUM.transactionInfo tranInfo= new FaultResponse_DTO_HUM.transactionInfo();
            tranInfo.URL_in='www.test.com';
            
            FaultResponse_DTO_HUM.detail objDetail=new FaultResponse_DTO_HUM.detail();
            objDetail.errorInfo=errorObj;
            objDetail.transactionInfo=tranInfo;
            
            FaultResponse_DTO_HUM.Fault faultObj=new FaultResponse_DTO_HUM.Fault();
            faultObj.faultcode='500';
            faultObj.faultstring='TestFault';
            faultObj.detail=objDetail;
            
            FaultResponse_DTO_HUM responseWrap=new FaultResponse_DTO_HUM();
            responseWrap.Fault=faultObj;
            
            res.setBody(JSON.serialize(responseWrap));
            Test.startTest();
            listOfErrorLog.add(HUMCalloutUtilityHelper.logBulkFaultError(res,'TestHelper', 'TestHelper.getData', '', 'CRMRetail_SDoHDetermt__c', 'INFO'));
            HUMCalloutUtilityHelper.logFaultError(res,'TestHelper', 'TestHelper.getData', '', 'CRMRetail_SDoHDetermt__c', 'INFO');
            Test.stopTest();
            system.assert((listOfErrorLog.size() > 0));
        }
    }
}