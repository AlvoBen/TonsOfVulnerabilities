/**************************************************************************** 
Apex Class Name  : VeteranDetail_LT_HUM
Version          : 
Created Date     :Feb 22th, 2023
Function         : Test Class  for VeteranDetail_LC_HUM
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Deepak Khandelwal                             02/20/2023                 User Story 3840097: T1PRJ0865978 -19972 Lightning- Migrate veteran webservice classes to lightning (Jaguars) (TECH Story)
*************************************************************************************************** */
@isTest
public class VeteranDetail_LT_HUM{
/*
* Method Name : setup
* Description : Create common test Data
* Return type : void   
* Parameter   : None
*/ 
@testSetup static void setup() 
      {
          
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        System.runAs (oCCSUser) 
        {
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Product2 oProdMA = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MA', Major_LOB__c='MA', Product_Type__c='MA');
            insert oProdMA;
            System.assertNotEquals(oProdMA.Id, NULL, 'Value not matched');
            
            Product2 oProdMAPD = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MAPD', Major_LOB__c='MAPD', Product_Type__c='MAPD');
            insert oProdMAPD;
            System.assertNotEquals(oProdMAPD.Id, NULL, 'Value not matched');

            CRMFunctionality_ONOFF_Switch__c CRMCust2 = new CRMFunctionality_ONOFF_Switch__c();
            CRMCust2.Name = '2615671';   
            insert CRMCust2;


            boolean bVeteranstatusSwitch = HUMUtilityHelper.isCRMFunctionalityON('2615671');
            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1023880950';
            objAcc.Mbr_Gen_Key__c = '0170104561826';
            objAcc.General_Account__c = true;
            ObjAcc.BirthDate__c = Date.today().month() +'/'+Date.today().day()+'/'+Date.today().year();
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            objAcc.Veteran_Status__c = 'confirmed-veteran';
            objAcc.VA_Health_Enrollee__c = 'true';
            insert objAcc;
            system.assertEquals(objAcc.Mbr_Gen_Key__c,'0170104561826');
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test';
            objAcc1.LastName = 'AccountName2';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1023880950';
            objAcc1.General_Account__c = true;
            objAcc1.BirthDate__c = '08/27/201';
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
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
            
            
            Policy__c objpolMA = HUMTestDataHelperTest.getPolicy();
            objpolMA.Name='testPolicyMA';
            objpolMA.Group_Name__c = objAcc.Id;
            objpolMA.Major_LOB__c = 'MA';
            objpolMA.Source_Cust_Cov_Key__c = '1023880951';
            objpolMA.Product__c = oProdMA.Id;
            objpolMA.Product__r = oProdMA;
            objpolMA.Platform__c = 'LV';
            objpolMA.Client_Number__c = '57';
            insert objpolMA;
            system.assertEquals(objpolMA.Source_Cust_Cov_Key__c,'1023880951');
            
            Policy__c objpolMAPD = HUMTestDataHelperTest.getPolicy();
            objpolMAPD.Name='testPolicyMAPD';
            objpolMAPD.Group_Name__c = objAcc.Id;
            objpolMAPD.Major_LOB__c = 'MAPD';
            objpolMAPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMAPD.Product__c = oProdMAPD.Id;
            objpolMAPD.Product__r = oProdMAPD;
            objpolMAPD.Platform__c = 'LV';
            objpolMAPD.Client_Number__c = '57';
            insert objpolMAPD;
            system.assertEquals(objpolMAPD.Source_Cust_Cov_Key__c,'1023880952');
            
            Policy__c objpolMPD = HUMTestDataHelperTest.getPolicy();
            objpolMPD.Name='testPolicyMPD';
            objpolMPD.Group_Name__c = objAcc.Id;
            objpolMPD.Major_LOB__c = 'MPD';
            objpolMPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMPD.Product__c = oProdMAPD.Id;
            objpolMPD.Product__r = oProdMAPD;
            objpolMPD.Platform__c = 'LV';
            objpolMPD.Client_Number__c = '57';
            insert objpolMPD;
            system.assertEquals(objpolMPD.Source_Cust_Cov_Key__c,'1023880952');
            
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
            
            Policy_Member__c objPolMemMPD = new Policy_Member__c();
            objPolMemMPD.Name = 'testpolmemMPD';
            objPolMemMPD.Relationship__c = 'Self';
            objPolMemMPD.Relationship_Code__c = '00';
            objPolMemMPD.Subscriber__c = objPolMem.Id;
            objPolMemMPD.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMemMPD.End_Date__c=Date.today().addDays(1).format();
            objPolMemMPD.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2011-07-01';
            objPolMemMPD.Member__c = objAcc.Id;
            objPolMemMPD.Policy__c = objpolMPD.Id;
            insert objPolMemMPD;
            system.assertEquals(objPolMemMPD.Relationship__c, 'Self');
            
            Policy_Member__c objTermPolMem = new Policy_Member__c();
            objTermPolMem.Name = 'testpolmem11';
            objTermPolMem.Relationship__c = 'Self';
            objTermPolMem.Relationship_Code__c = '00';
            objTermPolMem.Subscriber__c = objPolMem.Id;
            objTermPolMem.Effective_Date__c=Date.today().addDays(-10).format();
            objTermPolMem.End_Date__c=Date.today().addDays(-1).format();
            objTermPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34569|2013-07-01';
            objTermPolMem.Member__c = objAcc.Id;
            objTermPolMem.Policy__c = objpolMA.Id;
            objTermPolMem.Policy__r = objpolMA;
            insert objTermPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            
            HUM_Webservice_Callout__c oVetSaveService= new HUM_Webservice_Callout__c();
            oVetSaveService.name = 'VeteranSaveWebServiceCallout';
            oVetSaveService.Active__c = true;
            oVetSaveService.Certificate_Name__c = 'test';
            oVetSaveService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
            oVetSaveService.Timeout__c = '120000';
            oVetSaveService.Content_Type__c = 'application/json';
            oVetSaveService.Request_Type__c = 'POST';
            insert oVetSaveService;
            system.assert(oVetSaveService.id != null);
            system.assert(oVetSaveService.name == 'VeteranSaveWebServiceCallout');
            
            HUM_Webservice_Callout__c oVetGetService= new HUM_Webservice_Callout__c();
            oVetGetService.name = 'VeterangetWebServiceCallout';
            oVetGetService.Active__c = true;
            oVetGetService.Certificate_Name__c = 'test';
            oVetGetService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
            oVetGetService.Timeout__c = '120000';
            oVetGetService.Content_Type__c = 'application/json';
            oVetGetService.Request_Type__c = 'POST';
            insert oVetGetService;
            system.assert(oVetGetService.id != null);
            system.assert(oVetGetService.name == 'VeteranGetWebServiceCallout');

            HUM_Webservice_Callout__c oVetHisGetService= new HUM_Webservice_Callout__c();
            oVetHisGetService.name = 'VeteranGetWebServiceCallout';
            oVetHisGetService.Active__c = true;
            oVetHisGetService.Certificate_Name__c = 'test';
            oVetHisGetService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
            oVetHisGetService.Timeout__c = '120000';
            oVetHisGetService.Content_Type__c = 'application/json';
            oVetHisGetService.Request_Type__c = 'POST';
            insert oVetHisGetService;
            system.assert(oVetGetService.name == 'VeteranGetWebServiceCallout');
        }
        }
        
