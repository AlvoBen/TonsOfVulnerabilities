/*******************************************************************************************************************************
Apex Class Name : CaseProcessEvaluation_LT_HUM
Version         : 1.0
Created On      : 2/13/2023
Function        : This class is test class for CaseProcessEvaluation_LC_Hum

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0            Abhishek Mangutkar                                       2/13/2023               Original Version 
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

@isTest
public class CaseProcessEvaluation_LT_HUM {
	/**
    * Test method to setup common test data for all test methods 
    *
    * @param None       
    * @return  Void
    */               
    @testSetup static void testSetup()
    {
       Group oNewGroup = null;
       oNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
       insert oNewGroup;
        
       QueueSObject oNewQueue = new QueueSObject(QueueId = oNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
       insert oNewQueue;
        
       User oUser = HUMTestDataHelperTest.createTestUser('CaseActionUser1','Customer Care Specialist');
       insert oUser;
       
       system.assertEquals(oUser.lastname == 'test CaseActionUser1',true);  
       
       system.runAs(oUser)
       {
           List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
            
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMConstantOne', IntegerValue__c = 1, StringValue__c = '1'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'Commercial', StringValue__c = 'COM'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'Vision', StringValue__c = 'VIS'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'Life', StringValue__c = 'LIF'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'Dental',  StringValue__c = 'DEN'));  
           lstHUMConstants.add(new HUMConstants__c( Name = 'Medicaid', StringValue__c = 'MCD'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'Medicare',  StringValue__c = 'MCR'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'RSO_PolicyType', StringValue__c = 'MedicareID;MedicaidID'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'SEMICOLON', StringValue__c = ';'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMLimit1000', StringValue__c = '1000', IntegerValue__c = 1000));
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMGSOCommercial',  StringValue__c = 'GSO- Commercial'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMDental', StringValue__c = 'Dental'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMRSOMedicare', StringValue__c = 'RSO- Medicare'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMSpecialty', StringValue__c = 'RSO- Speciality'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMClosed', StringValue__c = 'Closed'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'HUMStatusCancelled', StringValue__c = 'Cancelled'));
           lstHUMConstants.add(new HUMConstants__c( Name = 'Member_Case_Type', StringValue__c = 'Member_Case'));
           insert lstHUMConstants;
           
           System.assertEquals(HUMConstants__c.getInstance('HUMConstantOne').StringValue__c,'1');
           System.assertEquals(HUMConstants__c.getInstance('Commercial').StringValue__c,'COM');
           System.assertEquals(HUMConstants__c.getInstance('Vision').StringValue__c,'VIS');
           System.assertEquals(HUMConstants__c.getInstance('Life').StringValue__c,'LIF');
           System.assertEquals(HUMConstants__c.getInstance('Dental').StringValue__c,'DEN');
           System.assertEquals(HUMConstants__c.getInstance('Medicaid').StringValue__c,'MCD');
           System.assertEquals(HUMConstants__c.getInstance('Medicare').StringValue__c,'MCR');
   
           List<HUMRecommended_Process__c> lstRecommendedProcesses = new List<HUMRecommended_Process__c>();
           lstRecommendedProcesses.add(new HUMRecommended_Process__c( Name = '1', classification__c = 'Claims', intent__c = 'Filling', RecommendedProcess__c = 'Medicare Disenrollment & Cancellation', PolicyType__c = 'RSO- Medicare'));    
           lstRecommendedProcesses.add(new HUMRecommended_Process__c( Name = '2', classification__c = 'Benefits', intent__c = 'Coordination', RecommendedProcess__c = 'Medicare Disenrollment & Cancellation', PolicyType__c = 'GSO- Commercial'));    
           lstRecommendedProcesses.add(new HUMRecommended_Process__c( Name = '3', classification__c = 'G&A', intent__c = 'Expedited', RecommendedProcess__c = 'Medicare Disenrollment & Cancellation', PolicyType__c = 'RSO- Speciality'));
           lstRecommendedProcesses.add(new HUMRecommended_Process__c( Name = '4', classification__c = 'DEAA Plan Change', intent__c = 'Full Dual', RecommendedProcess__c = 'DEAA Medicare Opt Out', PolicyType__c = 'RSO- Medicare'));  
           lstRecommendedProcesses.add(new HUMRecommended_Process__c( Name = '5', classification__c = 'Demographics', RecommendedProcess__c = 'Update Commercial Demographics', PolicyType__c = 'RSO- Medicare'));  
           lstRecommendedProcesses.add(new HUMRecommended_Process__c( Name = '6', classification__c = 'Demographics', RecommendedProcess__c = 'Update Plan Demographics', PolicyType__c = 'GSO- Commercial'));  
           lstRecommendedProcesses.add(new HUMRecommended_Process__c( Name = '7', classification__c = 'Demographics', RecommendedProcess__c = 'Update Plan Demographics', PolicyType__c = 'Dental')); 
           insert lstRecommendedProcesses;
           System.assertEquals(HUMRecommended_Process__c.getInstance('1').PolicyType__c, 'RSO- Medicare');
           
           
           
   
           Account oAccount = HUMTestDataHelperTest.getAccount();
           oAccount.Name = 'New Case Account';
           oAccount.Group_Number__c  = '123';
           oAccount.BillingState = 'New York';
           oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
           insert oAccount;
           System.assert(oAccount.Name == 'New Case Account');
                  
           Account mAccount = HUMTestDataHelperTest.getAccount();
           mAccount.FirstName = 'General';
           mAccount.LastName  = 'Member';
           mAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
           insert mAccount;
           System.assert(mAccount.FirstName == 'General');

            //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name ='MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
            insert objMedProduct;
            System.assert(objMedProduct.Product_Type__c == 'Med');

            //Medical Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890';
            oMedicalPolicy.Group_Name__c = mAccount.Id;
            oMedicalPolicy.Product__c = objMedProduct.Id;
            oMedicalPolicy.Major_LOB__c = 'EPO';
            insert oMedicalPolicy;
            System.assert(oMedicalPolicy.Name == 'Medical Product');

           //Policy_Plan_Detail__c - Added for test coverage
            Policy_Plan_Details__c MemberPlan = new Policy_Plan_Details__c();
            MemberPlan.Name = 'Medical Policy Plan Detail';
            MemberPlan.ETL_Record_Deleted__c = false;
            MemberPlan.Policy_Plan_External_ID__c = 'EFGH';
            MemberPlan.Coverage_Plan_End_Date__c = '12/31/2016';
            MemberPlan.Coverage_Plan_Effective_Date__c = '2/12/2016';
            MemberPlan.Policy__c = oMedicalPolicy.Id;
            MemberPlan.Product__c = objMedProduct.Id;
            MemberPlan.Benefit_Coverage__c = 'FL103104';
            insert MemberPlan;
            System.assert(MemberPlan.Name == 'Medical Policy Plan Detail');
            
          //Medical Member Plan , SubscriberId, EffectiveT
            MemberPlan objMemberPlan = New MemberPlan();
            objMemberPlan.Member = mAccount;
            objMemberPlan.Policy__c = oMedicalPolicy.Id;
            objMemberPlan.EffectiveTo =Date.valueOf('2025-12-31');
            objMemberPlan.EffectiveFrom = Date.valueOf('2015-12-31');
            objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|123456789|89|89|02-01-2001';
            objMemberPlan.name = '123456789';
            objMemberPlan.MemberId = mAccount.id;
            insert objMemberPlan;
            System.assert(objMemberPlan.Member_Plan_External_Id__c == '00|ODS|123456789|89|89|02-01-2001');
           
           Account mAccountUnknown = HUMTestDataHelperTest.getAccount();
           mAccountUnknown.FirstName = 'General1';
           mAccountUnknown.LastName  = 'Member';
           mAccountUnknown.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();
           insert mAccountUnknown;
           System.assert(mAccountUnknown.FirstName == 'General1');
   
           Product2 oProduct = new Product2();
           oProduct.Name = 'Mediclaim';
           oProduct.ProductCode = 'MED';
           oProduct.Major_LOB__c = 'MGR';
           oProduct.Segment_Type__c = 'COM';
           insert oProduct;
           System.assert(oProduct.Name == 'Mediclaim'); 
           
            Product2 oProductMEDPDP = new Product2();
           oProductMEDPDP.Name = 'MEDPDP';
           oProductMEDPDP.ProductCode = 'MED';
           oProductMEDPDP.Major_LOB__c = 'PDP';
           oProductMEDPDP.Segment_Type__c = 'COM';
           insert oProductMEDPDP ;
           System.assert(oProductMEDPDP .Name == 'MEDPDP'); 
           
           Product2 oProductMEDMCD = new Product2();
           oProductMEDMCD.Name = 'MEDMCD';
           oProductMEDMCD.ProductCode = 'MED';
           oProductMEDMCD.Major_LOB__c = 'MCD';
           oProductMEDMCD.Segment_Type__c = 'MCD';
           insert oProductMEDMCD ;
           System.assert(oProductMEDMCD.Name == 'MEDMCD'); 
   
           Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
           oPolicy.Name = 'test123';
           oPolicy.Group_Name__c = oAccount.Id;
           oPolicy.Funding_Type_Code__c = 'Y';
           oPolicy.Product__c = oProduct.Id;
           oPolicy.Issue_State__c = 'IL'; 
           insert oPolicy;
           System.assert(oPolicy.Name == 'test123'); 
   
           Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
           oPolicyMem.Name = '12345';
           oPolicyMem.Member__c = mAccount.Id;
           oPolicyMem.Policy__c = oPolicy.Id;            
           oPolicyMem.Relationship__c = 'Self';
           insert oPolicyMem;
           System.assert(oPolicyMem.Name == '12345'); 
   
           Member_ID__c oMemID = new Member_ID__c();  
           oMemID.Policy__c = oPolicy.id;
           oMemID.Name ='1234567890';
           oMemID.Policy_Member__c = oPolicyMem.id; 
           oMemID.Type__c = 'MedicareID';
           oMemID.Member_Card_External_ID__c = '1231';
           insert oMemID;  
           System.assert(oMemID.Type__c == 'MedicareID');
           
            Classification__c classificationRec = New Classification__c();
           classificationRec.Name = 'Claims';
           classificationRec.Is_Active__c = true;
           insert classificationRec;
           System.assertEquals(classificationRec.Name,'Claims');
           
           Intent__c intentRec = New Intent__c();
           intentRec.Name = 'Filling';
           intentRec.Is_Active__c = true;
           insert intentRec;
           System.assertEquals(intentRec.Name,'Filling');
           
           Classification_Type__c objcT = new Classification_Type__c();
           objcT.Name ='Calls';
           objcT.Is_Active__c = true;
           insert objcT;
           System.assertEquals(objcT.Name,'Calls');
           
           Classification_Type__c objcCallRSO = new Classification_Type__c();
           objcCallRSO.Name ='Calls (RSO)';
           objcCallRSO.Is_Active__c = true;
           insert objcCallRSO;
           System.assertEquals(objcCallRSO.Name,'Calls (RSO)');
           
           Classification_Type__c EnrollmentRSO = new Classification_Type__c();
           EnrollmentRSO.Name ='Enrollment (RSO)';
           EnrollmentRSO.Is_Active__c = true;
           insert EnrollmentRSO;
           System.assertEquals(EnrollmentRSO.Name,'Enrollment (RSO)');
           
           CTCI_Junction__c objCTCI = new CTCI_Junction__c();
           objCTCI.Classification__c=classificationRec.id;
           objCTCI.Classification_Type__c= objcT.id;
           objCTCI.Intent__c =intentRec.id;
           objCTCI.IsActive__c=true;
           insert objCTCI ;
           system.assertNotEquals(Null,objCTCI.id);

           Classification__c classificationDEAA = New Classification__c();
           classificationDEAA.Name = 'DEAA Plan Change';
           classificationDEAA.Is_Active__c = true;
           insert classificationDEAA;
           System.assertEquals(classificationDEAA.Name,'DEAA Plan Change');
           
           Intent__c intentDEAA = New Intent__c();
           intentDEAA.Name = 'Full Dual';
           intentDEAA.Is_Active__c = true;
           insert intentDEAA;
           System.assertEquals(intentDEAA.Name,'Full Dual');
           
           CTCI_Junction__c objCTCIDEAA = new CTCI_Junction__c();
           objCTCIDEAA.Classification__c=classificationDEAA.id;
           objCTCIDEAA.Classification_Type__c= EnrollmentRSO.id;
           objCTCIDEAA.Intent__c =intentDEAA.id;
           objCTCIDEAA.IsActive__c=true;
           insert objCTCIDEAA ;
           system.assertNotEquals(Null,objCTCIDEAA.id);            

           Classification__c classificationRecDemo = New Classification__c();
           classificationRecDemo.Name = 'Website (CT & TM)';
           classificationRecDemo.Is_Active__c = true;
           insert classificationRecDemo;
           System.assertEquals(classificationRecDemo.Name,'Website (CT & TM)');
           
           Intent__c intentDemoChange = New Intent__c();
           intentDemoChange.Name = 'Web Education/Navigation';
           intentDemoChange.Is_Active__c = true;
           insert intentDemoChange;
           System.assertEquals(intentDemoChange.Name,'Web Education/Navigation');
           
           CTCI_Junction__c objCTCIDemo = new CTCI_Junction__c();
           objCTCIDemo.Classification__c=classificationRecDemo.id;
           objCTCIDemo.Classification_Type__c= EnrollmentRSO.id;
           objCTCIDemo.Intent__c =intentDemoChange.id;
           objCTCIDemo.IsActive__c=true;
           insert objCTCIDemo ;
           system.assertNotEquals(Null,objCTCIDemo.id);
           
           CRMFunctionality_ONOFF_Switch__c objCS1 = new CRMFunctionality_ONOFF_Switch__c();
           objCS1.Name = '846171';
           objCS1.IsON__c=true;
           objCS1.Release_Name__c='07/07/2020';
           objCS1.Scrum_Team__c='CRM Transformers';
           objCS1.Story_Title__c='Add a Flag to Turn Off and On - MF 2- Member Maint.- Quick Start Options/Link in Available Processes to navigate to "Update Demo" Template in CRM';
           insert objCS1;

           Case oCase = New Case(AccountId = mAccount.Id, Interacting_About_Type__c = 'Member', Origin = 'Inbound Call', 
                              Type = 'Customer Inquiry', CTCI_List__c= objCTCI.id,
                              Policy_Member__c = oPolicyMem.Id, Interacting_With_Type__c = 'Member',
                              Interacting_With__c = mAccount.Id, Due_Date__c = Date.Today(), Classification_Type__c = 'Enrollment (RSO)',
                              Priority = 'Normal', Line_of_Business__c = 'Commercial', G_A_Rights_Given__c = 'Yes', 
                              G_A_Reason__c = 'All Others', Complaint__c = 'Yes - Medicare Part C', Complaint_Reason__c = 'Clinical Programs',
                              Behavioral_Health__c = 'None',status = 'In Progress');
           oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
                 
           insert oCase;
           
           
            Case oCasePro = New Case(AccountId = oAccount.Id, CTCI_List__c= objCTCI.id ,
                                  Interacting_With_Type__c = 'Group', Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),
                                  Case_Owner__c=UserInfo.getUserName(), 
                                  Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
                                  oCase.Complaint__c = 'Yes'                   ;
                                  oCase.Complaint_Reason__c = 'Claims';
                                  oCase.Classification_Type__c = 'Calls';
                                  oCase.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
                                  oCase.G_A_Rights_Given__c ='No';

           insert oCasePro ;   
           
            Case oCasePro1 = New Case(AccountId = mAccount.Id, CTCI_List__c= objCTCI.id ,
                                  Interacting_With_Type__c = 'Group', Interacting_With__c=mAccount.Id, Due_Date__c=Date.Today(),
                                  Case_Owner__c=UserInfo.getUserName(), 
                                  Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
                                  oCasePro1.Complaint__c = 'Yes'                   ;
                                  oCasePro1.Complaint_Reason__c = 'Claims';
                                  oCasePro1.Classification_Type__c = 'Calls (RSO)';
                                  oCasePro1.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
                                  oCasePro1.G_A_Rights_Given__c ='No';

           insert oCasePro1 ;  
           
            Case oCasePro2 = New Case(AccountId = mAccountUnknown.Id, CTCI_List__c= objCTCI.id ,
                                  Interacting_With_Type__c = 'Unknown-Member', Interacting_With__c=mAccountUnknown.Id, Due_Date__c=Date.Today(),
                                  Case_Owner__c=UserInfo.getUserName(), 
                                  Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
                                  oCasePro2.Complaint__c = 'Yes'                   ;
                                  oCasePro2.Complaint_Reason__c = 'Claims';
                                  oCasePro2.Classification_Type__c = 'Calls (RSO)';
                                  oCasePro2.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Unknown Case').getRecordTypeId();
                                  oCasePro2.G_A_Rights_Given__c ='No';

           insert oCasePro2 ;  

           Case oCasePro3 = New Case(AccountId = mAccountUnknown.Id, CTCI_List__c= objCTCI.id ,
                                  Interacting_With_Type__c = 'Unknown-Member', Interacting_With__c=mAccountUnknown.Id, Due_Date__c=Date.Today(),
                                  Case_Owner__c=UserInfo.getUserName(), 
                                  Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress', Origin='CRMTRR');
                                  oCasePro2.Complaint__c = 'Yes'                   ;
                                  oCasePro2.Complaint_Reason__c = 'Claims';
                                  oCasePro2.Classification_Type__c = 'Calls (RSO)';
                                  oCasePro2.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Unknown Case').getRecordTypeId();
                                  oCasePro2.G_A_Rights_Given__c ='No';

           insert oCasePro3 ;

           Case oCasePro4 = new Case(AccountId = mAccount.Id, Interacting_About_Type__c = 'Member', Origin = 'DEAA', 
                              Type = 'Customer Inquiry', CTCI_List__c= objCTCIDEAA.id,
                              Policy_Member__c = oPolicyMem.Id, Interacting_With_Type__c = 'Member',
                              Interacting_With__c = mAccount.Id, Due_Date__c = Date.Today(), Classification_Type__c = 'Enrollment (RSO)',
                              Priority = 'Normal', Line_of_Business__c = 'Commercial', G_A_Rights_Given__c = 'No', 
                              Complaint__c = 'No',
                              Behavioral_Health__c = 'None',status = 'In Progress');
           oCasePro4.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
                 
           insert oCasePro4;

            Case oCasePro5 = New Case(AccountId = mAccount.Id, Interacting_About_Type__c = 'Member', Origin = 'Inbound Call', 
                              Type = 'Customer Inquiry', CTCI_List__c= objCTCIDemo.id,
                              Policy_Member__c = oPolicyMem.Id, Interacting_With_Type__c = 'Member',
                              Interacting_With__c = mAccount.Id, Due_Date__c = Date.Today(), Classification_Type__c = 'Enrollment (RSO)',
                              Priority = 'Normal', Line_of_Business__c = 'Commercial', G_A_Rights_Given__c = 'Yes', 
                              G_A_Reason__c = 'All Others', Complaint__c = 'Yes - Medicare Part C', Complaint_Reason__c = 'Clinical Programs',
                              Behavioral_Health__c = 'None',status = 'In Progress');
           oCasePro5.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();

           insert oCasePro5 ;

           Case oCasePro6 = New Case(AccountId = mAccount.Id, Interacting_About_Type__c = 'Member', Origin = 'Inbound Call', 
                              Type = 'Customer Inquiry', CTCI_List__c= objCTCIDemo.id,
                              Policy_Member__c = oPolicyMem.Id, Interacting_With_Type__c = 'Member',
                              Interacting_With__c = mAccount.Id, Due_Date__c = Date.Today(), Classification_Type__c = 'Humana Pharmacy Calls',
                              Priority = 'Normal', Line_of_Business__c = 'Commercial', G_A_Rights_Given__c = 'Yes', 
                              G_A_Reason__c = 'All Others', Complaint__c = 'Yes - Medicare Part C', Complaint_Reason__c = 'Clinical Programs',
                              Behavioral_Health__c = 'None',status = 'In Progress');
           oCasePro6.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();

           insert oCasePro6 ;
           System.assert(oCasePro5.status == 'In Progress');  

           System.assert(oCasePro6.status == 'In Progress');  

           Template__c t = new Template__c();
           t.Description__c = 'DEAA Medicare Opt Out';

           Insert t;
       
           Template_Field__c tf = new Template_Field__c();
           tf.Template__c = t.Id;
           tf.Data_Type__c = 'String';
           tf.Name = 'Name';
           Insert tf;

           Template_Field__c tf1 = new Template_Field__c();
           tf1.Template__c = t.Id;
           tf1.Data_Type__c = 'String';
           tf1.Name = 'SubmissionData';
           Insert tf1;
       
           Template_Submission_Owner__c tso = new Template_Submission_Owner__c();
           tso.Object_Owner_ID__c = oCasePro6.Id;
           tso.Object_Owner_Type__c = 'Case';
       
           Insert tso;
       
           Template_Submission__c ts = new Template_Submission__c();
           ts.Template__c = t.Id;
           ts.Submission_Owner__c = tso.Id;
       
           insert ts;
           
           Template_Submission_Data__c tempData = new Template_Submission_Data__c(Template_Field__c=tf.Id, Template_Submission__c = ts.Id, Value__c = 'Y');
           insert tempData;
           
           Template_Submission_Data__c tempData1 = new Template_Submission_Data__c(Template_Field__c=tf1.Id, Template_Submission__c = ts.Id, Value__c = 'Y');
           insert tempData1;

            Work_Queue_Setup__c oWorkQueue = New Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=objcCallRSO.Id);
           insert oWorkQueue;  
           
       } 
        
        Work_Queue_Setup__c oInsertedQueue = [SELECT Id, Name, OwnerId
                                             FROM Work_Queue_Setup__c
                                             WHERE Name = 'Test Business Process and Testing'];
       oUser.Current_Queue__c = oInsertedQueue.Name;
       update oUser;
   }
    
    @IsTest
   static void testGetCaseDetails(){
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        Assert.isNotNull(oUser.Id, 'User Id is present');
       
       System.runAs(oUser){
        Test.startTest();
        Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
        Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
        Policy_Member__c, Interacting_With_Type__c,
        Interacting_With__c, Due_Date__c, Classification_Type__c,
        Priority, Line_of_Business__c, G_A_Rights_Given__c, 
        G_A_Reason__c, Complaint__c, Complaint_Reason__c,
        Behavioral_Health__c, status, RecordTypeId, recordType.developerName, RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and Classification_Type__c = 'Humana Pharmacy Calls' and RecordTypeId = :Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId() limit 1];
        Assert.isTrue(oCase.status == 'In Progress');
        CaseProcessEvaluation_LC_Hum objCaseLaunch = new CaseProcessEvaluation_LC_Hum();
        List<CaseProcessEvaluation_LC_Hum.CaseDetails_DTO_HUM> caseDetails = CaseProcessEvaluation_LC_Hum.getCaseDetails(oCase.Id);
        Assert.areEqual(oCase.status, caseDetails[0].status);
        Test.stopTest();
       }
   }
    
    @IsTest
   static void testInitialDetails(){
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        Assert.isNotNull(oUser.Id, 'User Id is present');
       
       System.runAs(oUser){
        Test.startTest();
        Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
        Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
        Policy_Member__c, Interacting_With_Type__c,
        Interacting_With__c, Due_Date__c, Classification_Type__c,
        Priority, Line_of_Business__c, G_A_Rights_Given__c, 
        G_A_Reason__c, Complaint__c, Complaint_Reason__c,
        Behavioral_Health__c, status, RecordTypeId, recordType.developerName, RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and Classification_Type__c = 'Humana Pharmacy Calls' and RecordTypeId = :Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId() limit 1];
        Assert.isTrue(oCase.status == 'In Progress');
        CaseProcessEvaluation_LC_Hum objCaseLaunch = New CaseProcessEvaluation_LC_Hum();
        List<MemberPlan> lstMemberPlan = [select id from memberplan where name=:'123456789'];
        List<CaseProcessEvaluation_LC_Hum.CaseProcessWrapper_DTO_HUM> caseDetails = CaseProcessEvaluation_LC_Hum.getInitialDetails(oCase.Id,lstMemberPlan[0]?.Id);
        Assert.areEqual(oCase.status, caseDetails[0].lstCaseDetails[0].status);
        Test.stopTest();
       }
   }
}