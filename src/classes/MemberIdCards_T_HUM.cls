/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_T_HUM.cls
Version         : 1.0
Created On      : Dec 20,2016
Function        : Class contains test methods MemberIdCards_C_HUM class.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Joel George                 207348                03/12/2018                  Original Version (REQ 357335)
 * Pradeep Dani                234619                05/28/2018                  Date format update - defect#360377.0001
 * Sunil Bukya                 268461                08/20/2018                  REQ - 367786 - Implemented Member ID fields platform encryption changes.
 * Santhi Mandava              355559                05/10/2019                  Removed "ENCRYPTIONTYPE" custom setting creation
 * Suresh Kokkeragadda                               08/13/2020                  Test Class code coverage for 8/28 Branch
 * Suresh Kokkeragadda                               07/29/2020                  User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
 * Suresh Kokkeragadda                               08/26/2020                  Test Class code coverage 
 * Atia Uzma                                         09/30/2020                  User Story 1563575: PR00091574 - MF 1 - Quality - Display updated information messages on the ID Card Management page to reflect accurate guidance as per the revised business process (CRM)
 * Rajassekhar Chintapalli                           10/08/2020                  User Story 156375: BuildFix
 * Atia Uzma                                         02/25/2021               User Story 1956960: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
 * Satyanarayana chenna                              04/09/2021                   US 1918663 PR00094226 - CR AEP readiness - Low Security Issues     
 * Pallavi Shewale                                   04/30/2021                  User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member
*  Atia Uzma                                         08/05/2021                  User Story 2551785: T1PRJ0001827 - MF 1 - SF - TECH - On/Off switch cleanup for 8/20/21 Release (CRM)
 * Prashant Moghe 								     08/20/2021					 US- 1928764 - T1PRJ0002736 - 2021 MF6 - Security  - CR AEP readiness - Information Security Issues
 * Prasanthi Kandula                                 09/01/2021                  Increased code coverage 
 * Samantha Tennyson                                 10/28/2021                  User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request is successfully submitted (RF)
 ********************************************************************************************************************************/
@isTest
private class MemberIdCards_T_HUM 
{
    Public Static final String MEMBERIDCARD_HSA = 'HSA';
    
