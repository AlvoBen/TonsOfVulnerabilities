/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsDetail_T_HUM
Version            : 1.0
Function           : This Class contains methods to test Pharmacy Rx Claims Detail.
Created On         : July 24 2020
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Goud                                      07/24/2020                 Original Version - REQ - 1151554 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claim Details page (RxXP)
* Prasanthi Kandula                                   07/28/2021                 US 2031568 - PR00094226 - CR AEP readiness - Low Security Issues       
*******************************************************************************************************************/
@isTest
public class PharmacyRXClaimsDetail_T_HUM {
    
    public static Integer RESPONSE_CODE = 200;
    public static String RESPONSE_STATUS = 'Complete';
    public static String RESPONSE_BODY = '{'+
                '   \"ActualProcessDate\": \"2019-08-23T13:34:19\",'+
                '   \"AuthorizationNumber\": \"396350215401\",'+
                '   \"ClaimAdjudicationDate\": \"2019-09-20T00:00:00\",'+
                '   \"ClaimBrandGeneric\": \"G\",'+
                '   \"ClaimLoadDate\": \"2019-09-22T06:40:48\",'+
                '   \"ClaimProcessDate\": \"2019-09-21T00:00:00\",'+
                '   \"ClaimProcessingFee\": 0,'+
                '   \"ClaimReceiveDate\": \"2019-08-23T00:00:00\",'+
                '   \"ClaimSource\": \"ARG\",'+
                '   \"ClaimStatus\": \"PAID\",'+
                '   \"ClaimTier\": \"1\",'+
                '   \"CoinsuranceAmount\": 0,'+
                '   \"CopayAmount\": 0,'+
                '   \"DataSource\": \"EDW\",'+
                '   \"DateOfService\": \"2019-08-04T00:00:00\",'+
                '   \"DedPaidAmount\": 15.51,'+
                '   \"DispenseAsWritten\": \"0\",'+
                '   \"FillNumber\": 1,'+
                '   \"IsMaintenanceDrug\": \"1\",'+
                '   \"IsPharmacyPAR\": \"Y\",'+
                '   \"IsPreferredNetwork\": \"N\",'+
                '   \"IsReversedClaim\": \"N\",'+
                '   \"LISAmount\": 15.51,'+
                '   \"MedicareID\": \"6HM5NJ9QK75\",'+
                '   \"MemberResponsibleAmount\": 0,'+
                '   \"NDC\": \"13668008190\",'+
                '   \"NetPaidAmount\": 15.51,'+
                '   \"Part_B_D\": \"D\",'+
                '   \"PersonGenKey\": \"9575004726422\",'+
                '   \"PharmacyNPI\": \"1538104732\",'+
                '   \"PharmacyStatusCode\": \" R\",'+
                '   \"PrescriberNPI\": \"1841267010\",'+
                '   \"PriorAuthorizationNumberSubmitted\": \"\",'+
                '   \"PriorAuthorizationTypeCode\": \"\",'+
                '   \"RxCount\": 1,'+
                '   \"RXCUI\": \"000200224\",'+
                '   \"RxDaySupply\": \"30\",'+
                '   \"RxNumber\": \"000000740010\",'+
                '   \"RxQuantity\": 30,'+
                '   \"RxSpecialityCode\": \"\",'+
                '   \"RxTypeCode\": \"RX\",'+
                '   \"RxWrittenDate\": \"2019-07-05T00:00:00\",'+
                '   \"TransitionClaimCode\": \"NA\",'+
                '   \"ClaimId\": \"396350215401\",'+
                '   \"DrugDiscountIndicator\": \"N\",'+
                '   \"RxClaimPharmacy\":    {'+
                '      \"PharmacyIdQualifier\": \"44697\",'+
                '      \"MailOrderIndicator\": \"N\",'+
                '      \"PharmacyAddress1\": \"8603 FLORIDA MINING BLVD\",'+
                '      \"PharmacyAddress2\": \"\",'+
                '      \"PharmacyCity\": \"TAMPA\",'+
                '      \"PharmacyFax\": \"8002511187\",'+
                '      \"PharmacyEmailAddress\": \"\",'+
                '      \"PharmacyId\": \"1090048\",'+
                '      \"PharmacyMailRetail\": \"R\",'+
                '      \"PharmacyNABP\": \"01\",'+
                '      \"PharmacyName\": \"OMNICARE OF TAMPA #48231\",'+
                '      \"PharmacyNetworkType\": \"L\",'+
                '      \"PharmacyNPI\": \"1538104732\",'+
                '      \"PharmacyPhone\": \"8132479225\",'+
                '      \"PharmacyState\": \"FL\",'+
                '      \"PharmacyTaxId\": \"760716528\",'+
                '      \"PharmacyZipCode\": \"33634\",'+
                '      \"IsSpecialtyPharmacy\": \"0\"'+
                '   },'+
                '   \"RxClaimDrug\":    {'+
                '      \"NDC\": \"13668008190\",'+
                '      \"DrugName\": \"montelukast 10 mg tablet\",'+
                '      \"GCN\": \"94444\",'+
                '      \"GenericName\": \"montelukast sodium\",'+
                '      \"IsRefillClaim\": \"0\",'+
                '      \"MedID\": \"265564\",'+
                '      \"MostCommonDaysSupply\": \"30\",'+
                '      \"MostCommonNDC\": \"13668008190\",'+
                '      \"MostCommonQuantity\": \"30\",'+
                '      \"OTCIndicator\": \"F\",'+
                '      \"QuantityDispensed\": \"30\",'+
                '      \"MDLIndicator\": \"N\",'+
                '      \"SpecialtyDrugIndicator\": \"N\",'+
                '      \"UnitOfMeasure\": \"EA\",'+
                '      \"CompoundDosageIndicator\": \"\",'+
                '      \"PartDPreferenceIndicator\": \"P\",'+
                '      \"SupBeneIndicator\": \"\"'+
                '   },'+
                '   \"RxClaimPatient\":    {'+
                '      \"FirstName\": \"JOANN\",'+
                '      \"LastName\": \"EARNEST\",'+
                '      \"DateOfBirth\": \"1936-09-10T00:00:00\",'+
                '      \"PersonCode\": \"00\",'+
                '      \"Gender\": \"F\",'+
                '      \"GroupId\": \"0000247318000000000000000031LV\",'+
                '      \"MemberId\": \"H74870099\",'+
                '      \"CustomerId\": \"0544\",'+
                '      \"ClientId\": \"00010\",'+
                '      \"SubsGenKey\": \"9575004726422\",'+
                '      \"MedicareId\": \"6HM5NJ9QK75\",'+
                '      \"CoverageCode\": \"590000010356\",'+
                '      \"ExpandedRenewalIndicator\": \"\"'+
                '   },'+
                '   \"RxClaimActivities\":    ['+
                '            {'+
                '         \"ClaimDateType\": \"ACTUAL_PROCESS_DATE\",'+
                '         \"ClaimDateValue\": \"2019-09-20T00:00:00\"'+
                '      },'+
                '            {'+
                '         \"ClaimDateType\": \"ADJ_DATE\",'+
                '         \"ClaimDateValue\": \"2019-09-20T00:00:00\"'+
                '      },'+
                '            {'+
                '         \"ClaimDateType\": \"CLM_ADJUD_BEG_TIMESTAMP\",'+
                '         \"ClaimDateValue\": \"2019-09-20T00:00:00\"'+
                '      },'+
                '            {'+
                '         \"ClaimDateType\": \"FIN_CUTOFF_DATE\",'+
                '         \"ClaimDateValue\": \"2019-09-20T00:00:00\"'+
                '      },'+
                '            {'+
                '         \"ClaimDateType\": \"ORIG_CLM_RECV_DATE\",'+
                '         \"ClaimDateValue\": \"2019-08-23T00:00:00\"'+
                '      },'+
                '            {'+
                '         \"ClaimDateType\": \"PCS_GRP_RENEWAL_DATE\",'+
                '         \"ClaimDateValue\": \"2020-01-01T00:00:00\"'+
                '      },'+
                '            {'+
                '         \"ClaimDateType\": \"PROCESS_DATE\",'+
                '         \"ClaimDateValue\": \"2019-09-21T00:00:00\"'+
                '      },'+
                '            {'+
                '         \"ClaimDateType\": \"RX_WRITTEN_DATE\",'+
                '         \"ClaimDateValue\": \"2019-07-05T00:00:00\"'+
                '      }'+
                '   ],'+
                '   \"RxClaimBSQData\":    ['+
                '            {'+
                '         \"BSQKey\": \"RESP_BEN_STG_AMT1\",'+
                '         \"BSQValue\": \"15.51\"'+
                '      },'+
                '            {'+
                '         \"BSQKey\": \"RESP_BEN_STG_CNT\",'+
                '         \"BSQValue\": \"1\"'+
                '      },'+
                '            {'+
                '         \"BSQKey\": \"RESP_BEN_STG_QUAL_CD1\",'+
                '         \"BSQValue\": \"01\"'+
                '      }'+
                '   ],'+
                '   \"RxClaimAmounts\":    ['+
                '            {'+
                '         \"ClaimAmountType\": \"AWP_TOT_AMT\",'+
                '         \"ClaimAmountValue\": \"150.85\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"AWP_UNIT_AMT\",'+
                '         \"ClaimAmountValue\": \"5.0283\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"CLNT_MAC_UNIT_AMT\",'+
                '         \"ClaimAmountValue\": \".342\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"DISPENSE_FEE\",'+
                '         \"ClaimAmountValue\": \"5.25\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"GDCB_AMT\",'+
                '         \"ClaimAmountValue\": \"15.51\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"MBR_PAY_DIFF_ALT_INGRD_AMT\",'+
                '         \"ClaimAmountValue\": \"10.26\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"PHAR_DISP_FEE_AMT\",'+
                '         \"ClaimAmountValue\": \"5.25\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"PHAR_INGR_COST_AMT\",'+
                '         \"ClaimAmountValue\": \"10.26\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"PHAR_MAC_UNIT_AMT\",'+
                '         \"ClaimAmountValue\": \".342\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"PHAR_TOTAL_PAID_AMT\",'+
                '         \"ClaimAmountValue\": \"15.51\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"RESP_BEN_STG_AMT1\",'+
                '         \"ClaimAmountValue\": \"15.51\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"RPTG_NET_PAID_AMT\",'+
                '         \"ClaimAmountValue\": \"15.51\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"TOT_DRUG_COST_ACCUM_AMT\",'+
                '         \"ClaimAmountValue\": \"365.02\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"TOT_TROOP_ACCUM_AMT\",'+
                '         \"ClaimAmountValue\": \"365.02\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"TROOP_APPLY_AMT\",'+
                '         \"ClaimAmountValue\": \"15.51\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"UC_AMT\",'+
                '         \"ClaimAmountValue\": \"159.85\"'+
                '      },'+
                '            {'+
                '         \"ClaimAmountType\": \"WAC_INGRD_COST_AMT\",'+
                '         \"ClaimAmountValue\": \"22.5\"'+
                '      }'+
                '   ],'+
                '   \"RxClaimPrescriber\":    {'+
                '      \"PrescriberId\": \"1841267010\",'+
                '      \"PrescriberIdQualifier\": \"01\",'+
                '      \"PrescriberName\": \"IQBAL AHMED FARUQUI\"'+
                '   },'+
                '   \"RxClaimErrors\":    ['+
                '            {'+
                '         \"ClaimErrorCode\": \"AUTO_IGNR_ERR_CD1\",'+
                '         \"ClaimErrorValue\": \"Y\"'+
                '      },'+
                '            {'+
                '         \"ClaimErrorCode\": \"AUTO_PRT_ERR_CD1\",'+
                '         \"ClaimErrorValue\": \"S\"'+
                '      },'+
                '            {'+
                '         \"ClaimErrorCode\": \"ERR_CD1\",'+
                '         \"ClaimErrorValue\": \"020\"'+
                '      },'+
                '            {'+
                '         \"ClaimErrorCode\": \"SRC_ADJ_REAS_CD1\",'+
                '         \"ClaimErrorValue\": \"020\"'+
                '      }'+
                '   ],'+
                '   \"RxClaimArgusData\":    ['+
                '            {'+
                '         \"ArgusFieldName\": \"ARGUS_ADD_TIMESTAMP\",'+
                '         \"ArgusFieldValue\": \"08/23/2019 01:34:19 PM\"'+
                '      },'+
                '            {'+
                '         \"ArgusFieldName\": \"ARGUS_CLIENT_NBR\",'+
                '         \"ArgusFieldValue\": \"00010\"'+
                '      },'+
                '            {'+
                '         \"ArgusFieldName\": \"ARGUS_CLM_RECEIVED_DATE\",'+
                '         \"ArgusFieldValue\": \"08/23/2019\"'+
                '      },'+
                '            {'+
                '         \"ArgusFieldName\": \"ARGUS_CLM_TYPE_CD\",'+
                '         \"ArgusFieldValue\": \"P\"'+
                '      },'+
                '            {'+
                '         \"ArgusFieldName\": \"ARGUS_CUST_NBR\",'+
                '         \"ArgusFieldValue\": \"0544\"'+
                '      },'+
                '            {'+
                '         \"ArgusFieldName\": \"ARGUS_GRP_NBR\",'+
                '         \"ArgusFieldValue\": \"0000247318000000000000000031LV\"'+
                '      }'+
                '   ]'+
                '}';
    
    
    @testSetup static void prepareTestData(){
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('RxClaimSummaryUser1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test RxClaimSummaryUser1');
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c oHUMClaimSummaryService = new HUM_Webservice_Callout__c();
            oHUMClaimSummaryService.name = 'RxClaimSummary_GetClaimDetail';
            oHUMClaimSummaryService.Active__c = true;
            oHUMClaimSummaryService.Certificate_Name__c = 'testts';
            oHUMClaimSummaryService.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
            oHUMClaimSummaryService.Timeout__c = '120000';
            oHUMClaimSummaryService.Content_Type__c = 'application/json';
            oHUMClaimSummaryService.Request_Type__c = 'POST';
            insert oHUMClaimSummaryService;
            system.assert(oHUMClaimSummaryService.id != null);
            system.assert(oHUMClaimSummaryService.name == 'RxClaimSummary_GetClaimDetail');
            
            Account objAcc = new Account(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.Mbr_Gen_Key__c = '0000104665522';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched'); 
            
            Policy__c objpol = new Policy__c();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            objPolMem.Relationship_Code__c = '00';
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
        }
    }
    
