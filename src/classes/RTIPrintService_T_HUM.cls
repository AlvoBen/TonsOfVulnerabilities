/**************************************************************************************************************************************************
Apex Class Name :   RTIPrintService_T_HUM
Version:  1.0
Created Date       :  27th June 2017
Function           : This Is the test class for RTIPrintSummary_S_HUM

Modification Log: 

*   Developer                   Code Review             Date               Description
* -------------------------------------------------------------------------------------------------------------------------------------------------  
* Shivam Anand                     106733            06/27/2017           Original Version
* Rahul Chaurasia                                    05/19/2021           US - 2082359 Increased code covrage for RTIPrintSummary_S_HUM
************************************************************************************************************************************************* */

@isTest
Private Class RTIPrintService_T_HUM
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
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c oHUMRTISummaryService = New HUM_Webservice_Callout__c();
            oHUMRTISummaryService.name='RTIService_GetInteractionDetails_V2';
            oHUMRTISummaryService.Active__c=true;
            oHUMRTISummaryService.Certificate_Name__c='testts';
            oHUMRTISummaryService.End_Point_URL__c='https://testst-cmservicews.hum.com/';
            oHUMRTISummaryService.Timeout__c='120000';
            oHUMRTISummaryService.Content_Type__c='application/json';
            oHUMRTISummaryService.Request_Type__c='POST';
            insert oHUMRTISummaryService;
            system.assert(oHUMRTISummaryService.id!=null);
            system.assert(oHUMRTISummaryService.name == 'RTIService_GetInteractionDetails_V2');
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
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        {   
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts'); 
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);
            
            ApexPages.currentPage().getParameters().put('sChannelCode','PRINT');
            ApexPages.currentPage().getParameters().put('CategoryDesc','PRINT');
          
            String Response ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 2,   "Prints": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAb/TS7WzfBXU8K1x6DFsF8KeEYFD33cq8KwuiWqhkMrzX4EvbI581cY6MNP9PR3/ltjj+SwjaLhMsZTeqjnNicBejC0eK3h4mQGoCTH+YdO65/dI0txXTp/hC7CksKHkld0ciII84j6XpolTpoJGBluSM7F50sZ3+QCswaoofYzxTu6vlf0hIyU6hlb7GgoMkw==&UrlType=MVS&UrlDocumentType=PDF"}]}}}';
            String sInteractionKey = 'e7ef28a30fa89829ecaf255d92c17b3e';
            String sChannelCode = 'PRINT';
            Integer iSkipNmbr = 0;
            Integer iRecordLimit = 200;
   
            Test.StartTest();
            RTIPrintSummary_S_HUM oRTISummary = New RTIPrintSummary_S_HUM();
            oRTISummary.buildRequest(sInteractionKey,sChannelCode,iSkipNmbr,iRecordLimit);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryList = New List<RTIPrintSummary_DTO_HUM>();
            oRTISummaryList = oRTISummary.processResponse(Response);
            ApexPages.currentPage().getParameters().put('sChannelCode','PRINT');
            System.assertEquals(ApexPages.currentPage().getParameters().get('sChannelCode'),'PRINT');            
            HttpRequest oRequest = New HttpRequest();
            oRTISummary.setupRequest(oRequest,oServiceCallout,sInteractionKey,sChannelCode,iSkipNmbr,iRecordLimit);            
            Test.stopTest();  
        }       
    } 
    
    /*
    * Method Name : runPositiveDataWeb
    * Description : Used to run positive scenarios of test data for Web
    * Return type : void
    * Paramater   : NA
    */ 
    Static testMethod void runPositiveDataWeb(){
        
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        { 
            String ResponseWeb ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "Webs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF"   }]}}}';
            String sInteractionKeyWeb = '3df0b3fc685e8220d96b0fe47a974ffa';
            String sChannelCodeWeb = 'WEB';
            Integer iSkipNmbrWeb = 0;
            Integer iRecordLimitWeb = 200;            
            Test.StartTest();            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts'); 
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);            
            ApexPages.currentPage().getParameters().put('sChannelCode','WEB');
            ApexPages.currentPage().getParameters().put('CategoryDesc','WEB');
            RTIPrintSummary_S_HUM oRTISummary = New RTIPrintSummary_S_HUM();            
            RTIPrintSummary_S_HUM oRTISummaryWeb = New RTIPrintSummary_S_HUM();
            oRTISummary.buildRequest(sInteractionKeyWeb,sChannelCodeWeb,iSkipNmbrWeb,iRecordLimitWeb);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryListWeb = New List<RTIPrintSummary_DTO_HUM>();
            oRTISummaryListWeb = oRTISummary.processResponse(ResponseWeb);
            System.assertEquals(ApexPages.currentPage().getParameters().get('sChannelCode'),'WEB');
            HttpRequest oRequestWeb = New HttpRequest();
            oRTISummary.setupRequest(oRequestWeb,oServiceCallout,sInteractionKeyWeb,sChannelCodeWeb,iSkipNmbrWeb,iRecordLimitWeb);           
            Test.stopTest();        
        }
    }
    
    /*
    * Method Name : runPositiveDataWb
    * Description : Used to run positive scenarios for channel code with value Wb
    * Return type : void
    * Paramater   : NA
    */
    Static testMethod void runPositiveDataWb(){
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        { 
            String ResponseWb ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "wBs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF",      "Code": "LINK",      "Purpose": "LINK",      "actionTime": "2018-10-18T04:00:00Z",      "pageId": null,      "siteId": null,      "prodName": null,      "shopActionTypeCD": null,      "eventActionTypeCD": null,      "eventName": null,      "elementName": null   }]}}}';
            String sInteractionKeyWb = '3df0b3fc685e8220d96b0fe47a974ffa';
            String sChannelCodeWb = 'WB';
            Integer iSkipNmbrWb = 0;
            Integer iRecordLimitWb = 200;            
            Test.StartTest();            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts'); 
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);            
            ApexPages.currentPage().getParameters().put('sChannelCode','WB');
            ApexPages.currentPage().getParameters().put('CategoryDesc','WB');            
            RTIPrintSummary_S_HUM oRTISummaryWb = New RTIPrintSummary_S_HUM();
            oRTISummaryWb.buildRequest(sInteractionKeyWb,sChannelCodeWb,iSkipNmbrWb,iRecordLimitWb);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryListWb = New List<RTIPrintSummary_DTO_HUM>();
            oRTISummaryListWb = oRTISummaryWb.processResponse(ResponseWb);
            HttpRequest oRequestWb = New HttpRequest();
            oRTISummaryWb.setupRequest(oRequestWb,oServiceCallout,sInteractionKeyWb,sChannelCodeWb,iSkipNmbrWb,iRecordLimitWb);            
            Test.stopTest();        
        }
        
    }
    
   /*
    * Method Name : runPositiveDataEmail
    * Description : Used to run positive scenarios for channel code Email
    * Return type : void
    * Paramater   : NA
    */
    Static testMethod void runPositiveDataEmail(){
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        { 
            String ResponseEmail ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "Emails": [   {      "documentType": "SAMPLE",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=GAdfqBjJA062EBb6Idl3qdwdHhyJGSI7/z7GyINHcLPEyd3PpXXayJJdA7qw2WIDDZUTwTzZZA9m23AYeJlDqD1pMMwTvmYX4ahAMODGwJQBeZpEs5eIqA7WnflDkh1V&UrlType=VDN&UrlDocumentType=PDF"   }]}}}';
            String sInteractionKeyEmail = '22704c25b5436bffa067552612071e2c';
            String sChannelCodeEmail = 'EMAIL';
            Integer iSkipNmbrEmail = 0;
            Integer iRecordLimitEmail = 200;            
            Test.StartTest();            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts'); 
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);            
            ApexPages.currentPage().getParameters().put('sChannelCode','EMAIL');
            ApexPages.currentPage().getParameters().put('CategoryDesc','EMAIL');            
            RTIPrintSummary_S_HUM oRTISummaryEmail = New RTIPrintSummary_S_HUM();
            oRTISummaryEmail.buildRequest(sInteractionKeyEmail,sChannelCodeEmail,iSkipNmbrEmail,iRecordLimitEmail);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryListEmail = New List<RTIPrintSummary_DTO_HUM>();
            oRTISummaryListEmail = oRTISummaryEmail.processResponse(ResponseEmail);
            HttpRequest oRequestEmail = New HttpRequest();
            oRTISummaryEmail.setupRequest(oRequestEmail,oServiceCallout,sInteractionKeyEmail,sChannelCodeEmail,iSkipNmbrEmail,iRecordLimitEmail);
            Test.stopTest();        
        }     
    }
    
    /*
    * Method Name : runPositiveDataEm
    * Description : Used to run positive scenarios for channel code Em
    * Return type : void
    * Paramater   : NA
    */    
    Static testMethod void runPositiveDataEm(){
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        { 
            String ResponseEm ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "eMs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF"   }]}}}';
            String sInteractionKeyEm = '3df0b3fc685e8220d96b0fe47a974ffa';
            String sChannelCodeEm = 'EM';
            Integer iSkipNmbrEm = 0;
            Integer iRecordLimitEm = 200;            
            Test.StartTest();            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts'); 
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);            
            ApexPages.currentPage().getParameters().put('sChannelCode','EM');
            ApexPages.currentPage().getParameters().put('CategoryDesc','EM');
            RTIPrintSummary_S_HUM oRTISummaryEm = New RTIPrintSummary_S_HUM();
            oRTISummaryEm.buildRequest(sInteractionKeyEm,sChannelCodeEm,iSkipNmbrEm,iRecordLimitEm);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryListEm = New List<RTIPrintSummary_DTO_HUM>();
            oRTISummaryListEm = oRTISummaryEm.processResponse(ResponseEm);
            HttpRequest oRequestEm = New HttpRequest();
            oRTISummaryEm.setupRequest(oRequestEm,oServiceCallout,sInteractionKeyEm,sChannelCodeEm,iSkipNmbrEm,iRecordLimitEm);            
            Test.stopTest();        
        }
        
    } 
    
   
     /*
    * Method Name : runPositiveDataVat
    * Description : Used to run positive scenarios for channel code VAT
    * Return type : void
    * Paramater   : NA
    */
    Static testMethod void runPositiveDataVat(){
          
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        {           
            String ResponseVat ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "vaTs": [{"documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=GAdfqBjJA062EBb6Idl3qdwdHhyJGSI7/z7GyINHcLPEyd3PpXXayJJdA7qw2WIDDZUTwTzZZA9m23AYeJlDqD1pMMwTvmYX4ahAMODGwJQBeZpEs5eIqA7WnflDkh1V&UrlType=VDN&UrlDocumentType=PDF"}]}}}';            
            String sInteractionKeyVat = '22704c25b5436bffa067552612071e2c';
            String sChannelCodeVat = 'VAT';
            Integer iSkipNmbrVat = 0;
            Integer iRecordLimitVat = 200;            
            Test.StartTest();            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts'); 
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);            
            ApexPages.currentPage().getParameters().put('sChannelCode','VAT');
            ApexPages.currentPage().getParameters().put('CategoryDesc','VAT');            
            RTIPrintSummary_S_HUM oRTISummaryVat = New RTIPrintSummary_S_HUM();
            oRTISummaryVat.buildRequest(sInteractionKeyVat,sChannelCodeVat,iSkipNmbrVat,iRecordLimitVat);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryListVat = New List<RTIPrintSummary_DTO_HUM>();
            oRTISummaryListVat = oRTISummaryVat.processResponse(ResponseVat);            
            ApexPages.currentPage().getParameters().put('sChannelCodeVat','VAT');
            System.assertEquals(ApexPages.currentPage().getParameters().get('sChannelCodeVat'),'VAT');                        
            HttpRequest oRequestVat = New HttpRequest();
            oRTISummaryVat.setupRequest(oRequestVat,oServiceCallout,sInteractionKeyVat,sChannelCodeVat,iSkipNmbrVat,iRecordLimitVat);           
            Test.stopTest();        
        }

    } 
    
     /*
    * Method Name : runPositiveDatatext
    * Description : Used to run positive scenarios for channel code Text
    * Return type : void
    * Paramater   : NA
    */
    Static testMethod void runPositiveDatatext(){         
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');  
        system.runAs (oCCSUser) 
        {             
            String ResponseText ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "smSs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF"   }]}}}';
            String sInteractionKeyText = '3df0b3fc685e8220d96b0fe47a974ffa';
            String sChannelCodeText = 'Text';
            Integer iSkipNmbrText = 0;
            Integer iRecordLimitText = 200;            
            Test.StartTest();            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractionDetails_V2' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts'); 
            Test.setCurrentPage(Page.RTIPrintDetails_VF_HUM);            
            ApexPages.currentPage().getParameters().put('sChannelCode','Text');
            ApexPages.currentPage().getParameters().put('CategoryDesc','Text');
            RTIPrintSummary_S_HUM oRTISummary = New RTIPrintSummary_S_HUM();         
            RTIPrintSummary_S_HUM oRTISummaryText = New RTIPrintSummary_S_HUM();
            oRTISummaryText.buildRequest(sInteractionKeytext,sChannelCodeText,iSkipNmbrText,iRecordLimitText);
            List<RTIPrintSummary_DTO_HUM> oRTISummaryListtext = New List<RTIPrintSummary_DTO_HUM>();
            oRTISummaryListText = oRTISummaryText.processResponse(ResponseText);
            ApexPages.currentPage().getParameters().put('sChannelCodeText','Text');
            System.assertEquals(ApexPages.currentPage().getParameters().get('sChannelCodeText'),'Text');           
            HttpRequest oRequestText = New HttpRequest();
            oRTISummaryText.setupRequest(oRequestText,oServiceCallout,sInteractionKeytext,sChannelCodeText,iSkipNmbrText,iRecordLimitText);            
            Test.stopTest();        
        }    
    }
}