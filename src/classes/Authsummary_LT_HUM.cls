/*Apex class Name  : Authsummary_LT_HUM
Version            : 1.0
Function           : It is Test class forAuthsummary_LC_HUM
Created Date       : 14/07/2022 
*/
/*******************************************************************************************
Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
* Rajesh Narode										14/07/2022			 User story 3362694 Authorization Summary table
* Sagar G      										18/08/2023			 User story 4762116: T1PRJ0891415 - MF4439757 - Test Class Code Coverage
*********************************************************************************************************************************/
@isTest
public class Authsummary_LT_HUM {
    /*
* Method Name : setup
* Description : Create common test Data
* Return type : void
* Paramater   : NA
*/ 
    @testSetup static void setup() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);
        
        System.runAs(u)
        {
            //Creating Account Record
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;
            system.assertEquals(acc1.Id != Null,true);
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);
            Product2 objp = new Product2();
            objp.Name='MED';
            insert objp;
            //Creating Policy Record
            Policy__c pol = HUMTestDataHelperTest.getPolicy();
            pol.Group_Name__c = acc1.Id;
            pol.Name = 'Test Policy';
            pol.Product__c = objp.Id;
            insert pol;
            system.assertEquals(pol.Id != Null,true);
            system.assertEquals(pol.Name,'Test Policy');
            system.assertEquals(pol.Group_Name__c == acc1.Id,true);
            
            //Insert Policy Member
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1';
            polMember.Policy__c=pol.Id;
            polMember.Member__c=acc1.Id;
            insert polMember;
            PurchaserPlan PolicyObjOI1 = new PurchaserPlan();
            PolicyObjOI1.Policy_Group_Number__c='11111';
            PolicyObjOI1.Name = 'Policy New';
            PolicyObjOI1.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObjOI1.PayerId = acc1.Id;
            PolicyObjOI1.Major_LOB__c ='EM';
            PolicyObjOI1.Platform__c = 'EM';
            PolicyObjOI1.ETL_Record_Deleted__c = false;
            insert PolicyObjOI1;
            
            MemberPlan objMembplanOI1 = new MemberPlan();
            objMembplanOI1.Name='Name New';
            objMembplanOI1.MemberId=acc1.id;
            objMembplanOI1.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI1.Member_Id_base__c = 'test23456';
            objMembplanOI1.GroupNumber = '672534';
            objMembplanOI1.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI1.EffectiveTo = Date.newInstance(1999,12, 31);
            objMembplanOI1.PlanId= PolicyObjOI1.Id;
            insert objMembplanOI1;
            
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1');
            system.assertEquals(polMember.Policy__c,pol.Id);
            string Certificate_Name =''; 
            String orgId = UserInfo.getOrganizationId(); 
            if(orgId == '00D4C0000000TunUAE')
                {
                    Certificate_Name='Humana_GuidanceCare_TestFull';
                }
                //QA
                else if(orgId == '00Dc0000003uIioEAE')
                {
                   Certificate_Name = 'Humana_GuidanceCare_QA';                            
                }
                //QAS
                else if(orgId == '00DL0000005rQrAMAU')
                {
                    Certificate_Name = 'Humana_GuidanceCare_QAS';                           
                } 
                //PROD
                else if(orgId == '00DF00000005DjnMAE')
                {
                    Certificate_Name = 'Humana_GuidanceCare';                           
                }
                //Pharmacy
                else if(orgId == '00DZ0000009T1qeMAC')
                {
                    Certificate_Name = 'Humana_GuidanceCare_Pharmacy';                            
                }
            
            HUM_Webservice_Callout__c oHUMAuthSummaryService = new HUM_Webservice_Callout__c();
            oHUMAuthSummaryService.name='Clinical_AuthSummary';
            oHUMAuthSummaryService.Active__c=true;
            oHUMAuthSummaryService.Certificate_Name__c=Certificate_Name;
            oHUMAuthSummaryService.End_Point_URL__c='https://testst-crmservicews.humana.com/';
            oHUMAuthSummaryService.Timeout__c='120000';
            oHUMAuthSummaryService.Content_Type__c='application/json';
            oHUMAuthSummaryService.Request_Type__c='POST';
            insert oHUMAuthSummaryService;
            system.assert(oHUMAuthSummaryService.id!=null);
            system.assert(oHUMAuthSummaryService.name == 'Clinical_AuthSummary');
            
            HUM_Webservice_Callout__c oHUMAuthSummaryServiceV2 = new HUM_Webservice_Callout__c();
            oHUMAuthSummaryServiceV2.name='Clinical_AuthSummary_V2';
            oHUMAuthSummaryServiceV2.Active__c=true;
            oHUMAuthSummaryServiceV2.Certificate_Name__c=Certificate_Name;
            oHUMAuthSummaryServiceV2.End_Point_URL__c='https://testst-crmservicews.humana.com/';
            oHUMAuthSummaryServiceV2.Timeout__c='120000';
            oHUMAuthSummaryServiceV2.Content_Type__c='application/json';
            oHUMAuthSummaryServiceV2.Request_Type__c='POST';
            insert oHUMAuthSummaryServiceV2;
            system.assert(oHUMAuthSummaryServiceV2.id!=null);
            system.assert(oHUMAuthSummaryServiceV2.name == 'Clinical_AuthSummary_V2');
            
            HUM_Webservice_Callout__c oHUMAuthSummaryServicedetail = new HUM_Webservice_Callout__c();
            oHUMAuthSummaryServicedetail.name='Clinical_AuthDetail';
            oHUMAuthSummaryServicedetail.Active__c=true;
            oHUMAuthSummaryServicedetail.Certificate_Name__c=Certificate_Name;
            oHUMAuthSummaryServicedetail.End_Point_URL__c='https://testtest-crmservicews.humana.com';
            oHUMAuthSummaryServicedetail.Timeout__c='120000';
            oHUMAuthSummaryServicedetail.Content_Type__c='application/json';
            oHUMAuthSummaryServicedetail.Request_Type__c='POST';
            insert oHUMAuthSummaryServicedetail ;
            system.assert(oHUMAuthSummaryServicedetail.id!=null);
            system.assert(oHUMAuthSummaryServicedetail.name == 'Clinical_AuthDetail');
            
            HUM_Webservice_Callout__c oHUAuthDetailServicedetail = new HUM_Webservice_Callout__c();
            oHUAuthDetailServicedetail.name='Clinical_AuthDetail_V2';
            oHUAuthDetailServicedetail.Active__c=true;
            oHUAuthDetailServicedetail.Certificate_Name__c=Certificate_Name;
            oHUAuthDetailServicedetail.End_Point_URL__c='https://testtest-crmservicews.humana.com';
            oHUAuthDetailServicedetail.Timeout__c='120000';
            oHUAuthDetailServicedetail.Content_Type__c='application/json';
            oHUAuthDetailServicedetail.Request_Type__c='POST';
            insert oHUAuthDetailServicedetail ;
            system.assert(oHUAuthDetailServicedetail.id!=null);
            system.assert(oHUAuthDetailServicedetail.name == 'Clinical_AuthDetail_V2');
            
            Template__c flowTemplate = new Template__c ();
            //flowTemplate.Name ='TN-007604';
            flowTemplate.Description__c='Authorization/Referral Requirements';
            insert flowTemplate;
        }
    }  
    
    /*
* Method Name : authSummary
* Description : To call the authsummary details to check the values
* Return type : void
* Paramater   : NA
*/ 
    static testMethod void authSummary(){
        Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        system.assert(polMember.name == 'Policy Member PM1');
        MemberPlan objPLan = [Select Id from MemberPlan limit 1];
        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];
        system.assert(oAccount.name == 'Test Account Cases1');
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u){
            
            Test.startTest();
            Authsummary_LC_Hum.bTotalRecordFlag = false;
            AuthSummary_LS_HUM obj = new AuthSummary_LS_HUM();
            obj.totalRecordFound = 10;
            Continuation conti = (Continuation)Authsummary_LC_Hum.initiaterequest(objPLan.Id,1);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            objCon.state='test';
            string labelValue= 'Continuation-1';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            string sJson =   '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
                '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"11111", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"11111", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
                '}, "TotalRecordsFound":"50" } }';
            
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200);
            System.debug('oResponse'+oResponse.getStatusCode());
            Test.setContinuationResponse(labelValue, oResponse);
            List<String> labels = new List<String>{labelValue};
             labels.add(sJson);   
            Authsummary_LC_Hum.processResponse(labels, Authsummary_LC_Hum.continuationState);      
            Test.stopTest();
        }
    }
    static testMethod void authSummary_1() 
    {
        Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        system.assert(polMember.name == 'Policy Member PM1');
        MemberPlan objPLan = [Select Id from MemberPlan limit 1];
        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];
        system.assert(oAccount.name == 'Test Account Cases1');
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            
            Authsummary_LC_Hum.initiaterequest(objPLan.Id,1);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            Authsummary_LC_Hum.bTotalRecordFlag = false;
            AuthSummary_LS_HUM obj = new AuthSummary_LS_HUM();
            obj.totalRecordFound = 10;
            
            
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            string sjson  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
                '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"11111", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"11111", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
                '}, "TotalRecordsFound":"50" } }';
            Map<String, String> DataTemplate = new Map<String, String>();
            DataTemplate.put('MemPlanRecId',objPLan.Id);
            Authsummary_LC_Hum.processResponse(new List<String>{Authsummary_LC_Hum.sRequest},DataTemplate);
            res.setBody(sjson);
            res.setStatusCode(200); 
            
            Test.setContinuationResponse(Authsummary_LC_Hum.sRequest,res);
            MemberPlan memberPlanObj1 = [select id, PlanId,Issue_State__c,MemberId from MemberPlan limit 1];
            Authsummary_LC_Hum.responseparse(res,memberPlanObj1.id);
            
            list<AuthRefferal_Wrapper_LDTO_HUM> AuthResponseList  = new list<AuthRefferal_Wrapper_LDTO_HUM>();
            AuthRefferal_Wrapper_LDTO_HUM obwrapper = new AuthRefferal_Wrapper_LDTO_HUM();
            obwrapper.sAuthorizationOrReferralNumber ='1234';
            System.assertEquals('1234',obwrapper.sAuthorizationOrReferralNumber);
            
            obwrapper.sAuthorizationType = 'ALL';
            System.assertEquals('ALL',obwrapper.sAuthorizationType);
            
            obwrapper.sOverallStatus  = 'SS'; 
            System.assertEquals('SS',obwrapper.sOverallStatus);      
            
            obwrapper.sAdmFirstDay  = '22';
            System.assertEquals('22',obwrapper.sAdmFirstDay);
            
            obwrapper.sDischargeLastDay = '22';
            System.assertEquals('22',obwrapper.sDischargeLastDay);
            
            obwrapper.sServiceType = 'test';
            System.assertEquals('test',obwrapper.sServiceType);
            
            obwrapper.sTreatingProvider ='ets';
            System.assertEquals('ets',obwrapper.sTreatingProvider);
            
            obwrapper.sFacility = 'facility';
            System.assertEquals('facility',obwrapper.sFacility);
            
            obwrapper.sRequestingrovider = 'tetsrqstprv';
            System.assertEquals('tetsrqstprv',obwrapper.sRequestingrovider);
            
            obwrapper.sGroupId = '12345';
            System.assertEquals('12345',obwrapper.sGroupId);
            
            obwrapper.bIsAccessible  =  true;
            System.assertEquals(true, obwrapper.bIsAccessible);
            
            AuthResponseList.add(obwrapper);
            AuthRefferal_Wrapper_LDTO_HUM obwrappernegative = new AuthRefferal_Wrapper_LDTO_HUM();
            obwrappernegative .sAuthorizationOrReferralNumber ='1234';
            System.assertEquals('1234',obwrappernegative.sAuthorizationOrReferralNumber);
            
            obwrappernegative .sAuthorizationType = 'ALL';
            System.assertEquals('ALL',obwrappernegative.sAuthorizationType);
            
            obwrappernegative .sOverallStatus  = 'SS'; 
            System.assertEquals('SS',obwrappernegative.sOverallStatus);      
            
            obwrappernegative.sAdmFirstDay  = '22';
            System.assertEquals('22',obwrappernegative.sAdmFirstDay);
            
            obwrappernegative.bIsAccessible  =  false;
            System.assertEquals(false, obwrappernegative .bIsAccessible);
            AuthResponseList.add(obwrappernegative);
            
            Authsummary_LC_Hum.finalAuthsummarylist(AuthResponseList);
            Authsummary_LC_Hum.bshowresults =true;
            Authsummary_LC_Hum.lstAuthResponse=AuthResponseList;
            
            
            HttpResponse res1 = new HttpResponse();
            res1.setHeader('Content-Type', 'application/json');
            string sjson1  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
                '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"R0877001", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"R0877001", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
                '}, "TotalRecordsFound":"1001" } }';
            
            res1.setBody(sjson1);
            res1.setStatusCode(300); 
            Authsummary_LC_Hum.responseparse(res1,memberPlanObj1.id);
            system.assertEquals(Authsummary_LC_Hum.bshowserviceError,true);
            
            HttpResponse res2 = new HttpResponse();
            res2.setHeader('Content-Type', 'application/json');
            string sjson2  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+']'+ '}, "TotalRecordsFound":"0" } }';
            
            res2.setBody(sjson2);
            res2.setStatusCode(200); 
            Authsummary_LC_Hum.responseparse(res2,memberPlanObj1.id);
            HttpResponse res2_1 = new HttpResponse();
            res2_1.setHeader('Content-Type', 'application/json');
            string sjson2_1  = 'null';
            
            res2_1.setBody(sjson2_1);
            res2_1.setStatusCode(200); 
            Authsummary_LC_Hum.responseparse(res2_1,null);
            //system.assertEquals(Authsummary_LC_Hum.bshowError,true);
            HttpResponse res3 = new HttpResponse();
            res3.setHeader('Content-Type', 'application/json');
            string sjson3  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
                ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
                ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
                '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
                '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
            
            res3.setBody(sjson3);
            res3.setStatusCode(200); 
            Authsummary_LC_Hum.SUserinputAuthId ='123456';
            
            MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c,MemberId from MemberPlan limit 1];
            Authsummary_LC_Hum.getMemberAccount(memberPlanObj.Id); 
            Authsummary_LC_Hum.getMemberAccount(''); 
            Authsummary_LC_Hum.initiaterequest('',1);
            Authsummary_LC_Hum.bTotalRecordFlag=false;
            ClinicalAuthDetailsResponse_H_HUM oRespWrapper = new ClinicalAuthDetailsResponse_H_HUM();
            oRespWrapper = (ClinicalAuthDetailsResponse_H_HUM)Json.deserialize(sjson3,ClinicalAuthDetailsResponse_H_HUM.class);
            AuthSummary_LS_HUM Osvc = new AuthSummary_LS_HUM();
            Osvc.checkAuthSecurity(oRespWrapper,memberPlanObj.Id);
            HUM_Webservice_Callout__c obj1 = [select id from HUM_Webservice_Callout__c where name='Clinical_AuthDetail_V2'];
            delete obj1;
            Test.stopTest();
        }
    }
    
    /*
* Method Name : authSummaryNegative
* Description : To call the authsummary details to check the negative scenario
* Return type : void
* Paramater   : NA
*/ 
    static testMethod void authSummaryNegative() 
    {
        Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        system.assert(polMember.name == 'Policy Member PM1');
        MemberPlan objPLan = [Select Id from MemberPlan limit 1];
        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];
        system.assert(oAccount.name == 'Test Account Cases1');
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            
            Authsummary_LC_Hum.initiaterequest(objPLan.Id,1);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            Authsummary_LC_Hum.bTotalRecordFlag = null;
            AuthSummary_S_HUM obj = new AuthSummary_S_HUM();
            obj.totalRecordFound = 2000;
            Authsummary_LC_Hum.processResponse(new List<String>{Authsummary_LC_Hum.sRequest},'State');
            
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            string sjson  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
                '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"11111", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"11111", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
                '}, "TotalRecordsFound":"1001" } }';
            
            res.setBody(sjson);
            res.setStatusCode(200); 
            Test.setContinuationResponse(Authsummary_LC_Hum.sRequest,res);
            MemberPlan memberPlanObj2 = [select id, PlanId,Issue_State__c,MemberId from MemberPlan limit 1];
            Authsummary_LC_Hum.responseparse(res,memberPlanObj2.Id);
            
            list<AuthRefferal_Wrapper_LDTO_HUM> AuthResponseList  = new list<AuthRefferal_Wrapper_LDTO_HUM>();
            AuthRefferal_Wrapper_LDTO_HUM obwrapper = new AuthRefferal_Wrapper_LDTO_HUM();
            obwrapper.sAuthorizationOrReferralNumber ='1234';
            System.assertEquals('1234',obwrapper.sAuthorizationOrReferralNumber);
            
            obwrapper.sAuthorizationType = 'ALL';
            System.assertEquals('ALL',obwrapper.sAuthorizationType);
            
            obwrapper.sOverallStatus  = 'SS'; 
            System.assertEquals('SS',obwrapper.sOverallStatus);      
            
            obwrapper.sAdmFirstDay  = '22';
            System.assertEquals('22',obwrapper.sAdmFirstDay);
            
            obwrapper.sDischargeLastDay = '22';
            System.assertEquals('22',obwrapper.sDischargeLastDay);
            
            obwrapper.sServiceType = 'test';
            System.assertEquals('test',obwrapper.sServiceType);
            
            obwrapper.sTreatingProvider ='ets';
            System.assertEquals('ets',obwrapper.sTreatingProvider);
            
            obwrapper.sFacility = 'facility';
            System.assertEquals('facility',obwrapper.sFacility);
            
            obwrapper.sRequestingrovider = 'tetsrqstprv';
            System.assertEquals('tetsrqstprv',obwrapper.sRequestingrovider);
            
            obwrapper.sGroupId = '12345';
            System.assertEquals('12345',obwrapper.sGroupId);
            
            obwrapper.bIsAccessible  =  true;
            System.assertEquals(true, obwrapper.bIsAccessible);
            
            AuthResponseList.add(obwrapper);
            AuthRefferal_Wrapper_LDTO_HUM obwrappernegative = new AuthRefferal_Wrapper_LDTO_HUM();
            obwrappernegative .sAuthorizationOrReferralNumber ='1234';
            System.assertEquals('1234',obwrappernegative.sAuthorizationOrReferralNumber);
            
            obwrappernegative .sAuthorizationType = 'ALL';
            System.assertEquals('ALL',obwrappernegative.sAuthorizationType);
            
            obwrappernegative .sOverallStatus  = 'SS'; 
            System.assertEquals('SS',obwrappernegative.sOverallStatus);      
            
            obwrappernegative.sAdmFirstDay  = '22';
            System.assertEquals('22',obwrappernegative.sAdmFirstDay);
            
            obwrappernegative.bIsAccessible  =  false;
            System.assertEquals(false, obwrappernegative .bIsAccessible);
            AuthResponseList.add(obwrappernegative);
            
            Authsummary_LC_Hum.finalAuthsummarylist(AuthResponseList);
            Authsummary_LC_Hum.bshowresults =true;
            Authsummary_LC_Hum.lstAuthResponse=AuthResponseList;
            
            
            HttpResponse res1 = new HttpResponse();
            res1.setHeader('Content-Type', 'application/json');
            string sjson1  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
                '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"R0877001", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"R0877001", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
                '}, "TotalRecordsFound":"1001" } }';
            
            res1.setBody(sjson1);
            res1.setStatusCode(300); 
            Authsummary_LC_Hum.responseparse(res1,memberPlanObj2.Id);
            
            system.assertEquals(Authsummary_LC_Hum.bshowserviceError,true);
            
            HttpResponse res2 = new HttpResponse();
            res2.setHeader('Content-Type', 'application/json');
            string sjson2  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+']'+ '}, "TotalRecordsFound":"0" } }';
            
            res2.setBody(sjson2);
            res2.setStatusCode(200); 
            Authsummary_LC_Hum.responseparse(res2,memberPlanObj2.Id);
            //system.assertEquals(Authsummary_LC_Hum.bshowError,true);
            
            HttpResponse res3 = new HttpResponse();
            res3.setHeader('Content-Type', 'application/json');
            string sjson3  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
                ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
                ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
                '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
                '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
            
            res3.setBody(sjson3);
            res3.setStatusCode(200); 
            Authsummary_LC_Hum.SUserinputAuthId ='123456';
            
            MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c,MemberId from MemberPlan limit 1];
            Authsummary_LC_Hum.getMemberAccount(memberPlanObj.Id); 
            Authsummary_LC_Hum.getMemberAccount(''); 
            Authsummary_LC_Hum.initiaterequest('',1);
            HUM_Webservice_Callout__c obj1 = [select id from HUM_Webservice_Callout__c where name='Clinical_AuthDetail_V2'];
            delete obj1;
            HUM_Webservice_Callout__c obj_1 = HUM_Webservice_Callout__c.getInstance('Clinical_AuthSummary_V2');
            obj_1.name = 'New Data';
            update obj_1;
            Authsummary_LC_Hum.initiaterequest('99',1);
            Test.stopTest();
        }
    }
    static testMethod void searchAuthTest()
    {
        Test.startTest();
        Continuation conti = (Continuation)Authsummary_LC_Hum.searchAuth('6747836','iuwruyeuyfue32');
        Continuation objCon = new Continuation(120);
        objCon.continuationMethod='processGoButtonResponse';
        objCon.state='test';
        string labelValue= 'Continuation-1';
        HttpResponse oResponse = new HttpResponse();
        oResponse.setHeader('Content-Type', 'application/json');
        oResponse.setHeader('SOAPAction', '');    
        oResponse.setHeader('Connection','keep-alive');
        string sJson =  '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
            ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
            ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
            '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
            '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
        
        oResponse.setBody(sJson);
        oResponse.setStatusCode(200);
        System.debug('oResponse'+oResponse.getStatusCode());
        Test.setContinuationResponse(labelValue, oResponse);
        List<String> labels = new List<String>{labelValue};
            Authsummary_LC_Hum.processGoButtonResponse(labels, Authsummary_LC_Hum.continuationState);      
        Test.stopTest();
    }
    static testMethod void searchAuthTest_1()
    {
        Test.startTest();
        HUM_Webservice_Callout__c obj = HUM_Webservice_Callout__c.getInstance('Clinical_AuthSummary_V2');
        obj.name = 'New Data';
        update obj;
        Continuation conti = (Continuation)Authsummary_LC_Hum.searchAuth('6747836','iuwruyeuyfue32');
        Continuation objCon = new Continuation(120);
        objCon.continuationMethod='processGoButtonResponse';
        objCon.state='test';
        string labelValue= 'Continuation-1';
        HttpResponse oResponse = new HttpResponse();
        oResponse.setHeader('Content-Type', 'application/json');
        oResponse.setHeader('SOAPAction', '');    
        oResponse.setHeader('Connection','keep-alive');
        string sJson =  '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
            ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
            ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
            '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
            '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
        
        oResponse.setBody(sJson);
        oResponse.setStatusCode(200);
        System.debug('oResponse'+oResponse.getStatusCode());
        Test.setContinuationResponse(labelValue, oResponse);
        List<String> labels = new List<String>{labelValue};
            Authsummary_LC_Hum.processGoButtonResponse(labels, Authsummary_LC_Hum.continuationState);      
        Test.stopTest();
    }
    static testMethod void searchAuthTest_2()
    {
        Test.startTest();
        HUM_Webservice_Callout__c obj = HUM_Webservice_Callout__c.getInstance('Clinical_AuthSummary_V2');
        obj.Certificate_Name__c = '';
        update obj;
        Continuation conti = (Continuation)Authsummary_LC_Hum.searchAuth('6747836','iuwruyeuyfue32');
        Continuation objCon = new Continuation(120);
        objCon.continuationMethod='processGoButtonResponse';
        objCon.state='test';
        string labelValue= 'Continuation-1';
        HttpResponse oResponse = new HttpResponse();
        oResponse.setHeader('Content-Type', 'application/json');
        oResponse.setHeader('SOAPAction', '');    
        oResponse.setHeader('Connection','keep-alive');
        string sJson =  '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
            ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
            ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
            '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
            '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
        
        oResponse.setBody(sJson);
        oResponse.setStatusCode(300);
        System.debug('oResponse'+oResponse.getStatusCode());
        Test.setContinuationResponse(labelValue, oResponse);
        List<String> labels = new List<String>{labelValue};
            Authsummary_LC_Hum.processGoButtonResponse(labels, Authsummary_LC_Hum.continuationState);
        Continuation contin = (Continuation)Authsummary_LC_Hum.searchAuth('','iuwruyeuyfue32');
        Test.stopTest();
    }
    
    static testMethod void flowtemplate_test(){
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            Authsummary_LC_Hum.getFlowTemplate();
            MemberPlan objPLan = [Select Id,MemberId from MemberPlan limit 1];
            Authsummary_LC_Hum.getPlanDetails(objPLan.Id);
            Authsummary_LC_Hum.getPlanDetails('test');
            Authsummary_LC_Hum.getPlanDetails('');
            AuthReferral_SecurityHO_LH_HUM AS_hum =new AuthReferral_SecurityHO_LH_HUM();
            Authsummary_LC_Hum.authDetailscheckRequest('11111',objPLan.Id);
            Test.stopTest();
        }      
    }
    
    
}