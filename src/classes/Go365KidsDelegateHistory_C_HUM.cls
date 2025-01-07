/**
   @Author Gargey Sharma
   @name Go365KidsDelegateHistory_C_HUM
   @CreateDate 01/09/2017
   @Description Controller for Go365 Kids Delegate History Section
   @Version 1.0
   @reference REQ - 290969
   @Test Class Go365KidsDelegateHistory_T_HUM
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         		70895                 01/09/2017                     Original version
 * Gargey Sharma         		70895                 02/17/2017                     Changes as per REQ - 301564
********************************************************************************************************************************/
public with sharing class Go365KidsDelegateHistory_C_HUM 
{
	public string sAgeHis{get; set;}
	public string sEntIdHis{get; set;}
	
	
	/**
    * Remote action method to call service
    * 
    * @param    string
    * @return   Object
    */
	@RemoteAction
    public static Object getKidsDelegateHistory(string sEntIdHis, string sAgeHis)
    {
    	try
    	{
    		String sDelegateType = '';
            If(Integer.valueOf(sAgeHis) < 18) sDelegateType = GLOBAL_CONSTANT_HUM.GO365KIDSDEL_DATAOWNER;
		    Else sDelegateType = GLOBAL_CONSTANT_HUM.GO365KIDSDEL_PERMITTEDUSER;
    		return (Go365KidsDelegate_S_HUM.callMemDelegateService(sEntIdHis, 'true', sDelegateType, 'processMDHistory'));	
    	}
    	catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegateHistory_C_HUM', 'getKidsDelegateHistory');
            return null;
        }
    }
    
    /**
    * callback method for continuation
    * 
    * @param    Object 
    * @return   Object
    */
    public static Object processMDHistory(Object state)
    {
    	try
    	{
    		return (Go365KidsDelegateHistory_D_HUM.processMemDelHisResponse(state));
    	}
    	catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegateHistory_C_HUM', 'processMDHistory');
            return null;
        }
    }    
}