/*******************************************************************************************************************************
Apex Class Name : GroupIdCards_T_HUM.cls
Version         : 1.0
Created On      : Mar 23,2018
Function        : Class contains test methods GrouMemberDetails_C_HUM class.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
*  Melkisan Selvaraj           220731                03/23/2018                  Original Version  (REQ 357336 )
* Prerna Prasad				   243782				 06/14/2018					 REQ - 366904
 ********************************************************************************************************************************/

@isTest
private class GroupMemberDetails_T_HUM 
{
    
    /**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
        
        HUM_Webservice_Callout__c RighSourceServiceDetails = new HUM_Webservice_Callout__c(Name ='HIDCMS_GroupMemberLevelSearch',Certificate_Name__c = 'Humana_GuidanceCare_Alderaan' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IDCardService/ GroupMemberLevelSearch' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceServiceDetails;
        RecordType PersonAccountRecordType = [Select Id from RecordType where Name = 'Group' and sObjectType='Account'];
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        system.runAs(objUser)
        {
            // Insert Group Account
            Account objGrpAccount = HUMTestDataHelperTest.getAccount();
            objGrpAccount.RecordType = PersonAccountRecordType;
            objGrpAccount.Name = 'TestGroup00';
            objGrpAccount.Group_Number__c = '216277';
            objGrpAccount.BillingState = 'Alabama';
            objGrpAccount.Home_Office_Account__c = true;
            insert objGrpAccount;
            system.assertEquals(objGrpAccount.Name == 'TestGroup00', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objGrpAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';          
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objGrpAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
        }
    }
    
    /**
    * Test method for Group Member Details data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testGroupIdCard()
    {

        Account objAcc = [Select Id, CreatedDate, Group_Number__c, Name, Industry, LastModifiedDate from Account Where Name ='TestGroup00' limit 1];
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.GroupMemberDetails_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',objAcc.Id);
				ApexPages.currentPage().getParameters().put('tc','20');
                GroupMemberDetails_C_HUM objMemId = new GroupMemberDetails_C_HUM();
                Continuation conNullObj = (Continuation)GroupMemberDetails_C_HUM.invokeGetGroupMemberDetailRequest(null,'11/30/2017','MTV','0013B00000J0mDDQAZ','John','Swen','H1100908990','00');
                Continuation continueObj = (Continuation)GroupMemberDetails_C_HUM.invokeGetGroupMemberDetailRequest('216277','11/30/2017','MTV','0013B00000J0mDDQAZ','John','Swen','H1100908990','00');
                continueObj.continuationMethod = 'parseRSGetGroupMemberDetailResponse';
                continueObj.state = 'Both';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"GroupMemberResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","GroupSearchDetail": [{"CardCount": "1","CardRequestDate": "11/30/2017","GroupName": "HUMANA INC.","Platform": "MTV","GroupNbr": "923382","FirstName": "John","LastName": "Swen","MemberId": "H1100908990"}]}}';
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
                GroupMemberDetails_C_HUM.parseRSGetGroupMemberDetailResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
    /**
    * Negative test method for Group Member Details data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testGroupIdCardExc()
    {
        Account objAcc = [Select Id, CreatedDate, Group_Number__c, Name, Industry, LastModifiedDate from Account Where Name ='TestGroup00' limit 1];
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.GroupMemberDetails_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',objAcc.Id);
				ApexPages.currentPage().getParameters().put('tc','20');
                GroupMemberDetails_C_HUM objMemId = new GroupMemberDetails_C_HUM();
                Continuation continueObj = (Continuation)GroupMemberDetails_C_HUM.invokeGetGroupMemberDetailRequest('216277','11/30/2017','MTV','0013B00000J0mDDQAZ','John','Swen','H1100908990','00');
                continueObj.continuationMethod = 'parseRSGetGroupMemberDetailResponse';
                continueObj.state = 'Both';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '<{ "Fault":{ "faultcode":"\n Code-100..1", "faultstring":"\n error_service_name :\"IDCardService |\n error_service_method : GroupSearch |\n fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"880464639", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/IDCardService/GroupSearch", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 400 Bad Request\r\nContent-Type: application/soap+xml\r\nX-Backside-Transport: FAIL FAIL\r\nConnection: close\r\n\r\n", "error-message":"Error From HIDCMS Service: Internal Error (from server)", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }>';
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
                GroupMemberDetails_C_HUM.parseRSGetGroupMemberDetailResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }

	/**
	* Test method for DTO class.
	*<p>
	* @param            NULL
	* @return           void
	*/
	static testMethod void testDTO()
	{
		User testUser = [Select Id, Name from User where LastName = 'test User1'];
		system.runAs(testUser)
		{
			Test.startTest();
			HIDCMSGroupMemLvlSearchResponse_DTO_HUM.GroupMemberLevelSearchDetailDTO gmlshd = new HIDCMSGroupMemLvlSearchResponse_DTO_HUM.GroupMemberLevelSearchDetailDTO();
			gmlshd.FirstName = '';
			gmlshd.CardRequestDate = '';
			gmlshd.LastName = '';
			gmlshd.BusinessSegment = '';
			gmlshd.CardFormat = '';
			gmlshd.Status = '';
			gmlshd.Reason = '';
			gmlshd.CardType = '';
			gmlshd.CardDesc = '';
			gmlshd.MemberId = '';
			gmlshd.DependentCode = '';
			gmlshd.StatusDate = '';
			gmlshd.ExpirationDate = '';
			gmlshd.GroupNbr = '';
			gmlshd.GroupName = '';
			gmlshd.CardTypeDesc = '';

			List<HIDCMSGroupMemLvlSearchResponse_DTO_HUM.GroupMemberLevelSearchDetailDTO> lstGmlsh = new List<HIDCMSGroupMemLvlSearchResponse_DTO_HUM.GroupMemberLevelSearchDetailDTO>();
			lstGmlsh.add(gmlshd);

			HIDCMSGroupMemLvlSearchResponse_DTO_HUM.GroupMembeLevelSearchResponseDTO gmlsh = new HIDCMSGroupMemLvlSearchResponse_DTO_HUM.GroupMembeLevelSearchResponseDTO();
			gmlsh.IsError = '';
			gmlsh.ErrorMsg = '';
			gmlsh.GroupMemberLevelSearchDetail = lstGmlsh;

			HIDCMSGroupMemLvlSearchResponse_DTO_HUM h = new HIDCMSGroupMemLvlSearchResponse_DTO_HUM();
			h.GroupMembeLevelSearchResponse = gmlsh;
		}
	}
}