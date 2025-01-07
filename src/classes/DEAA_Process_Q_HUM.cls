/******************************************************************************************************************************
Apex Class Name  : DEAA_Process_Q_HUM
Version          : 1.0 
Created Date     : November 20 2019
Function         : Queueable APEX calss for ZAPI service callout for DEAA Cases.                   
Test Class       : DEAA_Process_T_HUM
Modification Log :
   Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
  Mohammed Noor                           11/20/2019           Original version. 
*********************************************************************************************************************************/
public with sharing class DEAA_Process_Q_HUM implements Queueable, Database.AllowsCallouts 
{	
	private list<string> lstCaseIDs {get; set;}
		
	/**
	* Constructor method to initialize instance variables.
	*
	* @param    List<String>	list containing CaseIDs.	
	* @return   N/A
	*/
	public DEAA_Process_Q_HUM(list<string> lstCaseIDs)
	{
		if(lstCaseIDs!=null && !lstCaseIDs.isEmpty())	this.lstCaseIDs = lstCaseIDs;
		else	lstCaseIDs = new list<string>();
	}
	
	/**
	* Execute method of the Queueable Class that calls the Helper class to make the ZAPI callout. 
	*
	* @param    QueueableContext	
	* @return   void
	*/
	public void execute(QueueableContext context)
	{
		try
		{
			if(lstCaseIDs!=null && !lstCaseIDs.isEmpty())
			{
				DEAA_Process_H_HUM processHelper = new DEAA_Process_H_HUM(lstCaseIDs);
				processHelper.invokeZAPIService();
			}
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'DEAA_Process_Q_HUM', 'execute');
		}
	}
}