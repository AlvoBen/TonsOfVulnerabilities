/****************************************************************************************
Apex Class Name  : HUMCaseServiceTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMCaseService Apex class
Modification Log :
 *  Developer                   Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Vishal Verma                  20294                12/23/2014              Original Version
 * SuryaKumari Medicherla        20579                01/28/2015              Code modified to fix test class failure
 * Ranjeeth Nagishetty           21167                02/26/2015              Modified test class as per REQ-81852
 * Avinash Choubey               33344                07/11/2016              Changes made as part of REQ- 255151
 * Pradeepkumar Dani             99570                06/08/2017              Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 * Pradeepkumar Dani            100996                06/13/2017              Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 * Saurabh Kaple                103457                06/10/2017              Created test method for CreateCase for REQ-318353  
 * Saurabh Kaple                112270                07/21/2017              REQ - 315928 - Creditable Coverage Intake - Case Service   
 * Santhi Mandava                                     27/03/2018              Removed comment body in SOQL where condition
 * Moshitha Gunasekaran         211055                04/04/2018              REQ - 359138 TECH - SF - CA Ticket 7119893 - Case Service 
 * Suresh Gottipati                                    7/31/2018              Created for map for Createaccount method with plain values of names.       
 * Sunil Bukya                                        12/26/2018              Removed Dpass callout code changes
 * Santhi Mandava               355559                05/10/2019              Removed "ENCRYPTIONTYPE" custom setting creation
 *******************************************************************************************/
@isTest
private class HUMCaseServiceTest
{
     
    
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
     {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);

        System.runAs(oUser) 
        {
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Enterprise_ID__c = '123456';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
             
            
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Affidavit';
           
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'New';
           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'New',true);
            
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = oAcc.Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.ID;
            caseObj.Intent_ID__c  = intentRec.id;
            caseobj.Interacting_With_Type__c = 'Group';
            insert caseObj ;
            System.assertEquals(caseobj.Interacting_With_Type__c == 'Group', true); 
            
            //for create case method
            Group testGroup = new group(name = 'Louisville RSO Enrollment' , type = 'Queue');
            insert testGroup;                     
            system.assertNotEquals(testGroup, null , 'Group  not created'); 
                      
            QueueSobject mappingObject = new QueueSobject(QueueId = testGroup.Id, SobjectType = 'Case');
            insert mappingObject;
            system.assertNotEquals(mappingObject, null, 'Value not matched');
             
            Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Louisville RSO Enrollment' , IsActive__c= true);
            insert oQSetup ;
            system.assertNotEquals(oQSetup, null, 'Value not matched');
            Queue_View__c oQv1 = new Queue_View__c(Queue_View_Name__c='Louisville RSO Enrollment', Department__c='Enrollment', Work_Queue_Name__c=oQSetup.Id); 
            insert oQv1;
        
            system.assertNotEquals(oQv1, null, 'Value not matched');
        
             HUMConstants__c test1  = new  HUMConstants__c();
             test1.name = 'UNKNOWN_MEMBER';
             test1.stringValue__C = 'Unknown Member';
                          
             insert test1;
             system.assertNotEquals(test1,null, 'Value not matched');
             
