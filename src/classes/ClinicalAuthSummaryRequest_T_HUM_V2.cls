/**************************************************************************** 
Apex Class Name  : ClinicalAuthSummaryRequest_T_HUM_V2
Version          : 1.0  
Created Date     : December 06, 2019
Function         : test class for ClinicalAuthSummaryRequest_H_HUM_V2 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Imran                                       12/06/2019                    Original Version
***************************************************************************************************/
@isTest
private Class ClinicalAuthSummaryRequest_T_HUM_V2
{
    /*
    * Method Name : testSetup
    * Description : Test method to setup common test data for all test methods
    * Return type : void
    * Paramater   : NA
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
     * Method Name : testClinicalAuthSummaryRequest
     * Description : Test method for  ClinicalAuthSummaryRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClinicalAuthSummaryRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            ClinicalAuthSummaryRequest_H_HUM_V2 oRequest = new  ClinicalAuthSummaryRequest_H_HUM_V2 ();
            ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest oAuthReq = new  ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest(); 
            oAuthReq.MemberId= '1097649428';
            oAuthReq.MemberIdType = 'SdrPersonID';
            oAuthReq.DateOfBirth= '2005-07-30 00:00:00.000';
            oAuthReq.IncludeCDRAuth= 'false';
            ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData oClinicalPageData = new  ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData ();
            oClinicalPageData.Pagesize = 1;
            oClinicalPageData.Pagenumber = 1;
            oAuthReq.PageMetaData = oClinicalPageData;
            oRequest.AuthSummaryRequest= oAuthReq;
            ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory oClincalAuthcategory = new ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory();
            oClincalAuthcategory.AuthCategory ='';
            oAuthReq.AuthFilter = oClincalAuthcategory;
            system.assert(oRequest.AuthSummaryRequest!= null);
            Test.StopTest();
        }
    }  
}