/**************************************************************************** 
Apex Class Name  : RTIInteraction_LT_HUM
Version          : 1.0  
Created Date     : 07/12/2022
Function         : Test class for RTIInteraction_LC_HUM And RTISummary_LS_HUM
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane 							  7/12/2022				     US-3298002 RTI Implementation
* Dimple Sharma									  10/23/2023				 US- 5232125 Code coverage Improvements
***************************************************************************************************/
@isTest
public with sharing class RTIInteraction_LT_HUM {
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
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c oHUMRTISummaryService = new HUM_Webservice_Callout__c();
            oHUMRTISummaryService.name='RTIService_GetInteractions_V2';
            oHUMRTISummaryService.Active__c=true;
            oHUMRTISummaryService.Certificate_Name__c='testts';
            oHUMRTISummaryService.End_Point_URL__c='https://testst-cmservicews.hum.com/';
            oHUMRTISummaryService.Timeout__c='120000';
            oHUMRTISummaryService.Content_Type__c='application/json';
            oHUMRTISummaryService.Request_Type__c='POST';
            insert oHUMRTISummaryService;
            system.assert(oHUMRTISummaryService.id!=null);
            system.assert(oHUMRTISummaryService.name == 'RTIService_GetInteractions_V2');
            
            HUM_Webservice_Callout__c oHUMRTISummaryService1 = new HUM_Webservice_Callout__c();
            oHUMRTISummaryService1.name='RTIService_GetInteractionDetails_V2';
            oHUMRTISummaryService1.Active__c=true;
            oHUMRTISummaryService1.Certificate_Name__c='testts';
            oHUMRTISummaryService1.End_Point_URL__c='https://testst-cmservicews.hum.com/';
            oHUMRTISummaryService1.Timeout__c='120000';
            oHUMRTISummaryService1.Content_Type__c='application/json';
            oHUMRTISummaryService1.Request_Type__c='POST';
            insert oHUMRTISummaryService1;
            system.assert(oHUMRTISummaryService1.id!=null);
            system.assert(oHUMRTISummaryService1.name == 'RTIService_GetInteractionDetails_V2');
            
            HUM_Webservice_Callout__c oHUMRTISummaryService2 = new HUM_Webservice_Callout__c();
            oHUMRTISummaryService2.name='Test';
            insert oHUMRTISummaryService2;
            
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
           
            MemberPlan objMemberPlan = New MemberPlan();
            objMemberPlan.Member = objAcc;
            objMemberPlan.Policy__c = objpol.Id;
            objMemberPlan.EffectiveTo = Date.today().addDays(1);
            objMemberPlan.EffectiveFrom = Date.today().addDays(-1);
            objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|6925004434817|12345678|34567|2013-07-01';
            objMemberPlan.name = 'testpolmem11';
            objMemberPlan.MemberId = objAcc.id;
            insert objMemberPlan;
             System.assert(objMemberPlan.Member_Plan_External_Id__c == '00|ODS|6925004434817|12345678|34567|2013-07-01');
             Document document;

            document = new Document();
            document.Body = Blob.valueOf('Some Text');
            document.ContentType = 'application/pdf';
            document.DeveloperName = 'my_document';
            document.IsPublic = true;
            document.Name = 'RTI VAT Card Icon';
            document.FolderId = [select id from folder where name = 'MemberIcons'].id;
            insert document;
                    
             system.assertEquals(document.Name , 'RTI VAT Card Icon');
            
            HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='HOME_OFFICE_ALL';
            objhumc.StringValue__c ='Home Office All';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
            
            HUMConstants__c  objhumc1 = New HUMConstants__c();
            objhumc1.name ='HOME_OFFICE_MEDICAL';
            objhumc1.StringValue__c ='Home Office Medical';
            insert objhumc1 ;
            system.assertEquals(objhumc1.id != null, True);
            
            HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='HOME_OFFICE_DENTAL';
            objhumc2.StringValue__c ='Home Office Dental';
            insert objhumc2 ;
            system.assertEquals(objhumc2.id != null, True);
        }
    }   
    
    /*
     * Method Name : testSearch
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    @isTest static void testSearch()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Enterprise_ID__c, '1023880950');
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           RTIInteraction_LC_HUM.sEnterpriseId = '1023880950';
           RTIInteraction_LC_HUM.sSelectedChannel = 'IVR';
           RTISummary_LS_HUM.sSelectedChannel = 'IVR';
           RTISummary_LS_HUM.bIsPageOnLoad = true;
           List<Error_Log__c> lstLogErrors = new RTISummary_LS_HUM().lstLogErrors;
           
           RTIInteraction_LC_HUM.sCategoryValue= 'All';
           RTIInteraction_LC_HUM.sStartDate = '';
           RTIInteraction_LC_HUM.sEndDate = '';
           RTIInteraction_LC_HUM.search('1023880950', '',  '' , 'All', '', 'O',0);
           RTIInteraction_LC_HUM.sInteractionWithType ='';
        
           Continuation objCon = New Continuation(120);
           objCon.continuationMethod='processResponse';
          
           
           HttpResponse oResponse = New HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
            
            
           
           /*String sJson ='{"InteractionSummaryResponse": {"InteractionSummaryResult":{"Interactions":[{"Interactionkey":'+
           '"d5b40952-1198-4e0a-874e-8dba991ac3c5","Interactionsrckey":"2166254365424839006","SourceDescription":"COREMETRICS",' +
           '"SourceCode":"CRMTRC","ChannelCode":"WB","ChannelDescription": "WEB BROWSER","DirectionCode": "I","ActionTime": '+
           '"2016-11-13T05:00:00Z","WB": {"interactionWith": "MEMBER"}}]}}}';
           
            sJson = '{ "InteractionSummaryResponse":{ "InteractionSummaryResult":{ "NbRecords":14, "Interactio'+
            'ns":[ { "Interactionkey":"72a17eaf90864c0b7d8f4762deadb4b4", "Interactionsrckey":"60828775197399608'+
            '45", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"WB", "ChannelDescrip'+
            'tion":"WEB", "DirectionCode":"I", "ActionTime":"2017-02-14T05:00:00Z", "WB":{ "interactionWithDesc":"MEM'+
            'BER", "interactingWithCode":"M" } },  { "Interactionkey":"5242091056e94921cdb2a3f17eb9bad4", "Interact'+
            'ionsrckey":"424561239", "SourceDescription":"Enterprise Measurable Messaging Ecosystem", "SourceCode":"E'+
            'MME", "ChannelCode":"DM", "ChannelDescription":"DIRECT MAIL", "DirectionCode":"O", "ActionTim'+
            'e":"2017-02-09T05:00:00Z", "DM":{ "CategoryCode":"SCR-0", "CategoryDescription":"Unknown or Not App'+
            'licable", "StartDateTime":"2017-02-09T05:00:00Z", "BusinessArea":"UNKNOWN", "BusineesSubArea":"", "Status'+
            'Description":"ThunderheadMetered", "StatusCode":"SCR-EM-5001", "EndDateTime":"2017-02-14T04:59:59Z", "Inte'+
            'ractionkey":"5242091056e94921cdb2a3f17eb9bad4", "DepartmentName":"Thunderhead Print", "IsFinal":true } } ] } } }';
            
            sJson = '{ "InteractionSummaryResponse":{ "InteractionSummaryResult":{ "NbRecords":14, "Interactio'+
            'ns":[ { "Interactionkey":"72a17eaf90864c0b7d8f4762deadb4b4", "Interactionsrckey":"60828775197399608'+
            '45", "SourceDescription":"COREMETRICS", "SourceCode":"CRMTRC", "ChannelCode":"WB", "ChannelDescrip'+
            'tion":"WEB", "DirectionCode":"I", "ActionTime":"2017-02-14T05:00:00Z", "WB":{ "interactionWithDesc":"MEM'+
            'BER", "interactingWithCode":"M" } },{"Interactionkey": '+
            '"d03cd3b4-4387-474f-9a82-89fd0cfb4a4a","Interactionsrckey":"11141600012223100138XP8886667730|723413020|GB",'+
            '"SourceDescription": "Interactive Voice Response","SourceCode":"IVR","channelCode":"IV","ChannelDescription":"IVR",'+
            '"DirectionCode": "I","ActionTime": "2016-11-14T05:00:00Z","IV":      {"CategoryCode": "","CategoryDescription": "",'+
            '"StartDateTime": "2016-11-14T05:22:31Z","EndDateTime": "2016-11-14T05:27:13Z","StatusDescription":'+
            '"OPT OUT NO SELFSERVICE","StatusCode":"IVRX","LastModuleCD":"IVRMAINMEN","LastModuleName": "IVR -MainMenu",'+
            '"interactingWithCode":"M","interactingWithDesc": "MEMBER"}},  { "Interactionkey":"5242091056e94921cdb2a3f17eb9bad4", "Interact'+
            'ionsrckey":"424561239", "SourceDescription":"Enterprise Measurable Messaging Ecosystem", "SourceCode":"E'+
            'MME", "ChannelCode":"DM", "ChannelDescription":"DIRECT MAIL", "DirectionCode":"O", "ActionTim'+
            'e":"2017-02-09T05:00:00Z", "DM":{ "CategoryCode":"SCR-0", "CategoryDescription":"Unknown or Not App'+
            'licable", "StartDateTime":"2017-02-09T05:00:00Z", "BusinessArea":"UNKNOWN", "BusineesSubArea":"", "Status'+
            'Description":"ThunderheadMetered", "StatusCode":"SCR-EM-5001", "EndDateTime":"2017-02-14T04:59:59Z", "Inte'+
            'ractionkey":"5242091056e94921cdb2a3f17eb9bad4", "DepartmentName":"Thunderhead Print", "IsFinal":true } },' +
            '{ "Interactionkey": "2c68caff25a11502defe74a3db98e70c", "Interactionsrckey": "55835020", "SourceDescripti' +
            'on": " Enterprise Measurable Messaging Ecosystem", "SourceCode": "EMME", "ChannelCode": "SMS", "ChannelDe' +
            'scription": "SMS", "DirectionCode": "O", "ActionTime": "2021-05-01T20:05:13Z", "SMS":{ "MessageDefinition' +
            'Description": "Set up to test SMS", "BusineesSubArea": "Medicare and Commercial", "StartDateTime": "2021-' +
            '05-01T20:05:13Z", "EndDateTime": "2021-05-01T20:05:13Z", "StatusDescription": "Delivered", "RecipientPhon' +
            'e": "7202090013", "TrackingID": "55835020"}}]} } }';*/
            
            String sJson = '{"InteractionSummaryResponse":{"InteractionSummaryResult":{"NbRecords":27,"Interactions":[{"Interactionkey":"d0f03df35e598cff73760ba2dfca41dc","Interactionsrckey":"2796698140","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"VAT","ChannelDescription":"VAT","DirectionCode":"O","ActionTime":"2023-05-23T05:04:10Z","VAT":{"MessageDefinitionIDCD":"SL0176_RXELEAD_WT_FS_NPRC","CampaignName":"WT FENCE SITTER-JC OFFSHORE","PhoneNumber":"","StartDateTime":"2023-05-23T05:04:10Z","EndDateTime":"2023-05-26T15:42:46Z","StatusCode":"D","StatusDescription":"Delivered","SubStatusCode":"72","SubStatusDesc":"AUTHENTICATED","TrackingID":"2796698140"}},{"Interactionkey":"bb9eb6218a97243406713a1501ece899","Interactionsrckey":"2795551881","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"SMS","ChannelDescription":"SMS","DirectionCode":"O","ActionTime":"2023-05-22T20:46:04Z","SMS":{"MessageDefinitionIDCD":"SM0169_HNC_AMS","MessageDefinitionDescription":"Promote a variety of in-person and online classes via SMS relevant to Aging, MH, & Sleep Disorder","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-05-22T20:46:04Z","EndDateTime":"2023-05-24T16:39:35Z","StatusDescription":"Delivered","RecipientPhone":"3652149870","TrackingID":"2795551881"}},{"Interactionkey":"5189bc6e981917bd22adfc3b0d7a6db6","Interactionsrckey":"2794387854","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-05-22T15:59:30Z","EMAIL":{"MessageDefinitionIDCD":"SP4288_HNCAGINGMHSLEEP_JUNE","MessageDefinitionDescription":"We want to promote a variety of in-person and online classes relevant to Aging, MH, & Sleep Disorder June","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-05-22T15:59:30Z","EndDateTime":"2023-05-28T02:11:26Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2794387854"}},{"Interactionkey":"619ae0d17f5bfdb07505f3f9ac6cebca","Interactionsrckey":"2793337217","SourceDescription":"Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"VAT","ChannelDescription":"VAT","DirectionCode":"O","ActionTime":"2023-05-22T16:06:49Z","VAT":{"MessageDefinitionIDCD":"SL0146_OTC_Order_Shipped","CampaignName":"OTC order shipped","PhoneNumber":"5042050337","StartDateTime":"2023-05-22T09:15:27Z","EndDateTime":"2023-05-22T16:08:39Z","StatusCode":"D","StatusDescription":"Delivered","SubStatusCode":"72","SubStatusDesc":"AUTHENTICATED","TrackingID":"2793337217"}},{"Interactionkey":"e5400faae8fb9de484f24c08ddc19cd4","Interactionsrckey":"1612465648","SourceDescription":"EMCON HISTORY","SourceCode":"EMCON","ChannelCode":"PRINT","ChannelDescription":"PRINT","DirectionCode":"O","ActionTime":"2023-05-22T04:00:00Z","PRINT":{"CategoryCode":"EOB","CategoryDescription":"EXPLANATION OF BENEFITS","MessageDefinitionIDCD":"43","MessageDefinitionDescription":"Standard EOB","StartDateTime":"2023-05-22T04:00:00Z","BusinessArea":"MEDICARE AND COMMERCIAL","BusineesSubArea":"MEDICARE AND COMMERCIAL","StatusDescription":"PRINTED","StatusCode":"77","EndDateTime":"2023-05-22T04:00:00Z","Interactionkey":"e5400faae8fb9de484f24c08ddc19cd4","DepartmentName":"Dialogue-Print","IsFinal":true,"TrackingID":"1612465648","MessageFulfillmentId":"1612465648","SDRPersonId":"1092455787","MemberCardId":"H41844156","DependentCode":"00","PlatformCode":"LV","PersonGenKey":"9726004690122","Printable":true}},{"Interactionkey":"0296ab214bbb611bd9f2b4a2b6a91b77","Interactionsrckey":"2787517906","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-05-16T14:29:06Z","EMAIL":{"MessageDefinitionIDCD":"SP4253_COLORECTAL2023_W2","MessageDefinitionDescription":"Objective of this campaign is to educate members on the importance of getting their colorectal cancer screening","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-05-16T14:29:06Z","EndDateTime":"2023-05-18T14:18:51Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2787517906"}},{"Interactionkey":"8504623e4922676d7cf4ac799bc1b117","Interactionsrckey":"2770533462","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-05-04T08:03:28Z","EMAIL":{"MessageDefinitionIDCD":"SP2603_MYDIRECTIVES","MessageDefinitionDescription":"A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-05-04T08:03:28Z","EndDateTime":"2023-05-13T22:48:37Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2770533462"}},{"Interactionkey":"05d26d6d6a2e09f050dbf227939632d6","Interactionsrckey":"1593736243","SourceDescription":"EMCON HISTORY","SourceCode":"EMCON","ChannelCode":"PRINT","ChannelDescription":"PRINT","DirectionCode":"O","ActionTime":"2023-05-04T04:00:00Z","PRINT":{"CategoryCode":"SS","CategoryDescription":"SMART SUMMARY","MessageDefinitionIDCD":"47","MessageDefinitionDescription":"PARTCDMerge","StartDateTime":"2023-05-04T04:00:00Z","BusinessArea":"MEDICARE AND COMMERCIAL","BusineesSubArea":"MEDICARE AND COMMERCIAL","StatusDescription":"PRINTED","StatusCode":"77","EndDateTime":"2023-05-04T04:00:00Z","Interactionkey":"05d26d6d6a2e09f050dbf227939632d6","DepartmentName":"Dialogue-Print","IsFinal":true,"TrackingID":"1593736243","MessageFulfillmentId":"1593736243","SDRPersonId":"1092455787","MemberCardId":"H41844156","DependentCode":"00","PlatformCode":"LV","PersonGenKey":"9726004690122","Printable":true}},{"Interactionkey":"e999f9a35959dff3d17973c331a6e98c","Interactionsrckey":"2767445496","SourceDescription":"Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"VAT","ChannelDescription":"VAT","DirectionCode":"O","ActionTime":"2023-05-02T17:29:41Z","VAT":{"MessageDefinitionIDCD":"SL0034_REFILL_REMIND_MEMBER_RESULTS_A_C","CampaignName":"Auto refill reminder","PhoneNumber":"5042050337","StartDateTime":"2023-05-02T09:59:12Z","EndDateTime":"2023-05-02T23:33:26Z","StatusCode":"D","StatusDescription":"Delivered","SubStatusCode":"71","SubStatusDesc":"Answering Machine","TrackingID":"2767445496"}},{"Interactionkey":"b6cbd6f5bbf1ab03493325d02343af18","Interactionsrckey":"1587585330","SourceDescription":"EMCON HISTORY","SourceCode":"EMCON","ChannelCode":"WB","ChannelDescription":"WEB","DirectionCode":"O","ActionTime":"2023-05-02T04:00:00Z","WB":{"MessageDefinitionIDCD":"43","CategoryCode":"EOB","CategoryDescription":"EXPLANATION OF BENEFITS","interactionWithDesc":"MEMBER","interactingWithCode":"M","MessageDefinitionDescription":"Standard EOB","BusineesSubArea":"MEDICARE AND COMMERCIAL","StartDateTime":"2023-05-02T04:00:00Z","EndDateTime":"2023-05-02T04:00:00Z","TrackingID":"1587585330"}},{"Interactionkey":"45f216ba93a44fc086a65ff84f1427ff","Interactionsrckey":"2758712391","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-04-27T08:29:22Z","EMAIL":{"MessageDefinitionIDCD":"SP2603_MYDIRECTIVES","MessageDefinitionDescription":"A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-04-27T08:29:22Z","EndDateTime":"2023-05-02T14:04:52Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2758712391"}},{"Interactionkey":"a03295217e6091ee11e5569ee1789b4b","Interactionsrckey":"2748311467","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-04-20T08:11:35Z","EMAIL":{"MessageDefinitionIDCD":"SP2603_MYDIRECTIVES","MessageDefinitionDescription":"A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-04-20T08:11:35Z","EndDateTime":"2023-04-29T23:42:29Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2748311467"}},{"Interactionkey":"6d49ce6700e686cf9369d43111abec82","Interactionsrckey":"2747033604","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"VAT","ChannelDescription":"VAT","DirectionCode":"O","ActionTime":"2023-04-19T16:34:30Z","VAT":{"MessageDefinitionIDCD":"SL0176_RXELEAD_WT_FS_NPRC","CampaignName":"WT FENCE SITTER-JC OFFSHORE","PhoneNumber":"","StartDateTime":"2023-04-19T16:34:30Z","EndDateTime":"2023-05-08T22:12:34Z","StatusCode":"D","StatusDescription":"Delivered","SubStatusCode":"72","SubStatusDesc":"AUTHENTICATED","TrackingID":"2747033604"}},{"Interactionkey":"af8c75a335d3061a536cd87263a0186c","Interactionsrckey":"2738071187","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"PRINT","ChannelDescription":"Print","DirectionCode":"O","ActionTime":"2023-04-14T08:03:21Z","PRINT":{"CategoryCode":"OTHER","CategoryDescription":"Other","MessageDefinitionIDCD":"MP0363_HOP_FS","MessageDefinitionDescription":"HOP Traditional mailing FS","StartDateTime":"2023-04-14T08:03:21Z","BusinessArea":"Medicare and Commercial","BusineesSubArea":"Medicare and Commercial","StatusDescription":"Delivered","StatusCode":"D","EndDateTime":"2023-04-25T12:49:45Z","Interactionkey":"af8c75a335d3061a536cd87263a0186c","DepartmentName":"MPS","IsFinal":true,"TrackingID":"2738071187","MessageFulfillmentId":"2738071187","SDRPersonId":"1092455787","MemberCardId":"H41844156","DependentCode":"00","PlatformCode":"lv","PersonGenKey":"9726004690122","Printable":true}},{"Interactionkey":"2ec23e28cb80b2ba1fa46837b5554304","Interactionsrckey":"2705962789","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-03-21T16:10:11Z","EMAIL":{"MessageDefinitionIDCD":"SP4228_AUTOREFILLCAMPAIGN","MessageDefinitionDescription":"The auto refill campaign reaches out to current CenterWell Pharmacy customers with medications eligible for auto refill","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-03-21T16:10:11Z","EndDateTime":"2023-03-27T20:35:32Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2705962789"}},{"Interactionkey":"562d9e7c4b5c0c677b3d5ec5ed55ecd9","Interactionsrckey":"2698488246","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"SMS","ChannelDescription":"SMS","DirectionCode":"O","ActionTime":"2023-03-17T10:12:32Z","SMS":{"MessageDefinitionIDCD":"SM0039_MY_DIRECTIVE","MessageDefinitionDescription":"Text to communicate about the My Directive available for members to complete.","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-03-17T10:12:32Z","EndDateTime":"2023-03-20T15:25:41Z","StatusDescription":"Delivered","RecipientPhone":"","TrackingID":"2698488246"}},{"Interactionkey":"b85fb2573ef7555bdbc15b1ad95d983f","Interactionsrckey":"2692910478","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"VAT","ChannelDescription":"VAT","DirectionCode":"O","ActionTime":"2023-03-14T04:33:35Z","VAT":{"MessageDefinitionIDCD":"SL0176_RXELEAD_NPRC","CampaignName":"Humana Pharmacy Education","PhoneNumber":"","StartDateTime":"2023-03-14T04:33:35Z","EndDateTime":"2023-03-16T15:22:53Z","StatusCode":"D","StatusDescription":"Delivered","SubStatusCode":"72","SubStatusDesc":"AUTHENTICATED","TrackingID":"2692910478"}},{"Interactionkey":"b85fb2573ef7555bdbc15b1ad99d983f","Interactionsrckey":"2692910478","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EM","ChannelDescription":"EM","DirectionCode":"O","ActionTime":"2023-03-14T04:33:35Z","em":{"MessageDefinitionIDCD":"SL0176_RXELEAD_NPRC","CampaignName":"Humana Pharmacy Education","PhoneNumber":"","StartDateTime":"2023-03-14T04:33:35Z","EndDateTime":"2023-03-16T15:22:53Z","StatusCode":"D","StatusDescription":"Delivered","SubStatusCode":"72","SubStatusDesc":"AUTHENTICATED","TrackingID":"2692910478"}},{"Interactionkey":"fb9712f1f6387e0ffdcfac5275e41875","Interactionsrckey":"1534830278","SourceDescription":"EMCON HISTORY","SourceCode":"EMCON","ChannelCode":"WEB","ChannelDescription":"WEB","DirectionCode":"O","ActionTime":"2023-03-11T05:00:00Z","web":{"MessageDefinitionIDCD":"43","CategoryCode":"EOB","CategoryDescription":"EXPLANATION OF BENEFITS","interactionWithDesc":"MEMBER","interactingWithCode":"M","MessageDefinitionDescription":"Standard EOB","BusineesSubArea":"MEDICARE AND COMMERCIAL","StartDateTime":"2023-03-11T05:00:00Z","EndDateTime":"2023-03-11T05:00:00Z","TrackingID":"1534830278"}},{"Interactionkey":"7b1317bc492a6b0c2816c87601e6d60d","Interactionsrckey":"2689588382","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"PRINT","ChannelDescription":"Print","DirectionCode":"O","ActionTime":"2023-03-10T06:01:33Z","PRINT":{"CategoryCode":"OTHER","CategoryDescription":"Other","MessageDefinitionIDCD":"MP0363_HOP_FS","MessageDefinitionDescription":"HOP Traditional mailing FS","StartDateTime":"2023-03-10T06:01:33Z","BusinessArea":"Medicare and Commercial","BusineesSubArea":"Medicare and Commercial","StatusDescription":"Delivered","StatusCode":"D","EndDateTime":"2023-03-24T15:06:37Z","Interactionkey":"7b1317bc492a6b0c2816c87601e6d60d","DepartmentName":"MPS","IsFinal":true,"TrackingID":"2689588382","MessageFulfillmentId":"2689588382","SDRPersonId":"1092455787","MemberCardId":"H41844156","DependentCode":"00","PlatformCode":"lv","PersonGenKey":"9726004690122","Printable":true}},{"Interactionkey":"ca863e9a4d85a36d0840c27eef4d6885","Interactionsrckey":"2683687641","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-03-06T19:58:58Z","EMAIL":{"MessageDefinitionIDCD":"SP4179_COLORECTAL2023","MessageDefinitionDescription":"bjective of this campaign is to educate members on the importance of getting their colorectal cancer screening","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-03-06T19:58:58Z","EndDateTime":"2023-03-12T01:26:26Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2683687641"}},{"Interactionkey":"9607ee6cd31e4d2a569883fcb6839beb","Interactionsrckey":"2683447113","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"SMS","ChannelDescription":"SMS","DirectionCode":"O","ActionTime":"2023-03-06T19:50:28Z","SMS":{"MessageDefinitionIDCD":"SM0118_COLORECTAL_2023","MessageDefinitionDescription":"Text to educate importance of colorectal cancer screening","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-03-06T19:50:28Z","EndDateTime":"2023-03-23T13:59:56Z","StatusDescription":"Delivered","RecipientPhone":"","TrackingID":"2683447113"}},{"Interactionkey":"66419de8b568faec884aa56546dcb271","Interactionsrckey":"1528461319","SourceDescription":"EMCON HISTORY","SourceCode":"EMCON","ChannelCode":"PRINT","ChannelDescription":"PRINT","DirectionCode":"O","ActionTime":"2023-03-04T05:00:00Z","PRINT":{"CategoryCode":"SS","CategoryDescription":"SMART SUMMARY","MessageDefinitionIDCD":"47","MessageDefinitionDescription":"PARTCDMerge","StartDateTime":"2023-03-04T05:00:00Z","BusinessArea":"MEDICARE AND COMMERCIAL","BusineesSubArea":"MEDICARE AND COMMERCIAL","StatusDescription":"PRINTED","StatusCode":"77","EndDateTime":"2023-03-04T05:00:00Z","Interactionkey":"66419de8b568faec884aa56546dcb271","DepartmentName":"Dialogue-Print","IsFinal":true,"TrackingID":"1528461319","MessageFulfillmentId":"1528461319","SDRPersonId":"1092455787","MemberCardId":"H41844156","DependentCode":"00","PlatformCode":"LV","PersonGenKey":"9726004690122","Printable":true}},{"Interactionkey":"81366b5bf94d4568f89b5b385db4577b","Interactionsrckey":"1522087611","SourceDescription":"EMCON HISTORY","SourceCode":"EMCON","ChannelCode":"WB","ChannelDescription":"WEB","DirectionCode":"O","ActionTime":"2023-03-04T05:00:00Z","WEB":{"MessageDefinitionIDCD":"43","CategoryCode":"EOB","CategoryDescription":"EXPLANATION OF BENEFITS","interactionWithDesc":"MEMBER","interactingWithCode":"M","MessageDefinitionDescription":"Standard EOB","BusineesSubArea":"MEDICARE AND COMMERCIAL","StartDateTime":"2023-03-04T05:00:00Z","EndDateTime":"2023-03-04T05:00:00Z","TrackingID":"1522087611"}},{"Interactionkey":"96d9d40eb241a83d88323fc6a779f011","Interactionsrckey":"1521618064","SourceDescription":"EMCON HISTORY","SourceCode":"EMCON","ChannelCode":"PRINT","ChannelDescription":"PRINT","DirectionCode":"O","ActionTime":"2023-03-04T05:00:00Z","PRINT":{"CategoryCode":"EOB","CategoryDescription":"EXPLANATION OF BENEFITS","MessageDefinitionIDCD":"43","MessageDefinitionDescription":"Standard EOB","StartDateTime":"2023-03-04T05:00:00Z","BusinessArea":"MEDICARE AND COMMERCIAL","BusineesSubArea":"MEDICARE AND COMMERCIAL","StatusDescription":"PRINTED","StatusCode":"77","EndDateTime":"2023-03-04T05:00:00Z","Interactionkey":"96d9d40eb241a83d88323fc6a779f011","DepartmentName":"Dialogue-Print","IsFinal":true,"TrackingID":"1521618064","MessageFulfillmentId":"1521618064","SDRPersonId":"1092455787","MemberCardId":"H41844156","DependentCode":"00","PlatformCode":"LV","PersonGenKey":"9726004690122","Printable":true}},{"Interactionkey":"71f3e433a9bedf53eb0d104bd6794a10","Interactionsrckey":"2677916666","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"EMAIL","ChannelDescription":"Email","DirectionCode":"O","ActionTime":"2023-03-01T22:43:46Z","EMAIL":{"MessageDefinitionIDCD":"SP4158_MHJSCORECARD2023","MessageDefinitionDescription":"Resource and scorecard outreach to members to encourage them to complete outstanding health screenings 2023","BusineesSubArea":"Individual","StartDateTime":"2023-03-01T22:43:46Z","EndDateTime":"2023-03-07T01:33:46Z","StatusDescription":"Delivered","RecipientEmail":"","TrackingID":"2677916666"}},{"Interactionkey":"129018dca0c1a5fdfd73c8f121810d7c","Interactionsrckey":"2677561593","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"SMS","ChannelDescription":"SMS","DirectionCode":"O","ActionTime":"2023-03-01T22:28:58Z","SMS":{"MessageDefinitionIDCD":"SM0117_MHJ_SCORECARD1_2023","MessageDefinitionDescription":"Resource and scorecard outreach to members to encourage them to complete outstanding health screenings","BusineesSubArea":"Medicare and Commercial","StartDateTime":"2023-03-01T22:28:58Z","EndDateTime":"2023-03-07T00:20:15Z","StatusDescription":"Delivered","RecipientPhone":"FL","TrackingID":"2677561593"}},{"Interactionkey":"5fa43cd7b12fb0fd9059902690ee9166","Interactionsrckey":"2677012337","SourceDescription":" Enterprise Measurable Messaging Ecosystem","SourceCode":"EMME","ChannelCode":"PRINT","ChannelDescription":"Print","DirectionCode":"O","ActionTime":"2023-03-01T22:12:08Z","PRINT":{"CategoryCode":"OTHER","CategoryDescription":"Other","MessageDefinitionIDCD":"MP0381_MHJ_SCORECARD1_2023","MessageDefinitionDescription":"Outstanding health screenings","StartDateTime":"2023-03-01T22:12:08Z","BusinessArea":"Medicare and Commercial","BusineesSubArea":"Medicare and Commercial","StatusDescription":"Delivered","StatusCode":"D","EndDateTime":"2023-03-17T19:11:20Z","Interactionkey":"5fa43cd7b12fb0fd9059902690ee9166","DepartmentName":"MPS","IsFinal":true,"TrackingID":"2677012337","MessageFulfillmentId":"2677012337","SDRPersonId":"1092455787","MemberCardId":"H41844156","DependentCode":"00","PlatformCode":"LV","PersonGenKey":"9726004690122","Printable":true}}]}}}';            
            //oResponse.setStatusCode(200);     
            //oResponse.setBody(sJson);
            
            Map<String, HTTPRequest> mapDetails = objCon.getRequests();
            string reqType = '';
            for (String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<string>labelSearch = new List<string>();
            labelSearch.add(reqType);
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            Test.setContinuationResponse(reqType, oResponse);
            RTIInteraction_LC_HUM.processResponse(labelSearch, 200);
         
            
            //oResponse.setStatusCode(200); 
            system.assertNotEquals(null,oResponse);
            
           Test.stopTest();  
        }
        
    }
    
    
    
    @isTest Static void testGetRTIApplicationValue(){
        User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        List<RTI_Application_Value__mdt> RTIAppList = [select Attribute_Label__c, Attribute_Value__c,
                                                                Field_Label__c, Parent_Attribute_Value__c, Disposition_Code__c,Category_Code__c,
                                                                Status_Code__c,
                                                                Parent_Field_Label__c, Sequence__c from 
                                                                RTI_Application_Value__mdt where
                                                                IsActive__c = true order by 
                                                                Sequence__c limit 1000 ];
        System.runAs(testUser)
        {
            System.assertEquals(RTIAppList, RTIInteraction_LC_HUM.GetRTIApplicationValue());
            System.assertNotEquals(null, RTIInteraction_LC_HUM.GetRTIApplicationValue());
        }
    }

     @isTest Static void testprocessResponse(){
        User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(testUser)
        {
            User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           
           //Continuation conti1 = (Continuation)RTIInteraction_LC_HUM.search('1023880950', '',  '' , 'IV', '', 'O',0);        
           Continuation objCon = New Continuation(120);
           objCon.continuationMethod='processResponse';
           objCon.State = 'Both';            
           
           HttpResponse oResponse = New HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           
           String sJson ='{"InteractionSummaryResponse": {"InteractionSummaryResult":{"Interactions":[{"Interactionkey":'+
           '"d5b40952-1198-4e0a-874e-8dba991ac3c5","Interactionsrckey":"2166254365424839006","SourceDescription":"COREMETRICS",' +
           '"SourceCode":"CRMTRC","ChannelCode":"WB","ChannelDescription": "WEB BROWSER","DirectionCode": "I","ActionTime": '+
           '"2016-11-13T05:00:00Z","WB": {"interactionWith": "MEMBER"}}]}}}';
            
            //sJson = '{"InteractionSummaryResponse": {"InteractionSummaryResult": {"NbRecords": 27,"Interactions": [{"Interactionkey": "d0f03df35e598cff73760ba2dfca41dc","Interactionsrckey": "2796698140","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-05-23T05:04:10Z","VAT": {"MessageDefinitionIDCD": "SL0176_RXELEAD_WT_FS_NPRC","CampaignName": "WT FENCE SITTER-JC OFFSHORE","PhoneNumber": "","StartDateTime": "2023-05-23T05:04:10Z","EndDateTime": "2023-05-26T15:42:46Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2796698140"}},{"Interactionkey": "bb9eb6218a97243406713a1501ece899","Interactionsrckey": "2795551881","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-05-22T20:46:04Z","SMS": {"MessageDefinitionIDCD": "SM0169_HNC_AMS","MessageDefinitionDescription": "Promote a variety of in-person and online classes via SMS relevant to Aging, MH, & Sleep Disorder","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-22T20:46:04Z","EndDateTime": "2023-05-24T16:39:35Z","StatusDescription": "Delivered","RecipientPhone": "3652149870","TrackingID": "2795551881"}},{"Interactionkey": "5189bc6e981917bd22adfc3b0d7a6db6","Interactionsrckey": "2794387854","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-05-22T15:59:30Z","EMAIL": {"MessageDefinitionIDCD": "SP4288_HNCAGINGMHSLEEP_JUNE","MessageDefinitionDescription": "We want to promote a variety of in-person and online classes relevant to Aging, MH, & Sleep Disorder June","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-22T15:59:30Z","EndDateTime": "2023-05-28T02:11:26Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2794387854"}},{"Interactionkey": "619ae0d17f5bfdb07505f3f9ac6cebca","Interactionsrckey": "2793337217","SourceDescription": "Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-05-22T16:06:49Z","VAT": {"MessageDefinitionIDCD": "SL0146_OTC_Order_Shipped","CampaignName": "OTC order shipped","PhoneNumber": "5042050337","StartDateTime": "2023-05-22T09:15:27Z","EndDateTime": "2023-05-22T16:08:39Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2793337217"}},{"Interactionkey": "e5400faae8fb9de484f24c08ddc19cd4","Interactionsrckey": "1612465648","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-05-22T04:00:00Z","PRINT": {"CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","MessageDefinitionIDCD": "43","MessageDefinitionDescription": "Standard EOB","StartDateTime": "2023-05-22T04:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-05-22T04:00:00Z","Interactionkey": "e5400faae8fb9de484f24c08ddc19cd4","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1612465648","MessageFulfillmentId": "1612465648","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "0296ab214bbb611bd9f2b4a2b6a91b77","Interactionsrckey": "2787517906","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-05-16T14:29:06Z","EMAIL": {"MessageDefinitionIDCD": "SP4253_COLORECTAL2023_W2","MessageDefinitionDescription": "Objective of this campaign is to educate members on the importance of getting their colorectal cancer screening","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-16T14:29:06Z","EndDateTime": "2023-05-18T14:18:51Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2787517906"}},{"Interactionkey": "8504623e4922676d7cf4ac799bc1b117","Interactionsrckey": "2770533462","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-05-04T08:03:28Z","EMAIL": {"MessageDefinitionIDCD": "SP2603_MYDIRECTIVES","MessageDefinitionDescription": "A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-04T08:03:28Z","EndDateTime": "2023-05-13T22:48:37Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2770533462"}},{"Interactionkey": "05d26d6d6a2e09f050dbf227939632d6","Interactionsrckey": "1593736243","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-05-04T04:00:00Z","PRINT": {"CategoryCode": "SS","CategoryDescription": "SMART SUMMARY","MessageDefinitionIDCD": "47","MessageDefinitionDescription": "PARTCDMerge","StartDateTime": "2023-05-04T04:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-05-04T04:00:00Z","Interactionkey": "05d26d6d6a2e09f050dbf227939632d6","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1593736243","MessageFulfillmentId": "1593736243","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "e999f9a35959dff3d17973c331a6e98c","Interactionsrckey": "2767445496","SourceDescription": "Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-05-02T17:29:41Z","VAT": {"MessageDefinitionIDCD": "SL0034_REFILL_REMIND_MEMBER_RESULTS_A_C","CampaignName": "Auto refill reminder","PhoneNumber": "5042050337","StartDateTime": "2023-05-02T09:59:12Z","EndDateTime": "2023-05-02T23:33:26Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "71","SubStatusDesc": "Answering Machine","TrackingID": "2767445496"}},{"Interactionkey": "b6cbd6f5bbf1ab03493325d02343af18","Interactionsrckey": "1587585330","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "WB","ChannelDescription": "WEB","DirectionCode": "O","ActionTime": "2023-05-02T04:00:00Z","WB": {"MessageDefinitionIDCD": "43","CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","interactionWithDesc": "MEMBER","interactingWithCode": "M","MessageDefinitionDescription": "Standard EOB","BusineesSubArea": "MEDICARE AND COMMERCIAL","StartDateTime": "2023-05-02T04:00:00Z","EndDateTime": "2023-05-02T04:00:00Z","TrackingID": "1587585330"}},{"Interactionkey": "45f216ba93a44fc086a65ff84f1427ff","Interactionsrckey": "2758712391","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-04-27T08:29:22Z","EMAIL": {"MessageDefinitionIDCD": "SP2603_MYDIRECTIVES","MessageDefinitionDescription": "A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-04-27T08:29:22Z","EndDateTime": "2023-05-02T14:04:52Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2758712391"}},{"Interactionkey": "a03295217e6091ee11e5569ee1789b4b","Interactionsrckey": "2748311467","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-04-20T08:11:35Z","EMAIL": {"MessageDefinitionIDCD": "SP2603_MYDIRECTIVES","MessageDefinitionDescription": "A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-04-20T08:11:35Z","EndDateTime": "2023-04-29T23:42:29Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2748311467"}},{"Interactionkey": "6d49ce6700e686cf9369d43111abec82","Interactionsrckey": "2747033604","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-04-19T16:34:30Z","VAT": {"MessageDefinitionIDCD": "SL0176_RXELEAD_WT_FS_NPRC","CampaignName": "WT FENCE SITTER-JC OFFSHORE","PhoneNumber": "","StartDateTime": "2023-04-19T16:34:30Z","EndDateTime": "2023-05-08T22:12:34Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2747033604"}},{"Interactionkey": "af8c75a335d3061a536cd87263a0186c","Interactionsrckey": "2738071187","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "PRINT","ChannelDescription": "Print","DirectionCode": "O","ActionTime": "2023-04-14T08:03:21Z","PRINT": {"CategoryCode": "OTHER","CategoryDescription": "Other","MessageDefinitionIDCD": "MP0363_HOP_FS","MessageDefinitionDescription": "HOP Traditional mailing FS","StartDateTime": "2023-04-14T08:03:21Z","BusinessArea": "Medicare and Commercial","BusineesSubArea": "Medicare and Commercial","StatusDescription": "Delivered","StatusCode": "D","EndDateTime": "2023-04-25T12:49:45Z","Interactionkey": "af8c75a335d3061a536cd87263a0186c","DepartmentName": "MPS","IsFinal": true,"TrackingID": "2738071187","MessageFulfillmentId": "2738071187","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "lv","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "2ec23e28cb80b2ba1fa46837b5554304","Interactionsrckey": "2705962789","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-03-21T16:10:11Z","EMAIL": {"MessageDefinitionIDCD": "SP4228_AUTOREFILLCAMPAIGN","MessageDefinitionDescription": "The auto refill campaign reaches out to current CenterWell Pharmacy customers with medications eligible for auto refill","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-21T16:10:11Z","EndDateTime": "2023-03-27T20:35:32Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2705962789"}},{"Interactionkey": "562d9e7c4b5c0c677b3d5ec5ed55ecd9","Interactionsrckey": "2698488246","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-03-17T10:12:32Z","SMS": {"MessageDefinitionIDCD": "SM0039_MY_DIRECTIVE","MessageDefinitionDescription": "Text to communicate about the My Directive available for members to complete.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-17T10:12:32Z","EndDateTime": "2023-03-20T15:25:41Z","StatusDescription": "Delivered","RecipientPhone": "","TrackingID": "2698488246"}},{"Interactionkey": "b85fb2573ef7555bdbc15b1ad95d983f","Interactionsrckey": "2692910478","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-03-14T04:33:35Z","VAT": {"MessageDefinitionIDCD": "SL0176_RXELEAD_NPRC","CampaignName": "Humana Pharmacy Education","PhoneNumber": "","StartDateTime": "2023-03-14T04:33:35Z","EndDateTime": "2023-03-16T15:22:53Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2692910478"}},{"Interactionkey": "fb9712f1f6387e0ffdcfac5275e41875","Interactionsrckey": "1534830278","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "WB","ChannelDescription": "WEB","DirectionCode": "O","ActionTime": "2023-03-11T05:00:00Z","WB": {"MessageDefinitionIDCD": "43","CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","interactionWithDesc": "MEMBER","interactingWithCode": "M","MessageDefinitionDescription": "Standard EOB","BusineesSubArea": "MEDICARE AND COMMERCIAL","StartDateTime": "2023-03-11T05:00:00Z","EndDateTime": "2023-03-11T05:00:00Z","TrackingID": "1534830278"}},{"Interactionkey": "7b1317bc492a6b0c2816c87601e6d60d","Interactionsrckey": "2689588382","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "PRINT","ChannelDescription": "Print","DirectionCode": "O","ActionTime": "2023-03-10T06:01:33Z","PRINT": {"CategoryCode": "OTHER","CategoryDescription": "Other","MessageDefinitionIDCD": "MP0363_HOP_FS","MessageDefinitionDescription": "HOP Traditional mailing FS","StartDateTime": "2023-03-10T06:01:33Z","BusinessArea": "Medicare and Commercial","BusineesSubArea": "Medicare and Commercial","StatusDescription": "Delivered","StatusCode": "D","EndDateTime": "2023-03-24T15:06:37Z","Interactionkey": "7b1317bc492a6b0c2816c87601e6d60d","DepartmentName": "MPS","IsFinal": true,"TrackingID": "2689588382","MessageFulfillmentId": "2689588382","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "lv","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "ca863e9a4d85a36d0840c27eef4d6885","Interactionsrckey": "2683687641","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-03-06T19:58:58Z","EMAIL": {"MessageDefinitionIDCD": "SP4179_COLORECTAL2023","MessageDefinitionDescription": "bjective of this campaign is to educate members on the importance of getting their colorectal cancer screening","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-06T19:58:58Z","EndDateTime": "2023-03-12T01:26:26Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2683687641"}},{"Interactionkey": "9607ee6cd31e4d2a569883fcb6839beb","Interactionsrckey": "2683447113","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-03-06T19:50:28Z","SMS": {"MessageDefinitionIDCD": "SM0118_COLORECTAL_2023","MessageDefinitionDescription": "Text to educate importance of colorectal cancer screening","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-06T19:50:28Z","EndDateTime": "2023-03-23T13:59:56Z","StatusDescription": "Delivered","RecipientPhone": "","TrackingID": "2683447113"}},{"Interactionkey": "66419de8b568faec884aa56546dcb271","Interactionsrckey": "1528461319","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-03-04T05:00:00Z","PRINT": {"CategoryCode": "SS","CategoryDescription": "SMART SUMMARY","MessageDefinitionIDCD": "47","MessageDefinitionDescription": "PARTCDMerge","StartDateTime": "2023-03-04T05:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-03-04T05:00:00Z","Interactionkey": "66419de8b568faec884aa56546dcb271","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1528461319","MessageFulfillmentId": "1528461319","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "81366b5bf94d4568f89b5b385db4577b","Interactionsrckey": "1522087611","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "WB","ChannelDescription": "WEB","DirectionCode": "O","ActionTime": "2023-03-04T05:00:00Z","WB": {"MessageDefinitionIDCD": "43","CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","interactionWithDesc": "MEMBER","interactingWithCode": "M","MessageDefinitionDescription": "Standard EOB","BusineesSubArea": "MEDICARE AND COMMERCIAL","StartDateTime": "2023-03-04T05:00:00Z","EndDateTime": "2023-03-04T05:00:00Z","TrackingID": "1522087611"}},{"Interactionkey": "96d9d40eb241a83d88323fc6a779f011","Interactionsrckey": "1521618064","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-03-04T05:00:00Z","PRINT": {"CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","MessageDefinitionIDCD": "43","MessageDefinitionDescription": "Standard EOB","StartDateTime": "2023-03-04T05:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-03-04T05:00:00Z","Interactionkey": "96d9d40eb241a83d88323fc6a779f011","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1521618064","MessageFulfillmentId": "1521618064","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "71f3e433a9bedf53eb0d104bd6794a10","Interactionsrckey": "2677916666","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-03-01T22:43:46Z","EMAIL": {"MessageDefinitionIDCD": "SP4158_MHJSCORECARD2023","MessageDefinitionDescription": "Resource and scorecard outreach to members to encourage them to complete outstanding health screenings 2023","BusineesSubArea": "Individual","StartDateTime": "2023-03-01T22:43:46Z","EndDateTime": "2023-03-07T01:33:46Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2677916666"}},{"Interactionkey": "129018dca0c1a5fdfd73c8f121810d7c","Interactionsrckey": "2677561593","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-03-01T22:28:58Z","SMS": {"MessageDefinitionIDCD": "SM0117_MHJ_SCORECARD1_2023","MessageDefinitionDescription": "Resource and scorecard outreach to members to encourage them to complete outstanding health screenings","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-01T22:28:58Z","EndDateTime": "2023-03-07T00:20:15Z","StatusDescription": "Delivered","RecipientPhone": "FL","TrackingID": "2677561593"}},{"Interactionkey": "5fa43cd7b12fb0fd9059902690ee9166","Interactionsrckey": "2677012337","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "PRINT","ChannelDescription": "Print","DirectionCode": "O","ActionTime": "2023-03-01T22:12:08Z","PRINT": {"CategoryCode": "OTHER","CategoryDescription": "Other","MessageDefinitionIDCD": "MP0381_MHJ_SCORECARD1_2023","MessageDefinitionDescription": "Outstanding health screenings","StartDateTime": "2023-03-01T22:12:08Z","BusinessArea": "Medicare and Commercial","BusineesSubArea": "Medicare and Commercial","StatusDescription": "Delivered","StatusCode": "D","EndDateTime": "2023-03-17T19:11:20Z","Interactionkey": "5fa43cd7b12fb0fd9059902690ee9166","DepartmentName": "MPS","IsFinal": true,"TrackingID": "2677012337","MessageFulfillmentId": "2677012337","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}}]}}}';
           
            Map<String, HTTPRequest> mapDetails = objCon.getRequests();
            string reqType = '';
            for (String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<string>labelSearch = new List<string>();
            labelSearch.add(reqType);
            //oResponse.setBody(sJson);
            //oResponse.setStatusCode(200); 
            //Test.setContinuationResponse(reqType, oResponse);
            //object res = RTIInteraction_LC_HUM.processResponse(labelSearch, 200);
            
            
            oResponse.setStatusCode(400); 
            Test.setContinuationResponse(reqType, oResponse);
            RTIInteraction_LC_HUM.processResponse(labelSearch, 400);
           
            //system.assertNotEquals(null,res);
            
           Test.stopTest();  
        }
        }
     }
    
    /*
     * Method Name : testDetails
     * Description : Used to test rti details scenarios
     * Return type : void
     * Paramater   : NA
     */ 
    @isTest static void testDetails()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
           String sRequestLabel;
           String sInteractionKey = '1234';
           String sChannelCode = 'PRINT';
           String sCategoryDesc = 'LETTER,LT';
           String sJson ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 2,   "Prints": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAb/TS7WzfBXU8K1x6DFsF8KeEYFD33cq8KwuiWqhkMrzX4EvbI581cY6MNP9PR3/ltjj+SwjaLhMsZTeqjnNicBejC0eK3h4mQGoCTH+YdO65/dI0txXTp/hC7CksKHkld0ciII84j6XpolTpoJGBluSM7F50sZ3+QCswaoofYzxTu6vlf0hIyU6hlb7GgoMkw==&UrlType=MVS&UrlDocumentType=PDF"}]}}}';
           RTIInteraction_LC_HUM oRTIInteraction = New RTIInteraction_LC_HUM();
           Test.StartTest();
           Continuation conti = (Continuation)RTIInteraction_LC_HUM.getRTIInteractionDetails(sInteractionKey, sChannelCode, sCategoryDesc);
           
           Continuation objCon = New Continuation(120);
           objCon.continuationMethod ='processDetailsResponse';
           objCon.State = 'Both';
            
            
           Map<String, HTTPRequest> mapDetails = objCon.getRequests();
           string reqType = '';
           for (String s : mapDetails.keyset())
           {
               reqType = s;
           }
           List<string>labelSearch = new List<string>();
           labelSearch.add(reqType);
           //object res = RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 200);
           
           HttpResponse oResponse = New HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200);
            
           Test.setContinuationResponse(reqType, oResponse);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 'PRINT_LETTER');
           system.assertNotEquals(null,oResponse); 
            
           oResponse.setStatusCode(500);
           Test.setContinuationResponse(reqType, oResponse);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 'PRINT_LETTER');
           
           Continuation conti1 = (Continuation)RTIInteraction_LC_HUM.getRTIInteractionDetails(sInteractionKey, 'Text', sCategoryDesc);
           
           
           Continuation objCon1 = New Continuation(120);
           objCon1.continuationMethod ='processDetailsResponse';
           objCon1.State = 'Both';
            
            
           Map<String, HTTPRequest> mapDetails1 = objCon.getRequests();
           string reqType1 = '';
           for (String s : mapDetails1.keyset())
           {
               reqType1 = s;
           }
           List<string>labelSearch1 = new List<string>();
           labelSearch1.add(reqType1);
           //object res = RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 200);
           
           HttpResponse oResponse1 = New HttpResponse();
           oResponse1.setHeader('Content-Type', 'application/json');
           oResponse1.setHeader('SOAPAction', '');    
           oResponse1.setHeader('Connection','keep-alive');
           oResponse1.setBody(sJson);
           oResponse1.setStatusCode(200);
            
           Test.setContinuationResponse(reqType1, oResponse1);
        
           String ResponseWeb ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "Webs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF"   }]}}}';
           oResponse1.setBody(ResponseWeb);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch1, 'WEB_WEB');
            
           String ResponseWb ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "wBs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF",      "Code": "LINK",      "Purpose": "LINK",      "actionTime": "2018-10-18T04:00:00Z",      "pageId": null,      "siteId": null,      "prodName": null,      "shopActionTypeCD": null,      "eventActionTypeCD": null,      "eventName": null,      "elementName": null   }]}}}';
           oResponse1.setBody(ResponseWb);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 'WB_WB');
            
           String ResponseEmail ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "Emails": [   {      "documentType": "SAMPLE",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=GAdfqBjJA062EBb6Idl3qdwdHhyJGSI7/z7GyINHcLPEyd3PpXXayJJdA7qw2WIDDZUTwTzZZA9m23AYeJlDqD1pMMwTvmYX4ahAMODGwJQBeZpEs5eIqA7WnflDkh1V&UrlType=VDN&UrlDocumentType=PDF"   }]}}}';
           oResponse1.setBody(ResponseEmail);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 'PRIN_EMAIL');
           
           Continuation conti11 = (Continuation)RTIInteraction_LC_HUM.getRTIInteractionDetails(sInteractionKey, 'WEB', 'EM');
           Test.setContinuationResponse(reqType1, oResponse1);
           String ResponseEm ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "eMs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF"   }]}}}';
           oResponse1.setBody(ResponseEm);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 'PRIN_EM');
           
           Continuation conti12 = (Continuation)RTIInteraction_LC_HUM.getRTIInteractionDetails(sInteractionKey, 'WEB', 'VAT');
           Test.setContinuationResponse(reqType1, oResponse1);
           String ResponseVat ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "vaTs": [{"documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=GAdfqBjJA062EBb6Idl3qdwdHhyJGSI7/z7GyINHcLPEyd3PpXXayJJdA7qw2WIDDZUTwTzZZA9m23AYeJlDqD1pMMwTvmYX4ahAMODGwJQBeZpEs5eIqA7WnflDkh1V&UrlType=VDN&UrlDocumentType=PDF"}]}}}';            
           oResponse1.setBody(ResponseVat);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 'PRINT_VAT');
            
           Continuation conti2 = (Continuation)RTIInteraction_LC_HUM.getRTIInteractionDetails(sInteractionKey, 'WEB', 'Text');
           Test.setContinuationResponse(reqType1, oResponse1);
           String ResponseText ='{"InteractionDetailResponse": {"InteractionDetailResult": {   "NbRecords": 1,   "smSs": [   {      "documentType": "LINK",      "documentViewUrl": "http://int-apps.humana.com/EMME/DocumentViewer/EMMEDocumentViewer.aspx?UrlKey=MucChPFdlJkEbGGJoJqYAWPvuPHyBXtFLaDOXZz3MsT6AUDmXh9CQE5QiMjhFaWObFZ1rLRf+WERU5quCzMBvktm2yCBc9OUZRaUiPtVTmpupPJeLElAaoSKKxObILFsBWHn5Dx4obz3snV8Id/pF9e2Xtrz972cpuP3Eo9yXaCLUc3TU/oe92ky238GNnRfdUcD4PeN82O0DRUbZ6SAlA==&UrlType=MVS&UrlDocumentType=PDF"   }]}}}';
           oResponse1.setBody(ResponseText);
           RTIInteraction_LC_HUM.processDetailsResponse(labelSearch, 'PRIN_Text');
           
           Test.StopTest();
        }
    }
    
     /*
     * Method Name : testSearchEmptyID
     * Description : Used to test search with empty id
     * Return type : void
     * Paramater   : NA
     */ 
    @isTest static void testSearchEmptyID()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        {
            Test.startTest();
            Object result = RTIInteraction_LC_HUM.search('', '',  '' , 'All', '', 'O',0);
            system.assertEquals(null, result);
            Test.stopTest();
        }
    }
    
    @isTest static void testException()
    {
        HUM_Webservice_Callout__c oSvcHeaderRTI = HUM_Webservice_Callout__c.getValues('RTIService_GetInteractionDetails_V2');
        delete oSvcHeaderRTI;
        RTIInteraction_LC_HUM.getRTIInteractionDetails('', '', '');
        HUM_Webservice_Callout__c oSvcHeaderRTI1 = HUM_Webservice_Callout__c.getValues('RTIService_GetInteractions_V2');
        delete oSvcHeaderRTI1;
        HUM_Webservice_Callout__c oSvcHeaderRTI2 = HUM_Webservice_Callout__c.getValues('Test');
        delete oSvcHeaderRTI2;
        RTIInteraction_LC_HUM.search('123' ,'', '' , '', '','' ,  0);
        HttpRequest request = new HttpRequest();
        List<RTIApplicationValues_H_HUM.AttributeValue> attrValue = new List<RTIApplicationValues_H_HUM.AttributeValue>();
        RTISummary_LS_HUM oRtiSumLS = new RTISummary_LS_HUM();
        oRtiSumLS.setupRequest(request,oSvcHeaderRTI1,'','','','',0,'','','',attrValue);
        oRtiSumLS.setupRequest(request,oSvcHeaderRTI2,'','','','',0,'','','',attrValue);
        RTIInteraction_LC_HUM.iRTICards = '';
        RTIInteraction_LC_HUM.bIsPageOnLoad = false;
        
        //String responseIV = '{"InteractionSummaryResponse": {"InteractionSummaryResult": {"NbRecords": 27,"Interactions": [{"Interactionkey": "d0f03df35e598cff73760ba2dfca41dc","Interactionsrckey": "2796698140","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-05-23T05:04:10Z","VAT": {"MessageDefinitionIDCD": "SL0176_RXELEAD_WT_FS_NPRC","CampaignName": "WT FENCE SITTER-JC OFFSHORE","PhoneNumber": "","StartDateTime": "2023-05-23T05:04:10Z","EndDateTime": "2023-05-26T15:42:46Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2796698140"}},{"Interactionkey": "bb9eb6218a97243406713a1501ece899","Interactionsrckey": "2795551881","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-05-22T20:46:04Z","SMS": {"MessageDefinitionIDCD": "SM0169_HNC_AMS","MessageDefinitionDescription": "Promote a variety of in-person and online classes via SMS relevant to Aging, MH, & Sleep Disorder","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-22T20:46:04Z","EndDateTime": "2023-05-24T16:39:35Z","StatusDescription": "Delivered","RecipientPhone": "3652149870","TrackingID": "2795551881"}},{"Interactionkey": "5189bc6e981917bd22adfc3b0d7a6db6","Interactionsrckey": "2794387854","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-05-22T15:59:30Z","EMAIL": {"MessageDefinitionIDCD": "SP4288_HNCAGINGMHSLEEP_JUNE","MessageDefinitionDescription": "We want to promote a variety of in-person and online classes relevant to Aging, MH, & Sleep Disorder June","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-22T15:59:30Z","EndDateTime": "2023-05-28T02:11:26Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2794387854"}},{"Interactionkey": "619ae0d17f5bfdb07505f3f9ac6cebca","Interactionsrckey": "2793337217","SourceDescription": "Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-05-22T16:06:49Z","VAT": {"MessageDefinitionIDCD": "SL0146_OTC_Order_Shipped","CampaignName": "OTC order shipped","PhoneNumber": "5042050337","StartDateTime": "2023-05-22T09:15:27Z","EndDateTime": "2023-05-22T16:08:39Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2793337217"}},{"Interactionkey": "e5400faae8fb9de484f24c08ddc19cd4","Interactionsrckey": "1612465648","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-05-22T04:00:00Z","PRINT": {"CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","MessageDefinitionIDCD": "43","MessageDefinitionDescription": "Standard EOB","StartDateTime": "2023-05-22T04:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-05-22T04:00:00Z","Interactionkey": "e5400faae8fb9de484f24c08ddc19cd4","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1612465648","MessageFulfillmentId": "1612465648","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "0296ab214bbb611bd9f2b4a2b6a91b77","Interactionsrckey": "2787517906","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-05-16T14:29:06Z","EMAIL": {"MessageDefinitionIDCD": "SP4253_COLORECTAL2023_W2","MessageDefinitionDescription": "Objective of this campaign is to educate members on the importance of getting their colorectal cancer screening","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-16T14:29:06Z","EndDateTime": "2023-05-18T14:18:51Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2787517906"}},{"Interactionkey": "8504623e4922676d7cf4ac799bc1b117","Interactionsrckey": "2770533462","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-05-04T08:03:28Z","EMAIL": {"MessageDefinitionIDCD": "SP2603_MYDIRECTIVES","MessageDefinitionDescription": "A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-05-04T08:03:28Z","EndDateTime": "2023-05-13T22:48:37Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2770533462"}},{"Interactionkey": "05d26d6d6a2e09f050dbf227939632d6","Interactionsrckey": "1593736243","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-05-04T04:00:00Z","PRINT": {"CategoryCode": "SS","CategoryDescription": "SMART SUMMARY","MessageDefinitionIDCD": "47","MessageDefinitionDescription": "PARTCDMerge","StartDateTime": "2023-05-04T04:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-05-04T04:00:00Z","Interactionkey": "05d26d6d6a2e09f050dbf227939632d6","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1593736243","MessageFulfillmentId": "1593736243","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "e999f9a35959dff3d17973c331a6e98c","Interactionsrckey": "2767445496","SourceDescription": "Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-05-02T17:29:41Z","VAT": {"MessageDefinitionIDCD": "SL0034_REFILL_REMIND_MEMBER_RESULTS_A_C","CampaignName": "Auto refill reminder","PhoneNumber": "5042050337","StartDateTime": "2023-05-02T09:59:12Z","EndDateTime": "2023-05-02T23:33:26Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "71","SubStatusDesc": "Answering Machine","TrackingID": "2767445496"}},{"Interactionkey": "b6cbd6f5bbf1ab03493325d02343af18","Interactionsrckey": "1587585330","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "WB","ChannelDescription": "WEB","DirectionCode": "O","ActionTime": "2023-05-02T04:00:00Z","WB": {"MessageDefinitionIDCD": "43","CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","interactionWithDesc": "MEMBER","interactingWithCode": "M","MessageDefinitionDescription": "Standard EOB","BusineesSubArea": "MEDICARE AND COMMERCIAL","StartDateTime": "2023-05-02T04:00:00Z","EndDateTime": "2023-05-02T04:00:00Z","TrackingID": "1587585330"}},{"Interactionkey": "45f216ba93a44fc086a65ff84f1427ff","Interactionsrckey": "2758712391","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-04-27T08:29:22Z","EMAIL": {"MessageDefinitionIDCD": "SP2603_MYDIRECTIVES","MessageDefinitionDescription": "A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-04-27T08:29:22Z","EndDateTime": "2023-05-02T14:04:52Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2758712391"}},{"Interactionkey": "a03295217e6091ee11e5569ee1789b4b","Interactionsrckey": "2748311467","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-04-20T08:11:35Z","EMAIL": {"MessageDefinitionIDCD": "SP2603_MYDIRECTIVES","MessageDefinitionDescription": "A direct-to-member email to all current Medicare Advantage members encouraging them to complete an advance care plan (living will) using the MyDirectives digital tool.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-04-20T08:11:35Z","EndDateTime": "2023-04-29T23:42:29Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2748311467"}},{"Interactionkey": "6d49ce6700e686cf9369d43111abec82","Interactionsrckey": "2747033604","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-04-19T16:34:30Z","VAT": {"MessageDefinitionIDCD": "SL0176_RXELEAD_WT_FS_NPRC","CampaignName": "WT FENCE SITTER-JC OFFSHORE","PhoneNumber": "","StartDateTime": "2023-04-19T16:34:30Z","EndDateTime": "2023-05-08T22:12:34Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2747033604"}},{"Interactionkey": "af8c75a335d3061a536cd87263a0186c","Interactionsrckey": "2738071187","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "PRINT","ChannelDescription": "Print","DirectionCode": "O","ActionTime": "2023-04-14T08:03:21Z","PRINT": {"CategoryCode": "OTHER","CategoryDescription": "Other","MessageDefinitionIDCD": "MP0363_HOP_FS","MessageDefinitionDescription": "HOP Traditional mailing FS","StartDateTime": "2023-04-14T08:03:21Z","BusinessArea": "Medicare and Commercial","BusineesSubArea": "Medicare and Commercial","StatusDescription": "Delivered","StatusCode": "D","EndDateTime": "2023-04-25T12:49:45Z","Interactionkey": "af8c75a335d3061a536cd87263a0186c","DepartmentName": "MPS","IsFinal": true,"TrackingID": "2738071187","MessageFulfillmentId": "2738071187","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "lv","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "2ec23e28cb80b2ba1fa46837b5554304","Interactionsrckey": "2705962789","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-03-21T16:10:11Z","EMAIL": {"MessageDefinitionIDCD": "SP4228_AUTOREFILLCAMPAIGN","MessageDefinitionDescription": "The auto refill campaign reaches out to current CenterWell Pharmacy customers with medications eligible for auto refill","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-21T16:10:11Z","EndDateTime": "2023-03-27T20:35:32Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2705962789"}},{"Interactionkey": "562d9e7c4b5c0c677b3d5ec5ed55ecd9","Interactionsrckey": "2698488246","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-03-17T10:12:32Z","SMS": {"MessageDefinitionIDCD": "SM0039_MY_DIRECTIVE","MessageDefinitionDescription": "Text to communicate about the My Directive available for members to complete.","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-17T10:12:32Z","EndDateTime": "2023-03-20T15:25:41Z","StatusDescription": "Delivered","RecipientPhone": "","TrackingID": "2698488246"}},{"Interactionkey": "b85fb2573ef7555bdbc15b1ad95d983f","Interactionsrckey": "2692910478","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "VAT","ChannelDescription": "VAT","DirectionCode": "O","ActionTime": "2023-03-14T04:33:35Z","VAT": {"MessageDefinitionIDCD": "SL0176_RXELEAD_NPRC","CampaignName": "Humana Pharmacy Education","PhoneNumber": "","StartDateTime": "2023-03-14T04:33:35Z","EndDateTime": "2023-03-16T15:22:53Z","StatusCode": "D","StatusDescription": "Delivered","SubStatusCode": "72","SubStatusDesc": "AUTHENTICATED","TrackingID": "2692910478"}},{"Interactionkey": "fb9712f1f6387e0ffdcfac5275e41875","Interactionsrckey": "1534830278","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "WB","ChannelDescription": "WEB","DirectionCode": "O","ActionTime": "2023-03-11T05:00:00Z","WB": {"MessageDefinitionIDCD": "43","CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","interactionWithDesc": "MEMBER","interactingWithCode": "M","MessageDefinitionDescription": "Standard EOB","BusineesSubArea": "MEDICARE AND COMMERCIAL","StartDateTime": "2023-03-11T05:00:00Z","EndDateTime": "2023-03-11T05:00:00Z","TrackingID": "1534830278"}},{"Interactionkey": "7b1317bc492a6b0c2816c87601e6d60d","Interactionsrckey": "2689588382","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "PRINT","ChannelDescription": "Print","DirectionCode": "O","ActionTime": "2023-03-10T06:01:33Z","PRINT": {"CategoryCode": "OTHER","CategoryDescription": "Other","MessageDefinitionIDCD": "MP0363_HOP_FS","MessageDefinitionDescription": "HOP Traditional mailing FS","StartDateTime": "2023-03-10T06:01:33Z","BusinessArea": "Medicare and Commercial","BusineesSubArea": "Medicare and Commercial","StatusDescription": "Delivered","StatusCode": "D","EndDateTime": "2023-03-24T15:06:37Z","Interactionkey": "7b1317bc492a6b0c2816c87601e6d60d","DepartmentName": "MPS","IsFinal": true,"TrackingID": "2689588382","MessageFulfillmentId": "2689588382","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "lv","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "ca863e9a4d85a36d0840c27eef4d6885","Interactionsrckey": "2683687641","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-03-06T19:58:58Z","EMAIL": {"MessageDefinitionIDCD": "SP4179_COLORECTAL2023","MessageDefinitionDescription": "bjective of this campaign is to educate members on the importance of getting their colorectal cancer screening","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-06T19:58:58Z","EndDateTime": "2023-03-12T01:26:26Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2683687641"}},{"Interactionkey": "9607ee6cd31e4d2a569883fcb6839beb","Interactionsrckey": "2683447113","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-03-06T19:50:28Z","SMS": {"MessageDefinitionIDCD": "SM0118_COLORECTAL_2023","MessageDefinitionDescription": "Text to educate importance of colorectal cancer screening","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-06T19:50:28Z","EndDateTime": "2023-03-23T13:59:56Z","StatusDescription": "Delivered","RecipientPhone": "","TrackingID": "2683447113"}},{"Interactionkey": "66419de8b568faec884aa56546dcb271","Interactionsrckey": "1528461319","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-03-04T05:00:00Z","PRINT": {"CategoryCode": "SS","CategoryDescription": "SMART SUMMARY","MessageDefinitionIDCD": "47","MessageDefinitionDescription": "PARTCDMerge","StartDateTime": "2023-03-04T05:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-03-04T05:00:00Z","Interactionkey": "66419de8b568faec884aa56546dcb271","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1528461319","MessageFulfillmentId": "1528461319","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "81366b5bf94d4568f89b5b385db4577b","Interactionsrckey": "1522087611","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "WB","ChannelDescription": "WEB","DirectionCode": "O","ActionTime": "2023-03-04T05:00:00Z","WB": {"MessageDefinitionIDCD": "43","CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","interactionWithDesc": "MEMBER","interactingWithCode": "M","MessageDefinitionDescription": "Standard EOB","BusineesSubArea": "MEDICARE AND COMMERCIAL","StartDateTime": "2023-03-04T05:00:00Z","EndDateTime": "2023-03-04T05:00:00Z","TrackingID": "1522087611"}},{"Interactionkey": "96d9d40eb241a83d88323fc6a779f011","Interactionsrckey": "1521618064","SourceDescription": "EMCON HISTORY","SourceCode": "EMCON","ChannelCode": "PRINT","ChannelDescription": "PRINT","DirectionCode": "O","ActionTime": "2023-03-04T05:00:00Z","PRINT": {"CategoryCode": "EOB","CategoryDescription": "EXPLANATION OF BENEFITS","MessageDefinitionIDCD": "43","MessageDefinitionDescription": "Standard EOB","StartDateTime": "2023-03-04T05:00:00Z","BusinessArea": "MEDICARE AND COMMERCIAL","BusineesSubArea": "MEDICARE AND COMMERCIAL","StatusDescription": "PRINTED","StatusCode": "77","EndDateTime": "2023-03-04T05:00:00Z","Interactionkey": "96d9d40eb241a83d88323fc6a779f011","DepartmentName": "Dialogue-Print","IsFinal": true,"TrackingID": "1521618064","MessageFulfillmentId": "1521618064","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}},{"Interactionkey": "71f3e433a9bedf53eb0d104bd6794a10","Interactionsrckey": "2677916666","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "EMAIL","ChannelDescription": "Email","DirectionCode": "O","ActionTime": "2023-03-01T22:43:46Z","EMAIL": {"MessageDefinitionIDCD": "SP4158_MHJSCORECARD2023","MessageDefinitionDescription": "Resource and scorecard outreach to members to encourage them to complete outstanding health screenings 2023","BusineesSubArea": "Individual","StartDateTime": "2023-03-01T22:43:46Z","EndDateTime": "2023-03-07T01:33:46Z","StatusDescription": "Delivered","RecipientEmail": "","TrackingID": "2677916666"}},{"Interactionkey": "129018dca0c1a5fdfd73c8f121810d7c","Interactionsrckey": "2677561593","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "SMS","ChannelDescription": "SMS","DirectionCode": "O","ActionTime": "2023-03-01T22:28:58Z","SMS": {"MessageDefinitionIDCD": "SM0117_MHJ_SCORECARD1_2023","MessageDefinitionDescription": "Resource and scorecard outreach to members to encourage them to complete outstanding health screenings","BusineesSubArea": "Medicare and Commercial","StartDateTime": "2023-03-01T22:28:58Z","EndDateTime": "2023-03-07T00:20:15Z","StatusDescription": "Delivered","RecipientPhone": "FL","TrackingID": "2677561593"}},{"Interactionkey": "5fa43cd7b12fb0fd9059902690ee9166","Interactionsrckey": "2677012337","SourceDescription": " Enterprise Measurable Messaging Ecosystem","SourceCode": "EMME","ChannelCode": "PRINT","ChannelDescription": "Print","DirectionCode": "O","ActionTime": "2023-03-01T22:12:08Z","PRINT": {"CategoryCode": "OTHER","CategoryDescription": "Other","MessageDefinitionIDCD": "MP0381_MHJ_SCORECARD1_2023","MessageDefinitionDescription": "Outstanding health screenings","StartDateTime": "2023-03-01T22:12:08Z","BusinessArea": "Medicare and Commercial","BusineesSubArea": "Medicare and Commercial","StatusDescription": "Delivered","StatusCode": "D","EndDateTime": "2023-03-17T19:11:20Z","Interactionkey": "5fa43cd7b12fb0fd9059902690ee9166","DepartmentName": "MPS","IsFinal": true,"TrackingID": "2677012337","MessageFulfillmentId": "2677012337","SDRPersonId": "1092455787","MemberCardId": "H41844156","DependentCode": "00","PlatformCode": "LV","PersonGenKey": "9726004690122","Printable": true}}]}}}';
          
        //oRtiSumLS.processResponse(responseIV);
            
        
    }

}