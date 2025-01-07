/**********************************************************************************************************************
Apex Class: HUMCaseActionProcessIntermediateTest
Description: Test class for HUMCaseActionProcessIntermediate
Created By: Chaitanya Kumar
Created On:  July 14 2014

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
 * Developer                 Code Review       Date                    Description
 * --------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar           17353             08/14/2014              Original version
 * Sagar Tapadia             17840             08/15/2014              Added Methods and Logic(like RunAs)
 * Raja Sekhar Reddy M V     19410             11/20/2014              Added assert statements.
 * Santhi Mandava            20576             01/28/2015              Code Modified to fix test class failure Defect # 164139  
 * SuryaKumari Medicherla    22791             04/28/2015              Code Modified for REQ-168974(data Model Changes for Case and Work Queue Setup are applied)   
 * SuryaKumari Medicherla    23305             05/21/2015              Code Modified to cover code added for REQ-105810
 * SuryaKumari Medicherla    24122             06/12/2015              Code Modified to cover code added for Updated REQ-105810
 * Shreya Choodamani         24122             06/18/2015              Code Modified to cover code added for Updated REQ-123923
 * Harshith Mandya           28341             21/12/2015              Removed logic to display Medicare DAC in a diff link - Defet #D26209
 * Ranjeeth Nagishetty       141064            10/25/2017              REQ 311453 - Logic for medicare good cause reinstatement flow
 * Sunil Bukya  			 293887			   10/03/2018			   REQ - 369758 - Implemented platform changes
 * Pallavi Ravishankar       338657            02/26/2019              REQ - 383432 - Medicaid Good Cause Request Process
 * Santhi Mandava                              03/12/2019              Removed "DpaaSWebServiceMockImpl" usage.
 * Atia Uzma                                   01/21/2021              User Story 1186358: PR00091574 - MF 1 - Quality - "Medicare Expedited Appeals Template" Updates (CRM)
 * Pooja Kumbhar							   03/02/2021			   REQ -2044588 pre-ex process removal for checkmarx scan issue
 * Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
 * Prudhvi Pamarthi                            07/01/2021              Code modified for US#2229116 Eliminate Old AVF Template in Case Page(s)
 * Venkata Nagasiva                                05/17/2022                US-3367406 T1PRJ0010888 - MF [1] - Rebranding - Update text references from "Humana Pharmacy" to "Centerwell Pharmacy" - Web Issues Guided Flow - Process Name
        ***********************************************************************************************************************/
