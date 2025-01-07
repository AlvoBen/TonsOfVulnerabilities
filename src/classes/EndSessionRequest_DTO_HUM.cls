/**
   @Author Chaitanya Vegendla
   @name EndSessionRequest_DTO_HUM
   @CreateDate 04/06/2017
   @Description Request Helper class for Interact End Session
   @Version 1.0
   @reference REQ - 306387
   @Test Class 
*/ 
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Vegendla        		                   04/06/2017                   Original version
********************************************************************************************************************************/

public with sharing class EndSessionRequest_DTO_HUM 
{
	
	public EndSessionRequest EndSessionRequest;
	
	public class EndSessionRequest
		{
			public String SessionID;
		}

}