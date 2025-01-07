/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCOBProcessController_V1_Test
Version          : 1.0
Created Date     : Aug 04 2014
Function         : Test Class for HUMCOBProcessController_V1
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor 	     	                     08/04/2020                   Original Version created for US825176
* Mohammed Noor                                  08/14/2020					  Added changes for defect fixes.
* Mohammed Noor								     02/09/2021					  Changes done for USER STORY 1700807.
* Jasmeen Shangari                          04/21/2021           Checkmarx fixes Azure ID - 2163309
****************************************************************************************************************************************************************************/
@isTest
private class HUMCOBProcessController_V1_Test {

    /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
    @testSetup static void initializeTestData()
    {
		User oUser = HUMTestDataHelperTest.createTestUser('COBV1User','Customer Care Specialist');
        oUser.Current_Queue__c = 'Louisville RSO Calls Support';
        insert oUser;
        system.assertEquals(oUser.lastname=='test COBV1User',true);        
		
		system.runAs(oUser)
		{
			list<Account> lstAccounts = new list<Account>();		
			//Known Member
			Account oAcc = HUMTestDataHelperTest.getAccount(); 
	        oAcc.FirstName ='TestMemberFN';
	        oAcc.LastName ='TestMemberLN';        
	        oAcc.Enterprise_ID__c = '1234567890';
	        oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();        
	        lstAccounts.add(oAcc);               
	        
	        //Group Account
	        Account oAcc1 = HUMTestDataHelperTest.getAccount(); 
	        oAcc1.Name ='TestGroupAccount';                
	        oAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
	        oAcc1.Enterprise_ID__c = '1234567896';
	        oAcc1.Source_Customer_Key__c = '54321';        
	        lstAccounts.add(oAcc1);
	        
	        Account oAcc2 = HUMTestDataHelperTest.getAccount(); 
	        oAcc2.FirstName ='TestFN';
	        oAcc2.LastName ='TestLN';        
	        oAcc2.Enterprise_ID__c = '1234567891';
	        oAcc2.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();        
	        lstAccounts.add(oAcc2);
	                        
	        insert lstAccounts;
	        
	        list<Account> lstAccounts2 = new list<Account>();
			for(integer i=0; i<10; i++)
			{
				Account oAcc3 = new Account(); 
				oAcc3.FirstName ='TestFN' +String.valueOf(i);
				oAcc3.LastName ='TestLN' +String.valueOf(i);        
				oAcc3.Enterprise_ID__c = '1234567891'+String.valueOf(i);
				oAcc3.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();        
				lstAccounts2.add(oAcc3);
			}
	        insert lstAccounts2;
	        
	                
	        Product2 oProd = HUMTestDataHelperTest.getProduct();
	        oProd.Name = 'TestProduct';
	        oProd.Product_Type__c = 'Medicare';
	        oProd.Sold_Product_Key_Value__c = '23456789';            
	        insert oProd;                                    
	        
	        Policy__c oPol = HUMTestDataHelperTest.getPolicy();
	        oPol.Name = 'TestPolicy';
	        oPol.Group_Name__c = oAcc1.Id;
	        oPol.Coverage_Plan_Effective_Date__c = '01/01/2020';
	        oPol.Coverage_Plan_end_date__c = '12/31/2020';
	        oPol.Product__c = oProd.Id;
	        oPol.Platform__c = 'LV';
	        insert oPol;
	        System.assert(oPol.Id != null);
			
			Policy_Plan_Details__c oPlan = new Policy_Plan_Details__c();
            oPlan.Policy__c = oPol.Id;
            oPlan.Product__c = oProd.Id;
            oPlan.Name = 'Test Policy Plan';
            oPlan.Coverage_Plan_Effective_Date__c='01/01/2020';
            oPlan.Coverage_Plan_End_Date__c='12/31/2020';
            oPlan.Policy_Plan_External_ID__c = '00|ODS|2800004665517|45|2020-01-01';
            insert oPlan;
            		        
	        Policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
	        oPolMem.Member__c = oAcc.Id;
	        oPolMem.Policy__c = oPol.Id;
	        oPolMem.Relationship__c = 'Subscriber';
	        oPolMem.Policy_Member_External_Id__c = '00|ODS|123456789A|89|89|01-01-20220';
	        oPolMem.Effective_Date__c = '01/01/2020';
	        oPolMem.End_Date__c = '12/31/2020';
	        insert oPolMem;
	        
	        Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
	        oPolMem1.Member__c = oAcc2.Id;
	        oPolMem1.Policy__c = oPol.Id;
	        oPolMem1.Relationship__c = 'Spouse';
	        oPolMem1.Policy_Member_External_Id__c = '00|ODS|123456789B|89|89|01-01-2020';
	        oPolMem1.Effective_Date__c = '01/01/2020';
	        oPolMem1.End_Date__c = '12/31/2020';
	        oPolMem1.Subscriber__C = oPolMem.Id;	       	        
	        insert oPolMem1;	        	
	        
	        list<Policy_Member__c> lstPolicyMem = new list<Policy_Member__c>();
	        for(integer i=0; i<10; i++)
	        {
	        	Policy_Member__c oPolMem2 = HUMTestDataHelperTest.getPolicyMember();
		        oPolMem2.Member__c = lstAccounts2[i].ID;
		        oPolMem2.Policy__c = oPol.Id;
		        oPolMem2.Relationship__c = 'Child';
		        oPolMem2.Policy_Member_External_Id__c = '00|ODS|123456789'+String.valueOf(i)+'|89|89|01-01-2020';
		        oPolMem2.Effective_Date__c = '01/01/2020';
		        oPolMem2.End_Date__c = '12/31/2020';
		        oPolMem2.Subscriber__C = oPolMem.Id;		        
		        lstPolicyMem.add(oPolMem2);
	        }
	        if(lstPolicyMem!=null && !lstPolicyMem.isEmpty())	insert lstPolicyMem;
	        	        
	
	        Case oCase = HUMTestDataHelperTest.getCase();
	        oCase.AccountId = oAcc2.Id;
	        oCase.Policy_Member__c = oPolMem1.Id;
	        oCase.Interacting_With__c = oAcc2.Id;
	        oCase.Interacting_With_Name__c = 'TestMemberFN TestMemberLN';
	        oCase.OwnerID = oUser.ID;
	        insert oCase;
	        System.assert(oCase.Id != null);
	
	        Case_Action__c  oCaseAction = HUMTestDataHelperTest.getCaseAction('COB');
	        oCaseAction.RecordTypeID = Schema.SObjectType.Case_Action__c.getRecordTypeInfosByName().get('COB').getRecordTypeId();
	        insert oCaseAction;
	        System.assert(oCaseAction.Id != null);
	        
	        List<HUMTemplateCOBColumns__c> lstCobColumns = new List<HUMTemplateCOBColumns__c>();
	        HUMTemplateCOBColumns__c cobColumn = HUMTestDataHelperTest.getCOBColumn();
	        cobColumn.Name = 'COB_V1';
	        cobColumn.Column_Titles__c = 'Member,Relationship,OI Carrier Name,OI Effective Date,OI End Date,OI Primary,OI Last Verified Date,Remove OI,Add OI,Verified';
	        lstCobColumns.add(cobColumn);
	
	        HUMTemplateCOBColumns__c addOIColumn = HUMTestDataHelperTest.getCOBColumn();
	        addOIColumn.Name = 'Add OI';
	        addOIColumn.Column_Titles__c = 'Member,Relationship,New Effective Date,OI Carrier Name,Other Carrier Name';
	        lstCobColumns.add(addOIColumn);
	
	        HUMTemplateCOBColumns__c removeOIColumn = HUMTestDataHelperTest.getCOBColumn();
	        removeOIColumn.Name = 'Remove OI';
	        removeOIColumn.Column_Titles__c = 'Member,Relationship,OI Should Never Have Been Listed,Cancellation Date,OI Carrier Name,Other Carrier Name';
	        lstCobColumns.add(removeOIColumn);
	
	        if(lstCOBColumns != null && lstCOBColumns.size() >0 ){insert lstCOBColumns; }
	
	        for(HUMTemplateCOBColumns__c col : lstCOBColumns)
	        {
	            System.assert(col.Id != null);
	        }       
	
	        Id cobQuestionRecordTypeId = Schema.SObjectType.Question__c.getRecordTypeInfosByName().get('COB').getRecordTypeId();
	        list<Question__c> lstParentQuestions = new list<Question__c>();
	        Question__c  oQues1 = HUMTestDataHelperTest.getQuestion('Q-0001','Drop down','Subscriber covered under external policy?',1);
	        oQues1.RecordTypeId = cobQuestionRecordTypeId;
	        oQues1.Question_Values__c = 'Yes;No';
	        lstParentQuestions.add(oQues1);
	        
	        Question__c oQuesA = HUMTestDataHelperTest.getQuestion('Q-0006','Grid','Add OI Information',0);
	        oQuesA.RecordTypeId = cobQuestionRecordTypeId;
	        lstParentQuestions.add(oQuesA);
	        
	        Question__c oQuesB = HUMTestDataHelperTest.getQuestion('Q-0006','Grid','Existing OI Information',0);
	        oQuesB.RecordTypeId = cobQuestionRecordTypeId;
	        lstParentQuestions.add(oQuesB);
	        
	        insert lstParentQuestions; 
	
	        List<Question__c >lstQuestions = new List<Question__c>();
	        Question__c  oQues2 = HUMTestDataHelperTest.getQuestion('Q-0002','Drop down','Other Insurance Policy subscriber name',2);
	        oQues2.Parent_Question__c = oQues1.Id;
	        oQues2.Dependent_Value__c = 'Yes';
	        oQues2.RecordTypeId = cobQuestionRecordTypeId;
	        oQues2.Question_Values__c = 'needs to be loaded from parent';
	        lstQuestions.add(oQues2);
	
	        Question__c  oQues3 = HUMTestDataHelperTest.getQuestion('Q-0003','Date','Indicate the effective date for Medicare Part A Coverage',3);
	        oQues3.Parent_Question__c = oQues1.Id;
	        oQues3.Dependent_Value__c = 'Yes';
	        oQues3.RecordTypeId = cobQuestionRecordTypeId;
	        lstQuestions.add(oQues3);
	
	        Question__c  oQues4 = HUMTestDataHelperTest.getQuestion('Q-0004','Text','Relationship',4);
	        oQues4.Parent_Question__c = oQues1.Id;
	        oQues4.Dependent_Value__c = 'Yes';
	        oQues4.RecordTypeId = cobQuestionRecordTypeId;
	        lstQuestions.add(oQues4);
	
	        Question__c  oQues5 = HUMTestDataHelperTest.getQuestion('Q-0005','Text Area','Other reasons',5);
	        oQues5.Parent_Question__c = oQues1.Id;
	        oQues5.Dependent_Value__c = 'Yes';
	        oQues5.RecordTypeId = cobQuestionRecordTypeId;
	        lstQuestions.add(oQues5);               
	        
	        insert lstQuestions;        
	                
	        List<Tabular_Question__c> lstTabularQuestions = new List<Tabular_Question__c>();
	        Tabular_Question__c tabQues1 = HUMTestDataHelperTest.getTabularQuestion('TQ-0001', 'Text', 'Member', 1);
	        tabQues1.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues1);
	        
	        Tabular_Question__c tabQues2 = HUMTestDataHelperTest.getTabularQuestion('TQ-0002', 'Text', 'Relationship', 2);
	        tabQues2.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues2);
	        
