/***************************************************************************************************************************************************************** 
Apex Class Name : MemberSearchEnrollmentHelper_LT_HUM
Function : Test Class to cover MemberSearchEnrollmentHelper_LC_HUM

* Version                   Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  1.0                      Rahul Krishan                                       11/17/2020                   Original Version
*  2.0                      Santhi Mandava                                      11/16/2022                   US3482496_TestClass issue fix
*  3.0                      Nilanjana Sanyal                                    02/27/2023                   Test Class issue fixing and test coverage increment for US-4003126          
*  4.0						Bhakti Vispute										04/14/2023					 Test class coverage increment         						
**********************************************************************************************************************************************************************/

@isTest
public class MemberSearchEnrollmentHelper_LT_HUM
{    
    @testSetup static void setUpTestData()
    {
        List<Endpoint_Url_HUM__c> lstConstants = new List<Endpoint_Url_HUM__c>();
        lstConstants.add(new Endpoint_Url_HUM__c(Name='FAST_APP_URL',Remote_Url__c='https://hss.humana.com/HumanaMedicare/FastAPPSearchOnly.asp'));
        lstConstants.add(new Endpoint_Url_HUM__c(Name='DCNSpider2URL',Remote_Url__c='https://spidar.humana.com/SpidarViewer/SearchViewerURL?txtCriteria=1&txtDCNFrom='));
        lstConstants.add(new Endpoint_Url_HUM__c(Name='SSO_LOGINURL',Remote_Url__c='https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx'));
        insert lstConstants;
        system.assert(lstConstants !=NULL);        
        List<HUMRestrictedGroupIDs__c> lstConstants1 = new List<HUMRestrictedGroupIDs__c>();
        lstConstants1.add(new HUMRestrictedGroupIDs__c(Name='207206',Group_Name__c='STATE OF FLORIDA'));
        insert lstConstants1;
        system.assert(lstConstants1 !=NULL);

        List<HUMConstants__c> lstConstantsNew = new List<HUMConstants__c>();
        lstConstantsNew.Add(new HUMConstants__c(Name='ENROLLMENT_H1',StringValue__c='UKM|00|H1|'));
        insert lstConstantsNew;
        system.assert(lstConstantsNew !=NULL); 

        List<HUMConstants__c> lstConstantsNew1 = new List<HUMConstants__c>();
        lstConstantsNew1.Add(new HUMConstants__c(Name='ENROLLMENT_AE',StringValue__c='UKM|00|AE|'));
        insert lstConstantsNew1;
        system.assert(lstConstantsNew1 !=NULL); 

        List<HUMConstants__c> lstConstantsNew2 = new List<HUMConstants__c>();
        lstConstantsNew2.Add(new HUMConstants__c(Name='CBIS_EXTID',StringValue__c='UKM|00|CB|'));
        insert lstConstantsNew2;
        system.assert(lstConstantsNew2 !=NULL);    

        List<HUMConstants__c> lstConstantsNew3 = new List<HUMConstants__c>();
        lstConstantsNew3.Add(new HUMConstants__c(Name='UNKNOWN_MEMBER',StringValue__c='Unknown Member'));
        insert lstConstantsNew3;
        system.assert(lstConstantsNew3 !=NULL); 
    }
    
