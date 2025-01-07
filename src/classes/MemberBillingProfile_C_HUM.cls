/*******************************************************************************************************************************************************
Apex Class Name  : MemberBillingProfile_C_HUM
Version          : 1.0
Created Date     : 10/29/2015
Function         : This is controller class for visual force page 'MemberBillingProfile_VF_HUM'.
                    This is used to display billing profile information assosiated to member.
Test Class       : MemberBillingProfile_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                    10/29/2015                 Original Version
* Santhi Mandava            29422                    01/19/2016                 Implemented billing phase 2 stories. 
* Santhi Mandava            40488                    09/19/2016                 Implemented billing logging.
* Prudhvi Pamarthi                                   02/18/2019                 REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Suraj Patil								   		 04/20/2021					REQ - 1874926 - PR00094226 - CR AEP readiness - Low Security Issues
* Prasanthi Kandula                                  07/26/2021                 US 2484356 T1PRJ0036776_MF_3SF : Member Billing Profile Page - Profile Selection  
**********************************************************************************************************************************************/
public with sharing class MemberBillingProfile_C_HUM extends BillingProfile_A_HUM
{
     /*
    * Method Name : MemberBillingProfile_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : ApexPages.StandardController
    */
    public MemberBillingProfile_C_HUM(ApexPages.StandardController oStdController)
    {
        try
        {
            if(!Test.isRunningTest()) oStdController.addFields(new List<String> {'Name','Policy_Platform__c','Source_Coverage_ID__c','Subscriber__c','Subscriber__r.Name'});
            objRecordData = (Policy_Member__c)oStdController.getrecord();
            bdisableButton = false;
            Policy_Member__c oPolMem = [SELECT id,ETL_Record_Deleted__c from Policy_Member__c where Id = :String.escapeSingleQuotes(objRecordData.Id)];
            if(oPolMem !=null)bdisableButton = oPolMem.ETL_Record_Deleted__c;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberBillingProfile_C_HUM', 'MemberBillingProfile_C_HUM');
        }  
    }

    public Boolean bdisableButton {get; set;}
 
    
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
    * Method Name : getsSrcCoveId
    * Description : This is used to get slected policy members source coverage id.
    * Return type : String
    * Parameter   : NA
    */
    public String getsSrcCoveId()
    {
        return ((Policy_Member__c)objRecordData).Source_Coverage_ID__c;
    }
    
    /*
    * Method Name : getThis
    * Description : This method is used to return GroupBillingProfile_C_HUM class object.
    * Return type : GroupBillingProfile_C_HUM 
    * Parameter   : NA
    */  
    public MemberBillingProfile_C_HUM getThis() 
    {
        return this;
    }
}