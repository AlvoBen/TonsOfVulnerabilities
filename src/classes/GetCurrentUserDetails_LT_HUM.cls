/*****************************************************************************************
Apex class Name    : GetCurrentUserDetails_LT_HUM
Version            : 1.0
Function           : test class to cover GetCurrentUserDetails class
Created Date       : 05/06/2021
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Kajal Namdev									         05/06/2021				    initial version
**********************************************************************************************************************************************************/
@isTest
public class GetCurrentUserDetails_LT_HUM {
    /*
    * Method Name : getUserDetailTest
    * Description : This method is used to test the getUserDetails methods
    * Return type : void
    * Parameter   : None
    */
    static testMethod void getUserDetailTest(){
       GetCurrentUserDetails userC = new GetCurrentUserDetails();
       GetCurrentUserDetails.getUserDetails(); 
    }
}