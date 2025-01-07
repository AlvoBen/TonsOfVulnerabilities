/********************************************************************************************************************************************************
Apex Class Name  : CRMRetail_Result_DTO_HUM
Version          : 1.0 
Created Date     : OCT 10 2022
Function         : Wrapper class to hold the storefront error log details
Modification Log :       
* Developer                         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------
* Vinoth L                      	10/11/2022           User Story 3759560: T1PRJ0154546 / SF / MF4 Storefront: Add Account Information on Tasks
* Mohamed Thameem                   27/05/2023           User Story - 4647349 T1PRJ0154546 / SF / MF9 Storefront - Add Last 3 One-On-One Information
*******************************************************************************************************************************************************/
public with sharing class CRMRetail_Result_DTO_HUM {
    @auraenabled public object sResult;
    @auraenabled public boolean isError = false;
}