/******************************************************************************************************************
Apex Class Name :   EncryptionService_T_HUM
Version: 1.0
Function           : Test Class to Cover EncryptionService DTO Class
Created On         : July 10, 2018

Modification Log:  
*   Developer             Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*  Lakshmi Madduri                        	7/10/2018           Original Version -> REQ - 366254
------------------------------------------------------------------------------------------------------------------*/
@isTest
Private class EncryptionService_T_HUM 
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
    }
    /*
    * Method Name : testEncryptService
    * Description : This method used To Test DTO For Encrypt Service
    * Return type : None
    * Parameter   : None
    */
    @isTest
    static void testEncryptService()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.StartTest();
            EncryptionServiceRequest_DTO_HUM objEncryptReq = new EncryptionServiceRequest_DTO_HUM();
            EncryptionServiceRequest_DTO_HUM.Encrypt objEncrypt = new EncryptionServiceRequest_DTO_HUM.Encrypt();
            objEncrypt.Value = 'H1008990';
            objEncryptReq.Encrypt = objEncrypt;
            
            EncryptionServiceResponse_DTO_HUM objEncryptRes = new EncryptionServiceResponse_DTO_HUM();
            EncryptionServiceResponse_DTO_HUM.EncryptResponse encryptRes = new EncryptionServiceResponse_DTO_HUM.EncryptResponse();
            EncryptionServiceResponse_DTO_HUM.EncryptResult objEncryptResult = new EncryptionServiceResponse_DTO_HUM.EncryptResult();
            objEncryptResult.EncryptValue = 'Test';
            encryptRes.EncryptResult = objEncryptResult;
            objEncryptRes.EncryptResponse = encryptRes;
            Test.stopTest();
        }
    }

}