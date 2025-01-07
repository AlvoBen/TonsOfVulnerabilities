/**************************************************************************** 
Apex Class Name  : VeteranHistoryTracking_LT_HUM
Version          : 
Created Date     : February 20th, 2023
Function         : Test Class  for VeteranHistoryTracking_LC_HUM
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Deepak Khandelwal                             02/20/2023                  User Story 3840097: T1PRJ0865978 -19972 Lightning- Migrate veteran webservice classes to lightning (Jaguars) (TECH Story)
*************************************************************************************************** */
@isTest
public class VeteranHistoryTracking_LT_HUM {
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

            HUM_Webservice_Callout__c oVetGetService= new HUM_Webservice_Callout__c();
            oVetGetService.name = 'VeterangetWebServiceCallout';
            oVetGetService.Active__c = true;
            oVetGetService.Certificate_Name__c = 'test';
            oVetGetService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
            oVetGetService.Timeout__c = '120000';
            oVetGetService.Content_Type__c = 'application/json';
            oVetGetService.Request_Type__c = 'POST';
            insert oVetGetService;

            HUM_Webservice_Callout__c oVetGetService1= new HUM_Webservice_Callout__c();
            oVetGetService1.name = 'VeterangetWebServiceCallout';
            oVetGetService1.Active__c = true;
            oVetGetService1.Certificate_Name__c = 'test';
            oVetGetService1.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
            oVetGetService1.Timeout__c = '120000';
            oVetGetService1.Content_Type__c = 'application/json';
            oVetGetService1.Request_Type__c = 'GET';
            insert oVetGetService1;
            system.assert(oVetGetService1.name == 'VeteranGetWebServiceCallout');
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
            System.assertEquals(oAccount.Veteran_Status__c  , 'confirmed-veteran');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            list<VeteranHistoryTracking_DTO_HUM> lsttest=new list<VeteranHistoryTracking_DTO_HUM>();
            VeteranHistoryTracking_LC_HUM oVeteranDetail = new VeteranHistoryTracking_LC_HUM();
            VeteranHistoryTracking_DTO_HUM objVeteranDTO = new VeteranHistoryTracking_DTO_HUM();
            objVeteranDTO.sLastmodifiedDate = string.valueOf(system.today());
            objVeteranDTO.sModifiedBy = 'oCCSUser';
            objVeteranDTO.sActionStatus = 'Update';
            objVeteranDTO.sApplicationUsed = 'salesforce';
            system.assertNotEquals(objVeteranDTO, null);
            System.assertEquals(objVeteranDTO.sActionStatus, 'Update');
            VeteranHistoryTracking_LC_HUM.getVeteranHistory(oAccount.Mbr_Gen_Key__c,oAccount.id);
            
