/*******************************************************************************************************************************
Apex Class Name : GroupDetailDivisionSubgroup_LT_HUM
Version         : 1.0
Created On      : 11/03/2020
Function        : This class is the test class for GroupDetailDivisionSubgroup_LC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     12/16/2020                Original Version
*    2.0           Kajal Namdev					                    03/19/2021		            Test Class Fix
*******************************************************************************************************************************************************************/
@isTest
public class GroupDetailDivisionSubgroup_LT_HUM 
{
    /*
    * Method Name : setup
    * Description : This method is used to insert the data only once and called automicticaly in all methods
    * Return type : Void
    * Paramater   : NA
    */
    @testSetup static void setup() 
    {   
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser.id,null,'​Value not matched');
        
        System.runAS(oUser)
        {
            RecordType oGroupAccountRecordType = [Select Id from RecordType where Name = 'Group' and sObjectType='Account'];
            system.assert(oGroupAccountRecordType.id != null);
            
            Account oacc =  HUMTestDataHelperTest.getAccount();
            oacc.Group_Number__c = '987654321';
            oacc.recordtypeid = oGroupAccountRecordType.id;
            oacc.name= 'duck';
            oacc.NPI_ID__c='0123456789';
            oacc.Individual_First_Name__c='testname';
            oacc.Individual_Last_Name__c='firstname';
            oacc.Source_Customer_Key__c ='608356';
            oacc.Source_Platform_Code__c='EM';
            insert oacc;
            system.assert(oacc.id!=null);
            system.assert(oacc.Source_Customer_Key__c == '608356');
            system.assert(oacc.Source_Platform_Code__c == 'EM');
            
            HUM_Webservice_Callout__c oHUMGBESservice = new HUM_Webservice_Callout__c();
            oHUMGBESservice.name='GroupBusinessEntityService';
            oHUMGBESservice.Active__c=true;
            oHUMGBESservice.Certificate_Name__c='Certificate';
            oHUMGBESservice.End_Point_URL__c='https://int-crmservicews.humana.com/GBEServiceREST/GetGroupInfo';
            oHUMGBESservice.Timeout__c='12000';
            oHUMGBESservice.Request_Type__c = 'POST';
            insert oHUMGBESservice ;
            system.assert(oHUMGBESservice.id!=null);
            system.assert(oHUMGBESservice.name == 'GroupBusinessEntityService');
            
            HUM_Webservice_Callout__c oHUMEDISservice = new HUM_Webservice_Callout__c();
            oHUMEDISservice.name='GetEDIData';
            oHUMEDISservice.Active__c=true;
            oHUMEDISservice.Certificate_Name__c='Certificate';
            oHUMEDISservice.End_Point_URL__c='https://int-crmservicews.humana.com/EDIGroupService/GetEDIGroupInfo';
            oHUMEDISservice.Timeout__c='12000';
            oHUMEDISservice.Request_Type__c = 'POST';
            insert oHUMEDISservice ;
            system.assert(oHUMEDISservice.id!=null);
            system.assert(oHUMEDISservice.name == 'GetEDIData');
            
            HUMConstants__c oConstant1 = new HUMConstants__c(Name = 'GBES_DivisionSubGroup_Records_HUM',IntegerValue__c= 5,StringValue__c = '5') ;
            insert oConstant1;
            system.assert(oConstant1.id!=null);
            Endpoint_Url_HUM__c oEndUrl = new Endpoint_Url_HUM__c(Name = 'MTVx', Remote_Url__c='https://test.com');
            insert oEndUrl;
            system.assert(oEndUrl.id!=null);
            Endpoint_Url_HUM__c oEndUrl1 = new Endpoint_Url_HUM__c(Name = 'SSO_LOGINURL', Remote_Url__c='https://test.com');
            insert oEndUrl1;
            system.assert(oEndUrl1.id!=null);
        }
    }
    
    
        
    /*
    * Method Name : testGroupAndDivisionData
    * Description : This method tests Group Detail section details is populating or not.
    * Return type : Void
    * Paramater   : None
    */
    public static testmethod void testGroupAndDivisionData() {
        
        GroupDetailDivisionSubgroup_LH_HUM oGrpDetailHelper = new GroupDetailDivisionSubgroup_LH_HUM(
            '',TRUE,'1234','Humana Group','122','D','FM');
        GroupDetailDivisionSubgroup_LH_HUM oGrpDetailHelper1 = new GroupDetailDivisionSubgroup_LH_HUM(
            '',TRUE,'1234','Humana Group','122','M','IM');
        GroupDetailDivisionSubgroup_LH_HUM oGrpDetailHelper2 = new GroupDetailDivisionSubgroup_LH_HUM(
            '',TRUE,'1234','Humana Group','122','H','EM');
        GroupDetailDivisionSubgroup_LH_HUM oGrpDetailHelper3 = new GroupDetailDivisionSubgroup_LH_HUM(
            '',TRUE,'1234','Humana Group','122','Z','GD');
        GroupDetailDivisionSubgroup_LH_HUM oGrpDetailHelpe4 = new GroupDetailDivisionSubgroup_LH_HUM(
            '',TRUE,'1234','Humana Group','122','','');
        GroupDetailDivisionSubgroup_LC_HUM oGrpDetailController = new GroupDetailDivisionSubgroup_LC_HUM();
        Account oAccount = [Select Id from Account where Source_Customer_Key__c = :'608356'limit 1];
        
        GroupDetailDivisionSubgroup_LC_HUM.objGrpAccount = oAccount;
        // Invoke the continuation by calling the action method
        Continuation oCon = (Continuation)GroupDetailDivisionSubgroup_LC_HUM.getGroupInfo(oAccount.Id);
        
        // Verify that the continuation has the proper requests
        Map<String, HttpRequest> mapReqs = oCon.getRequests();
        list<string> lstConLabels = new list<string>(mapReqs.keyset());
        system.assert(mapReqs.size() == 2);
        
        HttpResponse oGBERes = new HttpResponse();
        oGBERes.setStatusCode(200);
        oGBERes.setBody('{ "GroupInfo":{ "GroupId":"837194", "Platform":"EM", "BenefitPackageId":"XXXXXXXX", "StartDate":"", "EndDate":"", "AsOfDate":"11/11/2020", "BenfitSequenceNumber":"", "GroupName":"THE KINGS DAUGHTERS AND SONS HOME", "EffectiveDate":"03/01/2020", "EffectiveStatus":"Active", "EnrolledSubscriberCountDental":"72", "EnrolledSubscriberCountMedical":"113", "NextRenewalDate":"03/01/2021", "DentalOriginalEffectiveDate":"03/01/2020", "VendorProgramOfferListSubGroup":{ "VendorProgramOffer":[ { "VendorIdentifier":"ACCL", "VendorIdentifierDescription":"ACCOLADE", "VendorType":"ACCOLADE", "VendorTypeDescription":"CONSUMER EXPERIENCE", "VendorBenefitCode":"V-AEMB", "VendorBenefitCodeDescription":"ACCOLADE EMBEDDED", "ProgramOfferEffectiveDate":"12/01/2020", "ProgramOfferEndDate":"12/31/9999" } ] }, "VendorProgramOfferListGroup":{ }, "DivisionRecordsTotalCount":"2", "DivisionList":{ "Division":[ { "VitalityIndicator":"true", "SubGroupID":"83719401", "SubGroupName":"ACTIVE", "UnitCount":"", "Unit":"", "EffectiveProvision":"" }, { "VitalityIndicator":"true", "SubGroupID":"83719402", "SubGroupName":"COBRA", "UnitCount":"", "Unit":"", "EffectiveProvision":"" } ] } } }');   
        Test.setContinuationResponse(lstConLabels.get(0), oGBERes);
        
        HttpResponse oEDIRes = new HttpResponse();
        oEDIRes.setStatusCode(200);
        oEDIRes.setBody('{ "GetEDIGroupInfoResponse":{ "Timestamp":"2020-12-18T01:53:46.9911833-05:00", "ResponseType":"Success", "ErrorCode":"", "ErrorDescription":"", "RequestGuid":"b99c12ba-ec6b-4083-bebf-9ee274d5bdac", "EdiGroupDetails":[ { "ErrorCode":"1002", "ErrorDescription":"Group Not Found", "GroupId":"675717", "EDIGroup":"N", "Platform":"", "PCP_PCDFlag":"Y", "Frequency":"5", "Day1":"1", "Day2":"2", "Day3":"3", "Day4":"4" } ] } }');   
        Test.setContinuationResponse(lstConLabels.get(1), oEDIRes);

        GroupDetailDivisionSubgroup_LS_HUM.ContinuationStateWrapper oState = new GroupDetailDivisionSubgroup_LS_HUM.ContinuationStateWrapper();
        GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo oRe = GroupDetailDivisionSubgroup_LC_HUM.processResponse(oState);
        oState.requestLabelEDIData = lstConLabels.get(1);
        oState.requestLabelGbe = lstConLabels.get(0);
        oState.sSourceCustomerKey = '';
        GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo oRes = GroupDetailDivisionSubgroup_LC_HUM.processResponse(oState);
    }
}