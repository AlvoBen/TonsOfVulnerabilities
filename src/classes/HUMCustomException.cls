/*******************************************************************************************************************************
Apex Class Name : HUMCustomException
Version         : 1.0
Created On      : 06/12/2014
Function        : Custom Exception class which extends Standard Exception. Used to identify the type of exception based on the
				  Parameters sent to exception wrapper
Test Class		: 
Modification Log: 
 * Developer Name          Code Review                   Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar        17152                       06/12/2014                   Original Version
 * Chaitanya Kumar		  17152						  07/07/2014				   Moved Constants to Custom Setting
 *******************************************************************************************************************************/

Public class HUMCustomException extends Exception 
{

	//Store the reference of the exception wrapper
	public HUMCustomExceptionHelper exceptionWrapper;
	
	
	/*
	 * Method Name : HUMCustomException
	 * Description : Constructor
	 * Return type : void
	 * Parameter   : HUMCustomExceptionHelper
	 */
	public HUMCustomException(HUMCustomExceptionHelper exceptionWrapper)
	{
		this.exceptionWrapper=exceptionWrapper; 
	} 
	
	/*
	 * Method Name : getExceptionType
	 * Description : Return the type of Exception based on the parameters passed
	 * Return type : String
	 * Parameter   : HUMCustomExceptionHelper
	 */
	public static string getExceptionType(HUMCustomExceptionHelper exceptionWrapper) 
	{
		if(exceptionWrapper!=null && !String.ISBLANK(exceptionWrapper.sExceptionType))
		{

			return HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c ;
		}
		else	
		{
			return HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c ;
		}
		return ''; 
	} 

}