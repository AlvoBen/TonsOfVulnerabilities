/**************************************************************************** 
Apex Class Name  : HUMIntGroupSearchServiceHelperTest 
Version          : 1.0  
Created Date     : October 20 2014 
Description      : This class is used as test class for HUMIntGroupSearchServiceHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth           18672                  10/20/2014                     Original Version
* Reena Rajan               22348                  04/07/2015                     Added line 39, 40 ,41 to increase the coverage for External Group Search code change REQ-160910
***************************************************************************************************/

@isTest
private Class HUMIntGroupSearchServiceHelperTest 
{
    static testMethod void testHUMIntGroupSearchServiceHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUserGrpSrch1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
                HUMIntGroupSearchServiceHelper grpSearchService = new HUMIntGroupSearchServiceHelper();
                HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO arrayGrpExchngDtl = new HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO();
                HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO arrayGrpExchngInfoDtl= new HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO();
                HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO grpExDetail = new HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO();
                system.assert(grpExDetail.GroupId == null); 
                
                HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO grpExInfoDetail = new HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO();
                system.assert(grpExInfoDetail.GroupId == null); 
                
                HUMIntGroupSearchServiceHelper.RequestGroupExchangeDTO reqGrpExc = new HUMIntGroupSearchServiceHelper.RequestGroupExchangeDTO();
                HUMIntGroupSearchServiceHelper.RequestGroupSearch reqGrpSeach = new HUMIntGroupSearchServiceHelper.RequestGroupSearch();
                system.assert(reqGrpSeach.GroupID == null); 
                
                HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO  respGrpExc = new HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO();
                HUMIntGroupSearchServiceHelper.ResponseGroupSearch resGrpSeach = new HUMIntGroupSearchServiceHelper.ResponseGroupSearch();
                system.assert(resGrpSeach.GroupName == null); 
            Test.stopTest(); 
        }
    }
}