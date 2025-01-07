/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityRequest_T_HUM 
Version          : 1.0  
Created Date     : February 05, 2016
Function         : test class for ProductBusinessEntityRequest_T_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        29829            02/05/2016                    Original Version
* Gargey Sharma             30736	         03/29/2016                    REQ - 263787: Added ServiceDetail
***************************************************************************************************/
@isTest
private Class ProductBusinessEntityRequest_T_HUM 
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
            ProductBusinessEntityRequest_H_HUM oRequest = new ProductBusinessEntityRequest_H_HUM();
            ProductBusinessEntityRequest_H_HUM.GetProduct getPro = new ProductBusinessEntityRequest_H_HUM.GetProduct();
            ProductBusinessEntityRequest_H_HUM.ProductIdentifier proIden = new ProductBusinessEntityRequest_H_HUM.ProductIdentifier();
            ProductBusinessEntityRequest_H_HUM.IncludeType incType = new ProductBusinessEntityRequest_H_HUM.IncludeType();
            ProductBusinessEntityRequest_H_HUM.ICDCode icCode = new ProductBusinessEntityRequest_H_HUM.ICDCode();
            ProductBusinessEntityRequest_H_HUM.ServiceDetail serDet = new ProductBusinessEntityRequest_H_HUM.ServiceDetail();
          	system.assert(oRequest != null);
          Test.StopTest(); 
        }
    }  
}