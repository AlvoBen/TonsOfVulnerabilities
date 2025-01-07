/************************************************************************************************************************************************
Apex Class Name  : PrimaryCare_LT_HUM
Version          : 1.0
Created Date     : APR 04 2022
Function         : Test Class for Lightning PrimaryCare classes.
Test Class       :
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson                                 04/04/2022                 Original Version as per REQ - 3196414
*************************************************************************************************************************************************/

@isTest
private class PrimaryCare_LT_HUM {
/*
	* Method Name : setUpTestData
	* Description : This method creates test data for test class
	* Return type : None
	* Paramater   : None
	*/
	@testSetup static void setUpTestData()
	{
		User objUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
		insert objUser;
		system.assertEquals(objUser.lastname == 'test User1', true);
		system.runAs(objUser)
		{

			HUM_Webservice_Callout__c MBEServiceDetails = new HUM_Webservice_Callout__c(Name = 'MBEPlusService', Certificate_Name__c = 'Humana_GuidanceCare_Mercury', Active__c = true, End_Point_URL__c = 'https://dev-crmservicews.humana.com/MBEPLUSServiceREST/GetMember', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
			insert MBEServiceDetails;
			system.assert(HUM_Webservice_Callout__c.getValues('MBEPlusService').Name == 'MBEPlusService');
			HUM_Webservice_Callout__c EPSServiceDetails = new HUM_Webservice_Callout__c(Name = 'EPSServiceREST_V1', Certificate_Name__c = 'Humana_GuidanceCare_Mercury', Active__c = true, End_Point_URL__c = 'https://dev-crmservicews.humana.com/MBEPLUSServiceREST/GetMember', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
			insert EPSServiceDetails;

			// Insert Member Account
			Account objMemAccount = HUMTestDataHelperTest.getAccount();
			objMemAccount.FirstName = 'Test FName';
			objMemAccount.LastName = 'LName';
			objMemAccount.Enterprise_Id__c = String.valueof(1011356393);
			objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
			objMemAccount.Birthdate__c = String.valueOf(System.today().month() + '/' + system.today().day() + '/' + System.today().year());
			insert objMemAccount;
			system.assert(objMemAccount.LastName == 'LName');

			Account objGrpAccount = HUMTestDataHelperTest.getAccount();
			objGrpAccount.Name = 'New Case Account';
			objGrpAccount.BillingState = 'New York';
			objGrpAccount.Enterprise_Id__c = String.valueof(4000);
			objGrpAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
			insert objGrpAccount;
			system.assert(objGrpAccount.BillingState == 'New York');

			Product2 objProduct = new Product2();
			objProduct.Name = 'MED';
			objProduct.Product_Type__c = 'test';
			objProduct.ProductCode = 'MED';
			insert objProduct;
			system.assert(objProduct.Product_Type__c == 'test');

			Policy__c objPolicy = HUMTestDataHelperTest.getPolicy();
			objPolicy.Name = 'Test Policy';
			objPolicy.Group_Name__c = objMemAccount.Id;
			objPolicy.Product__c = objProduct.Id;

			insert objPolicy;
			system.assert(objPolicy.Name == 'Test Policy');
		}
	}
    
    /*
    * Method Name : testPrimaryCare
    * Description : This method is used to test the Primary Care funtionality
    * Return type : None
    * Paramater   : None
    */
    static testMethod void testPrimaryCare()
    {
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
      
        System.runAs(testUser)
        {
            Test.startTest();
            PrimaryCare_CE_HUM primaryCare = New PrimaryCare_CE_HUM();

            Continuation con = (Continuation)PrimaryCare_LC_HUM.getPcpPcdData('1011422755', '2022-09-09');
 
            if (con != null)
            {
                con.continuationMethod = 'parseMBEResponse';
                PcpPdpHistory_DTO_HUM pcppdpHisObj = new PcpPdpHistory_DTO_HUM('test', 'test', '2017-03-03', 'test', 'test','tgrouper','123', true);
                List<PcpPdpHistory_DTO_HUM> lstPcpPdpHisDTO = new List<PcpPdpHistory_DTO_HUM>();
                lstPcpPdpHisDTO.add(pcppdpHisObj);
                PrimaryCare_DTO_HUM DTOObj = new PrimaryCare_DTO_HUM();
                DTOObj.refDate = '2016-12-12';
                DTOObj.PcpPdpActiveDTO = New PcpPdpActive_DTO_HUM('test', 'test', 'test', '2017-03-03', 'test', 'test', 'test','test', true);
                DTOObj.lstPcpPdpHistoryDTO = lstPcpPdpHisDTO;
                con.State = DTOObj;
                system.assertEquals(lstPcpPdpHisDTO[0].sPracticeLocation, 'test');
				HttpResponse resMBE = new Httpresponse();
				resMBE.setBody('{ "GetMemberResponse":{ "Header":{ "ResponseHeader":{ "Environment":"Dev", "Version":"2.0", "Server":{ "Name":"LOUXI52DI01" }, "Metrics":{ "Duration":"79", "RequestTimeStamp":"2017-09-20T15:42:51.486Z", "ResponseTimeStamp":"2017-09-20T15:42:51.565Z" }, "ResponseMetaData":{ "Status":"Warning", "ClientSessionID":"GUID", "SessionID":"f8fec266-ed27-4a33-9cba-7853e2712a3f", "DataAuthorizationConstraintList":{ "DataAuthorizationConstraint":[ { "ConstraintType":"Include", "Tenant":"1" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"5" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"588555" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598631" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598641" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598642" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598643" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598644" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598645" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598646" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598647" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598648" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598651" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598652" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598653" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598654" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598655" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"598673" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"634682" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"655069" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"673733" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"707431" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"713828" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"735673" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"735675" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"735676" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"738206" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"879178" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"879179" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"9", "Group":"923382" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"16", "Group":"234645" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"47", "Group":"598640" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"47", "Group":"898631" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"47", "Group":"898632" }, { "ConstraintType":"Include", "Tenant":"1", "Source":"16" } ] } } } }, "Body":{ "Member":{ "DateOfBirth":"08/05/1957", "FirstName":"ANA", "Gender":"F", "LastName":"UBINAS", "MiddleInitial":"A", "Ssn":"582042684", "Contact":{ "ContactProfile":{ "EmailList":{ "PersonEmail":[  ] }, "PhoneList":{ "PersonPhone":[  ] }, "AddressList":{ "PersonAddress":[  ] } } },  "MasterId":"1011356393", "PlatformPointerList":{ "PlatformProxyKey":[ { "Application":"", "KeyType":"PERSONID", "KeyValue":"58204268401", "Source":"PR" }, { "Application":"", "KeyType":"PERSONID", "KeyValue":"003071127", "Source":"LV" }, { "Application":"", "KeyType":"AKANAME", "KeyValue":"Aka18113120655", "Source":"WPP" }, { "Application":"", "KeyType":"PERSONID", "KeyValue":"H44647884", "Source":"PR" }, { "Application":"", "KeyType":"PERSONID", "KeyValue":"H446478841", "Source":"PR" }, { "Application":"", "KeyType":"ODS_GENKEY", "KeyValue":"8121814140042", "Source":"ODS" } ] }, "SoldProductList":{ "SoldProduct":[ { "InsuranceProduct":{ "AlternateDescription":"MER RISK GATE CAP EXC", "Description":"076 813", "MajorLineOfBusinessCode":"MER", "MajorLineOfBusinessDescription":"MEDICARE HMO", "ProductStartDate":"01/01/2017", "ProductEndDate":"12/31/2017", "ProductId":"2021770101", "Source":"LV", "ProductLineCode":"MED", "ProductLineDescription":"MEDICAL PRODUCTS", "ProductTypeCode":"MAPD", "SegmentType":"MCR", "SoldProductDetailList":{ "SoldProductDetail":[ { "InsuranceCoverage":{ "EndDate":"12/31/9999", "StartDate":"01/01/2017", "PolicyType":"Individual", "CoverageType":"Single", "ContractId":"928095035", "PrimaryCarePhysician":{ "EffectiveDate":"01/01/2017", "Name":"ACOSTA AZCONA, CLARA J MD", "Npi":"1619987369", "SourceProviderKey":"098480118", "SourceGrouperNumber":"86540328", "GrouperName":"CORPORACION MEDICA ORIENTAL", "PracticeLocationNumber":"213155" }, "PrimaryCarePhysicianList":{ "PrimaryCarePhysician":[ { "EffectiveDate":"01/02/2017", "EndDate":"01/17/2017", "Name":"ACOTA AZCONA, CLARA J", "Npi":"1619985378", "SourceProviderKey":"093480118", "SourceGrouperNumber":"86240328", "GrouperName":"CORPORCION MEDICA ORIENTAL", "PracticeLocationNumber":"213555" } ] }, "OperationalMajorLineofBusiness":"MER", "PolicySequenceNumber":"0", "HcfaEnrollmentSourceCode":"P" } } ] }, "IsCarryOverCredit":"false", "IsCustomerCarveOut":"false", "IsCustomPlan":"false", "IsFedQualified":"false", "IsHdhp":"false", "IsPca":"false", "IsPcpRequired":"true", "IsRxMedicalIntegratedDeductible":"false", "IsRxSpeciality":"false" } } ] } }, "MessageList":{ "Message":[ { "Type":"Warning", "Code":"MBEW0003", "Source":"OSE Services", "Description":"No coverage found for one of the platforms." } ] } } } }');
				resMBE.setStatusCode(200);
				Map<String, HTTPRequest> mapDetails = con.getRequests();

                String reqType = '';
                for (String s : mapDetails.keyset())
                {
                    reqType = s;
                }
                List<string>labelMBE = new List<string>();
                labelMBE.add(reqType);
                Test.setContinuationResponse(reqType, resMBE);
                PcpPdpHistory_DTO_HUM pcppdpHisObj2 = new PcpPdpHistory_DTO_HUM('test', 'test', '2017-03-03', 'test', 'test', 'tgrouper','123',true);
                List<PcpPdpHistory_DTO_HUM> lstPcpPdpHisDTO2 = new List<PcpPdpHistory_DTO_HUM>();
                lstPcpPdpHisDTO2.add(pcppdpHisObj2);
                system.assertEquals(lstPcpPdpHisDTO2[0].sPracticeLocation, 'test');
				PrimaryCare_DTO_HUM DTOObj2 = new PrimaryCare_DTO_HUM();
				DTOObj2.refDate = '2016-11-11';
				DTOObj2.PcpPdpActiveDTO = New PcpPdpActive_DTO_HUM('test', 'test', 'test', '2017-03-03', 'test', 'test', 'test', 'test', true);
				DTOObj2.lstPcpPdpHistoryDTO = lstPcpPdpHisDTO2;
                String processedResponse = JSON.serialize(PrimaryCare_LC_HUM.parseMBEResponse(labelMBE, DTOObj2));

				Continuation conEPS = (Continuation)PrimaryCare_LC_HUM.getProviderPhoneNumber('1598146409');
				String EPSRes = '{ "SearchProviderResponse":{ "EnterpriseProviders":[ { "EnterpriseID":"11060211", "UniqueID":{ "Source":"PIM" , "ProviderID":"000079599UD" }, "ProviderName"' +
					':{ "Last":"Artis", "First":"Krystal", "Degree":"PA" }, "DbaName":"Artis, Krystal N PA", "Classification":"", "Demographics":{ "Addresses":[ ' +
					'{ "AddressType":"Billing", "Line1":"PO BOX 37215", "Line2":"", "City":"Baltimore", "State":"MD", "Zip":"212755001", "Phones":[ ' +
					'{ "PhoneType":"Phone", "PhoneNumber":"4016029262", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"4239902470", "Extension":"" }, ' +
					'{ "PhoneType":"Phone", "PhoneNumber":"4103285946", "Extension":"" } ] }, { "AddressType":"", "Line1":"111 Michigan Ave NW", "Line2":"", ' +
					'"City":"Washington", "State":"DC", "Zip":"200102978", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2024763032", "Extension":"" }, ' +
					'{ "PhoneType":"Fax", "PhoneNumber":"2024764928", "Extension":"" } ] } ], "DateOfBirth":"0001-01-01", "Gender":"Female" }, "Identifiers":[ ' +
					'{ "IdentifierType":"NpiID", "Identifiers":[ { "ID":"1457502858" } ] } ], "Specialties":[ { "Description":"Physician Assistant [PIMS] / ' +
					'Psychoanalysis [ARC,AMA]" } ], "SourceProviders":[ { "Source":"CAS", "ProviderID":"000079599UD" }, { "Source":"MTV", "ProviderID":"165809451278" }' +
					', { "Source":"NPI", "ProviderID":"1457502858" }, { "Source":"PIM", "ProviderID":"1641194" } ], "Taxonomy":[ { "Code":"363A00000X" }, ' +
					'{ "Code":"363AM0700X" } ] }, { "EnterpriseID":"4366474", "UniqueID":{ "Source":"DEA" }, "ProviderName":{ "Last":"ARTIS", "First":"KRYSTAL", ' +
					'"Degree":"PA-C" }, "DbaName":"ARTIS,KRYSTAL NYA PA-C", "Classification":"", "Demographics":{ "Addresses":[{ "AddressType":"Mail", "Line1":"PO", "Phones":[{ "PhoneType":"Phone", "PhoneNumber":"4103285946", "Extension":"" }]}], "DateOfBirth":"", "Gender":"" }, ' +
					'"Identifiers":[ { "IdentifierType":"DeaID", "Identifiers":[ { "ID":"MA1917410" } ] } ], "Specialties":[ { "Description":"Family and General ' +
					'Practice [DEA,NPI]" } ], "SourceProviders":[ { "Source":"DEA", "ProviderID":"MA1917410" } ], "Taxonomy":[ ] } ] } }';

				conEPS.continuationMethod = 'processEPSSData';
				conEPS.State = primaryCare.sRefDate;
				HttpResponse resEBS = new Httpresponse();
				resEBS.setBody(EPSRes);
				resEBS.setStatusCode(200);
				Map<String, HTTPRequest> mapEpsDetails = conEPS.getRequests();
				system.assert(mapEpsDetails.size() >  0);
				reqType = '';
				for (String s : mapEpsDetails.keyset())
				{
					reqType = s;
				}
				List<string>labelEPS = new List<string>();
				labelEPS.add(reqType);
				Test.setContinuationResponse(reqType, resEBS);
				DTOObj = New PrimaryCare_DTO_HUM();
				DTOObj.refDate = '2017-09-09';
				PcpPdpActive_DTO_HUM activePCP = new PcpPdpActive_DTO_HUM('test', 'test', 'test', '2017-03-03', 'test', 'test', 'test', 'test', true);
				DTOObj.PcpPdpActiveDTO = activePCP;
				String re = JSON.serialize(PrimaryCare_LC_HUM.processEPSSData(labelEPS, DTOObj));

			}

			Test.stopTest();
		}
	}
}