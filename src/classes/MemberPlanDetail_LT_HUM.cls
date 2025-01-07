/*******************************************************************************************************************************
Apex Class Name : MemberPlanDetail_LT_HUM
Version         : 1.0
Created On      : 05/01/2021
Function        : Test class for MemberPlanDetail_LC_HUM
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0                        Kajal Namdev                                        05/01/2021                     Original Version 
* 2.0                        Kajal Namdev                                        05/19/2021                     Removed references of Policy__c object 
* 3.0                    	 Joel George                                         05/18/2021						PBE Service
* 4.0                        Kajal Namdev                                        06/09/2021                     Added few lines to cover vendor function
* 4.0                        Kajal Namdev                                        06/10/2021                     Rollback PBE
* 6.0                        Kajal Namdev                                        06/15/2021                     SIT Test CLass Fix
* 7.0                        Kajal Namdev                                        06/30/2021                     MBE plus service
* 8.0                        Kajal Namdev                                        09/07/2021                     Test coverage fix  
* 9.0 						Tharun Madishetti									 03/01/2024						Test Coverage Fix
******************************************************************************************************************************************************************************/

@isTest
public class MemberPlanDetail_LT_HUM {
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1',GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PHARMACYSPECIALIST_PROFILE_HUM);   
        insert objUser;
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_240_GBO_Segment_Service_Access' Limit 1];
        insert new PermissionSetAssignment(AssigneeId = objUser.id, PermissionSetId = ps.Id);
        system.runAs(objUser)
        {

            Pharmacy_Supervisor_HUM__c objPharSuper = new Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor' );
            insert objPharSuper;
            system.assertEquals(objPharSuper.name , 'CRMS_300_Humana_Pharmacy_Supervisor');
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new CRMFunctionality_ONOFF_Switch__c(Name = '1481853',IsON__c=true);
            insert csCRMOnOffSwitch;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch1 = new CRMFunctionality_ONOFF_Switch__c(Name = '1896703',IsON__c=false);
            insert csCRMOnOffSwitch1;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch2 = new CRMFunctionality_ONOFF_Switch__c(Name = '1114331',IsON__c=true);
            insert csCRMOnOffSwitch2;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch3 = new CRMFunctionality_ONOFF_Switch__c(Name = '1760349',IsON__c=true);
            insert csCRMOnOffSwitch3;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch4 = new CRMFunctionality_ONOFF_Switch__c(Name = '1789465',IsON__c=false);
            insert csCRMOnOffSwitch4;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch5 = new CRMFunctionality_ONOFF_Switch__c(Name = '2117860',IsON__c=true);
            insert csCRMOnOffSwitch5;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch6 = new CRMFunctionality_ONOFF_Switch__c(Name = '2262025',IsON__c=true);
            insert csCRMOnOffSwitch6;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch7 = new CRMFunctionality_ONOFF_Switch__c(Name = '1707499_Switch',IsON__c=false);
            insert csCRMOnOffSwitch7;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch8 = new CRMFunctionality_ONOFF_Switch__c(Name = '2364782',IsON__c=true);
            insert csCRMOnOffSwitch8;
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch9 = new CRMFunctionality_ONOFF_Switch__c(Name = '2104099',IsON__c=true);
            insert csCRMOnOffSwitch9;
            Ancillary_State_Code__c aSCode = new Ancillary_State_Code__c(Name = 'SC');
            insert aSCode;
           /* Ancillary_State_Code__c aSCodeOH = new Ancillary_State_Code__c(Name = 'OH');
            insert aSCodeOH;
            Ancillary_State_Code__c aSCodeOK = new Ancillary_State_Code__c(Name = 'OK');
            insert aSCodeOK; */
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = '1017531585';
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            
            Account AccountObj = HUMTestDataHelperTest.getAccount();
            AccountObj.FirstName = 'FirstName';
            AccountObj.LastName = 'LastName'; 
            AccountObj.source_platform_code__c = 'LV' ;
            AccountObj.Enterprise_Id__c = String.valueof(2000);
            AccountObj.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            AccountObj.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert AccountObj;
            system.assertEquals(AccountObj.LastName == 'LastName', true);
            
            
            //JOEL - Have added this code as I require ASO which is a formula field for PBE service to run
            Policy__c objPol = new Policy__c();
            objPol.ASO__c = 'Y';
            objPol.Group_Name__c =objMemAccount.Id;
            objPol.Issue_State__c = 'SC';
            insert objPol;
            
        
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c='MA',Sold_Product_Key_Value__c='FEHVNP55');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Product2 oProd1 = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MCD',Product_Type__c='MA',Sold_Product_Key_Value__c='FEHVNP55');
            insert oProd1;
            System.assertNotEquals(oProd1.Id, NULL, 'Value not matched');
            
            Account AccountObj1 = HUMTestDataHelperTest.getAccount();
            AccountObj1.FirstName = 'FirstName2';
            AccountObj1.LastName = 'LastName2'; 
            AccountObj1.source_platform_code__c = 'EM' ;
            AccountObj1.Enterprise_Id__c = String.valueof(2000);
            AccountObj1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            AccountObj1.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert AccountObj1;
            system.assertEquals(AccountObj1.LastName == 'LastName2', true);
            PurchaserPlan PolicyObjOI1 = new PurchaserPlan();
            PolicyObjOI1.Policy_Group_Number__c='11111';
            PolicyObjOI1.Name = 'Policy New';
            PolicyObjOI1.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObjOI1.PayerId = AccountObj1.Id;
            PolicyObjOI1.Major_LOB__c ='EM';
            PolicyObjOI1.Platform__c = 'EM';
            PolicyObjOI1.ETL_Record_Deleted__c = false;
            insert PolicyObjOI1;
            MemberPlan objMembplanOI1 = new MemberPlan();
            objMembplanOI1.Name='Name New';
            objMembplanOI1.MemberId=AccountObj1.id;
            objMembplanOI1.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI1.Member_Id_base__c = 'test23456';
            objMembplanOI1.GroupNumber = '672534';
            objMembplanOI1.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI1.EffectiveTo = Date.newInstance(1999,12, 31);
            objMembplanOI1.PlanId= PolicyObjOI1.Id;
            insert objMembplanOI1;

            PurchaserPlan oDentalPolicy = new PurchaserPlan();
            oDentalPolicy.Policy_Group_Number__c='11111';
            oDentalPolicy.Name = 'Dummy PurchaserPlan';
            oDentalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oDentalPolicy.PayerId = objMemAccount.Id;
            oDentalPolicy.Major_LOB__c ='DCM';
            oDentalPolicy.Product__c = oProd.Id;
            oDentalPolicy.Platform__c = 'LV';
            oDentalPolicy.ETL_Record_Deleted__c = false;
            oDentalPolicy.Policy__c = objPol.Id;
            insert oDentalPolicy;
            
            PurchaserPlan PolicyObj = new PurchaserPlan();
            PolicyObj.Policy_Group_Number__c='11111';
            PolicyObj.Name = 'Dental Product';
            PolicyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObj.PayerId = AccountObj.Id;
            PolicyObj.Major_LOB__c ='MCD';
            PolicyObj.ETL_Record_Deleted__c = false;
            PolicyObj.Product__c = oProd.Id;
            insert PolicyObj;
                        
            MemberPlan MemberPlanObj = new MemberPlan();
            MemberPlanObj.Name='MemberPlan2';
            MemberPlanObj.MemberId=AccountObj.id;
            MemberPlanObj.Member_Dependent_Code__c = '1234567890';
            MemberPlanObj.Member_Id_base__c = 'test23456';
            MemberPlanObj.GroupNumber = '737803';
            MemberPlanObj.PlanId= PolicyObj.Id;
            insert MemberPlanObj;
            
            MemberPlan objMemberplan = new MemberPlan();
            objMemberplan.Name='MemberPlan1';
            objMemberplan.MemberId=objMemAccount.id;
            objMemberPlan.Member_Dependent_Code__c = '1234567890';
            objMemberPlan.Member_Id_base__c = 'test23456';
            objMemberPlan.GroupNumber = '176366';
            objMemberPlan.PlanId= PolicyObj.Id;
            insert objMemberplan;
            
            MemberPlan objMembplan = new MemberPlan();
            objMembplan.Name='MemberPlanPBE';
            objMembplan.MemberId=objMemAccount.id;
            objMembplan.Member_Dependent_Code__c = 'H1234567890';
            objMembplan.Member_Id_base__c = 'test23456';
            objMembplan.Policy__c = objPol.Id;
            objMembplan.GroupNumber = '737803';
            objMembplan.PlanId= oDentalPolicy.Id;
            insert objMembplan;

