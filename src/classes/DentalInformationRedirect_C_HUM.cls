/************************************************************************************************************************************************
Apex Class Name  : DentalInformationRedirect_C_HUM
Version          : 1.0
Created Date     : Apr 11 2016
Function         : Class to redirect to Tooth History Application
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey                                 04/10/2016                    Original Version
* Avinash Kumar Choubey     		              06/06/2016                    Changed URL creation logic as per new design on REQ - 237241 
* Avinash Choubey             30512               06/17/2016                 	Updated to redirect from JS instead of Controller
* Rajesh Keswani			  38022				  09/01/2016				    Added Environment variable and getting CaseNumber from URL parameter
*************************************************************************************************************************************************/
public with sharing class DentalInformationRedirect_C_HUM
{   
    
	public  DentalInformationRedirect_C_HUM()
    {  
    	String pmId = ApexPages.currentpage().getParameters().get(GLOBAL_CONSTANT_HUM.ID_HUM);
    	sCaseNumber = ApexPages.currentpage().getParameters().get('RefId');
    	Policy_Member__C objPolMem = null;
    	if( NULL != pmId )
		{
		   objPolMem =  [Select Id,  Name from Policy_Member__C where Id=:pmId];
		   getAppURL ( objPolMem);
		}
    }
    
   public  String sCrtdBy {get;set;}
   public  String sCCPAppURL {get;set;}
   public  String sPmName  {get;set;}
   public  String sCaseNumber  {get;set;}
   public  String sEnvVariable  {get;set;}
   
     /**
    * <p>
    * Method to to get Tooth History app URL and User N/W Id
    *
    * @param Policy_Member__c objPolMem
    * @see
    */
    private void getAppURL (Policy_Member__C objPolMem)
    {
	 	if(objPolMem != null &&  objPolMem.name != null)
        {
            sCrtdBy=[select id,Network_User_id__c from User where id = :UserInfo.getUserId() limit 1].Network_User_id__c;
            HUMToothHistoryAppURL__c objAppURL = HUMToothHistoryAppURL__c.getValues(GLOBAL_CONSTANT_HUM.VOB_DENTAL_CCPURL_HUM);
            if(objAppURL!= null && null!= objAppURL.CCPAppURL__C)
            {
                sCCPAppURL = ''+objAppURL.CCPAppURL__C;
                sEnvVariable = objAppURL.EnvVariable__c;
            }
            sPmName = objPolMem.name;
        } 
    }
}