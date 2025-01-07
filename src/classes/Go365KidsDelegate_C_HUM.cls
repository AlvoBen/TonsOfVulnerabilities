/**
   @Author Gargey Sharma
   @name Go365KidsDelegate_C_HUM
   @CreateDate 12/21/2016
   @Description Controller for Go365 Kids Delegate Section
   @Version 1.0
   @reference REQ - 290967
   @Test Class Go365KidsDelegate_T_HUM
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         		70895                 12/21/2016                     Original version
 * Gargey Sharma         		70895                 02/17/2017                     Changes as per REQ - 301565
********************************************************************************************************************************/
public with sharing class Go365KidsDelegate_C_HUM
{
	public string sEntId{get; set;}
    public string sAge{get; set;}
    
    /**
    * Remote action method to call service
    * 
    * @param  string  
    * @return   Object
    */
    @RemoteAction
    public static Object getKidsDelegateDetails(string sEntId, string sAge)
    {
    	try
    	{
    		String sDelegateType = '';
            If(Integer.valueOf(sAge) < 18) sDelegateType = GLOBAL_CONSTANT_HUM.GO365KIDSDEL_DATAOWNER;
		    Else sDelegateType = GLOBAL_CONSTANT_HUM.GO365KIDSDEL_PERMITTEDUSER;
        	return (Go365KidsDelegate_S_HUM.callMemDelegateService(sEntId, 'false', sDelegateType, 'processMDS'));
    	}
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegate_C_HUM', 'getKidsDelegateDetails');
            return null;
        }
    }
    
    /**
    * Callback method for continuation
    * 
    * @param    
    * @return   Object
    */
    public static Object processMDS(Object state)
    {
    	try
    	{
        	return (Go365KidsDelegate_D_HUM.processMemDelResponse(state));
    	}
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Go365KidsDelegate_C_HUM', 'processMDS');
            return null;
        }
    }
    
    /**
    * This method is used to set page message
    * 
    * @param    
    * @return   pagereference
    */
    public pagereference displayPageMessage()
    {
        if(Integer.valueOf(sAge) < 18)
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, Label.Go365KidsDelegate_Under18)); 
        }
        else
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, Label.Go365KidsDelegate_Above18)); 
        }
        return null;
    }
}