/************************************************************************************************************************************************************************************       
Apex Class Name  : HUMLaunchCompass_C_HUM.cls 
Created Date     : July 17, 2019
Function         : Executes SOQL in support of the Launch compass Component
Test Class       : HUMLaunchCompass_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* -----------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj								07/17/2019				   Original Version
************************************************************************************************************************************************************************************/ 
    
public class LaunchCompassApp_C_HUM 
{
    //Variable to denote if Rapid Force is the current App
    public boolean bInsideRapidForce{get;set;}

    /*
    * Constructor
    * 
    * @see populate function and sub function for Canvas app
    */
    public LaunchCompassApp_C_HUM()
    {  	    	   
        bInsideRapidForce = HUMUtilityHelper.IsRapidForceAPP();
    }
    /*
    * Method Name: redirectToMentorPage
    * 
    * @see Logic to redirect to Mentor to SFDC page in subtab
    */
    public pageReference redirectToMentorPage()
    {
    	PageReference pg ;
    	if(!bInsideRapidForce)
        {
        	pg = new PageReference('/apex/HumMentorToSFDC');	
        }
        return pg;
    }
}