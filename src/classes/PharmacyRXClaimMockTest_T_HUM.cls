/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimMockTest_T_HUM
Version            : 1.0
Function           : This Class contains mock response to for test class
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Goud                                      07/24/2020                Original Version - REQ - 1151554 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claim Details page (RxXP)
*******************************************************************************************************************/

public class PharmacyRXClaimMockTest_T_HUM implements HttpCalloutMock  {
    public String json = '{'+
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
       
    string jsonError =  '{ "Fault":{ "faultcode":"Code-67.8.1", "faultstring":"","detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/PharmacyClaimsService/GetClaimDetails", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" },  "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
          
    public HTTPResponse respond(HTTPRequest req)
    {
        HttpResponse resp = new HttpResponse();
        resp.setStatusCode(200);
        resp.setStatus('Complete');
        
		 resp.setBody(json);
             
        return resp;
    }
    public HTTPResponse respondError(HTTPRequest req)
    {
        HttpResponse resp = new HttpResponse();
        resp.setStatusCode(400);
        resp.setStatus('Error');
        
        resp.setBody(jsonError);
        
        return resp;
    }
    

}