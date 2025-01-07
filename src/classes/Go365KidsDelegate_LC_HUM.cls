public with sharing class Go365KidsDelegate_LC_HUM {
    
	public string sEntId{get; set;}
    public string sAge{get; set;}
    public Static final String GO365KIDSDEL_PERMITTEDUSER = 'permitted_user';
    public Static final String GO365KIDSDEL_DATAOWNER = 'data_owner';
    /**
    * Remote action method to call service
    * 
    * @param  string  
    * @return   Object
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Object getKidsDelegateDetails(string sEntId, string sAge)
    {
    	try
    	{
    		String sDelegateType = '';
            sDelegateType = (Integer.valueOf(sAge) < 18) ? GO365KIDSDEL_DATAOWNER:GO365KIDSDEL_PERMITTEDUSER;
        	return (Go365KidsDelegate_LS_HUM.callMemDelegateService(sEntId, 'false', sDelegateType, 'processMDS'));
    	}
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegate_LC_HUM', 'getKidsDelegateDetails');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    
    /**
    * Callback method for continuation
    * 
    * @param    
    * @return   Object
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Object processMDS(Object state)
    {
    	try
    	{
        	return Json.serialize((Go365KidsDelegate_LD_HUM.processMemDelResponse(state)));
    	}
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegate_C_HUM', 'processMDS');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    
    @AuraEnabled(continuation=true)
    public static object getMemDelLst(List<String> serRespMap)
    {
        try{
            list<Account> lstAccMemDel = new list<Account>();
            lstAccMemDel = [Select Id, Individual_First_Name__c, Individual_Last_Name__c, BirthDate__c, Enterprise_Id__c From Account Where Enterprise_Id__c IN : serRespMap WITH USER_MODE];
            return lstAccMemDel;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegate_LC_HUM', 'getMemDelLst');
            throw new AuraHandledException(ex.getMessage());
        }
    }
   
}