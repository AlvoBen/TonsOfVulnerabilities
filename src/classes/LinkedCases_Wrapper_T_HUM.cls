/**************************************************************************** 
Apex Class Name  : LinkedCases_Wrapper_T_HUM
Version          : 1.0  
Created Date     : 07/14/2016
Function         : Test class for LinkedCases_Wrapper_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Harika Devi Kalidindi    33954             07/14/2016                  REQ-269761 Original Version
* Harika Devi Kalidindi    37687             08/30/2016                  Defect 238584.0001 Fix  
* Anupama tavva            81949             04/04/2017					 REQ - 306357- update to the CTCI model
* Pradeepkumar Dani		  102268 			 06/16/2017					 Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Harika Devi Kalidindi   112000             07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Vinay Lingegowda                           12/14/2020                  Test class Failure.
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/
@isTest
private Class LinkedCases_Wrapper_T_HUM
{
   
    /*
     * Method Name : setup
     * Description : Create common test Data
     * Return type : void
     * Paramater   : NA
     */ 
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        System.runAs(oUser)
        {
            Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Cincinnati CRU');
            insert oQSetup ;
            system.assertNotEquals(Null , oQSetup.id);
            
            Account oAcc = new Account();
            oAcc.Group_Number__c='11111';
            oAcc.BillingState='New York';
            oAcc.Name = 'TestAccount1';            
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();            
            oAcc.Source_Platform_Code__c = 'CA';            
            insert oAcc;                        
            system.assertNotEquals(null , oAcc.Id, 'Value not matched'); 
           
            Policy__c oPolicy1 = new Policy__c();
            oPolicy1.Name ='test123';
            oPolicy1.Group_Name__c= oAcc.Id;
            oPolicy1.Policy_Group_Number__c='11111';
            oPolicy1.Funding_Type_Code__c = 'Y';
            oPolicy1.Issue_State__c = 'IL';  
            insert oPolicy1;            
            system.assertNotEquals(oPolicy1, null, 'Value not matched');
            System.assertEquals(oPolicy1.Funding_Type_Code__c, 'Y');
            
            Policy_Member__c oPolMem1 = new Policy_Member__c();
            oPolMem1.Relationship__c = 'Self';
            oPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            oPolMem1.End_Date__c=Date.today().addDays(1).format();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
            system.assertNotEquals(oPolMem1, null, 'Value not matched');
            System.assertEquals(oPolMem1.Relationship__c, 'Self');
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
	        
            List<Case> lstCases = new List<Case>();
            
            lstCases.Add(new Case(AccountId=oAcc.Id, CTCI_List__c = ctciRec.Id, Policy_Member__c= oPolMem1.Id ,
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
            lstCases.Add(new Case(AccountId=oAcc.Id, CTCI_List__c = ctciRec.Id, Policy_Member__c= oPolMem1.Id,
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
            lstCases.Add(new Case(AccountId=oAcc.Id, CTCI_List__c = ctciRec.Id, Policy_Member__c= oPolMem1.Id ,
                              Interacting_With_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Owner_Queue__c='Cincinnati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed'));
            insert lstCases;
            system.assertNotEquals(null, lstCases[0].Id);
               
            List<Case_Junction__c> lstCaseJun = new List<Case_Junction__c>();

            lstCaseJun.add(new Case_Junction__c(Case_Master__c = lstCases[0].Id, Case__c = lstCases[1].Id,IsLinked__c=true,Work_Queue__c = oQSetup.Id));
            lstCaseJun.add(new Case_Junction__c(Case_Master__c = lstCases[2].Id, Case__c = lstCases[0].Id,IsLinked__c=true,Work_Queue__c = oQSetup.Id));
            lstCaseJun.add(new Case_Junction__c(Case_Master__c = lstCases[1].Id, Case__c = lstCases[2].Id,IsLinked__c=true,Work_Queue__c = oQSetup.Id));
            insert lstCaseJun;   
            system.assertNotEquals(null, lstCaseJun[0].Id);
            system.assertEquals( lstCaseJun[0].Case__c,lstCases[1].Id);         
        }
    }
      
      
    /*
     * Method Name : runPositiveData
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runPositiveData()
    {    
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
             Test.StartTest();
             List<Case_Junction__c> lstCasejunction = [select Case__r.Id,Case_Master__r.Id,CreatedDate,IsLinked__c,IsCloneCase__c,Work_Queue__r.Name,CreatedBy.Name,Case__r.CaseNumber,Case__r.Created_By_Queue__c,Case__r.CreatedBy.Name,Case__r.CTCI_List__r.Intent__r.Name, Case__r.ClosedDate,Case__r.Interacting_About__c,Case__r.Status,Case__r.CreatedDate,Case__r.CTCI_List__r.Classification__c,Case_Master__r.CaseNumber from Case_Junction__c where IsLinked__c=true];

             LinkedCases_Wrapper_DTO_HUM oMedicare= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[0],'dCaseCreatedDate','DESC');
             oMedicare.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
             oMedicare.sSortType ='dCaseCreatedDate';
             oMedicare.Id = lstCasejunction[0].Id;
             oMedicare.sCreatedDate = string.valueof(lstCasejunction[0].CreatedDate);
             oMedicare.sClosedDate = '';
             oMedicare.compareTo(oMedicare);

             LinkedCases_Wrapper_DTO_HUM cDate= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],'dCaseCreatedDate','DESC');
             cDate.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
             cDate.sSortType ='dCaseCreatedDate';
             cDate.dCaseCreatedDate = DateTime.Now().AddDays(1);
             cDate.compareTo(oMedicare);
             
             List<String> sortType = new List<String>();
             sortType.add('sCaseCaseNumber');
             sortType.add('sCaseClassification');
             sortType.add('sCaseIntent');
             sortType.add('sCaseInteractingAbout');
             sortType.add('sCaseStatus');
             sortType.add('dCaseCreatedDate');
             sortType.add('dCaseClosedDate');
             sortType.add('sCaseCreatedByName');
             sortType.add('sCaseCreatedByQueue');
             sortType.add('dCreatedDate');
             sortType.add('sWorkQueueName');
            
             
             LinkedCases_Wrapper_DTO_HUM oMedicareDetail= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[0],'sCaseCaseNumber',GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
             oMedicareDetail.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM;
             oMedicareDetail.sSortType = 'sCaseCaseNumber';
            oMedicareDetail.compareTo(oMedicareDetail);
             for(String s:sortType)
             {
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail0= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[0],s,GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
                oMedicareDetail0.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM;
                oMedicareDetail0.sSortType = s;
                oMedicareDetail0.sWorkQueueName = 'Cincinnati';
                oMedicareDetail0.compareTo(oMedicareDetail0);
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail9= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],s,GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
                oMedicareDetail9.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM;
                oMedicareDetail9.sSortType = s;
                oMedicareDetail9.compareTo(oMedicareDetail0);
                oMedicareDetail0.compareTo(oMedicareDetail9);

             }
             
             LinkedCases_Wrapper_DTO_HUM oMedicareDetail2= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],'sCaseCaseNumber',GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
             //oMedicareDetail1.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
             //oMedicareDetail1.sSortType ='sCaseCaseNumber';
             oMedicareDetail2.compareTo(oMedicareDetail);
             for(String s:sortType)
             {
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail3= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[0],s,GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
                oMedicareDetail3.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
                oMedicareDetail3.sSortType = s;
                oMedicareDetail3.sWorkQueueName = 'Cincinnati';
                oMedicareDetail3.compareTo(oMedicareDetail3);
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail1= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],s,GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
                oMedicareDetail1.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
                oMedicareDetail1.sSortType = s;
                oMedicareDetail1.compareTo(oMedicareDetail3);
                oMedicareDetail3.compareTo(oMedicareDetail1);
             }
             Test.StopTest();
        }
    }
    static testMethod void runCompareTo()
    {    
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
             Test.StartTest();
             List<Case_Junction__c> lstCasejunction = [select Case__r.Id,Case_Master__r.Id,CreatedDate,IsLinked__c,IsCloneCase__c,Work_Queue__r.Name,CreatedBy.Name,Case__r.CaseNumber,Case__r.Created_By_Queue__c,Case__r.CreatedBy.Name,Case__r.CTCI_List__r.Intent__r.Name, Case__r.ClosedDate,Case__r.Interacting_About__c,Case__r.Status,Case__r.CreatedDate,Case__r.CTCI_List__r.Classification__c,Case_Master__r.CaseNumber from Case_Junction__c where IsLinked__c=true];
             //update lstCasejunction;
             List<String> sortType = new List<String>();
             sortType.add('sCaseCaseNumber');
             sortType.add('sCaseClassification');
             sortType.add('sCaseIntent');
             sortType.add('sCaseInteractingAbout');
             sortType.add('sCaseStatus');
             sortType.add('dCaseCreatedDate');
             sortType.add('dCaseClosedDate');
             sortType.add('sCaseCreatedByName');
             sortType.add('sCaseCreatedByQueue');
             sortType.add('dCreatedDate');
             sortType.add('sWorkQueueName');
            
             
            LinkedCases_Wrapper_DTO_HUM oMedicareDetail= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[0],'sCaseCaseNumber',GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            oMedicareDetail.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM;
            oMedicareDetail.sSortType = 'sCaseCaseNumber';
            oMedicareDetail.compareTo(oMedicareDetail);

            LinkedCases_Wrapper_DTO_HUM cDate= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],'dCaseCreatedDate',GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
             cDate.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM;
             cDate.sSortType ='dCaseCreatedDate';
             cDate.dCaseCreatedDate = DateTime.Now().AddDays(-4);
             cDate.compareTo(oMedicareDetail);

             for(String s:sortType)
             {
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail0= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[0],s,GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
                oMedicareDetail0.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM;
                oMedicareDetail0.sSortType = s;
                oMedicareDetail0.compareTo(oMedicareDetail0);
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail9= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],s,GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
                oMedicareDetail9.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM;
                oMedicareDetail9.sSortType = s;
                oMedicareDetail9.dCreatedDate =System.Today();
                oMedicareDetail9.compareTo(oMedicareDetail0);

             }
             
             LinkedCases_Wrapper_DTO_HUM oMedicareDetail2= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],'sCaseCaseNumber',GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
             //oMedicareDetail1.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
             //oMedicareDetail1.sSortType ='sCaseCaseNumber';
             oMedicareDetail2.compareTo(oMedicareDetail);
             for(String s:sortType)
             {
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail3= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[0],s,GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
                oMedicareDetail3.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
                oMedicareDetail3.sSortType = s;
                oMedicareDetail3.compareTo(oMedicareDetail3);
                LinkedCases_Wrapper_DTO_HUM oMedicareDetail1= new LinkedCases_Wrapper_DTO_HUM(lstCasejunction[1],s,GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
                oMedicareDetail1.sortOrder = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
                oMedicareDetail1.sSortType = s;
                oMedicareDetail1.dCreatedDate =System.Today();
                oMedicareDetail1.compareTo(oMedicareDetail3);
             }
             Test.StopTest();
        }
	}
}