    /**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        
        HUM_Webservice_Callout__c RighSourceServiceDetails = new HUM_Webservice_Callout__c(Name ='HIDCMS_MemberSearch',Certificate_Name__c = 'Humana_GuidanceCare_Alderaan' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IDCardService/MemberSearch' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceServiceDetails;
        HUM_Webservice_Callout__c OrderIdCardRequest = new HUM_Webservice_Callout__c(Name ='HIDCMS_OrderIdCard',Certificate_Name__c = 'Humana_GuidanceCare_Alderaan' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IDCardService/OrderIdCard' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert OrderIdCardRequest;
        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Home_Office_Account__c = true;
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';    
            oMedicalPolicy.Coverage_Plan_Effective_Date__c  = '02/02/2018';     
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            //Pol Member for getActivePolicy
            Policy_Member__c objMedPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem2.Member__c = objMemAccount.Id;
            objMedPolMem2.Policy__c = oMedicalPolicy.Id;
            objMedPolMem2.Relationship__c = 'Self';    
            //objMedPolMem2.end_date__c = '12/31/2025'; 
            objMedPolMem2.end_date__c =  Date.today().addDays(20).format();
            //objMedPolMem2.Effective_Date__c='12/31/2024'; 
            objMedPolMem2.Effective_Date__c = Date.today().addDays(10).format();      
            objMedPolMem2.Policy_Member_External_ID__c = '01|ODS|123456789|89|89|02-01-2002';
            //objMedPolMem2.Subscriber__c = objMedPolMem1.Id;
            objMedPolMem2.Source_Coverage_ID__c = 'TestFutureDated';
            insert objMedPolMem2;
            
            Interaction__c oInteraction = New Interaction__c(
                Interacting_With__c = objMemAccount.id,
                Interacting_With_Type__c = 'Member',
                Caller_Name__c = 'testMcCree',
                Interaction_Origin__c = 'Inbound Call'
            );
            insert oInteraction;
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Name = 'Calls';
            oClassType.Is_Active__c = true;
            insert oClassType;
            
            Classification__c oClass = new Classification__c();
            oClass.Name = 'Fulfillment';
            oClass.Is_Active__c = true;
            insert oClass;
            
            Intent__c oIntent = new Intent__c();
            oIntent.Name = 'ID Card/Access Card Request';
            oIntent.Is_Active__c = true;
            insert oIntent;
            
            CTCI_Junction__c oCTCI = new CTCI_Junction__c();
            oCTCI.Classification_Type__c = oClassType.id;
            oCTCI.Classification__c = oClass.Id;
            oCTCI.Intent__c = oIntent.id;
            oCTCI.IsActive__c = true;
            insert oCTCI;
            
            Interaction_Member__c oIntMem = new Interaction_Member__c(
	            Interaction__c =oInteraction.id,
	            Interacting_About__c = objMemAccount .id,
	            Interacting_About_Type__c = 'Member'
            );
            insert oIntMem;
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = objMemAccount.id;
            caseRecord.ownerId = objUser.Id;
            caseRecord.Interacting_With__c = objMemAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = objMedPolMem.Id;
            caseRecord.CTCI_List__c = oCTCI.Id;
            insert caseRecord;
            
            Product2 prod1 = New Product2();
            prod1.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MA;
            prod1.Name = 'Prod1';
            prod1.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCD;
            insert prod1;
            
            HUMCaseInteractionAboutType__c InteractingAboutType = new HUMCaseInteractionAboutType__c();
            InteractingAboutType.Name = 'Member';
            InteractingAboutType.CaseRecordType__c = 'Member';
            InteractingAboutType.CaseInteraction_About_Type__c = 'Member';
            insert InteractingAboutType;
            }
    }
    
    /**
    * Method to create test data fpr HSANonHumana Check
    *<p>
    * @param            NULL
    * @return           void
    */
    private static void setUpTestDataForHSANonHumana()
    {
        User objUser = [Select Id,Name from User where LastName='test User1'];
        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Home_Office_Account__c = true;
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c = MEMBERIDCARD_HSA;
            oMedicalPolicy.Coverage_Plan_Effective_Date__c  = '02/02/2018';     
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            
            Policy_Member__c objMedPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem1.Member__c = objMemAccount.Id;
            objMedPolMem1.Policy__c = oMedicalPolicy.Id;
            objMedPolMem1.Relationship__c = 'self';    
            objMedPolMem1.end_date__c = '12/31/2015'; 
            objMedPolMem1.Effective_Date__c='01/01/2015';       
            objMedPolMem1.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2002';
            insert objMedPolMem1;
            
            
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'CHILD';    
            objMedPolMem.end_date__c = '12/31/2014'; 
            objMedPolMem.Effective_Date__c='01/01/2014';       
            objMedPolMem.Policy_Member_External_ID__c = '07|ODS|123456789|89|89|02-01-2002';
            objMedPolMem.Subscriber__c = objMedPolMem1.Id;
            insert objMedPolMem;
            
            Interaction__c oInteraction = New Interaction__c(
                Interacting_With__c = objMemAccount .id,
                Interacting_With_Type__c = 'Member',
                Caller_Name__c = 'testMcCree',
                Interaction_Origin__c = 'Inbound Call'
            );
            insert oInteraction;
            
        }
    }
    
     /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testOrderIdCard()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];

        policy_member__c policyObj = [select id,Relationship__c,name from policy_member__c where Relationship__c ='self' limit 1];
        Policy__c policyyObj = [select id, name from Policy__c where name ='Medical Product' Limit 1];
        //policy_member__c policyObj2 = [select id,Relationship__c,name from policy_member__c where Relationship__c ='self' and Policy__c =:policyyObj.id  limit 1];
        
        Policy__c policyyObj2 = [select id, name from Policy__c where name ='Medical Product' and Major_LOB__c ='EPO' Limit 1];
        policy_member__c policyObj2 = [select id,Relationship__c,name from policy_member__c where Relationship__c ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        policy_member__c policyObj3 = [select id,Relationship__c,name,Member__c,Policy__r.Segment_Indicator__c from policy_member__c where Relationship__c ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        
        List<String> lstMemId = new List<String>();
        String strId;
        String polId =policyyObj.Id;
        String polMemId = policyObj.Id;
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.MemberIdCards_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                MemberIdCards_C_HUM objMemId = new MemberIdCards_C_HUM();
               // objMemId.getPolicyMemberData();
                strId = policyObj.Id;
                lstMemId.add(strId);
                Continuation continueObj =  MemberIdCards_C_HUM.invokeOrderIdCardService(lstMemId, polMemId);
                continueObj.continuationMethod = 'parseOrderIdCardResponse';
                
                continueObj.state = 'Both';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"MemberSearchResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","MemberSearchDetail": [{" FirstName ": "Marc","CardRequestDate": "01/01/2017","RequestDate": "01 JAN 2017"," LastName ": "Tio."," BusinessSegment ": "MTV"," GroupNbr ": "923382"," GroupName": " HUMANA INC "," Status ": "Approved."," Reason ": "MTV"," CardType ": "New ","MemberId": "1211019","DependentCode": "00"," StatusDate ": "01/01/20181"," ExpirationDate ": "01/01/2018"}]}}';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                MemberIdCards_C_HUM.parseOrderIdCardResponse(labelGetmember,continueObj);
                //positive scenario
                RequestIdCard_DTO_HUM.PolicyMembersListDTO retObj = MemberIdCards_C_HUM.getActivePolicyMembers(polMemId,polMemId,polId);
                //negative scenario
                Policy_Member__c p = [Select id,Subscriber__c,Policy__c,Status__c from Policy_Member__c where id=:policyObj2.id];
                retObj = MemberIdCards_C_HUM.getActivePolicyMembers(policyObj2.id,null,policyyObj2.id);
                Object retObj2 = MemberIdCards_C_HUM.getMemberLevelDetails('923382','RENA JORDAN', policyyObj2.id);
                Object retObj3 = MemberIdCards_C_HUM.getMemberLevelDetails('', 'RENA JORDAN', strId);
                Object retObj4 = MemberIdCards_C_HUM.getMemberLevelDetails('', '', strId);
                retObj2 = MemberIdCards_C_HUM.processMemberLevelResponse(labelGetmember,continueObj);
                List<Policy_Member__c> lstPM = new List<Policy_Member__c>();
                lstPM.add(policyObj3);
                Boolean bidcardtest = MemberIdCards_D_HUM.getIdCardRequestInL7Days(lstPM,'');
                Boolean bRequestIdCardTest = MemberIdCards_S_HUM.checkIDCardRequestInL7Days(lstPM,'');
            Test.stopTest();
        
        }
    }
    
    /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testHSANonHumana()
    {
        setUpTestDataForHSANonHumana();
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<String> lstMemId = new List<String>();
        String strId;
        policy_member__c policyObj = [select id,Relationship__c,name from policy_member__c where Policy__r.Major_LOB__c = :GLOBAL_CONSTANT_HUM.MEMBERIDCARD_HSA limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.MemberIdCards_VF_HUM);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            MemberIdCards_C_HUM objMemId = new MemberIdCards_C_HUM();
            //system.assertEquals(objMemId.bDispSPAATransferError, true);
            MemberIdCards_C_HUM.parseOrderIdCardResponse(null,null);
            Test.stopTest();
        
        }
    }
    
    /**
    * Test method for negative Member Id Card.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testNegativeMemberIdCard()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<String> lstMemId = new List<String>();
        String strId;

        policy_member__c policyObj = [select id,Relationship__c,name from policy_member__c where Relationship__c ='self' limit 1];
        Policy__c policyyObj = [select id, name from Policy__c where name ='Medical Product' Limit 1];
        //policy_member__c policyObj2 = [select id,Relationship__c,name from policy_member__c where Relationship__c ='self' and Policy__c =:policyyObj.id  limit 1];
        
        Policy__c policyyObj2 = [select id, name from Policy__c where name ='Medical Product' and Major_LOB__c ='EPO' Limit 1];
        policy_member__c policyObj2 = [select id,Relationship__c,name from policy_member__c where Relationship__c ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        

        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.MemberIdCards_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                
                MemberIdCards_C_HUM memIdCard = new MemberIdCards_C_HUM();
                MemberIdCards_S_HUM oServiceMember = new MemberIdCards_S_HUM();
                delete lstWebCallout;
                strId = policyObj.Id;
                lstMemId.add(strId);
                Continuation continueObj = (Continuation)MemberIdCards_C_HUM.invokeOrderIdCardService(lstMemId, policyObj2.id);
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"MemberSearchResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","MemberSearchDetail": [{" FirstName ": "Marc","CardRequestDate": "01/01/2017","RequestDate": "01 JAN 2017"," LastName ": "Tio."," BusinessSegment ": "MTV"," GroupNbr ": "923382"," GroupName": " HUMANA INC "," Status ": "Approved."," Reason ": "MTV"," CardType ": "New ","MemberId": "1211019","DependentCode": "00"," StatusDate ": "01/01/20181"," ExpirationDate ": "01/01/2018"}]}}';
                httpRes.setBody(resBodyString);
				System.assertEquals(httpRes.getBody(),resBodyString);
                string reqType = '';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                MemberIdCards_C_HUM.parseOrderIdCardResponse(labelGetmember,continueObj);
            	MemberIdCards_C_HUM memberIdCards = new MemberIdCards_C_HUM();
                MemberIDCards.bMedicareMedicaid = true;
            	memberIdCards.checkForMemberIDCardRequest();
            	memberIdCards.getRecords();
            	memberIdCards.getThis();
                memberIdCards.getRecord();
                
                

            Test.stopTest();
        
        }
    }
    /**
    * Test method for covering DTO classes
    *<p>
    * @param            NULL
    * @return           void
    */
    static testmethod void testDTOClass() {
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        System.runAs(testUser)
        {
            HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails idcardDet = new HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails();
            idcardDet.CardIndex = '';
            idcardDet.CardOrderDate = '';
            idcardDet.CardsFound = '';
            idcardDet.ErrorCode = '';
            idcardDet.ErrorDescription = '';
            idcardDet.IsError = '';
            idcardDet.OrderInProgress = '';
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail Order = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail();
            Order.CustCovSeqNbr = '';
            Order.CustomerGenKey = '';
            Order.DateOfBirth = '';
            Order.DependentCode = '';
            Order.GroupNbr = '';
            Order.IdCardDetailsDto = idcardDet;
            List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail> lOrder = new List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail>();
            lOrder.add(Order);
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail lOrders = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail();
            lOrders.OrderMemberIdCardResult = lOrder;
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail respDet = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail();
            respDet.OrderMemberIdCardResults = lOrders;

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO  memDTO = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO();
            memDTO.FirstName = '';
            memDTO.CardRequestDate = '';
            memDTO.LastName = '';
            memDTO.RequestDate = '';
            memDTO.BusinessSegment = '';
            memDTO.GroupNbr = '';
            memDTO.GroupName = '';
            memDTO.Status = '';
            memDTO.Reason = '';
            memDTO.ReasonToolTip = '';
            memDTO.CardType = '';
            memDTO.MemberId = '';
            memDTO.DependentCode = '';
            memDTO.StatusDate = '';
            memDTO.ExpirationDate = '';
            memDTO.CardTypeDesc = '';

            List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> lmemDTO = new List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>();
            lmemDTO.add(memDTO);

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse  memSearchResp = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse();
            memSearchResp.MemberSearchDetail = lmemDTO;
            memSearchResp.IsError = '';
            memSearchResp.ErrorMsg = '';
			System.assertEquals(memSearchResp.MemberSearchDetail,lmemDTO);
        }
    }
    
    
    static testmethod void testDTOClassNegative() {
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        System.runAs(testUser)
        {
             
            HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails idcardDet = new HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails();
            idcardDet.CardIndex = '';
            idcardDet.CardOrderDate = '';
            idcardDet.CardsFound = '';
            idcardDet.ErrorCode = '';
            idcardDet.ErrorDescription = '';
            idcardDet.IsError = '';
            idcardDet.OrderInProgress = '';
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail Order = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail();
            Order.CustCovSeqNbr = '';
            Order.CustomerGenKey = '';
            Order.DateOfBirth = '';
            Order.DependentCode = '';
            Order.GroupNbr = '';
            Order.IdCardDetailsDto = idcardDet;
            List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail> lOrder = new List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail>();
            lOrder.add(Order);
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail lOrders = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail();
            lOrders.OrderMemberIdCardResult = lOrder;
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail respDet = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail();
            respDet.OrderMemberIdCardResults = lOrders;

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO  memDTO = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO();
            memDTO.FirstName = '';
            memDTO.CardRequestDate = '';
            memDTO.LastName = '';
            memDTO.RequestDate = '';
            memDTO.BusinessSegment = '';
            memDTO.GroupNbr = '';
            memDTO.GroupName = '';
            memDTO.Status = '';
            memDTO.Reason = '';
            memDTO.ReasonToolTip = '';
            memDTO.CardType = '';
            memDTO.MemberId = '';
            memDTO.DependentCode = '';
            memDTO.StatusDate = '';
            memDTO.ExpirationDate = '';
            memDTO.CardTypeDesc = '';

            List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> lmemDTO = new List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>();
            lmemDTO.add(memDTO);

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse  memSearchResp = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse();
            memSearchResp.MemberSearchDetail = lmemDTO;
            memSearchResp.IsError = '';
            memSearchResp.ErrorMsg = '';

        }
    }
    
    static testmethod void testAssignCaseValues() {
    
        User testUser = [SELECT Current_Queue__c 
                            FROM User 
                            WHERE Id = :UserInfo.getUserId()];
                               
        Account acc = [select id, FirstName from Account where FirstName = 'Test FName'];
        policy_member__c policyMemObj = [select id,Relationship__c,name from policy_member__c where Relationship__c ='self' limit 1];
        
        Product2 prod = [Select id, Product_Type__c from Product2 where Product_Type__c =: GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MA];
        
        Policy__c policyObj = [select id, name, Product_Type__c from Policy__c where name ='Medical Product' Limit 1];
        policyObj.Product__c= prod.Id;
        update policyObj;
        
        Interaction__c intObj = [SELECT Id, Interacting_With__c FROM Interaction__c WHERE Caller_Name__c = 'testMcCree'];
        
        Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true]; 
                                                 
        Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Fulfillment'];  
        
        Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'ID Card/Access Card Request' AND Is_Active__c = true LIMIT 1];                                               
        
        CTCI_Junction__c oCTCI = [SELECT Id, Classification_Type__c, Classification__c, Intent__c
                                      FROM CTCI_Junction__c
                                      WHERE Classification_Type__c = :oClassType.Id AND Classification__c =:oClsMatch.Id AND Intent__c =: oIntMatch.Id AND IsActive__c = true];
                                      
        LIst<Interaction_Member__c> listmem = [SELECT id,Interaction__c ,Interacting_About__c ,Interacting_About_Type__c   from Interaction_Member__c];

        case caseobj = [select id, Owner_Queue__c, Status, Interacting_With_Type__c, Interacting_About_Type__c from case where Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];

                
        System.runAs(testUser){
        
            Test.startTest();
            Test.setCurrentPage(page.MemberIdCards_VF_HUM);
            ApexPages.currentPage().getParameters().put('Id',policyMemObj.Id);
            
            ApexPages.StandardController scase = new ApexPages.StandardController(caseobj);
            
            MemberIdCards_C_HUM oMembreIdCards = new MemberIdCards_C_HUM();

            oMembreIdCards.assignCaseValues();  
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            HUMNewCaseRedirectExtension caseExtn = new HUMNewCaseRedirectExtension(scase); 
            caseExtn.objectID = policyMemObj.Id;
            caseExtn.sObjectName = policyMemObj.Id.getSobjectType().getDescribe().getName();
            caseExtn.funcOnPageLoad();
            caseExtn.saveCase(); 
            Test.stopTest();            
        }
    }
    
}