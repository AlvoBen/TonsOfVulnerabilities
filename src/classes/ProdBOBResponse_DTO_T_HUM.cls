/****************************************************************************************
Apex Class Name  : ProdBOBResponse_DTO_T_HUM
Version          : 1.0 
Function         : Test class to ProdBOBResponse_DTO_HUM Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*    Chetan S                                      11/13/2019              Original Version     
***********************************************************************************************************/
@isTest
private class ProdBOBResponse_DTO_T_HUM
{
    /*
    * Method Name : setup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */ 
    static testMethod void testSetup(){
        Test.startTest();
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){
            ProdBOBResponse_DTO_HUM ProdResponse = new ProdBOBResponse_DTO_HUM();
            ProdBOBResponse_DTO_HUM.ProducerBusinessResultData ProdBusinessResult = new ProdBOBResponse_DTO_HUM.ProducerBusinessResultData();
            ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa ProdBusinessGa = new ProdBOBResponse_DTO_HUM.ProducerBusinessWithGa();
            
            ProdBusinessGa.AbcProductCode = 'PDP';
            ProdBusinessGa.AorName = 'CO  HUMANA INSURANCE';
            ProdBusinessGa.AorSan = '1064625';
            ProdBusinessGa.BlockOfBusiness = 'MA';
            ProdBusinessGa.MemberId = '';
            ProdBusinessGa.MemberName = 'Test';
            ProdBusinessGa.PID = '987654321';
            ProdBusinessGa.Platform = 'LV';
            
            ProdBusinessResult.ProducerBusinessWithGa = ProdBusinessGa;
            ProdResponse.ProducerBusinessResultData = ProdBusinessResult;
        }
     }
}