/*************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryHelperTest
Version          : 1.0
Created Date     : September 29 2014
Function         : Test class to HUMInquiryHistoryHelper class

* Modification Log :
* Modification ID     Developer                Code Review            Date                 Description
* -------------------------------------------------------------------------------------------------------------------------
* 1.0              Shruthi Karanth              18313                09/29/2014           Original Version
* 1.1              Shruthi Karanth              18931                10/31/2014           Changes as per changes to class to fix defect 153495.0001
* 1.2              Vishal Verma                 20677                02/02/2015           Defect : 169366.0001 Updated date range in the filter conditions.
* 1.3              Vishal Verma                 22273                04/06/2015           Req  - 166509 : Updated test class to cover new methods
* 1.4              Prudhvi Pamarthi             297150               10/16/2018           REQ - 377195 Fix for CA Ticket 7742421 - Added warning msg for invalid Inquiry ID   
***************************************************************************************************************************/

@isTest
private class HUMInquiryHistoryHelperTest
{
    /*
    * Method Name : testInquiryHistoryHelper
    * Description : Test method to test InquiryHistoryHelper
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testInquiryHistoryHelper()
    {   
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
            Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgTest());
            HUMTestDataHelperTest.getHUMConstantsData();
            HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '3000', End_Point_URL__c = 'SampleURL', Active__c=true);
            insert vfs;
            system.assert(vfs.Id != null);
            
            HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'SampleURL', Active__c=true);
            insert cstGCP ;
            system.assert(cstGCP.Id != null);
            
            Account objAcc = HUMTestDataHelperTest.getAccount();
            objAcc.FirstName = 'Test';
            objAcc.LastName ='Account Last';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            objAcc.OwnerId = u.Id;
            insert objAcc;
            
            system.assertEquals(objAcc.id!=null, true);
            
            ApexPages.CurrentPage().getparameters().put('id', objAcc.Id);
            
            Test.startTest();
                HUMInquiryHistoryController inqHistory = new HUMInquiryHistoryController();
                inqHistory.sFromDate ='12/02/2010';
                inqHistory.sToDate ='12/12/2010';
                inqHistory.iTableSize = 1;
                inqHistory.retrieveLegacyData();
                list<HUMInquiryHistoryWrapperHelper> lstInquiryHistorylist = inqHistory.lstReqInquiryHistorylist;
                system.assert(lstInquiryHistorylist != null);
                
                Map<Integer,List<HUMInquiryHistoryWrapperHelper>> mapInqHistory = HUMInquiryHistoryHelper.constructMap(inqHistory.lstReqInquiryHistorylist,1,'datetimeopened','ASC');
                boolean bHasRec =  HUMInquiryHistoryHelper.hasRecord(1,mapInqHistory);
                HUMInquiryHistoryHelper.displayErrorMessage();
                HUMInquiryHistoryHelper.addError('sFromDate', 'sToDate', 'sReferenceId');
                boolean bValid = HUMInquiryHistoryHelper.validateRequest('sFromDate', 'sToDate', 'sReferenceId');
                system.assert(bValid == false);
                bValid = HUMInquiryHistoryHelper.validateRequest('', '', '');
                system.assert(bValid == false);
                bValid = HUMInquiryHistoryHelper.validateRequest('', '', '123');
                system.assert(bValid == true);
                bValid = HUMInquiryHistoryHelper.validateRequest('', '', '123a');
                system.assert(bValid == false);
            Test.stopTest();
        }
    }
}