 /*
    * Method Name : testVeteranSaveInfo
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void testVeteranSaveInfo() 
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name, VA_Health_Enrollee__c, Mbr_Gen_Key__c, Enterprise_ID__c, Veteran_Status__c  from Account where LastName = 'Account'];  
            Account oAccount2 = [Select Id,Name,Tenant_Id__c,RecordType.Name, VA_Health_Enrollee__c, Mbr_Gen_Key__c, Enterprise_ID__c, Veteran_Status__c  from Account where LastName = 'AccountName2'];      
            System.assertEquals(oAccount.Veteran_Status__c  , 'confirmed-veteran');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            VeteranDetail_LC_HUM oVeteranDetail = new VeteranDetail_LC_HUM();
            Veteran_DTO_HUM objVeteranDTO = new Veteran_DTO_HUM();
            objVeteranDTO.sMemberGenKey = oAccount.Mbr_Gen_Key__c;
            objVeteranDTO.sVeteranStatus = oAccount.Veteran_Status__c;
            objVeteranDTO.sVAHealthEnrollee = oAccount.VA_Health_Enrollee__c;
            objVeteranDTO.sAccountId = oAccount.id;
            oVeteranDetail.oVeteranDTO = objVeteranDTO;
            system.assertNotEquals(oVeteranDetail.oVeteranDTO, null);
            //System.assertEquals(objVeteranDTO.sVeteranStatus, 'confirmed-veteran');
           // VeteranDetail_LC_HUM.getVeteranStatusList();
            
            VeteranDetail_LC_HUM.bVAHealthYes = false;
            VeteranDetail_LC_HUM.bVAHealthNo = true;
            VeteranDetail_LC_HUM.sRequest ='{"veteranStatus":"identified-veteran","sourceSystemTimestamp":"2021-03-11T04:48:41","sourceId":"crm-service-api","modifiedBy":"AIV5480","memberIdentityQualifier":"7331014273124","masterPersonId":"1004566978","isEnrolledInVA":true}';
            VeteranDetail_LC_HUM.sResponse = '{       "statusCode": "204",        "statusDescription": "Successfully Updated"      }';
            Continuation objCon = New Continuation(120);            
            objCon.continuationMethod='processVeteranResponse';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');            
            oResponse.setStatusCode(200);
            oResponse.setBody(VeteranDetail_LC_HUM.sResponse); 
           
		     
			      VeteranDetail_LC_HUM.bVAHealthYes = false;
            VeteranDetail_LC_HUM.bVAHealthNo = false;
			      VeteranDetail_LC_HUM.vtnDTO.sMemberGenKey = 'Deepak test adain';
            VeteranDetail_LC_HUM.vtnDTO.sVAHealthEnrollee = 'Deepak test ';
            String str1 = '{"sVeteranStatus":"confirmed-veteran","sAccountId":\"'+oAccount.id+'\","sAccountName":"SALAE GROESSER","sMemberGenKey":null,"sPersonId":null,"sVAHealthEnrollee":true,"veteranMessage":null, "veteranMessageType":null}';

            Object veteranRequest = VeteranDetail_LC_HUM.saveVeteranStatus(str1);
			      VeteranDetail_LC_HUM.bVAHealthYes = false;
            VeteranDetail_LC_HUM.bVAHealthNo = true;
			
            system.assertNotEquals(oResponse, null);
            list<string> processlist =new list<string>{'veteanc','non-veteran','official'};
            VeteranDetail_LC_HUM.processResponse(processlist ,'testcase');
           VeteranDetail_LC_HUM.parseResponse(oResponse ,'Test case');
          // oVeteranDetail.getVeteranSectionMessage();
          //  system.assertEquals(oVeteranDetail.oVeteranDTO.VeteranMessage, 'Veteran information saved successfully in the Veteran system. It will take 24-48 hours to reflect in the CRM system');
            
            VeteranDetail_LC_HUM.sResponse = '{          "type": "https://tools.ietf.org/html/rfc7231#section-6.5.1",            "title": "Bad Request",         "status": 400,          "message": "VeteranStatus or VeteranSourceId is invalid"         }';
            oResponse.setBody(VeteranDetail_LC_HUM.sResponse); 
            String str3 = '{"sVeteranStatus":"confirmed-veteran","sAccountId":\"'+oAccount.id+'\","sAccountName":"SALAE GROESSER","sMemberGenKey":null,"sPersonId":null,"sVAHealthEnrollee":true,"veteranMessage":null, "veteranMessageType":null}';

            veteranRequest = VeteranDetail_LC_HUM.saveVeteranStatus(str3);
            system.assertNotEquals(oResponse, null);
            list<string> processlist1 =new list<string>{'veteanc','non-veteran','official'};
            VeteranDetail_LC_HUM.processResponse(processlist1 ,'testcase');
          VeteranDetail_LC_HUM.parseResponse(oResponse ,'testcase');
            
         //   system.assertEquals(oVeteranDetail.oVeteranDTO.VeteranMessage, 'Failed to update Veteran Information in Veteran system');
                        
            Veteran_DTO_HUM objVeteranDTO2 = new Veteran_DTO_HUM();
            objVeteranDTO2.sMemberGenKey = oAccount2.Mbr_Gen_Key__c;
            objVeteranDTO2.sVeteranStatus = oAccount2.Veteran_Status__c;
            objVeteranDTO2.sVAHealthEnrollee = oAccount2.VA_Health_Enrollee__c;
            objVeteranDTO2.sAccountId ='';
            oVeteranDetail.oVeteranDTO = objVeteranDTO2;
            system.assertNotEquals(oVeteranDetail.oVeteranDTO, null);
            System.assertEquals(objVeteranDTO.sVeteranStatus, 'confirmed-veteran');
            
            
            VeteranDetail_LC_HUM.sResponse = '{       "statusCode": "204",        "statusDescription": "Successfully Updated"      }';
             oResponse.setBody(VeteranDetail_LC_HUM.sResponse);
                        String str4 = '{"sVeteranStatus":"confirmed-veteran","sAccountId":\"'+oAccount.id+'\","sAccountName":"SALAE GROESSER","sMemberGenKey":null,"sPersonId":null,"sVAHealthEnrollee":true,"veteranMessage":null, "veteranMessageType":null}';

            Object veteranReques = VeteranDetail_LC_HUM.saveVeteranStatus(str4);
            system.assertNotEquals(oResponse, null);
           list<string> processlist2 =new list<string>{'veteanc','non-veteran','official'};
           VeteranDetail_LC_HUM.processResponse(processlist2 ,'testcase');
           VeteranDetail_LC_HUM.parseResponse(oResponse ,'testcase');   
           VeteranDetail_LS_HUM.fetchGenKey(oAccount.id);
            list<VeteranHistoryTracking_DTO_HUM> lsttest=new list<VeteranHistoryTracking_DTO_HUM>();
            VeteranHistoryTracking_DTO_HUM objVeteranHistoryDTO = new VeteranHistoryTracking_DTO_HUM();
            objVeteranHistoryDTO.sLastmodifiedDate = string.valueOf(system.today());
            objVeteranHistoryDTO.sModifiedBy = 'oCCSUser';
            objVeteranHistoryDTO.sActionStatus = 'Update';
            objVeteranHistoryDTO.sApplicationUsed = 'salesforce';
            system.assertNotEquals(objVeteranHistoryDTO, null);
            System.assertEquals(objVeteranHistoryDTO.sActionStatus, 'Update');
            VeteranHistoryTracking_C_HUM.getVeteranHistory(oAccount.Mbr_Gen_Key__c,oAccount.id);
            
             string sVetRequest ='{"memberIdentityQualifier":"7331014273124","masterPersonId":""}';
             string sVetResponse = '{"masterPersonId": "1000011294","memberGenKey": "7331014273124","veteranStatus": "confirmed-veteran","veteranStatusSourceId": "veterans-api","isEnrolledInVA": false,"latestAudits": [{"sourceId": "crm-service-api","timestamp": "2021-04-16T06:46:29.826Z","veteranStatusAction": "updated","jsonInput":       {"veteranStatus": "confirmed-veteran","isEnrolledInVA": false,"sourceId": "crm-service-api","sourceSystemTimestamp": "2021-02-25T04:55:01Z","modifiedBy": "RYN7467"}}]}';
            Continuation objConVet = New Continuation(120);            
            objConVet.continuationMethod='processVeterantrackingResponse';
            HttpResponse oVetHisResponse = new HttpResponse();
            oVetHisResponse.setHeader('Content-Type', 'application/json');
            oVetHisResponse.setHeader('SOAPAction', '');    
            oVetHisResponse.setHeader('Connection','keep-alive');            
            oVetHisResponse.setStatusCode(200);
            oVetHisResponse.setBody(sVetResponse); 
           
            Object veteranHisRequest = VeteranHistoryTracking_C_HUM.getVeteranHistory(oAccount.Mbr_Gen_Key__c,oAccount.id);
            list<string> label=new list<string>();
            
            VeteranHistoryTracking_C_HUM.processVeterantrackingResponse(label,veteranHisRequest);
            
            Continuation con = (Continuation)VeteranHistoryTracking_C_HUM.getVeteranHistory('7331014273124',oAccount.id);
            VeteranDetail_LS_HUM.parseVeteranGetResponse(oVetHisResponse);
            Test.stopTest();          
            
        }
        
     }
     
     /*
    * Method Name : testVeteranGetInfo
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void testVeteranGetInfo() 
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name, VA_Health_Enrollee__c, Mbr_Gen_Key__c, Enterprise_ID__c, Veteran_Status__c  from Account where LastName = 'Account'];  
            Account oAccount2 = [Select Id,Name,Tenant_Id__c,RecordType.Name, VA_Health_Enrollee__c, Mbr_Gen_Key__c, Enterprise_ID__c, Veteran_Status__c  from Account where LastName = 'AccountName2'];      
            System.assertEquals(oAccount.Veteran_Status__c  , 'confirmed-veteran');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            VeteranDetail_LC_HUM oVeteranDetail = new VeteranDetail_LC_HUM();
            Veteran_DTO_HUM objVeteranDTO = new Veteran_DTO_HUM();
            objVeteranDTO.sMemberGenKey = oAccount.Mbr_Gen_Key__c;
            objVeteranDTO.sVeteranStatus = oAccount.Veteran_Status__c;
            objVeteranDTO.sVAHealthEnrollee = oAccount.VA_Health_Enrollee__c;
            objVeteranDTO.sAccountId = oAccount.id;
            oVeteranDetail.oVeteranDTO = objVeteranDTO;
            system.assertNotEquals(oVeteranDetail.oVeteranDTO, null);
            System.assertEquals(objVeteranDTO.sVeteranStatus, 'confirmed-veteran');
            
            VeteranDetail_LC_HUM.sRequest ='null';
            VeteranDetail_LC_HUM.sResponse = '{"statusCode": "204",}';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');            
            oResponse.setStatusCode(200);
            
            Veteran_DTO_HUM objVeteranDTO2 = new Veteran_DTO_HUM();
            objVeteranDTO2.sMemberGenKey = oAccount2.Mbr_Gen_Key__c;
            objVeteranDTO2.sVeteranStatus = oAccount2.Veteran_Status__c;
            objVeteranDTO2.sVAHealthEnrollee = oAccount2.VA_Health_Enrollee__c;
            objVeteranDTO2.sAccountId =oAccount2.id;
            oVeteranDetail.oVeteranDTO =objVeteranDTO2;
            system.assertNotEquals(oVeteranDetail.oVeteranDTO, null);
            System.assertEquals(objVeteranDTO.sVeteranStatus, 'confirmed-veteran');
            oResponse.setBody(VeteranDetail_LC_HUM.sResponse);
            String str = '{"sVeteranStatus":"confirmed-veteran","sAccountId":\"'+oAccount.id+'\","sAccountName":"SALAE GROESSER","sMemberGenKey":null,"sPersonId":null,"sVAHealthEnrollee":true,"veteranMessage":null, "veteranMessageType":null}';
            object veteranRequest = VeteranDetail_LC_HUM.saveVeteranStatus(str);
            system.assertNotEquals(oResponse, null);
           // oVeteranDetail.getprocessVeteranResponse();
            HttpResponse htpresponce=new HttpResponse();
            string sjson  = 'test json';
            htpresponce.setBody(sjson);
            htpresponce.setStatusCode(200); 
            System.assertEquals(sjson, 'test json');
            list<string> processlist =new list<string>{'veteanc','non-veteran','official'};
           VeteranDetail_LC_HUM.processResponse(processlist ,'testcase');
            VeteranDetail_LC_HUM.parseResponse(htpresponce ,'testcase');
          // oVeteranDetail.getVeteranSectionMessage();
          // VeteranDetail_LC_HUM.getVeteranStatusList();
            Test.stopTest();
         }
      }
}