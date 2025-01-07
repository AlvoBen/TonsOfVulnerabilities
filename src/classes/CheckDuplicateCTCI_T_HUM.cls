/*******************************************************************************************************************************
Apex Class Name : CheckDuplicateCTCI_T_HUM
Version         : 1.0
Created On      : 03/23/2017
Function        : Class contains test methods CheckDuplicateCTCI_H_HUMclass.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty                                   03/23/2017                Original Version
* Archana Das               237454                      05/31/2018               Added new test method
* Pooja Kumbhar                                         08/28/2020               Changes done to increase code coverage 
*  Ashok Nutalapati                              01/18/2021                    REQ - 1598487 - Add 'Service Model Type' Picklist to CTCI List Pages in Business Configuration App (CRMS)
* Samantha Tennyson                                     12/28/2020               User Story 1745934: Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
* Vamshi Krishna Pemberthi                              07/08/2021      Improved Code Coverage
*******************************************************************************************************************************/
@isTest
private Class CheckDuplicateCTCI_T_HUM
{

    /**
    *  This method creates test data for test class
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  @see 
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('CTCIUSER1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test CTCIUSER1');
        HUMTriggerSwitch__c objTrigswitch = new HUMTriggerSwitch__c ();
        objTrigswitch.name ='CTCI_Junction__c';
        objTrigswitch.Exeute_Trigger__c =true;
        insert objTrigswitch;
        system.assertEquals(objTrigswitch.name,'CTCI_Junction__c');
        
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'Demographics';
        classificationRec.Is_Active__c = true;
        insert classificationRec;
        System.assertEquals(classificationRec.Name,'Demographics');
        
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Change';
        intentRec.Is_Active__c = true;
        insert intentRec;
        System.assertEquals(intentRec.Name,'Change');
        
        Classification_Type__c objcT = new Classification_Type__c();
        objcT.Name ='PCU';
        objcT.Is_Active__c = true;
        insert objcT;
        System.assertEquals(objcT.Name,'PCU');
        Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c();
        oWQS.Service_Center__c ='Cincinnati';
        oWQS.Department__c ='CRU1';
        oWQS.CokY__c =true;
        oWQS.HO_Medical__c =true;
        oWQS.TenantID__c ='00'; 
        oWQs.Public_Group_Name__c='Cincinnati_CRU1_PGQ';   
        oWQs.isActive__c = TRUE;                  
        oWQS.Classification_Type__c = objcT.id;         
        insert oWQS;
        System.assertEquals(oWQS.TenantID__c ,'00');
            
        Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
        insert testProduct;

        CTCI_Junction__c objCTCI = new CTCI_Junction__c();
        objCTCI.Classification__c=classificationRec.id;
        objCTCI.Classification_Type__c= objcT.id;
        objCTCI.Intent__c =intentRec.id;
        objCTCI.Never_a_Complaint__c= false; //CI Combo
        objCTCI.Never_a_G_A_Rights_Given__c= false; //CI Combo
        objCTCI.IsActive__c=true;
        insert objCTCI ;
        system.assertNotEquals(Null,objCTCI.id);
        
        Quick_Start_Pretext__c qsp = new Quick_Start_Pretext__c();
        qsp.Complaint_GA_Setup__c = 'Complaint_true';
        qsp.CTCI_List_lkp__c = objCTCI.id;
        qsp.Is_Active__c = true;
        insert qsp;
        
        Quick_Start_Pretext__c qsp2 = new Quick_Start_Pretext__c();
        qsp2.Complaint_GA_Setup__c = 'GnA_true';
        qsp2.CTCI_List_lkp__c = objCTCI.id;
        insert qsp2;
        
        Case_Transfer_Assist__c objcta = new Case_Transfer_Assist__c();
        objcta.CTCI_List__c=objCTCI.id;
        objcta.Work_Queue_Setup__c=oWQS.id;
        objcta.Product__c=testProduct.id;
        insert objcta;
        system.assertNotEquals(null, objcta);
            
    }
    
    /**
    *  This method tests the insert logic on CTCI object 
    *  @name testInsertOnCTCI
    *  @param NA
    *  @return NA
    *  @throws exception - check all associated  CTCI if any data is duplicate throw error
    *  @see 
    */
    static testMethod void testInsertOnCTCI()
    {            
     User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];                
        system.runAs(oUser)
        { 
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
            objcT.Name ='CallRSO';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'CallRSO');
            
            Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c();
            oWQS.Service_Center__c ='Cincinnati';
            oWQS.Department__c ='CRU1';
            oWQS.CokY__c =true;
            oWQS.HO_Medical__c =true;
            oWQS.TenantID__c ='00'; 
            oWQs.Public_Group_Name__c='Cincinnati_CRU1_PGQ';   
            oWQs.isActive__c = TRUE;                  
            oWQS.Classification_Type__c = objcT.id;         
            insert oWQS;
            System.assertEquals(oWQS.TenantID__c ,'00');
                
            Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
            insert testProduct;
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            system.assertNotEquals(Null,objCTCI.id);
            
            CTCI_Junction__c objCTCInew = new CTCI_Junction__c();
            objCTCInew.Classification__c=classificationRec.id;
            objCTCInew.Classification_Type__c= objcT.id;
            objCTCInew.Intent__c =intentRec.id;
            objCTCInew.IsActive__c=true;
            try 
            {
                insert objCTCInew ;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORDCTCI)); 
            }
            
            Quick_Start_Pretext__c qsp = new Quick_Start_Pretext__c();
            qsp.Complaint_GA_Setup__c = 'Complaint_true';
            qsp.CTCI_List_lkp__c = objCTCI.id;
            qsp.Is_Active__c = true;
            insert qsp;
            
            Quick_Start_Pretext__c qsp2 = new Quick_Start_Pretext__c();
            qsp2.Complaint_GA_Setup__c = 'GnA_true';
            qsp2.CTCI_List_lkp__c = objCTCInew.id;
            insert qsp2;
            
            Case_Transfer_Assist__c objcta = new Case_Transfer_Assist__c();
        objcta.CTCI_List__c=objCTCI.id;
        objcta.Work_Queue_Setup__c=oWQS.id;
        objcta.Product__c=testProduct.id;
        insert objcta;
        system.assertNotEquals(null, objcta);
            
       objCTCI.IsActive__c = false;
        update objCTCI;
        }
     }
     
      /**
    *  This method tests the insert logic on CTCI object 
    *  @name testUpdateOnAssociatedCTCI
    *  @param NA
    *  @return NA
    *  @throws exception - check all associated  CTCI if any data is duplicate throw error
    *  @see 
    */
    static testMethod void testUpdateOnAssociatedCTCI()
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];                
        system.runAs(oUser)
        {      
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'ClaimsRSO12';
            classificationRec1.Is_Active__c = true;
            insert classificationRec1;
            System.assertEquals(classificationRec1.Name,'ClaimsRSO12');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'FillingRSO';
            intentRec1.Is_Active__c = true;
            insert intentRec1;
            System.assertEquals(intentRec1.Name,'FillingRSO');
            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRS';
            objcT.Is_Active__c = true;
            insert objcT ;
            System.assertEquals(objCT.Name,'CallRS');
            
            Classification_Type__c objcT1 = new Classification_Type__c();
            objcT1.Name ='CallRSOtype';
            objcT1.Is_Active__c = true;
            insert objcT1 ;
            System.assertEquals(objCT1.Name,'CallRSOtype');
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ; 
            system.assertNotEquals(Null,objCTCI.id);
            update objCTCI ;
            
            
            CTCI_Junction__c objCTCI1 = new CTCI_Junction__c();
            objCTCI1.Classification__c=classificationRec1.id;
            objCTCI1.Classification_Type__c= objcT1.id;
            objCTCI1.Intent__c =intentRec1.id;
            objCTCI1.IsActive__c=true;
            insert objCTCI1 ;
            system.assertNotEquals(Null,objCTCI1.id);
            
            objCTCI1.Classification_Type__c= objcT.id;
            objCTCI1.IsActive__c=true;
            objCTCI1.Intent__c = intentRec.id;
            objCTCI1.Classification__c=classificationRec.id;
            try 
            {
                update objCTCI1;
            }
            catch(DMLException e)
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORDCTCI)); 
            }            
             Quick_Start_Pretext__c qsp = new Quick_Start_Pretext__c();
            qsp.Complaint_GA_Setup__c = 'Complaint_true';
            qsp.CTCI_List_lkp__c = objCTCI.id;
            qsp.Is_Active__c = true;
            insert qsp;
            
            Quick_Start_Pretext__c qsp2 = new Quick_Start_Pretext__c();
            qsp2.Complaint_GA_Setup__c = 'GnA_true';
            qsp2.CTCI_List_lkp__c = objCTCI.id;
            insert qsp2;
        }
    }
    
      /**
    *  This method tests the insert logic on CTCI object 
    *  @name testUpdatepostivescenario
    *  @param NA
    *  @return NA
    *  @throws exception - check all associated  CTCI if any data is duplicate throw error
    *  @see 
    */
    static testMethod void testUpdatepostivescenario()
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];                
        system.runAs(oUser)
        {      
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'ClaimsRSO12';
            classificationRec1.Is_Active__c = true;
            insert classificationRec1;
            System.assertEquals(classificationRec1.Name,'ClaimsRSO12');
            
            Classification__c classificationRec2 = New Classification__c();
            classificationRec2.Name = 'testcsp';
            classificationRec2.Is_Active__c = true;
            insert classificationRec2;
            System.assertEquals(classificationRec2.Name,'testcsp');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'FillingRSO';
            intentRec1.Is_Active__c = true;
            insert intentRec1;
            System.assertEquals(intentRec1.Name,'FillingRSO');
            
            Intent__c intentRec2 = New Intent__c();
            intentRec2.Name = 'UpdateCSP';
            intentRec2.Is_Active__c = true;
            insert intentRec2;
            System.assertEquals(intentRec2.Name,'UpdateCSP');
            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRS';
            objcT.Is_Active__c = true;
            insert objcT ;
            System.assertEquals(objCT.Name,'CallRS');
            
            Classification_Type__c objcT1 = new Classification_Type__c();
            objcT1.Name ='CallRSOtype';
            objcT1.Is_Active__c = true;
            insert objcT1 ;
            System.assertEquals(objCT1.Name,'CallRSOtype');
            
            Classification_Type__c objcT2 = new Classification_Type__c();
            objcT2.Name ='CallUpdate';
            objcT2.Is_Active__c = true;
            insert objcT2;
            System.assertEquals(objCT2.Name,'CallUpdate');
                   
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ; 
            system.assertNotEquals(Null,objCTCI.id);
            objCTCI.Intent__c =intentRec2.id;
            update objCTCI ;
            
            CTCI_Junction__c objCTCI1 = new CTCI_Junction__c();
            objCTCI1.Classification__c=classificationRec1.id;
            objCTCI1.Classification_Type__c= objcT1.id;
            objCTCI1.Intent__c =intentRec1.id;
            objCTCI1.IsActive__c=true;
            insert objCTCI1 ;
            system.assertNotEquals(Null,objCTCI1.id);
            
            objCTCI1.Classification_Type__c= objcT2.id;
            update objCTCI1;
            
             
            Quick_Start_Pretext__c qsp = new Quick_Start_Pretext__c();
            qsp.Complaint_GA_Setup__c = 'Complaint_true';
            qsp.CTCI_List_lkp__c = objCTCI.id;
            qsp.Is_Active__c = true;
            insert qsp;
            
            Quick_Start_Pretext__c qsp2 = new Quick_Start_Pretext__c();
            qsp2.Complaint_GA_Setup__c = 'GnA_true';
            qsp2.CTCI_List_lkp__c = objCTCI.id;
            insert qsp2;

    }
    }
    
    /**
    *  This method tests the insert logic on CTCI object 
    *  @name testCTCICominationforMultiRec
    *  @param NA
    *  @return NA
    *  @throws exception - check all combination of CTCI if any data is duplicate throw error
    *  @see 
    */
    static testMethod void testCTCICominationforMultiRec()
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];                
        system.runAs(oUser)
        {      
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Critical Test';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Critical Test');
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'Critical Error Test';
            classificationRec1.Is_Active__c = true;
            insert classificationRec1;
            System.assertEquals(classificationRec1.Name,'Critical Error Test');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Denied Test';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Denied Test');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'No Action Test';
            intentRec1.Is_Active__c = true;
            insert intentRec1;
            System.assertEquals(intentRec1.Name,'No Action Test');
            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRSOTest';
            objcT.Is_Active__c = true;
            insert objcT ;
            System.assertEquals(objCT.Name,'CallRSOTest');
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ; 
            system.assertNotEquals(Null,objCTCI.id);
            
            CTCI_Junction__c objCTCI1 = new CTCI_Junction__c();
            objCTCI1.Classification__c=classificationRec1.id;
            objCTCI1.Classification_Type__c= objcT.id;
            objCTCI1.Intent__c =intentRec1.id;
            objCTCI1.IsActive__c=true;
            insert objCTCI1 ;
            system.assertNotEquals(Null,objCTCI1.id);
            
             
            Quick_Start_Pretext__c qsp = new Quick_Start_Pretext__c();
            qsp.Complaint_GA_Setup__c = 'Complaint_true';
            qsp.CTCI_List_lkp__c = objCTCI.id;
            qsp.Is_Active__c = true;
            insert qsp;
            
            Quick_Start_Pretext__c qsp2 = new Quick_Start_Pretext__c();
            qsp2.Complaint_GA_Setup__c = 'GnA_true';
            qsp2.CTCI_List_lkp__c = objCTCI.id;
            insert qsp2;

            
            
            //Classification 'Critical Test' and Intent 'Denied Test' was already added. 
            //It should allow adding list of Classification 'Critical Test' & Intent 'No Action Test' and Classification 'Critical Error Test' & Intent 'Denied Test'
            List<CTCI_Junction__c> lstCTCI = new List<CTCI_Junction__c>();
            objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec1.id;
            objCTCI.IsActive__c=true;
            lstCTCI.add(objCTCI);
            
            objCTCI1 = new CTCI_Junction__c();
            objCTCI1.Classification__c=classificationRec1.id;
            objCTCI1.Classification_Type__c= objcT.id;
            objCTCI1.Intent__c =intentRec.id;
            objCTCI1.IsActive__c=true;
            lstCTCI.add(objCTCI1);
            
            try 
            {
                insert lstCTCI;
            }
            catch(DMLException e)
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORDCTCI)); 
            }            
        }
    }
    
    /**
    *  This method tests the Update logic on CTCI object 
    *  @name UpdateCTCI
    *  @param NA
    *  @return NA
    *  @throws exception - check all combination of CTCI if any data is duplicate throw error
    *  @see 
    */
    static testMethod void UpdateCTCI()
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];                
        system.runAs(oUser)
        {   
            test.startTest();
            CTCI_Junction__C ctci = new CTCI_Junction__c();
            ctci = [select Classification__c,Intent__c, Classification_Type__c from CTCI_Junction__C limit 1];
                
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'DemographicsTest';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'DemographicsTest');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Change Mode';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Change Mode');
            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='Web Chat';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'Web Chat');
            
            ctci.Classification__c = classificationRec.Id;
            ctci.Intent__c = intentRec.id;
            ctci.Classification_Type__c = objcT.id ;
            
            update ctci;
            
            test.stopTest();
        }        
    }
    
    /**
    *  This method tests the Update logic on CTCI object 
    *  @name HpUpdateCTCI
    *  @param NA
    *  @return NA
    *  @throws exception - check all combination of CTCI if any data is duplicate throw error
    *  @see 
    */
    static testMethod void HpUpdateCTCI()
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];                
        system.runAs(oUser)
        {   
            test.startTest();
            CTCI_Junction__C ctci = new CTCI_Junction__c();
            ctci = [select Classification__c,Intent__c, Classification_Type__c, IsActive__c from CTCI_Junction__C limit 1];
            
            update ctci;
            
            test.stopTest();
        }        
    }
    
    static testMethod void HpInsertCTCI()
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];                
        system.runAs(oUser)
        {   
            test.startTest();
            
            CTCI_Junction__C ctci = new CTCI_Junction__c();
            ctci = [select Classification__c,Intent__c, Classification_Type__c, IsActive__c from CTCI_Junction__C limit 1];
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c= ctci.Classification__c;
            objCTCI.Classification_Type__c= ctci.Classification_Type__c;
            objCTCI.Intent__c = ctci.Intent__c;
            objCTCI.IsActive__c=false;
            
            try 
            {
                insert objCTCI;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains('Insert failed.')); 
            }   
            
            test.stopTest();
        }        
    }

    static testMethod void updateQuickStartPretext() 
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];    
        
        Classification__c classificationRec = [SELECT Name, Is_Active__c  from Classification__c where Name = 'Demographics'];
        Intent__c intentRec = [SELECT Name, Is_Active__c from Intent__c where Name = 'Change'];
        Classification_Type__c objcT = [SELECT Name, Is_Active__c from Classification_Type__c where Name = 'PCU'];
        
        CTCI_Junction__c objCTCI = [SELECT Never_a_Complaint__c,Never_a_G_A_Rights_Given__c,IsActive__c from CTCI_Junction__c where IsActive__c = true AND Never_a_Complaint__c = false and Never_a_G_A_Rights_Given__c = false];
           
        system.runAs(oUser)
        {   
            test.startTest();
            
            objCTCI.Never_a_Complaint__c= true;
            objCTCI.Never_a_G_A_Rights_Given__c= false; 
            update objCTCI ;
            
            Quick_Start_Pretext__c qsp = [SELECT Complaint_GA_Setup__c,CTCI_List_lkp__c,Is_Active__c from Quick_Start_Pretext__c where CTCI_List_lkp__c =:objCTCI.id and Complaint_GA_Setup__c = 'GnA_true'];

            test.stopTest();
        }        
    }
    
    static testMethod void updateException() 
    {            
        User oUser = [select Id,Name from User where name ='test CTCIUSER1' limit 1];    
        
        Classification__c classificationRec = [SELECT Name, Is_Active__c  from Classification__c where Name = 'Demographics'];
        Intent__c intentRec = [SELECT Name, Is_Active__c from Intent__c where Name = 'Change'];
        Classification_Type__c objcT = [SELECT Name, Is_Active__c from Classification_Type__c where Name = 'PCU'];
        
        CTCI_Junction__c objCTCI = [SELECT Never_a_Complaint__c,Never_a_G_A_Rights_Given__c,IsActive__c from CTCI_Junction__c where IsActive__c = true AND Never_a_Complaint__c = false and Never_a_G_A_Rights_Given__c = false];
           
        system.runAs(oUser)
        {   
            test.startTest();
            
            objCTCI.IsActive__c = false; 
            //objCTCI.Classification_Type__c = null;
            objCTCI.Intent__c = null;
            //objCTCI.Classification__c = null;
            update objCTCI ;
            
            Quick_Start_Pretext__c qsp = [SELECT Complaint_GA_Setup__c,CTCI_List_lkp__c,Is_Active__c from Quick_Start_Pretext__c where CTCI_List_lkp__c =:objCTCI.id and Complaint_GA_Setup__c = 'GnA_true'];

            test.stopTest();
        }        
    }
}