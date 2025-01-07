/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityRequestV2_T_HUM 
Version          : 1.0  
Created Date     : April 12, 2018
Function         : test class for ProductBusinessEntityRequestV2_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rakesh Patni             220187            04/12/2018                    Original Version
***************************************************************************************************/
@isTest
private Class ProductBusinessEntityRequestV2_T_HUM 
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
     * Method Name : testProductBusinessEntityRequest_H_HUM
     * Description : Test method for ProductBusinessEntityRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testProductBusinessEntityRequest_H_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
            ProductBusinessEntityRequestV2_H_HUM.GetProduct getPro = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();
            ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier proIden = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
            ProductBusinessEntityRequestV2_H_HUM.IncludeList incType = new ProductBusinessEntityRequestV2_H_HUM.IncludeList();
            ProductBusinessEntityRequestV2_H_HUM.ICDCodeList icCode = new ProductBusinessEntityRequestV2_H_HUM.ICDCodeList();
            ProductBusinessEntityRequestV2_H_HUM.ServiceDetail serDet = new ProductBusinessEntityRequestV2_H_HUM.ServiceDetail();
            ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail  incAddDet = new ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail();
            system.assert(oRequest != null);
            system.assertEquals(oRequest.GetProduct,null);
          Test.StopTest(); 
        }
    }  
}