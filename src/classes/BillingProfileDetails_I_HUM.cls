/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfileDetails_I_HUM
Version          : 1.0
Created Date     : 01/11/2016
Function         : This is interface for controller BillingProfile_A_HUM
Test Class       : NA
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                    01/11/2016                 Original Version
* Santhi Mandava            29422                    01/19/2016                 Corrected modification log as per standards.
************************************************************************************************************************************************/

public interface BillingProfileDetails_I_HUM
{
   /*
    * Method Name : getSObjName
    * Description : This with return selected  sObject Name.
    * Return type : String
    * Parameter   : NA
    */
    String getSObjName(Id recID);
    
    /*
    * Method Name : getRecordId
    * Description : This with return selected sObject Id.
    * Return type : Id
    * Parameter   : NA
    */
    Id getRecordId();
}