    static testMethod void testClaimDetailsRequest(){
        
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c oPolMem = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            PharmacyRXClaimsDetail_C_HUM prc=new PharmacyRXClaimsDetail_C_HUM();
            String authNumber = '405477696291';
            Continuation con = (Continuation)PharmacyRXClaimsDetail_C_HUM.GetClaimDetail(oPolMem.member__r.Mbr_Gen_Key__c,authNumber);
            con.continuationMethod = 'processPharmacyRXClaimDetailResponse';
            HTTPRequest req = new HTTPRequest();
            PharmacyRXClaimMockTest_T_HUM fakeResponse = new PharmacyRXClaimMockTest_T_HUM();
            Test.setMock(HttpCalloutMock.class, fakeResponse);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            
            Test.setContinuationResponse(reqType,fakeResponse.respond(req));
            PharmacyRXClaimsDetail_C_HUM.processPharmacyRXClaimDetailResponse(labelGetmember,con);
            HTTPResponse response = fakeResponse.respond(req);
            System.assertEquals(RESPONSE_CODE, response.getStatusCode());
            System.assertEquals(RESPONSE_STATUS, response.getStatus());
            PharmacyRXClaimsDetail_Response_DTO_HUM pp = new PharmacyRXClaimsDetail_Response_DTO_HUM();  
            PharmacyRXClaimsDetail_Response_DTO_HUM.parse(response.getBody());
            Test.stopTest();
            
        } 
    }
    static testMethod void testClaimDetailsRequestCatch(){
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c oPolMem = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyRXClaimsDetail_C_HUM prc=new PharmacyRXClaimsDetail_C_HUM();
            String authNumber = '405477696291';
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='RxClaimSummary_GetClaimDetail' Limit 1];
            oSvcHeaderPayment.Name = 'ClaimDtailsTest';
            update oSvcHeaderPayment;
            system.assertEquals(oSvcHeaderPayment.Name,'ClaimDtailsTest');
            PharmacyRXClaimsDetail_C_HUM.GetClaimDetail(oPolMem.member__r.Mbr_Gen_Key__c,authNumber);
            Test.stopTest();
        }
    }
     static testMethod void testClaimDetailsRequestError(){
        
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c oPolMem = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            PharmacyRXClaimsDetail_C_HUM prc=new PharmacyRXClaimsDetail_C_HUM();
            String authNumber = '405477696291';
            Continuation con = (Continuation)PharmacyRXClaimsDetail_C_HUM.GetClaimDetail(oPolMem.member__r.Mbr_Gen_Key__c,authNumber);
            con.continuationMethod = 'processPharmacyRXClaimDetailResponse';
            HTTPRequest req = new HTTPRequest();
            PharmacyRXClaimMockTest_T_HUM fakeResponse = new PharmacyRXClaimMockTest_T_HUM();
            Test.setMock(HttpCalloutMock.class, fakeResponse);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;                
            }
            system.assert(reqType!=null);
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            
            Test.setContinuationResponse(reqType,fakeResponse.respondError(req));
            PharmacyRXClaimsDetail_C_HUM.processPharmacyRXClaimDetailResponse(labelGetmember,con);
            HTTPResponse response = fakeResponse.respondError(req);
            System.assertEquals(400, response.getStatusCode());
            PharmacyRXClaimsDetail_Response_DTO_HUM pp = new PharmacyRXClaimsDetail_Response_DTO_HUM();  
            PharmacyRXClaimsDetail_Response_DTO_HUM.parse(response.getBody());
            Test.stopTest();
            
        } 
    }
   
}