	        Tabular_Question__c tabQues3 = HUMTestDataHelperTest.getTabularQuestion('TQ-0003', 'Checkbox', 'Remove OI', 3);
	        tabQues3.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues3);
	        
	        Tabular_Question__c tabQues4 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Checkbox', 'Add OI', 4);
	        tabQues4.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues4);
	        
	        Tabular_Question__c tabQues5 = HUMTestDataHelperTest.getTabularQuestion('TQ-0005', 'Checkbox', 'Verified', 5);
	        tabQues5.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues5);
	        
	        Tabular_Question__c tabQues6 = HUMTestDataHelperTest.getTabularQuestion('TQ-0006', 'Text', 'OI Should Never Have Been Listed', 6);
	        tabQues6.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues6);
	        
	        Tabular_Question__c tabQues7 = HUMTestDataHelperTest.getTabularQuestion('TQ-0007', 'Drop down', 'OI Carrier Name', 7);
	        tabQues7.Question_Values__c = 'Aetna; Blue Cross/ Blue Shield; Cigna; Guardian;Humana-CAS;Humana-MTV;Medical Mutual;'+
	                'Medicare-Part A; Medicare-Part A & B; Other Carrier; Pacificare;Priority Health;Sisco;Unicare;'+
	                'United Heathcare;United Medical Resources; Unknown;Wausou;WPS;YVAT;';
	        tabQues7.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues7);
	        
	        Tabular_Question__c tabQues9 = HUMTestDataHelperTest.getTabularQuestion('TQ-0008', 'Date', 'OI Effective Date', 9);
	        tabQues9.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues9);
	        
	        Tabular_Question__c tabQues10 = HUMTestDataHelperTest.getTabularQuestion('TQ-0009', 'Date', 'OI End Date', 10);
	        tabQues10.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues10);
	        
	        Tabular_Question__c tabQues11 = HUMTestDataHelperTest.getTabularQuestion('TQ-0010', 'Text', 'OI Primary', 11);
	        tabQues11.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues11);
	        
	        Tabular_Question__c tabQues12 = HUMTestDataHelperTest.getTabularQuestion('TQ-0011', 'Date', 'OI Medical Last Verified Date', 12);
	        tabQues12.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues12);
	        
	        Tabular_Question__c tabQues13 = HUMTestDataHelperTest.getTabularQuestion('TQ-0012', 'Text', 'OI Dental Last Verified Date', 13);
	        tabQues13.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues13);
			
			Tabular_Question__c tabQues20 = HUMTestDataHelperTest.getTabularQuestion('TQ-0013', 'Text', 'Member', 14);
	        tabQues20.Question__c = oQuesB.Id;
	        lstTabularQuestions.add(tabQues20);
	        
	        Tabular_Question__c tabQues14 = HUMTestDataHelperTest.getTabularQuestion('TQ-0014', 'Text', 'Relationship', 15);
	        tabQues14.Question__c = oQuesB.Id;
	        lstTabularQuestions.add(tabQues14);
	        
	        Tabular_Question__c tabQues15 = HUMTestDataHelperTest.getTabularQuestion('TQ-0015', 'Text', 'OI Carrier Name', 16);
	        tabQues15.Question__c = oQuesB.Id;
	        lstTabularQuestions.add(tabQues15);
	        
	        Tabular_Question__c tabQues16 = HUMTestDataHelperTest.getTabularQuestion('TQ-0016', 'Text', 'OI Effective Date', 17);
	        tabQues16.Question__c = oQuesB.Id;
	        lstTabularQuestions.add(tabQues16);
	        
	        Tabular_Question__c tabQues17 = HUMTestDataHelperTest.getTabularQuestion('TQ-0017', 'Text', 'OI End Date', 18);
	        tabQues17.Question__c = oQuesB.Id;
	        lstTabularQuestions.add(tabQues17);
	        
	        Tabular_Question__c tabQues18 = HUMTestDataHelperTest.getTabularQuestion('TQ-0018', 'Text', 'OI Primary', 19);
	        tabQues18.Question__c = oQuesB.Id;
	        lstTabularQuestions.add(tabQues18);
	        
	        Tabular_Question__c tabQues19 = HUMTestDataHelperTest.getTabularQuestion('TQ-0019', 'Text', 'OI Last Verified Date', 20);
	        tabQues19.Question__c = oQuesB.Id;
	        lstTabularQuestions.add(tabQues19);
	        	        
	        Tabular_Question__c tabQues21 = HUMTestDataHelperTest.getTabularQuestion('TQ-0021', 'Text', 'Member First Name', 21);
	        tabQues21.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues21);
	        
	        Tabular_Question__c tabQues22 = HUMTestDataHelperTest.getTabularQuestion('TQ-0022', 'Text', 'Member Last Name', 22);
	        tabQues22.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues22);
	        	        
	        Tabular_Question__c tabQues23 = HUMTestDataHelperTest.getTabularQuestion('TQ-0023', 'Text', 'Other Carrier Name', 23);
	        tabQues23.Question__c = oQuesA.Id;
	        lstTabularQuestions.add(tabQues23);
	        
	        insert lstTabularQuestions;
	        
	        Case oCase1 = HUMTestDataHelperTest.getCase();
	        oCase1.AccountId = oAcc.Id;
	        oCase1.Policy_Member__c = oPolMem.Id;
	        oCase1.Interacting_With__c = oAcc.Id;
	        oCase1.Interacting_With_Name__c = 'TestMemberFN TestMemberLN';
	        oCase1.status = 'Closed';
	        oCase1.OwnerID = oUser.ID;
	        insert oCase1;
	        System.assert(oCase1.Id != null);
	        
	        Case_Action_Association__c oCaseAssocication = new Case_Action_Association__c();
	        oCaseAssocication.Case__c = oCase1.Id;
	        oCaseAssocication.Case_Action__c = oCaseAction.Id;
	        insert oCaseAssocication;
	        
	        list<Tabular_Answer_Response__c> lstTabAns = new list <Tabular_Answer_Response__c>();
	        Tabular_Answer_Response__c tAnsResp1 = new Tabular_Answer_Response__c();
	        tAnsResp1.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp1.Row_Number__c = 1;
	        tAnsResp1.Column_Title__c = 'Member';
	        tAnsResp1.Grid_Question__c = tabQues1.Id;
	        tAnsResp1.Response__c = 'TestMemberFN TestMemberFN';
	        lstTabAns.add(tAnsResp1);
	        
	        Tabular_Answer_Response__c tAnsResp2 = new Tabular_Answer_Response__c();
	        tAnsResp2.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp2.Row_Number__c = 1;
	        tAnsResp2.Column_Title__c = 'Relationship';
	        tAnsResp2.Grid_Question__c = tabQues2.Id;
	        tAnsResp2.Response__c = 'Subscriber';
	        lstTabAns.add(tAnsResp2);
	        
	        Tabular_Answer_Response__c tAnsResp3 = new Tabular_Answer_Response__c();
	        tAnsResp3.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp3.Row_Number__c = 1;
	        tAnsResp3.Column_Title__c = 'Remove OI';
	        tAnsResp3.Grid_Question__c = tabQues3.Id;
	        tAnsResp3.Response__c = 'true';
	        lstTabAns.add(tAnsResp3);
	        
	        Tabular_Answer_Response__c tAnsResp4 = new Tabular_Answer_Response__c();
	        tAnsResp4.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp4.Row_Number__c = 1;
	        tAnsResp4.Column_Title__c = 'Add OI';
	        tAnsResp4.Grid_Question__c = tabQues4.Id;
	        tAnsResp4.Response__c = 'true';
	        lstTabAns.add(tAnsResp4);
	        
	        Tabular_Answer_Response__c tAnsResp5 = new Tabular_Answer_Response__c();
	        tAnsResp5.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp5.Row_Number__c = 1;
	        tAnsResp5.Column_Title__c = 'Verified';
	        tAnsResp5.Grid_Question__c = tabQues5.Id;
	        tAnsResp5.Response__c = 'true';
	        lstTabAns.add(tAnsResp5);
	        
	        Tabular_Answer_Response__c tAnsResp6 = new Tabular_Answer_Response__c();
	        tAnsResp6.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp6.Row_Number__c = 1;
	        tAnsResp6.Column_Title__c = 'OI Effective Date';
	        tAnsResp6.Grid_Question__c = tabQues9.Id;
	        tAnsResp6.Response__c = '07/01/2020';
	        lstTabAns.add(tAnsResp6);
	        
	        Tabular_Answer_Response__c tAnsResp7 = new Tabular_Answer_Response__c();
	        tAnsResp7.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp7.Row_Number__c = 1;
	        tAnsResp7.Column_Title__c = 'OI End Date';
	        tAnsResp7.Grid_Question__c = tabQues10.Id;
	        tAnsResp7.Response__c = '10/01/2020';
	        lstTabAns.add(tAnsResp7);
	        
	        Tabular_Answer_Response__c tAnsResp8 = new Tabular_Answer_Response__c();
	        tAnsResp8.Action_Response__c = oCaseAssocication.Id;
	        tAnsResp8.Row_Number__c = 1;
	        tAnsResp8.Column_Title__c = 'OI Primary';
	        tAnsResp8.Grid_Question__c = tabQues11.Id;
	        tAnsResp8.Response__c = 'Y';
	        lstTabAns.add(tAnsResp8);
	        
	        Tabular_Answer_Response__c tAnsRespA = new Tabular_Answer_Response__c();
	        tAnsRespA.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespA.Row_Number__c = 2;
	        tAnsRespA.Column_Title__c = 'Member';
	        tAnsRespA.Grid_Question__c = tabQues20.Id;
	        tAnsRespA.Response__c = 'TestMemberFN TestMemberFN';
	        lstTabAns.add(tAnsRespA);
	        
	        Tabular_Answer_Response__c tAnsRespB = new Tabular_Answer_Response__c();
	        tAnsRespB.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespB.Row_Number__c = 2;
	        tAnsRespB.Column_Title__c = 'Relationship';
	        tAnsRespB.Grid_Question__c = tabQues14.Id;
	        tAnsRespB.Response__c = 'Subscriber';
	        lstTabAns.add(tAnsRespB);
	        
	        Tabular_Answer_Response__c tAnsRespC = new Tabular_Answer_Response__c();
	        tAnsRespC.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespC.Row_Number__c = 2;
	        tAnsRespC.Column_Title__c = 'OI Carrier Name';
	        tAnsRespC.Grid_Question__c = tabQues15.Id;
	        tAnsRespC.Response__c = 'true';
	        lstTabAns.add(tAnsRespC);
	                      
	        Tabular_Answer_Response__c tAnsRespD = new Tabular_Answer_Response__c();
	        tAnsRespD.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespD.Row_Number__c = 2;
	        tAnsRespD.Column_Title__c = 'OI Effective Date';
	        tAnsRespD.Grid_Question__c = tabQues16.Id;
	        tAnsRespD.Response__c = '07/01/2020';
	        lstTabAns.add(tAnsRespD);
	        
	        Tabular_Answer_Response__c tAnsRespE = new Tabular_Answer_Response__c();
	        tAnsRespE.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespE.Row_Number__c = 2;
	        tAnsRespE.Column_Title__c = 'OI End Date';
	        tAnsRespE.Grid_Question__c = tabQues17.Id;
	        tAnsRespE.Response__c = '10/01/2020';
	        lstTabAns.add(tAnsRespE);
	        
	        Tabular_Answer_Response__c tAnsRespF = new Tabular_Answer_Response__c();
	        tAnsRespF.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespF.Row_Number__c = 2;
	        tAnsRespF.Column_Title__c = 'OI Primary';
	        tAnsRespF.Grid_Question__c = tabQues18.Id;
	        tAnsRespF.Response__c = 'Y';
	        lstTabAns.add(tAnsRespF);
			
			Tabular_Answer_Response__c tAnsRespG = new Tabular_Answer_Response__c();
	        tAnsRespG.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespG.Row_Number__c = 2;
	        tAnsRespG.Column_Title__c = 'OI Last Verified Date';
	        tAnsRespG.Grid_Question__c = tabQues19.Id;
	        tAnsRespG.Response__c = 'Y';
	        lstTabAns.add(tAnsRespG);
	        
	        
	        Tabular_Answer_Response__c tAnsRespH = new Tabular_Answer_Response__c();
	        tAnsRespH.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespH.Row_Number__c = 2;
	        tAnsRespH.Column_Title__c = 'Member First Name';
	        tAnsRespH.Grid_Question__c = tabQues21.Id;
	        tAnsRespH.Response__c = 'TestMemberFN';
	        lstTabAns.add(tAnsRespH);
	        
	        Tabular_Answer_Response__c tAnsRespI = new Tabular_Answer_Response__c();
	        tAnsRespI.Action_Response__c = oCaseAssocication.Id;
	        tAnsRespI.Row_Number__c = 2;
	        tAnsRespI.Column_Title__c = 'Member Last Name';
	        tAnsRespI.Grid_Question__c = tabQues22.Id;
	        tAnsRespI.Response__c = 'TestMemberLN';
	        lstTabAns.add(tAnsRespI);
	        
	        insert lstTabAns;
			
			list<Case_Action_Response__c> lstCaseActResp = new list<Case_Action_Response__c>();
			Case_Action_Response__c oCaseActResp1 = new Case_Action_Response__c();
			oCaseActResp1.Action__c = oCaseAssocication.Id;
			oCaseActResp1.Case_Question__c = oQues1.Id;
			oCaseActResp1.Process_Question__c = 'Subscriber covered under external policy?';
			oCaseActResp1.Process_Question_Response__c = 'Yes';
			lstCaseActResp.add(oCaseActResp1);        
	        
	        Case_Action_Response__c oCaseActResp2 = new Case_Action_Response__c();
			oCaseActResp2.Action__c = oCaseAssocication.Id;
			oCaseActResp2.Case_Question__c = oQues2.Id;
			oCaseActResp2.Process_Question__c = 'Other Insurance Policy subscriber name';
			oCaseActResp2.Process_Question_Response__c = 'TestMemberFN TestMemberFN';
			lstCaseActResp.add(oCaseActResp2);
			
			Case_Action_Response__c oCaseActResp3 = new Case_Action_Response__c();
			oCaseActResp3.Action__c = oCaseAssocication.Id;
			oCaseActResp3.Case_Question__c = oQues4.Id;
			oCaseActResp3.Process_Question__c = 'Relationship';
			oCaseActResp3.Process_Question_Response__c = 'Susbscriber';
			lstCaseActResp.add(oCaseActResp3);	
			
			insert lstCaseActResp;						
            
            HUM_Webservice_Callout__c MBEServiceDetails = new HUM_Webservice_Callout__c();
	        MBEServiceDetails.Name = 'MBEPlusService';
	        MBEServiceDetails.End_Point_URL__c = 'https://test.humana.com';
	        MBEServiceDetails.Certificate_Name__c = 'TestCertificate';
	        MBEServiceDetails.Request_Type__c = 'POST';
	        MBEServiceDetails.Timeout__c = '12000';
	        MBEServiceDetails.Content_Type__c = 'application/json';
	        insert MBEServiceDetails;
		}
    }
    
    /*
    * This method is used to test the detail view of the COB Template
    * @param        NA
    * @return       void
    */
    private static testMethod void testDetailView()
    {
    	User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test COBV1User' LIMIT 1];
        system.assertEquals(oUser.lastname=='test COBV1User',true);
        
        Case oCase = [SELECT Id from Case WHERE Status = 'Closed' limit 1]; 
         
        system.runas(oUser)
        {                        
            PageReference COBTemplateV1 = Page.HUMCOBProcess_V1;
            Test.setCurrentPage(COBTemplateV1);
            ApexPages.currentPage().getParameters().put('CaseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');  
			Apexpages.currentPage().getParameters().put('tabId', '123'); 
            Test.StartTest(); 
            HUMCOBProcessController_V1 objCOB = new HUMCOBProcessController_V1();
            system.assertEquals(objCOB.sLoadView, Label.HUMCOBView);
        }
    }
    
    /*
    * This method is used to test the new COB Template
    * @param        NA
    * @return       void
    */
    private static testMethod void testLoadGuide()
    {
    	User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test COBV1User' LIMIT 1];
        system.assertEquals(oUser.lastname=='test COBV1User',true);
        
        Case oCase = [SELECT Id from Case where status!= 'Closed' limit 1]; 
		
		list<HUMPersonServiceResponseHelper> lstCOBData = new list<HUMPersonServiceResponseHelper>();
		HUMPersonServiceResponseHelper oCOBData = new HUMPersonServiceResponseHelper();
		oCOBData.Member = 'TestFN TestLN';
	    oCOBData.Relationship = 'Subscriber';
	    oCOBData.OICarriername = 'Test Carrier';
	    oCOBData.OIEffectiveDate = '01/02/2020';
	    oCOBData.OIEndDate = '12/31/2020';
	    oCOBData.OIPrimary = 'Y';	    
	    oCOBData.OILastVerifieddate = '07/01/2020';
	    oCOBData.memberRelationship = '00';
	    oCOBData.memberFirstName = 'TestFN';
	    oCOBData.memberLastName = 'TestLN';
	    lstCOBData.add(oCOBData);
		
				                        
        system.runas(oUser)
        {
        	PageReference COBTemplateV1 = Page.HUMCOBProcess_V1;
            Test.setCurrentPage(COBTemplateV1);
            ApexPages.currentPage().getParameters().put('CaseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');    
			Apexpages.currentPage().getParameters().put('tabId', '123'); 
            Test.StartTest(); 
            HUMCOBProcessController_V1 objCOB = new HUMCOBProcessController_V1();
            
            /*Load Guide with Empty COB Details*/
            objCOB.loadGuide();
            
            /*Load Guide with COB Data*/
            objCOB.mapResponseWrapperMBE.put('1234567891', lstCOBData);
            objCOB.loadGuide();
            
            /*Call ADD OI Method for true*/
            objCOB.boolOIResponse = true;
            objCOB.sColumnName = Label.HUMCOBAddOI;
            objCOB.iRowNumAddOI = 1;
            objCOB.prepareAddOITable();
            
            /*Call ADD OI Method for false*/
            objCOB.boolOIResponse = false;
            objCOB.sColumnName = Label.HUMCOBAddOI;
            objCOB.iRowNumAddOI = 1;
            objCOB.prepareAddOITable();
            
            /*Call Remove OI Method for true*/
            objCOB.boolOIResponse = true;
            objCOB.sColumnName = Label.HUMCOBRemoveOI;
            objCOB.iRowNumAddOI = 1;
            objCOB.prepareAddOITable();
            
            /*Call Remove OI Method for false*/
            objCOB.boolOIResponse = false;
            objCOB.sColumnName = Label.HUMCOBRemoveOI;
            objCOB.iRowNumAddOI = 1;
            objCOB.prepareAddOITable();
                        
            objCOB.iRowNumRemoveOI = 1;
            objCOB.sColumnName = Label.HUMCOBRemoveOI;
            objCOB.displayOtherCarrier();
            objCOB.reCalculateRemoveOIDisplay();
            
            objCOB.iRowNumRemoveOI = 1;
            objCOB.sColumnName = Label.HUMCOBAddOI;
            objCOB.displayOtherCarrier();
            
            objCOB.sColumnName = Label.Archival_Cancellation_Date;
            objCOB.dDateValue = Date.today();
            objCOB.persistDateValue();
            
            objCOB.sColumnName = Label.Archival_New_Effective_Date;            
            objCOB.persistDateValue();
            
            HUMCaseTemplateHelper oWrapper = new HUMCaseTemplateHelper();
            Tabular_Answer_Response__c oResponseRec = new Tabular_Answer_Response__c();
            oResponseRec.Column_Title__c = Label.HUMCOBOIShouldNeverListed;
            oWrapper.responseRec = oResponseRec;
            objCOB.createWrapperCopy(oWrapper);					
                                    
            objCOB.saveAction();
            list<Case_Action_Association__c> lstSavedRecs = [Select Id, Case_Action__c, Action__c From Case_Action_Association__c WHERE Case__c = :oCase.Id];
            system.assertNotEquals(lstSavedRecs, null);
            system.assertNotEquals(lstSavedRecs.isEmpty(), true);
            Test.StopTest();            
        }	
    }          
    
    
    /*
    * This method is used to test the remaining methods from the controller
    * @param        NA
    * @return       void
    */
    private static testMethod void testPageMethods()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test COBV1User' LIMIT 1];
        system.assertEquals(oUser.lastname=='test COBV1User',true);
        
        Case oCase = [SELECT Id from Case where status!= 'Closed' limit 1]; 
                        
        system.runas(oUser)
        {            
            PageReference COBTemplateV1 = Page.HUMCOBProcess_V1;
            Test.setCurrentPage(COBTemplateV1);
            ApexPages.currentPage().getParameters().put('CaseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB'); 			
            Test.StartTest(); 
            HUMCOBProcessController_V1 objCOB = new HUMCOBProcessController_V1();
            
            objCOB.mapSequenceResponses = null;
            Integer iSize = objCOB.getMapSequenceResponsesSize();
            system.assertEquals(0,iSize);
            
            objCOB.rowDispTableMap = null;
            Integer iSizeA = objCOB.getRowDispTableMapSize();
            system.assertEquals(0,iSizeA);
            
            objCOB.mapSequenceResponses = new map<Integer, List<HUMCaseTemplateHelper>>(); 
            objCOB.mapSequenceResponses.put(1,new list<HUMCaseTemplateHelper>());
            iSize = objCOB.getMapSequenceResponsesSize();
            system.assertEquals(1,iSize);
            
            objCOB.rowDispTableMap = new map<Integer, List<Tabular_Answer_Response__c>>();
            objCOB.rowDispTableMap.put(1, new list< Tabular_Answer_Response__c >());
            iSizeA = objCOB.getRowDispTableMapSize();
            system.assertEquals(1,iSizeA);
            
            PageReference pgRef = objCOB.cancel();
            system.assertNotEquals(pgRef, null);                                   
                                                           
            Test.StopTest();
          }
    }
	
	private static testMethod void responseMethodMBEPlusTest()
	{
		User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test COBV1User' LIMIT 1];
        system.assertEquals(oUser.lastname=='test COBV1User',true);
        
        Case oCase = [SELECT Id from Case where status!= 'Closed' limit 1]; 
                        
		String sSampleResponse = '{ "GetMemberResponse":{ "Header":{ "ResponseHeader":{ "Environment":"Int", "Version":"2.0", "Server":{ "Name":"louigwii06s01" }, "Metrics":{ "Duration":"53", "RequestTimeStamp":"2020-08-17T11:10:38.674Z", "ResponseTimeStamp":"2020-08-17T11:10:38.727Z" }, ' +
								'"ResponseMetaData":{ "Status":"Warning", "ClientSessionID":"GUID", "SessionID":"7e734e30-654e-483e-8088-587d95ff0a34", "DataAuthorizationConstraintList":{ "DataAuthorizationConstraint":[ { "ConstraintType":"Exclude", "Tenant":"1", "Source":"5" }, ' +
								'{ "ConstraintType":"Include", "Tenant":"1" }, { "ConstraintType":"Include", "Tenant":"1", "Source":"16" } ] } } } },' +
								'"Body":{ "Member":{ "DateOfBirth":"", "FirstName":"TestMemberFN", "Gender":"M", "LastName":"TestMemberLN", "MiddleInitial":"", "Ssn":"", "Contact":{ "ContactProfile":{ "EmailList":{ "PersonEmail":[  ] }, "PhoneList":{ "PersonPhone":[  ] }, "AddressList":{ "PersonAddress":[  ] } } }, ' +
								'"Suffix":"", "MasterId":"1234567891", "SoldProductList":{ "SoldProduct":[ { "InsuranceProduct":{ "AlternateDescription":"", "Description":"", "MajorLineOfBusinessCode":"", "MajorLineOfBusinessDescription":"", "ProductStartDate":"01/01/2020", "ProductEndDate":"12/31/2020", ' +
								'"ProductId":"23456789", "Source":"LV", "ProductLineCode":"", "ProductLineDescription":"", "SegmentType":"", "SoldProductDetailList":{ "SoldProductDetail":[ { "InsuranceCoverage":{ "EndDate":"12/31/2020", "StartDate":"01/01/2020", "CoveredRoleList":{ "CoveredRole":[ { "StartDate":"01/01/2020", ' +
								'"EndDate":"12/31/2020", "MemberMasterId":"1234567891", "RelationshipToSubscriber":"EmployeeSelf", "CoveredRolePlatformPointerList":{ "PlatformProxyKey":[ { "Application":"CI", "KeyType":"MemberIDDependentCode", "KeyValue":"00", "Source":"LV" } ] }, "Hipaa":"", "CoverageKey":"" }]}, ' +
								'"PolicyType":"", "CoverageType":"", "GroupPlatformPointerList":{ "PlatformProxyKey":[ { "Application":"CI", "KeyType":"GroupId", "KeyValue":"54321", "Source":"LV" }]}, "ContractId":"", "OICoordinationOfBenefitList":{ "OICoordinationOfBenefit":[ { "CarrierName":"CARLLYN", "EffectiveDate":"06/01/2002", ' +
								'"EndDate":"12/31/9999", "Primary":"1", "LastUpdatedDate":"2019-11-04", "LastVerifiedDate":"2019-11-04", "StatusCode":"M", "StatusDescription":"OVER 65,MEDICARE PART A&B" } ] }, "OperationalMajorLineofBusiness":"", "PolicySequenceNumber":"" } } ] }, ' +
								'"IsCarryOverCredit":"", "IsCustomerCarveOut":"", "IsCustomPlan":"", "IsFedQualified":"", "IsHdhp":"", "IsPca":"", "IsPcpRequired":"", "IsRxMedicalIntegratedDeductible":"", "IsRxSpeciality":"" } }] } }, ' +
								'"MessageList":{ "Message":[ { "Type":"Warning", "Code":"MBEW0003", "Source":"OSE Services", "Description":"No coverage found for one of the platforms." }] } } } }';
		
        system.runas(oUser)
        {
 			PageReference COBTemplateV1 = Page.HUMCOBProcess_V1;
            Test.setCurrentPage(COBTemplateV1);
            ApexPages.currentPage().getParameters().put('CaseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');  
			Apexpages.currentPage().getParameters().put('tabId', '123'); 			
            HttpResponse response1 = new HttpResponse();
            response1.setbody(sSampleResponse);
			response1.setStatusCode(200);
            response1.setStatus('success');
            Test.StartTest(); 
            HUMCOBProcessController_V1 objCOB = new HUMCOBProcessController_V1();
            Continuation conti = (Continuation)objCOB.responseMethodMBEPlus();
            Test.setContinuationResponse('Continuation-1', response1);
            Test.setContinuationResponse('Continuation-2', response1);
            Test.setContinuationResponse('Continuation-3', response1);
            objCOB.mbePlusCallBack();
            Test.StopTest();       		
        }		
	}           
}