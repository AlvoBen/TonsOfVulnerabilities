/*******************************************************************************************************************************************************
Apex Class Name  : GroupBillingProfile_C_HUM 
Version          : 1.0
Created Date     : 10/29/2015
Function         : This is controller class for visual force page 'GroupBillingProfile_VF_HUM'.
                    This is used to display billing profile information assosiated to group.
Test Class       : GroupBillingProfile_T_HUM 
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                    10/29/2015                 Original Version
* Santhi Mandava            29422                    01/19/2016                 Implemented billing phase 2 stories. 
* Santhi Mandava                                     09/19/2016                 Implemented billing logging.
* Prudhvi Pamarthi         163987                    01/02/2018                 REQ - 328929 Fix for HUMExceptionHelper: DML
**********************************************************************************************************************************************/
public with sharing class GroupBillingProfile_C_HUM extends BillingProfile_A_HUM
{
    /*
    * Method Name : GroupBillingProfile_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : ApexPages.StandardController
    */
    public GroupBillingProfile_C_HUM(ApexPages.StandardController oStdController)
    {
        try
        {
            if(!Test.isRunningTest()) oStdController.addFields(new List<String> {'Name','Source_Platform_Code__c','Source_Customer_Key__c'});
            objRecordData = (Account)oStdController.getrecord();
        }
        catch(Exception ex)
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'GroupBillingProfile_C_HUM', 'GroupBillingProfile_C_HUM');
        }  
    } 
    
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
       }  
       set;
    } 
    
    /*
    * Method Name : getsRecId
    * Description : This is used to get Id of the selected record.
    * Return type : String
    * Parameter   : NA
    */
    public String getsRecId()
    {
        return objRecordData.Id;
    }
    
    /*
    * Method Name : getsSrcCustKey
    * Description : This is used to get slected account's source customer key.
    * Return type : String
    * Parameter   : NA
    */
    public String getsSrcCustKey()
    {
        return ((Account)objRecordData).Source_Customer_Key__c;
    }
    
    /*
    * Method Name : getThis
    * Description : This method is used to return GroupBillingProfile_C_HUM class object.
    * Return type : GroupBillingProfile_C_HUM 
    * Parameter   : NA
    */  
    public GroupBillingProfile_C_HUM getThis() 
    {
        return this;
    }
}