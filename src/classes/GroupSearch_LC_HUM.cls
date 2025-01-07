/*******************************************************************************************************************************
Apex Class Name : GroupSearch_LC_HUM
Version         : 1.0
Created On      : 09/18/2020
Function        : This class is used for performing the Lightning Group Search
Test Class      : GroupSearch_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	09/18/2020                	Original Version
*    2.0           Ankima                                           06/18/2012                  Checkmarx issue fix
*    3.0           Vardhman Jain                                    09/27/2022                  US3429402 - Home Office/ Contract Protected Data Changes
*******************************************************************************************************************************************************************/

public with sharing class GroupSearch_LC_HUM 
{
    public static GroupSearchInputWrapper_LH_HUM oGroupSearchInputWraper{get; set;}
    private static List<Account> lstAccountList = new List<Account>();  
    public static GroupSearchResponseWrapper objGrpSerResponse = new GroupSearchResponseWrapper(); 
    public static List<Account> lstFinalSearchResult = new List<Account>();     
    /*  
    * Method name : searchGroupAccount
    * Description : 

    * Return Type : List<Account>
    * Parameter   : NA
    */
    @AuraEnabled(cacheable=true)
    public static GroupSearchResponseWrapper searchGroupAccount(GroupSearchInputWrapper_LH_HUM oGroupSearchInputWraper)
    {  
        try
        {	
            String sQry = '';  
            String sGroupNumberCondition=GLOBAL_CONSTANT_LH_HUM.sGroupNumberField;
            String sGroupSourceCustomerKeyCondition= GLOBAL_CONSTANT_LH_HUM.sGroupSourceCustomerKeyField;
            
            if(!String.isblank(oGroupSearchInputWraper.sGroupName ))
            {   
                lstAccountList= GroupSearch_LD_HUM.requestGroupAccountListBySOSL(oGroupSearchInputWraper,sGroupNumberCondition);
                
                if(lstAccountList.isempty())
                {
                    lstAccountList= GroupSearch_LD_HUM.requestGroupAccountListBySOSL(oGroupSearchInputWraper,sGroupSourceCustomerKeyCondition);
                }
            }
            else
            {	
                lstAccountList= GroupSearch_LD_HUM.requestGroupAccountListBySOQL(oGroupSearchInputWraper,sGroupNumberCondition);
                
                if(lstAccountList.isempty())
                {
                    lstAccountList= GroupSearch_LD_HUM.requestGroupAccountListBySOQL(oGroupSearchInputWraper,sGroupSourceCustomerKeyCondition);
                }
            }
            if(!lstAccountList.isEmpty()){
                lstFinalSearchResult = GroupSearch_LD_HUM.dataSecurity(lstAccountList);
            }
        }
        catch(Exception ex)
        {	
            HUMExceptionHelper.logErrors(ex, 'GroupSearch_LC_HUM', 'searchGroupAccount');
        }
		objGrpSerResponse.lstAccounts = lstFinalSearchResult;
        return objGrpSerResponse;
    }
	    public class GroupSearchResponseWrapper{
        @AuraEnabled
        public List<Account> lstAccounts;
        @AuraEnabled
        public Map<Id, Boolean> mapRecordAccess;
		}
}