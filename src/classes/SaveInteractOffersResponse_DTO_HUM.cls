/**
   @Author Gargey Sharma
   @name SaveInteractOffersResponse_DTO_HUM
   @CreateDate 01/04/2017
   @Description Response helper class for Save Interact Offers Service
   @Version 1.0
   @reference REQ - 291110
   @Test Class 
*/ 
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         		                      01/04/2017                     Original version
********************************************************************************************************************************/
public with sharing class SaveInteractOffersResponse_DTO_HUM 
{
    public SaveOffersResponse SaveOffersResponse = null;
    public String SaveOfferResponseString;
    
    public class SaveOffersResponse 
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