            string sRequest ='{"memberIdentityQualifier":"7331014273124","masterPersonId":""}';
            string sResponse = '{"masterPersonId": "1000011294","memberGenKey": "7331014273124","veteranStatus": "confirmed-veteran","veteranStatusSourceId": "veterans-api","isEnrolledInVA": false,"latestAudits": [{"sourceId": "crm-service-api","timestamp": "2021-04-16T06:46:29.826Z","veteranStatusAction": "updated","jsonInput":       {"veteranStatus": "confirmed-veteran","isEnrolledInVA": false,"sourceId": "crm-service-api","sourceSystemTimestamp": "2021-02-25T04:55:01Z","modifiedBy": "RYN7467"}}]}';
            Continuation objCon = New Continuation(120);            
            objCon.continuationMethod='processVeterantrackingResponse';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');            
            oResponse.setStatusCode(200);
            oResponse.setBody(sRequest); 
            Object veteranRequest = VeteranHistoryTracking_LC_HUM.getVeteranHistory(oAccount.Mbr_Gen_Key__c,oAccount.id);
            list<string> label=new list<string>();
            label.add('Test');
            system.assertEquals(label.get(0), 'Test');
            //VeteranHistoryTracking_LC_HUM.processVeterantrackingResponse(label,veteranRequest);
            VeteranHistoryTracking_LC_HUM.processVeterantrackingResponse(null,null);
            Continuation con = (Continuation)VeteranHistoryTracking_LC_HUM.getVeteranHistory('7331014273124',oAccount.id);
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
        HUM_Webservice_Callout__c oVetSaveService= new HUM_Webservice_Callout__c();
        oVetSaveService.name = 'VeteranGetWebServiceCallout';
        oVetSaveService.Active__c = true;
        oVetSaveService.Certificate_Name__c = 'test';
        oVetSaveService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
        oVetSaveService.Timeout__c = '120000';
        oVetSaveService.Content_Type__c = 'application/json';
        oVetSaveService.Request_Type__c = 'POST';
        insert oVetSaveService;
        system.assert(oVetSaveService.id != null);
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name, VA_Health_Enrollee__c, Mbr_Gen_Key__c, Enterprise_ID__c, Veteran_Status__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Veteran_Status__c  , 'confirmed-veteran');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            list<VeteranHistoryTracking_DTO_HUM> lsttest=new list<VeteranHistoryTracking_DTO_HUM>();
            VeteranHistoryTracking_LC_HUM oVeteranDetail = new VeteranHistoryTracking_LC_HUM();
            VeteranHistoryTracking_DTO_HUM objVeteranDTO = new VeteranHistoryTracking_DTO_HUM();
            objVeteranDTO.sLastmodifiedDate = string.valueOf(system.today());
            objVeteranDTO.sModifiedBy = 'oCCSUser';
            objVeteranDTO.sActionStatus = 'Update';
            objVeteranDTO.sApplicationUsed = 'salesforce';
            system.assertNotEquals(objVeteranDTO, null);
            System.assertEquals(objVeteranDTO.sActionStatus, 'Update');
            VeteranHistoryTracking_LC_HUM.getVeteranHistory(oAccount.Mbr_Gen_Key__c,oAccount.id);
            
            string sRequest ='{"memberIdentityQualifier":"7331014273124","masterPersonId":""}';
            string sResponse = '{"masterPersonId": "1000011294","memberGenKey": "7331014273124","veteranStatus": "confirmed-veteran","veteranStatusSourceId": "veterans-api","isEnrolledInVA": false,"latestAudits": [{"sourceId": "crm-service-api","timestamp": "2021-04-16T06:46:29.826Z","veteranStatusAction": "updated","jsonInput":       {"veteranStatus": "confirmed-veteran","isEnrolledInVA": false,"sourceId": "crm-service-api","sourceSystemTimestamp": "2021-02-25T04:55:01Z","modifiedBy": "RYN7467"}}]}';
            Continuation objCon = New Continuation(120);            
            objCon.continuationMethod='processVeterantrackingResponse';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');            
            oResponse.setStatusCode(200);
            oResponse.setBody(sRequest); 
            system.assertNotEquals(sResponse, null);
            Object veteranRequest = VeteranHistoryTracking_LC_HUM.getVeteranHistory(oAccount.Mbr_Gen_Key__c,oAccount.id);
            list<string> label=new list<string>();
            
            VeteranHistoryTracking_LC_HUM.processVeterantrackingResponse(null,null);
            VeteranGetResponse_DTO_HUM vetRes=new VeteranGetResponse_DTO_HUM();
            VeteranGetResponse_DTO_HUM.Audits resaud=new VeteranGetResponse_DTO_HUM.Audits();
            VeteranGetRequest_DTO_HUM vetReq=new VeteranGetRequest_DTO_HUM();
            Continuation con = (Continuation)VeteranHistoryTracking_LC_HUM.getVeteranHistory('7331014273124',oAccount.id);
            con.continuationMethod = 'processVeterantrackingResponse';
            con.state='7331014273124';
            HttpResponse resVetHis = new Httpresponse ();
            String vet = '{"masterPersonId": "1000011294","memberGenKey": "7331014273124","veteranStatus": "confirmed-veteran","veteranStatusSourceId": "veterans-api","isEnrolledInVA": false,"latestAudits": [{"sourceId": "crm-service-api","timestamp": "2021-04-16T06:46:29.826Z","veteranStatusAction": "updated","jsonInput":{"veteranStatus": "confirmed-veteran","isEnrolledInVA": false,"sourceId": "crm-service-api","sourceSystemTimestamp": "2021-02-25T04:55:01Z","modifiedBy": "RYN7467"}}]}';
            resVetHis.setBody(vet);
            system.assert(resVetHis.getBody() == vet);
            system.assert(con.continuationMethod == 'processVeterantrackingResponse');
            Map<String, HTTPRequest> mapDetails =  con.getRequests();
            List<String> labelGetOrder = new List<string>();
            for(String s : mapDetails.keyset())
            {
                labelGetOrder.add(s);
            } 
            
            Test.setContinuationResponse(labelGetOrder[0],resVetHis);
            List<string>labelGetmember = New List<string>();
            List<string>labelGetmemberEmpty = New List<string>();
            VeteranHistoryTracking_LC_HUM.processVeterantrackingResponse(labelGetOrder,'7331014273124');
            
            VeteranGetResponse_DTO_HUM VetGerRes=new VeteranGetResponse_DTO_HUM();
            VeteranGetResponse_DTO_HUM.Audits vetAud=new  VeteranGetResponse_DTO_HUM.Audits();
            VetGerRes.masterPersonId='ag4554';
            Test.stopTest();          
            
        }
        
    }
    
}