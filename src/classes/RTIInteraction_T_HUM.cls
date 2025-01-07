/**************************************************************************************************************************************************
Apex class Name    :  RTIInteraction_T_HUM
Version            :  1.0
Function           :  This is the test class for RTIInteraction_C_HUM controller class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Pallavi Ravishankar          61268                 01/04/2016          Original Version
 *  Kritika Popat                86349                 07/04/2016          Phase 2 Service changes
 *  Kritika Popat                86349                 10/04/2016          Pagination    
 *  Kritika Popat                                      24/04/2016          IVR and Print 2nd level
 *  Sumit Pharande                                     12/9/2018           NBA Changes
 *  Sumit Pharande                                                         REQ-386780 -Removed the 18 to 15 Digit Id conversion    
 *  Shailesh Mali                                      03/08/2021		   RTI build fix
 *  Rahul Chaurasia								  	   03/22/2021		   1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service
 *  Anuradha Gajbhe                                    05/18/2021          US - 2082385 Increased code coverage for the changes made on RTIInteraction_C_HUM  
************************************************************************************************************************************************* */
@isTest
Public  with sharing class RTIInteraction_T_HUM
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
        }
    }   
    
    /*
     * Method Name : runPositiveData
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runPositiveData()
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
           
           Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
           System.assertEquals(oPolMem.Relationship__c, 'Self');
           
           ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
           System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
           
           RTIInteraction_C_HUM oRTIInteractions = new RTIInteraction_C_HUM();
           oRTIInteractions.sEnterpriseId = '1023880950';
           oRTIInteractions.bDirectionSwitch = false;
           oRTIInteractions.sSelectedChannel = 'IVR';
           oRTIInteractions.populateCategory();
           oRTIInteractions.populateChannel();
           oRTIInteractions.sSelectedChannel = 'IV';
           oRTIInteractions.bDirectionSwitch = true;
           oRTIInteractions.populateCategory();
           oRTIInteractions.sSelectedChannel = 'DM';
           oRTIInteractions.populateCategory();
           oRTIInteractions.sSelectedChannel = 'Text';
           oRTIInteractions.populateCategory();
           oRTIInteractions.sSelectedChannel = 'Email';
           oRTIInteractions.populateCategory();
           oRTIInteractions.sSelectedChannel = 'PRINT';
           oRTIInteractions.populateCategory();
           oRTIInteractions.sCategoryValue= 'All';
           oRTIInteractions.sStartDate = '';
           oRTIInteractions.sEndDate = '';
           oRTIInteractions.search();
           oRTIInteractions.sInteractionWithType ='';
           List<SelectOption> channelList  = oRTIInteractions.channelList ;
           oRTIInteractions.resetChannel();
           Continuation objCon = New Continuation(120);
           objCon.continuationMethod='processResponse';
           oRTIInteractions.processResponse();
           system.assert(oRTIInteractions!=null);
           oRTIInteractions.getLast();
           oRTIInteractions.getPrevious();
           oRTIInteractions.getNext();
           oRTIInteractions.getFirst();
           oRTIInteractions.getLastRecordOf_Page();
           oRTIInteractions.getFirstRecordOf_Page();
           oRTIInteractions.getTotal_SizeRecord();
           HttpResponse oResponse = New HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           
           String sJson ='{"InteractionSummaryResponse": {"InteractionSummaryResult":{"Interactions":[{"Interactionkey":'+
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
            'BER", "interactingWithCode":"M" } },  { "Interactionkey":"5242091056e94921cdb2a3f17eb9bad4", "Interact'+
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
            'e": "7202090013", "TrackingID": "55835020"}}]} } }';
                
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            oRTIInteractions.sSelectedChannel='All';
            oRTIInteractions.responseparse(oResponse);
            system.assert(oRTIInteractions!=null);
            
           Test.stopTest();  
        }
        
    }
 
    /*
     * Method Name : runNegativeData
     * Description : Used to run negative scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runNegativeData()
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
           
           //Test.setCurrentPage(Page.RTISummary_VF_HUM);
           ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
           System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
                   
           RTIInteraction_C_HUM oRTIInteractions = new RTIInteraction_C_HUM();
           oRTIInteractions.sEnterpriseId = '';
           oRTIInteractions.bIsPageOnLoad = true;
           oRTIInteractions.search();
           String sJson1 ='{ "InteractionSummaryResponse":{ "InteractionSummaryResult":{ "nbRecords":0, "Interactions":[  ] } } }';
           HttpResponse oResponse1 = New HttpResponse();
           oResponse1.setHeader('Content-Type', 'application/json');
           oResponse1.setHeader('SOAPAction', '');    
           oResponse1.setHeader('Connection','keep-alive');
           oResponse1.setBody(sJson1);
           oResponse1.setStatusCode(400); 
           oRTIInteractions.responseparse(oResponse1);
           String sJson ='{ "InteractionSummaryResponse":{ "InteractionSummaryResult":{ "nbRecords":0, "Interactions":[  ] } } }';
           HttpResponse oResponse = New HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200); 
           oRTIInteractions.responseparse(oResponse);
           system.assert(oRTIInteractions!=null);
           Test.stopTest();  
        }
        
    }  

    /*
     * Method Name : checkStatusTest
     * Description : Used to test the check status in RTIApplicationChannelValues_H_HUM class
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void checkStatusTest()
    {   
        //Make Data
        insert new RTI_Switch__c(Name='All', Parent_Attribute_Value__c = 'DM', IsEnabled__c = true);
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
            Test.startTest();
            Boolean b = RTIApplicationChannelValues_H_HUM.checkstaus('All');
            System.assertEquals(b, true);
            Test.stopTest();
        }
        
    }

    /*
     * Method Name : checkStatusTestNegative
     * Description : Used to test the check status in RTIApplicationChannelValues_H_HUM class
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void checkStatusTestNegative()
    {   
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
            Test.startTest();
            Boolean b = RTIApplicationChannelValues_H_HUM.checkstaus('All');
            System.assertEquals(b, false);
            Test.stopTest();
        }
        
    }   
}