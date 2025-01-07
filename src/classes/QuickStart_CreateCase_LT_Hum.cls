/*****************************************************************************************************************************************************************************
    @Apex Class Name  : QuickStart_CreateCase_LT_Hum
    @Version          : 1.0
    @Created Date     : 07/12/2022
    @Function         : Test Class for QuickStart_CreateCase_LC_Hum
******************************************************************************************************************************************************************************
Modification Log :  

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi                                 07/12/2022                    Original Version  
* Prasuna Pattabhi										09/02/2022					3723454 - CI Error Message Display
******************************************************************************************************************************************************************************/

@isTest
Public With sharing Class QuickStart_CreateCase_LT_Hum {    

    @testSetup static void prepareTestData() {
         
        Group oNewGroup = null;
        oNewGroup = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oNewGroup.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;   
        
        User QSUser = HUMTestDataHelperTest.createTestUser('QuickStartSpecialist','Customer Care Specialist');
        insert QSUser;
        system.assertEquals(QSUser.LastName,'test QuickStartSpecialist');

        QSUser.Current_Queue__c = 'Test Business Process and Testing';
        update QSUser;        

        System.runAs(QSUser) { 
            
            Work_Queue_Setup__c qsWorkQueue = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert qsWorkQueue;           

            Classification_Type__c callsClassificationType = New Classification_Type__c(Name = 'Calls',Is_Active__c = true);  
            insert callsClassificationType;

            qsWorkQueue.Classification_Type__c = callsClassificationType.id;
            update qsWorkQueue;          
            
            List<Classification__c> lstClassification  = New List<Classification__c>();
            Classification__c billing = New Classification__c(Name = 'Billing',Is_Active__c = true);
            lstClassification.add(billing);
            Classification__c enrollment = New Classification__c(Name = 'Enrollment',Is_Active__c = true);
            lstClassification.add(enrollment);
            Classification__c hpRefill = New Classification__c(Name = 'HP Refill Request',Is_Active__c = true); 
            lstClassification.add(hpRefill);
            insert lstClassification;

            Intent__c makePayment = New Intent__c(Name = 'Make Payment',Is_Active__c = true);                       
            insert makePayment;
            Intent__c refilled = New Intent__c(Name = 'Refilled',Is_Active__c = true);                     
            insert refilled;
            
            CTCI_Junction__c ctciValid = New CTCI_Junction__c(Classification_Type__c = callsClassificationType.id,
            Classification__c = billing.Id, Intent__c = makePayment.id, IsActive__c = True);
            insert ctciValid;
            CTCI_Junction__c ctciCIError = New CTCI_Junction__c(Classification_Type__c = callsClassificationType.id,
            Classification__c = hpRefill.Id, Intent__c = refilled.id, IsActive__c = True);
            insert ctciCIError;

            RecordType qsMemberAccountType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
                WHERE SobjectType = 'Account' AND DeveloperName = 'Member'];

            RecordType qsGroupAccountType = [SELECT Id, Name, DeveloperName, SobjectType
                FROM RecordType WHERE SobjectType = 'Account' AND DeveloperName = 'Group']; 

            List<Account> lstAccounts = New List<Account>();
            Account qsMemberAccountValid = New Account(
                FirstName = 'quickstart', LastName = 'validaccount',RecordTypeId = qsMemberAccountType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true, NPI_ID__c = '182245',
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = False
            );
            lstAccounts.add(qsMemberAccountValid);

            Account qsMemberAccountInvalid = New Account(
                FirstName = 'quickstart', LastName = 'etldeletedaccount',RecordTypeId = qsMemberAccountType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true,
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = True
            );
            lstAccounts.add(qsMemberAccountInvalid);

            Account oGroupAccount = New Account(Name = 'Nimble Enterprise',RecordTypeId = qsGroupAccountType.Id,
                CoKY_Account__c = False, DST_Account__c = False, General_Account__c = True, Group_Number__c = 'R7567001',
                Home_Office_Account__c = False, Humana_One_Indicator__c = True, Sic = '8062',
                Source_Application_Code__c = 'CI',Source_Customer_Key__c = '257567',
                Source_Platform_Code__c = 'LV',Tenant_Id__c = '00'
            );
            lstAccounts.add(oGroupAccount);

            insert lstAccounts; 

            Product2 productRec = New Product2(
                Name = 'MED',ProductCode = 'MED',Description = '302 006',
                Alternate_Description__c = 'UNKNOWN', HDHP_Indicator__c = 'N', 
                Major_LOB__c = 'EPO', Policy_Type__c = 'I',Segment_Type__c = 'COM', 
                Sold_Product_Key_Value__c = '9881770101', Tenant_ID__c = '00'
            );
            insert productRec;

            Policy__c policyRec = New Policy__c(
                Name = 'Business Group Legit Policy',Group_Name__c = oGroupAccount.Id,
                Product__c = productRec.id, ASO__c = 'N', 
                Benefit_Coverage__c = '9881770101', Client_Number__c = '58', 
                Exchange_Type__c = 'Federal', Exchange__c = 'FEDERAL', 
                Issue_State__c = 'TX', Major_LOB__c = 'EPO', 
                Metallic_Tier__c = 'Gold', Exchange_Indicator__c = true, 
                Selling_Market_Number__c = '13550',Source_Cust_Cov_Key__c = '257567001000', 
                Tenant_ID__c = '00', Fin_Legal_Entity__c = 'hic', 
                Platform__c = 'lv', Business_Segment__c = 'Individual-HumanaOne', 
                Coverage_Plan_Effective_Date__c = '01/01/2016', Coverage_Plan_End_Date__c = '12/31/2016', 
                Policy_Group_Number__c = 'R7567001'
            );
            insert policyRec;

            PurchaserPlan policyObj = New PurchaserPlan();
            policyObj.Policy_Group_Number__c='11111';
            policyObj.Name = 'Policy New';
            policyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            policyObj.PayerId = qsMemberAccountValid.Id;
            policyObj.Major_LOB__c ='EM';
            policyObj.Platform__c = 'EM';
            policyObj.ETL_Record_Deleted__c = false;
            insert policyObj;
            

            List<MemberPlan> lstMemPlan = New List<MemberPlan>();

            MemberPlan memPlan = New MemberPlan(Name ='memplanQs',MemberId=qsMemberAccountValid.id,
            Member_Dependent_Code__c = 'H1234567890',Member_Id_base__c = 'test23456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = False);
            lstMemPlan.add(memPlan);

            MemberPlan memPlanEtlDel = New MemberPlan(Name ='memplanQsEtlDel',MemberId=qsMemberAccountValid.id,
            Member_Dependent_Code__c = 'H1234567890',Member_Id_base__c = 'test23456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = True);
            lstMemPlan.add(memPlanEtlDel);

            MemberPlan memPlaninValidAcc = New MemberPlan(Name ='memPlaninValidAcc',MemberId=qsMemberAccountInvalid.id,
            Member_Dependent_Code__c = 'H1234567890',Member_Id_base__c = 'test23456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = False);
            lstMemPlan.add(memPlaninValidAcc);

            insert lstMemPlan;
            
            List<Interaction__c> lstInteraction = New List<Interaction__c>();
            Interaction__c qsInteraction = New Interaction__c(
                Interacting_With__c = qsMemberAccountValid.id,
                Interacting_With_Type__c = 'Member',
                Caller_Name__c = 'testQSCreateCase',
                Interaction_Origin__c = 'Inbound Call'
            );
            lstInteraction.add(qsInteraction);
            Interaction__c qsInteractionInvalid = New Interaction__c(
                Interacting_With__c = qsMemberAccountValid.id,
                Interacting_With_Type__c = 'Member',
                Caller_Name__c = 'testQSCreateCaseInvalid',
                Interaction_Origin__c = 'Inbound Call'
            );
            lstInteraction.add(qsInteractionInvalid);
            insert lstInteraction;            
            
            List<Interaction_Member__c> lstInteractionMem = New List<Interaction_Member__c>();
            Interaction_Member__c qsInteractionMem = New Interaction_Member__c(
                Interaction__c = qsInteraction.id,
                Interacting_About__c = qsMemberAccountValid.id,
                Interacting_About_Type__c = 'Member'
            );
            lstInteractionMem.add(qsInteractionMem);
            Interaction_Member__c qsInteractionMemInvalid = New Interaction_Member__c(
                Interaction__c = qsInteractionInvalid.id,
                Interacting_About__c = qsMemberAccountInvalid.id,
                Interacting_About_Type__c = 'Member'
            );
            lstInteractionMem.add(qsInteractionMemInvalid);
            insert lstInteractionMem;
        }       
    }
    
    Static String setCaseValues(String classificationId,String intentId,User qsUser,Work_Queue_Setup__c qsWorkQueue,String ctciId,String mVal){
        
        Case oNewCase = New Case();

		oNewCase.Owner_Queue__c = qsUser.Current_Queue__c;
        oNewCase.Case_Owner__c = qsUser.UserName;
        oNewCase.Classification_Type__c = qsWorkQueue.Classification_Type__r.Name;
        oNewCase.Service_Center__c = qsWorkQueue.Service_Center__c;
        oNewCase.Department__c = qsWorkQueue.Department__c;
        oNewCase.G_A_Reason__c = 'No';
        oNewCase.Complaint_Reason__c = '';
        oNewCase.Complaint_Type__c = '';
        oNewCase.G_A_Rights_Given__c = '';
        oNewCase.Complaint__c = 'No';
        oNewCase.Classification_Id__c = classificationId;
        oNewCase.Intent_Id__c = intentId;
        oNewCase.CTCI_List__c = ctciId;
        oNewCase.Interacting_With_Type__c = mVal;

        Return JSON.serialize(oNewCase);
    }
    @isTest
    Static void testAssociateCase(){

        User qsUser = [SELECT Id, Name, UserName, Current_Queue__c  FROM User WHERE LastName = 'test QuickStartSpecialist'];
        Work_Queue_Setup__c qsWorkQueue =[SELECT Id, Name, Classification_Type__c, Classification_Type__r.Name, Service_Center__c,  Department__c FROM Work_Queue_Setup__c WHERE Name = : qsUser.Current_Queue__c LIMIT 1];
        Classification_Type__c classificationType = [SELECT Id, Name, Is_Active__c FROM Classification_Type__c WHERE Name = 'Calls' AND Is_Active__c = true Limit 1]; 
        Classification__c classification = [SELECT Id, Name FROM Classification__c WHERE Name = 'Billing' Limit 1];
        Intent__c intent = [SELECT Id, Name FROM Intent__c WHERE Name = 'Make Payment' AND Is_Active__c = true LIMIT 1];
		Interaction__c interaction = [SELECT Id, Interacting_With__c FROM Interaction__c WHERE Caller_Name__c = 'testQSCreateCase'];
        Interaction__c invalidInteraction = [SELECT Id, Interacting_With__c FROM Interaction__c WHERE Caller_Name__c = 'testQSCreateCaseInvalid'];       
        Account validAccount = [SELECT Id, RecordTypeId, RecordType.Name, NPI_ID__c,ETL_Record_Deleted__c FROM Account WHERE FirstName = 'quickstart' AND LastName = 'validaccount'];
        Account invalidAccount = [SELECT Id FROM Account WHERE FirstName = 'quickstart' AND LastName = 'etldeletedaccount'];
        MemberPlan memberPlanValid = [SELECT id, Name, MemberId FROM MemberPlan WHERE Name = 'memplanQs' limit 1];
        MemberPlan memberPlaninValid = [SELECT id, Name, MemberId FROM MemberPlan WHERE Name = 'memplanQsEtlDel' limit 1];
        MemberPlan memberPlanAccountinValid = [SELECT id, Name, MemberId FROM MemberPlan WHERE Name = 'memPlaninValidAcc' limit 1];
        CTCI_Junction__c ctciValid = [SELECT Id, Classification__r.Name,Intent__r.Name FROM CTCI_Junction__c WHERE Classification_Type__c =: classificationType.id And IsActive__c = true And Classification__c =: classification.Id And Intent__c =: intent.id];
        Classification__c enrollment = [SELECT Id, Name FROM Classification__c WHERE Name = 'Enrollment' Limit 1];
        Classification__c classificationCIError = [SELECT Id, Name FROM Classification__c WHERE Name = 'HP Refill Request' Limit 1];
        Intent__c intentCIError = [SELECT Id, Name FROM Intent__c WHERE Name = 'Refilled' AND Is_Active__c = true LIMIT 1];
        CTCI_Junction__c ctciCIError = [SELECT Id, Classification__r.Name,Intent__r.Name FROM CTCI_Junction__c WHERE 
            Classification_Type__c =: classificationType.id And IsActive__c = true And Classification__c =: classificationCIError.Id 
            And Intent__c =: intentCIError.id];

        System.runAs(qsUser){   

		    String defaultData = '{"userId":"'+qsUser.Id+'","profileName":"Customer Care Specialist","bIsAccount":"true","bAllowMultipleCase":"false","interactionId":"'+interaction.id+'","classificationTypeId":"'+classificationType.id+'","bProviderUser":"false","classificationLabel":"","intentLabel":"'+ctciValid.Intent__r.Name+'",';
            String addlInfo;
            String caseData;
            
            /* Test User has no record access */
            caseData = setCaseValues(enrollment.Id,intent.Id,qsUser,qsWorkQueue,null,'');
            addlInfo = defaultData+'"recordId":""}';
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            /* Classification Type with  no classification */  
            addlInfo = defaultData+'"recordId":"'+validAccount.id+'"}';            
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            /* Associate to Account with valid data but run the exception scenarion as Record Id Not available */
            defaultData = '{"userId":"'+qsUser.Id+'","profileName":"Customer Care Specialist","bIsAccount":"true","bAllowMultipleCase":"false","interactionId":"'+interaction.id+'","classificationTypeId":"'+classificationType.id+'","bProviderUser":"false","classificationLabel":"'+ctciValid.Classification__r.Name+'","intentLabel":"'+ctciValid.Intent__r.Name+'",';
            caseData = setCaseValues(classification.Id,intent.Id,qsUser,qsWorkQueue,ctciValid.id,'Member'); 
            addlInfo = defaultData+'"recordId":"'+validAccount.id+'"}';            
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            HUMAccountCaseRecordTypes__c memberAccount = New HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
            insert memberAccount;

            /* Associate to Account with Proper data to create Case */
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);            
            
            /* A case with interaction exists for the  classification id */
            Case caseRecordInp = New Case(Status = 'In Progress',AccountId = validAccount.id,
            ctci_list__c = ctciValid.id, Complaint__c = 'Yes - Medicare Part C',Complaint_Reason__c = 'Other',
            Complaint_Type__c = 'Behavioral',Interaction_ID__c=interaction.Id);
            insert caseRecordInp;

            addlInfo = defaultData+'"recordId":"'+validAccount.id+'"}';           
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            /** Call the dupliate classification test */
            addlInfo = defaultData+'"recordId":"'+validAccount.id+'"}'; 

            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            /** Call the insertCase methos */
            QuickStart_CreateCase_LC_Hum.insertCase(caseData,'true');
            QuickStart_CreateCase_LC_Hum.insertCase('','true');

            /* Invalid interaction test */
            defaultData = '{"userId":"'+qsUser.Id+'","profileName":"Customer Care Specialist","bIsAccount":"true","bAllowMultipleCase":"false","interactionId":"'+invalidInteraction.Id+'","classificationTypeId":"'+classificationType.Id+'","bProviderUser":"false","classificationLabel":"'+ctciValid.Classification__r.Name+'","intentLabel":"'+ctciValid.Intent__r.Name+'",';
            addlInfo = defaultData+'"recordId":"'+validAccount.id+'"}';           
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);  
            
            /* Associate to ETL deleted Account */
            addlInfo = defaultData+'"recordId":"'+invalidAccount.id+'"}';           
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            /** Get Case Number  */
            QuickStart_CreateCase_LC_Hum.getCaseNumber(caseRecordInp.Id);          

            /** Policy details test */
            defaultData = '{"userId":"'+qsUser.Id+'","profileName":"Customer Care Specialist","bIsAccount":"false","bAllowMultipleCase":"false","interactionId":"'+interaction.id+'","classificationTypeId":"'+classificationType.id+'","bProviderUser":"false","classificationLabel":"'+ctciValid.Classification__r.Name+'","intentLabel":"'+ctciValid.Intent__r.Name+'",';
            /* Associate to Member Plan -  valid Member plan */
            addlInfo = defaultData+'"recordId":"'+memberPlanValid.id+'"}';           
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo); 

            /* Associate to Member Plan -  invalid Member plan */
            addlInfo = defaultData+'"recordId":"'+memberPlaninValid.id+'"}';           
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            /* Associate to Member Plan -  invalid Account */
            addlInfo = defaultData+'"recordId":"'+memberPlanAccountinValid.id+'"}';           
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo);

            /** CI Error - Execution */             
            defaultData = '{"userId":"'+qsUser.Id+'","profileName":"Customer Care Specialist","bIsAccount":"true","bAllowMultipleCase":"false","interactionId":"'+interaction.id+'","classificationTypeId":"'+classificationType.id+'","bProviderUser":"false","classificationLabel":"'+ctciCIError.Classification__r.Name+'","intentLabel":"'+ctciCIError.Intent__r.Name+'",';
            caseData = setCaseValues(classificationCIError.Id,intentCIError.Id,qsUser,qsWorkQueue,ctciCIError.id,'Member'); 
            addlInfo = defaultData+'"recordId":"'+validAccount.id+'"}';    
            QuickStart_CreateCase_LC_Hum.validateCreateCase(caseData,addlInfo); 
        }
    }
    
    @isTest
    Static void verifyPolicyByClassification(){
        MemberPlan memberPlanValid = [SELECT id, Name, MemberId FROM MemberPlan WHERE Name = 'memplanQs' limit 1];
        User qsUser = [SELECT Id, Name, UserName, Current_Queue__c  FROM User WHERE LastName = 'test QuickStartSpecialist'];
        
        System.runAs(qsUser){  
            
            String Id = memberPlanValid.Id;              
            QuickStart_CreateCase_LC_Hum.queryPolicyByClassification(Id,'Yes - Medicare Part C');
            QuickStart_CreateCase_LC_Hum.queryPolicyByClassification(Id,'Yes - Medicaid');
            QuickStart_CreateCase_LC_Hum.queryPolicyByClassification(Id,'YesMedicaid');
        }
    }
    @isTest
    Static void exceptionScenations(){
        User qsUser = [SELECT Id, Name, UserName, Current_Queue__c  FROM User WHERE LastName = 'test QuickStartSpecialist'];
        RecordType qsMemberAccountType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
                WHERE SobjectType = 'Account' AND DeveloperName = 'Member'];
        System.runAs(QSUser){           
            QuickStart_CreateCase_LC_Hum.queryActiveAccount(null);
            QuickStart_CreateCase_LC_Hum.queryActiveMemberPlan(null);
            QuickStart_CreateCase_LC_Hum.verifyCasesByActiveInteraction(New Case());
            QuickStart_CreateCase_LC_Hum.getCaseRecordType(qsMemberAccountType.Name,true,'Customer Care Specialist',qsUser.Current_Queue__c);
            QuickStart_CreateCase_LC_Hum.getCaseRecordType(qsMemberAccountType.Name,false,'Humana Pharmacy Specialist',qsUser.Current_Queue__c);
        }
    }
}