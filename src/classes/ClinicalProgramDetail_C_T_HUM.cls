/*****************************************************************************************************************************************************************************
    @Apex Class Name  : ClinicalProgramDetail_C_T_HUM
    @Version          : 1.0
    @Created Date     : Apr 14, 2020
    @Function         : Test class for ClinicalProgramDetail_C_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Imran                                               04/14/2020                    Original Version  
******************************************************************************************************************************************************************************/
@isTest
private class ClinicalProgramDetail_C_T_HUM
{
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
            
            
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'GetClinicalPrograms_V2';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert calloutSettings;
            
             HUM_Webservice_Callout__c calloutSettings1 = new HUM_Webservice_Callout__c();
            calloutSettings1.Name = 'ClinicalPrograms';
            calloutSettings1.Active__c = True;
            calloutSettings1.Enable_Service_Tester__c = True;
            calloutSettings1.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings1.Content_Type__c = 'application/json';
            calloutSettings1.Request_Type__c = 'Post';
            calloutSettings1.Timeout__c = '12000';
            calloutSettings1.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert calloutSettings1;
             
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
            Policy_Member__c objPolMem = [select id,Name,Member__c, Member__r.Mbr_Gen_Key__c from Policy_Member__c where Name = 'testpolmem11'];
            Test.StartTest();             
             ClinicalProgramDetail_C_HUM clinicalProgram = new ClinicalProgramDetail_C_HUM();
             ClinicalProgramDetail_C_HUM.getClinicalNurseCareData(Id.valueOf(objPolMem.id), String.valueOf(objPolMem.Member__r.Mbr_Gen_Key__c));
             HttpResponse response = new HttpResponse();
             response.setBody('{"ClinicalProgramResponse": {    "EligibleProgramDetails":    [             {          "ProgramName": "ASO ALL",          "InterventionVendor": "HUM"       },             {          "ProgramName": "ASO Preventative",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Behavioral Health Preauths",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Brain Health MyStrength",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Cancer",          "InterventionVendor": "HUM"       },             {          "ProgramName": "BHCCM",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Cardiac Consultation",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Chronic Kidney Disease",          "InterventionVendor": "Village Health"       },             {          "ProgramName": "Chronic Obstructive Pulmonary Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Clinical Program Framework Only",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Commercial Case Management",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Coronary Artery Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Dental Clinical Outreach",          "InterventionVendor": "HUM"       },             {          "ProgramName": "EAP - 3 visits",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "End Stage Renal Disease",          "InterventionVendor": "Village Health"       },             {          "ProgramName": "Commercial Utilization Management & Concurrent Review",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Genetic/Molecular Testing Preauthorizations",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Getting Started Guide",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Go365 Self Directed Content",          "InterventionVendor": "Go365"       },             {          "ProgramName": "Greeting Card",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Achieve",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Congestive Heart Failure",          "InterventionVendor": "VRI"       },             {          "ProgramName": "Humana Behavioral Health Autism Program",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana First",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Pharmacy",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana TeleMedicine",          "InterventionVendor": "Doctors On Demand"       },             {          "ProgramName": "Humana Nurse",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "HumanaBeginnings",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Internal Diabetes",          "InterventionVendor": "HUM"       },             {          "ProgramName": "NICU_Neonatal",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Non-Utilizer Letter",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana US Imaging Services",          "InterventionVendor": "US Imaging Services"       },             {          "ProgramName": "MOD A - Outpatient Physical, Occupational, Speech Therapy Preauthorizations",          "InterventionVendor": "OrthoNet"       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "Oncology Analytics"       },             {          "ProgramName": "Postcard/Magnet Mailer",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Radiation Therapy Preauthorizations",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Vision Clinical Outreach",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Welcome Letter",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Weight Watchers",          "InterventionVendor": "Weight Watchers"       },             {          "ProgramName": "Value of Home Delivery",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Transplant Program",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Specialized Onsite Care Management",          "InterventionVendor": "Specialized On-Site Care Management"       },             {          "ProgramName": "Rare Disease",          "InterventionVendor": "HUM"       },             {          "ProgramName": "RadConsult-Outpatient Radiology Preauthorizations",          "InterventionVendor": "HealthHelp"       },             {          "ProgramName": "Personal Nurse",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Outsourced Health Assessment",          "InterventionVendor": "WellSource"       },             {          "ProgramName": "Oncology Quality Management",          "InterventionVendor": "New Century Health"       },             {          "ProgramName": "MOD D – Spinal Surgery, Pain Management",          "InterventionVendor": "OrthoNet"       },             {          "ProgramName": "Maximize Your Benefits",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Integrated Care Managment",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana Vitality Full Program",          "InterventionVendor": "Vitality"       },             {          "ProgramName": "Humana Standard Commercial Preauthorization List",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Humana PHQ",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Health Planning & Support",          "InterventionVendor": "HUM"       },             {          "ProgramName": "Health Coach-Wellness Standard",          "InterventionVendor": "Humana Behavioral Health"       },             {          "ProgramName": "Go365 Full Program",          "InterventionVendor": "Go365"       },             {          "ProgramName": "Diabetes",          "InterventionVendor": "HUM"       }    ],    "EnrolledProgramDetails": [] }}');   
             Test.setContinuationResponse('label', response);             
             ClinicalProgramDetail_C_HUM.parseResponseClinicalProgram(new List<string>{(String.valueOf(response))},null);
             clinicalProgram.showPageMessage();
             Test.StopTest();
        }
    }
    
}