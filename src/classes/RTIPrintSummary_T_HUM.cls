/**************************************************************************************************************************************************
Apex Class Name :   RTIPrintSummary_T_HUM
Version:  1.0
Created Date       :  28 June 2017
Function           : This Is the test class for RTIPrintSummary_C_HUM controller class

   Modification Log: 
    
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------
 *  Shivam Anand                    106733              06/28/2017         Original Version
 *  Imran  Khan                                         02/24/2020         VAt channel
 *  Rahul Chaurasia								  		03/22/2021			1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service
 *  Rahul Chaurasia                                     05/19/2021         US - 2082372 Increased code coverage for RTIPrintSummary_S_HUM
*****************************************************************************************************************************/

@isTest
Public with sharing Class RTIPrintSummary_T_HUM
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
        System.assertEquals(oCCSUser.LastName,'test User1');
        System.runAs(oCCSUser) 
        {
            Test.StartTest();
            HUM_Webservice_Callout__c oHUMRTISummaryService = New HUM_Webservice_Callout__c();
            oHUMRTISummaryService.name ='RTIService_GetInteractionDetails_V2';
            oHUMRTISummaryService.Active__c = True;
            oHUMRTISummaryService.Certificate_Name__c ='testts';
            oHUMRTISummaryService.End_Point_URL__c ='https://testst-cmservicews.hum.com/';
            oHUMRTISummaryService.Timeout__c ='120000';
            oHUMRTISummaryService.Content_Type__c ='application/json';
            oHUMRTISummaryService.Request_Type__c ='POST';
            insert oHUMRTISummaryService;
            System.assert(oHUMRTISummaryService.id!= null);
            System.assert(oHUMRTISummaryService.name == 'RTIService_GetInteractionDetails_V2');

        Document document;

            document = New Document();
            document.Body = Blob.valueOf('Some Text');
            document.ContentType = 'application/pdf';
            document.DeveloperName = 'RTI_VAT_Card_Icon';
            document.IsPublic = True;
            document.Name = 'RTI VAT Card Icon';            
            document.FolderId = UserInfo.getUserId();
            insert document;
            Test.stopTest(); 
        }
    }   
    
   /*
    * Method Name : runData
    * Description : Used to cover scenarios of controller
    * Return type : void
    * Paramater   : NA
    */
    Static testMethod void runData()
    {    
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);
            ApexPages.currentPage().getParameters().put('InterKey','1234');
            RTIPrintSummary_C_HUM oRTISummary = New RTIPrintSummary_C_HUM();  
            ApexPages.currentPage().getParameters().put('sChannelCode','EMAIL');
            Document oDocument = [SELECT Id, Name FROM Document where Name ='RTI VAT Card Icon' or Name='RTI Email Icon' or Name='RTI Text Icon'];
            Map<string,Id> mapRTIIcons = new Map<string,Id>();
            mapRTIIcons.put(oDocument.Name,oDocument.Id);
            String siconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI Email Icon');  
            
            Integer iTIMEOUT_INT_SECS = 120000;
            HttpRequest request = New HttpRequest();
            String sInteractionKey = '1234';
            String sChannelCode = 'PRINT';
            Integer iSkipNmbr = 0;
            Integer iRecordLimit = 200;
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            RTIPrintSummary_S_HUM oRTISummaryS = New RTIPrintSummary_S_HUM();

            oRTISummaryS.setupRequest(request,oServiceCallout,sInteractionKey,sChannelCode,iSkipNmbr,iRecordLimit);
            Continuation objCon2 = New Continuation(120);
            objCon2.continuationMethod='processResponse';
            
            
            oRTISummary.initiateRequest();
            oRTISummary.sInteractionKey ='1234';
            oRTISummary.iSkip = 0;
            oRTISummary.sChannelCode ='PRINT';
            oRTISummary.iRecordLimit = 200;
            HUM_Webservice_Callout__c oHUMRTISummaryService = New HUM_Webservice_Callout__c();
            oHUMRTISummaryService.name ='RTIService_GetInteractionDetails_V2';
            oHUMRTISummaryService.Active__c = True;
            oHUMRTISummaryService.Certificate_Name__c ='testts';
            oHUMRTISummaryService.End_Point_URL__c ='https://testst-cmservicews.hum.com/';
            oHUMRTISummaryService.Timeout__c ='120000';
            oHUMRTISummaryService.Content_Type__c ='application/json';
            oHUMRTISummaryService.Request_Type__c ='POST';
            insert oHUMRTISummaryService;
            System.assert(oHUMRTISummaryService.id!= null);
            System.assert(oHUMRTISummaryService.name == 'RTIService_GetInteractionDetails_V2');
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod ='processResponse';
            oRTISummary.processResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive'); 
            String sJson ='{"InteractionDetailResponse": {"InteractionDetailResult":{"prints":[{"documentViewUrl":'+'"https://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=GAdfqBjJA062EBb6Idl3qdwdHhyJGSI7/z7GyINHcLNJmOGcI+bTJr+tQOwEvuc2+IKcdKPJyv4GwLD8Vr5FjCvmwWwZHgcvbU/4q2ZrjKCLVBtFkRk+/hUwEYufyd5vAOQ1XVi/H+E+wpzqBFME2w==&UrlType=VDN&UrlDocumentType=PDF"}]}}}';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            oRTISummary.responseparse(oResponse);
            System.assert(oRTISummary!= null);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryList = New list<RTIPrintSummary_DTO_HUM>();
            RTIPrintSummary_DTO_HUM objectWrapper = New RTIPrintSummary_DTO_HUM();
            objectWrapper.sdocumentViewUrl='https://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=GAdfqBjJA062EBb6Idl3qdwdHhyJGSI7/z7GyINHcLNJmOGcI+bTJr+tQOwEvuc2+IKcdKPJyv4GwLD8Vr5FjCvmwWwZHgcvbU/4q2ZrjKCLVBtFkRk+/hUwEYufyd5vAOQ1XVi/H+E+wpzqBFME2w==&UrlType=VDN&UrlDocumentType=PDF';
            System.assertEquals('https://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=GAdfqBjJA062EBb6Idl3qdwdHhyJGSI7/z7GyINHcLNJmOGcI+bTJr+tQOwEvuc2+IKcdKPJyv4GwLD8Vr5FjCvmwWwZHgcvbU/4q2ZrjKCLVBtFkRk+/hUwEYufyd5vAOQ1XVi/H+E+wpzqBFME2w==&UrlType=VDN&UrlDocumentType=PDF',objectWrapper.sdocumentViewUrl); 
            oRTISummaryList.add(objectWrapper);
            RTIPrintSummary_C_HUM oRTIPrint = New RTIPrintSummary_C_HUM();
            oRTIPrint.initiateRequest();
            Continuation objCon1 = New Continuation(120);
            objCon1.continuationMethod='processResponseParellel';
            
            oRTIPrint.processResponse();
            HttpResponse oResponse1 = New HttpResponse();
            oResponse1.setHeader('Content-Type', 'application/json');
            oResponse1.setHeader('SOAPAction', '');    
            oResponse1.setHeader('Connection','keep-alive');
            String sjson1 = '{ "InteractionDetailResponse":{ "InteractionDetailResult":{ "prints":['+
                             ']'+ 
                             '}} }';
                 
            oResponse1.setBody(sJson);
            oResponse1.setStatusCode(200); 
            oRTIPrint.responseparse(oResponse1);
            oResponse1.setBody(sJson1);
            oResponse1.setStatusCode(200);
            oRTIPrint.responseparse(oResponse1);
            oRTIPrint.lstRTIInteractions.clear();
            oRTIPrint.responseParse(oResponse1);
            oResponse1.setBody(sJson1);
            oResponse1.setStatusCode(404);
            oRTIPrint.responseparse(oResponse1);
            Test.stopTest(); 
        }
    }
    
     /*
     * Method Name : printSummarException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    Static testMethod void printSummarException() 
    {
        Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        {
            Test.startTest();
            Test.setCurrentPage(Page.ClaimSummary_VF_HUM_V2);
            RTIPrintSummary_C_HUM oRTISummary = new RTIPrintSummary_C_HUM();
            oRTISummary.initiateRequest();
            oRTISummary.processResponse();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = '{"GetInteractionDetail": []}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(203); 
            oRTISummary.responseparse(oResponse); 
        }
    }
}