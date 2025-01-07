/**
   @Author Sagar Gulleve
   @name Go365KidsDelegate_LT_HUM
   @CreateDate  21/01/2022
   @Description Test class for Go365 Member Delegate Section 2588464
   @Version 1.0
   @reference REQ - 2588464
   @Test Class 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Sagar Gulleve        		                       21/01/2022                   Original version
 
********************************************************************************************************************************/
public with sharing class Go365KidsDelegate_LD_HUM 
{
    /**
    * This method is used to process Member Delegate Response
    * 
    * @param    
    * @return   Object
    */
	Public Static Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static Integer HTTP_OK_HUM = 200;
    Public Static String FAULTCODE_ALERTS_HUM = 'faultcode';
	
    @AuraEnabled(continuation=true cacheable=true)
    public static object processMemDelResponse(Object state)
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