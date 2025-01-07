/************************************************************************************************************************************************
Apex Class Name :  PrimaryCare_LC_HUM
Version: 1.0
Created Date     : APR 04 2022
Function         : Controller Layer Class for PrimaryCarePhysicanHum component.
Test Class       :
*************************************************************************************************************************************************

Modification Log: 

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson                                 04/04/2022                 Original Version as per REQ - 3196414
*************************************************************************************************************************************************/

Public with sharing Class PrimaryCare_LC_HUM {    
    @AuraEnabled (continuation=true cacheable=true)
    public static object getPcpPcdData( String memberPlanId,String asOfDate) 
    {
        return PrimaryCare_LS_HUM.getPcpPcdData(memberPlanId,asOfDate);     
    }
	/**       
    * Returns responses of continuation object
    * Callback Method to parse the MBE service response
    * @return Object*/
 	@AuraEnabled
	public static Object parseMBEResponse(List<string> labels, Object State)
	{
		try{
			return Json.serialize(PrimaryCare_LS_HUM.processMBEResponse(labels, State));
		}
		catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    @AuraEnabled (continuation=true cacheable=true)
    public static object getProviderPhoneNumber(String providerNumber) {
        return PrimaryCare_LS_HUM.makeEPSServiceCall(providerNumber);
    }
    
    @AuraEnabled
    public static object processEPSSData(List<string> labels, object state){
        return JSON.serialize(PrimaryCare_LS_HUM.processPcpPhoneNumberResponse(Labels, State));
    }
}