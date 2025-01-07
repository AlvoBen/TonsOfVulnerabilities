/*****************************************************************************************
Apex class Name    : MTVRemarksGetClaimsResponse_LH_HUM_V1
Version            : 1.0   
Created Date       : 06/01/2022   

Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
@isTest
public class MTVRemarksGetClaimsRequest_LH_T_HUM_V1 {
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
     * Method Name : testHelperMethod
     * Description : Test method for MTVRemarksServiceGetClaimsRequest
     * Return type : NA
     * Paramater   : NA
     */
	@isTest
    private static void testHelperMethod(){
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
        	MTVRemarksGetClaimsRequest_LH_HUM_V1 helper=new MTVRemarksGetClaimsRequest_LH_HUM_V1();
        	MTVRemarksGetClaimsRequest_LH_HUM_V1.GetClaimsRemarksRequest claimRemarkRequest=new MTVRemarksGetClaimsRequest_LH_HUM_V1.GetClaimsRemarksRequest();
        	MTVRemarksGetClaimsRequest_LH_HUM_V1.ClaimNumbersList claimNumbersList=new MTVRemarksGetClaimsRequest_LH_HUM_V1.ClaimNumbersList();
    		System.assertEquals(claimNumbersList.ClaimNumber, '');
    	}
    }
}