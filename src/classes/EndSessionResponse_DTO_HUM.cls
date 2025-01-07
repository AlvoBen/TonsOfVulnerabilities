/**
   @Author Chaitanya Vegendla
   @name EndSessionResponse_DTO_HUM
   @CreateDate 04/06/2017
   @Description Response Helper class for Interact End Session
   @Version 1.0
   @reference REQ - 306387
   @Test Class 
*/ 
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Vegendla        		                   04/06/2017                   Original version
********************************************************************************************************************************/

public with sharing class EndSessionResponse_DTO_HUM 
{
	
	public EndSessionResponse  EndSessionResponse;
	public String endSessionResponseString;
	
	public class EndSessionResponse
		{
			public Status Status;
		}
		
	public class Status 
	    {
	    	public string Description = '';
	    	public string Code = '';
	    	public string SessionID = '';
	    }

}