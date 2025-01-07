/******************************************************************************************************************
Apex class Name    : SearchMBEPlus_T_HUM
Version            : 1.0
Function           : Test class for Mbe plus PID Search
Created On         : 08/21/2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                    Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Lakshmi Madduri    							09/05/2018                Original Version 
------------------------------------------------------------------------------------------------------------------*/
@isTest
Private Class SearchMBEPlus_T_HUM 
{
     /*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
        System.assertEquals(objUser.LastName,'test User1');
    }
    /*
    * Method Name : testSearchMbePlus
    * Description : This method used To Test DTO for MBE plus in member search page
    * Return type : None
    * Parameter   : None
    */
    @isTest
    Static void testSearchMbePlus()
    {
        User testUser = [SELECT Id, Name , LastName  FROM  User WHERE LastName='test User1'];
        System.assert(testUser.LastName=='test User1');
        System.runAs(testUser)
        {
            Test.startTest();
            SearchMBEPlusRequest_DTO_HUM objReq = new SearchMBEPlusRequest_DTO_HUM();
			SearchMBEPlusRequest_DTO_HUM.GetMember getMemvar =new SearchMBEPlusRequest_DTO_HUM.GetMember ();
			List<string> platformlst=new List<string> {'LV','EM'};
			List<String> periodLst=new List<String>{'Current','Past','Future'};
			SearchMBEPlusRequest_DTO_HUM.PeriodList periodVar=new SearchMBEPlusRequest_DTO_HUM.PeriodList() ;
			periodVar.period=periodLst;
			SearchMBEPlusRequest_DTO_HUM.SourcePersonIdCriteria sourcePersonIdVar=new SearchMBEPlusRequest_DTO_HUM.SourcePersonIdCriteria();
			sourcePersonIdVar.SourcePersonId='0261350310165609';
            System.assertEquals(sourcePersonIdVar.SourcePersonId,'0261350310165609');
            
			sourcePersonIdVar.AsOfDate= '2018-05-09';
            System.assertEquals(sourcePersonIdVar.AsOfDate,'2018-05-09');
            
			sourcePersonIdVar.PeriodList=periodVar;
			sourcePersonIdVar.Platform=platformlst;
			
			SearchMBEPlusRequest_DTO_HUM.GetCriteria getCriteriaVar=new SearchMBEPlusRequest_DTO_HUM.GetCriteria ();
			getCriteriaVar.SourcePersonIdCriteria=sourcePersonIdVar;
			
			SearchMBEPlusRequest_DTO_HUM.PlatformPointerList platformPointerVar=new SearchMBEPlusRequest_DTO_HUM.PlatformPointerList();
			platformPointerVar.PlatformProxyKey='test';
            System.assertEquals(platformPointerVar.PlatformProxyKey,'test');
			SearchMBEPlusRequest_DTO_HUM.Member memberVar=new SearchMBEPlusRequest_DTO_HUM.Member();
			memberVar.PlatformPointerList=platformPointerVar;
			memberVar.GetCriteria=getCriteriaVar;
			getMemvar.member=memberVar;
			objReq.GetMember =getMemvar;
            
            SearchMBEPlusResponse_DTO_HUM objRes = new SearchMBEPlusResponse_DTO_HUM();
            SearchMBEPlusResponse_DTO_HUM.GetMemberResponse objGetMember = new SearchMBEPlusResponse_DTO_HUM.GetMemberResponse();
            SearchMBEPlusResponse_DTO_HUM.Body objBody = new SearchMBEPlusResponse_DTO_HUM.Body();
            SearchMBEPlusResponse_DTO_HUM.Member objMember = new SearchMBEPlusResponse_DTO_HUM.Member();
            Test.stopTest();
            
        }
    }

}