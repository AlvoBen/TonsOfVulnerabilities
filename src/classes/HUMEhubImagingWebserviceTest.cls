/****************************************************************************
Apex Class Name  : HUMEhubImagingWebserviceTest
Version          : 1.0 
Created Date     : March 11 2014
Function         : Test class to HUMEhubImagingWebservice
Modification Log :
 *   Modification ID     Developer          Code Review             Date                    Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *           1.0         Sharan Shanmugam      21654              03/11/2015             Original Version  
 *        				 Prashant Moghe							   08/20/2021					US- 1928764 - T1PRJ0002736 - 2021 MF6 - Security  - CR AEP readiness - Information Security Issues
 **********************************************************************************************************************************/
@isTest
private class HUMEhubImagingWebserviceTest 
{  
    /*
     * Method Name : EhubImagingWebservice
     * Description : 
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void EhubImagingWebservice() 
    {
        Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
        HUMEhubImagingWebservice.GetDocumentURL_element GetDocumentURL_element = new HUMEhubImagingWebservice.GetDocumentURL_element();
        HUMEhubImagingWebservice.GetDocumentBytesResponse_element GetDocumentBytesResponse_element = new HUMEhubImagingWebservice.GetDocumentBytesResponse_element();
        HUMEhubImagingWebservice.DssResultDTO DssResultDTO = new HUMEhubImagingWebservice.DssResultDTO();
        HUMEhubImagingWebservice.AddDocumentWithUserIdResponse_element AddDocumentWithUserIdResponse_element = new HUMEhubImagingWebservice.AddDocumentWithUserIdResponse_element();
        HUMEhubImagingWebservice.GetDocumentXmlResult_element GetDocumentXmlResult_element = new HUMEhubImagingWebservice.GetDocumentXmlResult_element();
        HUMEhubImagingWebservice.DeleteDocumentResponse_element DeleteDocumentResponse_element = new HUMEhubImagingWebservice.DeleteDocumentResponse_element();
        HUMEhubImagingWebservice.GetDocumentURLResponse_element GetDocumentURLResponse_element = new HUMEhubImagingWebservice.GetDocumentURLResponse_element();
        HUMEhubImagingWebservice.DeleteDocument_element DeleteDocument_element = new HUMEhubImagingWebservice.DeleteDocument_element();
        HUMEhubImagingWebservice.GetDocumentBytes_element GetDocumentBytes_element = new HUMEhubImagingWebservice.GetDocumentBytes_element();
        HUMEhubImagingWebservice.GetDocumentXmlResponse_element GetDocumentXmlResponse_element = new HUMEhubImagingWebservice.GetDocumentXmlResponse_element();
        HUMEhubImagingWebservice.AddDocumentWithUserId_element AddDocumentWithUserId_element = new HUMEhubImagingWebservice.AddDocumentWithUserId_element();
        HUMEhubImagingWebservice.GetDocumentXml_element GetDocumentXml_element = new HUMEhubImagingWebservice.GetDocumentXml_element();
        HUMEhubImagingWebservice.DSSImportServiceSoap DSSImportServiceSoap = new HUMEhubImagingWebservice.DSSImportServiceSoap();
        System.assertEquals('https://dev-crmservicews.humana.com/eHubAttachmentService_V1.0', DSSImportServiceSoap.endpoint_x);
		DSSImportServiceSoap.AddDocumentWithUserId('1234','1234','','abc','cc','1234');
    }
}