             List<Account> lstAccount = new  List<Account>();
             Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc1 = new Account( Enterprise_Id__c = '223300104', firstname = 'test2' ,lastname = 'method2' , Birthdate__c= '01/02/1990', ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc2 = new Account( Enterprise_Id__c = '002935049', firstname = 'test3', lastname = 'method3' ,Birthdate__c= '01/01/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc3 = new Account( Enterprise_Id__c = '002935049', firstname = 'test4', lastname = 'method4' ,Birthdate__c= '01/04/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc4 = new Account( Enterprise_Id__c = '223366104', firstname = 'test5' ,lastname = 'method5' , Birthdate__c= '01/01/1990',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc5 = new Account( Enterprise_Id__c = '223366104', firstname = 'test5' ,lastname = 'method5' , Birthdate__c= '01/01/1990',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc6 = new Account( Enterprise_Id__c = '223366104', firstname = 'test6' ,lastname = 'method6' , Birthdate__c= '01/01/1990',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc7 = new Account( Enterprise_Id__c = '', firstname = 'test7' ,lastname = 'method7' , Birthdate__c= '01/01/1997',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'KY', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId());
             Account acc8 = new Account( Enterprise_Id__c = '223366105', firstname = 'test8' ,lastname = 'method8' , Birthdate__c= '01/01/1997',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'KY', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             
             lstAccount.Add(acc);
             lstAccount.Add(acc1);
             lstAccount.Add(acc2);
             lstAccount.Add(acc3);
             lstAccount.Add(acc4);
             lstAccount.Add(acc5);
             lstAccount.Add(acc6);
             lstAccount.Add(acc7);
             lstAccount.Add(acc8);
             
             Insert lstAccount;
             system.assertNotEquals(lstAccount, null , 'Accounts not created');
                          
             list<Product2> listProduct = new List<Product2>();
             Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
             Product2 testProduct1 = new Product2(Name ='testprod2', ProductCode ='MED');
             Product2 testProduct2 = new Product2(Name ='testprod3', ProductCode ='MED');
             Product2 testProduct3 = new Product2(Name ='testprod4', ProductCode ='MED');
             Product2 testProduct4 = new Product2(Name ='testprod5', ProductCode ='MED');
             Product2 testProduct5 = new Product2(Name ='testprod6', ProductCode ='MED');
             Product2 testProduct6 = new Product2(Name ='testprod8', ProductCode ='MED');
             
             listProduct.Add(testProduct);
             listProduct.Add(testProduct1);
             listProduct.Add(testProduct2);
             listProduct.Add(testProduct3);
             listProduct.Add(testProduct4);
             listProduct.Add(testProduct5);
             listProduct.Add(testProduct6);
             
             Insert listProduct;
             system.assertNotEquals(listProduct, null , 'Products not created');
             
                          
             list<Policy__c> listPolicy = new List<Policy__c>();             
             Policy__c testPolicy = new Policy__c(Group_Name__c = lstAccount[0].ID, Major_LOB__c = 'MER', Product__c = listProduct[0].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy1 = new Policy__c(Group_Name__c = lstAccount[1].ID, Major_LOB__c = 'MCD', Product__c = listProduct[1].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy2 = new Policy__c(Group_Name__c = lstAccount[2].ID, Major_LOB__c = 'MEF', Product__c = listProduct[2].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy3 = new Policy__c(Group_Name__c = lstAccount[3].ID, Major_LOB__c = 'MEP', Product__c = listProduct[3].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy4 = new Policy__c(Group_Name__c = lstAccount[4].ID, Major_LOB__c = 'MES', Product__c = listProduct[4].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy5 = new Policy__c(Group_Name__c = lstAccount[5].ID, Major_LOB__c = 'MGP', Product__c = listProduct[5].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy6 = new Policy__c(Group_Name__c = lstAccount[6].ID, Major_LOB__c = 'MGP', Product__c = listProduct[5].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy7 = new Policy__c(Group_Name__c = lstAccount[8].ID, Major_LOB__c = 'MGP', Product__c = listProduct[6].ID,Policy_Group_Number__c='11111');
                     
             listPolicy.Add(testPolicy);
             listPolicy.Add(testPolicy1);
             listPolicy.Add(testPolicy2);
             listPolicy.Add(testPolicy3);
             listPolicy.Add(testPolicy4);
             listPolicy.Add(testPolicy5);
             listPolicy.Add(testPolicy6);
             listPolicy.Add(testPolicy7);
             
             insert listPolicy; 
             system.assertNotEquals(listPolicy, null , 'Policies not created');
             
             List<Policy_Member__c>  listPolicyMember = New List<Policy_Member__c>();
             Policy_Member__c memeber = New Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = lstAccount[0].ID , ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = system.today().adddays(365).format());
             Policy_Member__c memeber1 = New Policy_Member__c(Policy__c = listPolicy[1].ID, Member__c = lstAccount[1].ID , ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017', End_Date__c = '12/31/2017');
             Policy_Member__c memeber2 = New Policy_Member__c(Policy__c = listPolicy[2].ID, Member__c = lstAccount[2].ID, ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber3 = New Policy_Member__c(Policy__c = listPolicy[3].ID , Member__c = lstAccount[3].ID, ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber4 = New Policy_Member__c(Policy__c = listPolicy[4].ID , Member__c = lstAccount[4].ID, ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber5 = New Policy_Member__c(Policy__c = listPolicy[5].ID , Member__c = lstAccount[4].ID, ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber6 = New Policy_Member__c(Policy__c = listPolicy[6].ID , Member__c = lstAccount[6].ID, ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber7 = New Policy_Member__c(Policy__c = listPolicy[7].ID , Member__c = lstAccount[8].ID, ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
            
             listPolicyMember.Add(memeber);
             listPolicyMember.Add(memeber1);
             listPolicyMember.Add(memeber2);
             listPolicyMember.Add(memeber3);
             listPolicyMember.Add(memeber4);
             listPolicyMember.Add(memeber5);
             listPolicyMember.Add(memeber6);
             listPolicyMember.Add(memeber7);
             
             Insert listPolicyMember ;
             system.assertNotEquals(listPolicyMember, null ,  'Policies  not created for members');
             
             List<Member_ID__c> listmember = new List<Member_ID__c>();
             Member_ID__c member = new Member_ID__c(Policy__c = listPolicy[0].ID,Policy_Member__c = listPolicyMember[0].ID, Member_Card_External_ID__c= '123345' , name = '360306641' , Type__c ='SSN');
             Member_ID__c member1 = new Member_ID__c(Policy__c = listPolicy[1].ID,Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c= '123645' , name = '360306641' , Type__c ='SSN');
             Member_ID__c member2 = new Member_ID__c(Policy__c = listPolicy[2].ID,Policy_Member__c = listPolicyMember[2].ID, Member_Card_External_ID__c= '123245' , name = '002935049' , Type__c ='MedicareID');
             Member_ID__c member3 = new Member_ID__c(Policy__c = listPolicy[3].ID,Policy_Member__c = listPolicyMember[3].ID , Member_Card_External_ID__c= '123154', name = '002935049' , Type__c ='MedicareID');
             Member_ID__c member4 = new Member_ID__c(Policy__c = listPolicy[4].ID,Policy_Member__c = listPolicyMember[4].ID , Member_Card_External_ID__c= '123654', name = '567234444' , Type__c ='SSN');
             Member_ID__c member5 = new Member_ID__c(Policy__c = listPolicy[5].ID,Policy_Member__c = listPolicyMember[4].ID , Member_Card_External_ID__c= '123674', name = '567234444' , Type__c ='SSN');
             Member_ID__c member6 = new Member_ID__c(Policy__c = listPolicy[6].ID,Policy_Member__c = listPolicyMember[6].ID , Member_Card_External_ID__c= '123664', name = '567234224' , Type__c ='MedicareID');
             Member_ID__c member7 = new Member_ID__c(Policy__c = listPolicy[7].ID,Policy_Member__c = listPolicyMember[7].ID , Member_Card_External_ID__c= '123684', name = 'H46738930' , Type__c ='Member-Id-Base');
             
             listmember.Add(member);
             listmember.Add(member1);
             listmember.Add(member2);
             listmember.Add(member3);
             listmember.Add(member4);
             listmember.Add(member5);
             listmember.Add(member6);
             listmember.Add(member7);
            
             insert listmember;
             system.assertNotEquals(listmember, null , 'Members  not created'); 
             
                          
             List<HUMAccountCaseRecordTypes__c> HUMAccountCaseRecordTypesList = new List<HUMAccountCaseRecordTypes__c>();
             HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes = new HUMAccountCaseRecordTypes__c(Name = 'Unknown Member' ,Account_RecordType__c = 'Unknown Member' , Case_RecordType__c = 'Unknown Case');
             HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes1 = new HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
             HUMAccountCaseRecordTypesList.add(HUMAccountCaseRecordTypes); 
             HUMAccountCaseRecordTypesList.add(HUMAccountCaseRecordTypes1);         
             insert HUMAccountCaseRecordTypesList;
             system.assertNotEquals(HUMAccountCaseRecordTypesList, null , 'Recordtypes  not created');
             
             List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> MedEnrollmentSubTypesList = new List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c>();
             MEDENROLLMENT_CASE_SUBTYPE_HUM__c MedEnrollmentSubTypes = new MEDENROLLMENT_CASE_SUBTYPE_HUM__c(Name = 'Routine' ,SubType__c = 'Routine');
             MedEnrollmentSubTypesList.add(MedEnrollmentSubTypes);        
             insert MedEnrollmentSubTypesList ;
             system.assertNotEquals(MedEnrollmentSubTypesList, null ,  'MedEnrollmentSubTypesLists  not created');
                      
              
             List<HUMCaseInteractionAboutType__c > HUMCaseInteractionAboutTypesList = new List<HUMCaseInteractionAboutType__c>();
             HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes = new HUMCaseInteractionAboutType__c(Name = 'Unknown Member' ,CaseInteraction_About_Type__c = 'Unknown-Member' );
             HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes1 = new HUMCaseInteractionAboutType__c(Name = 'Member' ,CaseInteraction_About_Type__c = 'Member' );
             HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes);
             HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes1);        
             insert HUMCaseInteractionAboutTypesList;
             system.assertNotEquals(HUMCaseInteractionAboutTypesList, null ,'HUMCaseInteractionAboutTypesLists  not created');
                        
             Classification__c classfication = new Classification__c(name = 'Chronic Care SNP' ,Is_Active__c = true);
             insert classfication ;
             system.assertNotEquals(MedEnrollmentSubTypesList, null, 'classfication  not created');
            
             Classification_Type__c classificationType = new Classification_Type__c(name = 'Enrollment (RSO)', Is_Active__c = true);
             insert classificationType ;
             system.assertNotEquals(classificationType, null , 'classificationType  not created');
            
             Intent__c intent = new Intent__c(name = 'Denied' , Is_Active__c = true);
             insert intent ;
             system.assertNotEquals(intent, null ,'classificationType  not created');
            
             List<CTCI_Junction__c> listctciJunction = new List<CTCI_Junction__c>();
             CTCI_Junction__c ctciJunction  =  new CTCI_Junction__c(Classification__c =  classfication.ID, Classification_Type__c = classificationType .ID, Intent__c = intent .ID , IsActive__c = true);
             listctciJunction.Add(ctciJunction);
             insert listctciJunction ;
             system.assertNotEquals(intent, null ,'CTCIJunctionlist  not created');            
                        
             
             HUMConstants__c test2  = new  HUMConstants__c();
             test2.name = 'UNKNOWN_MEMBER_EXTID';
             insert test2;
             system.assertNotEquals(intent, null ,'Unknown member ext id  not created');
        }
    }
     
    /**
    * This is a test method for Get Case Info Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseService_GetCase()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User1',true);
        
        System.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            insert new HUMCipherCloudSettings__c( Name = 'APPLICATION_NAME' , Value__c = 'TEST');
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            gtCaseReqInfo.CaseId = objCase.CaseNumber;
            List<HUMEntityIdentifierHelper> lstInputInquireAboutCriteria = new List<HUMEntityIdentifierHelper>();
            HUMGetCaseServiceRequestHelper request = new HUMGetCaseServiceRequestHelper();
            request.getCaseInfo = true;
            request.caseInfoRequest = gtCaseReqInfo;
            request.IntAboutCaseAvailabilityCheck = lstInputInquireAboutCriteria;
            Test.startTest();
            HUMGetCaseServiceResponseHelper respHelper = HUMCaseService.getCaseInfo(request);
            Test.StopTest();
        }
    }
    
    /**
    * This is a test method for Get Case Info Service -HUMCaseService Negative Scenarios
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseService_GetCase_Negative()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id ,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User1',true);
        
        System.runAs(oUser) 
        {
            HUMGetCaseServiceRequestHelper request = new HUMGetCaseServiceRequestHelper();
            request.getCaseInfo = null;
            request.caseInfoRequest = null;
            request.IntAboutCaseAvailabilityCheck = null;
            Test.startTest();
            HUMGetCaseServiceResponseHelper respHelper = HUMCaseService.getCaseInfo(request);
            Test.StopTest();
        }
    }
    
    /**
    * This is a test method for Update Case Info Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseService_UpdateCase()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test User1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
            String caseStatus = 'C';
            
            HUMCaseUpdateLogAttachmentHelper request = new HUMCaseUpdateLogAttachmentHelper();
            request.sCaseNumber = caseNumber;
            request.sCaseStatus = caseStatus;
            request.sComment = ' New Comment';
            request.sOwnerId = oUser.Id;
            
            List<HUMCaseUpdateAttachmentDTO> listAttachmentDTO = new List<HUMCaseUpdateAttachmentDTO>();
            
            HUMCaseUpdateAttachmentDTO attachmentDTO1 = new HUMCaseUpdateAttachmentDTO();
            attachmentDTO1.sFileName = 'New Attachment';
            attachmentDTO1.sAttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);
            
            HUMCaseUpdateAttachmentDTO attachmentDTO2 = new HUMCaseUpdateAttachmentDTO();
            listAttachmentDTO.add(attachmentDTO2);
            
            request.lstAttachmentDTO = listAttachmentDTO; 
            
            HUMLogAttachmentRequestDTO  logAttachemntRequest = new HUMLogAttachmentRequestDTO();
            
            List<HumAttachmentAttributeRequestDTO> listAttachemntAttribute = new List<HumAttachmentAttributeRequestDTO>();
            
            HumAttachmentAttributeRequestDTO attachemntAttribute1 = new HumAttachmentAttributeRequestDTO();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            HumAttachmentAttributeRequestDTO attachemntAttribute2 = new HumAttachmentAttributeRequestDTO();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest.AttachmentAttribute = listAttachemntAttribute;
            logAttachemntRequest.CreatedBy = oUser.Id;
            logAttachemntRequest.AttachmentDescription = 'Description';
            logAttachemntRequest.AttachmentKey = 'Key';
            logAttachemntRequest.AttachmentType = 'Type';
            
            request.attachmentDTO = logAttachemntRequest;
            HUMUpdateCaseServiceRequestHelper updateServiceRequest = new HUMUpdateCaseServiceRequestHelper();
            updateServiceRequest.CaseUpdateRequest = request;
            
            Test.StartTest();               
            HUMUpdateCaseServiceResponseHelper response =  HUMCaseService.updateCaseInfo(updateServiceRequest);
            system.assert(response.updateServiceResponse.bStatus == true);
            Test.StopTest();
        }
    }  
        
    /**
    * This is a test method for Create Case  Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseService_CreateCase()
    {
        
       HUMTestDataHelperTest.getHUMConstantsData();       
       User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname == 'test User1',true);
        
        
        System.runAs(oUser) 
        {
                        
             HUMCreateCaseRequestHelper request = new HUMCreateCaseRequestHelper ();
            
             HUMCreateCaseServiceRequestHelper createServiceRequest = new HUMCreateCaseServiceRequestHelper();
             createServiceRequest.createCaseRequest= request;
             
                  request.bAsynchronousProcess = true;
                  request.sSourceApp ='AE';
                  request.sSourceKey = 'test';
                  request.sInteractingAboutId = '002935049';
                  request.sInteractingAboutIdType = 'MedicareID';
                  request.sInteractingAboutType = 'Member';
                  request.sInteractingAboutFirstName = 'test3';
                  request.sInteractingAboutLastName = 'method3';
                  request.sInteractingAboutDOB = '01/01/1990';
                  request.sInteractingWithId = '002935049';
                  request.sInteractingWithIdType ='MedicareID';
                  request.sInteractingWithType = 'Member';
                  request.sInteractingWithFirstName = 'test3';
                  request.sInteractingWithLastName = 'method3';
                  request.sInteractingWithDOB = '01/01/1990';
                  request.sOrigin = 'AEDUASNP';
                  request.sType = 'Customer Inquiry';
                  request.sCaseSubType = 'Routine';
                  request.sStatus = 'Closed';
                  request.sCreatedQueue = 'Louisville RSO Enrollment';
                  request.sOwnerQueue = 'Louisville RSO Enrollment';
                
                  request.sServiceCenter = 'testcenter';
                  request.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request.sClassificationType = 'Enrollment (RSO)';
                  request.sPriority = 'Normal';
                  request.sComplaint = 'test';
                  request.sComplaintReason = 'test';
                  request.sComplaintType = 'test';
                  
                  request.sGARightsGiven ='yes' ;
                  request.sGAReason = 'test';
                  request.sFollowupDueDate = '10/01/2017';
                  request.sClassification = 'Chronic Care SNP';
                  request.sIntent = 'Denied';  
                  request.sDocType= 'pdf';
                  request.sDCN= 'test';
                  
                  List<HUMCaseCommentRequestDTO> listCommentDTO = new List<HUMCaseCommentRequestDTO>();
                  HUMCaseCommentRequestDTO commentDTO = new HUMCaseCommentRequestDTO();
                  commentDTO.CommentBody  = 'New comment';
                  listCommentDTO.add(commentDTO);                                   
                  request.CaseCommentDTO  = listCommentDTO;                           
                               
                  request.sClosedDate = '10/10/2017';
                  request.sContract = 'test';
                  request.sExternalTrackId = '1234';
                  request.sInteractingAboutAddressLine1 = '1100 employers blvd';
                  request.sInteractingAboutCity = 'depere';
                  request.sInteractingAboutEmailID = 'test@humana.com';
                  request.sInteractingAboutHomePhone = '9203375562';
                  request.sInteractingAboutState = 'WI';
                  request.sInteractingAboutZipCode = '54115';                  
                  request.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request.sInteractingWithCity = 'de pere';
                  request.sInteractingWithEmailID = 'test@humana.com';
                  request.sInteractingWithHomePhone = '9203375562';
                  request.sInteractingWithState = 'WI';
                  request.sInteractingWithZipCode = '54115';                    
                  request.sMbrSignDate = '06/05/2017';
                  request.sMemberSSN = '360306641';
                  request.sMilestoneId = '123';
                  request.sPBP = 'test';
                  request.sPendKey = 'test';
                  request.sSegment = 'test';
                  request.sSourceIdTypeCd = 'test';  
                  request.sTopicName = 'test topic';
                  request.sWorkQueueViewName = 'Other';
                  request.sFollowupDueDate = '10/1/2017';
                  request.sRecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case'); 
                    
                  //new request
                  HUMCreateCaseRequestHelper request1 = new HUMCreateCaseRequestHelper (); 
                  request1.sSourceApp ='AE';
                  request1.sSourceKey = 'test';
                  request1.sInteractingAboutId = '223338104';
                  request1.sInteractingAboutIdType = 'SSN';
                  request1.sInteractingAboutType = 'Member';
                  request1.sInteractingAboutFirstName = 'test5';
                  request1.sInteractingAboutLastName = 'method5';
                  request1.sInteractingAboutDOB = '01/10/1990';
                  request1.sInteractingWithId = '002978049';
                  request1.sInteractingWithIdType ='Medicaid-Id';
                  request1.sInteractingWithType = 'Member';
                  request1.sInteractingWithFirstName = 'test10';
                  request1.sInteractingWithLastName = 'method10';
                  request1.sInteractingWithDOB = '01/10/1991';
                  request1.sOrigin = 'AEDUASNP';
                  request1.sType = 'Customer Inquiry';
                  request1.sCaseSubType = 'Routine';
                  request1.sStatus = 'Closed';
                  request1.sCreatedQueue = 'Louisville RSO Enrollment';
                  request1.sOwnerQueue = 'Louisville RSO Enrollment';
                  request1.sServiceCenter = 'testcenter';
                  request1.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request1.sClassificationType = 'Enrollment (RSO)';
                  request1.sPriority = 'Normal';
                  request1.sComplaint = 'test';
                  request1.sComplaintReason = 'test';
                  request1.sComplaintType = 'test';                  
                  request1.sGARightsGiven ='yes' ;
                  request1.sGAReason = 'test';
                  request1.sFollowupDueDate = '10/01/2017';
                  request1.sClassification = 'Chronic Care SNP';
                  request1.sIntent = 'Denied';  
                  request1.sDocType= 'pdf';
                  request1.sDCN= 'test';
                  request1.CaseCommentDTO  = listCommentDTO; 
                  request1.sContract = 'test';
                  request1.sExternalTrackId = '1234';
                  request1.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request1.sInteractingAboutCity = 'depere';
                  request1.sInteractingAboutEmailID = 'test@humana.com';
                  request1.sInteractingAboutHomePhone = '9203375562';
                  request1.sInteractingAboutState = 'WI';
                  request1.sInteractingAboutZipCode = '54115';                  
                  request1.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request1.sInteractingWithCity = 'de pere';
                  request1.sInteractingWithEmailID = 'test@humana.com';
                  request1.sInteractingWithHomePhone = '9203375562';
                  request1.sInteractingWithState = 'WI';
                  request1.sInteractingWithZipCode = '54115';                    
                  request1.sMbrSignDate = '06/05/2017';
                  request1.sMemberSSN = '567234444';
                  request1.sMilestoneId = '123';
                  request1.sPBP = 'test';
                  request1.sPendKey = 'test';
                  request1.sSegment = 'test';
                  request1.sSourceIdTypeCd = 'test';  
                  request1.sTopicName = 'test topic';
                  request1.sWorkQueueViewName = 'Other';
                  request1.bAsynchronousProcess = true;
                
                  //new request
                  HUMCreateCaseRequestHelper request2 = new HUMCreateCaseRequestHelper (); 
                  request2.sSourceApp ='AE';
                  request2.bAsynchronousProcess= true;
                  request2.sSourceKey = 'test';
                  request2.sInteractingAboutId = '223366104';
                  request2.sInteractingAboutIdType = 'EnterprisePersonID';
                  request2.sInteractingAboutType = 'Member';
                  request2.sInteractingAboutFirstName = 'test5';
                  request2.sInteractingAboutLastName = 'method5';
                  request2.sInteractingAboutDOB = '01/01/1990';
                  request2.sInteractingWithId = '';
                  request2.sInteractingWithIdType ='Member-Id-Base';
                  request2.sInteractingWithType = 'Member';
                  request2.sInteractingWithFirstName = 'test55';
                  request2.sInteractingWithLastName = 'method55';
                  request2.sInteractingWithDOB = '01/01/1990';
                  request2.sOrigin = 'AEDUASNP';
                  request2.sType = 'Customer Inquiry';
                  request2.sCaseSubType = 'Routine';
                  request2.sStatus = 'Closed';
                  request2.sCreatedQueue = 'Louisville RSO Enrollment';
                  request2.sOwnerQueue = 'Louisville RSO Enrollment';                
                  request2.sServiceCenter = 'testcenter';
                  request2.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request2.sClassificationType = 'Enrollment (RSO)';
                  request2.sPriority = 'Normal';
                  request2.sComplaint = 'test';
                  request2.sComplaintReason = 'test';
                  request2.sComplaintType = 'test';                  
                  request2.sGARightsGiven ='yes' ;
                  request2.sGAReason = 'test';
                  request2.sFollowupDueDate = '10/01/2017';
                  request2.sClassification = 'Chronic Care SNP';
                  request2.sIntent = 'Denied';  
                  request2.sDocType= 'pdf';
                  request2.sDCN= 'test';                
                                     
                  request2.CaseCommentDTO  = listCommentDTO; 
                  request2.sContract = 'test';
                  request2.sExternalTrackId = '1234';
                  request2.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request2.sInteractingAboutCity = 'depere';
                  request2.sInteractingAboutEmailID = 'test@humana.com';
                  request2.sInteractingAboutHomePhone = '9203375562';
                  request2.sInteractingAboutState = 'WI';
                  request2.sInteractingAboutZipCode = '54115';                  
                  request2.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request2.sInteractingWithCity = 'de pere';
                  request2.sInteractingWithEmailID = 'test@humana.com';
                  request2.sInteractingWithHomePhone = '9203375562';
                  request2.sInteractingWithState = 'WI';
                  request2.sInteractingWithZipCode = '54115';                    
                  request2.sMbrSignDate = '06/05/2017';
                  request2.sMemberSSN = '567234444';
                  request2.sMilestoneId = '123';
                  request2.sPBP = 'test';
                  request2.sPendKey = 'test';
                  request2.sSegment = 'test';
                  request2.sSourceIdTypeCd = 'test';  
                  request2.sTopicName = 'test topic';
                  request2.sWorkQueueViewName = 'Other';
                                 
                  //New request
                  HUMCreateCaseRequestHelper request3 = New HUMCreateCaseRequestHelper(); 
                  request3.bAsynchronousProcess = true;
                  request3.sSourceApp ='AE';
                  request3.sSourceKey = 'test';
                  request3.sInteractingAboutId = '223366104';
                  request3.sInteractingAboutIdType = 'Medicaid-Id';
                  request3.sInteractingAboutType = 'Member';
                  request3.sInteractingAboutFirstName = 'test5';
                  request3.sInteractingAboutLastName = 'method5';
                  request3.sInteractingAboutDOB = '01/01/1990';
                  request3.sInteractingWithId = '223355104';
                  request3.sInteractingWithIdType ='SSN';
                  request3.sInteractingWithType = 'Member';
                  request3.sInteractingWithFirstName = 'test55';
                  request3.sInteractingWithLastName = 'method55';
                  request3.sInteractingWithDOB = '01/01/1990';
                  request3.sOrigin = 'AEDUASNP';
                  request3.sType = 'Customer Inquiry';
                  request3.sCaseSubType = 'Routine';
                  request3.sStatus = 'Closed';
                  request3.sCreatedQueue = 'Louisville RSO Enrollment';
                  request3.sOwnerQueue = 'Louisville RSO Enrollment';                
                  request3.sServiceCenter = 'testcenter';
                  request3.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request3.sClassificationType = 'Enrollment (RSO)';
                  request3.sPriority = 'Normal';
                  request3.sComplaint = 'test';
                  request3.sComplaintReason = 'test';
                  request3.sComplaintType = 'test';                  
                  request3.sGARightsGiven ='yes' ;
                  request3.sGAReason = 'test';
                  request3.sFollowupDueDate = '10/01/2017';
                  request3.sClassification = 'Chronic Care SNP';
                  request3.sIntent = 'Denied';  
                  request3.sDocType= 'pdf';
                  request3.sDCN= 'test';                
                                     
                  request3.CaseCommentDTO  = listCommentDTO; 
                  request3.sContract = 'test';
                  request3.sExternalTrackId = '1234';
                  request3.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request3.sInteractingAboutCity = 'depere';
                  request3.sInteractingAboutEmailID = 'test@humana.com';
                  request3.sInteractingAboutHomePhone = '9203375562';
                  request3.sInteractingAboutState = 'WI';
                  request3.sInteractingAboutZipCode = '54115';                  
                  request3.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request3.sInteractingWithCity = 'de pere';
                  request3.sInteractingWithEmailID = 'test@humana.com';
                  request3.sInteractingWithHomePhone = '9203375562';
                  request3.sInteractingWithState = 'WI';
                  request3.sInteractingWithZipCode = '54115';                    
                  request3.sMbrSignDate = '06/05/2017';
                  request3.sMemberSSN = '567234444';
                  request3.sMilestoneId = '123';
                  request3.sPBP = 'test';
                  request3.sPendKey = 'test';
                  request3.sSegment = 'test';
                  request3.sSourceIdTypeCd = 'test';  
                  request3.sTopicName = 'test topic';
                  request3.sWorkQueueViewName = 'Other';
                  
                  //New request
                  HUMCreateCaseRequestHelper request4 = New HUMCreateCaseRequestHelper(); 
                  request4.bAsynchronousProcess = true;
                  request4.sSourceApp ='AE';
                  request4.sSourceKey = 'test';
                  request4.sInteractingAboutId = '223369104';
                  request4.sInteractingAboutIdType = 'EnterprisePersonID';
                  request4.sInteractingAboutType = 'Member';
                  request4.sInteractingAboutFirstName = 'test57';
                  request4.sInteractingAboutLastName = 'method57';
                  request4.sInteractingAboutDOB = '02/01/1990';
                  request4.sInteractingWithId = '223355804';
                  request4.sInteractingWithIdType ='SSN';
                  request4.sInteractingWithType = 'Member';
                  request4.sInteractingWithFirstName = 'test65';
                  request4.sInteractingWithLastName = 'method65';
                  request4.sInteractingWithDOB = '11/01/1990';
                  request4.sOrigin = 'AEDUASNP';
                  request4.sType = 'Customer Inquiry';
                  request4.sCaseSubType = 'Routine';
                  request4.sStatus = 'Closed';
                  request4.sCreatedQueue = 'Louisville RSO Enrollment';
                  request4.sOwnerQueue = 'Louisville RSO Enrollment';                
                  request4.sServiceCenter = 'testcenter';
                  request4.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request4.sClassificationType = 'Enrollment (RSO)';
                  request4.sPriority = 'Normal';
                  request4.sComplaint = 'test';
                  request4.sComplaintReason = 'test';
                  request4.sComplaintType = 'test';                  
                  request4.sGARightsGiven ='yes' ;
                  request4.sGAReason = 'test';
                  request4.sFollowupDueDate = '10/01/2017';
                  request4.sClassification = 'Chronic Care SNP';
                  request4.sIntent = 'Denied';  
                  request4.sDocType= 'pdf';
                  request4.sDCN= 'test';                
                                     
                  request4.CaseCommentDTO  = listCommentDTO; 
                  request4.sContract = 'test';
                  request4.sExternalTrackId = '1234';
                  request4.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request4.sInteractingAboutCity = 'depere';
                  request4.sInteractingAboutEmailID = 'test@humana.com';
                  request4.sInteractingAboutHomePhone = '9203375562';
                  request4.sInteractingAboutState = 'WI';
                  request4.sInteractingAboutZipCode = '54115';                  
                  request4.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request4.sInteractingWithCity = 'de pere';
                  request4.sInteractingWithEmailID = 'test@humana.com';
                  request4.sInteractingWithHomePhone = '9203375562';
                  request4.sInteractingWithState = 'WI';
                  request4.sInteractingWithZipCode = '54115';                    
                  request4.sMbrSignDate = '06/05/2017';
                  request4.sMemberSSN = '567200444';
                  request4.sMilestoneId = '123';
                  request4.sPBP = 'test';
                  request4.sPendKey = 'test';
                  request4.sSegment = 'test';
                  request4.sSourceIdTypeCd = 'test';  
                  request4.sTopicName = 'test topic';
                  request4.sWorkQueueViewName = 'Other';
                  
                  
                  HUMCreateCaseRequestHelper request5 = new HUMCreateCaseRequestHelper ();
                  request5.bAsynchronousProcess = true;
                  request5.sSourceApp ='AE';
                  request5.sSourceKey = 'test';
                  request5.sInteractingAboutId = '567234224';
                  request5.sInteractingAboutIdType = 'MedicareID';
                  request5.sInteractingAboutType = 'Member';
                  request5.sInteractingAboutFirstName = 'test6';
                  request5.sInteractingAboutLastName = 'method6';
                  request5.sInteractingAboutDOB = '01/01/1990';
                  request5.sInteractingWithId = '002935049';
                  request5.sInteractingWithIdType ='MedicareID';
                  request5.sInteractingWithType = 'Member';
                  request5.sInteractingWithFirstName = 'test3';
                  request5.sInteractingWithLastName = 'method3';
                  request5.sInteractingWithDOB = '01/01/1990';
                  request5.sOrigin = 'AEDUASNP';
                  request5.sType = 'Customer Inquiry';
                  request5.sCaseSubType = 'Routine';
                  request5.sStatus = 'Closed';
                  request5.sCreatedQueue = 'Louisville RSO Enrollment';
                  request5.sOwnerQueue = 'Louisville RSO Enrollment';
                
                  request5.sServiceCenter = 'testcenter';
                  request5.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request5.sClassificationType = 'Enrollment (RSO)';
                  request5.sPriority = 'Normal';
                  request5.sComplaint = 'test';
                  request5.sComplaintReason = 'test';
                  request5.sComplaintType = 'test';
                  
                  request5.sGARightsGiven ='yes' ;
                  request5.sGAReason = 'test';
                  request5.sFollowupDueDate = '10/01/2017';
                  request5.sClassification = 'Chronic Care SNP';
                  request5.sIntent = 'Denied';  
                  request5.sDocType= 'pdf';
                  request5.sDCN= 'test';
                   
                  List<HUMCaseCommentRequestDTO> listCommentDTO1 = new List<HUMCaseCommentRequestDTO>();                            
                  HUMCaseCommentRequestDTO commentDTO1 = new HUMCaseCommentRequestDTO();
                  commentDTO1.CommentBody  = 'New comment';
                  listCommentDTO1.add(commentDTO1);                                   
                  request5.CaseCommentDTO  = listCommentDTO1;                           
                    
                  request5.sClosedDate = '10/10/2017';
                  request5.sContract = 'test';
                  request5.sExternalTrackId = '1234';
                  request5.sInteractingAboutAddressLine1 = '1100 employers blvd';
                  request5.sInteractingAboutCity = 'depere';
                  request5.sInteractingAboutEmailID = 'test@humana.com';
                  request5.sInteractingAboutHomePhone = '9203375562';
                  request5.sInteractingAboutState = 'WI';
                  request5.sInteractingAboutZipCode = '54115';                  
                  request5.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request5.sInteractingWithCity = 'de pere';
                  request5.sInteractingWithEmailID = 'test@humana.com';
                  request5.sInteractingWithHomePhone = '9203375562';
                  request5.sInteractingWithState = 'WI';
                  request5.sInteractingWithZipCode = '54115';                    
                  request5.sMbrSignDate = '06/05/2017';
                  request5.sMemberSSN = '360306641';
                  request5.sMilestoneId = '123';
                  request5.sPBP = 'test';
                  request5.sPendKey = 'test';
                  request5.sSegment = 'test';
                  request5.sSourceIdTypeCd = 'test';  
                  request5.sTopicName = 'test topic';
                  request5.sWorkQueueViewName = 'Other';
                  request5.sFollowupDueDate = '10/1/2017';
                  request5.sRecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case'); 
                  
                  
                  HUMCreateCaseRequestHelper request6 = new HUMCreateCaseRequestHelper ();
                  HUMCreateCaseServiceRequestHelper createServiceRequest1 = new HUMCreateCaseServiceRequestHelper();
                  createServiceRequest1.createCaseRequest= request6;          


    
                  request6.bAsynchronousProcess = false;
                  request6.sSourceApp ='AE';
                  request6.sSourceKey = 'test';
                  request6.sInteractingAboutId = '';
                  request6.sInteractingAboutIdType = '';
                  request6.sInteractingAboutType = 'Member';
                  request6.sInteractingAboutFirstName = '';
                  request6.sInteractingAboutLastName = '';
                  request6.sInteractingAboutDOB = '';
                  request6.sInteractingWithId = '';
                  request6.sInteractingWithIdType ='';
                  request6.sInteractingWithType = '';
                  request6.sInteractingWithFirstName = '';
                  request6.sInteractingWithLastName = '';
                  request6.sInteractingWithDOB = '01/1990';
                  request6.sOrigin = 'AEDUASNP';
                  request6.sType = '';
                  request6.sCaseSubType = '';
                  request6.sStatus = '';
                  request6.sCreatedQueue = 'Louisville RSO Enrollment';
                  request6.sOwnerQueue = 'Louisville RSO Enrollment';
                
                  request6.sServiceCenter = 'testcenter';
                  request6.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request6.sClassificationType = 'Enrollment (RSO)';
                  request6.sPriority = 'Normal';
                  request6.sComplaint = 'test';
                  request6.sComplaintReason = 'test';
                  request6.sComplaintType = 'test';
                  
                  request6.sGARightsGiven ='yes' ;
                  request6.sGAReason = 'test';
                  request6.sFollowupDueDate = '10/01/2017';
                  request6.sClassification = 'Chronic Care SNP';
                  request6.sIntent = 'Denied';  
                  request6.sDocType= 'pdf';
                  request6.sDCN= 'test';
                   
                  List<HUMCaseCommentRequestDTO> listCommentDTO11 = new List<HUMCaseCommentRequestDTO>();                            
                  HUMCaseCommentRequestDTO commentDTO11 = new HUMCaseCommentRequestDTO();
                  commentDTO1.CommentBody  = 'New comment';
                  listCommentDTO11.add(commentDTO11);                                   
                  request6.CaseCommentDTO  = listCommentDTO11;                           
                    
                  request6.sClosedDate = '10/10/2017';
                  request6.sContract = 'test';
                  request6.sExternalTrackId = '1234';
                  request6.sInteractingAboutAddressLine1 = '1100 employers blvd';
                  request6.sInteractingAboutCity = 'depere';
                  request6.sInteractingAboutEmailID = 'test@humana.com';
                  request6.sInteractingAboutHomePhone = '9203375562';
                  request6.sInteractingAboutState = '';
                  request6.sInteractingAboutZipCode = '54115';                  
                  request6.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request6.sInteractingWithCity = 'de pere';
                  request6.sInteractingWithEmailID = 'test@humana.com';
                  request6.sInteractingWithHomePhone = '9203375562';
                  request6.sInteractingWithState = '';
                  request6.sInteractingWithZipCode = '54115';                    
                  request6.sMbrSignDate = '06/05/2017';
                  request6.sMemberSSN = '360306641';
                  request6.sMilestoneId = '123';
                  request6.sPBP = 'test';
                  request6.sPendKey = 'test';
                  request6.sSegment = 'test';
                  request6.sSourceIdTypeCd = 'test';  
                  request6.sTopicName = 'test topic';
                  request6.sWorkQueueViewName = 'Other';
                  request6.sFollowupDueDate = '10/1/2017';
                  request6.sRecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case'); 
                  
                  //new request
                  HUMCreateCaseRequestHelper request7 = new HUMCreateCaseRequestHelper (); 
                  request7.bAsynchronousProcess = true;
                  request7.sSourceApp ='AE';
                  request7.sSourceKey = 'test';
                  request7.sInteractingAboutId = '';
                  request7.sInteractingAboutIdType = 'EnterprisePersonID';
                  request7.sInteractingAboutType = 'Member';
                  request7.sInteractingAboutFirstName = 'test1';
                  request7.sInteractingAboutLastName = 'method1';
                  request7.sInteractingAboutDOB = '01/01/1990';
                  request7.sInteractingWithId = '';
                  request7.sInteractingWithIdType ='SSN';
                  request7.sInteractingWithType = 'Member';
                  request7.sInteractingWithFirstName = 'test1';
                  request7.sInteractingWithLastName = 'method1';
                  request7.sInteractingWithDOB = '01/01/1990';
                  request7.sOrigin = 'AEDUASNP';
                  request7.sType = 'Customer Inquiry';
                  request7.sCaseSubType = 'Routine';
                  request7.sStatus = 'Closed';
                  request7.sCreatedQueue = 'Louisville RSO Enrollment';
                  request7.sOwnerQueue = 'Louisville RSO Enrollment';                
                  request7.sServiceCenter = 'testcenter';
                  request7.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request7.sClassificationType = 'Enrollment (RSO)';
                  request7.sPriority = 'Normal';
                  request7.sComplaint = 'test';
                  request7.sComplaintReason = 'test';
                  request7.sComplaintType = 'test';                  
                  request7.sGARightsGiven ='yes' ;
                  request7.sGAReason = 'test';
                  request7.sFollowupDueDate = '10/01/2017';
                  request7.sClassification = 'Chronic Care SNP';
                  request7.sIntent = 'Denied';  
                  request7.sDocType= 'pdf';
                  request7.sDCN= 'test';                
                                     
                  request7.CaseCommentDTO  = listCommentDTO; 
                  request7.sContract = 'test';
                  request.sExternalTrackId = '1234';
                  request7.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request7.sInteractingAboutCity = 'depere';
                  request7.sInteractingAboutEmailID = 'test@humana.com';
                  request7.sInteractingAboutHomePhone = '9203375562';
                  request7.sInteractingAboutState = 'WI';
                  request7.sInteractingAboutZipCode = '54115';                  
                  request7.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request7.sInteractingWithCity = 'de pere';
                  request7.sInteractingWithEmailID = 'test@humana.com';
                  request7.sInteractingWithHomePhone = '9203375562';
                  request7.sInteractingWithState = 'WI';
                  request7.sInteractingWithZipCode = '54115';                    
                  request7.sMbrSignDate = '06/05/2017';
                  request7.sMemberSSN = '567200444';
                  request7.sMilestoneId = '123';
                  request7.sPBP = 'test';
                  request7.sPendKey = 'test';
                  request7.sSegment = 'test';
                  request7.sSourceIdTypeCd = 'test';  
                  request7.sTopicName = 'test topic';
                  request7.sWorkQueueViewName = 'Other'; 
                                          
                  
                  HUMCreateCaseRequestHelper request8 = new HUMCreateCaseRequestHelper (); 
                  request8.bAsynchronousProcess = true;
                  request8.sSourceApp ='CORR';
                  request8.sSourceKey = 'test';
                  request8.sInteractingAboutId = 'H46738930';
                  request8.sInteractingAboutIdType = 'Member-Id-Base';
                  request8.sInteractingAboutType = 'Member';
                  request8.sInteractingAboutFirstName = 'test8';
                  request8.sInteractingAboutLastName = 'method8';
                  request8.sInteractingAboutDOB = '01/01/1997';
                  request8.sInteractingWithId = 'H46738930';
                  request8.sInteractingWithIdType ='Member-Id-Base';
                  request8.sInteractingWithType = 'Member';
                  request8.sInteractingWithFirstName = 'test8';
                  request8.sInteractingWithLastName = 'method8';
                  request8.sInteractingWithDOB = '01/01/1997';
                  request8.sOrigin = 'Correspondence';
                  request8.sType = 'Correspondence Inquiry';
                  request8.sCaseSubType = 'Creditable Coverage';
                  request8.sStatus = 'In Progress';
                  request8.sCreatedQueue = 'Louisville RSO Enrollment';
                  request8.sOwnerQueue = 'Louisville RSO Enrollment';                
                  request8.sServiceCenter = 'testcenter';
                  request8.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request8.sClassificationType = 'Enrollment (RSO)';
                  request8.sPriority = 'Normal';
                  request8.sComplaint = 'test';
                  request8.sComplaintReason = 'test';
                  request8.sComplaintType = 'test';                  
                  request8.sGARightsGiven ='yes' ;
                  request8.sGAReason = 'test';
                  request8.sFollowupDueDate = '10/01/2017';
                  request8.sClassification = 'Chronic Care SNP';
                  request8.sIntent = 'Denied';  
                  request8.sDocType= 'pdf';
                  request8.sDCN= 'test';                
                                     
                  request8.CaseCommentDTO  = listCommentDTO; 
                  request8.sContract = 'test';
                  request8.sExternalTrackId = '1234';
                  request8.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request8.sInteractingAboutCity = 'depere';
                  request8.sInteractingAboutEmailID = 'test@humana.com';
                  request8.sInteractingAboutHomePhone = '9203375562';
                  request8.sInteractingAboutState = 'KY';
                  request8.sInteractingAboutZipCode = '54115';                  
                  request8.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request8.sInteractingWithCity = 'de pere';
                  request8.sInteractingWithEmailID = 'test@humana.com';
                  request8.sInteractingWithHomePhone = '9203375562';
                  request8.sInteractingWithState = 'WI';
                  request8.sInteractingWithZipCode = '54115';                    
                  request8.sMbrSignDate = '06/05/2017';
                  request8.sMemberSSN = '';
                  request8.sMilestoneId = '123';
                  request8.sPBP = 'test';
                  request8.sPendKey = 'test';
                  request8.sSegment = 'test';
                  request8.sSourceIdTypeCd = 'test';  
                  request8.sTopicName = 'test topic';
                  request8.sWorkQueueViewName = 'Other';
                  
                  HUMCreateCaseRequestHelper request9 = new HUMCreateCaseRequestHelper (); 
                  request9.bAsynchronousProcess = true;
                  request9.sSourceApp ='CORR';
                  request9.sSourceKey = 'test';
                  request9.sInteractingAboutId = 'H46738930';
                  request9.sInteractingAboutIdType = 'Member-Id-Base';
                  request9.sInteractingAboutType = 'Member';
                  request9.sInteractingAboutFirstName = 'test8';
                  request9.sInteractingAboutLastName = 'method8';
                  request9.sInteractingAboutDOB = '01/01/1997';
                  request9.sInteractingWithId = 'H46738930';
                  request9.sInteractingWithIdType ='Member-Id-Base';
                  request9.sInteractingWithType = 'Member';
                  request9.sInteractingWithFirstName = 'test8';
                  request9.sInteractingWithLastName = 'method8';
                  request9.sInteractingWithDOB = '01/01/1997';
                  request9.sOrigin = 'Correspondence';
                  request9.sType = 'Correspondence Inquiry';
                  request9.sCaseSubType = 'Creditable Coverage';
                  request9.sStatus = 'In Progress';
                  request9.sCreatedQueue = 'Louisville RSO Enrollment';
                  request9.sOwnerQueue = 'Louisville RSO Enrollment';                
                  request9.sServiceCenter = 'testcenter';
                  request9.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request9.sClassificationType = 'Enrollment (RSO)';
                  request9.sPriority = 'Normal';
                  request9.sComplaint = 'test';
                  request9.sComplaintReason = 'test';
                  request9.sComplaintType = 'test';                  
                  request9.sGARightsGiven ='yes' ;
                  request9.sGAReason = 'test';
                  request9.sFollowupDueDate = '10/01/2017';
                  request9.sClassification = 'Chronic Care SNP';
                  request9.sIntent = 'Denied';  
                  request9.sDocType= 'pdf';
                  request9.sDCN= 'test';                
                                     
                  request9.CaseCommentDTO  = listCommentDTO; 
                  request9.sContract = 'test';
                  request9.sExternalTrackId = '1234';
                  request9.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request9.sInteractingAboutCity = 'depere';
                  request9.sInteractingAboutEmailID = 'test@humana.com';
                  request9.sInteractingAboutHomePhone = '9203375562';
                  request9.sInteractingAboutState = 'KK';
                  request9.sInteractingAboutZipCode = '54115';                  
                  request9.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request9.sInteractingWithCity = 'de pere';
                  request9.sInteractingWithEmailID = 'test@humana.com';
                  request9.sInteractingWithHomePhone = '9203375562';
                  request9.sInteractingWithState = 'KK';
                  request9.sInteractingWithZipCode = '54115';                    
                  request9.sMbrSignDate = '06/05/2017';
                  request9.sMemberSSN = '';
                  request9.sMilestoneId = '123';
                  request9.sPBP = 'test';
                  request9.sPendKey = 'test';
                  request9.sSegment = 'test';
                  request9.sSourceIdTypeCd = 'test';  
                  request9.sTopicName = 'test topic';
                  request9.sWorkQueueViewName = 'Other';
                  
                  HUMCreateCaseRequestHelper request10 = new HUMCreateCaseRequestHelper (); 
                  request10.bAsynchronousProcess = true;
                  request10.sSourceApp ='CORR';
                  request10.sSourceKey = 'test';
                  request10.sInteractingAboutId = 'H46738930';
                  request10.sInteractingAboutIdType = 'Member-Id-Base';
                  request10.sInteractingAboutType = 'Member';
                  request10.sInteractingAboutFirstName = 'test8';
                  request10.sInteractingAboutLastName = 'method8';
                  request10.sInteractingAboutDOB = '01/01/1997';
                  request10.sInteractingWithId = 'H46738930';
                  request10.sInteractingWithIdType ='Member-Id-Base';
                  request10.sInteractingWithType = 'Member';
                  request10.sInteractingWithFirstName = 'test8';
                  request10.sInteractingWithLastName = 'method8';
                  request10.sInteractingWithDOB = '01/01/1997';
                  request10.sOrigin = 'Correspondence';
                  request10.sType = 'Correspondence Inquiry';
                  request10.sCaseSubType = 'Creditable Coverage';
                  request10.sStatus = 'In Progress';
                  request10.sCreatedQueue = 'Louisville RSO Enrollment';
                  request10.sOwnerQueue = 'Louisville RSO Enrollment';                
                  request10.sServiceCenter = 'testcenter';
                  request10.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request10.sClassificationType = 'Enrollment (RSO)';
                  request10.sPriority = 'Normal';
                  request10.sComplaint = 'test';
                  request10.sComplaintReason = 'test';
                  request10.sComplaintType = 'test';                  
                  request10.sGARightsGiven ='yes' ;
                  request10.sGAReason = 'test';
                  request10.sFollowupDueDate = '10/01/2017';
                  request10.sClassification = 'Chronic Care SNP';
                  request10.sIntent = 'Denied';  
                  request10.sDocType= 'pdf';
                  request10.sDCN= 'test';                
                                     
                  request10.CaseCommentDTO  = listCommentDTO; 
                  request10.sContract = 'test';
                  request10.sExternalTrackId = '1234';
                  request10.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request10.sInteractingAboutCity = 'depere';
                  request10.sInteractingAboutEmailID = 'test@humana.com';
                  request10.sInteractingAboutHomePhone = '9203375562';
                  request10.sInteractingAboutState = 'KK';
                  request10.sInteractingAboutZipCode = '54115';                  
                  request10.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request10.sInteractingWithCity = 'de pere';
                  request10.sInteractingWithEmailID = 'test@humana.com';
                  request10.sInteractingWithHomePhone = '9203375562';
                  request10.sInteractingWithState = 'KK';
                  request10.sInteractingWithZipCode = '54115';                    
                  request10.sMbrSignDate = '06/05/2017';
                  request10.sMemberSSN = '';
                  request10.sMilestoneId = '123';
                  request10.sPBP = 'test';
                  request10.sPendKey = 'test';
                  request10.sSegment = 'test';
                  request10.sSourceIdTypeCd = 'test';  
                  request10.sTopicName = 'test topic';
                  request10.sWorkQueueViewName = 'Other';
                       
                  ID testId = '500L0000008uOi1IAE';
                  CreateCase_D_HUM.createCaseComment('test comment',testId);
                  system.assertNotEquals(testId, null , 'ID not created');
                    
                  case oCase = new case();
                  oCase.Id = testId;
                  oCase.AccountID = testId;
                  oCase.Interacting_About_Type__c = 'member';
                  oCase.Interacting_With__c = testId;
                  oCase.Interacting_With_Type__c = 'SSN';   
                                 
            
            
            Test.StartTest();                           
             
            HUMCreateCaseServiceResponseHelper response =  HUMCaseService.createCaseInfo(createServiceRequest);
            system.assertNotEquals(response ,NULL);            
                        
            
            CreateCase_D_HUM.updateCaseAndAccount(request5,testId);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL);   
            
            CreateCase_D_HUM.updateMemberAndPolicy(oCase);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL); 
            
            CreateCase_D_HUM.updateCaseDetails(oCase);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL); 
            
            CreateCase_D_HUM.logError(new List<Database.Error>(),'testmethod','testobject','123');
            system.assertNotEquals([Select method_name__c from error_log__c where method_name__c='testmethod'] ,NULL);             
                        
            HUMCreateCaseResponseHelper  logResponse =  CreateCase_D_HUM.getErrorLogInstance('00','test error',true);
            system.assertNotEquals(logResponse ,NULL);
            
            CreateCase_D_HUM.createCaseComment('test coment', testId);            
            system.assertNotEquals([SELECT commentBody   FROM CaseComment   WHERE parentid =: testId ], Null);
            
            Map<String,String> memeberResult = CreateCase_D_HUM.getMemberAccountMaps(request1,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult ,NULL);
          
            Map<String,String> memeberResult1 = CreateCase_D_HUM.getMemberAccountMaps(request1,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult1 ,NULL);
          
            Map<String,String> memeberResult2 = CreateCase_D_HUM.getMemberAccountMaps(request2,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult2 ,NULL);
            
            Map<String,String> memeberResult3 = CreateCase_D_HUM.getMemberAccountMaps(request2,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult3 ,NULL);                      
            
             Map<String,String> memeberResult4 = CreateCase_D_HUM.getMemberAccountMaps(request3,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult4 ,NULL);
            
            Map<String,String> memeberResult5 = CreateCase_D_HUM.getMemberAccountMaps(request3,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult5 ,NULL); 
            
            Map<String,String> memeberResult6 = CreateCase_D_HUM.getMemberAccountMaps(request4,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult6 ,NULL);
            
            Map<String,String> memeberResult7 = CreateCase_D_HUM.getMemberAccountMaps(request4,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult7 ,NULL); 
            
            Map<String,String> memeberResult8 = CreateCaseAccount_D_HUM.createAccount(request2,'InteractingAboutType','', testId);
            system.assertNotEquals(memeberResult8 ,NULL); 
            
            Map<String,String> memeberResult9 = CreateCase_D_HUM.getMemberAccountMaps(request7,'InteractingAboutType',testId);
            system.assertNotEquals(memeberResult9 ,NULL);
                            
            String MemberId = CreateCase_D_HUM.locateMemberByName('test1','method1','01/01/1990', 'WI');  
            system.assertNotEquals(MemberId,Null);
            
            String MemberId1 = CreateCase_D_HUM.locateMemberByName('test7', 'method7', '01/01/1997', 'KY');  
            system.assertNotEquals(MemberId1, Null);
            
            HUMCreateCaseResponseHelper response2= CreateCase_D_HUM.createCase(NULL);  
            system.assertNotEquals(response2,NULL);
            
            HUMCreateCaseResponseHelper response3 = CreateCase_D_HUM.createCaseRecord(request5);
            system.assertNotEquals(response3 ,NULL);
            
            Map<String,String> memeberResult10 = CreateCase_D_HUM.getMemberAccountMaps(request8,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult10 ,NULL); 
  
            Map<String,String> memeberResult11 = CreateCase_D_HUM.getMemberAccountMaps(request9,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult11 ,NULL); 
            
            Map<String,String> memeberResult12 = CreateCaseAccount_D_HUM.createAccount(request10, NULL,'', testId);
            system.assertNotEquals(memeberResult12 ,NULL);
            
            HUMCreateCaseServiceResponseHelper response1 =  HUMCaseService.createCaseInfo(createServiceRequest1);
            system.assertNotEquals(response1 ,NULL); 
                         
            Test.StopTest();
        }
    }
        /**
    * This is a test method for Create Case  Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
   static testMethod void testHUMCaseService_CreateCase1()
    {
        
       HUMTestDataHelperTest.getHUMConstantsData();       
       User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname == 'test User1',true);
        List<Account> accList = [select id,name,Enterprise_ID__c,firstname,lastname,Birthdate__c,ETL_Record_Deleted__c,PERSONMAILINGSTATECODE from account where firstname='test1'];
        
        System.runAs(oUser) 
        {
                        
             HUMCreateCaseRequestHelper request = new HUMCreateCaseRequestHelper ();
            
             HUMCreateCaseServiceRequestHelper createServiceRequest = new HUMCreateCaseServiceRequestHelper();
             createServiceRequest.createCaseRequest= request;
             
                  request.bAsynchronousProcess = true;
                  request.sSourceApp ='AE';
                  request.sSourceKey = 'test';
                  request.sInteractingAboutId = '002935049';
                  request.sInteractingAboutIdType = 'MedicareID';
                  request.sInteractingAboutType = 'Member';
                  request.sInteractingAboutFirstName = 'test3';
                  request.sInteractingAboutLastName = 'method3';
                  request.sInteractingAboutDOB = '01/01/1990';
                  request.sInteractingWithId = '002935049';
                  request.sInteractingWithIdType ='MedicareID';
                  request.sInteractingWithType = 'Member';
                  request.sInteractingWithFirstName = 'test3';
                  request.sInteractingWithLastName = 'method3';
                  request.sInteractingWithDOB = '01/01/1990';
                  request.sOrigin = 'AEDUASNP';
                  request.sType = 'Customer Inquiry';
                  request.sCaseSubType = 'Routine';
                  request.sStatus = 'Closed';
                  request.sCreatedQueue = 'Louisville RSO Enrollment';
                  request.sOwnerQueue = 'Louisville RSO Enrollment';
                
                  request.sServiceCenter = 'testcenter';
                  request.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request.sClassificationType = 'Enrollment (RSO)';
                  request.sPriority = 'Normal';
                  request.sComplaint = 'test';
                  request.sComplaintReason = 'test';
                  request.sComplaintType = 'test';
                  
                  request.sGARightsGiven ='yes' ;
                  request.sGAReason = 'test';
                  request.sFollowupDueDate = '10/01/2017';
                  request.sClassification = 'Chronic Care SNP';
                  request.sIntent = 'Denied';  
                  request.sDocType= 'pdf';
                  request.sDCN= 'test';
                  
                  List<HUMCaseCommentRequestDTO> listCommentDTO = new List<HUMCaseCommentRequestDTO>();
                  HUMCaseCommentRequestDTO commentDTO = new HUMCaseCommentRequestDTO();
                  commentDTO.CommentBody  = 'New comment';
                  listCommentDTO.add(commentDTO);                                   
                  request.CaseCommentDTO  = listCommentDTO;                           
                               
                  request.sClosedDate = '10/10/2017';
                  request.sContract = 'test';
                  request.sExternalTrackId = '1234';
                  request.sInteractingAboutAddressLine1 = '1100 employers blvd';
                  request.sInteractingAboutCity = 'depere';
                  request.sInteractingAboutEmailID = 'test@humana.com';
                  request.sInteractingAboutHomePhone = '9203375562';
                  request.sInteractingAboutState = 'WI';
                  request.sInteractingAboutZipCode = '54115';                  
                  request.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request.sInteractingWithCity = 'de pere';
                  request.sInteractingWithEmailID = 'test@humana.com';
                  request.sInteractingWithHomePhone = '9203375562';
                  request.sInteractingWithState = 'WI';
                  request.sInteractingWithZipCode = '54115';                    
                  request.sMbrSignDate = '06/05/2017';
                  request.sMemberSSN = '360306641';
                  request.sMilestoneId = '123';
                  request.sPBP = 'test';
                  request.sPendKey = 'test';
                  request.sSegment = 'test';
                  request.sSourceIdTypeCd = 'test';  
                  request.sTopicName = 'test topic';
                  request.sWorkQueueViewName = 'Other';
                  request.sFollowupDueDate = '10/1/2017';
                  request.sRecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case'); 
                    
                  //new request
                  HUMCreateCaseRequestHelper request1 = new HUMCreateCaseRequestHelper (); 
                  request1.bAsynchronousProcess = false;
                  request1.sSourceApp ='AE';
                  request1.sSourceKey = 'test';
                  request1.sInteractingAboutId = '223338104';
                  request1.sInteractingAboutIdType = 'SSN';
                  request1.sInteractingAboutType = 'Member';
                  request1.sInteractingAboutFirstName = 'test5';
                  request1.sInteractingAboutLastName = 'method5';
                  request1.sInteractingAboutDOB = '01/10/1990';
                  request1.sInteractingWithId = '002978049';
                  request1.sInteractingWithIdType ='Medicaid-Id';
                  request1.sInteractingWithType = 'Member';
                  request1.sInteractingWithFirstName = 'test10';
                  request1.sInteractingWithLastName = 'method10';
                  request1.sInteractingWithDOB = '01/10/1991';
                  request1.sOrigin = 'AEDUASNP';
                  request1.sType = 'Customer Inquiry';
                  request1.sCaseSubType = 'Routine';
                  request1.sStatus = 'Closed';
                  request1.sCreatedQueue = 'Louisville RSO Enrollment';
                  request1.sOwnerQueue = 'Louisville RSO Enrollment';
                  request1.sServiceCenter = 'testcenter';
                  request1.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request1.sClassificationType = 'Enrollment (RSO)';
                  request1.sPriority = 'Normal';
                  request1.sComplaint = 'test';
                  request1.sComplaintReason = 'test';
                  request1.sComplaintType = 'test';                  
                  request1.sGARightsGiven ='yes' ;
                  request1.sGAReason = 'test';
                  request1.sFollowupDueDate = '10/01/2017';
                  request1.sClassification = 'Chronic Care SNP';
                  request1.sIntent = 'Denied';  
                  request1.sDocType= 'pdf';
                  request1.sDCN= 'test';
                  request1.CaseCommentDTO  = listCommentDTO; 
                  request1.sContract = 'test';
                  request1.sExternalTrackId = '1234';
                  request1.sInteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request1.sInteractingAboutCity = 'depere';
                  request1.sInteractingAboutEmailID = 'test@humana.com';
                  request1.sInteractingAboutHomePhone = '9203375562';
                  request1.sInteractingAboutState = 'WI';
                  request1.sInteractingAboutZipCode = '54115';                  
                  request1.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request1.sInteractingWithCity = 'de pere';
                  request1.sInteractingWithEmailID = 'test@humana.com';
                  request1.sInteractingWithHomePhone = '9203375562';
                  request1.sInteractingWithState = 'WI';
                  request1.sInteractingWithZipCode = '54115';                    
                  request1.sMbrSignDate = '06/05/2017';
                  request1.sMemberSSN = '567234444';
                  request1.sMilestoneId = '123';
                  request1.sPBP = 'test';
                  request1.sPendKey = 'test';
                  request1.sSegment = 'test';
                  request1.sSourceIdTypeCd = 'test';  
                  request1.sTopicName = 'test topic';
                  request1.sWorkQueueViewName = 'Other';
            
            
                  HUMCreateCaseRequestHelper request2 = new HUMCreateCaseRequestHelper ();
                  HUMCreateCaseServiceRequestHelper createServiceRequest1 = new HUMCreateCaseServiceRequestHelper();
                  createServiceRequest1.createCaseRequest= request2;          

                  request2.bAsynchronousProcess = false;
                  request2.sSourceApp ='AE';
                  request2.sSourceKey = 'test';
                  request2.sInteractingAboutId = '';
                  request2.sInteractingAboutIdType = 'Member-Id-Base';
                  request2.sInteractingAboutType = 'Member';
                  request2.sInteractingAboutFirstName = '';
                  request2.sInteractingAboutLastName = '';
                  request2.sInteractingAboutDOB = '';
                  request2.sInteractingWithId = '';
                  request2.sInteractingWithIdType ='Member-Id-Base';
                  request2.sInteractingWithType = '';
                  request2.sInteractingWithFirstName = '';
                  request2.sInteractingWithLastName = '';
                  request2.sInteractingWithDOB = '01/1990';
                  request2.sOrigin = 'AEDUASNP';
                  request2.sType = '';
                  request2.sCaseSubType = '';
                  request2.sStatus = '';
                  request2.sCreatedQueue = 'Louisville RSO Enrollment';
                  request2.sOwnerQueue = 'Louisville RSO Enrollment';
                
                  request2.sServiceCenter = 'testcenter';
                  request2.sDepartment = 'Retail Service Operations (RSO) Enrollment';
                  request2.sClassificationType = 'Enrollment (RSO)';
                  request2.sPriority = 'Normal';
                  request2.sComplaint = 'test';
                  request2.sComplaintReason = 'test';
                  request2.sComplaintType = 'test';
                  
                  request2.sGARightsGiven ='yes' ;
                  request2.sGAReason = 'test';
                  request2.sFollowupDueDate = '10/01/2017';
                  request2.sClassification = 'Chronic Care SNP';
                  request2.sIntent = 'Denied';  
                  request2.sDocType= 'pdf';
                  request2.sDCN= 'test';
                   
                  List<HUMCaseCommentRequestDTO> listCommentDTO11 = new List<HUMCaseCommentRequestDTO>();                            
                  HUMCaseCommentRequestDTO commentDTO11 = new HUMCaseCommentRequestDTO();
                  commentDTO11.CommentBody  = 'New comment';
                  listCommentDTO11.add(commentDTO11);                                   
                  request2.CaseCommentDTO  = listCommentDTO11;                           
                    
                  request2.sClosedDate = '10/10/2017';
                  request2.sContract = 'test';
                  request2.sExternalTrackId = '1234';
                  request2.sInteractingAboutAddressLine1 = '1100 employers blvd';
                  request2.sInteractingAboutCity = 'depere';
                  request2.sInteractingAboutEmailID = 'test@humana.com';
                  request2.sInteractingAboutHomePhone = '9203375562';
                  request2.sInteractingAboutState = '';
                  request2.sInteractingAboutZipCode = '54115';                  
                  request2.sInteractingWithAddressLine1 = '1100 employers blvd';                    
                  request2.sInteractingWithCity = 'de pere';
                  request2.sInteractingWithEmailID = 'test@humana.com';
                  request2.sInteractingWithHomePhone = '9203375562';
                  request2.sInteractingWithState = '';
                  request2.sInteractingWithZipCode = '54115';                    
                  request2.sMbrSignDate = '06/05/2017';
                  request2.sMemberSSN = '360306641';
                  request2.sMilestoneId = '123';
                  request2.sPBP = 'test';
                  request2.sPendKey = 'test';
                  request2.sSegment = 'test';
                  request2.sSourceIdTypeCd = 'test';  
                  request2.sTopicName = 'test topic';
                  request2.sWorkQueueViewName = 'Other';
                  request2.sFollowupDueDate = '10/1/2017';
                  request2.sRecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case'); 
            
                  ID testId = '500L0000008uOi1IAE';
                  CreateCase_D_HUM.createCaseComment('test comment',testId);
                  system.assertNotEquals(testId, null , 'ID not created');
                    
                  case oCase = new case();
                  oCase.Id = testId;
                  oCase.AccountID = testId;
                  oCase.Interacting_About_Type__c = 'member';
                  oCase.Interacting_With__c = testId;
                  oCase.Interacting_With_Type__c = 'SSN';   
            
                    case oCase1 = new case();
                    oCase1.Id = testId;
                    oCase1.AccountID = accList[0].Id;
                    oCase1.Interacting_About_Type__c = 'member';
                    oCase1.Interacting_With__c = testId;
                    oCase1.Interacting_With_Type__c = 'SSN';   
                    oCase1.RecordTypeId = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case'); 

            
            Test.StartTest();                           
             
            HUMCreateCaseServiceResponseHelper response =  HUMCaseService.createCaseInfo(createServiceRequest);
            system.assertNotEquals(response ,NULL);            
                        
            CreateCase_D_HUM.updateMemberAndPolicy(oCase);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL); 
            
            CreateCase_D_HUM.updateCaseDetails(oCase);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL);  
            
            CreateCase_D_HUM.logError(new List<Database.Error>(),'testmethod','testobject','123');
            system.assertNotEquals([Select method_name__c from error_log__c where method_name__c='testmethod'] ,NULL);             
                        
            HUMCreateCaseResponseHelper  logResponse =  CreateCase_D_HUM.getErrorLogInstance('00','test error',true);
            system.assertNotEquals(logResponse ,NULL);
            
            CreateCase_D_HUM.createCaseComment('test coment', testId);            
            system.assertNotEquals([SELECT commentBody   FROM CaseComment   WHERE parentid =: testId ], Null);
            
            //
            Member_Id__c memberAcc1= [SELECT ID, Name FROM Member_Id__c WHERE Name = '360306641' LIMIT 1];
            Id [] fixedSearchResults1= new Id[1];
            fixedSearchResults1[0] = memberAcc1.id;
            Test.setFixedSearchResults(fixedSearchResults1);
                            
            String MemberId = CreateCase_D_HUM.locateMemberByName('test1','method1','01/01/1990', 'WI');  
            system.assertNotEquals(MemberId,Null);
            
            String MemberId1 = CreateCase_D_HUM.locateMemberByName('test7', 'method7', '01/01/1997', 'KY');  
            system.assertNotEquals(MemberId1, Null);
            
            //new
            String MemberId2 = CreateCase_D_HUM.locateMemberByName('','','01/01/1990', 'WI');
            system.assertNotEquals(MemberId2, Null);
            
           	Member_Id__c memberacct = [SELECT ID, Name FROM Member_Id__c WHERE Name = '567234224' LIMIT 1];
            Id [] fixedSearchResults2= new Id[1];
            fixedSearchResults2[0] = memberacct.id;
            Test.setFixedSearchResults(fixedSearchResults2);
            //end
            HUMCreateCaseResponseHelper response2= CreateCase_D_HUM.createCase(NULL);  
            system.assertNotEquals(response2,NULL);
            
           List<Member_ID__c> listmember = [Select id, Name, Type__c, Policy_Member__r.Member__c, Policy_Member__r.Member__r.name,Policy_Member__r.Member__r.firstname,Policy_Member__r.Member__r.lastname, Policy_Member__r.Member__r.Birthdate__c, Policy_Member__r.Member__r.PersonBirthdate from Member_ID__c];
            Map<String,String> memeberResult = CreateCase_D_HUM.getMemberAccountMaps(request,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult ,NULL);    
            CreateCase_D_HUM.createUniqMemberMap('SSN',listmember);
            CreateCase_D_HUM.findMatchingMember(listmember,'01/01/1990','Test','Data');
            CreateCase_D_HUM.getMemberPolicy(request,oCase1);
            CreateCase_D_HUM.createCase(request2);
            
            //add2
            List<Member_Id__c> memberAcc1list2= [SELECT ID, Name FROM Member_Id__c WHERE Name = '360306641' LIMIT 2];
            Id [] fixedSearchResults3= new Id[2];
            fixedSearchResults3[0] = memberAcc1list2[0].id;
            fixedSearchResults3[1] = memberAcc1list2[1].id;
            Test.setFixedSearchResults(fixedSearchResults3);
            CreateCase_D_HUM.locateMember('H46738930', 'Member-Id-Base', '01/01/1997','test7', 'method7') ;
            HUMCreateCaseResponseHelper response3= CreateCase_D_HUM.createCase(NULL);  
            system.assertNotEquals(response3,NULL);
            //
            
            Test.StopTest();
        }
    }

}