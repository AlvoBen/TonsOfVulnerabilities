/*******************************************************************************************************************************
Apex Class Name : GroupDetailDivisionSubgroup_LC_HUM
Version         : 1.0
Created On      : 11/03/2020
Function        : This class is used for getting data for Division and Subgroup table for Group Account
Test Class      : GroupDetailDivisionSubgroup_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     12/18/2020                Original Version
*    2.0           Ashish Kumar                                     02/20/2021                Code refactor
*******************************************************************************************************************************************************************/
public with sharing Class GroupDetailDivisionSubgroup_LC_HUM 
{   
    public static Account objGrpAccount{get;set;}

    /*
    * Method Name : 
    * Description : Constructor for the class , initialize variable
    * Return type : N/A
    * Parameter   : ApexPages.StandardController
    */
    public GroupDetailDivisionSubgroup_LC_HUM(){}
        
    /*
    * Method Name   :  getGroupInfo()
    * Description   :  This method is used to Call the Service while loading the page
    * Return Type   :  Object
    * Parameters    :  NA
    */
    @AuraEnabled(continuation=true)         
    public static Object getGroupInfo(String sAccId) 
    {       
        Account objGrpAccount = GroupDetailDivisionSubgroup_LD_HUM.getAccount(sAccId); 
        Continuation con= GroupDetailDivisionSubgroup_LS_HUM.requestGroupInfo(objGrpAccount);
        return con;
    }
    

    /*
    * Method Name   :    processResponse() 
    * Description   :    Method to process the Response from getGroupInfo().
    * return Type   :    void
    * Parameters    :    NA
    */
    @AuraEnabled
    public static GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo processResponse(Object state) 
    {    
        GroupDetailDivisionSubgroup_LS_HUM.GroupDetailMainInfo listGroupDivisionData;
        try
        {   
            listGroupDivisionData=GroupDetailDivisionSubgroup_LS_HUM.processResponseGroupInfo(state);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM_V2', 'processResponse');
        }  
        return listGroupDivisionData;
    }  

    @AuraEnabled
    public static List<Account> fetchShipppingDetails(String accId){
        List<Account> lstAcc =  [select id, Shipping_FIPS_Code__c, Shipping_FIPS_Desc__c,HRA_HNA_Completed__c from Account where id =:accId];
        return lstAcc;
    }   
		
}