/*******************************************************************************************************************************
Apex Class Name : Go365KidsDelegateHistory_LD_HUM
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

public with sharing class Go365KidsDelegateHistory_LD_HUM {
	public Static final Integer HTTP_ACCEPTED_HUM = 202; 
	public Static final Integer HTTP_OK_HUM = 200;
	public Static final String FAULTCODE_ALERTS_HUM = 'faultcode';

    /**
    * This method is used to process Member Delegate Response
    * 
    * @param    
    * @return   Object
	*/
	@AuraEnabled(continuation=true cacheable=true)
    public static object processMemDelHisResponse(Object state)
	{
		HttpResponse response = Continuation.getResponse((string)state);
        MemberDelegatesServiceResponse_DTO_HUM oResponse = new MemberDelegatesServiceResponse_DTO_HUM();
        if(response != null && (response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) && !response.getbody().contains(FAULTCODE_ALERTS_HUM))
		{
	        oResponse = (MemberDelegatesServiceResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), MemberDelegatesServiceResponse_DTO_HUM.class);
		}
		return oResponse;
	}  
}