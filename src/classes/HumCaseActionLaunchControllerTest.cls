/**********************************************************************************************************************
Apex Class: HumCaseActionLaunchControllerTest
Description: Test class for HumCaseActionLaunchController
Created By: Chaitanya Kumar
Created On:  July 14 2014

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer            Code Review         Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Chaitanya Kumar       17353            08/14/2014               Original version
* Chaitanya Kumar       17840            09/10/2014               Made changes according to Humana standards
* Krishna Tadikonda                      10/08/2014               Creating StandardController and passing as parameter to LaunchController constructor.
* Asha Deepika Koyyana  20579            01/28/2015               Fixed the test class failure  
* Pooja Wadhwa          23336            05/22/2015               Updated as per REQ - 121512  & refactored the test class w.r.t standards 
* Rakesh Patni          23345            05/25/2015               Updated as per REQ- 158397.
* Pooja Wadhwa          23831            06/10/2015               Updated code as per new fix to retrieve LOB value from Product
* Naveen B Kanth        30848            04/05/2016               REQ - 252538 : Updated test class to accommodate the validation rule.
* Avinash Choubey       33344            07/11/2016               Changes made as part of REQ- 255151
* Subhamay Mandal                        03/10/2017               Added new test menthod to validate getMentorLinks method. REQ - 303482
* Ranjeeth Nagishetty                    04/06/2017               REQ - 306327 Changes to CTCI
* Ranjeeth Nagishetty                    10/04/2017               REQ - 311453 Good Cause Reinstatement Template for CRM
* Bharath Rajappan                       03/22/2018               REQ - 357304 Creditable Coverage Link test methods added
* Sunil Bukya           244531           06/17/2018               REQ - 366900 - Implemented Member_Id__c platform encryption changes.
* Darshan Sharma                         10/01/2018               REQ - 374820: CTCI Model Update (RF+CRM)
* Pallavi Ravishankar   338657           02/26/2019               REQ - 383432 - Medicaid Good Cause Request Process
* Santhi Mandava                         03/12/2019               Removed "DpaaSWebServiceMockImpl" usage.
* Moshitha Gunasekaran  348700           03/25/2019               REQ - 380742 - Ability to view Transaction Reply Report (TRR) Process data for a specific member on a Medicare case
* Santhi Mandava                         04/15/2019               Replaced "Member_Id_Value__c" with Name 
* Moshitha Gunasekaran                   01/29/2020               Story - 725129 - DEAA Medicare Opt Out process display in CRM for DEAA (Dual Eligible Auto Assigned) cases
* Mohammed Noor                          03/27/2020               US#975731 - Add DEAA OnOff Switch changes
* Mohammed Noor                          04/24/2020               US#1020879 - Remove DEAA OnOff Switch  
* Jasmeen Shangari                       04/29/2020               Rename iCreditableConstant to iProcessSeqNo to resolve Checkmarx error
* Pooja Kumbhar                          05/29/2020               Member Maintainance changes for Demographic links
* Santhi Mandava                         01/02/2021               Fixed coverage issue
* Atia Uzma                              03/23/2021               User Story 1898796: T1PRJ0001827 - MF 1 - RF Clean Up (RF)
**************************************************************************************************************************************************************/
@isTest
private class HumCaseActionLaunchControllerTest
{
    
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
            
            MET_Enrollment_Launch_EMME_URL__c METLink = new MET_Enrollment_Launch_EMME_URL__c();
            METLink.Name = 'Non_ME_Create_EMME_URL';
            METLink.Launch_EMME_URL__c = 'http://qa-hss.humana.com/Emme/CreateMessage/Create?&role=V1&OtherTypeCd=V2&mbrNo=V3&Suffix=V4&platform=V5&grpNo=V6&CCPInquiryNo=V7';
            insert METLink;
            
            Endpoint_Url_HUM__c e = new Endpoint_Url_HUM__c();
            e.Name = 'SSO_LOGINURL';
            e.Remote_Url__c = 'http://test-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx';
            insert e;
            
    
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
            classificationRecDemo.Name = 'Demographics';
            classificationRecDemo.Is_Active__c = true;
            insert classificationRecDemo;
            System.assertEquals(classificationRecDemo.Name,'Demographics');
            
