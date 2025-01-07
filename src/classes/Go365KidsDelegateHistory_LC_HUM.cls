/*******************************************************************************************************************************
Apex Class Name : Go365KidsDelegateHistory_LC_HUM
Version         : 1.0
Created On      : 12/30/2021
Function        : This class is used for performing Go365 kids delegation History data related work
Test Class      : Go365KidsDelegateHistory_LT_HUM
                 
Modification Log: 
* Developer Name                      Date                        Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane                	  01/10/2022                  REQ# 2588478 -Go365 - Ability to View Go365 Kids Delegation History in a Lightning Experience
* Swapnali Sonawane                   07/14/2023                  US# 4812119  Critical- Lightning - Go365
***************************************************************************************************************************************************************/

public with sharing class Go365KidsDelegateHistory_LC_HUM {
    
    public string sAgeHis{get; set;}
    public string sEntIdHis{get; set;}
    public Static final String GO365KIDSDEL_PERMITTEDUSER = 'permitted_user';
    public Static final String GO365KIDSDEL_DATAOWNER = 'data_owner';
    /**
    * Remote action method to call service
    * 
    * @param    string
    * @return   Object
    */
	@AuraEnabled(continuation=true cacheable=true)
    public static Object getKidsDelegateHistory(string sEntIdHis, string sAgeHis)
    {
    	try
    	{
    		String sDelegateType = '';
            sDelegateType = (Integer.valueOf(sAgeHis) < 18)?GO365KIDSDEL_DATAOWNER:GO365KIDSDEL_PERMITTEDUSER;
    		return (Go365KidsDelegate_LS_HUM.callMemDelegateService(sEntIdHis, 'true', sDelegateType, 'processMDHistory'));	
    	}
    	catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegateHistory_LC_HUM', 'getKidsDelegateHistory');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    
    /**
    * callback method for continuation
    * 
    * @param    Object 
    * @return   Object
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Object processMDHistory(Object state)
    {
    	try
    	{
            return Json.serialize((Go365KidsDelegateHistory_LD_HUM.processMemDelHisResponse(state)));
    	}
    	catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegateHistory_LC_HUM', 'processMDHistory');
            throw new AuraHandledException(ex.getMessage());
        }
    }   
    @AuraEnabled(continuation=true)
    public static object getMemDelLst(List<String> serRespMap)
    {
        try
        {
            list<Account> lstAccMemDel = new list<Account>();
            lstAccMemDel = [Select Id, Individual_First_Name__c, Individual_Last_Name__c, BirthDate__c, Enterprise_Id__c From Account Where Enterprise_Id__c IN : serRespMap WITH USER_MODE];
            return lstAccMemDel;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegateHistory_LC_HUM', 'getMemDelLst');
            throw new AuraHandledException(ex.getMessage());
        }
    } 
    
}