/*****************************************************************************************************************************************************************************
    @Apex Class Name  : MBEPlusOIRequest_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : June 05,2020
    @Function         : Test class for MBEPlusOIRequest_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Keerti Durbha                                    06/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class MBEPlusOIRequest_DTO_T_HUM
{
    /*
    * Method to test MBEPlusOI Request structure
    * @param        None
    * @return       None
    */
    private Static testmethod void TestMBEPlusOIRequest_DTO_T_HUM()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();    	

        MBEPlusOIRequest_DTO_HUM objM = new MBEPlusOIRequest_DTO_HUM();
        MBEPlusOIRequest_DTO_HUM.GetMember objGetMember = new MBEPlusOIRequest_DTO_HUM.GetMember();
        MBEPlusOIRequest_DTO_HUM.MemberPlatformPointerList objMemberPlatformPointerList = new MBEPlusOIRequest_DTO_HUM.MemberPlatformPointerList();
        MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList objGroupPlatformPointerList = new MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList();
        MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList objCoveredRolePlatformPointerList = new MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList();
        string json = JSON.serialize(objM);
        MBEPlusOIRequest_DTO_HUM.parse(json);
                Test.stopTest();    
        }
    }
}