/*****************************************************************************************************************************************************************************
    @Apex Class Name  : HealthResource_LT_HUM
    @Version          : 1.0
    @Created Date     : 10/11/2021
    @Function         : Test class for HealthResource_LC_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                       10/11/2021                    Original Version  
******************************************************************************************************************************************************************************/

@isTest
public with sharing class HealthResource_LT_HUM {
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
  @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.Mbr_Gen_Key__c = '1234';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'New';
            objAcc1.LastName = 'Account';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.General_Account__c = true;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
         
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            PurchaserPlan PolicyObjOI1 = new PurchaserPlan();
            PolicyObjOI1.Policy_Group_Number__c='11111';
            PolicyObjOI1.Name = 'Policy New';
            PolicyObjOI1.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObjOI1.PayerId = objAcc.Id;
            PolicyObjOI1.Major_LOB__c ='EM';
            PolicyObjOI1.Platform__c = 'EM';
            PolicyObjOI1.ETL_Record_Deleted__c = false;
            PolicyObjOI1.Product__c = oProd.Id;
            insert PolicyObjOI1;
            
            MemberPlan objMembplanOI1 = new MemberPlan();
            objMembplanOI1.Name='testpolmem11';
            objMembplanOI1.MemberId=objAcc.id;
            objMembplanOI1.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI1.Member_Id_base__c = 'test23456';
            objMembplanOI1.GroupNumber = '672534';
            objMembplanOI1.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI1.EffectiveTo = Date.newInstance(1999,12, 31);
            objMembplanOI1.PlanId= PolicyObjOI1.Id;
            insert objMembplanOI1;
            
            MemberPlan objMembplanOI2 = new MemberPlan();
            objMembplanOI2.Name='testpolmem2';
            objMembplanOI2.MemberId=objAcc1.id;
            objMembplanOI2.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI2.Member_Id_base__c = 'test23456';
            objMembplanOI2.GroupNumber = '672534';
            objMembplanOI2.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI2.EffectiveTo = Date.newInstance(1999,12, 31);
            objMembplanOI2.PlanId= PolicyObjOI1.Id;
            insert objMembplanOI2;
            
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'GetClinicalPrograms_V2';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert calloutSettings;
            
            CRM_Functionality_Enablement__mdt mdt = [select Active__c from CRM_Functionality_Enablement__mdt where Label = 'Activate GetClinicalV2 Service'];

            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new CRMFunctionality_ONOFF_Switch__c(Name = '2080855',IsON__c=true);
            insert csCRMOnOffSwitch;
            
            CRM_Functionality_Enablement__mdt mdtTest = [select Active__c from CRM_Functionality_Enablement__mdt where Label = 'Activate GetClinicalV2 Service'];
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitchTest = new CRMFunctionality_ONOFF_Switch__c(Name = '2080855',IsON__c=false);
            insert csCRMOnOffSwitchTest;
             
        }
    }        
   
    
     /*
    * Method to create the test class
    * @param        None
    * @return       None
    */
    private static testmethod void testGetClinicalPrograms_V2()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            Map<String,String> stateMap= new Map<String,String>();
            MemberPlan memberPlanObj = [SELECT Id,Member.Mbr_Gen_Key__c,Member.Enterprise_ID__c FROM MemberPlan WHERE Name = 'testpolmem11'];
            Test.StartTest();             
            HealthResource_LC_HUM.getHealthResource(memberPlanObj.Id);
            HttpResponse httpRes = new HttpResponse();
            httpRes.setBody('{"ClinicalProgramResponse": {    "EligibleProgramDetails":    [             {          "ProgramName": "ASO ALL",          "InterventionVendor": "HUM"       },             {          "ProgramName": "ASO Preventative",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Behavioral Health Preauths",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Brain Health MyStrength",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Cancer",          "InterventionVendor": "HUM"       },             {          "ProgramName": "BHCCM",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Cardiac Consultation",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Chronic Kidney Disease",          "InterventionVendor": "Village Health"       },             {          "ProgramName": "Chronic Obstructive Pulmonary Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Clinical Program Framework Only",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Commercial Case Management",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Coronary Artery Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Dental Clinical Outreach",          "InterventionVendor": "HUM"       },             {          "ProgramName": "EAP - 3 visits",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "End Stage Renal Disease",          "InterventionVendor": "Village Health"       },             {          "ProgramName": "Commercial Utilization Management & Concurrent Review",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Genetic/Molecular Testing Preauthorizations",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Getting Started Guide",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Go365 Self Directed Content",          "InterventionVendor": "Go365"       },             {          "ProgramName": "Greeting Card",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Achieve",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Congestive Heart Failure",          "InterventionVendor": "VRI"       },             {          "ProgramName": "Humana Behavioral Health Autism Program",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana First",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Pharmacy",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana TeleMedicine",          "InterventionVendor": "Doctors On Demand"       },             {          "ProgramName": "Humana Nurse",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "HumanaBeginnings",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Internal Diabetes",          "InterventionVendor": "HUM"       },             {          "ProgramName": "NICU_Neonatal",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Non-Utilizer Letter",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana US Imaging Services",          "InterventionVendor": "US Imaging Services"       },             {          "ProgramName": "MOD A - Outpatient Physical, Occupational, Speech Therapy Preauthorizations",          "InterventionVendor": "OrthoNet"       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "Oncology Analytics"       },             {          "ProgramName": "Postcard/Magnet Mailer",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Radiation Therapy Preauthorizations",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Vision Clinical Outreach",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Welcome Letter",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Weight Watchers",          "InterventionVendor": "Weight Watchers"       },             {          "ProgramName": "Value of Home Delivery",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Transplant Program",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Specialized Onsite Care Management",          "InterventionVendor": "Specialized On-Site Care Management"       },             {          "ProgramName": "Rare Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "RadConsult-Outpatient Radiology Preauthorizations",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Personal Nurse",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Outsourced Health Assessment",          "InterventionVendor": "WellSource"       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "New Century Health"       },             {          "ProgramName": "MOD D – Spinal Surgery, Pain Management",          "InterventionVendor": "OrthoNet"       },             {          "ProgramName": "Maximize Your Benefits",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Integrated Care Managment",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Vitality Full Program",          "InterventionVendor": "Vitality"       },             {          "ProgramName": "Humana Standard Commercial Preauthorization List",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana PHQ",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Health Planning & Support",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Health Coach-Wellness Standard",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Go365 Full Program",          "InterventionVendor": "Go365"       },             {          "ProgramName": "Diabetes",          "InterventionVendor": "HUM"       }    ],    "EnrolledProgramDetails": [] }}');    
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            stateMap.put('sRequest',reqType);
            List<String> Label = new List<String>();
            HealthResource_LC_HUM.parseResponseClinicalProgram(Label,stateMap);
            
            Test.StopTest();
        }
    }
    
     /*
    * Method to create the test class
    * @param        None
    * @return       None
    */
    private static testmethod void testGetClinicalPrograms_V4()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            HUM_Webservice_Callout__c myCS1 = HUM_Webservice_Callout__c.getValues('GetClinicalPrograms_V2');
            myCS1.Timeout__c = '12000';
            update myCS1;
            Map<String,String> stateMap= new Map<String,String>();
            MemberPlan memberPlanObj = [SELECT Id,Member.Mbr_Gen_Key__c,Member.Enterprise_ID__c FROM MemberPlan WHERE Name = 'testpolmem11'];
            Test.StartTest();             
            HealthResource_LC_HUM.getHealthResource(memberPlanObj.Id);
            HttpResponse httpRes = new HttpResponse();
            httpRes.setBody('{"ClinicalProgramResponse": {    "EligibleProgramDetails":    [             {          "ProgramName": "ASO ALL",          "InterventionVendor": "HUM"       },             {          "ProgramName": "ASO Preventative",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Behavioral Health Preauths",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Brain Health MyStrength",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Cancer",          "InterventionVendor": "HUM"       },             {          "ProgramName": "BHCCM",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Cardiac Consultation",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Chronic Kidney Disease",          "InterventionVendor": "Village Health"       },             {          "ProgramName": "Chronic Obstructive Pulmonary Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Clinical Program Framework Only",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Commercial Case Management",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Coronary Artery Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Dental Clinical Outreach",          "InterventionVendor": "HUM"       },             {          "ProgramName": "EAP - 3 visits",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "End Stage Renal Disease",          "InterventionVendor": "Village Health"       },             {          "ProgramName": "Commercial Utilization Management & Concurrent Review",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Genetic/Molecular Testing Preauthorizations",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Getting Started Guide",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Go365 Self Directed Content",          "InterventionVendor": "Go365"       },             {          "ProgramName": "Greeting Card",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Achieve",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Congestive Heart Failure",          "InterventionVendor": "VRI"       },             {          "ProgramName": "Humana Behavioral Health Autism Program",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana First",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Pharmacy",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana TeleMedicine",          "InterventionVendor": "Doctors On Demand"       },             {          "ProgramName": "Humana Nurse",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "HumanaBeginnings",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Internal Diabetes",          "InterventionVendor": "HUM"       },             {          "ProgramName": "NICU_Neonatal",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Non-Utilizer Letter",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana US Imaging Services",          "InterventionVendor": "US Imaging Services"       },             {          "ProgramName": "MOD A - Outpatient Physical, Occupational, Speech Therapy Preauthorizations",          "InterventionVendor": "OrthoNet"       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "Oncology Analytics"       },             {          "ProgramName": "Postcard/Magnet Mailer",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Radiation Therapy Preauthorizations",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Vision Clinical Outreach",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Welcome Letter",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Weight Watchers",          "InterventionVendor": "Weight Watchers"       },             {          "ProgramName": "Value of Home Delivery",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Transplant Program",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Specialized Onsite Care Management",          "InterventionVendor": "Specialized On-Site Care Management"       },             {          "ProgramName": "Rare Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "RadConsult-Outpatient Radiology Preauthorizations",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Personal Nurse",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Outsourced Health Assessment",          "InterventionVendor": "WellSource"       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "New Century Health"       },             {          "ProgramName": "MOD D – Spinal Surgery, Pain Management",          "InterventionVendor": "OrthoNet"       },             {          "ProgramName": "Maximize Your Benefits",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Integrated Care Managment",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Vitality Full Program",          "InterventionVendor": "Vitality"       },             {          "ProgramName": "Humana Standard Commercial Preauthorization List",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana PHQ",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Health Planning & Support",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Health Coach-Wellness Standard",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Go365 Full Program",          "InterventionVendor": "Go365"       },             {          "ProgramName": "Diabetes",          "InterventionVendor": "HUM"       }    ],    "EnrolledProgramDetails": [] }}');    
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            stateMap.put('sRequest',reqType);
            List<String> Label = new List<String>();
            Label.add(reqType);
            HealthResource_LC_HUM.parseResponseClinicalProgram(Label,stateMap);
            
            Test.StopTest();
        }
    }
    
     /*
    * Method to create the test class
    * @param        None
    * @return       None
    */
    private static testmethod void testGetClinicalPrograms_V3()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Map<String,String> stateMap= new Map<String,String>();
            MemberPlan memberPlanObj = [SELECT Id,Member.Mbr_Gen_Key__c,Member.Enterprise_ID__c FROM MemberPlan WHERE Name = 'testpolmem2'];
            Test.StartTest();             
            HealthResource_LC_HUM.getHealthResource(memberPlanObj.Id);
            
            HttpResponse httpRes = new HttpResponse();
            httpRes.setBody('{"ClinicalProgramResponse":{"EligibleProgramDetails":[{"ProgramName":"ASO ALL","InterventionVendor":"HUM","BeginDate":null,"EndDate":null,"Comments":"Test Comments"},{"ProgramName":"ASO ALL","InterventionVendor":"HUM","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00","Comments":"Test Comments"},{"ProgramName":"ASO ALL","InterventionVendor":"HUM","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00","Comments":"Test Comments"},{"ProgramName":"ASO ALL","InterventionVendor":"HUM","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00","Comments":"Test Comments"},{"ProgramName":"ASO ALL","InterventionVendor":"HUM","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00","Comments":"Test Comments"}],"EnrolledProgramDetails":[{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":null,"EndDate":null},{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00"},{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00"},{"ProgramName":"LTSS (Long Term Support Services)","CareManagerName":"","PhoneNumber":"5025802396 x123456","BeginDate":"2017-09-16T00:00:00","EndDate":"2017-09-16T00:00:00"}]}}');
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            stateMap.put('sRequest',reqType);
            List<String> Label = new List<String>();
            Label.add(reqType);
            HealthResource_LC_HUM.parseResponseClinicalProgram(Label,stateMap);
            HealthResource_LC_HUM.getHealthResource('catchTest');
            
            Test.StopTest();
        }
    }
    
}