@isTest
private class HUMCaseActionProcessIntermediateTest 
{
    /*
     * Method Name : constructorPositive
     * Description : Intialize variables
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void constructorPositive() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched'); 
               
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Donald Duck';
            insert oAcc;
            system.assertNotEquals(oAcc,null,'​Value not matched');
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );
             
            Policy__c oPol = HUMTestDataHelperTest.getPolicy();
            oPol.group_Name__c = oAcc.Id;
            insert oPol;
            system.assertNotEquals(oPol,null,'​Value not matched');
                
            policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = oPol.Id;
            insert oPolMem;
            system.assertNotEquals(oPolMem,null,'​Value not matched');
                
            Case oCase =HUMTestDataHelperTest.getCase();
            oCase.Policy_Member__c = oPolMem.Id;
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');
    
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Pagereference redirectCOB;
            Test.setCurrentPageReference(pg);
            
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            Apexpages.currentPage().getParameters().put('ProcessType','COB');
            
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            Test.StartTest();
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicare Other Insurance (OI) Form');
            
            caseIntermediateController = new HUMCaseActionProcessIntermediate(null);
            redirectCOB = caseIntermediateController.redirectProcess();
            System.assertEquals(caseIntermediateController.caseID,oCase.id);
            System.assertEquals(caseIntermediateController.lstProcessTypes.size(),6);
            caseIntermediateController.cancel();
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicare/Medicaid Expedited Appeal');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicare Address Verification Form');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Authorization/Referral Requirements');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicare Part D Redetermination Request');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicare Good Cause Reinstatement Form');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicaid Good Cause Request Form');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            Test.stopTest();
        }
    }


    /*
     * Method Name : loadRedirectProcessCOB
     * Description : Based on Process Type redirect to COB page.
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void loadRedirectProcessCOB() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched'); 
        
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );
            
            Case oCase =HUMTestDataHelperTest.getCase();
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');
            
            Case_Action__c cAction = HUMTestDataHelperTest.getCaseAction('COB');
            insert cAction;
            system.assertNotEquals(cAction,null,'​Value not matched');
            
            Case_Action_Association__c cAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
            cAssociation.Case__c = oCase.Id;
            cAssociation.Case_Action__c=cAction.Id;
            insert cAssociation;
            system.assertNotEquals(cAssociation,null,'​Value not matched');
            
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Test.setCurrentPageReference(pg);
    
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            PageReference prexReference;
            Pagereference redirectCOB; 
            Test.StartTest();
            
            CreateTemplateOwnerMock(oCase.Id, 'Case','Medicare Other Insurance (OI) Form');
            
            caseIntermediateController = new HUMCaseActionProcessIntermediate(null);
            caseIntermediateController.sprocessType='COB';
            prexReference = caseIntermediateController.loadProcess();
            redirectCOB = caseIntermediateController.redirectProcess();
            Test.stopTest();
            if(oCase.Policy_Member__c != null)
            System.assertEquals(redirectCOB.getParameters().get('ProcessType'),'COB');
            else
            System.assertEquals(redirectCOB,null);
            caseIntermediateController.sprocessType='COB';
            prexReference = caseIntermediateController.loadProcess();
            System.assertEquals(prexReference,null);
        }
    }

    /*
     * Method Name : loadRedirectProcessQAA
     * Description : Based on Process Type redirect to QAA page.
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void loadRedirectProcessQAA() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched'); 
        
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            Case oCase =HUMTestDataHelperTest.getCase();
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');
            
            Case_Action__c cAction = HUMTestDataHelperTest.getCaseAction('QAA');
            insert cAction;
            system.assertNotEquals(cAction,null,'​Value not matched');
            
            Case_Action_Association__c cAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
            cAssociation.Case__c = oCase.Id;
            cAssociation.Case_Action__c=cAction.Id;
            insert cAssociation;
            system.assertNotEquals(cAssociation,null,'​Value not matched');
            
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Test.setCurrentPageReference(pg);
    
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','QAA');
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            PageReference prexReference;
            Pagereference redirectQAA;    
               
            Test.StartTest();
            
            CreateTemplateOwnerMock(oCase.Id, 'Case','Medicare Other Insurance (OI) Form');
            
                caseIntermediateController = new HUMCaseActionProcessIntermediate(null);
                caseIntermediateController.sprocessType='QAA';
                prexReference = caseIntermediateController.loadProcess();
                redirectQAA = caseIntermediateController.redirectProcess();
                if(oCase.Policy_Member__c != null)
                System.assertEquals(prexReference.getParameters().get('ProcessType'),'QAA');
                else
                System.assertEquals(prexReference,null);
            
            Test.stopTest();
     
        }
    }

    /*
     * Method Name : redirectProcessexistingQAA
     * Description : Based on Action redirect to existing QAA page.
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void redirectProcessexistingQAA() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched'); 
        
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            Case oCase =HUMTestDataHelperTest.getCase();
            insert oCase;
           
            system.assertNotEquals(oCase,null,'​Value not matched');
            
            Case_Action__c cAction = HUMTestDataHelperTest.getCaseAction('QAA');
            insert cAction;
            system.assertNotEquals(cAction,null,'​Value not matched');
            
            Case_Action_Association__c cAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
            cAssociation.Case__c = oCase.Id;
            cAssociation.Case_Action__c=cAction.Id;
            insert cAssociation;
            system.assertNotEquals(cAssociation,null,'​Value not matched');
            
            ApexPages.StandardController controller = new ApexPages.StandardController(oCase);
            
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Test.setCurrentPageReference(pg);
    
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            Apexpages.currentPage().getParameters().put('ProcessType','QAA');
            
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            Pagereference redirectQAA;   
                
            Test.StartTest();
            CreateTemplateOwnerMock(oCase.Id, 'Case','Medicare Other Insurance (OI) Form');
            caseIntermediateController = new HUMCaseActionProcessIntermediate(controller);
            redirectQAA = caseIntermediateController.redirectProcess();
            Test.stopTest();
            
            if(oCase.Policy_Member__c != null)
            System.assertEquals(redirectQAA.getParameters().get('ProcessType'),'QAA');
            else
            System.assertEquals(redirectQAA,null);
        }
    }
    
    private static void CreateTemplateOwnerMock(string OwnerID, string OwnerType, String sDescription)
    {
        Template__c t = new Template__c();
        //t.Description__c = String.valueof(DateTime.now().getTime());
        t.Description__c = sDescription;

        Insert t;
        
        Template_Field__c tf = new Template_Field__c();
        tf.Template__c = t.Id;
        tf.Data_Type__c = 'String';
        
        Insert tf;
        
        Template_Submission_Owner__c tso = new Template_Submission_Owner__c();
        tso.Object_Owner_ID__c = OwnerID;
        tso.Object_Owner_Type__c = OwnerType;
        
        Insert tso;
        
        Template_Submission__c ts = new Template_Submission__c();
        ts.Template__c = t.Id;
        ts.Submission_Owner__c = tso.Id;
        
        insert ts;
                
    }

    /*
     * Method Name : redirectProcessexistingCOB
     * Description : Based on Action redirect to existing COB page.
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void redirectProcessexistingCOB() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched');
        
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            Case oCase =HUMTestDataHelperTest.getCase();
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');
    
            Case_Action__c cAction = HUMTestDataHelperTest.getCaseAction('COB');
            insert cAction;
            system.assertNotEquals(cAction,null,'​Value not matched');
            
            Case_Action_Association__c cAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
            cAssociation.Case__c = oCase.Id;
            cAssociation.Case_Action__c=cAction.Id;
            insert cAssociation;
            system.assertNotEquals(cAssociation,null,'​Value not matched');
            
            ApexPages.StandardController controller = new ApexPages.StandardController(oCase);
            
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Test.setCurrentPageReference(pg);
    
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            Apexpages.currentPage().getParameters().put('ProcessType','COB');
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            Pagereference redirectCOB;     
              
            Test.StartTest();
            caseIntermediateController = new HUMCaseActionProcessIntermediate(controller);
            redirectCOB = caseIntermediateController.redirectProcess();
            Test.stopTest();
            
            if(oCase.Policy_Member__c != null)
            System.assertEquals(redirectCOB.getParameters().get('ProcessType'),'COB');
            else
            System.assertEquals(redirectCOB,null);
        }
    }

    /*
     * Method Name : redirectProcessexistingPreEx
     * Description : Based on Action redirect to existing Pre Ex page.
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void redirectProcessexistingPreEx() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched');
        
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );
            
            Case oCase =HUMTestDataHelperTest.getCase();
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');
    
            Case_Action__c cAction = HUMTestDataHelperTest.getCaseAction('PreEx');
            insert cAction;
            system.assertNotEquals(cAction,null,'​Value not matched');
    
            Case_Action_Association__c cAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
            cAssociation.Case__c = oCase.Id;
            cAssociation.Case_Action__c=cAction.Id;
            insert cAssociation;
            system.assertNotEquals(cAssociation,null,'​Value not matched');
            
            ApexPages.StandardController controller = new ApexPages.StandardController(oCase);
            
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Test.setCurrentPageReference(pg);
    
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            Apexpages.currentPage().getParameters().put('ProcessType','PreEx');
            
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            Pagereference redirectPreEx; 
                  
            Test.StartTest();
            caseIntermediateController = new HUMCaseActionProcessIntermediate(controller);
            caseIntermediateController.sMedicareDACTypePage ='Medicare Good Cause';
            redirectPreEx = caseIntermediateController.redirectProcess();
            Test.stopTest();
            
            if(oCase.Policy_Member__c != null)
            System.assertEquals(redirectPreEx.getParameters().get('ProcessType'),'PreEx');
            else
            System.assertEquals(redirectPreEx,null);
        }
    }
    
    /*
     * Method Name : redirectDisenrollmentProcess
     * Description : Based on Action redirect to existing Pre Ex page.
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void redirectDisenrollmentProcess() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched');
        
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'MMedicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Donald Duck';
            insert oAcc;
            system.assertNotEquals(oAcc,null,'​Value not matched');
            
            Policy__c oPol = HUMTestDataHelperTest.getPolicy();
            oPol.group_Name__c = oAcc.Id;
            insert oPol;
            system.assertNotEquals(oPol,null,'​Value not matched');
                
            policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = oPol.Id;
            insert oPolMem;
            system.assertNotEquals(oPolMem,null,'​Value not matched');
            
            Case oCase =HUMTestDataHelperTest.getCase();
            oCase.Policy_Member__c = oPolMem.Id;
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');
            
            ApexPages.StandardController controller = new ApexPages.StandardController(oCase);
            
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Test.setCurrentPageReference(pg);
    
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            Apexpages.currentPage().getParameters().put('ProcessType','Medicare DACPage');
            Apexpages.currentPage().getParameters().put(Label.HUMTabId,null);
            
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            Pagereference redirectPreEx;       
            Test.StartTest();
            caseIntermediateController = new HUMCaseActionProcessIntermediate(controller);
            redirectPreEx = caseIntermediateController.redirectProcess();
            caseIntermediateController.cancel();
            Test.stopTest();
            Apexpages.currentPage().getParameters().put('ProcessType',null);
            
            Attachment_Log__c oAttachLog = new Attachment_Log__c();
            oAttachLog.Attachment_Key__c = HUMConstants__c.getInstance('HUMAttachmentLogForDisenrollment').StringValue__c;
            oAttachLog.Attachment_Type__c = 'FM';
            oAttachLog.Case__c = oCase.id;
            insert oAttachLog;
            system.assertNotEquals(oAttachLog,null,'​Value not matched');
            
            Attachment_Log_Item__c oAttchLogItem = new Attachment_Log_Item__c();
            oAttchLogItem.Attachment_Log__c = oAttachLog.id;
            oAttchLogItem.Value__c = 'URL';
            insert oAttchLogItem;
            system.assertNotEquals(oAttchLogItem,null,'​Value not matched');
            
            oCase.Policy_Member__c = null;
            update oCase;
            
            Apexpages.currentPage().getParameters().put('ProcessType',null);
            
            HUMCaseActionProcessIntermediate caseIntermediateController2 = new HUMCaseActionProcessIntermediate(controller);
            redirectPreEx = caseIntermediateController2.redirectProcess();
        }
    }
    /*
     * Method Name : redirectMedicareGoodcause
     * Description : Based on Action redirect to existing Pre Ex page.
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void redirectMedicareGoodcause() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched');
        
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );

            HUMTemplateSettings__c objtemplate = new HUMTemplateSettings__c();
            objtemplate.name ='Medicare Good Cause Reinstatement';
            objtemplate.VFPageName__c='HUMMedicareGoodCauseReinstatementProcess';
            objtemplate.ProcessActionType__c ='Medicare Good Cause Reinstatement Form';
           
             insert objtemplate;
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Donald Duck';
            insert oAcc;
            system.assertNotEquals(oAcc,null,'​Value not matched');
            
            Policy__c oPol = HUMTestDataHelperTest.getPolicy();
            oPol.group_Name__c = oAcc.Id;
            insert oPol;
            system.assertNotEquals(oPol,null,'​Value not matched');
                
            policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = oPol.Id;
            insert oPolMem;
            system.assertNotEquals(oPolMem,null,'​Value not matched');
            
            Case oCase =HUMTestDataHelperTest.getCase();
            oCase.Policy_Member__c = oPolMem.Id;
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');
            
            ApexPages.StandardController controller = new ApexPages.StandardController(oCase);
            
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Test.setCurrentPageReference(pg);
    
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            Apexpages.currentPage().getParameters().put('ProcessType','Medicare DACPage');
            Apexpages.currentPage().getParameters().put(Label.HUMTabId,null);
            
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            Pagereference redirectPreEx;       
            Test.StartTest();
            caseIntermediateController = new HUMCaseActionProcessIntermediate(controller);
            redirectPreEx = caseIntermediateController.redirectProcess();
            caseIntermediateController.cancel();
            Test.stopTest();
            Apexpages.currentPage().getParameters().put('ProcessType',null);
            
            Attachment_Log__c oAttachLog = new Attachment_Log__c();
            oAttachLog.Attachment_Key__c = HUMConstants__c.getInstance('HUMAttachmentLogForDisenrollment').StringValue__c;
            oAttachLog.Attachment_Type__c = 'FM';
            oAttachLog.Case__c = oCase.id;
            insert oAttachLog;
            system.assertNotEquals(oAttachLog,null,'​Value not matched');
            
            Attachment_Log_Item__c oAttchLogItem = new Attachment_Log_Item__c();
            oAttchLogItem.Attachment_Log__c = oAttachLog.id;
            oAttchLogItem.Value__c = 'URL';
            insert oAttchLogItem;
            system.assertNotEquals(oAttchLogItem,null,'​Value not matched');
            
            oCase.Policy_Member__c = null;
            update oCase;
            
            Apexpages.currentPage().getParameters().put('ProcessType',null);
            
            HUMCaseActionProcessIntermediate caseIntermediateController2 = new HUMCaseActionProcessIntermediate(controller);
            redirectPreEx = caseIntermediateController2.redirectProcess();
        }
    }
    static testMethod void constructorPositive1() 
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert u;
        system.assertNotEquals(u,null,'​Value not matched'); 
               
        system.runAs(u)
        {
            insert new HUMConstants__c( Name = 'HUMLastIndex' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMFlowAddressVerifDesc' , StringValue__c = 'Medicare Address Verification Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareAVF' , StringValue__c = 'Medicare AVF');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpedited' ,StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareExpeditedDescr' , StringValue__c = 'Medicare/Medicaid Expedited Appeal');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOI' , StringValue__c = 'Medicare OI');
            insert new HUMConstants__c( Name = 'HUMFlowMedicareOIDescription' , StringValue__c = 'Medicare Other Insurance (OI) Form');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartD' , StringValue__c = 'Medicare Part D');
            insert new HUMConstants__c( Name = 'HUMFlowMedicarePartDescr' , StringValue__c = 'Medicare Part D Redetermination Request');
            insert new HUMConstants__c( Name = 'HUMATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            insert new HUMConstants__c( Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' , StringValue__c = 'Medicare_Election_Lock_In_Template');
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Donald Duck';
            insert oAcc;
            system.assertNotEquals(oAcc,null,'​Value not matched');
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            List<HUMTemplateSettings__c> lstTemplatesData = Test.loadData(HUMTemplateSettings__c.sObjectType,'HUMTemplateSettings');
            System.assert(lstTemplatesData.size() > 0 );

            HUMTemplateSettings__c objtemplate = new HUMTemplateSettings__c();
            objtemplate.name ='Medicaid Good Cause Request';
            objtemplate.VFPageName__c='MedicaidGoodCauseRequestProcess_VF_HUM';
            objtemplate.ProcessActionType__c ='Medicaid Good Cause Request Form';
           
             insert objtemplate;
             
            Policy__c oPol = HUMTestDataHelperTest.getPolicy();
            oPol.group_Name__c = oAcc.Id;
            insert oPol;
            system.assertNotEquals(oPol,null,'​Value not matched');
                
            policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = oPol.Id;
            insert oPolMem;
            system.assertNotEquals(oPolMem,null,'​Value not matched');
                
            Case oCase =HUMTestDataHelperTest.getCase();
            oCase.Policy_Member__c = oPolMem.Id;
            insert oCase;
            system.assertNotEquals(oCase,null,'​Value not matched');

            ApexPages.StandardController controller = new ApexPages.StandardController(oCase);
    
            PageReference pg =Page.HUMCaseActionProcessIntermediate;
            Pagereference redirectCOB;
            Test.setCurrentPageReference(pg);
            
            Apexpages.currentPage().getParameters().put('CaseId',oCase.Id);
            Apexpages.currentPage().getParameters().put('ProcessType','COB');
            
            HUMCaseActionProcessIntermediate caseIntermediateController ;
    
            Test.StartTest();
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicare Other Insurance (OI) Form');
            
            caseIntermediateController = new HUMCaseActionProcessIntermediate(null);
            redirectCOB = caseIntermediateController.redirectProcess();
            System.assertEquals(caseIntermediateController.caseID,oCase.id);
            System.assertEquals(caseIntermediateController.lstProcessTypes.size(),6);
            caseIntermediateController.cancel();
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Creditable Coverage Attestation');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            caseIntermediateController.createPageref('Medicaid Good Cause Request');
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'PCP/PCD Questions');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Invoice Request');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Centerwell Pharmacy Web Issues');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Contact Demographic Update - Commercial');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);
            
            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Critical Demographic Update - Commercial');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Contact Demographic Update - Medicare');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Critical Demographic Update - Medicare');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Address Update - Medicare');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Address Update - Medicaid');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'DEAA Medicare Opt Out');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicaid PAR Provider Not Accepting');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicare Disenrollment');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(null);

            CreateTemplateOwnerMock(oCase.Id, 'Case', 'Medicaid Good Cause Request Form');
            caseIntermediateController  = new HUMCaseActionProcessIntermediate(controller);
            Test.stopTest();
        }
    }
    
}