/**************************************************************************** 
Apex Class Name  : CreditableCoverage_T_HUM
Version          : 1.0  
Created saveCC.     : February,2018
Function         : test class for Complete Creditable Coverage Functionality
****************************************************************************
Modification Log:
* Developer Name          Code Review #         saveCC.                       Description
*---------------------------------------------------------------------------------------------------
* Sumit  Kumar                         
* Vaishnavi Manda                               05/14/2021                    US2163327 - test class for MF6 checkmarx fix
***************************************************************************************************/
@isTest
private Class CreditableCoverage_T_HUM
{
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
       /**
    * Test method to setup common test data for all test methods 
    *
    * @param None       
    * @return  Void
    */               
     @testSetup static void testSetup() 
     {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        
        system.assertEquals(oUser.lastname == 'test User1',true);  
        
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
            objcCallRSO.Name = GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM;
            objcCallRSO.Is_Active__c = true;
            insert objcCallRSO;
            System.assertEquals(objcCallRSO.Name,GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM);
            
            Classification_Type__c objcCallRSO1 = new Classification_Type__c();
            objcCallRSO1.Name = GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO;
            objcCallRSO1.Is_Active__c = true;
            insert objcCallRSO1;            
            
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            system.assertNotEquals(Null,objCTCI.id);
            
            Case oCase = new Case(AccountId = mAccount.Id, Interacting_About_Type__c = 'Member', Origin = 'Inbound Call', 
                               Type = 'Customer Inquiry', CTCI_List__c= objCTCI.id,
                               Policy_Member__c = oPolicyMem.Id, Interacting_With_Type__c = 'Member',
                               Interacting_With__c = mAccount.Id, Due_Date__c = Date.Today(), Classification_Type__c = 'Calls',
                               Priority = 'Normal', Line_of_Business__c = 'Commercial', G_A_Rights_Given__c = 'true', 
                               G_A_Reason__c = 'All Others', Complaint__c = 'Yes - Medicare Part C', Complaint_Reason__c = 'Clinical Programs',
                               Behavioral_Health__c = 'None',status = 'In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
                  
            insert oCase;
            
            
             Case oCasePro = New Case(AccountId = oAccount.Id, CTCI_List__c= objCTCI.id ,
                                   Interacting_With_Type__c = 'Group', Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),
                                   Case_Owner__c=UserInfo.getUserName(), 
                                   Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
                                   oCase.Complaint__c = 'true'                   ;
                                   oCase.Complaint_Reason__c = 'Claims';
                                   oCase.Classification_Type__c = 'Calls';
                                   oCase.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
                                   oCase.G_A_Rights_Given__c ='No';
                                   oCase.Guided_Process_Flag__c = true;

            insert oCasePro ;   
            
             Case oCasePro1 = New Case(AccountId = mAccount.Id, CTCI_List__c= objCTCI.id ,
                                   Interacting_With_Type__c = 'Group', Interacting_With__c=mAccount.Id, Due_Date__c=Date.Today(),
                                   Case_Owner__c=UserInfo.getUserName(), 
                                   Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
                                   oCasePro1.Complaint__c = 'true'                   ;
                                   oCasePro1.Complaint_Reason__c = 'Claims';
                                   oCasePro1.Classification_Type__c = GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM;
                                   oCasePro1.Subtype__c = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE;                                   
                                   oCasePro1.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
                                   oCasePro1.G_A_Rights_Given__c ='No';
                                   oCasePro1.Guided_Process_Flag__c = true;

            insert oCasePro1 ;  
            
             Case oCasePro2 = New Case(AccountId = mAccountUnknown.Id, CTCI_List__c= objCTCI.id ,
                                   Interacting_With_Type__c = 'Unknown-Member', Interacting_With__c=mAccountUnknown.Id, Due_Date__c=Date.Today(),
                                   Case_Owner__c=UserInfo.getUserName(), 
                                   Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
                                   oCasePro2.Complaint__c = 'true'                   ;
                                   oCasePro2.Complaint_Reason__c = 'Claims';
                                   oCasePro2.Classification_Type__c = GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM;
                                   oCasePro2.Subtype__c = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE;
                                   oCasePro2.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Unknown Case').getRecordTypeId();
                                   oCasePro2.G_A_Rights_Given__c ='No';
                                   oCasePro2.Guided_Process_Flag__c = true;

            insert oCasePro2 ;  
              
            System.assert(oCasePro.status == 'In Progress'); 
            
    if([SELECT ID FROM Template__c WHERE Unique_Template_Name__c='ATVF007'].isEmpty())
    {
        //Insert Template data
        Template__c templateObj = new Template__c(Unique_Template_Name__c='ATVF007',Description__c='Creditable Coverage Attestation');
        Insert templateObj;     
        
        //Insert Template Field Data
        List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CaseNumber'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MemberId'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CRMCaseCreateDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MedicareTrackId'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_RecieptDate'));    
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_EnrolleeFirstName'));  
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_EnrolleeLastName'));    
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PersonSpeakingWith'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_POAUnderstandDefOfCridtableCoverage'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_OtherMemberConsentMoveForwardCall'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_90DaysLetterSent'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FinalDisclaimer'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_NeverHadCrditableCoverage'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoveragePCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUToCoverageDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageSourceName'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_IsCICMSCalled'));
        
        insert templateFieldObj;
        
    }
    if([select id from HUMTemplateSettings__c where name = 'ATTVF'].isEmpty())
    {
        insert (new HUMTemplateSettings__c(Name='ATTVF',ProcessActionType__c='Creditable Coverage Attestation Verification Form',VFPageName__c='AttestationVerification_VF_HUM'));
    }                  
     
    List<Template_Submission_Data__c> submissionList = new List<Template_Submission_Data__c>();
        Template__c templateObj = [Select id from Template__c where Unique_Template_Name__c='ATVF007'];

         Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
         tOnwer.Object_Owner_ID__c=string.valueOf([select id from case where Classification_Type__c = :GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM limit 1].id);
         tOnwer.Object_Owner_Type__c='Case';
         insert tOnwer;

         Template_Submission__c submission = new Template_Submission__c();
         submission.Template__c = templateObj.id;
         submission.Submission_Owner__c = tOnwer.id;
         insert submission;
         
         List<Template_Submission_Data__c> newSubmissionData = new List<Template_Submission_Data__c>();
        for(Template_Field__c submissionDataObj : [SELECT id,Name from Template_Field__c])
        {
            Template_Submission_Data__c tObj = new Template_Submission_Data__c();
            tObj.Template_Field__c = submissionDataObj.Id;
            tObj.Template_Submission__c = submission.id;
            
            
            if(submissionDataObj.Name == 'ATVF_RecieptDate')
            {
                dateTime strReciepDateTime = datetime.now();
                tObj.Value__c = (String) strReciepDateTime.format('MM/dd/yyyy');
            }
            if(submissionDataObj.Name == 'ATVF_EnrolleeFirstName')
            {
                tObj.Value__c = 'testfirstname';
            }
            
            if(submissionDataObj.Name == 'ATVF_EnrolleeLastName')
            {
                tObj.Value__c = 'testlastname';
            }

            if(submissionDataObj.Name == 'ATVF_IsCICMSCalled')
            {
                tObj.Value__c = 'NO' ;
            }
            
            if(submissionDataObj.Name == 'ATVF_MemberId')
            {
                tObj.Value__c = '123456' ;
            }
                            
            if(submissionDataObj.Name == 'ATVF_CaseNumber')
            {
                tObj.Value__c = '123456' ;
            }

            if(submissionDataObj.Name == 'ATVF_MemberId')
            {
                tObj.Value__c = '711274' ;
            }

            if(submissionDataObj.Name == 'ATVF_CRMCaseCreateDate')
            {
                dateTime strCreatedDateTime = datetime.now();
                tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy');           
            }     
            
            if(submissionDataObj.Name == 'ATVF_MedicareTrackId')
            {
                tObj.Value__c = '123456';
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoveragePCheck')
            {
                tObj.Value__c = 'true' ;
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageToDate')
            {
                tObj.Value__c ='03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUToCoverageDate')
            {
                tObj.Value__c = '03/2018';
            }               

            if(submissionDataObj.Name == 'ATVF_PACECoverageCheck')
            {
                tObj.Value__c = 'true';
            }               
            
            if(submissionDataObj.Name == 'ATVF_PACECoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }   
            
            if(submissionDataObj.Name == 'ATVF_PACECoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }               
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_NeverHadCrditableCoverage')
            {
                tObj.Value__c = 'true';
            }  
            
            if(submissionDataObj.Name == GLOBAL_CONSTANT_HUM.ATTESTATION_DISCLAIMERNAME )
            {
                tObj.Value__c = GLOBAL_CONSTANT_HUM.ATTESTATION_DISCLAIMERYESVALUE;
            }  
            
                          
            newSubmissionData.add(tObj);
        }
        insert newSubmissionData;    
                
    } 
}
     
     
    /*
     * Method Name : testCreditableCoverage
     * Description : Test method for  Complete Creditable Coverage functionality
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCreditableCoverage()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
        Test.StartTest();
           // Method for Request DTO
           testCreditableCoverageRequestDTO();
           // Method for Response DTO
           testCreditableCoverageResponseDTO();
           // Method for Flow Plugin Class
           testCreditableCoveragePlugin_Invoke();
           // Method for Request Helper
           testCreditableCoverageRequestHelper();
           // Method to test the testCreditableCoverageFaultDTO
           testCreditableCoverageFaultDTO();
           // Method for Response Helper
           testCreditableCoverageResponseHelper();

        Test.StopTest();
        }
    }
    /*
    * Method Name : testCreditableCoverageRequestDTO 
    * Description : Test method to setup common test data for Request DTO     
    * Return type : void     
    * Paramater   : NA  
    */
    public static void testCreditableCoverageRequestDTO()
    {
     CreditableCoverageRequest_DTO_HUM oRequest = new CreditableCoverageRequest_DTO_HUM();
     CreditableCoverageRequest_DTO_HUM.SaveCCTemplate saveCC = new CreditableCoverageRequest_DTO_HUM.SaveCCTemplate();
     saveCC.ReceiptDate='10/10/2018';
     saveCC.MemberID='H48205565';
     saveCC.EnrolleeFirstName='Brian'; 
     saveCC.EnrolleeLastName='Oneill';
     saveCC.CRMCaseNumber='1254864578978';
     saveCC.CRMCaseCreateDate='10/10/2018';
     saveCC.TrackID='';
     saveCC.EmpUnionCoverage='true';
     saveCC.EmpUnionCoverageStartDate='10/10/2018';
     saveCC.EmpUnionCoverageEndDate='10/10/2018';
     saveCC.StateCoverage='true';
     saveCC.StateCoverageStartDate='10/10/2018';
     saveCC.StateCoverageEndDate='10/10/2018';
     saveCC.VABenefits='true';
     saveCC.VABenefitsStartDate='10/10/2018';
     saveCC.VABenefitsEndDate='10/10/2018';
     saveCC.TricareMilitaryCoverage='true';
     saveCC.TricareMilitaryCoverageStartDate='10/10/2018';
     saveCC.TricareMilitaryCoverageEndDate='10/10/2018';
     saveCC.MedigapCreditableCoverage='true';
     saveCC.MedigapCreditableCoverageStartDate='10/10/2018';
     saveCC.MedigapCreditableCoverageEndDate='10/10/2018';
     saveCC.IndianHealthService='true';
     saveCC.IndianHealthServiceStartDate='10/10/2018';
     saveCC.IndianHealthServiceEndDate='10/10/2018';
     saveCC.PaceCoverage='false';
     saveCC.PaceCoverageStartDate='10/10/2018';
     saveCC.PaceCoverageEndDate='10/10/2018';
     saveCC.DiffSourceCoverage='false';
     saveCC.DiffSourceCoverageStartDate='10/10/2018';
     saveCC.DiffSourceCoverageEndDate='10/10/2018';
     saveCC.HumanaCoverage='false';
     saveCC.HumanaCoverageStartDate='10/10/2018';
     saveCC.HumanaCoverageEndDate='10/10/2018';
     saveCC.ExtraMedicareCoverage='false';
     saveCC.ExtraMedicareCoverageStartDate='10/10/2018';
     saveCC.ExtraMedicareCoverageEndDate='10/10/2018';
     saveCC.HurricaneKatrinaCoverage='false';
     saveCC.HurricaneKatrinaCoverageStartDate='10/10/2018';
     saveCC.HurricaneKatrinaCoverageEndDate='10/10/2018';
     saveCC.PuertoRicoReformaCoverage='false';
     saveCC.PuertoRicoReformaCoverageStartDate='10/10/2018';
     saveCC.PuertoRicoReformaCoverageEndDate='10/10/2018';
     saveCC.NeverHadCreditibleCoverage='true';
     system.assert(saveCC != null);
        

    }
    /*
    * Method Name : testCreditableCoverageResponseDTO 
    * Description : Test method to setup common test data for Response DTO Class    
    * Return type : void     
    * Paramater   : NA  
    */
    public static void testCreditableCoverageResponseDTO()
    {
     CreditableCoverageResponse_DTO_HUM oResponse = new CreditableCoverageResponse_DTO_HUM();
     CreditableCoverageResponse_DTO_HUM.SaveCCTemplateResponse ccRes = new CreditableCoverageResponse_DTO_HUM.SaveCCTemplateResponse();
     ccRes.CRMCaseNumber='123456789';
     ccRes.Type='TEST';
     ccRes.ReturnCode='CICMSIN001';
     ccRes.Description='Data Transfer is successful';
     system.assert(ccRes != null);
    }
    /*
    * Method Name : testCreditableCoverageRequestHelper 
    * Description : Test method to setup common test data for Request Helper Class     
    * Return type : void     
    * Paramater   : NA  
    */
    public static void testCreditableCoverageRequestHelper()
    {
     //Setting mockup for service test       
     CreditableCoverageTemplateRequest_H_HUM oRequestHelper=new CreditableCoverageTemplateRequest_H_HUM();
     oRequestHelper.ReceiptDate='10/10/2018';     
     oRequestHelper.MemberID='H48205565';
     oRequestHelper.EnrolleeFirstName='Brian'; 
     oRequestHelper.EnrolleeLastName='Oneill';
     oRequestHelper.CRMCaseNumber='1254864578978';
     oRequestHelper.CRMCaseCreateDate='10/10/2018';
     oRequestHelper.TrackID='234234234234';
     oRequestHelper.EmpUnionCoverage='true';
     oRequestHelper.EmpUnionCoverageStartDate='10/10/2018';
     oRequestHelper.EmpUnionCoverageEndDate='10/10/2018';
     oRequestHelper.StateCoverage='true';
     oRequestHelper.StateCoverageStartDate='10/10/2018';
     oRequestHelper.StateCoverageEndDate='10/10/2018';
     oRequestHelper.VABenefits='true';
     oRequestHelper.VABenefitsStartDate='10/10/2018';
     oRequestHelper.VABenefitsEndDate='10/10/2018';
     oRequestHelper.TricareMilitaryCoverage='true';
     oRequestHelper.TricareMilitaryCoverageStartDate='10/10/2018';
     oRequestHelper.TricareMilitaryCoverageEndDate='10/10/2018';
     oRequestHelper.MedigapCreditableCoverage='true';
     oRequestHelper.MedigapCreditableCoverageStartDate='10/10/2018';
     oRequestHelper.MedigapCreditableCoverageEndDate='10/10/2018';
     oRequestHelper.IndianHealthService='true';
     oRequestHelper.IndianHealthServiceStartDate='10/10/2018';
     oRequestHelper.IndianHealthServiceEndDate='10/10/2018';
     oRequestHelper.PaceCoverage='false';
     oRequestHelper.PaceCoverageStartDate='10/10/2018';
     oRequestHelper.PaceCoverageEndDate='10/10/2018';
     oRequestHelper.DiffSourceCoverage='false';
     oRequestHelper.DiffSourceCoverageStartDate='10/10/2018';
     oRequestHelper.DiffSourceCoverageEndDate='10/10/2018';
     oRequestHelper.HumanaCoverage='false';
     oRequestHelper.HumanaCoverageStartDate='10/10/2018';
     oRequestHelper.HumanaCoverageEndDate='10/10/2018';
     oRequestHelper.ExtraMedicareCoverage='false';
     oRequestHelper.ExtraMedicareCoverageStartDate='10/10/2018';
     oRequestHelper.ExtraMedicareCoverageEndDate='10/10/2018';
     oRequestHelper.HurricaneKatrinaCoverage='false';
     oRequestHelper.HurricaneKatrinaCoverageStartDate='10/10/2018';
     oRequestHelper.HurricaneKatrinaCoverageEndDate='10/10/2018';
     oRequestHelper.PuertoRicoReformaCoverage='false';
     oRequestHelper.PuertoRicoReformaCoverageStartDate='10/10/2018';
     oRequestHelper.PuertoRicoReformaCoverageEndDate='10/10/2018';
     oRequestHelper.NeverHadCreditibleCoverage='true';
     system.assert(oRequestHelper != null);
    }
    /*
    * Method Name : testMockupMethod 
    * Description : Http Mockup callout method for success response     
    * Return type : void     
    * Paramater   : NA  
    */
    static testMethod void testMockupMethod()
    { 
         HUM_Webservice_Callout__c oVfs = new HUM_Webservice_Callout__c(Name='CCTemplate', Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TemplateService_V1.0/SaveCCTemplate', Timeout__c='12000', Request_Type__c='REST', Content_Type__c='application/json'); 
         insert oVfs;
         system.assert(HUM_Webservice_Callout__c.getValues('CCTemplate') != NULL);
         Test.startTest();

         Test.setMock(HttpCalloutMock.class, new CCTemplateServiceMockHttpResponseTest('INFORMATION'));           
         system.assert(CreditableCoverage_S_HUM.getCreditCoverageResponse(new CreditableCoverageTemplateRequest_H_HUM())!=null);
         Test.stopTest();
    }
    /*
    * Method Name : testMockupFaultMethod 
    * Description : Http Mockup callout method for success response     
    * Return type : void     
    * Paramater   : NA  
    */
    static testMethod void testMockupFaultMethod()
    { 
         HUM_Webservice_Callout__c oVfs = new HUM_Webservice_Callout__c(Name='CCTemplate', Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TemplateService_V1.0/SaveCCTemplate', Timeout__c='12000', Request_Type__c='REST', Content_Type__c='application/json'); 
         insert oVfs;
         system.assert(HUM_Webservice_Callout__c.getValues('CCTemplate') != NULL);
         Test.startTest();

         Test.setMock(HttpCalloutMock.class, new CCTemplateServiceMockHttpResponseTest('ERROR'));           
         system.assert(CreditableCoverage_S_HUM.getCreditCoverageResponse(new CreditableCoverageTemplateRequest_H_HUM())!=null);
         Test.stopTest();
    }
    
    /*
    * Method Name : testCreditableCoverageResponseHelper
    * Description : Test method to setup common test data for Response Helper Class     
    * Return type : void     
    * Paramater   : NA  
    */
    public static void testCreditableCoverageResponseHelper()
    {
     CreditableCoverageTemplateResponse_H_HUM objResponseHelper=new CreditableCoverageTemplateResponse_H_HUM();
     objResponseHelper.CRMCaseNumber='123456789';
     objResponseHelper.Type='TEST';
     objResponseHelper.ReturnCode='CICMSIN001';
     objResponseHelper.Description='Data Transfer is successful';
     system.assert(objResponseHelper != null);
    }
    /*
    * Method Name : testCreditableCoveragePlugin_Invoke
    * Description : Test method to setup common test data for Flow Plugin Class     
    * Return type : void     
    * Paramater   : NA  
    */
    public static void testCreditableCoveragePlugin_Invoke()
    {

        CreditableCoverage_S_HUM oAvfTemp = new CreditableCoverage_S_HUM();
        string caseId = String.valueOf([select id,createdDate,Classification_Type__c  from case where Classification_Type__c = :GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM Limit 1].Id);
        CreditableCoverageServiceCall_H_HUM oPlugin= new CreditableCoverageServiceCall_H_HUM(caseId );
        oPlugin.invoke(caseId );
               
    }
    
    /*
    * Method Name : testCreditableCoverageFaultDTO 
    * Description : Test method to setup common test data for Response Helper Class     
    * Return type : void     
    * Paramater   : NA  
    */
    public static void testCreditableCoverageFaultDTO()
    {
        CCResponseFaultCode_DTO_HUM objResponseFaultDTO = new CCResponseFaultCode_DTO_HUM();
        System.assert(objResponseFaultDTO != null);
        CCResponseFaultCode_DTO_HUM.Fault objFault = new CCResponseFaultCode_DTO_HUM.Fault();
        System.assert(objFault != null);
        CCResponseFaultCode_DTO_HUM.detail objDetail = new CCResponseFaultCode_DTO_HUM.detail();
        System.assert(objDetail != null);
        CCResponseFaultCode_DTO_HUM.transactionInfo objTransactionInfor=new CCResponseFaultCode_DTO_HUM.transactionInfo();
        System.assert(objTransactionInfor != null);
        CCResponseFaultCode_DTO_HUM.errorInfo objErrorInfo=new CCResponseFaultCode_DTO_HUM.errorInfo();
        System.assert(objErrorInfo != null);
    }
    
    /*
    * Method Name : updateCaseClassificationAndIntentTrueTest
    * Description : Test method to validate updateCaseClassificationAndIntent   
    * Return type : void     
    * Paramater   : NA  
    */
    public static testMethod void updateCaseClassificationAndIntentTrueTest()
    {
        Case caseObj = [SELECT Id,Guided_Process_Flag__c  from Case WHERE Line_of_Business__c = 'Commercial' LIMIT 1];
                
        Classification__c classifcationObj = new Classification__c(Name=GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_ELIGIBILITY);
        insert classifcationObj;
        
        Intent__c intentObj = new Intent__c(Name=GLOBAL_CONSTANT_HUM.CASE_INTENT_LATEENROLLMENTPENALTY);
        insert intentObj;
        
        Classification_Type__c classificationTypeObj =[select id from Classification_Type__c  where Name = :GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO LIMIT 1];
       
        
        CTCI_Junction__c ctciObj = new CTCI_Junction__c(Classification_Type__c=classificationTypeObj.Id, Classification__c=classifcationObj.Id,Intent__c = intentObj.Id);
        insert ctciObj;
        
        
        
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];         
        List<List<String>> lstStrings = new List<List<String>>
                                    {
                                        new List<String>{String.ValueOf(caseObj.Id), 'true'}                                                                       
                                    };                                                 
        system.runAs(oUser)
        {
            Test.StartTest();
            CreditableCoverage_S_HUM.updateCaseClassificationAndIntent(lstStrings);
            Test.StopTest();
             System.assertEquals([SELECT Id,Guided_Process_Flag__c  from Case WHERE Id = :caseObj.Id][0].Guided_Process_Flag__c,true);
        }
    }
    
    /*
    * Method Name : updateCaseClassificationAndIntentFalseTest
    * Description : Test method to validate updateCaseClassificationAndIntent   
    * Return type : void     
    * Paramater   : NA  
    */
    public static testMethod void updateCaseClassificationAndIntentFalseTest()
    {
        Case caseObj = [SELECT Id,Guided_Process_Flag__c  from Case WHERE Line_of_Business__c = 'Commercial' LIMIT 1];
        
        Classification__c classifcationObj = new Classification__c(Name=GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_COMMUNICATION);
        insert classifcationObj;
        
        Intent__c intentObj = new Intent__c(Name=GLOBAL_CONSTANT_HUM.CASE_INTENT_ELIGIBILITY);
        insert intentObj;
        
        Classification_Type__c classificationTypeObj =[select id from Classification_Type__c  where Name = :GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM LIMIT 1];
       
        
        CTCI_Junction__c ctciObj = new CTCI_Junction__c(Classification_Type__c=classificationTypeObj.Id, Classification__c=classifcationObj.Id,Intent__c = intentObj.Id);
        insert ctciObj;
     
        
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];         
        List<List<String>> lstStrings = new List<List<String>>
                                    {
                                        new List<String>{String.ValueOf(caseObj.Id), 'false'}                                                                       
                                    };                                                 
        system.runAs(oUser)
        {
            Test.StartTest();            
            CreditableCoverage_S_HUM.updateCaseClassificationAndIntent(lstStrings);
            Test.StopTest();
            System.assertEquals([SELECT Id,Guided_Process_Flag__c  from Case WHERE Id = :caseObj.Id][0].Guided_Process_Flag__c,false);
        }
    }
    
    /*
    * Method Name : updateCaseClassificationAndIntentNegativeTest
    * Description : Test method to validate updateCaseClassificationAndIntent   
    * Return type : void     
    * Paramater   : NA  
    */
    public static testMethod void updateCaseClassificationAndIntentNegativeTest()
    {
        Case caseObj = [SELECT Id,Guided_Process_Flag__c  from Case WHERE Classification_Type__c = :GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM LIMIT 1];
        
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];         
        List<List<String>> lstStrings = new List<List<String>>
                                    {
                                        new List<String>{String.ValueOf(caseObj.Id), 'true'}                                                                       
                                    };                                                 
        system.runAs(oUser)
        {
            Test.StartTest();
            List<string> outputList = CreditableCoverage_S_HUM.updateCaseClassificationAndIntent(lstStrings);
            Test.StopTest();
            System.assertEquals(outputList[0],'false');
        }
    }
    
    /*
    * Method Name : updateCaseClassificationAndIntentNegativeCaseTest
    * Description : Test method to validate updateCaseClassificationAndIntent   
    * Return type : void     
    * Paramater   : NA  
    */
    public static testMethod void updateCaseClassificationAndIntentNegativeCaseTest()
    { 
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];         
        List<List<String>> lstStrings = new List<List<String>>();
                                                                                    
        system.runAs(oUser)
        {
            Test.StartTest();
            List<string> outputList = CreditableCoverage_S_HUM.updateCaseClassificationAndIntent(lstStrings);
            Test.StopTest();
            System.assertEquals(outputList[0],'false');
        }
    }
    
}