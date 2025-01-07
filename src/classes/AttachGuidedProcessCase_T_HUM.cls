/****************************************************************************
Apex Class Name  : AttachGuidedProcessCase_T_HUM
Version          : 1.0 
Created On       : 05/13/2016
Function         : Test class to AttachGuidedProcessCase_C_HUM Apex class

Modification Log :
* Developer Name              Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi         27942                      05/13/2016                 Original Version 
* Prachi Bhasin                33416                      07/18/2016                 Changes made as part of REQ- 270349
* Praveen Kumar Parimi         39715                      09/15/2016                 Changes made as part of Work Queue Pop Up.
* Pradeepkumar Dani										  04/04/2017 				 CTCI Data model update REQ - 306337
* Anil parvathaneni                                       26/06/2022                 User Story 3252318 - Code Coverage issue Resolution - Test Classes
*************************************************************************************************************************/
@isTest
private class AttachGuidedProcessCase_T_HUM
{
   /*
    * This is the test method which loads data for GuidedProcessDetail_T_HUM. 
    * @param     NA
    * @return    void 
    */ 
    @testSetup static void testData()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.Lastname == 'test User1',true);
         
        System.runAs(u)
        {        
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;        
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);            
        
            Policy__c pol = HUMTestDataHelperTest.getPolicy();
            pol.Group_Name__c = acc1.Id;
            pol.Name = 'Test Policy';
            insert pol;
            system.assertEquals(pol.Name,'Test Policy');
            system.assertEquals(pol.Group_Name__c == acc1.Id,true);            
        
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1';
            polMember.Policy__c=pol.Id;
            polMember.Member__c=acc1.Id;
            insert polMember;
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1');
            system.assertEquals(polMember.Policy__c,pol.Id);  
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
	        Classification_Type__c ctRec = new Classification_Type__c();
	        ctRec.name = 'Claims (RSO)';
	        ctRec.Is_Active__c=true;
	        ctList.add(ctRec);
	        
	        insert ctList;
	        system.assertEquals(ctList.size(),1);
	        
	        Work_Queue_Setup__c wqsRecord = new Work_Queue_Setup__c();
            wqsRecord.Name = 'Cincinnati CRU';
            wqsRecord.Classification_Type__c = ctRec.Id;
            insert wqsRecord;
            system.assertEquals(wqsRecord.Name, 'Cincinnati CRU');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);    
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
                
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);                
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Filling',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
	        CTCI_Junction__c ctciRec = new CTCI_Junction__c();
	        ctciRec.Classification_Type__c = ctRec.id;
	        ctciRec.Classification__c = classificationRec.Id;
	        ctciRec.Intent__c = intentRec.id;
	        ctciRec.IsActive__c = true;
	        ctciList.add(ctciRec);
	        insert ctciList;
	        system.assertEquals(ctciList.size(),1);
            
            Case oCase = new Case(	AccountId=acc1.Id,
            						CTCI_List__c = ctciRec.Id,
            						General_Case__c = true,
            						Interacting_With_Type__c = 'Group',
            						Interacting_With__c=acc1.Id, 
            						Due_Date__c=Date.Today(), 
            						Case_Owner__c=UserInfo.getUserName(), 
            						Owner_Queue__c='Cincinnati CRU1', 
            						Work_Queue_View_Name__c='Cincinati CRU1',
            						Service_Center__c ='Cincinnati',
            						Department__c='CRU1', 
            						status='In Progress');
            insert oCase;
            system.assertNotEquals(oCase,null,'Value not matched');
                 
            Template__c oTempate = new Template__c(Description__c ='Medicare Address Verification Form');
            insert oTempate;
            system.assertNotEquals(oTempate,null,'Value not matched');
            system.assertEquals(oTempate.Description__c ,'Medicare Address Verification Form');   
            
            Template_Field__c oTemplateField = new Template_Field__c(Name= 'SubmissionData', Data_Type__c= 'String',Template__c = oTempate.Id );
            insert oTemplateField ;
            system.assertNotEquals(oTemplateField ,null,'Value not matched'); 
            system.assertEquals(oTemplateField.Name,'SubmissionData');   
            
            Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase.Id,  Object_Owner_Type__c = 'Case')  ;
            insert oTemplateSubmsissionOwner;
            system.assertNotEquals(oTemplateSubmsissionOwner,null,'Value not matched'); 
            system.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c  ,'Case'); 
        
            Template_Submission_Owner__c  oTemplateSubmsissionOwner1 = new Template_Submission_Owner__c(Object_Owner_ID__c = 'a9b09f4d-0911-4f05',  Object_Owner_Type__c = 'Case')  ;
            insert oTemplateSubmsissionOwner1;
            system.assertNotEquals(oTemplateSubmsissionOwner1,null,'Value not matched'); 
            system.assertEquals(oTemplateSubmsissionOwner1.Object_Owner_Type__c  ,'Case'); 
            
            Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
            insert oTempalteSubmsision;
            system.assertNotEquals(oTempalteSubmsision,null,'Value not matched');  
            
            Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='TestValue' );
            insert oTemplateSubmissionData ;
            system.assertNotEquals(oTemplateSubmissionData ,null,'Value not matched'); 
            
            List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
    
            lstConstanst.Add(new HUMConstants__c(Name='HUMLISTSIZEONE',IntegerValue__c=1,StringValue__c='0'));
            lstConstanst.Add(new HUMConstants__c(Name='HUMConstantHundered',IntegerValue__c=100,StringValue__c='0'));
            
            insert lstConstanst;
            System.assert(lstConstanst.size() > 0 );
        }
    }
    /*
     * This is the test method which is used to test the GuidedProcessDetail_C_HUM. 
     * Return type : Void
     * Paramater   : NA
     */     
    private static testMethod void testGuidedProcessDetail()
    {
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];                           
        system.runAs(u)
        {       
            Test.StartTest();    
            Account objAcc = [Select Id,Name from Account limit 1];
            System.assertEquals('Test Account Cases1',objAcc.Name);            
            
            AttachGuidedProcessCase_C_HUM objClass = new AttachGuidedProcessCase_C_HUM();
            objClass.sObjectId = objAcc.Id;
            
            objClass.selectedCaseOption  = 'Existing Case';
            objClass.accId = objAcc.Id;
            objClass.sObjName = 'Policy_Member__c'; 
            objClass.displayGuidedProcessCaseList();             
            objClass.sCaseNumber = '';
            objClass.reRenderQueueSelectionPage = true;
            objClass.userCurrentQueue = 'Cincinati calls'; 
            objClass.search();
            System.assertEquals(1, objClass.stdsetcon.getResultSize());
            
            System.assertEquals(false, objClass.hasNext); 
            System.assertEquals(false, objClass.hasPrevious);
            System.assertEquals(1, objClass.pageNumber );
            System.assertEquals(1, objClass.lstCases.size());
            System.assertEquals(100, objClass.iConstantValue);
            System.assertEquals('CreatedDate', objClass.sSortExpression );    
            
            ApexPages.currentPage().getParameters().put('caseId', objClass.lstFinalCases[0].Id);
            
            objClass.getCaseId();
            System.assertEquals(objClass.lstFinalCases[0].Id, objClass.sCaseId );
            
            objClass.sSortExpression = 'CTCI_List__r.Intent__r.Name'; 
            
            System.assertEquals('DESC', objClass.getSortDirection() );
            objClass.setSortDirection('ASC');
            System.assertEquals('ASC', objClass.getSortDirection() );
            
            objClass.sortResultPage();
            System.assertEquals('Filling', objClass.lstFinalCases[0].CTCI_List__r.Intent__r.Name );

            objClass.sCaseNumber = objClass.lstFinalCases[0].CaseNumber;
            objClass.search();
            System.assertEquals(1, objClass.stdsetcon.getResultSize());
            
            objClass.sObjectId = objAcc.Id;
            objClass.createCaseAndRedirect();           
            
            objClass.first();
            System.assertEquals(1,objClass.stdsetcon.getResultSize());
            
            objClass.previous();
            System.assertEquals(1,objClass.stdsetcon.getResultSize());
            
            objClass.last();
            System.assertEquals(1,objClass.stdsetcon.getResultSize());
            
            objClass.next();
            System.assertEquals(1,objClass.stdsetcon.getResultSize());
            
            objClass.stdsetcon = null;
            System.assertEquals(null, objClass.hasNext);
            System.assertEquals(null, objClass.hasPrevious );
            System.assertEquals(null, objClass.pageNumber );            

            objClass.sSortExpression = 'CreatedDate';            
            
            objClass.sSortDirection = '';
            System.assertEquals('DESC', objClass.getSortDirection());

            objClass.checkGuidedProcessPresent();
            List<Template_submission__c> lstTempalteSubmsision = [select id from Template_submission__c where Version__c = 1.00 LIMIT 1];
            objClass.sSubId =lstTempalteSubmsision[0].id;
            objClass.updateWhoID(); 
            System.assertEquals(objClass.sSortExpression,'CreatedDate');   
            Test.StopTest();                            
        }
    }
    
    /*
     * This is the test method which is used to test the GuidedProcessDetail_C_HUM. 
     * @param         NA
     * @return        void
     */     
    private static testMethod void testGuidedProcessDetail1()
    {
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];                           
        system.runAs(u)
        {       
            Test.StartTest();    
            Account objAcc = [Select Id,Name from Account limit 1];
            Intent__c intObj = [Select id from Intent__c limit 1];
            System.assertEquals('Test Account Cases1',objAcc.Name);
            
            Case obcase = [select ID from case where CTCI_List__r.Intent__c =: intObj.id LIMIT 1];
            
            Template__c temp = [select id,Name,Description__c from Template__c where Description__c = 'Medicare Address Verification Form' LIMIT 1 ];
            temp.Description__c = 'Medicare Part D';
            update temp;
            AttachGuidedProcessCase_C_HUM objClass = new AttachGuidedProcessCase_C_HUM();            
            objClass.sObjectId = objAcc.Id;
            objClass.sSelectedCaseId = obcase.ID;
            objClass.createCaseAndRedirect();
            objClass.sSelectedCaseNumber = '1234567';
            objClass.stdsetcon = null;            
            objClass.checkguidedprocesspresent(); 
            objClass.checkUserQueue();
            AttachGuidedProcessCase_C_HUM.updateQueueLogic();           
            system.assertEquals(objClass.sSelectedCaseNumber,'1234567');            
            Test.StopTest();            
        }
     }
}