            PurchaserPlan PolicyObjOI = new PurchaserPlan();
            PolicyObjOI.Policy_Group_Number__c='11111';
            PolicyObjOI.Name = 'Policy IO';
            PolicyObjOI.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObjOI.PayerId = AccountObj.Id;
            PolicyObjOI.Major_LOB__c ='EM';
            PolicyObjOI.Platform__c = 'EM';
            PolicyObjOI.ETL_Record_Deleted__c = false;
            PolicyObjOI.Product__c = oProd.Id;
            insert PolicyObjOI;
            
            MemberPlan objMembplanOI = new MemberPlan();
            objMembplanOI.Name='10674931401';
            objMembplanOI.MemberId=objMemAccount.id;
            objMembplanOI.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI.Member_Id_base__c = 'test23456';
            objMembplanOI.GroupNumber = '672534';
            objMembplanOI.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI.EffectiveTo = Date.newInstance(1999,12, 31);
            objMembplanOI.PlanId= PolicyObjOI.Id;
            insert objMembplanOI;
            
            HUM_Webservice_Callout__c PBEServiceDetails = New HUM_Webservice_Callout__c(Name = 'MBEPlusService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/PBEService_V2/GetProduct' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            HUM_Webservice_Callout__c PBEServiceGetEDIData = New HUM_Webservice_Callout__c(Name = 'GetEDIData',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/PBEService_V2/GetProduct' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert PBEServiceDetails;
            insert PBEServiceGetEDIData;
            HUM_Webservice_Callout__c PBEServiceEntityService = New HUM_Webservice_Callout__c(Name = 'GroupBusinessEntityService',Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/GBEServiceREST/GetGroupInfo' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert PBEServiceEntityService;   
            HUM_Webservice_Callout__c oHUMMoopService = new HUM_Webservice_Callout__c();
            oHUMMoopService.name='MBEPLUSServiceREST';
            oHUMMoopService.Active__c=true;
            oHUMMoopService.Certificate_Name__c='testts';
            oHUMMoopService.End_Point_URL__c='https://testst-cmservicews.hum.com';
            oHUMMoopService.Timeout__c='120000';
            oHUMMoopService.Content_Type__c='application/json';
            oHUMMoopService.Request_Type__c='POST';
            insert oHUMMoopService ;
            system.assert(oHUMMoopService.id!=null);
            system.assert(oHUMMoopService.name == 'MBEPLUSServiceREST');  
        }
        User objUser2 = HUMTestDataHelperTest.createTestUser('User2', 'Customer Care Specialist');
        insert objUser2;

    }
    
    /*
    * Method Name : testMbeplus
    * Description : This method is used to test the initiateMbeRequest and processMbeResponse methods
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testMbeplus()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        PurchaserPlan policy = [select id from PurchaserPlan where name = 'Dental Product'];
        policy.Issue_State__c  = 'KY';
        policy.Major_LOB__c = 'PD';
        update policy;
        MemberPlan memberPlanObj = [select id,Name,Issue_State__c,Medicaid_Id__c,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        memberPlanObj.PlanId = policy.Id;
        update memberPlanObj;
        HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            MedicaidPlanDetailsModel_LH_HUM medicaid = (MedicaidPlanDetailsModel_LH_HUM)MemberPlanDetail_LC_HUM.initiateMbeRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processMbeResponse(MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(200);
            String resBodyString = '<?xml version="1.0"?><Return SessionID="f7f86eee2ab94c60881597075c7bc9f6" Version="4.0.7457.41799" Duration="94" TimeStamp="11/22/2020 15:11:02:380" Environment="PROD" Server="LOUWEBWPL365S13"><PlatformMembers><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial/><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0000000088800213</PID><SrcPlatformCode>LV</SrcPlatformCode><Prefix/><Suffix/><MiddleName/><MaritalStatus>S</MaritalStatus><Smoker>U</Smoker><Language>ENG</Language><DeceasedDate>NONE</DeceasedDate><Family><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial/><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0000000088800213</PID><SrcPlatformCode>LV</SrcPlatformCode><Prefix/><Suffix/><MiddleName/><MaritalStatus>S</MaritalStatus><Smoker>U</Smoker><Language>ENG</Language><DeceasedDate>NONE</DeceasedDate><MemberContracts><MemberContract><ContractID>0000000000000000215504252</ContractID><EffectiveDate>01/01/2015</EffectiveDate><EndDate>12/31/2018</EndDate><Group><GroupID>290622</GroupID><GroupName>CARITEN HEALTH PLAN</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>MER</LineOfBusiness><Category>MED</Category><SrcPlatformCode>LV</SrcPlatformCode><CategoryDesciption>MEDICARE</CategoryDesciption><CoverageTier>01</CoverageTier><MemberCoverageEffectiveDate>01/01/2015</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2018</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2015</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2018</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode/><TermedReason>CB</TermedReason><Student/><Disabled/><MemberIDBase>H01029186</MemberIDBase><MemberIDDependentCode>00</MemberIDDependentCode><SubscriberIDDependentCode>001</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="CI Class" Value="000000000000"/><Property Key="CI Ben Seq" Value="00000001"/><Property Key="CI Medicare ID" Value="9U57J45JM73"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/><AlternatePayerDetails/></PlatformMemberCoverage></Coverages></MemberContract><MemberContract><ContractID>0000000000000000986650221</ContractID><EffectiveDate>01/01/2019</EffectiveDate><EndDate>12/31/2019</EndDate><Group><GroupID>310908</GroupID><GroupName>CARITEN HEALTH PLAN INC</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>MER</LineOfBusiness><Category>MED</Category><SrcPlatformCode>LV</SrcPlatformCode><CategoryDesciption>MEDICARE</CategoryDesciption><CoverageTier>01</CoverageTier><MemberCoverageEffectiveDate>01/01/2019</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2019</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2019</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2019</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode/><TermedReason>107</TermedReason><Student/><Disabled/><MemberIDBase>H01029186</MemberIDBase><MemberIDDependentCode>00</MemberIDDependentCode><SubscriberIDDependentCode>001</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="CI Class" Value="000000000000"/><Property Key="CI Ben Seq" Value="00000001"/><Property Key="CI Medicare ID" Value="9U57J45JM73"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/><AlternatePayerDetails/></PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses><EMailAddress><Address>BENSR114@GMAIL.COM</Address><Type>HOM</Type><Primary>N</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3187713926</Number><Extension/><Type>HOM</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember></Family><EMailAddresses><EMailAddress><Address>BENSR114@GMAIL.COM</Address><Type>HOM</Type><Primary>N</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3187713926</Number><Extension/><Type>HOM</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial>V</MiddleInitial><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0254421123031729</PID><SrcPlatformCode>EM</SrcPlatformCode><Prefix/><Suffix/><MiddleName>V</MiddleName><MaritalStatus>I</MaritalStatus><Smoker>U</Smoker><Language>E</Language><DeceasedDate>NONE</DeceasedDate><Family><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial>V</MiddleInitial><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0254421123031729</PID><SrcPlatformCode>EM</SrcPlatformCode><Prefix/><Suffix/><MiddleName>V</MiddleName><MaritalStatus>I</MaritalStatus><Smoker>U</Smoker><Language>E</Language><DeceasedDate>NONE</DeceasedDate><MemberContracts><MemberContract><ContractID>005024810</ContractID><EffectiveDate>01/01/2015</EffectiveDate><EndDate>12/31/2018</EndDate><Group><GroupID>675717</GroupID><GroupName>MEDICARE DENTAL</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>DMDR</LineOfBusiness><Category>DEN</Category><SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>DENTAL</CategoryDesciption><CoverageTier>EMP</CoverageTier><MemberCoverageEffectiveDate>01/01/2018</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2018</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2015</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2018</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode>SUB</RelationCode><TermedReason>MR</TermedReason><Student>N</Student><Disabled>N</Disabled><MemberIDBase>005024810</MemberIDBase><MemberIDDependentCode>01</MemberIDDependentCode><SubscriberIDDependentCode>01</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="MTV Rate Pkg ID" Value="OF04"/><Property Key="Network ID" Value="HDMEDICARE"/><Property Key="Coverage Election Amount" Value="0.00"/><Property Key="Coverage Election Effective Date" Value="NONE"/><Property Key="Coverage Election End Date" Value="NONE"/><Property Key="MTV Division" Value="67571733"/><Property Key="MTV Ben Pkg ID" Value="MC3756CH"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/></PlatformMemberCoverage></Coverages></MemberContract><MemberContract><ContractID>005024810</ContractID><EffectiveDate>01/01/2019</EffectiveDate><EndDate>12/31/2019</EndDate><Group><GroupID>675717</GroupID><GroupName>MEDICARE DENTAL</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>DMDR</LineOfBusiness><Category>DEN</Category><SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>DENTAL</CategoryDesciption><CoverageTier>EMP</CoverageTier><MemberCoverageEffectiveDate>01/01/2019</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2019</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2019</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2019</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode>SUB</RelationCode><TermedReason>MR</TermedReason><Student>N</Student><Disabled>N</Disabled><MemberIDBase>005024810</MemberIDBase><MemberIDDependentCode>01</MemberIDDependentCode><SubscriberIDDependentCode>01</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="MTV Rate Pkg ID" Value="FO0P"/><Property Key="Network ID" Value="HDMEDICARE"/><Property Key="Coverage Election Amount" Value="0.00"/><Property Key="Coverage Election Effective Date" Value="NONE"/><Property Key="Coverage Election End Date" Value="NONE"/><Property Key="MTV Division" Value="67571733"/><Property Key="MTV Ben Pkg ID" Value="MC375704"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/></PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses><EMailAddress><Address>bensr114@gmail.com</Address><Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3185240075</Number><Extension>00000</Extension><Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember></Family><EMailAddresses><EMailAddress><Address>bensr114@gmail.com</Address><Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3185240075</Number><Extension>00000</Extension><Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember></PlatformMembers></Return>';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            Map<String,String> stateMap= new Map<String,String>();
            stateMap.put('sRequest',reqType);
            stateMap.put('state',memberPlanObj.Issue_State__c);
            stateMap.put('medicaidId',memberPlanObj.Medicaid_Id__c);
            MemberPlanDetail_LC_HUM.processMbeResponse(stateMap);
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{ "GetMemberResponse":{ "Header":{ "ResponseHeader":{ "Environment":"Int", "Version":"2.0", "Server":{ "Name":"louigwii06s01" }, "Metrics":{ "Duration":"151", "RequestTimeStamp":"2021-05-05T06:41:37.466Z", "ResponseTimeStamp":"2021-05-05T06:41:37.617Z" }, "ResponseMetaData":{ "Status":"Success", "ClientSessionID":"GUID", "SessionID":"641bc694-3796-451e-bfae-587d95ff4866", "DataAuthorizationConstraintList":{ "DataAuthorizationConstraint":[ { "ConstraintType":"Include", "Tenant":"1" }, { "ConstraintType":"Exclude", "Tenant":"1", "Source":"5" }, { "ConstraintType":"Include", "Tenant":"1", "Source":"16" } ] } } } }, "Body":{ "Member":{ "DateOfBirth":"02/01/2000", "FirstName":"RUTHALIA", "Gender":"F", "LastName":"BEACHEL", "Ssn":"133273473", "Contact":{ "ContactProfile":{ "EmailList":{ "PersonEmail":[  ] }, "PhoneList":{ "PersonPhone":[  ] }, "AddressList":{ "PersonAddress":[  ] } } },"RaceList":{"Race":[{"Code":"123456", "Description":"Code Description 123456"}]}, "MasterId":"1006913039" }, "MessageList":{ "Message":[ { "Type":"Information", "Code":"OSI001", "Source":"OSE Services", "Description":"Member Details returned successfully." } ] } } } }';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            MemberPlanDetail_LC_HUM.stateMap.put('state','KY');
            MemberPlanDetail_LC_HUM.mbeResponseparse(oResponse,MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse oRes = New HttpResponse();
            oRes.setHeader('Content-Type', 'application/json');
            oRes.setHeader('SOAPAction', '');    
            oRes.setHeader('Connection','keep-alive');
            String response1 = '{"GetMemberResponse": {   "Header": {"ResponseHeader":    {      "Environment": "Dev",      "Version": "2.0",      "Server": {"Name": "SIMIGWDI05"},      "Metrics":       {         "Duration": "145",         "RequestTimeStamp": "2020-09-08T11:24:12.723Z",         "ResponseTimeStamp": "2020-09-08T11:24:12.868Z"      },      "ResponseMetaData":       {         "Status": "Warning",         "ClientSessionID": "GUID",         "SessionID": "c79aafb5-b615-4add-803a-63a92fddde66",         "DataAuthorizationConstraintList": {"DataAuthorizationConstraint":          [                        {               "ConstraintType": "Exclude",               "Tenant": "1",               "Source": "5"            },                        {               "ConstraintType": "Include",               "Tenant": "1"            },                        {               "ConstraintType": "Include",               "Tenant": "1",               "Source": "16"            }         ]}      }   }},   "Body":    {      "Member":       {         "DateOfBirth": "09/15/1940",         "FirstName": "LUSELA",         "Gender": "M",         "LastName": "BROWNELL",         "Ssn": "581177602",         "Contact": {"ContactProfile":          {            "EmailList": {"PersonEmail": [            {               "Address": "T0037004240917@HUMANATEST.COM",               "Type": "Primary"            }]},            "PhoneList": {"PersonPhone": [            {               "PhoneNumber": "6544390720",               "Type": "Home"            }]},            "AddressList": {"PersonAddress": [            {               "AddressLine1": "8561 SUMNER STREET",               "City": "CHICAGO",               "StateCode": "IL",               "County": "COOK",               "ZipCode": "60618",               "ZipPlus": "1166",               "Type": "Home",               "GeoLocation": {"Position":                {                  "Coordinates":                   {                     "Latitude": "84.047100",                     "Longitude": "-28.581800"                  },                  "AcquiredTime": ""               }},               "CountyCode": "031"            }]}         }},         "RaceList": "\n\nR4.01.002\nSAMOAN",         "EthnicityList": {"Ethnicity": [         {            "Code": "G",            "Description": "HISPANIC OR LATINO SPANISH"         }]},         "LanguageList": "\n\nENG\nEnglish\nSpoken\n\nENG\nEnglish\nWritten",         "MedicaidIdentifierList": {"MedicaidIdentifier": [         {            "MedicaidId": "114315096",            "StateCode": "IL"         }]},         "MedicareIdentifierList": {"MedicareIdentifier": [{"MedicareId": "8Q10R47EF24"}]},         "MasterId": "1008647672",         "LongTermCareEligibilityList": {"LongTermCareEligibility": [         {            "EffectiveDate": "02/01/2019",            "EndDate": "10/31/2019",            "PlanTypeCode": "DEMO",            "ProgramCategoryCode": "A0",            "StateCode": "IL"         }]},         "LowIncomeSubsidyLevelList": {"LowIncomeSubsidyLevel":          [                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2021",               "EndDate": "12/31/2021",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS3",               "EffectiveDate": "03/01/2019",               "EndDate": "12/31/2019",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2013",               "EndDate": "12/31/2013",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2019",               "EndDate": "02/28/2019",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2012",               "EndDate": "12/31/2012",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2015",               "EndDate": "12/31/2015",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2007",               "EndDate": "12/31/2007",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "06/01/2014",               "EndDate": "12/31/2014",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2018",               "EndDate": "12/31/2018",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2014",               "EndDate": "05/31/2014",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2010",               "EndDate": "12/31/2010",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2017",               "EndDate": "12/31/2017",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "04/01/2006",               "EndDate": "12/31/2006",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2009",               "EndDate": "12/31/2009",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS3",               "EffectiveDate": "01/01/2020",               "EndDate": "12/31/2020",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2011",               "EndDate": "12/31/2011",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2016",               "EndDate": "12/31/2016",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2008",               "EndDate": "12/31/2008",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2006",               "EndDate": "03/31/2006",               "Percent": "100"            }         ]},         "PlatformPointerList": {"PlatformProxyKey":          [                        {               "Application": "",               "KeyType": "PERSONID",               "KeyValue": "364195843",               "Source": "LV"            },                        {               "Application": "",               "KeyType": "PERSONID",               "KeyValue": "0631432423285108",               "Source": "EM"            },                        {               "Application": "",               "KeyType": "ODS_GENKEY",               "KeyValue": "0037004240917",               "Source": "ODS"            },                        {               "Application": "",               "KeyType": "NIEXTERNALSYSTEMENTITYID",               "KeyValue": "2532497316",               "Source": "HV"            }         ]},         "SoldProductList": {"SoldProduct":          [            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "7228910206",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "10/31/2019",                  "StartDate": "02/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459006000||7228910206|2019-01-01|false|||2019-02-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459006000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2021",               "ProductEndDate": "12/31/2021",               "ProductId": "2512690201",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||2512690201|2021-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "3010020106",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "10/31/2019",                  "StartDate": "02/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461006000||3010020106|2019-01-01|false|||2019-02-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461006000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "3010020102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||3010020102|2019-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2020",               "ProductEndDate": "12/31/2020",               "ProductId": "9064930102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||9064930102|2020-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "7228910202",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||7228910202|2019-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2017",               "ProductEndDate": "12/31/2017",               "ProductId": "0252770108",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "01/31/2019",                  "StartDate": "06/01/2014",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||0252770108|2017-01-01|false|||2014-06-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642824",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2020",               "ProductEndDate": "12/31/2020",               "ProductId": "8615660102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||8615660102|2020-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2021",               "ProductEndDate": "12/31/2021",               "ProductId": "2140440401",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||2140440401|2021-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2017",               "ProductEndDate": "12/31/2017",               "ProductId": "2372530201",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "01/31/2019",                  "StartDate": "06/01/2014",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||2372530201|2017-01-01|false|||2014-06-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643824",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }}         ]},         "IndicatorList": {"Indicator":          [                        {               "Name": "Retire",               "Value": "False"            },                        {               "Name": "HumanaEmployee",               "Value": "False"            },                        {               "Name": "Smoke",               "Value": "False"            },                        {               "Name": "Tenant",               "Value": "3"            },                        {               "Name": "MedicareEnrolled",               "Value": "True"            }         ]}      },      "MessageList": {"Message":       [                  {            "Type": "Warning",            "Code": "MBEW0002",            "Source": "OSE Services",            "Description": "Unable to retrieve membership details for one of the platform."         },                  {            "Type": "Warning",            "Code": "MBEW0003",            "Source": "OSE Services",            "Description": "No coverage found for one of the platforms." } ]}}}}';
            oRes.setBody(response1);
            oRes.setStatusCode(200); 
            MemberPlanDetail_LC_HUM.mbeResponseparse(oRes,MemberPlanDetail_LC_HUM.stateMap);
            oRes.setStatusCode(400); 
            MemberPlanDetail_LC_HUM.mbeResponseparse(oRes,MemberPlanDetail_LC_HUM.stateMap);
            Test.stopTest();
        }
    }
    
        /*
    * Method Name : testMemberAncillaryKY
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberAncillaryKY()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        PurchaserPlan policy = [select id from PurchaserPlan where name = 'Dental Product'];
        policy.Issue_State__c  = 'KY';
        
        update policy;
        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c from MemberPlan where Name = 'MemberPlan1' limit 1];
        memberPlanObj.PlanId = policy.Id;
        update memberPlanObj;
        HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetMemberAncillary_V2',
                                                                                      End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberAncillary;
        system.assert(oGetMemberAncillary.id!=null);
        system.assert(oGetMemberAncillary.name == 'GetMemberAncillary_V2');
        System.runAs(testUser)
        {
            Test.startTest();
            MedicaidPlanDetailsModel_LH_HUM continueObj1 = (MedicaidPlanDetailsModel_LH_HUM)MemberPlanDetail_LC_HUM.initiateMbeRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processMbeResponse(MemberPlanDetail_LC_HUM.stateMap);
            MedicaidPlanDetailsModel_LH_HUM continueObj = (MedicaidPlanDetailsModel_LH_HUM)MemberPlanDetail_LC_HUM.initiateAncillaryRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processAncillaryResponse(MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(200);
            String resBodyString = '<?xml version="1.0"?><Return SessionID="f7f86eee2ab94c60881597075c7bc9f6" Version="4.0.7457.41799" Duration="94" TimeStamp="11/22/2020 15:11:02:380" Environment="PROD" Server="LOUWEBWPL365S13"><PlatformMembers><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial/><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0000000088800213</PID><SrcPlatformCode>LV</SrcPlatformCode><Prefix/><Suffix/><MiddleName/><MaritalStatus>S</MaritalStatus><Smoker>U</Smoker><Language>ENG</Language><DeceasedDate>NONE</DeceasedDate><Family><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial/><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0000000088800213</PID><SrcPlatformCode>LV</SrcPlatformCode><Prefix/><Suffix/><MiddleName/><MaritalStatus>S</MaritalStatus><Smoker>U</Smoker><Language>ENG</Language><DeceasedDate>NONE</DeceasedDate><MemberContracts><MemberContract><ContractID>0000000000000000215504252</ContractID><EffectiveDate>01/01/2015</EffectiveDate><EndDate>12/31/2018</EndDate><Group><GroupID>290622</GroupID><GroupName>CARITEN HEALTH PLAN</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>MER</LineOfBusiness><Category>MED</Category><SrcPlatformCode>LV</SrcPlatformCode><CategoryDesciption>MEDICARE</CategoryDesciption><CoverageTier>01</CoverageTier><MemberCoverageEffectiveDate>01/01/2015</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2018</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2015</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2018</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode/><TermedReason>CB</TermedReason><Student/><Disabled/><MemberIDBase>H01029186</MemberIDBase><MemberIDDependentCode>00</MemberIDDependentCode><SubscriberIDDependentCode>001</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="CI Class" Value="000000000000"/><Property Key="CI Ben Seq" Value="00000001"/><Property Key="CI Medicare ID" Value="9U57J45JM73"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/><AlternatePayerDetails/></PlatformMemberCoverage></Coverages></MemberContract><MemberContract><ContractID>0000000000000000986650221</ContractID><EffectiveDate>01/01/2019</EffectiveDate><EndDate>12/31/2019</EndDate><Group><GroupID>310908</GroupID><GroupName>CARITEN HEALTH PLAN INC</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>MER</LineOfBusiness><Category>MED</Category><SrcPlatformCode>LV</SrcPlatformCode><CategoryDesciption>MEDICARE</CategoryDesciption><CoverageTier>01</CoverageTier><MemberCoverageEffectiveDate>01/01/2019</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2019</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2019</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2019</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode/><TermedReason>107</TermedReason><Student/><Disabled/><MemberIDBase>H01029186</MemberIDBase><MemberIDDependentCode>00</MemberIDDependentCode><SubscriberIDDependentCode>001</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="CI Class" Value="000000000000"/><Property Key="CI Ben Seq" Value="00000001"/><Property Key="CI Medicare ID" Value="9U57J45JM73"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/><AlternatePayerDetails/></PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses><EMailAddress><Address>BENSR114@GMAIL.COM</Address><Type>HOM</Type><Primary>N</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3187713926</Number><Extension/><Type>HOM</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember></Family><EMailAddresses><EMailAddress><Address>BENSR114@GMAIL.COM</Address><Type>HOM</Type><Primary>N</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3187713926</Number><Extension/><Type>HOM</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial>V</MiddleInitial><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0254421123031729</PID><SrcPlatformCode>EM</SrcPlatformCode><Prefix/><Suffix/><MiddleName>V</MiddleName><MaritalStatus>I</MaritalStatus><Smoker>U</Smoker><Language>E</Language><DeceasedDate>NONE</DeceasedDate><Family><PlatformMember><FirstName>SHEILA</FirstName><MiddleInitial>V</MiddleInitial><LastName>VENKOBA RAO</LastName><DateOfBirth>03/14/1943</DateOfBirth><Gender>F</Gender><SSN>274965160</SSN><Age>77</Age><Address><Line1>11804 RIDGELAND DR</Line1><Line2/><Line3/><City>KNOXVILLE</City><County>093</County><State>TN</State><Zip>37932</Zip><ZipPlus>2835</ZipPlus></Address><HomeAddress><Line1/><Line2/><Line3/><City/><County/><State/><Zip/><ZipPlus/></HomeAddress><PID>0254421123031729</PID><SrcPlatformCode>EM</SrcPlatformCode><Prefix/><Suffix/><MiddleName>V</MiddleName><MaritalStatus>I</MaritalStatus><Smoker>U</Smoker><Language>E</Language><DeceasedDate>NONE</DeceasedDate><MemberContracts><MemberContract><ContractID>005024810</ContractID><EffectiveDate>01/01/2015</EffectiveDate><EndDate>12/31/2018</EndDate><Group><GroupID>675717</GroupID><GroupName>MEDICARE DENTAL</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>DMDR</LineOfBusiness><Category>DEN</Category><SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>DENTAL</CategoryDesciption><CoverageTier>EMP</CoverageTier><MemberCoverageEffectiveDate>01/01/2018</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2018</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2015</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2018</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode>SUB</RelationCode><TermedReason>MR</TermedReason><Student>N</Student><Disabled>N</Disabled><MemberIDBase>005024810</MemberIDBase><MemberIDDependentCode>01</MemberIDDependentCode><SubscriberIDDependentCode>01</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="MTV Rate Pkg ID" Value="OF04"/><Property Key="Network ID" Value="HDMEDICARE"/><Property Key="Coverage Election Amount" Value="0.00"/><Property Key="Coverage Election Effective Date" Value="NONE"/><Property Key="Coverage Election End Date" Value="NONE"/><Property Key="MTV Division" Value="67571733"/><Property Key="MTV Ben Pkg ID" Value="MC3756CH"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/></PlatformMemberCoverage></Coverages></MemberContract><MemberContract><ContractID>005024810</ContractID><EffectiveDate>01/01/2019</EffectiveDate><EndDate>12/31/2019</EndDate><Group><GroupID>675717</GroupID><GroupName>MEDICARE DENTAL</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>DMDR</LineOfBusiness><Category>DEN</Category><SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>DENTAL</CategoryDesciption><CoverageTier>EMP</CoverageTier><MemberCoverageEffectiveDate>01/01/2019</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2019</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>01/01/2019</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2019</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Past</CoveragePeriod><RelationCode>SUB</RelationCode><TermedReason>MR</TermedReason><Student>N</Student><Disabled>N</Disabled><MemberIDBase>005024810</MemberIDBase><MemberIDDependentCode>01</MemberIDDependentCode><SubscriberIDDependentCode>01</SubscriberIDDependentCode><Properties><Property Key="OBGYN Name" Value=""/><Property Key="OBGYN Current Patient" Value="N"/><Property Key="Election Amount" Value="0.00"/><Property Key="MTV Rate Pkg ID" Value="FO0P"/><Property Key="Network ID" Value="HDMEDICARE"/><Property Key="Coverage Election Amount" Value="0.00"/><Property Key="Coverage Election Effective Date" Value="NONE"/><Property Key="Coverage Election End Date" Value="NONE"/><Property Key="MTV Division" Value="67571733"/><Property Key="MTV Ben Pkg ID" Value="MC375704"/></Properties><HSAContributions/><TobaccoUser/><EdeliveryIndicator/></PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses><EMailAddress><Address>bensr114@gmail.com</Address><Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3185240075</Number><Extension>00000</Extension><Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember></Family><EMailAddresses><EMailAddress><Address>bensr114@gmail.com</Address><Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>3185240075</Number><Extension>00000</Extension><Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember></PlatformMembers></Return>';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            MemberPlanDetail_LC_HUM.processAncillaryResponse(MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{"AncillaryEntityList":[{"MaintenanceReason":"MaintenanceReason","HandicapIndicator":"Y","CoverageEndDate":"2021-05-30","ChishlmCase":{"startDate":"2021-05-16","endDate":"2021-06-16","manager":"Louxi52di01"},"CapitationCode":"CapitationCode","ChoiceCode":"ChoiceCode","HighRiskPregancyCode":"HighRiskPregancyCode","BehavioralHealthCode":"BehavioralHealthCode","ClosureCode":"ClosureCode","RenewalDate":"05/16/2021 3:41:19 PM","RenewalCode":"RenewalCode","MultipleBirthIndicator":"MultipleBirthIndicator","MedicalAssistanceCategoryCode":"MedicalAssistanceCategoryCode","CoordinatedSystemOfCare":{"admitDate":"2021-05-16","dischargeDate":"2021-05-18"},"NewBornMothersReferenceId":"NewBornMothersReferenceId","CaseTypeCode":"CaseTypeCode","DepartmentOfCorrection":{"type":"type","startDate":"2023-05-17","endDate":"2021-05-16","preReleaseDate":"2020-05-16","locationIndicator":"Y"},"HeadOfHousehold":{"FirstName":"FirstName","LastName":"LastName","postalAddress":{"addressLine1":"addressLine1","addressLine2":"addressLine2","city":"LA","zip":"12345","state":"state"}},"LTSS":{"specialCond":"specialCond","prodPlanId":"prodPlanId","levelOfCareDate":"levelOfCareDate","levelOfCareCode":"levelOfCareCode","specialNeedsNote":"specialNeedsNote","medicaidAidCategoryCode":"medicaidAidCategoryCode","medicaidBeginDate":"2020-05-16","medicaidEndDate":"2020-05-16","ACWM":"ACWM","otherHealthIndicators":"otherHealthIndicators","ltcProgramRecommendation":"ltcProgramRecommendation","providerCode":"providerCode"}}]}}';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            MemberPlanDetail_LC_HUM.ancillaryResponseParse(oResponse,MemberPlanDetail_LC_HUM.stateMap);
            system.assert(MemberPlanDetail_LC_HUM.AncillaryInfo!=null,true);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testMemberAncillaryLA
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberAncillaryLA()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        PurchaserPlan policy = [select id from PurchaserPlan where name = 'Dental Product'];
        policy.Issue_State__c  = 'LA';
        update policy;
        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c from MemberPlan where Name = 'MemberPlan1' limit 1];
        memberPlanObj.PlanId = policy.Id;
        update memberPlanObj;
        HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetMemberAncillary_V2',
                                                                                      End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberAncillary;
        system.assert(oGetMemberAncillary.id!=null);
        system.assert(oGetMemberAncillary.name == 'GetMemberAncillary_V2');
        System.runAs(testUser)
        {
            Test.startTest();
            MedicaidPlanDetailsModel_LH_HUM med = (MedicaidPlanDetailsModel_LH_HUM)MemberPlanDetail_LC_HUM.initiateMbeRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processMbeResponse(MemberPlanDetail_LC_HUM.stateMap);
            MedicaidPlanDetailsModel_LH_HUM  continueObj = (MedicaidPlanDetailsModel_LH_HUM )MemberPlanDetail_LC_HUM.initiateAncillaryRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processAncillaryResponse(MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{ "AncillaryEntityList":[ { "MaintenanceReason":"MaintenanceReason", "HandicapIndicator":"Y", "CoverageEndDate":"2021-05-30", "ChishlmCase":{ "startDate":"2021-05-16", "endDate":"2021-06-16", "manager":"Louxi52di01" }, "CapitationCode":"CapitationCode", "ChoiceCode":"ChoiceCode", "HighRiskPregancyCode":"HighRiskPregancyCode", "BehavioralHealthCode":"BehavioralHealthCode", "ClosureCode":"ClosureCode", "RenewalDate":"05/16/2021 3:41:19 PM", "RenewalCode":"RenewalCode", "MultipleBirthIndicator":"MultipleBirthIndicator", "MedicalAssistanceCategoryCode":"MedicalAssistanceCategoryCode", "CoordinatedSystemOfCare":{ "admitDate":"2021-05-16", "dischargeDate":"2021-05-18" }, "NewBornMothersReferenceId":"NewBornMothersReferenceId", "CaseTypeCode":"CaseTypeCode", "DepartmentOfCorrection":{ "type":"type", "startDate":"2023-05-17", "endDate":"2021-05-16", "preReleaseDate":"2020-05-16", "locationIndicator":"Y" } } ] } }';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            MemberPlanDetail_LC_HUM.ancillaryResponseParse(oResponse,MemberPlanDetail_LC_HUM.stateMap);
            system.assert(MemberPlanDetail_LC_HUM.AncillaryInfo!=null,true);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testMemberAncillarySC
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberAncillarySC()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        PurchaserPlan policy = [select id from PurchaserPlan where name = 'Dental Product'];
        policy.Issue_State__c  = 'SC';
        update policy;
        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c from MemberPlan where Name = 'MemberPlan1' limit 1];
        memberPlanObj.PlanId = policy.Id;
        update memberPlanObj;
        HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetMemberAncillary_V2',
                                                                                      End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberAncillary;
        system.assert(oGetMemberAncillary.id!=null);
        system.assert(oGetMemberAncillary.name == 'GetMemberAncillary_V2');
        System.runAs(testUser)
        {
            Test.startTest();
            Object medic = (object)MemberPlanDetail_LC_HUM.initiateMbeRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processMbeResponse(MemberPlanDetail_LC_HUM.stateMap);
            object continueObj = (object)MemberPlanDetail_LC_HUM.initiateAncillaryRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processAncillaryResponse(MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{ "AncillaryEntityList":[ { "MaintenanceReason":"MaintenanceReason", "HandicapIndicator":"Y", "CoverageEndDate":"2021-05-30", "ChishlmCase":{ "startDate":"2021-05-16", "endDate":"2021-06-16", "manager":"Louxi52di01" }, "CapitationCode":"CapitationCode", "ChoiceCode":"ChoiceCode", "HighRiskPregancyCode":"HighRiskPregancyCode", "BehavioralHealthCode":"BehavioralHealthCode", "ClosureCode":"ClosureCode", "RenewalDate":"05/16/2021 3:41:19 PM", "RenewalCode":"RenewalCode", "MultipleBirthIndicator":"MultipleBirthIndicator", "MedicalAssistanceCategoryCode":"MedicalAssistanceCategoryCode", "CoordinatedSystemOfCare":{ "admitDate":"2021-05-16", "dischargeDate":"2021-05-18" }, "NewBornMothersReferenceId":"NewBornMothersReferenceId", "CaseTypeCode":"CaseTypeCode", "DepartmentOfCorrection":{ "type":"type", "startDate":"2023-05-17", "endDate":"2021-05-16", "preReleaseDate":"2020-05-16", "locationIndicator":"Y" } } ] } }';
            oResponse.setBody(response);
            oResponse.setStatusCode(404); 
            System.assert(oResponse!=null);
            MemberPlanDetail_LC_HUM.ancillaryResponseParse(oResponse,MemberPlanDetail_LC_HUM.stateMap);
            MemberPlanDetail_LD_HUM.getMemberName(memberPlanObj.Id);
            system.assert(MemberPlanDetail_LC_HUM.AncillaryInfo!=null,true);
            MemberPlanDetail_LC_HUM.getAdditionalMemberPlan(memberPlanObj.Id);
            Test.stopTest();
        }
    }
    
     /*
    * Method Name : testMemberAncillaryOH
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberAncillaryOH()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        PurchaserPlan policy = [select id from PurchaserPlan where name = 'Dental Product'];
        policy.Issue_State__c  = 'OH';
        update policy;
        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c from MemberPlan where Name = 'MemberPlan1' limit 1];
        memberPlanObj.PlanId = policy.Id;
        update memberPlanObj;
        HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetMemberAncillary_V2',
                                                                                      End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberAncillary;
        system.assert(oGetMemberAncillary.id!=null);
        system.assert(oGetMemberAncillary.name == 'GetMemberAncillary_V2');
        System.runAs(testUser)
        {
            Test.startTest();
            Object medic = (object)MemberPlanDetail_LC_HUM.initiateMbeRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processMbeResponse(MemberPlanDetail_LC_HUM.stateMap);
            object continueObj = (object)MemberPlanDetail_LC_HUM.initiateAncillaryRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processAncillaryResponse(MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{ "AncillaryEntityList":[ { "MaintenanceReason":"MaintenanceReason", "HandicapIndicator":"Y", "CoverageEndDate":"2021-05-30", "ChishlmCase":{ "startDate":"2021-05-16", "endDate":"2021-06-16", "manager":"Louxi52di01" }, "CapitationCode":"CapitationCode", "ChoiceCode":"ChoiceCode", "HighRiskPregancyCode":"HighRiskPregancyCode", "BehavioralHealthCode":"BehavioralHealthCode", "ClosureCode":"ClosureCode", "RenewalDate":"05/16/2021 3:41:19 PM", "RenewalCode":"RenewalCode", "MultipleBirthIndicator":"MultipleBirthIndicator", "MedicalAssistanceCategoryCode":"MedicalAssistanceCategoryCode", "CoordinatedSystemOfCare":{ "admitDate":"2021-05-16", "dischargeDate":"2021-05-18" }, "NewBornMothersReferenceId":"NewBornMothersReferenceId", "CaseTypeCode":"CaseTypeCode", "DepartmentOfCorrection":{ "type":"type", "startDate":"2023-05-17", "endDate":"2021-05-16", "preReleaseDate":"2020-05-16", "locationIndicator":"Y" } } ] } }';
            oResponse.setBody(response);
            oResponse.setStatusCode(404); 
            System.assert(oResponse!=null);
            MemberPlanDetail_LC_HUM.ancillaryResponseParse(oResponse,MemberPlanDetail_LC_HUM.stateMap);
            MemberPlanDetail_LD_HUM.getMemberName(memberPlanObj.Id);
            system.assert(MemberPlanDetail_LC_HUM.AncillaryInfo!=null,true);
            MemberPlanDetail_LC_HUM.getAdditionalMemberPlan(memberPlanObj.Id);
            Test.stopTest();
        }
    }
        
     /*
    * Method Name : testMemberAncillaryOK
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberAncillaryOK()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        PurchaserPlan policy = [select id from PurchaserPlan where name = 'Dental Product'];
        policy.Issue_State__c  = 'OK';
        update policy;
        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c from MemberPlan where Name = 'MemberPlan1' limit 1];
        memberPlanObj.PlanId = policy.Id;
        update memberPlanObj;
        HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetMemberAncillary_V2',
                                                                                      End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberAncillary;
        system.assert(oGetMemberAncillary.id!=null);
        system.assert(oGetMemberAncillary.name == 'GetMemberAncillary_V2');
        System.runAs(testUser)
        {
            Test.startTest();
            Object medic = (object)MemberPlanDetail_LC_HUM.initiateMbeRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processMbeResponse(MemberPlanDetail_LC_HUM.stateMap);
            object continueObj = (object)MemberPlanDetail_LC_HUM.initiateAncillaryRequest(memberPlanObj.Id);
            MemberPlanDetail_LC_HUM.processAncillaryResponse(MemberPlanDetail_LC_HUM.stateMap);
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{ "AncillaryEntityList": [{"PregnancyInfo":{"pregnancyInd": "Test","pregnancyDueDate": "2024-08-21"},"CaseHead":{"firstName": "ANNAUAT","lastName": "JONESRSO"},"AIANIndicator": "N","RenewalDate": "01/31/2025","CustodyDetails":{"code": "code","description": "desc"},"CoverageEndDate": "date","PenaltyReasonIndicator":{"penaltyValue": "Value","penaltyStartDate": "Date","penaltyEndDate":"date"},"Premium":{"premiumValue": "Value","premiumStartDate": "Date","premiumEndDate":"date"},"ParentInfoIdentifier":{"MotherMedicaidID":"medicadId"},"RiseDetails":{"BeginDate": "Value","EndDate": "Date","indicator":"date"}}]} }';
            oResponse.setBody(response);
            oResponse.setStatusCode(404); 
            System.assert(oResponse!=null);
            MemberPlanDetail_LC_HUM.ancillaryResponseParse(oResponse,MemberPlanDetail_LC_HUM.stateMap);            
            MemberAncillaryResponse_DTO_HUM_V2 memResponse = new MemberAncillaryResponse_DTO_HUM_V2();
            MemberAncillaryResponse_H_HUM_V2 memHRes = new MemberAncillaryResponse_H_HUM_V2();
            memHRes = (MemberAncillaryResponse_H_HUM_V2)System.JSON.deserialize(response, MemberAncillaryResponse_H_HUM_V2.class);
            memResponse = MemberPlanAncillary_LS_HUM.processResponse(response);            
            MemberPlanDetail_LD_HUM.getMemberName(memberPlanObj.Id);
            system.assert(MemberPlanDetail_LC_HUM.AncillaryInfo!=null,true);
            MemberPlanDetail_LC_HUM.getAdditionalMemberPlan(memberPlanObj.Id);
            Test.stopTest();
        }
    }
    
    
    
    
    /*
    * Method Name : testMemberAncillarySC
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void coverCatchBlock()
    {
        MemberPlanDetail_LC_HUM.initiateMbeRequest('dummy');
        system.assert(MemberPlanDetail_LC_HUM.initiateAncillaryRequest('dummy')!=null,true);
    }
    
    
     /*
    * Method Name : testMemberEDI
    * Description : This method is used to test the processResponseEDI
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberEDI()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
         Map<String,String> stateMap= new Map<String,String>();

        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c,ASO__c from MemberPlan where Name = 'MemberPlan2' limit 1];
        String recId =memberPlanObj.Id;
        
        
         HUM_Webservice_Callout__c oGetMemberEDI = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetEDIData',
                                                                                      End_Point_URL__c = 'https://int-crmservicews.humana.com/EDIGroupService/GetEDIGroupInfo', Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberEDI;
        
        System.runAs(testUser)
        {
            Test.startTest();
            object con = (object)MemberPlanDetail_LC_HUM.getEDIData(recId);
            object con1 = (object)MemberPlanDetail_LC_HUM.getEDIData('');
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(200);
            String resBodyString = '{"GetEDIGroupInfoResponse": {"Timestamp": "","ResponseType": "","ErrorCode": "","ErrorDescription":"","RequestGuid":"", "EdiGroupDetails": [{"ErrorCode": "","ErrorDescription":  "","GroupId": "","PCP_PCDFlag":"","Frequency":"","Day1":"","Day2":"","Day3":"","Day4":"","Platform": "MTV","EDIGroup": ""}]}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            stateMap.put('sRequest',reqType);
            MemberPlanDetail_LC_HUM.processResponseEDI(stateMap);
            Test.stopTest();
        }
    }
    
     /*
    * Method Name : testMemberVendor
    * Description : This method is used to test the processResponseVendor and GetGroupInfoFromGBE methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberVendor()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
         Map<String,String> stateMap= new Map<String,String>();

        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c,ASO__c from MemberPlan where Name = 'MemberPlanPBE' limit 1];
        String recId =memberPlanObj.Id;
        
        
         HUM_Webservice_Callout__c oGetMemberEDI = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GroupBusinessEntityService',
                                                                                      End_Point_URL__c = 'https://int-crmservicews.humana.com/EDIGroupService/GetEDIGroupInfo', Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberEDI;
        
        System.runAs(testUser)
        {
            Test.startTest();
            Object conobj = (object)MemberPlanDetail_LC_HUM.GetGroupInfoFromGBE(null);
            Object con = (object)MemberPlanDetail_LC_HUM.GetGroupInfoFromGBE(recId);
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('1789465');
            csCRMOnOffSwitch.IsON__c = true;
            update csCRMOnOffSwitch;
            object con2 = (object)MemberPlanDetail_LC_HUM.GetGroupInfoFromGBE(recId);
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(200);
            String resBodyString =  '{"GroupInfo":{"GroupId":"810314","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"AssociationList": {"Association": [{"AssociationType": "GPEO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/09/2021"},{"AssociationType": "PEO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/07/2022"},{"AssociationType": "GPCO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/19/2021"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            stateMap.put('sRequest',reqType);
            MemberPlanDetail_LC_HUM.processResponseVendor(stateMap);
            HttpResponse res = new Httpresponse();
            res.setStatusCode(200);
			String resBody = '{"GroupInfo":{"GroupId":"810314","Platform":"LV","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"AssociationList":{"Association":[{"AssociationType":"GPEO","AssociationName":"PREFERRED EMPLOYER ORG.","AssociationEffectiveDate":"01/01/2009","AssociationEndDate":"02/09/2021"},{"AssociationType":"PEO","AssociationName":"PREFERRED EMPLOYER ORG.","AssociationEffectiveDate":"01/01/2009","AssociationEndDate":"02/07/2022"},{"AssociationType":"GPCO","AssociationName":"PREFERRED EMPLOYER ORG.","AssociationEffectiveDate":"01/01/2009","AssociationEndDate":"02/19/2021"}]},"OrganizationTypeList":{"OrganizationType":[{"Code":"PE"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            res.setBody(resBody);
            system.assert(res.getBody() == resBody);
            Test.setContinuationResponse(reqType,res);
            stateMap.put('sRequest',reqType);
            MemberPlanDetail_LC_HUM.processResponseVendor(stateMap);
            Test.stopTest();
        }
    }
    
       static testMethod void testOtherInsuranceService(){
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c,ASO__c,EffectiveTo from MemberPlan where Name = '10674931401' limit 1];
        HUM_Webservice_Callout__c oMBEPlusService = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'MBEPlusService',
                                                                                      End_Point_URL__c = 'https://int-crmservicews.humana.com/MBEPLUSServiceREST/GetMember', Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        Test.startTest();
        Object con = (object)MemberPlanDetail_LC_HUM.getOIData(memberPlanObj.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        string resBodyString = '{"GetMemberResponse":{"Body":{"Member":{"DateOfBirth":"06/05/1953","FirstName":"SEKILA","Gender":"F","LastName":"NAPOLES","MiddleInitial":"H","Ssn":"453335217","MasterId":"1017531585","SoldProductList":{"SoldProduct":[{"InsuranceProduct":{"AlternateDescription":"FEHBP NPOS VALUE PLAN 40","Description":"FEHBP TX Value Plan 80/50","MajorLineOfBusinessCode":"NPOS","MajorLineOfBusinessDescription":"POINT OF SERVICE(POS)","ProductStartDate":"01/01/2021","ProductEndDate":"12/31/9999","ProductId":"FEHVNP55","Source":"EM","ProductLineCode":"MED","ProductLineDescription":"MEDICAL PRODUCTS","ProductTypeCode":"LNXX16","SoldProductDetailList":{"SoldProductDetail":[{"InsuranceCoverage":{"EndDate":"12/31/1999","StartDate":"01/01/2020","CoveredRoleList":{"CoveredRole":[{"StartDate":"01/01/2020","EndDate":"12/31/9999","MemberMasterId":"1017531585","RelationshipToSubscriber":"EmployeeSelf","CoveredRolePlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"MemberIDDependentCode","KeyValue":"01","Source":"EM"},{"Application":"EE","KeyType":"MemberIdBase","KeyValue":"106749314","Source":"EM"},{"Application":"","KeyType":"PersonId","KeyValue":"0314122518100289","Source":"EM"}]},"CoverageKey":"1017531585|0314122518100289|0314122518100289|EM|672534|67253463||FEHVNP55|2021-01-01|false|||2020-01-01|106749314|01|TX"},{"StartDate":"01/01/2020","EndDate":"12/31/9999","MemberMasterId":"1012243493","RelationshipToSubscriber":"Spouse","CoveredRolePlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"MemberIDDependentCode","KeyValue":"02","Source":"EM"},{"Application":"EE","KeyType":"MemberIdBase","KeyValue":"106749312","Source":"EM"},{"Application":"","KeyType":"PersonId","KeyValue":"0327522518100343","Source":"EM"}]},"CoverageKey":"1012243493|0314122518100289|0327522518100343|EM|672534|67253463||FEHVNP55|2021-01-01|false|||2020-01-01|106749314|02|TX"}]},"CoverageType":"SelfPlusOne","GroupPlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"GroupId","KeyValue":"672534","Source":"EM"},{"Application":"EE","KeyType":"DivisionId","KeyValue":"67253463","Source":"EM"}]},"ContractId":"106749314","CoordinationOfBenefitList":{"CoordinationOfBenefit":[{"CarrierName":"MEDICARE PART A & B","EffectiveDate":"06/01/2018","EndDate":"12/31/9999","Primary":"1","OILastVerifiedDate":"12/31/9999","StatusCode":"AMDC","StatusDescription":"MEDICARE"}]},"OriginalEffectiveDate":"12/31/9999","OperationalMajorLineofBusiness":"NPOS","PolicySequenceNumber":"0"}}]}}}]}}}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        stateMap.put('sPolicyMember',memberPlanObj.Id);
        MemberPlanDetail_LC_HUM.processMBEOtherInsuranceResponse(stateMap);
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('2104099');
        csCRMOnOffSwitch2.IsON__c = false;
        update csCRMOnOffSwitch2;   
        MemberPlanDetail_LC_HUM.processMBEOtherInsuranceResponse(stateMap);
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('1707499_Switch');
        csCRMOnOffSwitch.IsON__c = true;
        update csCRMOnOffSwitch;

        Object con1 = (object)MemberPlanDetail_LC_HUM.getOIData(memberPlanObj.Id);
        HttpResponse httpRes1 = new Httpresponse();
        httpRes1.setStatusCode(200);
        string resBodyString1 ='{"GetMemberResponse":{"Body":{"Member":{"DateOfBirth":"06/05/1953","FirstName":"SEKILA","Gender":"F","LastName":"NAPOLES","MiddleInitial":"H","Ssn":"453335217","MasterId":"1017531585","SoldProductList":{"SoldProduct":[{"InsuranceProduct":{"AlternateDescription":"FEHBP NPOS VALUE PLAN 40","Description":"FEHBP TX Value Plan 80/50","MajorLineOfBusinessCode":"NPOS","MajorLineOfBusinessDescription":"POINT OF SERVICE(POS)","ProductStartDate":"01/01/2021","ProductEndDate":"12/31/9999","ProductId":"FEHVNP55","Source":"EM","ProductLineCode":"MED","ProductLineDescription":"MEDICAL PRODUCTS","ProductTypeCode":"LNXX16","SoldProductDetailList":{"SoldProductDetail":[{"InsuranceCoverage":{"EndDate":"12/31/1999","StartDate":"01/01/2020","CoveredRoleList":{"CoveredRole":[{"StartDate":"01/01/2020","EndDate":"12/31/9999","MemberMasterId":"1017531585","RelationshipToSubscriber":"EmployeeSelf","CoveredRolePlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"MemberIDDependentCode","KeyValue":"01","Source":"EM"},{"Application":"EE","KeyType":"MemberIdBase","KeyValue":"106749314","Source":"EM"},{"Application":"","KeyType":"PersonId","KeyValue":"0314122518100289","Source":"EM"}]},"CoverageKey":"1017531585|0314122518100289|0314122518100289|EM|672534|67253463||FEHVNP55|2021-01-01|false|||2020-01-01|106749314|01|TX"},{"StartDate":"01/01/2020","EndDate":"12/31/9999","MemberMasterId":"1012243493","RelationshipToSubscriber":"Spouse","CoveredRolePlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"MemberIDDependentCode","KeyValue":"02","Source":"EM"},{"Application":"EE","KeyType":"MemberIdBase","KeyValue":"106749314","Source":"EM"},{"Application":"","KeyType":"PersonId","KeyValue":"0327522518100343","Source":"EM"}]},"CoverageKey":"1012243493|0314122518100289|0327522518100343|EM|672534|67253463||FEHVNP55|2021-01-01|false|||2020-01-01|106749314|02|TX"}]},"CoverageType":"SelfPlusOne","GroupPlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"GroupId","KeyValue":"672534","Source":"EM"},{"Application":"EE","KeyType":"DivisionId","KeyValue":"67253463","Source":"EM"}]},"ContractId":"106749314","OICoordinationOfBenefitList":{"OICoordinationOfBenefit":[{"CarrierName":"MEDICARE PART A & B","EffectiveDate":"06/01/2018","EndDate":"12/31/9999","Primary":"1","OILastVerifiedDate":"12/31/9999","StatusCode":"AMDC","StatusDescription":"MEDICARE"}]},"OriginalEffectiveDate":"12/31/9999","OperationalMajorLineofBusiness":"NPOS","PolicySequenceNumber":"0"}}]}}}]}}}}}';
        httpRes1.setBody(resBodyString1);
        system.assert(httpRes1.getBody() == resBodyString1);                 
        Test.setContinuationResponse(reqType,httpRes1);
        MemberPlanDetail_LC_HUM.processMBEOtherInsuranceResponse(stateMap);
        memberPlanObj.EffectiveTo = Date.newInstance(4000,12, 31);
        update memberPlanObj;  
     	MemberPlanDetail_LC_HUM.processMBEOtherInsuranceResponse(stateMap);
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitchOI = CRMFunctionality_ONOFF_Switch__c.getValues('2262025');
        csCRMOnOffSwitchOI.IsON__c = false;
        update csCRMOnOffSwitchOI;
        Object con2 = (object)MemberPlanDetail_LC_HUM.getOIData(memberPlanObj.Id);
        Test.setContinuationResponse(reqType,httpRes);
        MemberPlanDetail_LC_HUM.processMBEOtherInsuranceResponse(stateMap);
        
        Test.stopTest();
    }
    static testMethod void testDualStatusService(){
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlan memberPlanObj = [select id, PlanId,Issue_State__c,ASO__c from MemberPlan where Name = '10674931401' limit 1];
        Test.startTest();
        Object con = (object)MemberPlanDetail_LC_HUM.getDualStatusData(memberPlanObj.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        string resBodyString = '{"GetMemberResponse":{"Body":{"DualStatusList":[{"EffectiveDate":"2020-10-01","EndDate":"2020-10-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-03-01","EndDate":"2020-03-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-12-01","EndDate":"2020-12-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-08-01","EndDate":"2020-08-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-12-01","EndDate":"2019-12-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-01-01","EndDate":"2020-01-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-07-01","EndDate":"2020-07-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-06-01","EndDate":"2019-06-30","Value":"QI","Code":"06","Indicator":true},{"EffectiveDate":"2020-04-01","EndDate":"2020-04-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-06-01","EndDate":"2020-06-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-08-01","EndDate":"2019-08-31","Value":"QI","Code":"06","Indicator":true},{"EffectiveDate":"2019-09-01","EndDate":"2019-09-30","Value":"QI","Code":"06","Indicator":true},{"EffectiveDate":"2020-09-01","EndDate":"2020-09-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-02-01","EndDate":"2020-02-29","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-07-01","EndDate":"2019-07-31","Value":"QI","Code":"06","Indicator":true},{"EffectiveDate":"2019-10-01","EndDate":"2019-10-31","Value":"QI","Code":"06","Indicator":true},{"EffectiveDate":"2020-11-01","EndDate":"2020-11-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-05-01","EndDate":"2020-05-31","Value":"QI","Code":"06","Indicator":false}],"DeemingPeriodList":[{"EffectiveDate":"2019-07-01","EndDate":"2019-07-31","Indicator":true},{"EffectiveDate":"2019-10-01","EndDate":"2019-10-31","Indicator":true},{"EffectiveDate":"2019-06-01","EndDate":"2019-06-30","Indicator":true},{"EffectiveDate":"2019-08-01","EndDate":"2019-08-31","Indicator":true},{"EffectiveDate":"2019-09-01","EndDate":"2019-09-30","Indicator":true}]}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        MemberPlanDetail_LC_HUM.processResponse(stateMap);
        
        httpRes.setStatusCode(400);
        Test.setContinuationResponse(reqType,httpRes);
        MemberPlanDetail_LC_HUM.processResponse(stateMap);
        Object conObj = (object)MemberPlanDetail_LC_HUM.getDualStatusData(null);
        HUM_Webservice_Callout__c MBEServiceDetailsRest = HUM_Webservice_Callout__c.getValues('MBEPLUSServiceREST');
        MBEServiceDetailsRest.Request_Type__c = '';
        update MBEServiceDetailsRest;
        Object con2 = (object)MemberPlanDetail_LC_HUM.getDualStatusData(memberPlanObj.Id);
        
    }
    
    static testMethod void testEDIElseCondition(){
        List<MemberPlan> memberPlanObj = [select id, PlanId,Issue_State__c,ASO__c,Plan.Payer.Source_Platform_Code__c,GroupNumber from MemberPlan where Name = 'Name New' limit 1];
		Object con1 = (object)MemberPlanDetail_LC_HUM.getEDIData(memberPlanObj[0].Id);
        MemberPlanDetail_LD_HUM.getEDIGroupNumber(memberPlanObj);
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('2364782');
        csCRMOnOffSwitch.IsON__c = false;
        update csCRMOnOffSwitch;
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlan memberPlanObj1 = [select id, PlanId,Issue_State__c,ASO__c from MemberPlan where Name = '10674931401' limit 1];
        Test.startTest();
        Object con = (object)MemberPlanDetail_LC_HUM.getDualStatusData(memberPlanObj1.Id);
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('2117860');
        csCRMOnOffSwitch1.IsON__c = false;
        update csCRMOnOffSwitch1;
        system.assert(csCRMOnOffSwitch1!=null,true);
        Object con3 = (object)MemberPlanDetail_LC_HUM.getEDIData(memberPlanObj1.Id);
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('2117860');
        csCRMOnOffSwitch2.IsON__c = false;
        update csCRMOnOffSwitch2; 
        Object con2 = (object)MemberPlanDetail_LC_HUM.GetGroupInfoFromGBE(memberPlanObj1.Id);
        MemberPlanDetail_LC_HUM.processAncillaryResponse(new Map<string,string>());
        
    }
    
    static testMethod void testElseOI(){
      Object con3 = (object)MemberPlanDetail_LC_HUM.getOIData(null);  
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        string resBodyString = '{}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString); 
    }
 
    
}