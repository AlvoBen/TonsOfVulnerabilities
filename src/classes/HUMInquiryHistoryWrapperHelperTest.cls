/*************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryWrapperHelperTest
Version          : 1.0
Created Date     : September 29 2014
Function         : Test class to HUMInquiryHistoryWrapperHelper class

* Modification Log :
* Modification ID     Developer                Code Review            Date                 Description
* -------------------------------------------------------------------------------------------------------------------------
* 1.0              Shruthi Karanth             18313                09/29/2014           Original Version
* 1.1              Palak Papneja               18313                10/06/2014           Renamed test class in comments
* 1.1              Vishal Verma                22273                04/04/2014           Req  - 166509 -  Updated test class to cover new methods for HUMInquiryHistoryWrapperHelper
***************************************************************************************************************************/

@isTest
private class HUMInquiryHistoryWrapperHelperTest
{
    /*
    * Method Name : testHUMInquiryHistoryWrapperHelper
    * Description : Test method for HUMInquiryHistoryWrapperHelper
    * Return type : void
    * Paramater   : NA
    */
    static testmethod void testHUMInquiryHistoryWrapperHelper()
    {    
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            HUMTestDataHelperTest.getHUMConstantsData();    
            List<HUMInquiryHistoryWrapperHelper> lstInquiryHistoryWrapper = new List<HUMInquiryHistoryWrapperHelper>();
            HUMInquiryHistoryWrapperHelper inquiryHistoryWrapper = new HUMInquiryHistoryWrapperHelper ();
            inquiryHistoryWrapper.ReferenceId = '123';
            inquiryHistoryWrapper.CaseOrigin = 'New';
            inquiryHistoryWrapper.Type = 'Open';      
            inquiryHistoryWrapper.Category = 'New';  
            inquiryHistoryWrapper.Product = 'P1';
            inquiryHistoryWrapper.Disposition = 'D1';
            inquiryHistoryWrapper.InquiringFor = 'abc';      
            inquiryHistoryWrapper.InquiringForType = 'abc'; 
            inquiryHistoryWrapper.InquiringAbout = 'xyz';  
            inquiryHistoryWrapper.Status = 'xyz';
            inquiryHistoryWrapper.Priority = 'High';      
            inquiryHistoryWrapper.DateTimeOpened = System.today()-5;  
            inquiryHistoryWrapper.DateTimeClosed = System.Today();
            inquiryHistoryWrapper.OwnerTeam = 'Team 1';
            inquiryHistoryWrapper.Reason = 'Reason 1';
            inquiryHistoryWrapper.OwnerDepartment = 'O 1';
            inquiryHistoryWrapper.ReferenceIDLink = 'Ref 1';  
            lstInquiryHistoryWrapper.add(inquiryHistoryWrapper);       
            
            inquiryHistoryWrapper = new HUMInquiryHistoryWrapperHelper ();
            inquiryHistoryWrapper.ReferenceId = '981';
            inquiryHistoryWrapper.CaseOrigin = 'Old';
            inquiryHistoryWrapper.Type = 'Closed';      
            inquiryHistoryWrapper.Category = 'Re open';  
            inquiryHistoryWrapper.Product = 'P2';
            inquiryHistoryWrapper.Disposition = 'D2';
            inquiryHistoryWrapper.InquiringFor = 'pqr';      
            inquiryHistoryWrapper.InquiringForType = 'pqr'; 
            inquiryHistoryWrapper.InquiringAbout = 'ijk';  
            inquiryHistoryWrapper.Status = 'ijk';
            inquiryHistoryWrapper.Priority = 'Low';      
            inquiryHistoryWrapper.DateTimeOpened = System.today()-10;  
            inquiryHistoryWrapper.DateTimeClosed = System.Today()-2;
            inquiryHistoryWrapper.OwnerTeam = 'Team 2';
            inquiryHistoryWrapper.Reason = 'Reason 2';
            inquiryHistoryWrapper.OwnerDepartment = 'O 2';
            inquiryHistoryWrapper.ReferenceIDLink = 'Ref 2';  
            lstInquiryHistoryWrapper.add(inquiryHistoryWrapper);       
            
            HUMInquiryHistoryWrapperHelper.sSortExpression = system.label.HumAscendingLiteral;
            HUMInquiryHistoryWrapperHelper.sSortExpression = system.label.HUM_CASEORIGIN;
            Test.StartTest();
                lstInquiryHistoryWrapper.sort();
                system.assert(lstInquiryHistoryWrapper.size()>0);
            Test.StopTest();
        }
    }
}