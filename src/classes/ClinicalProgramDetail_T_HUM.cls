/**
 *   name        : ClinicalProgramDetail_T_HUM
 *   CreateDate  : 08/28/2018
 *   Description : This class serves as Testclass for ClinicalProgramDetail_C_HUM component
 *   Version     : 1.0
 *   reference   : ClinicalProgramDetail_C_HUM
 *   Test        : ClinicalProgramDetail_T_HUM
 *   Modification Log:
    
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Imran                                      04/14/2020                  Original Version 
 * Ranadheer                                   05/18/2021                  Code coverage
 *  Pinky Vijur                               08/19/2021                 REQ - 2080855 - T1PRJ0052463 - MF 6 -  Display Palliative Care program participation details in CRM - Pallitus Health Partners (CRM)
 ************************************************************************************************************************************************* */
@isTest 
private class ClinicalProgramDetail_T_HUM{ 

	/*
    * Method Name : setupTestData
    * Description : Method to set up test data
    * Return type : void
    * Paramater   : NA
    */
     @testSetup static void setup() 
    {
           //User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
           User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
           insert oUser;
         
           System.runAs(oUser)
           {
           Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.Mbr_Gen_Key__c ='14594124'; //values -- 14594124 / 9999904594124 /13039905
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');           

            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
           
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
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
             
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Id_Value__c = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';
            objMemberID1.Type__c = 'SSN';        
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );

            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'ClinicalPrograms';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pharmacy';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';//https://dev-crmservicews.humana.com/AuthReferralService/GetClinicalProgramDetails
            insert calloutSettings;
			
			HUM_Webservice_Callout__c oHUMAuthDetailsService = new HUM_Webservice_Callout__c();
			oHUMAuthDetailsService.name='GetClinicalPrograms_V2';
			oHUMAuthDetailsService.Active__c=true;
			oHUMAuthDetailsService.Certificate_Name__c='Humana_GuidanceCare_Pharmacy';
			oHUMAuthDetailsService.End_Point_URL__c='https://testtest-crmservicews.humana.com/';
			oHUMAuthDetailsService.Timeout__c='120000';
			oHUMAuthDetailsService.Content_Type__c='application/json';
			oHUMAuthDetailsService.Request_Type__c='POST';
			insert oHUMAuthDetailsService;
			system.assert(oHUMAuthDetailsService.id!=null);
			system.assert(oHUMAuthDetailsService.name == 'GetClinicalPrograms_V2');
            
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = New CRMFunctionality_ONOFF_Switch__c();
            objSwitch1.IsON__c = true;
            objSwitch1.Name = '2080855';
            insert objSwitch1;
            system.assertequals('2080855',objSwitch1.Name);
           }
    }
  

    
    /*
    * Method Name : getClinicalprogramData_Test1
    * Description : Test method to test getClinicalprogramData method
    * Return type : void
    * Paramater   : NA
    */
   
    public static testMethod void getClinicalprogramData_Test1()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        //system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');          
        System.runAs (oCCSUser) 
        {
            Test.StartTest(); 
			Account accountObj = [Select id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c,Mbr_Gen_Key__c from account where LastName = 'Account'];                
			Policy_Member__c oPolMem =  [Select Id,Name,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c, Member__r.Mbr_Gen_Key__c from Policy_Member__c where Member__c =:accountObj.Id];
			ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);  
			String sMemberIDType= 'membergenkey';
			ClinicalProgramDetail_C_HUM comp = new ClinicalProgramDetail_C_HUM();  

			ClinicalProgramDetail_C_HUM.getClinicalNurseCareData(accountObj.Mbr_Gen_Key__c,sMemberIDType);
			comp.showPageMessage();
			HttpResponse response = new HttpResponse();      
			response.setBody('{"ClinicalProgramResponse":{"EligibleProgramDetails":[{"ProgramName":"ASO ALL","InterventionVendor":"HUM","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00","Comments":"Test Comments"}],"EnrolledProgramDetails":[{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00"},{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00"}]}}');
			response.setStatusCode(200);
			Test.setContinuationResponse('labels', response);     
			ClinicalProgramDetail_S_HUM.processResponseClinicalProgram((ClinicalProgramResponse_DTO_HUM)System.JSON.deserialize(response.getBody(),ClinicalProgramResponse_DTO_HUM.class));
			ClinicalProgramDetail_S_HUM.parseResponseClinicalProgram(new List<string>{(String.valueOf(response))},null);        
			ClinicalProgramDetail_S_HUM.parseResponseClinicalProgram(new List<string>{'labels'},null);        
			Test.StopTest();       
      }                  
    }
 
	/*
    * Method Name : getClinicalprogramData_Test2
    * Description : Test method to test getClinicalprogramData method
    * Return type : void
    * Paramater   : NA
    */
   
    public static testMethod void getClinicalprogramData_Test2()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;        
        System.runAs (oCCSUser) 
        {
			Test.StartTest(); 
			Account accountObj = [Select id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c,Mbr_Gen_Key__c from account where LastName = 'Account'];                
			Policy_Member__c oPolMem =  [Select Id,Name,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c, Member__r.Mbr_Gen_Key__c from Policy_Member__c where Member__c =:accountObj.Id];
			ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);              
			String sMemberIDType= 'membergenkey';
			ClinicalProgramDetail_C_HUM comp = new ClinicalProgramDetail_C_HUM();  

			ClinicalProgramDetail_C_HUM.getClinicalNurseCareData(accountObj.Mbr_Gen_Key__c,sMemberIDType);
			comp.showPageMessage();
			HttpResponse response = new HttpResponse();       
			response.setBody('{"ClinicalProgramResponse": {    "EligibleProgramDetails":    [             {          "ProgramName": "ASO Preventative",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "BHCCM",          "InterventionVendor": "Humana Behavioral Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Brain Health MyStrength",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Behavioral Health Preauths",          "InterventionVendor": "Humana Behavioral Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "ASO ALL",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Cancer",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Cardiac Consultation",          "InterventionVendor": "HealthHelp",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Chronic Kidney Disease",          "InterventionVendor": "Village Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Clinical Program Framework Only",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Commercial Utilization Management & Concurrent Review",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Coronary Artery Disease",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Commercial Case Management",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Chronic Obstructive Pulmonary Disease",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Genetic/Molecular Testing Preauthorizations",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Getting Started Guide",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Go365 Self Directed Content",          "InterventionVendor": "Go365",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Congestive Heart Failure",          "InterventionVendor": "VRI",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Greeting Card",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Dental Clinical Outreach",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "EAP - 3 visits",          "InterventionVendor": "Humana Behavioral Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Health Coach-Wellness Standard",          "InterventionVendor": "Humana Behavioral Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Health Planning & Support",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana Behavioral Health Autism Program",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana Nurse",          "InterventionVendor": "Humana Behavioral Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana Pharmacy",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana Standard Commercial Preauthorization List",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Integrated Care Managment",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Maximize Your Benefits",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "MOD A - Outpatient Physical, Occupational, Speech Therapy Preauthorizations",          "InterventionVendor": "OrthoNet",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "MOD D – Spinal Surgery, Pain Management",          "InterventionVendor": "OrthoNet",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Non-Utilizer Letter",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Internal Diabetes",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "New Century Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "Oncology Analytics",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Outsourced Health Assessment",          "InterventionVendor": "WellSource",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "NICU_Neonatal",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Personal Nurse",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Postcard/Magnet Mailer",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Specialized Onsite Care Management",          "InterventionVendor": "Specialized On-Site Care Management",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Welcome Letter",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Weight Watchers",          "InterventionVendor": "Weight Watchers",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Value of Home Delivery",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Radiation Therapy Preauthorizations",          "InterventionVendor": "HealthHelp",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Vision Clinical Outreach",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Transplant Program",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Rare Disease",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "RadConsult-Outpatient Radiology Preauthorizations",          "InterventionVendor": "HealthHelp",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "HumanaBeginnings",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana Vitality Full Program",          "InterventionVendor": "Vitality",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana US Imaging Services",          "InterventionVendor": "US Imaging Services",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana TeleMedicine",          "InterventionVendor": "Doctors On Demand",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana PHQ",          "InterventionVendor": "Humana Behavioral Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana First",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Humana Achieve",          "InterventionVendor": "Humana Behavioral Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Go365 Full Program",          "InterventionVendor": "Go365",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "End Stage Renal Disease",          "InterventionVendor": "Village Health",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       },             {          "ProgramName": "Diabetes",          "InterventionVendor": "HUM",          "BeginDate": "2019-01-01T00:00:00",          "EndDate": "9999-12-31T00:00:00",          "Comments": ""       }    ],    "EnrolledProgramDetails": [] }}');
			response.setStatusCode(200);
			Test.setContinuationResponse('labels', response);    
			ClinicalProgramDetail_S_HUM.processResponseClinicalProgram((ClinicalProgramResponse_DTO_HUM)System.JSON.deserialize(response.getBody(),ClinicalProgramResponse_DTO_HUM.class));
			ClinicalProgramDetail_S_HUM.parseResponseClinicalProgram(new List<string>{(String.valueOf(response))},null); 
			ClinicalProgramDetail_S_HUM.parseResponseClinicalProgram(new List<string>{'labels'},null);             		 
			Test.StopTest();        
      }                  
    }  
    
    /*
    * Method Name : getClinicalprogramDataException_Test3
    * Description : Test method to test getClinicalprogramData method
    * Return type : void
    * Paramater   : NA
    */
   
    public static testMethod void getClinicalprogramDataException_Test3()
    {
		HUM_Webservice_Callout__c callout = [SELECT Id, Certificate_Name__c FROM HUM_Webservice_Callout__c WHERE Name='GetClinicalPrograms_V2' LIMIT 1];
		if(callout != null) {
			callout.Certificate_Name__c = 'dummy';
			update callout;
		}
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ];
        System.runAs (oCCSUser) 
        {
            Test.StartTest(); 
			Account accountObj = [Select id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c,Mbr_Gen_Key__c from account where LastName = 'Account'];                
			Policy_Member__c oPolMem =  [Select Id,Name,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c, Member__r.Mbr_Gen_Key__c from Policy_Member__c where Member__c =:accountObj.Id];
			ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);  
			String sMemberIDType= 'membergenkey';
			ClinicalProgramDetail_C_HUM comp = new ClinicalProgramDetail_C_HUM();  

			ClinicalProgramDetail_C_HUM.getClinicalNurseCareData(accountObj.Mbr_Gen_Key__c,sMemberIDType);
			comp.showPageMessage();
			HttpResponse response = new HttpResponse();      
			response.setBody('{"ClinicalProgramResponse":{"EligibleProgramDetails":[{"ProgramName":"ASO ALL","InterventionVendor":"HUM","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00","Comments":"Test Comments"}],"EnrolledProgramDetails":[{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00"},{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00"}]}}');
			response.setStatusCode(500);
			Test.setContinuationResponse('labels', response);     
			ClinicalProgramDetail_S_HUM.processResponseClinicalProgram((ClinicalProgramResponse_DTO_HUM)System.JSON.deserialize(response.getBody(),ClinicalProgramResponse_DTO_HUM.class));
			ClinicalProgramDetail_S_HUM.parseResponseClinicalProgram(New List<string>{(String.valueOf(response))},null);        
			ClinicalProgramDetail_S_HUM.parseResponseClinicalProgram(New List<string>{'labels'},null);        
			Test.StopTest();        
      }                  
    }
    /*
    * Method Name : getClinicalprogramDataException_Test3
    * Description : Test method to test Intervention Vendor column 
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void checkNewColumnName_Test4()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ];
        System.runAs (oCCSUser) 
        {
            Test.StartTest(); 
			Account accountObj = [Select id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c,Mbr_Gen_Key__c from account where LastName = 'Account'];                
			Policy_Member__c oPolMem =  [Select Id,Name,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c, Member__r.Mbr_Gen_Key__c from Policy_Member__c where Member__c =:accountObj.Id];
			ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);  
			String sMemberIDType= 'membergenkey';
			ClinicalProgramDetail_C_HUM comp = new ClinicalProgramDetail_C_HUM();  
			String sColumnHeader = comp.sColumnHeaderEnroll;
            System.assert(sColumnHeader.contains('Intervention Vendor'));
            Test.StopTest();        
      }
    }
}