    static testMethod void MemberSearchEnrollmentHelper()
    {
        HUMH1SearchResponseHelper.EnrollmentDetail h1 = new HUMH1SearchResponseHelper.EnrollmentDetail();
        h1.firstName='Rahul';
        h1.lastName='Krishan';
        h1.strDob = '1984-05-04 12:20:20';
        h1.covEffDt = '2020-11-20 12:20:20';
        h1.covTrmDt = '2020-11-30 12:20:20';
        
        Map<String,String> mapAccount = new Map<String,String>();
        mapAccount.put('firstName','abc');
        mapAccount.put('lastName','fgh');
        mapAccount.put('birthDate','1984-05-04 12:20:20');
        mapAccount.put('effectiveDate','2020-11-20 12:20:20');
        mapAccount.put('PersonMailingPostalCode','122001');
       
        HUMAEServicesHistoryResponse.PendHistory aeHis = new HUMAEServicesHistoryResponse.PendHistory();
        aeHis.First_Name = 'Test';
        aeHis.Last_Name = 'User';
        aeHis.Pend_Key = '0000000000000';
        aeHis.Birth_Date =system.today()-10;
        aeHis.Platform = 'M';
        aeHis.Group_Id ='675717';
        aeHis.Ben_Id='03MC376162';
        aeHis.Cov_Eff_Date='2023-01-01';
        aeHis.Cov_End_Date='2023-04-04';
        HUMUtilityHelper.getMD5Hash('CRMS Test');
        
        TRRServiceRestResponse_DTO_HUM.TRRDetail TRREnroll = new TRRServiceRestResponse_DTO_HUM.TRRDetail();
        TRREnroll.MedicareId='asdc';                                
        TRREnroll.LastName='eadfef';           
        TRREnroll.FirstName='aefg';
        TRREnroll.Contract='adfe';
        TRREnroll.PBP='dfgg';
        TRREnroll.EffectiveDate='11/20/2020';
        TRREnroll.ReplyCode='1234';
        TRREnroll.ErrorMessage='2344';
        TRREnroll.HumanaReceivedDate='12/20/2020';
        TRREnroll.SegmentNumber='1234';
        
        ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetails = new ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper();
        eDetails.CoverageTermDate='11/20/2020'; 
        eDetails.MedicareId='12345';
        eDetails.FirstName='ert';
        eDetails.LastName='dee';
        eDetails.DOB='04/05/1984';
        eDetails.ContractId='ffgb';
        eDetails.PlanBenefitPackage='dfgb';
        eDetails.BSN='dfr';
        eDetails.GroupId='5632';
        eDetails.SegmentId='556';
        eDetails.CoverageEffectiveDate='12/20/2020';
        eDetails.MarketReceiptDate='11/20/2020';
        eDetails.ApplicationChannel='3566';
        eDetails.OECConfirmationId='1234';
        eDetails.PlanYear='2020';
        eDetails.ApplicationId='12345';
        
        ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetails1 = new ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper();
        eDetails1.CoverageTermDate='11/20/2020'; 
        eDetails1.MedicareId='12345';
        eDetails1.FirstName='ert';
        eDetails1.LastName='dee';
        eDetails1.DOB='04/05/1984';
        eDetails1.ContractId='ffgb';
        eDetails1.PlanBenefitPackage='dfgb';
        eDetails1.BSN='dfr';
        eDetails1.GroupId='5632';
        eDetails1.SegmentId='556';
        eDetails1.CoverageEffectiveDate='12/20/2020';
        eDetails1.MarketReceiptDate='11/20/2020';
        eDetails1.ApplicationChannel='3566';
        eDetails1.OECConfirmationId='1234';
        eDetails1.PlanYear='2020';
        eDetails1.ApplicationId='';
        eDetails1.DCNNumber ='ac1234';
        
        HUMCBISSearchServiceHelper.ResponseItem oRespItem = new HUMCBISSearchServiceHelper.ResponseItem();
        oRespItem.FirstName = 'adff';
        oRespItem.LastName='adf';
        oRespItem.Platform='asff';
        oRespItem.Policy = new HUMCBISSearchServiceHelper.Policy();
        oRespItem.Policy.GroupId = '207206';
        oRespItem.Address = new HUMCBISSearchServiceHelper.Address();
        oRespItem.Address.Zip='76058';
              
        String ExternalID = oRespItem.FirstName+ oRespItem.LastName+String.valueof(oRespItem.DOB)+oRespItem.Address.Zip;
        HUMUtilityHelper.getMD5Hash(ExternalID.trim());
        
        HUMCBISSearchServiceHelper.ResponseItem oRespItem1 = new HUMCBISSearchServiceHelper.ResponseItem();
        oRespItem1.FirstName = 'adff';
        oRespItem1.LastName='adf';
        oRespItem1.Platform='asff';
        oRespItem1.Policy = new HUMCBISSearchServiceHelper.Policy();
        oRespItem1.Policy.GroupId = '207207';
        oRespItem1.Address = new HUMCBISSearchServiceHelper.Address();
        oRespItem1.Address.Zip='76059';

        String ExternalID1 = oRespItem1.FirstName+ oRespItem1.LastName+String.valueof(oRespItem1.DOB)+oRespItem1.Address.Zip;
        HUMUtilityHelper.getMD5Hash(ExternalID1.trim());
		
		HUMCBISSearchResponse_H_HUM.ResponseItem oRespItem2 = new HUMCBISSearchResponse_H_HUM.ResponseItem();
        oRespItem2.FirstName = 'adff';
        oRespItem2.LastName='adf';
        oRespItem2.Platform='asff';
        oRespItem2.Policy = new HUMCBISSearchResponse_H_HUM.Policy();
        oRespItem2.Policy.GroupId = '207208';
        oRespItem2.Address = new HUMCBISSearchResponse_H_HUM.Address();
        oRespItem2.Address.Zip='76060';
        
        String ExternalID2 = oRespItem2.FirstName+ oRespItem2.LastName+String.valueof(oRespItem2.DOB)+oRespItem2.Address.Zip;
        HUMUtilityHelper.getMD5Hash(ExternalID2.trim());
        
        Map<String,Map<String,String>> mapacc = new Map<String,Map<String,String>>();
        mapacc.put('Family:PlatformMember',mapAccount);
        mapacc.put('MemberContract:Group',mapAccount);
        mapacc.put('Coverages:PlatformMemberCoverage1',mapAccount);
        
        MemberSearchEnrollmentHelper_LC_HUM m1 = new MemberSearchEnrollmentHelper_LC_HUM(h1);
        MemberSearchEnrollmentHelper_LC_HUM m2 = new MemberSearchEnrollmentHelper_LC_HUM(mapAccount);
        MemberSearchEnrollmentHelper_LC_HUM m3 = new MemberSearchEnrollmentHelper_LC_HUM(aeHis);
        MemberSearchEnrollmentHelper_LC_HUM m4 = new MemberSearchEnrollmentHelper_LC_HUM(TRREnroll,1);
        MemberSearchEnrollmentHelper_LC_HUM m5 = new MemberSearchEnrollmentHelper_LC_HUM(eDetails,1);
        MemberSearchEnrollmentHelper_LC_HUM m6 = new MemberSearchEnrollmentHelper_LC_HUM(eDetails1,1);
        MemberSearchEnrollmentHelper_LC_HUM m7 = new MemberSearchEnrollmentHelper_LC_HUM(oRespItem,1);
        MemberSearchEnrollmentHelper_LC_HUM m8 = new MemberSearchEnrollmentHelper_LC_HUM(oRespItem1,1);
		MemberSearchEnrollmentHelper_LC_HUM m9 = new MemberSearchEnrollmentHelper_LC_HUM(oRespItem2);
        MemberSearchEnrollmentHelper_LC_HUM m10 = new MemberSearchEnrollmentHelper_LC_HUM(mapacc,1);
        MemberSearchEnrollmentHelper_LC_HUM m11 = new MemberSearchEnrollmentHelper_LC_HUM(mapacc,null); 

		MemberSearchEnrollmentHelper_LC_HUM.insertMemberAccount('Test', 'Test1');  				
    }     
}