            Intent__c intentDemoChange = New Intent__c();
            intentDemoChange.Name = 'Change';
            intentDemoChange.Is_Active__c = true;
            insert intentDemoChange;
            System.assertEquals(intentDemoChange.Name,'Change');
            
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
            oCasePro5.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();

            insert oCasePro5 ;
            System.assert(oCasePro5.status == 'In Progress');  

            System.assert(oCasePro.status == 'In Progress');  

            Template__c t = new Template__c();
            t.Description__c = 'DEAA Medicare Opt Out';

            Insert t;
        
            Template_Field__c tf = new Template_Field__c();
            tf.Template__c = t.Id;
            tf.Data_Type__c = 'String';
            tf.Name = 'Name';
            Insert tf;
        
            Template_Submission_Owner__c tso = new Template_Submission_Owner__c();
            tso.Object_Owner_ID__c = oCasePro4.Id;
            tso.Object_Owner_Type__c = 'Case';
        
            Insert tso;
        
            Template_Submission__c ts = new Template_Submission__c();
            ts.Template__c = t.Id;
            ts.Submission_Owner__c = tso.Id;
        
            insert ts;
            
            Template_Submission_Data__c tempData = new Template_Submission_Data__c(Template_Field__c=tf.Id, Template_Submission__c = ts.Id, Value__c = 'Y');
            insert tempData;

