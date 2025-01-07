/*********************************************************************************************
Apex Class Name  : LoggedInformation_C_HUM
Version          : 1.0
Created Date     : 02/22/2016
Function         : This is controller class for visual force page 'LoggedInformation_VF_HUM'.
                    This is used to display attachment log records.
Test Class       : LoggedInformation_C_HUM
************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
------------------------------------------------------------------------------------------------
* Santhi Mandava                                     02/22/2016                 Original Version
*************************************************************************************************/

public with sharing class LoggedInformation_C_HUM
{
    public String sCaseId{get;set;}
    
     /*
    * Method Name : LoggedInformation_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : ApexPages.StandardController
    */
    public LoggedInformation_C_HUM(ApexPages.StandardController controller) 
    {
        Case objCase = (Case)controller.getRecord();
        sCaseId = objCase.Id;
    }
}