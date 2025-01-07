/**************************************************************************************************************************************************
Apex class Name    :  RTISummaryService_T_HUM
Version            :  1.0
Created Date       :  18th January 2017
Function           :  This is the test class for RTISummary_S_HUM

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Pallavi Ravishankar          61268                 01/04/2016          Original Version
 *  Kritika Popat                86349                 07/04/2016          Phase 2 Service changes
 *  Kritika Popat                86349                 10/04/2016          Pagination    
 *  Kritika Popat                                      24/04/2016          IVR and Print 2nd level
 *  Shivam Anand                 180694                08/02/2018          REQ -350840,350841,350843-Display End Date/Time for IV WB, MB and MA
 *  Muralidhar Kollu                                   20/07/2020          RTI- 836091 View member communications for those members with security profile
 *  Prasanthi Kandula                                  05/12/2021          US - 2082407 Increased code coverage for new channels
************************************************************************************************************************************************* */
@isTest
private Class RTISummaryService_T_HUM
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
            HUM_Webservice_Callout__c oHUMRTISummaryService = new HUM_Webservice_Callout__c();
            oHUMRTISummaryService.name='RTIService_GetInteractions';
            oHUMRTISummaryService.Active__c=true;
            oHUMRTISummaryService.Certificate_Name__c='testts';
            oHUMRTISummaryService.End_Point_URL__c='https://testst-cmservicews.hum.com/';
            oHUMRTISummaryService.Timeout__c='120000';
            oHUMRTISummaryService.Content_Type__c='application/json';
            oHUMRTISummaryService.Request_Type__c='POST';
            insert oHUMRTISummaryService;
            system.assert(oHUMRTISummaryService.id!=null);
            system.assert(oHUMRTISummaryService.name == 'RTIService_GetInteractions');
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='RTIPagination',StringValue__c='50',IntegerValue__c = 50));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 1);
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1023880950';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '1023880950';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'1023880950');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Document objDoc = new Document();
            objDoc.Name = 'RTI VAT Card Icon';
            objDoc.FolderId = [select id, name from folder where name = 'Image Folder'].Id; 
            insert objDoc;
            system.assertEquals(objDoc.Name,'RTI VAT Card Icon');                        
        }
    }   
    
     /*
     * Method Name : runwebOutboundChannel
     * Description : Used to run scenerios for Outbound Web channel code
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runwebOutboundChannel()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','All',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),true);
            
            String responseWeb ='{"InteractionSummaryResponse":{"InteractionSummaryResult":{"NbRecords":1,"Interactions":[{"Interactionkey":"150228a0358b8cc7d7929080adfc8395","Interactionsrckey": "15038786","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME", "ChannelCode": "WEB","ChannelDescription": "Web","DirectionCode": "O","ActionTime": "2021-04-04T20:05:13Z","WEB":{"CategoryCode": "LETTER","CategoryDescription": "Letter","interactionWithDesc": "Member","interactingWithCode": "M","MessageDefinitionDescription": "Action Required","BusineesSubArea": "Individual/TRR","StartDateTime": "2021-04-04T20:05:13Z","EndDateTime": "2021-04-04T20:05:13Z"}}]}}}';
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(responseWeb,true); 
            System.assertEquals(oRTISummaryList[0].sChannelCode, 'WEB');
            
            String responseWB ='{"InteractionSummaryResponse":{"InteractionSummaryResult":{"NbRecords":1,"Interactions":[{"Interactionkey":"150228a0358b8cc7d7929080adfc8395","Interactionsrckey": "15038786","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME", "ChannelCode": "WB","ChannelDescription": "Web","DirectionCode": "O","ActionTime": "2021-04-04T20:05:13Z","WB":{"CategoryCode": "LETTER","CategoryDescription": "Letter","interactionWithDesc": "Member","interactingWithCode": "M","MessageDefinitionDescription": "Action Required","BusineesSubArea": "Individual/TRR","StartDateTime": "2021-04-04T20:05:13Z","EndDateTime": "2021-04-04T20:05:13Z"}}]}}}';
            List<RTISummary_DTO_HUM > oRTISummaryListResponse = oRTISummary.processResponse(responseWB,true); 
            System.assertEquals(oRTISummaryListResponse[0].sChannelCode, 'WB');
            Test.stopTest();  
        }       
    } 
    
    /*
     * Method Name : runWebInboundChannel
     * Description : Used to run scenerios for Inbound Web channel code
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void runWebInboundChannel()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','All',0, 'All', 'All','I',mapRTIattributes.get('Channel').getAttributeValues('I'),true);
            
            String response ='{"InteractionSummaryResponse":{"InteractionSummaryResult":{"NbRecords": 1,"Interactions":[{"Interactionkey":"c2acea5e3e24ea6fb7e39230befe1680",      "Interactionsrckey": "16006137","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "WB","ChannelDescription": "WEB",      "DirectionCode": "I","ActionTime": "2021-04-11T20:05:13Z","WB":{"CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","interactionWithDesc": "MEMBER","interactingWithCode": "M","MessageDefinitionDescription": "Standard EOB","BusineesSubArea": "MEDICARE AND COMMERCIAL","StartDateTime": "2021-04-11T20:05:13Z","EndDateTime": "2021-04-11T20:05:13Z"}}]}}}';
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sChannelCode, 'WB');
            Test.stopTest();  
        }       
    } 
    
     /*
     * Method Name : runWebOlderVersion
     * Description : Used to run scenerios for older version of Web channel code
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void runWebOlderVersion()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        system.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','All',0, 'All', 'All','I',mapRTIattributes.get('Channel').getAttributeValues('I'),false);
            
            String response ='{"InteractionSummaryResponse": {"InteractionSummaryResult":{"Interactions":[{"Interactionkey":'+
            '"d5b40952-1198-4e0a-874e-8dba991ac3c5","Interactionsrckey":"2166254365424839006","SourceDescription":"COREMETRICS",' +
            '"SourceCode":"CRMTRC","ChannelCode":"WB","ChannelDescription": "WEB BROWSER","DirectionCode": "I","ActionTime": '+
            '"2016-11-13T05:00:00Z","WB": {"interactionWith": "MEMBER"}}]}}}';
            
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sSourceDescription, 'COREMETRICS');
            Test.stopTest();  
        }       
    } 
    
      /*
     * Method Name : runIVRData
     * Description : Used to run scenerios of IVR channel
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runIVRData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','IVR',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),false);
            
            String response ='{"InteractionSummaryResponse":{"InteractionSummaryResult":{"Interactions":[{"Interactionkey": '+
            '"d03cd3b4-4387-474f-9a82-89fd0cfb4a4a","Interactionsrckey":"11141600012223100138XP8886667730|723413020|GB",'+
            '"SourceDescription": "Interactive Voice Response","SourceCode":"IVR","ChannelCode":"IV","ChannelDescription":"IVR",'+
            '"DirectionCode": "I","ActionTime": "2016-11-14T05:00:00Z","IV":      {"CategoryCode": "","CategoryDescription": "",'+
            '"StartDateTime": "2016-11-14T05:22:31Z","EndDateTime": "2016-11-14T05:27:13Z","StatusDescription":'+
            '"OPT OUT NO SELFSERVICE","StatusCode":"IVRX","LastModuleCD":"IVRMAINMEN","LastModuleName": "IVR -MainMenu",'+
            '"interactingWithCode":"M","interactingWithDesc": "MEMBER"}}]}}}';
            
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sSourceDescription, 'Interactive Voice Response');
            Test.stopTest();  
        }       
    } 
    
     /*
     * Method Name : runMBData
     * Description : Used to run scenerios of MB channel
     * Return type : void
     * Paramater   : NA
     */ 
     static testMethod void runMBData()
     {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
         { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','MB',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),true);
            
            String response ='{ "InteractionSummaryResponse":{ "InteractionSummaryResult":{ "NbRecords":1, "Interactions":[ { "Interactionkey":"55f0f458baa87167bff8628c423b566e", "Interactionsrckey":"3MI36722061911610639523", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"MB", "ChannelDescription":"MOBILE BROWSER", "DirectionCode":"I", "ActionTime":"2017-11-11T05:00:00Z", "MB":{ "interactionWithDesc":"MEMBER", "interactingWithCode":"M", "StartDateTime":"2017-11-12T00:54:48Z", "EndDateTime":"2017-11-12T00:54:49Z" } } ] } } }';
            
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sSourceDescription, 'COREMETRICS');
            Test.stopTest();  
        }       
    } 
    
    /*
     * Method Name : runMAData
     * Description : Used to run scenerios of MA channel
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runMAData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','MA',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),true);
            
            String response ='{"InteractionSummaryResponse":{ "InteractionSummaryResult":{ "NbRecords":5, "Interactions":[ { "Interactionkey":"696a81992145140444e1c4e0d9b00c09", "Interactionsrckey":"3MI36722061911610639523", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"MA", "ChannelDescription":"MOBILE APP", "DirectionCode":"I", "ActionTime":"2017-11-11T05:00:00Z", "MA":{ "interactionWithDesc":"MEMBER", "interactingWithCode":"M", "StartDateTime":"2017-11-12T00:54:48Z", "EndDateTime":"2017-11-12T00:54:49Z" } }, { "Interactionkey":"2b9ce749f30fac626f81135a6940c6ac", "Interactionsrckey":"3MI36722061911610639523", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"MA", "ChannelDescription":"MOBILE APP", "DirectionCode":"I", "ActionTime":"2017-11-11T05:00:00Z", "MA":{ "interactionWithDesc":"MEMBER", "interactingWithCode":"M", "StartDateTime":"2017-11-12T00:54:48Z", "EndDateTime":"2017-11-12T00:54:49Z" } }, { "Interactionkey":"bcdf16a4be1e31d8c152c351905d21e8", "Interactionsrckey":"3MI36722061911610639523", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"MA", "ChannelDescription":"MOBILE APP", "DirectionCode":"I", "ActionTime":"2017-11-11T05:00:00Z", "MA":{ "interactionWithDesc":"MEMBER", "interactingWithCode":"M", "StartDateTime":"2017-11-12T00:54:48Z", "EndDateTime":"2017-11-12T00:54:49Z" } }, { "Interactionkey":"3478bd09e00f6af3eacecc3d18ae2a44", "Interactionsrckey":"3MI36722061911610639523", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"MA", "ChannelDescription":"MOBILE APP", "DirectionCode":"I", "ActionTime":"2017-11-11T05:00:00Z", "MA":{ "interactionWithDesc":"MEMBER", "interactingWithCode":"M", "StartDateTime":"2017-11-12T00:54:48Z", "EndDateTime":"2017-11-12T00:54:49Z" } }, { "Interactionkey":"275484d6eff8491e13d26ddad95d6951", "Interactionsrckey":"3MI36722061911610639523", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"MA", "ChannelDescription":"MOBILE APP", "DirectionCode":"I", "ActionTime":"2017-11-11T05:00:00Z", "MA":{ "interactionWithDesc":"MEMBER", "interactingWithCode":"M", "StartDateTime":"2017-11-12T00:54:48Z", "EndDateTime":"2017-11-12T00:54:49Z" } } ] } } }';
            
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sSourceDescription, 'COREMETRICS');
            Test.stopTest();  
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
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            RTISummary_S_HUM oSum = new RTISummary_S_HUM();
            System.assert(oSum.lstLogErrors.size() == 0);
            List<Error_Log__c> lstLog = new List<Error_Log__c>();
            Error_Log__c error = new Error_Log__c(Status__c = 'testStatus');
            lstLog.add(error);
            oSum.lstLogErrors = lstLog;
            System.assert(oSum.lstLogErrors.size() > 0);
            oSum.totalRec(); 
            Test.stopTest();  
        }       
    }        
   
   
   /*
     * Method Name : runDMData
     * Description : Used to run scenerios of DM channel
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runPrintData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','PRINT',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),true);
            String response ='{ "InteractionSummaryResponse":{ "InteractionSummaryResult":{ "NbRecords":1, "Interactions":[ { "Interactionkey":"8f1cd2048cd711dc156d21cfeeaf8e84", "Interactionsrckey":"EMME0021510721079458161228210721", "SourceDescription":"Enterprise Measurable Messaging Ecosystem", "SourceCode":"EMME", "ChannelCode":"PRINT", "ChannelDescription":"DIRECT MAIL", "DirectionCode":"O", "ActionTime":"2017-11-11T05:00:00Z", "PRINT":{ "CategoryCode":"SCR-OTHER", "CategoryDescription":"Other", "MessageDefinitionIDCD":"SCR-EM-1067", "MessageDefinitionDescription":"SBC notification - On Exchange LETTER ONLY", "StartDateTime":"2017-11-11T22:33:00Z", "BusinessArea":"GOVERNMENT", "BusineesSubArea":"", "StatusDescription":"Delivered", "StatusCode":"SCR-EM-D", "EndDateTime":"2017-11-12T04:59:00Z", "Interactionkey":"8f1cd2048cd711dc156d21cfeeaf8e84", "DepartmentName":"Thunderhead Print", "IsFinal":true } } ] } } }';
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sSourceDescription, 'Enterprise Measurable Messaging Ecosystem');
            Test.stopTest();  
        }       
    } 
             
    /*
     * Method Name : runVATData
     * Description : Used to run scenerios of VAT channel
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runVATData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','VAT',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),true);
            
            String response ='{"InteractionSummaryResponse": { "InteractionSummaryResult": { "NbRecords":1,"Interactions":[ { "Interactionkey":"92b8870f723dce152b968bdd2e4ca9b0", "Interactionsrckey": "65802153",                       "SourceDescription":"Enterprise Measurable Messaging Ecosystem", "SourceCode":"EMME", "ChannelCode": "VAT",  "ChannelDescription": "VAT",        "DirectionCode": "O","ActionTime": "2020-07-07T20:05:13Z","VAT":  { "MessageDefinitionIDCD": "EL0069_WALMARTPDP",             "CampaignName": "Outbound call to ensure Members received their ANOC and noted plan changes",             "PhoneNumber": "8595592218",             "StartDateTime": "2020-07-07T20:05:13Z",             "EndDateTime": "2020-07-07T20:05:13Z",             "StatusCode": "I",             "StatusDescription": "In Progress",             "SubStatusCode": "52",             "SubStatusDesc": "NO_ANSWER",             "TrackingID": "65802153"          }       }] }}}';
            
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sSourceDescription, 'Enterprise Measurable Messaging Ecosystem');
            Test.stopTest();  
        }       
    }    
    
     /*
     * Method Name : runEmailChannelCode
     * Description : Used to run scenerios of Email channel code
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runEmailChannelCode()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        system.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','Email',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),true);
            
            String response ='{"InteractionSummaryResponse":{"InteractionSummaryResult":{"NbRecords": 1,"Interactions":[{"Interactionkey": "135886e5bfcb6e73f2b65f5c1db5cf59",    "Interactionsrckey": "69344929","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME", "ChannelCode": "EMAIL",   "ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2021-05-01T20:05:13Z","EMAIL":{"MessageDefinitionDescription": "Thank you email for Grievance and Appeal submission","BusineesSubArea": "Provider Communications","StartDateTime": "2021-05-01T20:05:13Z","EndDateTime": "2021-05-01T20:05:13Z", "StatusDescription": "Delivered","RecipientEmail": "dshanmugam@humana.com" }}]}}}';
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sChannelCode, 'EMAIL');
            
            String responseEM ='{"InteractionSummaryResponse":{"InteractionSummaryResult":{"NbRecords": 1,"Interactions":[{"Interactionkey": "135886e5bfcb6e73f2b65f5c1db5cf59",    "Interactionsrckey": "69344929","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME", "ChannelCode": "EM",   "ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2021-05-01T20:05:13Z","EM":{"MessageDefinitionDescription": "Thank you email for Grievance and Appeal submission","BusineesSubArea": "Provider Communications","StartDateTime": "2021-05-01T20:05:13Z","EndDateTime": "2021-05-01T20:05:13Z", "StatusDescription": "Delivered","RecipientEmail": "dshanmugam@humana.com" }}]}}}';
            List<RTISummary_DTO_HUM > oRTISummaryListEM = oRTISummary.processResponse(responseEM,true); 
            System.assertEquals(oRTISummaryListEM[0].sChannelCode, 'EM');
            Test.stopTest();  
        }       
    } 
    
    /*
     * Method Name : runSMSChannelCode
     * Description : Used to run scenerios of SMS Channel Code
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runSMSChannelCode()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self'); 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='RTIService_GetInteractions' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            RTISummary_S_HUM oRTISummary = new  RTISummary_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
            oRequest = oRTISummary.setupRequest(oRequest, oServiceCallout, oAccount.Enterprise_ID__c,'','','SMS',0, 'All', 'All','O',mapRTIattributes.get('Channel').getAttributeValues('O'),true);
            
            String response ='{"InteractionSummaryResponse":{"InteractionSummaryResult":{"NbRecords":1,"Interactions":[{"Interactionkey":"4388cf40bfa4f8aa9084fd8dbab4a5f6","Interactionsrckey": "65793766","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2021-04-11T07:51:37.658Z","SMS":{"MessageDefinitionDescription": "SL0037_COB_MEMBER_RESULTS","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2021-04-11T07:51:37.658Z","EndDateTime": "2021-04-11T07:52:00Z","StatusDescription": "Delivered",   "RecipientPhone": "5026457274","TrackingID": "65793766"}}]}}}';
            List<RTISummary_DTO_HUM > oRTISummaryList = oRTISummary.processResponse(response,true); 
            System.assertEquals(oRTISummaryList[0].sChannelCode, 'SMS');
            Test.stopTest();  
        }       
    }  
}