             Work_Queue_Setup__c oWorkQueue = New Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=objcCallRSO.Id);
            insert oWorkQueue;  
            
        } 
         
         Work_Queue_Setup__c oInsertedQueue = [SELECT Id, Name, OwnerId
                                              FROM Work_Queue_Setup__c
                                              WHERE Name = 'Test Business Process and Testing'];
        oUser.Current_Queue__c = oInsertedQueue.Name;
        update oUser;
    }
     
   
    /**
    * This method is used to initialize variables 
    *
    * @param None       
    * @return  Void
    */
    static testMethod void constructorTest() 
    {
        User oUser = [select id, name from user where lastname='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
        
        system.runAs(oUser)
        {
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                               Type, CTCI_List__r.Classification__c, CTCI_List__c, Intent_ID__r.name,CTCI_List__r.Intent__c, CTCI_List__r.Classification__r.name,
                               Policy_Member__c, Interacting_With_Type__c,CTCI_List__r.Intent__r.Name,
                               Interacting_With__c, Due_Date__c, Classification_Type__c,
                               Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                               G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                               Behavioral_Health__c, status, RecordTypeId, recordType.developerName, RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' limit 1];
            System.assert(oCase.status == 'In Progress');  
            
            List<Reference_Business_Line_Product_Grouping__c> lstRBLP = new List<Reference_Business_Line_Product_Grouping__c>();
            lstRBLP.add(new Reference_Business_Line_Product_Grouping__c(Name = 'RSO- Medicare', Business_Description__c = 'RSO- Medicare', Major_LOB__c = 'MGR', ProductCode__c = 'MED'));       
           
            insert lstRBLP;
            System.assertEquals(lstRBLP.size(), 1, 'Value is not matched');
             
            PageReference pg =Page.HUMCaseActionProcessIntermediate; 
            Test.setCurrentPageReference(pg);
          
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            HumCaseActionLaunchController launchController ;
          
            Test.startTest();
                Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
                launchController = new HumCaseActionLaunchController(controller);
            
                launchController.caseRec.Intent_ID__c = oCase.intent_ID__c;
                launchController.caseRec.Classification_ID__c = oCase.classification_Id__c;
                   
                System.assertEquals(launchController.sRecommendedProcess, 'Medicare Disenrollment & Cancellation');
            Test.stopTest();
        }
    }
    
    /**
    * This method is used to validate getMentorLinks from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testGetMentorLinks() {
        MentorDocumentLinks__c mentor_links1 = new MentorDocumentLinks__c(Name='Medicare Authorization Calls1', Document_Label__c='Medicare Authorization Calls', Document_Link__c='http://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092980db1698&dl=0&searchID=VI-8d446b54f5180ce&row=0&mode=Mentor', Classification__c='Claims', Intent__c='Filling');
        insert mentor_links1;
        MentorDocumentLinks__c mentor_links2 = new MentorDocumentLinks__c(Name='VOB Inquiry Initial Call1', Document_Label__c='VOB Inquiry Initial Call', Document_Link__c='http://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092980d7e522&dl=0&searchID=VI-8d446b52b4e16ea&row=0&mode=Mentor', Classification__c='Benefits', Intent__c='Accums');
        insert mentor_links2;
        
        User oUser = [select id, name from user where lastname = 'test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
        
        system.runAs(oUser) {
            PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.QUICKSTARTACCESS_PERMISSIONSET_HUM];
            insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps.Id );
            
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                               Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                               Policy_Member__c, Interacting_With_Type__c,
                               Interacting_With__c, Due_Date__c, Classification_Type__c,
                               Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                               G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                               Behavioral_Health__c, status, RecordTypeId, recordType.developerName, RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' limit 1];
            
            System.assert(oCase.status == 'In Progress');
             
            PageReference pageRef = Page.HUMCaseActionLaunch; 
            Test.setCurrentPageReference(pageRef);
          
            Apexpages.currentPage().getParameters().put('Classification', 'Claims');
            Apexpages.currentPage().getParameters().put('Intent', 'Filling');
            HumCaseActionLaunchController launchController ;
          
             Test.startTest();
                Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
                launchController = new HumCaseActionLaunchController(controller);
                launchController.getMentorLinks();
                launchController.getRecord();
                launchController.getRecords();
                launchController.getThis();
                launchController.bShowLinks=true;
                launchController.bShowCommercialLinks =true;
                launchController.bShowSpecialityLinks =true;
                launchController.bShowPDPLinks = true;
                launchController.bShowMCDLinks = true;
            Test.stopTest();
        }
    }
    
    /**
    * This method is used to validate creditable link for known member from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testCreditableLinkforMember() {
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
      
        system.runAs(oUser) 
        {       
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                           Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c,
                           Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                           G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                           Behavioral_Health__c, status, RecordTypeId, recordType.developerName,RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and Classification_Type__c = 'Calls (RSO)' and Account.RecordType.Name = 'Member' limit 1];
            System.assert(oCase.status == 'In Progress');

            PageReference pageRef = Page.HUMCaseActionLaunch; 
            Test.setCurrentPageReference(pageRef);

            Apexpages.currentPage().getParameters().put('Classification', 'Claims');
            Apexpages.currentPage().getParameters().put('Intent', 'Filling');
            HumCaseActionLaunchController launchController ;

            Test.startTest();
            Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
            launchController = new HumCaseActionLaunchController(controller);                
            System.assert(launchController.bShowCreditableCoverageLinks);           
            System.assertEquals(launchController.iProcessSeqNo,1) ;
            Test.stopTest();
                       
        }
    }  
    
    /**
    * This method is used to validate creditable link for Unknown member from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testCreditableLinkForUnKnownMember() {
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
      
        PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_TESTER_PERMISSIONSET];
        insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps.Id );
                
        system.runAs(oUser) 
        {     
            
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                           Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c,
                           Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                           G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                           Behavioral_Health__c, status, RecordTypeId, recordType.developerName, RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and Classification_Type__c = 'Calls (RSO)' and RecordTypeId = :Schema.SObjectType.Case.getRecordTypeInfosByName().get('Unknown Case').getRecordTypeId() limit 1];
            System.assert(oCase.status == 'In Progress');

            PageReference pageRef = Page.HUMCaseActionLaunch; 
            Test.setCurrentPageReference(pageRef);

            Apexpages.currentPage().getParameters().put('Classification', 'Claims');
            Apexpages.currentPage().getParameters().put('Intent', 'Filling');
            HumCaseActionLaunchController launchController ;

            Test.startTest();
            Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
            launchController = new HumCaseActionLaunchController(controller);                
            System.assert(launchController.bShowCreditableCoverageLinksUnKnown); 
            Test.stopTest();
                       
        }
    } 
    
    /**
    * This method is used to validate for TRR Origin 
    * @param None       
    * @return  Void
    */
    static testMethod void testTRRTemplate() {
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null); 
        system.runAs(oUser) 
        {     
            
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                           Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c,
                           Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                           G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                           Behavioral_Health__c, status, RecordTypeId, recordType.developerName, RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where origin = 'CRMTRR' limit 1];
            System.assert(oCase.Origin == 'CRMTRR');
            Test.startTest();
            Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
            HumCaseActionLaunchController launchController = new HumCaseActionLaunchController(controller);                
            Test.stopTest();
        }
    }

            /**
    * This method is used to validate creditable link for known member from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testDEAALinkforMember() {
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
      
        system.runAs(oUser) 
        {       
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                           Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c,
                           Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                           G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                           Behavioral_Health__c, status, RecordTypeId, recordType.developerName,RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and Classification_Type__c = 'Enrollment (RSO)' and CTCI_List__r.Classification__r.name = 'DEAA Plan Change' and Account.RecordType.Name = 'Member' limit 1];
            System.assert(oCase.status == 'In Progress');

            PageReference pageRef = Page.HUMCaseActionLaunch; 
            Test.setCurrentPageReference(pageRef);
            
            HumCaseActionLaunchController launchController ;

            Test.startTest();
            Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
            launchController = new HumCaseActionLaunchController(controller);                
            System.assert(launchController.bShowDEAALink);           
            Test.stopTest();
                       
        }
    }
    
        
     /**
    * This method is used to validate Update Plan Demographics Recommended section links from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testDemographicRecommendedRSO() {
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
      
        PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.MM_CRMS_COMMPERM_NAME];
        insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps.Id );
                
        system.runAs(oUser) 
        {       
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                           Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                           Policy_Member__c, Interacting_With_Type__c,intent_ID__c,classification_Id__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c,
                           Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                           G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                           Behavioral_Health__c, status, RecordTypeId, recordType.developerName,RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and CTCI_List__r.Classification__r.name = 'Demographics' and Classification_Type__c = 'Enrollment (RSO)' and Account.RecordType.Name = 'Member' limit 1];
           
            System.assert(oCase.status == 'In Progress');
            
            List<Reference_Business_Line_Product_Grouping__c> lstRBLP = new List<Reference_Business_Line_Product_Grouping__c>();
            lstRBLP.add(new Reference_Business_Line_Product_Grouping__c(Name = 'RSO- Medicare', Business_Description__c = 'RSO- Medicare', Major_LOB__c = 'MGR', ProductCode__c = 'MED'));    
            insert lstRBLP;
            System.assertEquals(lstRBLP.size(), 1, 'Value is not matched');
             
            PageReference pg =Page.HUMCaseActionProcessIntermediate; 
            Test.setCurrentPageReference(pg);
          
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            HumCaseActionLaunchController launchController ;
          
            test.startTest();
            Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
            launchController = new HumCaseActionLaunchController(controller);
            
            launchController.caseRec.Intent_ID__c = oCase.intent_ID__c;
            launchController.caseRec.Classification_ID__c = oCase.classification_Id__c;
            launchController.bShowCommercialDemographicRecommended = true;
            launchController.populateRecommendedProcess('RSO- Medicare');  
            
            
            launchController.caseRec.recordType.developerName = 'HP_Member_Case';
            launchController.populateLinks();
            
            Test.stopTest();
        }
    }
    
    /**
    * This method is used to validate Update Plan Demographics Recommended section links from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testDemographicRecommendedGSO() {
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
      
        PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.MM_CRMS_MEDPERM_NAME ];
        insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps.Id );
                
        system.runAs(oUser) 
        {       
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                           Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                           Policy_Member__c, Interacting_With_Type__c,intent_ID__c,classification_Id__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c,
                           Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                           G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                           Behavioral_Health__c, status, RecordTypeId, recordType.developerName,RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and CTCI_List__r.Classification__r.name = 'Demographics' and Classification_Type__c = 'Enrollment (RSO)' and Account.RecordType.Name = 'Member' limit 1];
            System.assert(oCase.status == 'In Progress');
            
            List<Reference_Business_Line_Product_Grouping__c> lstRBLP = new List<Reference_Business_Line_Product_Grouping__c>();
            lstRBLP.add(new Reference_Business_Line_Product_Grouping__c(Name = 'GSO - Commercial', Business_Description__c = 'GSO- Commercial', Major_LOB__c = 'MGR', ProductCode__c = 'MED'));    
            insert lstRBLP;
            System.assertEquals(lstRBLP.size(), 1, 'Value is not matched');
             
            PageReference pg =Page.HUMCaseActionProcessIntermediate; 
            Test.setCurrentPageReference(pg);
          
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            HumCaseActionLaunchController launchController ;
          
            test.startTest();
            Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
            launchController = new HumCaseActionLaunchController(controller);
            
            launchController.caseRec.Intent_ID__c = oCase.intent_ID__c;
            launchController.caseRec.Classification_ID__c = oCase.classification_Id__c;
            launchController.bShowDemographicRecommended = true;
            launchController.populateRecommendedProcess('GSO- Commercial');      
            Test.stopTest();
        }
    }
    
    /**
    * This method is used to validate Update Plan Demographics Recommended section links from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testDemographicRecommendedDental() {
        User oUser = [select id, name from user where lastname ='test CaseActionUser1' limit 1];
        system.assert(oUser.Id!= null);
      
        PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.MM_CRMS_COMMPERM_NAME];
        insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps.Id ); 
            
        PermissionSet ps1 = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.MM_CRMS_MEDPERM_NAME];
        insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps1.Id );  
        
        PermissionSet ps2 = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM];
        insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps2.Id );   
                
        system.runAs(oUser) 
        {       
            Case oCase = [select AccountId, Interacting_About_Type__c, Origin, 
                           Type, CTCI_List__c,CTCI_List__r.Intent__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.name,
                           Policy_Member__c, Interacting_With_Type__c,intent_ID__c,classification_Id__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c,
                           Priority, Line_of_Business__c, G_A_Rights_Given__c, 
                           G_A_Reason__c, Complaint__c, Complaint_Reason__c,
                           Behavioral_Health__c, status, RecordTypeId, recordType.developerName,RecordType.Name,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Group_Number__c,CaseNumber from Case where status = 'In Progress' and CTCI_List__r.Classification__r.name = 'Demographics' and Classification_Type__c = 'Enrollment (RSO)' and Account.RecordType.Name = 'Member' limit 1];
            System.assert(oCase.status == 'In Progress');
            
            List<Reference_Business_Line_Product_Grouping__c> lstRBLP = new List<Reference_Business_Line_Product_Grouping__c>();
            lstRBLP.add(new Reference_Business_Line_Product_Grouping__c(Name = 'Dental', Business_Description__c = 'Dental', Major_LOB__c = 'MGR', ProductCode__c = 'MED'));    
            insert lstRBLP;
            System.assertEquals(lstRBLP.size(), 1, 'Value is not matched');
             
            PageReference pg =Page.HUMCaseActionProcessIntermediate; 
            Test.setCurrentPageReference(pg);
          
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            HumCaseActionLaunchController launchController ;
          
            test.startTest();
            Apexpages.standardcontroller controller = new Apexpages.standardcontroller(oCase);
            launchController = new HumCaseActionLaunchController(controller);
            
            launchController.caseRec.Intent_ID__c = oCase.intent_ID__c;
            launchController.caseRec.Classification_ID__c = oCase.classification_Id__c;
            launchController.bShowBothDemographicRecommended = true;
            launchController.populateRecommendedProcess('Dental');   

            Test.stopTest();
        }
    }
}